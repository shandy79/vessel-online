package org.vesselonline.draftroom.dao.hibernate;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.vesselonline.draftroom.api.Owner;
import org.vesselonline.draftroom.beans.OwnerBean;
import org.vesselonline.draftroom.dao.OwnerDAO;
import org.vesselonline.hibernate.GenericHibernateDAO;

public class OwnerHibernateDAO extends GenericHibernateDAO<OwnerBean, Long> implements OwnerDAO {
  /** Returns all Owners, sorted in ascending order by last name, first name. */
  public List<Owner> findAllSorted() {
    List<OwnerBean> list = (List<OwnerBean>) getSession().createCriteria(OwnerBean.class)
                                                         .addOrder(Order.asc("lastname"))
                                                         .addOrder(Order.asc("firstname")).list();
    return new ArrayList<Owner>(list);
  }

  /** Returns a count of the total number of Owners in this league. */
  public int getCount() {
    Query qry = getSession().createQuery("SELECT Count(*) FROM Owner");
    Integer count = (Integer) qry.uniqueResult();

    return count;
  }
}
