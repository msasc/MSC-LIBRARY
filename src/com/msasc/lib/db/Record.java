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
import java.util.Arrays;
import java.util.List;

import com.msasc.lib.json.JSONObject;
import com.msasc.lib.util.Numbers;

/**
 * A record packs a list of values and their corresponding field definitions.
 * @author Miquel Sas
 */
public class Record {

	/** The list of fields. */
	private FieldList fields;
	/** The list of values. */
	private Value[] values;
	/** List of modified flags. */
	private boolean[] modified;

	/**
	 * Constructor assigning the list of fields.
	 * @param fields The list of fields.
	 */
	public Record(FieldList fields) {
		this(fields, fields.getDefaultValues());
	}
	/**
	 * Constructor assigning the list of fields and values. For performance issues
	 * fields and values are not validated. In case the construction of the list of
	 * values should be validated against the fields, use
	 * <i>FieldList.validate(Value[] values)</i>.
	 * @param fields The list of fields.
	 * @param values The list of values.
	 */
	public Record(FieldList fields, List<Value> values) {
		this.fields = fields;
		this.values = new Value[values.size()];
		for (int i = 0; i < values.size(); i++) { this.values[i] = values.get(i); }
		this.modified = new boolean[fields.size()];
	}

	/**
	 * Constructor assigning the list of fields and values. For performance issues
	 * fields and values are not validated. In case the construction of the list of
	 * values should be validated against the fields, use
	 * <i>FieldList.validate(Value[] values)</i>.
	 * @param fields The list of fields.
	 * @param values The list of values.
	 */
	public Record(FieldList fields, Value[] values) {
		this.fields = fields;
		this.values = Arrays.copyOf(values, values.length);
		this.modified = new boolean[fields.size()];
	}

	/**
	 * Return the field.
	 * @param index Index.
	 * @return the field.
	 */
	public Field getField(int index) { return fields.getField(index); }
	/**
	 * Return the field.
	 * @param alias Alias.
	 * @return the field.
	 */
	public Field getField(String alias) { return fields.getField(alias); }
	/**
	 * Return the field list.
	 * @return The field list.
	 */
	public FieldList getFields() { return fields; }
	/**
	 * Returns the order key for the given order. The order must contain fields of
	 * the record.
	 * @param order The order.
	 * @return The key.
	 */
	public OrderKey getOrderKey(Order order) {
		OrderKey key = new OrderKey();
		for (Order.Segment seg : order.getSegments()) {
			Field field = seg.getField();
			boolean asc = seg.isAscending();
			Value value = getValue(field.getAlias());
			if (value == null) {
				throw new IllegalArgumentException();
			}
			key.add(value, asc);
		}
		return key;
	}
	/**
	 * Get the primary key.
	 * @return The primary key.
	 */
	public OrderKey getPrimaryKey() {
		List<Field> keyFields = fields.getPrimaryKeyFields();
		OrderKey orderKey = new OrderKey();
		for (Field field : keyFields) {
			orderKey.add(getValue(field.getAlias()), true);
		}
		return orderKey;
	}

	/**
	 * Returns the value.
	 * @param index Field Index.
	 * @return Field value;
	 */
	public Value getValue(int index) { validateIndex(index); return values[index]; }
	/**
	 * Returns the value.
	 * @param alias Field alias.
	 * @return Field value.
	 */
	public Value getValue(String alias) { return values[fields.indexOf(alias)]; }

	/**
	 * Set the value.
	 * @param index Field index.
	 * @param value Field value.
	 */
	public void setValue(int index, boolean value) { setValue(index, new Value(value)); }
	/**
	 * Set the value.
	 * @param index Field index.
	 * @param value Field value.
	 */
	public void setValue(int index, Boolean value) { setValue(index, new Value(value)); }

	/**
	 * Set the value.
	 * @param alias Field alias.
	 * @param value Field value.
	 */
	public void setValue(String alias, boolean value) { setValue(alias, new Value(value)); }
	/**
	 * Set the value.
	 * @param alias Field alias.
	 * @param value Field value.
	 */
	public void setValue(String alias, Boolean value) { setValue(alias, new Value(value)); }

