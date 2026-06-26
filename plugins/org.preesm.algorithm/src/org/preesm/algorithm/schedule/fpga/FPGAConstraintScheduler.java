package org.preesm.algorithm.schedule.fpga;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Propagator;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.search.loop.monitors.IMonitorContradiction;
import org.chocosolver.solver.search.strategy.BlackBoxConfigurator;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.IVariableMonitor;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.events.IEventType;
import org.preesm.algorithm.mapper.ui.stats.StatEditorSynthesisTask;
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
  static final int CYCLE_MAX    = 1_000_000;     // chemin critique et sommer latence*brv pour chaque acteur ?
  static final int TOKENS_MAX   = CYCLE_MAX / 2; // arbitrary
  static final int MAX_MULTIPLE = 100;

  final boolean monitor = true;
  final boolean logs    = true;

  public static long gcd(long a, long b) {
    if (b == 0) {
      return a;
    }
    return gcd(b, a % b);
  }

  public static long lcm(long a, long b) {
    return a * b / gcd(a, b);
  }

  public static long ppcm(List<Long> numbers) {
    long result = numbers.getFirst();

    for (int i = 1; i < numbers.size(); i++) {
      result = lcm(result, numbers.get(i));
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

  /**
   * returns the list of ExecutableActor without DataInterface actors
   *
   * @param graph
   *          the graph
   * @return the list of ExecutableActor
   */
  private List<ExecutableActor> getNonDataInterfaceActors(PiGraph graph) {
    return graph.getExecutableActors().stream().filter(a -> !(a instanceof DataInterface)).toList();
  }

  /**
   * returns all the graph's fifos that are not linked to a data interface
   *
   * @param graph
   *          the graph
   * @return the list of fifos
   */
  private List<Fifo> getRelevantFifos(PiGraph graph) {
    return graph.getFifos().stream()
        .filter(f -> !(f.getSource() instanceof DataInterface || f.getTarget() instanceof DataInterface)).toList();
  }

  private int getProdRate(Fifo fifo) {
    return (int) fifo.getSourcePort().getPortRateExpression().evaluateAsLong();
  }

  private int getConsRate(Fifo fifo) {
    return (int) fifo.getTargetPort().getPortRateExpression().evaluateAsLong();
  }

  /**
   * Runs a first call to the propagation method to check the model's viability. Prints an error message if failure.
   *
   * @param solver
   *          the parameterized solver
   */
  private void runInitialPropagation(Solver solver) {
    try {
      System.out.println("Starting initial propagation (might take some time)");
      solver.propagate();
      System.out.println("Initial propagation finished");
    } catch (final ContradictionException e) {
      e.printStackTrace();
      System.out.println("Initial propagation failed : model might be unsolvable.");
    }
  }

  /**
   * Creates the constraints forcing periods to be multiples of a base computed from the fifo's prod and cons rates.
   *
   * @param prod_rate
   *          the fifo's rate of production
   * @param cons_rate
   *          the fifo's rate of consumption
   * @param sourceTimings
   *          the timings structure for the source actor
   * @param targetTimings
   *          the timings structure for the target actor
   * @param model
   *          the model
   */
  private void generatePeriodBasisConstraints(int prod_rate, int cons_rate, ActorTimings sourceTimings,
      ActorTimings targetTimings, Model model) {

    final int basisOfTp = (int) (prod_rate / gcd(prod_rate, cons_rate));
    final int basisOfTc = (int) (cons_rate / gcd(prod_rate, cons_rate));

    // the periods must be a multiple of their base, starting from the minimum allowed : their latency
    // we generate 100 possible values, assuming it is unlikely that period > 100 * latency (C'est au pif !)
    // TODO : j'ai peur que cette borne soit trop petite dans beaucoup de cas
    // TODO : calculer une bonne borne supérieure
    if (basisOfTp != 1) {
      // Méthode 1 : restriction de l'ensemble de définition de la période à une liste de multiples de la base
      // Avantages : une seule contrainte par période

      // Inconvénient : un ensemble trop grand est converti en intervalle [LB, UB] --> on perd l'élagage de valeurs
      final int startMultiple = (sourceTimings.executionTime + basisOfTp - 1) / basisOfTp;
      final int[] possibleTpValues = IntStream.rangeClosed(startMultiple, startMultiple * 100).map(n -> n * basisOfTp)
          .toArray();
      model.member(sourceTimings.period_var, possibleTpValues).post();

      // Méthode 2 : forcer la période à être un multiple de la base
      // Avantage : on ne perd pas l'élagage de valeur
      // Inconvénient : multiplication des contraintes à évaluer sur la période

      // final IntVar TpMultiple = model.intVar("TpMultiple_" + fifo.getId(), 1,
      // MAX_MULTIPLE * source.executionTime / basisOfTp);
      // source.period_var.eq(TpMultiple.mul(basisOfTp)).post();

    }
    if (basisOfTc != 1) {
      // Même commentaire qu'au-dessus
      final int startMultiple = (targetTimings.executionTime + basisOfTc - 1) / basisOfTc;
      final int[] possibleTpValues = IntStream.rangeClosed(startMultiple, startMultiple * 100).map(n -> n * basisOfTc)
          .toArray();
      model.member(targetTimings.period_var, possibleTpValues).post();

      // final IntVar TcMultiple = model.intVar("TpMultiple_" + fifo.getId(), 1,
      // MAX_MULTIPLE * target.executionTime / basisOfTc);
      // target.period_var.eq(TcMultiple.mul(basisOfTc)).post();
    }
  }

  private ActorTimings initActorTimings(AbstractActor actor, Scenario scenario, Component component,
      Map<AbstractVertex, Long> brv, Model model) {
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

    // TODO : tous les acteurs sont liés par une égalité de débit : dès qu'on a fixé la période d'un acteur, toutes les
    // autres en découlent ! On n'a donc besoin de spécifier qu'une variable de période, et tout le reste n'est
    // qu'à calculer.
    // arbitrary limit : period <= 100 * executionTime
    res.period_var = model.intVar("period_" + actor.getName() + "_var", res.executionTime,
        MAX_MULTIPLE * res.executionTime);

    // must start early enough to finish all its periods (=brv) before MAX_CYCLE
    // could even be bounded by its followers' latencies sum
    res.startDate_var = model.intVar("start_" + actor.getName() + "_var", 0,
        CYCLE_MAX - res.executionTime * res.repetitionCount);

    // must have at least executed all its firings by end time
    res.endDate_var = model.intVar("end_" + actor.getName() + "_var", 0 /* res.executionTime * res.repetitionCount */,
        CYCLE_MAX);

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
    final List<ExecutableActor> actors = getNonDataInterfaceActors(piGraph);

    // for now, we will not consider fifos linking actors from/to data interfaces.
    // It may by interesting to model them as actors with a start date equal to the comm. time and rate of comm size
    final List<Fifo> fifos = getRelevantFifos(piGraph);

    final Map<AbstractVertex, Long> brv = PiBRV.compute(piGraph, BRVMethod.LCM);

    // the graph is supposed to be mapped to a single PE type (FPGA, CPU, DSP...)
    final Component Fpga = scenario.getPossibleMappings(piGraph).getFirst().getComponent();

    final Model model = new Model("Period computing");

    // Attention ! Le déclarer comme IntVar[] comme ça créerait de nouvelle variables qui devraient être mises à .eq()
    // mieux : déclarer un tableau mais pas de variable choco
    final IntVar[] latencies = new IntVar[actors.size()];
    final IntVar[] periods = new IntVar[actors.size()];

    int i = 0;
    // TODO faut-il mettre le startDate du 1er acteur à 0 ?
    for (final AbstractActor actor : actors) {
      final ActorTimings at = initActorTimings(actor, scenario, Fpga, brv, model);

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
    // model.arithm(at.startDate_var, ">=", predTiming.startDate_var).post(); // Vraiment utile comme contrainte ?
    // }
    // }

    // on met en place le modèle pour chaque fifo
    for (final Fifo fifo : fifos) {

      // ================================================================
      // 1. Paramètres des acteurs
      // ================================================================
      final AbstractActor sourceActor = fifo.getSource();
      final ActorTimings sourceTimings = schedule.get(sourceActor);
      final AbstractActor targetActor = fifo.getTarget();
      final ActorTimings targetTimings = schedule.get(targetActor);

      final int prod_rate = getProdRate(fifo);
      final int cons_rate = getConsRate(fifo);

      generatePeriodBasisConstraints(prod_rate, cons_rate, sourceTimings, targetTimings, model);

      // equal rates constraints --> Tc * rp = Tp * rc
      // redondant avec le calcul des valeurs de possibleTpValues et possibleTcValues ?
      final IntVar left = model.intVar("left_" + fifo.getId() + "_var", 0, CYCLE_MAX);
      final IntVar right = model.intVar("right_" + fifo.getId() + "_var", 0, CYCLE_MAX);
      model.times(sourceTimings.period_var, cons_rate, left).post();
      model.times(targetTimings.period_var, prod_rate, right).post();
      model.arithm(left, "=", right).post();

      // -- Computing breakpoints positions --

      // nombre de breakpoints de chaque
      final int nbBreakpointsProd = (int) (lcm(prod_rate, cons_rate) / prod_rate); // bornes : [1 ; cons_rate]
      final int nbBreakpointsCons = (int) (lcm(prod_rate, cons_rate) / cons_rate); // bornes : [1 ; prod_rate]
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
          breakpoints[bk - 1].eq(sourceTimings.startDate_var.add(sourceTimings.period_var.mul(bk)).sub(prod_rate))
              .post();
        }
      } else {
        // consumer breakpoints
        for (int bk = 1; bk <= nbBreakpointsCons; bk++) {
          // delay_cons + (bk - 1) * periodCons + taux_cons
          breakpoints[bk - 1].eq(targetTimings.startDate_var.add(targetTimings.period_var.mul(bk - 1)).add(cons_rate))
              .post();
        }
      }

      // les valeurs aux breakpoints
      final IntVar[] cumP = model.intVarArray("cumP_" + fifo.getId() + "_var", nbBreakpoints, 0, TOKENS_MAX);
      final IntVar[] cumC = model.intVarArray("cumC_" + fifo.getId() + "_var", nbBreakpoints, 0, TOKENS_MAX);

      final IntVar zero = model.intVar(0);
      final IntVar rate_cons = model.intVar(cons_rate);

      for (int bk = 1; bk <= nbBreakpoints; bk++) {
        final IntVar t = breakpoints[bk - 1];

        // -------------------------------------------
        // -- cumulated production at breakpoint bk --
        // -------------------------------------------

        // t - delay_prod : always positive, since breakpoints at prod are after it started, and cons starts after prod
        // at most the biggest breakpoint, which are capped to CYCLE_MAX
        final IntVar delta_prod = model.intVar("deltaProd_" + fifo.getId() + "_" + bk + "_var", 0, CYCLE_MAX);

        final IntVar prodInPeriod = model.intVar("inPeriodProd_" + fifo.getId() + "_" + bk + "_var", 0, prod_rate);
        final IntVar prodFromPreviousPeriods = model
            .intVar("prodFromPreviousPeriods_" + fifo.getId() + "_" + bk + "_var", 0, TOKENS_MAX);

        model.arithm(delta_prod, "=", t, "-", sourceTimings.startDate_var).post();

        // in-period production

        if (chosenBreakpoints.equals("Prod")) {
          // when the breakpoints are on the producer, it has not produced tokens yet
          // ==> prodInPeriod = 0 for all breakpoints
          model.arithm(prodInPeriod, "=", zero).post();
        } else {
          // Non-simplified formula : prodInPeriod = (t - delay_prod) % periodProd - periodProd + taux_prod
          final IntVar inter2 = model.intVar("inter2_" + bk + "_var", 0, TOKENS_MAX); // forced to 0 or more later

          inter2.eq((delta_prod.mod(sourceTimings.period_var)).sub(sourceTimings.period_var).add(prod_rate)).post();
          model.max(prodInPeriod, inter2, zero).post(); // force it to be 0 or more
        }

        // Production from previous periods : ((t - delay_prod) / periodProd) * taux_prod
        // choco operates on natural numbers so the fraction results are automatically floored
        prodFromPreviousPeriods.eq((delta_prod.div(sourceTimings.period_var)).mul(prod_rate)).post();

        // prodFromPreviousPeriods + prodInPeriod
        model.arithm(cumP[bk - 1], "=", prodFromPreviousPeriods, "+", prodInPeriod).post();

        // ---------------------------------
        // -- cumulated consumption at bk --
        // ---------------------------------

        // t - delay_cons
        // lower bound : the consumer is delayed from the producer by at least the time it takes to start producing
        // this time is prod.executionTime - rate (at least ! In general : prod.period - rate)
        // Upper bound : the cons should start before the prod has finished an iteration's worth of firings I guess ?
        final IntVar delta_cons = model.intVar("deltaCons_" + fifo.getId() + "_" + bk + "_var",
            0 /* source.executionTime - prod_rate */, CYCLE_MAX /* target.repetitionCount * source.executionTime */);

        final IntVar consInPeriod = model.intVar("inPeriodCons_" + fifo.getId() + "_" + bk + "_var", 0, cons_rate);
        final IntVar consFromPreviousPeriods = model
            .intVar("consFromPreviousPeriods_" + fifo.getId() + "_" + bk + "_var", 0, TOKENS_MAX);

        // To have t - delay_cons be 0 or more
        model.max(delta_cons, t.sub(targetTimings.startDate_var).intVar(), zero).post();

        if (chosenBreakpoints.equals("Cons")) {
          // in this special case, the breakpoint happens always after the consumer has consumed
          // a period's worth of tokens
          // ==> consInPeriod = rate_cons
          model.arithm(consInPeriod, "=", cons_rate).post();

        } else {
          // this intermediate variable can be negative
          final IntVar inter4 = model.intVar("inter4_" + bk + "_var", -TOKENS_MAX, TOKENS_MAX);

          // (t - delay_cons) % periodCons : the consumption in this period
          inter4.eq(delta_cons.mod(targetTimings.period_var)).post();

          // min(x, rate_cons) <= rate_cons
          model.min(consInPeriod, inter4, rate_cons).post();
        }

        // ((t - delay_cons) / periodCons) * taux_cons
        consFromPreviousPeriods.eq(delta_cons.div(targetTimings.period_var).mul(cons_rate)).post();

        // consFromPreviousPeriods + consInPeriod
        model.arithm(cumC[bk - 1], "=", consFromPreviousPeriods, "+", consInPeriod).post();

        model.arithm(cumP[bk - 1], ">=", cumC[bk - 1]).post();
      }

    }

    // objectif : optimiser la latence = la date de fin du dernier acteur relative à une période
    // on pourrait utiliser le chemin critique, mais pour le moment je vais juste optimiser la fin d'exécution de
    // l'acteur le plus tardif
    final IntVar latency = model.max("latency", latencies);

    // optimiser periods avant latencies permet de bien réduire l'espace d'état avant
    final IntVar[] variablesToOptimize = Stream.of(new IntVar[] { latency }, periods, latencies).flatMap(Arrays::stream)
        .toArray(IntVar[]::new);

    // --------
    // Solution
    // --------
    final Solver solver = model.getSolver();

    if (monitor) {
      solver.showDashboard();
      solver.showStatisticsDuringResolution(1000);
      System.out.printf(" Number of variables : %d %n Number of constraints : %d %n", model.getNbVars(),
          model.getNbCstrs());
      // solver.verboseSolving(1000); // marche pas
    }
    if (logs) {
      solver.showContradiction();
      solver.showDecisions();
      solver.showContradiction();
      setLoggingMonitors(model, solver);
    }

    solver.showStatistics();
    solver.limitTime("5s");

    // va optimiser les variables dans l'ordre d'apparition dans le tableau
    // TODO : vérifier si on peut donner des priorités aux contraintes, pour vérifier les plus contraignantes en
    // premières et élaguer l'arbre des possibles le plus vite possible
    solver.setSearch(Search.inputOrderLBSearch(variablesToOptimize));
    BlackBoxConfigurator.forCOP();

    model.displayPropagatorOccurrences(); // pour vérifier que des propagateurs safe sont utilisés

    Solution solution = new Solution(model);

    runInitialPropagation(solver);

    if (logs) {
      // System.out.printf("%s %n", model.toString());
    }

    // AFFICHER TOUTES LES SOLUTIONS JUSQU'À TROUVER L'OPTIMALE ?
    solution = solver.findOptimalSolution(latency, Model.MINIMIZE);

    if (solver.getSolutionCount() != 0) {
      saveAndPrintResults(solution, latency, schedule);
    } else {
      printFailureAndLog(model);
    }

    final StatEditorSynthesisTask truc = new StatEditorSynthesisTask();
    final Map<String, Object> inputs = new HashMap<>();
    inputs.put("scenario", scenario);
    inputs.put("architecture", scenario.getDesign());
    inputs.put("algorithm", piGraph);
    // truc.execute(null, null, null, null, null)
    return new AnalysisResultFPGA(piGraph, null, null);
  }

  private void saveAndPrintResults(Solution solution, IntVar latency, Map<AbstractActor, ActorTimings> schedule) {
    final FpgaSchedule result = new FpgaSchedule(solution.getIntVal(latency));
    System.out.println("Solution found !");
    for (final var res : schedule.entrySet()) {
      res.getValue().storeResults(solution);
      res.getValue().printSchedule(res.getKey());
      result.addActorTimings(res.getValue());
    }
    System.out.println("latency = " + result.latency);
    System.out.println("hyperperiod = " + ppcm(schedule.values().stream().map(a -> (long) a.period).toList()));
    System.out.print("\n");
  }

  private void printFailureAndLog(Model model) {
    System.out.println("No solution was found !");
    final List<String> truc = Arrays.asList(model.getVars()).stream().filter(v -> v.getName().endsWith("_var"))
        .map(Object::toString).toList();
    System.out.printf("%s %n", String.join("\n", truc));
  }

  private void setLoggingMonitors(Model model, Solver solver) {
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
}
