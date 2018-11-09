/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2011)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011)
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
package org.ietr.dftools.workflow.implement;

import java.util.Map;
import org.ietr.dftools.workflow.WorkflowException;

/**
 * Abstract scenario that can be implemented by a plugin wanting workflow execution capabilities. The prototype of the
 * scenario element is set in the plugin extension.
 *
 * @author mpelcat
 */
public abstract class AbstractScenarioImplementation extends AbstractWorkflowNodeImplementation {

  /**
   * Returns the preferred prototype for the node in a workflow. Useful to give user information in the workflow
   *
   * @return the string
   */
  @Override
  public final String displayPrototype() {
    return super.displayPrototype();
  }

  /**
   * The workflow scenario element must have an initialize method that is called by the workflow to generate the
   * scenario outputs (for example, an algorithm and an architecture).
   *
   * @param path
   *          the path
   * @return the map
   * @throws WorkflowException
   *           the workflow exception
   */
  public abstract Map<String, Object> extractData(String path);
}
