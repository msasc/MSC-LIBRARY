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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.msasc.lib.db.Field;
import com.msasc.lib.db.Value;
import com.msasc.lib.db.rdbms.DBEngine;
import com.msasc.lib.db.rdbms.SQL;
import com.msasc.lib.util.Lists;
import com.msasc.lib.util.Strings;
import com.msasc.lib.util.iterators.ArrayIterator;
import com.msasc.lib.util.iterators.FunctionIterator;

/**
 * Builder of filter clauses.
 * @author Miquel Sas
 */
public abstract class Filter extends SQL {

	/**
	 * A condition. Conditions are chained within a segment.
	 */
	private static class Condition {
		/** Condition type. */
		private Type type;
		/** Logical operator. */
		private String logOp;
		/** Left expression. */
		private String leftExpr;
		/** Comparison operator. */
		private String compOp;
		/** Right expression. */
		private String rightExpr;
		/** Select object in EXISTS conditions. */
		private Select select;
		/**
		 * Copy constructor.
		 * @param c The condition to copy.
		 */
		private Condition(Condition c) { this(c.type, c.logOp, c.leftExpr, c.compOp, c.rightExpr); }
		/**
		 * Constructor.
		 * @param type      Condition type.
		 * @param logOp     Logical operator.
		 * @param leftExpr  Left expression.
		 * @param compOp    Comparison operator.
		 * @param rightExpr Right expression.
		 */
		private Condition(Type type, String logOp, String leftExpr, String compOp, String rightExpr) {
			this.type = type;
			this.logOp = logOp;
			this.leftExpr = leftExpr;
			this.compOp = compOp;
			this.rightExpr = rightExpr;
		}
	}
	/**
	 * A segment, that chains a list of conditions.
	 */
	private static class Segment {
		/*** Logical operator to link segments. */
		private String logOp;
		/** List of conditions. */
		private List<Condition> conditions = new ArrayList<>();
		/**
		 * Copy constructor.
		 * @param s The segment to copy.
		 */
		private Segment(Segment s) {
			this.logOp = s.logOp;
			for (Condition c : s.conditions) { conditions.add(new Condition(c)); }
		}
		/**
		 * Constructor.
		 * @param logOp Logical operator.
		 */
		private Segment(String logOp) { this.logOp = logOp; }
		/**
		 * Add a condition to the list of conditions.
		 * @param type      Condition type.
		 * @param logOp     Logical operator to chain the condition.
		 * @param leftExpr  Left expression of the condition.
		 * @param compOp    Comparison operator.
		 * @param rightExpr Right expression of the condition.
		 */
		private void add(Type type, String logOp, String leftExpr, String compOp, String rightExpr) {
			conditions.add(new Condition(type, logOp, leftExpr, compOp, rightExpr));
		}
		/**
		 * Check whether the segment is empty.
		 * @return A boolean.
		 */
		private boolean isEmpty() { return conditions.isEmpty(); }
	}
	/** Condition types. */
	private enum Type {
		COMPARE, EXISTS, IN, LIKE, IS_NULL
	}

	/** Supported comparison operators. */
	private static final String[] comp_ops = new String[] {
			"=", ">", ">=", "<", "<=", "!="
	};
	/** Supported existence operators. */
	private static final String[] exists_ops = new String[] {
			"EXISTS", "NOT EXISTS"
	};
	/** Supported inclusion operators. */
	private static final String[] in_ops = new String[] {
			"IN", "NOT IN"
	};
	/** Supported like operators. */
	private static final String[] like_ops = new String[] {
			"LIKE", "NOT LIKE"
	};
	/** Supported logical operators. */
	private static final String[] log_ops = new String[] {
			"AND", "AND NOT", "OR", "OR NOT"
	};
	/** Supported null operators. */
	private static final String[] null_ops = new String[] {
			"IS NULL", "IS NOT NULL"
	};

	/** List of segments. */
	private List<Segment> segments = new ArrayList<>();

	/**
	 * Constructor.
	 * @param db The underlying database engine.
	 */
	public Filter(DBEngine db) {
		super(db);
		segments.add(new Segment((String) null));
	}

