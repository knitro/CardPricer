package knitro.betterSearch.priceGetter.scg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import knitro.betterSearch.database.search.Search;
import knitro.betterSearch.database.search.Style;
import knitro.betterSearch.priceGetter.AbstractPriceGetter;
import knitro.betterSearch_legacy.priceGetter.info.CardImage;
import knitro.betterSearch_legacy.priceGetter.info.CardInfo;
import knitro.betterSearch_legacy.priceGetter.info.impl.CardInfoImpl;
import knitro.support.InvalidModeException;
import knitro.support.Preconditions;

public class StarCityGames extends AbstractPriceGetter {

	///////////////////////////////////
	/*Fields*/
	///////////////////////////////////
	
	///////////////////////////////////
	/*Constructors*/
	///////////////////////////////////
	
	public StarCityGames() {
		super();
	}
	
	///////////////////////////////////
	/*Overridden Methods*/
	///////////////////////////////////
	
	@Override
	public List<CardInfo>  getCardInfo_buy(Search searchTerm) {
		//TODO::
		return null;
	}

	@Override
	public List<CardInfo> getCardInfo_sell(Search searchTerm) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(searchTerm != null, "searchTerm is null");
		Preconditions.preconditionCheck(searchTerm.getIsSell(), "searchTerm is not sell");
		
		/*Get Unparsed Content from all possible pages*/
		int pageNumber = 1;
		List<CardInfo> listOfResults = new ArrayList<>();
		
		//Loop for all Pages
		pageLoop: while (true) {
			
			String unparsedContent = null;
			unparsedContent = getUnparsedSearchQuery_sell(searchTerm, pageNumber);
			
			/*Send through parser. Get a List of Results*/
			List<CardInfo> partialResults = null;
			partialResults = parseContent_sell(unparsedContent);
			
			if (partialResults.size() == 0) {
				break pageLoop;
			}
			
			listOfResults.addAll(partialResults);
			pageNumber++;
		}
		
		/*Print out Cards Found*/
		for (CardInfo card : listOfResults) {
			System.out.println(card.toString());
		}
		
		/*Remove Duplicates*/
		//Removed for being too slow --> O(N^2)
		/*
		List<CardInfo> noDuplicatesList = new ArrayList<>();
		for (CardInfo card : listOfResults) {
			boolean doesNotContain = true;
			checkLoop : for (CardInfo check : noDuplicatesList) {
				if (card.equals(check)) {
					doesNotContain = false;
					break checkLoop;
				}
			}
			if (doesNotContain) {
				noDuplicatesList.add(card);
			}
		}
		*/
		
