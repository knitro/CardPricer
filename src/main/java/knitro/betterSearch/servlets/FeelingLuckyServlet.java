package knitro.betterSearch.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import knitro.betterSearch.database.Database;
import knitro.betterSearch.database.DatabaseImpl;
import knitro.betterSearch.database.card.DbItem;
import knitro.betterSearch.database.card.DbPrinting;
import knitro.betterSearch.database.filter.Filter;
import knitro.betterSearch.database.search.Search;
import knitro.betterSearch.database.search.Style;
import knitro.betterSearch.database.search.impl.SearchImpl;
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
		
		/*Get the Parameter*/
		String param = request.getParameter("searchTerm");
		
		//Perform Null Checks
		if (param == null) {
			displayWebpage(request, response, null);
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
		
		Search returnSearch = new SearchImpl(searchTerm, false, 5, filter);
		return returnSearch;
		
	}
	
	/**
	 * Displays the Webpage using the PrintWriter to display the card, and its appropriate qualities.
	 * @param request
	 * @param response
	 * @param card - the DbItem that the webpage should display. If null, then no card is being searched up.
	 * @throws IOException
	 */
	private void displayWebpage(HttpServletRequest request, HttpServletResponse response, DbItem card) throws IOException {
		
		/*Initialisation*/
		response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        /*HTML Generation*/
        out.println("<html>");
        out.println("<head>");
        out.println("<title>BetterSearch: I'm Feeling Lucky</title>");
        out.println("</head>");
        out.println("<body>");
        
        //Code From https://www.w3schools.com/howto/howto_css_search_button.asp
        out.println("<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">");
        out.println("<form class=\"example\" action=\"\">");
        out.println("<input type=\"text\" placeholder=\"Search..\" name=\"searchTerm\">");
        out.println("<button type=\"submit\"><i class=\"fa fa-search\"></i></button>");
        out.println("</form>");
        
        if (card != null) {
        	
        	//Card Information:
        	String cardName = card.getName();
        	String cardType = card.getFullType();
        	DbPrinting printing = card.getDbPrinting(0);
        	String setCode = printing.getSetCode().toLowerCase();
        	String id = printing.getId();
        	
        	//Check Foiling:
        	boolean hasFoil = printing.isHasFoil();
        	boolean hasNonFoil = printing.isHasNonFoil();
        	Style currentStyle = (!hasNonFoil) ? Style.FOIL : Style.NON_FOIL;
        	
        	//Card Details:
        	out.println("<h1>" + cardName + "</h1>");
        	out.println("<h3>" + cardType + "</h3>");
        	
        	//Card Prices:
        	PriceGetter scg = new StarCityGames();
        	double scgPrice = scg.getSpecificCardPrice(cardName, setCode, id, currentStyle);
        	out.println("<h4> StarCityGames Price: " + scgPrice +"</h4>");
        	
        	//Card Image:
        	String url = "https://api.scryfall.com/cards/" + setCode + "/" + id + "?format=image&version=normal";
        	
        	out.println("<img src=\"" + url + "\" alt=\"image here\">");
        	
        	
        }
        
        out.println("</body>");
        out.println("</html>");
        
        out.close();
	}
	
	private void initialiseDatabase() {
		DATABASE.loadDatabase();
	}
}
