package model;

import controller.InputValidation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Represents the data model for the Transactions which contains the amount, category and timestam fields.
 */
public class Transaction {

  public static final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    
  //final means that the variable cannot be changed
  private final double amount;
  private final String category;
  private final String timestamp;

  public Transaction(double amount, String category) {
    // Since this is a public constructor, perform input validation
    // to guarantee that the amount and category are both valid
    if (InputValidation.isValidAmount(amount) == false) {
	throw new IllegalArgumentException("The amount is not valid.");
    }
    if (InputValidation.isValidCategory(category) == false) {
	throw new IllegalArgumentException("The category is not valid.");
    }
      
    this.amount = amount;
    this.category = category;
    this.timestamp = generateTimestamp();
  }

  /**
   * @return amount of the transaction
   */
  public double getAmount() {
    return amount;
  }

  //setter method is removed because we want to make the Transaction immutable
  // public void setAmount(double amount) {
  //   this.amount = amount;
  // }
  /**
   * @return category of the transaction
   */
  public String getCategory() {
    return category;
  }

  // public void setCategory(String category) {
  //   this.category = category; 
  // }

  /**
   * @return timestamp of the transaction
   */
  public String getTimestamp() {
    return timestamp;
  }
  //private helper method to generate timestamp
    /**
   * Generates the Timestamp at the time transaction was added
   * @return Formatted timestamp
   */
  private String generateTimestamp() {
     return dateFormatter.format(new Date());
  }

}
