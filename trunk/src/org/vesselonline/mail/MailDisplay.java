package org.vesselonline.mail;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Properties;
import java.util.Vector;
import javax.mail.AuthenticationFailedException;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class MailDisplay extends JPanel implements ActionListener {
  private JLabel acctLbl, timeLbl, msgLbl;
  private JTable tbl;
  private DefaultTableModel tmdl;
  private javax.swing.Timer timer;

  private Properties props = new Properties();
  private Session session;
  private Store store;
  private Folder folder;

  private int tabIndex;
  private MailAccount acct;
  private static final int ONE_MINUTE = 60000;
  private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
  private static final Font SMALL_FONT = new Font("SansSerif", Font.PLAIN, 11);

  public MailDisplay(int tabIndex, MailAccount acct) {
    super();
    this.tabIndex = tabIndex;
    this.acct = acct;

    // Create and set up the panel, add widgets
    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    this.addWidgets();

    // Set properties object and generate the mail Session
    this.setProperties();
    session = Session.getInstance(props);

    // Check mail by starting the timer with a small initial delay
    // Have to reset the initial delay due to use of the restart
    timer = new javax.swing.Timer(ONE_MINUTE * acct.getDelay(), this);
    timer.setInitialDelay(this.tabIndex * 20000);
    timer.start();
    timer.setInitialDelay(timer.getDelay());
  }

  private void addWidgets() {
    // Create the widgets
    JPanel paneTtl = new JPanel(new BorderLayout());
    acctLbl = new JLabel(acct.getUsername() + " - " + acct.getFolder());
    acctLbl.setFont(SMALL_FONT);
    timeLbl = new JLabel("-not yet checked-", JLabel.RIGHT);
    timeLbl.setFont(SMALL_FONT);
    paneTtl.add(acctLbl, BorderLayout.WEST);
    paneTtl.add(timeLbl, BorderLayout.EAST);

    // Build table to contain message headers, use TableModel so contents can be manipulated
    Vector<String> columnNames = new Vector<String>(3);
    columnNames.add("#");
    columnNames.add("From");
    columnNames.add("Subject");
    tmdl = new DefaultTableModel(null, columnNames);
    tbl = new JTable(tmdl);
    TableColumn col = tbl.getColumnModel().getColumn(0);
    col.setMaxWidth(24);
    col.setResizable(false);

    // Make table scrollable
    JScrollPane paneScr = new JScrollPane(tbl);
    tbl.setPreferredScrollableViewportSize(new Dimension(360, 80));

    JPanel paneSts = new JPanel(new BorderLayout());
    msgLbl = new JLabel("-not yet checked-");
    msgLbl.setFont(SMALL_FONT);

    JButton actionBtn = new JButton("Check Mail");
    actionBtn.setFont(SMALL_FONT);
    // Listen to events from the Check Mail button
    actionBtn.addActionListener(this);

    paneSts.add(msgLbl, BorderLayout.WEST);
    paneSts.add(actionBtn, BorderLayout.EAST);

    // Add the widgets to the container
    this.add(paneTtl);
    this.add(paneScr);
    this.add(paneSts);
  }

  public void actionPerformed(ActionEvent e) {
  	msgLbl.setText("Checking for messages . . .");

    final SwingWorker worker = new SwingWorker() {
      public Object construct() {
        try {
          MailDisplay.this.retrieveMsgs();
          timer.restart();
        } catch (MessagingException me1) {
          msgLbl.setText(me1.getMessage());

          try {
            if (folder != null) folder.close(false);
            if (store != null) store.close();
          } catch (MessagingException me2) {
            msgLbl.setText(me2.getMessage());
          }
        } finally {
          // Trigger visual notification of updates (unset selection first to make sure that
          // actual tab selection causes ChangeEvent)
          ((JTabbedPane) MailDisplay.this.getParent()).setSelectedIndex(-1);
          ((JTabbedPane) MailDisplay.this.getParent()).setSelectedIndex(MailDisplay.this.tabIndex);
        }

        return null;
      }
    };
    worker.start();
  }

  private String inputPassword() {
    return JOptionPane.showInputDialog("Please input your " + acct.getAccount() + " password, " + acct.getName() + ":");
  }

  private void retrieveMsgs() throws MessagingException {
    Vector<String> row;

    // If password has not yet been entered by the user, then prompt
    if (acct.getPassword() == null) acct.setPassword(inputPassword());

    // Get the store for the provider specified
    try {
      store = session.getStore(acct.getProvider());
    } catch (NoSuchProviderException nspe) {
      msgLbl.setText(nspe.getMessage());
      return;
    }

    // Connect to the store; catch exception and re-prompt for password if authentication fails
    try {
      store.connect(acct.getHost(), acct.getUsername(), acct.getPassword());
    } catch (AuthenticationFailedException afe) {
      acct.setPassword(null);
      msgLbl.setText(afe.getMessage());
      return;
    }

    // Get folder and retrieve messages
    folder = store.getFolder(acct.getFolder());
    folder.open(Folder.READ_ONLY);
    Message message[] = folder.getMessages();
    int newUnreadMsgCount = 0;

    // Empty the current table data, then reload with retrieved messages
    tmdl.setRowCount(0);
    for (int i = message.length - 1; i >= 0; i--) {
      row = new Vector<String>(3);
      row.add(0, Integer.toString(i + 1));
      row.add(1, message[i].getFrom()[0].toString());
      row.add(2, message[i].getSubject());
      tmdl.addRow(row);
      // If message is new since last time folder opened, then highlight.
      // 2nd part of || clause added to support Gmail recent message handling.
      // 2nd part of && clause added to support Exchange/Outlook recent message handling.
      if ((message[i].isSet(Flags.Flag.RECENT) || acct.getHost().equals("imap.gmail.com")) && ! message[i].isSet(Flags.Flag.SEEN)) {
        tbl.addRowSelectionInterval(message.length - (i + 1), message.length - (i + 1));
        newUnreadMsgCount++;
      }
    }

    // Update time and message count labels
    timeLbl.setText(getCurrentTime());
    // Replaced implementation using Folder.getNewMessageCount() and Folder.getUnreadMessageCount()
    // due to issues with performance and with Gmail and Exchange/Outlook use of Flags.
    msgLbl.setText(folder.getMessageCount() + " messages (" + newUnreadMsgCount + " new/unread)");

    // Close connection
    folder.close(false);
    store.close();
  }

  /** Construct formatted string for current date and time. */
  private String getCurrentTime() {
    Calendar now = Calendar.getInstance();
    String nowStr = "as of " + (now.get(Calendar.MONTH) + 1) + "/" + now.get(Calendar.DAY_OF_MONTH) +
                    "/" + now.get(Calendar.YEAR) + " " + now.get(Calendar.HOUR_OF_DAY) + ":";
    if (now.get(Calendar.MINUTE) < 10) nowStr += "0";
    nowStr += now.get(Calendar.MINUTE) + ":";
    if (now.get(Calendar.SECOND) < 10) nowStr += "0";
    nowStr += now.get(Calendar.SECOND);
    
    return nowStr;
  }

  /** Need to override default JavaMail properties for secure connections. */
  private void setProperties() {
    if (acct.isSecure()) {
       if (acct.getProvider().equals("imap")) {
        props.setProperty("mail.imap.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.imap.socketFactory.fallback", "false");
        props.setProperty("mail.imap.port", "993");
        props.setProperty("mail.imap.socketFactory.port", "993");
      } else if (acct.getProvider().equals("pop3")) {
        props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.pop3.socketFactory.fallback", "false");
        props.setProperty("mail.pop3.port", "995");
        props.setProperty("mail.pop3.socketFactory.port", "995");
      }
    } else props.clear();
  }
}
