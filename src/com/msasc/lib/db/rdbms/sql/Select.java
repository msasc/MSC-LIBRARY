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
package com.msasc.lib.db.rdbms.sql;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.msasc.lib.db.Field;
import com.msasc.lib.db.FieldList;
import com.msasc.lib.db.Order;
import com.msasc.lib.db.Table;
import com.msasc.lib.db.Types;
import com.msasc.lib.db.rdbms.DBEngine;
import com.msasc.lib.db.rdbms.SQL;
import com.msasc.lib.util.Lists;
import com.msasc.lib.util.Strings;

/**
 * Builder of SELECT queries, handling the type of each result column to facilitate the subsequent
 * reading of the results.
 * @author Miquel Sas
 */
public class Select extends SQL {

	/**
	 * Join information.
	 */
	private class Join {
		/** A segment of fields join. */
		private class Segment {
			private String localFieldName;
			private String foreignFieldName;
			private Segment(String localFieldName, String foreignFieldName) {
				this.localFieldName = localFieldName;
				this.foreignFieldName = foreignFieldName;
			}
		}

		/** Join type, INNER, LEFT or RIGHT. */
		private String type;
		/** Local table alias. */
		private String localTableAlias;
		/** Foreign table name. */
		private String foreignTableName;
		/** Foreign table alias. */
		private String foreignTableAlias;

		/** List of segments. */
		private List<Segment> segments = new ArrayList<>();

		/**
		 * Constructor.
		 * @param type              The type of join, INNER, LEFT, RIGHT or CROSS.
		 * @param localTableAlias   Local table alias.
		 * @param foreignTableName  Foreign table name.
		 * @param foreignTableAlias Foreign table alias.
		 */
		private Join(String type, String localTableAlias, String foreignTableName, String foreignTableAlias) {

			if (type == null) throw new NullPointerException("Type can not be null");
			if (localTableAlias == null) throw new NullPointerException("Local table alias can not be null");
			if (foreignTableName == null) throw new NullPointerException("Foreign table name can not be null");
			if (foreignTableAlias == null) throw new NullPointerException("Foreign table alias can not be null");

			type = type.toUpperCase().trim();
			if (!Strings.in(type, "INNER", "LEFT", "RIGHT")) {
				throw new IllegalArgumentException("Invalid join type: " + type);
			}
			this.type = type;
			this.localTableAlias = localTableAlias;
			this.foreignTableName = foreignTableName;
			this.foreignTableAlias = foreignTableAlias;
		}
		/**
		 * Add a link condition or segment.
		 * @param localFieldName   Local field name, without table alias.
		 * @param foreignFieldName Foreign field name, without table alias.
		 */
		private void link(String localFieldName, String foreignFieldName) {
			if (localFieldName == null) throw new NullPointerException("Local field name can not be null");
			if (foreignFieldName == null) throw new NullPointerException("Foreign field name can not be null");
			segments.add(new Segment(localFieldName, foreignFieldName));
		}
	}

	/** List of result columns. */
	private List<Field> fields = new ArrayList<>();

	/** Master table schema. */
	private String tableSchema;
	/** Master table name. */
	private String tableName;
	/** Master table alias. */
	private String tableAlias;

	/** From select query. */
	private Select selectFrom;
	/** From select alias. */
	private String selectAlias;

	/** List of joins or relations. */
	private List<Join> joins = new ArrayList<>();

	/** Master WHERE conditions. */
	private Where where;
	/** Group by list. */
	private List<String> groupBy = new ArrayList<>();
	/** HAVING conditions. */
	private Having having;
	/** Order by part. */
	private LinkedHashMap<String, Boolean> orderByMap = new LinkedHashMap<>();

	/**
	 * Constructor.
	 * @param db The database engine.
	 */
	public Select(DBEngine db) {
		super(db);
		this.where = new Where(db);
		this.having = new Having(db);
	}

	public FieldList getFields() { return new FieldList(fields); }

	/**
	 * Add a field as a result column.
	 * @param field The field.
	 */
	public void select(Field field) {
		fields.add(field);
	}
	/**
	 * Add a field as a result column.
	 * @param field The field.
	 * @param alias An optional alias different from the current field alias.
	 */
	public void select(Field field, String alias) {
		field = new Field(field);
		field.setAlias(alias);
		select(field);
	}
	/**
	 * Add the list of fields.
	 * @param fields The list of fields to add as select part.
	 */
	public void select(FieldList fields) { fields.forEach(field -> select(field)); }
	/**
	 * Add an expression indicating an alias and a type.
	 * @param expr  The expression.
	 * @param alias The alias.
	 * @param type  The expected date type.
	 */
	public void select(String expr, String alias, Types type) { select(expr, alias, type, null); }
	/**
	 * Add an expression indicating an alias, a type and optionally the number of decimal places. A
	 * field is created to support the expression.
	 * @param expr     The expression.
	 * @param alias    The alias.
	 * @param type     The expected date type.
	 * @param decimals Optional number of decimal places.
	 */
	public void select(String expr, String alias, Types type, Integer decimals) {
		Field field = new Field();
		field.setName(alias);
		field.setAlias(alias);
		field.setType(type);
		if (decimals != null) field.setDecimals(decimals);
		field.setFunction(expr);
		select(field);
	}