	/**
	 * Add the first segment condition of type null option.
	 * @param field  The field.
	 * @param nullOp The is null operator.
	 */
	public void condition(Field field, String null_op) {
		condition(field.getNameParent(), null_op);
	}
	/**
	 * Add a chained condition of type comparison or like.
	 * @param field           The field.
	 * @param comp_or_like_op Comparison or like operator.
	 * @param value           The value to compare with.
	 */
	public void condition(Field field, String comp_or_like_op, Value value) {
		condition(field.getNameParent(), comp_or_like_op, db.toStringSQL(value));
	}
	/**
	 * Add a chained condition of type comparison or like.
	 * @param field  The field.
	 * @param in_op  Inclusion operator.
	 * @param values List of values.
	 */
	public void condition(Field field, String in_op, Value... values) {
		validate(in_op, in_ops);
		condition(field.getNameParent(), in_op, toStringList(values));
	}
	/**
	 * Add a chained condition of type comparison or like.
	 * @param field  The field.
	 * @param in_op  Inclusion operator.
	 * @param values List of values.
	 */
	public void condition(Field field, String in_op, Collection<Value> values) {
		validate(in_op, in_ops);
		condition(field.getNameParent(), in_op, toStringList(values.iterator()));
	}
	/**
	 * Add a chained condition of type null option.
	 * @param log_op  Logical operator.
	 * @param field   The field.
	 * @param null_op The is null operator.
	 */
	public void condition(String log_op, Field field, String null_op) {
		if (field == null) throw new NullPointerException("Field can not be null");
		condition(log_op, field.getNameParent(), null_op);
	}
	/**
	 * Add a chained condition of type comparison or like.
	 * @param log_op          Logical operator.
	 * @param field           The field.
	 * @param comp_or_like_op Comparison or like operator.
	 * @param value           The value to compare with.
	 */
	public void condition(String log_op, Field field, String comp_or_like_op, Value value) {
		condition(log_op, field.getNameParent(), comp_or_like_op, db.toStringSQL(value));
	}
	/**
	 * Add a chained condition of type comparison or like.
	 * @param log_op Logical operator.
	 * @param field  The field.
	 * @param in_op  Inclusion operator.
	 * @param values List of values.
	 */
	public void condition(String log_op, Field field, String in_op, Value... values) {
		validate(in_op, in_ops);
		condition(log_op, field.getNameParent(), in_op, toStringList(values));
	}
	/**
	 * Add a chained condition of type comparison or like.
	 * @param log_op Logical operator.
	 * @param field  The field.
	 * @param in_op  Inclusion operator.
	 * @param values Collection of values.
	 */
	public void condition(String log_op, Field field, String in_op, Collection<Value> values) {
		validate(in_op, in_ops);
		condition(log_op, field.getNameParent(), in_op, toStringList(values.iterator()));
	}

	/**
	 * Add an EXISTS condition with en entire SELECT object.
	 * @param log_op
	 * @param exists_op
	 * @param select
	 */
	public void condition(String log_op, String exists_op, Select select) {
		validate(log_op, log_ops);
		validate(exists_op, exists_ops);
		if (select == null) throw new NullPointerException("Null select");
		condition(log_op, exists_op, select.toSQL());
		List<Condition> conditions = Lists.getLast(segments).conditions;
		Lists.getLast(conditions).select = select;
	}

