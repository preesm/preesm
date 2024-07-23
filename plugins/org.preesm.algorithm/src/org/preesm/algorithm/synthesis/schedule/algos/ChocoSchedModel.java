/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
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
package org.preesm.algorithm.synthesis.schedule.algos;

import java.util.Arrays;
import java.util.SortedMap;
import java.util.logging.Level;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.preesm.algorithm.synthesis.schedule.algos.ChocoScheduler.Task;
import org.preesm.commons.logger.PreesmLogger;

/**
 * Basic scheduling model for Choco solver.
 *
 * @author ahonorat
 */
public class ChocoSchedModel {

  protected final String                   name;
  protected final SortedMap<Integer, Task> tasks;
  protected final int                      nbCores;
  protected final int                      horizon;

  protected final int         nbTasks;
  protected final Model       model;
  protected final IntVar[]    startTimeVars;
  protected final BoolVar[][] mapping;

  /**
   *
   * @param name
   *          Name of the model.
   * @param tasks
   *          Tasks to schedule.
   * @param nbCores
   *          Number of available cores for scheduling.
   * @param horizon
   *          Maximum makespan if positive, not considered if negative.
   */
  protected ChocoSchedModel(final String name, final SortedMap<Integer, Task> tasks, final int nbCores,
      final int horizon) {
    this.name = name;
    this.tasks = tasks;
    this.nbCores = nbCores;
    this.horizon = horizon;

    nbTasks = tasks.size();
    model = new Model(name);

    startTimeVars = new IntVar[nbTasks];
    mapping = model.boolVarMatrix("c", nbTasks, nbCores);
  }

