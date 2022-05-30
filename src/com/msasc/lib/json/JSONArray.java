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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Miquel Sas
 */
public class JSONArray implements Iterable<JSONEntry> {

	/**
	 * Internal list.
	 */
	private List<JSONEntry> entries = new ArrayList<>();

	/**
	 * Constructor.
	 */
	public JSONArray() {
	}

	/**
	 * Add an entry to the array.
	 * 
	 * @param entry The entry.
	 */
	public void add(JSONEntry entry) {
		entries.add(entry);
	}

	/**
	 * Insert an entry to the array.
	 * 
	 * @param index The insert index.
	 * @param entry The entry.
	 */
	public void add(int index, JSONEntry entry) {
		entries.add(index, entry);
	}

	/**
	 * Add an entry to the array.
	 * 
	 * @param type  The JSON type.
	 * @param value A value according to the type.
	 */
	public void add(JSONTypes type, Object value) {
		entries.add(new JSONEntry(type, value));
	}

	/**
	 * Insert an entry to the array.
	 * 
	 * @param index The insert index.
	 * @param type  The JSON type.
	 * @param value A value according to the type.
	 */
	public void add(int index, JSONTypes type, Object value) {
		entries.add(index, new JSONEntry(type, value));
	}

	/**
	 * Returns the entry at index.
	 * 
	 * @param index The index.
	 * @return The entry.
	 */
	public JSONEntry get(int index) {
		return entries.get(index);
	}

	/**
	 * Remove and return the entry at index.
	 * 
	 * @param index The index.
	 * @return The entry.
	 */
	public JSONEntry remove(int index) {
		return entries.remove(index);
	}

	/**
	 * Set the entry at the given index.
	 * 
	 * @param index The index.
	 * @param entry The entry.
	 */
	public void set(int index, JSONEntry entry) {
		entries.set(index, entry);
	}

	/**
	 * Set the entry at the given index.
	 * 
	 * @param index The index.
	 * @param type  The entry type.
	 * @param value The entry value.
	 */
	public void set(int index, JSONTypes type, Object value) {
		entries.set(index, new JSONEntry(type, value));
	}

	/**
	 * Check emptiness.
	 * 
	 * @return A boolean.
	 */
	public boolean isEmtpy() {
		return entries.isEmpty();
	}

	/**
	 * Returns the size or number of entries.
	 * 
	 * @return The size.
	 */
	public int size() {
		return entries.size();
	}

	/**
	 * Returns a copy of this array.
	 * 
	 * @return A copy.
	 */
	public JSONArray copy() {
		JSONArray arr = new JSONArray();
		for (int i = 0; i < entries.size(); i++) {
			arr.add(entries.get(i).copy());
		}
		return arr;
	}

	/**
	 * Check equals. Uses the string representation.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof JSONArray) {
			JSONArray arr = (JSONArray) obj;
			return toString().equals(arr.toString());
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

	/**
	 * Returns the iterator through the list of entries.
	 */
	@Override
	public Iterator<JSONEntry> iterator() {
		return entries.iterator();
	}
}
