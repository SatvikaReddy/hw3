package controller;

import view.ExpenseTrackerView;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import model.ExpenseTrackerModel;
import model.Transaction;
import model.Filter.TransactionFilter;

/**
 * Controller for the ExpenseTracker application.
 * <p>
 * This class is used to handle the user inputs, and interacting and updating the model.
 * </p>
 */
public class ExpenseTrackerController {
  
  private ExpenseTrackerModel model;
  private ExpenseTrackerView view;
  /** 
   * The Controller is applying the Strategy design pattern.
   * This is the has-a relationship with the Strategy class 
   * being used in the applyFilter method.
   */
  private TransactionFilter filter;

  /**
   * Constructs a new ExpenseTrackerController with arguments as the model and view.
   *
   * @param model The model for this controller.
   * @param view  The view for this controller.
   */
  public ExpenseTrackerController(ExpenseTrackerModel model, ExpenseTrackerView view) {
    this.model = model;
    this.view = view;
  }

  public void setFilter(TransactionFilter filter) {
    // Sets the Strategy class being used in the applyFilter method.
    this.filter = filter;
  }

  /**
   * This is used to update the transactions view with any new inserted transactions.
   */
  public void refresh() {
    List<Transaction> transactions = model.getTransactions();
    view.refreshTable(transactions);
  }

  /**
   * Adds a new transaction when the validation checks for amount and category have been checked with the entered amount and category.
   *
   * @param amount   Amount entered for transaction.
   * @param category Category of the transaction.
   * @return {@code true} if the values are validated and transaction has been added; {@code false} otherwise.
   */
  public boolean addTransaction(double amount, String category) {
    if (!InputValidation.isValidAmount(amount)) {
      return false;
    }
    if (!InputValidation.isValidCategory(category)) {
      return false;
    }
    
    Transaction t = new Transaction(amount, category);
    model.addTransaction(t);
    view.getTableModel().addRow(new Object[]{t.getAmount(), t.getCategory(), t.getTimestamp()});
    refresh();
    return true;
  }
  
  /**
   * Applies a amount or category filter to the transactions based on the view selected by the user.
   */
  public void applyFilter() {
    //null check for filter
    if(filter!=null){
      // Use the Strategy class to perform the desired filtering
      List<Transaction> transactions = model.getTransactions();
      List<Transaction> filteredTransactions = filter.filter(transactions);
      List<Integer> rowIndexes = new ArrayList<>();
      for (Transaction t : filteredTransactions) {
        int rowIndex = transactions.indexOf(t);
        if (rowIndex != -1) {
          rowIndexes.add(rowIndex);
        }
      }
      view.highlightRows(rowIndexes);
    }
    else{
      JOptionPane.showMessageDialog(view, "No filter applied");
      view.toFront();}

  }

  /**
   * Removes a transaction selected by the user, checks if undo button is invalid or not as well
   * @param row the indice of the row for which transactions will be removed
   */
  public void removeTransaction(int[] row) {
    if (!(row.length < 1)) {
      Transaction t = model.getTransactions().get(row[0]);
      model.removeTransaction(t);
      refresh();
    } else if(row.length < 1 && model.getTransactions().size()>0){
      JOptionPane.showMessageDialog(view, "Undo Disallowed, Select a row");
      throw new IllegalArgumentException("Undo Disallowed.");
    }
    else {
      JOptionPane.showMessageDialog(view, "Undo Disallowed.");
      throw new IllegalArgumentException("Undo Disallowed.");
    }
  }

}
