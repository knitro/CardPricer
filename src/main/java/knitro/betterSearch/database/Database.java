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
	
	/**
	 * Returns the Map of Sets.
	 * <ul>
	 * 	<li>String 1 = SetCode
	 * 	<li>String 2 = SetName
	 * </ul>
	 * @return
	 */
	public abstract Map<String, String> getSetMap();

	public abstract Set<String> getMatchingSets(String param);
	
	public abstract Set<DbItem> getCardsMatchingSet(String setUUID);
}
