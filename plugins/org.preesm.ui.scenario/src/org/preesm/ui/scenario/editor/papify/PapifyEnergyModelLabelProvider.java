/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2015)
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
package org.preesm.ui.scenario.editor.papify;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.preesm.model.scenario.PapiEvent;
import org.preesm.model.scenario.PapiEventInfo;
import org.preesm.model.slam.Component;

/**
 * Displays the labels for the parameters added to the energy model.
 *
 * @author dmadronal
 */
public class PapifyEnergyModelLabelProvider extends BaseLabelProvider implements ITableLabelProvider {

  /** List of events in order to be matched while using the interface */

  private List<PapiEvent> eventList = null;

  public PapifyEnergyModelLabelProvider(PapiEventInfo papiData) {
    this.eventList = new ArrayList<>();
  }

  @Override
  public Image getColumnImage(final Object element, final int columnIndex) {
    return null;
  }

  @Override
  public String getColumnText(final Object element, final int columnIndex) {
    String text = "";

    if (element instanceof Entry) {
      @SuppressWarnings("unchecked")
      final Entry<Component, EMap<PapiEvent, Double>> parameter = (Entry<Component, EMap<PapiEvent, Double>>) element;

      if (columnIndex == 0) {
        text = parameter.getKey().getVlnv().getName();
      } else if (parameter.getValue().containsKey(eventList.get(columnIndex - 1))) {
        text = Double.toString(parameter.getValue().get(this.eventList.get(columnIndex - 1)));
      }
    }

    return text;

  }

  public void clearEventList() {
    this.eventList.clear();
  }

  public void addEventToList(PapiEvent event) {
    this.eventList.add(event);
  }
}
