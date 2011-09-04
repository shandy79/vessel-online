package com.swimshower.dao;

import java.util.List;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.vesselonline.dao.GenericHibernateDAO;
import com.swimshower.model.Song;
import com.swimshower.model.SwimShowerResource;

/**
 * Adapted from <a href="http://www.hibernate.org/328.html">hibernate.org - Generic Data Access Objects</a>
 */
public class SongHibernateDAO extends GenericHibernateDAO<Song, Long> implements SongDAO {
  /** Returns all Records, sorted by ID in descending order. */
  public List<Song> findAllSorted() {
    return (List<Song>) getSession().createCriteria(Song.class).addOrder(Order.asc("title")).list();
  }

  /** Returns recent Records, sorted by ID in descending order. */
  public List<Song> findRecentSorted() {
    return (List<Song>) getSession().createCriteria(Song.class).addOrder(Order.desc("id"))
                                      .setMaxResults(SwimShowerResource.RECENT_ITEM_COUNT).list();
  }

  /** Returns most recent Song, or an empty Song if none have been made. */
  public Song findMostRecent() {
    Song mostRecent = (Song) getSession().createCriteria(Song.class).addOrder(Order.desc("id"))
                                             .setMaxResults(1).uniqueResult();
    
    return mostRecent;
  }

  /** Returns a count of the total number of Songs. */
  public int getCount() {
    Query qry = getSession().createQuery("SELECT Count(*) FROM Song");
    Integer songCount = (Integer) qry.uniqueResult();

    return songCount;
  }

  public long getPreviousId(long currentId) {
    Query qry = getSession().createQuery("SELECT Max(id) FROM Song WHERE id < " + currentId);
    Long previousId = (Long) qry.uniqueResult();

    return (previousId == null) ? -1 : previousId;
  }

  public long getNextId(long currentId) {
    Query qry = getSession().createQuery("SELECT Min(id) FROM Song WHERE id > " + currentId);
    Long nextId = (Long) qry.uniqueResult();

    return (nextId == null) ? -1 : nextId;
  }
}
