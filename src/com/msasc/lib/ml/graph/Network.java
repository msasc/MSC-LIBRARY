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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.msasc.lib.json.JSONArray;
import com.msasc.lib.json.JSONObject;
import com.msasc.lib.json.JSONTypes;
import com.msasc.lib.ml.graph.nodes.ActivationNode;
import com.msasc.lib.ml.graph.nodes.BiasNode;
import com.msasc.lib.ml.graph.nodes.WeightsNode;

/**
 * A network or computational graph, made of wired cells of nodes that interface through input and
 * output edges. Data is forward pushed to the input edges and a call to <i>forward()</i> triggers
 * the calculation processes to finally push the output to the output edges. Inversely, deltas or
 * errors are backward pushed to the output edges and a call to <i>backward()</i> triggers the
 * calculation processes to adjust internal parameters.
 * 
 * @author Miquel Sas
 */
public class Network {

	/** Master map with all cells in this network. */
	private Map<Cell, Cell> cells = new HashMap<>();

	/** List of input edges. */
	private List<Edge> inputEdges;
	/** List of output edges. */
	private List<Edge> outputEdges;
	/** List of layers in forward order. */
	private List<List<Node>> layers;
	/** Map with all edges in the network. */
	private Map<Edge, Edge> edges;

	/**
	 * Constructor.
	 */
	public Network() {}

	/**
	 * Add a series of cells.
	 * @param cells The list of cells.
	 */
	public void add(Cell... cells) { for (Cell cell : cells) { this.cells.put(cell, cell); } }

	/**
	 * Launch the backward pass.
	 * @param outputDeltasList List of arrays of output deltas, in the same order as the list of
	 *                         output edges.
	 */
	public void backward(List<double[]> outputDeltasList) {

		/* Validate initialized and sizes. */
		checkInitialized();
		checkSizes(outputDeltasList.size(), outputEdges.size());
		
		/* Push backward output deltas. */
		for (int i = 0; i < outputDeltasList.size(); i++) {
			double[] outputDeltas = outputDeltasList.get(i);
			outputEdges.get(i).pushBackward(outputDeltas);
		}


		/* Push backward layers. */
		for (int i = layers.size() - 1; i >= 0; i--) {
			List<Node> nodes = layers.get(i);
			for (Node node : nodes) {
				node.backward();
			}
		}

		/* Unfold. */
		unfold();
	}

	/**
	 * Launch the forward pass.
	 * @param inputValuesList List of arrays of input values, in the same order as the list of input
	 *                        edges.
	 */
	public void forward(List<double[]> inputValuesList) {

		/* Validate initialized and sizes. */
		checkInitialized();
		checkSizes(inputValuesList.size(), inputEdges.size());

		/* Push forward input values. */
		for (int i = 0; i < inputValuesList.size(); i++) {
			double[] inputValues = inputValuesList.get(i);
			inputEdges.get(i).pushForward(inputValues);
		}

		/* Push forward layers. */
		for (int i = 0; i < layers.size(); i++) {
			List<Node> nodes = layers.get(i);
			for (Node node : nodes) {
				node.forward();
			}
		}
	}

	/**
	 * Return the list of input edges.
	 * @return The list of input edges.
	 */
	public List<Edge> getInputEdges() { return Collections.unmodifiableList(inputEdges); }
	/**
	 * Return the list of output edges.
	 * @return The list of output edges.
	 */
	public List<Edge> getOutputEdges() { return Collections.unmodifiableList(outputEdges); }

	/**
	 * Initialize the network. This method must be called after adding all the cells and wires, and
	 * before the call to the first <i>forward()</i> or <i>backward()</i>.
	 * <p>
	 * Builds the lists of input and output edges, and the list of layers in a forward order.
	 */
	public void initialize() {

		/* Lists of input and output edges. */
		inputEdges = new ArrayList<>();
		outputEdges = new ArrayList<>();
		for (Cell cell : cells.values()) {
			for (Node node : cell.getNodes()) {
				for (Edge edge : node.getInputEdges()) {
					if (edge.isInput()) inputEdges.add(edge);
				}
				for (Edge edge : node.getOutputEdges()) {
					if (edge.isOutput()) outputEdges.add(edge);
				}
			}
		}

		/* List of layers. */
		layers = new ArrayList<>();

		/* Map of avoid infinite recurrence and working list of edges. */
		Map<Node, Node> processedNodes = new HashMap<>();
		List<Edge> scanEdges = new ArrayList<>();

		/* Start with input edges. */
		scanEdges.addAll(inputEdges);

		/* Build layers with output nodes of the edges, until no more output nodes are found. */
		while (true) {
			List<Node> layer = new ArrayList<>();

			/* Add output nodes to the layer. */
			for (Edge edge : scanEdges) {
				Node node = edge.getOutputNode();
				if (node != null && !processedNodes.containsKey(node)) {
					layer.add(node);
					processedNodes.put(node, node);
				}
			}

			/* If the layer is empty, we are one, if not add it and continue. */
			if (layer.isEmpty()) break;
			layers.add(layer);

			/* Fill edges with output edges of the layer. */
			scanEdges.clear();
			for (Node node : layer) { scanEdges.addAll(node.getOutputEdges()); }
		}

		/* Build the map with all edges. */
		edges = new HashMap<>();
		for (List<Node> nodes : layers) {
			for (Node node : nodes) {
				for (Edge edge : node.getInputEdges()) {
					edges.put(edge, edge);
				}
				for (Edge edge : node.getOutputEdges()) {
					edges.put(edge, edge);
				}
			}
		}
	}

