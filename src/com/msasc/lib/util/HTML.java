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
package com.msasc.lib.util;

import java.util.List;

/**
 * Helper to build HTML content.
 * @author Miquel Sas
 */
public class HTML {

	private static void print(StringBuilder b, String s) { b.append(s); }
	private static void println(StringBuilder b) { b.append("\n"); }
	private static void println(StringBuilder b, String s) { println(b); print(b, s); }
	private static void printStyles(StringBuilder b, String... styles) {
		print(b, "\"");
		List<String> styleList = Strings.parseCSS(styles);
		for (String style : styleList) {
			print(b, style);
			print(b, ";");
		}
		print(b, "\"");
	}

	/** Head styles. */
	private StringBuilder head;
	/** Body. */
	private StringBuilder body;

	/**
	 * Constructor.
	 */
	public HTML() {
		head = new StringBuilder();
		body = new StringBuilder();
	}

	/**
	 * Add head styles.
	 * @param key    The class key, either a simple class with the form ".class", a tag of a
	 *               specific class like for instance "p.class", or a simple tag like "td".
	 * @param styles The list of styles.
	 */
	public void headStyles(String key, String... styles) {
		if (head.length() > 0) println(head);
		print(head, key);
		print(head, " {");
		List<String> styleList = Strings.parseCSS(styles);
		for (String style : styleList) {
			println(head);
			print(head, style);
			print(head, ";");
		}
		println(head, "}");
	}

	/**
	 * Print an end tag.
	 * 
	 * @param tag The tag.
	 */
	public void endTag(String tag) {
		print(body, "</");
		print(body, tag);
		print(body, ">");
	}
	/**
	 * Print text to the body, under the current tag, adding the styles.
	 * @param text   The text.
	 * @param styles The list of styles.
	 */
	public void print(String text, String... styles) {
		if (styles != null) {
			print(body, "<span style=");
			printStyles(body, styles);
			print(body, ">");
		}
		print(body, text);
		if (styles != null) {
			print(body, "</span>");
		}
	}

	/**
	 * Print a start tag.
	 * @param tag    The tag.
	 * @param styles The list of additional styles.
	 */
	public void startTag(String tag, String... styles) {
		startTagClassStyles(tag, null, styles);
	}
	/**
	 * Print a start tag.
	 * 
	 * @param tag    The tag.
	 * @param clazz  The class name.
	 * @param styles The list of additional styles.
	 */
	public void startTagClass(String tag, String clazz, String... styles) {
		startTagClassStyles(tag, clazz, styles);
	}
	/**
	 * Start a tag with optional class and styles.
	 * @param tag    The tag.
	 * @param clazz  The optional class.
	 * @param styles The optional styles.
	 */
	private void startTagClassStyles(String tag, String clazz, String... styles) {
		print(body, "<");
		print(body, tag);
		if (clazz != null) {
			print(body, " class=\"" + clazz + "\"");
		}
		if (styles != null) {
			print(body, " style=");
			printStyles(body, styles);
		}
		print(body, ">");
	}

	/**
	 * Print a table detail.
	 * @param text   The text.
	 * @param styles List of styles.
	 */
	public void td(String text, String... styles) {
		startTag("td", styles);
		print(text);
		endTag("td");
	}

	/**
	 * Return the HTML string.
	 */
	public String toString() {
		StringBuilder html = new StringBuilder();
		print(html, "<!DOCTYPE html>");
		println(html, "<html>");
		println(html, "<head>");
		println(html, "<style>");
		println(html, head.toString());
		println(html, "</style>");
		println(html, "</head>");
		println(html, "<body>");
		println(html, body.toString());
		println(html, "</body>");
		println(html, "</html>");
		return html.toString();
	}
}
