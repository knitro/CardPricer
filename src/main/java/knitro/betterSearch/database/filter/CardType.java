package knitro.betterSearch.database.filter;

import knitro.support.Preconditions;

/**
 * The SubTypes of what a card could have
 * @author Calvin Lee
 *
 */
public enum CardType {

	ARTIFACT, CONSPIRACY, CREATURE, ENCHANTMENT, INSTANT, LAND, PHENOMENONS,
	PLANE, PLANESWALKER, SCHEME, SORCERY, TRIBAL, VANGUARD, META;
	
	/**
	 * Gets a string representing a subtype and converts it to the appropriate enum value.
	 * @param subtype - String being converted to the appropriate subtype.
	 * @return the respective CardColour value.
	 */
	public static CardType getCardSubType(String subtype) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(subtype != null, "letter parameter is null");
		
		/*Return Appropriately given the String*/
		
		if (subtype.equalsIgnoreCase("ARTIFACT")) {
			return CardType.ARTIFACT;
		} else if (subtype.equalsIgnoreCase("CONSPIRACY")) {
			return CardType.CONSPIRACY;
		} else if (subtype.equalsIgnoreCase("CREATURE")) {
			return CardType.CREATURE;
		} else if (subtype.equalsIgnoreCase("ENCHANTMENT")) {
			return CardType.ENCHANTMENT;
		} else if (subtype.equalsIgnoreCase("INSTANT")) {
			return CardType.INSTANT;
		} else if (subtype.equalsIgnoreCase("LAND")) {
			return CardType.LAND;
		} else if (subtype.equalsIgnoreCase("PHENOMENONS")) {
			return CardType.PHENOMENONS;
		} else if (subtype.equalsIgnoreCase("PLANE")) {
			return CardType.PLANE;
		} else if (subtype.equalsIgnoreCase("PLANESWALKER")) {
			return CardType.PLANESWALKER;
		} else if (subtype.equalsIgnoreCase("SCHEME")) {
			return CardType.SCHEME;
		} else if (subtype.equalsIgnoreCase("SORCERY")) {
			return CardType.SORCERY;
		} else if (subtype.equalsIgnoreCase("TRIBAL")) {
			return CardType.TRIBAL;
		} else if (subtype.equalsIgnoreCase("VANGUARD")) {
			return CardType.VANGUARD;
		} else {
			return CardType.META;
//			throw new RuntimeException("Invalid subtype string provided. Subtype = " + subtype);
		}
	}
}
