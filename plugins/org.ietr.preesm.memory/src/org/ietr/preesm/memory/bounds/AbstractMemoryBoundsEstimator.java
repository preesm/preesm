package org.ietr.preesm.memory.bounds;

import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;

public abstract class AbstractMemoryBoundsEstimator extends AbstractTaskImplementation {

	static final public String PARAM_SOLVER = "Solver";
	static final public String VALUE_SOLVER_DEFAULT = "? C {Heuristic, Ostergard, Yamaguchi}";

	static final public String PARAM_VERBOSE = "Verbose";
	static final public String VALUE_VERBOSE_DEFAULT = "? C {True, False}";
	static final public String VALUE_VERBOSE_TRUE = "True";
	static final public String VALUE_VERBOSE_FALSE = "False";

}
