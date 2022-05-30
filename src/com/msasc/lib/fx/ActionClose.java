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

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.stage.Window;

/**
 * Action to close the parent stage if any, related to the action event source.
 * @author Miquel Sas
 */
public class ActionClose implements EventHandler<ActionEvent> {
	/**
	 * Constructor.
	 */
	public ActionClose() {}
	/**
	 * Handle the event.
	 */
	@Override
	public void handle(ActionEvent event) {
		Object source = event.getSource();
		if (source != null && source instanceof Node) {
			Node node = (Node) source;
			Window window = node.getScene().getWindow();
			if (window != null) window.hide();
		}
	}
}
