package com.swimshower.beans;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.vesselonline.beans.CommentableBean;
import org.vesselonline.beans.VesselJSFBean;
import org.vesselonline.model.Comment;
import org.vesselonline.model.Person;
import com.swimshower.dao.SongDAO;
import com.swimshower.dao.SongHibernateDAO;
import com.swimshower.model.Song;
import com.swimshower.model.SwimShowerResource;

public class SongBean extends CommentableBean implements VesselJSFBean {
  private static final String songDir = SwimShowerResource.SERVER_DIR + SwimShowerResource.UPLOAD_DIR +
                                        File.separator + "song";

  private Song song;
  private SongDAO songDAO = new SongHibernateDAO();
  private UploadedFile songFile;

  public SongBean() {
    super();
    song = new Song();
  }

  public UploadedFile getSongFile() { return songFile; }
  public void setSongFile(UploadedFile songFile) { this.songFile = songFile; }

  public String getId() { return Long.toString(song.getId()); }
  public void setId(String id) { song = songDAO.findById(new Long(id), false); }

  public String getTitle() { return song.getTitle(); }
  public void setTitle(String title) { song.setTitle(title.trim()); }

  public String getLength() { return song.getSubject(); }
  public void setLength(String length) { song.setSubject(length.trim()); }

  public String getDescription() { return song.getDescription(); }
  public void setDescription(String description) { song.setDescription(description.trim()); }

  public String getDate() {
    if (song.getDate() != null) {
      return SwimShowerResource.DATE_FORMAT.format(song.getDate());
    } else {
      return "";
    }
  }

  public String getSource() {
    if (song.getSource() != null) {
      return song.getSource().toExternalForm();
    } else {
      return "";
    }
  }
  public void setSource(String source) {
    try {
      song.setSource(new URL(source.trim()));
    } catch (MalformedURLException murle) {
      song.setSource(null);
    }
  }

  public String getAuthor() { return song.getCreator(); }
  public void setAuthor(String author) { song.setCreator(author.trim()); }

  public String getSizeInMegabytes() { return Float.toString(song.getSizeInMegabytes()); }
  public void setSizeInMegabytes(String sizeInMegabytes) {
    try {
      song.setSizeInMegabytes(new Float(sizeInMegabytes));
    } catch (Exception e) { }
  }

  public String getLyrics() { return song.getLyrics(); }
  public void setLyrics(String lyrics) { song.setLyrics(lyrics.trim()); }

  public Person getContributor() {
    if (song.getContributor() != null) {
      return song.getContributor();
    } else {
      return new Person();
    }
  }

  public String getEditor() {
    if (song.getEditor() != null) {
      return song.getEditor().getUsername();
    } else {
      return "";
    }
  }

  public String getEdited() {
    if (song.getEdited() != null) {
      return SwimShowerResource.DATE_FORMAT.format(song.getEdited());
    } else {
      return "";
    }
  }

  private boolean storeSongFile() {
    if (songFile == null) return true;

    String ext = songFile.getName().substring(songFile.getName().lastIndexOf('.') + 1);
    if (! ext.toLowerCase().equals("mp3")) return false; 

    File f = new File(songDir + File.separator + songFile.getName());
    byte[] b = new byte[1024];

    try {
      System.gc();
      BufferedInputStream in = new BufferedInputStream(songFile.getInputStream());
      BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));

      int i = in.read(b);
      while (i != -1) {
        out.write(b);
        i = in.read(b);
      }
      out.flush();
      out.close();
      in.close();
      System.gc();

      setSource(SwimShowerResource.SERVER_PREFIX + SwimShowerResource.UPLOAD_DIR + "/song/" + songFile.getName());

      DecimalFormat df = new DecimalFormat("#.#");
      setSizeInMegabytes(df.format(songFile.getSize() / 1048576.0));
      return true;
    } catch (IOException ioe) {
      System.out.println("Unable to save song file: " + f);
      ioe.printStackTrace(System.out);
      return false;
    }
  }

  private boolean storeSongContextItems() {
    // If the song file is not able to be stored, then cancel the update
    if (! storeSongFile()) return false;

    ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
    HttpSession session = (HttpSession) context.getSession(false);

    // For a new song
    if (song.getId() == 0)  {  
      if (session != null && session.getAttribute("user") != null) {
        song.setContributor((Person) session.getAttribute("user"));
        return true;
      } else {
        return false;
      }

    // For an edited song
    } else if (getComment() == null || getComment().equals("")) {
      if (session != null && session.getAttribute("user") != null) {
        song.setEditor((Person) session.getAttribute("user"));
        song.setEdited(new Date());
        return true;
      } else {
        return false;
      }

    } else {
      return true;
    }
  }

  public void setCommentToEdit(long commentToEdit) {
    this.commentToEdit = commentToEdit;

    if (commentToEdit > 0) {
      for (Comment c : song.getComments()) {
        if (commentToEdit == c.getId()) {
          comment = c;
          break;
        }
      }
    }
  }

  public List<Comment> getComments() { return song.getComments(); }

  protected boolean storeComment() {
    boolean result = storeCommentContextItems();

    if (result) {
      // For a new comment
      if (commentToEdit == 0) {
        comment.setDate(new Date());
        song.addComment(comment);

      // For an edited comment
      } else {
        for (Comment c : song.getComments()) {
          if (c.getId() == commentToEdit) {
            c.setDescription(getComment());
            c.setEdited(new Date());
            break;
          }
        }
      }

      songDAO.makePersistent(song);
    }

    // Fixes a bug with doubly displaying the comment just submitted, not sure why
    song.getCommentCount();
    // Must clear to prevent display on next presentation of song.jsp
    comment = new Comment();
    commentToEdit = 0;
    return result;
  }

  public int getCount() { return songDAO.getCount(); }
  public long getPreviousId() { return songDAO.getPreviousId(song.getId()); }
  public long getNextId() { return songDAO.getNextId(song.getId()); }

  // Method for the form action, returns a string used in navigation-rules
  public String navigate() {
    boolean result = false;

    // Always store the song
    result = storeSongContextItems();
    if (song.getDate() == null) song.setDate(new Date());
    if (result) songDAO.makePersistent(song);

    // For a new comment or an edited comment
    if (getComment() != null && ! getComment().equals("")) {
      result = storeComment();
    }

    return (result) ? "valid" : "invalid";
  }
}
