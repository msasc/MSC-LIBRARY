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
package com.msasc.lib.fx.ml.trainers;

import com.msasc.lib.fx.FX;
import com.msasc.lib.fx.progress.ProgressMonitor;
import com.msasc.lib.ml.training.sl.SLTrainer;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * A frame to manage the supervised learning trainer.
 * @author Miquel Sas
 */
public class SLTrainerFrame {

	/* Supervised learning trainer. */
	private SLTrainer trainer;

	/** Main stage. */
	private Stage stage;
	/** Progress monitor. */
	private ProgressMonitor pm;

	/** Label epoch. */
	private Label labelEpoch;
	/** Progress bar epoch. */
	private ProgressBar progressBarEpoch;
	/** Label total. */
	private Label labelTotal;
	/** Progress bar total. */
	private ProgressBar progressBarTotal;

	/**
	 * Default constructor.
	 */
	public SLTrainerFrame() {
		stage = new Stage();
		stage.initModality(Modality.NONE);
		stage.initStyle(StageStyle.DECORATED);
		stage.setResizable(true);
	}
	/**
	 * Constructor assigning te primary stage.
	 * @param primaryStage The primary stage.
	 */
	public SLTrainerFrame(Stage primaryStage, String title) {
		try {
			primaryStage.initModality(Modality.NONE);
			System.exit(0);
		} catch (Throwable ignore) {}
		stage = primaryStage;
		setTitle(title);
	}
	/**
	 * Constructor assigning the title.
	 * @param title The frame title.
	 */
	public SLTrainerFrame(String title) { this(); setTitle(title); }
	/**
	 * Set the title.
	 * @param title The frame title.
	 */
	public void setTitle(String title) { stage.setTitle(title); }
	/**
	 * Set the trainer and configure the frame.
	 * @param sltrainer The supervised learning trainer.
	 */
	public void setTrainer(SLTrainer sltrainer) {
		trainer = sltrainer;

		pm = new ProgressMonitor();
		pm.setup(2, 2);

		trainer.setProgressListener(pm);

		labelEpoch = pm.getLabelMessage(1);
		labelEpoch.setPadding(new Insets(5, 5, 5, 5));
		labelEpoch.maxWidthProperty().set(Double.MAX_VALUE);

		progressBarEpoch = pm.getProgressBar(1);
		progressBarEpoch.setPadding(new Insets(5, 5, 5, 5));
		progressBarEpoch.maxWidthProperty().set(Double.MAX_VALUE);

		labelTotal = pm.getLabelMessage(0);
		labelTotal.setPadding(new Insets(0, 5, 5, 5));
		labelTotal.maxWidthProperty().set(Double.MAX_VALUE);

		progressBarTotal = pm.getProgressBar(0);
		progressBarTotal.setPadding(new Insets(0, 5, 5, 5));
		progressBarTotal.maxWidthProperty().set(Double.MAX_VALUE);

		GridPane center = new GridPane();

		GridPane.setConstraints(labelEpoch, 0, 0);
		GridPane.setConstraints(progressBarEpoch, 1, 0);
		GridPane.setConstraints(labelTotal, 0, 1);
		GridPane.setConstraints(progressBarTotal, 1, 1);

		center.getChildren().add(labelEpoch);
		center.getChildren().add(progressBarEpoch);
		center.getChildren().add(labelTotal);
		center.getChildren().add(progressBarTotal);

		HBox hbox = new HBox();
		hbox.paddingProperty().set(new Insets(0, 5, 5, 5));
		hbox.getChildren().add(new Label("Start: "));
		hbox.getChildren().add(pm.getLabelStart());
		hbox.getChildren().add(new Label("   Elapsed: "));
		hbox.getChildren().add(pm.getLabelElapsed());
		hbox.getChildren().add(new Label("   Expected: "));
		hbox.getChildren().add(pm.getLabelExpected());
		hbox.getChildren().add(new Label("   End: "));
		hbox.getChildren().add(pm.getLabelEnd());
		hbox.getChildren().add(new Label("   State: "));
		hbox.getChildren().add(pm.getLabelState());

		GridPane.setConstraints(hbox, 0, 2, 2, 1);

		center.getChildren().add(hbox);

		ColumnConstraints c0 = new ColumnConstraints();
		c0.setPercentWidth(50);
		ColumnConstraints c1 = new ColumnConstraints();
		c1.setPercentWidth(50);
		center.getColumnConstraints().add(c0);
		center.getColumnConstraints().add(c1);

		/* Buttons. */

		Button buttonStart = new Button("Start");
		buttonStart.idProperty().set("start");
		buttonStart.onActionProperty().set(e -> {
			if (!trainer.isRunning()) {
				trainer.reinitialize();
				new Thread(trainer).start();
			}
		});

		Button buttonCancel = new Button("Cancel");
		buttonCancel.idProperty().set("cancel");
		buttonCancel.onActionProperty().set(e -> trainer.requestCancel());

		Button buttonClose = new Button("Close");
		buttonClose.idProperty().set("close");
		buttonClose.onActionProperty().set(e -> {
			trainer.requestCancel();
			stage.hide();
		});

		ButtonBar buttonBar = new ButtonBar();
		buttonBar.paddingProperty().set(new Insets(5, 5, 5, 5));
		buttonBar.getButtons().add(buttonStart);
		buttonBar.getButtons().add(buttonCancel);
		buttonBar.getButtons().add(buttonClose);

		BorderPane root = new BorderPane();
		root.setCenter(center);
		root.setBottom(buttonBar);

		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setWidth(FX.screenWidth(0.6));
	}

	/**
	 * Show the trainer frame.
	 */
	public void show() {
		stage.show();
		FX.centerOnScreen(stage);
	}
}
