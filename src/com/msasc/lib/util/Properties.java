/*
 * Copyright (C) 2018 Miquel Sas
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the
 * GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the
 * License, or (at
 * your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without
 * even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If
 * not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.msasc.lib.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A useful and quite generic properties table with typed access for commonly used objects.
 * @author Miquel Sas
 */
public class Properties {

	/** The properties map. */
	private final Map<Object, Object> properties = new HashMap<>();

	/**
	 * Constructor.
	 */
	public Properties() {}

	/**
	 * Clear this properties.
	 */
	public void clear() { properties.clear(); }

	/**
	 * Returns a stored Boolean value.
	 * @param key The key.
	 * @return The stored boolean value.
	 */
	public Boolean getBoolean(Object key) { return getBoolean(key, null); }
	/**
	 * Returns a stored Boolean value.
	 * @param key The key.
	 * @param defaultValue Default value
	 * @return The stored boolean value.
	 */
	public Boolean getBoolean(Object key, Boolean defaultValue) {
		Boolean value = (Boolean) properties.get(key);
		return (value == null ? defaultValue : value);
	}
	/**
	 * Returns a stored Double value.
	 * @param key The key.
	 * @return The stored double value.
	 */
	public Double getDouble(Object key) { return (Double) properties.get(key); }
	/**
	 * Return a stored double vector.
	 * @param key The key.
	 * @return The double vector.
	 */
	public double[] getDouble1A(Object key) { return (double[]) properties.get(key); }
	/**
	 * Return a stored double 2d matrix.
	 * @param key The key.
	 * @return The double 2d matrix.
	 */
	public double[][] getDouble2A(Object key) { return (double[][]) properties.get(key); }
	/**
	 * Returns a stored Integer value.
	 * @param key The key.
	 * @return The stored integer value.
	 */
	public Integer getInteger(Object key) { return (Integer) properties.get(key); }
	/**
	 * Returns a stored Long value.
	 * @param key The key.
	 * @return The stored long value.
	 */
	public Long getLong(Object key) { return (Long) properties.get(key); }
	/**
	 * Returns a stored object.
	 * @param key The key.
	 * @return The stored object.
	 */
	public Object getObject(Object key) { return (Object) properties.get(key); }
	/**
	 * Returns a stored string value, returning <code>null</code> if not set.
	 * @param key The key.
	 * @return The stored string value.
	 */
	public String getString(Object key) { return (String) properties.get(key); }
	
	/**
	 * Return a stored typed list.	
	 * @param <T> The type of the elements of the list.
	 * @param key The key.
	 * @return The typed list.
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getList(Object key) { return (List<T>) properties.get(key); }
	/**
	 * Return a stored typed map.	
	 * @param <K, V> The type of the keys and values of the map.
	 * @param key The key.
	 * @return The typed map.
	 */
	@SuppressWarnings("unchecked")
	public <K, V> Map<K, V> getMap(Object key) { return (Map<K, V>) properties.get(key); }
	
	/**
	 * Return the set of keys.
	 * @return The set of keys.
	 */
	public Set<Object> keySet() { return properties.keySet(); }
	
	/**
	 * Put a value.
	 * @param key The key.
	 * @param value The value.
	 */
	public void put(Object key, Object value) { properties.put(key, value); }
	/**
	 * Put all properties.
	 * @param properties The properties to use to fill.
	 */
	public void putAll(Properties properties) {
		this.properties.putAll(properties.properties);
	}
	
	/**
	 * Remove the property at key.
	 * @param key The key.
	 * @return The removed property or null.
	 */
	public Object remove(Object key) { return properties.remove(key); }
	/**
	 * Return the collection of values.
	 * @return The values.
	 */
	public Collection<Object> values() { return properties.values(); }

}
