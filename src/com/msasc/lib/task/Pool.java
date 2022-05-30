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
package com.msasc.lib.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

import com.msasc.lib.util.Numbers;
import com.msasc.lib.util.Strings;

/**
 * A pool to execute tasks implemented using a {@link ForkJoinPool}.
 * 
 * @author Miquel Sas
 */
public class Pool {

	/**
	 * Thread of the pool, named using the root name.
	 */
	private class ThreadTask extends ForkJoinWorkerThread {

		/**
		 * Constructor.
		 * @param pool The parent pool.
		 */
		protected ThreadTask(ForkJoinPool pool) {
			super(pool);
			int pad = Numbers.getDigits(pool.getParallelism());
			String index = Integer.toString(getPoolIndex());
			setName(name + "-THREAD-" + Strings.leftPad(index, pad, "0"));
		}
	}
	/**
	 * Task thread factory.
	 */
	private class ThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {

		@Override
		public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
			return new ThreadTask(pool);
		}
	}
	/**
	 * Uncaught exception handler.
	 */
	private class ThreadHandler implements Thread.UncaughtExceptionHandler {

		@Override
		public void uncaughtException(Thread t, Throwable e) {
		}
	}

	/**
	 * Root name of the pool.
	 */
	private String name;
	/**
	 * Fork join pool for execution.
	 */
	private ForkJoinPool pool;

	/**
	 * Constructor with a default "ROOT" name and "availableProcessors" pool size.
	 */
	public Pool() { this(Runtime.getRuntime().availableProcessors()); }
	/**
	 * Constructor assigning the pool size with a default "ROOT" name.
	 * @param poolSize The pool size.
	 */
	public Pool(int poolSize) { this("ROOT", poolSize); }
	/**
	 * Constructor the root name for threads and the pool size.
	 * @param name     The root name of the pool.
	 * @param poolSize The pool size.
	 */
	public Pool(String name, int poolSize) {
		if (name == null) {
			throw new NullPointerException("Root name can not be null");
		}
		if (poolSize < 1) {
			throw new IllegalArgumentException("Invalid pool size " + poolSize);
		}
		this.name = name;
		this.pool = new ForkJoinPool(poolSize, new ThreadFactory(), new ThreadHandler(), true);
	}

	/**
	 * Execute the argument collection of tasks until all finished, either by
	 * correctly ending their work or by throwing an exception.
	 * @param tasks The collection of tasks.
	 */
	public void execute(Collection<? extends Task> tasks) { pool.invokeAll(tasks); }
	/**
	 * Execute the argument list of tasks until all finished, either by correctly
	 * ending their work or by throwing an exception.
	 * @param tasks The list of tasks.
	 */
	public void execute(Task... tasks) {
		List<Task> taskList = new ArrayList<>();
		Collections.addAll(taskList, tasks);
		pool.invokeAll(taskList);
	}
	/**
	 * Execute a group of tasks that will cancel on exception.
	 * @param group The group of tasks.
	 */
	public void execute(Group group) { execute(group.tasks()); }

	/**
	 * Submit the collection of tasks for execution as soon as possible.
	 * @param tasks The collection of tasks.
	 */
	public void submit(Collection<? extends Task> tasks) {
		for (Task task : tasks) { pool.submit((Runnable) task); }
	}
	/**
	 * Submit a group of tasks that will cancel on exception.
	 * @param group The group of tasks.
	 */
	public void submit(Group group) { submit(group.tasks()); }
	/**
	 * Submit the list of tasks for execution as soon as possible.
	 * @param tasks The list of tasks.
	 */
	public void submit(Task... tasks) {
		for (Task task : tasks) { pool.submit((Runnable) task); }
	}

	/**
	 * Request the pool to shutdown.
	 */
	public void shutdown() { pool.shutdown(); }

	/**
	 * Request the pool to shutdown canceling already executing tasks.
	 */
	public void shutdownNow() { pool.shutdownNow(); }

	/**
	 * Wait for termination of the collection of tasks submitted.
	 * @param tasks The collection of tasks to wait for their termination.
	 */
	public void waitForTermination(Collection<? extends Task> tasks) {
		for (;;) {
			boolean allTerminated = true;
			for (Task task : tasks) {
				Thread.yield();
				if (!task.hasTerminated()) {
					allTerminated = false;
					break;
				}
			}
			if (allTerminated) break;
		}
	}
	/**
	 * Wait for termination of the group of tasks submitted.
	 * @param tasks The group of tasks to wait for their termination.
	 */
	public void waitForTermination(Group group) { waitForTermination(group.tasks()); }
}
