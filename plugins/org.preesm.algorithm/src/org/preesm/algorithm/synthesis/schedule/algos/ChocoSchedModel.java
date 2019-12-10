package org.preesm.algorithm.synthesis.schedule.algos;

import java.util.Arrays;
import java.util.SortedMap;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.preesm.algorithm.synthesis.schedule.algos.ChocoScheduler.Task;

/**
 * Basic scheduling model for Choco solver.
 * 
 * @author ahonorat
 */
public class ChocoSchedModel {

  protected final String                   name;
  protected final SortedMap<Integer, Task> tasks;
  protected final int                      nbCores;

  protected final int         nbTasks;
  protected final Model       model;
  protected final IntVar[]    startTimeVars;
  protected final BoolVar[][] mapping;

  protected ChocoSchedModel(final String name, final SortedMap<Integer, Task> tasks, final int nbCores) {
    this.name = name;
    this.tasks = tasks;
    this.nbCores = nbCores;

    nbTasks = tasks.size();
    model = new Model(name);

    startTimeVars = new IntVar[nbTasks];
    mapping = model.boolVarMatrix("c", nbTasks, nbCores);
  }

  protected Model generateModel() {
    // other transient variables but we don't care of their values
    BoolVar[][] mappingT = model.boolVarMatrix("c", nbCores, nbTasks);

    BoolVar[][] overlapVars = model.boolVarMatrix("o", nbTasks, nbTasks);
    BoolVar[][] overlapSymVars = model.boolVarMatrix("oo", nbTasks, nbTasks);

    BoolVar[][][] samecoreVars = new BoolVar[nbTasks][nbTasks][nbCores];
    BoolVar[][] samecoreSymVars = model.boolVarMatrix("i", nbTasks, nbTasks);

    BoolVar[][] oversameSymVars = model.boolVarMatrix("oi", nbTasks, nbTasks);

    // break symmetries in cores
    model.addClauseTrue(mapping[0][0]);
    for (int ic = 0; ic < nbCores; ic++) {
      for (int it = 0; it < nbTasks; it++) {
        model.addClausesBoolEq(mapping[it][ic], mappingT[ic][it]);
      }
    }

    for (int ic = 1; ic < nbCores; ic++) {
      for (int it = ic; it < nbTasks; it++) {
        model.sum(Arrays.copyOfRange(mappingT[ic - 1], 0, it), ">=", mappingT[ic][it]);
      }
    }

    // start time
    for (Task t : tasks.values()) {
      startTimeVars[t.id] = model.intVar("s" + t.id, t.ns, t.xs, false);
    }

    // all other constraints
    for (Task t : tasks.values()) {

      // start time and preds
      for (Integer pred : t.predId) {
        Task temp = tasks.get(pred);
        model.arithm(startTimeVars[temp.id], "<=", startTimeVars[t.id], "-", temp.load).post();
      }

      // unique mapping
      model.sum(mapping[t.id], "=", 1).post();

      // no overlapping if on same core
      for (Task tt : tasks.values()) {
        // two tasks half overlapping
        model.arithm(startTimeVars[t.id], "<", startTimeVars[tt.id], "+", tt.load).reifyWith(overlapVars[t.id][tt.id]);

        if (t.id == tt.id) {
          for (int i = 0; i < nbCores; i++) {
            samecoreVars[t.id][tt.id][i] = model.boolVar(false);
            // samecoreVars[t.id][tt.id][i].post();
          }
          model.addClauseFalse(samecoreSymVars[t.id][tt.id]);
          model.addClauseFalse(overlapSymVars[t.id][tt.id]);
        }

        if (t.id < tt.id) {
          // check the task overlapping
          model.addClausesBoolAndEqVar(overlapVars[t.id][tt.id], overlapVars[tt.id][t.id], overlapSymVars[t.id][tt.id]);
          // symmetry of overllaping tasks
          model.addClausesBoolEq(overlapSymVars[t.id][tt.id], overlapSymVars[tt.id][t.id]);
          // symmetry of two tasks on same cores
          model.addClausesBoolEq(samecoreSymVars[t.id][tt.id], samecoreSymVars[tt.id][t.id]);
          for (int i = 0; i < nbCores; i++) {
            samecoreVars[t.id][tt.id][i] = model.boolVar();
            // samecoreVars[t.id][tt.id][i].post();
            samecoreVars[tt.id][t.id][i] = model.boolVar();
            // samecoreVars[tt.id][t.id][i].post();
            // are two tasks on the core
            model.addClausesBoolAndEqVar(mapping[t.id][i], mapping[tt.id][i], samecoreVars[t.id][tt.id][i]);
            // symmetry of the line just above
            model.addClausesBoolEq(samecoreVars[t.id][tt.id][i], samecoreVars[tt.id][t.id][i]);
          }
          // are two tasks on the same core
          model.addClausesBoolOrArrayEqVar(samecoreVars[t.id][tt.id], samecoreSymVars[t.id][tt.id]);
        }

        model.addClausesBoolAndEqVar(samecoreSymVars[t.id][tt.id], overlapSymVars[t.id][tt.id],
            oversameSymVars[t.id][tt.id]);
        model.addClauseFalse(oversameSymVars[t.id][tt.id]);
      }
    }

    return model;
  }

}
