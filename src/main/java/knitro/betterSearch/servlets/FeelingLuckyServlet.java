package knitro.betterSearch.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import knitro.betterSearch.database.Database;
import knitro.betterSearch.database.DatabaseImpl;
import knitro.betterSearch.database.card.DbItem;
import knitro.betterSearch.database.card.DbItemImpl;
import knitro.betterSearch.database.card.DbPrinting;
import knitro.betterSearch.database.filter.Filter;
import knitro.betterSearch.database.search.Search;
import knitro.betterSearch.database.search.Style;
import knitro.betterSearch.database.search.impl.SearchImpl;
import knitro.betterSearch.priceGetter.NoPriceFoundException;
import knitro.betterSearch.priceGetter.PriceGetter;
import knitro.betterSearch.priceGetter.scg.StarCityGames;

public class FeelingLuckyServlet extends HttpServlet {

	/*HTTP Servlet Requirements*/
	private static final long serialVersionUID = 4525571131681005745L;
	
	/////////////////////////////////////////
	/*Alert Messages*/
	/////////////////////////////////////////
	
	public static final String GET_MESSAGE_200 = "Request Received";
	
	/////////////////////////////////////////
	/*Enum(s)*/
	/////////////////////////////////////////
	
	/**
	 * The setting that determines whether the servlet will return the oldest version
	 * of the card, or the newest version of the card.
	 * @author Calvin Lee
	 *
	 */
	public enum Setting {
		
		NEWEST(0), OLDEST(1);
		
		private final int value;
		private Setting(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }

	}
	
	/////////////////////////////////////////
	/*Field(s)*/
	/////////////////////////////////////////
	
	public static Setting setting = Setting.NEWEST; //Default Setting is the Newest Version of the Card
	private static final String reactURL = "http://localhost:3000";
	private static final Database DATABASE = new DatabaseImpl();
	private static final Filter filter = new Filter(); //Default Empty Filter
	
	/////////////////////////////////////////
	/*Constructor(s)*/
	/////////////////////////////////////////
	
	public FeelingLuckyServlet() {
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
			displayWebpage(request, response, DbItemImpl.EMPTY);
			return;
		}
		
		/*Perform the Search*/
		//Get the Search Instance
		Search currentSearch = generateSearch(param);
		
		//Get the Cards from the Database
		initialiseDatabase(); //Initialises the database if not already done
		Set<DbItem> cardList = DATABASE.getCards(currentSearch);
		
		//Get the "Feeling Lucky" Search
		Iterator<DbItem> cardListIterator = cardList.iterator();
		DbItem firstOutput = null;
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
	private void displayWebpage(HttpServletRequest request, HttpServletResponse response, DbItem card) throws IOException {
		
		/*Servlet Initialisation*/
		response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        /*HTML Initialisation*/
        out.println("<html>");
        out.println("<head>");
        out.println("<title>BetterSearch: I'm Feeling Lucky</title>");
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
        
        /*Body*/
        out.println("<div class=\"row\">");
        
        //Column 1: Image
        out.println("<div class=\"column\">");
        if ((card == null) || (card.equals(DbItemImpl.EMPTY))) {
        	out.println("<h1> Use the Search Bar above to begin!</h1>");
        } else {
        	String cardName = card.getName();
            out.println("<h1>" + cardName + "</h1>");
        }
        addCardImage(request, out, card);
        out.println("</div");
        
        //Column 2: Details
        out.println("<div class=\"column\">");
        addCardDetails(out, card);
        addCardPrices(out, card);
        out.println("</div");
        
        //Close Row Div
        out.println("</div");
        
        //Close Body and HTML
        out.println("</body>");
        out.println("</html>");
        
        out.close();
	}
	
	/////////////////////////////////////////
	/*HTML Content Additions*/
	/////////////////////////////////////////
	
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
	
	private void addCardDetails(PrintWriter out, DbItem card) {
		
		if (card == null) {
			//Do Nothing?
        } else if (card.equals(DbItemImpl.EMPTY)) {
        	out.println("<h2> No Results Found </h2>");
        } else {
        	
        	/*Card Information*/
        	String cardName = card.getName();
        	String cardType = card.getFullType();
        	DbPrinting printing = card.getDbPrinting(0);
        	String setCode = printing.getSetCode().toLowerCase();
        	
        	//Check Foiling:
        	boolean hasNonFoil = printing.isHasNonFoil();
        	Style currentStyle = (!hasNonFoil) ? Style.FOIL : Style.NON_FOIL;
        	String currentStyle_string = currentStyle.toString().toLowerCase();
    	    //Capitalise First Letter
        	currentStyle_string = currentStyle_string.replaceFirst(currentStyle_string.substring(0, 1), currentStyle_string.substring(0, 1).toUpperCase());
        	
        	/*Print Card Details*/
        	out.println("<h1>" + cardName + "</h1>");
        	out.println("<h2>" + cardType + "</h2>");
        	out.println("<h2>" + "Set Code: " + setCode + "</h2>");
        	out.println("<h2>" + currentStyle_string + "</h2>");

        }
	}

	private void addCardPrices(PrintWriter out, DbItem card) {
		
		if (card == null) {
			//Do Nothing?
        } else if (card.equals(DbItemImpl.EMPTY)) {
        	//Do Nothing?
        } else {
        	
        	/*Card Information:*/
        	String cardName = card.getName();
        	DbPrinting printing = card.getDbPrinting(0);
        	String setCode = printing.getSetCode().toLowerCase();
        	String id = printing.getId();
        	
        	//Check Foiling
        	boolean hasNonFoil = printing.isHasNonFoil();
        	Style currentStyle = (!hasNonFoil) ? Style.FOIL : Style.NON_FOIL;
        	
        	
        	/*Get Card Prices*/
        	//SCG
        	PriceGetter scg = new StarCityGames();
        	try {
        		double scgPrice = scg.getSpecificCardPrice(cardName, setCode, id, currentStyle);
            	out.println("<h4> StarCityGames Price: " + scgPrice +"</h4>");
        	} catch (NoPriceFoundException e) {
        		//This scenario suggests that the url gives out a 404
        		out.println("<h4> StarCityGames Price: No Price Found </h4>");
        	}
        	
        	//CK
        	//TODO::
        	
        	//TCG
        	//TODO::
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
