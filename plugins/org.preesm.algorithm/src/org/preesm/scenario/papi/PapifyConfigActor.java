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

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * A PapifyConfig stores the monitoring configuration of each core instance.
 *
 * @author dmadronal
 */
public class PapifyConfigActor {

  /** The actor instance. */
  private String actorId;
  /** The actor instance path. */
  private String actorPath;

  /** The set of eventSets that are going to be monitored. */
  private Map<String, Set<PapiEvent>> actorEventMap;

  /**
   * Instantiates a new PapifyConfig group.
   */
  public PapifyConfigActor() {
    this.actorEventMap = new LinkedHashMap<>();
  }

  /**
   * Instantiates a new PapifyConfig group.
   */
  public PapifyConfigActor(final String actorId, final String actorPath) {
    this.actorId = actorId;
    this.actorPath = actorPath;
    this.actorEventMap = new LinkedHashMap<>();
  }

  /**
   * Adds the actorId.
   *
   * @param actorId
   *          the actor instance
   */
  public void addActorId(final String actorId) {
    this.actorId = actorId;

  }

  /**
   * Adds the actorPath.
   *
   * @param actorPath
   *          the actor instance
   */
  public void addActorPath(final String actorPath) {
    this.actorPath = actorPath;

  }

  /**
   * Adding an event
   *
   * @param event
   *          the PAPI event
   */
  public void addPAPIEvent(final String component, final PapiEvent event) {
    Set<PapiEvent> eventSetAux = null;
    if (component != null && !component.equals("") && event != null) {
      eventSetAux = this.actorEventMap.get(component);
      if (eventSetAux != null) {
        if (!eventSetAux.contains(event)) {
          eventSetAux.add(event);
        }
      } else {
        eventSetAux = new LinkedHashSet<>();
        eventSetAux.add(event);
        this.actorEventMap.put(component, eventSetAux);
      }
    }
  }

  /**
   * Adding an eventSet
   *
   * @param events
   *          the PAPI events
   */
  public void addPAPIEventSet(final String component, final Set<PapiEvent> events) {
    Set<PapiEvent> eventSetAux = null;
    if (component != null && !component.equals("") && !events.isEmpty()) {
      eventSetAux = this.actorEventMap.get(component);
      if (eventSetAux != null) {
        for (final PapiEvent eventAux : events) {
          if (!eventSetAux.contains(eventAux)) {
            eventSetAux.add(eventAux);
          }
        }
      } else {
        eventSetAux = new LinkedHashSet<>();
        for (final PapiEvent eventAux : events) {
          eventSetAux.add(eventAux);
        }
        this.actorEventMap.put(component, eventSetAux);
      }
    }
  }

  /**
   * Removes the actorId.
   *
   * @param actorId
   *          the actorId
   */
  public void removeActorId(final String actorId) {
    if (actorId.equals(this.actorId)) {
      this.actorId = "";
      this.actorEventMap = null;
    }
  }

  /**
   * Removes an event.
   *
   * @param event
   *          the PAPI event
   */
  public void removePAPIEvent(final String component, final PapiEvent event) {
    Set<PapiEvent> eventSetAux = null;
    if (component != null && !component.equals("") && event != null) {
      eventSetAux = this.actorEventMap.get(component);
      if (eventSetAux != null) {
        if (eventSetAux.contains(event)) {
          eventSetAux.remove(event);
        }
      }
    }
  }

  /**
   * Gets the actor id.
   *
   * @return the actor id
   */
  public String getActorId() {
    return (this.actorId);
  }

  /**
   * Gets the actor path.
   *
   * @return the actor path
   */
  public String getActorPath() {
    return (this.actorPath);
  }

  /**
   * Gets the PAPI events.
   *
   * @return the PAPI events
   */
  public Map<String, Set<PapiEvent>> getPAPIEvents() {
    return this.actorEventMap;
  }

  /**
   * Checks for Actor id.
   *
   * @param actorId
   *          the actor id
   * @return true, if successful
   */
  public boolean isActorId(final String actorId) {

    return actorId.equals(this.actorId);
  }

  /**
   * Checks for Actor path.
   *
   * @param actorPath
   *          the actor path
   * @return true, if successful
   */
  public boolean isActorPath(final String actorPath) {

    return actorPath.equals(this.actorPath);
  }

  /**
   * Checks for PAPI events.
   *
   * @param event
   *          the PAPI event
   * @return true, if successful
   */
  public boolean hasPapiEvent(String component, final PapiEvent event) {
    Set<PapiEvent> eventSetAux = null;
    if (component != null && !component.equals("") && event != null) {
      eventSetAux = this.actorEventMap.get(component);
      if (eventSetAux != null) {
        if (eventSetAux.contains(event)) {
          return true;
        }
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
    String s = "<Printing actorId> \n";
    s += this.actorId;
    s += "<Printing actorPath> \n";
    s += this.actorPath;
    s += "\n<Printing components and Events> \n";
    s += this.actorEventMap.toString();
    s += "<end printing>\n";

    return s;
  }
}