	/**
	 * Set the value.
	 * @param index Field index.
	 * @param value Field value.
	 */
	public void setValue(int index, BigDecimal value) { setValue(index, new Value(value)); }
	/**
	 * Set the value.
	 * @param index Field index.
	 * @param value Field value.
	 */
	public void setValue(int index, double value) { setValue(index, new Value(value)); }
	/**
	 * Set the value.
	 * @param index Field index.
	 * @param value Field value.
	 */
	public void setValue(int index, Double value) { setValue(index, new Value(value)); }
	/**
	 * Set the value.
	 * @param index Field index.
	 * @param value Field value.
	 */
	public void setValue(int index, int value) { setValue(index, new Value(value)); }
	/**
	 * Set the value.
	 * @param index Field index.
	 * @param value Field value.
	 */
	public void setValue(int index, Integer value) { setValue(index, new Value(value)); }
	/**
	 * Set the value.
	 * @param index Field index.
	 * @param value Field value.
	 */
	public void setValue(int index, long value) { setValue(index, new Value(value)); }
	/**
	 * Set the value.
	 * @param index Field index.
	 * @param value Field value.
	 */
	public void setValue(int index, Long value) { setValue(index, new Value(value)); }

	/**
	 * Set the value.
	 * @param alias Field alias.
	 * @param value Field value.
	 */
	public void setValue(String alias, BigDecimal value) { setValue(alias, new Value(value)); }
	/**
	 * Set the value.
	 * @param alias Field alias.
	 * @param value Field value.
	 */
	public void setValue(String alias, double value) { setValue(alias, new Value(value)); }
	/**
	 * Set the value.
	 * @param alias Field alias.
	 * @param value Field value.
	 */
	public void setValue(String alias, Double value) { setValue(alias, new Value(value)); }
	/**
	 * Set the value.
	 * @param alias Field alias.
	 * @param value Field value.
	 */
	public void setValue(String alias, int value) { setValue(alias, new Value(value)); }
	/**
	 * Set the value.
	 * @param alias Field alias.
	 * @param value Field value.
	 */
	public void setValue(String alias, Integer value) { setValue(alias, new Value(value)); }
	/**
	 * Set the value.
	 * @param alias Field alias.
	 * @param value Field value.
	 */
	public void setValue(String alias, long value) { setValue(alias, new Value(value)); }
	/**
	 * Set the value.
	 * @param alias Field alias.
	 * @param value Field value.
	 */
	public void setValue(String alias, Long value) { setValue(alias, new Value(value)); }

	/**
	 * Set the value.
	 * @param index Field index.
	 * @param value Field value.
	 */
	public void setValue(int index, LocalDate value) { setValue(index, new Value(value)); }
	/**
	 * Set the value.
	 * @param index Field index.
	 * @param value Field value.
	 */
	public void setValue(int index, LocalTime value) { setValue(index, new Value(value)); }
	/**
	 * Set the value.
	 * @param index Field index.
	 * @param value Field value.
	 */
	public void setValue(int index, LocalDateTime value) { setValue(index, new Value(value)); }

	/**
	 * Set the value.
	 * @param alias Field alias.
	 * @param value Field value.
	 */
	public void setValue(String alias, LocalDate value) { setValue(alias, new Value(value)); }
	/**
	 * Set the value.
	 * @param alias Field alias.
	 * @param value Field value.
	 */
	public void setValue(String alias, LocalTime value) { setValue(alias, new Value(value)); }
	/**
	 * Set the value.
	 * @param alias Field alias.
	 * @param value Field value.
	 */
	public void setValue(String alias, LocalDateTime value) { setValue(alias, new Value(value)); }

	/**
	 * Set the value.
	 * @param index Field index.
	 * @param value Field value.
	 */
	public void setValue(int index, String value) { setValue(index, new Value(value)); }
	/**
	 * Set the value.
	 * @param alias Field alias.
	 * @param value Field value.
	 */
	public void setValue(String alias, String value) { setValue(alias, new Value(value)); }

