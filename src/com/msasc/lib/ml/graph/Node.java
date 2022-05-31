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
package com.msasc.lib.ml.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.msasc.lib.json.JSONObject;
import com.msasc.lib.json.JSONTypes;

/**
 * A node of a neural network computational graph.
 * <p>
 * In the forward pass, a node reads data, normally values, from the input edges, processes it, and
 * pushes the resulting data to the output edges.
 * <p>
 * In the backward pass, a node may read data, normally deltas to adjust parameters, from the output
 * edges, does any internal necesary operation, and optionally pushes backward data to the input
 * edges.
 * 
 * @author Miquel Sas
 */
public abstract class Node {

	/** Universal unique id. */
	private UUID uuid;

	/** The cell to which the node belongs. */
	private Cell cell;

	/** List of input edges. */
	private List<Edge> inputEdges = new ArrayList<>();
	/** List of output edges. */
	private List<Edge> outputEdges = new ArrayList<>();

	/**
	 * Constructor.
	 */
	public Node() { uuid = UUID.randomUUID(); }
	/**
	 * Constructor to restore.
	 * @param uuid UUID:
	 */
	protected Node(UUID uuid) { this.uuid = uuid; }

	/**
	 * Return the universal unique id.
	 * @return The UUID.
	 */
	public UUID getUUID() { return uuid; }

	/**
	 * Request deltas, apply any parameter update, and push deltas to input edges.
	 */
	public abstract void backward();
	/**
	 * Request values from input edges, apply node calculations and push values to output edges.
	 */
	public abstract void forward();

	/**
	 * Return the list of input edges.
	 * @return the list of edges.
	 */
	public List<Edge> getInputEdges() { return inputEdges; }
	/**
	 * Return the list of output edges.
	 * @return The list of edges.
	 */
	public List<Edge> getOutputEdges() { return outputEdges; }

	/**
	 * Return the cell to which the node belongs.
	 * @return The cell.
	 */
	public Cell getCell() { return cell; }
	/**
	 * Set the cell to which the node should belong.
	 * @param cell The cell.
	 */
	void setCell(Cell cell) { this.cell = cell; }

	/**
	 * Check equality.
	 */
	@Override
	public boolean equals(Object o) { return (o instanceof Node n ? uuid.equals(n.uuid) : false); }
	/**
	 * Return a suitable hash code.
	 */
	@Override
	public int hashCode() { return uuid.hashCode(); }

	/**
	 * Return a JSON definition of the edge.
	 * @return The JSON definition.
	 */
	public JSONObject toJSONObject() {
		JSONObject def = new JSONObject();
		def.put("uuid", JSONTypes.STRING, getUUID().toString());
		def.put("name", JSONTypes.STRING, getClass().getSimpleName());
		toJSONObject(def);
		return def;
	}
	/**
	 * Append the particular node definition.
	 * @param def The JSONObject definition.
	 */
	public abstract void toJSONObject(JSONObject def);
}
