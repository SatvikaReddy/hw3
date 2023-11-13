// package test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.swing.JTable;
import java.awt.*;
import javax.swing.table.DefaultTableModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import controller.ExpenseTrackerController;
import model.ExpenseTrackerModel;
import model.Transaction;
import model.Filter.AmountFilter;
import model.Filter.CategoryFilter;
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

    private int countHighlightedRows(JTable table, Color highlightColor, Color normalColor) {
        int highlightCount = 0;
        for (int row = 0; row < table.getRowCount(); row++) {
            for (int col = 0; col < table.getColumnCount(); col++) {
                Component comp = table.prepareRenderer(table.getCellRenderer(row, col), row, col);
                if (!comp.getBackground().equals(normalColor)) {
                    if (comp.getBackground().equals(highlightColor)) {
                        highlightCount++;
                        break;
                    }
                }
            }
        }
        return highlightCount;
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

    /**
     * Test case 1
     */
    @Test
    public void testViewUpdateAddTransaction() {
        // Pre-condition: The view's table model is empty
        DefaultTableModel tableModel = view.getTableModel();
        assertEquals(0, tableModel.getRowCount());

        // Perform action: Adding a transaction
        double amount = 50.00;
        String category = "food";
        controller.addTransaction(amount, category);

        // Post-conditions: Check the view's table model has one row added: 2 exist because 1 is total row
        assertEquals(1+1, tableModel.getRowCount());

        // Check the contents of the table model
        double tableAmount = (Double) tableModel.getValueAt(0, 1);
        String tableCategory = (String) tableModel.getValueAt(0, 2);

        // Verify that the view's table model contains the transaction details
        assertEquals(amount, tableAmount, 0.01);
        assertEquals(category, tableCategory);

        // Verifying the total cost displayed in the view matches the expected value
        // Total cost is displayed in the last row, last column
        double TotalCostDisplayed = (Double) tableModel.getValueAt(tableModel.getRowCount() - 1, 3);
        assertEquals(amount, TotalCostDisplayed, 0.01);
    }

    /**
     * Test case 2
     */
    @Test
    public void testInvalidInput() {
        // Pre Condition: Check the transactions are empty, and initial total cost
        double initialTotalCost = getTotalCost();
        DefaultTableModel tableModel = view.getTableModel();
        assertEquals(0, tableModel.getRowCount());

        // Perform Action: ExpenseTrackerModel class addTransaction method to cover it's exceptional situation
        try{
            model.addTransaction(null);
        }catch(Exception e){
            assertEquals(e.getMessage(),"The new transaction must be non-null.");
        }

        // Transaction constructor to cover it's exceptional situation for invalid Amount
        try{
            Transaction addedTransaction = new Transaction(-1, "food");
            model.addTransaction(addedTransaction);
        }catch(Exception e){
            assertEquals(e.getMessage(),"The amount is not valid.");
        }

        // Perform Action: Add a transaction with invalid amount , calling controller exception case
        boolean result = controller.addTransaction(-1, "food");

        // Transaction constructor to cover it's exceptional situation for invalid Category
        try{
            Transaction addedTransaction = new Transaction(50.0, "");
            model.addTransaction(addedTransaction);
        }catch(Exception e){
            assertEquals(e.getMessage(),"The category is not valid.");
        }

        //Perform Action: Add a transaction with invalid Category
        boolean result2 = controller.addTransaction(50.0, "");

        // Post Condition : Controller.addTransaction() returns false if the transaction is invalid due to amount
        assertEquals(result, false);
        assertEquals(initialTotalCost, getTotalCost(), 0.01);
        // Transactions and Total Cost are unchanged
        assertEquals(0, model.getTransactions().size());
        
        // View shouldn't have changed 
        assertEquals(0, tableModel.getRowCount());

        // Post Condition : Controller.addTransaction() returns false if the transaction is invalid due to category
        // Assert: Transactions and Total Cost are unchanged again
        assertEquals(result2, false);
        assertEquals(initialTotalCost, getTotalCost(), 0.01);
        assertEquals(0, model.getTransactions().size());
        // View shouldn't have changed 
        assertEquals(0, tableModel.getRowCount());
    }


    /**
     * Test case 3
     */
    @Test
    public void testFilterAmountHighlight() {
        // Pre-condition: Check the transactions are empty
        DefaultTableModel tableModel = view.getTableModel();
        assertEquals(0, tableModel.getRowCount());
        assertEquals(0, model.getTransactions().size());

        // Perform Action: Add transactions
        controller.addTransaction(50.0, "food");
        controller.addTransaction(50.0, "travel");
        controller.addTransaction(100.0, "food");
        controller.addTransaction(50.0, "bills");

        // Retrieve the table from the view
        JTable transactionsTable = view.getTransactionsTable();

        Color highlightColor = new Color(173, 255, 168); // Green colour
        Color normalColor = Color.WHITE; 

        // Perform Action: Amount exists in the table
        AmountFilter filterTrue = new AmountFilter(50);
        controller.setFilter(filterTrue);
        controller.applyFilter();

        int filteredTransactions1 = countHighlightedRows(transactionsTable, highlightColor, normalColor);

        // Perform Action: Amount not existing in the table
        AmountFilter filterNull = new AmountFilter(10);
        controller.setFilter(filterNull);
        controller.applyFilter();

        int filteredTransactions2 = countHighlightedRows(transactionsTable, highlightColor, normalColor);

        //Post Conditions:
        // Checking that filtered Transactions1 is 3:
        assertEquals(filteredTransactions1, 3);

        // Checking that filtered Transactions2 is empty:
        assertEquals(filteredTransactions2, 0);

        //the model data remains same, not affected
        assertEquals(4, model.getTransactions().size());
        // 4+1(total row also added)
        assertEquals(5, tableModel.getRowCount());

    }

    /**
     * Test case 4
     */
    @Test
    public void testFilterCategoryHighlight() {
        // Pre-condition: Check the transactions are empty
        DefaultTableModel tableModel = view.getTableModel();
        assertEquals(0, tableModel.getRowCount());
        assertEquals(0, model.getTransactions().size());

        // Perform Action: Add transactions
        controller.addTransaction(50.0, "food");
        controller.addTransaction(50.0, "travel");
        controller.addTransaction(100.0, "food");
        controller.addTransaction(50.0, "bills");

        // Retrieve the table from the view
        JTable transactionsTable = view.getTransactionsTable();

        Color highlightColor = new Color(173, 255, 168); // Green colour
        Color normalColor = Color.WHITE; 

        // Perform Action: Category exists in the table
        CategoryFilter filterTrue = new CategoryFilter("food");
        controller.setFilter(filterTrue);
        controller.applyFilter();

        int filteredTransactions1 = countHighlightedRows(transactionsTable, highlightColor, normalColor);

        // Perform Action: Category doesnt exist in the table
        CategoryFilter filterNull = new CategoryFilter("entertainment");
        controller.setFilter(filterNull);
        controller.applyFilter();

        int filteredTransactions2 = countHighlightedRows(transactionsTable, highlightColor, normalColor);

        //Post Conditions:
        // Checking that filtered Transactions1 is 3:
        assertEquals(filteredTransactions1, 2);

        // Checking that filtered Transactions2 is empty:
        assertEquals(filteredTransactions2, 0);

        //the model data remains same, not affected
        assertEquals(4, model.getTransactions().size());
        // 4+1(total row also added)
        assertEquals(5, tableModel.getRowCount());
    }
    
    /**
     * Test case 5
     */
    @Test
    public void undoDisallowed() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
        DefaultTableModel tableModel = view.getTableModel();
        assertEquals(0, tableModel.getRowCount());
        // Perform the action: try undo
        try {
        controller.removeTransaction(new int[0]);

        } catch (Exception e) {
            assertEquals("Undo Disallowed.", e.getMessage());
        }
        // Post-condition: List of transactions is still empty
        assertEquals(0, model.getTransactions().size());
        // Checking if view is updated
        assertEquals(0, tableModel.getRowCount());
    }

    /**
     * Test case 6
     */
    @Test
    public void undoAllowed() {
        // Pre-condition: List of transactions is empty in model
        assertEquals(0, model.getTransactions().size());
        DefaultTableModel tableModel = view.getTableModel();
        // Checking view before undo button is clicked
        assertEquals(0, tableModel.getRowCount());

        // Perform Action: Add transactions
        controller.addTransaction(50.0, "food");
        controller.addTransaction(50.0, "travel");

        //total cost in model before undo
        assertEquals(100.0, getTotalCost(), 0.01);

        // Perform the action: Undo
        controller.removeTransaction(new int[] {0});

        // Post-condition: List of transactions after undo in model is 1
        assertEquals(1, model.getTransactions().size());
        //total cost in model after undo
        assertEquals(50.0, getTotalCost(), 0.01);
        // Checking if view is updated
        assertEquals(2, tableModel.getRowCount());
    }


}
