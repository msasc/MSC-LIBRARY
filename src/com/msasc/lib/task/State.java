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

/**
 * Task states.
 * 
 * @author Miquel Sas
 */
public enum State {
	/** The task is ready to be executed. */
	READY,
	/** The task is running. */
	RUNNING,
	/** The task has completed successfully. */
	SUCCEEDED,
	/** The task has been cancelled, usually as a result of an external request. */
	CANCELLED,
	/** The task has failed, an exception can be retrieved from the task. */
	FAILED
}
