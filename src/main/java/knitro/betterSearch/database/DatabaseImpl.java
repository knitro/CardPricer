package knitro.betterSearch.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;

import org.apache.commons.io.FileUtils;

import knitro.betterSearch.database.card.DbItem;
import knitro.betterSearch.database.card.DbItemImpl;
import knitro.betterSearch.database.card.DbPrinting;
import knitro.betterSearch.database.filter.CardColour;
import knitro.betterSearch.database.filter.CardType;
import knitro.betterSearch.database.filter.Filter;
import knitro.betterSearch.database.filter.TypeOfMatch;
import knitro.betterSearch.database.search.Search;
import knitro.betterSearch.database.typoanalysis.TypoAnalysis;
import knitro.support.Preconditions;

public class DatabaseImpl implements Database {

	///////////////////////////////////
	/*Constants*/
	///////////////////////////////////
	
	/*Limits*/
	public static final int MAX_NUM_OF_RESULTS = 10000;
	
	/*Locations*/
	private static final String DB_LOCATION = "src/main/resources/data/mtg/";
	private static final String DB_NAME = "AllPrintings.json";
	private static final String SET_LIST_NAME = "SetList.json";
//	private static final String TYPE_LIST_NAME = "CardTypes.json";
	
	/*Important URLs*/
	private static final String URL_DB = "https://mtgjson.com/api/v5/AllPrintings.json";
	private static final String URL_SET = "https://mtgjson.com/api/v5/SetList.json";
	
	private static final Comparator<DbItem> dbItemComparator = new Comparator<DbItem>(){
		
		@Override
		public int compare(DbItem o1, DbItem o2) {
			
			//Compare Sorting Value (using TypoAnalysis)
			if (o1.getSortingValue() < o2.getSortingValue()) {
				return 1;
			} else if (o1.getSortingValue() > o2.getSortingValue())  {
				return -1;
			}
			
			//Compare Dates
			DbPrinting o1_print = o1.getDbPrinting(0);
			DbPrinting o2_print = o2.getDbPrinting(0);
			
			Date o1_date = o1_print.getDate();
			Date o2_date = o2_print.getDate();
			
			if (o1_date.after(o2_date)) {
				return -1;
			} else if (o1_date.before(o2_date)) {
				return 1;
			} 
			
			//Compare Names
			return (o1.getName().compareTo(o2.getName()));
		}
	};

	
	///////////////////////////////////
	/*Fields*/
	///////////////////////////////////
	
	/*General Fields*/
	private boolean loaded;
	
	/*Main Database*/
	private Set<String> cardNames;
	private Map<String, DbItem> database;
	
	/*Set List Database*/
	private Map<String, String> setListMap;
	
	//TODO:: Make this adjustable
	private TypeOfMatch matching = TypeOfMatch.INCLUDE_LEAST_ONE;
	
	
	///////////////////////////////////
	/*Constructors*/
	///////////////////////////////////
	
	public DatabaseImpl() {
		super();
		loaded = false;
		cardNames = new HashSet<>();
	}

	///////////////////////////////////
	/*Overridden Methods*/
	///////////////////////////////////
	
	@Override
	public Set<DbItem> getCards(Search card) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(card != null, "card is null");
		System.out.println("Beginning Search for: " + card.getSearchTerm());
		
		/*Variable Initialisation*/
		String searchTerm = card.getSearchTerm().toLowerCase();
		int marginOfError = card.getMarginOfError();
		Map<String, DbItem> filteredDatabase = new HashMap<>();
		Filter searchFilter = card.getFilter();
		Set<DbItem> results = new TreeSet<>(dbItemComparator);
		
		/*Get Possible Matching Cards*/
		//Step 1: Apply any Filters
		
