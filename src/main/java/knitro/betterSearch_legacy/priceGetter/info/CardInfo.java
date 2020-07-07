package knitro.betterSearch_legacy.priceGetter.info;

public abstract class CardInfo {
	
	///////////////////////////////////
	/*Fields*/
	///////////////////////////////////
	
	protected final String cardName;
	protected final String cardURL;
	protected final CardImage cardImage;
	
	///////////////////////////////////
	/*Constructors*/
	///////////////////////////////////
	
	public CardInfo(String cardName, String cardURL, CardImage cardImage) {
		super();
		this.cardName = cardName;
		this.cardURL = cardURL;
		
		/*Check if cardImage is null*/
		if (cardImage == null) {
			this.cardImage = CardImage.NO_IMAGE;
		} else {
			this.cardImage = cardImage;
		}
	}

	///////////////////////////////////
	/*Public Methods*/
	///////////////////////////////////
	
	public String getCardName() {
		return cardName;
	}

	public String getCardURL() {
		return cardURL;
	}

	public CardImage getCardImage() {
		return cardImage;
	}
	
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("Card:\n");
		sb.append("Card Name: " + cardName + "\n");
		sb.append("Card URL: " + cardURL + "\n");
		sb.append("Card Image URL: " + cardImage.getImageURL() + "\n");
		
		return sb.toString();
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cardImage == null) ? 0 : cardImage.hashCode());
		result = prime * result + ((cardName == null) ? 0 : cardName.hashCode());
		result = prime * result + ((cardURL == null) ? 0 : cardURL.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CardInfo other = (CardInfo) obj;
		if (cardImage == null) {
			if (other.cardImage != null)
				return false;
		} else if (!cardImage.equals(other.cardImage))
			return false;
		if (cardName == null) {
			if (other.cardName != null)
				return false;
		} else if (!cardName.equals(other.cardName))
			return false;
		if (cardURL == null) {
			if (other.cardURL != null)
				return false;
		} else if (!cardURL.equals(other.cardURL))
			return false;
		return true;
	}
	
	///////////////////////////////////
	/*Protected Methods*/
	///////////////////////////////////
	
}
