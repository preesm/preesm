/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020) :

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
package org.preesm.algorithm.clustering.partitioner;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.check.CheckerErrorLevel;
import org.preesm.model.pisdf.check.PiGraphConsistenceChecker;
import org.preesm.model.scenario.Scenario;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * Cluster Partitioner Task
 *
 * @author orenaud
 *
 */
@PreesmTask(id = "cluster-partitioner-PIP", name = "Cluster Partitioner PIP",
    inputs = {
      @Port(name = AbstractWorkflowNodeImplementation.KEY_SCENARIO, type = Scenario.class, description = "Scenario") },
    outputs = { @Port(name = AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, type = PiGraph.class,
        description = "Output PiSDF graph") })

public class ClusterPartitionerPIPTask extends ClusterPartitionerTask {

  public static final String NON_CLUSTER_PARAM   = "Non-cluster actor";
  public static final String NON_CLUSTER_DEFAULT = "";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {
    // Task inputs

    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final PiGraph inputGraph = scenario.getAlgorithm();

    // Parameters
    final int nbPE = scenario.getDesign().getProcessingElements().size();

    // Cluster input graph

    new ClusterPartitionerSEQ(inputGraph, scenario, nbPE).cluster();
    Map<AbstractVertex, Long> brv = PiBRV.compute(inputGraph, BRVMethod.LCM);
    final PiGraph outputGraph = new ClusterPartitionerLOOP(inputGraph, scenario, nbPE, brv, 0).cluster();
    final PiGraphConsistenceChecker pgcc = new PiGraphConsistenceChecker(CheckerErrorLevel.FATAL_ALL,
        CheckerErrorLevel.FATAL_ALL);
    pgcc.check(outputGraph);
    brv = PiBRV.compute(inputGraph, BRVMethod.LCM);
    PiBRV.printRV(brv);
    // Build output map
    final Map<String, Object> output = new HashMap<>();
    output.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, outputGraph);

    return output;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    return new LinkedHashMap<>();
  }

  @Override
  public String monitorMessage() {
    return "Starting Execution of Cluster Partitioner Focusing Pipeline Parallelism (Seq+Loop) Task";
  }

}
