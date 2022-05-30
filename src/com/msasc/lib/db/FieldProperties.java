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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.msasc.lib.db.util.converters.ValueStringConverter;
import com.msasc.lib.util.Properties;
import com.msasc.lib.util.Strings;

/**
 * Helper to get/set additional properties of the field, like header, label, uppercase, and any
 * visual or layout properties.
 * @author Miquel Sas
 */
public class FieldProperties {

	/** Reserved keys. */
	private static final String[] RESERVED_KEYS = new String[] {
			"display-length", "display-decimals",
			"header", "label", "title",
			"uppercase", "fixed-with", "possible-values",
			"css-style", "string-converter"
	};

	/** Referenced field. */
	private Field field;
	/** Internal properties. */
	private Properties properties = new Properties();

	/**
	 * Constructor.
	 */
	public FieldProperties(Field field) { this.field = field; }

	public void addPossibleValue(String value, String label) { addPossibleValue(new Value(value), label); }
	public void addPossibleValue(Value value, String label) { getPossibleValues().put(value, label); }

	public Value getPossibleValue(int index) {
		List<Value> values = getPossibleValueList();
		if (index >= values.size()) throw new ArrayIndexOutOfBoundsException();
		return values.get(index);
	}
	public String getPossibleValueLabel(Value value) { return getPossibleValues().get(value); }

	public List<Value> getPossibleValueList() { return new ArrayList<>(getPossibleValues().keySet()); }

	public boolean hasPossibleValues() {
		Map<Value, String> map = properties.getMap("possible-values");
		return (map != null && !map.isEmpty());
	}

	public Map<Value, String> getPossibleValues() {
		Map<Value, String> map = properties.getMap("possible-values");
		if (map == null) {
			map = new LinkedHashMap<>();
			properties.put("possible-values", map);
		}
		return map;
	}

	public Integer getDisplayLength() {
		Integer length = properties.getInteger("display-length");
		return (length == null ? field.getLength() : length);
	}
	public void setDisplaylength(int length) { properties.put("display-length", length); }

	public Integer getDisplayDecimals() {
		Integer decimals = properties.getInteger("display-decimals");
		if (decimals == null) decimals = field.getDecimals();
		return (decimals == null ? field.getDecimals() : decimals);
	}
	public void setDisplayDecimals(int decimals) { properties.put("display-decimals", decimals); }

	public String getHeader() { return properties.getString("header"); }
	public void setHeader(String header) { properties.put("header", header); }

	public String getLabel() { return properties.getString("label"); }
	public void setLabel(String label) { properties.put("label", label); }

	public String getTitle() { return properties.getString("title"); }
	public void setTitle(String title) { properties.put("title", title); }

	public boolean isFixedWidth() { return properties.getBoolean("fixed-width", false); }
	public void setFixedWidth(boolean fixedWidth) { properties.put("fixed-width", fixedWidth); }

	public boolean isUppercase() { return properties.getBoolean("uppercase", false); }
	public void setUppercase(boolean uppercase) { properties.put("uppercase", uppercase); }

	public void addStyles(String... styleArray) {

		List<String> styles = new ArrayList<>();

		if (getStyle() != null) styles.addAll(Strings.parseCSS(getStyle()));
		styles.addAll(Strings.parseCSS(styleArray));

		StringBuilder b = new StringBuilder();
		for (String style : styles) {
			b.append(style + "; ");
		}

		setStyle(b.toString());
	}

	public String getStyle() { return properties.getString("css-style"); }
	public void setStyle(String style) { properties.put("css-style", style); }

	public ValueStringConverter getStringConverter() {
		return (ValueStringConverter) properties.getObject("string-converter");
	}
	public void setStringConverter(ValueStringConverter converter) {
		properties.put("string-converter", converter);
	}

	/**
	 * Return the object.
	 * @param key The search key.
	 * @return The object or null.
	 */
	public Object getObject(String key) {
		checkReserved(key);
		return properties.getObject(key);
	}
	
	/**
	 * Fill with the argument properties.
	 * @param properties The field properties.
	 */
	public void putAll(FieldProperties properties) {
		this.properties.putAll(properties.properties);
	}

	/**
	 * Check whether the key is reserved.
	 * @param key The key to check.
	 */
	private void checkReserved(String key) {
		if (Strings.in(key, RESERVED_KEYS)) {
			throw new IllegalArgumentException("Reserved key: " + key);
		}
	}
}
