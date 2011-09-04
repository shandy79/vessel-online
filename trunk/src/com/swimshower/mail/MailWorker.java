package com.swimshower.mail;

import java.util.ArrayList;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import com.swimshower.model.SwimShowerResource;

public class MailWorker {

  public static ArrayList<MailMessage> retrieveMessages() throws MessagingException {
    MailAccount acct = new MailAccount();
    Session session = Session.getInstance(acct.getProperties());
    MailMessage msg;

    // Get the store for the provider specified
    Store store = session.getStore(acct.getProvider());

    // Connect to the store
    store.connect(acct.getHost(), acct.getUsername(), acct.getPassword());

    // Get folder and retrieve messages
    Folder folder = store.getFolder(acct.getFolder());
    folder.open(Folder.READ_ONLY);
    Message messages[] = folder.getMessages();
    ArrayList<MailMessage> msgs = new ArrayList<MailMessage>(messages.length);

    // Load ArrayList with retrieved messages
    for (int i = messages.length - 1; i >= 0; i--) {
      msg = new MailMessage();
      msg.setMessageNumber(messages[i].getMessageNumber());
      msg.setFrom(messages[i].getFrom()[0].toString());
      msg.setSubject(messages[i].getSubject());
      msg.setSentDate(messages[i].getSentDate());
      if (messages[i].isSet(Flags.Flag.RECENT)) msg.setRecent(true);
      
      msgs.add(msg);
    } 
        
    // Close connection
    folder.close(false);
    store.close();

    return msgs;
  } 

  public static void sendMessage(MailMessage msg) throws MessagingException {
    MailAccount acct = new MailAccount();
    Session session = Session.getInstance(acct.getProperties());

    // Define message to be sent based on MailMessage object
    MimeMessage message = new MimeMessage(session);
    message.setFrom(new InternetAddress(msg.getFrom()));
    message.addRecipient(Message.RecipientType.TO, new InternetAddress(msg.getTo()));
    message.setSubject(msg.getSubject());
    message.setText(msg.getContent());

    // Send message
    Transport.send(message);
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
  // TODO Auto-generated method stub
    try {
      ArrayList<MailMessage> msgs = MailWorker.retrieveMessages();
      for (MailMessage msg : msgs) {
        System.out.println(msg.getMessageNumber() + "\t" + msg.getFrom() + "\t" +
                           msg.getSubject() + "\t" + SwimShowerResource.DATE_FORMAT.format(msg.getSentDate()));
      }
      MailMessage msg2 = new MailMessage();
      msg2.setTo("agbadza79@yahoo.com");
      msg2.setFrom("theband@swimshower.com");
      msg2.setSubject("test from Swim Shower app");
      msg2.setContent("This is a test.");
      MailWorker.sendMessage(msg2);
    } catch (MessagingException me) {
      me.printStackTrace();
    }
  }

}
