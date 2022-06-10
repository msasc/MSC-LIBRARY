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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.msasc.lib.util.Files;
import com.msasc.lib.util.Properties;

import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Screen;
import javafx.stage.Window;

/**
 * FX utilities.
 * @author Miquel Sas
 */
public class FX {
	/**
	 * String to determine the logical height of a font.
	 */
	private static String STRING_FOR_HEIGHT = null;
	static {
		StringBuilder b = new StringBuilder();
		for (char c = 32; c < 128; c++) {
			b.append(c);
		}
		STRING_FOR_HEIGHT = b.toString();
	}

	/**
	 * Center the window on screen.
	 * @param w The window to center.
	 */
	public static void centerOnScreen(Window w) {
		Rectangle2D r = Screen.getPrimary().getVisualBounds();
		w.setX((r.getWidth() - w.getWidth()) / 2);
		w.setY((r.getHeight() - w.getHeight()) / 2);
	}
	/**
	 * Fill the source list of nodes with nodes starting at the argument node, included.
	 * @param nodes     The list to fill.
	 * @param startNode The starting node.
	 */
	public static void fillNodesFrom(List<Node> nodes, Node startNode) {
		nodes.add(startNode);
		if (startNode instanceof Parent) {
			Parent parent = (Parent) startNode;
			if (parent instanceof TabPane) {
				((TabPane) parent).getTabs().forEach(tab -> fillNodesFrom(nodes, tab.getContent()));
			} else {
				parent.getChildrenUnmodifiable().forEach(child -> fillNodesFrom(nodes, child));
			}
		}
	}
	/**
	 * Return the resource as an ImageView.
	 * @param resource The resource path.
	 * @return The image view.
	 * @throws IOException If an error occurs.
	 */
	public static ImageView getImageView(String resource) throws IOException {
		File file = Files.getFileFromPathEntries(resource);
		FileInputStream fi = new FileInputStream(file);
		Image image = new Image(fi);
		return new ImageView(image);
	}
	/**
	 * Return the properties stored in the node user data.
	 * @param node The node.
	 * @return The properties.
	 */
	public static Properties getProperties(Node node) {
		Object userData = node.getUserData();
		if (userData != null && !(userData instanceof Properties)) {
			throw new IllegalArgumentException("Node has user data of type: " + userData.getClass());
		}
		Properties properties = (Properties) node.getUserData();
		if (properties == null) {
			properties = new Properties();
			node.setUserData(properties);
		}
		return properties;
	}
	/**
	 * Return the logical string bounds for text sizes calculations.
	 * @param string The string.
	 * @param font   The optional font.
	 * @return The bounds.
	 */
	public static Bounds getStringBounds(String string, Font font, String style) {
		Text text = new Text(string);
		if (font != null) text.setFont(font);
		if (style != null) text.setStyle(style);
		text.setBoundsType(TextBoundsType.LOGICAL_VERTICAL_CENTER);
		return text.getLayoutBounds();
	}
	/**
	 * Return the string height for the font.
	 * @param font The font
	 * @return The height.
	 */
	public static double getStringHeight(Font font) {
		return getStringBounds(STRING_FOR_HEIGHT, font, null).getHeight();
	}
	/**
	 * Return the string height for the font.
	 * @param style The text style.
	 * @return The height.
	 */
	public static double getStringHeight(String style) {
		return getStringBounds(STRING_FOR_HEIGHT, null, style).getHeight();
	}
	/**
	 * Return the string width.
	 * @param string The string.
	 * @return The width.
	 */
	public static double getStringWidth(String string) {
		return getStringBounds(string, null, null).getWidth();
	}
	/**
	 * Return the string width.
	 * @param string The string.
	 * @param font   Optional font.
	 * @return The width.
	 */
	public static double getStringWidth(String string, Font font) {
		return getStringBounds(string, font, null).getWidth();
	}
	/**
	 * Return the string width.
	 * @param string The string.
	 * @param style  The text style.
	 * @return The width.
	 */
	public static double getStringWidth(String string, String style) {
		return getStringBounds(string, null, style).getWidth();
	}
	/**
	 * Return a ratio of the screen height.
	 * @param ratio The ratio.
	 * @return The screen height multiplied by the ratio.
	 */
	public static double screenHeight(double ratio) {
		Rectangle2D r = Screen.getPrimary().getVisualBounds();
		return r.getHeight() * ratio;
	}
	/**
	 * Return a of the screen width.
	 * @param ratio The ratio.
	 * @return The screen width multiplied by the ratio.
	 */
	public static double screenWidth(double ratio) {
		Rectangle2D r = Screen.getPrimary().getVisualBounds();
		return r.getWidth() * ratio;
	}
	
	public static void setDisable(Scene scene, boolean disable, String... ids) {
		setDisable(scene.getRoot(), disable, ids);
	}
	
	public static void setDisable(Node start, boolean disable, String... ids) {
		List<Node> nodes = new ArrayList<>();
		fillNodesFrom(nodes, start);
		for (Node node : nodes) {
			for (String id : ids) {
				if (node.getId() != null && node.getId().equals(id)) {
					node.setDisable(disable);
				}
			}
		}
	}
	
	public static void setVisible(Scene scene, boolean visible, String... ids) {
		setVisible(scene.getRoot(), visible, ids);
	}

	public static void setVisible(Node start, boolean visible, String... ids) {
		List<Node> nodes = new ArrayList<>();
		fillNodesFrom(nodes, start);
		for (Node node : nodes) {
			for (String id : ids) {
				if (node.getId() != null && node.getId().equals(id)) {
					node.setVisible(visible);
				}
			}
		}
	}

	/**
	 * Size the window by screen s and center it.
	 * @param window      The window.
	 * @param widthRatio  Width .
	 * @param heightRatio Height .
	 */
	public static void sizeAndCenter(Window window, double widthRatio, double heightRatio) {
		Rectangle2D rect = Screen.getPrimary().getVisualBounds();
		double width = rect.getWidth() * widthRatio;
		double height = rect.getHeight() * heightRatio;
		double x = (rect.getWidth() - width) / 2;
		double y = (rect.getHeight() - height) / 2;
		window.setWidth(width);
		window.setHeight(height);
		window.setX(x);
		window.setY(y);
	}

}
