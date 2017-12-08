/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011 - 2012)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.ietr.preesm.ui.scenario.editor.constraints;

import java.util.LinkedHashSet;
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
import org.ietr.dftools.algorithm.model.IRefinement;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.core.scenario.ConstraintGroup;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.ui.scenario.editor.HierarchicalSDFVertex;
import org.ietr.preesm.ui.scenario.editor.ISDFCheckStateListener;
import org.ietr.preesm.ui.scenario.editor.Messages;
import org.ietr.preesm.ui.scenario.editor.PreesmAlgorithmTreeContentProvider;

// TODO: Auto-generated Javadoc
/**
 * Listener of the check state of the SDF tree but also of the selection modification of the current core definition. It updates the check state of the vertices
 * depending on the constraint groups in the scenario
 *
 * @author mpelcat
 */
public class ConstraintsCheckStateListener implements ISDFCheckStateListener {

  /** Currently edited scenario. */
  private PreesmScenario scenario = null;

  /** Current operator. */
  private String currentOpId = null;

  /** Current section (necessary to diplay busy status). */
  private Section section = null;

  /** Tree viewer used to set the checked status. */
  private CheckboxTreeViewer treeViewer = null;

  /** Content provider used to get the elements currently displayed. */
  private PreesmAlgorithmTreeContentProvider contentProvider = null;

  /** Constraints page used as a property listener to change the dirty state. */
  private IPropertyListener propertyListener = null;

  /**
   * Instantiates a new constraints check state listener.
   *
   * @param section
   *          the section
   * @param scenario
   *          the scenario
   */
  public ConstraintsCheckStateListener(final Section section, final PreesmScenario scenario) {
    super();
    this.scenario = scenario;
    this.section = section;
  }

  /**
   * Sets the different necessary attributes.
   *
   * @param treeViewer
   *          the tree viewer
   * @param contentProvider
   *          the content provider
   * @param propertyListener
   *          the property listener
   */
  @Override
  public void setTreeViewer(final CheckboxTreeViewer treeViewer, final PreesmAlgorithmTreeContentProvider contentProvider,
      final IPropertyListener propertyListener) {
    this.treeViewer = treeViewer;
    this.contentProvider = contentProvider;
    this.propertyListener = propertyListener;
  }

  /**
   * Fired when an element has been checked or unchecked.
   *
   * @param event
   *          the event
   */
  @Override
  public void checkStateChanged(final CheckStateChangedEvent event) {
    final Object element = event.getElement();
    final boolean isChecked = event.getChecked();

    BusyIndicator.showWhile(this.section.getDisplay(), () -> {
      if (ConstraintsCheckStateListener.this.scenario.isIBSDFScenario()) {
        if (element instanceof SDFGraph) {
          final SDFGraph graph1 = (SDFGraph) element;
          fireOnCheck(graph1, isChecked);
          // updateConstraints(null,
          // contentProvider.getCurrentGraph());
          updateCheck();
        } else if (element instanceof HierarchicalSDFVertex) {
          final HierarchicalSDFVertex vertex = (HierarchicalSDFVertex) element;
          fireOnCheck(vertex, isChecked);
          // updateConstraints(null,
          // contentProvider.getCurrentGraph());
          updateCheck();

        }
      } else if (ConstraintsCheckStateListener.this.scenario.isPISDFScenario()) {
        if (element instanceof PiGraph) {
          final PiGraph graph2 = (PiGraph) element;
          fireOnCheck(graph2, isChecked);
          updateCheck();
        } else if (element instanceof AbstractActor) {
          final AbstractActor actor = (AbstractActor) element;
          fireOnCheck(actor, isChecked);
          updateCheck();
        }
      }
    });
    this.propertyListener.propertyChanged(this, IEditorPart.PROP_DIRTY);
  }

  /**
   * Adds or remove constraints for all vertices in the graph depending on the isChecked status.
   *
   * @param graph
   *          the graph
   * @param isChecked
   *          the is checked
   */
  private void fireOnCheck(final SDFGraph graph, final boolean isChecked) {
    if (this.currentOpId != null) {
      // Checks the children of the current graph
      for (final HierarchicalSDFVertex v : this.contentProvider.filterIBSDFChildren(graph.vertexSet())) {
        fireOnCheck(v, isChecked);
      }
    }
  }

  /**
   * Fire on check.
   *
   * @param graph
   *          the graph
   * @param isChecked
   *          the is checked
   */
  private void fireOnCheck(final PiGraph graph, final boolean isChecked) {
    if (this.currentOpId != null) {
      // Checks the children of the current graph
      for (final AbstractActor v : this.contentProvider.filterPISDFChildren(graph.getActors())) {
        if (v instanceof PiGraph) {
          fireOnCheck(((PiGraph) v), isChecked);
        }
        fireOnCheck(v, isChecked);
      }
      fireOnCheck((AbstractActor) graph, isChecked);
    }
  }

  /**
   * Adds or remove a constraint depending on the isChecked status.
   *
   * @param vertex
   *          the vertex
   * @param isChecked
   *          the is checked
   */
  private void fireOnCheck(final HierarchicalSDFVertex vertex, final boolean isChecked) {
    if (this.currentOpId != null) {
      if (isChecked) {
        this.scenario.getConstraintGroupManager().addConstraint(this.currentOpId, vertex.getStoredVertex());
      } else {
        this.scenario.getConstraintGroupManager().removeConstraint(this.currentOpId, vertex.getStoredVertex());
      }
    }

    // Checks the children of the current vertex
    final IRefinement refinement = vertex.getStoredVertex().getRefinement();
    if ((refinement != null) && (refinement instanceof SDFGraph)) {
      final SDFGraph graph = (SDFGraph) refinement;

      for (final HierarchicalSDFVertex v : this.contentProvider.filterIBSDFChildren(graph.vertexSet())) {
        fireOnCheck(v, isChecked);
      }
    }
  }

