package com.msasc.lib.db.util.converters;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

import com.msasc.lib.db.Field;
import com.msasc.lib.db.Value;
import com.msasc.lib.util.Formats;

/**
 * String converter for values of type number.
 * @author Miquel Sas
 */
public class NumberStringConverter implements ValueStringConverter {

	/** Field. */
	private Field field;
	/** Locale. */
	private Locale locale;

	/**
	 * Constructor.
	 * @param field Field.
	 */
	public NumberStringConverter(Field field) { this(field, Locale.getDefault()); }
	/**
	 * Constructor.
	 * @param field  Field.
	 * @param locale Locale.
	 */
	public NumberStringConverter(Field field, Locale locale) {
		if (!field.isNumber()) throw new IllegalArgumentException("Field is not a number");
		this.field = field;
		this.locale = locale;
	}
	/**
	 * Return the value given the string.
	 */
	@Override
	public Value fromString(String string) {
		try {
			if (field.isDecimal()) {
				BigDecimal value = Formats.toBigDecimal(string, locale);
				value = value.setScale(field.getDecimals(), RoundingMode.HALF_UP);
				return new Value(value);
			}
			if (field.isDouble()) {
				double value = Formats.toDouble(string, locale);
				return new Value(value);
			}
			if (field.isInteger()) {
				int value = Formats.toInteger(string, locale);
				return new Value(value);
			}
			if (field.isLong()) {
				long value = Formats.toLong(string, locale);
				return new Value(value);
			}
		} catch (java.text.ParseException exc) {
			exc.printStackTrace();
		}
		return null;
	}
	/**
	 * Return the string given the value.
	 */
	@Override
	public String toString(Value value) {
		if (value == null) return toString(field.getDefaultValue());
		Integer decimals = field.getProperties().getDisplayDecimals();
		if (decimals == null) decimals = 0;
		return Formats.getNumberFormat(decimals, locale).format(value.getDouble());
	}
}