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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * An ordered list of fields that can efficiently be accessed by index or by key
 * or field alias.
 * 
 * @author Miquel Sas
 */
public class FieldList implements Iterable<Field> {

	/** List of fields. */
	private List<Field> fields = new ArrayList<>();

	/** List of keys or aliases. */
	private List<String> keys = new ArrayList<>();
	/** Map of indexes by key. */
	private Map<String, Integer> indexes = new HashMap<>();

	/** The list of persistent fields. */
	private List<Field> persistentFields = new ArrayList<>();
	/** The list of primary key fields. */
	private List<Field> primaryKeyFields = new ArrayList<>();

	/** List of default values. */
	private List<Value> defaultValues = new ArrayList<>();

	/**
	 * Constructor.
	 */
	public FieldList() {}
	/**
	 * Constructor passing a list of fields.
	 * @param fields The list of fields.
	 */
	public FieldList(FieldList fields) {
		for (Field field : fields) { this.fields.add(field); }
		setupAndValidate();
	}
	/**
	 * Constructor passing a list of fields.
	 * @param fields The list of fields.
	 */
	public FieldList(List<Field> fields) {
		this.fields.addAll(fields);
		setupAndValidate();
	}

	/**
	 * Add a field.
	 * @param field A field.
	 */
	public void addField(Field field) {
		fields.add(field);
		setupAndValidate();
	}
	/**
	 * Add a field.
	 * @param index The index to add the field.
	 * @param field A field.
	 */
	public void addField(int index, Field field) {
		fields.add(index, field);
		setupAndValidate();
	}
	/**
	 * Add a list of fields.
	 * @param fields The list of fields.
	 */
	public void addFields(List<Field> fields) {
		this.fields.addAll(fields);
		setupAndValidate();
	}

	/**
	 * Return a default record.
	 * @return The default record.
	 */
	public Record getDefaultRecord() { return new Record(this, defaultValues); }

	/**
	 * Returns the list of default values.
	 * @return The list of values.
	 */
	public List<Value> getDefaultValues() { return defaultValues; }

	/**
	 * Returns the field or null if none is found.
	 * @param index The index of the field.
	 * @return The field or null if none is found.
	 */
	public Field getField(int index) { return fields.get(index); }
	/**
	 * Returns the field or null if none is found.
	 * @param key The key or field name or alias.
	 * @return The field or null if none is found.
	 */
	public Field getField(String key) { return getField(indexOf(key)); }
	/**
	 * Returns an unmodifiable collection with the list of fields.
	 * @return A collection with the list of fields.
	 */
	public List<Field> getFields() { return fields; }

	/**
	 * Returns an unmodifiable collection with the list of keys.
	 * @return A collection with the list of keys.
	 */
	public List<String> getKeys() { return keys; }

	/**
	 * Returns the list of persistent fields.
	 * @return The list of persistent fields.
	 */
	public List<Field> getPersistentFields() { return persistentFields; }
	/**
	 * Returns the list of primary key fields.
	 * @return The list of primary key fields.
	 */
	public List<Field> getPrimaryKeyFields() { return primaryKeyFields; }
	/**
	 * Returns the primary order.
	 * @return The primary order.
	 */
	public Order getPrimaryOrder() {
		Order order = new Order();
		List<Field> pkFields = getPrimaryKeyFields();
		for (Field field : pkFields) { order.addField(field, true); }
		return order;
	}

	/**
	 * Returns the index of the field.
	 * @param key The key or field alias.
	 * @return The index of the field.
	 */
	public int indexOf(String key) {
		Integer index = indexes.get(key);
		if (index == null) throw new IllegalArgumentException("Invalid key");
		return index;
	}

	/**
	 * Returns a boolean indicating whether the list of fields is empty.
	 * @return A boolean.
	 */
	public boolean isEmpty() { return fields.isEmpty(); }

	/**
	 * Returns the size or number of fields.
	 * @return The size or number of fields.
	 */
	public int size() { return fields.size(); }

	/**
	 * Check whether this field list contains the argument field.
	 * @param field The field to check.
	 * @return A boolean.
	 */
	public boolean contains(Field field) { return fields.contains(field); }
	/**
	 * Check whether the key is contained.
	 * @param key The key to check.
	 * @return A boolean.
	 */
	public boolean containsKey(String key) { return indexes.containsKey(key); }

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		FieldList fieldList = (FieldList) obj;
		return fields.equals(fieldList.fields);
	}
	@Override
	public int hashCode() {
		int hash = 5;
		for (Field field : fields) { hash += field.hashCode(); }
		return hash;
	}

	@Override
	public Iterator<Field> iterator() { return fields.iterator(); }

	/**
	 * Validates the list of values.
	 * @param values The array of values to validate.
	 */
	public void validate(Value[] values) {
		if (values == null) {
			throw new NullPointerException();
		}
		if (values.length != size()) {
			throw new IllegalArgumentException("Invalid values size " + values.length);
		}
		for (int i = 0; i < size(); i++) {
			if (getField(i).getType() != values[i].getType()) {
				throw new IllegalArgumentException("Invalid value type " + (i) + " type " + values[i].getType());
			}
		}
	}

	/**
	 * Setup internal structures.
	 */
	public void setupAndValidate() {

		keys.clear();
		indexes.clear();
		persistentFields.clear();
		primaryKeyFields.clear();
		defaultValues.clear();

		for (int i = 0; i < fields.size(); i++) {
			Field field = fields.get(i);
			if (field == null) throw new IllegalStateException("Null field " + i);
			String key = field.getAlias();
			keys.add(key);
			indexes.put(key, i);
			if (field.isLocal() && field.isPrimaryKey()) {
				primaryKeyFields.add(field);
			}
			if (field.isPersistent()) {
				persistentFields.add(field);
			}
			defaultValues.add(field.getDefaultValue());
		}
	}
}
