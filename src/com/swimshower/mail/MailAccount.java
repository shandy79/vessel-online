package com.swimshower.mail;

import java.util.Properties;

public class MailAccount {
  private String account = "Swim Shower";
  private String provider = "pop3";
  private boolean secure = false;
  private String host = "mail.swimshower.com";
  private String smtpHost = "smtpout.secureserver.net";
  private String username = "theband@swimshower.com";
  private String password = "jicPosse4eva";
  private final String folder = "INBOX";
  private final Properties props = new Properties();

  private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

  public MailAccount() {
    setProperties();
  }

  public String getAccount() { return account; }
  public String getProvider() { return provider; }
  public boolean isSecure() { return secure; }
  public String getHost() { return host; }
  public String getSmtpHost() { return smtpHost; }
  public String getUsername() { return username; }
  protected String getPassword() { return password; }
  public String getFolder() { return folder; }
  protected Properties getProperties() { return props; }

  /** Need to override default JavaMail properties for secure connections. */
  private void setProperties() {
    if (isSecure()) {
       if (getProvider().equals("imap")) {
        props.setProperty("mail.imap.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.imap.socketFactory.fallback", "false");
        props.setProperty("mail.imap.port", "993");
        props.setProperty("mail.imap.socketFactory.port", "993");
      } else if (getProvider().equals("pop3")) {
        props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.pop3.socketFactory.fallback", "false");
        props.setProperty("mail.pop3.port", "995");
        props.setProperty("mail.pop3.socketFactory.port", "995");
      }
    } else props.clear();

    props.setProperty("mail.smtp.host", smtpHost);
  }
}
