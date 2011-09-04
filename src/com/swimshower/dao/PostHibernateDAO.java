package com.swimshower.dao;

import java.util.List;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.vesselonline.dao.GenericHibernateDAO;
import com.swimshower.model.Post;
import com.swimshower.model.SwimShowerResource;

/**
 * Adapted from <a href="http://www.hibernate.org/328.html">hibernate.org - Generic Data Access Objects</a>
 */
public class PostHibernateDAO extends GenericHibernateDAO<Post, Long> implements PostDAO {
  /** Returns all Posts, sorted by ID in descending order. */
  public List<Post> findAllSorted() {
    return (List<Post>) getSession().createCriteria(Post.class).addOrder(Order.desc("id")).list();
  }

  /** Returns recent Posts, sorted by ID in descending order. */
  public List<Post> findRecentSorted() {
    return (List<Post>) getSession().createCriteria(Post.class).addOrder(Order.desc("id"))
                                    .setMaxResults(SwimShowerResource.RECENT_ITEM_COUNT).list();
  }

  /** Returns most recent Post, or an empty Post if none have been made. */
  public Post findMostRecent() {
    Post mostRecent = (Post) getSession().createCriteria(Post.class).addOrder(Order.desc("id"))
                                         .setMaxResults(1).uniqueResult();
    
    return mostRecent;
  }

  /** Returns a count of the total number of Posts. */
  public int getCount() {
    Query qry = getSession().createQuery("SELECT Count(*) FROM Post");
    Integer postCount = (Integer) qry.uniqueResult();

    return postCount;
  }

  public long getPreviousId(long currentId) {
    Query qry = getSession().createQuery("SELECT Max(id) FROM Post WHERE id < " + currentId);
    Long previousId = (Long) qry.uniqueResult();

    return (previousId == null) ? -1 : previousId;
  }

  public long getNextId(long currentId) {
    Query qry = getSession().createQuery("SELECT Min(id) FROM Post WHERE id > " + currentId);
    Long nextId = (Long) qry.uniqueResult();

    return (nextId == null) ? -1 : nextId;
  }
}
