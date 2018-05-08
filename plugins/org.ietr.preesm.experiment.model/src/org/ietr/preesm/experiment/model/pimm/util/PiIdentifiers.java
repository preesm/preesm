/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
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
package org.ietr.preesm.experiment.model.pimm.util;

// TODO: Auto-generated Javadoc
/**
 * This class provide identifiers shared by all PiMM entities.
 */
public class PiIdentifiers {

  /** The Constant TYPE. */
  // SHARED IDENTIFIERS BETWEEN SEVERAL PIMM ELEMENTS
  private static final String TYPE = "type";

  /** The Constant TARGET_PORT. */
  private static final String TARGET_PORT = "targetport";

  /** The Constant SOURCE_PORT. */
  private static final String SOURCE_PORT = "sourceport";

  /** The Constant TARGET. */
  private static final String TARGET = "target";

  /** The Constant SOURCE. */
  private static final String SOURCE = "source";

  /** The Constant EXPR. */
  private static final String EXPR = "expr";

  /** The Constant KIND. */
  private static final String KIND = "kind";

  /** The Constant NAME. */
  private static final String NAME = "name";

  /** The Constant ID. */
  private static final String ID = "id";

  /** The Constant GRAPH. */
  // GRAPHS
  public static final String GRAPH = "graph";

  /** The Constant GRAPH_NAME. */
  public static final String GRAPH_NAME = PiIdentifiers.NAME;

  /** The Constant GRAPH_EDGE_DEFAULT. */
  public static final String GRAPH_EDGE_DEFAULT = "edgedefault";

  /** The Constant GRAPH_DIRECTED. */
  public static final String GRAPH_DIRECTED = "directed";

  /** The Constant DATA. */
  // DATA
  public static final String DATA = "data";

  /** The Constant DATA_KEY. */
  public static final String DATA_KEY = "key";

  /** The Constant NODE. */
  // NODES
  public static final String NODE = "node";

  /** The Constant NODE_KIND. */
  public static final String NODE_KIND = PiIdentifiers.KIND;

  /** The Constant ACTOR. */
  // ACTORS
  public static final String ACTOR = "actor";

  /** The Constant ACTOR_NAME. */
  public static final String ACTOR_NAME = PiIdentifiers.ID;

  /** The Constant ACTOR_MEMORY_SCRIPT. */
  public static final String ACTOR_MEMORY_SCRIPT = "memoryScript";

  /** The Constant BROADCAST. */
  // SPECIAL ACTORS
  public static final String BROADCAST = "broadcast";

  /** The Constant FORK. */
  public static final String FORK = "fork";

  /** The Constant JOIN. */
  public static final String JOIN = "join";

  /** The Constant ROUND_BUFFER. */
  public static final String ROUND_BUFFER = "roundbuffer";

  /** The Constant DATA_INPUT_INTERFACE_NAME. */
  public static final String DATA_INPUT_INTERFACE_NAME = PiIdentifiers.ID;

  /** The Constant DATA_OUTPUT_INTERFACE_NAME. */
  public static final String DATA_OUTPUT_INTERFACE_NAME = PiIdentifiers.ID;

  /** The Constant PARAMETER. */
  // PARAMETERS
  public static final String PARAMETER = "param";

  /** The Constant PARAMETER_NAME. */
  public static final String PARAMETER_NAME = PiIdentifiers.ID;

  /** The Constant PARAMETER_EXPRESSION. */
  public static final String PARAMETER_EXPRESSION = PiIdentifiers.EXPR;

  /** The Constant CONFIGURATION_INPUT_INTERFACE_NAME. */
  public static final String CONFIGURATION_INPUT_INTERFACE_NAME = PiIdentifiers.ID;

  /** The Constant CONFIGURATION_OUTPUT_INTERFACE_NAME. */
  public static final String CONFIGURATION_OUTPUT_INTERFACE_NAME = PiIdentifiers.ID;

  /** The Constant REFINEMENT. */
  // REFINEMENTS
  public static final String REFINEMENT = "graph_desc";

  /** The Constant REFINEMENT_LOOP. */
  public static final String REFINEMENT_LOOP = "loop";

  /** The Constant REFINEMENT_INIT. */
  public static final String REFINEMENT_INIT = "init";

