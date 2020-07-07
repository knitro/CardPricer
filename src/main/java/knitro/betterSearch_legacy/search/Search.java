package knitro.betterSearch_legacy.search;

import knitro.betterSearch_legacy.database.filter.Filter;

public interface Search {
	
	public abstract String getSearchTerm();
	
	public abstract boolean getIsBuy();
	
	public abstract boolean getIsSell();

	public abstract int getMarginOfError();
	
	public abstract Filter getFilter();
}
