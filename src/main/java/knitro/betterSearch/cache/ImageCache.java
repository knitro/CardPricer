package knitro.betterSearch.cache;

public class ImageCache implements Cache<String> {

	///////////////////////////////////
	/*Constants*/
	///////////////////////////////////
	
	public static final String DB_LOCATION = "src/main/resources/data/image-cache";
	
	///////////////////////////////////
	/*Fields*/
	///////////////////////////////////
	
	///////////////////////////////////
	/*Constructor*/
	///////////////////////////////////
	
	public ImageCache() {
		/*Empty*/
	}
	
	///////////////////////////////////
	/*Overridden Methods*/
	///////////////////////////////////
	
	@Override
	public String getCache(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean downloadCache(String id, String itemToCache, boolean doReplace) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkCache(String id) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
