package knitro.betterSearch.database.filter;

import knitro.support.Preconditions;

/**
 * The colours that a card can be.
 * COLOURLESS is included, it should be the sole indicator of a card being colourless.
 * Cards with "no enum" associated should be assumed to be an error.
 * @author Calvin Lee
 *
 */
public enum CardColour {

	WHITE, BLUE, BLACK, RED, GREEN, COLOURLESS;
	
	/**
	 * Gets a colour representing letter [W, U, B, R, G], and converts it to a CardColour enum value.
	 * @param letter - letter being [W, U, B, R, G]
	 * @return the respective CardColour value.
	 */
	public static CardColour getCardColour(String letter) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(letter != null, "letter parameter is null");
		Preconditions.preconditionCheck(letter.length() == 1, "letter length must be 1");
		
		/*Return Appropriately given the Letter*/
		if (letter.equalsIgnoreCase("W")) {
			return CardColour.WHITE;
		} else if (letter.equalsIgnoreCase("U")) {
			return CardColour.BLUE;
		} else if (letter.equalsIgnoreCase("B")) {
			return CardColour.BLACK;
		} else if (letter.equalsIgnoreCase("R")) {
			return CardColour.RED;
		} else if (letter.equalsIgnoreCase("G")) {
			return CardColour.GREEN;
		} else {
			throw new RuntimeException("Invalid Letter provided");
		}
	}
}
