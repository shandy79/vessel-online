package org.vesselonline.draftroom.dao;

import java.util.List;
import org.vesselonline.draftroom.api.Player;
import org.vesselonline.draftroom.beans.PlayerBean;
import org.vesselonline.hibernate.GenericDAO;

public interface PlayerDAO extends GenericDAO<PlayerBean, Long> {
  List<Player> findAllSorted();
  List<Player> findRecentSorted();
  Player findMostRecent();
  int getCount();
  long getPreviousId(long currentId);
  long getNextId(long currentId);
}
