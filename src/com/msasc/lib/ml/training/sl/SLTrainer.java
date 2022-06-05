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
package com.msasc.lib.ml.training.sl;

import java.util.List;

import com.msasc.lib.ml.data.Pattern;
import com.msasc.lib.ml.data.PatternSource;
import com.msasc.lib.ml.graph.Network;
import com.msasc.lib.task.Task;

/**
 * Supervised Learning trainer.
 * 
 * @author Miquel Sas
 */
public class SLTrainer extends Task {
	
	/** Pattern source training. */
	private PatternSource sourceTrain;
	/** Optional pattern source test. */
	private PatternSource sourceTest;
	/** The network to train. */
	private Network network;
	
	/** Number of epochs or iterations on the train source, default to 100. */
	private int epochs = 100;
	
	/** Progess listener. */

	/**
	 * Constructor.
	 */
	public SLTrainer() {}

	/**
	 * Execute this trainer task.
	 */
	@Override
	public void execute() throws Throwable {
		/* Validate. */
		validate();
		
		/* Initialize the network. */
		network.initialize();
	}

	/**
	 * Calculate the metrics of a pattern source versus a network
	 * @param label   Metrics label.
	 * @param source  Pattern source.
	 * @return The supervised learning metrics.
	 */
	private SLMetrics calculate(String label, PatternSource source) {

		if (label == null) throw new NullPointerException("Label required");

		int[] lengths = new int[network.getOutputEdges().size()];
		for (int i = 0; i < network.getOutputEdges().size(); i++) {
			lengths[i] = network.getOutputEdges().get(i).size();
		}
		
		SLMetrics metrics = new SLMetrics(label, lengths);
		
		source.reset();
		while (source.hasNext()) {
			
			if (shouldCancel()) {
				setCancelled();
				return metrics;
			}
			
			Pattern pattern = source.next();
			List<double[]> patternInput = pattern.getInputValues();
			List<double[]> patternOutput = pattern.getOutputValues();
			
			network.forward(patternInput);
			List<double[]> networkOutput = network.getOutputValues();
			
			metrics.compute(patternOutput, networkOutput);
			
		}

		return metrics;
	}


	/**
	 * Set the number of epochs.
	 * @param epochs The number of epochs.
	 */
	public void setEpochs(int epochs) { this.epochs = epochs; }
	/**
	 * Set the network to train.
	 * @param network The network.
	 */
	public void setNetwork(Network network) { this.network = network; }
	/**
	 * Set the pattern train source.
	 * @param sourceTrain The pattern source train.
	 */
	public void setSourceTrain(PatternSource sourceTrain) { this.sourceTrain = sourceTrain; }
	/**
	 * Set the optional test source.
	 * @param sourceTest The pattern source test.
	 */
	public void setSourceTest(PatternSource sourceTest) { this.sourceTest = sourceTest; }
	
	/**
	 * Validate the start conditions.
	 */
	private void validate() {
		if (network == null) throw new IllegalStateException("Null network");
		if (sourceTrain == null) throw new IllegalStateException("Null training source");
	}
}
