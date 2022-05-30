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

import com.msasc.lib.json.JSONTypes;

/**
 * Data types supported.
 * 
 * @author Miquel Sas
 */
public enum Types {

	/**
	 * Boolean, java type is <i>Boolean</i>. Supported by a VARCHAR of length 1,
	 * with T/F values.
	 */
	BOOLEAN,

	/**
	 * Decimal, java type is <i>BigDecimal</i>.
	 */
	DECIMAL,
	/**
	 * Double, java type is <i>Double</i>.
	 */
	DOUBLE,
	/**
	 * Integer, java type is <i>Integer</i>.
	 */
	INTEGER,
	/**
	 * Long, java type is <i>Long</i>.
	 */
	LONG,

	/**
	 * Date, java type is <i>LocalDate</i>. Normalized format is <i>YYYY-MM-DD</i>.
	 */
	DATE,
	/**
	 * Time, java type is <i>LocalTime</i>. Normalized format is
	 * <i>hh:mm:ss.nnnnnnnnn</i>.
	 */
	TIME,
	/**
	 * Date-time, java type is <i>LocalDateTime</i>. Normalized format is
	 * <i>YYYY-MM-DDThh:mm:ss.nnnnnnnnn</i>.
	 */
	TIMESTAMP,

	/**
	 * String, java type is <i>String</i>. With a maximum length defined or not,
	 * whether it will be backed in the database by a simple <i>VARCHAR</i> or a
	 * <i>CLOB</i> will depend on the database, the supported maximum length for
	 * varying chars and the required length of the field.
	 */
	STRING,

	/**
	 * Binary, java type is <i>byte[]</i>.
	 */
	BINARY,

	/**
	 * Array type, java type is <i>Value[]</i>. Supported by a JSON object with an
	 * array value.
	 */
	ARRAY,
	/**
	 * JSON object, java type is <i>JSONObject</i>. Modern relational databases use
	 * to support JSON objects.
	 */
	JSONOBJECT;

	/**
	 * Return a boolean indicating whether the type is BOOLEAN.
	 * 
	 * @return A boolean.
	 */
	public boolean isBoolean() { return this == Types.BOOLEAN; }
	/**
	 * Return a boolean indicating whether the type is DECIMAL.
	 * 
	 * @return A boolean.
	 */
	public boolean isDecimal() { return this == Types.DECIMAL; }
	/**
	 * Return a boolean indicating whether the type is DOUBLE.
	 * 
	 * @return A boolean.
	 */
	public boolean isDouble() { return this == Types.DOUBLE; }
	/**
	 * Return a boolean indicating whether the type is INTEGER.
	 * 
	 * @return A boolean.
	 */
	public boolean isInteger() { return this == Types.INTEGER; }
	/**
	 * Return a boolean indicating whether the type is LONG.
	 * 
	 * @return A boolean.
	 */
	public boolean isLong() { return this == Types.LONG; }
	/**
	 * Return a boolean indicating whether the type is a number Types.
	 * 
	 * @return A boolean.
	 */
	public boolean isNumber() { return isDecimal() || isDouble() || isInteger() || isLong(); }

	/**
	 * Return a boolean indicating whether the type is DATE.
	 * 
	 * @return A boolean.
	 */
	public boolean isDate() { return this == Types.DATE; }
	/**
	 * Return a boolean indicating whether the type is TIME.
	 * 
	 * @return A boolean.
	 */
	public boolean isTime() { return this == Types.TIME; }
	/**
	 * Return a boolean indicating whether the type is TIMESTAMP.
	 * 
	 * @return A boolean.
	 */
	public boolean isTimestamp() { return this == Types.TIMESTAMP; }

	/**
	 * Return a boolean indicating whether the type is STRING.
	 * 
	 * @return A boolean.
	 */
	public boolean isString() { return this == Types.STRING; }

	/**
	 * Return a boolean indicating whether the type is BINARY.
	 * 
	 * @return A boolean.
	 */
	public boolean isBinary() { return this == Types.BINARY; }

	/**
	 * Return a boolean indicating whether the type an ARRAY.
	 * 
	 * @return A boolean.
	 */
	public boolean isArray() { return this == Types.ARRAY; }
	/**
	 * Return a boolean indicating whether the type is JSONOBJECT.
	 * 
	 * @return A boolean.
	 */
	public boolean isJSONObject() { return this == Types.JSONOBJECT; }

	/**
	 * Check whether the argument type is comparable to this type.
	 * 
	 * @param  type The type to check.
	 * @return      A boolean.
	 */
	public boolean isComparable(Types type) {
		if (type == null) throw new NullPointerException("Type can not be null");
		if (isNumber() && type.isNumber()) return true;
		if (this == type) return true;
		return false;
	}

	/**
	 * Returns the corresponding JSON type.
	 * 
	 * @return The JSON type.
	 */
	public JSONTypes toJSONTypes() {
		if (this == BOOLEAN) return JSONTypes.BOOLEAN;
		if (this == DECIMAL) return JSONTypes.DECIMAL;
		if (this == DOUBLE) return JSONTypes.DOUBLE;
		if (this == INTEGER) return JSONTypes.INTEGER;
		if (this == LONG) return JSONTypes.LONG;
		if (this == DATE) return JSONTypes.DATE;
		if (this == TIME) return JSONTypes.TIME;
		if (this == TIMESTAMP) return JSONTypes.TIMESTAMP;
		if (this == STRING) return JSONTypes.STRING;
		if (this == BINARY) return JSONTypes.BINARY;
		if (this == ARRAY) return JSONTypes.ARRAY;
		if (this == JSONOBJECT) return JSONTypes.OBJECT;
		throw new IllegalStateException();
	}
}