		/*Sort and Filter Results*/
		List<CardInfo> filteredResults = new ArrayList<>();
		String searchTerm_string = searchTerm.getSearchTerm();
		
//		for (CardInfo card : noDuplicatesList) {
		for (CardInfo card : listOfResults) {
			String cardName = card.getCardName();
			
			//Card Name must be contained in search term
			if (searchTerm_string.contains(cardName)) {
				filteredResults.add(card);
			}
		}
		
		
		/*Return Appropriately*/
		return filteredResults;
	}
	
	///////////////////////////////////
	/*Public Methods*/
	///////////////////////////////////
	
	///////////////////////////////////
	/*Protected Methods*/
	///////////////////////////////////
	
	protected String getUnparsedSearchQuery_buy(Search searchTerm) {
		
		/*Preconditions*/ //All checks are performed by OpenWeatherChecker
		Preconditions.preconditionCheck(searchTerm != null, "searchTerm is null");
		Preconditions.preconditionCheck(searchTerm.getIsBuy(), "searchTerm is not buy");
		
		/*Initialisations*/	
		String returnString = null;
		
		/*Get URL*/
		try {
			URL url = new URL("https://starcitygames.com/"
					+ "search.php?search_query=%22Llanowar%2BElves%22");
			returnString = getURLContents(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*Return Value w/Checks*/
		if (returnString == null) {
			throw new RuntimeException("URL is incorrect");
		}
		return returnString;
	}
	
	protected String getUnparsedSearchQuery_sell(Search searchTerm, int pageNumber) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(searchTerm != null, "searchTerm is null");
		Preconditions.preconditionCheck(searchTerm.getIsSell(), "searchTerm is not sell");
		
		/*Initialisations*/	
		String returnString = null;
		
		/*Convert Search Term into Appropriate String*/
		String urlAddition = searchTerm.getSearchTerm();
		urlAddition = urlAddition.replaceAll(" ", "%20"); //Replace space with HTML encoding
		urlAddition = urlAddition.replaceAll(",", "%2C"); //Replace comma with HTML encoding
		urlAddition = urlAddition.concat("\""); //Add Quotation Mark at End
		urlAddition = "\"".concat(urlAddition); //Add Quotation Mark at Start
		urlAddition = urlAddition.concat("&page="
				+ pageNumber
				+ "&section=product"
		);
		
		/*Get URL*/
		try {
			//Replace Space with 
			URL url = new URL("https://starcitygames.com/"
					+ "search.php?search_query="
					+ urlAddition);
			returnString = getURLContents(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*Return Value w/Checks*/
		if (returnString == null) {
			throw new RuntimeException("URL is incorrect");
		}
		return returnString;
	}
	
	protected List<CardInfo> parseContent_sell(String unparsedContent) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(unparsedContent != null, "unparsedContent is null");

		/*Initialisation*/
		List<CardInfo> listOfResults = new ArrayList<>();
		
		/*Parsing to Correct DOM Node*/
		Document doc = Jsoup.parse(unparsedContent);
		Element productListingContainer = doc.getElementById("product-listing-container");
		
		/*If there are no products, return empty List*/
		if (productListingContainer.select("form").size() == 1) {
			return new ArrayList<CardInfo>();
		}
		
		Element categoryProducts = productListingContainer.select("form").get(1);//getElementById("category-products");
		Element productList = categoryProducts.select("article").first();//("productList");	
		Elements table = productList.select("table");
		
		Elements rows = table.select("tr");
		
		/*Parse through Table*/
		for (int i = 1; i < rows.size(); i++) {
			
			/*For-Loop Initialisation*/
			Element row = rows.get(i);
			
			/*Variable Initialisation*/
			String cardImageURL = null;
			String cardName = null;
			String cardURL = null;
				
			/*td-listItem --Name fixed*/
			Element column = row.getElementsByAttributeValue("class", "td-listItem --Name fixed").first();
				
			//Get CardImage
			Element listItem_figure = column.select("figure").first();
			Element image = listItem_figure.select("img").first();
			cardImageURL = image.absUrl("data-src");
			
			//Get Card Name and URL
			Element listItem_details = column.select("div").first();
			Element listItem_title = listItem_details.select("h4").first();
			Element link = listItem_title.select("a").first();
			cardName = link.text();
			cardURL = link.attr("href");
			
			/*Clean Up Data Scrape*/
			cardName = removeCardNameTag(cardName);
			
			/*Create CardInfo if Valid*/
			
			if ((cardImageURL != null) && (cardName != null) && (cardURL != null)) {
				CardImage cardImage = new CardImage(cardImageURL);
				CardInfo currentSearchedCard = new CardInfoImpl(cardName, cardURL, cardImage);
				
				listOfResults.add(currentSearchedCard);
				System.out.println("# " + listOfResults.size() +" Card Found");
			}
		}
		
		/*Return*/
		return listOfResults;
		
	} 
	
	///////////////////////////////////
	/*Private Methods*/
	///////////////////////////////////
	
	/**
	 * Returns a String representation of the URL.
	 * This method assumes that the URL provided is valid!
	 * @param url - the URL to get the content from.
	 * @return a String with the contents of the URL.
	 */
	private String getURLContents(URL url) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(url != null, "ERROR: url Parameter is null");
		
		/*Variable Initialisation*/
		StringBuilder requestString = new StringBuilder();
		
		/*Get the URL Contents*/
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String inputLine = "";
	        while (inputLine != null) {
	        	inputLine = reader.readLine();
//	        	System.out.println(inputLine);
	        	requestString.append(inputLine);
	        }
	        reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String returnString = requestString.toString();
		return returnString;
	}
	
	/**
	 * Removes the Card Tag applied by SCG on the HTML.
	 * This is required since in the HTML, the card names have a tag describing the card.
	 * Eg. Mind Stone [SGL-MTG-DD2-22-ENN]
	 * @param cardName - The string to have the tag removed.
	 * @return the Card name without the tag.
	 */
	private String removeCardNameTag(String cardName) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(cardName != null, "Input string is null");
		
		/*Detect the Tag*/
		for (int i = 0; i < cardName.length(); i++) {
			char charAtIndex = cardName.charAt(i);
			if (charAtIndex == '[') {
				if (i == 0) { //Card started off with [
					return cardName;
				} else {
					int indexRemoveSpace = i - 1;
					String cardName_withoutTag = cardName.substring(0, indexRemoveSpace);
					return cardName_withoutTag;
				}
			}
		}
		
		/*Returns at failure*/
		//At this point, the string is deemed to not have a tag.
		return cardName;
	}

	@Override
	public String getSpecificCardURL(String cardName, String setName, String collectorNumber, Style style) {
		
		StringBuilder sb = new StringBuilder();
		sb.append("https://starcitygames.com/");
		
		//Example:
//		sb.append("karn-liberated-sgl-mtg-nph-1-enn/");
		
		//Card Name
		String cardName_lower = cardName.toLowerCase();
		String cardName_dashes = cardName_lower.replaceAll(" ", "-");
		String cardName_apostrophe = cardName_dashes.replaceAll("'", "");
		String cardName_comma = cardName_apostrophe.replaceAll(",", "");
		sb.append(cardName_comma);
		sb.append("-");
		
		//Set Name
		sb.append("sgl-mtg-"); //Default for MTG Cards
		String setNameLowerCase = setName.toLowerCase();
		sb.append(setNameLowerCase);
		sb.append("-");
		
		//Collector Number
		sb.append(collectorNumber);
		
		//Foiling
		if (style.equals(Style.NON_FOIL)) {
			sb.append("-enn/");
		} else if (style.equals(Style.FOIL)) {
			sb.append("-enf/");
		} else {
			throw new InvalidModeException("Style not recognised");
		}
		
		
		String returnString = sb.toString();
		return returnString;
	}

	@Override
	public double getSpecificCardPrice(String cardName, String setName, String collectorNumber, Style style) {
		
		String url = getSpecificCardURL(cardName, setName, collectorNumber, style);
		String urlContents = null;
		try {
			urlContents = getURLContents(new URL(url));
//			System.out.println(urlContents);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Should not occur: getSpecificCardURL broken while using getSpecificCardPrice");
		}
		
		/*Parsing to Correct DOM Node*/
		Document doc = Jsoup.parse(urlContents);
		
		System.out.println("url = " + url);
		
		Elements productSectionClasses = doc.select("span.price.price--withoutTax");
		Element productSectionClass = productSectionClasses.get(0); //Should be the first one
		
		String price = productSectionClass.text();
		String price_editted = price.replace("$", "");
		
		return Double.parseDouble(price_editted);
	}
	
}
