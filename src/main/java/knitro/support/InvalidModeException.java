package knitro.support;

/**
 * A Runtime Exception for when the mode used is invalid, 
 *  unrecognised, or unused.
 * @author Calvin Lee
 *
 */
@SuppressWarnings("serial")
public class InvalidModeException extends RuntimeException {
	
	///////////////////////////////////
	/*Constructors*/
	///////////////////////////////////
	
	/**
	 * Constructor for an InvalidModeException.
	 * @param message
	 */
	public InvalidModeException(String message) {
		super(message);
	}
}
