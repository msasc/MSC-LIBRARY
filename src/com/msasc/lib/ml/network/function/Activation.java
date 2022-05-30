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

package com.msasc.lib.ml.network.function;

import com.msasc.lib.ml.network.function.activation.BipolarSigmoid;
import com.msasc.lib.ml.network.function.activation.ReLU;
import com.msasc.lib.ml.network.function.activation.Sigmoid;
import com.msasc.lib.ml.network.function.activation.SoftMax;
import com.msasc.lib.ml.network.function.activation.TANH;

/**
 * Activation function.
 *
 * @author Miquel Sas
 */
public abstract class Activation {

	/**
	 * Return the function given the name in a restore operation.
	 * @param name The activation name.
	 * @return The activation given the name.
	 */
	public static Activation get(String name) {
		if (name.equals(BipolarSigmoid.class.getSimpleName())) return new BipolarSigmoid();
		if (name.equals(ReLU.class.getSimpleName())) return new ReLU();
		if (name.equals(Sigmoid.class.getSimpleName())) return new Sigmoid();
		if (name.equals(SoftMax.class.getSimpleName())) return new SoftMax();
		if (name.equals(TANH.class.getSimpleName())) return new TANH();
		throw new IllegalArgumentException("Invalid ativation name: " + name);
	}

	/**
	 * Return an identification id of this activation function.
	 * 
	 * @return The id.
	 */
	public String getId() { return getClass().getSimpleName(); }

	/**
	 * Calculates the output values of the function given the trigger values.
	 * 
	 * @param triggers The trigger (weighted sum plus bias) values.
	 * @return The activation outputs .
	 */
	public abstract double[] activations(double[] triggers);

	/**
	 * Calculates the first derivatives of the function, given the outputs.
	 * 
	 * @param outputs The outputs obtained applying the triggers to
	 *                <i>activations</i>.
	 * @return The derivatives.
	 */
	public abstract double[] derivatives(double[] outputs);
}
