/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
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
package org.ietr.preesm.memory.script;

import java.util.LinkedHashMap;
import java.util.Map;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.preesm.memory.allocation.AbstractMemoryAllocatorTask;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractMemoryScriptTask.
 */
public abstract class AbstractMemoryScriptTask extends AbstractTaskImplementation {

  /** The Constant PARAM_VERBOSE. */
  public static final String PARAM_VERBOSE = "Verbose";

  /** The Constant VALUE_TRUE. */
  public static final String VALUE_TRUE = "True";

  /** The Constant VALUE_FALSE. */
  public static final String VALUE_FALSE = "False";

  /** The Constant PARAM_LOG. */
  public static final String PARAM_LOG = "Log Path";

  /** The Constant VALUE_LOG. */
  public static final String VALUE_LOG = "log_memoryScripts";

  /** The Constant PARAM_CHECK. */
  public static final String PARAM_CHECK = "Check";

  /** The Constant VALUE_CHECK_NONE. */
  public static final String VALUE_CHECK_NONE = "None";

  /** The Constant VALUE_CHECK_FAST. */
  public static final String VALUE_CHECK_FAST = "Fast";

  /** The Constant VALUE_CHECK_THOROUGH. */
  public static final String VALUE_CHECK_THOROUGH = "Thorough";

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#getDefaultParameters()
   */
  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> param = new LinkedHashMap<>();
    param.put(AbstractMemoryScriptTask.PARAM_VERBOSE,
        "? C {" + AbstractMemoryScriptTask.VALUE_TRUE + ", " + AbstractMemoryScriptTask.VALUE_FALSE + "}");
    param.put(AbstractMemoryScriptTask.PARAM_CHECK, "? C {" + AbstractMemoryScriptTask.VALUE_CHECK_NONE + ", "
        + AbstractMemoryScriptTask.VALUE_CHECK_FAST + ", " + AbstractMemoryScriptTask.VALUE_CHECK_THOROUGH + "}");
    param.put(AbstractMemoryAllocatorTask.PARAM_ALIGNMENT, AbstractMemoryAllocatorTask.VALUE_ALIGNEMENT_DEFAULT);
    param.put(AbstractMemoryScriptTask.PARAM_LOG, AbstractMemoryScriptTask.VALUE_LOG);

    return param;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation#monitorMessage()
   */
  @Override
  public String monitorMessage() {
    return "Running Memory Optimization Scripts.";
  }

}
