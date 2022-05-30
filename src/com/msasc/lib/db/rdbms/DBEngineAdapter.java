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
package com.msasc.lib.db.rdbms;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.msasc.lib.db.Field;
import com.msasc.lib.db.Types;
import com.msasc.lib.db.Value;
import com.msasc.lib.json.JSONObject;

/**
 * A <i>DBEngineAdapter</i> addresses the particular properties of different
 * RBDMS systems, like column definition or type mapping.
 * 
 * @author Miquel Sas
 */
public abstract class DBEngineAdapter {

	/**
	 * Length to apply readers or writers.
	 */
	private static final int FIXED_LENGTH = 2000;

	/**
	 * Retrieve a value from a result set by field alias.
	 * @param rs    The result set.
	 * @param field The field.
	 * @return The value.
	 * @throws SQLException If an error occurs.
	 */
	public Value fromResultSet(DBResultSet rs, Field field) throws SQLException {
		return fromResultSet(rs, field, null);
	}
	/**
	 * Retrieve a value from a result set by field index.
	 * @param rs    The result set.
	 * @param field The field.
	 * @param index The index.
	 * @return The value.
	 * @throws SQLException If an error occurs.
	 */
	public Value fromResultSet(DBResultSet rs, Field field, Integer index) throws SQLException {
		if (field == null) throw new NullPointerException("Field can not be null");
		String alias = field.getAlias();
		Value value = null;
		Types type = field.getType();
		if (type == Types.BOOLEAN) {
			String str = null;
			if (alias != null) str = rs.getString(alias);
			else str = rs.getString(index);
			value = new Value(str != null && str.equals("T"));
		} else if (type == Types.DECIMAL) {
			BigDecimal dec = null;
			if (alias != null) dec = rs.getBigDecimal(alias);
			else dec = rs.getBigDecimal(index);
			if (dec != null) {
				dec = dec.setScale(field.getDecimals(), RoundingMode.HALF_UP);
				value = new Value(dec);
			}
		} else if (type == Types.DOUBLE) {
			Double num = null;
			if (alias != null) num = rs.getDouble(alias);
			else num = rs.getDouble(index);
			value = new Value(num);
		} else if (type == Types.INTEGER) {
			Integer num = null;
			if (alias != null) num = rs.getInt(alias);
			else num = rs.getInt(index);
			value = new Value(num);
		} else if (type == Types.LONG) {
			Long num = null;
			if (alias != null) num = rs.getLong(alias);
			else num = rs.getLong(index);
			value = new Value(num);
		} else if (type == Types.DATE) {
			Date date = null;
			if (alias != null) date = rs.getDate(alias);
			else date = rs.getDate(index);
			if (date == null) {
				value = new Value((LocalDate) null);
			} else {
				value = new Value(date.toLocalDate());
			}
		} else if (type == Types.TIME) {
			Time time = null;
			if (alias != null) time = rs.getTime(alias);
			else time = rs.getTime(index);
			if (time == null) {
				value = new Value((LocalTime) null);
			} else {
				value = new Value(time.toLocalTime());
			}
		} else if (type == Types.TIMESTAMP) {
			Timestamp timestamp = null;
			if (alias != null) timestamp = rs.getTimestamp(alias);
			else timestamp = rs.getTimestamp(index);
			if (timestamp == null) {
				value = new Value((LocalDateTime) null);
			} else {
				value = new Value(timestamp.toLocalDateTime());
			}
		} else if (type == Types.STRING) {
			String str = null;
			if (alias != null) str = rs.getString(alias);
			else str = rs.getString(index);
			value = new Value(str);
		} else if (type == Types.BINARY) {
			byte[] bin = null;
			if (alias != null) bin = rs.getBytes(alias);
			else bin = rs.getBytes(index);
			value = new Value(bin);
		} else if (type == Types.ARRAY) {
			String str = null;
			if (alias != null) str = rs.getString(alias);
			else str = rs.getString(index);
			JSONObject json_obj = JSONObject.parse(str);
			Value[] values = Value.toValueArray(json_obj);
			value = new Value(values);
		} else if (type == Types.JSONOBJECT) {
			String str = null;
			if (alias != null) str = rs.getString(alias);
			else str = rs.getString(index);
			JSONObject json_obj = JSONObject.parse(str);
			value = new Value(json_obj);
		} else {
			throw new IllegalStateException("Should never come here");
		}
		return value;
	}

	/**
	 * Returns the CURRENT DATE function as a string.
	 * @return The CURRENT DATE function as a string.
	 */
	public abstract String getCurrentDate();

	/**
	 * Returns the CURRENT TIME function as a string.
	 * @return The CURRENT TIME function as a string.
	 */
	public abstract String getCurrentTime();

	/**
	 * Returns the CURRENT TIMESTAMP function as a string.
	 * @return The CURRENT TIMESTAMP function as a string.
	 */
	public abstract String getCurrentTimestamp();

