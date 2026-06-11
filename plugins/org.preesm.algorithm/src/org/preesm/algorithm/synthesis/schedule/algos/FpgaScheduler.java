package org.preesm.algorithm.synthesis.schedule.algos;

import org.chocosolver.solver.Model;
import org.preesm.algorithm.schedule.fpga.AbstractGenericFpgaFifoEvaluator.AnalysisResultFPGA;
import org.preesm.algorithm.schedule.fpga.FpgaAnalysis;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

public class FpgaScheduler implements IScheduler {

  public String fifoEvaluator;

  public FpgaScheduler(String fifoEvaluator) {
    super();
    this.fifoEvaluator = fifoEvaluator;
  }

  public SynthesisResult scheduleAndMap(final PiGraph piGraph, final Design slamDesign, final Scenario scenario) {

    // TODO vérifier que ça fait bien ce que je veux
    final AnalysisResultFPGA synthesisResults = FpgaAnalysis.checkAndAnalyzeAlgorithm(piGraph, scenario,
        this.fifoEvaluator);

    return synthesisResults;
  }

  public SynthesisResult scheduleWithTimings(final PiGraph piGraph, final Design slamDesign, final Scenario scenario) {

    return new SynthesisResult(null, null, null);
  }

  protected Model generateModel() {
    final Model model = new Model("schedulerModel");
    /*
     * final SortedMap<Integer, Task> tasks = createTasksFromAbsGraph(absGraph);
     * 
     * // other transient variables but we don't care of their values final IntVar[] finishTimeVars = new
     * IntVar[nbTasks];
     * 
     * final BoolVar[][] overlapVars = new BoolVar[nbTasks][nbTasks]; final BoolVar[][] overlapSymVars = new
     * BoolVar[nbTasks][nbTasks]; final BoolVar[][] samecoreSymVars = new BoolVar[nbTasks][nbTasks]; final BoolVar[][]
     * oversameSymVars = new BoolVar[nbTasks][nbTasks];
     * 
     * // start time and finish time constraints on each task for (final Task t : tasks.values()) { startTimeVars[t.id]
     * = model.intVar("s" + t.id, t.ns, t.xs, false); finishTimeVars[t.id] = model.intVar("f" + t.id, t.ns + t.load,
     * t.xs + t.load, false); model.arithm(finishTimeVars[t.id], "=", startTimeVars[t.id], "+", t.load).post(); }
     * 
     * long removedComputationMax = 0;
     * 
     * // all other constraints for (final Task t : tasks.values()) {
     * 
     * // start time and predecessors for (final Integer pred : t.predId) { final Task temp = tasks.get(pred);
     * model.arithm(finishTimeVars[temp.id], "<=", startTimeVars[t.id]).post(); }
     * 
     * // unique mapping model.sum(mapping[t.id], "=", 1).post();
     * 
     * // no overlapping if on same core for (final Task tt : tasks.values()) {
     * 
     * // is useful if Choco allocation of boolVar matrices, otherwise no variable //
     * model.addClauseFalse(oversameSymVars[t.id][tt.id]);
     * 
     * // if in the list of all precedences, we already know that there will be no overlap if ((t.id == tt.id) || (t.id
     * < tt.id && tt.allPredId.contains(t.id)) || (tt.id < t.id && t.allPredId.contains(tt.id))) { for (int i = 0; i <
     * nbCores; i++) { // is useful if Choco allocation of boolVar matrices, otherwise no variable //
     * model.addClauseFalse(samecoreVars[t.id][tt.id][i]); removedComputationMax++; } // is useful if Choco allocation
     * of boolVar matrices, otherwise no variable // model.addClauseFalse(samecoreSymVars[t.id][tt.id]); //
     * model.addClauseFalse(overlapSymVars[t.id][tt.id]); // model.addClauseFalse(overlapVars[t.id][tt.id]);
     * 
     * // we do it for the opposite if different if (t.id != tt.id) { for (int i = 0; i < nbCores; i++) { // is useful
     * if Choco allocation of boolVar matrices, otherwise no variable //
     * model.addClauseFalse(samecoreVars[tt.id][t.id][i]); removedComputationMax++; }
     * 
     * // is useful if Choco allocation of boolVar matrices, otherwise no variable //
     * model.addClauseFalse(samecoreSymVars[tt.id][t.id]); // model.addClauseFalse(overlapSymVars[tt.id][t.id]); //
     * model.addClauseFalse(overlapVars[tt.id][t.id]); }
     * 
     * continue; }
     * 
     * if (t.id < tt.id) {
     * 
     * // is useful if NOT Choco allocation of boolVar matrices overlapVars[tt.id][t.id] = model.boolVar();
     * overlapVars[t.id][tt.id] = model.boolVar();
     * 
     * // two tasks half overlapping model.arithm(startTimeVars[t.id], "<",
     * finishTimeVars[tt.id]).reifyWith(overlapVars[t.id][tt.id]); model.arithm(startTimeVars[tt.id], "<",
     * finishTimeVars[t.id]).reifyWith(overlapVars[tt.id][t.id]);
     * 
     * // is useful if NOT Choco allocation of boolVar matrices oversameSymVars[t.id][tt.id] = model.boolVar(false);
     * samecoreSymVars[t.id][tt.id] = model.boolVar(); overlapSymVars[t.id][tt.id] = model.boolVar();
     * 
     * // check the task overlapping model.addClausesBoolAndEqVar(overlapVars[t.id][tt.id], overlapVars[tt.id][t.id],
     * overlapSymVars[t.id][tt.id]); // symmetry of overllaping tasks //
     * model.addClausesBoolEq(overlapSymVars[t.id][tt.id], overlapSymVars[tt.id][t.id]); // symmetry of two tasks on
     * same cores // model.addClausesBoolEq(samecoreSymVars[t.id][tt.id], samecoreSymVars[tt.id][t.id]); for (int i = 0;
     * i < nbCores; i++) { // is useful if NOT Choco allocation of boolVar matrices samecoreVars[t.id][tt.id][i] =
     * model.boolVar(); // are two tasks on the core model.addClausesBoolAndEqVar(mapping[t.id][i], mapping[tt.id][i],
     * samecoreVars[t.id][tt.id][i]); // symmetry of the line just above //
     * model.addClausesBoolEq(samecoreVars[t.id][tt.id][i], samecoreVars[tt.id][t.id][i]);
     * 
     * } // are two tasks on the same core model.addClausesBoolOrArrayEqVar(samecoreVars[t.id][tt.id],
     * samecoreSymVars[t.id][tt.id]);
     * 
     * model.addClausesBoolAndEqVar(samecoreSymVars[t.id][tt.id], overlapSymVars[t.id][tt.id],
     * oversameSymVars[t.id][tt.id]); }
     * 
     * // is useful if Choco allocation of boolVar matrices //
     * model.addClausesBoolAndEqVar(samecoreSymVars[t.id][tt.id], overlapSymVars[t.id][tt.id], //
     * oversameSymVars[t.id][tt.id]); } }
     * 
     * if (horizon > 0) { // minimize latency final IntVar varLatency = model.intVar(0, horizon); model.max(varLatency,
     * finishTimeVars).post(); model.setObjective(Model.MINIMIZE, varLatency); }
     * 
     * final long totalComputationMax = nbTasks * (long) nbTasks * nbCores; final long percentageRemoved = (100 *
     * removedComputationMax / totalComputationMax);
     * 
     * PreesmLogger.getLogger().info(() -> "Redundant constraints removed from model: " + percentageRemoved + " %");
     */
    return model;
  }
}
