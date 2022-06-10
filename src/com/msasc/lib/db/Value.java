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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

import com.msasc.lib.json.JSONArray;
import com.msasc.lib.json.JSONEntry;
import com.msasc.lib.json.JSONObject;
import com.msasc.lib.json.JSONTypes;
import com.msasc.lib.util.Binaries;
import com.msasc.lib.util.Numbers;
import com.msasc.lib.util.Values;

/**
 * A value contains a constant reference to one of the supported types. It can
 * be considered immutable in its reference for all types, and in the underlying
 * value for all types except <i>JSONObject</i>.
 * <p>
 * Values accept typed nulls: to construct a null value of a decimal type, use
 * <i>new Value((BigDecimal) null)</i>.
 * 
 * @author Miquel Sas
 */
public class Value implements Comparable<Object> {

	/**
	 * Return a <i>JSONArray</i> that maps the array of values.
	 * @param value_array The array o values.
	 * @return The <i>JSONArray</i>.
	 */
	public static JSONArray toJSONArray(Value[] value_array) {
		JSONArray json_array = new JSONArray();
		for (Value value : value_array) {
			json_array.add(value.toJSONEntry());
		}
		return json_array;
	}
	/**
	 * Return a <i>JSONObject</i> that maps the argument value array.
	 * @param value_array The array o values.
	 * @return The <i>JSONObject</i>.
	 */
	public static JSONObject toJSONObject(Value[] value_array) {
		JSONObject json_obj = new JSONObject();
		json_obj.put("value_array", JSONTypes.ARRAY, toJSONArray(value_array));
		return json_obj;
	}
	/**
	 * Convert a <i>JSONEntry</i> to a <i>Value</i>.
	 * @param entry The <i>JSONEntry</i>.
	 * @return The corresponding <i>Value</i>.
	 */
	public static Value toValue(JSONEntry entry) {
		switch (entry.getType()) {
		case NULL:
			return new Value((String) null);
		case BOOLEAN:
			return new Value(entry.getBoolean());
		case DATE:
			return new Value(entry.getDate());
		case TIME:
			return new Value(entry.getTime());
		case TIMESTAMP:
			return new Value(entry.getTimestamp());
		case NUMBER:
			return new Value(entry.getNumber());
		case DECIMAL:
			return new Value(entry.getDecimal());
		case DOUBLE:
			return new Value(entry.getDouble());
		case INTEGER:
			return new Value(entry.getInteger());
		case LONG:
			return new Value(entry.getLong());
		case BINARY:
			return new Value(entry.getBinary());
		case STRING:
			return new Value(entry.getString());
		case OBJECT:
			return new Value(entry.getObject());
		case ARRAY:
			return new Value(toValueArray(entry.getArray()));
		}
		throw new IllegalStateException("Unreachable code");
	}
	/**
	 * Convert the argument <i>JSONArray</i> to a <i>Value[]</i>.
	 * @param json_array The <i>JSONArray</i> to convert.
	 * @return The corresponding <i>Value[]</i>.
	 */
	public static Value[] toValueArray(JSONArray json_array) {
		if (json_array == null) {
			return new Value[] {};
		}
		Value[] value_array = new Value[json_array.size()];
		for (int i = 0; i < json_array.size(); i++) {
			value_array[i] = toValue(json_array.get(i));
		}
		return value_array;
	}
	/**
	 * Returns a value array from a <i>JSONObject</i> that contains a
	 * <i>JSONArray</i> that maps the value array.
	 * @param json_obj The source <i>JSONObject</i>.
	 * @return The value array.
	 */
	public static Value[] toValueArray(JSONObject json_obj) {
		JSONArray json_arr = json_obj.get("value_array").getArray();
		Value[] values = Value.toValueArray(json_arr);
		return values;
	}

	/** Internal value. */
	private Object value;
	/** The type, registered to track it for null values. */
	private Types type;

