package org.ietr.preesm.memory.exclusiongraph;

import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;

public abstract class AbstractMemExUpdater extends AbstractTaskImplementation {
	static final public String PARAM_VERBOSE = "Verbose";

	static final public String PARAM_LIFETIME = "Update with MemObject lifetime";

	static final public String PARAM_SUPPR_FORK_JOIN = "Suppr Fork/Join";
	static final public String VALUE_TRUE_FALSE_DEFAULT = "? C {True, False}";
	static final public String VALUE_TRUE = "True";
	static final public String VALUE_FALSE = "False";
}