	/**
	 * Add a condition to the last segment.
	 * @param args List of arguments.
	 */
	public void condition(String... args) {

		/* Must have at least 2 arguments. */
		if (args == null) throw new NullPointerException("List of arguments can not be null");
		if (args.length < 2) throw new IllegalArgumentException("Two arguments are at least required");
		if (args.length > 4) throw new IllegalArgumentException("Four arguments is the maximum admitted");

		/* Build a list with arguments. */
		LinkedList<String> queue = new LinkedList<>();
		for (String arg : args) { queue.addLast(arg); }

		/* Literal arguments. */
		String log_op = null, left_expr = null, comp_op = null, right_expr = null;

		/* Logical operator to chain the condition. */
		if (Strings.in(queue.getFirst(), log_ops)) {
			log_op = queue.removeFirst();
		}
		if (log_op == null && !Lists.getLast(segments).isEmpty()) {
			throw new IllegalArgumentException("First argument must be a logical operator");
		}
		if (Lists.getLast(segments).isEmpty()) {
			log_op = null;
		}

		/* Case "EXISTS" expression. */
		if (Strings.in(queue.getFirst(), exists_ops)) {
			if (queue.size() != 2) {
				throw new IllegalArgumentException("Invalid number of arguments for an EXISTS expression");
			}
			comp_op = queue.removeFirst();
			right_expr = queue.removeFirst();
			Lists.getLast(segments).add(Type.EXISTS, log_op, left_expr, comp_op, right_expr);
			return;
		}

		/* Minimum number of arguments left is 2. */
		if (queue.size() < 2) {
			throw new IllegalArgumentException("Invalid number of arguments to complete the condition");
		}

		/* Left expression and comparison operator. */
		left_expr = queue.removeFirst();
		comp_op = queue.removeFirst();

		/* Left expression can not be a comparison operator. */
		boolean invalidLeftExpr = false;
		invalidLeftExpr |= Strings.in(left_expr, comp_ops);
		invalidLeftExpr |= Strings.in(left_expr, in_ops);
		invalidLeftExpr |= Strings.in(left_expr, like_ops);
		invalidLeftExpr |= Strings.in(left_expr, null_ops);
		if (invalidLeftExpr) {
			throw new IllegalArgumentException("Left expression can not be a comparison operator");
		}

		/* Case "IS NULL" expression. Discard right expression is present. */
		if (Strings.in(comp_op, null_ops)) {
			Lists.getLast(segments).add(Type.IS_NULL, log_op, left_expr, comp_op, right_expr);
			return;
		}

		/* Case "IN" expression, right argument must be present. */
		if (Strings.in(comp_op, in_ops)) {
			if (queue.isEmpty()) {
				throw new IllegalArgumentException("Invalid number of arguments to complete the condition");
			}
			right_expr = queue.removeFirst();
			Lists.getLast(segments).add(Type.IN, log_op, left_expr, comp_op, right_expr);
			return;
		}

		/* Case "LIKE" expression. */
		if (Strings.in(comp_op, like_ops)) {
			if (queue.isEmpty()) {
				throw new IllegalArgumentException("Invalid number of arguments to complete the condition");
			}
			right_expr = queue.removeFirst();
			Lists.getLast(segments).add(Type.LIKE, log_op, left_expr, comp_op, right_expr);
			return;
		}

		/* Case general comparison expression. */
		if (Strings.in(comp_op, comp_ops)) {
			if (queue.isEmpty()) {
				throw new IllegalArgumentException("Invalid number of arguments to complete the condition");
			}
			right_expr = queue.removeFirst();
			Lists.getLast(segments).add(Type.COMPARE, log_op, left_expr, comp_op, right_expr);
			return;
		}

		throw new IllegalArgumentException("Invalid arguments to complete the condition");
	}

	/**
	 * Check whether this where clause is empty.
	 * @return A boolean.
	 */
	public boolean isEmpty() { return segments.size() == 1 && segments.get(0).isEmpty(); }

	/**
	 * Add a new segment. If the last segment is empty nothing is added.
	 * @param logOp The logical operator to chain the segment.
	 */
	public void segment(String logOp) {
		if (Lists.getLast(segments).isEmpty()) {
			return;
		}
		if (!Strings.in(logOp, log_ops)) {
			throw new IllegalArgumentException("Invalid logical operator: " + logOp);
		}
		segments.add(new Segment(logOp));
	}

