/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
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

import java.util.LinkedHashSet;
import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * A PapifyConfig stores the monitoring configuration of each core instance.
 *
 * @author dmadronal
 */
public class PapifyConfig {

  /** The core instance. */
  private String coreId;

  /** The PAPI component associated to the core instance. */
  private PapiComponent PAPIComponent;

  /** The set of events that are going to be monitored. */
  private final Set<PapiEvent> PAPIEvents;

  /**
   * Instantiates a new PapifyConfig group.
   */
  public PapifyConfig() {
    this.coreId = "";
    this.PAPIComponent = null;
    this.PAPIEvents = new LinkedHashSet<>();

  }

  /**
   * Adds the coreId.
   *
   * @param coreId
   *          the core instance
   */
  public void addCoreId(final String coreId) {
    this.coreId = coreId;

  }

  /**
   * Adds the PAPI component.
   *
   * @param component
   *          the PAPI component
   */
  public void addPAPIComponent(final PapiComponent component) {
    this.PAPIComponent = component;

  }

  /**
   * Adding an event
   *
   * @param event
   *          the PAPI event
   */
  public void addPAPIEvent(final PapiEvent event) {
    if (!this.PAPIEvents.contains(event)) {
      this.PAPIEvents.add(event);
    }
  }

  /**
   * Removes the coreId.
   *
   * @param coreId
   *          the coreId
   */
  public void removeCoreId(final String coreId) {
    if (coreId.equals(this.coreId)) {
      this.coreId = "";
    }
  }

  /**
   * Removes the PAPI component.
   *
   * @param component
   *          the PAPI component
   */
  public void removePAPIComponent(final PapiComponent component) {
    if (component.equals(this.PAPIComponent)) {
      this.PAPIComponent = null;
    }
  }

  /**
   * Removes an event.
   *
   * @param event
   *          the PAPI event
   */
  public void removePAPIEvent(final PapiEvent event) {
    if (this.PAPIEvents.contains(event)) {
      this.PAPIEvents.remove(event);
    }
  }

  /**
   * Gets the Core id.
   *
   * @return the Core id
   */
  public String getCoreId() {
    return (this.coreId);
  }

  /**
   * Gets the PAPI component.
   *
   * @return the PAPI component
   */
  public PapiComponent getPAPIComponent() {
    return (this.PAPIComponent);
  }

  /**
   * Gets the PAPI events.
   *
   * @return the PAPI events
   */
  public Set<PapiEvent> getPAPIEvents() {
    return new LinkedHashSet<>(this.PAPIEvents);
  }

  /**
   * Checks for Core id.
   *
   * @param coreId
   *          the PAPI component
   * @return true, if successful
   */
  public boolean isCoreId(final String coreId) {

    if (coreId.equals(this.coreId)) {
      return true;
    }

    return false;
  }

  /**
   * Checks for PAPI component.
   *
   * @param component
   *          the PAPI component
   * @return true, if successful
   */
  public boolean isPAPIComponent(final PapiComponent component) {

    if (component.equals(this.PAPIComponent)) {
      return true;
    }

    return false;
  }

  /**
   * Checks for PAPI events.
   *
   * @param event
   *          the PAPI event
   * @return true, if successful
   */
  public boolean hasPapiEvent(final PapiEvent event) {

    for (final PapiEvent eventTest : this.PAPIEvents) {
      if (eventTest.equals(event)) {
        return true;
      }
    }

    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    String s = "<Printing core> \n";
    s += this.coreId.toString();
    s += "\n<Printing component> \n";
    s += this.PAPIComponent.toString();
    s += "\n<Printing events> \n";
    s += this.PAPIEvents.toString();
    s += "<end printing>\n";

    return s;
  }
}
