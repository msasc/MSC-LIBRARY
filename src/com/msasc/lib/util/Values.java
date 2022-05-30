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

import com.msasc.lib.db.Value;

/**
 * Value utilities.
 * 
 * @author Miquel Sas
 */
public class Values {

	/**
	 * Compare for order.
	 * 
	 * @param a1 Value array 1.
	 * @param a2 Value array 2.
	 * @return An integer -1, 0 or 1.
	 */
	public static int compare(Value[] a1, Value[] a2) {
		if (a1 == null && a2 == null)
			return 0;
		if (a1 != null && a2 == null)
			return 1;
		if (a1 == null && a2 != null)
			return -1;
		int size = Math.max(a1.length, a2.length);
		for (int i = 0; i < size; i++) {
			if (i >= a1.length)
				return -1;
			if (i >= a2.length)
				return 1;
			int compare = a1[i].compareTo(a2[i]);
			if (compare != 0)
				return compare;
		}
		return 0;
	}
}
