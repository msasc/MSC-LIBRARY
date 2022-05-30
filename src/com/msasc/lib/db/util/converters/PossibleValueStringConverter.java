package com.msasc.lib.db.util.converters;

import com.msasc.lib.db.Field;
import com.msasc.lib.db.Value;

/**
 * String converter for a possible value.
 * 
 * @author Miquel Sas
 */
public class PossibleValueStringConverter implements ValueStringConverter {

	/** Field. */
	private Field field;

	/**
	 * Constructor.
	 * @param field The field
	 */
	public PossibleValueStringConverter(Field field) {
		this.field = field;
		if (!this.field.getProperties().hasPossibleValues()) {
			throw new IllegalArgumentException("Field is not possible values");
		}
	}
	/**
	 * Return the value given the string.
	 */
	@Override
	public Value fromString(String string) { return null; }
	/**
	 * Return the string given the value.
	 */
	@Override
	public String toString(Value value) {
		return field.getProperties().getPossibleValueLabel(value);
	}
}