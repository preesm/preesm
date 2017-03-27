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

package org.ietr.preesm.mapper.ui.gantt;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.mapper.activator.Activator;
import org.ietr.preesm.mapper.gantt.GanttData;

/**
 * Input of the simple editor of implementation gantt chart
 * 
 * @author mpelcat
 */
public class GanttEditorInput implements IEditorInput {

	private GanttData ganttData = null;
	private String name = null;

	public GanttEditorInput(GanttData ganttData, String name) {
		super();
		this.ganttData = ganttData;
		this.name = name;
	}

	public GanttData getGanttData() {
		return ganttData;
	}

	public void setGanttData(GanttData ganttData) {
		this.ganttData = ganttData;
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		ImageDescriptor img = Activator
				.getImageDescriptor("icons/preesm3mini.PNG");
		return img;
	}

	@Override
	public String getName() {
		return name + " " + WorkflowLogger.getFormattedTime();
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return name;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

}
