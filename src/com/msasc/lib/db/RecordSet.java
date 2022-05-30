/*
 * Copyright (C) 2018 Miquel Sas
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.msasc.lib.db;

import java.util.Comparator;
import java.util.Iterator;

/**
 * A RecordSet packs a list of records.
 *
 * @author Miquel Sas
 */
public abstract class RecordSet implements Iterable<Record> {

	/** The list of fields. */
	private FieldList fields;
	/** Index of the row number column if present. */
	private Integer rowNumberIndex;

	/**
	 * Default constructor.
	 */
	public RecordSet() {}
	/**
	 * Constructor assigning the list of fields.
	 * @param fields The list of fields.
	 */
	public RecordSet(FieldList fields) { setFieldList(fields); }

	/**
	 * Inserts a record at a given index.
	 * @param index  The index.
	 * @param record The record to insert.
	 */
	public abstract void add(int index, Record record);
	/**
	 * Add a record to the list.
	 * @param record The record to add
	 * @return A boolean indicating if the record has been added.
	 */
	public abstract void add(Record record);

	/**
	 * Clear the list of records.
	 */
	public abstract void clear();

	/**
	 * Check if the record set contains a record with the given primary key.
	 * @param key The key to look for.
	 * @return A boolean.
	 */
	public boolean contains(OrderKey key) { return indexOf(key) >= 0; }

	/**
	 * Check if the record set contains the record.
	 * @param record The record to check.
	 * @return A boolean.
	 */
	public boolean contains(Record record) { return indexOf(record) >= 0; }

	/**
	 * Get a record given its index in the record list.
	 * @return The Record.
	 * @param index The index in the record list.
	 */
	public Record get(int index) {
		Record rc = getRecord(index);
		if (rowNumberIndex != null) {
			rc.setValue(rowNumberIndex, new Value((long) (index + 1)));
		}
		return rc;
	}

	/**
	 * Internally get a record given its index in the record list.
	 * @return The Record.
	 * @param index The index in the record list.
	 */
	protected abstract Record getRecord(int index);

	/**
	 * Returns a copy of this record set.
	 * @return A copy.
	 */
	public abstract RecordSet getCopy();

	/**
	 * Get the field at the given index.
	 * @param index The index of the field.
	 * @return The field.
	 */
	public Field getField(int index) { return fields.getField(index); }
	/**
	 * Get a field by alias.
	 * @param alias The field alias.
	 * @return The field or null if not found.
	 */
	public Field getField(String alias) { return fields.getField(alias); }
	/**
	 * Returns the number of fields.
	 * @return The number of fields.
	 */
	public int getFieldCount() { return fields.size(); }
	/**
	 * Returns the fields, for use in the friend class Cursor.
	 * @return The field list.
	 */
	public FieldList getFieldList() { return fields; }
	/**
	 * Gets the insert index using the order key.
	 * @param record The record.
	 * @return The insert index.
	 */
	public int getInsertIndex(Record record) { return getInsertIndex(record, fields.getPrimaryOrder()); }
	/**
	 * Gets the insert index using the order key.
	 * @param record The record.
	 * @param order  The order.
	 * @return The insert index.
	 */
	public abstract int getInsertIndex(Record record, Order order);

	/**
	 * Find the index of the given key.
	 * @param key The key to find its index.
	 * @return The index of the record with the given key.
	 */
	public int indexOf(OrderKey key) {
		for (int i = 0; i < size(); i++) {
			if (get(i).getPrimaryKey().equals(key)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Find the index of the given record.
	 *
	 * @param record The record to find its index.
	 * @return The index of the given record.
	 */
	public int indexOf(Record record) { return indexOf(record.getPrimaryKey()); }

	/**
	 * @return If the record set is empty.
	 * @see java.util.ArrayList#isEmpty()
	 */
	public abstract boolean isEmpty();

	/**
	 * Check if the record set can be sorted. Default is true, overwrite this method
	 * if the record set implementation can not be sorted.
	 *
	 * @return A boolean.
	 */
	public boolean isSortable() { return true; }

	/**
	 * Return the iterator.
	 */
	@Override
	public abstract Iterator<Record> iterator();

	/**
	 * Remove a record given its index in the list.
	 * @return The removed record.
	 * @param index The index in the list of records.
	 */
	public abstract Record remove(int index);

	/**
	 * Sets a record given its index in the record list.
	 * @param index  The index in the record list.
	 * @param record The record.
	 */
	public abstract void set(int index, Record record);

	/**
	 * Sets the field list.
	 * @param fields The field list.
	 */
	public final void setFieldList(FieldList fields) {
		this.fields = fields;
		if (fields.containsKey(Field.ALIAS_ROW_NUMBER)) {
			rowNumberIndex = fields.indexOf(Field.ALIAS_ROW_NUMBER);
		}
	}

	/**
	 * Returns this record set size.
	 * @return The size.
	 */
	public abstract int size();

	/**
	 * Sort this list of records based on the order by key pointers, or in its
	 * default the primary key pointers.
	 */
	public void sort() {
		if (size() == 0) return;
		sort(fields.getPrimaryOrder());
	}

	/**
	 * Sort this list of records based on a comparator.
	 * @param comparator The comparator.
	 */
	public abstract void sort(Comparator<Record> comparator);

	/**
	 * Sort this list of records based on the order stated by the argument order.
	 * @param order The <code>Order</code> to use in the sort.
	 */
	public void sort(Order order) {
		sort(new RecordComparator(order));
	}

	/**
	 * Returns an array containing all the records.
	 * @return The array of records.
	 */
	public abstract Record[] toArray();

	/**
	 * To string.
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < Math.min(size(), 500); i++) {
			if (i > 0) b.append("\n");
			b.append(get(i));
		}
		return b.toString();
	}

}
