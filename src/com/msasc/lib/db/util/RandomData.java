/*
 * Copyright (C) 2018 Miquel Sas
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.msasc.lib.db.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;

import com.msasc.lib.db.Field;
import com.msasc.lib.db.FieldList;
import com.msasc.lib.db.Record;
import com.msasc.lib.db.RecordList;
import com.msasc.lib.db.RecordSet;
import com.msasc.lib.db.Types;
import com.msasc.lib.db.Value;
import com.msasc.lib.util.Strings;

/**
 * A generator of random data, mainly records and recordsets, useful for tests.
 *
 * @author Miquel Sas
 */
public class RandomData {

	/**
	 * Test data generation using a pre defined field list.
	 */
	public static class Test {

		public static final String CARTICLE = "CARTICLE";
		public static final String DTITLE = "DTITLE";
		public static final String DARTICLE = "DARTICLE";
		public static final String CBUSINESS = "CBUSINESS";
		public static final String TCREATED = "TCREATED";
		public static final String QSALES = "QSALES";
		public static final String QPROD = "QPROD";
		public static final String QPURCH = "QPURCH";
		public static final String ICHECKED = "ICHECKED";
		public static final String IREQUIRED = "IREQUIRED";
		public static final String ISTATUS = "ISTATUS";

		/** Random data. */
		private RandomData rd;
		/** Field list. */
		private FieldList fields;

