package knitro.betterSearch_legacy.priceGetter.info;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

public class CardImage {
	
	///////////////////////////////////
	/*Fields*/
	///////////////////////////////////
	
	public static final CardImage NO_IMAGE = new CardImage();
	
	public final boolean isInitialised;
	private final Image image;
	private final String imageURL;
	
	///////////////////////////////////
	/*Constructors*/
	///////////////////////////////////
	
	/**
	 * Creates a CardImage that contains an image.
	 * @param image - the image being stored.
	 */
	/*
	public CardImage(Image image) {
		
	}
	*/
	
	/**
	 * Initialises an "Empty" CardImage
	 */
	public CardImage() {
		this.image = null;
		this.isInitialised = false;
		this.imageURL = null;
	}

	public CardImage(String cardImageURL) {
		isInitialised = true;
		URL url = null;
		try {
			url = new URL(cardImageURL);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.imageURL = url.toString();
		BufferedImage c = null;
		try {
			c = ImageIO.read(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.image = c;
	}
	
	///////////////////////////////////
	/*Overridden Methods*/
	///////////////////////////////////
	
	///////////////////////////////////
	/*Public Methods*/
	///////////////////////////////////
	
	public Image getImage() {
		return image;
	}
	
	public String getImageURL() {
		if (imageURL == null) {
			return "No Image URL Available";
		} else {
			return imageURL;
		}
	}
	
	///////////////////////////////////
	/*Private Methods*/
	///////////////////////////////////
	
}
