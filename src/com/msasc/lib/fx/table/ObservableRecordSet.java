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
package com.msasc.lib.fx.table;

import com.msasc.lib.db.RecordSet;

import javafx.collections.ObservableListBase;
import com.msasc.lib.db.Record;;

/**
 * Observable recordset.
 * @author Miquel Sas
 */
public class ObservableRecordSet extends ObservableListBase<Record> {

	/** Underlying recordset. */
	private RecordSet rs;

	/**
	 * Constructor.
	 * @param rs The underlying recordset.
	 */
	public ObservableRecordSet(RecordSet rs) { this.rs = rs; }
	/**
	 * Return the record at index.
	 */
	@Override
	public Record get(int index) { return rs.get(index); }
	/**
	 * Return the size.
	 */
	@Override
	public int size() { return rs.size(); }

}
