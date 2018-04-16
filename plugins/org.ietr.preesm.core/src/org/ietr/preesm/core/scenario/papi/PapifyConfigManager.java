/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
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
 * container and manager of PapifyConfig groups. It can load and store PapifyConfig groups
 *
 * @author dmadronal
 */
public class PapifyConfigManager {

  /** List of all PapifyConfig groups. */
  private final Set<PapifyConfig> papifyConfigGroups;

  /**
   * Instantiates a new PapifyConfig manager.
   */
  public PapifyConfigManager() {
    this.papifyConfigGroups = new LinkedHashSet<>();
  }

  /**
   * Adds the PapifyConfig group.
   *
   * @param pg
   *          the pg
   */
  public void addPapifyConfigGroup(final PapifyConfig pg) {

    this.papifyConfigGroups.add(pg);
  }

  /**
   * Adding a component to the core.
   *
   * @param opId
   *          the op id
   * @param component
   *          the PAPI component
   */
  public void addComponent(final String opId, final String component) {

    final Set<PapifyConfig> pgSet = getCorePapifyConfigGroups(opId);

    if (pgSet.isEmpty()) {
      final PapifyConfig pg = new PapifyConfig();
      pg.addCoreId(opId);
      pg.addPAPIComponent(component);
      this.papifyConfigGroups.add(pg);
    } else {
      ((PapifyConfig) pgSet.toArray()[0]).addPAPIComponent(component);
    }
  }

  /**
   * Removes the component from the PapifyConfig for the core.
   *
   * @param opId
   *          the op id
   * @param component
   *          the PAPI component
   */
  public void removeComponent(final String opId, final String component) {
    final Set<PapifyConfig> pgSet = getCorePapifyConfigGroups(opId);

    if (!pgSet.isEmpty()) {
      for (final PapifyConfig pg : pgSet) {
        pg.removePAPIComponent(component);
      }
    }
  }

  /**
   * Adds an event to the PapifyConfig for the core.
   *
   * @param opId
   *          the op id
   * @param event
   *          the PAPI event
   */
  public void addEvent(final String opId, final PapiEvent event) {

    final Set<PapifyConfig> pgSet = getCorePapifyConfigGroups(opId);

    if (pgSet.isEmpty()) {
      final PapifyConfig pg = new PapifyConfig();
      pg.addCoreId(opId);
      pg.addPAPIEvent(event);
      this.papifyConfigGroups.add(pg);
    } else {
      ((PapifyConfig) pgSet.toArray()[0]).addPAPIEvent(event);
    }
  }

  /**
   * Removes an event from the PapifyConfig for the core.
   *
   * @param opId
   *          the op id
   * @param event
   *          the PAPI event
   */
  public void removeEvent(final String opId, final PapiEvent event) {
    final Set<PapifyConfig> pgSet = getCorePapifyConfigGroups(opId);

    if (!pgSet.isEmpty()) {
      for (final PapifyConfig pg : pgSet) {
        pg.removePAPIEvent(event);
      }
    }
  }

  /**
   * Gets the PapifyConfig groups.
   *
   * @return the PapifyConfig groups
   */
  public Set<PapifyConfig> getPapifyConfigGroups() {

    return new LinkedHashSet<>(this.papifyConfigGroups);
  }

  /**
   * Gets the component PapifyConfig groups.
   *
   * @param component
   *          the PAPI component
   * @return the component PapifyConfig groups
   */
  public Set<PapifyConfig> getComponentPapifyConfigGroups(final String component) {
    final Set<PapifyConfig> graphPapifyConfigGroups = new LinkedHashSet<>();

    for (final PapifyConfig pg : this.papifyConfigGroups) {
      if (pg.isPAPIComponent(component)) {
        graphPapifyConfigGroups.add(pg);
      }
    }

    return graphPapifyConfigGroups;
  }

  /**
   * Gets the op PapifyConfig groups.
   *
   * @param opId
   *          the op id
   * @return the op PapifyConfig groups
   */
  public Set<PapifyConfig> getCorePapifyConfigGroups(final String opId) {
    final Set<PapifyConfig> graphPapifyConfigGroups = new LinkedHashSet<>();

    for (final PapifyConfig pg : this.papifyConfigGroups) {
      if (pg.isCoreId(opId)) {
        graphPapifyConfigGroups.add(pg);
      }
    }

    return graphPapifyConfigGroups;
  }

  /**
   * Removes the all.
   */
  public void removeAll() {

    this.papifyConfigGroups.clear();
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    String s = "";

    for (final PapifyConfig pg : this.papifyConfigGroups) {
      s += pg.toString();
    }

    return s;
  }

  /**
   * Update.
   */
  public void update() {
    removeAll();
  }
}
