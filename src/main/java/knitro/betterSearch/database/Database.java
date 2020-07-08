package knitro.betterSearch.database;

import java.util.Map;
import java.util.Set;

import knitro.betterSearch.database.card.DbItem;
import knitro.betterSearch_legacy.search.Search;

public interface Database {

	public abstract Set<DbItem> getCards(Search card);
	
	public abstract boolean loadDatabase();
	
	public abstract boolean isLoaded();
	
	public abstract boolean reloadDatabase();
	
	public abstract Map<String, String> getSetMap();
}
