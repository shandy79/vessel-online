package com.swimshower.dao;

import java.util.Date;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.vesselonline.dao.GenericHibernateDAO;
import com.swimshower.model.Event;
import com.swimshower.model.SwimShowerResource;

/**
 * Adapted from <a href="http://www.hibernate.org/328.html">hibernate.org - Generic Data Access Objects</a>
 */
public class EventHibernateDAO extends GenericHibernateDAO<Event, Long> implements EventDAO {
  /** Returns all Events, sorted by date in descending order. */
  public List<Event> findAllSorted() {
    return (List<Event>) getSession().createCriteria(Event.class).addOrder(Order.desc("date")).list();
  }

  /** Returns upcoming Events, sorted by date in ascending order. */
  public List<Event> findUpcomingSorted() {
    Query qry = getSession().createQuery("FROM Event WHERE date >= current_date() ORDER BY date ASC");
    qry.setMaxResults(SwimShowerResource.RECENT_ITEM_COUNT);

    return (List<Event>) qry.list(); 
  }

  /** Returns the next upcoming Event, or an empty Event if none exist. */
  public Event findNextUpcoming() {
    Query qry = getSession().createQuery("FROM Event WHERE date >= current_date() ORDER BY date ASC");
    qry.setMaxResults(1);
    Event nextUpcoming = (Event) qry.uniqueResult();

    return nextUpcoming;
  }

  /** Returns a count of the total number of Events. */
  public int getCount() {
    Query qry = getSession().createQuery("SELECT Count(*) FROM Event");
    Integer eventCount = (Integer) qry.uniqueResult();

    return eventCount;
  }


  public long getPreviousId(Date currentDate) {
    Query qry = getSession().createQuery("SELECT id FROM Event WHERE date < '" + currentDate + "' ORDER BY date DESC");
    qry.setMaxResults(1);
    Long previousId = (Long) qry.uniqueResult();

    return (previousId == null) ? -1 : previousId;
  }

  public long getNextId(Date currentDate) {
    Query qry = getSession().createQuery("SELECT id FROM Event WHERE date > '" + currentDate + "' ORDER BY date ASC");
    qry.setMaxResults(1);
    Long nextId = (Long) qry.uniqueResult();

    return (nextId == null) ? -1 : nextId;
  }
}
