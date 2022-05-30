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

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebView;

/**
 * Alert functionality.
 * @author Miquel Sas
 */
public class Alert {

	/** Underlying dialog. */
	private Dialog dialog;
	/** Header flow text. */
	private TextFlow headerText;
	/** Content type. */
	private String contentType;

	/**
	 * Constructor assigning a size ratio.
	 * @param widthRatio  Width ratio.
	 * @param heightRatio Height ratio.
	 */
	public Alert(double widthRatio, double heightRatio) {
		this.dialog = new Dialog();
		this.dialog.sizeAndCenter(widthRatio, heightRatio);
	}

	/**
	 * Add a content text with a CSS style.
	 * @param text The text.
	 */
	public void addContentText(String text) { addContentText(text, null); }
	/**
	 * Add a content text with a CSS style.
	 * @param text  The text.
	 * @param style The style.
	 */
	public void addContentText(String text, String style) {
		Text textNode = new Text(text);
		textNode.setStyle(style);
		addContentText(textNode);
	}
	/**
	 * Add a content text.
	 * @param text The text node to add.
	 */
	public void addContentText(Text text) {
		if (!contentType.equals("TEXT")) throw new IllegalStateException("Content type is not text flow");
		TextFlow textFlow = (TextFlow) dialog.getCenter();
		textFlow.getChildren().add(text);
	}

	public void clearContentText() {
		if (!contentType.equals("TEXT")) throw new IllegalStateException("Content type is not text flow");
		TextFlow textFlow = (TextFlow) dialog.getCenter();
		textFlow.getChildren().clear();
	}

	/**
	 * Add a header text with a CSS style.
	 * @param text The text.
	 */
	public void addHeaderText(String text) { addHeaderText(text, null); }
	/**
	 * Add a header text with a CSS style.
	 * @param text  The text.
	 * @param style The style.
	 */
	public void addHeaderText(String text, String style) {
		Text textNode = new Text(text);
		if (style != null) textNode.setStyle(style);
		addHeaderText(textNode);
	}
	/**
	 * Add a header text with a CSS style.
	 * @param text The text.
	 */
	public void addHeaderText(Text text) {
		if (headerText == null) throw new IllegalStateException("Set type to other than plain");
		headerText.getChildren().add(text);
	}

	/**
	 * Set the buttons.
	 * @param buttons The list of buttons.
	 */
	public void setButtons(Button... buttons) {
		dialog.getButtonBar().getButtons().clear();
		dialog.getButtonBar().getButtons().addAll(buttons);
	}

	/**
	 * Set the content to any kind of node.
	 * @param node Te node.
	 */
	public void setContent(Node node) {
		dialog.setCenter(node);
		contentType = "NODE";
	}
	/**
	 * Set the content to be a TexFlow control.
	 */
	public void setContentText() {
		double pad = dialog.getPadding();
		TextFlow textFlow = new TextFlow();
		textFlow.setPadding(new Insets(0, pad, pad, pad));
		dialog.setCenter(textFlow);
		contentType = "TEXT";
	}
	/**
	 * Set the content to be a WebView control.
	 */
	public void setContentHTML(String html) {
		WebView webView = new WebView();
		webView.getEngine().loadContent(html);
		dialog.setCenter(webView);
		contentType = "HTML";
	}

	public void setTitle(String title) { dialog.getStage().setTitle(title); }

	public void setTypeConfirmation() { setType("CONFIRMATION"); }
	public void setTypeError() { setType("ERROR"); }
	public void setTypeInformation() { setType("INFORMATION"); }
	public void setTypeWarning() { setType("WARNING"); }
	public void setTypePlain() { setType("PLAIN"); }

	private void setType(String type) {

		try {
			if (type.equals("PLAIN")) { setButtons(Buttons.ok()); return; }

			double pad = dialog.getPadding();
			headerText = new TextFlow();
			headerText.setPadding(new Insets(0, pad, pad, pad));
			headerText.setMaxHeight(Region.USE_PREF_SIZE);

			ImageView image = null;
			if (type.equals("CONFIRMATION")) image = FX.getImageView("dialog-confirm.png");
			if (type.equals("ERROR")) image = FX.getImageView("dialog-error.png");
			if (type.equals("INFORMATION")) image = FX.getImageView("dialog-information.png");
			if (type.equals("WARNING")) image = FX.getImageView("dialog-warning.png");

			Separator hsepTop = new Separator(Orientation.HORIZONTAL);
			Separator hsepBottom = new Separator(Orientation.HORIZONTAL);

			GridPane.setConstraints(hsepTop, 0, 0, 2, 1);
			GridPane.setConstraints(headerText, 0, 1, 1, 1, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.NEVER,
				null);
			GridPane.setConstraints(image, 1, 1, 1, 1);
			GridPane.setConstraints(hsepBottom, 0, 2, 2, 1);

			GridPane header = new GridPane();
			header.getChildren().addAll(hsepTop, headerText, image, hsepBottom);

			dialog.setTop(header);

			if (type.equals("CONFIRMATION")) setButtons(Buttons.ok(), Buttons.cancel());
			else setButtons(Buttons.ok());
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public Button show() { return dialog.show(); }
}
