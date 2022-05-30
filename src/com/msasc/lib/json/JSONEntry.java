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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * An entry of a JSON document or list. Packs the value and the type.
 * 
 * @author Miquel Sas
 */
public class JSONEntry {

	/**
	 * The JSON type, either standard or extended.
	 */
	private JSONTypes type;
	/**
	 * The value handled by this entry.
	 */
	private Object value;

	/**
	 * Constructor.
	 * 
	 * @param type  The JSON type.
	 * @param value The value.
	 */
	public JSONEntry(JSONTypes type, Object value) {
		setValue(type, value);
	}

	/**
	 * Returns a copy of the entry. Modifying the copy does not modify the entry.
	 * 
	 * @return A copy of this entry.
	 */
	public JSONEntry copy() {
		Object value = null;
		if (type == JSONTypes.OBJECT) value = getObject().copy();
		else if (type == JSONTypes.ARRAY) value = getArray().copy();
		else value = this.value;
		return new JSONEntry(type, value);
	}

	/**
	 * Returns the standard object value if it is so, otherwise throws an
	 * <i>IllegalStateException</i>.
	 * 
	 * @return The standard object value.
	 */
	public JSONObject getObject() {
		if (type != JSONTypes.OBJECT) {
			throw new IllegalStateException("Entry value is not a JSONObject");
		}
		return (JSONObject) value;
	}

	/**
	 * Returns the standard array value if it is so, otherwise throws an
	 * <i>IllegalStateException</i>.
	 * 
	 * @return The standard array value.
	 */
	public JSONArray getArray() {
		if (type != JSONTypes.ARRAY) {
			throw new IllegalStateException("Entry value is not a JSONArray");
		}
		return (JSONArray) value;
	}

	/**
	 * Returns the standard value as a string, no matter what type it is.
	 * 
	 * @return The standard string value.
	 */
	public String getString() {
		if (value == null) return null;
		return value.toString();
	}

	/**
	 * Returns the standard number if it is numeric, otherwise throws an
	 * <i>IllegalStateException</i>.
	 * 
	 * @return The standard number value.
	 */
	public BigDecimal getNumber() {
		if (type.isNumeric() && value == null) return null;
		if (type == JSONTypes.NUMBER) return (BigDecimal) value;
		if (type == JSONTypes.DECIMAL) return (BigDecimal) value;
		if (type == JSONTypes.DOUBLE) return BigDecimal.valueOf((Double) value);
		if (type == JSONTypes.INTEGER) return BigDecimal.valueOf((Integer) value);
		if (type == JSONTypes.LONG) return BigDecimal.valueOf((Long) value);
		throw new IllegalStateException("Entry value is not numeric");
	}

	/**
	 * Returns the standard boolean if it is so, otherwise throws an
	 * <i>IllegalStateException</i>.
	 * 
	 * @return The standard boolean value.
	 */
	public Boolean getBoolean() {
		if (type == JSONTypes.BOOLEAN && value != null) return (Boolean) value;
		throw new IllegalStateException("Entry value is not a boolean");
	}

	/**
	 * Returns the extended binary if it is so, otherwise throws an
	 * <i>IllegalStateException</i>.
	 * 
	 * @return The extended binary value.
	 */
	public byte[] getBinary() {
		if (type == JSONTypes.BINARY) return (byte[]) value;
		throw new IllegalStateException("Entry value is not an extended binary");
	}

	/**
	 * Returns the extended date if it is so, otherwise throws an
	 * <i>IllegalStateException</i>.
	 * 
	 * @return The extended date value.
	 */
	public LocalDate getDate() {
		if (type == JSONTypes.DATE) return (LocalDate) value;
		throw new IllegalStateException("Entry value is not an extended date");
	}

	/**
	 * Returns the extended time if it is so, otherwise throws an
	 * <i>IllegalStateException</i>.
	 * 
	 * @return The extended time value.
	 */
	public LocalTime getTime() {
		if (type == JSONTypes.TIME) return (LocalTime) value;
		throw new IllegalStateException("Entry value is not an extended time");
	}

	/**
	 * Returns the extended timestamp if it is so, otherwise throws an
	 * <i>IllegalStateException</i>.
	 * 
	 * @return The extended timestamp value.
	 */
	public LocalDateTime getTimestamp() {
		if (type == JSONTypes.TIMESTAMP) return (LocalDateTime) value;
		throw new IllegalStateException("Entry value is not an extended timestamp");
	}

	/**
	 * Returns the number, either standard or strict extended if it is numeric, as a
	 * <i>BigDecimal</i>, otherwise throws an <i>IllegalStateException</i>.
	 * 
	 * @return The extended decimal value.
	 */
	public BigDecimal getDecimal() {
		return getNumber();
	}

	/**
	 * Returns the number, either standard or strict extended if it is numeric, as a
	 * <i>Double</i>, otherwise throws an <i>IllegalStateException</i>.
	 * 
	 * @return The extended double value.
	 */
	public Double getDouble() {
		if (type.isNumeric() && value == null) return null;
		if (type == JSONTypes.DOUBLE) return (Double) value;
		if (type.isNumeric()) return getNumber().doubleValue();
		throw new IllegalStateException("Entry value is not numeric");
	}

