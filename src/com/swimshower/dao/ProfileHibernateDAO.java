package com.swimshower.dao;

import java.util.List;
import org.hibernate.Query;
import org.vesselonline.dao.GenericHibernateDAO;
import org.vesselonline.model.Comment;
import org.vesselonline.model.Person;
import com.swimshower.model.Post;

/**
 * Adapted from <a href="http://www.hibernate.org/328.html">hibernate.org - Generic Data Access Objects</a>
 * Would like to have this extend PersonHibernateDAO, but constructor for GenericHibernateDAO relies on
 * the implementing class being a direct subclass of GenericHibernateDAO with getGenericSuperclass().
 */
public class ProfileHibernateDAO extends GenericHibernateDAO<Person, Long> implements ProfileDAO {
  public int getCommentCount(Long id) {
    Query qry = getSession().createQuery("SELECT Count(*) FROM post_comment WHERE contributor = " + id);
    int cmntCount = (Integer) qry.uniqueResult();

    return cmntCount;
  }

  public List<Post> findPersonPosts(Long id) {
    Query qry = getSession().createQuery("FROM post WHERE contributor = " + id);
    return (List<Post>) qry.list();
  }

  public Comment getLatestPostComment(Long id) {
    Query qry = getSession().createQuery("SELECT Max(post_comment_id) FROM post_comment WHERE contributor = " + id);
    Long maxCmntId = (Long) qry.uniqueResult();

    Comment cmnt = new Comment();
    getSession().load(cmnt, maxCmntId);

    return cmnt;
  }
}
