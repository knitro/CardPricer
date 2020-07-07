package knitro.betterSearch_legacy.database.filter;

import java.util.HashSet;
import java.util.Set;

import knitro.betterSearch_legacy.search.Style;
import knitro.support.Preconditions;

public class Filter {
	
	///////////////////////////////////
	/*Fields*/
	///////////////////////////////////
	
	/*Defaults for "NO_FILTER" or "No Filter Applied for this Field"*/
	public static final String NO_FILTER_SETCODE = "NO_FILTER";
	/*colour is assumed to have no filter if the set is empty*/
	/*types is assumed to have no filter if set is empty*/
	/*cmc is assumed to have no filter if cmc = -1*/
	/*style is assumed to have "no filter" if style = Style.ANY*/
	
	private String setCode;
	private Set<CardColour> colour;
	private Set<CardType> types;
	private int cmc;
	private Style style;
	
	private boolean isNoFilter;
	
	///////////////////////////////////
	/*Constructors*/
	///////////////////////////////////
	
	/**
	 * Generates a NO_FILTER.
	 * This means that the filter has no "actual filters" applied.
	 */
	public Filter() {
		
		super();
		
		/*Initialisations required as final fields*/
		this.setCode = NO_FILTER_SETCODE;
		this.colour = new HashSet<>();
		this.types = new HashSet<>();
		this.cmc = -1;
		this.style = Style.ANY;
		
		isNoFilter = true;
	}
	
	/**
	 * Generates a filter given the following parameters.
	 * @param setCode - the setCode being applied to the filter
	 * @param colour - the colour/s being applied to the filter
	 * @param types - the type/s being applied to the filter
	 * @param cmc - the CMC being applied to the filter
	 * @param style - the style being applied to the filter
	 */
	public Filter(String setCode, Set<CardColour> colour, Set<CardType> types, int cmc, Style style) {
		super();
		
		/*Preconditions*/
		Preconditions.preconditionCheck(isValidSetCode(setCode), "Invalid SetCode");
		Preconditions.preconditionCheck(colour != null, "colour is null");
		Preconditions.preconditionCheck(types != null, "types is null");
		Preconditions.preconditionCheck(isValidCMC(cmc),"Invalid CMC");
		Preconditions.preconditionCheck(style != null, "currentStyle is null");
		
		this.setCode = setCode;
		this.colour = colour;
		this.types = types;
		this.cmc = cmc;
		this.style = style;
		
		isNoFilter = false;
	}

	///////////////////////////////////
	/*Public Checker Methods*/
	///////////////////////////////////
	
	public boolean checkSetCode() {
		return (setCode != NO_FILTER_SETCODE);
	}
	
	public boolean checkColour() {
		return (!colour.isEmpty());
	}
	
	public boolean checkTypes() {
		return (!types.isEmpty());
	}
	
	public boolean checkCMC() {
		return (cmc != -1);
	}
	
	public boolean checkNoFilters() {
		return isNoFilter;
	}
	
	///////////////////////////////////
	/*Public Getter Methods*/
	///////////////////////////////////
	
	public String getSetCode() {
		return setCode;
	}

	public Set<CardColour> getColour() {
		return colour;
	}

	public Set<CardType> getTypes() {
		return types;
	}

	public int getCMC() {
		return cmc;
	}

	public Style getStyle() {
		return style;
	}
	
	public boolean getIsNoFilter() {
		return isNoFilter;
	}

	///////////////////////////////////
	/*Public Setter Methods*/
	///////////////////////////////////
	
	public void setSetCode(String setCode) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(isValidSetCode(setCode), "Invalid SetCode");
		
		this.setCode = setCode;
		
		//Check if it is NO_FILTER or not
		checkNoFilter();
	}

	public void setColour(Set<CardColour> colour) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(colour != null, "colour is null");
		
		this.colour = colour;
		
		//Check if it is NO_FILTER or not
		checkNoFilter();
	}

	public void setTypes(Set<CardType> types) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(types != null, "types is null");
		
		this.types = types;
		
		//Check if it is NO_FILTER or not
		checkNoFilter();
	}

	public void setCMC(int cmc) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(isValidCMC(cmc),"Invalid CMC");
		
		this.cmc = cmc;
		
		//Check if it is NO_FILTER or not
		checkNoFilter();
	}

	public void setStyle(Style style) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(style != null, "currentStyle is null");
		
		this.style = style;
		
		//Check if it is NO_FILTER or not
		checkNoFilter();
	}
	
	/**
	 * This method gets a string (In the form of [SETCODE] - [SETNAME]), and extracts the set code, 
	 * and then applies it to the filter.
	 * This method is generally used when you are going through a choice list, and the set name 
	 * also needs to be displayed as well as the set code.
	 * @param fullString - the string that the set code is being extracted from.
	 */
	public void extractAndApplySetCode(String fullString) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(fullString != null, "fullString is null");
		
		/*Extract SetCode*/
		for (int i = 0; i < fullString.length(); i++) {
			char currentChar = fullString.charAt(i);
			if (currentChar == '-') {
				//Index of last char of setcode is the index of '-' - 1 (remove space)
				int indexOfSetCode = i - 1;
				String setCode = fullString.substring(0, indexOfSetCode);
				setSetCode(setCode);
			}
		}
	}
	
	///////////////////////////////////
	/*Private Methods*/
	///////////////////////////////////

	/**
	 * Determines whether the String provided for the constructor is a valid setCode.
	 * This method is important to use as it determines obscure setCodes to be individually checked, 
	 * as well as allowing the String "NO_FILTER" passed through as a "No Filter to be applied" option. 
	 * @param setCode - String that is being determined to be a valid setCode or not.
	 * @return true if String parameter is a valid setCode, otherwise false.
	 */
	private boolean isValidSetCode(String setCode) {
		
		if (setCode == null) {
			System.out.println("setCode is null");
			return false;
		}
		if (setCode.equals(NO_FILTER_SETCODE)) {
			return true;
		} else if (setCode.length() == 6) {
			//TODO:: Add proper set checks here
			return true;
		} else if (setCode.length() == 5) {
			//TODO:: Add proper set checks here
			return true;
		} else if (setCode.length() == 4) {
			//TODO:: Add proper set checks here
			return true;
		} else if (setCode.length() == 3) {
			//TODO:: Add proper set checks here.
			return true;
		} else
			System.out.println("INVALID SETCODE = " + setCode); //For Debugging Purposes
			//Did not pass any of the conditions to be valid, therefore failed.
			return false;
	}

	/**
	 * Determines whether the int provided is a valid CMC or not.
	 * This method is important to use as it considers whether the int value is -1 or not,
	 * meaning "assume no filter for CMC field", while also neglecting all other negative values.
	 * @param cmc - the int being checked to be valid or not
	 * @return true if the cmc value is valid or not, otherwise false.
	 */
	private boolean isValidCMC(int cmc) {
		
		/*Check if CMC is "NO_FILTER"*/
		if (cmc == -1) {
			return true;
		}
		/*Check if CMC is 0 or above*/
		else if (cmc >= 0) {
			return true;
		}
		/*Return false otherwise as all positive valid conditions are not met*/
		return false;
	}
	
	/**
	 * Determines whether the currentFilter is a NO_FILTER or not
	 */
	private void checkNoFilter() {
		
		if (setCode == NO_FILTER_SETCODE) {
			if (colour.isEmpty()) {
				if (types.isEmpty()) {
					if (cmc == -1) {
						if (style == Style.ANY) {
							isNoFilter = true;
						}
					}
				}
			}
		}
		
		isNoFilter = false;
	}
}
