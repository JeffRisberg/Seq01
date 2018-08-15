package com.company.jobServer.common;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Semaphore;

public class DynamicProperties {
  private static final Logger logger = LoggerFactory.getLogger(DynamicProperties.class);
  private static final boolean IS_CACHED = false;
  private static DynamicProperties instance = null;
  private static String CONFIGURATION_PATH = "/company/configuration/dynamic";
  private static String ZK_HOSTS_CONFIG_KEY = "company_zk_uri";
  private final CuratorFramework framework;
  private final TreeCache cache;

  /**
   *
   * @return
   */
  public static synchronized Optional<DynamicProperties> getInstance() {
    if (instance == null) {
      try {
        String hosts = ResourceLocator.getResource(ZK_HOSTS_CONFIG_KEY)
          .orElseThrow(() -> new Exception("Failed to find ZK entry"));
        instance = new DynamicProperties(hosts);
      } catch (Exception e) {
        logger.error("Failed to create a configurator", e);
      }
    }

    return Optional.ofNullable(instance);
  }

  /**
   * Only for tests
   * @param hosts
   * @return
   */
  static synchronized Optional<DynamicProperties> getTestInstance(String hosts) throws Exception {
    return Optional.of(new DynamicProperties(hosts));
  }

  private DynamicProperties(String hosts) throws Exception {
    this.framework = CuratorFrameworkFactory.builder()
      .sessionTimeoutMs(30000)
      .retryPolicy(new ExponentialBackoffRetry(1000, 3))
      .connectString(hosts)
      .build();
    this.framework.start();

    // create cache object
    this.cache = TreeCache.newBuilder(this.framework, CONFIGURATION_PATH)
      .setCacheData(IS_CACHED)
      .build();
    this.cache.start();

    // semaphore to wait for initialization
    Semaphore sem = new Semaphore(0);

    // add change listeners
    addListener(cache, sem);

    // wait for the initialization to happen
    sem.acquire();
  }

  /**
   * Listener for the
   * @param cache
   */
  private static void addListener(TreeCache cache, Semaphore semaphore) {
    // a PathChildrenCacheListener is optional. Here, it's used just to log changes
    TreeCacheListener listener = (client, event) -> {
      switch (event.getType()) {
        case NODE_ADDED: {
          logger.info("Node added: ({})", ZKPaths.getNodeFromPath(event.getData().getPath()));
          break;
        }

        case NODE_UPDATED: {
          logger.info("Node changed: ({}), new value: ({})", ZKPaths.getNodeFromPath(event.getData().getPath()),
            new String(event.getData().getData()));
          break;
        }

        case NODE_REMOVED: {
          logger.info("Node removed: ({})", ZKPaths.getNodeFromPath(event.getData().getPath()));
          break;
        }

        case INITIALIZED: {
          semaphore.release();
          break;
        }
      }
    };

    // register listener
    cache.getListenable().addListener(listener);
  }

  public Optional<String> getProperty(String tenantId, String key) {
    return (IS_CACHED) ? getCachedNode(tenantId, key) : getNodeData(tenantId, key);
  }

  public Optional<Boolean> getPropertyAsBoolean(String tenantId, String key) {
    return getProperty(tenantId, key).map(value -> Boolean.parseBoolean(value));
  }

  public Optional<Double> getPropertyAsDouble(String tenantId, String key) {
    return getProperty(tenantId, key).map(value -> Double.parseDouble(value));
  }

  public Optional<Long> getPropertyAsLong(String tenantId, String key) {
    return getProperty(tenantId, key).map(value -> Long.parseLong(value));
  }

  private Optional<String> getProperty(String path) {
    try {
      byte[] data = this.framework.getData().forPath(path);
      return (data != null) ? Optional.of(new String(data)) : Optional.empty();
    } catch (Exception e) {
      logger.error ("Failed to get data from path", e);
    }

    return Optional.empty();
  }

  public void setProperty(String tenantId, String key, Boolean value) throws Exception {
    this.setProperty(tenantId, key, Boolean.toString(value));
  }

  public void setProperty(String tenantId, String key, Double value) throws Exception {
    this.setProperty(tenantId, key, Double.toString(value));
  }

  public void setProperty(String tenantId, String key, Long value) throws Exception {
    this.setProperty(tenantId, key, Long.toString(value));
  }

  public void setProperty(String tenantId, String key, String value) throws Exception {
    String path = ZKPaths.makePath(CONFIGURATION_PATH, tenantId, key);
    Stat stat = this.framework.checkExists().creatingParentContainersIfNeeded().forPath(path);
    if (stat == null) {
      this.framework.create().withMode(CreateMode.PERSISTENT).forPath(path, value.getBytes());
    } else {
      this.framework.setData().forPath(path, value.getBytes());
    }
  }

  public Map<String, String> listProperties(String tenantId) throws Exception {
    Map<String, String> map = new HashMap<>();
    // get children
    this.framework.getChildren()
      .forPath(ZKPaths.makePath(CONFIGURATION_PATH, tenantId))
      .forEach(path -> getProperty(path).ifPresent(data -> map.put(ZKPaths.getPathAndNode(path).getNode(), data)));
    return map;
  }

  private Optional<String> getNodeData(String tenantId, String key) {
    String path = (key != null)
      ? ZKPaths.makePath(CONFIGURATION_PATH, tenantId, key)
      : ZKPaths.makePath(CONFIGURATION_PATH, tenantId);

    byte[] data = null;
    try {
      data = this.framework.getData().forPath(path);
    } catch (Exception e) {}

    return (data != null) ? Optional.of(new String(data)) : Optional.empty();
  }

  private Optional<String> getCachedNode(String tenantId, String key) {
    ChildData childData = this.cache.getCurrentData((key != null)
      ? ZKPaths.makePath(CONFIGURATION_PATH, tenantId, key)
      : ZKPaths.makePath(CONFIGURATION_PATH, tenantId));

    return Optional.ofNullable(childData)
      .map(cd -> cd.getData())
      .filter(x -> (x != null))
      .map(data -> Optional.ofNullable(new String(data)))
      .orElse(Optional.empty());
  }
}
