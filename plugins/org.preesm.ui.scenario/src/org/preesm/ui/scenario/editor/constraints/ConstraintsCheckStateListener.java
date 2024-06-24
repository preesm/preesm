/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2011 - 2012)
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
package org.preesm.ui.scenario.editor.constraints;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.ui.scenario.editor.ISDFCheckStateListener;
import org.preesm.ui.scenario.editor.Messages;
import org.preesm.ui.scenario.editor.PreesmAlgorithmTreeContentProvider;

/**
 * Listener of the check state of the SDF tree but also of the selection modification of the current core definition. It
 * updates the check state of the vertices depending on the constraint groups in the scenario
 *
 * @author mpelcat
 */
public class ConstraintsCheckStateListener implements ISDFCheckStateListener {

  /** Currently edited scenario. */
  private Scenario scenario = null;

  /** Current operator. */
  private ComponentInstance currentOpId = null;

  /** Current section (necessary to diplay busy status). */
  private Section section = null;

  /** Tree viewer used to set the checked status. */
  private CheckboxTreeViewer treeViewer = null;

  /** Content provider used to get the elements currently displayed. */
  private PreesmAlgorithmTreeContentProvider contentProvider = null;

  /** Constraints page used as a property listener to change the dirty state. */
  IPropertyListener propertyListener = null;

  /**
   * Instantiates a new constraints check state listener.
   *
   * @param section
   *          the section
   * @param scenario
   *          the scenario
   */
  public ConstraintsCheckStateListener(final Section section, final Scenario scenario) {
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
  public void setTreeViewer(final CheckboxTreeViewer treeViewer,
      final PreesmAlgorithmTreeContentProvider contentProvider, final IPropertyListener propertyListener) {
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
      if (element instanceof final PiGraph piGraph) {
        fireOnCheck(piGraph, isChecked);
        updateCheck();
      } else if (element instanceof final AbstractActor actor) {
        fireOnCheck(actor, isChecked);
        updateCheck();
      }
    });
    this.propertyListener.propertyChanged(this, IEditorPart.PROP_DIRTY);
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
        if (v instanceof final PiGraph piGraph) {
          fireOnCheck(piGraph, isChecked);
        }
        fireOnCheck(v, isChecked);
      }
      fireOnCheck((AbstractActor) graph, isChecked);
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
        this.scenario.getConstraints().addConstraint(this.currentOpId, abstractActor);
      } else {
        this.scenario.getConstraints().getGroupConstraints().get(currentOpId).remove(abstractActor);
      }
    }

    // Checks the children of the current vertex
    if (abstractActor instanceof final Actor actor && actor.isHierarchical()) {
      final PiGraph subGraph = actor.getSubGraph();
      for (final AbstractActor v : this.contentProvider.filterPISDFChildren(subGraph.getActors())) {
        fireOnCheck(v, isChecked);
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
    // no behavior by default
  }

  /**
   * Core combo box listener that selects the current core.
   *
   * @param e
   *          the e
   */
  @Override
  public void widgetSelected(final SelectionEvent e) {
    if (e.getSource() instanceof final Combo combo) {
      final String item = combo.getItem(combo.getSelectionIndex());

      this.currentOpId = this.scenario.getDesign().getComponentInstance(item);
      updateCheck();
    }

  }

  /**
   * Update the check status of the whole tree.
   */
  public void updateCheck() {
    if (this.scenario != null) {
      updateCheckPISDF();
    }
  }

  /**
   * Update check PISDF.
   */
  private void updateCheckPISDF() {
    final PiGraph currentGraph = this.contentProvider.getPISDFCurrentGraph();
    if ((this.currentOpId != null) && (currentGraph != null)) {
      final Set<AbstractActor> cgSet = new LinkedHashSet<>();

      final List<AbstractActor> cg = this.scenario.getConstraints().getGroupConstraints().get(this.currentOpId);

      if (cg != null) {
        // Retrieves the elements in the tree that have the same name as
        // the ones to select in the constraint group
        for (final AbstractActor v : cg) {
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

    final GridData componentNameGridData = new GridData();
    componentNameGridData.widthHint = 250;
    combocps.setLayoutData(componentNameGridData);

    combocps.setVisible(true);
    final Combo combo = new Combo(combocps, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.SIMPLE);
    combo.setVisibleItemCount(20);
    combo.setToolTipText(Messages.getString("Constraints.coreSelectionTooltip"));
    comboDataInit(combo);
    combo.addSelectionListener(this);
    combo.select(0);
  }

  /**
   * Combo data init.
   *
   * @param combo
   *          the combo
   */
  private void comboDataInit(final Combo combo) {
    combo.removeAll();
    final Design design = this.scenario.getDesign();
    final List<ComponentInstance> orderedOperators = design.getOrderedOperatorComponentInstances();
    for (final ComponentInstance id : orderedOperators) {
      combo.add(id.getInstanceName());
    }
    // select first operator by default
    this.currentOpId = orderedOperators.get(0);
  }

  @Override
  public void paintControl(final PaintEvent e) {
    updateCheck();

  }
}
