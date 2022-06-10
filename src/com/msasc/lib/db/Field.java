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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

import com.msasc.lib.json.JSONObject;
import com.msasc.lib.util.resources.StringRes;

/**
 * Field metadata definition. These are the essential table properties.
 * 
 * @author Miquel Sas
 */
public class Field implements Comparable<Field> {

	/** Standard field alias (key) row number. */
	public final static String ALIAS_ROW_NUMBER = "ROW_NUM";

	/**
	 * Helper to rapidly create fields.
	 * @param name        Field name.
	 * @param description Description.
	 * @param type        Type.
	 * @return The field definition.
	 */
	public static Field create(String name, Types type) {
		return create(name, type, null, null, false);
	}
	/**
	 * Helper to rapidly create fields.
	 * @param name        Field name.
	 * @param description Description.
	 * @param type        Type.
	 * @param length      Length.
	 * @return The field definition.
	 */
	public static Field create(String name, Types type, Integer length) {
		return create(name, type, length, null, false);
	}
	/**
	 * Helper to rapidly create fields.
	 * @param name        Field name.
	 * @param description Description.
	 * @param type        Type.
	 * @param length      Length.
	 * @param decimals    Decimals.
	 * @return The field definition.
	 */
	public static Field create(String name, Types type, Integer length, Integer decimals) {
		return create(name, type, length, decimals, false);
	}
	/**
	 * Helper to rapidly create fields.
	 * @param name        Field name.
	 * @param description Description.
	 * @param type        Type.
	 * @param length      Length.
	 * @param decimals    Decimals.
	 * @param primaryKey  primary key indicator.
	 * @return The field definition.
	 */
	public static Field create(String name, Types type, Integer length, Integer decimals, boolean primaryKey) {
		Field field = new Field();
		field.setName(name);
		field.setAlias(name);
		field.setType(type);
		field.setLength(length);
		field.setDecimals(decimals);
		field.setPrimaryKey(primaryKey);
		return field;
	}
	/**
	 * Creates the default row number field..
	 * @return The field.
	 */
	public static Field createRowNum() { return createRowNum(Locale.getDefault()); }
	/**
	 * Creates the default row number field..
	 * @param locale The required locale for header and label.
	 * @return The field.
	 */
	public static Field createRowNum(Locale locale) {
		Field field = create(ALIAS_ROW_NUMBER, Types.LONG);
		field.setPersistent(false);
		field.getProperties().setHeader(StringRes.get("fieldRowNumHeader", locale));
		field.getProperties().setLabel(StringRes.get("fieldRowNumLabel", locale));
		field.getProperties().setTitle(StringRes.get("fieldRowNumTitle", locale));
		return field;
	}

	/** The name or key used to access the field within a document or row. */
	private String name;
	/** The alias by which the field is accessed across documents and schemas. */
	private String alias;
	/** The type. */
	private Types type;
	/** Length if applicable or null. This is a display or validation length. */
	private Integer length;
	/** The number of decimal places if applicable or null. */
	private Integer decimals;

	/** A flag that indicates whether this field is persistent. */
	private boolean persistent = true;
	/** A flag that indicates whether this field is a primary key field. */
	private boolean primaryKey = false;
	/** A flag that indicates whether this field is nullable. */
	private boolean nullable = true;
	/** A supported database function if the column is virtual or calculated. */
	private String function;

	/** Optional default create value. */
	private Value defaultCreateValue;

	/** Optional parent table. */
	private Table table;
	/** Optional parent view. */
	private View view;

	/** Additional and optional properties. */
	private FieldProperties properties;

	/**
	 * Constructor.
	 */
	public Field() { this.properties = new FieldProperties(this); }
	/**
	 * Copy constructor.
	 * @param field The source field.
	 */
	public Field(Field field) {
		this.name = field.name;
		this.alias = field.alias;
		this.length = field.length;
		this.decimals = field.decimals;
		this.type = field.type;
		this.persistent = field.persistent;
		this.primaryKey = field.primaryKey;
		this.function = field.function;
		this.table = field.table;
		this.view = field.view;
		this.properties = new FieldProperties(this);
		this.properties.putAll(field.getProperties());
	}
	/**
	 * Compare for order.
	 */
	@Override
	public int compareTo(Field field) {
		if (getAlias().equals(field.getAlias())) {
			if (getType().equals(field.getType())) {
				if (getLength() == field.getLength()) {
					if (getDecimals() == field.getDecimals()) {
						return 0;
					}
				}
			}
		}
		return getAlias().compareTo(field.getAlias());
	}
	/**
	 * Check equality.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Field) {
			Field field = (Field) obj;
			if (compareTo(field) == 0) {
				if (table != null && field.table != null) {
					return table.equals(field.table);
				} else {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns the alias.
	 * @return The alias.
	 */
	public String getAlias() { return alias == null ? name : alias; }
	/**
	 * Returns the number of decimal places if applicable, otherwise null.
	 * @return The decimals.
	 */
	public Integer getDecimals() { return decimals; }

