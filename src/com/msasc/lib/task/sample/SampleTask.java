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
package com.msasc.lib.task.sample;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import com.msasc.lib.task.Task;
import com.msasc.lib.util.Numbers;
import com.msasc.lib.util.Strings;

/**
 * Sample task used to test pool options.
 * 
 * @author Miquel Sas
 */
public class SampleTask extends Task {
	/**
	 * Creates a list of sample tasks.
	 * @param count          Number of tasks.
	 * @param iterations     Iterations per task.
	 * @param minSleep       Minimum sleep.
	 * @param maxSleep       Maximum sleep.
	 * @param throwException Force to throw an exception.
	 * @return A list of sample tasks.
	 */
	public static List<SampleTask> getTasks(
			int count,
			int iterations,
			int minSleep,
			int maxSleep,
			boolean throwException) {
		return getTasks(null, count, iterations, minSleep, maxSleep, throwException, null);
	}
	/**
	 * 
	 * @param prefix         Termination prefix.
	 * @param count          Number of tasks.
	 * @param iterations     Iterations per task.
	 * @param minSleep       Minimum sleep.
	 * @param maxSleep       Maximum sleep.
	 * @param throwException Force to throw an exception.
	 * @param suffix         Supplier of termination suffix.
	 * @return A list of sample tasks.
	 */
	public static List<SampleTask> getTasks(
			String prefix,
			int count,
			int iterations,
			int minSleep,
			int maxSleep,
			boolean throwException,
			Supplier<String> suffix) {

		if (minSleep > maxSleep) throw new IllegalArgumentException();

		int digits = Numbers.getDigits(count);
		Random rand = new Random();
		int throwIndex = iterations / 2;

		List<SampleTask> tasks = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			String id = (prefix != null ? prefix + " " : "") + Strings.leftPad(i, digits, "0");
			int throwAfter = (throwException ? throwIndex : -1);
			long sleep;
			if (maxSleep == minSleep) {
				sleep = maxSleep;
			} else {
				sleep = minSleep + rand.nextInt(maxSleep - minSleep + 1);
			}
			SampleTask task = new SampleTask(id, iterations, throwAfter, sleep);
			task.setTerminationSuffix(suffix);
			tasks.add(task);
		}
		return tasks;
	}

	/** Id or name. */
	private String id;
	/** Total number of iterations. */
	private int iterations;
	/** Throw after iterations. */
	private int throwAfterIterations;
	/** Iteration sleep. */
	private long sleep = 0;
	/** Start time. */
	private long startTime;
	/** End time. */
	private long endTime;
	/** Prefix for after execute message. */
	private String prefix;
	/** Supplier of termination suffix. */
	private Supplier<String> suffix;

	/**
	 * Constructor.
	 * 
	 * @param id                   Id or name.
	 * @param iterations           Number of iterations.
	 * @param throwAfterIterations Number of iterations to throw an exception, if
	 *                             less or equal to zero no exception is thrown.
	 * @param sleep                Milliseconds to sleep at each iteration.
	 */
	public SampleTask(String id, int iterations, int throwAfterIterations, long sleep) {
		this.id = id;
		this.iterations = iterations;
		this.throwAfterIterations = throwAfterIterations;
		this.sleep = sleep;
	}

	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @param suffix the terminationSuffix to set
	 */
	public void setTerminationSuffix(Supplier<String> suffix) {
		this.suffix = suffix;
	}

	@Override
	protected void beforeExecute() { startTime = System.currentTimeMillis(); }
	@Override
	protected void afterExecute() {
		endTime = System.currentTimeMillis();
		double duration = (endTime - startTime) / 1000.0;
		StringBuilder b = new StringBuilder();
		if (prefix != null) {
			b.append(prefix + " ");
		}
		b.append(id + " ");
		if (suffix != null) {
			b.append(suffix.get() + " ");
		}
		b.append(getState());
		b.append(" " + Numbers.getBigDecimal(duration, 2) + " ");
		System.out.println(b);
	}

	/**
	 * Returns the number of expected seconds.
	 * @return The number of expected seconds.
	 */
	private BigDecimal getExpectedSeconds() {
		double seconds = (iterations * sleep) / 1000.0;
		return Numbers.getBigDecimal(seconds, 2);
	}

	/**
	 * Do execute.
	 */
	@Override
	public void execute() throws Throwable {
		for (int i = 0; i < iterations; i++) {

			if (throwAfterIterations > 0 && i >= throwAfterIterations) {
				throw new Exception(id + "(" + getExpectedSeconds() + ") maximum iterations reached.");
			}

			/* Sleep. */
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException ignore) {
				Thread.currentThread().interrupt();
			}

			/* Check cancel requested. */
			if (shouldCancel()) {
				setCancelled();
				break;
			}
		}
	}
}