		//Step 1a: Check if there is any filters are applied
		if (!searchFilter.getIsNoFilter()) {
			
			//Loop through Database to filter DbItems
			for (String cardName : database.keySet()) {
				DbItem currentDbItem = database.get(cardName);
				
				//Step 1b: Apply setCode Filter
				if (searchFilter.checkSetCode()) {
					String setCode = searchFilter.getSetCode();
					if (!currentDbItem.hasSetCode(setCode)) {
						continue;
					}
				}
				
				//Step 1c: Apply colour Filter
				if (searchFilter.checkColour()) {
					Set<CardColour> colours = searchFilter.getColour();
					if (!currentDbItem.hasColours(colours, matching)) {
						continue;
					}
				}
				
				//Step 1d: Apply types Filter
				if (searchFilter.checkTypes()) {
					Set<CardType> types = searchFilter.getTypes();
					if (!currentDbItem.hasTypes(types, matching)) {
						continue;
					}
				}
				
				//Step 1e: Apply CMC Filter
				if (searchFilter.checkCMC()) {
					int cmc = searchFilter.getCMC();
					if (!currentDbItem.hasCMC(cmc)) {
						continue;
					}
				}
				
				//Passed all Filters, therefore add to filteredDatabase
				filteredDatabase.put(cardName, currentDbItem);
			}
		} else {
			filteredDatabase.putAll(database);
		}
		
		//Step 2: Apply cardName matches
		Set<String> filteredCardNames = filteredDatabase.keySet();

		//Step 2a: Get exact matches
		
		//Get Cleaned Search Terms:
		searchTerm = TypoAnalysis.removeSpecialChars(searchTerm);
		
		if (filteredCardNames.contains(searchTerm)) {
			DbItem result = filteredDatabase.get(searchTerm);
			results.add(result);
			System.out.println("Found exact Card!");
			return results;
		}
		
		//Step 2b: Get cards that have the word contained within
		for (String cardName : filteredCardNames) {
			
			String cardName_cleaned = TypoAnalysis.removeSpecialChars(cardName);
			if (cardName_cleaned.contains(searchTerm)) {
				DbItem result = filteredDatabase.get(cardName);
				results.add(result);
				if (results.size() >= MAX_NUM_OF_RESULTS) {
					System.out.println("Too many results. Displaying Top results");
					return results;
				}
			}		
		}
		
		//Step 2c: Get close matches using the TypoAnalysis Package.
		
		for (String cardName : filteredCardNames) {
			int distance = TypoAnalysis.getDifference(searchTerm, cardName);
			
			//Check if card meets Margin of Error condition
			if (distance <= marginOfError) {
				DbItem result = filteredDatabase.get(cardName);
				
				this.setSortingValue(result);
				
				results.add(result);
				if (results.size() >= MAX_NUM_OF_RESULTS) {
					System.out.println("Too many results. Displaying Top results");
					return results;
				}
			}		
		}
		
		System.out.println("Finished Search for: \"" + card.getSearchTerm() 
				+ "\" with " + results.size() + " results");
		
