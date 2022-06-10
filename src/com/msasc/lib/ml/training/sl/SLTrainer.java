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

import java.util.ArrayList;
import java.util.List;

import com.msasc.lib.ml.data.Pattern;
import com.msasc.lib.ml.data.PatternSource;
import com.msasc.lib.ml.graph.Network;
import com.msasc.lib.task.State;
import com.msasc.lib.task.Task;
import com.msasc.lib.task.progress.ProgressListener;
import com.msasc.lib.util.Numbers;
import com.msasc.lib.util.Vector;

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

	/** Progress listener. */
	private ProgressListener listener;

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

		/* Start monitor. */
		notifyStart();

		/* Initialize the network. */
		network.initialize();

		/* Total work and work done. */
		double totalWork = sourceTrain.size() * epochs;
		double totalDone = 0;

		/* Iterate epochs. */
		resetProgress(0);
		for (int epoch = 0; epoch < epochs; epoch++) {

			/* Check cancelled. */
			if (shouldCancel()) {
				setCancelled();
				break;
			}

			/* Reset the source and scan it. */
			resetProgress(1);
			double epochWork = sourceTrain.size();
			double epochDone = 0;
			sourceTrain.reset();
			while (sourceTrain.hasNext()) {

				/* Check cancelled. */
				if (shouldCancel()) {
					setCancelled();
					break;
				}

				/* Notify. */
				totalDone++;
				epochDone++;

				StringBuilder totalMsg = new StringBuilder();
				totalMsg.append("Total work ");
				totalMsg.append((int) totalDone);
				totalMsg.append(" of ");
				totalMsg.append((int) totalWork);
				totalMsg.append(" of ");
				double totalPercent = totalDone * 100.0 / totalWork;
				totalMsg.append(Numbers.getBigDecimal(totalPercent, 2));
				totalMsg.append("%)");
				notifyMessage(0, totalMsg.toString());
				notifyProgress(0, 1, totalWork);

				StringBuilder epochMsg = new StringBuilder();
				epochMsg.append("Epoch work ");
				epochMsg.append((int) epochDone);
				epochMsg.append(" of ");
				epochMsg.append((int) epochWork);
				epochMsg.append(" of ");
				double epochPercent = epochDone * 100.0 / epochWork;
				epochMsg.append(Numbers.getBigDecimal(epochPercent, 2));
				epochMsg.append("%)");
				notifyMessage(1, epochMsg.toString());
				notifyProgress(1, 1, epochWork);

				/* Read pattern and process it. */
				Pattern pattern = sourceTrain.next();
				List<double[]> patternInput = pattern.getInputValues();
				List<double[]> patternOutput = pattern.getOutputValues();
				network.forward(patternInput);
				List<double[]> networkOutput = network.getOutputValues();
				List<double[]> networkDeltas = new ArrayList<>();
				for (int i = 0; i < networkOutput.size(); i++) {
					double[] p_output = patternOutput.get(i);
					double[] n_output = networkOutput.get(i);
					double[] n_deltas = Vector.subtract(p_output, n_output);
					networkDeltas.add(n_deltas);
				}
				network.backward(networkDeltas);

			}

		}

		/* End monitor. */
		notifyEnd();

	}

	/**
	 * Calculate the metrics of a pattern source versus a network
	 * @param label  Metrics label.
	 * @param source Pattern source.
	 * @return The supervised learning metrics.
	 */
	private SLMetrics calculateMetrics(String label, PatternSource source) {

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
	 * Notify that the calling task has started.
	 */
	private void notifyStart() {
		if (listener != null) listener.notifyStart();
	}
	/**
	 * Notify that the calling task has finished.
	 */
	private void notifyEnd() {
		if (listener != null) listener.notifyEnd();
	}
	/**
	 * Notify a progress message, normally when the task is indeterminate.
	 * @param index   The message index.
	 * @param message The message.
	 */
	private void notifyMessage(int index, String message) {
		if (listener != null) listener.notifyMessage(index, message);
	}
	/**
	 * Notify an increase in the work done. Zero or negative work increases or total work are not
	 * considered and skipped. Additionally, in an indeterminate state, work and total are also
	 * skipped.
	 * @param index        Progress bar index.
	 * @param workIncrease Work increase.
	 * @param totalWork    Total work.
	 */
	private void notifyProgress(int index, double workIncrease, double totalWork) {
		if (listener != null) listener.notifyProgress(index, workIncrease, totalWork);
	}
	/**
	 * Reset the progress bar to zero work done.
	 * @param index The index of the progress bar.
	 */
	private void resetProgress(int index) {
		if (listener != null) listener.resetProgress(index);
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
	 * Set the progress listener.
	 * @param listener The progress listener.
	 */
	public void setProgressListener(ProgressListener listener) { this.listener = listener; }

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
	 * Override to notify.
	 */
	@Override
	protected void setState(State state) {
		super.setState(state);
		if (listener != null) listener.notifyState(state);
	}

	/**
	 * Validate the start conditions.
	 */
	private void validate() {
		if (network == null) throw new IllegalStateException("Null network");
		if (sourceTrain == null) throw new IllegalStateException("Null training source");
	}
}
