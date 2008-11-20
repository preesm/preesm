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
 
package org.ietr.preesm.core.scenario.editor.codegen;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.ietr.preesm.core.scenario.CodegenVertexPhases;
import org.ietr.preesm.core.scenario.Scenario;
import org.ietr.preesm.core.scenario.editor.ISDFCheckStateListener;
import org.ietr.preesm.core.scenario.editor.Messages;
import org.ietr.preesm.core.scenario.editor.SDFTreeContentProvider;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * Listener of the check state of the SDF tree but also of the selection
 * modification of the current code generation phase. It updates the check
 * state of the vertices depending on the check states in the scenario
 * 
 * @author mpelcat
 */
public class CodegenPhasesCheckStateListener implements ISDFCheckStateListener {

	/**
	 * Currently edited scenario
	 */
	private Scenario scenario = null;
	
	/**
	 * Current operator
	 */
	private String currentPhase = "";

	/**
	 * Current section (necessary to diplay busy status)
	 */
	private Section section = null;

	/**
	 * Tree viewer used to set the checked status
	 */
	private CheckboxTreeViewer treeViewer = null;

	/**
	 * Content provider used to get the elements currently displayed
	 */
	private SDFTreeContentProvider contentProvider = null;

	/**
	 * Constraints page used as a property listener to change the dirty state
	 */
	private IPropertyListener propertyListener = null;

	public CodegenPhasesCheckStateListener(Section section, Scenario scenario) {
		super();
		this.scenario = scenario;
		this.section = section;
	}

	/**
	 * Sets the different necessary attributes
	 */
	public void setTreeViewer(CheckboxTreeViewer treeViewer, SDFTreeContentProvider contentProvider,IPropertyListener propertyListener) {
		this.treeViewer = treeViewer;
		this.contentProvider = contentProvider;
		this.propertyListener = propertyListener;
	}

	/**
	 * Fired when an element has been checked or unchecked
	 */
	@Override
	public void checkStateChanged(CheckStateChangedEvent event) {
		final Object element = event.getElement();
		final boolean isChecked = event.getChecked();
		BusyIndicator.showWhile(section.getDisplay(), new Runnable() {

			public void run() {
				if (element instanceof SDFGraph) {
					SDFGraph graph = (SDFGraph) element;
					fireOnCheck(graph, isChecked);
				} else if (element instanceof SDFAbstractVertex) {
					SDFAbstractVertex vertex = (SDFAbstractVertex) element;
					fireOnCheck(vertex, isChecked);

				}
			}
		});
	}

	/**
	 * Adds or remove constraints for all vertices in the graph depending 
	 * on the isChecked status
	 */
	public void fireOnCheck(SDFGraph graph, boolean isChecked) {
//		if (currentOpDef != null) {
//			if (isChecked)
//				scenario.getConstraintGroupManager().addConstraints(
//						currentOpDef, graph.vertexSet());
//			else
//				scenario.getConstraintGroupManager().removeConstraints(
//						currentOpDef, graph.vertexSet());
//		}
//		updateCheck();
	}

	/**
	 * Adds or remove a constraint depending on the isChecked status
	 */
	public void fireOnCheck(SDFAbstractVertex vertex, boolean isChecked) {
//		if (currentOpDef != null) {
//			if (isChecked)
//				scenario.getConstraintGroupManager().addConstraint(
//						currentOpDef, vertex);
//			else
//				scenario.getConstraintGroupManager().removeConstraint(
//						currentOpDef, vertex);
//		}
//		updateCheck();
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub

	}

	/**
	 * Core combo box listener that selects the current core
	 */
	@Override
	public void widgetSelected(SelectionEvent e) {
//		if (e.getSource() instanceof Combo) {
//			Combo combo = ((Combo) e.getSource());
//			String item = combo.getItem(combo.getSelectionIndex());
//
//			MultiCoreArchitecture archi = (MultiCoreArchitecture) combo.getData();
//			currentOpDef = (Operator)archi.getComponent(ArchitectureComponentType.operator,item);
//			updateCheck();
//		}

	}

	/**
	 * Update the check status of the whole tree
	 */
	public void updateCheck() {
//		SDFGraph currentGraph = contentProvider.getCurrentGraph();
//		if (scenario != null && !currentPhase.isEmpty() && currentGraph != null) {
//			Set<SDFAbstractVertex> cgSet = new HashSet<SDFAbstractVertex>();
//			Map<SDFAbstractVertex,CodegenVertexPhases> phaseMap = scenario.getCodegenManager().getCodegenPhases();
//
//			for(SDFAbstractVertex v : scenario.getCodegenManager().getCodegenPhases()){
//				
//			}
//			
//			for (ConstraintGroup cg : scenario.getConstraintGroupManager()
//					.getOpConstraintGroups(currentOpDef)) {
//				
//				// Retrieves the elements in the tree that have the same name as
//				// the ones to select in the constraint group
//				for(SDFAbstractVertex vertex:cg.getVertices()){
//					cgSet.add(currentGraph.getVertex(vertex.getName()));
//				}
//			}
//
//			treeViewer.setCheckedElements(cgSet.toArray());
//			
//			if(cgSet.size() == currentGraph.vertexSet().size())
//				treeViewer.setChecked(currentGraph,true);
//			
//			propertyListener.propertyChanged(this, IEditorPart.PROP_DIRTY);
//		}
	}

	/**
	 * Adds a combo box for the codegen phases selection
	 */
	public void addComboBoxSelector(Composite parent, FormToolkit toolkit) {
		Composite combocps = toolkit.createComposite(parent);
		combocps.setLayout(new FillLayout());
		combocps.setVisible(true);
		Combo combo = new Combo(combocps, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setToolTipText(Messages
				.getString("Codegen.Phase.phaseSelectionTooltip"));

		combo.add(CodegenVertexPhases.initPhaseId);
		combo.add(CodegenVertexPhases.loopPhaseId);
		combo.add(CodegenVertexPhases.closePhaseId);

		combo.setData(scenario.getCodegenManager());
		combo.addSelectionListener(this);
	}
}
