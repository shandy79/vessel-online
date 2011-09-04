package org.vesselonline.draftroom.dao.hibernate;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.vesselonline.draftroom.api.Team;
import org.vesselonline.draftroom.beans.TeamBean;
import org.vesselonline.draftroom.dao.TeamDAO;
import org.vesselonline.hibernate.GenericHibernateDAO;

/**
 * Adapted from <a href="http://www.hibernate.org/328.html">hibernate.org - Generic Data Access Objects</a>
 */
public class TeamHibernateDAO extends GenericHibernateDAO<TeamBean, Long> implements TeamDAO {
  /** Returns all Teams, sorted by city, name in ascending order. */
  public List<Team> findAllSorted() {
    List<TeamBean> list = (List<TeamBean>) getSession().createCriteria(TeamBean.class)
                                                       .addOrder(Order.asc("city"))
                                                       .addOrder(Order.asc("name")).list();
    return new ArrayList<Team>(list);
  }

  /** Returns a count of the total number of Teams. */
  public int getCount() {
    Query qry = getSession().createQuery("SELECT Count(*) FROM Team");
    Integer count = (Integer) qry.uniqueResult();

    return count;
  }
}