		/**
		 * Constructor.
		 */
		public Test(RandomData rd) {

			this.rd = rd;

			fields = new FieldList();

			fields.addField(Field.createRowNum());

			Field fCARTICLE = new Field();
			fCARTICLE.setName("CARTICLE");
			fCARTICLE.setAlias("CARTICLE");
			fCARTICLE.getProperties().setTitle("Artice code");
			fCARTICLE.getProperties().setLabel("Artice code");
			fCARTICLE.getProperties().setHeader("Article");
			fCARTICLE.setType(Types.STRING);
			fCARTICLE.setLength(10);
			fCARTICLE.getProperties().setFixedWidth(true);
			fCARTICLE.setPrimaryKey(true);
			fCARTICLE.getProperties().setUppercase(true);
			fCARTICLE.getProperties().setStyle("-fx-font-family: consolas");
			fields.addField(fCARTICLE);

			Field fDTITLE = new Field();
			fDTITLE.setName("DTITLE");
			fDTITLE.setAlias("DTITLE");
			fDTITLE.getProperties().setTitle("Title");
			fDTITLE.getProperties().setLabel("Title");
			fDTITLE.getProperties().setHeader("Title");
			fDTITLE.setType(Types.STRING);
			fDTITLE.setLength(60);
			fDTITLE.getProperties().setFixedWidth(false);
			fields.addField(fDTITLE);

			Field fDARTICLE = new Field();
			fDARTICLE.setName("DARTICLE");
			fDARTICLE.setAlias("DARTICLE");
			fDARTICLE.getProperties().setTitle("Description");
			fDARTICLE.getProperties().setLabel("Description");
			fDARTICLE.getProperties().setHeader("Description");
			fDARTICLE.setType(Types.STRING);
			fDARTICLE.setLength(60);
			fDARTICLE.getProperties().setFixedWidth(false);
			fields.addField(fDARTICLE);

			Field fCBUSINESS = new Field();
			fCBUSINESS.setName("CBUSINESS");
			fCBUSINESS.setAlias("CBUSINESS");
			fCBUSINESS.getProperties().setTitle("Business code");
			fCBUSINESS.getProperties().setLabel("Business code");
			fCBUSINESS.getProperties().setHeader("Business");
			fCBUSINESS.setType(Types.STRING);
			fCBUSINESS.setLength(6);
			fCBUSINESS.getProperties().setFixedWidth(true);
			fCBUSINESS.getProperties().setStyle("-fx-font-family: consolas");
			fields.addField(fCBUSINESS);

			Field fTCREATED = new Field();
			fTCREATED.setName("TCREATED");
			fTCREATED.setAlias("TCREATED");
			fTCREATED.getProperties().setTitle("Date created");
			fTCREATED.getProperties().setLabel("Date created");
			fTCREATED.getProperties().setHeader("Date created");
			fTCREATED.setType(Types.DATE);
			fields.addField(fTCREATED);

			Field fQSALES = new Field();
			fQSALES.setName("QSALES");
			fQSALES.setAlias("QSALES");
			fQSALES.getProperties().setTitle("Sales");
			fQSALES.getProperties().setLabel("Sales");
			fQSALES.getProperties().setHeader("Sales");
			fQSALES.setType(Types.DECIMAL);
			fQSALES.setLength(14);
			fQSALES.setDecimals(4);
			fields.addField(fQSALES);

			Field fQPROD = new Field();
			fQPROD.setName("QPROD");
			fQPROD.setAlias("QPROD");
			fQPROD.getProperties().setTitle("Production");
			fQPROD.getProperties().setLabel("Production");
			fQPROD.getProperties().setHeader("Production");
			fQPROD.setType(Types.DECIMAL);
			fQPROD.setLength(14);
			fQPROD.setDecimals(4);
			fields.addField(fQPROD);

			Field fQPURCH = new Field();
			fQPURCH.setName("QPURCH");
			fQPURCH.setAlias("QPURCH");
			fQPURCH.getProperties().setTitle("Purchases");
			fQPURCH.getProperties().setLabel("Purchases");
			fQPURCH.getProperties().setHeader("Purchases");
			fQPURCH.setType(Types.DECIMAL);
			fQPURCH.setLength(14);
			fQPURCH.setDecimals(4);
			fields.addField(fQPURCH);

			Field fICHECKED = new Field();
			fICHECKED.setName("ICHECKED");
			fICHECKED.setAlias("ICHECKED");
			fICHECKED.getProperties().setTitle("Checked");
			fICHECKED.getProperties().setLabel("Checked");
			fICHECKED.getProperties().setHeader("Checked");
			fICHECKED.setType(Types.BOOLEAN);
//			fICHECKED.setEditBooleanInCheckBox(true);
			fields.addField(fICHECKED);

			Field fIREQUIRED = new Field();
			fIREQUIRED.setName("IREQUIRED");
			fIREQUIRED.setAlias("IREQUIRED");
			fIREQUIRED.getProperties().setTitle("Required");
			fIREQUIRED.getProperties().setLabel("Required");
			fIREQUIRED.getProperties().setHeader("Required");
			fIREQUIRED.setType(Types.BOOLEAN);
//			fIREQUIRED.setEditBooleanInCheckBox(false);
			fields.addField(fIREQUIRED);

			Field fISTATUS = new Field();
			fISTATUS.setName("ISTATUS");
			fISTATUS.setAlias("ISTATUS");
			fISTATUS.getProperties().setTitle("Status");
			fISTATUS.getProperties().setLabel("Status");
			fISTATUS.getProperties().setHeader("Status");
			fISTATUS.setType(Types.STRING);
			fISTATUS.setLength(2);
			fISTATUS.getProperties().addPossibleValue("01", "Created");
			fISTATUS.getProperties().addPossibleValue("02", "Acceptance");
			fISTATUS.getProperties().addPossibleValue("03", "Accepted");
			fISTATUS.getProperties().addPossibleValue("04", "Engineered");
			fISTATUS.getProperties().addPossibleValue("05", "Produced");
			fISTATUS.getProperties().addPossibleValue("06", "Sales");
			fISTATUS.getProperties().addPossibleValue("07", "Obsolete");
			fields.addField(fISTATUS);
			
			fields.setupAndValidate();

		}

		/**
		 * Return the internal test field list.
		 * @return The copy of the field list.
		 */
		public FieldList getFieldList() { return fields; }

		/**
		 * Return a default record using the test field list.
		 */
		public Record getRecordDefault() { return new Record(fields); }

		/**
		 * Return a random record using the test field list.
		 */
		public Record getRecordRandom(long row) { return rd.getRecord(row, fields); }

