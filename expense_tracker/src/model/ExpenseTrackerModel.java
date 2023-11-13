package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExpenseTrackerModel {

  //encapsulation - data integrity
  private List<Transaction> transactions;

  public ExpenseTrackerModel() {
    transactions = new ArrayList<>(); 
  }
  /**
   * Adds a transaction to the previously defined set of transactions
   *
   * @param t The transaction to be added.
   */
  public void addTransaction(Transaction t) {
    // Perform input validation to guarantee that all transactions added are non-null.
    if (t == null) {
      throw new IllegalArgumentException("The new transaction must be non-null.");
    }
    transactions.add(t);
  }
  /**
   * Removes a transaction to the previously defined set of transactions
   *
   * @param t The transaction to be deleted.
   */
  public void removeTransaction(Transaction t) {
    transactions.remove(t);
  }
  /**
   * returns a existing set of transactions
   * @return A list containing all managed transactions.
   */
  public List<Transaction> getTransactions() {
    //encapsulation - data integrity
    return Collections.unmodifiableList(new ArrayList<>(transactions));
  }

}
