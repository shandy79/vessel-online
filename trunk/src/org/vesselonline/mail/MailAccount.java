package org.vesselonline.mail;

public class MailAccount {
  private String account;
  private final String name = "Steve Handy";
  private String provider;
  private boolean secure;
  private String host;
  private String username;
  private String password = null;
  private final String folder = "INBOX";
  private int delay;

  public MailAccount(String account, String provider, boolean secure, String host, String username, int delay) {
    this.account = account;
    this.provider = provider;
    this.secure = secure;
    this.host = host;
    this.username = username;
    this.delay = delay;
  }

  public String getAccount() { return account; }
  public String getName() { return name; }
  public String getProvider() { return provider; }
  public boolean isSecure() { return secure; }
  public String getHost() { return host; }
  public String getUsername() { return username; }
  protected String getPassword() { return password; }
  protected void setPassword(String password) { this.password = password; }
  public String getFolder() { return folder; } 
  public int getDelay() { return delay; }
}