	/**
	 * Constructor of a <i>BOOLEAN</i> value.
	 * @param value A <i>Boolean</i>.
	 */
	public Value(Boolean value) {
		this.type = Types.BOOLEAN;
		this.value = value;
	}
	/**
	 * Constructor of a <i>DECIMAL</i> value.
	 * @param value A <i>BigDecimal</i>.
	 */
	public Value(BigDecimal value) {
		this.type = Types.DECIMAL;
		this.value = value;
	}
	/**
	 * Constructor of a <i>DOUBLE</i> value.
	 * @param value A <i>Double</i>.
	 */
	public Value(Double value) {
		this.type = Types.DOUBLE;
		this.value = value;
	}
	/**
	 * Constructor of a <i>INTEGER</i> value.
	 * @param value A <i>Integer</i>.
	 */
	public Value(Integer value) {
		this.type = Types.INTEGER;
		this.value = value;
	}
	/**
	 * Constructor of a <i>LONG</i> value.
	 * @param value A <i>Long</i>.
	 */
	public Value(Long value) {
		this.type = Types.LONG;
		this.value = value;
	}
	/**
	 * Constructor of a <i>DATE</i> value.
	 * @param value A <i>LocalDate</i>.
	 */
	public Value(LocalDate value) {
		this.type = Types.DATE;
		this.value = value;
	}
	/**
	 * Constructor of a <i>TIME</i> value.
	 * @param value A <i>LocalTime</i>.
	 */
	public Value(LocalTime value) {
		this.type = Types.TIME;
		this.value = value;
	}
	/**
	 * Constructor of a <i>TIMESTAMP</i> value.
	 * @param value A <i>LocalDateTime</i>.
	 */
	public Value(LocalDateTime value) {
		this.type = Types.TIMESTAMP;
		this.value = value;
	}
	/**
	 * Constructor of a <i>STRING</i> value.
	 * @param value A <i>String</i>.
	 */
	public Value(String value) {
		this.type = Types.STRING;
		this.value = value;
	}
	/**
	 * Constructor of a <i>BINARY</i> value.
	 * @param value A <i>byte[]</i>.
	 */
	public Value(byte[] value) {
		this.type = Types.BINARY;
		this.value = value;
	}
	/**
	 * Constructor of an <i>ARRAY</i> value.
	 * @param value A <i>Value[]</i>.
	 */
	public Value(Value[] value) {
		this.type = Types.ARRAY;
		this.value = value;
	}
	/**
	 * Constructor of a <i>JSONOBJECT</i> value.
	 * @param value A <i>JSONObject</i>.
	 */
	public Value(JSONObject value) {
		this.type = Types.JSONOBJECT;
		this.value = value;
	}

	/**
	 * Return the <i>Boolean</i> value.
	 * @return The <i>Boolean</i> value or null.
	 * @throws ClassCastException if the type is not a <i>BOOLEAN</i>.
	 */
	public Boolean getBoolean() {
		if (type != Types.BOOLEAN) throw new ClassCastException("Type is not a BOOLEAN");
		return (Boolean) value;
	}

	/**
	 * Return the <i>BigDecimal</i> value.
	 * @return The <i>BigDecimal</i> value or null.
	 * @throws ClassCastException if the type is not numeric.
	 */
	public BigDecimal getBigDecimal() {
		if (!type.isNumber()) throw new ClassCastException("Type is not numeric");
		if (value == null) return (BigDecimal) null;
		if (type == Types.DECIMAL) return (BigDecimal) value;
		return BigDecimal.valueOf(((Number) value).doubleValue());
	}
	/**
	 * Return the <i>Double</i> value.
	 * @return The <i>Double</i> value or null.
	 * @throws ClassCastException if the type is not numeric.
	 */
	public Double getDouble() {
		if (!type.isNumber()) throw new ClassCastException("Type is not numeric");
		if (value == null) return (Double) null;
		if (type == Types.DOUBLE) return (Double) value;
		return ((Number) value).doubleValue();
	}
	/**
	 * Return the <i>Integer</i> value.
	 * @return The <i>Integer</i> value or null.
	 * @throws ClassCastException if the type is not numeric.
	 */
	public Integer getInteger() {
		if (!type.isNumber()) throw new ClassCastException("Type is not numeric");
		if (value == null) return (Integer) null;
		if (type == Types.INTEGER) return (Integer) value;
		return ((Number) value).intValue();
	}
	/**
	 * Return the <i>Long</i> value.
	 * @return The <i>Long</i> value or null.
	 * @throws ClassCastException if the type is not numeric.
	 */
	public Long getLong() {
		if (!type.isNumber()) throw new ClassCastException("Type is not numeric");
		if (value == null) return (Long) null;
		if (type == Types.LONG) return (Long) value;
		return ((Number) value).longValue();
	}
	/**
	 * Return the <i>Number</i> value.
	 * @return The <i>Number</i> value or null.
	 * @throws ClassCastException if the type is not numeric.
	 */
	public Number getNumber() {
		if (!type.isNumber()) throw new ClassCastException("Type is not numeric");
		return ((Number) value);
	}