		/**
		 * Return a random recordset of the given size using the test field list.
		 * @param size The size of the recordset.
		 * @return The random recordset.
		 */
		public RecordSet getRecordSet(int size) {
			RecordList rs = new RecordList(fields);
			for (int i = 0; i < size; i++) {
				rs.add(getRecordRandom(i + 1));
			}
			return rs;
		}
	}
	/**
	 * Code pattern. Any character in the pattern not included in the valid pattern
	 * characters is treated literally.
	 * Valid pattern characters are:
	 * <ul>
	 * <li><b>#</b> a digit</li>
	 * <li><b>A</b> an upper case alpha numerical digit or letter</li>
	 * <li><b>a</b> a lower case alpha numerical digit or letter</li>
	 * <li><b>?</b> an alpha numerical digit or letter with random case</li>
	 * <li><b>L</b> an upper case letter</li>
	 * <li><b>l</b> a lower case letter</li>
	 * <li><b>!</b> a letter with random case</li>
	 * </ul>
	 */
	public static class CodePattern {

		/**
		 * Pattern.
		 */
		private String pattern;
		/**
		 * Source letters.
		 */
		private String letters;
		/**
		 * Source digits.
		 */
		private String digits;

		/**
		 * Constructor.
		 *
		 * @param pattern Pattern.
		 */
		public CodePattern(String pattern) {
			this(pattern, Strings.LETTERS, Strings.DIGITS);
		}

		/**
		 * Constructor.
		 *
		 * @param pattern Pattern.
		 * @param letters Source letters.
		 * @param digits  Source digits.
		 */
		public CodePattern(String pattern, String letters, String digits) {
			this.letters = letters;
			this.digits = digits;
		}

		/**
		 * Return the pattern.
		 *
		 * @return The pattern.
		 */
		public String getPattern() { return pattern; }

		/**
		 * Return source the letters.
		 *
		 * @return The letters.
		 */
		public String getLetters() { return letters; }

		/**
		 * Return the source digits.
		 *
		 * @return The digits.
		 */
		public String getDigits() { return digits; }

	}

	/** Random. */
	private Random random;
	/** Date start. */
	private int yearStart;
	/** Date end. */
	private int yearEnd;

	/**
	 * Constructor.
	 */
	public RandomData(int yearStart, int yearEnd) {
		super();
		this.random = new Random();
		this.yearStart = yearStart;
		this.yearEnd = yearEnd;
	}

	/**
	 * Returns a random boolean.
	 *
	 * @return A randomly generated boolean.
	 */
	public boolean getBoolean() { return (random.nextInt(2) == 1); }

	/**
	 * Returns a random char within the source string.
	 *
	 * @param source The source string.
	 * @return The random char.
	 */
	public char getChar(String source) {
		int index = random.nextInt(source.length());
		return source.charAt(index);
	}

	/**
	 * Returns a random string.
	 *
	 * @param length The desired length of the string.
	 * @param source The source string from where to extract characters.
	 * @return The randomly generated string.
	 */
	public String getString(int length, String source) {
		return getString(length, source, false, true);
	}

