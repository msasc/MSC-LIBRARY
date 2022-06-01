/*
 * Copyright (C) 2018 Miquel Sas
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.msasc.lib.ml.function.match;

import java.util.ArrayList;
import java.util.List;

import com.msasc.lib.ml.function.Matcher;

/**
 * Evaluate the match when the correct output is a category, normally one element is
 * 1 and the rest 0. The index of the maximum of the network pattern has to be
 * the same than the index of the maximum of the pattern output.
 *
 * @author Miquel Sas
 */
public class CategoryMatcher implements Matcher {

	/**
	 * Constructor.
	 */
	public CategoryMatcher() {}

	/**
	 * Check whether the argument vectors can be considered to be the same.
	 * 
	 * @param patternOutput Pattern output
	 * @param networkOutput Network output.
	 * @return A boolean.
	 */
	@Override
	public boolean match(List<double[]> patternOutput, List<double[]> networkOutput) {
		
		List<Integer> indexesPattern = new ArrayList<>();
		List<Integer> indexesNetwork = new ArrayList<>();
		for (int i = 0; i < patternOutput.size(); i++) {
			double[] pattern = patternOutput.get(i);
			double[] network = networkOutput.get(i);
			int indexPattern = -1;
			int indexNetwork = -1;
			double maxPattern = Double.NEGATIVE_INFINITY;
			double maxNetwork = Double.NEGATIVE_INFINITY;
			for (int j = 0; j < pattern.length; j++) {
				if (pattern[j] > maxPattern) {
					maxPattern = pattern[j];
					indexPattern = j;
				}
				if (network[j] > maxNetwork) {
					maxNetwork = network[j];
					indexNetwork = j;
				}
			}
			indexesPattern.add(indexPattern);
			indexesNetwork.add(indexNetwork);
		}
		
		for (int i = 0; i < indexesPattern.size(); i++) {
			if (indexesPattern.get(i) != indexesNetwork.get(i)) {
				return false;
			}
		}
		return true;
	}

}