	/**
	 * Return the <i>LocalDate</i> value.
	 * @return The <i>LocalDate</i> value or null.
	 * @throws ClassCastException if the type is not a <i>DATE</i>.
	 */
	public LocalDate getDate() {
		if (type != Types.DATE) throw new ClassCastException("Type is not a DATE");
		return (LocalDate) value;
	}
	/**
	 * Return the <i>LocalTime</i> value.
	 * @return The <i>LocalTime</i> value or null.
	 * @throws ClassCastException if the type is not a <i>TIME</i>.
	 */
	public LocalTime getTime() {
		if (type != Types.TIME) throw new ClassCastException("Type is not a TIME");
		return (LocalTime) value;
	}
	/**
	 * Return the <i>LocalDateTime</i> value.
	 * @return The <i>LocalDateTime</i> value or null.
	 * @throws ClassCastException if the type is not a <i>TIMESTAMP</i>.
	 */
	public LocalDateTime getTimestamp() {
		if (type != Types.TIMESTAMP) throw new ClassCastException("Type is not a TIMESTAMP");
		return (LocalDateTime) value;
	}

	/**
	 * Return the <i>String</i> value.
	 * @return The <i>String</i> value or null.
	 * @throws ClassCastException if the type is not a <i>STRING</i>.
	 */
	public String getString() {
		if (type != Types.STRING) throw new ClassCastException("Type is not a STRING");
		return (String) value;
	}

	/**
	 * Return the <i>byte[]</i> value.
	 * @return The <i>byte[]</i> value or null.
	 * @throws ClassCastException if the type is not a <i>BINARY</i>.
	 */
	public byte[] getBinary() {
		if (type != Types.BINARY) throw new ClassCastException("Type is not a BINARY");
		return (byte[]) value;
	}

	/**
	 * Return the <i>Value[]</i> value.
	 * @return The <i>Value[]</i> value or null.
	 * @throws ClassCastException if the type is not an <i>ARRAY</i>.
	 */
	public Value[] getArray() {
		if (type != Types.ARRAY) throw new ClassCastException("Type is not a ARRAY");
		return (Value[]) value;
	}

	/**
	 * Return the <i>JSONObject</i> value.
	 * @return The <i>JSONObject</i> value or null.
	 * @throws ClassCastException if the type is not a <i>JSONOBJECT</i>.
	 */
	public JSONObject getJSONObject() {
		if (type != Types.JSONOBJECT) throw new ClassCastException("Type is not a JSONOBJECT");
		return (JSONObject) value;
	}

	/**
	 * Return the type.
	 * @return The type.
	 */
	public Types getType() { return type; }

	/**
	 * Check whether the value is null.
	 * @return A boolean.
	 */
	public boolean isNull() { return value == null; }
	/**
	 * Return a boolean indicating whether the type is BOOLEAN.
	 * @return A boolean.
	 */
	public boolean isBoolean() { return type.isBoolean(); }
	/**
	 * Return a boolean indicating whether the type is DECIMAL.
	 * @return A boolean.
	 */
	public boolean isDecimal() { return type.isDecimal(); }
	/**
	 * Return a boolean indicating whether the type is DOUBLE.
	 * @return A boolean.
	 */
	public boolean isDouble() { return type.isDouble(); }
	/**
	 * Return a boolean indicating whether the type is INTEGER.
	 * @return A boolean.
	 */
	public boolean isInteger() { return type.isInteger(); }
	/**
	 * Return a boolean indicating whether the type is LONG.
	 * @return A boolean.
	 */
	public boolean isLong() { return type.isLong(); }
	/**
	 * Return a boolean indicating whether the type is a number Types.
	 * 
	 * @return A boolean.
	 */
	public boolean isNumber() { return type.isNumber(); }
	/**
	 * Return a boolean indicating whether the type is DATE.
	 * @return A boolean.
	 */
	public boolean isDate() { return type.isDate(); }
	/**
	 * Return a boolean indicating whether the type is TIME.
	 * @return A boolean.
	 */
	public boolean isTime() { return type.isTime(); }
	/**
	 * Return a boolean indicating whether the type is TIMESTAMP.
	 * @return A boolean.
	 */
	public boolean isTimestamp() { return type.isTimestamp(); }
	/**
	 * Return a boolean indicating whether the type is STRING.
	 * @return A boolean.
	 */
	public boolean isString() { return type.isString(); }
	/**
	 * Return a boolean indicating whether the type is BINARY.
	 * @return A boolean.
	 */
	public boolean isBinary() { return type.isBinary(); }
	/**
	 * Return a boolean indicating whether the type an ARRAY.
	 * @return A boolean.
	 */
	public boolean isArray() { return type.isArray(); }
	/**
	 * Return a boolean indicating whether the type is JSONOBJECT.
	 * @return A boolean.
	 */
	public boolean isJSONObject() { return type.isJSONObject(); }

