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

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
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
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.ui.scenario.editor.Messages;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * Displays the labels for tasks timings. These labels are the time of each task
 * 
 * @author mpelcat
 */
public class TimingsTableLabelProvider implements ITableLabelProvider,
		SelectionListener {

	private PreesmScenario scenario = null;

	private String currentOpDefId = null;

	private TableViewer tableViewer = null;

	private Image imageOk, imageError;

	/**
	 * Constraints page used as a property listener to change the dirty state
	 */
	private IPropertyListener propertyListener = null;

	public TimingsTableLabelProvider(PreesmScenario scenario,
			TableViewer tableViewer, IPropertyListener propertyListener) {
		super();
		this.scenario = scenario;
		this.tableViewer = tableViewer;
		this.propertyListener = propertyListener;

		Bundle bundle = FrameworkUtil.getBundle(TimingsTableLabelProvider.class);

		URL url = FileLocator.find(bundle, new Path("icons/error.png"), null);
		ImageDescriptor imageDcr = ImageDescriptor.createFromURL(url);
		this.imageError = imageDcr.createImage();

		url = FileLocator.find(bundle, new Path("icons/ok.png"), null);
		imageDcr = ImageDescriptor.createFromURL(url);
		this.imageOk = imageDcr.createImage();
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (scenario.isPISDFScenario()) return getPISDFColumnImage(element, columnIndex);
		else return null;
	}

	private Image getPISDFColumnImage(Object element, int columnIndex) {
		if (element instanceof AbstractActor && currentOpDefId != null) {
			AbstractActor vertex = (AbstractActor) element;

			Timing timing = scenario.getTimingManager().getTimingOrDefault(
					vertex.getName(), currentOpDefId);
			switch (columnIndex) {
			case 1:// Parsing column
				if (timing.canParse())
					return imageOk;
				else
					return imageError;
			case 2:// Evaluation column
				if (timing.canEvaluate())
					return imageOk;
				else
					return imageError;
			default:// Others
				break;
			}
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (scenario.isPISDFScenario()) return getPISDFColumnText(element, columnIndex);
		else if (scenario.isIBSDFScenario()) return getIBSDFColumnText(element, columnIndex);
		else return null;
	}

	private String getIBSDFColumnText(Object element, int columnIndex) {
		String text = "";
		if (element instanceof SDFAbstractVertex && currentOpDefId != null) {			
			SDFAbstractVertex vertex = (SDFAbstractVertex) element;
			
			Timing timing = scenario.getTimingManager().getTimingOrDefault(
					vertex.getName(), currentOpDefId);
			switch (columnIndex) {
			case 0:
				return vertex.getName();	
			case 1: // Expression Column
				if (timing != null)
					text = timing.getStringValue();
				break;
			default:// Others
				break;
			}
		}
		return text;
	}

	private String getPISDFColumnText(Object element, int columnIndex) {
		String text = "";
		if (element instanceof AbstractActor && currentOpDefId != null) {
			AbstractActor vertex = (AbstractActor) element;

			Timing timing = scenario.getTimingManager().getTimingOrDefault(
					vertex.getName(), currentOpDefId);

			switch (columnIndex) {
			case 0:
				return vertex.getName();
			case 1: // Parsing Column
			case 2: // Evaluation Column
				return null;
			case 3: // Variables Column
				if (timing != null) {
					if (timing.getInputParameters().isEmpty())
						text = "-";
					else
						text = timing.getInputParameters().toString();
				}
				break;
			case 4: // Expression Column
				if (timing != null)
					text = timing.getStringValue();
				break;
			default:// Others
				break;
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

			currentOpDefId = item;
			tableViewer.refresh();
		}

	}

	public void handleDoubleClick(IStructuredSelection selection) {
		IInputValidator validator = new IInputValidator() {
			@Override
			public String isValid(String newText) {
				String message = null;
				// int time = 0;
				//
				// try {
				// time = Integer.valueOf(newText);
				// } catch (NumberFormatException e) {
				// time = 0;
				// }
				//
				// if (time == 0)
				// message = Messages.getString("Timings.invalid");

				return message;
			}
		};

		String vertexName = null;
		if (selection.getFirstElement() instanceof SDFVertex) {
			vertexName = ((SDFVertex) selection.getFirstElement()).getName();
		} else if (selection.getFirstElement() instanceof AbstractActor) {
			vertexName = ((AbstractActor) selection.getFirstElement()).getName();
		}
		
		if (vertexName != null && currentOpDefId != null) {
			String title = Messages.getString("Timings.dialog.title");
			String message = Messages.getString("Timings.dialog.message")
					+ vertexName;
			String init = scenario.getTimingManager()
					.getTimingOrDefault(vertexName, currentOpDefId)
					.getStringValue();

			InputDialog dialog = new InputDialog(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), title, message,
					init, validator);
			if (dialog.open() == Window.OK) {
				String value = dialog.getValue();

				scenario.getTimingManager().setTiming(vertexName,
						currentOpDefId, value);

				tableViewer.refresh();
				propertyListener.propertyChanged(this, IEditorPart.PROP_DIRTY);
			}
		}
	}

}
