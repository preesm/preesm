package org.preesm.algorithm.schedule.fpga;

import static org.chocosolver.solver.search.strategy.Search.intVarSearch;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Propagator;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.search.loop.monitors.IMonitorContradiction;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.variables.InputOrder;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IVariableMonitor;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.events.IEventType;
import org.preesm.algorithm.schedule.fpga.AbstractGenericFpgaFifoEvaluator.AnalysisResultFPGA;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.algorithm.synthesis.schedule.algos.IScheduler;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.DataInterface;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.TimingType;

public class FPGAConstraintScheduler implements IScheduler {

  static final int MAX_INT = Integer.MAX_VALUE;

  // attention aux valeurs ! Si elles sont trop grandes, choco pourrait overflow son calcul d'upper bound de résultats
  // intermédiaire de multiplication
  // vraiment static ? Elles pourraient peut-être prendre des valeurs différentes selon l'algo
  static final int CYCLE_MAX  = 100_000_000;// trouver le chemin critique et sommer latence*brv pour chaque acteur ?
  static final int TOKENS_MAX = 100_000_000;

  final boolean monitor = true;
  final boolean logs    = true;

  public static long pgcd(long a, long b) {
    if (b == 0) {
      return a;
    }
    return pgcd(b, a % b);
  }

  public static long ppcm(long a, long b) {
    return a * b / pgcd(a, b);
  }

  public static long ppcm(List<Long> numbers) {
    long result = numbers.getFirst();

    for (int i = 1; i < numbers.size(); i++) {
      result = ppcm(result, numbers.get(i));
    }

    return result;
  }

  public FPGAConstraintScheduler() {
    super();
  }

  public class FpgaSchedule {
    protected List<ActorTimings> actorTimings;
    protected int                latency;

    public FpgaSchedule(int l) {
      super();
      actorTimings = new LinkedList<>();
      latency = l;
    }

    public void addActorTimings(ActorTimings t) {
      actorTimings.add(t);
    }
  }

  public class ActorTimings {
    private IntVar period_var;
    private IntVar startDate_var;
    private IntVar endDate_var;
    public int     repetitionCount;
    public int     executionTime;
    public int     period;
    public int     startDate;
    public int     endDate;

    ActorTimings() {
      super();
    }

    public void printSchedule(AbstractActor a) {
      System.out.printf("%s : start=%d \t latency=%d \t end=%d \t period=%d %n", a.getName(), startDate, executionTime,
          endDate, period);
    }

    public void storeResults(Solution s) {
      period = s.getIntVal(period_var);
      startDate = s.getIntVal(startDate_var);
      endDate = s.getIntVal(endDate_var);

      // no longer needed
      period_var = null;
      startDate_var = null;
      endDate_var = null;
    }
  }

  private ActorTimings initActorTimings(AbstractActor actor, Scenario scenario, Component component,
      Map<AbstractVertex, Long> brv, Model model, int LAT_MAX) {
    final ActorTimings res = new ActorTimings();

    // default value for broadcast and round buffer actors
    if (actor instanceof SpecialActor) {
      // TODO est-ce que 1 marche tout le temps ? probablement pas...
      res.executionTime = 1;
    } else {
      res.executionTime = (int) scenario.getTimings().evaluateTimingOrDefault(actor, component,
          TimingType.EXECUTION_TIME);
    }

    res.repetitionCount = brv.get(actor).intValue();

    // arbitrary limit : period <= 100 * executionTime
    res.period_var = model.intVar("period_" + actor.getName() + "_var", res.executionTime, 100 * res.executionTime);

    // must start early enough to finish all its periods (=brv) before MAX_CYCLE
    // could even be bounded by its followers' latencies sum
    res.startDate_var = model.intVar("start_" + actor.getName() + "_var", 0,
        LAT_MAX - res.executionTime * res.repetitionCount);

    // must have at least executed all its firings by end time
    res.endDate_var = model.intVar("end_" + actor.getName() + "_var", 0 /* res.executionTime * res.repetitionCount */,
        LAT_MAX);

    // endDate = startDate + T * (repetition_count - 1) + execution_time
    // le dernier token est produit à la fin de l'exécution du dernier acteur, qui est possiblement bien avant la fin
    // de sa période = le début du prochain firing
    res.endDate_var.eq(res.startDate_var.add(res.period_var.mul(res.repetitionCount - 1).add(res.executionTime)))
        .post();
    // bornes : [-repetition_count + execution_time ; LAT_MAX + 101 * res.executionTime]

    return res;
  }

