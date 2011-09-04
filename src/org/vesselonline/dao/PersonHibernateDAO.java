package org.vesselonline.dao;

import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Order;
import org.vesselonline.model.Person;

/**
 * Adapted from <a href="http://www.hibernate.org/328.html">hibernate.org - Generic Data Access Objects</a>
 */
public class PersonHibernateDAO extends GenericHibernateDAO<Person, Long> implements PersonDAO {
  /** Returns a Person identified by the supplied username. */
  public Person findByUsername(String username) {
    Person entity = (Person) getSession().createQuery("FROM Person WHERE username = '" + username  + "'").uniqueResult();
    // Force initialization of Roles for user
    Hibernate.initialize(entity.getRoles());
    
    return entity;
  }

  /** Returns all Persons, sorted by last name, then first name. */
  public List<Person> findAllSorted() {
    List<Person> persons = (List<Person>) getSession().createCriteria(Person.class).addOrder(Order.asc("lastname"))
                                                      .addOrder(Order.asc("firstname")).list();
    return persons;
  }
}
