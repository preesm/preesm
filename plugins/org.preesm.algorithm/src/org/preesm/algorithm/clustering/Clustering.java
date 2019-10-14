/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2016 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Daniel Madroñal [daniel.madronal@upm.es] (2019)
 * Dylan Gageot [gageot.dylan@gmail.com] (2019)
 * Julien Hascoet [jhascoet@kalray.eu] (2016 - 2017)
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
@PreesmTask(id = "org.ietr.preesm.pisdfclustering", name = "PiSDF Clustering",

    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class) },
    outputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "schedules", type = Map.class) },
    description = "Workflow task responsible for clustering actors by following a specified algorithm.",
    parameters = {
        @Parameter(name = "Algorithm",
            description = "Specify which clustering algorithm to use. A clustering algorithm will decide which "
                + "actors to clusterize in order to get a specific configuration "
                + "(in terms of memory space and parallelism).",
            values = {
                @Value(name = "APGAN",
                    effect = "Acyclic Pairwise Grouping of Adjacent Nodes, an algorithm use to minimize memory space "
                        + "needed to implement the resulting cluster. It stops when no more actors can be clustered."),
                @Value(name = "Dummy",
                    effect = "Choose, without intelligence, the actors to be clustered. "
                        + "It stops when no more actors can be clustered."),
                @Value(name = "Random",
                    effect = "Choose randomly the actors to be clustered. It used the parameter \"Seed\". "
                        + "It stops when no more actors can be clustered."),
                @Value(name = "Parallel",
                    effect = "(Not stable) Identify branches in input graph. "
                        + "It stops when no more actors can be clustered.") }),
        @Parameter(name = "Seed",
            description = "Specify the seed that will feed the random number generator "
                + "for the random clustering algortihm",
            values = { @Value(name = "$$n\\in \\mathbb{N}^*$$", effect = "Seed for Random algorithm") }),
        @Parameter(name = "Optimization criteria",
            description = "Specify the criteria to optimize. If memory is choosen, some parallelizable "
                + "actors will be sequentialized to minimize memory space. On the other hand, if performance "
                + "is choosen, the algorithm will exploit every parallelism possibility.",
            values = { @Value(name = "Memory", effect = "Minimize memory space of resulting clusters"),
                @Value(name = "Performance", effect = "Maximize performance of resulting clusters") }) })
public class Clustering extends AbstractTaskImplementation {

  public static final String ALGORITHM_CHOICE   = "Algorithm";
  public static final String ALGORITHM_APGAN    = "APGAN";
  public static final String ALGORITHM_DUMMY    = "Dummy";
  public static final String ALGORITHM_RANDOM   = "Random";
  public static final String ALGORITHM_PARALLEL = "Parallel";
  public static final String DEFAULT_ALGORITHM  = ALGORITHM_APGAN;

  public static final String SEED_CHOICE  = "Seed";
  public static final String DEFAULT_SEED = "0";

  public static final String OPTIMIZATION_CHOICE      = "Optimization criteria";
  public static final String OPTIMIZATION_MEMORY      = "Memory";
  public static final String OPTIMIZATION_PERFORMANCE = "Performance";
  public static final String DEFAULT_OPTIMIZATION     = OPTIMIZATION_PERFORMANCE;

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
    String optimization = parameters.get(OPTIMIZATION_CHOICE);

    // Instantiate a ClusteringBuilder and process clustering
    ClusteringBuilder clusteringBuilder = new ClusteringBuilder(graph, scenario, algorithm, Long.parseLong(seed),
        optimization);
    Map<AbstractActor, Schedule> scheduleMapping = clusteringBuilder.processClustering();

    // Print information in console
    for (Entry<AbstractActor, Schedule> entry : scheduleMapping.entrySet()) {
      Schedule schedule = entry.getValue();
      // Printing
      String str = "Schedule for cluster " + entry.getKey().getName() + ":";
      PreesmLogger.getLogger().log(Level.INFO, str);
      str = schedule.shortPrint();
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
    defaultParams.put(OPTIMIZATION_CHOICE, DEFAULT_OPTIMIZATION);
    return defaultParams;
  }

  @Override
  public String monitorMessage() {
    return "PiSDF Clustering";
  }

}