	/**
	 * Returns a random string.
	 *
	 * @param length          The desired length of the string.
	 * @param source          The source string from where to extract characters.
	 * @param startWithSpaces Control starting with spaces.
	 * @param trim            Control trimming the result.
	 * @return The randomly generated string.
	 */
	public String getString(int length, String source, boolean startWithSpaces, boolean trim) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < length; i++) {
			char c = getChar(source);
			if (i == 0 && !startWithSpaces && c == ' ') {
				while (c == ' ') {
					c = getChar(source);
				}
			}
			b.append(c);
		}
		return (trim ? b.toString().trim() : b.toString());
	}

	/**
	 * Returns a random string.
	 */
	public String getString(int minLength, int maxLength, String source) {
		int length = minLength + random.nextInt(maxLength - minLength);
		return getString(length, source);
	}

	/**
	 * Return a text made of token.
	 * 
	 * @param length Text length.
	 * @return The tokens.
	 */
	public String getTokens(int length) {
		StringBuilder b = new StringBuilder();
		while (b.length() < length) {
			int bound = (length - b.length()) / 4;
			if (bound < 5) {
				bound = 5;
			}
			if (bound > 30) {
				bound = 30;
			}
			int tokenLength = random.nextInt(bound);
			while (tokenLength < 2) {
				tokenLength = random.nextInt(bound);
			}
			boolean vowel = getBoolean();
			for (int i = 0; i < tokenLength; i++) {
				b.append(getChar(vowel ? Strings.VOWELS : Strings.CONSONANTS));
				vowel = !vowel;
			}
			b.append(' ');
		}
		return b.toString().substring(0, length).trim();
	}

	/**
	 * Returns a random big decimal.
	 *
	 * @param length The total length.
	 * @return A randomly generated number with optional decimal places.
	 */
	public BigDecimal getDecimal(int length) {
		return getDecimal(length, 0);
	}

	/**
	 * Returns a random big decimal.
	 *
	 * @param length   The total length.
	 * @param decimals The number of decimal places.
	 * @return A randomly generated number with optional decimal places.
	 */
	public BigDecimal getDecimal(int length, int decimals) {
		int integer = random.nextInt((length - (decimals > 0 ? decimals + 1 : 0)) / 2) + 1;
		String integerPart = getString(integer, Strings.DIGITS);
		String decimalPart = getString(decimals, Strings.DIGITS);
		String number = integerPart + "." + decimalPart;
		return new BigDecimal(number).setScale(decimals, RoundingMode.HALF_UP);
	}

	public BigDecimal getDecimal(Field field) {
		if (!field.isNumber()) {
			throw new IllegalArgumentException("Field is not a number");
		}
		if (field.getDecimals() < 0) {
			throw new IllegalArgumentException("Field must have fixed zero or more decimals");
		}
		return getDecimal(field.getLength(), field.getDecimals());
	}

	/**
	 * Return a random code.
	 *
	 * @param pattern The code pattern.
	 * @return The random code.
	 */
	public String getCode(CodePattern pattern) {
		return getCode(pattern.getPattern(), pattern.getLetters(), pattern.getDigits());
	}

	/**
	 * Returns a random code based on the given pattern. Any character in the mask
	 * not included in the valid mask
	 * characters is treated literally. Valid mask characters are:
	 * <ul>
	 * <li><b>#</b> a digit</li>
	 * <li><b>A</b> an upper case alpha numerical digit or letter</li>
	 * <li><b>a</b> a lower case alpha numerical digit or letter</li>
	 * <li><b>?</b> an alpha numerical digit or letter with random case</li>
	 * <li><b>L</b> an upper case letter</li>
	 * <li><b>l</b> a lower case letter</li>
	 * <li><b>!</b> a letter with random case</li>
	 * </ul>
	 *
	 * @param pattern The pattern.
	 * @return The random code.
	 */
	public String getCode(String pattern) {
		return getCode(pattern, Strings.LETTERS, Strings.DIGITS);
	}

	/**
	 * Returns a random text.
	 */
	public String getCode(Field field, int prefixLength) {
		int suffixLength = field.getLength() - prefixLength;
		return getString(prefixLength, Strings.LETTERS) + getString(suffixLength, Strings.DIGITS);
	}

	/**
	 * Returns a random code based on the given pattern. Any character in the mask
	 * not included in the valid mask
	 * characters is treated literally. Valid mask characters are:
	 * <ul>
	 * <li><b>#</b> a digit</li>
	 * <li><b>A</b> an upper case alpha numerical digit or letter</li>
	 * <li><b>a</b> a lower case alpha numerical digit or letter</li>
	 * <li><b>?</b> an alpha numerical digit or letter with random case</li>
	 * <li><b>L</b> an upper case letter</li>
	 * <li><b>l</b> a lower case letter</li>
	 * <li><b>!</b> a letter with random case</li>
	 * </ul>
	 *
	 * @param pattern The pattern.
	 * @param letters Optional letters.
	 * @param digits  Optional list of digits.
	 * @return The random code.
	 */
	public String getCode(String pattern, String letters, String digits) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < pattern.length(); i++) {
			char c = pattern.charAt(i);
			Character r;
			switch (c) {
			case '#':
				r = getChar(digits);
				break;
			case 'A':
				r = Character.toUpperCase(getChar(digits + letters));
				break;
			case 'a':
				r = Character.toLowerCase(getChar(digits + letters));
				break;
			case '?':
				if (getBoolean()) {
					r = Character.toUpperCase(getChar(digits + letters));
				} else {
					r = Character.toLowerCase(getChar(digits + letters));
				}
				break;
			case 'L':
				r = Character.toUpperCase(getChar(letters));
				break;
			case 'l':
				r = Character.toLowerCase(getChar(letters));
				break;
			case '!':
				if (getBoolean()) {
					r = Character.toUpperCase(getChar(letters));
				} else {
					r = Character.toLowerCase(getChar(letters));
				}
				break;
			default:
				r = c;
				break;
			}
			b.append(r);
		}
		return b.toString();
	}

	/**
	 * Return a random date.
	 *
	 * @return A random date.
	 */
	public LocalDate getDate() {
		int year = yearStart + random.nextInt(yearEnd - yearStart + 1);
		int month = random.nextInt(12) + 1;
		int days = LocalDate.of(year, month, 1).lengthOfMonth();
		int day = random.nextInt(days) + 1;
		return LocalDate.of(year, month, day);
	}

	/**
	 * Return a random time.
	 *
	 * @return A random time.
	 */
	public LocalTime getTime() {
		int hour = random.nextInt(24);
		int minute = random.nextInt(60);
		int second = random.nextInt(60);
		return LocalTime.of(hour, minute, second);
	}

	/**
	 * Return a random timestamp.
	 *
	 * @return A random timestamp.
	 */
	public LocalDateTime getTimestamp() { return LocalDateTime.of(getDate(), getTime()); }

	/**
	 * Randomly get an element of the list.
	 *
	 * @param <T>  The type.
	 * @param list The list.
	 * @return The selected element.
	 */
	public <T> T getElement(List<T> list) {
		return list.get(random.nextInt(list.size()));
	}

	/**
	 * Randomly generate a record given the field list.
	 *
	 * @param fields The field list.
	 * @return The generated record.
	 */
	public Record getRecord(long row, FieldList fields) {
		Record rc = new Record(fields);
		for (int i = 0; i < fields.size(); i++) {
			Field field = fields.getField(i);

			/* Possible values, set one of them. */
			if (field.getProperties().hasPossibleValues()) {
				List<Value> values = field.getProperties().getPossibleValueList();
				rc.setValue(i, getElement(values));
				continue;
			}

			/* Boolean field. */
			if (field.isBoolean()) {
				rc.setValue(i, new Value(getBoolean()));
				continue;
			}

			/* String field, do as appropriate. */
			if (field.isString()) {
				/* Has a random pattern. */
//				if (field.getRandomPattern() != null) {
//					rc.setValue(i, new Value(getCode(field.getRandomPattern())));
//					continue;
//				}
				/* Get a valid string... */
				String value = null;
				Integer displayLength = field.getProperties().getDisplayLength();
				if (displayLength != null && displayLength > 0) {
					if (field.getProperties().isFixedWidth()) {
						value = getString(displayLength, Strings.LETTERS);
					} else {
						int length = random.nextInt(displayLength);
						while (length == 0 || length < displayLength / 4) {
							length = random.nextInt(displayLength);
						}
						value = getTokens(length);
					}
				}
				/* Do assign it. */
				rc.setValue(i, new Value(value));
				continue;
			}

			/* Number field. */
			if (field.isNumber()) {
				/* Decimal field, most common and easy to generate. */
				if (field.isDecimal()) {
					int length = field.getLength();
					int decimals = field.getDecimals();
					rc.setValue(i, new Value(getDecimal(length, decimals)));
					continue;
				}
				/* Double, 20 integer positions and 10 decimal places. */
				if (field.isDouble()) {
					int length = 31;
					int decimals = 10;
					rc.setValue(i, new Value(getDecimal(length, decimals).doubleValue()));
					continue;
				}
				/* Long, 30 integer positions. */
				if (field.isLong()) {
					int length = 30;
					int decimals = 0;
					rc.setValue(i, new Value(getDecimal(length, decimals).longValue()));
					continue;
				}
				/* Integer, 15 integer positions. */
				if (field.isLong()) {
					int length = 15;
					int decimals = 0;
					rc.setValue(i, new Value(getDecimal(length, decimals).intValue()));
					continue;
				}
			}

			/* Date, time or timestamp field. */
			if (field.isDate()) {
				rc.setValue(i, new Value(getDate()));
				continue;
			}
			if (field.isTime()) {
				rc.setValue(i, new Value(getTime()));
				continue;
			}
			if (field.isTimestamp()) {
				rc.setValue(i, new Value(getTimestamp()));
				continue;
			}

			/* Other, byte array or value, set to null. */
//			rc.setValue(i).setNull();
		}
		return rc;
	}
}
