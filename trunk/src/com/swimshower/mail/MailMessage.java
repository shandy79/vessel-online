package com.swimshower.mail;

import java.util.Date;

public class MailMessage {
  private int messageNumber;
  private String from;
  private String to;
  private String subject;
  private String content;
  private Date sentDate;
  private boolean isRecent;

  public int getMessageNumber() { return messageNumber; }
  public void setMessageNumber(int messageNumber) { this.messageNumber = messageNumber; }

  public String getTo() { return to; }
  public void setTo(String to) { this.to = to; }

  public String getFrom() { return from; }
  public void setFrom(String from) { this.from = from; }

  public String getSubject() { return subject; }
  public void setSubject(String subject) { this.subject = subject; }

  public String getContent() { return content; }
  public void setContent(String content) { this.content = content; }

  public Date getSentDate() { return sentDate; }
  public void setSentDate(Date sentDate) { this.sentDate = sentDate; }

  public boolean isRecent() { return isRecent; }
  public void setRecent(boolean isRecent) { this.isRecent = isRecent; }
}
