package com.swimshower.dao;

import java.util.Date;
import java.util.List;
import org.vesselonline.dao.GenericDAO;
import com.swimshower.model.Event;

public interface EventDAO extends GenericDAO<Event, Long> {
  List<Event> findAllSorted();
  List<Event> findUpcomingSorted();
  Event findNextUpcoming();
  int getCount();
  long getPreviousId(Date currentDate);
  long getNextId(Date currentDate);
}
