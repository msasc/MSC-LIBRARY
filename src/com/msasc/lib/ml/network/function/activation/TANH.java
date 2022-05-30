/*
 * Copyright (C) 2018 Miquel Sas
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.msasc.lib.ml.network.function.activation;

import com.msasc.lib.ml.network.function.Activation;

/**
 * TANH activation.
 *
 * @author Miquel Sas
 */
public class TANH extends Activation {

	/**
	 * Constructor.
	 */
	public TANH() {}

	/**
	 * Apply activations.
	 */
	@Override
	public double[] activations(double[] triggers) {
		double[] outputs = new double[triggers.length];
		double epos = 0;
		double eneg = 0;
		for (int i = 0; i < triggers.length; i++) {
			epos = Math.exp(triggers[i]);
			eneg = Math.exp(-triggers[i]);
			outputs[i] = (epos - eneg) / (epos + eneg);
		}
		return outputs;
	}

	/**
	 * Apply derivatives.
	 */
	@Override
	public double[] derivatives(double[] outputs) {
		double[] derivatives = new double[outputs.length];
		for (int i = 0; i < outputs.length; i++) {
			derivatives[i] = (1.0 + outputs[i]) * (1 - outputs[i]);
		}
		return derivatives;
	}
}