	/**
	 * Return the JDBC driver class name, for example <i>org.mariadb.jdbc.Driver</i>
	 * for a MariaDB driver, <i>org.postgresql.Driver</i> for a PostgreSQL driver,
	 * or <i>oracle.jdbc.driver.OracleDriver</i> for an Oracle driver.
	 * @return The JDBC driver class name.
	 */
	public abstract String getDriverClassName();

	/**
	 * Return the driver protocol, for example <i>jdbc:mariadb://</i> for a MariaDB
	 * driver, <i>jdbc:postgresql://</i> for a PostgreSQL driver, or
	 * <i>jdbc:oracle:thin:@</i> for an Oracle driver.
	 * @return
	 */
	public abstract String getDriverProtocol();

	/**
	 * Return the field definition to use in a <i>CREATE TABLE</i>.
	 * @return The field definition.
	 * @param field The field.
	 */
	public abstract String getFieldDefinition(Field field);
	/**
	 * Returns the suffix part of a field definition, that is standard, with DEFAULT
	 * values and NOT NULL.
	 * @param field The argument field.
	 * @return The suffix part of the field definition.
	 */
	public String getFieldDefinitionSuffix(Field field) {
		StringBuilder b = new StringBuilder();
		if (!field.isNullable()) {
			b.append(" NOT NULL");
		}
		if (field.getDefaultCreateValue() != null) {
			b.append(" DEFAULT " + toStringSQL(field.getDefaultCreateValue()));
		} else {
			if (!field.isNullable()) {
				if (field.isDate()) b.append(" DEFAULT " + getCurrentDate());
				if (field.isTime()) b.append(" DEFAULT " + getCurrentTime());
				if (field.isTimestamp()) b.append(" DEFAULT " + getCurrentTimestamp());
			}
			if (field.isBoolean()) b.append(" DEFAULT 'F'");
			if (field.isNumber()) b.append(" DEFAULT 0");
		}
		return b.toString();
	}

	/**
	 * Returns the database metadata information.
	 * @param db The database engine.
	 * @return The metadata information class.
	 * @throws SQLException If an error occurs.
	 */
	public abstract DBMetaData getMetaData(DBEngine db) throws SQLException;
	
	/**
	 * Return the library type.
	 * @param columnType The column data type.
	 * @return The library type.
	 */
	public abstract Types getType(String columnType);

	/**
	 * Apply the value to an index position of a <i>DBPreparedStatement</i>.
	 * @param ps    Prepared statement.
	 * @param index Index.
	 * @param value Value to apply.
	 * @throws SQLException If an error occurs.
	 */
	public void toPreparedStatement(DBPreparedStatement ps, int index, Value value) throws SQLException {
		if (value.isNull()) {
			ps.setNull(index, value.getType());
			return;
		}
		Types type = value.getType();
		if (type == Types.BOOLEAN) {
			ps.setString(index, (value.getBoolean() ? "T" : "F"));
		} else if (type == Types.DECIMAL) {
			ps.setBigDecimal(index, value.getBigDecimal());
		} else if (type == Types.DOUBLE) {
			ps.setDouble(index, value.getDouble());
		} else if (type == Types.INTEGER) {
			ps.setInt(index, value.getInteger());
		} else if (type == Types.LONG) {
			ps.setLong(index, value.getLong());
		} else if (type == Types.DATE) {
			ps.setDate(index, Date.valueOf(value.getDate()));
		} else if (type == Types.TIME) {
			ps.setTime(index, Time.valueOf(value.getTime()));
		} else if (type == Types.TIMESTAMP) {
			ps.setTimestamp(index, Timestamp.valueOf(value.getTimestamp()));
		} else if (type == Types.STRING) {
			int length = value.getString().length();
			if (length <= FIXED_LENGTH) {
				ps.setString(index, value.getString());
			} else {
				String str = value.getString();
				ps.setCharacterStream(index, new StringReader(str));
			}
		} else if (type == Types.BINARY) {
			byte[] bytes = value.getBinary();
			if (bytes.length <= FIXED_LENGTH) {
				ps.setBytes(index, bytes);
			} else {
				ps.setBinaryStream(index, new ByteArrayInputStream(bytes));
			}
		} else if (type == Types.ARRAY) {
			String str = Value.toJSONObject(value.getArray()).toString();
			ps.setCharacterStream(index, new StringReader(str));
		} else if (type == Types.JSONOBJECT) {
			String str = value.getJSONObject().toString();
			ps.setCharacterStream(index, new StringReader(str));
		} else {
			throw new IllegalStateException("Should never come here");
		}
	}

	/**
	 * Convert the argument value to a string that can be used in a SELECT, INSERT,
	 * UPDATE or WHERE query. Note that type <i>BINARY</i> issues an error.
	 * @param value The value to format as a string.
	 * @return The formatted string.
	 */
	public abstract String toStringSQL(Value value);
}
