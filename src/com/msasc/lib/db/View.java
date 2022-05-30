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
 * A database view, build with a master table and relations. When defining a
 * view, fields must belong to tables of the relations or be non persistent.
 * 
 * @author Miquel Sas
 */
public class View {

	/** The name of the table. */
	private String name;
	/** The alias. */
	private String alias;
	/** The master table. */
	private Table masterTable;
	/** The list of fields. */
	private FieldList fields = new FieldList();
	/** The array of relations. */
	private List<Relation> relations;
	/** The array of group by fields. */
	private List<Field> groupBy;
	/** The order. */
	private Order orderBy;

	/**
	 * Constructor.
	 */
	public View() {}

	/**
	 * Add a field to this view.
	 * @param field The field to add.
	 */
	public void addField(Field field) { fields.addField(new Field(field)); }
	
	/**
	 * Add a field to the group by list.
	 * @param field The field to add.
	 */
	public void addGroupBy(Field field) {
		if (groupBy == null) groupBy = new ArrayList<>();
		groupBy.add(new Field(field));
	}

	/**
	 * Add an order by segment.
	 * @param field The field of the order by segment.
	 */
	public void addOrderBy(Field field) { addOrderBy(field, true); }
	/**
	 * Add an order by segment.
	 * @param field The field of the order by segment.
	 * @param asc   A flag indicating if the segment is ascending or descending.
	 */
	public void addOrderBy(Field field, boolean asc) {
		if (orderBy == null) orderBy = new Order();
		orderBy.addField(field, asc);
	}

	/**
	 * Add a relation to this view.
	 * @param relation The relation to add.
	 */
	public void addRelation(Relation relation) {
		if (relations == null) relations = new ArrayList<>();
		relations.add(relation);
	}

	/**
	 * Gets the alias.
	 * @return The alias.
	 */
	public String getAlias() { return alias == null ? name : alias; }

	/**
	 * Returns the field or null.
	 * @param alias The alias.
	 * @return The field or null.
	 */
	public Field getField(String alias) { return fields.getField(alias); }
	/**
	 * Returns the list of fields.
	 * @return The list of fields.
	 */
	public FieldList getFields() { return fields; }
	/**
	 * Returns the list of group by fields.
	 * @return The list of group by fields.
	 */
	public List<Field> getGroupBy() { return groupBy; }
	/**
	 * Get the name.
	 * @return The table name
	 */
	public String getName() { return name; }
	/**
	 * Get the master table.
	 * @return The master table.
	 */
	public Table getMasterTable() { return masterTable; }
	/**
	 * Gets the order by index.
	 * @return The order by index.
	 */
	public Order getOrderBy() { return orderBy; }
	/**
	 * Returns the list of relations.
	 * @return The list of relations.
	 */
	public List<Relation> getRelations() { return relations; }

	/**
	 * Sets the alias.
	 * @param alias The table alias.
	 */
	public void setAlias(String alias) { this.alias = alias; }
	/**
	 * Set the master table.
	 * @param masterTable The master table.
	 */
	public void setMasterTable(Table masterTable) { this.masterTable = masterTable; }
	/**
	 * Set the name.
	 * @param name The table name
	 */
	public void setName(String name) { this.name = name; }
	/**
	 * Set the order by.
	 * @param orderBy The order by index.
	 */
	public void setOrderBy(Order orderBy) { this.orderBy = orderBy; }

	/**
	 * Setup internal structures and validate.
	 */
	public void setupAndValidate() {
		fields.setupAndValidate();

		/* Validate fields. */
		for (Field field : fields) {
			/* Persistent fields must have a table. */
			if (field.isPersistent()) {
				if (field.getTable() == null) {
					throw new IllegalStateException("Field " + field.getName() + " has no table reference");
				}
			}
			/* Set the view. */
			field.setView(this);
		}
		
		/* Group by fields must be in the field list. */
		if (groupBy != null) {
			List<Field> groupBy_tmp = new ArrayList<>();
			for (Field field : groupBy) {
				Field field_tmp = fields.getField(field.getAlias());
				if (field_tmp == null) {
					throw new IllegalStateException("Group by field " + field + " not found in master list");
				}
				groupBy_tmp.add(field_tmp);
			}
			groupBy.clear();
			groupBy.addAll(groupBy_tmp);
		}

		/* Set view to order by fields. */
		if (orderBy != null) {
			List<Order.Segment> segments_tmp = new ArrayList<>();
			for (Order.Segment segment : orderBy.getSegments()) {
				Field field = segment.getField();
				Field field_tmp = fields.getField(field.getAlias());
				if (field_tmp == null) {
					throw new IllegalStateException("Order by field " + field + " not found in master list");
				}
				segments_tmp.add(new Order.Segment(field_tmp, segment.isAscending()));
			}
			orderBy.getSegments().clear();
			orderBy.getSegments().addAll(segments_tmp);
		}
	}
}
