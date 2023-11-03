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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.check.CheckerErrorLevel;
import org.preesm.model.pisdf.check.PiGraphConsistenceChecker;
import org.preesm.model.scenario.Scenario;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * Cluster Partitioner Task
 *
 * @author orenaud
 *
 */
@PreesmTask(id = "cluster-partitioner-DATA", name = "Cluster Partitioner",
    inputs = { @Port(name = "scenario", type = Scenario.class, description = "Scenario") },
    outputs = { @Port(name = "PiMM", type = PiGraph.class, description = "Output PiSDF graph") },
    parameters = {
        @Parameter(name = "Number of PEs in clusters",
            description = "The number of PEs in compute clusters. This information is used to balance actor firings"
                + " between coarse and fine-grained levels.",
            values = { @Value(name = "Fixed:=n", effect = "Where $$n\\in \\mathbb{N}^*$$.") }),
        @Parameter(name = "SCAPE mode",
            description = "choose the clustering mode : 1 = set of clustering config + only fit data parallelism,"
                + " 2 = set of clustering config + fit data & pip parallelism, 3 = best clustering config ",
            values = { @Value(name = "Fixed:=n", effect = "switch of clustering algorithm") }),
        @Parameter(name = "Non-cluster actor", description = "does not allow to group the actors entered in parameter",
            values = { @Value(name = "String", effect = "disable cluster") }) })
public class ClusterPartitionerDATATask extends AbstractTaskImplementation {

  public static final String NB_PE                   = "Number of PEs in compute clusters";
  public static final String DEFAULT_NB_PE           = "1";
  public static final String CLUSTERING_MODE_DEFAULT = "0";                                // SCAPE1
  public static final String CLUSTERING_PARAM        = "SCAPE mode";
  public static final String NON_CLUSTER_DEFAULT     = "";
  public static final String NON_CLUSTER_PARAM       = "Non-cluster actor";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {
    // Task inputs

    final Scenario scenario = (Scenario) inputs.get("scenario");
    final PiGraph inputGraph = scenario.getAlgorithm();
    // Parameters
    final String nbPE = parameters.get(NB_PE);
    final String modeStr = parameters.get(ClusterPartitionerDATATask.CLUSTERING_PARAM);
    final String nonClusterable = parameters.get(ClusterPartitionerDATATask.NON_CLUSTER_PARAM);
    final String[] nonClusterableListStr = nonClusterable.split("\\*");
    final List<AbstractActor> nonClusterableList = new LinkedList<>();
    for (final String element : nonClusterableListStr) {
      for (final AbstractActor a : scenario.getAlgorithm().getExecutableActors()) {
        if (a.getName().equals(element) && !nonClusterableList.contains(a)) {
          nonClusterableList.add(a);
        }
      }
    }
    Map<AbstractVertex, Long> brv = PiBRV.compute(inputGraph, BRVMethod.LCM);
    // Cluster input graph
    new ClusterPartitionerURC(scenario, Integer.parseInt(nbPE), brv, 0, nonClusterableList, Integer.decode(modeStr))
        .cluster();
    final PiGraph outputGraph = new ClusterPartitionerSRV(scenario, Integer.parseInt(nbPE), brv, 0, nonClusterableList,
        Integer.decode(modeStr)).cluster();
    final PiGraphConsistenceChecker pgcc = new PiGraphConsistenceChecker(CheckerErrorLevel.FATAL_ALL,
        CheckerErrorLevel.FATAL_ALL);
    pgcc.check(outputGraph);
    brv = PiBRV.compute(inputGraph, BRVMethod.LCM);
    PiBRV.printRV(brv);

    // Build output map
    final Map<String, Object> output = new HashMap<>();
    output.put("PiMM", outputGraph);

    return output;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> defaultParams = new LinkedHashMap<>();
    defaultParams.put(NB_PE, DEFAULT_NB_PE);
    // non cluster default
    defaultParams.put(NON_CLUSTER_PARAM, NON_CLUSTER_DEFAULT);
    defaultParams.put(CLUSTERING_PARAM, CLUSTERING_MODE_DEFAULT);
    return defaultParams;
  }

  @Override
  public String monitorMessage() {
    return "Starting Execution of Cluster Partitioner Focusing Data Parallelism (URC+SRV) Task";
  }

}
