package org.preesm.algorithm.schedule.fpga;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.stream.IntStream;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.propagation.PropagationProfiler;
import org.chocosolver.solver.search.strategy.BlackBoxConfigurator;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.RealVar;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.algorithm.schedule.fpga.AbstractGenericFpgaFifoEvaluator.ActorNormalizedInfos;
import org.preesm.algorithm.schedule.fpga.AbstractGenericFpgaFifoEvaluator.AnalysisResultFPGA;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.algorithm.synthesis.schedule.algos.IScheduler;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.commons.files.PreesmResourcesHelper;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInterface;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.util.FifoCycleDetector;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.TimingType;

public class FPGAConstraintScheduler implements IScheduler {

  static final int MAX_INT = Integer.MAX_VALUE;

  // attention aux valeurs ! Si elles sont trop grandes, choco pourrait overflow son calcul d'upper bound de résultats
  // intermédiaire de multiplication
  // vraiment static ? Elles pourraient peut-être prendre des valeurs différentes selon l'algo
  static int       DEFAULT_CYCLE_MAX = 500_000_000;
  static int       CYCLE_MAX         = DEFAULT_CYCLE_MAX; // default value
  static int       TOKENS_MAX        = CYCLE_MAX / 2;     // arbitrary
  static int       MAX_START_TIME    = CYCLE_MAX / 2;     // abitrary
  static final int THRESHOLD_RV      = 1_000;             // arbitrary

  int     logLevel = 0; // between 0 and 2 included
  boolean logsInFile;
  boolean computeGantt;

  /**
   * According to {@link org.preesm.model.pisdf.AbstractVertex#getVertexPath}
   */
  public static final String HIERARCHY_DELIMITER = "/";

  public FPGAConstraintScheduler() {
    super();
  }

  public FPGAConstraintScheduler(boolean computeGantt) {
    super();
    this.computeGantt = computeGantt;
    this.logsInFile = true;
  }

  public FPGAConstraintScheduler(boolean computeGantt, boolean logsInFile) {
    super();
    this.computeGantt = computeGantt;
    this.logsInFile = logsInFile;
  }

  public FPGAConstraintScheduler(boolean computeGantt, boolean logsInFile, int level) {
    super();
    this.computeGantt = computeGantt;
    this.logsInFile = logsInFile;
    this.logLevel = level;
  }

  // ============================================
  // ============= Helper functions =============
  // ============================================

  public static long gcd(long a, long b) {
    if (b == 0) {
      return a;
    }
    return gcd(b, a % b);
  }

