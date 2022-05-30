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
package com.msasc.lib.task.progress;

/**
 * Listener of the progress of tasks and processes.
 * @author Miquel Sas
 */
public interface ProgressListener {
	/**
	 * Notify that the calling task has started.
	 */
	void notityStart();
	/**
	 * Notify that the calling task has finished.
	 */
	void notifyEnd();
	/**
	 * Notify a progress message, normally when the task is indeterminate.
	 * @param message  The message.
	 * @param messages Optional additional messages.
	 */
	void notifyProgress(String message, String... messages);
	/**
	 * Notify an increase in the work done. Zero or negative work increases or total work are not
	 * considered and skipped.
	 * @param message      User message.
	 * @param workIncrease Work increase.
	 * @param totalWork    Total work.
	 * @param messages     Optional additional messages.
	 */
	void notifyProgress(String message, double workIncrease, double totalWork, String... messages);
	/**
	 * Set that the task is indeterminate.
	 * @param indeterminate A boolean.
	 */
	void setIndeterminate(boolean indeterminate);
}