		return results;
	}
	
	@Override
	public boolean loadDatabase() {
		
		/*Precondition: Avoid reloaded Database again*/
		if (loaded) {
			return true;
		}
		System.out.println("Database Loading: Started");
		
		/*Precondition: Check if File Exists*/
		checkDatabaseFileExists();
		
		/*Load all the Relevant Databases*/
		loadAllCards();
		loadSetList();
		
		/*Adjust Field and Return*/
		System.out.println("Database Loading: Finished");
		loaded = true;
		return true;
	}

	@Override
	public boolean isLoaded() {
		return loaded;
	}

	@Override
	public boolean reloadDatabase() {
		this.loaded = false;
		return loadDatabase();
	}
	
	@Override
	public Map<String, String> getSetMap() {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(loaded == true, "Database has not loaded");
		
		/*Return*/
		return Collections.unmodifiableMap(setListMap);
	}
	
	///////////////////////////////////
	/*Public Methods*/
	///////////////////////////////////
	
	///////////////////////////////////
	/*Private Methods*/
	///////////////////////////////////
	
	/**
	 * Checks if the database file exists or not.
	 * If not, it will execute the {@link #downloadFile()} method.
	 */
	private void checkDatabaseFileExists() {
		
		/*Check for Main Database File*/
		
		try {
			if (!new File(DB_LOCATION + DB_NAME).exists()) {
				downloadFile(URL_DB, DB_LOCATION + DB_NAME);
			}
			
			/*Check for Set List File*/
			if (!new File(DB_LOCATION + SET_LIST_NAME).exists()) {
				downloadFile(URL_SET, DB_LOCATION + SET_LIST_NAME);
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("This should not occur");
		} catch (IOException e) {
			throw new RuntimeException("This should not occur");
		}
		
		return;
	}
	
	/**
	 * Downloads a File from a given URL to a given file location.
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	private void downloadFile(String url, String fileLocation) throws MalformedURLException, IOException {
		FileUtils.copyURLToFile(
				  new URL(url), 
				  new File(fileLocation), 
				  500, 
				  500);
	}
	
	private JsonReader loadJSON(String directory) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(directory != null, "directory is null");
		
		/*Load Database*/
		File file = new File(directory);
		InputStream fileStream = null;
		try {
			fileStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		JsonReader jsonReader = Json.createReader(fileStream);
		
		/*Return and Clean-Up*/
		return jsonReader;
	}
	
	private void loadAllCards() {
		
		/*Variable Initialisation*/
		System.out.println("AllPrintings Loading: Started");
		database = new HashMap<>();
		
		/*Load Database*/
		String directory = DB_LOCATION + DB_NAME;
		
		JsonReader jsonReader = loadJSON(directory);
		JsonObject overarchJSON = jsonReader.readObject();
		JsonValue dataJSON = overarchJSON.get("data");
		JsonObject dataJSON_object = dataJSON.asJsonObject();
		
		
		
		/*Load Database in Map*/
		for (String attribute: dataJSON_object.keySet()) {
			
			//Get Set
			JsonValue currentSet_value = dataJSON_object.get(attribute);
			JsonObject currentSet_object = currentSet_value.asJsonObject();
			String currentSet_code = currentSet_object.getString("code");
			
			//Add the Release Date of the set
			String releaseDateString = currentSet_object.getString("releaseDate");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
			format.setTimeZone(TimeZone.getTimeZone("UTC"));
			Date currentDate = null;
			try {
				currentDate = format.parse(releaseDateString.trim());
			} catch (ParseException e) {
				e.printStackTrace();
				throw new RuntimeException("Date Parsing Error");
			}
			
			//Get Cards
			JsonValue currentCards_value = currentSet_object.get("cards");
			JsonArray currentCards_array = currentCards_value.asJsonArray();
			
			//Add All the Cards
			for (int setIndex = 0; setIndex < currentCards_array.size(); setIndex++) {
				
				//Get the JSON Card Value
				JsonValue currentCard_value = currentCards_array.get(setIndex);
				JsonObject currentJSON = currentCard_value.asJsonObject();
				
				//Get Card Name
				String cardName = currentJSON.getString("name");
				final String lowercaseName = cardName.toLowerCase(); 
				
				//Check if Card already exists in Database, Otherwise Get the Card
				DbItem currentCardEntry = null;
				if (database.containsKey(lowercaseName)) {
					currentCardEntry = database.get(lowercaseName);
				} else {
					
					//Get Colours
					JsonArray coloursJSON = currentJSON. getJsonArray("colorIdentity");
					Set<CardColour> colours = new HashSet<>();
					for (int j = 0; j < coloursJSON.size(); j++) {
						String currentColour_string = coloursJSON.getString(j);
						CardColour currentColour_cardColour = CardColour.getCardColour(currentColour_string);
						colours.add(currentColour_cardColour);
					}
					
					//Get Types
					JsonArray typesJSON = currentJSON.getJsonArray("types");
					Set<CardType> types = new HashSet<>();
					for (int j = 0; j < typesJSON.size(); j++) {
						String currentPrinting_string = typesJSON.getString(j);
						CardType currentPrinting_cardType = CardType.getCardSubType(currentPrinting_string);
						if (currentPrinting_cardType != CardType.META) {
							types.add(currentPrinting_cardType);
						}
					}
					
					//Get CMC
					int cmc = currentJSON.getInt("convertedManaCost");
					
					//Get Full Type
					String fullType = currentJSON.getString("type");
					
					//Get Card Text
					JsonString value = currentJSON.getJsonString("text");
					String text = null;
					if (value == null) { //This is here since getString("text") can return null
						text = "";
					} else {
						text = value.getString();
					}
					
					//Create DbItem and place into Database
					currentCardEntry = new DbItemImpl(cardName, colours, types, cmc, fullType, text);
					database.put(lowercaseName, currentCardEntry);
					cardNames.add(lowercaseName);
				}
				
				//Get the DbPrinting
				String currentID = currentJSON.getString("number");
				
				boolean hasFoil = currentJSON.getBoolean("hasFoil");
				boolean hasNonFoil = currentJSON.getBoolean("hasNonFoil");
				
				//Create add add the instance
				DbPrinting currentPrinting = new DbPrinting(currentSet_code, currentID, currentDate, hasFoil, hasNonFoil);
				currentCardEntry.addPrinting(currentPrinting);
			}
		}
		
		/*Clean-Up*/
		System.out.println("AllPrintings Loading: Finished");
	}
	
	private void loadSetList() {
		
		/*Variable Initialisation*/
		System.out.println("SetList Loading: Started");
		setListMap = new HashMap<>();
		
		/*Load Database*/
		String directory = DB_LOCATION + SET_LIST_NAME;
		JsonReader jsonReader = loadJSON(directory);
		JsonObject overarchJSON = jsonReader.readObject();
		JsonValue dataJSON = overarchJSON.get("data");
		JsonArray arrayJSON = dataJSON.asJsonArray();
		
		/*Load Database in Map*/
		for (int j = 0; j < arrayJSON.size(); j++) {
			
			JsonValue currentValue = arrayJSON.get(j);
			JsonObject currentJSON = currentValue.asJsonObject();
			
			//Get Set Code
			String currentSetCode = currentJSON.getString("code");
			//Get Set Name
			String currentSetName = currentJSON.getString("name");
			
			//Create Map Entry
			setListMap.put(currentSetCode, currentSetName);
		}
		
		/*Clean-Up*/
		System.out.println("AllCards Loading: Finished");
		
	}

	private void setSortingValue(DbItem result) {
		
//		result.setSortingValue(distance);
		DbPrinting latestPrinting = result.getDbPrinting(0);
		Date releaseDate = latestPrinting.getDate();
		result.setSortingValue(releaseDate.getTime());
		
	}
	
	///////////////////////////////////
	/*Main Method*/
	///////////////////////////////////
	
//	public static void main (String[] args) {
//		DatabaseImpl test = new DatabaseImpl();
//		test.loadDatabase();
//		Set<DbItem> cardList = test.getCards(new SearchImpl("Chandra", false, 0, new Filter()));
//		
//		Iterator<DbItem> cardListIterator = cardList.iterator();
//		DbItem firstOutput = cardListIterator.next();
//		System.out.println(firstOutput.getModifiablePrintings().size());
//		DbPrinting printing = firstOutput.getDbPrinting(0);
//		
//		
//    	String setCode = printing.getSetCode().toLowerCase();
//    	String id = printing.getId();
//    	
//    	String url = "https://api.scryfall.com/cards/" + setCode + "/" + id + "?format=image&version=normal";
//    	System.out.println(url);
//	}
	
//	public static void main (String[] args) {
//		DatabaseImpl test = new DatabaseImpl();
//		test.loadDatabase();
//		Set<DbItem> cardList = test.getCards(new SearchImpl("Chandra", false, 0, new Filter()));
//		
//		Iterator<DbItem> cardListIterator = cardList.iterator();
//		while (cardListIterator.hasNext()) {
//			DbItem output = cardListIterator.next();
//			System.out.println(output.getDbPrinting(0).getDate() + "\t\t" + output.getName());
//			
//		}
//		
//	}
}
