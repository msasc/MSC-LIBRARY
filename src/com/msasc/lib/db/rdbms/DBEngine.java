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

import java.sql.SQLException;

import com.msasc.lib.db.Field;
import com.msasc.lib.db.FieldList;
import com.msasc.lib.db.Order;
import com.msasc.lib.db.OrderKey;
import com.msasc.lib.db.Record;
import com.msasc.lib.db.RecordList;
import com.msasc.lib.db.RecordSet;
import com.msasc.lib.db.Table;
import com.msasc.lib.db.Value;
import com.msasc.lib.db.rdbms.sql.AddPrimaryKey;
import com.msasc.lib.db.rdbms.sql.CreateSchema;
import com.msasc.lib.db.rdbms.sql.CreateTable;
import com.msasc.lib.db.rdbms.sql.DropTable;
import com.msasc.lib.db.rdbms.sql.Select;
import com.msasc.lib.task.progress.ProgressListener;

/**
 * A database engine represent a back-end RDBMS system to which we connect
 * through a JDBC driver.
 * 
 * @author Miquel Sas
 */
public class DBEngine {

	/** Connection pool. */
	private DBConnectionPool connectionPool;
	/** Database engine adapter. */
	private DBEngineAdapter adapter;

	/**
	 * Constructor.
	 * @param adapter  <i>DBEngineAdapter</i>.
	 * @param database Database.
	 * @param user     User.
	 * @param password Password.
	 */
	public DBEngine(DBEngineAdapter adapter, String database, String user, String password) {
		this.adapter = adapter;
		this.connectionPool = new DBConnectionPool(
				adapter.getDriverClassName(),
				adapter.getDriverProtocol(),
				database,
				user,
				password);
	}

	/**
	 * Executes an add primary key statement.
	 * @param table The table.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int execAddPrimaryKey(Table table) throws SQLException {
		return executeUpdate(new AddPrimaryKey(this, table));
	}

	/**
	 * Executes a create schema statement.
	 * @param schema The schema.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int execCreateSchema(String schema) throws SQLException {
		return executeUpdate(new CreateSchema(this, schema));
	}

	/**
	 * Executes a create table statement.
	 * @param table The table.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int execCreateTable(Table table) throws SQLException {
		return executeUpdate(new CreateTable(this, table));
	}

	/**
	 * Executes a drop table statement.
	 * @param table The table.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int execDropTable(Table table) throws SQLException {
		return executeUpdate(new DropTable(this, table));
	}

	/**
	 * Count the number of rows in the select query.
	 * @param sql The select query.
	 * @return The number of rows.
	 * @throws SQLException
	 */
	public long execSelectCount(String sql) throws SQLException {
		DBConnection cn = null;
		DBStatement st = null;
		DBResultSet rs = null;
		long count = -1;
		try {
			cn = getConnection();
			st = cn.createStatement();
			rs = st.executeQuery("SELECT COUNT(*) FROM (" + sql + ") AS COUNTER");
			if (rs.next()) {
				count = rs.getLong(1);
			}
			rs.close();
		} finally {
			if (rs != null && !rs.isClosed()) rs.close();
			if (st != null && !st.isClosed()) st.close();
			if (cn != null && !cn.isClosed()) cn.close();
		}
		return count;
	}

	/**
	 * Select a record from a table using the primary key.
	 * @param table The table
	 * @param pk    The primary key values.
	 * @return The record or null.
	 * @throws SQLException If such an error occurs.
	 */
	public Record execSelectRecord(Table table, OrderKey pk) throws SQLException {
		RecordSet rs = execSelectRecordSet(table, table.getPrimaryKey(), pk);
		if (!rs.isEmpty()) return rs.get(0);
		return null;
	}

