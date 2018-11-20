/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2013)
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
package org.preesm.ui.pisdf.util;

import java.util.LinkedHashSet;
import java.util.Set;
import org.eclipse.jface.dialogs.IInputValidator;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Port;

// TODO: Auto-generated Javadoc
/**
 * This validator is used to check whether a port in a vertex already has a given name.
 *
 * @author kdesnos
 *
 */
public class PortNameValidator implements IInputValidator {

  /** The vertex. */
  protected AbstractActor vertex;

  /** The renamed port. */
  protected Port renamedPort;

  /** The ports names. */
  protected Set<String> portsNames;

  /**
   * Default constructor of the {@link PortNameValidator}.
   *
   * @param vertex
   *          the port to which we add/rename a port
   * @param renamedPort
   *          the renamed port, or <code>null</code> if not a rename operation
   */
  public PortNameValidator(final AbstractActor vertex, final Port renamedPort) {
    this.vertex = vertex;
    this.renamedPort = renamedPort;

    // Create the list of already existing names
    this.portsNames = new LinkedHashSet<>();

    for (final Port port : vertex.getConfigInputPorts()) {
      this.portsNames.add(port.getName());
    }

    for (final Port port : vertex.getConfigOutputPorts()) {
      this.portsNames.add(port.getName());
    }

    for (final Port port : vertex.getDataInputPorts()) {
      this.portsNames.add(port.getName());
    }

    for (final Port port : vertex.getDataOutputPorts()) {
      this.portsNames.add(port.getName());
    }

    if (this.renamedPort != null) {
      this.portsNames.remove(renamedPort.getName());
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.dialogs.IInputValidator#isValid(java.lang.String)
   */
  @Override
  public String isValid(final String newPortName) {
    String message = null;
    // Check if the name is not empty
    if (newPortName.length() < 1) {
      message = "/!\\ Port name cannot be empty /!\\";
      return message;
    }

    // Check if the name contains a space
    if (newPortName.contains(" ")) {
      message = "/!\\ Port name must not contain spaces /!\\";
      return message;
    }

    // Check if no other port has the same name
    if (this.portsNames.contains(newPortName)) {
      message = "/!\\ A port with name " + newPortName + " already exists /!\\";
      return message;
    }

    return message;
  }

}
