/*
 * Copyright (c) 2021. Miquel Sas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.msasc.lib.json;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A JSON object is an ordered collection of name/value pairs, externally
 * represented as a string formatted following the ECMA-404 specification.
 * 
 * @author Miquel Sas
 */
public class JSONObject {

	/**
	 * Parse the string JSON object.
	 * 
	 * @param obj The string JSON object to parse.
	 * @return The parsed JSON object.
	 */
	public static JSONObject parse(String obj) {
		try {
			StringReader reader = new StringReader(obj);
			return parse(reader);
		} catch (IOException exc) {
			throw new IllegalArgumentException("Invalid JSON object string", exc);
		}
	}

	/**
	 * Parse the reader and return the JSON object.
	 * 
	 * @param reader The reader.
	 * @return The JSON object.
	 * @throws IOException If an error occurs.
	 */
	public static JSONObject parse(Reader reader) throws IOException {
		JSONParser parser = new JSONParser();
		return parser.parse(reader);
	}

	/**
	 * Map ordered by insertion.
	 */
	private Map<String, JSONEntry> map = new LinkedHashMap<>();

	/**
	 * Constructor.
	 */
	public JSONObject() {
	}

	/**
	 * Append the argument JSON object.
	 * 
	 * @param obj The JSON object to append.
	 */
	public void append(JSONObject obj) {
		map.putAll(obj.map);
	}

	/**
	 * Returns the entry or null.
	 * 
	 * @param key The key.
	 * @return The entry.
	 */
	public JSONEntry get(String key) {
		return map.get(key);
	}

	/**
	 * Add the entry to the map.
	 * 
	 * @param key   The key.
	 * @param entry The entry.
	 */
	public void put(String key, JSONEntry entry) {
		map.put(key, entry);
	}

	/**
	 * Add the entry to the map.
	 * 
	 * @param key   The key.
	 * @param type  The type.
	 * @param value The value.
	 */
	public void put(String key, JSONTypes type, Object value) {
		map.put(key, new JSONEntry(type, value));
	}

	/**
	 * Remove and return the entry at the given key.
	 * 
	 * @param key The key.
	 * @return The entry at the given key or null.
	 */
	public JSONEntry remove(String key) {
		return map.remove(key);
	}

	/**
	 * Returns the collection of keys.
	 * 
	 * @return The collection of keys.
	 */
	public Collection<String> keys() {
		return map.keySet();
	}

	/**
	 * Check emptiness.
	 * 
	 * @return A boolean.
	 */
	public boolean isEmtpy() {
		return map.isEmpty();
	}

	/**
	 * Returns the size or number of entries.
	 * 
	 * @return The size.
	 */
	public int size() {
		return map.size();
	}

	/**
	 * Returns a copy of this JSONObject.
	 * 
	 * @return A copy.
	 */
	public JSONObject copy() {
		return JSONObject.parse(toString());
	}

	/**
	 * Check equals. Uses the string representation.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof JSONObject) {
			JSONObject json = (JSONObject) obj;
			return toString().equals(json.toString());
		}
		return false;
	}

	/**
	 * Hash code. Uses the string representation.
	 */
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	/**
	 * Returns the string representation.
	 */
	@Override
	public String toString() {
		return toString(false);
	}

	/**
	 * Returns the string representation.
	 * 
	 * @param readable A boolean indicating that the output should be formatted in a
	 *                 more readable form.
	 * @return A string.
	 */
	public String toString(boolean readable) {
		JSONWriter w = new JSONWriter(readable);
		return w.toString(this);
	}
}
