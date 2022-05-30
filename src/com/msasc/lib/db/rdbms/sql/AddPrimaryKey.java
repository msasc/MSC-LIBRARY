/*
 * Copyright (C) 2018 Miquel Sas
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.msasc.lib.db.rdbms.sql;

import com.msasc.lib.db.Index;
import com.msasc.lib.db.Order;
import com.msasc.lib.db.Table;
import com.msasc.lib.db.rdbms.DBEngine;
import com.msasc.lib.db.rdbms.SQL;

/**
 * An ALTER TABLE ADD PRIMARY KEY builder.
 * @author Miquel Sas
 */
public class AddPrimaryKey extends SQL {

	/** The table to alter. */
	private Table table;

	/**
	 * Constructor.
	 * @param db    The database engine.
	 * @param table The table to create.
	 */
	public AddPrimaryKey(DBEngine db, Table table) {
		super(db);
		this.table = table;
	}

	/**
	 * Returns the query optionally formatted to be readable.
	 * @param formatted A boolean indicating whether the query should be formatted.
	 * @return The query.
	 */
	@Override
	public String toSQL(boolean formatted) {
		if (table == null) {
			throw new IllegalStateException("Malformed ADD PRIMARY KEY query: table is null");
		}
		if (table.getPrimaryKey() == null) {
			throw new IllegalStateException("Malformed ADD PRIMARY KEY query: table has no primary key");
		}

		StringBuilder sql = new StringBuilder(256);
		Index primaryKey = table.getPrimaryKey();
		sql.append("ALTER TABLE ");
		sql.append(table.getNameSchema());
		sql.append(" ADD PRIMARY KEY (");
		boolean first = true;
		for (Order.Segment seg : primaryKey.getSegments()) {
			if (!first) sql.append(", ");
			sql.append(seg.getField().getName());
			first = false;
		}
		sql.append(") ");

		return sql.toString();
	}
}
