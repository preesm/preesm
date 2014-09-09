package org.ietr.preesm.memory.script;

import java.util.HashMap;
import java.util.Map;

import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.preesm.memory.allocation.MemoryAllocatorTask;

public abstract class AbstractMemoryScriptTask extends AbstractTaskImplementation {

	public static final String PARAM_VERBOSE = "Verbose";
	public static final String VALUE_TRUE = "True";
	public static final String VALUE_FALSE = "False";

	public static final String PARAM_LOG = "Log Path";
	public static final String VALUE_LOG = "log_memoryScripts";

	public static final String PARAM_CHECK = "Check";
	public static final String VALUE_CHECK_NONE = "None";
	public static final String VALUE_CHECK_FAST = "Fast";
	public static final String VALUE_CHECK_THOROUGH = "Thorough";

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> param = new HashMap<String, String>();
		param.put(PARAM_VERBOSE, "? C {" + VALUE_TRUE + ", " + VALUE_FALSE
				+ "}");
		param.put(PARAM_CHECK, "? C {" + VALUE_CHECK_NONE + ", "
				+ VALUE_CHECK_FAST + ", " + VALUE_CHECK_THOROUGH + "}");
		param.put(MemoryAllocatorTask.PARAM_ALIGNMENT,
				MemoryAllocatorTask.VALUE_ALIGNEMENT_DEFAULT);
		param.put(PARAM_LOG, VALUE_LOG);

		return param;
	}

	@Override
	public String monitorMessage() {
		return "Running Memory Optimization Scripts.";
	}

}
