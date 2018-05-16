package com.holddie.springboot.freemarker.framework.cache;


/**
 * 缓存接口
 * @author kingapex
 *
 */
public interface ICache<T> {
	
	/**
	 * Get an item from the cache, nontransactionally
	 * @param key
	 * @return the cached object or <tt>null</tt>
	 * @throws CacheException
	 */
	public T get(Object key);
	/**
	 * Add an item to the cache, nontransactionally, with
	 * failfast semantics
	 * @param key
	 * @param value
	 * @throws CacheException
	 */
	public void put(Object key, T value);
	
	/**
	 * 往缓存中写入内容
	 * @param key
	 * @param value
	 * @param exp	超时时间，单位为秒
	 */
	public void put(Object key, T value, int exp);
	
	/**
	 * Remove an item from the cache
	 */
	public void remove(Object key);
	/**
	 * Clear the cache
	 */
	public void clear();
}
