/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2014)
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
package org.ietr.preesm.ui.pimm.util;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.impl.Reason;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.Port;

// TODO: Auto-generated Javadoc
/**
 * This class is used to test the equality of ports a return the reason why they are not equal.
 *
 * @author kdesnos
 *
 */
public class PortEqualityHelper {
  /**
   * Ports have different classes (e.g. InputPort and OutputPort)
   */
  public static final String DIFFERENT_CLASSES = "Different port classes";

  /** Ports have different name. */
  public static final String DIFFERENT_NAMES = "Different port names";

  /** One of the two ports is Null. */
  public static final String NULL_PORT = "A port is null";

  /**
   * Builds and return a {@link Map} of equivalent {@link Port}s and, if needed, an associated {@link IReason} for their
   * inequality. <br>
   * <br>
   * Two ports are equivalent if they have the same class and name but may have different rates. The returned
   * {@link Map}is structured as the following example:
   * <table border="1">
   * <tr>
   * <td colspan=2>Entry(Port,Port)</td>
   * <td>IReason</td>
   * </tr>
   * <tr>
   * <td>Vertex1.Port1</td>
   * <td>Vertex2.Port1</td>
   * <td>true</td>
   * </tr>
   * <tr>
   * <td>Vertex1.Port2</td>
   * <td>null</td>
   * <td>No equivalent</td>
   * </tr>
   * <tr>
   * <td>null</td>
   * <td>Vertex2.PortY</td>
   * <td>No equivalent</td>
   * </tr>
   * <tr>
   * <td>Vertex1.PortX</td>
   * <td>Vertex2.PortX</td>
   * <td>false (different rates</td>
   * </tr>
   * </table>
   * Ports of vertex1 always are the key of the Entry and Ports of vertex2 always are the value.
   *
   *
   * @param vertex1
   *          First {@link AbstractActor} whose {@link Port}s are compared.
   * @param vertex2
   *          Second {@link AbstractActor} whose {@link Port}s are compared.
   * @return the {@link Map} of equivalent {@link Port}s
   */
  public static Map<SimpleEntry<Port, Port>, IReason> buildEquivalentPortsMap(final AbstractActor vertex1,
      final AbstractActor vertex2) {
    final Map<SimpleEntry<Port, Port>, IReason> result = new LinkedHashMap<>();

    // Do it backward (vertex2 with vertex1) for ConfigInputPort
    // because ConfigInputPort of actor are not mandatorily in the
    // refinement
    PortEqualityHelper.comparePortLists(vertex2.getConfigInputPorts(), vertex1.getConfigInputPorts(), result, false);
    PortEqualityHelper.comparePortLists(vertex1.getConfigOutputPorts(), vertex2.getConfigOutputPorts(), result, true);
    PortEqualityHelper.comparePortLists(vertex1.getDataInputPorts(), vertex2.getDataInputPorts(), result, true);
    PortEqualityHelper.comparePortLists(vertex1.getDataOutputPorts(), vertex2.getDataOutputPorts(), result, true);

    return result;
  }

  /**
   * Compares two lists of {@link Port} to make sure all ports from the first list are also in the second. (and
   * optionally vice-versa).
   *
   * @param <T>
   *          the generic type
   * @param ports1
   *          the first {@link List} of {@link Port}
   * @param ports2
   *          the second {@link List} of {@link Port}
   * @param result
   *          cf. Comments of {@link #buildEquivalentPortsMap(AbstractActor, AbstractActor)} for details.
   * @param backwardTest
   *          Optionally tests if all ports of the second list belong to the first
   */
  protected static <T extends Port> void comparePortLists(final EList<T> ports1, final EList<T> ports2,
      final Map<SimpleEntry<Port, Port>, IReason> result, final boolean backwardTest) {
    // Maintain a list of input port of vertex2 whose equivalent has not
    // been
    // found yet
    final List<Port> noEquivalentFound = new ArrayList<>(ports2);

    // Scan ports of vertex1 looking for an equivalent
    for (final Port p1 : ports1) {
      Port equivalent = null;
      for (final Port p2 : noEquivalentFound) {
        if (PortEqualityHelper.comparePorts(p1, p2).toBoolean()) {
          equivalent = p2;
          break;
        }
      }
      noEquivalentFound.remove(equivalent);
      result.put(new SimpleEntry<>(p1, equivalent), PortEqualityHelper.comparePorts(p1, equivalent));
    }

    if (backwardTest) {
      // Add of vertex2 that have no equivalents
      for (final Port p2 : noEquivalentFound) {
        result.put(new SimpleEntry<>((Port) null, p2), PortEqualityHelper.comparePorts((Port) null, p2));
      }
    }
  }

  /**
   * Check if two ports are equal. If not, the reason for their inequality is returned. <br>
   * <br>
   * NB: Incoming and Outgoing {@link Fifo}s are not considered when comparing {@link Port}. Use
   * {@link EcoreUtil#EqualityHelper} for this type of comparison.
   *
   * @param port1
   *          the first {@link Port} to compare
   * @param port2
   *          the second {@link Port} to compare
   * @return an {@link IReason} for the equality or inequality
   */
  public static IReason comparePorts(final Port port1, final Port port2) {

    if (port1 == port2) {
      return Reason.createTrueReason();
    }

    if ((port1 == null) || (port2 == null)) {
      return Reason.createFalseReason(PortEqualityHelper.NULL_PORT);
    }

    // Check if the two ports have the same class
    if (port1.eClass() != port2.eClass()) {
      // otherwise, return false
      return Reason.createFalseReason(PortEqualityHelper.DIFFERENT_CLASSES);
    }

    // Check if the name are identical
    if (!port1.getName().equals(port2.getName())) {
      return Reason.createFalseReason(PortEqualityHelper.DIFFERENT_NAMES);
    }

    // TODO Check equality of production/consumption rates (or expression if
    // depending on parameters)

    // Ports are considered equal
    return Reason.createTrueReason();
  }

}
