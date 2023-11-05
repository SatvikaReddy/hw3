// package test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import controller.ExpenseTrackerController;
import model.ExpenseTrackerModel;
import model.Transaction;
import view.ExpenseTrackerView;


public class TestExample {
  
  private ExpenseTrackerModel model;
  private ExpenseTrackerView view;
  private ExpenseTrackerController controller;

  @Before
  public void setup() {
    model = new ExpenseTrackerModel();
    view = new ExpenseTrackerView();
    controller = new ExpenseTrackerController(model, view);
  }

    @After
    public void tearDown() {
        if (view != null) {
            view.dispose();
        }
        model = null;
        view = null;
        controller = null;
    }

    public double getTotalCost() {
        double totalCost = 0.0;
        List<Transaction> allTransactions = model.getTransactions(); // Using the model's getTransactions method
        for (Transaction transaction : allTransactions) {
            totalCost += transaction.getAmount();
        }
        return totalCost;
    }


    public void checkTransaction(double amount, String category, Transaction transaction) {
	assertEquals(amount, transaction.getAmount(), 0.01);
        assertEquals(category, transaction.getCategory());
        String transactionDateString = transaction.getTimestamp();
        Date transactionDate = null;
        try {
            transactionDate = Transaction.dateFormatter.parse(transactionDateString);
        }
        catch (ParseException pe) {
            pe.printStackTrace();
            transactionDate = null;
        }
        Date nowDate = new Date();
        assertNotNull(transactionDate);
        assertNotNull(nowDate);
        // They may differ by 60 ms
        assertTrue(nowDate.getTime() - transactionDate.getTime() < 60000);
    }


    @Test
    public void testAddTransaction() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
    
        // Perform the action: Add a transaction
	double amount = 50.0;
	String category = "food";
        assertTrue(controller.addTransaction(amount, category));
    
        // Post-condition: List of transactions contains only
	//                 the added transaction	
        assertEquals(1, model.getTransactions().size());
    
        // Check the contents of the list
	Transaction firstTransaction = model.getTransactions().get(0);
	checkTransaction(amount, category, firstTransaction);
	
	// Check the total amount
        assertEquals(amount, getTotalCost(), 0.01);
    }


    @Test
    public void testRemoveTransaction() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
    
        // Perform the action: Add and remove a transaction
	double amount = 50.0;
	String category = "food";
        Transaction addedTransaction = new Transaction(amount, category);
        model.addTransaction(addedTransaction);
    
        // Pre-condition: List of transactions contains only
	//                the added transaction
        assertEquals(1, model.getTransactions().size());
	Transaction firstTransaction = model.getTransactions().get(0);
	checkTransaction(amount, category, firstTransaction);

	assertEquals(amount, getTotalCost(), 0.01);
	
	// Perform the action: Remove the transaction
        model.removeTransaction(addedTransaction);
    
        // Post-condition: List of transactions is empty
        List<Transaction> transactions = model.getTransactions();
        assertEquals(0, transactions.size());
    
        // Check the total cost after removing the transaction
        double totalCost = getTotalCost();
        assertEquals(0.00, totalCost, 0.01);
    }


    @Test
    public void testViewUpdateAddTransaction() {
        // Pre-condition: The view's table model is empty
        DefaultTableModel tableModel = view.getTableModel();
        assertEquals(0, tableModel.getRowCount());

        // Perform action: Adding a transaction
        double amount = 50.00;
        String category = "food";
        controller.addTransaction(amount, category); // This should update the model and the view

        // Post-condition: Check the view's table model has one row added: 2 exist because 1 is total row
        assertEquals(1+1, tableModel.getRowCount());

        // Check the contents of the table model
        double tableAmount = (Double) tableModel.getValueAt(0, 1); // Assuming amount is at column index 1
        String tableCategory = (String) tableModel.getValueAt(0, 2); // Assuming category is at column index 2

        // Verify that the view's table model contains the transaction details
        assertEquals(amount, tableAmount, 0.01);
        assertEquals(category, tableCategory);

        // Verifying the total cost displayed in the view matches the expected value
        // Total cost is displayed in the last row, last column
        double displayedTotalCost = (Double) tableModel.getValueAt(tableModel.getRowCount() - 1, 3);
        assertEquals(amount, displayedTotalCost, 0.01);
    }

    @Test
    public void testInvalidInput() {
        // Pre Condition: remember the initial total cost
        double initialTotalCost = getTotalCost();
        DefaultTableModel tableModel = view.getTableModel();
        assertEquals(0, tableModel.getRowCount());

        // Perform Action: Attempt to add a transaction with invalid amount
        double invalidAmount = -1;
        String category = "food";
        
        boolean result = controller.addTransaction(invalidAmount, category);

        // Post Condition : Assuming controller.addTransaction() returns false if the transaction is invalid
        // Transactions and Total Cost are unchanged
        assertEquals("Total cost should not change after invalid input",
                     initialTotalCost, getTotalCost(), 0.01);
        assertEquals("Transactions list should not change after invalid input",
                     0, model.getTransactions().size());
        
        // View shouldn't have changed 
        assertEquals(0, tableModel.getRowCount());

        // Action: Attempt to add a transaction with invalid category
        double amount = 50.0;
        String invalidCategory = ""; // Assuming empty category is invalid

        result = controller.addTransaction(amount, invalidCategory);

        // Post Condition : Assuming controller.addTransaction() returns false if the transaction is invalid
        // Assert: Transactions and Total Cost are unchanged again
        assertEquals("Total cost should not change after invalid input",
                     initialTotalCost, getTotalCost(), 0.01);
        assertEquals("Transactions list should not change after invalid input",
                     0, model.getTransactions().size());
        // View shouldn't have changed 
        assertEquals(0, tableModel.getRowCount());
    }
    
}
