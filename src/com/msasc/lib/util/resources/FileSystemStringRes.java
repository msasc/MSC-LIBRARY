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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

import com.msasc.lib.util.Files;
import com.msasc.lib.util.Lists;

/**
 * File system string resources provider.
 * @author Miquel Sas
 */
public class FileSystemStringRes extends StringRes {

	/** Map with properties per locale. */
	private Map<Locale, Properties> map = new HashMap<>();
	/** List of string resource bundles. */
	private List<String> bundles = new ArrayList<>();
	/** Concurrent lock. */
	private ReentrantLock lock = new ReentrantLock();

	/**
	 * Constructor.
	 * @param bundles The list of reference bundles, without locale suffix.
	 */
	public FileSystemStringRes(String... bundles) { this.bundles.addAll(Lists.asList(bundles)); }

	private Properties loadBundles(Properties properties, Locale locale) throws IOException, FileNotFoundException {
		for (String bundle : bundles) {
			File file = Files.getLocalizedFile(locale, bundle);
			if (file == null) throw new FileNotFoundException(bundle);
			Properties fileProperties = Files.getProperties(file);
			properties.putAll(fileProperties);
		}
		return properties;
	}
	
	/**
	 * Returns the string that corresponds to the search key for the given locale.
	 */
	@Override
	public String getString(String key, Locale locale) {
		try {
			lock.lock();
			Properties properties = map.get(locale);
			if (properties == null) {
				properties = new Properties();
				try {
					loadBundles(properties, locale);
				} catch (Exception exc) {
					exc.printStackTrace();
				}
				map.put(locale, properties);
			}
			String str = properties.getProperty(key);
			return (str == null ? "[" + key + "]" : str);
		} finally {
			lock.unlock();
		}
	}
}
