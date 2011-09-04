package com.swimshower.dao;

import java.util.List;
import org.vesselonline.dao.GenericDAO;
import org.vesselonline.model.Comment;
import org.vesselonline.model.Person;
import com.swimshower.model.Post;

public interface ProfileDAO extends GenericDAO<Person, Long> {
  int getCommentCount(Long id);
  List<Post> findPersonPosts(Long id);
  Comment getLatestPostComment(Long id);
}
