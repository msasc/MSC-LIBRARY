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
package com.msasc.lib.db.rdbms.adapters;

import java.sql.SQLException;

import com.msasc.lib.db.Field;
import com.msasc.lib.db.Types;
import com.msasc.lib.db.Value;
import com.msasc.lib.db.rdbms.DBEngine;
import com.msasc.lib.db.rdbms.DBEngineAdapter;
import com.msasc.lib.db.rdbms.DBMetaData;

/**
 * MariaDB <i>DBEngineAdapter</i>.
 * 
 * @author Miquel Sas
 */
public class MariaDBAdapter extends DBEngineAdapter {

	public static final long SIZE_TINY = 255;
	public static final long SIZE_SHORT = 65535;
	public static final long SIZE_MEDIUM = 16777215;
	public static final long SIZE_LONG = 4294967295L;

	/**
	 * Constructor.
	 */
	public MariaDBAdapter() {}

	/**
	 * Returns the CURRENT DATE function as a string.
	 */
	@Override
	public String getCurrentDate() { return "CURRENT_DATE"; }

	/**
	 * Returns the CURRENT TIME function as a string.
	 */
	@Override
	public String getCurrentTime() { return "CURRENT_TIME"; }

	/**
	 * Returns the CURRENT TIMESTAMP function as a string.
	 */
	@Override
	public String getCurrentTimestamp() { return "CURRENT_TIMESTAMP"; }

	/**
	 * Return the driver class name.
	 */
	@Override
	public String getDriverClassName() { return "org.mariadb.jdbc.Driver"; }

	/**
	 * Return the driver protocol.
	 */
	@Override
	public String getDriverProtocol() { return "jdbc:mariadb://"; }

	/**
	 * Return the field definition.
	 */
	public String getFieldDefinition(Field field) {

		if (field == null) {
			throw new NullPointerException("Field can not be null");
		}

		StringBuilder def = new StringBuilder();
		def.append(field.getName());
		def.append(" ");

		if (field.isBoolean()) {
			def.append("VARCHAR(1)");
		}

		if (field.isDecimal()) {
			if (field.getLength() == null) throw new IllegalArgumentException("Field " + field
					.getName() + " without length");
			if (field.getDecimals() == null) throw new IllegalArgumentException("Field " + field
					.getName() + " without decimals");
			int len = field.getLength();
			int dec = field.getDecimals();
			if (dec < 0) dec = 0;
			if (len <= 0) len = 65 - dec;
			def.append("DECIMAL(" + len + "," + dec + ")");
		}
		if (field.isDouble()) {
			def.append("DOUBLE");
		}
		if (field.isLong()) {
			def.append("BIGINT");
		}
		if (field.isInteger()) {
			def.append("INTEGER");
		}

		if (field.isDate()) {
			def.append("DATE");
		}
		if (field.isTime()) {
			def.append("TIME");
		}
		if (field.isTimestamp()) {
			def.append("TIMESTAMP");
		}

		if (field.isBinary()) {
			int len = field.getLength();
			if (len <= SIZE_TINY) def.append("TINYBLOB");
			else if (len <= SIZE_SHORT) def.append("BLOB");
			else if (len <= SIZE_MEDIUM) def.append("MEDIUMBLOB");
			else def.append("LONGBLOB");
		}

		if (field.isString()) {
			int len = field.getLength();
			if (len > 0 && len <= SIZE_SHORT) {
				def.append("VARCHAR(");
				def.append(len);
				def.append(")");
			} else {
				if (len <= SIZE_TINY) def.append("TINYTEXT");
				else if (len <= SIZE_SHORT) def.append("TEXT");
				else if (len <= SIZE_MEDIUM) def.append("MEDIUMTEXT");
				else def.append("LONGTEXT");
			}
		}

		if (field.isArray()) {
			def.append("JSON");
		}
		if (field.isJSONObject()) {
			def.append("JSON");
		}

		return def.toString();
	}

	/**
	 * Return the database metadata.
	 */
	@Override
	public DBMetaData getMetaData(DBEngine db) throws SQLException {
		return new MariaDBMetaData(db);
	}

	/**
	 * Return the library type.
	 */
	@Override
	public Types getType(String columnType) {
		return null;
	}

	/**
	 * Convert the argument value to a string that can be used in a SELECT, INSERT,
	 * UPDATE or WHERE query.
	 */
	public String toStringSQL(Value value) {

		if (value.isBinary()) {
			throw new IllegalArgumentException("BINARY type not permitted");
		}

		if (value.isBoolean()) {
			return value.getBoolean() ? "'T'" : "'F'";
		}

		if (value.isNumber()) {
			if (value.isNull()) return "0";
			if (value.isInteger() || value.isLong()) {
				return value.getBigDecimal().setScale(0).toPlainString();
			}
			return value.getBigDecimal().toPlainString();
		}

		if (value.isNull()) {
			return "NULL";
		}

		if (value.isDate()) {
			return "DATE'" + value.getDate().toString() + "'";
		}
		if (value.isTime()) {
			return "TIME'" + value.getTime().toString() + "'";
		}
		if (value.isTimestamp()) {
			return "TIMESTAMP'" + value.getTimestamp().toString() + "'";
		}

		if (value.isString()) {
			return "'" + value.toString() + "'";
		}

		if (value.isArray()) {
			return "'" + Value.toJSONObject(value.getArray()) + "'";
		}
		if (value.isJSONObject()) {
			return "'" + value.getJSONObject().toString() + "'";
		}

		throw new IllegalStateException("Unreachable code");
	}
}
