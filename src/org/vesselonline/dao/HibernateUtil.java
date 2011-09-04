package org.vesselonline.dao;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * From <a href="http://www.hibernate.org/42.html">hibernate.org - Sessions and transactions</a>
 */
public class HibernateUtil {
  private static final SessionFactory sessionFactory;

  static {
    try {
      // Create the SessionFactory from hibernate.cfg.xml
      sessionFactory = new Configuration().configure().buildSessionFactory();
    } catch (Throwable ex) {
      // Make sure you log the exception, as it might be swallowed
      System.err.println("Initial SessionFactory creation failed." + ex);
      throw new ExceptionInInitializerError(ex);
    }
  }

  public static SessionFactory getSessionFactory() { return sessionFactory; }
}
