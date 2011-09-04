package org.vesselonline.draftroom.dao;

import java.util.List;
import org.vesselonline.draftroom.api.Team;
import org.vesselonline.draftroom.beans.TeamBean;
import org.vesselonline.hibernate.GenericDAO;

public interface TeamDAO extends GenericDAO<TeamBean, Long> {
  List<Team> findAllSorted();
  int getCount();
}
