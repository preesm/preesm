/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2020)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
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

package org.preesm.algorithm.mparameters;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.memory.allocation.BasicAllocator;
import org.preesm.algorithm.memory.allocation.MemoryAllocator;
import org.preesm.algorithm.memory.exclusiongraph.MemoryExclusionGraph;
import org.preesm.algorithm.mparameters.DSEpointIR.ParetoPointState;
import org.preesm.algorithm.pisdf.autodelays.IterationDelayedEvaluator;
import org.preesm.algorithm.pisdf.pimm2srdag.StaticPiMM2MapperDAGVisitor;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.algorithm.synthesis.evaluation.energy.SimpleEnergyCost;
import org.preesm.algorithm.synthesis.evaluation.energy.SimpleEnergyEvaluation;
import org.preesm.algorithm.synthesis.evaluation.latency.LatencyCost;
import org.preesm.algorithm.synthesis.evaluation.latency.SimpleLatencyEvaluation;
import org.preesm.algorithm.synthesis.schedule.ScheduleOrderManager;
import org.preesm.algorithm.synthesis.schedule.algos.IScheduler;
import org.preesm.algorithm.synthesis.schedule.algos.PeriodicScheduler;
import org.preesm.algorithm.synthesis.schedule.algos.PreesmSchedulingException;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.MalleableParameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.statictools.PiSDFToSingleRate;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

@PreesmTask(id = "pisdf-mparams.pareto", name = "Pareto graph generator", shortDescription = "",

    description = "",

    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class),
        @Port(name = "architecture", type = Design.class) },

    parameters = {
        @org.preesm.commons.doc.annotations.Parameter(name = ParetoGraphTask.DEFAULT_LOG_NAME,
            description = "Export all explored points with associated metrics in a csv file.",
            values = {
                @Value(name = ParetoGraphTask.DEFAULT_LOG_VALUE, effect = "Path relative to the project root.") }),
        @org.preesm.commons.doc.annotations.Parameter(name = ParetoGraphTask.DEFAULT_LOG_NAME,
            description = "Export all explored points with associated metrics in a csv file.", values = {
                @Value(name = ParetoGraphTask.DEFAULT_LOG_VALUE, effect = "Path relative to the project root.") }) })

public class ParetoGraphTask extends AbstractTaskImplementation {

  public static final String DEFAULT_LOG_VALUE = "/Code/generated/";

