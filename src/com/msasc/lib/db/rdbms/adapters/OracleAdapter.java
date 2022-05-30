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
import com.msasc.lib.util.Strings;

/**
 * Oracle <i>DBEngineAdapter</i>.
 * 
 * @author Miquel Sas
 */
public class OracleAdapter extends DBEngineAdapter {

	public static final long MAX_VARCHAR = 2000;

	/**
	 * Constructor.
	 */
	public OracleAdapter() {}

	/**
	 * Returns the CURRENT DATE function as a string.
	 */
	@Override
	public String getCurrentDate() { return "SYSTIMESTAMP"; }
	/**
	 * Returns the CURRENT TIME function as a string.
	 */
	@Override
	public String getCurrentTime() { return "SYSTIMESTAMP"; }
	/**
	 * Returns the CURRENT TIMESTAMP function as a string.
	 */
	@Override
	public String getCurrentTimestamp() { return "SYSTIMESTAMP"; }

	/**
	 * Return the driver class name.
	 */
	@Override
	public String getDriverClassName() { return "oracle.jdbc.driver.OracleDriver"; }
	/**
	 * Return the driver protocol.
	 */
	@Override
	public String getDriverProtocol() { return "jdbc:oracle:thin:@"; }

	/**
	 * Return the field definition.
	 */
	public String getFieldDefinition(Field field) {

		if (field == null) {
			throw new NullPointerException("Field can not be null");
		}

		StringBuilder def = new StringBuilder();

		if (field.isBoolean()) { def.append("VARCHAR2(1)"); }

		if (field.isDecimal()) {
			int len = field.getLength();
			int dec = field.getDecimals();
			def.append("NUMBER(" + len + "," + dec + ")");
		}
		if (field.isDouble()) { def.append("NUMBER"); }
		if (field.isLong()) { def.append("NUMBER"); }
		if (field.isInteger()) { def.append("NUMBER"); }

		if (field.isDate()) { def.append("DATE"); }
		if (field.isTime()) { def.append("TIME"); }
		if (field.isTimestamp()) { def.append("TIMESTAMP"); }

		if (field.isBinary()) {
			int len = field.getLength();
			if (len <= MAX_VARCHAR) {
				def.append("RAW");
			} else {
				def.append("LONG RAW");
			}
		}

		if (field.isString()) {
			int len = field.getLength();
			if (len <= MAX_VARCHAR) {
				def.append("VARCHAR2(" + len + ")");
			} else {
				def.append("CLOB");
			}
		}

		if (field.isArray()) { def.append("CLOB"); }
		if (field.isJSONObject()) { def.append("CLOB"); }

		return def.toString();
	}

	/**
	 * Return the database metadata.
	 */
	@Override
	public DBMetaData getMetaData(DBEngine db) throws SQLException { return null; }

	/**
	 * Return the library type.
	 */
	@Override
	public Types getType(String columnType) { return null; }

	/**
	 * Convert the argument value to a string that can be used in a SELECT, INSERT,
	 * UPDATE or WHERE query.
	 */
	@Override
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
			return "TO_TIMESTAMP('" + value.getDate() + "', 'YYYY-MM-DD')";
		}
		if (value.isTime()) {
			return "TO_TIMESTAMP('" + value.getTime() + "', 'HH24:MI:SS.FF')";
		}
		if (value.isTimestamp()) {
			String timestamp = Strings.replace(value.getTimestamp().toString(), "T", " ");
			return "TO_TIMESTAMP('" + timestamp + "', 'YYYY-MM-DD HH24:MI:SS.FF')";
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
