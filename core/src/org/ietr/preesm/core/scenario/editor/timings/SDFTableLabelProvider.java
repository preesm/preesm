/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

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
import org.ietr.preesm.core.architecture.IOperator;
import org.ietr.preesm.core.architecture.IOperatorDefinition;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.advancedmodel.IpCoprocessorDefinition;
import org.ietr.preesm.core.architecture.advancedmodel.ProcessorDefinition;
import org.ietr.preesm.core.architecture.simplemodel.OperatorDefinition;
import org.ietr.preesm.core.scenario.Scenario;
import org.ietr.preesm.core.scenario.editor.Messages;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFVertex;

/**
 * Displays the labels for tasks timings. These labels are the time of each task
 * 
 * @author mpelcat
 */
public class SDFTableLabelProvider implements ITableLabelProvider,
		SelectionListener {

	private Scenario scenario = null;

	private IOperatorDefinition currentOpDef = null;

	private TableViewer tableViewer = null;

	/**
	 * Constraints page used as a property listener to change the dirty state
	 */
	private IPropertyListener propertyListener = null;

	public SDFTableLabelProvider(Scenario scenario, TableViewer tableViewer,
			IPropertyListener propertyListener) {
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

			MultiCoreArchitecture archi = (MultiCoreArchitecture) combo
					.getData();
			currentOpDef = (OperatorDefinition) archi.getComponentDefinition(
					ArchitectureComponentType.operator, item);
			if (currentOpDef == null) {
				currentOpDef = (ProcessorDefinition) archi
						.getComponentDefinition(
								ArchitectureComponentType.processor, item);
			}
			if (currentOpDef == null) {
				currentOpDef = (IpCoprocessorDefinition) archi
						.getComponentDefinition(
								ArchitectureComponentType.ipCoprocessor, item);
			}
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