  public static final String DEFAULT_LOG_NAME = "1. Log path";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);
    final PiGraph graph = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
    final MapperDAG dag = (MapperDAG) inputs.get(AbstractWorkflowNodeImplementation.KEY_SDF_DAG);

    final List<Comparator<DSEpointIR>> listComparators = new ArrayList<>();
    listComparators.add(new DSEpointIR.ThroughputMaxComparator());
    listComparators.add(new DSEpointIR.LatencyMinComparator());
    listComparators.add(new DSEpointIR.PowerMinComparator());
    listComparators.add(new DSEpointIR.MemoryMinComparator());

    final Map<String, Object> output = new LinkedHashMap<>();

    List<MalleableParameter> mparams = graph.getAllParameters().stream().filter(x -> x instanceof MalleableParameter)
        .map(x -> (MalleableParameter) x).collect(Collectors.toList());

    if (mparams.isEmpty()) {
      PreesmLogger.getLogger().log(Level.WARNING, "No malleable pararemters were found in the graph");
      return output;
    }

    if (mparams.stream().anyMatch(x -> !x.isLocallyStatic())) {
      throw new PreesmRuntimeException(
          "One or more malleable parameter is not locally static, this is not allowed in this task.");
    }

    List<MalleableParameterIR> mparamsIR = mparams.stream().map(x -> new MalleableParameterIR(x))
        .collect(Collectors.toList());

    long nbCombinations = 1;
    for (MalleableParameterIR mpir : mparamsIR) {
      nbCombinations *= mpir.nbValues;
    }
    PreesmLogger.getLogger().log(Level.INFO, "The number of parameter combinations is: " + nbCombinations);

    List<DSEpointIR> listParetoOptimum = null;

    // final Map<DSEpointIR, List<Integer>> listOptimumConfigs = new HashMap<>(); // maybe useless because the config is
                                                                               // already in DSEpointIR

    StringBuilder logDSEpoints = new StringBuilder();
    StringBuilder logParetoOptimum = new StringBuilder();

    PreesmLogger.getLogger().log(Level.FINE, "Start of the Pareto graph computation");

    listParetoOptimum = paretoDSE(scenario, graph, architecture, dag, mparamsIR, listComparators, logDSEpoints);

    logCsvContentMparams(logParetoOptimum, mparamsIR, listParetoOptimum);

    final String logPath = parameters.get(DEFAULT_LOG_NAME);
    logCsvFile(logDSEpoints, mparamsIR, workflow, scenario, logPath, "_all_points_log.csv");
    logCsvFile(logParetoOptimum, mparamsIR, workflow, scenario, logPath, "_pareto_set_log.csv");

    return output;
  }

  protected static List<DSEpointIR> paretoDSE(final Scenario scenario, final PiGraph graph, final Design architecture,
      final MapperDAG dag, final List<MalleableParameterIR> mparamsIR,
      final List<Comparator<DSEpointIR>> listComparator, final StringBuilder logDSEpoints) {

    final List<DSEpointIR> paretoPoint = new ArrayList<DSEpointIR>();
    final ParameterCombinationExplorer pce = new ParameterCombinationExplorer(mparamsIR, scenario);
    int index = 0;
    ParetoPointState code;
    while (pce.setNext()) {
      index++;
      PreesmLogger.getLogger().log(Level.FINE, "==> Testing combination: " + index);

      final PeriodicScheduler scheduler = new PeriodicScheduler();
      final DSEpointIR dsep = runConfiguration(scenario, graph, architecture, scheduler, dag);
      logCsvContentMparams(logDSEpoints, mparamsIR, dsep); // résultat ensemble des points calculés
      code = ParetoFrontierUpdate(paretoPoint, dsep, listComparator); // on update la listes de pareto optimums
      PreesmLogger.getLogger().log(Level.FINE,
          "code de retour du DSE point : " + code.toString() + " du point : " + dsep);
      // algo test point DSE

    }

    return paretoPoint;

  }

  protected void logCsvFile(final StringBuilder logDSEpoints, final List<MalleableParameterIR> mparamsIR,
      final Workflow workflow, final Scenario scenario, final String logPath, final String filename) {
    final StringBuilder header = new StringBuilder();
    for (MalleableParameterIR mpir : mparamsIR) {
      header.append(mpir.mp.getName() + ";");
    }
    header.append(DSEpointIR.CSV_HEADER_STRING + "\n");

    // Get the root of the workspace
    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
    final IWorkspaceRoot root = workspace.getRoot();
    // Get the project
    final String projectName = workflow.getProjectName();
    final IProject project = root.getProject(projectName);

    // Get a complete valid path with all folders existing
    String exportAbsolutePath = project.getLocation() + logPath;
    final File parent = new File(exportAbsolutePath);
    parent.mkdirs();

    final String fileName = scenario.getScenarioName() + filename;
    final File file = new File(parent, fileName);
    try (final FileWriter fw = new FileWriter(file, true)) {
      fw.write(header.toString());
      fw.write(logDSEpoints.toString());
    } catch (IOException e) {
      PreesmLogger.getLogger().log(Level.SEVERE,
          "Unhable to write the DSE task log in file:" + exportAbsolutePath + fileName);
    }

  }

  protected static void logCsvContentMparams(final StringBuilder logDSEpoints,
      final List<MalleableParameterIR> mparamsIR, final DSEpointIR point) {
    for (MalleableParameterIR mpir : mparamsIR) {
      logDSEpoints.append(mpir.mp.getExpression().evaluate() + ";");
    }
    logDSEpoints.append(point.toCsvContentString() + "\n");
  }

  protected static void logCsvContentMparams(final StringBuilder logDSEpoints,
      final List<MalleableParameterIR> mparamsIR, final List<DSEpointIR> listPoint) {
    for (DSEpointIR p : listPoint) {
      logCsvContentMparams(logDSEpoints, mparamsIR, p);
    }
  }

  protected static ParetoPointState ParetoFrontierUpdate(List<DSEpointIR> listPareto, final DSEpointIR dsep,
      List<Comparator<DSEpointIR>> listComparator) {
    ParetoPointState returnCode = ParetoPointState.newTradeoff;
    boolean allMetricsGreaterOrEqual;
    boolean allMetricsLowerOrEqual;
    if (!listPareto.isEmpty()) {
      for (DSEpointIR d : listPareto) {
        allMetricsGreaterOrEqual = true;
        allMetricsLowerOrEqual = true;
        for (Comparator<DSEpointIR> c : listComparator) {
          if ((allMetricsGreaterOrEqual == true) && (c.compare(dsep, d) < 0)) {
            // if allMetricsGreaterOrEqual already = false don't need to execute the true statement
            // if returnCode == perfectTradeOff it mean that dsep will
            allMetricsGreaterOrEqual = false;
          } else if ((allMetricsLowerOrEqual == true) && (c.compare(dsep, d) > 0)) {
            allMetricsLowerOrEqual = false;
          }
        }
        if (allMetricsGreaterOrEqual && allMetricsLowerOrEqual) {
          // dsep and d have all their metrics equals (d<=dsep && d=>dsep)
          listPareto.add(dsep);
          return ParetoPointState.overlapPoint;
        }
        if (!allMetricsLowerOrEqual && allMetricsGreaterOrEqual) {
          // all the metrics of dsep are greater or equals than the metrics of d
          return ParetoPointState.notRelevantTradeoff;
        }
        if (allMetricsLowerOrEqual && (allMetricsGreaterOrEqual == false)) {
          // all the metrics of dsep are lower or equals than the metrics of d
          listPareto.remove(d);
          returnCode = ParetoPointState.perfectTradeoff;
        }

      }
    } 
    if (returnCode == ParetoPointState.newTradeoff || returnCode == ParetoPointState.perfectTradeoff) {
      listPareto.add(dsep);
    }
    return returnCode;
  }

  protected static DSEpointIR runConfiguration(final Scenario scenario, final PiGraph graph, final Design architecture,
      final IScheduler scheduler, final MapperDAG mDAG) {
    final Level backupLevel = PreesmLogger.getLogger().getLevel();
    PreesmLogger.getLogger().setLevel(Level.SEVERE);

    // copy graph since flatten transfo has side effects (on parameters)
    final int iterationDelay = IterationDelayedEvaluator.computeLatency(graph);

    final PiGraph dag = PiSDFToSingleRate.compute(graph, BRVMethod.LCM);

    SynthesisResult scheduleAndMap = null;
    try {
      scheduleAndMap = scheduler.scheduleAndMap(dag, architecture, scenario);
    } catch (PreesmSchedulingException e) {
      // put back all messages
      PreesmLogger.getLogger().setLevel(backupLevel);
      PreesmLogger.getLogger().log(Level.WARNING, "Scheduling was impossible.", e);
      return new DSEpointIR(Long.MAX_VALUE, iterationDelay, Long.MAX_VALUE, Long.MAX_VALUE, false);
    }

    // use implementation evaluation of PeriodicScheduler instead?
    final ScheduleOrderManager scheduleOM = new ScheduleOrderManager(dag, scheduleAndMap.schedule);
    final LatencyCost evaluateLatency = new SimpleLatencyEvaluation().evaluate(dag, architecture, scenario,
        scheduleAndMap.mapping, scheduleOM);
    final long durationII = evaluateLatency.getValue();
    final SimpleEnergyCost evaluateEnergy = new SimpleEnergyEvaluation().evaluate(dag, architecture, scenario,
        scheduleAndMap.mapping, scheduleOM);
    final long energy = evaluateEnergy.getValue();

    // computation of the memory footprint
    // final SimpleMemoryAllocation simpleAlloc = new SimpleMemoryAllocation();
    // final Allocation alloc = simpleAlloc.allocateMemory(graph, architecture, scenario, scheduleAndMap.schedule,
    // scheduleAndMap.mapping);

    final MemoryExclusionGraph memEx = new MemoryExclusionGraph(scenario);
    MapperDAG d = StaticPiMM2MapperDAGVisitor.convert(graph, architecture, scenario);
    memEx.buildGraph(d);
    final MemoryAllocator alloc = new BasicAllocator(memEx);
    alloc.setAlignment(0);
    alloc.allocate();

    final Long memory = alloc.getMemorySize();

    // put back all messages
    PreesmLogger.getLogger().setLevel(backupLevel);
    return new DSEpointIR(energy, iterationDelay, durationII, memory, true);
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(DEFAULT_LOG_NAME, DEFAULT_LOG_VALUE);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Computing the list of optimal trade-offs of moldable parameters";
  }

}
