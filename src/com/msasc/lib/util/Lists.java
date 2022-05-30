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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.msasc.lib.util.iterators.ArrayIterator;

/**
 * Array and list utility functions.
 *
 * @author Miquel Sas
 */
public class Lists {
	/**
	 * Returns a list given the argument array.
	 *
	 * @param <T>   The type.
	 * @param array The array.
	 * @return The list.
	 */
	@SafeVarargs
	public static <T> List<T> asList(T... array) {
		if (array == null) {
			return new ArrayList<>();
		}
		List<T> list = new ArrayList<>(array.length);
		for (T e : array) {
			list.add(e);
		}
		return list;
	}
	/**
	 * Compares two lists of the same size.
	 *
	 * @param <T>        The type to compare.
	 * @param list1      First list.
	 * @param list2      Second list.
	 * @param comparator The comparator.
	 * @return The comparison integer.
	 */
	public static <T> int compare(List<T> list1, List<T> list2, Comparator<T> comparator) {
		if (list1 == null || list2 == null || comparator == null) {
			throw new NullPointerException();
		}
		for (int i = 0; i < list1.size(); i++) {
			int compare = comparator.compare(list1.get(i), list2.get(i));
			if (compare != 0) return compare;
		}
		return 0;
	}
	/**
	 * Check whether two lists are equal.
	 *
	 * @param l1 List 1.
	 * @param l2 List 2.
	 * @return A boolean.
	 */
	public static boolean equals(List<?> l1, List<?> l2) {
		if (l1.size() != l2.size()) return false;
		for (int i = 0; i < l1.size(); i++) {
			if (l1.get(i) == null && l2.get(i) != null) return false;
			if (l1.get(i) != null && l2.get(i) == null) return false;
			if (l1.get(i) == null && l2.get(i) == null) continue;
			if (!l1.get(i).equals(l2.get(i))) return false;
		}
		return true;
	}
	/**
	 * Returns the last element of a list.
	 *
	 * @param <T>  The type.
	 * @param list The list.
	 * @return The last element.
	 */
	public static <T> T getLast(List<T> list) {
		if (list == null || list.isEmpty()) return null;
		return list.get(list.size() - 1);
	}

	/**
	 * Check in the list.
	 *
	 * @param <T>    The type to check.
	 * @param value  The value to check.
	 * @param values The list of values.
	 * @return A boolean.
	 */
	public static <T> boolean in(T value, Collection<T> values) {
		return values.stream().anyMatch((v) -> (v.equals(value)));
	}

	/**
	 * Check in the list.
	 *
	 * @param <T>    The type to check in.
	 * @param value  The value to check.
	 * @param values The list of values.
	 * @return A boolean.
	 */
	@SafeVarargs
	public static <T> boolean in(T value, T... values) {
		for (T v : values) {
			if (v.equals(value)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Remove the last element in the list.
	 *
	 * @param <T>  The type.
	 * @param list The list.
	 * @return The removed element.
	 */
	public static <T> T removeLast(List<T> list) {
		return list.remove(list.size() - 1);
	}
	/**
	 * Return a comma separated list of the values converted to string.
	 * @param <T>    The type.
	 * @param values The list of values.
	 * @return The comma separated list.
	 */
	@SuppressWarnings("unchecked")
	public static <T> String toString(T... values) { return toString(new ArrayIterator<T>(values)); }
	/**
	 * Return a comma separated list of the values converted to string.
	 * @param <T>    The type.
	 * @param values The list of values.
	 * @return The comma separated list.
	 */
	public static <T> String toString(Iterator<T> iterator) {
		StringBuilder b = new StringBuilder();
		boolean comma = false;
		while (iterator.hasNext()) {
			if (comma) b.append(", ");
			b.append(iterator.next());
			comma = true;
		}
		return b.toString();
	}
	public static String[] concat(String[]... strss) {
		int len = 0;
		for (int i = 0; i < strss.length; i++) {
			if (strss[i] != null) {
				len += strss[i].length;
			}
		}
		String[] arr = new String[len];
		int index = 0;
		for (int i = 0; i < strss.length; i++) {
			if (strss[i] != null) {
				for (int j = 0; j < strss[i].length; j++) {
					arr[index++] = strss[i][j];
				}
			}
		}
		return arr;
	}
}