	/**
	 * Return the default create value if any.
	 * @return The default create value or null.
	 */
	public Value getDefaultCreateValue() { return defaultCreateValue; }
	/**
	 * Returns the default value for this field.
	 * @return The default value.
	 */
	public Value getDefaultValue() {
		switch (type) {
		case BOOLEAN:
			return new Value(false);
		case DECIMAL:
			new Value(new BigDecimal(0).setScale(getDecimals(), RoundingMode.HALF_UP));
		case DOUBLE:
			return new Value((double) 0);
		case INTEGER:
			return new Value((int) 0);
		case LONG:
			return new Value((long) 0);
		case DATE:
			return new Value((LocalDate) null);
		case TIME:
			return new Value((LocalTime) null);
		case TIMESTAMP:
			return new Value((LocalDateTime) null);
		case STRING:
			return new Value("");
		case BINARY:
			return new Value(new byte[0]);
		case ARRAY:
			return new Value(new Value[0]);
		case JSONOBJECT:
			return new Value(new JSONObject());
		}
		throw new IllegalStateException("Never should come here");
	}
	/**
	 * Gets the function or formula.
	 * @return The function.
	 */
	public String getFunction() { return function; }
	/**
	 * Returns the length if applicable, otherwise null.
	 * @return The length.
	 */
	public Integer getLength() { return length; }
	/**
	 * Returns the name.
	 * @return The name.
	 */
	public String getName() { return name; }
	/**
	 * Returns the name to use in a <i>GROUP BY</i> clause of a <i>SELECT</i> query.
	 * @return The name.
	 */
	public String getNameGroupBy() { return getNameSelect(); }
	/**
	 * Returns the name to use in an <i>ORDER BY</i> clause of a select query.
	 * @return The name.
	 */
	public String getNameOrderBy() { return getNameSelect(); }
	/**
	 * Returns the name of the field in the database, qualified with the parent
	 * table or view alias if it exists.
	 * @return The name.
	 */
	public String getNameParent() {
		StringBuilder name = new StringBuilder();
		if (table != null) {
			name.append(table.getAlias());
			name.append(".");
		}
		name.append(getName());
		return name.toString();
	}
	/**
	 * Returns the name to use in an <i>SELECT</i> clause of a select query.
	 * @return The name.
	 */
	public String getNameSelect() {
		StringBuilder name = new StringBuilder();
		if (isVirtual()) {
			name.append("(");
			name.append(getFunction());
			name.append(")");
		} else {
			name.append(getNameParent());
		}
		return name.toString();
	}
	/**
	 * Give access to additional and optional properties.
	 * @return The properties container.
	 */
	public FieldProperties getProperties() { return properties; }
	/**
	 * Return this field parent table.
	 * @return The parent table.
	 */
	public Table getTable() { return table; }
	/**
	 * Returns the type.
	 * @return The type.
	 */
	public Types getType() { return type; }
	/**
	 * Return this field parent view.
	 * @return The parent view.
	 */
	public View getView() { return view; }

