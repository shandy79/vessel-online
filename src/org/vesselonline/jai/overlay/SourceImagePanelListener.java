package org.vesselonline.jai.overlay;

public interface SourceImagePanelListener {
  void addSourceImagePanel(SourceImagePanel sip);
  void removeSourceImagePanel(SourceImagePanel sip);

  void imageUpdated();
//  void messageOutput(Object iMessage, String iTitle, int iMessageType);
//  void exceptionThrown(Throwable iThrowable, String iMessage);
}
