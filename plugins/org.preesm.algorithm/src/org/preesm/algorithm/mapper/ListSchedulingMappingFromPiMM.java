/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Daniel Madroñal [daniel.madronal@upm.es] (2019)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Jonathan Piat [jpiat@laas.fr] (2008 - 2011)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2017 - 2018)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2008 - 2012)
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
package org.preesm.algorithm.mapper;

import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.mapper.abc.impl.latency.LatencyAbc;
import org.preesm.algorithm.mapper.energyawareness.EnergyAwarenessProvider;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.model.dag.DirectedAcyclicGraph;
import org.preesm.algorithm.pisdf.pimm2srdag.StaticPiMM2MapperDAGVisitor;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 *
 */
@PreesmTask(id = "pisdf-mapper.list", name = "List Scheduling from PiSDF", category = "Schedulers",

    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "architecture", type = Design.class),
        @Port(name = "scenario", type = Scenario.class) },

    outputs = { @Port(name = "DAG", type = DirectedAcyclicGraph.class), @Port(name = "ABC", type = LatencyAbc.class) },

    parameters = { @Parameter(name = "edgeSchedType", values = { @Value(name = "Simple") }),
        @Parameter(name = "simulatorType", values = { @Value(name = "LooselyTimed") }),
        @Parameter(name = "Check", values = { @Value(name = "True") }),
        @Parameter(name = "Optimize synchronization", values = { @Value(name = "False") }),
        @Parameter(name = "balanceLoads", values = { @Value(name = "false") }),
        @Parameter(name = "EnergyAwareness",
            values = { @Value(name = "True", effect = "Turns on energy aware mapping/scheduling") }),
        @Parameter(name = "EnergyAwarenessFirstConfig",
            values = { @Value(name = "First", effect = "Takes as starting point the first valid combination of PEs"),
                @Value(name = "Middle", effect = "Takes as starting point half of the available PEs"),
                @Value(name = "Max", effect = "Takes as starting point all the available PEs"),
                @Value(name = "Random", effect = "Takes as starting point a random number of PEs") }),
        @Parameter(name = "EnergyAwarenessSearchType", values = {
            @Value(name = "Thorough",
                effect = "Analyzes PE combinations one by one until the performance objective is reached"),
            @Value(name = "Halves", effect = "Divides in halves the remaining available PEs and goes up/down depending"
                + " if the FPS reached are below/above the objective") })

    })
public class ListSchedulingMappingFromPiMM extends ListSchedulingMappingFromDAG {

  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {

    final PiGraph algorithm = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);
    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);

    String messageLogger = "";
    Map<String, Object> mapping = null;

    if (parameters.containsKey("EnergyAwareness") && parameters.get("EnergyAwareness").equalsIgnoreCase("true")) {
      messageLogger = "Energy-awareness enabled. This option will increase the mapping/scheduling "
          + "task so it may take a while";
      PreesmLogger.getLogger().log(Level.INFO, messageLogger);
      String firstConfig = "middle";
      String searchingMode = "halves";
      if (parameters.containsKey("EnergyAwarenessFirstConfig")) {
        firstConfig = parameters.get("EnergyAwarenessFirstConfig");
      }
      if (parameters.containsKey("EnergyAwarenessSearchType")) {
        searchingMode = parameters.get("EnergyAwarenessSearchType");
      }
      final EnergyAwarenessProvider provider = new EnergyAwarenessProvider(scenario, firstConfig, searchingMode);
      /** iterate **/
      while (true) {
        /** Get configuration **/
        provider.computeNextConfig();
        /** Update scenario mapping for the current configuration **/
        provider.updateScenario();
        /** Check if the configuration is valid **/
        if (provider.hasFinished()) {
          break;
        }
        /** Try the mapping */
        final MapperDAG dag = StaticPiMM2MapperDAGVisitor.convert(algorithm, architecture,
            provider.getScenarioMapping());
        inputs.put(AbstractWorkflowNodeImplementation.KEY_SDF_DAG, dag);
        inputs.put(AbstractWorkflowNodeImplementation.KEY_SCENARIO, provider.getScenarioMapping());
        mapping = super.execute(inputs, parameters, monitor, nodeName, workflow);
        /** Evaluate mapping **/
        provider.evaluateMapping(mapping);
      }
      mapping = provider.getFinalMapping();
    } else {
      final MapperDAG dag = StaticPiMM2MapperDAGVisitor.convert(algorithm, architecture, scenario);
      inputs.put(AbstractWorkflowNodeImplementation.KEY_SDF_DAG, dag);
      mapping = super.execute(inputs, parameters, monitor, nodeName, workflow);
    }
    return mapping;
  }

}
