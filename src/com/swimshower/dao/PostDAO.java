package com.swimshower.dao;

import java.util.List;
import org.vesselonline.dao.GenericDAO;
import com.swimshower.model.Post;

public interface PostDAO extends GenericDAO<Post, Long> {
  List<Post> findAllSorted();
  List<Post> findRecentSorted();
  Post findMostRecent();
  int getCount();
  long getPreviousId(long currentId);
  long getNextId(long currentId);
}
