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

import java.util.List;

/**
 * A pattern of data.
 * 
 * @author Miquel Sas
 */
public abstract class Pattern {

	/** Optional label. */
	private String label;

	/**
	 * Constructor.
	 */
	public Pattern() {}
	
	/**
	 * Return the pattern input values.
	 * @return The pattern input values.
	 */
	public abstract List<double[]> getInputValues();
	/**
	 * Return the optional pattern output values.
	 * @return The pattern output values.
	 */
	public abstract List<double[]> getOutputValues();

	/**
	 * Return the optional label.
	 * @return The label.
	 */
	public String getLabel() { return label; }
	/**
	 * Set the label.
	 * @param label The label.
	 */
	public void setLabel(String label) { this.label = label; }
}
