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
 
package org.ietr.preesm.plugin.mapper.plot.stats;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.plugin.abc.AbcType;
import org.ietr.preesm.plugin.abc.AbstractAbc;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.impl.ReceiveVertex;
import org.ietr.preesm.plugin.mapper.model.impl.SendVertex;
import org.ietr.preesm.plugin.mapper.plot.GanttPlotter;
import org.sdf4j.model.PropertyBean;
import org.sdf4j.model.dag.DAGVertex;

/**
 * This page contains the gantt display
 * 
 * @author mpelcat
 */
public class GanttPage extends FormPage {

	private StatGenerator statGen = null;
	

	public GanttPage(StatGenerator statGen, FormEditor editor, String id, String title) {
		super(editor, id, title);
		
		this.statGen = statGen;
	}

	/**
	 * Creation of the sections and their initialization
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		
		ScrolledForm form = managedForm.getForm();
		ColumnLayout layout = new ColumnLayout();
		layout.topMargin = 0;
		layout.bottomMargin = 5;
		layout.leftMargin = 10;
		layout.rightMargin = 10;
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 10;
		layout.maxNumColumns = 4;
		layout.minNumColumns = 1;
		form.getBody().setLayout(layout);
		
		MapperDAG dag = statGen.getDag().clone();
		MultiCoreArchitecture archi = statGen.getArchi();
		
		
		if(dag != null && archi != null){

			// Gets the appropriate abc to generate the gantt.
			PropertyBean bean = dag.getPropertyBean();
			AbcType abctype = (AbcType)bean.getValue(AbstractAbc.propertyBeanName);
			
			IAbc simu = AbstractAbc
			.getInstance(abctype, dag, archi);

			StatGenerator.removeSendReceive(dag);
			
			simu.setDAG(dag);

			simu.getFinalTime();
			GanttPlotter.plotInComposite(simu, form.getBody());
		}
		
	}


	public StatGenerator getStatGen() {
		return statGen;
	}
	
}
