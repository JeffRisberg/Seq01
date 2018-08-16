package com.company.jobServer;

import com.company.jobServer.common.ResourceLocator;
import io.swagger.jaxrs.config.DefaultJaxrsConfig;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.servlet.ServletProperties;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.servlet.DispatcherType;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobServer {
    public static Logger logger = Logger.getLogger(JobServer.class.getName());

    public final static String JOB_SERVER_PORT = "company_job_server_port";
    public final static String DB_HOST = "company_datastores_sql_host";
    public final static String DB_PORT = "company_datastores_sql_port";
    public final static String DB_USER = "company_sql_user";
    public final static String DB_PASSWORD = "company_sql_password";

    public final static String DEFAULT_JOB_SERVER_PORT = "8087";
    public final static String DEFAULT_DB_HOST = "localhost";
    public final static String DEFAULT_DB_PORT = "3306";
    public final static String DEFAULT_DB_USER = "root";
    public final static String DEFAULT_DB_PASSWORD = "rootroot";

    public static final String HIBERNATE_CONNECTION_URL_KEY = "hibernate.connection.url";

    private static Server server;
    //public static SQLStoreFactory.SQLStore sqlStore;
    //public static ObjectStoreFactory.ObjectStore objectStore;
    //public static ContainerServiceFactory.ContainerService containerService;
    public static SessionFactory sessionFactory;

    public static ScheduledExecutorService executor = Executors.newScheduledThreadPool(100);

    public static Map<String, Future> currentTimers = new HashMap<String, Future>();

    private static void configureServer() throws Exception {
        server = new Server();
        int plainPort = Integer.parseInt(ResourceLocator.getResource(JOB_SERVER_PORT).orElse(DEFAULT_JOB_SERVER_PORT));

        logger.info("Using port: " + plainPort);
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(plainPort);
        server.setConnectors(new Connector[]{connector});

        ServletContextHandler sch = new ServletContextHandler(server, "/job-server", ServletContextHandler.SESSIONS);

        ServletHolder mainServlet = new ServletHolder(org.glassfish.jersey.servlet.ServletContainer.class);
        mainServlet.setInitParameter(ServletProperties.JAXRS_APPLICATION_CLASS, JobServerConfig.class.getCanonicalName());
        mainServlet.setInitOrder(1);
        sch.addServlet(mainServlet, "/*");

        // This makes a servletHolder, configures it, and then adds it to the contextHandler
        ServletHolder swaggerServletHolder = new ServletHolder(new DefaultJaxrsConfig());
        swaggerServletHolder.setInitParameter("api.version", "1.0.0");
        swaggerServletHolder.setInitParameter("swagger.api.basepath", "/job-server");
        swaggerServletHolder.setInitOrder(2);
        sch.addServlet(swaggerServletHolder, "/api-docs");

        // Add header options
        FilterHolder cors = sch.addFilter(CrossOriginFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,GET,POST,HEAD");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[]{"index.html"});
        resource_handler.setResourceBase("swagger-ui");
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resource_handler, sch});
        server.setHandler(handlers);

        Logger.getLogger(JobServer.class.getName()).info("Job Server configured");
    }

    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Logger.getLogger(JobServer.class.getName()).info("Server is shutting down");
                    // ServicesApiServiceImpl services = new ServicesApiServiceImpl();
                    // services.deleteAllService();
                    server.stop();
                } catch (Exception ex) {
                    Logger.getLogger(JobServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }));
    }

    private static void loadProperties() throws Exception {
        ResourceLocator.registerProperties(
                JobServer.class.getClassLoader().getResourceAsStream("job-server.properties"));
    }

    private static void configurePlugins() throws Exception {
        logger.info("Configuring DB");
        Configuration configuration = createHibernateConfig();
        sessionFactory = configuration.buildSessionFactory();

    /*
    logger.info("Configuring Container Service");
    containerService = IOHelper.getContainerService(
      "com.company.plugins.orchestration.kubernetes.KubernetesContainerService$KubernetesContainerServiceFactory");

    try {
      logger.info("Configuring Object Store");
      objectStore = OrchestrationContext.getInstance().getObjectStoreHandle()
        .orElseThrow(() -> new Exception("Failed to get orchestration"));
    } catch (Exception e) {
      logger.info("Exception " + e.getMessage());
    }
    */
    }

    private static Configuration createHibernateConfig() {
        Configuration configuration = new Configuration();

        String dbHost = ResourceLocator.getResource(DB_HOST).orElse(DEFAULT_DB_HOST);
        String dbPort = ResourceLocator.getResource(DB_PORT).orElse(DEFAULT_DB_PORT);

        String jdbcUrl = "jdbc:mariadb://" + dbHost + ":" + dbPort + "/tenant_store";
        logger.info("Using jdbcURL: " + jdbcUrl);
        configuration.setProperty(HIBERNATE_CONNECTION_URL_KEY, jdbcUrl);

        String dbUser = ResourceLocator.getResource(DB_USER).orElse(DEFAULT_DB_USER);
        logger.info("Using dbUser: " + dbUser);
        configuration.setProperty("hibernate.connection.username", dbUser);

        String dbPassword = ResourceLocator.getResource(DB_PASSWORD).orElse(DEFAULT_DB_PASSWORD);
        logger.info("Using dbPassword: " + dbPassword);
        configuration.setProperty("hibernate.connection.password", dbPassword);

        return configuration.configure();
    }

    public static void startJobServer() throws Exception {
        loadProperties();
        configureServer();
        configurePlugins();

        server.start();
    }

    public static void stopJobServer() throws Exception {
        server.stop();

        executor.shutdown();
    }

    public static void main(String[] args) throws Exception {
        startJobServer();

        logger.info("Job Server is started");
    }
}
