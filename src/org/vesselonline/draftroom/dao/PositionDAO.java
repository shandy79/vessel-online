package org.vesselonline.draftroom.dao;

import java.util.List;
import org.vesselonline.draftroom.api.Position;
import org.vesselonline.draftroom.beans.PositionBean;
import org.vesselonline.hibernate.GenericDAO;

public interface PositionDAO extends GenericDAO<PositionBean, Long> {
  List<Position> findAllSorted();
  int getCount();
}
