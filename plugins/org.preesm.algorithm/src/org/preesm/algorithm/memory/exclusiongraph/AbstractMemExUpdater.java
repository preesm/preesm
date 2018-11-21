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
package org.preesm.algorithm.memory.exclusiongraph;

import org.preesm.workflow.implement.AbstractTaskImplementation;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractMemExUpdater.
 */
public abstract class AbstractMemExUpdater extends AbstractTaskImplementation {

  /** The Constant PARAM_VERBOSE. */
  public static final String PARAM_VERBOSE = "Verbose";

  /** The Constant PARAM_LIFETIME. */
  public static final String PARAM_LIFETIME = "Update with MemObject lifetime";

  /** The Constant PARAM_SUPPR_FORK_JOIN. */
  public static final String PARAM_SUPPR_FORK_JOIN = "Suppr Fork/Join";

  /** The Constant VALUE_TRUE_FALSE_DEFAULT. */
  public static final String VALUE_TRUE_FALSE_DEFAULT = "? C {True, False}";

  /** The Constant VALUE_TRUE. */
  public static final String VALUE_TRUE = "True";

  /** The Constant VALUE_FALSE. */
  public static final String VALUE_FALSE = "False";
}
