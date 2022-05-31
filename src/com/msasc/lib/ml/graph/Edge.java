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

import java.util.Deque;
import java.util.LinkedList;
import java.util.UUID;

import com.msasc.lib.json.JSONObject;
import com.msasc.lib.json.JSONTypes;

/**
 * An edge of a computational graph.
 * <p>
 * Data (double[]), normally called values, is forward pushed to the edge by an external means if
 * the edge is an input edge, or by the input node if it is a transfer edge. The data is then made
 * available to the output node, allowing for further processing, normally output calculations.
 * <p>
 * Data (double[]), normally called deltas, is backward pushed to the edge by an external means if
 * the edge is an output edge, or by the output node if it is a transfer edge. The data is then made
 * available to the input node, allowing for further processing, normally internal parameters
 * adjusting.
 * <p>
 * An edge without an input node is an input edge. An edge without an output node is an output edge.
 * An edge with both an input and an output node is a transfer edge.
 * 
 * @author Miquel Sas
 */
public class Edge {

	/**
	 * Connect two nodes of a network with an edge of the given size.
	 * @param size       The size.
	 * @param inputNode  Input node.
	 * @param outputNode Output node.
	 */
	public static void connect(int size, Node inputNode, Node outputNode) {
		Edge edge = new Edge(size);
		if (inputNode != null) {
			edge.setInputNode(inputNode);
			inputNode.getOutputEdges().add(edge);
		}
		if (outputNode != null) {
			edge.setOutputNode(outputNode);
			outputNode.getInputEdges().add(edge);
		}
	}

	/** Universal unique id. */
	private UUID uuid;

	/** Input node. */
	private Node inputNode;
	/** Output node. */
	private Node outputNode;

	/** Deque to maintain the backward queue (deltas). */
	private Deque<double[]> backwardQueue = new LinkedList<>();
	/** Deque to maintain the forward queue (values). */
	private Deque<double[]> forwardQueue = new LinkedList<>();

	/** Size of the forward and backward vectors. */
	private int size;

	/**
	 * Constructor.
	 * @param size The size of the forward and backward vectors.
	 */
	public Edge(int size) { this(size, UUID.randomUUID()); }
	/**
	 * Constructor to restore.
	 * @param size The size.
	 * @param uuid The UUID.
	 */
	Edge(int size, UUID uuid) {
		this.size = size;
		this.uuid = uuid;
	}

	/**
	 * Return the backward deltas.
	 * @return The backward data, normally called deltas.
	 */
	public double[] getBackwardDeltas() {
		if (backwardQueue.isEmpty()) return new double[size];
		return backwardQueue.getFirst();
	}
	/**
	 * Return the forward values.
	 * @return The forward data, normally called values.
	 */
	public double[] getForwardValues() {
		if (forwardQueue.isEmpty()) return new double[size];
		return forwardQueue.getFirst();
	}

	/**
	 * Return the universal unique id.
	 * @return The UUID.
	 */
	public UUID getUUID() { return uuid; }

	/**
	 * Returns the input node.
	 * @return The input node.
	 */
	public Node getInputNode() { return inputNode; }
	/**
	 * Set the input node.
	 * @param inputNode The input node.
	 */
	void setInputNode(Node inputNode) { this.inputNode = inputNode; }
	/**
	 * Return the output node.
	 * @return The output node.
	 */
	public Node getOutputNode() { return outputNode; }
	/**
	 * Set the output node.
	 * @param outputNode The output node.
	 */
	void setOutputNode(Node outputNode) { this.outputNode = outputNode; }

	/**
	 * Check whether this edge is an input edge because the input node is null.
	 * @return A boolean.
	 */
	public boolean isInput() { return inputNode == null; }
	/**
	 * Check whether this edge is an output edge because the output node is null.
	 * @return A boolean.
	 */
	public boolean isOutput() { return outputNode == null; }
	/**
	 * Check whether this edge is a transfer edge, with both nodes not null.
	 * @return A boolean.
	 */
	public boolean isTransfer() { return inputNode != null && outputNode != null; }

	/**
	 * Push backward values (deltas), adding them at the beginning of the backward queue.
	 * @param deltas The vector of output deltas.
	 */
	public void pushBackward(double[] deltas) {
		if (deltas.length != size) throw new IllegalArgumentException("Invalid output deltas size");
		backwardQueue.addFirst(deltas);
	}
	/**
	 * Push forward values, adding them at the beginning of the forward queue.
	 * @param values The vector of input values.
	 */
	public void pushForward(double[] values) {
		if (values.length != size) throw new IllegalArgumentException("Invalid input values size");
		forwardQueue.addFirst(values);
	}

	/**
	 * Return the size of the input and output vectors.
	 * @return The size.
	 */
	public int size() { return size; }

	/**
	 * Return the current size of the forward queue.
	 * @return The current size of the forward queue.
	 */
	public int sizeForwardQueue() { return forwardQueue.size(); }

	/**
	 * Return the current size of the backward queue.
	 * @return The current size of the backward queue.
	 */
	public int sizeBackwardQueue() { return backwardQueue.size(); }

	/**
	 * Unfold the internal queues. Some network processes, like batch back propagation or
	 * episode reinforcement learning may require to remember the some flow of data to batch process
	 * backward in a series of calls to <i>backward()</i> to every node, and <i>unfold()</i> to
	 * every node and edge.
	 * <p>
	 * The calls to <i>backward()</i> or <i>unfold()</i> are managed by the network.
	 */
	public void unfold() {
		if (!backwardQueue.isEmpty()) backwardQueue.removeFirst();
		if (!forwardQueue.isEmpty()) forwardQueue.removeFirst();
	}

	/**
	 * Check equality.
	 */
	@Override
	public boolean equals(Object o) { return (o instanceof Edge e ? uuid.equals(e.uuid) : false); }
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
		def.put("size", JSONTypes.NUMBER, size);
		if (inputNode != null) {
			def.put("input-node", JSONTypes.STRING, inputNode.getUUID().toString());
		}
		if (outputNode != null) {
			def.put("output-node", JSONTypes.STRING, outputNode.getUUID().toString());
		}
		return def;
	}
}
