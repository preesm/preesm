/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-François Nezan, Mickaël Raulet

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

package org.ietr.preesm.ui.scenario.editor.constraints;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.ietr.preesm.core.scenario.ConstraintGroup;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.ui.scenario.editor.HierarchicalSDFVertex;
import org.ietr.preesm.ui.scenario.editor.ISDFCheckStateListener;
import org.ietr.preesm.ui.scenario.editor.Messages;
import org.ietr.preesm.ui.scenario.editor.SDFTreeContentProvider;
import org.sdf4j.model.IRefinement;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * Listener of the check state of the SDF tree but also of the selection
 * modification of the current core definition. It updates the check state of
 * the vertices depending on the constraint groups in the scenario
 * 
 * @author mpelcat
 */
public class ConstraintsCheckStateListener implements ISDFCheckStateListener {

	/**
	 * Currently edited scenario
	 */
	private PreesmScenario scenario = null;

	/**
	 * Current operator
	 */
	private String currentOpId = null;

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

	public ConstraintsCheckStateListener(Section section,
			PreesmScenario scenario) {
		super();
		this.scenario = scenario;
		this.section = section;
	}

	/**
	 * Sets the different necessary attributes
	 */
	public void setTreeViewer(CheckboxTreeViewer treeViewer,
			SDFTreeContentProvider contentProvider,
			IPropertyListener propertyListener) {
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
					// updateConstraints(null,
					// contentProvider.getCurrentGraph());
					updateCheck();
				} else if (element instanceof HierarchicalSDFVertex) {
					HierarchicalSDFVertex vertex = (HierarchicalSDFVertex) element;
					fireOnCheck(vertex, isChecked);
					// updateConstraints(null,
					// contentProvider.getCurrentGraph());
					updateCheck();

				}
			}
		});

		propertyListener.propertyChanged(this, IEditorPart.PROP_DIRTY);
	}

	/**
	 * Adds or remove constraints for all vertices in the graph depending on the
	 * isChecked status
	 */
	public void fireOnCheck(SDFGraph graph, boolean isChecked) {
		if (currentOpId != null) {

			// Checks the children of the current graph
			for (HierarchicalSDFVertex v : contentProvider
					.keepAndConvertAppropriateChildren(graph.vertexSet())) {
				fireOnCheck(v, isChecked);
			}
		}
	}

	/**
	 * Adds or remove a constraint depending on the isChecked status
	 */
	public void fireOnCheck(HierarchicalSDFVertex vertex, boolean isChecked) {
		if (currentOpId != null) {
			if (isChecked) {
				scenario.getConstraintGroupManager().addConstraint(
						currentOpId, vertex.getStoredVertex());
			} else {
				scenario.getConstraintGroupManager().removeConstraint(
						currentOpId, vertex.getStoredVertex());
			}
		}

		// Checks the children of the current vertex
		IRefinement refinement = vertex.getStoredVertex().getRefinement();
		if (refinement != null && refinement instanceof SDFGraph) {
			SDFGraph graph = (SDFGraph) refinement;

			for (HierarchicalSDFVertex v : contentProvider
					.keepAndConvertAppropriateChildren(graph.vertexSet())) {
				fireOnCheck(v, isChecked);
			}
		}
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

			currentOpId = item;
			updateCheck();
		}

	}

	/**
	 * Update the check status of the whole tree
	 */
	public void updateCheck() {
		SDFGraph currentGraph = contentProvider.getCurrentGraph();
		if (scenario != null && currentOpId != null && currentGraph != null) {
			Set<HierarchicalSDFVertex> cgSet = new HashSet<HierarchicalSDFVertex>();

			for (ConstraintGroup cg : scenario.getConstraintGroupManager()
					.getOpConstraintGroups(currentOpId)) {

				// Retrieves the elements in the tree that have the same name as
				// the ones to select in the constraint group
				for (String vertexId : cg.getVertexPaths()) {
					SDFAbstractVertex v = currentGraph
							.getHierarchicalVertexFromPath(vertexId);

					if (v != null) {
						cgSet.add(contentProvider.convertChild(v));
					}
				}
			}

			treeViewer.setCheckedElements(cgSet.toArray());

			// If all the children of a graph are checked, it is checked itself
			boolean allChildrenChecked = true;
			for (HierarchicalSDFVertex v : contentProvider
					.keepAndConvertAppropriateChildren(currentGraph.vertexSet())) {
				allChildrenChecked &= treeViewer.getChecked(v);
			}

			if (allChildrenChecked)
				treeViewer.setChecked(currentGraph, true);

		}
	}

	/**
	 * Adds a combo box for the core selection
	 */
	public void addComboBoxSelector(Composite parent, FormToolkit toolkit) {
		Composite combocps = toolkit.createComposite(parent);
		combocps.setLayout(new FillLayout());
		combocps.setVisible(true);
		Combo combo = new Combo(combocps, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setVisibleItemCount(20);
		combo.setToolTipText(Messages
				.getString("Constraints.coreSelectionTooltip"));

		combo.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				comboDataInit((Combo) e.getSource());

			}

			@Override
			public void focusLost(FocusEvent e) {
			}

		});

		combo.addSelectionListener(this);
	}

	private void comboDataInit(Combo combo) {

		combo.removeAll();

		for (String id : scenario.getOperatorIds()) {
			combo.add(id);
		}
	}

	@Override
	public void paintControl(PaintEvent e) {
		updateCheck();

	}
}
