package knitro.betterSearch.database.card;

import java.util.Collections;
import java.util.Set;

import knitro.betterSearch.database.filter.CardColour;
import knitro.betterSearch.database.filter.CardType;
import knitro.betterSearch.database.filter.TypeOfMatch;
import knitro.support.InvalidModeException;
import knitro.support.Preconditions;

public abstract class DbItem {

	///////////////////////////////////
	/*Fields*/
	///////////////////////////////////
	
	private final String name;
	private final Set<String> printings;
	
	private final Set<CardColour> colours;
	private final Set<CardType> types;
	private final int cmc;

	private final String fullType;
	private final String text;
	
	private int sortingValue; //Used for comparators
	
	///////////////////////////////////
	/*Constructors*/
	///////////////////////////////////
	
	public DbItem(String name, Set<String> printings,
			Set<CardColour> colours, Set<CardType> type, int cmc,
			String fullType, String text) {
		super();
		this.name = name;
		this.printings = printings;
		
		this.colours = colours;
		this.types = type;
		this.cmc = cmc;
		this.fullType = fullType;
		this.text = text;
	}
	
	///////////////////////////////////
	/*Overridden Methods*/
	///////////////////////////////////
	
	///////////////////////////////////
	/*Public Methods*/
	///////////////////////////////////

	public String getName() {
		return name;
	}

	public Set<String> getPrintings() {
		return Collections.unmodifiableSet(printings);
	}
	
	public Set<String> getModifiablePrintings() {
		return printings;
	}

	public Set<CardColour> getColours() {
		return Collections.unmodifiableSet(colours);
	}

	public Set<CardType> getTypes() {
		return Collections.unmodifiableSet(types);
	}

	public int getCmc() {
		return cmc;
	}
	
	public String getFullType() {
		return fullType;
	}
	
	public String getText() {
		return text;
	}
	
	public int getSortingValue() {
		return sortingValue;
	}

	public void setSortingValue(int sortingValue) {
		this.sortingValue = sortingValue;
	}
	
	///////////////////////////////////
	/*Database Check Methods*/
	///////////////////////////////////
	
	public boolean hasSetCode(String setCode) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(setCode != null, "setCode is null");
		
		return printings.contains(setCode);
	}
	
	public boolean hasColours(Set<CardColour> colours, TypeOfMatch matching) {
			
			/*Preconditions*/
			Preconditions.preconditionCheck(colours != null, "setCode is null");
			
			if (matching == TypeOfMatch.EXACT_MATCH) {
				if (this.colours.equals(colours)) {
					return true;
				} else {
					return false;
				}
				
			} else if (matching == TypeOfMatch.INCLUDE_LEAST_ONE) {
				for (CardColour currentColour : this.colours) {
					if (colours.contains(currentColour)) {
						return true;
					}
				}
				return false;
			} else {
				throw new InvalidModeException("Invalid/Unimplemented TypeOfMatch");
			}
		}
	
	public boolean hasTypes(Set<CardType> types, TypeOfMatch matching) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(types != null, "setCode is null");
		
		if (matching == TypeOfMatch.EXACT_MATCH) {
			if (this.types.equals(types)) {
				return true;
			} else {
				return false;
			}
			
		} else if (matching == TypeOfMatch.INCLUDE_LEAST_ONE) {
			for (CardType currentType : this.types) {
				if (types.contains(currentType)) {
					return true;
				}
			}
			return false;
		} else {
			throw new InvalidModeException("Invalid/Unimplemented TypeOfMatch");
		}
	}
	
	public boolean hasCMC(int cmc) {
		return (this.cmc == cmc);
	}
	
	public boolean hasFullType(String fullType) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(fullType != null, "fullType is null");
		
		if (this.fullType.equals(fullType)) {
			return true;
		} else {
			return false;
		}
		//TODO:: Potentially have the check see if the string is included at all in String
	}
	
	public boolean hasText(String text) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(text != null, "text is null");
		
		if (this.text.equals(text)) {
			return true;
		} else {
			return false;
		}
		//TODO:: Potentially have the check see if the string is included at all in String
	}
	
	
	///////////////////////////////////
	/*Private Methods*/
	///////////////////////////////////
	
}
