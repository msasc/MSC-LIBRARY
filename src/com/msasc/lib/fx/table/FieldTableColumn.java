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
package com.msasc.lib.fx.table;

import com.msasc.lib.db.Field;
import com.msasc.lib.db.Record;
import com.msasc.lib.db.Value;
import com.msasc.lib.db.util.converters.BooleanStringConverter;
import com.msasc.lib.db.util.converters.NumberStringConverter;
import com.msasc.lib.db.util.converters.PossibleValueStringConverter;
import com.msasc.lib.db.util.converters.ValueStringConverter;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.text.Font;

/**
 * A field table column.
 * @author Miquel Sas
 */
public class FieldTableColumn extends TableColumn<Record, Value> {

	/**
	 * Field table cell.
	 */
	private class FieldCell extends TableCell<Record, Value> {
		protected void updateItem(Value value, boolean empty) {
			super.updateItem(value, empty);
			if (value != null) {
				if (stringConverter == null) setText(value.toString());
				else setText(stringConverter.toString(value));
				if (font != null) setFont(font);
			}
		}
	}

	/** Underlying field. */
	private Field field;
	/** Observable value to be returned by the value callback. */
	private SimpleObjectProperty<Value> valueProperty;
	/** String converter used to convert the value to string. */
	private ValueStringConverter stringConverter;
	/** Optional font. */
	private Font font;

	/**
	 * Constructor.
	 * @param field The field.
	 */
	public FieldTableColumn(Field field) {
		this.field = field;
		setupColumn();
	}
	
	public Field getField() { return field; }

	private void setupColumn() {
		
		valueProperty = new SimpleObjectProperty<Value>();

		/* Forced string converter. */
		stringConverter = field.getProperties().getStringConverter();
		if (stringConverter == null) {
			/* Default string converter by type. */
			if (field.isNumber()) {
				stringConverter = new NumberStringConverter(field);
			} else if (field.isBoolean()) {
				stringConverter = new BooleanStringConverter();
			} else if (field.getProperties().hasPossibleValues()) {
				stringConverter = new PossibleValueStringConverter(field);
			}
		}

		/* Header. */
		setText(this.field.getProperties().getHeader());

		/* Value factory. */
		setCellValueFactory(p -> {
			Record record = p.getValue();
			Value value = record.getValue(this.field.getAlias());
			valueProperty.set(value);
			return valueProperty;
		});

		/* Cell factory. */
		setCellFactory(p -> {
			FieldCell cell = new FieldCell();

			/* Default alignment by type. */
			if (field.isNumber()) cell.setAlignment(Pos.BASELINE_RIGHT);
			else if (field.isBoolean()) cell.setAlignment(Pos.BASELINE_CENTER);
			else if (field.isDate()) cell.setAlignment(Pos.BASELINE_CENTER);
			else if (field.isTime()) cell.setAlignment(Pos.BASELINE_CENTER);
			else if (field.isTimestamp()) cell.setAlignment(Pos.BASELINE_CENTER);

			/* CSS style. */
			String style = field.getProperties().getStyle();
			if (style != null) cell.setStyle(style);

			return cell;
		});
	}
}
