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
package com.msasc.lib.json;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Iterator;

import com.msasc.lib.util.Strings;

/**
 * A writer of JSON objects.
 * 
 * @author Miquel Sas
 */
public class JSONWriter {

	/** Tab size, default is 3. */
	private int tabSize = 3;
	/** Tab level. */
	private int tabLevel = 0;
	/** Maximum tab level. */
	private int maxTabLevel = Integer.MAX_VALUE;
	/** A boolean that indicates whether the format should be readable. */
	private boolean readable = false;

	/**
	 * Constructor of a writer with non readable format.
	 */
	public JSONWriter() {
	}

	/**
	 * Constructor assigning indicating whether the format should be readable.
	 * 
	 * @param readable A boolean that indicates whether the format should be
	 *                 readable.
	 */
	public JSONWriter(boolean readable) {
		this.readable = readable;
	}

	/**
	 * Constructor assigning tab size. By default, when the tab size is assigned,
	 * the format is readable.
	 * 
	 * @param tabSize The tab size.
	 */
	public JSONWriter(int tabSize) {
		this.tabSize = tabSize;
		this.readable = true;
	}

	/**
	 * Set the maximum tab level.
	 * 
	 * @param maxTabLevel The maximum tab level.
	 */
	public void setMaxTabLevel(int maxTabLevel) {
		this.maxTabLevel = maxTabLevel;
	}

	/**
	 * Write an JSON object to the writer.
	 * 
	 * @param w The writer.
	 * @param o The JSONObject.
	 * @throws IOException If an error occurs.
	 */
	public void write(Writer w, JSONObject o) throws IOException {
		boolean readable = this.readable && tabLevel < maxTabLevel;
		w.write("{");
		if (readable) {
			tabLevel++;
		}
		Iterator<String> i = o.keys().iterator();
		while (i.hasNext()) {
			if (readable) {
				w.write("\n" + Strings.repeat(" ", tabLevel * tabSize));
			}
			String key = i.next();
			w.write("\"" + key + "\":");
			JSONEntry e = o.get(key);
			write(w, e);
			if (i.hasNext()) w.write(",");
		}
		if (readable) {
			tabLevel--;
			w.write("\n" + Strings.repeat(" ", tabLevel * tabSize));
		}
		w.write("}");

	}

	/**
	 * Write an JSON array to the writer.
	 * 
	 * @param w The writer.
	 * @param a The JSONArray.
	 * @throws IOException IOException If an error occurs.
	 */
	public void write(Writer w, JSONArray a) throws IOException {
		boolean readable = this.readable && tabLevel < maxTabLevel;
		w.write("[");
		if (readable) {
			tabLevel++;
		}
		Iterator<JSONEntry> i = a.iterator();
		while (i.hasNext()) {
			if (readable) {
				w.write("\n" + Strings.repeat(" ", tabLevel * tabSize));
			}
			write(w, i.next());
			if (i.hasNext()) w.write(",");
		}
		if (readable) {
			tabLevel--;
			w.write("\n" + Strings.repeat(" ", tabLevel * tabSize));
		}
		w.write("]");
	}

	/**
	 * Write an JSON entry to the writer.
	 * 
	 * @param w The writer.
	 * @param e The JSONEntry.
	 * @throws IOException IOException If an error occurs.
	 */
	public void write(Writer w, JSONEntry e) throws IOException {

		JSONTypes type = e.getType();
		Object value = e.getValue();

		/* Null value. */
		if (value == null) {
			w.write("null");
			return;
		}

		/* Standard types. */
		if (type == JSONTypes.OBJECT) {
			write(w, (JSONObject) value);
			return;
		}
		if (type == JSONTypes.ARRAY) {
			write(w, (JSONArray) value);
			return;
		}
		if (type == JSONTypes.STRING) {
			w.write("\"" + value + "\"");
			return;
		}
		if (type == JSONTypes.NUMBER) {
			w.write(((BigDecimal) value).toPlainString());
			return;
		}
		if (type == JSONTypes.BOOLEAN) {
			w.write(((Boolean) value).toString());
			return;
		}

		/* Extended type BINARY. */
		if (type == JSONTypes.BINARY) {
			w.write("{\"" + type.getKey() + "\":\"");
			byte[] bytes = (byte[]) value;
			for (byte b : bytes) {
				w.write(Strings.leftPad(Integer.toString(b, 16), 2, "0"));
			}
			w.write("\"}");
			return;
		}

		/* Extended types DATE, TIME or TIMESTAMP. */
		if (type == JSONTypes.DATE || type == JSONTypes.TIME || type == JSONTypes.TIMESTAMP) {
			w.write("{\"" + type.getKey() + "\":");
			w.write("\"" + value.toString() + "\"");
			w.write("}");
			return;
		}

		/* Extended type decimal. */
		if (type == JSONTypes.DECIMAL) {
			w.write("{\"" + type.getKey() + "\":");
			w.write("\"" + ((BigDecimal) value).toPlainString() + "\"");
			w.write("}");
			return;
		}
		/* Extended type double. */
		if (type == JSONTypes.DOUBLE) {
			w.write("{\"" + type.getKey() + "\":");
			w.write("\"" + BigDecimal.valueOf((Double) value).toPlainString() + "\"");
			w.write("}");
			return;
		}
		/* Extended type integer. */
		if (type == JSONTypes.INTEGER) {
			w.write("{\"" + type.getKey() + "\":");
			w.write("\"" + BigDecimal.valueOf((Integer) value).toPlainString() + "\"");
			w.write("}");
			return;
		}
		/* Extended type double. */
		if (type == JSONTypes.LONG) {
			w.write("{\"" + type.getKey() + "\":");
			w.write("\"" + BigDecimal.valueOf((Long) value).toPlainString() + "\"");
			w.write("}");
			return;
		}
	}

	/**
	 * Returns the JSON array as a string.
	 * 
	 * @param arr The JSON array.
	 * @return The string representation.
	 */
	public String toString(JSONArray arr) {
		try {
			StringWriter w = new StringWriter();
			write(w, arr);
			return w.toString();
		} catch (IOException exc) {
			exc.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns the JSON object as a string.
	 * 
	 * @param obj The JSON object.
	 * @return The string representation.
	 */
	public String toString(JSONObject obj) {
		try {
			StringWriter w = new StringWriter();
			write(w, obj);
			return w.toString();
		} catch (IOException exc) {
			exc.printStackTrace();
		}
		return null;
	}
}
