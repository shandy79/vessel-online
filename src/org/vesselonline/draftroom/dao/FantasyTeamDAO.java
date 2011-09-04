package org.vesselonline.draftroom.dao;

import java.util.List;
import org.vesselonline.draftroom.api.FantasyTeam;
import org.vesselonline.draftroom.beans.FantasyTeamBean;
import org.vesselonline.hibernate.GenericDAO;

public interface FantasyTeamDAO extends GenericDAO<FantasyTeamBean, Long> {
  List<FantasyTeam> findAllSorted();
  int getCount();
}
