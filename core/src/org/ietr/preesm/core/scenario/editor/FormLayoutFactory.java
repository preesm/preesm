/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.ietr.preesm.core.scenario.editor;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * FormLayoutFactory
 *
 */
public class FormLayoutFactory {

	// Used in place of 0.  If 0 is used, widget borders will appear clipped
	// on some platforms (e.g. Windows XP Classic Theme).
	// Form tool kit requires parent composites containing the widget to have
	// at least 1 pixel border margins in order to paint the flat borders.
	// The form toolkit paints flat borders on a given widget when native 
	// borders are not painted by SWT.  See FormToolkit#paintBordersFor()
	public static final int DEFAULT_CLEAR_MARGIN = 2;

	// Required to allow space for field decorations
	public static final int CONTROL_HORIZONTAL_INDENT = 3;

	// UI Forms Standards

	// FORM BODY
	public static final int FORM_BODY_MARGIN_TOP = 12;
	public static final int FORM_BODY_MARGIN_BOTTOM = 12;
	public static final int FORM_BODY_MARGIN_LEFT = 6;
	public static final int FORM_BODY_MARGIN_RIGHT = 6;
	public static final int FORM_BODY_HORIZONTAL_SPACING = 20;
	// Should be 20; but, we minus 3 because the section automatically pads the 
	// bottom margin by that amount 	
	public static final int FORM_BODY_VERTICAL_SPACING = 17;
	public static final int FORM_BODY_MARGIN_HEIGHT = 0;
	public static final int FORM_BODY_MARGIN_WIDTH = 0;

	// SECTION CLIENT
	public static final int SECTION_CLIENT_MARGIN_TOP = 5;
	public static final int SECTION_CLIENT_MARGIN_BOTTOM = 5;
	// Should be 6; but, we minus 4 because the section automatically pads the
	// left margin by that amount
	public static final int SECTION_CLIENT_MARGIN_LEFT = 2;
	// Should be 6; but, we minus 4 because the section automatically pads the
	// right margin by that amount	
	public static final int SECTION_CLIENT_MARGIN_RIGHT = 2;
	public static final int SECTION_CLIENT_HORIZONTAL_SPACING = 5;
	public static final int SECTION_CLIENT_VERTICAL_SPACING = 5;
	public static final int SECTION_CLIENT_MARGIN_HEIGHT = 0;
	public static final int SECTION_CLIENT_MARGIN_WIDTH = 0;

	public static final int SECTION_HEADER_VERTICAL_SPACING = 6;

	// CLEAR
	public static final int CLEAR_MARGIN_TOP = DEFAULT_CLEAR_MARGIN;
	public static final int CLEAR_MARGIN_BOTTOM = DEFAULT_CLEAR_MARGIN;
	public static final int CLEAR_MARGIN_LEFT = DEFAULT_CLEAR_MARGIN;
	public static final int CLEAR_MARGIN_RIGHT = DEFAULT_CLEAR_MARGIN;
	public static final int CLEAR_HORIZONTAL_SPACING = 0;
	public static final int CLEAR_VERTICAL_SPACING = 0;
	public static final int CLEAR_MARGIN_HEIGHT = 0;
	public static final int CLEAR_MARGIN_WIDTH = 0;

	// FORM PANE
	public static final int FORM_PANE_MARGIN_TOP = 0;
	public static final int FORM_PANE_MARGIN_BOTTOM = 0;
	public static final int FORM_PANE_MARGIN_LEFT = 0;
	public static final int FORM_PANE_MARGIN_RIGHT = 0;
	public static final int FORM_PANE_HORIZONTAL_SPACING = FORM_BODY_HORIZONTAL_SPACING;
	public static final int FORM_PANE_VERTICAL_SPACING = FORM_BODY_VERTICAL_SPACING;
	public static final int FORM_PANE_MARGIN_HEIGHT = 0;
	public static final int FORM_PANE_MARGIN_WIDTH = 0;

	// MASTER DETAILS
	public static final int MASTER_DETAILS_MARGIN_TOP = 0;
	public static final int MASTER_DETAILS_MARGIN_BOTTOM = 0;
	// Used only by masters part.  Details part margin dynamically calculated
	public static final int MASTER_DETAILS_MARGIN_LEFT = 0;
	// Used only by details part.  Masters part margin dynamically calcualated
	public static final int MASTER_DETAILS_MARGIN_RIGHT = 1;
	public static final int MASTER_DETAILS_HORIZONTAL_SPACING = FORM_BODY_HORIZONTAL_SPACING;
	public static final int MASTER_DETAILS_VERTICAL_SPACING = FORM_BODY_VERTICAL_SPACING;
	public static final int MASTER_DETAILS_MARGIN_HEIGHT = 0;
	public static final int MASTER_DETAILS_MARGIN_WIDTH = 0;

	/**
	 * For composites set as section clients.
	 * For composites containg form text.
	 * @param makeColumnsEqualWidth
	 * @param numColumns
	 * @return
	 */
	public static GridLayout createSectionClientGridLayout(boolean makeColumnsEqualWidth, int numColumns) {
		GridLayout layout = new GridLayout();

		layout.marginHeight = SECTION_CLIENT_MARGIN_HEIGHT;
		layout.marginWidth = SECTION_CLIENT_MARGIN_WIDTH;

		layout.marginTop = SECTION_CLIENT_MARGIN_TOP;
		layout.marginBottom = SECTION_CLIENT_MARGIN_BOTTOM;
		layout.marginLeft = SECTION_CLIENT_MARGIN_LEFT;
		layout.marginRight = SECTION_CLIENT_MARGIN_RIGHT;

		layout.horizontalSpacing = SECTION_CLIENT_HORIZONTAL_SPACING;
		layout.verticalSpacing = SECTION_CLIENT_VERTICAL_SPACING;

		layout.makeColumnsEqualWidth = makeColumnsEqualWidth;
		layout.numColumns = numColumns;

		return layout;
	}



	/**
	 * For miscellaneous grouping composites.
	 * For sections (as a whole - header plus client).
	 * @param makeColumnsEqualWidth
	 * @param numColumns
	 * @return
	 */
	public static GridLayout createClearGridLayout(boolean makeColumnsEqualWidth, int numColumns) {
		GridLayout layout = new GridLayout();

		layout.marginHeight = CLEAR_MARGIN_HEIGHT;
		layout.marginWidth = CLEAR_MARGIN_WIDTH;

		layout.marginTop = CLEAR_MARGIN_TOP;
		layout.marginBottom = CLEAR_MARGIN_BOTTOM;
		layout.marginLeft = CLEAR_MARGIN_LEFT;
		layout.marginRight = CLEAR_MARGIN_RIGHT;

		layout.horizontalSpacing = CLEAR_HORIZONTAL_SPACING;
		layout.verticalSpacing = CLEAR_VERTICAL_SPACING;

		layout.makeColumnsEqualWidth = makeColumnsEqualWidth;
		layout.numColumns = numColumns;

		return layout;
	}
}
