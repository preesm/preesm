/**
 * Copyright or © or Copr. Åbo Akademi University (2017 - 2019),
 * IETR/INSA - Rennes (2017 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Sudeep Kanur [skanur@abo.fi] (2017 - 2018)
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
package fi.abo.preesm.dataparallel

import fi.abo.preesm.dataparallel.operations.DataParallelCheckOperations
import fi.abo.preesm.dataparallel.pojo.RetimingInfo
import java.util.Map
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.xtend.lib.annotations.Accessors
import org.preesm.algorithm.model.sdf.SDFGraph
import org.preesm.commons.doc.annotations.DocumentedError
import org.preesm.commons.doc.annotations.Port
import org.preesm.commons.doc.annotations.PreesmTask
import org.preesm.commons.exceptions.PreesmException
import org.preesm.commons.exceptions.PreesmRuntimeException
import org.preesm.commons.logger.PreesmLogger
import org.preesm.workflow.elements.Workflow
import org.preesm.workflow.implement.AbstractTaskImplementation
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation

/**
 * Wrapper class that performs the data-parallel checks and transforms
 *
 * @author Sudeep Kanur
 */
@PreesmTask(id = "fi.abo.preesm.dataparallel.DataParallel", name = "Data-parallel Transformation",
    category = "Graph Transformation",

    inputs = #[@Port(type = SDFGraph, name = "SDF")],
    outputs = #[@Port(type = SDFGraph, name = "CySDF"), @Port(type = RetimingInfo, name = "Info")],

    shortDescription = "Detect whether an SDF graph is data-parallel and provide its data-parallel equivalent Single-Rate SDF and its re-timing information.",

    description = "An SDF graph is data-parallel when for each actor of the SDF graph, all of its instances can be executed at the same time. For instance, all strictly acyclic SDF graphs are data-parallel. This task increases the scope of this analysis to generic SDF graphs.

The task analyses an input SDF graph and reports if it is data-parallel by analysing each strongly connected component of the graph. If a strongly connected component requires a re-timing transformation for it to be data-parallel, then the transformation is carried out such that the corresponding strongly connected component in the output single-rate SDF is data-parallel. The re-timing transformation modifies the delays in the original SDF graph such that the final single-rate SDF output is data-parallel.

However, if a strongly connected component of the SDF is not data-parallel, then the plugin reports the actors of this strongly connected component. In this case, the strongly connected component at the output single-rate SDF graph is same as that of the single-rate transformation on the original SDF.

The data-structure INFO describes mapping of delays in original FIFO to delays in the transformed SDF. This non-trivial initialization of delays in FIFOs is represented using SDF-like graphs where FIFO initialization function is represented as an actor. The data-structure INFO is provided for sake of completion and is not being used by other plugins. The data-structure INFO can change in future based on the design of the initialization of the FIFOs.",

	documentedErrors = @DocumentedError(message = "DAGComputationBug", explanation = "There is a bug in implementation due to incorrect assumption. Report the bug by opening an issue and attaching the graph that caused it."),

    seeAlso = "**Implementation details**: Sudeep Kanur, Johan Lilius, and Johan Ersfolk. Detecting data-parallel synchronous dataflow graphs. Technical Report 1184, 2017."
    )
class DataParallel extends AbstractTaskImplementation {

	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val KEY_INFO = "Info"

	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val KEY_CySDF = "CySDF"

	/**
	 * Execute data-parallel plugin and re-timing transformation. Actual work is carried out
	 * by {@link DataParallelCheckOperations}
	 */
	override execute(Map<String, Object> inputs, Map<String, String> parameters, IProgressMonitor monitor, String nodeName, Workflow workflow) throws PreesmException {
		val sdf = inputs.get(AbstractWorkflowNodeImplementation.KEY_SDF_GRAPH) as SDFGraph
		// Check if sdf is schedulable
		if(!sdf.isSchedulable) {
			throw new PreesmRuntimeException("Graph " + sdf + " not schedulable")
		}

		val logger = PreesmLogger.getLogger

		val checker = new DataParallelCheckOperations(logger)
		sdf.accept(checker)

		return newLinkedHashMap(KEY_INFO -> checker.info,
						  KEY_CySDF -> checker.cyclicGraph
		)
	}

	/**
	 * No default parameters yet
	 */
	override getDefaultParameters() {
		return newLinkedHashMap
	}

	/**
	 * Default monitor message
	 */
	override monitorMessage() {
		return "Running Data-parallel checks and transformations"
	}

}
