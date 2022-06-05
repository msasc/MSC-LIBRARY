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
package com.msasc.lib.fx;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

import com.msasc.lib.task.progress.ProgressListener;
import com.msasc.lib.util.Numbers;
import com.msasc.lib.util.Strings;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Font;

/**
 * Utility to monitor the progress of tasks.
 * @author Miquel Sas
 */
public class ProgressMonitor implements ProgressListener {

	/** Timer task. */
	private class TimerReport extends TimerTask {
		public void run() { timerReport(); }
	}

	/** Label to show the start time. */
	private Label labelStart;
	/** Label to show the elapsed time. */
	private Label labelElapsed;
	/** Label to show the expected time. */
	private Label labelExpected;
	/** Label to show the end time. */
	private Label labelEnd;
	/** Label to show the percentage. */
	private Label labelPercentage;
	/** Label to show the work done. */
	private Label labelWorkDone;
	/** Label to show the total work. */
	private Label labelTotalWork;

	/** List of progress bars to visually monitor progresses. */
	private List<ProgressBar> progressBars;
	/** Indeterminate status of the progress bars. */
	private List<Boolean> indeterminates;
	/** List of total works. */
	private List<Double> totalWorks;
	/** List of works done. */
	private List<Double> worksDone;

	/** Number of progress bars. */
	private int numBars = -1;

	/** List of message labels. */
	private List<Label> labels;
	/** List of last optional messages reported. */
	private List<String> messages;

	/** Number of message labels. */
	private int numLabels = -1;

	/** Start time. */
	private LocalDateTime timeStart;

	/** Timer to monitor progress. */
	private Timer timer;
	/** Timeout to notify in millis, to not collapse the event queue. */
	private long timeout = 50;

	/** Lock to permit concurrent notification. */
	private ReentrantLock lock = new ReentrantLock();

	/**
	 * Constructor.
	 */
	public ProgressMonitor() {}

	/**
	 * Check whether this progress monitor has been setup.
	 */
	private void checkSetup() {
		if (numLabels < 0 || numBars < 0) {
			throw new IllegalStateException("Monitor has not been setup");
		}
	}
	/**
	 * Check whether this progress monitor has been started.
	 */
	private void checkStarted() {
		if (timer == null || timeStart == null) {
			throw new IllegalStateException("Monitor has not been started");
		}
	}
	/**
	 * Check the label index.
	 * @param index The index.
	 */
	private void checkLabelIndex(int index) {
		if (index < 0 || index > labels.size() - 1) {
			throw new IllegalArgumentException("Invalid label index " + index);
		}
	}
	/**
	 * Check the progress bar index.
	 * @param index The index.
	 */
	private void checkProgressBarIndex(int index) {
		if (index < 0 || index > progressBars.size() - 1) {
			throw new IllegalArgumentException("Invalid progress bar index " + index);
		}
	}

	/**
	 * Return the additional message label.
	 * @param index The index of the label.
	 * @return The label.
	 */
	public Label getLabelMessage(int index) { return labels.get(index); }
	/**
	 * Return the percentage label.
	 * @return The percentage label.
	 */
	public Label getLabelPercentage() { return labelPercentage; }
	/**
	 * Return the work done label.
	 * @return The work done label.
	 */
	public Label getLabelWorkDone() { return labelWorkDone; }
	/**
	 * Return the total work label.
	 * @return The total work label.
	 */
	public Label getLabelTotalWork() { return labelTotalWork; }
	/**
	 * Return the start time label.
	 * @return The start time label.
	 */
	public Label getLabelStart() { return labelStart; }
	/**
	 * Return the elapsed time label.
	 * @return The elapsed time label.
	 */
	public Label getLabelElapsed() { return labelElapsed; }
	/**
	 * Return the expected time label.
	 * @return The expected time label.
	 */
	public Label getLabelExpected() { return labelExpected; }
	/**
	 * Return the end time label.
	 * @return The end time label.
	 */
	public Label getLabelEnd() { return labelEnd; }
	/**
	 * Return the progress bar.
	 * @param index The index of the progress bar.
	 * @return The progress bar.
	 */
	public ProgressBar getProgressBar(int index) { return progressBars.get(index); }