	@Override
	public int compareTo(Object o) {

		/* No null compare. */
		if (o == null) throw new NullPointerException();

		/* Value. */
		if (o instanceof Value v) {
			if (type.isNumber() && v.type.isNumber()) {
				if (!isNull() && v.isNull()) return -1;
				if (isNull() && !v.isNull()) return 1;
				if (isNull() && v.isNull()) return 0;
				return Numbers.compare(getNumber(), v.getNumber());
			}
			if (type.isBoolean() && v.type.isBoolean()) {
				if (!isNull() && v.isNull()) return -1;
				if (isNull() && !v.isNull()) return 1;
				if (isNull() && v.isNull()) return 0;
				return getBoolean().compareTo(v.getBoolean());
			}
			if (type.isDate() && v.type.isDate()) {
				if (!isNull() && v.isNull()) return -1;
				if (isNull() && !v.isNull()) return 1;
				if (isNull() && v.isNull()) return 0;
				return getDate().compareTo(v.getDate());
			}
			if (type.isTime() && v.type.isTime()) {
				if (!isNull() && v.isNull()) return -1;
				if (isNull() && !v.isNull()) return 1;
				if (isNull() && v.isNull()) return 0;
				return getTime().compareTo(v.getTime());
			}
			if (type.isTimestamp() && v.type.isTimestamp()) {
				if (!isNull() && v.isNull()) return -1;
				if (isNull() && !v.isNull()) return 1;
				if (isNull() && v.isNull()) return 0;
				return getTimestamp().compareTo(v.getTimestamp());
			}
			if (type.isString() && v.type.isString()) {
				if (!isNull() && v.isNull()) return -1;
				if (isNull() && !v.isNull()) return 1;
				if (isNull() && v.isNull()) return 0;
				return getString().compareTo(v.getString());
			}
			if (type.isBinary() && v.type.isBinary()) {
				if (!isNull() && v.isNull()) return -1;
				if (isNull() && !v.isNull()) return 1;
				if (isNull() && v.isNull()) return 0;
				return Binaries.compare(getBinary(), v.getBinary());
			}
			if (type.isArray() && v.type.isArray()) {
				if (!isNull() && v.isNull()) return -1;
				if (isNull() && !v.isNull()) return 1;
				if (isNull() && v.isNull()) return 0;
				return Values.compare(getArray(), v.getArray());
			}
			if (type.isJSONObject() && v.type.isJSONObject()) {
				if (!isNull() && v.isNull()) return -1;
				if (isNull() && !v.isNull()) return 1;
				if (isNull() && v.isNull()) return 0;
				return toString().compareTo(v.toString());
			}
			throw new IllegalStateException("Never should happen");
		}

		/* Booleans. */
		if (type.isBoolean() && (o instanceof Boolean b)) {
			if (isNull()) return -1;
			return getBoolean().compareTo(b);
		}

		/* Numbers. */
		if (type.isNumber() && (o instanceof Number n)) {
			if (isNull()) return -1;
			return Numbers.compare(getNumber(), n);
		}

		/* Date. */
		if (type.isDate() && (o instanceof LocalDate d)) {
			if (isNull()) return -1;
			return getDate().compareTo(d);
		}
		/* Time. */
		if (type.isTime() && (o instanceof LocalTime t)) {
			if (isNull()) return -1;
			return getTime().compareTo(t);
		}
		/* Timestamp. */
		if (type.isTimestamp() && (o instanceof LocalDateTime t)) {
			if (isNull()) return -1;
			return getTimestamp().compareTo(t);
		}

		/* String. */
		if (type.isString() && (o instanceof String s)) {
			if (isNull()) return -1;
			return getString().compareTo(s);
		}

		/* Binary. */
		if (type.isBinary() && (o instanceof byte[] b)) {
			if (isNull()) return -1;
			return Binaries.compare(getBinary(), b);
		}

		/* Value[]. */
		if (type.isArray() && (o instanceof Value[] a)) {
			if (isNull()) return -1;
			return Values.compare(getArray(), a);
		}

		/* JSONObject. */
		if (type.isJSONObject() && (o instanceof JSONObject j)) {
			return getJSONObject().toString().compareTo(j.toString());
		}

		throw new IllegalStateException("Not comparable types");
	}
	@Override
	public boolean equals(Object o) { return compareTo(o) == 0; }
	@Override
	public int hashCode() { return Objects.hashCode(value); }

	/**
	 * Returns a proper JSON entry.
	 * @return A JSON entry for this value.
	 */
	public JSONEntry toJSONEntry() {
		return new JSONEntry(type.toJSONTypes(), value);
	}
	@Override
	public String toString() {
		if (value == null) return "null";
		return value.toString();
	}
}
