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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * File utility functions.
 * @author Miquel Sas
 */
public class Files {
	/**
	 * Returns the file extension is present.
	 * @param fileName The file name.
	 * @return The extension part.
	 */
	public static String getFileExtension(String fileName) {
		int index = fileName.lastIndexOf('.');
		if (index == -1) return "";
		return fileName.substring(index + 1);
	}
	/**
	 * Returns the file name if an exension is present.
	 * @param fileName The file name.
	 * @return The name part.
	 */
	public static String getFileName(String fileName) {
		int index = fileName.lastIndexOf('.');
		if (index == -1) return fileName;
		return fileName.substring(0, index);
	}
	/**
	 * Return the file or null, scanning the current name as a file, and then the available path
	 * entries recursively.
	 * @param fileName The file name to search.
	 * @return The file or null.
	 * @throws FileNotFoundException If the file is not found.
	 */
	public static File getFileFromPathEntries(String fileName) throws FileNotFoundException {

		/* Check direct. */
		File file = new File(fileName);
		if (file.exists()) return file;

		/* Check path entries. */
		List<String> entries = getPathEntries();
		for (int i = 0; i < entries.size(); i++) {
			File entry = new File(entries.get(i));
			File check = getFileRecursive(entry, file);
			if (check != null) return check;
		}

		throw new FileNotFoundException(fileName);
	}
	/**
	 * Return the file composed by the parent and child, scanning recursively if the parent is a
	 * directory.
	 * @param parent The parent, file or directory.
	 * @param file   The file to search.
	 * @return The file or null if not found recursively.
	 */
	public static File getFileRecursive(File parent, File file) {
		if (parent.isFile()) {
			if (parent.getAbsolutePath().endsWith(file.getPath())) {
				return parent;
			}
		}
		if (parent.isDirectory()) {
			File[] children = parent.listFiles();
			for (File child : children) {
				File check = getFileRecursive(child, file);
				if (check != null) return check;
			}
		}
		return null;
	}
	/**
	 * Returns the localized file or the default given the locale, the file name and the extension.
	 * @param locale The locale.
	 * @param name   The file name.
	 * @param ext    The file extension.
	 * @return The localized file or null if it does not exist.
	 */
	public static File getLocalizedFile(Locale locale, String fileName) {
		File file = null;
		String name = getFileName(fileName);
		String ext = getFileExtension(fileName);
		if (!ext.isEmpty()) ext = "." + ext;

		/* First attempt: language and country. */
		if (!locale.getCountry().isEmpty()) {
			try {
				fileName = name + "_" + locale.getLanguage() + "_" + locale.getCountry() + ext;
				file = getFileFromPathEntries(fileName);
			} catch (FileNotFoundException ignore) {}
		}
		if (file != null) return file;

		/* Second attempt: language only. */
		if (!locale.getLanguage().isEmpty()) {
			try {
				fileName = name + "_" + locale.getLanguage() + ext;
				file = getFileFromPathEntries(fileName);
			} catch (FileNotFoundException ignore) {}
		}
		if (file != null) return file;

		/* Third attempt: no locale reference. */
		try {
			fileName = name + ext;
			file = getFileFromPathEntries(fileName);
		} catch (FileNotFoundException ignore) {}
		if (file != null) return file;

		/* Not found at all. */
		return null;
	}
	/**
	 * Returns the list of path entries parsing the class path string.
	 * @param classPath The class path.
	 * @return A list of of path entries.
	 */
	public static List<String> getPathEntries() {
		String classPath = System.getProperty("java.class.path");
		String pathSeparator = System.getProperty("path.separator");
		StringTokenizer tokenizer = new StringTokenizer(classPath, pathSeparator);
		List<String> entries = new ArrayList<>();
		while (tokenizer.hasMoreTokens()) { entries.add(tokenizer.nextToken()); }
		return entries;
	}
	/**
	 * Gets the properties by loading the file.
	 * @param file The file.
	 * @return The properties.
	 * @throws IOException If an IO error occurs.
	 */
	public static Properties getProperties(File file) throws IOException {
		boolean xml = getFileExtension(file.getName()).toLowerCase().equals("xml");
		FileInputStream fileIn = new FileInputStream(file);
		BufferedInputStream buffer = new BufferedInputStream(fileIn, 4096);
		Properties properties = getProperties(buffer, xml);
		buffer.close();
		fileIn.close();
		return properties;
	}
	/**
	 * Gets the properties from the input stream.
	 * @param stream The input stream.
	 * @param xml    A boolean that indicates if the input stream has an xml format
	 * @return The properties.
	 * @throws IOException If an IO error occurs.
	 */
	public static Properties getProperties(InputStream stream, boolean xml) throws IOException {
		Properties properties = new Properties();
		if (xml) properties.loadFromXML(stream);
		else properties.load(stream);
		return properties;
	}
}
