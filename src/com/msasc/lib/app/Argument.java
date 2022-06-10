/*
 * Copyright (C) 2018 Miquel Sas
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.msasc.lib.app;

import java.util.ArrayList;
import java.util.List;

import com.msasc.lib.util.Lists;

/**
 * Startup or command line argument to help the management, i.e. validation and reading in any
 * order, of the <code>main</code> arguments or parameters in a JNLP file in the form of (name,value).
 * Examples of arguments are:
 * <ul>
 * <li>unnamed</li>
 * <li>name=value</li>
 * <li>name="value_with_spaces"</li>
 * <li>name=value_1+value_2+...+value_n</li>
 * <li>name="value_with_spaces_1"+"value_with_spaces_2"+...+"value_with_spaces_n"</li>
 * <li>--unnamed</li>
 * <li>--name=value</li>
 * <li>--name="value_with_spaces"</li>
 * <li>--name=value_1+value_2+...+value_n</li>
 * <li>--name="value_with_spaces_1"+"value_with_spaces_2"+...+"value_with_spaces_n"</li>
 * <li>/unnamed</li>
 * <li>/name=value</li>
 * <li>/name="value_with_spaces"</li>
 * <li>/name=value_1+value_2+...+value_n</li>
 * <li>/name="value_with_spaces_1"+"value_with_spaces_2"+...+"value_with_spaces_n"</li>
 * </ul>
 * Name-value separators can be =, : and any configured in the argument manager.
 * <p>
 * <b>Arguments are not case sensitive.</b>
 * 
 * @author Miquel Sas
 */
public class Argument {

	/** Argument name. */
	private String name = null;
	/** Description. */
	private String description = null;
	/** A flag to control if the argument is always required. */
	private boolean required = false;
	/** A flag to control if the argument requires at least a value or it is strictly a flag. */
	private boolean valuesRequired = false;
	/** A flag to control if the arguments accepts multiple values. */
	private boolean multipleValues = false;
	/** List of possible values. */
	private List<String> possibleValues = new ArrayList<>();

	/**
	 * Constructor. If a non empty value is required, any value can be set.
	 * 
	 * @param name           The name
	 * @param description    The description
	 * @param required       Required flag
	 * @param valuesRequired Requires value/s flag.
	 * @param multipleValues Multiple values flag.
	 */
	public Argument(String name, String description, boolean required, boolean valuesRequired, boolean multipleValues) {
		super();
		this.name = name;
		this.description = description;
		this.required = required;
		if (!valuesRequired && multipleValues) {
			throw new IllegalArgumentException("Not compatible 'valuesRequired' and 'multipleValues'");
		}
		this.valuesRequired = valuesRequired;
		this.multipleValues = multipleValues;
	}

	/**
	 * Constructor assigning a list of possible values.
	 * 
	 * @param name           The name
	 * @param description    The description
	 * @param required       Required flag
	 * @param multipleValues Multiple values flag.
	 * @param values         List of possible values.
	 */
	public Argument(String name, String description, boolean required, boolean multipleValues, String... values) {
		super();
		this.name = name;
		this.description = description;
		this.required = required;
		this.valuesRequired = true;
		this.multipleValues = multipleValues;
		this.possibleValues.addAll(Lists.asList(values));
	}

	/**
	 * Constructor assigning a list of possible values.
	 * 
	 * @param name           The name
	 * @param description    The description
	 * @param required       Required flag
	 * @param multipleValues Multiple values flag.
	 * @param values         List of possible values.
	 */
	public Argument(String name, String description, boolean required, boolean multipleValues, List<String> values) {
		super();
		this.name = name;
		this.description = description;
		this.required = required;
		this.valuesRequired = true;
		this.multipleValues = multipleValues;
		this.possibleValues.addAll(values);
	}

	/**
	 * Add a possible value.
	 * 
	 * @param value The value.
	 */
	public void addPossibleValue(String value) {
		if (!isValuesRequired()) {
			throw new IllegalStateException("No value required");
		}
		possibleValues.add(value);
	}

	/**
	 * Returns this argument description.
	 * 
	 * @return The description.
	 */
	public String getDescription() { return description; }

	/**
	 * Returns the name.
	 * 
	 * @return The name.
	 */
	public String getName() { return name; }

	/**
	 * Returns the array of possible values.
	 * 
	 * @return The possibleValues.
	 */
	public List<String> getPossibleValues() { return possibleValues; }

	/**
	 * Checks if the argument is required.
	 * 
	 * @return A boolean.
	 */
	public boolean isRequired() { return required; }

	/**
	 * Checks if the argument requires at least a value.
	 * 
	 * @return A boolean.
	 */
	public boolean isValuesRequired() { return valuesRequired; }

	/**
	 * Checks if the argument accepts multiple values.
	 * 
	 * @return A boolean.
	 */
	public boolean isMultipleValues() { return multipleValues; }

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Name: ");
		b.append(getName());
		b.append("\n");
		b.append("Description: ");
		b.append(getDescription());
		b.append("\n");
		b.append("Required: ");
		b.append(isRequired());
		if (isValuesRequired()) {
			if (!isMultipleValues()) {
				b.append(", single value.");
			} else {
				b.append(", multiple values.");
			}
		} else {
			b.append(", no value.");
		}
		if (!possibleValues.isEmpty()) {
			b.append("\n");
			b.append("Possible values: ");
			for (int i = 0; i < possibleValues.size(); i++) {
				String value = possibleValues.get(i);
				if (i > 0) {
					b.append(", ");
				}
				b.append("[");
				b.append(value);
				b.append("]");
			}
		}
		return b.toString();
	}
}
