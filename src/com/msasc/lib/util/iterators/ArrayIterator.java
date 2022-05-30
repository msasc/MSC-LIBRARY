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
package com.msasc.lib.util.iterators;

import java.util.Iterator;

/**
 * Iterator on an array.
 * @author Miquel Sas
 */
public class ArrayIterator<E> implements Iterator<E> {

	/** Underlying array. */
	private E[] data;
	/** Index. */
	private int index;

	/**
	 * Constructor.
	 * @param data Data array.
	 */
	@SuppressWarnings("unchecked")
	public ArrayIterator(E... data) {
		if (data == null) throw new NullPointerException();
		this.data = data;
		this.index = 0;
	}
	/**
	 * Return a boolean indicating whether there are more elements to retrieve.
	 */
	@Override
	public boolean hasNext() { return index < data.length; }
	/**
	 * Returns the next element.
	 */
	@Override
	public E next() { return data[index++]; }
}
