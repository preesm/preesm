package org.ietr.preesm.core.scenario.editor;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Interface allowing code generation page and constraint page to share the same class for sdf tree edition.
 * 
 * @author mpelcat
 */
public interface ISDFCheckStateListener extends SelectionListener,
ICheckStateListener, PaintListener {

	public void addComboBoxSelector(Composite parent, FormToolkit toolkit);
	public void setTreeViewer(CheckboxTreeViewer treeViewer, SDFTreeContentProvider contentProvider,IPropertyListener propertyListener);
}