  @Override
  /***
   * The method assumes it is scheduling a flat graph. If it encounters a cluster, it will be treated as an actor.
   ***/
  public SynthesisResult scheduleAndMap(final PiGraph piGraph, final Design slamDesign, final Scenario scenario) {

    final Map<AbstractActor, ActorTimings> schedule = new HashMap<>();

    // TODO récupérer en ordre d'exécution pour forcer startDate du 1er acteur à 0 ?
    final List<ExecutableActor> actors = piGraph.getExecutableActors().stream()
        .filter(a -> !(a instanceof DataInterface)).toList();

    // for now, we will not consider fifos linking actors from/to data interfaces.
    // It may by interesting to model them as actors with a start date equal to the comm. time and rate of comm size
    final List<Fifo> fifos = piGraph.getFifos().stream()
        .filter(f -> !(f.getSource() instanceof DataInterface || f.getTarget() instanceof DataInterface)).toList();

    final Map<AbstractVertex, Long> brv = PiBRV.compute(piGraph, BRVMethod.LCM);
    // the graph is supposed to be mapped to a single PE type (FPGA, CPU, DSP...)
    final Component Fpga = scenario.getPossibleMappings(piGraph).getFirst().getComponent();

    final Model model = new Model("Period computing");

    // Attention ! Le déclarer comme ça créerait de nouvelle variables qui devraient être mises à .eq()
    // mieux : déclarer un tableau mais pas de variable choco
    // final IntVar[] latencies = model.intVarArray("latencies", actors.size(), 0, LAT_MAX);
    final IntVar[] latencies = new IntVar[actors.size()];
    final IntVar[] periods = new IntVar[actors.size()];

    int i = 0;
    // TODO faut-il mettre le startDate du 1er acteur à 0 ?
    for (final AbstractActor actor : actors) {
      final ActorTimings at = initActorTimings(actor, scenario, Fpga, brv, model, CYCLE_MAX);

      schedule.put(actor, at);
      latencies[i] = at.endDate_var;
      periods[i] = at.period_var;
      i++;
    }

    // for (final AbstractActor actor : actors) {
    // final var at = schedule.get(actor);
    //
    // // we know the actor's start date is superior or equal to all its predecessors' start dates
    // for (final var pred : actor.getDirectPredecessors().stream().filter(actors::contains).toList()) {
    // final ActorTimings predTiming = schedule.get(pred);
    // model.arithm(at.startDate_var, ">=", predTiming.startDate).post(); // Vraiment utile comme contrainte ?
    // }
    // }

    final List<IntVar> listCumP = new LinkedList<>();
    final List<IntVar> listCumC = new LinkedList<>();

    // on met en place le modèle pour chaque fifo
    for (final Fifo fifo : fifos) {

      // ================================================================
      // 1. Paramètres des acteurs
      // ================================================================
      final AbstractActor sourceActor = fifo.getSource();
      final ActorTimings source = schedule.get(sourceActor);
      final AbstractActor targetActor = fifo.getTarget();
      final ActorTimings target = schedule.get(targetActor);

      final int prod_rate = (int) fifo.getSourcePort().getPortRateExpression().evaluateAsLong();
      final int cons_rate = (int) fifo.getTargetPort().getPortRateExpression().evaluateAsLong();

      final int basisOfTp = (int) (prod_rate / pgcd(prod_rate, cons_rate));
      final int basisOfTc = (int) (cons_rate / pgcd(prod_rate, cons_rate));

      final IntVar TpMultiple = model.intVar("TpMultiple_" + fifo.getId(), 0, 100);
      final IntVar TcMultiple = model.intVar("TpMultiple_" + fifo.getId(), 0, 100);

      // the periods must be a multiple of their base, starting from the minimum allowed : their latency
      // we generate 100 possible values, assuming it is unlikely that period > 100 * latency (C'est au pif !)
      // Il paraît intéressant de générer une restriction de l'ensemble permis et pas des contraintes, mais j'ai
      // l'impression que dans tous les cas cela sera traité comme une contrainte et qu'il faut générer un très grand
      // nombre de valeurs...
      if (basisOfTp != 1) {
        // éviter de poster une contrainte inutile
        // model.mod(source.period_var, basisOfTp, 0).post();

        // final int startMultiple = (source.executionTime + basisOfTp - 1) / basisOfTp;
        // final int[] possibleTpValues = IntStream.rangeClosed(startMultiple, startMultiple * 10).map(n -> n *
        // basisOfTp)
        // .toArray();
        // model.member(source.period_var, possibleTpValues).post();

        source.period_var.eq(TpMultiple.mul(basisOfTp)).post();

      }
      if (basisOfTc != 1) {
        // éviter de poster une contrainte inutile
        // model.mod(target.period_var, basisOfTc, 0).post();

        // final int startMultiple = (target.executionTime + basisOfTc - 1) / basisOfTc;
        // final int[] possibleTcValues = IntStream.rangeClosed(startMultiple, startMultiple * 10).map(n -> n *
        // basisOfTc)
        // .toArray();
        // model.member(target.period_var, possibleTcValues).post();

        target.period_var.eq(TcMultiple.mul(basisOfTc)).post();

      }

      // equal rates constraints --> Tc * rp = Tp * rc
      // redondant avec le calcul des valeurs de possibleTpValues et possibleTcValues ?
      final IntVar left = model.intVar("left_" + fifo.getId() + "_var", 0, CYCLE_MAX);
      final IntVar right = model.intVar("right_" + fifo.getId() + "_var", 0, CYCLE_MAX);
      model.times(source.period_var, cons_rate, left).post();
      model.times(target.period_var, prod_rate, right).post();
      model.arithm(left, "=", right).post();

      // -- Computing breakpoints positions --

      // nombre de breakpoints de chaque
      final int nbBreakpointsProd = (int) (ppcm(prod_rate, cons_rate) / prod_rate); // bornes : [1 ; cons_rate]
      final int nbBreakpointsCons = (int) (ppcm(prod_rate, cons_rate) / cons_rate); // bornes : [1 ; prod_rate]
      final String chosenBreakpoints = nbBreakpointsProd <= nbBreakpointsCons ? "Prod" : "Cons";
      final int nbBreakpoints = Math.min(nbBreakpointsProd, nbBreakpointsCons);

      // TODO on peut simplifier l'évaluation de la courbe dont on a choisi les breakpoints
      // vu qu'on est au moment où elle commence/finit de produire dans sa période
      final IntVar[] breakpoints = model.intVarArray("breakpoint" + chosenBreakpoints + "_" + fifo.getId() + "_var",
          nbBreakpoints, 0, CYCLE_MAX);

      if (chosenBreakpoints.equals("Prod")) {
        // producer breakpoints
        for (int bk = 1; bk <= nbBreakpointsProd; bk++) {
          // delay_prod + bk * periodProd - taux_prod
          breakpoints[bk - 1].eq(source.startDate_var.add(source.period_var.mul(bk)).sub(prod_rate)).post();
        }
      } else {
        // consumer breakpoints
        for (int bk = 1; bk <= nbBreakpointsCons; bk++) {
          // delay_cons + (bk - 1) * periodCons + taux_cons
          breakpoints[bk - 1].eq(target.startDate_var.add(target.period_var.mul(bk - 1)).add(cons_rate)).post();
        }

      }

      // les valeurs aux breakpoints
      final IntVar[] cumP = model.intVarArray("cumP_" + fifo.getId() + "_var", nbBreakpoints, 0, TOKENS_MAX);
      final IntVar[] cumC = model.intVarArray("cumC_" + fifo.getId() + "_var", nbBreakpoints, 0, TOKENS_MAX);
      Collections.addAll(listCumP, cumP);
      Collections.addAll(listCumC, cumC);

      final IntVar zero = model.intVar(0);
      final IntVar rate_cons = model.intVar(cons_rate);

      for (int bk = 1; bk <= nbBreakpoints; bk++) {

        // -------------------------------------------
        // -- cumulated production at breakpoint bk --
        // -------------------------------------------

        final IntVar t = breakpoints[bk - 1];

        // t - delay_prod : always positive, since breakpoints at prod are after it started, and cons starts after prod
        // at most the biggest breakpoint, which are capped to CYCLE_MAX
        final IntVar delta_prod = model.intVar("deltaProd_" + fifo.getId() + "_" + bk + "_var", 0, CYCLE_MAX);

        final IntVar prodInPeriod = model.intVar("inPeriodProd_" + fifo.getId() + "_" + bk + "_var", 0, TOKENS_MAX);
        final IntVar inter2 = model.intVar("inter2_" + bk + "_var", -TOKENS_MAX, TOKENS_MAX);

        model.arithm(delta_prod, "=", t, "-", source.startDate_var).post();

        // in-period production
        // (t - delay_prod) % periodProd - periodProd + taux_prod
        inter2.eq((delta_prod.mod(source.period_var)).sub(source.period_var).add(prod_rate)).post();
        model.max(prodInPeriod, inter2, zero).post(); // force it to be 0 or more

        // choco operates on natural numbers so the fraction results are automatically floored
        // ((t - delay_prod) / periodProd) * taux_prod + inter2
        cumP[bk - 1].eq(((delta_prod.div(source.period_var)).mul(prod_rate)).add(prodInPeriod)).post();

        // ---------------------------------
        // -- cumulated consumption at bk --
        // ---------------------------------

        // t - delay_cons
        // lower bound : the consumer is delayed from the producer by at least the time it takes to start producing
        // this time is prod.executionTime - rate (at least ! In general : prod.period - rate)
        // Upper bound : the cons should start before the prod has finished an iteration's worth of firings I guess ?
        final IntVar delta_cons = model.intVar("deltaCons_" + fifo.getId() + "_" + bk + "_var",
            0 /* source.executionTime - prod_rate */, CYCLE_MAX /* target.repetitionCount * source.executionTime */);
        final BoolVar consNotYetStarted = model.boolVar("consNotYetStarted_" + fifo.getId() + "_" + bk + "_var");
        consNotYetStarted.addMonitor((vari, evt) -> System.out.printf("%s -> %s%n", vari.getName(), vari));

        // METTRE PLUTÔT UN MAX(delta_prod, 0) ???????

        // since t - delay_cons can be negative and we DON'T want that, we create an intermediary that stores
        // 0 if delay_cons > t, else (t - delay_cons)
        model.reifyXgtY(target.startDate_var, t, consNotYetStarted);
        model.ifThen(consNotYetStarted, delta_cons.eq(0).decompose());
        model.ifThen(consNotYetStarted.not(), delta_cons.eq(t.sub(target.startDate_var)).decompose());

        // this intermediate variable can be negative
        // min(x, rate_cons) <= rate_cons
        final IntVar inPeriodCons = model.intVar("inPeriodCons_" + fifo.getId() + "_" + bk + "_var", 0, cons_rate);
        final IntVar inter4 = model.intVar("inter4_" + bk + "_var", 0, TOKENS_MAX);

        // (t - delay_cons) % periodCons
        inter4.eq(delta_cons.mod(target.period_var)).post();
        // inter4 = min(inter3, taux_cons)
        model.min(inPeriodCons, inter4, rate_cons).post();

        // METTRE PLUTÔT UN MAX(cumC, 0) ???????

        // ((t - delay_cons) / periodCons) * taux_cons + inter4
        model.ifThenElse(consNotYetStarted, // conditional value
            cumC[bk - 1].eq(0).decompose(), // if cons start > bk, cumC[bk] = 0
            cumC[bk - 1].eq((delta_cons.div(target.period_var).mul(cons_rate)).add(inter4)).decompose());

        model.arithm(cumP[bk - 1], ">=", cumC[bk - 1]).post();
      }

    }

    // objectif : optimiser la latence = la date de fin du dernier acteur relative à une période
    // on pourrait utiliser le chemin critique, mais à la place je vais juste optimiser la fin d'exécution de l'acteur
    // le plus tardif
    final IntVar latency = model.max("latency", latencies);

    final IntVar[] variablesToMonitor = Stream
        .of(new IntVar[] { latency }, latencies, periods, listCumP.toArray(), listCumC.toArray())
        .flatMap(Arrays::stream).toArray(IntVar[]::new);

    // --------
    // Solution
    // --------
    final Solver solver = model.getSolver();

    if (monitor) {
      solver.showContradiction();
      solver.showDashboard();
      solver.showStatisticsDuringResolution(1000);
      // solver.verboseSolving(1000); // marche pas

      System.out.printf("Variables : %n %s %n", String.join("\n",
          schedule.values().stream()
              .map(a -> a.period_var.toString() + "\t" + a.startDate_var.toString() + "\t" + a.endDate_var.toString())
              .toList()));
      System.out.printf("%s%n", String.join("\n", Arrays.asList(model.getVars()).stream()
          .filter(v -> v.getName().contains("left") || v.getName().contains("right")).map(Object::toString).toList()));

    }
    if (logs) {
      solver.showDecisions();
      solver.showContradiction();

      final Map<String, int[]> lastDomain = new HashMap<>();
      for (final var v : model.getVars()) {
        if (v instanceof final IntVar iv) {
          lastDomain.put(iv.getName(), new int[] { iv.getLB(), iv.getUB() });

          iv.addMonitor(new IVariableMonitor<IntVar>() {
            @Override
            public void onUpdate(IntVar variable, IEventType evt) {
              final int[] prev = lastDomain.get(variable.getName());
              System.out.printf("[UPDATE] %-20s %s → [%d, %d] (was [%d, %d])%n", variable.getName(), evt,
                  variable.getLB(), variable.getUB(), prev[0], prev[1]);
              lastDomain.put(variable.getName(), new int[] { variable.getLB(), variable.getUB() });
            }
          });
        }
      }

      solver.plugMonitor(new IMonitorContradiction() {
        @Override
        public void onContradiction(ContradictionException cex) {
          final int depth = solver.getDecisionPath().size();

          if (depth == 0) {
            // Happens during root propagation — directly proves UNSAT
            System.out.println("[ROOT CONTRADICTION] " + cex.c);
          } else {
            // Just pruning a branch at depth " + depth
            System.out.println("[pruning d=" + depth + "] " + cex.c);
          }
          if (cex.v instanceof final IntVar v) {
            final int[] prev = lastDomain.get(v.getName());
            System.out.printf(" Variable : %s%n", v.getName());
            System.out.printf(" Domain now : [%d, %d]%n", v.getLB(), v.getUB());
            System.out.printf(" Previous domain : [%d, %d]%n", prev[0], prev[1]);
          }
          if (cex.c instanceof final Propagator<?> p) {
            System.out.printf(" Propagator : %s%n", p);
            System.out.printf(" Constraint : %s%n", p.getConstraint());
          }
          System.out.printf("%n");
        }
      });
    }

    solver.showStatistics();
    solver.limitTime("5s");

    // on veut en fait choisir la latence, pour qu'il comprenne que la première valeur qui réussit est l'optim
    solver.setSearch(intVarSearch(new InputOrder<>(model), // pick variables in order
        new IntDomainMin(), // try smallest value first
        variablesToMonitor // toutes les variables sur lesquelles "brancher" (?)
    ));

    // model.displayPropagatorOccurrences(); // pour vérifier que des propagateurs safe sont utilisés

    Solution solution = new Solution(model);

    try {
      solver.propagate();
    } catch (final ContradictionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      System.out.println("First propagation failed : model might be unsolvable.");
    }

    System.out.printf("%s %n", model.toString());

    // AFFICHER TOUTES LES SOLUTIONS JUSQU'À TROUVER L'OPTIMALE
    solution = solver.findOptimalSolution(latency, Model.MINIMIZE);
    // solution = solver.findLexOptimalSolution(objectives, Model.MINIMIZE); // beaucoup trop lent, besoin de
    // l'améliorer

    // Now that the latency is minimized, we want to fix a value for all other variables
    // for example, we may want to minimize start delays

    if (solver.getSolutionCount() != 0) {
      final FpgaSchedule result = new FpgaSchedule(solution.getIntVal(latency));
      System.out.println("Solution found !");
      // for (final IntVar var : model.retrieveIntVars(true)) {
      // System.out.println(var.getName() + " = " + solution.getIntVal(var));
      // }
      for (final var res : schedule.entrySet()) {
        res.getValue().storeResults(solution);
        res.getValue().printSchedule(res.getKey());
        result.addActorTimings(res.getValue());
      }
      System.out.println("latency = " + result.latency);
      System.out.println("hyperperiod = " + ppcm(schedule.values().stream().map(a -> (long) a.period).toList()));
      System.out.print("\n");

    } else {
      System.out.println("No solution was found !");
      final List<String> truc = Arrays.asList(model.getVars()).stream().filter(v -> v.getName().endsWith("_var"))
          .map(Object::toString).toList();
      System.out.printf("%s %n", String.join("\n", truc));
    }

    return new AnalysisResultFPGA(piGraph, null, null);
  }

}
