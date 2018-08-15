package com.company.jobServer.controllers;

import com.company.jobServer.JobServer;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.function.Consumer;

@Slf4j
public class BaseController {
  public void doWork(Consumer<Session> consumer) throws Exception {
    try (Session session = JobServer.sessionFactory.openSession()) {
      Transaction transaction = session.beginTransaction();
      try {
        // call consumer
        consumer.accept(session);
        transaction.commit();
      } catch (Exception x) {
        log.error("Failed to execute transaction", x);
        transaction.rollback();
        throw x;
      }
    }
  }
}
