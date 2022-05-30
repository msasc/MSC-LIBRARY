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
package com.msasc.lib.util.resources;

import java.util.Locale;

/**
 * Provider of localized string resources.
 * @author Miquel Sas
 */
public abstract class StringRes {
	
	/** Default resources installed when starting a desktop application. */
	private static StringRes res;
	/**
	 * Return the string that corresponds to the search key.
	 * @param key Lookup key.
	 * @return The localized string.
	 */
	public static String get(String key) { return get(key, Locale.getDefault()); }
	/**
	 * Return the string that corresponds to the search key.
	 * @param key Lookup key.
	 * @param locale The preferred locale.
	 * @return The localized string.
	 */
	public static String get(String key, Locale locale) { return res.getString(key, locale); }
	
	/**
	 * Set the default resources.
	 * @param res The resources.
	 */
	public static void setDefault(StringRes res) { StringRes.res = res; }
	/**
	 * Return the string that corresponds to the search key.
	 * @param key Lookup key.
	 * @return The localized string.
	 */
	public String getString(String key) { return getString(key, Locale.getDefault()); }
	/**
	 * Return the string that corresponds to the search key.
	 * @param key Lookup key.
	 * @param locale The preferred locale.
	 * @return The localized string.
	 */
	public abstract String getString(String key, Locale locale);
}
