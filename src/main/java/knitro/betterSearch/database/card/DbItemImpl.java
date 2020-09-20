package knitro.betterSearch.database.card;

import java.util.Set;

import knitro.betterSearch.database.filter.CardColour;
import knitro.betterSearch.database.filter.CardType;

public class DbItemImpl extends DbItem {

	public DbItemImpl(String name, Set<CardColour> colours, Set<CardType> subtypes, int cmc, String fullType, String text) {
		super(name, colours, subtypes, cmc, fullType, text);
	}
	
	public static final DbItem EMPTY = new DbItemImpl();
	
	private DbItemImpl() {
		super();
	}

}
