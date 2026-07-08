package org.preesm.algorithm.schedule.fpga;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.search.strategy.BlackBoxConfigurator;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.RealVar;
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
  static int       CYCLE_MAX      = 100_000_000;    // chemin critique et sommer latence*brv de chaque acteur ?
  static int       TOKENS_MAX     = CYCLE_MAX / 10; // arbitrary
  static int       MAX_START_TIME = CYCLE_MAX / 2;  // abitrary
  static final int THRESHOLD_RV   = 50;

  final boolean logsLv1    = true;
  final boolean logsLv2    = false;
  boolean       logsInFile = false;

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

    public void printSchedule(AbstractActor a, PrintStream writer) {
      writer.printf("%s : start=%d  \t  latency=%d  \t  II=%d  \t  end=%d  \t  period=%d  %n", a.getName(), startDate,
          executionTime, initiationInterval, endDate, period);
    }

    public void storeResults(Solution s) {
      period = s.getIntVal(period_var);
      startDate = s.getIntVal(startDate_var);
      endDate = s.getIntVal(endDate_var);

      // no longer needed after solving
      if (!(logsLv1 || logsLv2)) {
        period_var = null;
        startDate_var = null;
        endDate_var = null;
      }
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

      // TODO mettre : period_var = other_period * tp/tp for actor in connecte_actors ?
      // on n'aurait ainse que des views et une seule période libre, qui pourrait être celle avec le moins de
      // combinaisons possibles.
      res.period_var = model.intVar("period_" + actor.getName() + "_var", res.initiationInterval,
          CYCLE_MAX / res.repetitionCount);

    } else {
      res.executionTime = (int) scenario.getTimings().evaluateTimingOrDefault(actor, component,
          TimingType.EXECUTION_TIME);
      res.initiationInterval = (int) scenario.getTimings().evaluateTimingOrDefault(actor, component,
          TimingType.INITIATION_INTERVAL);

      // TODO : tous les acteurs sont liés par une égalité de débit : dès qu'on a fixé la période d'un acteur, toutes
      // les autres en découlent ! On n'a donc besoin de spécifier qu'une variable de période, et tout le reste n'est
      // qu'à calculer.

      // arbitrary UB to reduce domain size
      res.period_var = model.intVar("period_" + actor.getName() + "_var", res.initiationInterval,
          CYCLE_MAX / res.repetitionCount);
    }

    // CONSTRAINT : the period is proportional to its basis. This basis will be updated as we iterate over the fifos.
    res.basisMultiplier = model.intVar("basisMultiplier_" + actor.getName() + "_var", 0,
        res.period_var.getUB() / res.basisOfPeriod);
    model.arithm(res.period_var, "=", res.basisMultiplier, "*", res.basisOfPeriod).post();

    // must start early enough to finish all its periods (=brv) before MAX_CYCLE
    // could even be bounded by its followers' latencies sum
    res.startDate_var = model.intVar("start_" + actor.getName() + "_var", 0,
        CYCLE_MAX - res.executionTime * res.repetitionCount); // affinable mais ça fera l'affaire

    // must have at least executed all its firings by end time
    res.endDate_var = model.intVar("end_" + actor.getName() + "_var",
        res.startDate_var.getLB() + res.initiationInterval * res.repetitionCount, CYCLE_MAX);

    // endDate = startDate + T * (repetition_count - 1) + execution_time
    // le dernier token est produit à la fin de l'exécution du dernier acteur, qui est possiblement bien avant la fin
    // de sa période = le début du prochain firing
    res.endDate_var.eq(res.startDate_var.add(res.period_var.mul(res.repetitionCount - 1).add(res.executionTime)))
        .post();

    return res;
  }

  private void updateActorsDomain(List<ExecutableActor> actors, Map<AbstractActor, ActorTimings> schedule,
      Model model) {
    for (final var actor : actors) {
      final ActorTimings at = schedule.get(actor);
      if (at.repetitionCount >= THRESHOLD_RV) {
        // période max : CYCLE_MAX / RV
        // période min : II
        // nombre d'éléments à énumérer : 1 + (CYCLE_MAX / RV - II) / basisOfPeriod
        // however we can also reuse the initial boundaries :
        // range start : max(period_var.LB / basisOfPeriod, II / basisOfPeriod)
        // range end : min(period_var.UB / basisOfPeriod , 1 + (CYCLE_MAX / RV - II) / basisOfPeriod)
        final int range_start = Math.max(at.period_var.getLB() / at.basisOfPeriod,
            at.initiationInterval / at.basisOfPeriod);
        final int range_end = Math.min(at.period_var.getUB() / at.basisOfPeriod,
            1 + (CYCLE_MAX / at.repetitionCount - at.initiationInterval) / at.basisOfPeriod);

        model.member(at.period_var, IntStream.range(range_start, range_end).map(n -> n * at.basisOfPeriod).toArray())
            .post();
      }

      // on peut mettre à jour les domaines de start : startDate >= pred.startDate for pred in predecessors
      // N'a pas vraiment d'impact mais je la laisse, ell ne fait pas de mal.
      actor.getDirectPredecessors().stream().filter(schedule::containsKey).map(schedule::get)
          .forEach(pt -> model.arithm(at.startDate_var, ">=", pt.startDate_var).post());

    }

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

      final RealVar sourcePeriodReal = model.realVar("sourcePeriodReal_" + fifo.getId(),
          sourceTimings.period_var.getLB(), sourceTimings.period_var.getUB(), precision);
      model.eq(sourcePeriodReal, sourceTimings.period_var).post();
      left.eq(sourcePeriodReal.mul(cons_rate / ratesGcd)).equation().post();
      // left.eq(sourcePeriodReal.div(prod_rate / ratesGcd)).post();

      final RealVar targetPeriodReal = model.realVar("targetPeriodReal_" + fifo.getId(),
          targetTimings.period_var.getLB(), targetTimings.period_var.getUB(), precision);
      model.eq(targetPeriodReal, targetTimings.period_var).post();
      right.eq(targetPeriodReal.mul(prod_rate / ratesGcd)).equation().post();
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
        final IntVar delta_prod = model.intVar("deltaProd_" + fifo.getId() + "_" + bk + "_var", 0, MAX_START_TIME);

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
          final IntVar prod_modulo = model.intVar("prod_modulo_" + fifo.getId(), 0,
              Math.min(sourceTimings.period_var.getUB(), TOKENS_MAX));
          final IntVar inter2 = model.intVar("inter2_" + bk + "_var", 0, TOKENS_MAX); // forced to 0 or more later

          // prod_modulo = delta_prod % period_var
          model.mod(delta_prod, sourceTimings.period_var, prod_modulo).post();

          inter2.eq(prod_modulo.sub(sourceTimings.period_var).add(prod_rate)).post();

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
            0 /* source.executionTime - prod_rate */,
            MAX_START_TIME /* target.repetitionCount * source.executionTime */);

        // the consumption in-period is bounded between 0 and cons_rate
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
          // this intermediate variable cannot be negative since we force delta_cons >= 0
          final IntVar modulo_cons = model.intVar("modulo_cons_" + bk + "_var", 0,
              Math.min(targetTimings.period_var.getUB(), TOKENS_MAX));

          // (t - delta_cons) % periodCons : the consumption in this period
          model.mod(delta_cons, targetTimings.period_var, modulo_cons).post();

          // min(x, rate_cons) <= rate_cons
          model.min(consInPeriod, modulo_cons, rate_cons).post();
        }

        // ((t - delay_cons) / periodCons) * taux_cons
        consFromPreviousPeriods.eq(delta_cons.div(targetTimings.period_var).mul(cons_rate)).post();

        // consFromPreviousPeriods + consInPeriod
        model.arithm(cumC[bk - 1], "=", consFromPreviousPeriods, "+", consInPeriod).post();

        // CONSTRAINT : production superior or equal to consumption at the breakpoint
        model.arithm(cumP[bk - 1], ">=", cumC[bk - 1]).post();
      }
    }
    // now that the periods' bases have all been computed, we can restrict the periods' domains
    updateActorsDomain(actors, schedule, model);

    // objectif : optimiser la latence = la date de fin du dernier acteur relative à une période
    // on pourrait utiliser le chemin critique, mais pour le moment je vais juste optimiser la fin d'exécution de
    // l'acteur le plus tardif
    // TODO : trouver des contraintes pour réduire l'espace d'état de latency, parce que là on ne fait qu'énumérer comme
    // des cons.
    final IntVar latency = model.max("latency", latencies);

    // l'hyperpériode ne fait que ralentir l'exemple test et n'aide pas vraiment pour wavelet, au moins sous 10s.
    final int basis = schedule.values().stream().map(at -> at.basisOfPeriod).reduce(1, (a, b) -> a * b);
    final IntVar hyperPeriod;
    if (basis > CYCLE_MAX / 100) {
      hyperPeriod = model.intVar("hyperPeriod", IntStream.range(1, CYCLE_MAX / basis).map(n -> n * basis).toArray());
    } else {
      hyperPeriod = model.intVar("hyperPeriod", basis, CYCLE_MAX);
    }

    for (final var at : schedule.values()) {
      model.arithm(hyperPeriod, "=", at.period_var, "*", at.repetitionCount).post();
    }

    // optimiser periods avant latencies permet de bien réduire l'espace d'état avant
    // Astuce : minimiser d'abord l'hyperpériode, dont on peut grandement réduire l'espace d'états.
    final IntVar[] variablesToOptimize = Stream.of(new IntVar[] { latency }, periods, latencies).flatMap(Arrays::stream)
        .toArray(IntVar[]::new);

    // --------
    // Solution
    // --------
    final Solver solver = model.getSolver();

    PrintStream writer = System.out;
    PrintStream resultsCsv = null;
    final Date date = new Date();

    final File dir = new File("/home/jamorin/Documents/these/data/fpga_scheduling/choco-run__" + piGraph.getName()
        + "__" + date.getMonth() + "-" + date.getDay() + "-" + date.getHours() + "h" + date.getMinutes());
    dir.mkdirs();
    final String fileName = dir.getAbsolutePath() + "/choco_solver_logs";
    if (logsInFile) {
      try {
        writer = new PrintStream(fileName + ".txt");
        System.out.printf("Writing logs to file \"%s\" %n", fileName);
      } catch (final FileNotFoundException e) {
        System.out.println("Could not create file " + fileName);
        writer = System.out;
      }
    }

    try {
      resultsCsv = new PrintStream(fileName + ".csv");
      resultsCsv.println(
          "CYCLE_MAX ; TOKENS_MAX ; MAX_START_TIME ; solving time (s) ; latency (cycles) ; graph period (cycles)");
    } catch (final FileNotFoundException e) {
      e.printStackTrace();
    }

    solver.log().remove(System.out);
    solver.log().add(writer);

    if (logsLv1) {
      writer.printf(" Number of variables : %d %n Number of constraints : %d %n", model.getNbVars(),
          model.getNbCstrs());
      // solver.verboseSolving(1000); // marche pas
    }
    if (logsLv2) {
      solver.showStatisticsDuringResolution(1000);
      model.displayPropagatorOccurrences(); // pour vérifier que des propagateurs safe sont utilisés
      solver.showContradiction();
      solver.showDecisions();
    }
    solver.showStatistics();

    final String paramName = "size";
    final int[] params = new int[] { 100 };// inutile : les paramètres doivent être maj avant de faire le modèle...
    final int[] token_divisors = new int[] { 1, }; // 2, 5, 20, 50, 100, 10
    final int[] start_divisors = new int[] { 1, };// 2, 5, 20, 50, 100, 10
    final int[] nbs_cycles = new int[] { 100_000_000 };// 10_000, 100_000, 1_000_000, 10_000_000,
    final int max_time_seconds = 10;
    final int nb_comb = token_divisors.length * start_divisors.length * nbs_cycles.length * params.length;
    System.out.printf("Total number of combinations : %d %n", nb_comb);
    System.out.printf("Estimated max duration : %d seconds %n", nb_comb * max_time_seconds);

    int run = 0;
    for (final int param : params) {
      for (final int tokens_divisor : token_divisors) {
        for (final int start_divisor : start_divisors) {
          for (final int nb_cycles : nbs_cycles) {
            writer.printf("%n%n");
            run++;
            System.out.printf("Starting run number %d/%d %n", run, nb_comb);

            CYCLE_MAX = nb_cycles;
            TOKENS_MAX = CYCLE_MAX / tokens_divisor; // arbitrary
            MAX_START_TIME = CYCLE_MAX / start_divisor; // abitrary

            String config = String.format("CYCLE_MAX=%d-TOKENS_MAX=%d-MAX_START_TIME=%d", CYCLE_MAX, TOKENS_MAX,
                MAX_START_TIME);

            final var t = piGraph.getParameters().stream().filter(p -> p.getName().contains(paramName)).findFirst()
                .orElse(null);
            if (t != null) {
              t.setExpression(param);
              config += "-" + paramName + "=" + param;
            }
            writer.println(config);
            if (writer != System.out) {
              System.out.printf("\tRunning config : %s %n", config);
            }

            PrintStream gantt_data = null;
            try {
              gantt_data = new PrintStream(dir.getAbsolutePath() + "/gantt_data_" + config + ".py");
            } catch (final FileNotFoundException e) {
              e.printStackTrace();
            }

            // solver.makeCompleteStrategy(true); // Possiblement utile ! Enquêter.
            solver.observeSolving();
            // solver.toCSV();
            solver.limitTime(max_time_seconds + "s");

            // Pour suivre l'arbre d'exploration et en sortir un .dot graphviz
            final Closeable searchTreeFile = solver.outputSearchTreeToGraphviz(fileName + ".dot");

            setStrategy(solver, variablesToOptimize);

            BlackBoxConfigurator.forCOP(); // Utile ? J'ai l'impression que non...

            Solution solution = new Solution(model);

            if (logsLv2) {
              writer.printf("%s %n", model.toString());
            }

            runInitialPropagation(solver, writer);

            solution = solver.findOptimalSolution(latency, Model.MINIMIZE);

            try {
              if (solver.getSolutionCount() != 0) {
                gantt_data.println("tasks = [");
                saveAndPrintResults(solution, latency, schedule, writer, resultsCsv, solver, gantt_data);
                gantt_data.println("\n]");
                // solver.printStatistics();
                // SolvingStatisticsFlow.toJSON(solver); // marche pas, dommage
              } else {
                printFailureAndLog(model, writer);
              }
            } catch (final Exception e) {
              e.printStackTrace();
            }

            gantt_data.close();

            solver.getMeasures().reset();
            solver.hardReset();

            try {
              searchTreeFile.close();
            } catch (final IOException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }

    writer.close();

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

  private void setStrategy(Solver solver, IntVar[] variablesToOptimize) {
    // TODO : vérifier si on peut donner des priorités aux contraintes, pour vérifier les plus contraignantes en
    // premières et élaguer l'arbre des possibles le plus vite possible

    // stratégies essayées sur l'algo test sans logs, 10s, avec CYCLE_MAX = 1_000_000_000 :
    // - activityBasedSearch : voir papier en doc de la méthode. Craque à size=100_000 avec 0,7 n/s.
    // - conflictHistorySearch : sélectionne une variable en conflits et l'instancie. Craque à size=10_000 avec 131 n/s.
    // - inputOrderLBSearch : minimise les variables de la liste dans l'ordre. Craque à size=10_000_000 avec 3,6 n/s.
    // - minDomLBSearch : variable de domaine min. assignée à lsa LB. Craque à size=10_000 avec 147,6 n/s (!).
    solver.setSearch(Search.inputOrderLBSearch(variablesToOptimize));
  }

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
  private void runInitialPropagation(Solver solver, PrintStream writer) {
    try {
      writer.println("Starting initial propagation (might take some time)");
      solver.propagate();
      writer.println("Initial propagation finished");
    } catch (final ContradictionException e) {
      writer.println("Initial propagation caught a contradiction : model might be unsolvable.");
    }
  }

  private FpgaSchedule saveAndPrintResults(Solution solution, IntVar latency, Map<AbstractActor, ActorTimings> schedule,
      PrintStream writer, PrintStream resultsCsv, Solver solver, PrintStream gantt_data) {

    final FpgaSchedule result = new FpgaSchedule(solution.getIntVal(latency));

    writer.println("Solution found in " + solver.getTimeCount() + "s");

    for (final var entry : schedule.entrySet()) {
      final var a = entry.getKey();
      final var res = entry.getValue();
      entry.getValue().storeResults(solution);
      entry.getValue().printSchedule(entry.getKey(), writer);
      result.addActorTimings(entry.getValue());
      gantt_data.printf("{%n \"%s\": \"%s\",%n \"%s\": %s,%n \"%s\":%s,%n \"%s\":%s,%n \"%s\":%s%n %n},", "name",
          a.getName(), "start", res.startDate, "II", res.initiationInterval, "duration", res.executionTime, "period",
          res.period);
    }

    writer.println("latency = " + result.latency);
    final long hyperperiod = lcm(schedule.values().stream().map(a -> (long) a.period).toList());
    writer.println("hyperperiod = " + hyperperiod);
    writer.print("\n");

    resultsCsv.printf("%d ; %d ; %d ; %f ; %d ; %d %n", CYCLE_MAX, TOKENS_MAX, MAX_START_TIME, solver.getTimeCount(),
        result.latency, hyperperiod);

    return result;
  }

  private void printFailureAndLog(Model model, PrintStream writer) {
    writer.println("No solution was found !");
    if (logsLv2) {
      final List<String> truc = Arrays.asList(model.getVars()).stream().filter(v -> v.getName().endsWith("_var"))
          .map(Object::toString).toList();
      writer.printf("%s %n", String.join("\n", truc));
    } else {
      final List<String> truc = Arrays.asList(model.getVars()).stream().filter(
          v -> v.getName().startsWith("period_") || v.getName().startsWith("start_") || v.getName().startsWith("end_"))
          .map(Object::toString).toList();
      writer.printf("%s %n", String.join("\n", truc));
    }
  }

}
