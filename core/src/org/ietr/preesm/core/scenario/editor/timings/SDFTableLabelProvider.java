/**
 * 
 */
package org.ietr.preesm.core.scenario.editor.timings;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PlatformUI;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.architecture.OperatorDefinition;
import org.ietr.preesm.core.scenario.Scenario;
import org.ietr.preesm.core.scenario.editor.Messages;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFVertex;

/**
 * 
 * 
 * @author mpelcat
 */
public class SDFTableLabelProvider implements ITableLabelProvider,
		SelectionListener {

	private Scenario scenario = null;

	private OperatorDefinition currentOpDef = null;

	private TableViewer tableViewer = null;

	/**
	 * Constraints page used as a property listener to change the dirty state
	 */
	private IPropertyListener propertyListener = null;

	public SDFTableLabelProvider(Scenario scenario, TableViewer tableViewer, IPropertyListener propertyListener) {
		super();
		this.scenario = scenario;
		this.tableViewer = tableViewer;
		this.propertyListener = propertyListener;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		String text = "";

		if (element instanceof SDFAbstractVertex) {
			SDFAbstractVertex vertex = (SDFAbstractVertex) element;

			if (columnIndex == 0)
				text = vertex.getName();
			else if (columnIndex == 1 && scenario != null
					&& currentOpDef != null) {
				int time = scenario.getTimingManager().getTimingOrDefault(
						vertex, currentOpDef);

				text = Integer.toString(time);
			}
		}

		return text;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {

	}

	/**
	 * Core combo box listener that selects the current core
	 */
	@Override
	public void widgetSelected(SelectionEvent e) {
		if (e.getSource() instanceof Combo) {
			Combo combo = ((Combo) e.getSource());
			String item = combo.getItem(combo.getSelectionIndex());

			IArchitecture archi = (IArchitecture) combo.getData();
			currentOpDef = archi.getComponentDefinition(ArchitectureComponentType.operator,item);

			tableViewer.refresh();
		}

	}

	public void handleDoubleClick(IStructuredSelection selection) {

		IInputValidator validator = new IInputValidator() {

			public String isValid(String newText) {
				String message = null;
				int time = 0;

				try {
					time = Integer.valueOf(newText);
				} catch (NumberFormatException e) {
					time = 0;
				}

				if (time == 0)
					message = "invalid timing";

				return message;
			}

		};

		if (selection.getFirstElement() instanceof SDFVertex
				&& currentOpDef != null) {
			SDFVertex vertex = (SDFVertex) selection.getFirstElement();

			String title = Messages.getString("Timings.dialog.title");
			String message = Messages.getString("Timings.dialog.message")
					+ vertex.getName();
			String init = String.valueOf(scenario.getTimingManager()
					.getTimingOrDefault(vertex, currentOpDef));

			InputDialog dialog = new InputDialog(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), title, message,
					init, validator);
			if (dialog.open() == Window.OK) {
				String value = dialog.getValue();

				scenario.getTimingManager().setTiming(vertex, currentOpDef,
						Integer.valueOf(value));

				tableViewer.refresh();
				propertyListener.propertyChanged(this, IEditorPart.PROP_DIRTY);
			}
		}

	}

}