	/**
	 * Set the value.
	 * @param index Field index.
	 * @param value Field value.
	 */
	public void setValue(int index, byte[] value) { setValue(index, new Value(value)); }
	/**
	 * Set the value.
	 * @param alias Field alias.
	 * @param value Field value.
	 */
	public void setValue(String alias, byte[] value) { setValue(alias, new Value(value)); }

	/**
	 * Set the value.
	 * @param index Field index.
	 * @param value Field value.
	 */
	public void setValue(int index, Value[] value) { setValue(index, new Value(value)); }
	/**
	 * Set the value.
	 * @param alias Field alias.
	 * @param value Field value.
	 */
	public void setValue(String alias, Value[] value) { setValue(alias, new Value(value)); }

	/**
	 * Set the value.
	 * @param index Field index.
	 * @param value Field value.
	 */
	public void setValue(int index, JSONObject value) { setValue(index, new Value(value)); }
	/**
	 * Set the value.
	 * @param alias Field alias.
	 * @param value Field value.
	 */
	public void setValue(String alias, JSONObject value) { setValue(alias, new Value(value)); }

	/**
	 * Set the value.
	 * @param index Field index.
	 * @param value Field value.
	 */
	public void setValue(int index, Value value) {

		/* Validate not null. */
		if (value == null) {
			throw new NullPointerException();
		}

		/* Validate index. */
		validateIndex(index);

		/* Validate type. */
		Types type_curr = fields.getField(index).getType();
		Types type_next = value.getType();

		/* Not numeric types. */
		if (!type_curr.isNumber() || !type_next.isNumber()) {
			if (value.getType() != fields.getField(index).getType()) {
				throw new IllegalArgumentException("Invalid value type " + value.getType());
			}
			values[index] = value;
			modified[index] = true;
			return;
		}

		/* Both numeric types: same type. */
		if (type_curr == type_next) {
			values[index] = value;
			modified[index] = true;
			return;
		}

		/* Both numeric types: different type -> adapt. */
		if (type_curr.isDecimal()) {
			if (value.isNull()) {
				values[index] = new Value((BigDecimal) null);
				modified[index] = true;
				return;
			}
			int decimals = fields.getField(index).getDecimals();
			values[index] = new Value(Numbers.getBigDecimal(value.getNumber(), decimals));
			modified[index] = true;
			return;
		}
		if (type_curr.isDouble()) {
			if (value.isNull()) {
				values[index] = new Value((Double) null);
				modified[index] = true;
				return;
			}
			values[index] = new Value(value.getNumber().doubleValue());
			modified[index] = true;
			return;
		}
		if (type_curr.isInteger()) {
			if (value.isNull()) {
				values[index] = new Value((Integer) null);
				modified[index] = true;
				return;
			}
			values[index] = new Value(value.getNumber().intValue());
			modified[index] = true;
			return;
		}
		if (type_curr.isLong()) {
			if (value.isNull()) {
				values[index] = new Value((Long) null);
				modified[index] = true;
				return;
			}
			values[index] = new Value(value.getNumber().longValue());
			modified[index] = true;
			return;
		}
	}
	/**
	 * Set the value.
	 * @param alias Field alias.
	 * @param value Field value.
	 */
	public void setValue(String alias, Value value) {
		int index = fields.indexOf(alias);
		setValue(index, value);
	}

	/**
	 * Returns the number of fields.
	 * @return The number of fields.
	 */
	public final int size() { return fields.size(); }
	
	/**
	 * Return the array of values.
	 * @return The array of values.
	 */
	public final Value[] values() { return values; }

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < size(); i++) {
			if (i > 0) b.append(", ");
			b.append(getValue(i));
		}
		return b.toString();
	}

	/**
	 * Validates the index.
	 * @param index The field or value index.
	 */
	private void validateIndex(int index) {
		if (index < 0 || index >= values.length) {
			throw new IllegalArgumentException("Invalid value index " + index);
		}
	}
}