	/**
	 * Returns this statement as a formatted SQL string.
	 * @param formatted A boolean that indicates whether the result query should be formatted to be
	 *                  readable.
	 * @return The query.
	 */
	public String toSQL(boolean formatted) {
		StringBuilder sql = new StringBuilder();
		for (Segment segment : segments) {
			if (segment.logOp != null) {
				String logOp = " " + segment.logOp + " ";
				if (formatted) sql.append("\n" + Strings.blank(PAD - logOp.length()));
				sql.append(logOp);
			}
			sql.append("(");
			for (Condition condition : segment.conditions) {
				if (formatted) sql.append("\n");
				if (condition.logOp != null) {
					String logOp = " " + condition.logOp + " ";
					if (formatted) sql.append(Strings.blank(PAD - logOp.length()));
					sql.append(logOp);
				} else {
					if (formatted) sql.append(Strings.blank(PAD));
				}
				sql.append(toSQL(condition, formatted));
			}
			if (formatted) sql.append("\n" + Strings.blank(PAD));
			sql.append(")");
		}
		return sql.toString();
	}
	/**
	 * Returns the condition as an SQL string.
	 * @param condition The condition.
	 * @param formatted A boolean that indicates whether the result query should be formatted to be
	 *                  readable.
	 * @return The query part.
	 */
	private String toSQL(Condition condition, boolean formatted) {
		StringBuilder sql = new StringBuilder();
		if (condition.type == Type.COMPARE) {
			sql.append(condition.leftExpr);
			sql.append(" " + condition.compOp + " ");
			sql.append(condition.rightExpr);
		}
		if (condition.type == Type.EXISTS) {
			sql.append(condition.compOp + " ");
			if (formatted) {
				sql.append("(");
				sql.append("\n");
				sql.append(condition.select.toSQL(formatted));
				sql.append("\n" + Strings.blank(PAD) + ")");
			} else {
				sql.append("(");
				sql.append(condition.rightExpr);
				sql.append(")");
			}
		}
		if (condition.type == Type.IN) {
			sql.append(condition.leftExpr);
			sql.append(" " + condition.compOp + " ");
			sql.append("(" + condition.rightExpr + ")");
		}
		if (condition.type == Type.LIKE) {
			sql.append(condition.leftExpr);
			sql.append(" " + condition.compOp + " ");
			sql.append(condition.rightExpr);
		}
		if (condition.type == Type.IS_NULL) {
			sql.append(condition.leftExpr);
			sql.append(" " + condition.compOp);
		}
		return sql.toString();
	}

	/**
	 * Return a string representation of the WHERE part in a more readable format.
	 */
	@Override
	public String toString() { return toSQL(true); }

	/**
	 * Convert an array of values to the string list applying the DBEngine conversion.
	 * @param values The list of values.
	 * @return The string list.
	 */
	private String toStringList(Value... values) { return toStringList(new ArrayIterator<>(values)); }
	/**
	 * Convert an array of values to the string list applying the DBEngine conversion.
	 * @param values The list of values.
	 * @return The string list.
	 */
	private String toStringList(Iterator<Value> values) {
		return Lists.toString(new FunctionIterator<Value, String>(values, (v) -> db.toStringSQL(v)));
	}

	/**
	 * Validate that the operator is within the list of operators.
	 * @param op   The operator.
	 * @param opss The list of lists of operators.
	 */
	private void validate(String op, String[]... opss) {
		if (!Strings.in(op, opss)) {
			throw new IllegalArgumentException("Invalid operator \"" + op + "\" for condition");
		}
	}

	/**
	 * Add an entire filter.
	 * @param filter The filter to add.
	 */
	protected void filter(Filter filter) { filter(null, filter); }
	/**
	 * Add an entire filter.
	 * @param logOp  The logical operator to chain the first segment.
	 * @param filter The filter to add.
	 */
	protected void filter(String logOp, Filter filter) {
		if (Lists.getLast(segments).isEmpty()) {
			Lists.removeLast(segments);
			logOp = null;
		} else {
			if (logOp == null || !Strings.in(logOp, log_ops)) {
				throw new IllegalArgumentException("Invalid operator logical operator");
			}
		}
		for (int i = 0; i < filter.segments.size(); i++) {
			Segment segment = new Segment(filter.segments.get(i));
			if (i == 0) segment.logOp = logOp;
			segments.add(segment);
		}
	}
}
