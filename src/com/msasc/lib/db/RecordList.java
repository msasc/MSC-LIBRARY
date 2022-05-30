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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * A <i>RecordSet</i> backed by an array list of records. If the source was a database table or
 * view, operations like add, set or remove are not persistent.
 *
 * @author Miquel Sas
 */
public class RecordList extends RecordSet {

	/** The list of records. */
	private List<Record> records = new ArrayList<>();

	/**
	 * Default constructor.
	 */
	public RecordList() {}
	/**
	 * Constructor assigning the list of fields.
	 * @param fields The list of fields.
	 */
	public RecordList(FieldList fields) { setFieldList(fields); }

	/**
	 * Clear the list of records.
	 */
	public void clear() { records.clear(); }

	/**
	 * Returns this record set size.
	 * @return The size.
	 */
	public int size() { return records.size(); }

	/**
	 * Add a record to the list.
	 * @param record The record to add
	 * @return A boolean indicating if the record has been added.
	 */
	public void add(Record record) {
		if (getFieldList() == null) setFieldList(record.getFields());
		records.add(record);
	}

	/**
	 * Gets the insert index using the order key.
	 * @param record The record.
	 * @return The insert index.
	 */
	public int getInsertIndex(Record record) {
		return getInsertIndex(record, getFieldList().getPrimaryOrder());
	}

	/**
	 * Gets the insert index using the order key.
	 * @param record The record.
	 * @param order  The order.
	 * @return The insert index.
	 */
	public int getInsertIndex(Record record, Order order) {
		OrderKey key = record.getOrderKey(order);
		int index;
		for (index = 0; index < records.size(); index++) {
			Record scanRecord = records.get(index);
			OrderKey scanKey = scanRecord.getOrderKey(order);
			if (key.compareTo(scanKey) <= 0) {
				break;
			}
		}
		return index;
	}

	/**
	 * Internally get a record given its index in the record list.
	 * @return The Record.
	 * @param index The index in the record list.
	 */
	protected Record getRecord(int index) {
		if (index < 0 || index >= size()) {
			throw new ArrayIndexOutOfBoundsException();
		}
		return records.get(index);
	}
	/**
	 * Find the index of the given record.
	 * @param record The record to find its index.
	 * @return The index of the given record.
	 */
	public int indexOf(Record record) { return records.indexOf(record); }

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
	 * Check if the recordset contains the record.
	 * @param record The record to check.
	 * @return A boolean.
	 */
	public boolean contains(Record record) { return indexOf(record) >= 0; }

	/**
	 * Check if the record set contains a record with the given primary key.
	 * @param key The key to look for.
	 * @return A boolean.
	 */
	public boolean contains(OrderKey key) { return indexOf(key) >= 0; }

	/**
	 * @return If the record set is empty.
	 * @see java.util.ArrayList#isEmpty()
	 */
	public boolean isEmpty() { return records.isEmpty(); }

	/**
	 * Remove a record given its index in the list.
	 * @return The removed record.
	 * @param index The index in the list of records.
	 */
	public Record remove(int index) {
		if (index < 0 || index >= size()) {
			throw new ArrayIndexOutOfBoundsException();
		}
		Record record = get(index);
		records.remove(index);
		return record;
	}

	/**
	 * Sets a record given its index in the record list.
	 * @param index  The index in the record list.
	 * @param record The record.
	 */
	public void set(int index, Record record) { records.set(index, record); }

	/**
	 * Sort this list of records based on the order by key pointers, or in its
	 * default the primary key pointers.
	 */
	public void sort() {
		if (size() == 0) return;
		sort(getFieldList().getPrimaryOrder());
	}

	/**
	 *
	 * @param order The <code>Order</code> to use in the sort.
	 */
	public void sort(Order order) { sort(new RecordComparator(order)); }

	/**
	 * Sort this list of records based on a comparator.
	 * @param comparator The comparator.
	 */
	public void sort(Comparator<Record> comparator) {
		Record[] recordArray = toArray();
		Arrays.sort(recordArray, comparator);
		records.clear();
		for (Record record : recordArray) {
			records.add(record);
		}
	}

	/**
	 * Returns an array containing all the records.
	 * @return The array of records.
	 */
	public Record[] toArray() { return records.toArray(new Record[records.size()]); }

	/**
	 * Return the iterator.
	 */
	@Override
	public Iterator<Record> iterator() { return records.iterator(); }

	/**
	 * Check if the record set can be sorted. Default is true, overwrite this method
	 * if the record set implementation can not be sorted.
	 * @return A boolean.
	 */
	public boolean isSortable() { return true; }

	/**
	 * To string.
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < Math.min(size(), 500); i++) {
			if (i > 0) {
				b.append("\n");
			}
			b.append(get(i));
		}
		return b.toString();
	}
}
