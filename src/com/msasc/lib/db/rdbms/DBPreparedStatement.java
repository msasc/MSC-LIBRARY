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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import com.msasc.lib.db.Types;

/**
 * Simplified <i>PreparedStatement</i>.
 * @author Miquel Sas
 */
public class DBPreparedStatement extends DBAbstractStatement {

	/** Underlying driver <i>PreparedStatement</i>. */
	private PreparedStatement drv_ps;

	/**
	 * Constructor.
	 * @param cn     The parent <i>DBConnection</i>.
	 * @param drv_ps The underlying <i>PreparedStatement</i>.
	 */
	DBPreparedStatement(DBConnection cn, PreparedStatement drv_ps) {
		super(cn, drv_ps);
		this.drv_ps = drv_ps;
	}

	/**
	 * Execute a SELECT query.
	 * @return A simplified forward only <i>ResultSet</i>.
	 * @throws SQLException If an error occurs.
	 */
	public DBResultSet executeQuery() throws SQLException {
		if (getFetchSize() == 0) setFetchSize(getConnection().getEngine().getFetchSize());
		ResultSet drv_rs = drv_ps.executeQuery();
		DBResultSet rs = new DBResultSet(this, drv_rs);
		add(rs);
		return rs;
	}
	/**
	 * Execute an UPDATE query.
	 * @return The number of updated rows or zero if not applicable.
	 * @throws SQLException If an error occurs.
	 */
	public int executeUpdate() throws SQLException { return drv_ps.executeUpdate(); }

	/**
	 * Set the parameter value.
	 * @param i Parameter index.
	 * @param x Parameter value.
	 * @throws SQLException If an error occurs.
	 */
	public void setBigDecimal(int i, BigDecimal x) throws SQLException { drv_ps.setBigDecimal(i, x); }
	/**
	 * Set the parameter value.
	 * @param i Parameter index.
	 * @param x Parameter value.
	 * @throws SQLException If an error occurs.
	 */
	public void setBytes(int i, byte[] x) throws SQLException { drv_ps.setBytes(i, x); }
	/**
	 * Set the parameter value.
	 * @param i Parameter index.
	 * @param x Parameter value.
	 * @throws SQLException If an error occurs.
	 */
	public void setBinaryStream(int i, InputStream x) throws SQLException { drv_ps.setBinaryStream(i, x); }
	/**
	 * Set the parameter value.
	 * @param i Parameter index.
	 * @param x Parameter value.
	 * @throws SQLException If an error occurs.
	 */
	public void setBoolean(int i, boolean x) throws SQLException { drv_ps.setBoolean(i, x); }
	/**
	 * Set the parameter value.
	 * @param i Parameter index.
	 * @param x Parameter value.
	 * @throws SQLException If an error occurs.
	 */
	public void setInt(int i, int x) throws SQLException { drv_ps.setInt(i, x); }
	/**
	 * Set the parameter value.
	 * @param i Parameter index.
	 * @param x Parameter value.
	 * @throws SQLException If an error occurs.
	 */
	public void setLong(int i, long x) throws SQLException { drv_ps.setLong(i, x); }
	/**
	 * Set the parameter value.
	 * @param i Parameter index.
	 * @param x Parameter value.
	 * @throws SQLException If an error occurs.
	 */
	public void setDouble(int i, double x) throws SQLException { drv_ps.setDouble(i, x); }
	/**
	 * Set the parameter value.
	 * @param i Parameter index.
	 * @param x Parameter value.
	 * @throws SQLException If an error occurs.
	 */
	public void setString(int i, String x) throws SQLException { drv_ps.setString(i, x); }
	/**
	 * Set the parameter value.
	 * @param i Parameter index.
	 * @param x Parameter value.
	 * @throws SQLException If an error occurs.
	 */
	public void setDate(int i, Date x) throws SQLException { drv_ps.setDate(i, x); }
	/**
	 * Set the parameter value.
	 * @param i Parameter index.
	 * @param x Parameter value.
	 * @throws SQLException If an error occurs.
	 */
	public void setTime(int i, Time x) throws SQLException { drv_ps.setTime(i, x); }
	/**
	 * Set the parameter value.
	 * @param i Parameter index.
	 * @param x Parameter value.
	 * @throws SQLException If an error occurs.
	 */
	public void setTimestamp(int i, Timestamp x) throws SQLException { drv_ps.setTimestamp(i, x); }
	/**
	 * Set the parameter value.
	 * @param i Parameter index.
	 * @param x Parameter value.
	 * @throws SQLException If an error occurs.
	 */
	public void setCharacterStream(int i, Reader reader) throws SQLException {
		drv_ps.setCharacterStream(i, reader);
	}

	/**
	 * Set the index value to ull.
	 * @param i Parameter index.
	 * @param type The type.
	 * @throws SQLException If an error occurs.
	 */
	public void setNull(int i, Types type) throws SQLException {
		if (type == null) throw new NullPointerException("Type can not be null");
		int sqlType = 0;
		switch (type) {
		case BOOLEAN:
			sqlType = java.sql.Types.VARCHAR;
			break;
		case DECIMAL:
			sqlType = java.sql.Types.DECIMAL;
			break;
		case DOUBLE:
			sqlType = java.sql.Types.DOUBLE;
			break;
		case INTEGER:
			sqlType = java.sql.Types.INTEGER;
			break;
		case LONG:
			sqlType = java.sql.Types.BIGINT;
			break;
		case DATE:
			sqlType = java.sql.Types.DATE;
			break;
		case TIME:
			sqlType = java.sql.Types.TIME;
			break;
		case TIMESTAMP:
			sqlType = java.sql.Types.TIMESTAMP;
			break;
		case STRING:
			sqlType = java.sql.Types.LONGVARCHAR;
			break;
		case BINARY:
			sqlType = java.sql.Types.LONGVARBINARY;
			break;
		case ARRAY:
			sqlType = java.sql.Types.LONGVARCHAR;
			break;
		case JSONOBJECT:
			sqlType = java.sql.Types.LONGVARCHAR;
			break;
		}
		drv_ps.setNull(i, sqlType);
	}
}