	/**
	 * Execute a select query and return the corresponding recordset.
	 * @param select The select query.
	 * @return The recordset
	 * @throws SQLException If such an error occurs.
	 */
	public RecordSet execSelectRecordSet(Select select) throws SQLException {
		String sql = select.toSQL();
		FieldList fields = select.getFields();
		return execSelectRecordSet(sql, fields);
	}
	/**
	 * Execute a select query and return the corresponding recordset.
	 * @param select   The select query.
	 * @param progress A progress listener to report the progress of the operation.
	 * @return The recordset
	 * @throws SQLException If such an error occurs.
	 */
	public RecordSet execSelectRecordSet(Select select, ProgressListener progress) throws SQLException {
		String sql = select.toSQL();
		FieldList fields = select.getFields();
		return execSelectRecordSet(sql, fields, progress);
	}
	/**
	 * Execute a select query and return the corresponding recordset.
	 * @param sql    The select query.
	 * @param fields The corresponding list of fields.
	 * @return The recordset
	 * @throws SQLException If such an error occurs.
	 */
	public RecordSet execSelectRecordSet(String sql, FieldList fields) throws SQLException {
		return execSelectRecordSet(sql, fields, null);
	}
	/**
	 * Execute a select query and return the corresponding recordset.
	 * @param sql      The select query.
	 * @param fields   The corresponding list of fields.
	 * @param progress A progress listener to report the progress of the operation.
	 * @return The recordset
	 * @throws SQLException If such an error occurs.
	 */
	public RecordSet execSelectRecordSet(String sql, FieldList fields, ProgressListener progress) throws SQLException {
		DBIterator iter = new DBIterator(this);
		double totalWork = 0;
		try {
			if (progress != null) {
				progress.notityStart();
				progress.setIndeterminate(0, true);
				progress.notifyMessage(0, "Preparing");
				totalWork = execSelectCount(sql);
			}
			iter.select(sql, fields);
			if (progress != null) progress.setIndeterminate(0, false);
			RecordList recordSet = new RecordList();
			recordSet.setFieldList(fields);
			while (iter.hasNext()) {
				if (progress != null) {
					progress.notifyProgress(0, 1.0, totalWork);
				}
				recordSet.add(iter.next());
			}
			iter.close();
			if (progress != null) progress.notifyEnd();
			return recordSet;
		} catch (Exception exc) {
			if (progress != null) progress.notifyEnd();
			exc.printStackTrace();
			return null;
		}
	}
	/**
	 * Execute a select query and return the corresponding recordset.
	 * @param sql      The select query.
	 * @param fields   The corresponding list of fields.
	 * @param progress A progress listener to report the progress of the operation.
	 * @return The recordset
	 * @throws SQLException If such an error occurs.
	 */
	public RecordSet execSelectRecordSet2(String sql, FieldList fields, ProgressListener progress) throws SQLException {

		DBConnection cn = null;
		DBStatement st = null;
		DBResultSet rs = null;

		double totalWork = 0;

		try {

			if (progress != null) {
				progress.notityStart();
			}

			cn = getConnection();
			st = cn.createStatement();

			if (progress != null) {
				progress.setIndeterminate(0, true);
				progress.notifyMessage(0, "Preparing");
				rs = st.executeQuery("SELECT COUNT(*) FROM (" + sql + ") AS COUNTER");
				if (rs.next()) {
					totalWork = rs.getDouble(1);
				}
				rs.close();
			}

			rs = st.executeQuery(sql);

			if (progress != null) {
				progress.setIndeterminate(0, false);
			}

			RecordList recordSet = new RecordList();
			recordSet.setFieldList(fields);
			while (rs.next()) {

				if (progress != null) {
					progress.notifyMessage(0, "Retrieving");
					progress.notifyProgress(0, 1.0, totalWork);
				}

				Record record = readRecord(rs, fields);
				recordSet.add(record);
			}

			if (progress != null) {
				progress.notifyEnd();
			}

			return recordSet;

		} catch (Exception exc) {
			if (progress != null) {
				progress.notifyEnd();
			}
			exc.printStackTrace();
			return null;
		} finally {
			if (rs != null && !rs.isClosed()) rs.close();
			if (st != null && !st.isClosed()) st.close();
			if (cn != null && !cn.isClosed()) cn.close();
		}
	}
	/**
	 * Execute a select query, with optional order and filter key values.
	 * @param table The table.
	 * @param order The order.
	 * @param key   The key values
	 * @return The recordset.
	 * @throws SQLException
	 */
	public RecordSet execSelectRecordSet(Table table, Order order, OrderKey key) throws SQLException {
		Select select = new Select(this);
		select.select(table.getFields());
		select.from(table);
		if (order != null && key != null) select.where().where(table, order, key);
		if (order != null) select.orderBy(order);
		return execSelectRecordSet(select);
	}

