package org.vesselonline.draftroom.dao.hibernate;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.vesselonline.draftroom.api.Player;
import org.vesselonline.draftroom.beans.PlayerBean;
import org.vesselonline.draftroom.dao.PlayerDAO;
import org.vesselonline.draftroom.util.DraftRoomUtil;
import org.vesselonline.hibernate.GenericHibernateDAO;

/**
 * Adapted from <a href="http://www.hibernate.org/328.html">hibernate.org - Generic Data Access Objects</a>
 */
public class PlayerHibernateDAO extends GenericHibernateDAO<PlayerBean, Long> implements PlayerDAO {
  /** Returns all Players, sorted by ID in descending order. */
  public List<Player> findAllSorted() {
    List<PlayerBean> list = (List<PlayerBean>) getSession().createCriteria(PlayerBean.class)
                                                           .addOrder(Order.desc("id")).list();
    return new ArrayList<Player>(list);
  }

  /** Returns recent Players, sorted by ID in descending order. */
  public List<Player> findRecentSorted() {
    List<PlayerBean> list =  (List<PlayerBean>) getSession().createCriteria(PlayerBean.class)
                                                            .addOrder(Order.desc("id"))
                                                            .setMaxResults(DraftRoomUtil.getInstance().getRecentItemCount()).list();
    return new ArrayList<Player>(list);
  }

  /** Returns most recent Player, or an empty Player if none have been made. */
  public Player findMostRecent() {
    Player mostRecent = (PlayerBean) getSession().createCriteria(PlayerBean.class).addOrder(Order.desc("id"))
                                                 .setMaxResults(1).uniqueResult();
    return mostRecent;
  }

  /** Returns a count of the total number of Players. */
  public int getCount() {
    Query qry = getSession().createQuery("SELECT Count(*) FROM Player");
    Integer count = (Integer) qry.uniqueResult();

    return count;
  }

  public long getPreviousId(long currentId) {
    Query qry = getSession().createQuery("SELECT Max(id) FROM Player WHERE id < " + currentId);
    Long previousId = (Long) qry.uniqueResult();

    return (previousId == null) ? -1 : previousId;
  }

  public long getNextId(long currentId) {
    Query qry = getSession().createQuery("SELECT Min(id) FROM Player WHERE id > " + currentId);
    Long nextId = (Long) qry.uniqueResult();

    return (nextId == null) ? -1 : nextId;
  }
}
