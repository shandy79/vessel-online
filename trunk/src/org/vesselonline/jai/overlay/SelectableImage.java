package org.vesselonline.jai.overlay;

import java.awt.Rectangle;

public interface SelectableImage {
  Rectangle getSelection();
  void setSelection(Rectangle selection);

  void addSelectableImageListener(SelectableImageListener sipl);
  void removeSelectableImageListener(SelectableImageListener sipl);
  void notifySelectionUpdated();
}
