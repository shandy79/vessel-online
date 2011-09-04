package org.vesselonline.dao;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * From <a href="http://www.hibernate.org/114.html">hibernate.org - Using Hibernate with Tomcat</a>
 */
public class HibernateListener implements ServletContextListener {
  // Call static initializer of HibernateUtil upon context initialization 
  public void contextInitialized(ServletContextEvent e) {
    HibernateUtil.getSessionFactory();    
  }

  // Free all resources upon context destruction
  public void contextDestroyed(ServletContextEvent e) {
    HibernateUtil.getSessionFactory().close();
  }
}