	/**
	 * Return a boolean indicating whether the type an ARRAY.
	 * @return A boolean.
	 */
	public boolean isArray() { return type.isArray(); }
	/**
	 * Return a boolean indicating whether the type is BINARY.
	 * @return A boolean.
	 */
	public boolean isBinary() { return type.isBinary(); }
	/**
	 * Return a boolean indicating whether the type is BOOLEAN.
	 * @return A boolean.
	 */
	public boolean isBoolean() { return type.isBoolean(); }
	/**
	 * Return a boolean indicating whether the type is DATE.
	 * @return A boolean.
	 */
	public boolean isDate() { return type.isDate(); }
	/**
	 * Return a boolean indicating whether the type is DECIMAL.
	 * @return A boolean.
	 */
	public boolean isDecimal() { return type.isDecimal(); }
	/**
	 * Return a boolean indicating whether the type is DOUBLE.
	 * @return A boolean.
	 */
	public boolean isDouble() { return type.isDouble(); }
	/**
	 * Check if this field is a foreign field, that is, belongs to a foreign table
	 * in the list of relations of the parent view.
	 * @return A boolean that indicates if this field is a foreign field
	 */
	public boolean isForeign() {
		if (getTable() == null) return false;
		if (getView() == null) return false;
		if (getView().getMasterTable().equals(getTable())) return false;
		List<Relation> relations = getView().getRelations();
		for (Relation relation : relations) {
			if (relation.getForeignTable().equals(getTable())) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Return a boolean indicating whether the type is INTEGER.
	 * @return A boolean.
	 */
	public boolean isInteger() { return type.isInteger(); }
	/**
	 * Return a boolean indicating whether the type is JSONOBJECT.
	 * @return A boolean.
	 */
	public boolean isJSONObject() { return type.isJSONObject(); }
	/**
	 * Check if this field is a local field. Has no parent table or belongs to the
	 * parent table.
	 * @return A boolean that indicates if this field is a local field.
	 */
	public boolean isLocal() { return !isForeign(); }
	/**
	 * Return a boolean indicating whether the type is LONG.
	 * @return A boolean.
	 */
	public boolean isLong() { return type.isLong(); }
	/**
	 * Check whether this field is nullable.
	 * @return A boolean.
	 */
	public boolean isNullable() { return nullable; }
	/**
	 * Return a boolean indicating whether the type is a number Types.
	 * @return A boolean.
	 */
	public boolean isNumber() { return type.isNumber(); }
	/**
	 * Check whether this field is persistent.
	 * @return A boolean.
	 */
	public boolean isPersistent() { return persistent; }
	/**
	 * Check whether this field is a primary key field.
	 * @return A boolean
	 */
	public boolean isPrimaryKey() { return primaryKey; }
	/**
	 * Return a boolean indicating whether the type is STRING.
	 * @return A boolean.
	 */
	public boolean isString() { return type.isString(); }
	/**
	 * Return a boolean indicating whether the type is TIME.
	 * @return A boolean.
	 */
	public boolean isTime() { return type.isTime(); }
	/**
	 * Return a boolean indicating whether the type is TIMESTAMP.
	 * @return A boolean.
	 */
	public boolean isTimestamp() { return type.isTimestamp(); }
	/**
	 * Check if this column is virtual. A column is virtual is it has a function.
	 * @return A boolean.
	 */
	public boolean isVirtual() { return (getFunction() != null); }

	/**
	 * Set the alias.
	 * @param alias The alias.
	 */
	public void setAlias(String alias) { this.alias = alias; }
	/**
	 * Set the decimal places, only valid for decimal fields.
	 * @param decimals The field decimals.
	 */
	public void setDecimals(Integer decimals) { this.decimals = decimals; }
	/**
	 * Set the default create value.
	 * @param value The default create value.
	 */
	public void setDefaultCreateValue(Value value) {
		if (value != null && value.getType() != getType()) {
			throw new IllegalArgumentException("Value type must match field type");
		}
		this.defaultCreateValue = value;
	}
	/**
	 * Sets the function or formula.
	 * @param function The function.
	 */
	public void setFunction(String function) {
		this.function = function;
		if (function != null) this.persistent = false;
	}
	/**
	 * Set the length.
	 * @param length The length.
	 */
	public void setLength(Integer length) { this.length = length; }
	/**
	 * Set the name.
	 * @param name The name.
	 */
	public void setName(String name) { this.name = name; }
	/**
	 * Sets whether this field is nullable.
	 * @param nullable A boolean.
	 */
	public void setNullable(boolean nullable) {
		if (nullable && isPrimaryKey()) {
			throw new IllegalArgumentException("Primary key fields can not be nullable");
		}
		this.nullable = nullable;
	}
	/**
	 * Sets whether this field is persistent.
	 * @param persistent A boolean.
	 */
	public void setPersistent(boolean persistent) { this.persistent = persistent; }
	/**
	 * Set whether this field is a primary key field.
	 * @param primaryKey A boolean.
	 */
	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
		if (primaryKey) this.persistent = true;
		if (primaryKey) this.nullable = false;
	}
	/**
	 * Set this field parent table.
	 * @param table The parent table.
	 */
	public void setTable(Table table) { this.table = table; }
	/**
	 * Set the type.
	 * @param type The type.
	 */
	public void setType(Types type) { this.type = type; }

	/**
	 * Set this field parent view.
	 * @param view The parent view.
	 */
	public void setView(View view) { this.view = view; }
	/**
	 * Return a string representation.
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Field: ");
		b.append(getAlias());
		b.append(", ");
		b.append(getType());
		b.append(", ");
		b.append(getLength());
		b.append(", ");
		b.append(getDecimals());
		return b.toString();
	}
}
