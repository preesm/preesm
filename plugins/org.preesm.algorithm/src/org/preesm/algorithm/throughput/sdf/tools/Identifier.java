/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Hamza Deroui [hamza.deroui@insa-rennes.fr] (2017)
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
package org.preesm.algorithm.throughput.sdf.tools;

/**
 * @author hderoui
 *
 *         Generates id's for graphs, actors, edges, and ports
 */
public abstract class Identifier {

  private Identifier() {
    // Forbids instantiation
  }

  private static int sdfGraphId   = 0;
  private static int inputPortId  = 0;
  private static int outputPortId = 0;
  private static int edgeId       = 0;

  /**
   * generates an Id for a graph
   *
   * @return id
   */
  public static String generateSDFGraphId() {
    final String id = "g" + sdfGraphId;
    sdfGraphId++;
    return id;
  }

  /**
   * generates an Id for an edge
   *
   * @return id
   */
  public static String generateEdgeId() {
    final String id = "e" + edgeId;
    edgeId++;
    return id;
  }

  /**
   * generates an Id for an input port
   *
   * @return id
   */
  public static String generateInputPortId() {
    final String id = "inP" + inputPortId;
    inputPortId++;
    return id;
  }

  /**
   * generates an Id for an output port
   *
   * @return id
   */
  public static String generateOutputPortId() {
    final String id = "OutP" + outputPortId;
    outputPortId++;
    return id;
  }

}
