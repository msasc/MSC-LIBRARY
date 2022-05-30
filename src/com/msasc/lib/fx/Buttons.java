/*
 * Copyright (C) 2017 Miquel Sas
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.msasc.lib.fx;

import com.msasc.lib.util.resources.StringRes;

import javafx.scene.control.Button;

/**
 * Button utilities. Buttons all around this development system are expected to have properties set
 * in the user data,
 * properties like the button group, the order in the group, or any useful property to handle the
 * action.
 *
 * @author Miquel Sas
 */
public class Buttons {
	
	/** Id of the default ACCEPT button. */
	public static final String ACCEPT = "button-accept";
	/** Id of the default APPLY button. */
	public static final String APPLY = "button-apply";
	/** Id of the default CANCEL button. */
	public static final String CANCEL = "button-cancel";
	/** Id of the default CLOSE button. */
	public static final String CLOSE = "button-close";
	/** Id of the default FINISH button. */
	public static final String FINISH = "button-finish";
	/** Id of the default IGNORE button. */
	public static final String IGNORE = "button-ignore";
	/** Id of the default NEXT button. */
	public static final String NEXT = "button-netx";
	/** Id of the default NO button. */
	public static final String NO = "button-no";
	/** Id of the default OK button. */
	public static final String OK = "button-ok";
	/** Id of the default LOAD button. */
	public static final String LOAD = "button-load";
	/** Id of the default OPEN button. */
	public static final String OPEN = "button-open";
	/** Id of the default PREVIOUS button. */
	public static final String PREVIOUS = "button-previous";
	/** Id of the default RETRY button. */
	public static final String RETRY = "button-retry";
	/** Id of the default SELECT button. */
	public static final String SELECT = "button-select";
	/** Id of the default YES button. */
	public static final String YES = "button-yes";

	/**
	 * Return the default accept button.
	 * @return The default accept button.
	 */
	public static Button accept() { return button(ACCEPT, str("buttonAccept"), true, false, true); }
	/**
	 * Return the default apply button.
	 * @return The default apply button.
	 */
	public static Button apply() { return button(APPLY, str("buttonApply"), true, false, true); }
	/**
	 * Return the default cancel button.
	 * @return The default cancel button.
	 */
	public static Button cancel() { return button(CANCEL, str("buttonCancel"), false, true, true); }
	/**
	 * Return the default close button.
	 * @return The default close button.
	 */
	public static Button close() { return button(CLOSE, str("buttonClose"), false, false, true); }
	/**
	 * Return the default close button.
	 * @return The default close button.
	 */
	public static Button finish() { return button(FINISH, str("buttonFinish"), false, false, true); }
	/**
	 * Return the default ignore button.
	 * @return The default ignore button.
	 */
	public static Button ignore() { return button(IGNORE, str("buttonIgnore"), false, false, true); }
	/**
	 * Return the default load button.
	 * @return The default load button.
	 */
	public static Button load() { return button(LOAD, str("buttonLoad"), false, false, false); }
	/**
	 * Return the default next button.
	 * @return The default next button.
	 */
	public static Button next() { return button(NEXT, str("buttonNext"), false, false, false); }
	/**
	 * Return the default no button.
	 * @return The default no button.
	 */
	public static Button no() { return button(NO, str("buttonNo"), false, true, true); }
	/**
	 * Return the default ok button.
	 * @return The default ok button.
	 */
	public static Button ok() { return button(OK, str("buttonOk"), true, false, true); }
	/**
	 * Return the default open button.
	 * @return The default open button.
	 */
	public static Button open() { return button(OPEN, str("buttonOpen"), true, false, true); }
	/**
	 * Return the default previous button.
	 * @return The default previous button.
	 */
	public static Button previous() { return button(PREVIOUS, str("buttonPrevious"), true, false, true); }
	/**
	 * Return the default retry button.
	 * @return The default retry button.
	 */
	public static Button retry() { return button(RETRY, str("buttonRetry"), false, false, true); }
	/**
	 * Return the default select button.
	 * @return The default select button.
	 */
	public static Button select() { return button(SELECT, str("buttonSelect"), true, false, true); }
	/**
	 * Return the default yes button.
	 * @return The default yes button.
	 */
	public static Button yes() { return button(YES, str("buttonYes"), true, false, true); }

	/**
	 * Return the button.
	 * 
	 * @param id            The button id.
	 * @param text          The text.
	 * @param defaultButton A boolean.
	 * @param cancelButton  A boolean.
	 * @param close         A boolean to set the close property, used in windows to close it after
	 *                      executing the button initial action if any..
	 * @return The button.
	 */
	public static Button button(
			String id,
			String text,
			boolean defaultButton,
			boolean cancelButton,
			boolean close) {
		Button button = new Button(text);
		button.setId(id);
		button.setDefaultButton(defaultButton);
		button.setCancelButton(cancelButton);
		FX.getProperties(button).put("close", close);
		return button;
	}
	/**
	 * Shortcut to access the string resource.
	 * @param key The key.
	 * @return The string resource.
	 */
	private static String str(String key) { return StringRes.get(key); }
}
