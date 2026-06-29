package org.preesm.algorithm.schedule.fpga;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
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
import org.chocosolver.solver.search.strategy.BlackBoxConfigurator;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.IVariableMonitor;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.RealVar;
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
  static final int CYCLE_MAX             = 100_000_000; // chemin critique et sommer latence*brv pour chaque acteur ?
  static final int TOKENS_MAX            = 100_000;     // arbitrary
  static final int MAX_PERIOD_MULTIPLIER = 100;

  final boolean monitor = true;
  final boolean logs    = false;

  public FPGAConstraintScheduler() {
    super();
  }

  /**
   * Structure storing the timing information for each actor in the graph
   */
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

  /**
   * Structure holding an actor's timing information, both the variables to be used in solving, and the result integers
   * to be used afterward by PREESM
   */
  public class ActorTimings {
    // Solving variables
    private IntVar period_var;
    private IntVar startDate_var;
    private IntVar endDate_var;
    private int    basisOfPeriod = 1;
    private IntVar basisMultiplier;

    // Timing variables
    public int repetitionCount;
    public int executionTime;
    public int initiationInterval;
    public int period;
    public int startDate;
    public int endDate;

    ActorTimings() {
      super();
    }

    public void printSchedule(AbstractActor a) {
      System.out.printf("%s : start=%d  \t  latency=%d  \t  II=%d  \t  end=%d  \t  period=%d  %n", a.getName(),
          startDate, executionTime, initiationInterval, endDate, period);
    }

    public void storeResults(Solution s) {
      period = s.getIntVal(period_var);
      startDate = s.getIntVal(startDate_var);
      endDate = s.getIntVal(endDate_var);

      // no longer needed after solving
      period_var = null;
      startDate_var = null;
      endDate_var = null;
    }
  }

  private ActorTimings initActorTimings(AbstractActor actor, Scenario scenario, Component component,
      Map<AbstractVertex, Long> brv, Model model) {
    final ActorTimings res = new ActorTimings();

    res.repetitionCount = brv.get(actor).intValue();

    // default value for broadcast and round buffer actors
    if (actor instanceof SpecialActor) {
      // a special actor's latency is estimated to be its max rate of input/output.
      // TODO est-ce que ça marche tout le temps ? probablement pas...
      res.executionTime = actor.getAllDataPorts().stream().map(dp -> (int) dp.getPortRateExpression().evaluateAsLong())
          .max(Integer::compare).orElse(1);
      res.initiationInterval = 1;

      // TODO : tous les acteurs sont liés par une égalité de débit : dès qu'on a fixé la période d'un acteur, toutes
      // les autres en découlent ! On n'a donc besoin de spécifier qu'une variable de période, et tout le reste n'est
      // qu'à calculer.
      // arbitrary limit : period <= 100 * executionTime for regular actors
      // arbitrary limit : (CYCLE_MAX / BRV) / 10 for special actors
      res.period_var = model.intVar("period_" + actor.getName() + "_var", res.initiationInterval,
          CYCLE_MAX / (10 * res.repetitionCount));

    } else {
      res.executionTime = (int) scenario.getTimings().evaluateTimingOrDefault(actor, component,
          TimingType.EXECUTION_TIME);
      res.initiationInterval = (int) scenario.getTimings().evaluateTimingOrDefault(actor, component,
          TimingType.INITIATION_INTERVAL);

      // TODO : tous les acteurs sont liés par une égalité de débit : dès qu'on a fixé la période d'un acteur, toutes
      // les autres en découlent ! On n'a donc besoin de spécifier qu'une variable de période, et tout le reste n'est
      // qu'à calculer.
      // arbitrary limit : period <= 100 * executionTime for regular actors
      res.period_var = model.intVar("period_" + actor.getName() + "_var", res.initiationInterval,
          MAX_PERIOD_MULTIPLIER * res.executionTime);
    }

    // CONSTRAINT : the period is proportional to its basis. This basis will be updated as we iterate over the fifos.
    // TODO : borne complètement arbitraire !
    res.basisMultiplier = model.intVar("basisMultiplier_" + actor.getName() + "_var", 0, 100_000);
    model.arithm(res.period_var, "=", res.basisMultiplier, "*", res.basisOfPeriod).post();

    // must start early enough to finish all its periods (=brv) before MAX_CYCLE
    // could even be bounded by its followers' latencies sum
    res.startDate_var = model.intVar("start_" + actor.getName() + "_var", 0,
        CYCLE_MAX - res.executionTime * res.repetitionCount); // affinable mais ça fera l'affaire

    // must have at least executed all its firings by end time
    res.endDate_var = model.intVar("end_" + actor.getName() + "_var",
        0 /* res.initiationInterval * res.repetitionCount */, CYCLE_MAX);

    // endDate = startDate + T * (repetition_count - 1) + execution_time
    // le dernier token est produit à la fin de l'exécution du dernier acteur, qui est possiblement bien avant la fin
    // de sa période = le début du prochain firing
    res.endDate_var.eq(res.startDate_var.add(res.period_var.mul(res.repetitionCount - 1).add(res.executionTime)))
        .post();

    return res;
  }

  // -------------------------------------------
  // ------------ Helper functions -------------
  // -------------------------------------------

  public static long gcd(long a, long b) {
    if (b == 0) {
      return a;
    }
    return gcd(b, a % b);
  }

  public static long lcm(long a, long b) {
    return a * b / gcd(a, b);
  }

  public static int lcm(int a, int b) {
    return (int) lcm((long) a, (long) b);
  }

  public static long lcm(List<Long> numbers) {
    long result = numbers.getFirst();

    for (int i = 1; i < numbers.size(); i++) {
      result = lcm(result, numbers.get(i));
    }

    return result;
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

  // -------------------------------------------
  // ------------ Scheduling method ------------
  // -------------------------------------------

  @Override
  /**
   * The method assumes it is scheduling a flat graph. If it encounters a cluster, it will be treated as an actor.
   */
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

    // We know an actor cannot start before its predecessors.
    // This should help cull the search space. Seems to have a considerable impact in some cases.
    for (final AbstractActor actor : actors) {
      final var predecessors = actor.getDirectPredecessors().stream().filter(schedule::containsKey).toList();
      for (final var pred : predecessors) {
        model.arithm(schedule.get(actor).startDate_var, ">=", schedule.get(pred).startDate_var).post();
      }
    }

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

      updatePeriodsBases(prod_rate, cons_rate, sourceTimings, targetTimings);

      // CONSTRAINT : equal rates : Tc / rc = Tp / rp --> Tc * rp = Tp * rc
      // Note : pas redondant avec d'autres contraintes ! J'ai trouvé un cas où l'enlever casse la résolution.
      // Allez savoir pourquoi, mais sans elle le modèle met tous les start_date à 0... Peut-être que cette contrainte
      // fait un lien implicite entre des variables, si oui il vaudrait mieux un lien explicite et la dégager.

      final double precision = 1d; // since we work on int multiplication, no need to waste precision on decimal
      final RealVar left = model.realVar("left_" + fifo.getId() + "_var", 0,
          (double) cons_rate * sourceTimings.period_var.getUB(), precision);
      final RealVar right = model.realVar("right_" + fifo.getId() + "_var", 0,
          (double) prod_rate * targetTimings.period_var.getUB(), precision);

      final double ratesGcd = gcd(prod_rate, cons_rate);

      final RealVar sourcePeriodReal = model.realVar(sourceTimings.period_var.getLB(), sourceTimings.period_var.getUB(),
          precision);
      model.eq(sourcePeriodReal, sourceTimings.period_var).post();
      left.eq(sourcePeriodReal.mul(cons_rate / ratesGcd)).post();
      // left.eq(sourcePeriodReal.div(prod_rate / ratesGcd)).post();

      final RealVar targetPeriodReal = model.realVar(targetTimings.period_var.getLB(), targetTimings.period_var.getUB(),
          precision);
      model.eq(targetPeriodReal, targetTimings.period_var).post();
      right.eq(targetPeriodReal.mul(prod_rate / ratesGcd)).post();
      // right.eq(targetPeriodReal.div(cons_rate / ratesGcd)).post();

      left.eq(right).post();

      // -- Computing breakpoints positions --

      // nombre de breakpoints de chaque
      final int nbBreakpointsProd = lcm(prod_rate, cons_rate) / prod_rate; // bornes : [1 ; cons_rate]
      final int nbBreakpointsCons = lcm(prod_rate, cons_rate) / cons_rate; // bornes : [1 ; prod_rate]
      final String chosenBreakpoints = nbBreakpointsProd <= nbBreakpointsCons ? "Prod" : "Cons";
      final int nbBreakpoints = Math.min(nbBreakpointsProd, nbBreakpointsCons);

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
        // IntStream.range(1, 1 + TOKENS_MAX / cons_rate).map(n -> n * cons_rate).toArray()
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

        // CONSTRAINT : production superior or equal to consumption at the breakpoint
        model.arithm(cumP[bk - 1], ">=", cumC[bk - 1]).post();
      }
    }

    // objectif : optimiser la latence = la date de fin du dernier acteur relative à une période
    // on pourrait utiliser le chemin critique, mais pour le moment je vais juste optimiser la fin d'exécution de
    // l'acteur le plus tardif
    // TODO : trouver des contraintes pour réduire l'espace d'état de latency, parce que là on ne fait qu'énumérer comme
    // des cons.
    final IntVar latency = model.max("latency", latencies);

    // optimiser periods avant latencies permet de bien réduire l'espace d'état avant
    // Astuce : minimiser d'abord l'hyperpériode, dont on peut grandement réduire l'espace d'états.
    final IntVar[] variablesToOptimize = Stream.of(new IntVar[] { latency }, periods, latencies).flatMap(Arrays::stream)
        .toArray(IntVar[]::new);

    // --------
    // Solution
    // --------
    final Solver solver = model.getSolver();
    // solver.makeCompleteStrategy(true); // Possiblement utile ! Enquêter.
    solver.observeSolving();
    // solver.toCSV();
    // Pour suivre l'arbre d'exploration et en sortir un .dot graphviz
    final Closeable searchTreeFile = solver.outputSearchTreeToGraphviz("/home/jamorin/recherche.dot");

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
    solver.limitTime("10s");

    // va optimiser les variables dans l'ordre d'apparition dans le tableau
    // TODO : vérifier si on peut donner des priorités aux contraintes, pour vérifier les plus contraignantes en
    // premières et élaguer l'arbre des possibles le plus vite possible
    final String strategy = "inputOrderLBSearch";
    switch (strategy) {
      case "minDomLBSearch": // trop lent : échoue à l'algo test pour size=10000, max=10s
        solver.setSearch(Search.minDomLBSearch(variablesToOptimize));
        break;
      case "inputOrderLBSearch":
        solver.setSearch(Search.inputOrderLBSearch(variablesToOptimize));
        break;
      default:
        solver.setSearch(Search.inputOrderLBSearch(variablesToOptimize));
        break;
    }
    BlackBoxConfigurator.forCOP(); // Utile ? J'ai l'impression que non...

    model.displayPropagatorOccurrences(); // pour vérifier que des propagateurs safe sont utilisés

    Solution solution = new Solution(model);

    runInitialPropagation(solver);

    if (logs) {
      System.out.printf("%s %n", model.toString());
    }

    // AFFICHER TOUTES LES SOLUTIONS JUSQU'À TROUVER L'OPTIMALE ?
    solution = solver.findOptimalSolution(latency, Model.MINIMIZE);

    if (solver.getSolutionCount() != 0) {
      saveAndPrintResults(solution, latency, schedule);
      // SolvingStatisticsFlow.toJSON(solver); // marche pas, dommage
    } else {
      printFailureAndLog(model);
    }
    try {
      searchTreeFile.close();
    } catch (final IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    final StatEditorSynthesisTask truc = new StatEditorSynthesisTask();
    final Map<String, Object> inputs = new HashMap<>();
    inputs.put("scenario", scenario);
    inputs.put("architecture", scenario.getDesign());
    inputs.put("algorithm", piGraph);
    // truc.execute(null, null, null, null, null)
    return new AnalysisResultFPGA(piGraph, null, null);
  }

  // -------------------------------------------
  // ------------ Solver functions -------------
  // -------------------------------------------

  /**
   * Updates the actors' basis for their period to be lcm(current basis, fifo-induced basis)
   *
   * @param prod_rate
   *          the fifo's rate of production
   * @param cons_rate
   *          the fifo's rate of consumption
   * @param sourceTimings
   *          the timings structure for the source actor
   * @param targetTimings
   *          the timings structure for the target actor
   */
  private void updatePeriodsBases(int prod_rate, int cons_rate, ActorTimings sourceTimings,
      ActorTimings targetTimings) {

    final int basisOfTp = (int) (prod_rate / gcd(prod_rate, cons_rate));
    final int basisOfTc = (int) (cons_rate / gcd(prod_rate, cons_rate));

    sourceTimings.basisOfPeriod = lcm(sourceTimings.basisOfPeriod, basisOfTp);
    targetTimings.basisOfPeriod = lcm(targetTimings.basisOfPeriod, basisOfTc);

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
      System.out.println("Initial propagation caught a contradiction : model might be unsolvable.");
    }
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
    System.out.println("hyperperiod = " + lcm(schedule.values().stream().map(a -> (long) a.period).toList()));
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
