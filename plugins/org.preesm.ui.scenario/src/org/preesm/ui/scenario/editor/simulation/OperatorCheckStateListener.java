/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2024) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2015)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2011)
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
package org.preesm.ui.scenario.editor.simulation;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.widgets.Section;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;

/**
 * Listener of the check state of the Operator tree.
 *
 * @author mpelcat
 */
public class OperatorCheckStateListener implements ICheckStateListener, PaintListener {

  /** Currently edited scenario. */
  private Scenario scenario = null;

  /** Current section (necessary to diplay busy status). */
  private Section section = null;

  /** Tree viewer used to set the checked status. */
  private CheckboxTreeViewer treeViewer = null;

  /** Constraints page used as a property listener to change the dirty state. */
  private IPropertyListener propertyListener = null;

  /**
   * Instantiates a new operator check state listener.
   *
   * @param section
   *          the section
   * @param scenario
   *          the scenario
   */
  public OperatorCheckStateListener(final Section section, final Scenario scenario) {
    super();
    this.scenario = scenario;
    this.section = section;
  }

  /**
   * Sets the different necessary attributes.
   *
   * @param treeViewer
   *          the tree viewer
   * @param propertyListener
   *          the property listener
   */
  public void setTreeViewer(final CheckboxTreeViewer treeViewer, final IPropertyListener propertyListener) {
    this.treeViewer = treeViewer;
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
    BusyIndicator.showWhile(this.section.getDisplay(), new Runnable() {

      @Override
      public void run() {
        if (element instanceof final ComponentInstance componentInstance) {

          if (isChecked) {
            OperatorCheckStateListener.this.scenario.getSimulationInfo().addSpecialVertexOperator(componentInstance);
          } else {
            OperatorCheckStateListener.this.scenario.getSimulationInfo().getSpecialVertexOperators()
                .remove(componentInstance);
          }

          OperatorCheckStateListener.this.propertyListener.propertyChanged(this, IEditorPart.PROP_DIRTY);
        }
      }
    });
  }

  /**
   * Update the check status of the whole tree.
   */
  public void updateCheck() {
    if (this.scenario != null) {
      this.treeViewer.setCheckedElements(this.scenario.getSimulationInfo().getSpecialVertexOperators().toArray());
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
