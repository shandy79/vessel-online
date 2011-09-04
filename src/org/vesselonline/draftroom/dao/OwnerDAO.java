package org.vesselonline.draftroom.dao;

import java.util.List;
import org.vesselonline.draftroom.api.Owner;
import org.vesselonline.draftroom.beans.OwnerBean;
import org.vesselonline.hibernate.GenericDAO;

public interface OwnerDAO extends GenericDAO<OwnerBean, Long> {
  List<Owner> findAllSorted();
  int getCount();
}
