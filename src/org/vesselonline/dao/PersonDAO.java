package org.vesselonline.dao;

import java.util.List;
import org.vesselonline.dao.GenericDAO;
import org.vesselonline.model.Person;

public interface PersonDAO extends GenericDAO<Person, Long> {
  Person findByUsername(String username);
  List<Person> findAllSorted();
}
