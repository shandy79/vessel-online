package org.vesselonline.jai.overlay;

import javax.swing.JViewport;

public interface SourceImagePanel {
  SourceImage getSourceImage();
  void setSourceImage(SourceImage sourceImage);
  void setSourceImage(String sourceImageFilePath);
  JViewport getViewport();
  void resetImagePanel();
  void importSettings(SourceImage sourceImage, JViewport viewport);

  void addSourceImagePanelListener(SourceImagePanelListener sipl);
  void removeSourceImagePanelListener(SourceImagePanelListener sipl);

  void notifyImageUpdated();
//  void notifyMessageOutput(Object iMessage, String iTitle, int iMessageType);
//  void notifyExceptionThrown(Throwable iThrowable, String iMessage);
}
