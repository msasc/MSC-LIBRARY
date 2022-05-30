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

/**
 * A group of tasks, packed together to request cancel all group tasks when one of them issues an
 * exception.
 * @author Miquel Sas
 */
public class Group {

	/** List of tasks. */
	List<Task> tasks = new ArrayList<>();

	/** Constructor. */
	public Group() {}
	/**
	 * Add a task to the group.
	 * @param task The task.
	 */
	public void add(Task task) { task.group = this; tasks.add(task); }
	/**
	 * Add the collection of tasks.
	 * @param tasks The collection of tasks.
	 */
	public void addAll(Collection<? extends Task> tasks) { for (Task task : tasks) { add(task); } }
	/**
	 * Returns the list of tasks as an unmodifiable collection.
	 * @return The list of tasks.
	 */
	public Collection<Task> tasks() { return Collections.unmodifiableCollection(tasks); }
	/**
	 * Request all the tasks to cancel.
	 */
	public void requestCancel() { tasks.forEach(task -> task.requestCancel()); }
}