	/**
	 * Unfold edges.
	 */
	public void unfold() {
		checkInitialized();
		for (Edge edge : edges.values()) { edge.unfold(); }
	}

	/**
	 * Check that the network has been properly initialized.
	 */
	private void checkInitialized() {
		if (inputEdges == null || outputEdges == null || layers == null || edges == null) {
			throw new IllegalStateException("Network not properly initialized.");
		}
	}
	/**
	 * Check sizes.
	 */
	private void checkSizes(int size1, int size2) {
		if (size1 != size2) {
			throw new IllegalArgumentException("Sizes do not match.");
		}
	}
	
	/**
	 * Restore the network from a JSONObject.
	 * @param net The object.
	 */
	public void fromJSONObject(JSONObject net) {
		
		/* Read the cells. */
		JSONArray arr_cells = net.get("cells").getArray();
		for (int i = 0; i < arr_cells.size(); i++) {
			
			JSONObject cell_obj = arr_cells.get(i).getObject();
			String cell_uuid = cell_obj.get("uuid").getString();
			String cell_name = cell_obj.get("name").getString();
			
			Cell cell = new Cell(UUID.fromString(cell_uuid), cell_name);
			
			JSONArray arr_nodes = cell_obj.get("nodes").getArray();
			for (int j = 0; j < arr_nodes.size(); j++) {
				JSONObject node_obj = arr_nodes.get(j).getObject();
				String node_name = node_obj.get("name").getString();
				Node node = null;
				if (node_name.equals(ActivationNode.class.getSimpleName())) {
					node = ActivationNode.fromJSONObject(node_obj);
				}
				if (node_name.equals(BiasNode.class.getSimpleName())) {
					node = BiasNode.fromJSONObject(node_obj);
				}
				if (node_name.equals(WeightsNode.class.getSimpleName())) {
					node = WeightsNode.fromJSONObject(node_obj);
				}
				cell.putNode(node);
			}
			
			cells.put(cell, cell);
		}
		
		/* Build a map with all nodes by string UUID. */
		Map<String, Node> nodes = new HashMap<>();
		for (Cell cell : cells.values()) {
			for (Node node : cell.getNodes()) {
				nodes.put(node.getUUID().toString(), node);
			}
		}
		
		/* Read edges and wire. */
		JSONArray arr_edges = net.get("edges").getArray();
		for (int i = 0; i < arr_edges.size(); i++) {
			JSONObject obj = arr_edges.get(i).getObject();
			String uuid = obj.get("uuid").getString();
			int size = obj.get("size").getInteger();
			Edge edge = new Edge(size, UUID.fromString(uuid));
			if (obj.get("input-node") != null) {
				uuid = obj.get("input-node").getString();
				Node node = nodes.get(uuid);
				if (node != null) {
					edge.setInputNode(node);
					node.getOutputEdges().add(edge);
				}
			}
			if (obj.get("output-node") != null) {
				uuid = obj.get("otput-node").getString();
				Node node = nodes.get(uuid);
				if (node != null) {
					edge.setOutputNode(node);
					node.getInputEdges().add(edge);
				}
			}
		}
		
		/* Initialize. */
		initialize();
	}

	/**
	 * Return a JSON definition of the edge.
	 * @return The JSON definition.
	 */
	public JSONObject toJSONObject() {
		JSONObject net = new JSONObject();

		/* Save cells. */
		JSONArray arrCells = new JSONArray();
		for (Cell cell : cells.values()) {
			arrCells.add(JSONTypes.OBJECT, cell.toJSONObject());
		}
		net.put("cells", JSONTypes.ARRAY, arrCells);

		/* Save edges. */
		JSONArray arrEdges = new JSONArray();
		for (Edge edge : edges.values()) {
			arrEdges.add(JSONTypes.OBJECT, edge.toJSONObject());
		}
		net.put("edges", JSONTypes.ARRAY, arrEdges);

		return net;
	}
}
