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

import java.util.LinkedHashSet;
import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * A PapifyConfig stores the monitoring configuration of each core instance.
 *
 * @author dmadronal
 */
public class PapifyConfigPE {

  /** The core instance. */
  private String coreId;

  /** The PAPI component(s) associated with the core instance. */
  private Set<PapiComponent> PAPIComponents;

  /** The PAPI component(s) ID(s) associated with the core instance. */
  private Set<String> PAPIComponentIDs;

  /**
   * Instantiates a new PapifyConfig group.
   */
  public PapifyConfigPE() {
    this.coreId = "";
    this.PAPIComponents = new LinkedHashSet<>();
    this.PAPIComponentIDs = new LinkedHashSet<>();

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
    this.PAPIComponents.add(component);
    this.PAPIComponentIDs.add(component.getId());

  }

  /**
   * Adds the PAPI components.
   *
   * @param components
   *          the PAPI components
   */
  public void addPAPIComponents(final Set<PapiComponent> components) {
    for (final PapiComponent component : components) {
      this.PAPIComponents.add(component);
      this.PAPIComponentIDs.add(component.getId());
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
    this.PAPIComponents.remove(component);
    this.PAPIComponentIDs.remove(component.getId());
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
   * Gets the PAPI components.
   *
   * @return the PAPI components
   */
  public Set<PapiComponent> getPAPIComponents() {
    return (this.PAPIComponents);
  }

  /**
   * Gets the PAPI component IDs.
   *
   * @return the PAPI component IDs
   */
  public Set<String> getPAPIComponentIDs() {
    return (this.PAPIComponentIDs);
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
  public boolean containsPAPIComponent(final PapiComponent component) {

    if (this.PAPIComponents.contains(component)) {
      return true;
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
    s += this.PAPIComponents.toString();
    s += "<end printing>\n";

    return s;
  }
}
