/**
 * Copyright or © or Copr. IETR/INSA - Rennes (%%DATE%%) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Daniel Madroñal [daniel.madronal@upm.es] (2019)
 * %%AUTHORS%%
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
package org.preesm.algorithm.clustering;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 *
 *
 */
@PreesmTask(id = "org.ietr.preesm.pisdfclustering", name = "Clustering",

    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class) },
    outputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "schedules", type = Map.class) },
    description = "Workflow task responsible for clustering hierarchical actors.",
    parameters = {
        @Parameter(name = "Algorithm",
            values = { @Value(name = "APGAN", effect = ""), @Value(name = "Dummy", effect = ""),
                @Value(name = "Random", effect = ""), @Value(name = "Parallel", effect = "") }),
        @Parameter(name = "Seed",
            values = { @Value(name = "$$n\\in \\mathbb{N}^*$$", effect = "Seed for random generator") }) })
public class Clustering extends AbstractTaskImplementation {

  public static final String ALGORITHM_CHOICE  = "Algorithm";
  public static final String DEFAULT_ALGORITHM = "APGAN";

  public static final String SEED_CHOICE  = "Seed";
  public static final String DEFAULT_SEED = "0";

  public static final String PISDF_REFERENCE_ACTOR = "PiSDFActor";

  public static final String PISDF_ACTOR_IS_CLUSTER = "isCluster";

  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {
    // Retrieve inputs and parameters
    final PiGraph graph = (PiGraph) inputs.get("PiMM");
    final Scenario scenario = (Scenario) inputs.get("scenario");
    String algorithm = parameters.get(ALGORITHM_CHOICE);
    String seed = parameters.get(SEED_CHOICE);

    // Instantiate a ClusteringBuilder and process clustering
    ClusteringBuilder clusteringBuilder = new ClusteringBuilder(graph, scenario, algorithm, Long.parseLong(seed));
    Map<AbstractActor, Schedule> scheduleMapping = clusteringBuilder.processClustering();

    // Print information in console
    for (Entry<AbstractActor, Schedule> entry : scheduleMapping.entrySet()) {
      Schedule schedule = entry.getValue();
      // Printing
      String str = "Schedule for cluster " + entry.getKey().getName() + ":";
      PreesmLogger.getLogger().log(Level.INFO, str);
      str = schedule.shortPrint(false);
      PreesmLogger.getLogger().log(Level.INFO, str);
      str = "Estimated memory space needed: " + ClusteringHelper.getMemorySpaceNeededFor(schedule) + " bytes";
      PreesmLogger.getLogger().log(Level.INFO, str);
      str = "Estimated execution time: " + ClusteringHelper.getExecutionTimeOf(schedule, scenario);
      PreesmLogger.getLogger().log(Level.INFO, str);
    }

    // Output PiSDF and Schedule Mapping attachment
    Map<String, Object> outputs = new LinkedHashMap<>();
    outputs.put("PiMM", graph);
    outputs.put("schedules", scheduleMapping);

    return outputs;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    Map<String, String> defaultParams = new LinkedHashMap<>();
    defaultParams.put(ALGORITHM_CHOICE, DEFAULT_ALGORITHM);
    defaultParams.put(SEED_CHOICE, DEFAULT_SEED);
    return defaultParams;
  }

  @Override
  public String monitorMessage() {
    return "Clustering";
  }

}
