package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import model.Transaction;

import java.util.List;
/**
 * The User interface for the Expense Tracker application which contains components to display, add or filter a transaction.
 */
public class ExpenseTrackerView extends JFrame {

  private JTable transactionsTable;
  private JButton addTransactionBtn;
  private JFormattedTextField amountField;
  private JTextField categoryField;
  private DefaultTableModel model;

  // private JTextField dateFilterField;
  private JTextField categoryFilterField;
  private JButton categoryFilterBtn;

  private JTextField amountFilterField;
  private JButton amountFilterBtn;
  private JButton undoBtn;

  
  /**
   * Constructs the main view for the Expense Tracker application.
   */
  public ExpenseTrackerView() {
    setTitle("Expense Tracker"); // Set title
    setSize(600, 400); // Make GUI larger

    String[] columnNames = {"serial", "Amount", "Category", "Date"};
    this.model = new DefaultTableModel(columnNames, 0);

    
    // Create table
    transactionsTable = new JTable(model);

    addTransactionBtn = new JButton("Add Transaction");

    // Create UI components
    JLabel amountLabel = new JLabel("Amount:");
    NumberFormat format = NumberFormat.getNumberInstance();

    amountField = new JFormattedTextField(format);
    amountField.setColumns(10);

    
    JLabel categoryLabel = new JLabel("Category:");
    categoryField = new JTextField(10);
    

    JLabel categoryFilterLabel = new JLabel("Filter by Category:");
    categoryFilterField = new JTextField(10);
    categoryFilterBtn = new JButton("Filter by Category");

    JLabel amountFilterLabel = new JLabel("Filter by Amount:");
    amountFilterField = new JTextField(10);
    amountFilterBtn = new JButton("Filter by Amount");
  

    undoBtn = new JButton("Undo");
  
    // Layout components
    JPanel inputPanel = new JPanel();
    inputPanel.add(amountLabel);
    inputPanel.add(amountField);
    inputPanel.add(categoryLabel); 
    inputPanel.add(categoryField);
    inputPanel.add(addTransactionBtn);

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(amountFilterBtn);
    buttonPanel.add(categoryFilterBtn);

    buttonPanel.add(undoBtn);
  
    // Add panels to frame
    add(inputPanel, BorderLayout.NORTH);
    add(new JScrollPane(transactionsTable), BorderLayout.CENTER); 
    add(buttonPanel, BorderLayout.SOUTH);
  
    // Set frame properties
    setSize(600, 400); // Increase the size for better visibility
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  
  
  }
  /**
   * @return The basic table model for transactions.
   */
  public DefaultTableModel getTableModel() {
    return model;
  }
  /**
 * @return The transactions table view.
 */
  public JTable getTransactionsTable() {
    return transactionsTable;
  }
  
  /**
   * @return The value entered by the user in the amount field.
   */
  public double getAmountField() {
    if(amountField.getText().isEmpty()) {
      return 0;
    }else {
    double amount = Double.parseDouble(amountField.getText());
    return amount;
    }
  }
  /**
   * Sets the amount field .
   * 
   * @param amountField The formatted text field to set.
   */
  public void setAmountField(JFormattedTextField amountField) {
    this.amountField = amountField;
  }

  /**
   * Gets the category input from the category field.
   * 
   * @return The category input.
   */
  public String getCategoryField() {
    return categoryField.getText();
  }

  /**
   * Sets the category field.
   * 
   * @param categoryField The category text field to set.
   */
  public void setCategoryField(JTextField categoryField) {
    this.categoryField = categoryField;
  }
/**
 * @param listener the ActionListener to be added to the category filter button
 */
  public void addApplyCategoryFilterListener(ActionListener listener) {
    categoryFilterBtn.addActionListener(listener);
  }
/**
 * @return the category filter string input by the user
 */
  public String getCategoryFilterInput() {
    return JOptionPane.showInputDialog(this, "Enter Category Filter:");
}

/**
 * @param listener the ActionListener to be added to the amount filter button
 */
  public void addApplyAmountFilterListener(ActionListener listener) {
    amountFilterBtn.addActionListener(listener);
  }
/**
 * @param listener the ActionListener to be added to the undo filter button
 */
  public void addUndoListener(ActionListener listener) {
    undoBtn.addActionListener(listener);
  }
/**
 * @return the Amount filter string input by the user
 */
  public double getAmountFilterInput() {
    String input = JOptionPane.showInputDialog(this, "Enter Amount Filter:");
    try {
        return Double.parseDouble(input);
    } catch (NumberFormatException e) {
        // Handle parsing error here
        // You can show an error message or return a default value
        return 0.0; // Default value (or any other appropriate value)
    }
  }


  /**
   * @return undo transaction button.
   */
  public JButton getUndoBtn() {
    return undoBtn;
  }

  public void refreshTable(List<Transaction> transactions) {
      // Clear existing rows
      model.setRowCount(0);
      // Get row count
      int rowNum = model.getRowCount();
      double totalCost=0;
      // Calculate total cost
      for(Transaction t : transactions) {
        totalCost+=t.getAmount();
      }
  
      // Add rows from transactions list
      for(Transaction t : transactions) {
        model.addRow(new Object[]{rowNum+=1,t.getAmount(), t.getCategory(), t.getTimestamp()}); 
      }
      // Add total row
      Object[] totalRow = {"Total", null, null, totalCost};
      model.addRow(totalRow);
  
      // Fire table update
      transactionsTable.updateUI();
  
    }  
  
  
  /**
   * @return Add transactions button.
   */
  public JButton getAddTransactionBtn() {
    return addTransactionBtn;
  }



  /**
   * @param rowIndexes are all the rows we need to highlight
   */
  public void highlightRows(List<Integer> rowIndexes) {
      // The row indices are being used as hashcodes for the transactions.
      // The row index directly maps to the the transaction index in the list.
      transactionsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
          @Override
          public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                        boolean hasFocus, int row, int column) {
              Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
              if (rowIndexes.contains(row)) {
                  c.setBackground(new Color(173, 255, 168)); // Light green
              } else {
                  c.setBackground(table.getBackground());
              }
              return c;
          }
      });

      transactionsTable.repaint();
  }


}
