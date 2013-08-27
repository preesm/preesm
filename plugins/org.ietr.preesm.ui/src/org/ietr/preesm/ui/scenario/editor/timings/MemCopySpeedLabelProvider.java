/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

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

package org.ietr.preesm.ui.scenario.editor.timings;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PlatformUI;
import org.ietr.preesm.core.scenario.MemCopySpeed;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.ui.scenario.editor.Messages;

/**
 * Displays the labels for memcopy speed
 * 
 * @author mpelcat
 */
public class MemCopySpeedLabelProvider implements ITableLabelProvider {

	private PreesmScenario scenario = null;

	private TableViewer tableViewer = null;

	/**
	 * Constraints page used as a property listener to change the dirty state
	 */
	private IPropertyListener propertyListener = null;

	public MemCopySpeedLabelProvider(PreesmScenario scenario,
			TableViewer tableViewer, IPropertyListener propertyListener) {
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

		if (element instanceof MemCopySpeed) {
			MemCopySpeed speed = (MemCopySpeed) element;

			if (columnIndex == 0) {
				text = speed.getOperatorDef();
			} else if (columnIndex == 1) {
				text = Integer.toString(speed.getSetupTime());
			} else if (columnIndex == 2) {
				text = Integer.toString(speed.getTimePerUnit());
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

	public void handleDoubleClick(IStructuredSelection selection) {

		IInputValidator validator = new IInputValidator() {

			public String isValid(String newText) {
				String message = null;
				int size = 0;

				try {
					size = Integer.valueOf(newText);
				} catch (NumberFormatException e) {
					size = 0;
				}

				if (size == 0)
					message = "invalid time";

				return message;
			}

		};

		if (selection.getFirstElement() instanceof MemCopySpeed) {
			MemCopySpeed speed = (MemCopySpeed) selection.getFirstElement();

			String title = Messages
					.getString("Timings.MemcopySpeeds.dialog.setupTitle");
			String message = Messages
					.getString("Timings.MemcopySpeeds.dialog.setupMessage")
					+ speed.getOperatorDef();

			String initSetupTime = String.valueOf(speed.getSetupTime());
			String initTimePerUnit = String.valueOf(speed.getTimePerUnit());

			InputDialog dialogSetupTime = new InputDialog(PlatformUI
					.getWorkbench().getActiveWorkbenchWindow().getShell(),
					title, message, initSetupTime, validator);

			title = Messages
					.getString("Timings.MemcopySpeeds.dialog.timePerUnitTitle");
			message = Messages
					.getString("Timings.MemcopySpeeds.dialog.timePerUnitMessage")
					+ speed.getOperatorDef();
					
			InputDialog dialogTimePerUnit = new InputDialog(PlatformUI
					.getWorkbench().getActiveWorkbenchWindow().getShell(),
					title, message, initTimePerUnit, validator);

			if (dialogSetupTime.open() == Window.OK) {
				if (dialogTimePerUnit.open() == Window.OK) {
					String valueSetupTime = dialogSetupTime.getValue();
					String valueTimePerUnit = dialogTimePerUnit.getValue();

					speed.setSetupTime(Integer.valueOf(valueSetupTime));
					speed.setTimePerUnit(Integer.valueOf(valueTimePerUnit));
					scenario.getTimingManager().putMemcpySpeed(speed);

					tableViewer.refresh();
					propertyListener.propertyChanged(this,
							IEditorPart.PROP_DIRTY);
				}
			}
		}

	}

}
