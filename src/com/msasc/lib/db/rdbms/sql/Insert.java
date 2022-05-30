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
package com.msasc.lib.db.rdbms.sql;

import java.util.ArrayList;
import java.util.List;

import com.msasc.lib.db.Field;
import com.msasc.lib.db.Table;
import com.msasc.lib.db.Value;
import com.msasc.lib.db.rdbms.DBEngine;
import com.msasc.lib.db.rdbms.SQL;
import com.msasc.lib.util.Strings;

/**
 * Builder of an insert statement.
 * @author Miquel Sas
 */
public class Insert extends SQL {

	/** Into table. */
	private String into;
	/** List of fields. */
	private List<String> fields = new ArrayList<>();
	/** List of values. */
	private List<String> values = new ArrayList<>();

	/**
	 * Constructor.
	 * @param db The underlying database engine.
	 */
	public Insert(DBEngine db) { super(db); }

	/**
	 * Set the INTO part, normally a SCHEMA.TABLE.
	 * @param into The into part.
	 */
	public void into(String into) { this.into = into; }
	/**
	 * Set the INTO part.
	 * @param table The table to insert into.
	 */
	public void into(Table table) { this.into = table.getNameSchema(); }

	/**
	 * Add the list of fields.
	 * @param field The list of fields to add.
	 */
	public void fields(Field... fields) {
		for (Field field : fields) { this.fields.add(field.getName()); }
	}
	/**
	 * Add the list of fields.
	 * @param field The list of fields to add.
	 */
	public void fields(List<Field> fields) {
		for (Field field : fields) { this.fields.add(field.getName()); }
	}
	/**
	 * Add the list of fields.
	 * @param field The list of fields to add.
	 */
	public void fields(String... fields) {
		for (String field : fields) { this.fields.add(field); }
	}

	/**
	 * Add the list of values.
	 * @param values The list of values to add.
	 */
	public void values(List<Value> values) {
		for (Value value : values) { this.values.add(db.toStringSQL(value)); }
	}
	/**
	 * Add the list of values.
	 * @param values The list of values to add.
	 */
	public void values(String... values) {
		for (String value : values) { this.values.add(value); }
	}
	/**
	 * Add the list of values.
	 * @param values The list of values to add.
	 */
	public void values(Value... values) {
		for (Value value : values) { this.values.add(db.toStringSQL(value)); }
	}
	
	/**
	 * Clear the list of fields.
	 */
	public void clearFields() { fields.clear(); }
	/**
	 * Clear the list of values.
	 */
	public void clearValues() { values.clear(); }

	/**
	 * Returns the query optionally formatted to be readable.
	 * @param formatted A boolean indicating whether the query should be formatted.
	 * @return The query.
	 */
	@Override
	public String toSQL(boolean formatted) {
		
		boolean illegal_state = (into == null);
		illegal_state |= fields.isEmpty();
		illegal_state |= values.isEmpty();
		illegal_state |= (fields.size() != values.size());
		if (illegal_state) {
			throw new IllegalStateException("Invalid INSERT data.");
		}
		
		StringBuilder sql = new StringBuilder();
		if (formatted) sql.append(Strings.blank(PAD - "INSERT INTO ".length()));
		sql.append("INSERT INTO ");
		sql.append(into);
		if (formatted) sql.append("\n" + Strings.blank(PAD - 1));
		sql.append(" (");
		for (int i = 0; i < fields.size(); i++) {
			if (i > 0) sql.append(", ");
			if (formatted) sql.append("\n" + Strings.blank(PAD));
			sql.append(fields.get(i));
		}
		if (formatted) sql.append("\n" + Strings.blank(PAD));
		sql.append(")");
		if (formatted) sql.append("\n" + Strings.blank(PAD - " VALUES ".length()));
		sql.append(" VALUES ");
		sql.append("(");
		for (int i = 0; i < values.size(); i++) {
			if (i > 0) sql.append(", ");
			if (formatted) sql.append("\n" + Strings.blank(PAD));
			sql.append(values.get(i));
		}
		if (formatted) sql.append("\n" + Strings.blank(PAD));
		sql.append(")");
		
		return sql.toString();
	}
}
