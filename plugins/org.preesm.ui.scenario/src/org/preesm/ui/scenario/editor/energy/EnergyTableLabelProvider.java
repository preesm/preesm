/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2019) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Daniel Madroñal [daniel.madronal@upm.es] (2019)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2011 - 2013)
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
package org.preesm.ui.scenario.editor.energy;

import java.util.List;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.ui.IPropertyListener;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.Design;

/**
 * Displays the labels for tasks energies. These labels are the energy of each task
 *
 * @author mpelcat
 */
public class EnergyTableLabelProvider extends BaseLabelProvider implements ITableLabelProvider, SelectionListener {

  /** The scenario. */
  private Scenario scenario = null;

  /** The current op def id. */
  private Component currentOpDefId = null;

  /** The table viewer. */
  private TableViewer tableViewer = null;

  /** Constraints page used as a property listener to change the dirty state. */
  private IPropertyListener propertyListener = null;

  /**
   * Instantiates a new actor energy label provider.
   *
   * @param scenario
   *          the scenario
   * @param tableViewer
   *          the table viewer
   * @param propertyListener
   *          the property listener
   */
  public EnergyTableLabelProvider(final Scenario scenario, final TableViewer tableViewer,
      final IPropertyListener propertyListener) {
    super();
    this.scenario = scenario;
    this.tableViewer = tableViewer;
    this.propertyListener = propertyListener;

    final Design design = scenario.getDesign();
    final List<Component> operators = design.getOperatorComponents();
    this.currentOpDefId = operators.get(0);
  }

  @Override
  public Image getColumnImage(final Object element, final int columnIndex) {
    // Not necessary here
    return null;
  }

  @Override
  public String getColumnText(final Object element, final int columnIndex) {
    if ((element instanceof AbstractActor) && (this.currentOpDefId != null)) {
      final AbstractActor vertex = (AbstractActor) element;

      final Double energy = this.scenario.getEnergyConfig().getEnergyActorOrDefault(vertex, this.currentOpDefId);

      switch (columnIndex) {
        case 0:
          return vertex.getVertexPath();
        case 1: // Value
          return Double.toString(energy);
        default:
      }
    }
    return "";
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
      this.currentOpDefId = this.scenario.getDesign().getComponent(item);
      this.tableViewer.refresh();
    }
  }

  @Override
  public void widgetDefaultSelected(SelectionEvent e) {
    // nothing
  }

}