	/**
	 * Returns the number, either standard or strict extended if it is numeric, as
	 * an <i>Integer</i>, otherwise throws an <i>IllegalStateException</i>.
	 * 
	 * @return The extended integer value.
	 */
	public Integer getInteger() {
		if (type.isNumeric() && value == null) return null;
		if (type == JSONTypes.INTEGER) return (Integer) value;
		if (type.isNumeric()) return getNumber().intValue();
		throw new IllegalStateException("Entry value is not numeric");
	}

	/**
	 * Returns the number, either standard or strict extended if it is numeric, as a
	 * <i>Long</i>, otherwise throws an <i>IllegalStateException</i>.
	 * 
	 * @return The extended long value.
	 */
	public Long getLong() {
		if (type.isNumeric() && value == null) return null;
		if (type == JSONTypes.LONG) return (Long) value;
		if (type.isNumeric()) return getNumber().longValue();
		throw new IllegalStateException("Entry value is not numeric");
	}

	/**
	 * Returns the type of this entry.
	 * 
	 * @return The type.
	 */
	public JSONTypes getType() {
		return type;
	}

	/**
	 * Returns the value of this entry.
	 * 
	 * @return The value.
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Set the typed value.
	 * 
	 * @param type  The JSON type.
	 * @param value The value.
	 */
	public void setValue(JSONTypes type, Object value) {

		/* Validate that type is not null. */
		if (type == null) throw new NullPointerException();

		/* Validate the value when it is not null. */
		if (value != null) {
			boolean valid = false;
			/* Standard types. */
			valid |= (type == JSONTypes.OBJECT && value instanceof JSONObject);
			valid |= (type == JSONTypes.ARRAY && value instanceof JSONArray);
			valid |= (type == JSONTypes.STRING && value instanceof String);
			valid |= (type == JSONTypes.NUMBER && value instanceof Number);
			valid |= (type == JSONTypes.BOOLEAN && value instanceof Boolean);
			/* Extended types strict. */
			valid |= (type == JSONTypes.BINARY && value instanceof byte[]);
			valid |= (type == JSONTypes.DATE && value instanceof LocalDate);
			valid |= (type == JSONTypes.TIME && value instanceof LocalTime);
			valid |= (type == JSONTypes.TIMESTAMP && value instanceof LocalDateTime);
			valid |= (type == JSONTypes.DECIMAL && value instanceof BigDecimal);
			valid |= (type == JSONTypes.DOUBLE && value instanceof Double);
			valid |= (type == JSONTypes.INTEGER && value instanceof Integer);
			valid |= (type == JSONTypes.LONG && value instanceof Long);
			/* Extended numeric types passed as a number. */
			valid |= (type == JSONTypes.DECIMAL && value instanceof Number);
			valid |= (type == JSONTypes.DOUBLE && value instanceof Number);
			valid |= (type == JSONTypes.INTEGER && value instanceof Number);
			valid |= (type == JSONTypes.LONG && value instanceof Number);
			if (!valid) {
				throw new IllegalArgumentException("Invalid type " + type + " for " + value.getClass());
			}
		}

		/* Null value. Only standard NULL and extended types support a null value. */
		if (value == null) {
			boolean valid = false;
			/* Standard types: only null type supports nulls. */
			valid |= (type == JSONTypes.NULL);
			/* Extended types support null values. */
			valid |= (type == JSONTypes.BINARY);
			valid |= (type == JSONTypes.DATE);
			valid |= (type == JSONTypes.TIME);
			valid |= (type == JSONTypes.TIMESTAMP);
			valid |= (type == JSONTypes.DECIMAL);
			valid |= (type == JSONTypes.DOUBLE);
			valid |= (type == JSONTypes.INTEGER);
			valid |= (type == JSONTypes.LONG);
			if (!valid) {
				throw new IllegalArgumentException("Invalid type " + type + " for a null value.");
			}
		}

		/* Convert a standard number into decimal. */
		if (value != null && type == JSONTypes.NUMBER) {
			if (value instanceof Double) value = BigDecimal.valueOf((Double) value);
			if (value instanceof Integer) value = BigDecimal.valueOf((Integer) value);
			if (value instanceof Long) value = BigDecimal.valueOf((Long) value);
		}

		/* Extended numeric types passed not strict (another number). */
		if (value != null) {
			if (type == JSONTypes.DECIMAL && !(value instanceof BigDecimal)) {
				value = BigDecimal.valueOf(((Number) value).doubleValue());
			}
			if (type == JSONTypes.DOUBLE && !(value instanceof Double)) {
				value = ((Number) value).doubleValue();
			}
			if (type == JSONTypes.INTEGER && !(value instanceof Integer)) {
				value = ((Number) value).intValue();
			}
			if (type == JSONTypes.LONG && !(value instanceof Long)) {
				value = ((Number) value).longValue();
			}
		}

		/* Just assign. */
		this.type = type;
		this.value = value;
	}

	/**
	 * Returns a string representation of this entry value.
	 */
	@Override
	public String toString() {
		if (value == null) return "null";
		return value.toString();
	}
}
