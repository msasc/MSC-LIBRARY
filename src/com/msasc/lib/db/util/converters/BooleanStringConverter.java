package com.msasc.lib.db.util.converters;

import java.util.Locale;

import com.msasc.lib.db.Value;
import com.msasc.lib.util.resources.StringRes;

/**
 * String converter for values of type boolean.
 * @author Miquel Sas
 */
public class BooleanStringConverter implements ValueStringConverter {

	/** String true. */
	private String tokenTrue;
	/** String false. */
	private String tokenFalse;

	/**
	 * Constructor.
	 */
	public BooleanStringConverter() { this(Locale.getDefault()); }
	/**
	 * Constructor.
	 * @param locale Locale.
	 */
	public BooleanStringConverter(Locale locale) {
		this.tokenTrue = StringRes.get("tokenYes", locale);
		this.tokenFalse = StringRes.get("tokenNo", locale);
	}
	/**
	 * Constructor assigning the true and false tokens.
	 * @param tokenTrue  Token for true values.
	 * @param tokenFalse Token for false values.
	 */
	public BooleanStringConverter(String tokenTrue, String tokenFalse) {
		this.tokenTrue = tokenTrue;
		this.tokenFalse = tokenFalse;
	}
	/**
	 * Return the value given the string.
	 */
	@Override
	public Value fromString(String string) {
		if (string == null) return new Value((Boolean) null);
		return new Value(string.equals(tokenTrue));
	}
	/**
	 * Return the string given the value.
	 */
	@Override
	public String toString(Value value) {
		return value.getBoolean() ? tokenTrue : tokenFalse;
	}
}