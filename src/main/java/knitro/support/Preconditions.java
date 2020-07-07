package knitro.support;

/**
 * An class intended to provide supporting methods that are global 
 * rather than specific to the application.
 * @author Calvin Lee
 *
 */
public class Preconditions {

  ///////////////////////////////////
  /*Static Methods*/
  ///////////////////////////////////
  
  /**
   * Performs a check of a boolean to ensure it returns true.
   * The purpose of this method is to be used as a simplification of the precondition checks.
   * It will return a Runtime Exception (IllegalArgumentException) if the precondition fails.
   * This variant of the method will NOT provide a error of what has occurred.
   * @param yourCheck - the boolean that is being ensured to be true
   * @return true, as otherwise a Runtime exception will occur.
   */
  public static boolean preconditionCheck(boolean yourCheck) {
    
    if (!yourCheck) {
      String printMessage = "ERROR: Failed a Precondition Check with no Error Message";
      throw new IllegalArgumentException(printMessage);
    }
    
    //This is only called if the boolean is true
    return yourCheck;
  }
  
  /**
   * Performs a check of a boolean to ensure it returns true.
   * The purpose of this method is to be used as a simplification of the precondition checks.
   * It will return a Runtime Exception (IllegalArgumentException) if the precondition fails.
   * This variant of the method will provide a error of what has failed, as per the parameter.
   * @param yourCheck - the boolean that is being ensured to be true
   * @param message - the message to be displayed
   * @return true, as otherwise a Runtime exception will occur.
   */
  public static boolean preconditionCheck(boolean yourCheck, String message) {
    
    if (!yourCheck) {
      
      //Check if the message is null (no provided error message)
      if (message == null) {
        throw new IllegalArgumentException("ERROR: Failed Precondition Check + "
            + "Error Message Parameter is null");
      }
      
      String printMessage = "ERROR: Failed a Precondition Check: \n";
      printMessage += message;
      throw new IllegalArgumentException(printMessage);
    }
    
    //This is only called if the boolean is true
    return yourCheck;
  }
  
  /**
   * Checks if String supplied as a parameter is parsable as an Integer.
   * @param possibleInt - The String to check if it is an Integer
   * @return true if the String is parsable as an Integer, otherwise false
   */
  public static boolean checkIfInt(String possibleInt) {
    
    try {
      Integer.parseInt(possibleInt);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
  
}
