package org.ietr.preesm.experiment.model.pimm.util;

public class PiIdentifiers {
	// SHARED IDENTIFIERS BETWEEN SEVERAL PIMM ELEMENTS
	private static final String TYPE = "type";
	private static final String TARGET_PORT = "targetport";
	private static final String SOURCE_PORT = "sourceport";
	private static final String TARGET = "target";
	private static final String SOURCE = "source";
	private static final String EXPR = "expr";
	private static final String KIND = "kind";
	private static final String NAME = "name";
	private static final String ID = "id";
	// GRAPHS
	public static final String GRAPH = "graph";
	public static final String GRAPH_NAME = NAME;
	public static final String GRAPH_EDGE_DEFAULT = "edgedefault";
	public static final String GRAPH_DIRECTED = "directed";
	// DATA
	public static final String DATA = "data";
	public static final String DATA_KEY = "key";
	// NODES
	public static final String NODE = "node";
	public static final String NODE_KIND = KIND;
	// ACTORS
	public static final String ACTOR = "actor";
	public static final String ACTOR_NAME = ID;
	public static final String ACTOR_MEMORY_SCRIPT = "memoryScript";
	// SPECIAL ACTORS
	public static final String BROADCAST = "broadcast";
	public static final String FORK = "fork";
	public static final String JOIN = "join";
	public static final String ROUND_BUFFER = "roundbuffer";
	// DATA INPUT INTERFACES
	public static final String DATA_INPUT_INTERFACE = "src";
	public static final String DATA_INPUT_INTERFACE_NAME = ID;
	// DATA OUTPUT INTERFACES
	public static final String DATA_OUTPUT_INTERFACE = "snk";
	public static final String DATA_OUTPUT_INTERFACE_NAME = ID;
	// PARAMETERS
	public static final String PARAMETER = "param";
	public static final String PARAMETER_NAME = ID;
	public static final String PARAMETER_EXPRESSION = EXPR;
	// CONFIGURATION INPUT INTERFACES
	public static final String CONFIGURATION_INPUT_INTERFACE = "cfg_in_iface";
	public static final String CONFIGURATION_INPUT_INTERFACE_NAME = ID;
	// CONFIGURATION OUTPUT INTERFACES
	public static final String CONFIGURATION_OUTPUT_INTERFACE = "cfg_out_iface";
	public static final String CONFIGURATION_OUTPUT_INTERFACE_NAME = ID;
	// REFINEMENTS
	public static final String REFINEMENT = "graph_desc";
	public static final String REFINEMENT_LOOP = "loop";
	public static final String REFINEMENT_INIT = "init";
	public static final String REFINEMENT_FUNCTION_PROTOTYPE_NAME = NAME;
	public static final String REFINEMENT_PARAMETER = "param";
	public static final String REFINEMENT_PARAMETER_NAME = NAME;
	public static final String REFINEMENT_PARAMETER_TYPE = TYPE;
	public static final String REFINEMENT_PARAMETER_DIRECTION = "direction";
	public static final String REFINEMENT_PARAMETER_IS_CONFIG = "isConfig";
	// PORTS
	public static final String PORT = "port";
	public static final String PORT_NAME = NAME;
	public static final String PORT_KIND = KIND;
	public static final String PORT_EXPRESSION = EXPR;
	public static final String PORT_MEMORY_ANNOTATION = "annotation";
	// DATA INPUT PORTS
	public static final String DATA_INPUT_PORT = "input";
	// DATA OUTPUT PORTS
	public static final String DATA_OUTPUT_PORT = "output";
	// CONFIGURATION INPUT PORTS
	public static final String CONFIGURATION_INPUT_PORT = "cfg_input";
	// CONFIGURATION OUTPUT PORTS
	public static final String CONFIGURATION_OUPUT_PORT = "cfg_output";
	// EDGES
	public static final String EDGE = "edge";
	public static final String EDGE_KIND = KIND;
	// FIFOS
	public static final String FIFO = "fifo";
	public static final String FIFO_TYPE = TYPE;
	public static final String FIFO_SOURCE = SOURCE;
	public static final String FIFO_TARGET = TARGET;
	public static final String FIFO_SOURCE_PORT = SOURCE_PORT;
	public static final String FIFO_TARGET_PORT = TARGET_PORT;
	// DELAY
	public static final String DELAY = "delay";
	public static final String DELAY_EXPRESSION = EXPR;
	// DEPENDENCIES
	public static final String DEPENDENCY = "dependency";
	public static final String DEPENDENCY_SOURCE = SOURCE;
	public static final String DEPENDENCY_TARGET = TARGET;
	public static final String DEPENDENCY_SOURCE_PORT = SOURCE_PORT;
	public static final String DEPENDENCY_TARGET_PORT = TARGET_PORT;
}
