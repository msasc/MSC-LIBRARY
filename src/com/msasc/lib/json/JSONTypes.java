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

/**
 * JSON types.
 * @author Miquel Sas
 */
public enum JSONTypes {

	/* Standard types. */

	/** Standard JSON object. */
	OBJECT,
	/** Standard JSON array. */
	ARRAY,
	/** Standard JSON string. */
	STRING,
	/** Standard JSON number. */
	NUMBER,
	/** Standard JSON boolean, true or false. */
	BOOLEAN,
	/** Standard JSON null. */
	NULL,

	/* Extended types. */

	/** Extended type binary. */
	BINARY("%binary%"),
	/** Extended type date. */
	DATE("%date%"),
	/** Extended type time. */
	TIME("%time%"),
	/** Extended type timestamp. */
	TIMESTAMP("%timestamp%"),
	/** Extended type decimal. */
	DECIMAL("%decimal%"),
	/** Extended type double. */
	DOUBLE("%double%"),
	/** Extended type integer. */
	INTEGER("%integer%"),
	/** Extended type long. */
	LONG("%long%");

	/** Key to store extended types. */
	private String key = null;

	/**
	 * Constructor.
	 */
	private JSONTypes() {
	}

	/**
	 * Constructor assigning the key for extended types.
	 * 
	 * @param key The key for extended types.
	 */
	private JSONTypes(String key) {
		this.key = key;
	}

	/**
	 * Returns the key for extended types, null otherwise.
	 * 
	 * @return The key.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Check whether this type is an extended type.
	 * 
	 * @return A boolean.
	 */
	public boolean isExtended() {
		return key != null;
	}

	/**
	 * Check whether this type is numeric.
	 * 
	 * @return A boolean.
	 */
	public boolean isNumeric() {
		if (this == NUMBER) return true;
		if (this == DECIMAL) return true;
		if (this == DOUBLE) return true;
		if (this == INTEGER) return true;
		if (this == LONG) return true;
		return false;
	}

	/** List of standard types. */
	public static final JSONTypes[] standardTypes = new JSONTypes[] {
			OBJECT, ARRAY, STRING, NUMBER, BOOLEAN, NULL
	};
	/** List of extended types. */
	public static final JSONTypes[] extendedTypes = new JSONTypes[] {
			BINARY, DATE, TIME, TIMESTAMP, DECIMAL, DOUBLE, INTEGER, LONG
	};
}
