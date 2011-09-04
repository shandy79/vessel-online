package org.vesselonline.science.eie_io;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class EieIo implements TableModelListener {
  private JFrame frame;
  private JPanel eiePnl;
  private   JPanel eqnPnl;
  private     JLabel eqnLbl;
  private   JPanel reactPnl;
  private     JLabel defLbl1, defLbl2;
  private     JFormattedTextField v16rFmtTxtFld, v18rFmtTxtFld;
  private   JPanel rdoPnl;
  private   ButtonGroup btnGrp;
  private     JRadioButton oxyRdoBtn, supRdoBtn, usrRdoBtn;
  private   JScrollPane tblScrPnl;
  private     EieTableModel tmdl;
  private     JTable pTbl;
  private   JPanel btnPnl;
  private     JButton eieBtn, rstBtn;
  private   JPanel calcPnl;
  private     JLabel zpeLbl, excLbl, mmiLbl, eieLbl;
  private Font stdFnt = new Font("Tahoma", 0, 11);
  // Default values for Oxygen
  private double v16r = 1556.3;
  private double v18r = 1512.5;
  // Values for Superoxide
  // v16r = 1064.8;
  // v18r = 1034.8;

  // Mathematical constants used in calculations
  static final double h = 6.62606876 * Math.pow(10, -34);
  static final double k = 1.3806503 * Math.pow(10, -23);
  static final double T = 298.00000;
  static final double c = 2.99792458 * Math.pow(10, 10);
  static final double hkT = 1 / 207.06;
  static final double h2kT = 1 / 414.3;

  private EieIo() {
    // Create and set up the window
    frame = new JFrame("EIE I/O");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Create and set up the panel
    eiePnl = new JPanel();
    eiePnl.setLayout(new BoxLayout(eiePnl, BoxLayout.Y_AXIS));
    eiePnl.setPreferredSize(new Dimension(320, 230));

    // Add the widgets
    addWidgets();

    // Add the panel to the window
    frame.getContentPane().add(eiePnl, BorderLayout.CENTER);

    // Display the window
    frame.pack();
    frame.setVisible(true);
  }

  private void addWidgets() {
    // Equation panel contains labels with the equation and constant values
    eqnPnl = new JPanel();
    eqnLbl = new JLabel("EIE = ZPE x EXC x MMI");
    eqnPnl.add(eqnLbl);

    reactPnl = new JPanel();

    defLbl1 = new JLabel("<html>Using &#957<sub>16(r)</sub> = </html>");
    defLbl1.setFont(stdFnt);

    v16rFmtTxtFld = new JFormattedTextField(new DecimalFormat("####0.0####"));
    v16rFmtTxtFld.setColumns(6);
    v16rFmtTxtFld.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
    v16rFmtTxtFld.setEditable(false);

    defLbl2 = new JLabel("<html> and &#957<sub>18(r)</sub> = </html>");
    defLbl2.setFont(stdFnt);

    v18rFmtTxtFld = new JFormattedTextField(new DecimalFormat("####0.0####"));
    v18rFmtTxtFld.setColumns(6);
    v18rFmtTxtFld.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
    v18rFmtTxtFld.setEditable(false);

    // Set and commit the default values
    try {
      v16rFmtTxtFld.setText("1556.3");
      v16rFmtTxtFld.commitEdit();
      v18rFmtTxtFld.setText("1512.5");
      v18rFmtTxtFld.commitEdit();
    } catch (ParseException e) {
      // Shouldn't get here unless the setText value doesn't agree with the format set above
      e.printStackTrace();
    }

    reactPnl.add(defLbl1);
    reactPnl.add(v16rFmtTxtFld);
    reactPnl.add(defLbl2);
    reactPnl.add(v18rFmtTxtFld);

    // Create the radio buttons for reactants
    rdoPnl = new JPanel();

    oxyRdoBtn = new JRadioButton("Oxygen");
    oxyRdoBtn.setFont(stdFnt);
    oxyRdoBtn.setActionCommand("oxy");
    oxyRdoBtn.addActionListener(new EieIo_setReactant_actionAdapter(this));
    oxyRdoBtn.setSelected(true);

    supRdoBtn = new JRadioButton("Superoxide");
    supRdoBtn.setFont(stdFnt);
    supRdoBtn.setActionCommand("sup");
    supRdoBtn.addActionListener(new EieIo_setReactant_actionAdapter(this));

    usrRdoBtn = new JRadioButton("User-defined");
    usrRdoBtn.setFont(stdFnt);
    usrRdoBtn.setActionCommand("usr");
    usrRdoBtn.addActionListener(new EieIo_setReactant_actionAdapter(this));

    btnGrp = new ButtonGroup();
    btnGrp.add(oxyRdoBtn);
    btnGrp.add(supRdoBtn);
    btnGrp.add(usrRdoBtn);

    rdoPnl.add(oxyRdoBtn);
    rdoPnl.add(supRdoBtn);
    rdoPnl.add(usrRdoBtn);

    // Table Scroll panel contains the table for the product entry pairs
    String[] colNames = {"<html>&#957<sub>16(p)</sub></html>", "<html>&#957<sub>18(p)</sub></html>"};
    tmdl = new EieTableModel(colNames, 3);
    tmdl.addTableModelListener(this);
    pTbl = new JTable(tmdl);
    tblScrPnl = new JScrollPane(pTbl);
    pTbl.setPreferredScrollableViewportSize(new Dimension(320, 48));
    pTbl.setDefaultRenderer(Double.class, new EieRenderer());

    // Button panel contains the calculate and reset buttons
    btnPnl = new JPanel();

    eieBtn = new JButton("Calculate");
    eieBtn.setFont(stdFnt);
    eieBtn.addActionListener(new EieIo_eieCalc_actionAdapter(this));

    rstBtn = new JButton("Reset");
    rstBtn.setFont(stdFnt);
    rstBtn.addActionListener(new EieIo_eieReset_actionAdapter(this));

    btnPnl.add(eieBtn);
    btnPnl.add(rstBtn);

    // Calc panel contains the results of the calculations
    calcPnl = new JPanel(new GridLayout(2, 2));

    zpeLbl = new JLabel("ZPE = ");
    zpeLbl.setFont(stdFnt);
    excLbl = new JLabel("EXC = ");
    excLbl.setFont(stdFnt);
    mmiLbl = new JLabel("MMI = ");
    mmiLbl.setFont(stdFnt);
    eieLbl = new JLabel("EIE = ");

    calcPnl.add(zpeLbl);
    calcPnl.add(excLbl);
    calcPnl.add(mmiLbl);
    calcPnl.add(eieLbl);

    // Add the subpanels to the main content panel
    eiePnl.add(eqnPnl);
    eiePnl.add(reactPnl);
    eiePnl.add(rdoPnl);
    eiePnl.add(tblScrPnl);
    eiePnl.add(btnPnl);
    eiePnl.add(calcPnl);
  }

  // Add a row to the table when the user enters a value in the last cell
  public void tableChanged(TableModelEvent e) {
    if (e.getType() == TableModelEvent.UPDATE && pTbl.getValueAt(pTbl.getRowCount()-1, 1) != null) {
      Double[] nAry = {null, null};
      tmdl.addRow(nAry);
    }
  }

  // Calculate ZPE, EXC, MMI, and EIE from user inputs
  protected void eieCalc_actionPerformed(ActionEvent e) {
    boolean values = false;
    double zpe_ttl = 0, exc_ttl = 1, mmi_ttl = 1;
    double zpe, exc, mmi, eie;
    double v16p, v18p;
    Double p0, p1;

    pTbl.clearSelection();
    if (pTbl.isEditing()) pTbl.getCellEditor().stopCellEditing();

    if (usrRdoBtn.isSelected()) {
      v16r = Double.parseDouble(v16rFmtTxtFld.getValue().toString());
      v18r = Double.parseDouble(v18rFmtTxtFld.getValue().toString());
    }

    for (int i=0; i<pTbl.getRowCount(); i++) {
      p0 = (Double) pTbl.getValueAt(i, 0);
      p1 = (Double) pTbl.getValueAt(i, 1);

      if (p0 != null && p1 != null) {
        values = true;
        v16p = p0.doubleValue();
        v18p = p1.doubleValue();

        zpe_ttl += (v16p - v18p);
        exc_ttl *= ((1 - Math.exp(-1 * hkT * v18p)) / (1 - Math.exp(-1 * hkT * v16p)));
        mmi_ttl *= (v16p / v18p);
      } else {
        pTbl.setValueAt(null, i, 0);
        pTbl.setValueAt(null, i, 1);
      }
    }

    if (values) {
      zpe = Math.exp(h2kT * ((v16r - v18r) - zpe_ttl));
      exc = ((1 - Math.exp(-1 * hkT * v16r)) / (1 - Math.exp(-1 * hkT * v18r))) * exc_ttl;
      mmi = (v18r / v16r) * mmi_ttl;
      eie = zpe * exc * mmi;

      zpeLbl.setText("ZPE = " + zpe);
      excLbl.setText("EXC = " + exc);
      mmiLbl.setText("MMI = " + mmi);
      eieLbl.setText("EIE = " + eie);
    } else {
      rstBtn.doClick();
    }
  }

  // Clear out all entries and remove any rows beyond the 3 default ones
  protected void eieReset_actionPerformed(ActionEvent e) {
    int i;
    pTbl.clearSelection();
    pTbl.removeEditor();

    for (i=0; i<3; i++) {
      pTbl.setValueAt(null, i, 0);
      pTbl.setValueAt(null, i, 1);
    }

    for (i=pTbl.getRowCount()-1; i>2; i--) {
      tmdl.removeRow(i);
    }

    zpeLbl.setText("ZPE = ");
    excLbl.setText("EXC = ");
    mmiLbl.setText("MMI = ");
    eieLbl.setText("EIE = ");
  }

  // Set the reactant values (v16r, v18r) based on user preference
  protected void setReactant_actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();

    if (cmd.equals("oxy")) {
      v16r = 1556.3;
      v18r = 1512.5;
      v16rFmtTxtFld.setEditable(false);
      v18rFmtTxtFld.setEditable(false);
    } else if (cmd.equals("sup")) {
      v16r = 1064.8;
      v18r = 1034.8;
      v16rFmtTxtFld.setEditable(false);
      v18rFmtTxtFld.setEditable(false);
    } else { // cmd.equals("usr")
      v16r = 0.0;
      v18r = 0.0;
      v16rFmtTxtFld.setEditable(true);
      v18rFmtTxtFld.setEditable(true);
    }

    try {
      v16rFmtTxtFld.setText(Double.toString(v16r));
      v16rFmtTxtFld.commitEdit();
      v18rFmtTxtFld.setText(Double.toString(v18r));
      v18rFmtTxtFld.commitEdit();
    } catch (ParseException pe) {
      pe.printStackTrace();
    }
  }

  // Subclass DefaultTableModel to support the constructor used and
  // define all cells as a Double
  private static final class EieTableModel extends DefaultTableModel {
    EieTableModel(Object[] columnNames, int rowCount) { super(columnNames, rowCount); }

    // JTable uses this method to determine the default renderer/editor for each cell.
    public Class<Double> getColumnClass(int c) { return Double.class; }
  }

  // Subclass DefaultTableCellRenderer to use our DecimalFormatter w/10 digit precision
  private static final class EieRenderer extends DefaultTableCellRenderer {
    private DecimalFormat formatter;

    EieRenderer() {
      super();
      setHorizontalAlignment(JLabel.RIGHT);
    }

    public void setValue(Object value) {
      if (formatter == null) {
        formatter = new DecimalFormat("#0.0#########");
      }
      setText((value == null) ? "" : formatter.format(value));
    }
  }

  /** For thread safety, this method should be invoked from the event-dispatching thread. */
  private static void createAndShowGUI() {
    // Make sure we have nice window decorations
    JFrame.setDefaultLookAndFeelDecorated(true);

    new EieIo();
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


class EieIo_eieCalc_actionAdapter implements ActionListener {
  private EieIo adaptee;

  EieIo_eieCalc_actionAdapter(EieIo adaptee) { this.adaptee = adaptee; }
  public void actionPerformed(ActionEvent e) { adaptee.eieCalc_actionPerformed(e); }
}

class EieIo_eieReset_actionAdapter implements ActionListener {
  private EieIo adaptee;

  EieIo_eieReset_actionAdapter(EieIo adaptee) { this.adaptee = adaptee; }
  public void actionPerformed(ActionEvent e) { adaptee.eieReset_actionPerformed(e); }
}

class EieIo_setReactant_actionAdapter implements ActionListener {
  private EieIo adaptee;

  EieIo_setReactant_actionAdapter(EieIo adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) { adaptee.setReactant_actionPerformed(e); }
}
