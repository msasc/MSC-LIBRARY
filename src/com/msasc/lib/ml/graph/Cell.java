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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.msasc.lib.json.JSONArray;
import com.msasc.lib.json.JSONObject;
import com.msasc.lib.json.JSONTypes;
import com.msasc.lib.ml.function.Activation;
import com.msasc.lib.ml.graph.nodes.ActivationNode;
import com.msasc.lib.ml.graph.nodes.BiasNode;
import com.msasc.lib.ml.graph.nodes.WeightsNode;

/**
 * Cells of the network computational graph. A cell can be considered a sub-network.
 * 
 * @author Miquel Sas
 */
public class Cell {

	/**
	 * Creates a generic RNN cell definition that can range from a simple BP cell without bias, up
	 * to a RNN cell with bias.
	 * 
	 * @param inputSize  Input size.
	 * @param outputSize Output size.
	 * @param activation Activation function.
	 * @param recurrent  A boolean that indicates whether the cell is recurrent.
	 * @param bias       A boolean that indicates whether the activation node will use biases.
	 */
	public static Cell rnn(int inputSize, int outputSize, Activation activation, boolean recurrent, boolean bias) {

		Cell cell = new Cell();

		StringBuilder name = new StringBuilder();
		name.append("RNN-");
		name.append(inputSize);
		name.append("-");
		name.append(outputSize);
		name.append("-");
		name.append(activation.getClass().getSimpleName());
		if (recurrent) name.append("-REC");
		if (bias) name.append("-BIAS");
		cell.name = name.toString();

		/* Normal weights node. Connect an input edge. */
		WeightsNode weightsNode = new WeightsNode(inputSize, outputSize);
		Edge.connect(inputSize, null, weightsNode);
		cell.putNode(weightsNode);

		/* Activation node. */
		ActivationNode actNode = new ActivationNode(activation);
		cell.putNode(actNode);

		/* Connect normal weights node to activation node. */
		Edge.connect(outputSize, weightsNode, actNode);

		/* Case bias node required. */
		if (bias) {
			BiasNode biasNode = new BiasNode(outputSize);
			Edge.connect(outputSize, biasNode, actNode);
			cell.putNode(biasNode);
		}

		/* Case recurrent weights node required. */
		if (recurrent) {
			WeightsNode recwNode = new WeightsNode(outputSize, outputSize);
			Edge.connect(outputSize, actNode, recwNode);
			Edge.connect(outputSize, recwNode, actNode);
			cell.putNode(recwNode);
		}

		/* Connect the output edge to the activation node. */
		Edge.connect(outputSize, actNode, null);

		return cell;
	}

	/** Universal unique id. */
	private UUID uuid;
	/** Name. */
	private String name;
	/** Master map with cell nodes. */
	private Map<Node, Node> nodes;

	/**
	 * Constructor.
	 */
	public Cell() {
		uuid = UUID.randomUUID();
		nodes = new HashMap<>();
	}

	/**
	 * Constructor to restore.
	 * @param uuid UUID.
	 * @param name Name.
	 */
	Cell(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
	}
	/**
	 * Put a node in a restore process.
	 * @param node The node.
	 */
	void putNode(Node node) {
		node.setCell(this);
		nodes.put(node, node);
	}

	/**
	 * Return the universal unique id.
	 * @return The UUID.
	 */
	public UUID getUUID() { return uuid; }
	/**
	 * Return the name.
	 * @return The name.
	 */
	public String getName() { return name; }

	/**
	 * Returns the list of input edges, edges that are input edges of nodes of the cell, and are
	 * input because they do not have an input node, or their input node is from another cell.
	 * @return The list of input edges.
	 */
	public List<Edge> getInputEdges() {
		List<Edge> edges = new ArrayList<>();
		for (Node node : nodes.values()) {
			for (Edge edge : node.getInputEdges()) {
				if (edge.isInput()) edges.add(edge);
				if (!edge.getInputNode().getCell().equals(this)) edges.add(edge);
			}
		}
		return edges;
	}
	/**
	 * Returns the list of output edges, edges that are output edges of nodes of the cell, and are
	 * output because they do not have an output node, or their output node is from another cell.
	 * @return The list of output edges.
	 */
	public List<Edge> getOutputEdges() {
		List<Edge> edges = new ArrayList<>();
		for (Node node : nodes.values()) {
			for (Edge edge : node.getOutputEdges()) {
				if (edge.isOutput()) edges.add(edge);
				if (!edge.getOutputNode().getCell().equals(this)) edges.add(edge);
			}
		}
		return edges;
	}
	/**
	 * Return the collection of nodes in this cell.
	 * @return The nodes.
	 */
	public Collection<Node> getNodes() { return nodes.values(); }

	/**
	 * Check equality.
	 */
	@Override
	public boolean equals(Object o) { return (o instanceof Cell c ? uuid.equals(c.uuid) : false); }
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
		def.put("name", JSONTypes.STRING, name);
		JSONArray arrNodes = new JSONArray();
		for (Node node : nodes.values()) {
			arrNodes.add(JSONTypes.OBJECT, node.toJSONObject());
		}
		def.put("nodes", JSONTypes.ARRAY, arrNodes);
		return def;
	}
}