  /** The Constant REFINEMENT_FUNCTION_PROTOTYPE_NAME. */
  public static final String REFINEMENT_FUNCTION_PROTOTYPE_NAME = PiIdentifiers.NAME;

  /** The Constant REFINEMENT_PARAMETER. */
  public static final String REFINEMENT_PARAMETER = "param";

  /** The Constant REFINEMENT_PARAMETER_NAME. */
  public static final String REFINEMENT_PARAMETER_NAME = PiIdentifiers.NAME;

  /** The Constant REFINEMENT_PARAMETER_TYPE. */
  public static final String REFINEMENT_PARAMETER_TYPE = PiIdentifiers.TYPE;

  /** The Constant REFINEMENT_PARAMETER_DIRECTION. */
  public static final String REFINEMENT_PARAMETER_DIRECTION = "direction";

  /** The Constant REFINEMENT_PARAMETER_IS_CONFIG. */
  public static final String REFINEMENT_PARAMETER_IS_CONFIG = "isConfig";

  /** The Constant PORT. */
  // PORTS
  public static final String PORT = "port";

  /** The Constant PORT_NAME. */
  public static final String PORT_NAME = PiIdentifiers.NAME;

  /** The Constant PORT_KIND. */
  public static final String PORT_KIND = PiIdentifiers.KIND;

  /** The Constant PORT_EXPRESSION. */
  public static final String PORT_EXPRESSION = PiIdentifiers.EXPR;

  /** The Constant PORT_MEMORY_ANNOTATION. */
  public static final String PORT_MEMORY_ANNOTATION = "annotation";

  /** The Constant EDGE. */
  // EDGES
  public static final String EDGE = "edge";

  /** The Constant EDGE_KIND. */
  public static final String EDGE_KIND = PiIdentifiers.KIND;

  /** The Constant FIFO. */
  // FIFOS
  public static final String FIFO = "fifo";

  /** The Constant FIFO_TYPE. */
  public static final String FIFO_TYPE = PiIdentifiers.TYPE;

  /** The Constant FIFO_SOURCE. */
  public static final String FIFO_SOURCE = PiIdentifiers.SOURCE;

  /** The Constant FIFO_TARGET. */
  public static final String FIFO_TARGET = PiIdentifiers.TARGET;

  /** The Constant FIFO_SOURCE_PORT. */
  public static final String FIFO_SOURCE_PORT = PiIdentifiers.SOURCE_PORT;

  /** The Constant FIFO_TARGET_PORT. */
  public static final String FIFO_TARGET_PORT = PiIdentifiers.TARGET_PORT;

  /** The Constant DELAY. */
  // DELAY
  public static final String DELAY = "delay";

  /** The Constant DELAY_NAME. */
  public static final String DELAY_NAME = PiIdentifiers.ID;

  /** The Constant DELAY_PERSISTENCE_LEVEL. */
  public static final String DELAY_PERSISTENCE_LEVEL = "level";

  /** The Constant DELAY_EXPRESSION. */
  public static final String DELAY_EXPRESSION = PiIdentifiers.EXPR;

  /** The Constant DELAY_REFINEMENT_INIT */
  public static final String DELAY_REFINEMENT = PiIdentifiers.REFINEMENT;

  /** The Constant DELAY_SETTER */
  public static final String DELAY_SETTER = "setter";

  /** The Constant DELAY_GETTER */
  public static final String DELAY_GETTER = "getter";

  /** The Constant DEPENDENCY. */
  // DEPENDENCIES
  public static final String DEPENDENCY = "dependency";

  /** The Constant DEPENDENCY_SOURCE. */
  public static final String DEPENDENCY_SOURCE = PiIdentifiers.SOURCE;

  /** The Constant DEPENDENCY_TARGET. */
  public static final String DEPENDENCY_TARGET = PiIdentifiers.TARGET;

  /** The Constant DEPENDENCY_SOURCE_PORT. */
  public static final String DEPENDENCY_SOURCE_PORT = PiIdentifiers.SOURCE_PORT;

  /** The Constant DEPENDENCY_TARGET_PORT. */
  public static final String DEPENDENCY_TARGET_PORT = PiIdentifiers.TARGET_PORT;
}
