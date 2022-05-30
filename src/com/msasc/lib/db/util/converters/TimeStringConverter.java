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
package com.msasc.lib.db.util.converters;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.msasc.lib.db.Value;
import com.msasc.lib.util.Strings;

/**
 * String converter for time values expressed as a long.
 * @author Miquel Sas
 */
public class TimeStringConverter implements ValueStringConverter {

	public static final String PATTERN_MILLIS = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String PATTERN_SECOND = "yyyy-MM-dd HH:mm:ss";
	public static final String PATTERN_MINUTE = "yyyy-MM-dd HH:mm";
	public static final String PATTERN_HOUR = "yyyy-MM-dd HH";
	public static final String PATTERN_DAY = "yyyy-MM-dd";
	public static final String PATTERN_WEEK = "yyyy-MM-dd";
	public static final String PATTERN_MONTH = "yyyy-MM";
	public static final String PATTERN_YEAR = "yyyy";
	
	/** Date format. */
	private SimpleDateFormat format;

	/**
	 * Constructor.
	 * @param pattern The pattern.
	 */
	public TimeStringConverter(String pattern) {
		if (pattern == null) throw new NullPointerException();
		String[] patterns = new String[] {
				PATTERN_MILLIS,
				PATTERN_SECOND,
				PATTERN_MINUTE,
				PATTERN_HOUR,
				PATTERN_DAY,
				PATTERN_WEEK,
				PATTERN_MONTH,
				PATTERN_YEAR
		};
		if (!Strings.in(pattern,  patterns)) {
			throw new IllegalArgumentException("Invalid pattern " + pattern);
		}
		format = new SimpleDateFormat(pattern);
	}
	/**
	 * Return the value given the string.
	 */
	@Override
	public Value fromString(String string) {
		long time = -1;
		try {
			time = format.parse(string).getTime();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return new Value(time);
	}
	/**
	 * Return the string given the value.
	 */
	@Override
	public String toString(Value value) {
		return format.format(new Timestamp(value.getLong()));
	}
}
