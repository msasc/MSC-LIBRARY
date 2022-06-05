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
package com.msasc.lib.ml.data;

import java.util.Iterator;

/**
 * Source of patterns.
 * @author Miquel Sas
 */
public abstract class PatternSource implements Iterator<Pattern> {
	/**
	 * Returns true it the source has more patterns.
	 */
	public abstract boolean hasNext();
	/**
	 * Returns the next pattern or null.
	 */
	public abstract Pattern next();
	/**
	 * Check whether the source is empty.
	 * @return A boolean.
	 */
	public boolean isEmpty() { return size() == 0; }
	/**
	 * Reset the source and point to the first pattern.
	 */
	public abstract void reset();
	/**
	 * Return the size of the source.
	 * @return The size.
	 */
	public abstract int size();
}
