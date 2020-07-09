package knitro.betterSearch.priceGetter;

import java.util.List;

import knitro.betterSearch.database.search.Search;
import knitro.betterSearch.database.search.Style;
import knitro.betterSearch_legacy.priceGetter.info.CardInfo;

public interface PriceGetter {
	
	public abstract List<CardInfo> getCardInfo_buy(Search searchTerm);
	
	public abstract List<CardInfo> getCardInfo_sell(Search searchTerm);
	
	public abstract String getSpecificCardURL(String cardName, String setName, String collectorNumber, Style style);
	
	public abstract double getSpecificCardPrice(String cardName, String setName, String collectorNumber, Style style);
}
