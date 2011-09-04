package org.vesselonline.mail;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class MailChecker extends JFrame implements ChangeListener {
  public MailChecker() {
    // Create and set up the window
    super("EmailChecker 3.2");

    // Create and set up the content panels, add to the tabbed pane
    JTabbedPane tabPane = new JTabbedPane();
    tabPane.addChangeListener(this);
    tabPane.setFont(new Font("SansSerif", Font.PLAIN, 11));

    MailAccount acct = new MailAccount("<display_name>", "imap", true, "<server_name>", "<account_name>", 11);
    JPanel contentPanel = new MailDisplay(0, acct);
    tabPane.addTab(acct.getAccount(), contentPanel);

    // Add the panel to the window
    add(tabPane, BorderLayout.CENTER);
  }

  public void stateChanged(ChangeEvent e) { toFront(); }
  
  /** For thread safety, this method should be invoked from the event-dispatching thread. */
  private static void createAndShowGUI() {
    // Make sure we have nice window decorations
    JFrame.setDefaultLookAndFeelDecorated(true);
    JDialog.setDefaultLookAndFeelDecorated(true);

    MailChecker mc = new MailChecker();
    mc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mc.pack();
    mc.setVisible(true);
  }

  public static void main(String[] args) {
    // Schedule a job for the event-dispatching thread:  create and show this application's GUI
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        createAndShowGUI();
      }
    });
  }
}