  protected Model generateModel() {
    // other transient variables but we don't care of their values
    final BoolVar[][] mappingT = model.boolVarMatrix("c", nbCores, nbTasks);

    // separate allocation of boolVars is faster

    // BoolVar[][][] samecoreVars = new BoolVar[nbTasks][][];
    // for (int it = 0; it < nbTasks; it++) {
    // samecoreVars[it] = model.boolVarMatrix("sc", nbTasks, nbCores);
    // }

    // BoolVar[][] overlapVars = model.boolVarMatrix("o", nbTasks, nbTasks);
    // BoolVar[][] overlapSymVars = model.boolVarMatrix("os", nbTasks, nbTasks);
    // BoolVar[][] samecoreSymVars = model.boolVarMatrix("scs", nbTasks, nbTasks);
    // BoolVar[][] oversameSymVars = model.boolVarMatrix("oss", nbTasks, nbTasks);

    // break symmetries in cores
    model.addClauseTrue(mapping[0][0]);
    for (int ic = 0; ic < nbCores; ic++) {
      for (int it = 0; it < nbTasks; it++) {
        model.addClausesBoolEq(mapping[it][ic], mappingT[ic][it]);
      }
    }

    for (int ic = 1; ic < nbCores; ic++) {
      for (int it = ic; it < nbTasks; it++) {
        // model.sum(Arrays.copyOfRange(mappingT[ic - 1], 0, it), ">=", mappingT[ic][it]).post();
        model.addClausesSumBoolArrayGreaterEqVar(Arrays.copyOfRange(mappingT[ic - 1], 0, it), mappingT[ic][it]);
      }
      // all cores must be used at least once if less than tasks
      // core 0 is always used according to the first constraint
      if (nbCores <= nbTasks) {
        // model.sum(mappingT[ic], ">=", 1).post();
        model.addClausesSumBoolArrayGreaterEqVar(mappingT[ic], model.boolVar(true));
      }
    }

    // start time and finish
    final IntVar[] finishTimeVars = new IntVar[nbTasks];
    for (final Task t : tasks.values()) {
      startTimeVars[t.id] = model.intVar("s" + t.id, t.ns, t.xs, false);
      finishTimeVars[t.id] = model.intVar("f" + t.id, t.ns + t.load, t.xs + t.load, false);
      model.arithm(finishTimeVars[t.id], "=", startTimeVars[t.id], "+", t.load).post();
    }

    long removedComputationMax = 0;

    // all other constraints
    for (final Task t : tasks.values()) {

      // start time and preds
      for (final Integer pred : t.predId) {
        final Task temp = tasks.get(pred);
        model.arithm(finishTimeVars[temp.id], "<=", startTimeVars[t.id]).post();
      }

      // unique mapping
      model.sum(mapping[t.id], "=", 1).post();

      // no overlapping if on same core
      for (final Task tt : tasks.values()) {

        // is useful if Choco allocation of boolVar matrices, otherwise no variable
        // model.addClauseFalse(oversameSymVars[t.id][tt.id]);

        // if in the list of all precedences, we already know that there will be no overlap
        if ((t.id == tt.id) || (t.id < tt.id && tt.allPredId.contains(t.id))
            || (tt.id < t.id && t.allPredId.contains(tt.id))) {
          for (int i = 0; i < nbCores; i++) {
            // is useful if Choco allocation of boolVar matrices, otherwise no variable
            // model.addClauseFalse(samecoreVars[t.id][tt.id][i]);
            removedComputationMax++;
          }
          // is useful if Choco allocation of boolVar matrices, otherwise no variable
          // model.addClauseFalse(samecoreSymVars[t.id][tt.id]);
          // model.addClauseFalse(overlapSymVars[t.id][tt.id]);
          // model.addClauseFalse(overlapVars[t.id][tt.id]);

          // we do it for the opposite if different
          if (t.id != tt.id) {
            for (int i = 0; i < nbCores; i++) {
              // is useful if Choco allocation of boolVar matrices, otherwise no variable
              // model.addClauseFalse(samecoreVars[tt.id][t.id][i]);
              removedComputationMax++;
            }

            // is useful if Choco allocation of boolVar matrices, otherwise no variable
            // model.addClauseFalse(samecoreSymVars[tt.id][t.id]);
            // model.addClauseFalse(overlapSymVars[tt.id][t.id]);
            // model.addClauseFalse(overlapVars[tt.id][t.id]);
          }

          continue;
        }

        if (t.id < tt.id) {
          final BoolVar[][] overlapVars = new BoolVar[nbTasks][nbTasks];
          // is useful if NOT Choco allocation of boolVar matrices
          overlapVars[tt.id][t.id] = model.boolVar();
          overlapVars[t.id][tt.id] = model.boolVar();

          // two tasks half overlapping
          model.arithm(startTimeVars[t.id], "<", finishTimeVars[tt.id]).reifyWith(overlapVars[t.id][tt.id]);
          model.arithm(startTimeVars[tt.id], "<", finishTimeVars[t.id]).reifyWith(overlapVars[tt.id][t.id]);

          // is useful if NOT Choco allocation of boolVar matrices
          final BoolVar[][] oversameSymVars = new BoolVar[nbTasks][nbTasks];
          oversameSymVars[t.id][tt.id] = model.boolVar(false);
          final BoolVar[][] samecoreSymVars = new BoolVar[nbTasks][nbTasks];
          samecoreSymVars[t.id][tt.id] = model.boolVar();
          final BoolVar[][] overlapSymVars = new BoolVar[nbTasks][nbTasks];
          overlapSymVars[t.id][tt.id] = model.boolVar();

          // check the task overlapping
          model.addClausesBoolAndEqVar(overlapVars[t.id][tt.id], overlapVars[tt.id][t.id], overlapSymVars[t.id][tt.id]);
          // symmetry of overllaping tasks
          // model.addClausesBoolEq(overlapSymVars[t.id][tt.id], overlapSymVars[tt.id][t.id]);
          // symmetry of two tasks on same cores
          // model.addClausesBoolEq(samecoreSymVars[t.id][tt.id], samecoreSymVars[tt.id][t.id]);
          final BoolVar[][][] samecoreVars = new BoolVar[nbTasks][nbTasks][nbCores];
          for (int i = 0; i < nbCores; i++) {
            // is useful if NOT Choco allocation of boolVar matrices
            samecoreVars[t.id][tt.id][i] = model.boolVar();
            // are two tasks on the core
            model.addClausesBoolAndEqVar(mapping[t.id][i], mapping[tt.id][i], samecoreVars[t.id][tt.id][i]);
            // symmetry of the line just above
            // model.addClausesBoolEq(samecoreVars[t.id][tt.id][i], samecoreVars[tt.id][t.id][i]);

          }
          // are two tasks on the same core
          model.addClausesBoolOrArrayEqVar(samecoreVars[t.id][tt.id], samecoreSymVars[t.id][tt.id]);

          model.addClausesBoolAndEqVar(samecoreSymVars[t.id][tt.id], overlapSymVars[t.id][tt.id],
              oversameSymVars[t.id][tt.id]);
        }

        // is useful if Choco allocation of boolVar matrices
        // model.addClausesBoolAndEqVar(samecoreSymVars[t.id][tt.id], overlapSymVars[t.id][tt.id],
        // oversameSymVars[t.id][tt.id]);
      }
    }

    if (horizon > 0) {
      // minimize latency
      final IntVar varLatency = model.intVar(0, horizon);
      model.max(varLatency, finishTimeVars).post();
      model.setObjective(Model.MINIMIZE, varLatency);
    }

    final long totalComputationMax = nbTasks * (long) nbTasks * nbCores;
    final long percentageRemoved = (100 * removedComputationMax / totalComputationMax);

    PreesmLogger.getLogger().log(Level.INFO, "Redundant constraints removed from model: " + percentageRemoved + " %");

    return model;
  }

}
