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

import com.msasc.lib.ml.function.Matcher;
import com.msasc.lib.ml.function.match.CategoryMatcher;
import com.msasc.lib.util.Vector;

/**
 * Metrics used to evaluate the performance in a supervised learning training process.
 * 
 * @author Miquel Sas
 */
public class SLMetrics {

	/** M-Squared vector. */
	private List<double[]> errors;
	/** Match function, default is match category. */
	private Matcher matcher = new CategoryMatcher();

	/** Number of matches. */
	private int matches;
	/** Calls to compute. */
	private double calls;

	/* List of lengths of the arrays of pattern and network output. */
	private int[] lengths;

	/** Label. */
	private String label;
	/** Average absolute error. */
	private double errorAvg;
	/** Average absolute error standard deviation. */
	private double errorStd;
	/** Performance. */
	private double performance;

	/**
	 * Constructor.
	 * @param label   Label to name the series of metrics being processed.
	 * @param lengths List of lengths of the arrays of pattern and network output.
	 */
	public SLMetrics(String label, int... lengths) {
		if (lengths == null || lengths.length == 0) throw new IllegalArgumentException();
		this.label = label;
		this.lengths = lengths;
		reset();
	}

	/**
	 * Compute a pattern and a network output to calculate the metric values.
	 * @param patternOutput The pattern output.
	 * @param networkOutput The network output.
	 */
	public void compute(List<double[]> patternOutput, List<double[]> networkOutput) {

		boolean valid = true;
		valid &= (patternOutput.size() == lengths.length);
		valid &= (networkOutput.size() == lengths.length);
		for (int i = 0; i < patternOutput.size(); i++) {
			valid &= (patternOutput.get(i).length == lengths[i]);
			valid &= (networkOutput.get(i).length == lengths[i]);
		}
		if (!valid) throw new IllegalArgumentException();

		for (int i = 0; i < lengths.length; i++) {
			double[] pattern = patternOutput.get(i);
			double[] network = networkOutput.get(i);
			double[] error = errors.get(i);
			for (int j = 0; j < lengths[i]; j++) {
				error[j] += Math.abs(pattern[j] - network[j]);
			}
		}

		if (matcher.match(patternOutput, networkOutput)) {
			matches++;
		}

		calls++;
		int length = 0;
		for (int len : lengths) { length += len; }
		double[] error = new double[length];
		int index = 0;
		for (int i = 0; i < lengths.length; i++) {
			for (int j = 0; j < lengths[i]; j++) {
				error[index++] = errors.get(i)[j] / calls;
			}
		}
		errorAvg = Vector.mean(error);
		errorStd = Vector.stddev(error, errorAvg);
	}

	/**
	 * Return the average of the error.
	 * @return The average of the error.
	 */
	public double getErrorAvg() { return errorAvg; }
	/**
	 * Return the standard deviation of the error.
	 * @return The standard deviation of the error.
	 */
	public double getErrorStd() { return errorStd; }
	/**
	 * Return the label.
	 * @return The label.
	 */
	public String getLabel() { return label; }
	/**
	 * Return the number of matches.
	 * @return The number of matches.
	 */
	public int getMatches() { return matches; }
	/**
	 * Return the performance.
	 * @return The performance.
	 */
	public double getPerformance() { return performance; }

	/**
	 * Reset.
	 */
	public void reset() {
		errors = new ArrayList<double[]>();
		for (int length : lengths) {
			errors.add(new double[length]);
		}
		matches = 0;
		calls = 0;
		errorAvg = 0;
		errorStd = 0;
		performance = 0;
	}

	/**
	 * Set the matcher.
	 * @param matcher The matcher.
	 */
	public void setMatcher(Matcher matcher) { this.matcher = matcher; }
}
