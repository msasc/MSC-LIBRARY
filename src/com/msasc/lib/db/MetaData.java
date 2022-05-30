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

import java.util.Locale;

/**
 * Interface to access library metadata stored in a persistent format.
 * @author Miquel Sas
 */
public abstract class MetaData {
	/**
	 * Return the field definition given an unique key, perhaps of the form
	 * SCHEMA_NAME.TABLE_NAME.FIELD_NAME.
	 * @param key
	 * @return
	 */
	public abstract Field getField(String key);
	/**
	 * Return the field.
	 * @param key  The key.
	 * @param name The field name.
	 * @return The field.
	 */
	public Field getField(String key, String name) { return getField(key, name, name); }
	/**
	 * Return the field.
	 * @param key   The key.
	 * @param name  The field name.
	 * @param alias The field alias.
	 * @return The field.
	 */
	public Field getField(String key, String name, String alias) {
		Field field = getField(key);
		field.setName(name);
		field.setAlias(alias);
		return field;
	}
	/**
	 * Return the field header, optionally localized.
	 * @param key    The search key.
	 * @param locale The locale.
	 * @return The field header.
	 */
	public abstract String getFieldHeader(String key, Locale locale);
	/**
	 * Return the field label, optionally localized.
	 * @param key    The search key.
	 * @param locale The locale.
	 * @return The field label.
	 */
	public abstract String getFieldLabel(String key, Locale locale);
	/**
	 * Return the field description, optionally localized.
	 * @param key    The search key.
	 * @param locale The locale.
	 * @return The field description.
	 */
	public abstract String getFieldDescription(String key, Locale locale);
	/**
	 * Return a table definition given an unique key, perhaps of the form SCHEMA_NAME.TABLE_NAME, or
	 * even CATALOG_NAME.SCHEMA_NAME.TABLE_NAME.
	 * @param key The string key.
	 * @return The table definition.
	 */
	public abstract Table getTable(String key);
	/**
	 * Return the table.
	 * @param key    The string key.
	 * @param schema The schema.
	 * @param name   The name.
	 * @return The table definition.
	 */
	public Table getTable(String key, String schema, String name) { return getTable(key, schema, name, name); }
	/**
	 * Return the table.
	 * @param key    The string key.
	 * @param schema The schema.
	 * @param name   The name.
	 * @param alias  The alias.
	 * @return The table definition.
	 */
	public Table getTable(String key, String schema, String name, String alias) {
		Table table = getTable(key);
		table.setSchema(schema);
		table.setName(name);
		table.setAlias(alias);
		return table;
	}
	/**
	 * Return the table title or short description, optionally localized.
	 * @param key    The search key.
	 * @param locale The locale.
	 * @return The table title.
	 */
	public abstract String getTableTitle(String key, Locale locale);
	/**
	 * Return the table description, optionally localized.
	 * @param key    The search key.
	 * @param locale The locale.
	 * @return The table description.
	 */
	public abstract String getTableDescription(String key, Locale locale);
}
