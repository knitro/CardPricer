package knitro.betterSearch.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.emory.mathcs.backport.java.util.Arrays;
import knitro.betterSearch.database.Database;
import knitro.betterSearch.database.DatabaseImpl;
import knitro.betterSearch.database.card.DbItem;
import knitro.betterSearch.database.card.DbItemImpl;
import knitro.betterSearch.database.card.DbPrinting;
import knitro.betterSearch.database.card.DbPrinting.FrameVersion;
import knitro.betterSearch.database.card.DbPrinting.Rarity;
import knitro.betterSearch.database.filter.CardColour;
import knitro.betterSearch.database.filter.Filter;
import knitro.betterSearch.database.search.Search;
import knitro.betterSearch.database.search.Style;
import knitro.betterSearch.database.search.impl.SearchImpl;
import knitro.betterSearch.priceGetter.NoPriceFoundException;
import knitro.betterSearch.priceGetter.PriceGetter;
import knitro.betterSearch.priceGetter.scg.StarCityGames;
import knitro.betterSearch.servlets.FeelingLuckyServlet.Setting;
import knitro.support.InvalidModeException;

public class SetEvServlet extends HttpServlet {
	
	/////////////////////////////////////////
	/*Constants*/
	/////////////////////////////////////////

	private static final long serialVersionUID = 8019080322551443238L;
	private static final List<String> mb1_foils = new ArrayList<String>(List.of(
			"Alchemist's Refuge", "Allosaurus Rider", "Amulet of Vigor", "Archetype of Endurance", "Aurelia's Fury", 
			"Balduvian Rage", "Balefire Liege", "Blasting Station", "Blighted Agent", "Boreal Druid", "Boundless Realms", 
			"Braid of Fire", "Bramblewood Paragon", "Bringer of the Black Dawn", "Burning Inquiry", "Celestial Dawn", 
			"Celestial Kirin", "Changeling Hero", "Chimney Imp", "Codex Shredder", "Conspiracy", "Council Guardian", 
			"Delay", "Drogskol Captain", "Echoing Decay", "Eidolon of Rhetoric", "Fatespinner", "Fiery Gambit", 
			"Flamekin Harbinger", "Form of the Dragon", "Frozen Aether", "Funeral Charm", "Fungusaur", 
			"Game-Trail Changeling", "Geth's Grimoire", "Gilder Bairn", "Gleeful Sabotage", "Glittering Wish", 
			"Goblin Bushwhacker", "Grand Architect", "Greater Mossdog", "Guerrilla Tactics", "Harmonic Sliver", 
			"Helix Pinnacle", "Herald of Leshrac", "Hornet Sting", "Intruder Alarm", "Iron Myr", "Isamaru, Hound of Konda", 
			"Karrthus, Tyrant of Jund", "Knowledge Pool", "Kulrath Knight", "Lantern of Insight", "Lapse of Certainty", 
			"Leveler", "Lich's Mirror", "Lightning Storm", "Lumithread Field", "Maelstrom Nexus", "Magewright's Stone", 
			"Manaweft Sliver", "Maro", "Marrow-Gnawer", "Memnite", "Minamo, School at Water's Edge", "Mind Funeral", 
			"Mindslaver", "Mirrodin's Core", "Misthollow Griffin", "Myojin of Life's Web", "Nezumi Shortfang", "Noggle Bandit", 
			"Norin the Wary", "Norn's Annex", "Not of This World", "Ogre Gatecrasher", "One with Nothing", "Panglacial Wurm",
			"Paradox Haze", "Patron of the Moon", "Pili-Pala", "Proclamation of Rebirth", "Puca's Mischief", "Pull from Eternity", 
			"Pyretic Ritual", "Ravenous Trap", "Reaper King", "Reki, the History of Kamigawa", "Rescue from the Underworld", 
			"Rhox", "Rune-Tail, Kitsune Ascendant", "Sakura-Tribe Scout", "Sarkhan the Mad", "Scourge of the Throne", 
			"Scryb Ranger", "Sen Triplets", "Sheltering Ancient", "Shizo, Death's Storehouse", "Sinew Sliver", 
			"Sosuke, Son of Seshiro", "Soul's Attendant", "Spelltithe Enforcer", "Spellweaver Volute", "Spike Feeder", 
			"Springjack Shepherd", "Stalking Stones", "Stigma Lasher", "Storm Crow", "Sundial of the Infinite", 
			"Teferi's Puzzle Box", "Trailblazer's Boots", "Treasonous Ogre", "Triskelion", "Undead Warchief", 
			"Viscera Seer", "Wall of Shards", "Wear // Tear", "White Knight", "Witchbane Orb", "Yore-Tiller Nephilim", 
			"Zur's Weirding"
	));
	
	
	
