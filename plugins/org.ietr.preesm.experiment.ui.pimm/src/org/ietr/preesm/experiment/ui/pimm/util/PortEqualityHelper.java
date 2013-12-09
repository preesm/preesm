/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
package org.ietr.preesm.experiment.ui.pimm.util;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.impl.Reason;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.Port;

/**
 * This class is used to test the equality of ports a return the reason why they
 * are not equal.
 * 
 * @author kdesnos
 * 
 */
public class PortEqualityHelper {
	/**
	 * Ports have different classes (e.g. InputPort and OutputPort)
	 */
	public final static String DIFFERENT_CLASSES = "Different port classes";

	/**
	 * Ports have different name
	 */
	public final static String DIFFERENT_NAMES = "Different port names";

	/**
	 * One of the two ports is Null
	 */
	public final static String NULL_PORT = "A port is null";

	/**
	 * Builds and return a {@link Map} of equivalent {@link Port}s and, if
	 * needed, an associated {@link IReason} for their inequality. <br>
	 * <br>
	 * Two ports are equivalent if they have the same class and name but may
	 * have different rates. The returned {@link Map}is structured as the
	 * following example:
	 * <table border>
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
	 * Ports of vertex1 always are the key of the Entry and Ports of vertex2
	 * always are the value.
	 * 
	 * 
	 * @param vertex1
	 *            First {@link AbstractActor} whose {@link Port}s are compared.
	 * @param vertex2
	 *            Second {@link AbstractActor} whose {@link Port}s are compared.
	 * @return the {@link Map} of equivalent {@link Port}s
	 */
	public static Map<SimpleEntry<Port, Port>, IReason> buildEquivalentPortsMap(
			AbstractActor vertex1, AbstractActor vertex2) {
		Map<SimpleEntry<Port, Port>, IReason> result = new HashMap<SimpleEntry<Port, Port>, IReason>();

		comparePortLists(vertex1.getConfigInputPorts(),
				vertex2.getConfigInputPorts(), result);
		comparePortLists(vertex1.getConfigOutputPorts(),
				vertex2.getConfigOutputPorts(), result);
		comparePortLists(vertex1.getDataInputPorts(), vertex2.getDataInputPorts(),
				result);
		comparePortLists(vertex1.getDataOutputPorts(), vertex2.getDataOutputPorts(),
				result);

		return result;
	}

	protected static <T extends Port> void comparePortLists(EList<T> ports1,
			EList<T> ports2, Map<SimpleEntry<Port, Port>, IReason> result) {
		// Maintain a list of input port of vertex2 whose equivalent has not
		// been
		// found yet
		List<Port> noEquivalentFound = new ArrayList<Port>(ports2);

		// Scan ports of vertex1 looking for an equivalent
		for (Port p1 : ports1) {
			Port equivalent = null;
			for (Port p2 : noEquivalentFound) {
				if (comparePorts(p1, p2).toBoolean()) {
					equivalent = p2;
					break;
				}
			}
			noEquivalentFound.remove(equivalent);
			result.put(new SimpleEntry<>(p1, equivalent),
					comparePorts(p1, equivalent));
		}

		// Add of vertex2 that have no equivalents
		for (Port p2 : noEquivalentFound) {
			result.put(new SimpleEntry<>((Port) null, p2),
					comparePorts((Port) null, p2));
		}
	}

	/**
	 * Check if two ports are equal. If not, the reason for their inequality is
	 * returned. <br>
	 * <br>
	 * NB: Incoming and Outgoing {@link Fifo}s are not considered when comparing
	 * {@link Port}. Use {@link EcoreUtil#EqualityHelper} for this type of
	 * comparison.
	 * 
	 * @param port1
	 *            the first {@link Port} to compare
	 * @param port2
	 *            the second {@link Port} to compare
	 * @return an {@link IReason} for the equality or inequality
	 */
	public static IReason comparePorts(Port port1, Port port2) {

		if (port1 == port2) {
			return Reason.createTrueReason();
		}

		if (port1 == null || port2 == null) {
			return Reason.createFalseReason(NULL_PORT);
		}

		// Check if the two ports have the same class
		if (port1.eClass() != port2.eClass()) {
			// otherwise, return false
			return Reason.createFalseReason(DIFFERENT_CLASSES);
		}

		// Check if the name are identical
		if (!port1.getName().equals(port2.getName())) {
			return Reason.createFalseReason(DIFFERENT_NAMES);
		}

		// TODO Check equality of production/consumption rates (or expression if
		// depending on parameters)

		// Ports are considered equal
		return Reason.createTrueReason();
	}

}
