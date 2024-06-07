/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2009 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2013)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2009 - 2012)
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
package org.preesm.algorithm.mapper.graphtransfo;

import org.preesm.algorithm.model.dag.DAGEdge;

/**
 * This class contains property bean names used to exchange vertex properties between the mapper and the code generation
 * plugins.
 *
 * @author mpelcat
 */
public final class ImplementationPropertyNames {

  private ImplementationPropertyNames() {
    // Forbids instantiation
  }

  // Graph properties
  /**
   * See AbcType for available ABC types.
   */
  public static final String GRAPH_ABC_REFERENCE_TYPE = "AbcReferenceType";

  /**
   * See EdgeSchedType for available edge scheduling types.
   */
  public static final String GRAPH_EDGE_SCHED_REFERENCE_TYPE = "EdgeSchedReferenceType";

  /**
   * SDFGraph object containing the reference graph for this dag graph.
   */
  public static final String GRAPH_SDF_REFERENCE_GRAPH = "SdfReferenceGraph";

  // Vertex properties
  /**
   * See {@link VertexType} for available vertex types.
   */
  public static final String VERTEX_VERTEX_TYPE = "vertexType";

  /**
   * Operator on which the vertex is mapped.
   */
  public static final String VERTEX_OPERATOR = "Operator";

  /**
   * Operator on which the vertex can be mapped.
   */
  public static final String VERTEX_AVAILABLE_OPERATORS = "availableOperators";

  /**
   * OperatorDefinition of the vertex operator.
   */
  public static final String VERTEX_OPERATOR_DEF = "OperatorDef";

  /**
   * integer equal to the total order of the vertex.
   */
  public static final String VERTEX_SCHEDULING_ORDER = "schedulingOrder";

  // Vertex of type task properties
  /**
   * integer equal to duration of the task.
   */
  public static final String TASK_DURATION = "duration";

  /**
   * Id of the original vertex with no hierarchy info.
   */
  public static final String VERTEX_ORIGINAL_VERTEX_ID = "originalId";

  /**
   * Starting Time of the Task in the simulation.
   */
  public static final String START_TIME = "TaskStartTime";

  // Vertex of type send or receive properties
  /** {@link DAGEdge} that corresponds to a Transfer Vertex. */
  public static final String SEND_RECEIVE_CORRESPONDING_DAG_EDGE = "correspondingDagEdge";

  /**
   * Route step transfering the data.
   */
  public static final String SEND_RECEIVE_ROUTE_STEP = "routeStep";

  /**
   * integer with the data size.
   */
  public static final String SEND_RECEIVE_DATA_SIZE = "dataSize";

  /**
   * String with the operator address.
   */
  public static final String SEND_RECEIVE_OPERATOR_ADDRESS = "Operator_address";

  /**
   * {@link OperatorDefinition} of the operator executing the send or receive.
   */
  public static final String SEND_RECEIVE_OPERATOR_DEF = "OperatorDef";

  // Vertex of type send properties
  /**
   * Name of the sender vertex.
   */
  public static final String SEND_SENDER_GRAPH_NAME = "senderGraphName";

  // Vertex of type receive properties
  /**
   * Name of the receiver vertex.
   */
  public static final String RECEIVE_RECEIVER_GRAPH_NAME = "receiverGraphName";

}
