/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2012)
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
package org.ietr.preesm.ui.scenario.editor.papify;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.ietr.preesm.core.scenario.papi.PapiComponent;
import org.ietr.preesm.core.scenario.papi.PapiEvent;
import org.ietr.preesm.core.scenario.papi.PapiEventInfo;
import org.ietr.preesm.core.scenario.papi.PapiEventModifier;
import org.ietr.preesm.core.scenario.papi.PapiEventSet;

// TODO: Auto-generated Javadoc
/**
 * Provides the events contained in the papify component.
 *
 * @author dmadronal
 */
public class PapifyEventListContentProvider implements IStructuredContentProvider {

  private List<PapiEvent> eventList;

  /**
   * Gets the Papi Event list.
   *
   * @return the Papi Event list
   */
  public List<PapiEvent> getEvents() {
    return this.eventList;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
   */
  @Override
  public Object[] getElements(final Object inputElement) {

    Object[] elementTable = null;

    if (inputElement instanceof PapiEventInfo) {
      final PapiEventInfo inputPapiEventInfo = (PapiEventInfo) inputElement;
      final PapiEvent timingEvent = new PapiEvent();
      final List<PapiEventModifier> modifTimingList = new ArrayList<>();
      PapiComponent compAux = null;
      PapiEventSet eventSetAux = null;
      PapiEvent eventAux = null;
      this.eventList = new ArrayList<>();
      timingEvent.setName("Timing");
      timingEvent.setDesciption("Event to time through PAPI_get_time()");
      timingEvent.setIndex(9999);
      timingEvent.setModifiers(modifTimingList);
      this.eventList.add(timingEvent);
      for (int i = inputPapiEventInfo.getComponents().size() - 1; i >= 0; i--) {
        compAux = inputPapiEventInfo.getComponents().get(i);
        for (int j = compAux.getEventSets().size() - 1; j >= 0; j--) {
          eventSetAux = compAux.getEventSets().get(j);
          for (int k = 0; k < eventSetAux.getEvents().size(); k++) {
            eventAux = eventSetAux.getEvents().get(k);
            if (eventAux.getModifiers().isEmpty()) {
              this.eventList.add(eventAux);
            }
          }
        }
      }
      elementTable = this.eventList.toArray();
    }

    return elementTable;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.IContentProvider#dispose()
   */
  @Override
  public void dispose() {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
   */
  @Override
  public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
    // TODO Auto-generated method stub

  }

}
