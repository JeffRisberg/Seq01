package com.company.jobServer.controllers.DAO;

import com.company.jobServer.FilterDescription;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class BaseDAOImpl implements BaseDAO {

  @Override
  public <T> T create(T obj, Class<T> type, Session session) {
    long id;
    id = (long) session.save(obj);
    obj = getById(type, id, session);
    return obj;
  }

  @Override
  public <T> T getById(Class<T> type, long id, Session session) {
    AtomicReference<T> reference = new AtomicReference<>();
    reference.set(session.get(type, id));
    return reference.get();
  }

  @Override
  public <T> T getById(Class<T> type, String id, Session session) {
    AtomicReference<T> reference = new AtomicReference<>();
    reference.set(session.get(type, id));
    return reference.get();
  }

  @Override
  public <T> List<T> listAll(Class<T> type, Session session) {
    Query query;
    query = session.createQuery("from " + type.getName());
    List<T> objects = query.list();
    return objects;
  }

  /**
   * Execute Custom SQL with Map of parameters
   *
   * @param type
   * @param sql
   * @param params
   * @param session
   * @param <T>
   * @return Result
   */
  public <T> List<T> getBySQL(Class<T> type, @NonNull String sql, @NonNull Map<String, Object> params, Session session) {
    try {
      Query query = session.createNativeQuery(
        sql)
        .addEntity(type);
      params.entrySet().stream().forEach(entry -> query.setParameter(entry.getKey(), entry.getValue()));
      List<T> result = query.list();

      if (CollectionUtils.isEmpty(result))
        return Collections.emptyList();

      return result;
    } catch (Exception e) {
      log.error("getBySQL:: exception", e);
      return null;
    }
  }

  /**
   * Execute Custom SQL with Map of parameters
   *
   * @param sql
   * @param params
   * @param session
   * @return Result
   */
  public int updateBySQL(@NonNull String sql, @NonNull Map<String, Object> params, Session session) {
    try {
      Query query = session.createNativeQuery(sql);
      params.entrySet().stream().forEach(entry -> query.setParameter(entry.getKey(), entry.getValue()));
      int updated = query.executeUpdate();
      log.info("Updated/Deleted by SQL: {}", updated);
      return updated;
    } catch (Exception e) {
      log.error("updateBySQL:: exception", e);
      return 0;
    }
  }

  /**
   * Use Criteria Builder to execute a SQL By Parameters.
   *
   * @param type
   * @param filterDescriptions
   * @param session
   * @param <T>
   * @return List of results
   */
  public <T> List<T> getByCriteria(Class<T> type, List<FilterDescription> filterDescriptions, @NonNull Session session) throws Exception {
    CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
    CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(type);
    Root<T> root = criteriaQuery.from(type);
    criteriaQuery.select(root);

    Predicate p = criteriaBuilder.conjunction();

    for (FilterDescription fd : filterDescriptions) {
      String key = fd.getField();
      Object value = fd.getValue();
      if (value == null)
        continue;
      switch (fd.getOperator()) {
        case eq:
          p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get(key), value));
          break;
        case like:
          p = criteriaBuilder.and(p, criteriaBuilder.like(root.get(key), "%" + (String) value + "%"));
          break;
        case gt:
          p = criteriaBuilder.and(p, criteriaBuilder.greaterThan(root.get(key), (Comparable) value));
          break;
        case lt:
          p = criteriaBuilder.and(p, criteriaBuilder.lessThan(root.get(key), (Comparable) value));
          break;
        case gte:
          p = criteriaBuilder.and(p, criteriaBuilder.greaterThanOrEqualTo(root.get(key), (Comparable) value));
          break;
        case lte:
          p = criteriaBuilder.and(p, criteriaBuilder.lessThanOrEqualTo(root.get(key), (Comparable) value));
          break;
        case timestamp_gte:
          p = criteriaBuilder.and(p, criteriaBuilder.greaterThanOrEqualTo(root.get(key), (Timestamp) value));
          break;
        case timestamp_lte:
          p = criteriaBuilder.and(p, criteriaBuilder.lessThanOrEqualTo(root.get(key), (Timestamp) value));
          break;
      }
    }
    criteriaQuery.where(p);
    return session.createQuery(criteriaQuery).getResultList();
  }

  @Override
  public <T> Boolean update(T obj, Session session) {
    session.update(obj);
    return true;
  }

  @Override
  public <T> Boolean deleteById(Class<T> type, long id, Session session) {
    if (id != 0) {
      log.info("Deleting object with Id=" + id);
      T obj = getById(type, id, session);
      if (obj != null) {
        session.delete(obj);
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  @Override
  public <T> Boolean deleteById(Class<T> type, String id, Session session) {
    if (id != null) {
      log.info("Deleting object with Id: {}" + id);
      T obj = getById(type, id, session);
      if (obj != null) {
        session.delete(obj);
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  @Override
  public <T> Boolean delete(T obj, Class<T> type, Session session) {
    if (obj != null) {
      log.info("Deleting object");
      session.delete(obj);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Use Criteria Builder to execute a SQL By Parameters.
   *
   * @param type
   * @param params
   * @param session
   * @param <T>
   * @return List of results
   */
  public <T> List<T> getByCriteria(Class<T> type, @NonNull Map<String, Object> params, Session session) {
    try {
      CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
      CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(type);
      Root<T> root = criteriaQuery.from(type);
      criteriaQuery.select(root);
      AtomicReference<Predicate> p = new AtomicReference<>(criteriaBuilder.conjunction());
      params.entrySet().stream().forEach(entry -> {
        p.set(criteriaBuilder.and(p.get(), criteriaBuilder.equal(root.get(entry.getKey()), entry.getValue())));
      });
      criteriaQuery.where(p.get());
      return session.createQuery(criteriaQuery).getResultList();
    } catch (Exception e) {
      log.error("getByCriteria:: exception", e);
      return null;
    }
  }
}