	/////////////////////////////////////////
	/*Field(s)*/
	/////////////////////////////////////////
	
	public static Setting setting = Setting.NEWEST; //Default Setting is the Newest Version of the Card
	private static final String reactURL = "http://localhost:3000";
	private static final Database DATABASE = new DatabaseImpl();
	private static final Filter filter = new Filter(); //Default Empty Filter
	private static PriceGetter priceGetter = new StarCityGames(); //Default Setting = SCG Pricing
	
	/////////////////////////////////////////
	/*Constructor(s)*/
	/////////////////////////////////////////
	
	public SetEvServlet() {
		/*Should Stay Empty*/
	}
	
	/////////////////////////////////////////
	/*Overridden Methods*/
	/////////////////////////////////////////
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.addHeader("Access-Control-Allow-Origin", reactURL);
		processForm(request, response);
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.addHeader("Access-Control-Allow-Origin", reactURL);
		processForm(request, response);
	}
	
	/////////////////////////////////////////
	/*Private Methods*/
	/////////////////////////////////////////
	/**
	 * Processes the get and post requests.
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	private void processForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		if (request.getParameterMap().keySet().size() == 0) {
			displayWebpage(request, response, null);
			return;
		}
		
		/*Get the Parameter*/
		String param = request.getParameter("searchTerm");
		
		//Perform Null Checks
		if (param == null) {
			displayWebpage(request, response, null);
			return;
		}
		
		/*Perform the Search*/
		
		initialiseDatabase(); //Initialises the database if not already done
		Set<String> setList = DATABASE.getMatchingSets(param); //Get the List of Matching Sets
		
		//Get the Closest Match
		Iterator<String> cardListIterator = setList.iterator();
		String firstOutput = null;
		if (cardListIterator.hasNext()) { //This accounts for empty responses
			//TODO:: This should eventually always be called. Test for this!!
			firstOutput = cardListIterator.next();
		}
		
		/*Display the Output*/
		displayWebpage(request, response, firstOutput);
	
	}
	
	/**
	 * Generates a "I'm Feeling Lucky" Search instance.
	 * This method assumes the following:
	 * <ul>
	 * 	<li>The search is for the "Selling Price".
	 * 	<li>The search expects the MoE to be exact (0).
	 * 	<li>The filter is whatever this servlet has as its field.
	 * </ul>
	 * @param searchTerm
	 * @return
	 */
	private Search generateSearch(String searchTerm) {
		
		Search returnSearch = new SearchImpl(searchTerm, false, 0, filter);
		return returnSearch;
		
	}

	private void initialiseDatabase() {
		DATABASE.loadDatabase();
	}
	
	/////////////////////////////////////////
	/*Main Webpage Methods*/
	/////////////////////////////////////////
	
	/**
	 * Displays the Webpage using the PrintWriter to display the card, and its appropriate qualities.
	 * @param request
	 * @param response
	 * @param card - the DbItem that the webpage should display. If null, then no card is being searched up.
	 * @throws IOException
	 */
	private void displayWebpage(HttpServletRequest request, HttpServletResponse response, String setUUID) throws IOException {
		
		/*Servlet Initialisation*/
		response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        /*HTML Initialisation*/
        out.println("<html>");
        out.println("<head>");
        out.println("<title>BetterSearch: Set EV Calculator</title>");
        addSearchBarCSS(out);
        addColumnCSS(out);
        out.println("</head>");
        out.println("<body>");
        
        /*Search Bar*/
        //Code From https://www.w3schools.com/howto/howto_css_search_button.asp
        out.println("<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">");
        out.println("<form class=\"example\" action=\"\">");
        out.println("<input type=\"text\" placeholder=\"Search..\" name=\"searchTerm\">");
        out.println("<button type=\"submit\"><i class=\"fa fa-search\"></i></button>");
        out.println("</form>");
        
        /*Summary Data*/
        addSummaryData(out, setUUID);
        
        
        /*Add Notable Card Images*/
        addNotableCardImages(out, setUUID);
        
        /*Graph*/
        addGraph(out, setUUID);
        
        
        //Close Body and HTML
        out.println("</body>");
        out.println("</html>");
        
        out.close();
	}

	/////////////////////////////////////////
	/*HTML Content Additions*/
	/////////////////////////////////////////
	
	private void addSummaryData(PrintWriter out, String setUUID) {
		
		/*Set Initialisations*/
		Set<DbItem> setCards = DATABASE.getCardsMatchingSet(setUUID);
		
		if (setUUID.equals("MB1")) {
			
			//Card Slot Organisation Initialisation
			Set<DbItem> whiteSlot = new HashSet<>();
			Set<DbItem> blueSlot = new HashSet<>();
			Set<DbItem> blackSlot = new HashSet<>();
			Set<DbItem> redSlot = new HashSet<>();
			Set<DbItem> greenSlot = new HashSet<>();
			Set<DbItem> multicolourSlot = new HashSet<>();
			Set<DbItem> artifactLandSlot = new HashSet<>();
			Set<DbItem> preM15Slot = new HashSet<>();
			Set<DbItem> postM15Slot = new HashSet<>();
			Set<DbItem> foilSlot = new HashSet<>();
			
			double whiteTotal = 0, blueTotal = 0, blackTotal = 0, redTotal = 0, greenTotal = 0, multicolourTotal = 0, 
					artifactLandTotal = 0, preM15Total = 0, postM15Total = 0, foilTotal = 0;
			
//			Set<Set<DbItem>> setOfSets = new HashSet<>();
//			setOfSets.add(whiteSlot);
//			setOfSets.add(blueSlot);
//			setOfSets.add(blackSlot);
//			setOfSets.add(redSlot);
//			setOfSets.add(greenSlot);
//			setOfSets.add(multicolourSlot);
//			setOfSets.add(artifactLandSlot);
//			setOfSets.add(preM15Slot);
//			setOfSets.add(postM15Slot);
//			setOfSets.add(foilSlot);
			
			//Card Slot Organisation
			cardOrganisation: for (DbItem currentDbItem : setCards) {
				
				//Get Important Values
				String cardName = currentDbItem.getName();
				DbPrinting currentPrinting = currentDbItem.getDbPrinting("MB1");
				FrameVersion frameVersion = currentPrinting.getFrameVersion();
				Rarity rarity = currentPrinting.getRarity();
				double currentPrice = getCardPrice(currentDbItem);
				
				//Check Foil Card
				if (mb1_foils.contains(cardName)) {
					foilSlot.add(currentDbItem);
					foilTotal += currentPrice;
					continue cardOrganisation;
				}
				
				//Check PreM15
				if ((frameVersion == FrameVersion.FRAME_1993) || (frameVersion == FrameVersion.FRAME_1997) 
						|| (frameVersion == FrameVersion.FRAME_2003) || (frameVersion == FrameVersion.FRAME_future)) {
					preM15Slot.add(currentDbItem);
					preM15Total += currentPrice;
					continue cardOrganisation;
				} 
				
				//Check Rarity
				if ((rarity == Rarity.RARE) || (rarity == Rarity.MYTHIC)) {
					postM15Slot.add(currentDbItem);
					postM15Total += currentPrice;
					continue cardOrganisation;
				} 
				
				//Must be a "Normal Card
				Set<CardColour> colours = currentDbItem.getColours();
				if (colours.size() > 1) { //Multicoloured
					multicolourSlot.add(currentDbItem);
					multicolourTotal += currentPrice;
					continue cardOrganisation;
				} else if (colours.contains(CardColour.WHITE)) {
					whiteSlot.add(currentDbItem);
					whiteTotal += currentPrice;
					continue cardOrganisation;
				} else if (colours.contains(CardColour.BLUE)) {
					blueSlot.add(currentDbItem);
					blueTotal += currentPrice;
					continue cardOrganisation;
				} else if (colours.contains(CardColour.BLACK)) {
					blackSlot.add(currentDbItem);
					blackTotal += currentPrice;
					continue cardOrganisation;
				} else if (colours.contains(CardColour.RED)) {
					redSlot.add(currentDbItem);
					redTotal += currentPrice;
					continue cardOrganisation;
				} else if (colours.contains(CardColour.GREEN)) {
					greenSlot.add(currentDbItem);
					greenTotal += currentPrice;
					continue cardOrganisation;
				} else if (colours.contains(CardColour.COLOURLESS)) {
					artifactLandSlot.add(currentDbItem);
					artifactLandTotal += currentPrice;
					continue cardOrganisation;
				} else {
					throw new InvalidModeException("Colour is not known");
				}
			}
			
			double singlePackEV = 0;
			singlePackEV += (whiteTotal / whiteSlot.size());
			singlePackEV += (blueTotal / blueSlot.size());
			singlePackEV += (blackTotal / blackSlot.size());
			singlePackEV += (redTotal / redSlot.size());
			singlePackEV += (greenTotal / greenSlot.size());
			singlePackEV += (artifactLandTotal / artifactLandSlot.size());
			singlePackEV += (preM15Total / preM15Slot.size());
			singlePackEV += (postM15Total / postM15Slot.size());
			singlePackEV += (foilTotal / foilSlot.size());
			
			double setEV = singlePackEV * 24;
			
			out.println("<h2> Set EV: $" + setEV + "</h2>");
			out.println("<h2> Single Pack EV: $" + singlePackEV + "</h2>");
			
		} else {
			/*TODO Later*/
			return;
		}
		
	}
	
	private void addNotableCardImages(PrintWriter out, String setUUID) {
		// TODO Auto-generated method stub
		
	}
	
	private void addGraph(PrintWriter out, String setUUID) {
		// TODO Auto-generated method stub
	}
	
	private void addCardImage(HttpServletRequest request, PrintWriter out, DbItem card) {
	
		if ((card == null) || (card.equals(DbItemImpl.EMPTY))) {
			String defaultImageLocation = request.getContextPath() + "/image/blank_card.png";
			System.out.println(defaultImageLocation);
//			String defaultImageLocation = "/image/blank_card.png";
			out.println("<img src=\"" + defaultImageLocation + "\" alt=\"image here\"/>");
		} else {
			
			//Get Relevant Card Details
			DbPrinting printing = card.getDbPrinting(0);
	    	String setCode = printing.getSetCode().toLowerCase();
	    	String id = printing.getId();
			
			//Add Card Image
	    	String url = "https://api.scryfall.com/cards/" + setCode + "/" + id + "?format=image&version=normal";
	    	out.println("<img src=\"" + url + "\" alt=\"image here\">");
		}
	}

	private void addCardPrices(PrintWriter out, DbItem card) {
		
		if (card == null) {
			//Do Nothing?
        } else if (card.equals(DbItemImpl.EMPTY)) {
        	//Do Nothing?
        } else {
        	
        	double cardPrice = getCardPrice(card);
        	if (cardPrice == 0) { //This assumes that a 404 error has occured, and/or price could not be found
        		out.println("No Price Found");
        	} else {
        		out.println("$" + cardPrice);
        	}
        }
	}
	
	
	private double getCardPrice(DbItem card) {
		
		/*Card Information:*/
    	String cardName = card.getName();
    	DbPrinting printing = card.getDbPrinting(0);
    	String setCode = printing.getSetCode().toLowerCase();
    	String id = printing.getId();
    	
    	//Check Foiling
    	boolean hasNonFoil = printing.isHasNonFoil();
    	Style currentStyle = (!hasNonFoil) ? Style.FOIL : Style.NON_FOIL;
    	
    	
    	/*Get Card Prices*/
    	try {
    		return priceGetter.getSpecificCardPrice(cardName, setCode, id, currentStyle);
    	} catch (NoPriceFoundException e) {
    		return 0;
    	}
	}
	
	/////////////////////////////////////////
	/*CSS Methods*/
	/////////////////////////////////////////
	
	private void addSearchBarCSS(PrintWriter out) {
		
		String searchBarCSS = "* {\r\n" + 
				"  box-sizing: border-box;\r\n" + 
				"}\r\n" + 
				"\r\n" + 
				"/* Style the search field */\r\n" + 
				"form.example input[type=text] {\r\n" + 
				"  padding: 10px;\r\n" + 
				"  font-size: 17px;\r\n" + 
				"  border: 1px solid grey;\r\n" + 
				"  float: left;\r\n" + 
				"  width: 80%;\r\n" + 
				"  background: #f1f1f1;\r\n" + 
				"}\r\n" + 
				"\r\n" + 
				"/* Style the submit button */\r\n" + 
				"form.example button {\r\n" + 
				"  float: left;\r\n" + 
				"  width: 20%;\r\n" + 
				"  padding: 10px;\r\n" + 
				"  background: #2196F3;\r\n" + 
				"  color: white;\r\n" + 
				"  font-size: 17px;\r\n" + 
				"  border: 1px solid grey;\r\n" + 
				"  border-left: none; /* Prevent double borders */\r\n" + 
				"  cursor: pointer;\r\n" + 
				"}\r\n" + 
				"\r\n" + 
				"form.example button:hover {\r\n" + 
				"  background: #0b7dda;\r\n" + 
				"}\r\n" + 
				"\r\n" + 
				"/* Clear floats */\r\n" + 
				"form.example::after {\r\n" + 
				"  content: \"\";\r\n" + 
				"  clear: both;\r\n" + 
				"  display: table;\r\n" + 
				"}";
		
		out.println("<style>");
		out.println(searchBarCSS);
		out.println("</style>");
	}
	
	private void addColumnCSS(PrintWriter out) {
		
		String columnCSS = ".column {\r\n" + 
				"  float: left;\r\n" + 
				"  width: 50%;\r\n" + 
				"  padding: 10px;\r\n" + 
				"}";
		String rowCSS = ".row:after {\r\n" + 
				"  content: \"\";\r\n" + 
				"  display: table;\r\n" + 
				"  clear: both;\r\n" + 
				"}";
		
		out.println("<style>");
		out.println(columnCSS);
		out.println(rowCSS);
		out.println("</style>");
		
	}
}
