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
import java.util.List;

import com.msasc.lib.util.Numbers;
import com.msasc.lib.util.Strings;

/**
 * A table definition.
 * 
 * @author Miquel Sas
 */
public class Table {

	/** The name of the table. */
	private String name;
	/** The alias. */
	private String alias;

	/** The database schema. */
	private String schema;

	/** The list of fields. */
	private FieldList fields = new FieldList();

	/** The primary key. */
	private Index primaryKey;
	/** The list of secondary indexes. */
	private List<Index> indexes = new ArrayList<>();

	/**
	 * Constructor.
	 */
	public Table() {}

	/**
	 * Add a field to the field list.
	 * @param field The field to add.
	 */
	public void addField(Field field) { fields.addField(new Field(field)); }
	/**
	 * Add an index to the list of secondary indexes.
	 * @param index The index to add
	 */
	public void addIndex(Index index) { indexes.add(index); }

	/**
	 * Check for equality.
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (getClass() != o.getClass()) return false;
		return getNameFrom().equals(((Table) o).getNameFrom());
	}
	/**
	 * Return a proper hash code.
	 */
	@Override
	public int hashCode() { return getNameFrom().hashCode(); }

	/**
	 * Gets the alias.
	 * @return The alias.
	 */
	public String getAlias() { return alias == null ? name : alias; }

	/**
	 * Returns the given fields.
	 * @param alias Field alias.
	 * @return The field.
	 */
	public Field getField(String alias) { return fields.getField(alias); }
	/**
	 * Returns the list of fields.
	 * @return The list of fields.
	 */
	public FieldList getFields() { return fields; }
	/**
	 * Return the list of indexes.
	 * @return The list of indexes.
	 */
	public List<Index> getIndexes() { return indexes; }

	/**
	 * Get the name.
	 * @return The table name
	 */
	public String getName() { return name; }
	/**
	 * Gets the name to use in <code>FROM</code> clause.
	 * @return The appropriate name for a <code>FROM</code> clause.
	 */
	public String getNameFrom() {
		if (alias != null && name != null) return getNameSchema() + " " + alias;
		return getNameSchema();
	}
	/**
	 * Gets the name qualified with the schema.
	 * @return The name qualified with the schema.
	 */
	public String getNameSchema() {
		if (schema != null && name != null) return schema + "." + name;
		return name;
	}

	/**
	 * Returns the primary key index.
	 * @return The primary key index.
	 */
	public Index getPrimaryKey() { setupAndValidate(); return primaryKey; }

	/**
	 * Get the schema.
	 * @return The table schema
	 */
	public String getSchema() { return schema; }

	/**
	 * Returns a view of this table, using the argument index as the order by index.
	 * @return The most simple view.
	 * @param orderBy The order by index.
	 */
	public View getView(Order orderBy) {
		View view = new View();
		view.setMasterTable(this);
		for (int i = 0; i < fields.size(); i++) {
			Field field = new Field(fields.getField(i));
			field.setView(view);
			view.addField(field);
		}
		view.setOrderBy(orderBy);
		view.setName(getName());
		view.setAlias(getAlias());
		return view;
	}

	/**
	 * Sets the alias.
	 * @param alias The table alias.
	 */
	public void setAlias(String alias) { this.alias = alias; }
	/**
	 * Set the name.
	 * @param name The table name
	 */
	public void setName(String name) { this.name = name; }
	/**
	 * Set the schema.
	 * @param schema The schema name.
	 */
	public void setSchema(String schema) { this.schema = schema; }

	/**
	 * Setup internal structures.
	 */
	public void setupAndValidate() {
		fields.setupAndValidate();

		/* Assign the table and validate fields. */
		for (Field field : fields) {
			if (field.isVirtual()) {
				throw new IllegalStateException("Field " + field + " can not be virtual");
			}
			field.setTable(this);
		}

		/* Build primary key. */
		if (!fields.getPrimaryKeyFields().isEmpty()) {
			primaryKey = new Index();
			primaryKey.setTable(this);
			primaryKey.setUnique(true);
			primaryKey.setName(getName() + "_PK");
			primaryKey.setSchema(getSchema());
			for (Field field : fields.getPrimaryKeyFields()) {
				primaryKey.addField(field);
			}
		}

		/* Setup indexes. */
		for (int i = 0; i < indexes.size(); i++) {
			Index index = indexes.get(i);
			if (index == null) throw new IllegalStateException("Null index " + i);
			index.setTable(this);
			if (index.getSchema() == null) {
				index.setSchema(getSchema());
			}
			if (index.getName() == null) {
				String name = getName() + "_SK";
				name += Strings.leftPad(i, Numbers.getDigits(indexes.size()), "0");
				index.setName(name);
			}
		}
	}
}
