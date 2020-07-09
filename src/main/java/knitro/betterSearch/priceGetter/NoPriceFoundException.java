package knitro.betterSearch.priceGetter;

/**
 * An exception when a price cannot be found (Generally when a 404 page is retrieved)
 * @author Calvin Lee
 *
 */
public class NoPriceFoundException extends Exception {

	private static final long serialVersionUID = 3007195358544428382L;

	///////////////////////////////////
	/*Constructors*/
	///////////////////////////////////
	
	/**
	 * Constructor for an NoPriceFoundException.
	 * @param message
	 */
	public NoPriceFoundException(String message) {
		super(message);
	}
}