	public void from(Select select, String alias) {
		valid(select == null, "NULL", "Select query can not be null");
		this.selectFrom = select;
		this.selectAlias = alias;
		this.tableSchema = null;
		this.tableName = null;
		this.tableAlias = null;
	}

	/**
	 * Set the from information part.
	 * @param tableSchema Master table schema.
	 * @param tableName   Master table name.
	 * @param tableAlias  Master table alias.
	 */
	public void from(String tableSchema, String tableName, String tableAlias) {
		this.tableSchema = tableSchema;
		this.tableName = tableName;
		this.tableAlias = tableAlias;
		this.selectFrom = null;
		this.selectAlias = null;
	}

	/**
	 * Set the from information part.
	 * @param masterTable The master table.
	 */
	public void from(Table masterTable) {
		this.tableSchema = masterTable.getSchema();
		this.tableName = masterTable.getName();
		this.tableAlias = masterTable.getAlias();
		this.selectFrom = null;
		this.selectAlias = null;
	}

	/**
	 * Add a new empty join, pending to append links to it.
	 * @param type              The type of join, INNER, LEFT, RIGHT or CROSS.
	 * @param localTableAlias   Local table alias.
	 * @param foreignTableName  Foreign table name.
	 * @param foreignTableAlias Foreign table alias.
	 */
	public void join(String type, String localTableAlias, String foreignTableName, String foreignTableAlias) {
		joins.add(new Join(type, localTableAlias, foreignTableName, foreignTableAlias));
	}
	/**
	 * /**
	 * Add a new empty join, pending to append links to it.
	 * @param type         The type of join, INNER, LEFT, RIGHT or CROSS.
	 * @param localTable   Local table.
	 * @param foreignTable Foreign table.
	 */
	public void join(String type, Table localTable, Table foreignTable) {
		join(type, localTable.getAlias(), foreignTable.getName(), foreignTable.getAlias());
	}
	/**
	 * Add a link condition to the last join.
	 * @param localField  Local field.
	 * @param forignField Foreign field.
	 */
	public void link(Field localField, Field forignField) {
		link(localField.getName(), forignField.getName());
	}
	/**
	 * Add a link condition to the last join.
	 * @param localFieldName   Local field name, without table alias.
	 * @param foreignFieldName Foreign field name, without table alias.
	 */
	public void link(String localFieldName, String foreignFieldName) {
		if (joins.isEmpty()) throw new IllegalStateException("No join available to set links");
		Lists.getLast(joins).link(localFieldName, foreignFieldName);
	}

	/**
	 * Access to the WHERE clause to add segments, conditions, or entire WHERE clauses.
	 * @return The WHERE clause.
	 */
	public Where where() { return where; }
	/**
	 * Add a WHERE list of filter conditions.
	 * @param where The WHERE clause to add.
	 */
	public void where(Where where) { this.where.where(where); }

	/**
	 * Add a group by field.
	 * @param field The field to add.
	 */
	public void groupBy(Field field) { groupBy(field.getNameGroupBy()); }
	/**
	 * Add a group by expression.
	 * @param expr The expression.
	 */
	public void groupBy(String expr) { groupBy.add(expr); }

	/**
	 * Access to the HAVING clause to add segments, conditions, or entire WHERE clauses.
	 * @return The HAVING clause.
	 */
	public Having having() { return having; }
	/**
	 * Add a HAVING list of filter conditions to the HAVING clause.
	 * @param having The HAVING clause to add.
	 */
	public void having(Having having) { this.having.having(having); }

	/**
	 * Add an ORDER BY expression.
	 * @param field The field.
	 */
	public void orderBy(Field field) { orderBy(field.getNameOrderBy(), true); }
	/**
	 * Add an ORDER BY expression.
	 * @param field The field.
	 * @param asc   Ascending flag.
	 */
	public void orderBy(Field field, boolean asc) { orderBy(field.getNameOrderBy(), asc); }
	/**
	 * Set an order by giving the order.
	 * @param order The order.
	 */
	public void orderBy(Order order) {
		for (Order.Segment seg : order.getSegments()) {
			orderBy(seg.getField(), seg.isAscending());
		}
	}
	/**
	 * Add an ORDER BY expression.
	 * @param expr The expression.
	 */
	public void orderBy(String expr) { orderBy(expr, true); }
	/**
	 * Add an ORDER BY expression.
	 * @param expr The expression.
	 * @param asc  Ascending flag.
	 */
	public void orderBy(String expr, boolean asc) { orderByMap.put(expr, asc); }

