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
 * container and manager of PapifyConfig groups. It can load and store PapifyConfig groups
 *
 * @author dmadronal
 */
public class PapifyConfigManager {

  /** List of all PapifyConfig groups. */
  private final Set<PapifyConfig> papifyConfigGroups;

  /** Path to a file containing constraints. */
  private String xmlFileURL = "";

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
  public void addComponent(final String opId, final PapiComponent component) {

    final PapifyConfig pgSet = getCorePapifyConfigGroups(opId);

    if (pgSet == null) {
      final PapifyConfig pg = new PapifyConfig();
      pg.addCoreId(opId);
      pg.addPAPIComponent(component);
      this.papifyConfigGroups.add(pg);
    } else {
      pgSet.addPAPIComponent(component);
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
  public void removeComponent(final String opId, final PapiComponent component) {
    final PapifyConfig pgSet = getCorePapifyConfigGroups(opId);

    if (pgSet != null) {
      pgSet.removePAPIComponent(component);
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

    final PapifyConfig pgSet = getCorePapifyConfigGroups(opId);

    if (pgSet == null) {
      final PapifyConfig pg = new PapifyConfig();
      pg.addCoreId(opId);
      pg.addPAPIEvent(event);
      this.papifyConfigGroups.add(pg);
    } else {
      pgSet.addPAPIEvent(event);
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
    final PapifyConfig pgSet = getCorePapifyConfigGroups(opId);

    if (pgSet != null) {
      pgSet.removePAPIEvent(event);
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
  public PapifyConfig getComponentPapifyConfigGroups(final PapiComponent component) {
    PapifyConfig papifyConfigGroup = null;

    for (final PapifyConfig pg : this.papifyConfigGroups) {
      if (pg.isPAPIComponent(component)) {
        papifyConfigGroup = pg;
      }
    }

    return papifyConfigGroup;
  }

  /**
   * Gets the op PapifyConfig group.
   *
   * @param opId
   *          the op id
   * @return the op PapifyConfig groups
   */
  public PapifyConfig getCorePapifyConfigGroups(final String opId) {
    PapifyConfig papifyConfigGroup = null;

    for (final PapifyConfig pg : this.papifyConfigGroups) {
      if (pg.isCoreId(opId)) {
        papifyConfigGroup = pg;
      }
    }

    return papifyConfigGroup;
  }

  /**
   * Removes the all.
   */
  public void removeAll() {

    this.papifyConfigGroups.clear();
  }

  /**
   * Gets the xml file URL.
   *
   * @return the xml file URL
   */
  public String getXmlFileURL() {
    return this.xmlFileURL;
  }

  /**
   * Sets the xml file URL.
   *
   * @param xmlFileURL
   *          the new xml file URL
   */
  public void setExcelFileURL(final String xmlFileURL) {
    this.xmlFileURL = xmlFileURL;
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
