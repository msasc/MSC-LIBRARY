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
package com.msasc.lib.db;

import java.util.ArrayList;
import java.util.List;

/**
 * A relation between two tables.
 *
 * @author Miquel Sas
 */
public class Relation {

	/** Relation type. */
	public static enum Type {
		INNER, LEFT, RIGHT
	}
	/**
	 * Segment.
	 */
	public class Segment {

		/** The local field. */
		private Field localField;
		/** The foreign field. */
		private Field foreignField;

		/**
		 * Default constructor.
		 */
		public Segment() {}
		/**
		 * Constructor assigning the fields.
		 * @param localField   The local field.
		 * @param foreignField The foreign field.
		 */
		public Segment(Field localField, Field foreignField) {
			this.localField = localField;
			this.foreignField = foreignField;
		}

		/**
		 * Check equality.
		 */
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Segment)) return false;
			Segment seg = (Segment) obj;
			boolean equals = true;
			equals &= getLocalField().equals(seg.getLocalField());
			equals &= getForeignField().equals(seg.getForeignField());
			return equals;
		}
		/**
		 * Return a proper hash code.
		 */
		@Override
		public int hashCode() {
			int hash = getLocalField().hashCode();
			hash += getForeignField().hashCode();
			return hash;
		}

		/**
		 * Get the local field.
		 * @return The local field.
		 */
		public Field getLocalField() { return localField; }
		/**
		 * Get the foreign field.
		 * @return The foreign field.
		 */
		public Field getForeignField() { return foreignField; }

		/**
		 * Set the local field.
		 * @param localField The local field.
		 */
		public void setLocalField(Field localField) { this.localField = localField; }
		/**
		 * Set the foreign field.
		 * @param foreignField The foreign field.
		 */
		public void setForeignField(Field foreignField) { this.foreignField = foreignField; }

		/**
		 * Returns a string representation.
		 */
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder(64);
			b.append(getLocalField().getNameParent());
			b.append(" = ");
			b.append(getForeignField().getNameParent());
			return b.toString();
		}
	}

	/** The local table. */
	private Table localTable = null;
	/** The foreign table. */
	private Table foreignTable = null;
	/** Local table alias. */
	private String localTableAlias;
	/** Foreign table alias. */
	private String foreignTableAlias;

	/** List of segments. */
	private List<Segment> segments = new ArrayList<>();

	/** The type property. */
	private Type type = Type.LEFT;

	/**
	 * Default constructor.
	 */
	public Relation() {}

	/**
	 * Add a segment to this table link.
	 * @param localField   The local field.
	 * @param foreignField The foreign field.
	 */
	public void addSegment(Field localField, Field foreignField) {
		segments.add(new Segment(localField, foreignField));
	}

	/**
	 * Returns a boolean indicating if the field is contained as a local field.
	 * @param field The field.
	 * @return A boolean.
	 */
	public boolean containsLocalField(Field field) { return getLocalFieldIndex(field) >= 0; }
	/**
	 * Returns a boolean indicating if the field is contained as a foreign field.
	 * @param field The field.
	 * @return A boolean.
	 */
	public boolean containsForeignField(Field field) { return getForeignFieldIndex(field) >= 0; }

	/**
	 * Check equality.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Segment)) return false;
		Relation rel = (Relation) obj;
		boolean equals = true;
		equals &= getLocalTable().equals(rel.getLocalTable());
		equals &= getLocalTableAlias().equals(rel.getLocalTableAlias());
		equals &= getForeignTable().equals(rel.getForeignTable());
		equals &= getForeignTableAlias().equals(rel.getForeignTableAlias());
		return equals;
	}
	/**
	 * Return a proper hash code.
	 */
	@Override
	public int hashCode() {
		int hash = getLocalTable().hashCode();
		hash += getLocalTableAlias().hashCode();
		hash += getForeignTable().hashCode();
		hash += getForeignTableAlias().hashCode();
		return hash;
	}

	/**
	 * Get the foreign table.
	 * @return The foreign table.
	 */
	public Table getForeignTable() { return foreignTable; }
	/**
	 * Returns the foreign table alias.
	 * @return The foreign table alias.
	 */
	public String getForeignTableAlias() {
		if (foreignTableAlias == null && foreignTable != null) {
			return foreignTable.getAlias();
		}
		return foreignTableAlias;
	}
	/**
	 * Returns the foreign field index or -1.
	 * @param field The field.
	 * @return The index.
	 */
	public int getForeignFieldIndex(Field field) {
		for (int i = 0; i < segments.size(); i++) {
			if (segments.get(i).getForeignField().equals(field)) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * Get the local table.
	 * @return The local table
	 */
	public Table getLocalTable() { return localTable; }
	/**
	 * Returns the local table alias.
	 * @return The local table alias.
	 */
	public String getLocalTableAlias() {
		if (localTableAlias == null && localTable != null) {
			return localTable.getAlias();
		}
		return localTableAlias;
	}
	/**
	 * Returns the local field index or -1.
	 * @param field The field.
	 * @return The index.
	 */
	public int getLocalFieldIndex(Field field) {
		for (int i = 0; i < segments.size(); i++) {
			if (segments.get(i).getLocalField().equals(field)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Return the list of segments.
	 * @return The list of segments.
	 */
	public List<Segment> getSegments() { return segments; }

	/**
	 * Return the relation typr.
	 * @return The type.
	 */
	public Relation.Type getType() { return type == null ? Type.LEFT : type; }

	/**
	 * Set the local table.
	 * @param localTable The local table
	 */
	public void setLocalTable(Table localTable) { this.localTable = localTable; }
	/**
	 * Sets the local table alias.
	 * @param localTableAlias The local table alias.
	 */
	public void setLocalTableAlias(String localTableAlias) { this.localTableAlias = localTableAlias; }
	/**
	 * Set the foreign table.
	 * @param foreignTable The foreign table.
	 */
	public void setForeignTable(Table foreignTable) { this.foreignTable = foreignTable; }
	/**
	 * Sets the foreign table alias.
	 * @param foreignTableAlias The foreign table alias.
	 */
	public void setForeignTableAlias(String foreignTableAlias) { this.foreignTableAlias = foreignTableAlias; }

	/**
	 * Sets the relation type.
	 * @param type The relation type.
	 */
	public void setType(Relation.Type type) { this.type = type; }

	/**
	 * Return a string representation.
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(64);
		b.append(getLocalTable().getName());
		b.append(" " + type + " JOIN ");
		b.append(getForeignTable().getName());
		b.append(" ON ");
		for (int i = 0; i < getSegments().size(); i++) {
			if (i > 0) {
				b.append(" AND ");
			}
			b.append(getSegments().get(i));
		}
		b.append(")");
		return b.toString();
	}
}