  /**
   * Fire on check.
   *
   * @param abstractActor
   *          the actor
   * @param isChecked
   *          the is checked
   */
  private void fireOnCheck(final AbstractActor abstractActor, final boolean isChecked) {
    if (this.currentOpId != null) {
      if (isChecked) {
        this.scenario.getConstraintGroupManager().addConstraint(this.currentOpId, abstractActor);
      } else {
        this.scenario.getConstraintGroupManager().removeConstraint(this.currentOpId, abstractActor);
      }
    }

    // Checks the children of the current vertex
    if (abstractActor instanceof Actor) {
      final Actor actor = (Actor) abstractActor;
      if (actor.isHierarchical()) {
        final PiGraph subGraph = actor.getSubGraph();
        for (final AbstractActor v : this.contentProvider.filterPISDFChildren(subGraph.getActors())) {
          fireOnCheck(v, isChecked);
        }
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
   */
  @Override
  public void widgetDefaultSelected(final SelectionEvent e) {
    // TODO Auto-generated method stub

  }

  /**
   * Core combo box listener that selects the current core.
   *
   * @param e
   *          the e
   */
  @Override
  public void widgetSelected(final SelectionEvent e) {
    if (e.getSource() instanceof Combo) {
      final Combo combo = ((Combo) e.getSource());
      final String item = combo.getItem(combo.getSelectionIndex());

      this.currentOpId = item;
      updateCheck();
    }

  }

  /**
   * Update the check status of the whole tree.
   */
  public void updateCheck() {
    if (this.scenario != null) {
      if (this.scenario.isIBSDFScenario()) {
        updateCheckIBSDF();
      } else if (this.scenario.isPISDFScenario()) {
        updateCheckPISDF();
      }
    }
  }

  /**
   * Update check PISDF.
   */
  private void updateCheckPISDF() {
    final PiGraph currentGraph = this.contentProvider.getPISDFCurrentGraph();
    if ((this.currentOpId != null) && (currentGraph != null)) {
      final Set<AbstractVertex> cgSet = new LinkedHashSet<>();

      for (final ConstraintGroup cg : this.scenario.getConstraintGroupManager().getOpConstraintGroups(this.currentOpId)) {

        // Retrieves the elements in the tree that have the same name as
        // the ones to select in the constraint group
        for (final String vertexId : cg.getVertexPaths()) {
          final AbstractVertex v = currentGraph.lookupActorFromPath(vertexId);
          if (v != null) {
            cgSet.add(v);
          }
        }
      }

      this.treeViewer.setCheckedElements(cgSet.toArray());

      // If all the children of a graph are checked, it is checked itself
      boolean allChildrenChecked = true;
      for (final AbstractActor v : this.contentProvider.filterPISDFChildren(currentGraph.getActors())) {
        allChildrenChecked &= this.treeViewer.getChecked(v);
      }

      if (allChildrenChecked) {
        this.treeViewer.setChecked(currentGraph, true);
      }

    }
  }

  /**
   * Update check IBSDF.
   */
  private void updateCheckIBSDF() {
    final SDFGraph currentGraph = this.contentProvider.getIBSDFCurrentGraph();
    if ((this.currentOpId != null) && (currentGraph != null)) {
      final Set<HierarchicalSDFVertex> cgSet = new LinkedHashSet<>();

      for (final ConstraintGroup cg : this.scenario.getConstraintGroupManager().getOpConstraintGroups(this.currentOpId)) {

        // Retrieves the elements in the tree that have the same name as
        // the ones to select in the constraint group
        for (final String vertexId : cg.getVertexPaths()) {
          final SDFAbstractVertex v = currentGraph.getHierarchicalVertexFromPath(vertexId);

          if (v != null) {
            cgSet.add(this.contentProvider.convertSDFChild(v));
          }
        }
      }

      this.treeViewer.setCheckedElements(cgSet.toArray());

      // If all the children of a graph are checked, it is checked itself
      boolean allChildrenChecked = true;
      for (final HierarchicalSDFVertex v : this.contentProvider.filterIBSDFChildren(currentGraph.vertexSet())) {
        allChildrenChecked &= this.treeViewer.getChecked(v);
      }

      if (allChildrenChecked) {
        this.treeViewer.setChecked(currentGraph, true);
      }

    }
  }

  /**
   * Adds a combo box for the core selection.
   *
   * @param parent
   *          the parent
   * @param toolkit
   *          the toolkit
   */
  @Override
  public void addComboBoxSelector(final Composite parent, final FormToolkit toolkit) {
    final Composite combocps = toolkit.createComposite(parent);
    combocps.setLayout(new FillLayout());
    combocps.setVisible(true);
    final Combo combo = new Combo(combocps, SWT.DROP_DOWN | SWT.READ_ONLY);
    combo.setVisibleItemCount(20);
    combo.setToolTipText(Messages.getString("Constraints.coreSelectionTooltip"));
    comboDataInit(combo);
    combo.addFocusListener(new FocusListener() {

      @Override
      public void focusGained(final FocusEvent e) {
        comboDataInit((Combo) e.getSource());

      }

      @Override
      public void focusLost(final FocusEvent e) {
      }

    });

    combo.addSelectionListener(this);
  }

  /**
   * Combo data init.
   *
   * @param combo
   *          the combo
   */
  private void comboDataInit(final Combo combo) {

    combo.removeAll();
    for (final String id : this.scenario.getOrderedOperatorIds()) {
      combo.add(id);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
   */
  @Override
  public void paintControl(final PaintEvent e) {
    updateCheck();

  }
}
