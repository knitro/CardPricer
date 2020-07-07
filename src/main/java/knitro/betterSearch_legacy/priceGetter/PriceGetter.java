package knitro.betterSearch_legacy.priceGetter;

import java.util.List;

import knitro.betterSearch_legacy.priceGetter.info.CardInfo;
import knitro.betterSearch_legacy.search.Search;
import knitro.betterSearch_legacy.search.Style;

public interface PriceGetter {
	
	public abstract List<CardInfo> getCardInfo_buy(Search searchTerm);
	
	public abstract List<CardInfo> getCardInfo_sell(Search searchTerm);
	
	public abstract String getSpecificCardURL(String cardName, String setName, int collectorNumber, Style style );
	
}
