package org.vesselonline.draftroom.dao.hibernate;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.vesselonline.draftroom.api.FantasyTeam;
import org.vesselonline.draftroom.beans.FantasyTeamBean;
import org.vesselonline.draftroom.dao.FantasyTeamDAO;
import org.vesselonline.hibernate.GenericHibernateDAO;

/**
 * Adapted from <a href="http://www.hibernate.org/328.html">hibernate.org - Generic Data Access Objects</a>
 */
public class FantasyTeamHibernateDAO extends GenericHibernateDAO<FantasyTeamBean, Long> implements FantasyTeamDAO {
  /** Returns all Fantasy Teams, sorted by city, name in ascending order. */
  public List<FantasyTeam> findAllSorted() {
    List<FantasyTeamBean> list = (List<FantasyTeamBean>) getSession().createCriteria(FantasyTeamBean.class)
                                                                     .addOrder(Order.asc("city"))
                                                                     .addOrder(Order.asc("name")).list();
    return new ArrayList<FantasyTeam>(list);
  }

  /** Returns a count of the total number of Fantasy Teams. */
  public int getCount() {
    Query qry = getSession().createQuery("SELECT Count(*) FROM FantasyTeam");
    Integer count = (Integer) qry.uniqueResult();

    return count;
  }
}