	/**
	 * Notify that the calling task has started. The timer is started and scheduled.
	 */
	public void notityStart() {
		checkSetup();
		try {
			lock.lock();
			for (int i = 0; i < numBars; i++) {
				totalWorks.set(i, 0.0);
				worksDone.set(i, 0.0);
			}
			timeStart = LocalDateTime.now();
			timer = new Timer(true);
			timer.schedule(new TimerReport(), timeout, timeout);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Notify that the calling task has finished. The timer is cancelled and a last time and
	 * progress calculation is done.
	 */
	public void notifyEnd() {
		try {
			lock.lock();
			checkStarted();
			timer.cancel();
			timerReport();
			timer = null;
			timeStart = null;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Notify a progress message, normally when the task is indeterminate.
	 * @param index   The message index.
	 * @param message The message.
	 */
	@Override
	public void notifyMessage(int index, String message) {
		checkStarted();
		checkLabelIndex(index);
		try {
			lock.lock();
			messages.set(index, message);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Notify an increase in the work done. Zero or negative work increases or total work are not
	 * considered and skipped. Additionally, in an indeterminate state, work and total are also
	 * skipped.
	 * @param index        Progress bar index.
	 * @param workIncrease Work increase.
	 * @param totalWork    Total work.
	 */
	@Override
	public void notifyProgress(int index, double workIncrease, double totalWork) {
		checkStarted();
		checkLabelIndex(index);
		try {
			lock.lock();
			double workDone = worksDone.get(index) + workIncrease;
			worksDone.set(index, workDone);
			totalWorks.set(index, totalWork);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Set that the progress bar is indeterminate.
	 * @param index         The index of the progress bar.
	 * @param indeterminate A boolean.
	 */
	@Override
	public void setIndeterminate(int index, boolean indeterminate) {
		checkProgressBarIndex(index);
		try {
			lock.lock();
			indeterminates.set(index, indeterminate);
			Platform.runLater(() -> {
				if (indeterminate) {
					progressBars.get(index).progressProperty().set(-1);
				} else {
					progressBars.get(index).progressProperty().set(0);
				}
			});
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Initialize and setup the listener to manage the argument number of labels and progress bars.
	 */
	@Override
	public void setup(int numLabels, int numBars) {

		if (numLabels < 1) throw new IllegalArgumentException("Invalid number of labels");
		if (numBars < 1) throw new IllegalArgumentException("Invalid number of progress bars");

		this.numLabels = numLabels;
		this.numBars = numBars;

		/* Time labels. */
		labelStart = new Label("");
		labelStart.idProperty().set("label-start");
		labelElapsed = new Label("");
		labelElapsed.idProperty().set("label-elapsed");
		labelExpected = new Label("");
		labelExpected.idProperty().set("label-expected");
		labelEnd = new Label("");
		labelEnd.idProperty().set("label-end");

		/* Percentage label. */
		labelPercentage = new Label("");
		labelPercentage.idProperty().set("label-percentage");
		double width = FX.getStringWidth("00100.0%", labelPercentage.getFont());
		labelPercentage.minWidthProperty().set(width);
		labelPercentage.alignmentProperty().set(Pos.CENTER_RIGHT);

		/* Work done and total work. */
		labelWorkDone = new Label("");
		labelWorkDone.idProperty().set("label-work-done");
		labelTotalWork = new Label("");
		labelTotalWork.idProperty().set("label-total-work");

		/* setup labels. */
		labels = new ArrayList<>();
		messages = new ArrayList<>();
		for (int i = 0; i < numLabels; i++) {
			Label label = new Label("");
			label.idProperty().set("label-message-" + i);
			label.fontProperty().set(new Font(12));
			labels.add(label);
			messages.add("");
		}

		/* Progress bars. */
		progressBars = new ArrayList<>();
		indeterminates = new ArrayList<>();
		worksDone = new ArrayList<>();
		totalWorks = new ArrayList<>();
		for (int i = 0; i < numBars; i++) {
			ProgressBar progressBar = new ProgressBar();
			progressBar.idProperty().set("progress-bar-" + i);
			progressBar.progressProperty().set(0);
			boolean indeterminate = false; // Default to determinate with zero progress
			progressBars.add(progressBar);
			indeterminates.add(indeterminate);
			worksDone.add(0.0);
			totalWorks.add(0.0);
		}
	}

	/**
	 * Report the status by filling information in the labels and progress bar.
	 */
	private void timerReport() {
		try {
			lock.lock();
			checkStarted();

			final String str_start;
			final String str_elapsed;
			final String str_expected;
			final String str_end;

			LocalDateTime timeNow = LocalDateTime.now();
			Duration timeElapsed = Duration.between(timeStart, timeNow);

			if (indeterminates.get(0) || totalWorks.get(0) == 0) {

				/* Time label. */
				boolean printDate = !timeStart.toLocalDate().equals(timeNow.toLocalDate());
				if (printDate) {
					str_start = timeStart.truncatedTo(ChronoUnit.SECONDS).toString();
				} else {
					str_start = timeStart.toLocalTime().truncatedTo(ChronoUnit.SECONDS).toString();
				}
				str_elapsed = Strings.toString(timeElapsed);
				
				str_expected = null;
				str_end = null;

			} else {

				/* Time message. */
				double workDone = worksDone.get(0);
				double totalWork = totalWorks.get(0);
				double elapsed = timeElapsed.toMillis();
				long expected = (long) (elapsed * totalWork / workDone);
				Duration timeExpected = Duration.ofMillis(expected);
				LocalDateTime timeEnd = timeStart.plus(timeExpected);
				boolean printDate = !timeStart.toLocalDate().equals(timeEnd.toLocalDate());
				if (printDate) {
					str_start = timeStart.truncatedTo(ChronoUnit.SECONDS).toString();
				} else {
					str_start = timeStart.toLocalTime().truncatedTo(ChronoUnit.SECONDS).toString();
				}
				str_elapsed = Strings.toString(timeElapsed);
				str_expected = Strings.toString(timeExpected);
				if (printDate) {
					str_end = timeEnd.truncatedTo(ChronoUnit.SECONDS).toString();
				} else {
					str_end = timeEnd.toLocalTime().truncatedTo(ChronoUnit.SECONDS).toString();
				}
			}

			Platform.runLater(() -> {
				if (str_start != null) labelStart.textProperty().set(str_start.toString());
				if (str_elapsed != null) labelElapsed.textProperty().set(str_elapsed.toString());
				if (str_expected != null) labelExpected.textProperty().set(str_expected.toString());
				if (str_end != null) labelEnd.textProperty().set(str_end.toString());
				for (int i = 0; i < numLabels; i++) {
					labels.get(i).textProperty().set(messages.get(i));
				}
			});
			
			for (int i = 0; i < numBars; i++) {
				
				/* Determinate. */
				boolean indeterminate = (indeterminates.get(i) || totalWorks.get(i) == 0);
				
				final String str_percentage;
				final String str_work_done;
				final String str_total_work;
				Double progressValue = null;
				
				if (indeterminate) {
					str_percentage = null;
					str_work_done = null;
					str_total_work = null;
				} else {
					/* Progress value. */
					double workDone = worksDone.get(i);
					double totalWork = totalWorks.get(i);
					progressValue = workDone / totalWork;
					/* Progress message. */
					str_percentage = Numbers.getBigDecimal(100 * progressValue, 1).toString() + "%";
					str_work_done = Numbers.getBigDecimal(workDone, 0).toString();
					str_total_work = Numbers.getBigDecimal(totalWork, 0).toString();
				}
				final boolean updateProgress = (progressValue != null);
				final double progress = (progressValue != null ? progressValue : 0);
				final int index = i;
				Platform.runLater(() -> {
					if (str_percentage != null) labelPercentage.textProperty().set(str_percentage);
					if (str_work_done != null) labelWorkDone.textProperty().set(str_work_done);
					if (str_total_work != null) labelTotalWork.textProperty().set(str_total_work);
					if (updateProgress) progressBars.get(index).progressProperty().set(progress);
				});
			}

		} finally {
			lock.unlock();
		}
	}
}
