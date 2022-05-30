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
package com.msasc.lib.ml.network.graph.nodes;

import java.util.Random;
import java.util.UUID;

import com.msasc.lib.json.JSONArray;
import com.msasc.lib.json.JSONObject;
import com.msasc.lib.json.JSONTypes;
import com.msasc.lib.ml.network.graph.Node;

/**
 * Minimum weights node using stochastic gradient descent back propagation to apply adjustments.
 * @author Miquel Sas
 */
public class WeightsNode extends Node {
	/**
	 * Builder to restore from a JSON object with the node definition.
	 * @param obj The JSON object.
	 * @return The node.
	 */
	public static WeightsNode fromJSONObject(JSONObject obj) {
		String uuid = obj.get("uuid").getString();
		WeightsNode node = new WeightsNode(UUID.fromString(uuid));
		node.inputSize = obj.get("input-size").getInteger();
		node.outputSize = obj.get("output-size").getInteger();
		node.eta = obj.get("eta").getDouble();
		node.alpha = obj.get("alpha").getDouble();
		node.lambda = obj.get("lambda").getDouble();
		node.gradients = new double[node.inputSize][node.outputSize];
		node.weights = new double[node.inputSize][node.outputSize];
		JSONArray arrIn = obj.get("weights").getArray();
		for (int in = 0; in < arrIn.size(); in++) {
			JSONArray arrOut = arrIn.get(in).getArray();
			for (int out = 0; out < arrOut.size(); out++) {
				node.weights[in][out] = arrOut.get(out).getDouble();
			}
		}
		return node;
	}

	/** Input size. */
	private int inputSize;
	/** Output size. */
	private int outputSize;

	/** Input values read from the unique input edge. */
	private double[] inputValues;
	/** Output values pushed to the output edge. */
	private double[] outputValues;

	/** Output deltas read from the unique output edge. */
	private double[] outputDeltas;
	/** input deltas pushed from the unique input edge. */
	private double[] inputDeltas;

	/** Gradients (in, out). */
	private double[][] gradients;
	/** Weights (in, out). */
	private double[][] weights;

	/** Learning rate. */
	private double eta = 0.1;
	/** Momentum factor. */
	private double alpha = 0.0;
	/** Weight decay factor, which is also a regularization term. */
	private double lambda = 0.0;

	/**
	 * Constructor.
	 * @param inputSize  Input size.
	 * @param outputSize Output size.
	 */
	public WeightsNode(int inputSize, int outputSize) {
		this.inputSize = inputSize;
		this.outputSize = outputSize;
		this.gradients = new double[inputSize][outputSize];
		this.weights = new double[inputSize][outputSize];

		/* Randomly initialize weights. */
		Random rand = new Random();
		for (int in = 0; in < inputSize; in++) {
			for (int out = 0; out < outputSize; out++) {
				weights[in][out] = rand.nextGaussian();
			}
		}

		// TODO Check initialize inputDeltas and outputValues and not create an array per pulse.
	}
	/**
	 * Constructor to restore.
	 * @param uuid UUID:
	 */
	WeightsNode(UUID uuid) { super(uuid); }

	/**
	 * Request deltas, apply any parameter update, and push deltas to input edges.
	 */
	@Override
	public void backward() {
		inputValues = getInputEdges().get(0).getForwardValues();
		outputDeltas = getOutputEdges().get(0).getBackwardDeltas();
		inputDeltas = new double[inputSize];
		for (int in = 0; in < inputSize; in++) {
			inputDeltas[in] = 0;
			double inputValue = inputValues[in];
			for (int out = 0; out < outputSize; out++) {
				double weight = weights[in][out];
				double outputDelta = outputDeltas[out];
				double gradientPrev = gradients[in][out];
				double gradientCurr = (1 - alpha) * eta * outputDelta * inputValue + (alpha * gradientPrev);
				inputDeltas[in] += (weight * outputDelta);
				gradients[in][out] = gradientCurr;
				weights[in][out] += gradientCurr;
				weights[in][out] *= (1.0 - eta * lambda);
			}
		}
		getInputEdges().get(0).pushBackward(inputDeltas);
	}

	/**
	 * Request values from input edges, apply node calculations and push values to output edges.
	 */
	@Override
	public void forward() {
		inputValues = getInputEdges().get(0).getForwardValues();
		outputValues = new double[outputSize];
		for (int out = 0; out < outputSize; out++) {
			outputValues[out] = 0;
			for (int in = 0; in < inputSize; in++) {
				double input = inputValues[in];
				double weight = weights[in][out];
				outputValues[out] += (input * weight);
			}
		}
		getOutputEdges().get(0).pushForward(outputValues);
	}

	/**
	 * Append the particular node definition.
	 */
	public void toJSONObject(JSONObject def) {
		def.put("input-size", JSONTypes.NUMBER, inputSize);
		def.put("output-size", JSONTypes.NUMBER, outputSize);
		def.put("eta", JSONTypes.NUMBER, eta);
		def.put("alpha", JSONTypes.NUMBER, alpha);
		def.put("lambda", JSONTypes.NUMBER, lambda);
		JSONArray arrIn = new JSONArray();
		for (int in = 0; in < inputSize; in++) {
			JSONArray arrOut = new JSONArray();
			for (int out = 0; out < outputSize; out++) {
				arrOut.add(JSONTypes.NUMBER, weights[in][out]);
			}
			arrIn.add(JSONTypes.ARRAY, arrOut);
		}
		def.put("weights", JSONTypes.ARRAY, arrIn);
	}
}
