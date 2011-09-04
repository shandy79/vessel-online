package org.vesselonline.draftroom.dao.hibernate;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.vesselonline.draftroom.api.Position;
import org.vesselonline.draftroom.beans.PositionBean;
import org.vesselonline.draftroom.dao.PositionDAO;
import org.vesselonline.hibernate.GenericHibernateDAO;

/**
 * Adapted from <a href="http://www.hibernate.org/328.html">hibernate.org - Generic Data Access Objects</a>
 */
public class PositionHibernateDAO extends GenericHibernateDAO<PositionBean, Long> implements PositionDAO {
  /** Returns all Positions, sorted in ascending roster order. */
  public List<Position> findAllSorted() {
    List<PositionBean> list = (List<PositionBean>) getSession().createCriteria(PositionBean.class)
                                                               .addOrder(Order.asc("roster_order")).list();
    return new ArrayList<Position>(list);
  }

  /** Returns a count of the total number of Positions available in this league. */
  public int getCount() {
    Query qry = getSession().createQuery("SELECT Count(*) FROM Position");
    Integer count = (Integer) qry.uniqueResult();

    return count;
  }
}