	/**
	 * Execute an update statement.
	 * @param sql The update SQL.
	 * @return The number of rows affected or zero.
	 * @throws SQLException If an error occurs.
	 */
	public int executeUpdate(SQL sql) throws SQLException {
		return executeUpdate(sql.toSQL());
	}
	/**
	 * Execute an update statement.
	 * @param sql The update SQL.
	 * @return The number of rows affected or zero.
	 * @throws SQLException If an error occurs.
	 */
	public int executeUpdate(String sql) throws SQLException {
		DBConnection cn = null;
		DBStatement st = null;
		int count = -1;
		try {
			cn = getConnection();
			st = cn.createStatement();
			count = st.executeUpdate(sql);
			return count;
		} finally {
			if (st != null && !st.isClosed()) st.close();
			if (cn != null && cn.isClosed()) cn.close();
		}
	}

	/**
	 * Retrieve a value from a result set by field alias.
	 * @param rs    The result set.
	 * @param field The field.
	 * @return The value.
	 * @throws SQLException If an error occurs.
	 */
	public Value fromResultSet(DBResultSet rs, Field field) throws SQLException {
		return getAdapter().fromResultSet(rs, field, null);
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
		return getAdapter().fromResultSet(rs, field, index);
	}

	/**
	 * Return the <i>DBEngineAdapter</i>.
	 * @return The <i>DBEngineAdapter</i>.
	 */
	public DBEngineAdapter getAdapter() { return adapter; }

	/**
	 * Returns a connection free to use.
	 * @return The connection.
	 * @throws SQLException If an error occurs.
	 */
	public DBConnection getConnection() throws SQLException { return connectionPool.getConnection(); }
	/**
	 * Returns the database metadata.
	 * @return The database metadata
	 * @throws SQLException If an error occurs.
	 */
	public DBMetaData getMetaData() throws SQLException { return adapter.getMetaData(this); }
	/**
	 * Close this database connection by closing the associated connection pool and
	 * related connections.
	 * @throws SQLException
	 */
	public void close() throws SQLException { connectionPool.close(); }
	/**
	 * Read a record from a ResultSet.
	 * @param rs     The source result set
	 * @param fields The field list
	 * @return The record.
	 * @throws SQLException If such an error occurs.
	 */
	public Record readRecord(DBResultSet rs, FieldList fields) throws SQLException {
		Record record = new Record(fields);
		for (Field field : fields) {
			Value value;
			if (field.isPersistent() || field.isVirtual()) {
				value = getAdapter().fromResultSet(rs, field);
			} else {
				value = field.getDefaultValue();
			}
			record.setValue(field.getAlias(), value);
		}
		return record;
	}

	/**
	 * Reads the correspondent record set.
	 * @param rs     The JDBC result set
	 * @param fields The applying field list
	 * @return The record set.
	 * @throws SQLException If an SQL error occurs.
	 */
	public RecordSet readRecordSet(DBResultSet rs, FieldList fields) throws SQLException {
		RecordList recordSet = new RecordList();
		recordSet.setFieldList(fields);
		while (rs.next()) {
			Record record = readRecord(rs, fields);
			recordSet.add(record);
		}
		return recordSet;
	}

	/**
	 * Convert the argument value to a string that can be used in a SELECT, INSERT,
	 * UPDATE or WHERE query. Note that type <i>BINARY</i> issues an error.
	 * @param value The value to format as a string.
	 * @return The formatted string.
	 */
	public String toStringSQL(Value value) { return adapter.toStringSQL(value); }
}
