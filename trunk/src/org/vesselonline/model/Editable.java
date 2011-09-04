package org.vesselonline.model;

import java.util.Date;

public interface Editable {
  Date getEdited();
  void setEdited(Date edited);
  Person getEditor();
  void setEditor(Person editor);
}
