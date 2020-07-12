package knitro.betterSearch.cache;

public interface Cache<T> {

	/**
	 * Stores the cached item with the given ID.
	 * @param id - the ID of the "to be cached" item
	 * @param itemToCache - the Item to be cached.
	 * @param doReplace - If true, it will replace a previously existing item with the same ID if it exists. If set to false, it will not replace.
	 * @return true if an item is successfully cached, otherwise false.
	 */
	public abstract boolean downloadCache(String id, T itemToCache, boolean doReplace);
	
	/**
	 * Checks the cache to see if there is an item in the cache that exists with the specified ID.
	 * @param id - the ID of the cached item
	 * @return true if the cache exists, otherwise false.
	 */
	public abstract boolean checkCache(String id);
	
	/**
	 * Gets the cache with the specified ID.
	 * This method will call {@link #checkCache(String)} first, and if it returns false, this method will return null.
	 * @param id - the ID of the cached item
	 * @return the cached item if the cached item exists, otherwise false.
	 */
	public abstract T getCache(String id);
}
