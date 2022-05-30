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

import java.util.Arrays;
import java.util.UUID;

import com.msasc.lib.json.JSONArray;
import com.msasc.lib.json.JSONObject;
import com.msasc.lib.json.JSONTypes;
import com.msasc.lib.ml.network.graph.Node;

/**
 * A bias node without weights adjustments. The backward process does nothing.
 * @author Miquel Sas
 */
public class BiasNode extends Node {
	/**
	 * Builder to restore from a JSON object with the node definition.
	 * @param obj The JSON object.
	 * @return The node.
	 */
	public static BiasNode fromJSONObject(JSONObject obj) {
		String uuid = obj.get("uuid").getString();
		BiasNode node = new BiasNode(UUID.fromString(uuid));
		JSONArray arr = obj.get("output-values").getArray();
		node.outputValues = new double[arr.size()];
		for (int i = 0; i < arr.size(); i++) {
			node.outputValues[i] = arr.get(i).getDouble();
		}
		return node;
	}

	/** Bias output values. */
	private double[] outputValues;

	/**
	 * Constructor.
	 * @param size The bias size.
	 */
	public BiasNode(int size) {
		outputValues = new double[size];
		Arrays.fill(outputValues, 1.0);
	}
	/**
	 * Constructor to restore.
	 * @param uuid UUID:
	 */
	BiasNode(UUID uuid) { super(uuid); }

	/**
	 * Request deltas, apply any parameter update, and push deltas to input edges.
	 */
	@Override
	public void backward() {}

	/**
	 * Request values from input edges, apply node calculations and push values to output edges.
	 */
	@Override
	public void forward() {
		for (int out = 0; out < getOutputEdges().size(); out++) {
			getOutputEdges().get(out).pushForward(outputValues);
		}
	}
	
	/**
	 * Append the particular node definition.
	 */
	public void toJSONObject(JSONObject def) {
		JSONArray arr = new JSONArray();
		for (double value : outputValues) { arr.add(JSONTypes.NUMBER, value); }
		def.put("output-values", JSONTypes.ARRAY, arr);
	}
}
