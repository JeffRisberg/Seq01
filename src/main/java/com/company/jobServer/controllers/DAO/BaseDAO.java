package com.company.jobServer.controllers.DAO;

import com.company.jobServer.FilterDescription;
import lombok.NonNull;
import org.hibernate.Session;

import java.util.List;
import java.util.Map;

public interface BaseDAO {
  <T> T create(T obj, Class<T> type, Session session);

  <T> T getById(Class<T> type, long id, Session session);

  <T> List<T> getBySQL(Class<T> type, @NonNull String sql, @NonNull Map<String, Object> params, Session session);

  <T> List<T> getByCriteria(Class<T> type, List<FilterDescription> filterDescriptions, Session session) throws Exception;

  int updateBySQL(@NonNull String sql, @NonNull Map<String, Object> params, Session session);

  <T> T getById(Class<T> type, String id, Session session);

  <T> Boolean update(T obj, Session session);

  <T> Boolean deleteById(Class<T> type, long id, Session session);

  <T> Boolean deleteById(Class<T> type, String id, Session session);

  <T> Boolean delete(T obj, Class<T> type, Session session);

  <T> List<T> listAll(Class<T> type, Session externalSession);
}
