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
package org.preesm.scenario.papi;

import java.util.List;

/**
 *
 */
public class PapiEventSet {

  private PapiEventSetType type;

  public PapiEventSetType getType() {
    return this.type;
  }

  public void setType(final PapiEventSetType type) {
    this.type = type;
  }

  public List<PapiEvent> getEvents() {
    return this.events;
  }

  public void setEvents(final List<PapiEvent> events) {
    this.events = events;
  }

  public void addEvent(final PapiEvent event) {
    this.events.add(event);
  }

  private List<PapiEvent> events;

  /**
   *
   */
  public boolean containsEvent(final PapiEvent event) {
    return this.events.contains(event);
  }

  @Override
  public boolean equals(final Object comparer) {

    boolean decision = false;
    boolean typeComp = false;
    boolean eventsComp = false;

    if (comparer instanceof PapiEventSet) {
      final PapiEventSet tester = (PapiEventSet) comparer;
      if (this.type.equals(tester.getType())) {
        typeComp = true;
      }
      if (this.events.equals(tester.getEvents())) {
        eventsComp = true;
      }
      if (typeComp && eventsComp) {
        decision = true;
      }
    }
    return decision;
  }

  @Override
  public int hashCode() {
    return this.type.hashCode();
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder();
    final String string = this.type.toString();
    b.append(String.format("    <eventset type=\"%s\">%n", string));
    for (final PapiEvent event : this.events) {
      b.append(event.toString());
    }
    b.append(String.format("    </eventset>%n"));
    return b.toString();
  }
}
