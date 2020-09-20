package knitro.betterSearch.cache;

public class ContentCache implements Cache<String> {

	///////////////////////////////////
	/*Constants*/
	///////////////////////////////////
	
	public static final String DB_LOCATION = "src/main/resources/data/content-cache";
	
	///////////////////////////////////
	/*Fields*/
	///////////////////////////////////
	
	///////////////////////////////////
	/*Constructor*/
	///////////////////////////////////
	
	public ContentCache() {
		/*Empty*/
	}
	
	///////////////////////////////////
	/*Overridden Methods*/
	///////////////////////////////////
	
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

	@Override
	public String getCache(String id) {
		// TODO Auto-generated method stub
		return null;
	}

}
