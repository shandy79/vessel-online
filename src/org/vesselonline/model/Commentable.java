package org.vesselonline.model;

import java.util.List;

public interface Commentable {
  List<Comment> getComments();
  void addComment(Comment c);
  void removeComment(Comment c);
  Comment getLatestComment();
  int getCommentCount();
}
