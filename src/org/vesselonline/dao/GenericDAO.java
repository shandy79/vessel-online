package org.vesselonline.dao;

import java.io.Serializable;
import java.util.List;

/**
 * From <a href="http://www.hibernate.org/328.html">hibernate.org - Generic Data Access Objects</a>
 *
 * @param <T>  Java type for the concrete class to be represented
 * @param <ID>  Java type for the identifier field for the concrete class
 */
public interface GenericDAO<T, ID extends Serializable> {
  T findById(ID id, boolean lock);
  List<T> findAll();
  List<T> findByExample(T exampleInstance, String[] excludeProperty);
  T makePersistent(T entity);
  void makeTransient(T entity);
}
