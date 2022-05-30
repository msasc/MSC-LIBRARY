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
package com.msasc.lib.db;

import java.util.ArrayList;
import java.util.List;

import com.msasc.lib.json.JSONArray;
import com.msasc.lib.json.JSONEntry;
import com.msasc.lib.json.JSONObject;
import com.msasc.lib.json.JSONTypes;

/**
 * An order definition.
 * 
 * @author Miquel Sas
 */
public class Order {

	/** An order segment. */
	public static class Segment {

		/** The field. */
		private Field field;
		/** The ascending flag. */
		private boolean asc = true;

		/**
		 * Constructor.
		 * @param field The field.
		 * @param asc   Ascending boolean flag.
		 */
		public Segment(Field field, boolean asc) {
			this.field = field;
			this.asc = asc;
		}
		/**
		 * Get the field.
		 * @return The field.
		 */
		public Field getField() { return field; }
		/**
		 * Check the ascending flag.
		 * @return A boolean that indicates whether the key is ascending or descending.
		 */
		public boolean isAscending() { return asc; }
		/**
		 * Check equals.
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Segment) {
				Segment seg = (Segment) obj;
				return field.equals(seg.field) && asc == seg.asc;
			}
			return false;
		}
		/**
		 * Hash code.
		 */
		@Override
		public int hashCode() { return (field.hashCode() + Boolean.valueOf(asc).hashCode()); }

		/**
		 * Returns a proper JSON entry.
		 * @return A JSON entry.
		 */
		public JSONEntry toJSONEntry() {
			JSONObject obj = new JSONObject();
			obj.put("field", JSONTypes.STRING, field.getName());
			obj.put("asc", JSONTypes.BOOLEAN, asc);
			return new JSONEntry(JSONTypes.OBJECT, obj);
		}
	}

	/** List of segments or field keys. */
	private List<Segment> segments = new ArrayList<>();

	/**
	 * Constructor.
	 */
	public Order() {}

	/**
	 * Add an ascending field to the list of field keys.
	 * @param field The field to add.
	 */
	public void addField(Field field) { addField(field, true); }
	/**
	 * Add a field to the list of field keys.
	 * @param field     The field to add.
	 * @param ascending The ascending indicator.
	 */
	public void addField(Field field, boolean ascending) { segments.add(new Segment(field, ascending)); }
	/**
	 * Return the list of segments.
	 * @return The segments.
	 */
	public List<Segment> getSegments() { return segments; }

	/**
	 * Check equals.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Order) {
			Order o = (Order) obj;
			return segments.equals(o.segments);
		}
		return false;
	}
	/**
	 * Hash code.
	 */
	@Override
	public int hashCode() { return segments.hashCode(); }
	
	/**
	 * Returns a string representation.
	 */
	@Override
	public String toString() { return toJSONObject().toString(); }
	/**
	 * Returns a string representation.
	 * @param readable A boolean.
	 * @return A readable JSON string representation.
	 */
	public String toString(boolean readable) { return toJSONObject().toString(readable); }

	/**
	 * Returns a JSON representation of this order.
	 * @return A JSON representation of this order.
	 */
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		JSONArray segs = new JSONArray();
		segments.forEach(seg -> segs.add(seg.toJSONEntry()));
		obj.put("segments", JSONTypes.ARRAY, segs);
		return obj;
	}
}
