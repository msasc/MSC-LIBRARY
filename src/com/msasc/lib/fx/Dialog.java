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

import java.util.ArrayList;
import java.util.List;

import com.msasc.lib.util.Lists;
import com.msasc.lib.util.Properties;

import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Separator;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * Implementation of the dialog functionality. The root is an {@link BorderPane} with a
 * {@link ButtonBar} at the bottom and any other layout at the center. Methods are provided to
 * access the center, the top, and the left and right panes. The bottom pane is reserved to the
 * button bar.
 * <p>
 * Buttons can have any action and, if if tagged so, a close action is added to be executed after
 * its own action.
 * <p>
 * Since buttons have access to the scene through the source in the action event, they can interact
 * with any dialog control. Additionally, through their properties stored in the user data, the have
 * access to this dialog with the key "dialog" and its properties to store any information that
 * could be of interest even after closing the dialog.
 * 
 * @author Miquel Sas
 */
public class Dialog {

	/** Stage. */
	private Stage stage;
	/** Content border pane. */
	private BorderPane content;
	/** Button bar. */
	private ButtonBar buttonBar;

	/** Close on escape property. */
	private boolean closeOnEscape = true;
	/** Default padding. */
	private double padding = 5;

	/** Properties. */
	private Properties properties = new Properties();

	/**
	 * Constructor.
	 */
	public Dialog() { this(null, 5); }
	/**
	 * Constructor.
	 * @param owner The owner window.
	 */
	public Dialog(Window owner) { this(owner, 5); }
	/**
	 * Constructor.
	 * @param owner   The owner window.
	 * @param padding Default padding, starting with button bar. Default is 5.
	 */
	public Dialog(Window owner, double padding) {

		/* Default padding. */
		this.padding = padding;

		/* The stage. */
		stage = new Stage();
		if (owner == null) {
			stage.initModality(Modality.APPLICATION_MODAL);
		} else {
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(owner);
		}
		stage.initStyle(StageStyle.DECORATED);
		stage.setResizable(true);

		/* Root. */
		BorderPane root = new BorderPane();
		root.setId("root");

		/* The button bar with its separator. */
		buttonBar = new ButtonBar();
		buttonBar.setId("button-bar");
		buttonBar.setPadding(new Insets(getPadding()));
		ListChangeListener<? super Node> listener = c -> setupButtons(c);
		buttonBar.getButtons().addListener(listener);

		Separator separator = new Separator();

		VBox vbox = new VBox();
		vbox.getChildren().addAll(separator, buttonBar);
		
		root.setBottom(vbox);

		content = new BorderPane();
		content.setId("content");
		
		root.setCenter(content);

		/* Launch the scene. */
		Scene scene = new Scene(root);
		stage.setScene(scene);
	}

	/**
	 * Return the button bar to be able to add or insert buttons.
	 * @return The button bar.
	 */
	public ButtonBar getButtonBar() { return buttonBar; }

	/**
	 * Return the center node.
	 * @return The center node.
	 */
	public Node getCenter() { return content.getCenter(); }
	/**
	 * Return the top node.
	 * @return The top node.
	 */
	public Node getTop() { return content.getTop(); }
	/**
	 * Return the bottom node.
	 * @return The bottom node.
	 */
	public Node getBottom() { return content.getBottom(); }
	/**
	 * Return the left node.
	 * @return The left node.
	 */
	public Node getLeft() { return content.getLeft(); }
	/**
	 * Return the right node.
	 * @return The right node.
	 */
	public Node getRight() { return content.getRight(); }

	/**
	 * Return the default padding to be used when building the content if so required.
	 * @return The default padding.
	 */
	public double getPadding() { return padding; }
	/**
	 * Return the dialog general properties.
	 * @return The properties.
	 */
	public Properties getProperties() { return properties; }
	/**
	 * Return the scene.
	 * @return The scene.
	 */
	public Scene getScene() { return stage.getScene(); }
	/**
	 * Return the stage.
	 * @return The stage.
	 */
	public Stage getStage() { return stage; }

	/**
	 * Set the close-on-escape property.
	 * @param close A boolean.
	 */
	public void setCloseOnEscape(boolean close) { this.closeOnEscape = close; }

	/**
	 * Set the center node above the buttons.
	 * @param center The center node.
	 */
	public void setCenter(Node center) { content.setCenter(center); }
	/**
	 * Set the top node.
	 * @param top The top node.
	 */
	public void setTop(Node top) { content.setTop(top); }
	/**
	 * Set the bottom node.
	 * @param bottom The bottom node.
	 */
	public void setBottom(Node bottom) { content.setBottom(bottom); }
	/**
	 * Set the left node.
	 * @param left The left node.
	 */
	public void setLeft(Node left) { content.setLeft(left); }
	/**
	 * Set the right node.
	 * @param right The right node.
	 */
	public void setRight(Node right) { content.setRight(right); }

	/**
	 * Setup a button that was added to the button bar.
	 * @param c The change interface that indicates what happened.
	 */
	private void setupButtons(ListChangeListener.Change<? extends Node> c) {
		if (c.next() && c.wasAdded()) {
			List<? extends Node> nodes = c.getList();
			int from = c.getFrom();
			int to = c.getTo();
			for (int i = from; i < to; i++) {
				Node node = nodes.get(i);
				if (!(node instanceof Button)) continue;
				Button button = (Button) node;

				/* New list of actions. */
				ActionList actions = new ActionList();
				EventHandler<ActionEvent> action = button.getOnAction();
				if (action != null) {
					if (action instanceof ActionList) {
						ActionList currentActions = (ActionList) action;
						actions.handlers().addAll(currentActions.handlers());
					} else {
						actions.handlers().add(action);
					}
				}
				if (!(Lists.getLast(actions.handlers()) instanceof ActionClose)) {
					Boolean close = FX.getProperties(button).getBoolean("close");
					if (close != null && close) {
						actions.handlers().add(new ActionClose());
					}
				}
				actions.handlers().add(e -> setResult(e));
				button.setOnAction(actions);

				/* Set this dialog as a button property. */
				FX.getProperties(button).put("dialog", this);
			}
		}
	}

	/**
	 * Set the result button.
	 * @param event The action event.
	 */
	private void setResult(ActionEvent event) {
		Object source = event.getSource();
		if (source != null && source instanceof Button) {
			properties.put("result-button", (Button) source);
		}
	}

	/**
	 * Size the window by screen ratios and center it.
	 * @param widthRatio  Width .
	 * @param heightRatio Height .
	 */
	public void sizeAndCenter(double widthRatio, double heightRatio) {
		FX.sizeAndCenter(getStage(), widthRatio, heightRatio);
	}

	/**
	 * Show the dialog and return the button that closed it.
	 * @return The button that closed the dialog.
	 */
	public Button show() {

		/* Set ESC to close the stage. */
		if (closeOnEscape) {
			List<Node> nodes = new ArrayList<>();
			FX.fillNodesFrom(nodes, getStage().getScene().getRoot());
			for (Node node : nodes) {
				node.setOnKeyPressed(e -> {
					if (e.getCode() == KeyCode.ESCAPE) {
						getStage().close();
					}
				});
			}
		}

		/* Do show. */
		getStage().showAndWait();

		/* Return the result button. */
		Button result = (Button) properties.getObject("result-button");
		return result;
	}
}
