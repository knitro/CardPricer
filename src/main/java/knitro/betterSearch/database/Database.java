package knitro.betterSearch.database;

import java.util.Map;
import java.util.Set;

import knitro.betterSearch.database.card.DbItem;
import knitro.betterSearch.database.search.Search;

public interface Database {
	
	///////////////////////////////////
	/*Initialisation Methods*/
	///////////////////////////////////

	public abstract boolean loadDatabase();
	
	public abstract boolean isLoaded();
	
	public abstract boolean reloadDatabase();
	
	
	///////////////////////////////////
	/*Main Methods*/
	///////////////////////////////////
	
	public abstract Set<DbItem> getCards(Search card);
	
	///////////////////////////////////
	/*Other Methods*/
	///////////////////////////////////
	
	public abstract Map<String, String> getSetMap();
}
