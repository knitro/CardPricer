package knitro.betterSearch.database.search;

import knitro.betterSearch.database.filter.Filter;

public interface Search {
	
	public abstract String getSearchTerm();
	
	public abstract boolean getIsBuy();
	
	public abstract boolean getIsSell();

	public abstract int getMarginOfError();
	
	public abstract Filter getFilter();
}
