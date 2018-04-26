/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 * Daniel Madroñal <daniel.madronal@upm.es> (2018)
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
package org.ietr.preesm.core.scenario.papi;

import java.util.List;

/**
 *
 * @author anmorvan
 *
 */
public class PapiComponent {

  public String getId() {
    return this.id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public PapiComponentType getType() {
    return this.type;
  }

  public void setType(final PapiComponentType type) {
    this.type = type;
  }

  public int getIndex() {
    return this.index;
  }

  public void setIndex(final int index) {
    this.index = index;
  }

  public List<PapiEventSet> getEventSets() {
    return this.eventSets;
  }

  public void setEventSets(final List<PapiEventSet> eventSets) {
    this.eventSets = eventSets;
  }

  /**
   *
   */
  public boolean containsEvent(final PapiEvent event) {
    boolean decision = false;
    for (final PapiEventSet eventSet : this.eventSets) {
      if (eventSet.containsEvent(event)) {
        decision = true;
      }
    }
    return decision;
  }

  private String             id;
  private PapiComponentType  type;
  private int                index;
  private List<PapiEventSet> eventSets;

  /**
   *
   */
  public PapiComponent(final String componentID, final String componentIndex, final String componentType) {
    this.index = Integer.valueOf(componentIndex);
    this.id = componentID;
    this.type = PapiComponentType.parse(componentType);
  }

  @Override
  public boolean equals(final Object comparer) {

    boolean decision = false;
    boolean idComp = false;
    boolean typeComp = false;
    boolean indexComp = false;
    boolean eventSetsComp = false;

    if (comparer instanceof PapiComponent) {
      final PapiComponent tester = (PapiComponent) comparer;
      if (this.id.equals(tester.getId())) {
        idComp = true;
      }
      if (this.type.equals(tester.getType())) {
        typeComp = true;
      }
      if (this.index == tester.getIndex()) {
        indexComp = true;
      }
      if (this.eventSets.equals(tester.getEventSets())) {
        eventSetsComp = true;
      }
      if (idComp && typeComp && indexComp && eventSetsComp) {
        decision = true;
      }
    }
    return decision;
  }

  @Override
  public int hashCode() {
    return this.index;
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder();
    final String string = this.type.toString();
    b.append(String.format("  <component id=\"%s\" index=\"%d\" type=\"%s\">%n", this.id, this.index, string));
    for (final PapiEventSet eventSet : this.eventSets) {
      b.append(eventSet.toString());
    }
    b.append(String.format("  </component>%n"));
    return b.toString();
  }
}
