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

import java.util.Comparator;
import java.util.Iterator;

/**
 * @author Miquel Sas
 */
public class RecordView extends RecordSet {

	/**
	 * 
	 */
	public RecordView() {}

	/**
	 * @param fields
	 */
	public RecordView(FieldList fields) { super(fields); }

	@Override
	public void add(int index, Record record) { // TODO Auto-generated method stub
	}

	@Override
	public void add(Record record) {}

	@Override
	public void clear() {}

	@Override
	protected Record getRecord(int index) { return null; }

	@Override
	public RecordSet getCopy() { return null; }

	@Override
	public int getInsertIndex(Record record, Order order) { return 0; }

	@Override
	public boolean isEmpty() { return false; }

	@Override
	public Iterator<Record> iterator() { return null; }

	@Override
	public Record remove(int index) { return null; }

	@Override
	public void set(int index, Record record) {}

	@Override
	public int size() { return 0; }

	@Override
	public void sort(Comparator<Record> comparator) {}

	@Override
	public Record[] toArray() { return null; }
}
