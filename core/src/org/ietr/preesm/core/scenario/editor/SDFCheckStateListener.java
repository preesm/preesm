/**
 * 
 */
package org.ietr.preesm.core.scenario.editor;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.internal.DirtyPerspectiveMarker;
import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.architecture.OperatorDefinition;
import org.ietr.preesm.core.scenario.ConstraintGroup;
import org.ietr.preesm.core.scenario.Scenario;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFGraph;
import org.sdf4j.model.sdf.SDFVertex;

/**
 * Listener of the check state of the SDF tree but also of the selection
 * modification of the current core definition
 * 
 * @author mpelcat
 */
public class SDFCheckStateListener implements SelectionListener,
		ICheckStateListener {

	private Scenario scenario = null;
	private Section section = null;
	private OperatorDefinition currentOpDef = null;
	private CheckboxTreeViewer treeViewer = null;
	private SDFTreeContentProvider contentProvider = null;
	private IPropertyListener propertyListener = null;

	public SDFCheckStateListener(Section section, Scenario scenario) {
		super();
		this.scenario = scenario;
		this.section = section;
	}

	public void setTreeViewer(CheckboxTreeViewer treeViewer, SDFTreeContentProvider contentProvider,IPropertyListener propertyListener) {
		this.treeViewer = treeViewer;
		this.contentProvider = contentProvider;
		this.propertyListener = propertyListener;
	}

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

	public void fireOnCheck(SDFGraph graph, boolean isChecked) {
		if (currentOpDef != null) {
			if (isChecked)
				scenario.getConstraintGroupManager().addConstraints(
						currentOpDef, graph.vertexSet());
			else
				scenario.getConstraintGroupManager().removeConstraints(
						currentOpDef, graph.vertexSet());
		}
		updateCheck();
	}

	public void fireOnCheck(SDFAbstractVertex vertex, boolean isChecked) {
		if (currentOpDef != null) {
			if (isChecked)
				scenario.getConstraintGroupManager().addConstraint(
						currentOpDef, vertex);
			else
				scenario.getConstraintGroupManager().removeConstraint(
						currentOpDef, vertex);
		}
		updateCheck();
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
		if (e.getSource() instanceof Combo) {
			Combo combo = ((Combo) e.getSource());
			String item = combo.getItem(combo.getSelectionIndex());

			IArchitecture archi = (IArchitecture) combo.getData();
			currentOpDef = archi.getOperatorDefinition(item);
			updateCheck();
		}

	}

	public void updateCheck() {
		SDFGraph currentGraph = contentProvider.getCurrentGraph();
		if (scenario != null && currentOpDef != null && currentGraph != null) {
			Set<SDFAbstractVertex> cgSet = new HashSet<SDFAbstractVertex>();

			for (ConstraintGroup cg : scenario.getConstraintGroupManager()
					.getOpdefConstraintGroups(currentOpDef)) {
				
				for(SDFAbstractVertex vertex:cg.getVertices()){
					cgSet.add(currentGraph.getVertex(vertex.getName()));
				}
			}

			treeViewer.setCheckedElements(cgSet.toArray());
			
			if(cgSet.size() == currentGraph.vertexSet().size())
				treeViewer.setChecked(currentGraph,true);
			
			propertyListener.propertyChanged(this, IEditorPart.PROP_DIRTY);
		}
	}
}
