package com.swimshower.dao;

import java.util.List;
import org.vesselonline.dao.GenericDAO;
import com.swimshower.model.Song;

public interface SongDAO extends GenericDAO<Song, Long> {
  List<Song> findAllSorted();
  List<Song> findRecentSorted();
  Song findMostRecent();
  int getCount();
  long getPreviousId(long currentId);
  long getNextId(long currentId);
}
