package knitro.betterSearch.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FeelingLuckyServlet extends HttpServlet {

	/*HTTP Servlet Requirements*/
	private static final long serialVersionUID = 4525571131681005745L;
	
	/////////////////////////////////////////
	/*Alert Messages*/
	/////////////////////////////////////////
	
	public static final String GET_MESSAGE_200 = "Request Received";
	
	/////////////////////////////////////////
	/*Enums(s)*/
	/////////////////////////////////////////
	
	/////////////////////////////////////////
	/*Constructor(s)*/
	/////////////////////////////////////////
	
	/////////////////////////////////////////
	/*Overridden Methods*/
	/////////////////////////////////////////
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		/*Initialisation*/
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();

		
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		/*Initialisation*/
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();

	}
}
