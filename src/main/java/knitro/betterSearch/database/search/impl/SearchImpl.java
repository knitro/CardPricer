package knitro.betterSearch.database.search.impl;

import knitro.betterSearch.database.filter.Filter;
import knitro.betterSearch.database.search.Search;

public class SearchImpl implements Search {
	
	///////////////////////////////////
	/*Fields*/
	///////////////////////////////////
	
	private final String searchTerm;
	private final boolean isBuy;
	private final int marginOfError;
	private final Filter filter;
	
	///////////////////////////////////
	/*Constructors*/
	///////////////////////////////////

	/**
	 * Default Constructor for SearchImpl.
	 * @param searchTerm - The string term that is being used to compare with other card names.
	 * @param isBuy - Determines whether the search is for buying price (true), or selling price (false).
	 * @param marginOfError - The margin of error (MoE) using Levenshtein Distance that is allowed for the search.
	 * @param filter - The filter of the search.
	 */
	public SearchImpl(String searchTerm, boolean isBuy, int marginOfError, Filter filter) {
		super();
		this.searchTerm = searchTerm;
		this.isBuy = isBuy;
		this.marginOfError = marginOfError;
		this.filter = filter;
	}
	
	/**
	 * Creates a new SearchImpl instance.
	 * This constructor is used if you want to update an older search with the appropriate new card name.
	 * @param cardName
	 * @param oldSearch
	 */
	public SearchImpl(String cardName, Search oldSearch) {
		super();
		this.searchTerm = cardName;
		this.isBuy = oldSearch.getIsBuy();
		this.marginOfError = oldSearch.getMarginOfError();
		this.filter = oldSearch.getFilter();
	}
	
	///////////////////////////////////
	/*Overridden Methods*/
	///////////////////////////////////
	
	@Override
	public String getSearchTerm() {
		return searchTerm;
	}

	@Override
	public boolean getIsBuy() {
		return isBuy;
	}

	@Override
	public boolean getIsSell() {
		return !isBuy;
	}
	
	@Override
	public int getMarginOfError() {
		return marginOfError;
	}


	@Override
	public Filter getFilter() {
		return filter;
	}
	
}
