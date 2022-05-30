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
package com.msasc.lib.util;

import java.io.IOException;
import java.io.Writer;

/**
 * Writer on a string builder.
 * @author Miquel Sas
 */
public class StringBuilderWriter extends Writer {

	/** Underlying string builder. */
	private StringBuilder str;

	/**
	 * Constructor.
	 * @param str The string builder.
	 */
	public StringBuilderWriter(StringBuilder str) {
		super();
		if (str == null) throw new NullPointerException();
		this.str = str;
	}
	/**
	 * Write.
	 */
	public void write(char[] cbuf, int off, int len) throws IOException { str.append(cbuf, off, len); }
	/**
	 * Flush. Does nothing.
	 */
	public void flush() throws IOException {}
	/**
	 * Close. Does nothing.
	 */
	public void close() throws IOException {}
}
