package controller;

import java.util.Arrays;
/**
 * This class provides static methods to run validations against the entered amounts and categories.
 */
public class InputValidation {
  /**
   * Validates if the provided amount is within acceptable limits.
   *
   * @param amount Transaction amount to be validated.
   * @return {@code true} if the amount is valid; {@code false} otherwise.
   */
  public static boolean isValidAmount(double amount) {
    
    // Check range
    if(amount >1000) {
      return false;
    }
    if (amount < 0){
      return false;
    }
    if (amount == 0){
      return false;
    }
    return true;
  }
  /**
   * Validates if the provided category matches with the predefined valid Words.
   *
   * @param category The category string to be validated.
   * @return {@code true} if the category is valid; {@code false} otherwise.
   */
  public static boolean isValidCategory(String category) {

    if(category == null) {
      return false; 
    }
  
    if(category.trim().isEmpty()) {
      return false;
    }

    if(!category.matches("[a-zA-Z]+")) {
      return false;
    }

    String[] validWords = {"food", "travel", "bills", "entertainment", "other"};

    if(!Arrays.asList(validWords).contains(category.toLowerCase())) {
      // invalid word  
      return false;
    }
  
    return true;
  
  }

}