  public static long lcm(long a, long b) {
    return a / gcd(a, b) * b; // Weird order to prevent overflow, just in case
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
   * returns the list of ExecutableActor without DataInterface actors.
   *
   * @param graph
   *          the graph
   * @return the list of ExecutableActor
   */
  private List<ExecutableActor> getNonDataInterfaceActors(PiGraph graph) {
    return new ArrayList<>(graph.getExecutableActors().stream().filter(a -> !(a instanceof DataInterface)).toList());
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

  private int getExecutionTime(ExecutableActor a, Scenario scenario, Component c) {
    if (a instanceof SpecialActor) {
      // a special actor's latency is estimated to be its max rate of input/output.
      // TODO est-ce que ça marche tout le temps ? Aucune idée, mais ça marchouille pour le moment
      return a.getAllDataPorts().stream().map(dp -> (int) dp.getPortRateExpression().evaluateAsLong())
          .max(Integer::compare).orElse(1);
    }
    return (int) scenario.getTimings().evaluateTimingOrDefault(a, c, TimingType.EXECUTION_TIME);
  }

  private int getInitiationInterval(ExecutableActor a, Scenario scenario, Component c) {
    if (a instanceof SpecialActor) {
      return 1;
    }
    return (int) scenario.getTimings().evaluateTimingOrDefault(a, c, TimingType.INITIATION_INTERVAL);
  }

  /**
   * Returns all the actors that output data only outside of the graph.
   *
   * @param actors
   *          the actors to search through.
   * @return the list of edge actors.
   */
  private List<ExecutableActor> getEndingActors(List<ExecutableActor> actors) {
    final List<ExecutableActor> res = actors.stream()
        .filter(a -> a.getDirectSuccessors().stream().allMatch(DataOutputInterface.class::isInstance)).toList();

    if (res.isEmpty()) {
      // In the case of cyclic graphs, it is possible for no actor to output only out of the graph.
      // In this case, all actors connected to a dataOutputInterface are considered
      return actors.stream()
          .filter(a -> a.getDirectSuccessors().stream().anyMatch(DataOutputInterface.class::isInstance)).toList();
    }
    return res;
  }

  /**
   * Returns the list of fifos linking actor src to actor snk.
   *
   * @param src
   *          the producer.
   * @param snk
   *          the consumer.
   * @return the list of fifos.
   */
  private List<Fifo> getLinkingFifos(ExecutableActor src, ExecutableActor snk) {
    final List<Fifo> res = new LinkedList<>();
    for (final var f : src.getOutgoingEdges().stream().map(e -> (Fifo) e).toList()) {
      if (f.getTarget().equals(snk)) {
        res.add(f);
      }
    }
    return res;
  }

  /**
   * Computes each actor's distance to the closest data interface and sorts executableActors by increasing order of
   * distance.
   *
   * @param graph
   *          the graph
   * @param executableActors
   *          the actors
   */
  private void sortByDistanceToInput(PiGraph graph, List<ExecutableActor> executableActors) {
    final Map<AbstractActor, Integer> distances = new HashMap<>();

    for (final ExecutableActor ea : executableActors) {
      distances.put(ea, Integer.MAX_VALUE);
    }

    final Queue<AbstractActor> q = new ConcurrentLinkedQueue<>();
    for (final DataInputInterface dii : graph.getDataInputInterfaces()) {
      q.add(dii);
      distances.put(dii, 0);
    }

    while (!q.isEmpty()) {
      final AbstractActor u = q.poll();

      for (final AbstractActor e : u.getDirectSuccessors().stream().map(a -> (AbstractActor) a).toList()) {
        if (distances.containsKey(e) && distances.get(e) > distances.get(u) + 1) {
          distances.put(e, distances.get(u) + 1);
          q.add(e);
        }

      }
    }

    distances.entrySet().removeIf(e -> !(e.getKey() instanceof ExecutableActor));

    executableActors.sort((a, b) -> distances.get(a) - distances.get(b));
  }

  /*
   * Returns the most constrained period. The domain of an actor's period is [| II ; CYCLE_MAX / RC|] with steps of size
   * basis. Hence, the domain size is (CYCLE_MAX / RC - II) / basis.
   */
  private IntVar extractAndEnumerateDomainForMostConstrainedPeriod(Map<ExecutableActor, ActorTimings> schedule,
      Model model) {
    final ActorTimings result = schedule.values().stream().min(Comparator.comparingInt(
        at -> (int) Math.ceil(((double) CYCLE_MAX / at.repetitionCount - at.initiationInterval) / at.basisOfPeriod)))
        .orElse(null);

    if (result == null) {
      PreesmLogger.getLogger().log(Level.SEVERE,
          "No actor in " + schedule.keySet() + " has a smallest domain size ! How is that even possible ??");
    }

    if (result.repetitionCount >= THRESHOLD_RV) {
      createEnumeratedDomainForPeriod(result, model);
    }

    return result.basisMultiplier;
  }

  private List<IntVar> getEdgeActorsEndDates(Map<ExecutableActor, ActorTimings> schedule) {
    final List<ExecutableActor> inter = getEndingActors(new LinkedList<>(schedule.keySet()));
    return inter.stream().map(a -> schedule.get(a).endDate_var).toList();
  }

  /**
   * Structure storing the timing information for each actor in the graph.
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

  // ============================================================
  // ============= ActorTimings class and functions =============
  // ============================================================

  /**
   * Structure holding an actor's timing information, both the variables to be used in solving, and the result integers
   * to be used afterward by PREESM
   */
  public class ActorTimings {
    // Solving variables
    private IntVar          period_var;
    private IntVar          startDate_var;
    private IntVar          endDate_var;
    private IntVar          basisMultiplier;
    private ExecutableActor actor;
    private int             basisOfPeriod = 1;

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

    ActorTimings(ExecutableActor ea) {
      super();
      this.actor = ea;
    }

    public void printSchedule(AbstractActor a, PrintStream writer) {
      if (computeGantt) {
        writer.printf("\n%s : latency=%d   \t  II=%d   \t start=%d   \t   end=%d   \t   period=%d", a.getName(),
            executionTime, initiationInterval, startDate, endDate, period);
      } else {
        writer.printf("can't print schedule since the gantt option isn't set !");
      }
    }

    public void storeResults(Solution s) {
      period = s.getIntVal(period_var);
      if (computeGantt) {
        startDate = s.getIntVal(startDate_var);
      }
      if (computeGantt) {
        endDate = s.getIntVal(endDate_var);
      }

      if (logLevel == 0) {
        // no longer needed after solving if no logging is needed
        period_var = null;
        startDate_var = null;
        endDate_var = null;
        basisMultiplier = null;
        actor = null;
      }
    }

  }

  /**
   * Sets the scheduling constraints for all actors.
   *
   * @param actors
   *          the actors.
   * @param model
   *          the model.
   * @param schedule
   *          the schedule.
   */
  private void setTimingConstraints(List<ExecutableActor> actors, Model model,
      Map<ExecutableActor, ActorTimings> schedule, IntVar graphPeriod) {

    for (final var actor : actors) {

      final ActorTimings ct = schedule.get(actor); // our actor is a consumer c

      // CONSTRAINT : T_a = b_a * mul_a. This basis must have been computed beforehand.
      model.arithm(ct.period_var, "=", ct.basisMultiplier, "*", ct.basisOfPeriod).post();

      // We also know that T_a * rc_a = H_G !
      // TODO utile ?
      model.arithm(graphPeriod, "=", ct.period_var, "*", ct.repetitionCount).post();

      final boolean allPredecessorsDataInterface = actor.getDirectPredecessors().stream()
          .filter(AbstractActor.class::isInstance).allMatch(DataInputInterface.class::isInstance);

      if (!allPredecessorsDataInterface) {
        // Start date constraints from predecessors :
        for (final Fifo fifo : actor.getDataInputPorts().stream().map(DataPort::getFifo)
            .filter(f -> schedule.containsKey(f.getSource())).toList()) {
          final ExecutableActor producer = (ExecutableActor) fifo.getSource();
          final ActorTimings pt = schedule.get(producer);

          final int prod_rate = getProdRate(fifo);
          final int cons_rate = getConsRate(fifo);

          final var startDateFromInput = model.intVar("min_start_date_" + actor.getName() + "_" + fifo.getId(),
              /* at.startDate_var.getLB() */ -CYCLE_MAX, ct.startDate_var.getUB());
          // the -CYCLE_MAX LB will be updated below, but we need this variable to accept negative values as it serves
          // as an intermediate. The actual tasks' start dates are forced to be positive though.

          final int n_max = (cons_rate - 1) / prod_rate;

          if (!fifo.isDelayPresent()) {
            // if the fifo has no delay, same business as usual.
            // SD >= SD(pt) + L(pt) + n*T(pt) - prod_rate * (n+1) + 1
            startDateFromInput.eq(pt.startDate_var.add(pt.executionTime).add(pt.period_var.mul(n_max))
                .sub(prod_rate * (n_max + 1)).add(1)).post();

          } else {
            // if the fifo has a delay, it's a bit trickier. It can start earlier than its delayed input, by as many
            // firings as the delays holds cons_rate.
            // SD >= esd - T_c * d_f / tau_c - d_f % tau_c

            final int delay_size = getFixedDelaySize(fifo);
            final int nbFirings = delay_size / cons_rate;
            final int supplementaryTokens = delay_size % cons_rate;

            startDateFromInput.eq(pt.startDate_var.add(pt.executionTime).add(pt.period_var.mul(n_max))
                .sub(prod_rate * (n_max + 1)).add(1).sub(ct.period_var.mul(nbFirings)).sub(supplementaryTokens)).post();

          }

          model.arithm(ct.startDate_var, ">=", startDateFromInput).post();
        }

      }

    }

  }

  /**
   * If an actor repeats a lot, this constraints its period to small values --> we can enumerate the domain.
   *
   * max period : CYCLE_MAX / RV
   *
   * min period : II
   *
   * number of elements to enumerate : 1 + (CYCLE_MAX / RV - II) / basisOfPeriod
   *
   * moreover we can reuse the initial boundaries :
   *
   * range start : max(period_var.LB / basisOfPeriod, II / basisOfPeriod)
   *
   * range end : min(period_var.UB / basisOfPeriod , 1 + (CYCLE_MAX / RV - II) / basisOfPeriod)
   *
   * @param at
   *          the ActorTimings
   * @param model
   *          the model
   */
  private void createEnumeratedDomainForPeriod(ActorTimings at, Model model) {
    final int range_start = Math.max(at.period_var.getLB() / at.basisOfPeriod,
        at.initiationInterval / at.basisOfPeriod);
    final int range_end = Math.min(at.period_var.getUB() / at.basisOfPeriod,
        1 + (CYCLE_MAX / at.repetitionCount - at.initiationInterval) / at.basisOfPeriod);

    model.member(at.period_var, IntStream.range(range_start, range_end + 1).map(n -> n * at.basisOfPeriod).toArray())
        .post();
  }

  private void computeActorPeriodBasis(List<ExecutableActor> actors, ActorTimings at) {
    final var inputFifos = at.actor.getIncomingEdges().stream().filter(e -> actors.contains(e.getSource()))
        .map(f -> (Fifo) f).toList();

    final var outputFifos = at.actor.getOutgoingEdges().stream().filter(e -> actors.contains(e.getTarget()))
        .map(f -> (Fifo) f).toList();

    for (final Fifo f : inputFifos) {
      final int prod_rate = getProdRate(f);
      final int cons_rate = getConsRate(f);

      final int basisOfTc = (int) (cons_rate / gcd(prod_rate, cons_rate));

      at.basisOfPeriod = lcm(at.basisOfPeriod, basisOfTc);
    }

    for (final Fifo f : outputFifos) {
      final int prod_rate = getProdRate(f);
      final int cons_rate = getConsRate(f);

      final int basisOfTp = (int) (prod_rate / gcd(prod_rate, cons_rate));

      at.basisOfPeriod = lcm(at.basisOfPeriod, basisOfTp);
    }
  }

  private ActorTimings createActorTimings(ExecutableActor actor, List<ExecutableActor> actors, Scenario scenario,
      Component component, Map<org.preesm.model.pisdf.AbstractVertex, Long> brv, Model model, int minGraphPeriod,
      boolean cyclicGraph) {

    final ActorTimings res = new ActorTimings(actor);

    res.repetitionCount = brv.get(actor).intValue();
    res.executionTime = getExecutionTime(actor, scenario, component);
    res.initiationInterval = getInitiationInterval(actor, scenario, component);

    computeActorPeriodBasis(actors, res);

    res.basisMultiplier = model.intVar("basisMultiplier_" + actor.getName() + "_var",
        Math.floorDiv(res.initiationInterval, res.basisOfPeriod),
        CYCLE_MAX / (res.repetitionCount * res.basisOfPeriod));

    // UB to reduce domain size : CYCLE_MAX is at least one graph period
    final int minPeriod = minGraphPeriod / res.repetitionCount;
    res.period_var = model.intVar("period_" + actor.getName() + "_var", minPeriod, CYCLE_MAX / res.repetitionCount);

    // TODO would it be useful to make an actual constraint CYCLE_MAX - period_var, to reduce the domain size ?w
    final int startDateUB = CYCLE_MAX - minPeriod * res.repetitionCount;

    // On tolère 10% d'erreur par rapport à la période minimale
    // final int tolerated_error = res.period_var.getLB() / 10;
    // final int divisor = (int) Math.pow(10, Math.floor(Math.log10(tolerated_error)));
    // if (divisor >= 10 && cyclicGraph && false) {
    // // On force start date à être un multiple de divisor
    // final IntVar multSD = model.intVar("multSD_" + actor.getName(), 0, Math.ceilDiv(startDateUB, divisor));
    // // model.arithm(res.startDate_var, "=", multSD, "*", 10).post();
    // res.startDate_var = model.intView(divisor, multSD, 0);
    // } else {
    // must start early enough to finish all its periods (=rv) before MAX_CYCLE
    // could even be bounded by its followers' latencies sum
    res.startDate_var = model.intVar("start_" + actor.getName() + "_var", 0, startDateUB);
    // }

    // must have at least executed all its firings by end time
    res.endDate_var = model.intVar("end_" + actor.getName() + "_var",
        res.startDate_var.getLB() + res.initiationInterval * (res.repetitionCount - 1) + res.executionTime, CYCLE_MAX);

    res.endDate_var.eq(res.startDate_var.add(res.period_var.mul(res.repetitionCount - 1).add(res.executionTime)))
        .post();

    return res;
  }

  /*
   * TODO : I divide by half for the PRECISE CASE of fixing adfg FOR GAUSSIAN DIFFERENCE ONLY !!!
   *
   * REMOVE AFTER PAPER SUBMISSION!!!!
   */
  private int getFixedDelaySize(Fifo f) {
    return (int) f.getDelay().getExpression().evaluateAsLong() / 2;
  }

  // -------------------------------------------
  // ------------ Scheduling method ------------
  // -------------------------------------------

  @Override
  /**
   * The method assumes it is scheduling a flat graph. If it encounters a cluster, it will be treated as an actor.
   */
  public SynthesisResult scheduleAndMap(final PiGraph piGraph, final Design slamDesign, final Scenario scenario) {
    final long start = System.nanoTime();
    long end;

    final var brv = PiBRV.compute(piGraph, BRVMethod.LCM);
    final Map<InterfaceActor, Pair<Long, Long>> interfaceRates = FpgaAnalysis.checkInterfaces(piGraph, brv);
    final AnalysisResultFPGA results = new AnalysisResultFPGA(piGraph, brv, interfaceRates);

    // A bit overkill, but useful as a sanity check. It might be useful to use mapActorNormalizedInfos in the code.
    final Map<AbstractActor, ActorNormalizedInfos> mapActorNormalizedInfos = AbstractGenericFpgaFifoEvaluator
        .logCheckAndSetActorNormalizedInfos(scenario, results);

    final Map<ExecutableActor, ActorTimings> schedule = new HashMap<>();

    PrintStream writer = System.out;
    PrintStream resultsCsv = null;

    final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    final IPath osIPath = root.getFolder(new Path(scenario.getCodegenDirectory())).getLocation();
    final String absolutePathString = osIPath.toOSString();

    final File dir = new File(absolutePathString + "/" + PreesmCopyTracker.getOriginalSource(piGraph).getName() + "/");
    dir.mkdirs();
    final String folderPath = dir.getAbsolutePath();
    final String fileName = folderPath + "/choco_solver_logs";

    final String codegen_dir = scenario.getCodegenDirectory() + "/";
    try {
      PreesmIOHelper.getInstance().print(codegen_dir, "plot_gantt.py",
          PreesmResourcesHelper.getInstance().read("resources/scripts/plot_gantt.py", this.getClass()));
      PreesmIOHelper.getInstance().print(codegen_dir, "plot_fifos_worst_buffer_sizes.py", PreesmResourcesHelper
          .getInstance().read("resources/scripts/plot_fifos_worst_buffer_sizes.py", this.getClass()));
      PreesmIOHelper.getInstance().print(codegen_dir, "plot_fifos_worst_latency.py",
          PreesmResourcesHelper.getInstance().read("resources/scripts/plot_fifos_worst_latency.py", this.getClass()));
      PreesmIOHelper.getInstance().print(codegen_dir, "plot_schedule.sh",
          PreesmResourcesHelper.getInstance().read("resources/scripts/plot_schedule.sh", this.getClass()));
      PreesmIOHelper.getInstance().print(codegen_dir, "collapse_dot_chains.py",
          PreesmResourcesHelper.getInstance().read("resources/scripts/collapse_dot_chains.py", this.getClass()));
      PreesmIOHelper.getInstance().print(codegen_dir, "choco_log_stats.py",
          PreesmResourcesHelper.getInstance().read("resources/scripts/choco_log_stats.py", this.getClass()));
    } catch (final IOException e) {
      PreesmLogger.getLogger().log(Level.WARNING, "Could not write all processing scripts to " + codegen_dir);
    }

    if (logsInFile) {
      try {
        writer = new PrintStream(new FileOutputStream(fileName + ".txt"), true);
        PreesmLogger.getLogger().log(Level.INFO, "Writing logs to file " + fileName);
      } catch (final FileNotFoundException e) {
        PreesmLogger.getLogger().log(Level.WARNING, "Could not create logs file " + fileName + ".txt");
        writer = System.out;
      }
    } else {
      writer = System.out;
    }

    try {
      resultsCsv = new PrintStream(fileName + ".csv");
      resultsCsv.println(
          "CYCLE_MAX ; TOKENS_MAX ; MAX_START_TIME ; solving time (s) ; latency (cycles) ; graph period (cycles)");
    } catch (final FileNotFoundException e) {
      PreesmLogger.getLogger().log(Level.SEVERE, "Could not create output csv file " + fileName + ".csv");
    }

    final Model model = new Model("Period computing");
    final List<IntVar> variablesToInstantiate = new LinkedList<>();
    final List<IntVar> variablesToOptimize = new LinkedList<>();
    final IntVar latency = buildModel(model, piGraph, slamDesign, scenario, schedule, variablesToInstantiate, writer,
        variablesToOptimize);

    // --------
    // Solution
    // --------

    final Solver solver = model.getSolver();
    PropagationProfiler profiler = null;

    solver.log().remove(System.out);
    solver.log().add(writer);

    // Pour suivre l'arbre d'exploration et en sortir un .dot graphviz
    Closeable searchTreeFile = null;

    if (logLevel >= 1) {
      writer.printf(" Number of variables : %d %n Number of constraints : %d %n", model.getNbVars(),
          model.getNbCstrs());
      searchTreeFile = solver.outputSearchTreeToGraphviz(fileName + ".dot");
    }
    if (logLevel >= 2) {
      profiler = solver.profilePropagation();
      model.displayPropagatorOccurrences(); // pour vérifier que des propagateurs safe sont utilisés
      solver.showContradiction();
      solver.showDecisions();
    }

    PrintStream gantt_data = null;
    try {
      gantt_data = new PrintStream(dir.getAbsolutePath() + "/gantt_data.py");
    } catch (final FileNotFoundException e) {
      PreesmLogger.getLogger().log(Level.WARNING,
          "Could not create gantt file " + dir.getAbsolutePath() + "/gantt_data.py");
    }

    // solver.makeCompleteStrategy(true); // Possiblement utile ! Enquêter.

    // solver.limitTime(max_time_seconds + "s");

    setStrategy(solver, variablesToInstantiate.toArray(new IntVar[0]));

    BlackBoxConfigurator.forCOP(); // Utile ? J'ai l'impression que non...

    Solution solution = new Solution(model, variablesToInstantiate.toArray(new IntVar[0]));

    final var w = writer; // fuck java

    final var start_propag = System.nanoTime();
    runInitialPropagation(solver, writer);
    final var duration_propag = System.nanoTime() - start_propag;
    writer.printf("\nInitial propagation duration : %d ms \n", duration_propag / 1_000_000);

    if (logLevel >= 2) {
      try {
        profiler.writeTo(new File(folderPath + "/choco-profiling.txt"), true);
      } catch (final IOException e) {
        PreesmLogger.getLogger().log(Level.WARNING, "Could not create file " + folderPath + "/choco-profiling.txt");
      }
      writer.println("\n\nAll vars : \n"
          + String.join("\n", Arrays.asList(model.getVars()).stream().map(Object::toString).toList()));
      writer.println("\n\nAll constraints : \n"
          + String.join("\n", Arrays.asList(model.getCstrs()).stream().map(Object::toString).toList()));
      for (final var v : model.getVars()) {
        v.addMonitor((va, event) -> w.printf("%s : %s -> %s%n", event, va.getName(), va));
      }
    }

    writer.println("\n\nStarting schedule search\n");

    if (variablesToOptimize.size() == 1) {
      model.setObjective(Model.MINIMIZE, latency);
      while (solver.solve()) {
        // PreesmLogger.getLogger().log(Level.FINE, "found a solution");
        solution.record();
      }
    } else {
      solution = solver.findLexOptimalSolution(variablesToInstantiate.toArray(new IntVar[0]), Model.MINIMIZE);
    }

    if (solver.getSolutionCount() != 0) {
      PreesmLogger.getLogger().log(Level.INFO, "Solution found for scheduling of cluster " + piGraph.getName());
      saveAndPrintResults(solution, schedule, writer, resultsCsv, solver, gantt_data, latency);
      final var res = computeWorstCaseBufferSizes(piGraph, schedule, scenario);
      for (final var t : res.entrySet()) {
        System.out.println(t.getKey().getSource().getName() + "->" + t.getKey().getTarget().getName() + " : "
            + t.getValue() + " bits");
      }
    } else {
      PreesmLogger.getLogger().log(Level.WARNING,
          "No solution was found for scheduling of cluster " + piGraph.getName());
      printFailureAndLog(model, writer);
    }

    if (gantt_data != null) {
      gantt_data.close();
    }
    if (resultsCsv != null) {
      resultsCsv.close();
    }

    if (logLevel >= 1 && searchTreeFile != null) {
      try {
        searchTreeFile.close();
      } catch (final IOException e) {
        PreesmLogger.getLogger().log(Level.WARNING,
            "Could not close file " + searchTreeFile.toString() + ", that's weird");
      }
    }

    end = System.nanoTime();

    final long duration = end - start;
    writer.println("Solving duration : " + duration);

    if (writer != System.out) {
      writer.close();
    }

    // final StatEditorSynthesisTask truc = new StatEditorSynthesisTask();
    // final Map<String, Object> inputs = new HashMap<>();
    // inputs.put("scenario", scenario);
    // inputs.put("architecture", scenario.getDesign());
    // inputs.put("algorithm", piGraph);

    // final LatencyCost lc = new LatencyCost(solution.getIntVal(latency), null);
    return results;
  }

  // ============================================
  // ============= Solver functions =============
  // ============================================

  private IntVar buildModel(Model model, final PiGraph piGraph, final Design slamDesign, final Scenario scenario,
      Map<ExecutableActor, ActorTimings> schedule, List<IntVar> variablesToInstantiate, PrintStream writer,
      List<IntVar> variablesToOptimize) {

    final var brv = PiBRV.compute(piGraph, BRVMethod.LCM);

    final List<ExecutableActor> actors = getNonDataInterfaceActors(piGraph);

    // we will sort the actors based on their distance to the graph's input interfaces.
    sortByDistanceToInput(piGraph, actors);

    // for now, we will not consider fifos linking actors from/to data interfaces.
    // It may by interesting to model them as actors with a start date equal to the comm. time and rate of comm size
    final List<Fifo> fifos = getRelevantFifos(piGraph);

    // the graph is supposed to be mapped to a single PE type (FPGA, CPU, DSP...)
    final Component Fpga = scenario.getPossibleMappings(piGraph).getFirst().getComponent();

    final FifoCycleDetector cycleDetector = new FifoCycleDetector(true);
    cycleDetector.casePiGraph(piGraph);

    final IntVar zero = model.intVar("zero", 0); // actually useful trust me

    // a lower bound on the graph's period is having all the actors' period be their II
    // We can take the max of that and use it as LB
    int minGraphPeriod = 1;
    for (final var a : actors) {
      final var II = getInitiationInterval(a, scenario, Fpga);
      // minGraphPeriod = lcm(II, minGraphPeriod);
      minGraphPeriod = (int) Math.max(minGraphPeriod, II * brv.get(a));
    }

    // We know that for all actors : T_a * rc_a = k, with k the graph's period
    final IntVar graphPeriod = model.intVar("graphPeriod", minGraphPeriod, CYCLE_MAX);

    final List<IntVar> startDates = new ArrayList<>(actors.size());
    final List<IntVar> periods = new ArrayList<>(actors.size());
    final List<IntVar> endDates = new ArrayList<>(actors.size());

    for (final ExecutableActor actor : actors) {
      final ActorTimings at = createActorTimings(actor, actors, scenario, Fpga, brv, model, minGraphPeriod,
          cycleDetector.cyclesDetected());
      if (computeGantt) {
        // the period and end date are linked to other variables by equations, and thus will be computed during the
        // minimization of the latency. However, the start dates are only constrained with >=, not =, so we need to
        // branch on them. The others are also branched on because ça fait pas de mal.
        startDates.add(at.startDate_var);
        periods.add(at.period_var);
        endDates.add(at.endDate_var);
      }
      schedule.put(actor, at);
    }

    // first instantiate periods, then start dates, then finally end dates
    if (computeGantt) {
      variablesToInstantiate.addAll(periods);
      variablesToInstantiate.addAll(startDates);
      variablesToInstantiate.addAll(endDates);
    }

    // Now that all the timings are initialized, we can compute set the constraints
    setTimingConstraints(actors, model, schedule, graphPeriod);

    // on met en place le modèle pour chaque fifo
    for (final Fifo fifo : fifos) {

      final AbstractActor prodActor = fifo.getSource();
      final ActorTimings prodTimings = schedule.get(prodActor);
      final AbstractActor consActor = fifo.getTarget();
      final ActorTimings consTimings = schedule.get(consActor);

      final int prod_rate = getProdRate(fifo);
      final int cons_rate = getConsRate(fifo);

      // CONSTRAINT : equal rates : Tc / rc = Tp / rp --> Tc * rp = Tp * rc
      // --> Tc * rp / gcd = Tp * rc / gcd ; with gcd(BasisTc * tp, BasisTp * tc)

      final long rateTimesBasisGcd = gcd((long) prod_rate * consTimings.basisOfPeriod,
          (long) cons_rate * prodTimings.basisOfPeriod);

      final double precision = 1d; // since we work on int multiplication, no need to waste precision on decimal

      // check if the products are small enough that we can afford to use IntVar
      // final long max_left = (long) ((double) cons_rate * prodTimings.period_var.getUB() / rateTimesBasisGcd);
      // final long max_right = (long) ((double) prod_rate * consTimings.period_var.getUB() / rateTimesBasisGcd);

      // if (max_left < Integer.MAX_VALUE && max_right < Integer.MAX_VALUE) {
      // // No risk of overflow ! IntVar are enough
      // final IntVar left = model.intVar("left__" + fifo.getId() + "_var",
      // (int) Math.floorDiv((long) cons_rate * prodTimings.period_var.getLB(), rateTimesBasisGcd),
      // (int) Math.ceilDiv((long) cons_rate * prodTimings.period_var.getUB(), rateTimesBasisGcd));
      // final IntVar right = model.intVar("right__" + fifo.getId() + "_var",
      // (int) Math.floorDiv((long) prod_rate * consTimings.period_var.getLB(), rateTimesBasisGcd),
      // (int) Math.ceilDiv((long) prod_rate * consTimings.period_var.getUB(), rateTimesBasisGcd));
      //
      // if (cons_rate % rateTimesBasisGcd == 0) {
      // left.eq(prodTimings.period_var.mul((int) (cons_rate / rateTimesBasisGcd))).post();
      // } else {
      // left.eq(prodTimings.period_var.mul(cons_rate).div((int) rateTimesBasisGcd)).post();
      // }
      //
      // if (prod_rate % rateTimesBasisGcd == 0) {
      // right.eq(consTimings.period_var.mul((int) (prod_rate / rateTimesBasisGcd))).post();
      // } else {
      // right.eq(consTimings.period_var.mul(prod_rate).div((int) rateTimesBasisGcd)).post();
      // }
      //
      // left.eq(right).post();
      //
      // } else {
      // risk of overflow : we have to use the slower RealVar
      final RealVar left = model.realVar("left__" + fifo.getId() + "_var",
          Math.floorDiv((long) cons_rate * prodTimings.period_var.getLB(), rateTimesBasisGcd),
          Math.ceilDiv((long) cons_rate * prodTimings.period_var.getUB(), rateTimesBasisGcd), precision);
      final RealVar right = model.realVar("right__" + fifo.getId() + "_var",
          Math.floorDiv((long) prod_rate * consTimings.period_var.getLB(), rateTimesBasisGcd),
          Math.ceilDiv((long) prod_rate * consTimings.period_var.getUB(), rateTimesBasisGcd), precision);

      final RealVar prodPeriodReal = model.realVar(prodTimings.period_var.getName() + "_Real",
          prodTimings.period_var.getLB(), prodTimings.period_var.getUB(), precision);
      model.eq(prodPeriodReal, prodTimings.period_var).post();

      final RealVar consPeriodReal = model.realVar(consTimings.period_var.getName() + "_Real",
          consTimings.period_var.getLB(), consTimings.period_var.getUB(), precision);
      model.eq(consPeriodReal, consTimings.period_var).post();

      // we try and reduce the number of constraints if possible
      if (cons_rate % rateTimesBasisGcd == 0) {
        left.eq(prodPeriodReal.mul((double) cons_rate / rateTimesBasisGcd)).post();
      } else {
        left.eq(prodPeriodReal.mul(cons_rate).div(rateTimesBasisGcd)).post();
      }

      if (prod_rate % rateTimesBasisGcd == 0) {
        right.eq(consPeriodReal.mul((double) prod_rate / rateTimesBasisGcd)).post();
      } else {
        right.eq(consPeriodReal.mul(prod_rate).div(rateTimesBasisGcd)).post();
      }

      left.eq(right).post();
      // }
      // -- Computing breakpoints positions --

      // nombre de breakpoints de chaque
      final int nbBreakpointsProd = lcm(prod_rate, cons_rate) / prod_rate; // bornes : [1 ; cons_rate]
      final int nbBreakpointsCons = lcm(prod_rate, cons_rate) / cons_rate; // bornes : [1 ; prod_rate]
      final String chosenBreakpoints = nbBreakpointsProd <= nbBreakpointsCons ? "Prod" : "Cons";
      final int nbBreakpoints = Math.min(nbBreakpointsProd, nbBreakpointsCons);
      if (logLevel >= 2) {
        writer.printf("Chosen side for fifo %s : %s with %d breakpoints \n", fifo.getId(), chosenBreakpoints,
            nbBreakpoints);
      }

      final IntVar[] breakpointsPositions = model.intVarArray(
          "breakpointPosition" + chosenBreakpoints + "_" + fifo.getId() + "_var", nbBreakpoints, 0, CYCLE_MAX);

      if (chosenBreakpoints.equals("Prod")) {
        // producer breakpoints
        for (int bk = 0; bk < nbBreakpointsProd; bk++) {
          // startDate + latence - taux_prod + bk * Tp
          breakpointsPositions[bk].eq(
              prodTimings.startDate_var.add(prodTimings.executionTime - prod_rate).add(prodTimings.period_var.mul(bk)))
              .post();
        }
      } else {
        // consumer breakpoints
        for (int bk = 0; bk < nbBreakpointsCons; bk++) {
          breakpointsPositions[bk].eq(consTimings.startDate_var.add(cons_rate - 1).add(consTimings.period_var.mul(bk)))
              .post();
        }
      }

      // les valeurs aux breakpoints
      final IntVar[] cumP = model.intVarArray("cumP__" + fifo.getId() + "_var", nbBreakpoints, 0, TOKENS_MAX);
      final IntVar[] cumC = model.intVarArray("cumC__" + fifo.getId() + "_var", nbBreakpoints, 0, TOKENS_MAX);

      final IntVar rate_cons = model.intVar("rate_cons_" + fifo.getId(), cons_rate);

      // These variables record whether we are in the particular case period = rate, in which there is not breakpoint
      // (change from idle/cons to prod/idle)
      final BoolVar activeProdBreakpoints = model.arithm(prodTimings.period_var, "!=", prod_rate).reify();
      final BoolVar activeConsBreakpoints = model.arithm(consTimings.period_var, "!=", cons_rate).reify();

      for (int bk = 0; bk < nbBreakpoints; bk++) {
        final IntVar t_bk = breakpointsPositions[bk];

        // ----------------------------------------------
        // --- cumulative production at breakpoint bk ---
        // ----------------------------------------------

        // first token produced at t - delay_prod + latency - rateprod : always positive, since breakpoints at prod are
        // after it started, and cons starts after prod. Negative still allowed for flexibility.
        final IntVar delta_prod = model.intVar("deltaProd_" + fifo.getId() + "_" + bk + "_var", -MAX_START_TIME,
            MAX_START_TIME);
        // delta_prod.eq(t.sub(prodTimings.startDate_var).sub(prodTimings.executionTime).add(prodTimings.period_var))
        // .post();
        delta_prod.eq(t_bk.sub(prodTimings.startDate_var).sub(prodTimings.executionTime).add(prod_rate)).post();

        final IntVar prodInPeriod = model.intVar("ProdInPeriod_" + fifo.getId() + "_" + bk + "_var", 0, prod_rate);
        final IntVar prodFromPreviousPeriods = model
            .intVar("prodFromPreviousPeriods_" + fifo.getId() + "_" + bk + "_var", 0, TOKENS_MAX);

        // in-period production
        if (chosenBreakpoints.equals("Prod")) {
          // when the breakpoints are on the producer, it has not produced tokens yet
          // ==> prodInPeriod = 0 for all breakpoints
          model.arithm(prodInPeriod, "=", zero).post();
        } else {
          // Non-simplified formula : prodInPeriod = (t - delay_prod) % periodProd - periodProd + taux_prod
          final IntVar prod_modulo = model.intVar("prod_modulo_" + fifo.getId() + "_" + bk + "__var", 0,
              Math.min(prodTimings.period_var.getUB(), TOKENS_MAX));
          final IntVar inter2 = model.intVar("inter2_" + bk + "_var", -TOKENS_MAX, TOKENS_MAX); // forced to >=0 later

          // delta_prod % period_var = prod_modulo
          model.ifThen(activeProdBreakpoints, model.mod(delta_prod, prodTimings.period_var, prod_modulo));

          model.ifThen(activeProdBreakpoints,
              inter2.eq(prod_modulo.sub(prodTimings.period_var).add(prod_rate)).decompose());

          model.ifThen(activeProdBreakpoints, model.max(prodInPeriod, inter2, zero));
        }

        // Production from previous periods : ((t - delay_prod) / periodProd) * taux_prod
        // choco operates on natural numbers so the fraction results are automatically floored
        // we add in the initial tokens if there is a delay
        if (fifo.getDelay() != null) {
          final int delay = getFixedDelaySize(fifo);
          prodFromPreviousPeriods.eq((delta_prod.div(prodTimings.period_var)).mul(prod_rate).add(delay)).post();
        } else {
          prodFromPreviousPeriods.eq((delta_prod.div(prodTimings.period_var)).mul(prod_rate)).post();
        }

        // prodFromPreviousPeriods (+ prodInPeriod if period_prod != prod_rate)
        model.ifThenElse(activeProdBreakpoints,
            model.arithm(cumP[bk], "=", prodFromPreviousPeriods, "+", prodInPeriod), // common case
            model.arithm(cumP[bk], "=", prodFromPreviousPeriods) // particular case
        );

        // ------------------------------------
        // --- cumulative consumption at bk ---
        // ------------------------------------

        // t - delay_cons
        // lower bound : the consumer is delayed from the producer by at least the time it takes to start producing
        // this time is prod.executionTime - rate (at least ! In general : prod.period - rate)
        // Upper bound : the cons should start before the prod has finished an iteration's worth of firings I guess ?
        final IntVar delta_cons = model.intVar("deltaCons__" + fifo.getId() + "_" + bk + "__var",
            0 /* source.executionTime - prod_rate */,
            MAX_START_TIME /* target.repetitionCount * source.executionTime */);

        // the consumption in-period is bounded between 0 and cons_rate
        final IntVar consInPeriod = model.intVar("ConsInPeriod__" + fifo.getId() + "_" + bk + "__var", 0, cons_rate);

        // IntStream.range(1, 1 + TOKENS_MAX / cons_rate).map(n -> n * cons_rate).toArray()
        final IntVar consFromPreviousPeriods = model
            .intVar("consFromPreviousPeriods__" + fifo.getId() + "_" + bk + "__var", 0, TOKENS_MAX);

        // To have t - delay_cons be 0 or more
        model.max(delta_cons, t_bk.sub(consTimings.startDate_var).intVar(), zero).post();

        if (chosenBreakpoints.equals("Cons")) {
          // in this special case, the breakpoint happens always after the consumer has consumed
          // a period's worth of tokens and the production will be counted in consFromPreviousPeriods, so there is
          // nothing to do : it's always cons_rate.
          model.arithm(consInPeriod, "=", cons_rate).post();

        } else {
          // this intermediate variable cannot be negative since we force delta_cons >= 0
          final IntVar cons_modulo = model.intVar("cons_modulo__" + fifo.getId() + "_" + bk + "__var", 0,
              Math.min(consTimings.period_var.getUB(), TOKENS_MAX));

          // (t - delta_cons) % periodCons : the consumption in this period
          // The constraint is posted only if we're in the case period != cons_rate
          model.ifThen(activeConsBreakpoints, model.mod(delta_cons, consTimings.period_var, cons_modulo));

          // min(x, rate_cons) <= rate_cons
          // The constraint is posted only if we're in the case period != cons_rate
          model.ifThen(activeConsBreakpoints, model.min(consInPeriod, cons_modulo, rate_cons));
        }

        // ((t - delay_cons) / periodCons) * taux_cons
        consFromPreviousPeriods.eq(delta_cons.div(consTimings.period_var).mul(cons_rate)).post();

        // TODO ordre d'évaluation de la condition important ?
        // cumC = consFromPreviousPeriods (+ consInPeriod if period_cons != cons_rate)
        model.ifThenElse(activeConsBreakpoints,
            model.arithm(cumC[bk], "=", consFromPreviousPeriods, "+", consInPeriod), // common case
            model.arithm(cumC[bk], "=", consFromPreviousPeriods) // particular case
        );

        // CONSTRAINT : production superior or equal to consumption at the breakpoint
        model.arithm(cumP[bk], ">=", cumC[bk]).post();
      }
    }

    final IntVar freePeriodMult = extractAndEnumerateDomainForMostConstrainedPeriod(schedule, model);

    // objectif : optimiser la latence = la date de fin du dernier acteur relative à une période
    // on pourrait utiliser le chemin critique, mais pour le moment je vais juste optimiser la fin d'exécution de
    // l'acteur le plus tardif
    // TODO : trouver des contraintes pour réduire l'espace d'état de latency, là on ne fait qu'énumérer comme des cons.
    final IntVar latency = model.max("latency", getEdgeActorsEndDates(schedule).toArray(new IntVar[0]));

    // Latency for a fully sequential execution
    final int cycle_max = actors.stream().mapToInt(a -> (int) (brv.get(a) * getExecutionTime(a, scenario, Fpga))).sum();
    // Arbitrary bound : let's suppose that at least 0.01% of the time is spend computing
    model.arithm(latency, "<=", cycle_max * 10000).post();

    // branch on freePeriod first then hyperperiod if wanted, even if no gantt, to reduce latency's domain size.
    // variablesToInstantiate.addFirst(graphPeriod);
    variablesToInstantiate.addFirst(freePeriodMult);

    // Branch on latency in last, because otherwise choco tries all values...
    variablesToInstantiate.add(latency);

    // for now, we optimize only latency. Later we may perform multi-objective optimizations.
    variablesToOptimize.add(latency);

    return latency; // we return the latency variable
  }

  private void setStrategy(Solver solver, IntVar[] variablesToAssign) {
    // TODO : vérifier si on peut donner des priorités aux contraintes, pour vérifier les plus contraignantes en
    // premières et élaguer l'arbre des possibles le plus vite possible

    // stratégies essayées sur l'algo test sans logs, 10s, avec CYCLE_MAX = 1_000_000_000 :
    // - activityBasedSearch : voir papier en doc de la méthode. Craque à size=100_000 avec 0,7 n/s.
    // - conflictHistorySearch : sélectionne une variable en conflits et l'instancie. Craque à size=10_000 avec 131 n/s.
    // - inputOrderLBSearch : minimise les variables de la liste dans l'ordre. Craque à size=1_000_000_000 avec 3,6 n/s.
    // - minDomLBSearch : variable de domaine min. assignée à sa LB. Craque à size=10_000 avec 147,6 n/s (!).
    // - roundRobinSearch : donne java.lang.IndexOutOfBoundsException: Index 0 out of bounds for length 0
    // - domOverWDegSearch : affecte un poids à chaque contrainte croissant avec le nombre d'échecs qu'elle cause.
    // Choisit ensuite la variable au meilleur ratio (domain size) / (somme des poids des contraintes liées).

    // Si wrappé dans lastConflict : en cas de conflit, on ignore la méthode de choix de variale et on choisit celle qui
    // a causé le conflit.

    // TODO : essayer de mettre plusieurs stratégies !
    // solver.setSearch(Search.inputOrderLBSearch(variablesToAssign));
    // solver.setSearch(Search.lastConflict(Search.inputOrderLBSearch(variablesToAssign)));
    solver.setSearch(Search.lastConflict(Search.domOverWDegSearch(variablesToAssign)));

    // scale mieux grâce à la dichotomie
    // solver.setSearch(Search.lastConflict(Search.intVarSearch(new DomOverWDeg<>(variablesToAssign, 0L),
    // new IntDomainMin(), DecisionOperatorFactory.makeIntSplit(), variablesToAssign)));

    // solver.setSearch(Search.lastConflict(Search.intVarSearch(// our strategy :
    // new InputOrder<>(solver.getModel()), // We will branch on variables in input order
    // new IntDomainMiddle(true), // and assign them to their domain's middle value
    // variablesToAssign)));

  }

  /**
   * Runs a first call to the propagation method to check the model's viability. Prints an error message if failure.
   *
   * @param solver
   *          the parameterized solver
   */
  private void runInitialPropagation(Solver solver, PrintStream writer) {
    try {
      writer.println("\n\nStarting initial propagation (might take some time)");
      solver.propagate();
      writer.println("\nInitial propagation finished");
    } catch (final ContradictionException e) {
      if (logLevel >= 1) {
        writer.println(e);
      }
      writer.println("\nInitial propagation caught a contradiction : model might be unsolvable.");
    }
  }

  /***
   * computes worst-case buffer sizes for each fifo.
   *
   * @param graph
   *          the graph.
   * @param schedule
   *          the schedule.
   * @return a map of fifo to buffer size.
   */
  Map<Fifo, Long> computeWorstCaseBufferSizes(PiGraph graph, Map<ExecutableActor, ActorTimings> schedule,
      Scenario scenario) {
    final List<Fifo> fifos = getRelevantFifos(graph);
    final Map<Fifo, Long> bufferSizes = new HashMap<>();

    int totalSize = 0;

    for (final Fifo f : fifos) {
      final AbstractActor prodActor = f.getSource();
      final ActorTimings prodTimings = schedule.get(prodActor);
      final AbstractActor consActor = f.getTarget();
      final ActorTimings consTimings = schedule.get(consActor);

      final int prod_rate = getProdRate(f);
      final int cons_rate = getConsRate(f);

      final int initial_tokens = f.getDelay() != null ? getFixedDelaySize(f) : 0;

      // Producers and consumers have the same breakpoint formulas as before, but swapped
      // the consumer now consumes at the end of its execution
      // the producer now produces at the beginning of its execution

      if (consTimings.period != cons_rate /* <==> prodTimings.period == prod_rate */) {
        final int nbBreakpointsProd = lcm(prod_rate, cons_rate) / prod_rate; // bornes : [1 ; cons_rate]
        final int nbBreakpointsCons = lcm(prod_rate, cons_rate) / cons_rate; // bornes : [1 ; prod_rate]
        final int nbBreakpoints = Math.min(nbBreakpointsProd, nbBreakpointsCons);

        final String chosenBreakpoints = nbBreakpointsProd > nbBreakpointsCons ? "Cons" : "Prod";

        final int[] breakpointsPositions = new int[nbBreakpoints];

        if (chosenBreakpoints.equals("Cons")) {
          // consumer breakpoints
          for (int bk = 1; bk <= nbBreakpointsCons; bk++) {
            // start_date - taux_cons + latency + (bk-1) * periodCons
            breakpointsPositions[bk - 1] = consTimings.startDate - cons_rate + consTimings.executionTime
                + (bk - 1) * consTimings.period;
          }
        } else {
          // producer breakpoints
          for (int bk = 1; bk <= nbBreakpointsProd; bk++) {
            // start_date + (bk - 1) * periodProd + taux_prod
            breakpointsPositions[bk - 1] = prodTimings.startDate + (bk - 1) * prodTimings.period + prod_rate;
          }
        }

        System.out.println("\n" + nbBreakpoints + " breakpoints " + chosenBreakpoints + " choisis pour la fifo "
            + prodActor.getName() + "->" + consActor.getName() + " : " + Arrays.toString(breakpointsPositions));

        // now for every breakpoint, we evaluate the cumulative production and consumption
        // again with the same formulas as before, but swapped.
        int maxBufferSize = 0;

        for (int bk = 1; bk <= nbBreakpoints; bk++) {
          final int t = breakpointsPositions[bk - 1];

          // cumulative production at bk :
          final int cumProd = ((t - prodTimings.startDate) / prodTimings.period) * prod_rate // previous firings
              + Math.min((t - prodTimings.startDate) % prodTimings.period, prod_rate) // current prod.
              + initial_tokens;

          // cumulative consumption at bk :
          final int t_delayed = t - consTimings.startDate - (consTimings.executionTime - consTimings.period);
          final int previous_periods_cons = (t_delayed <= 0) ? 0 : (t_delayed / consTimings.period) * cons_rate;

          final int current_period_cons = (consTimings.period == cons_rate) ? 0
              : Math.max((t - consTimings.startDate) % consTimings.period - (consTimings.period - cons_rate), 0);

          final int cumCons = previous_periods_cons + current_period_cons;

          System.out.println("\tbreakpoint " + bk + " à t=" + t + " : cumP = " + cumProd + "\t cumC = " + cumCons);
          maxBufferSize = Math.max(cumProd - cumCons, maxBufferSize);
        }
        System.out.println("max buffer size for " + f.getId() + " : " + maxBufferSize + " tokens");
        bufferSizes.put(f, maxBufferSize * scenario.getSimulationInfo().getDataTypeSizeInBit(f.getType()));
      } else {
        // Special case period = rate for both actor (same-throughput condition). In this case, we simply have to
        // evaluate the cumulative production at the consumer's first breakpoint.
        final int t = consTimings.startDate - cons_rate + consTimings.executionTime;

        // cumulative production at bk :
        final int cumProd = ((t - prodTimings.startDate) / prodTimings.period) * prod_rate // previous firings
            + Math.min((t - prodTimings.startDate) % prodTimings.period, prod_rate) // current prod.
            + initial_tokens;

        bufferSizes.put(f, cumProd * scenario.getSimulationInfo().getDataTypeSizeInBit(f.getType()));

        System.out.println("\nSpecial case : 1 breakpoints at " + t + " choisis pour la fifo " + prodActor.getName()
            + "->" + consActor.getName());

        System.out.println("breakpoint à t=" + t + " : cumP = " + cumProd + "\t cumC = 0");
        System.out.println("max buffer size for " + f.getId() + " : " + cumProd + " tokens");
      }
      totalSize += bufferSizes.get(f);
    }
    System.out.println("Total buffer size : " + totalSize + " bits");

    return bufferSizes;
  }

  // ====================================================
  // ============= Logging/saving functions =============
  // ====================================================

  private FpgaSchedule saveAndPrintResults(Solution solution, Map<ExecutableActor, ActorTimings> schedule,
      PrintStream writer, PrintStream resultsCsv, Solver solver, PrintStream gantt_data, IntVar latency) {

    final FpgaSchedule result = new FpgaSchedule(solution.getIntVal(latency));

    if (this.computeGantt) {
      gantt_data.println("tasks = [");
    }

    writer.println("Scheduling result : ");
    for (final var entry : schedule.entrySet()) {
      final var a = entry.getKey();
      final var res = entry.getValue();
      entry.getValue().storeResults(solution);
      entry.getValue().printSchedule(entry.getKey(), writer);
      result.addActorTimings(entry.getValue());

      if (this.computeGantt) {
        final List<ExecutableActor> preds = a.getDirectPredecessors().stream().filter(schedule::containsKey)
            .map(ea -> (ExecutableActor) ea).toList();
        final List<String> predsString = preds.stream().map(p -> "\"" + p.getName() + "\"").toList();

        final List<ExecutableActor> succs = a.getDirectSuccessors().stream().filter(schedule::containsKey)
            .map(ea -> (ExecutableActor) ea).toList();
        final var succsString = succs.stream().map(s -> "\"" + s.getName() + "\"").toList();

        final StringBuilder prod_rates = new StringBuilder("{");
        final StringBuilder cons_rates = new StringBuilder("{");
        final StringBuilder initial_tokens = new StringBuilder("{");

        // the tokens a produces
        for (final var s : succs) {
          final var fifos = getLinkingFifos(a, s);
          prod_rates.append("\"").append(s.getName()).append("\":")
              .append(fifos.getFirst().getSourcePort().getPortRateExpression().evaluateAsLong()).append(", ");

          // all the delays and initial tokens on that fifo (if present)
          final var delayed_fifos = fifos.stream().filter(Fifo::isDelayPresent);
          delayed_fifos.forEach(df -> {
            // df.getDelay().getExpression().evaluateAsLong()
            initial_tokens.append("\"").append(s.getName()).append("\":").append(getFixedDelaySize(df)).append(", ");
          });
        }

        // the tokens a consumes
        for (final var p : preds) {
          final var fifos = getLinkingFifos(p, a);
          // TODO gérer les cas où on a plusieurs fifos vers un même acteur !
          cons_rates.append("\"").append(p.getName()).append("\":")
              .append(fifos.getFirst().getTargetPort().getPortRateExpression().evaluateAsLong()).append(", ");
        }

        // all the delays and initial tokens

        prod_rates.append("}");
        cons_rates.append("}");
        initial_tokens.append("}");

        gantt_data.printf(
            "{%n \"%s\": \"%s\",%n \"%s\": %s,%n \"%s\":%s,%n \"%s\":%s,%n \"%s\":%s,%n \"%s\":%s,%n \"%s\":%s,%n "
                + "\"%s\":%s,%n \"%s\":%s,%n \"%s\":%s,%n \"%s\":%s,%n}, %n",
            "name", a.getName(), "start", res.startDate, "II", res.initiationInterval, "duration", res.executionTime,
            "period", res.period, "predecessors", predsString, "successors", succsString, "prod_rates", prod_rates,
            "cons_rates", cons_rates, "initial-tokens", initial_tokens, "RC", res.repetitionCount);
      }
    }

    final long hyperperiod = lcm(schedule.values().stream().map(a -> (long) a.period).toList());
    // System.out.printf("hyperpériode par recherche : %d \n", solution.);
    System.out.printf("hyperpériode par calcul après recherche : %d \n", hyperperiod);

    if (this.computeGantt) {
      gantt_data.println("\n]");
    }
    gantt_data.println("hyperperiod = " + hyperperiod);
    gantt_data.println("latency = " + result.latency);
    gantt_data.println("solving_duration = " + solver.getTimeCount());

    writer.println("\nlatency = " + result.latency);
    writer.println("hyperperiod = " + hyperperiod);
    writer.println("Solving time : " + solver.getTimeCount() + "s");
    writer.print("\n");

    resultsCsv.printf("%d ; %d ; %d ; %f ; %d ; %d %n", CYCLE_MAX, TOKENS_MAX, MAX_START_TIME, solver.getTimeCount(),
        result.latency, hyperperiod);

    return result;
  }

  private void printFailureAndLog(Model model, PrintStream writer) {
    writer.println();
    writer.println("No solution was found !");
    if (logLevel >= 2) {
      final List<String> truc = Arrays.asList(model.getVars()).stream().filter(v -> v.getName().endsWith("_var"))
          .map(Object::toString).toList();
      writer.printf("%s \n", String.join("\n", truc));
      final List<String> blockingVars = Arrays.asList(model.getVars()).stream()
          .filter(v -> (v instanceof IntVar) && v.getDomainSize() == 0).map(Object::toString).toList();
      writer.printf("\nblocking variables : \n", String.join("\n", blockingVars));
    } else {
      final List<
          String> truc = Arrays.asList(model.getVars()).stream()
              .filter(v -> v.getName().startsWith("period_") || v.getName().startsWith("start_")
                  || v.getName().startsWith("end_") || v.getName().startsWith("min_delay"))
              .map(Object::toString).toList();
      writer.printf("%s \n", String.join("\n", truc));
    }
  }

}