	/**
	 * Returns the query optionally formatted to be readable.
	 * @param formatted A boolean indicating whether the query should be formatted.
	 * @return The query.
	 */
	@Override
	public String toSQL(boolean formatted) {
		StringBuilder sql = new StringBuilder(512);

		/* Select part. */

		if (formatted) {
			sql.append(Strings.blank(PAD - "SELECT ".length()));
		}
		sql.append("SELECT ");
		for (int i = 0; i < fields.size(); i++) {
			if (i > 0 && formatted) {
				sql.append("\n");
				sql.append(Strings.blank(PAD));
			}
			Field field = fields.get(i);
			if (field.isPersistent() || field.isVirtual()) {
				sql.append(field.getNameSelect());
				sql.append(" AS ");
				sql.append(field.getAlias());
			} else {
				sql.append(db.toStringSQL(field.getDefaultValue()));
				sql.append(" AS ");
				sql.append(field.getAlias());
			}
			if (i < fields.size() - 1) sql.append(", ");
		}

		/* From part. */

		if (tableName != null || selectFrom != null) {
			if (formatted) {
				sql.append("\n");
				sql.append(Strings.blank(PAD - " FROM ".length()));
			}
			sql.append(" FROM ");
			if (tableName != null) {
				if (tableSchema != null) {
					sql.append(tableSchema);
					sql.append(".");
				}
				sql.append(tableName);
				sql.append(" ");
				sql.append(tableAlias);
			} else {
				sql.append("(");
				sql.append(selectFrom.toSQL(formatted));
				sql.append(")");
				sql.append(" AS ");
				sql.append(selectAlias);
			}
		}

		/* Join part. */

		if (!joins.isEmpty()) {
			for (Join join : joins) {
				String prefix = " " + join.type + " JOIN ";
				if (formatted) {
					sql.append("\n");
					sql.append(Strings.blank(PAD - prefix.length()));
				}
				sql.append(prefix);
				sql.append(join.foreignTableName);
				sql.append(" ");
				sql.append(join.foreignTableAlias);
				for (int i = 0; i < join.segments.size(); i++) {
					if (formatted) {
						sql.append("\n");
						if (i == 0) sql.append(Strings.blank(PAD - " ON ".length()));
						else sql.append(Strings.blank(PAD - " AND ".length()));
					}
					if (i == 0) sql.append(" ON ");
					else sql.append(" AND ");
					Join.Segment seg = join.segments.get(i);
					sql.append(join.localTableAlias);
					sql.append(".");
					sql.append(seg.localFieldName);
					sql.append(" = ");
					sql.append(join.foreignTableAlias);
					sql.append(".");
					sql.append(seg.foreignFieldName);
				}
			}
		}

		/* Where part. */

		if (!where.isEmpty()) {
			if (formatted) {
				sql.append("\n");
				sql.append(Strings.blank(PAD - " WHERE ".length()));
			}
			sql.append(" WHERE ");
			sql.append(where.toSQL(formatted));
		}

		/* Group by part. */

		if (!groupBy.isEmpty()) {
			if (formatted) {
				sql.append("\n");
				sql.append(Strings.blank(PAD - " GROUP BY ".length()));
			}
			sql.append(" GROUP BY ");
			for (int i = 0; i < groupBy.size(); i++) {
				String expr = groupBy.get(i);
				if (i > 0 && formatted) sql.append("\n" + Strings.blank(PAD));
				sql.append(expr);
				if (i < groupBy.size() - 1) sql.append(", ");
			}
		}

		/* Having part. */

		if (!having.isEmpty()) {
			if (formatted) {
				sql.append("\n");
				sql.append(Strings.blank(PAD - " HAVING ".length()));
			}
			sql.append(" HAVING ");
			sql.append(having.toSQL(formatted));
		}

		/* Order by part. */

		if (!orderByMap.isEmpty()) {
			if (formatted) {
				sql.append("\n");
				sql.append(Strings.blank(PAD - " ORDER BY ".length()));
			}
			sql.append(" ORDER BY ");
			List<String> orderBy = new ArrayList<>(orderByMap.keySet());
			for (int i = 0; i < orderBy.size(); i++) {
				if (i > 0 && formatted) sql.append("\n" + Strings.blank(PAD));
				String expr = orderBy.get(i);
				boolean asc = orderByMap.get(expr);
				sql.append(expr);
				sql.append(" ");
				sql.append(asc ? "ASC" : "DESC");
				if (i < orderBy.size() - 1) sql.append(", ");
			}
		}

		return sql.toString();
	}

	/**
	 * Validation helper.
	 * @param error   Error as a boolean
	 * @param type    Type NULL, ARG, STAT.
	 * @param message Error message.,
	 */
	private void valid(boolean error, String type, String message) {
		if (error) {
			if (type.equals("NULL")) {
				throw new NullPointerException(message);
			}
			if (type.equals("ARG")) {
				throw new IllegalArgumentException(message);
			}
			if (type.equals("STAT")) {
				throw new IllegalStateException(message);
			}
		}
	}
}
