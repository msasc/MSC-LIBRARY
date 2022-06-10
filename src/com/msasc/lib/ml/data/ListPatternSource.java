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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A pattern source backed by an array list.
 * @author Miquel Sas
 */
public class ListPatternSource extends PatternSource {

	/** The underlying pattern list. */
	private List<Pattern> patterns = new ArrayList<>();
	/** Pattern iterator. */
	private Iterator<Pattern> iterator;

	/**
	 * Constructor.
	 */
	public ListPatternSource() {}

	/**
	 * Add a pattern.
	 * @param pattern A pattern.
	 */
	public void add(Pattern pattern) { patterns.add(pattern); iterator = null; }

	@Override
	public boolean hasNext() { return iterator.hasNext(); }
	@Override
	public Pattern next() { return iterator.next(); }
	@Override
	public void reset() { iterator = patterns.iterator(); }
	@Override
	public int size() { return patterns.size(); }
}
