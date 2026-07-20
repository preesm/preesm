package org.preesm.algorithm.schedule.fpga;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.search.loop.monitors.IMonitorContradiction;
import org.chocosolver.solver.search.strategy.BlackBoxConfigurator;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.selectors.values.IntValueSelector;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.RealVar;
import org.preesm.algorithm.mapper.ui.stats.StatEditorSynthesisTask;
import org.preesm.algorithm.schedule.fpga.AbstractGenericFpgaFifoEvaluator.AnalysisResultFPGA;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.algorithm.synthesis.schedule.algos.IScheduler;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInterface;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.Parameter;
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
  static int       CYCLE_MAX      = 1_000_000_000;  // chemin critique et sommer latence*brv de chaque acteur ?
  static int       TOKENS_MAX     = CYCLE_MAX / 10; // arbitrary
  static int       MAX_START_TIME = CYCLE_MAX / 2;  // abitrary
  static final int THRESHOLD_RV   = 1_000;          // arbitrary

  static final int logLevel     = 2;   // between 2 and 2 included
  boolean          logsInFile   = true;
  boolean          computeGantt = true;

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
  }

  public FPGAConstraintScheduler(boolean computeGantt, boolean logsInFile) {
    super();
    this.computeGantt = computeGantt;
    this.logsInFile = logsInFile;
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
  private ActorTimings extractMostConstrainedPeriodTimings(Map<ExecutableActor, ActorTimings> schedule) {
    final var result = schedule.values().stream()
        .min(Comparator.comparingInt(at -> (CYCLE_MAX / at.repetitionCount - at.initiationInterval) / at.basisOfPeriod))
        .orElse(null);

    if (result == null) {
      PreesmLogger.getLogger().log(Level.SEVERE,
          "No actor in " + schedule.keySet() + " has a smallest domain size ! How is that even possible ??");
    }

    return result;
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
    private int             basisOfPeriod = 1;
    private IntVar          basisMultiplier;
    private IntVar[]        startDatesFromInputs;
    private ExecutableActor actor;

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
        writer.printf("%n%s : start=%d  \t  latency=%d  \t  II=%d  \t  end=%d  \t  period=%d", a.getName(), startDate,
            executionTime, initiationInterval, endDate, period);
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
        startDatesFromInputs = null;
        actor = null;
      }
    }

    /**
     * Computes the actor's period's basis.
     *
     * @param actors
     *          the list of executable actors. All other actors won't be considered in the calculation.
     * @param schedule
     *          the schedule.
     */
    private void computePeriodBasis(List<ExecutableActor> actors, Map<ExecutableActor, ActorTimings> schedule) {
      final var inputFifos = actor.getIncomingEdges().stream().filter(e -> actors.contains(e.getSource()))
          .map(f -> (Fifo) f).toList();

      for (final Fifo f : inputFifos) {
        final AbstractActor sourceActor = f.getSource();
        final ActorTimings sourceTimings = schedule.get(sourceActor);

        final int prod_rate = getProdRate(f);
        final int cons_rate = getConsRate(f);

        updatePeriodsBasis(prod_rate, cons_rate, sourceTimings, this);
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
      Map<ExecutableActor, ActorTimings> schedule) {

    // TODO : tous les acteurs sont liés par une égalité de débit : dès qu'on a fixé la période d'un acteur, toutes
    // les autres en découlent ! On n'a donc besoin de spécifier qu'une variable de période, et tout le reste n'est
    // qu'à calculer : period_var = other_period * tp/tp.

    for (final var actor : actors) {

      final ActorTimings at = schedule.get(actor);

      at.computePeriodBasis(actors, schedule);

      at.basisMultiplier = model.intVar("basisMultiplier_" + actor.getName() + "_var", 0,
          at.period_var.getUB() / at.basisOfPeriod);

      // CONSTRAINT : the period is proportional to its basis. This basis must have been computed beforehand.
      model.arithm(at.period_var, "=", at.basisMultiplier, "*", at.basisOfPeriod).post();

      final boolean allPredecessorsDataInterface = actor.getDirectPredecessors().stream()
          .filter(AbstractActor.class::isInstance).allMatch(DataInputInterface.class::isInstance);

      if (allPredecessorsDataInterface) {
        // If all the predecessors are data interfaces, we assume the actor can start right away
        model.arithm(at.startDate_var, "=", 0).post();

      } else {
        // Start date constraints from predecessors :
        int nb = 0;
        for (final Fifo fifo : actor.getDataInputPorts().stream().map(DataPort::getFifo)
            .filter(f -> schedule.containsKey(f.getSource())).toList()) {
          final ExecutableActor producer = (ExecutableActor) fifo.getSource();
          final ActorTimings pt = schedule.get(producer);

          final int prod_rate = (int) fifo.getSourcePort().getExpression().evaluateAsLong();
          final int cons_rate = (int) fifo.getTargetPort().getExpression().evaluateAsLong();

          at.startDatesFromInputs[nb] = model.intVar("min_delay_" + actor.getName() + "_" + fifo.getId(),
              at.startDate_var.getLB(), at.startDate_var.getUB());

          final int mult_ceil = Math.ceilDiv(cons_rate, prod_rate);
          final int mult_floor = Math.floorDiv(cons_rate, prod_rate);

          at.startDatesFromInputs[nb]
              .eq(pt.startDate_var.add(pt.period_var.mul(mult_floor)).add(pt.executionTime).sub(prod_rate * mult_ceil))
              .post();

          nb++;
        }
        model.max(at.startDate_var, at.startDatesFromInputs).post();

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

    model.member(at.period_var, IntStream.range(range_start, range_end).map(n -> n * at.basisOfPeriod).toArray())
        .post();
  }

  private ActorTimings createActorTimings(ExecutableActor actor, List<ExecutableActor> actors, Scenario scenario,
      Component component, Map<AbstractVertex, Long> brv, Model model) {

    final ActorTimings res = new ActorTimings(actor);

    res.repetitionCount = brv.get(actor).intValue();

    // default value for broadcast and round buffer actors
    if (actor instanceof SpecialActor) {
      // a special actor's latency is estimated to be its max rate of input/output.
      // TODO est-ce que ça marche tout le temps ? probablement pas...
      res.executionTime = actor.getAllDataPorts().stream().map(dp -> (int) dp.getPortRateExpression().evaluateAsLong())
          .max(Integer::compare).orElse(1);
      res.initiationInterval = 1;

    } else {
      res.executionTime = (int) scenario.getTimings().evaluateTimingOrDefault(actor, component,
          TimingType.EXECUTION_TIME);
      res.initiationInterval = (int) scenario.getTimings().evaluateTimingOrDefault(actor, component,
          TimingType.INITIATION_INTERVAL);
    }

    // UB to reduce domain size : CYCLE_MAX is at least one graph period
    res.period_var = model.intVar("period_" + actor.getName() + "_var", res.initiationInterval,
        CYCLE_MAX / res.repetitionCount);

    // must start early enough to finish all its periods (=rv) before MAX_CYCLE
    // could even be bounded by its followers' latencies sum
    res.startDate_var = model.intVar("start_" + actor.getName() + "_var", 0,
        CYCLE_MAX - res.executionTime * res.repetitionCount); // affinable

    // the number of incoming fifos
    final int nbPreds = (int) actor.getIncomingEdges().stream().filter(e -> actors.contains(e.getSource())).count();
    res.startDatesFromInputs = model.intVarArray("startDatesFromPreds" + actor.getName(), nbPreds, 0,
        CYCLE_MAX - res.executionTime * res.repetitionCount);

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
  private void updatePeriodsBasis(int prod_rate, int cons_rate, ActorTimings sourceTimings,
      ActorTimings targetTimings) {

    final int basisOfTp = (int) (prod_rate / gcd(prod_rate, cons_rate));
    final int basisOfTc = (int) (cons_rate / gcd(prod_rate, cons_rate));

    sourceTimings.basisOfPeriod = lcm(sourceTimings.basisOfPeriod, basisOfTp);
    targetTimings.basisOfPeriod = lcm(targetTimings.basisOfPeriod, basisOfTc);
  }

  // -------------------------------------------
  // ------------ Scheduling method ------------
  // -------------------------------------------

  @Override
  /**
   * The method assumes it is scheduling a flat graph. If it encounters a cluster, it will be treated as an actor.
   */
  public SynthesisResult scheduleAndMap(final PiGraph piGraph, final Design slamDesign, final Scenario scenario) {

    final Map<ExecutableActor, ActorTimings> schedule = new HashMap<>();
    final List<IntVar> variablesToAssign = new LinkedList<>();

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
    } else {
      writer = System.out;
    }

    try {
      resultsCsv = new PrintStream(fileName + ".csv");
      resultsCsv.println(
          "CYCLE_MAX ; TOKENS_MAX ; MAX_START_TIME ; solving time (s) ; latency (cycles) ; graph period (cycles)");
    } catch (final FileNotFoundException e) {
      e.printStackTrace();
    }

    final int[] token_divisors = new int[] { 1, }; // 2, 5, 20, 50, 100, 10
    final int[] start_divisors = new int[] { 1, };// 2, 5, 20,50, 100, 10
    final int[] nbs_cycles = new int[] { 1_000_000_000 };// 10_000, 100_000, 1_000_000, 10_000_000, 100_000_000,
    final Map<String, List<Integer>> parameters = new HashMap<>();
    parameters.put("size", Arrays.asList(100)); // 10_000, 100_000, 1_000_000,10_000_000

    final int max_time_seconds = 10;
    final int nb_comb = token_divisors.length * start_divisors.length * nbs_cycles.length
        * parameters.values().stream().mapToInt(l -> l.size()).sum();
    System.out.printf("Total number of combinations : %d %n", nb_comb);
    System.out.printf("Estimated max duration : %d seconds %n", nb_comb * max_time_seconds);

    int nbRun = 0;
    Expression savedParameterValue = null;
    for (final var paramName : parameters.keySet()) {
      final Parameter graphParameter = piGraph.getParameters().stream().filter(p -> p.getName().contains(paramName))
          .findFirst().orElse(null);

      int nbParams;
      if (graphParameter != null) {
        savedParameterValue = graphParameter.getExpression();
        nbParams = parameters.get(paramName).size();
      } else {
        writer.println("No parameter named " + paramName + " found in graph " + piGraph.getName()
            + ", running scheduling with only base parameter values.");
        nbParams = 1;
      }

      for (int param_index = 0; param_index < nbParams; param_index++) {
        if (graphParameter != null) {
          graphParameter.setExpression(parameters.get(paramName).get(param_index));
        }

        final Model model = new Model("Period computing");
        final IntVar variableToOptimize = buildModel(model, piGraph, slamDesign, scenario, schedule, variablesToAssign);

        // --------
        // Solution
        // --------
        final Solver solver = model.getSolver();

        solver.log().remove(System.out);
        solver.log().add(writer);

        if (logLevel >= 1) {
          writer.printf(" Number of variables : %d %n Number of constraints : %d %n", model.getNbVars(),
              model.getNbCstrs());
          // solver.verboseSolving(1000); // marche pas
        }
        if (logLevel >= 2) {
          solver.showStatisticsDuringResolution(1000);
          model.displayPropagatorOccurrences(); // pour vérifier que des propagateurs safe sont utilisés
          solver.showContradiction();
          solver.showDecisions();
        }

        for (final int tokens_divisor : token_divisors) {
          for (final int start_divisor : start_divisors) {
            for (final int nb_cycles : nbs_cycles) {
              nbRun++;
              writer.printf("%n%n");
              System.out.printf("Starting run number %d/%d %n", nbRun, nb_comb);

              CYCLE_MAX = nb_cycles;
              TOKENS_MAX = CYCLE_MAX / tokens_divisor;
              MAX_START_TIME = CYCLE_MAX / start_divisor;

              // No need to enumerate all parameters, since only one is set to a non-base value at a time.
              final String config = String.format("CYCLE_MAX=%d-TOKENS_MAX=%d-MAX_START_TIME=%d-%s=%s", CYCLE_MAX,
                  TOKENS_MAX, MAX_START_TIME, paramName, parameters.get(paramName).get(param_index));

              writer.println(config);
              if (writer != System.out) {
                System.out.printf("\tRunning config : %s %n", config);
              }

              PrintStream gantt_data = null;
              if (this.computeGantt) {
                try {
                  gantt_data = new PrintStream(dir.getAbsolutePath() + "/gantt_data_" + config + ".py");
                } catch (final FileNotFoundException e) {
                  e.printStackTrace();
                }
              }

              // solver.makeCompleteStrategy(true); // Possiblement utile ! Enquêter.
              solver.observeSolving();
              // solver.toCSV();
              solver.limitTime(max_time_seconds + "s");

              // Pour suivre l'arbre d'exploration et en sortir un .dot graphviz
              final Closeable searchTreeFile = solver.outputSearchTreeToGraphviz(fileName + ".dot");

              setStrategy(solver, variablesToAssign.toArray(new IntVar[0]));

              BlackBoxConfigurator.forCOP(); // Utile ? J'ai l'impression que non...

              Solution solution = new Solution(model);

              if (logLevel >= 1) {
                solver.plugMonitor((IMonitorContradiction) cex -> System.out.println("Contradiction on " + cex.v
                    + (cex.v != null ? "with domain size " + cex.v.getDomainSize() : "") + " via " + cex.c));
              }
              if (logLevel >= 2) {
                writer.printf("%s %n", model.toString());
              }

              runInitialPropagation(solver, writer);
              System.out.println(solver.getSearch());

              writer.println("The variables to instanciate after the initial propagation :");
              writer.println(variablesToAssign.stream().map(Object::toString).collect(Collectors.joining("\n")));

              solution = solver.findOptimalSolution(variableToOptimize, Model.MINIMIZE);
              // solution = solver.findLexOptimalSolution(variablesToAssign.toArray(new IntVar[0]), Model.MINIMIZE,
              // null);

              try {
                if (solver.getSolutionCount() != 0) {
                  System.out.println("Solution found !");
                  saveAndPrintResults(solution, variableToOptimize, variablesToAssign, schedule, writer, resultsCsv,
                      solver, gantt_data);
                  // solver.printStatistics();
                  // SolvingStatisticsFlow.toJSON(solver); // marche pas, dommage
                } else {
                  printFailureAndLog(model, writer);
                }
              } catch (final Exception e) {
                e.printStackTrace();
              }

              if (computeGantt) {
                gantt_data.close();
              }

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
      // reset to base value
      if (graphParameter != null) {
        graphParameter.setExpression(savedParameterValue);
      }
    }

    if (writer != System.out) {
      writer.close();
    }

    final StatEditorSynthesisTask truc = new StatEditorSynthesisTask();
    final Map<String, Object> inputs = new HashMap<>();
    inputs.put("scenario", scenario);
    inputs.put("architecture", scenario.getDesign());
    inputs.put("algorithm", piGraph);
    // truc.execute(null, null, null, null, null)
    return new AnalysisResultFPGA(piGraph, null, null);
  }

  // ============================================
  // ============= Solver functions =============
  // ============================================

  private IntVar buildModel(Model model, final PiGraph piGraph, final Design slamDesign, final Scenario scenario,
      Map<ExecutableActor, ActorTimings> schedule, List<IntVar> variablesToAssign) {

    final Map<AbstractVertex, Long> brv = PiBRV.compute(piGraph, BRVMethod.LCM);

    // TODO récupérer en ordre d'exécution pour forcer startDate du 1er acteur à 0 ?
    final List<ExecutableActor> actors = getNonDataInterfaceActors(piGraph);

    // we will sort the actors based on their distance to the graph's input interfaces.
    sortByDistanceToInput(piGraph, actors);

    // for now, we will not consider fifos linking actors from/to data interfaces.
    // It may by interesting to model them as actors with a start date equal to the comm. time and rate of comm size
    final List<Fifo> fifos = getRelevantFifos(piGraph);

    // the graph is supposed to be mapped to a single PE type (FPGA, CPU, DSP...)
    final Component Fpga = scenario.getPossibleMappings(piGraph).getFirst().getComponent();

    // Attention ! Le déclarer comme IntVar[] comme ça créerait de nouvelle variables qui devraient être mises à .eq()
    // mieux : déclarer un tableau mais pas de variable choco
    final IntVar[] latencies = new IntVar[actors.size()];
    IntVar period;

    // useful later
    final IntVar zero = model.intVar("zero", 0);

    int i = 0;
    for (final ExecutableActor actor : actors) {
      final ActorTimings at = createActorTimings(actor, actors, scenario, Fpga, brv, model);

      schedule.put(actor, at);
      latencies[i] = at.endDate_var;
      i++;
    }

    // Now that all the timings are initialized, we can compute the bases and set the constraints
    for (final ActorTimings at : schedule.values()) {
      at.computePeriodBasis(actors, schedule);
    }
    setTimingConstraints(actors, model, schedule);

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

      final double rateGcd = gcd((long) prod_rate * consTimings.basisOfPeriod,
          (long) cons_rate * prodTimings.basisOfPeriod);
      final double rateBasisGcd = gcd(prod_rate, cons_rate);

      final double precision = 1d; // since we work on int multiplication, no need to waste precision on decimal
      final RealVar left = model.realVar("left__" + fifo.getId() + "_var",
          (double) cons_rate * prodTimings.period_var.getLB() / rateBasisGcd,
          (double) cons_rate * prodTimings.period_var.getUB() / rateBasisGcd, precision);
      final RealVar right = model.realVar("right__" + fifo.getId() + "_var",
          (double) prod_rate * consTimings.period_var.getLB() / rateBasisGcd,
          (double) prod_rate * consTimings.period_var.getUB() / rateBasisGcd, precision);

      // méthode 1 : calcul avec des réels

      // final RealVar sourcePeriodReal = model.realVar("sourcePeriodReal_" + fifo.getId(),
      // prodTimings.period_var.getLB(),
      // prodTimings.period_var.getUB(), precision);
      // model.eq(sourcePeriodReal, prodTimings.period_var).post();
      // left.eq(sourcePeriodReal.mul(cons_rate / rateGcd)).post();
      //
      // final RealVar targetPeriodReal = model.realVar("targetPeriodReal_" + fifo.getId(),
      // consTimings.period_var.getLB(),
      // consTimings.period_var.getUB(), precision);
      // model.eq(targetPeriodReal, consTimings.period_var).post();
      // right.eq(targetPeriodReal.mul(prod_rate / rateGcd)).post();
      //
      // left.eq(right).post();

      // méthode 2 : réels avec pgcd des bases et taux, avec ordre des opé pour éviter les virgules.
      // bonne nouvelle, choco semble respecter l'ordre des opérations et ne pas optimiser le .mul.did impliquant 2
      // constantes
      // --> on peut éviter les décimales intermédiaires et donc simplifier par le gros pgcd

      final RealVar sourcePeriodReal = model.realVar("sourcePeriodReal_" + fifo.getId(), prodTimings.period_var.getLB(),
          prodTimings.period_var.getUB(), precision);
      model.eq(sourcePeriodReal, prodTimings.period_var).post();
      left.eq(sourcePeriodReal.mul(cons_rate).div(rateBasisGcd)).post();

      final RealVar targetPeriodReal = model.realVar("targetPeriodReal_" + fifo.getId(), consTimings.period_var.getLB(),
          consTimings.period_var.getUB(), precision);
      model.eq(targetPeriodReal, consTimings.period_var).post();
      right.eq(targetPeriodReal.mul(prod_rate).div(rateBasisGcd)).post();

      left.eq(right).post();

      // -- Computing breakpoints positions --

      // nombre de breakpoints de chaque
      final int nbBreakpointsProd = lcm(prod_rate, cons_rate) / prod_rate; // bornes : [1 ; cons_rate]
      final int nbBreakpointsCons = lcm(prod_rate, cons_rate) / cons_rate; // bornes : [1 ; prod_rate]
      final String chosenBreakpoints = nbBreakpointsProd <= nbBreakpointsCons ? "Prod" : "Cons";
      final int nbBreakpoints = Math.min(nbBreakpointsProd, nbBreakpointsCons);

      final IntVar[] breakpointsPositions = model.intVarArray(
          "breakpointPosition" + chosenBreakpoints + "_" + fifo.getId() + "_var", nbBreakpoints, 0, CYCLE_MAX);

      if (chosenBreakpoints.equals("Prod")) {
        // producer breakpoints
        for (int bk = 1; bk <= nbBreakpointsProd; bk++) {
          // delay_prod + bk * periodProd - taux_prod
          breakpointsPositions[bk - 1].eq(prodTimings.startDate_var.add(prodTimings.period_var.mul(bk)).sub(prod_rate))
              .post();
        }
      } else {
        // consumer breakpoints
        for (int bk = 1; bk <= nbBreakpointsCons; bk++) {
          // delay_cons + (bk - 1) * periodCons + taux_cons
          breakpointsPositions[bk - 1]
              .eq(consTimings.startDate_var.add(consTimings.period_var.mul(bk - 1)).add(cons_rate)).post();
        }
      }

      // les valeurs aux breakpoints
      final IntVar[] cumP = model.intVarArray("cumP_" + fifo.getId() + "_var", nbBreakpoints, 0, TOKENS_MAX);
      final IntVar[] cumC = model.intVarArray("cumC_" + fifo.getId() + "_var", nbBreakpoints, 0, TOKENS_MAX);

      final IntVar rate_cons = model.intVar("rate_cons_" + fifo.getId(), cons_rate);

      for (int bk = 1; bk <= nbBreakpoints; bk++) {
        final IntVar t = breakpointsPositions[bk - 1];

        // -------------------------------------------
        // -- cumulated production at breakpoint bk --
        // -------------------------------------------

        // t - delay_prod : always positive, since breakpoints at prod are after it started, and cons starts after prod
        // at most the biggest breakpoint, which are capped to CYCLE_MAX
        final IntVar delta_prod = model.intVar("deltaProd_" + fifo.getId() + "_" + bk + "_var", 0, MAX_START_TIME);

        final IntVar prodInPeriod = model.intVar("inPeriodProd_" + fifo.getId() + "_" + bk + "_var", 0, prod_rate);
        final IntVar prodFromPreviousPeriods = model
            .intVar("prodFromPreviousPeriods_" + fifo.getId() + "_" + bk + "_var", 0, TOKENS_MAX);

        model.arithm(delta_prod, "=", t, "-", prodTimings.startDate_var).post();

        // in-period production

        if (chosenBreakpoints.equals("Prod")) {
          // when the breakpoints are on the producer, it has not produced tokens yet
          // ==> prodInPeriod = 0 for all breakpoints
          model.arithm(prodInPeriod, "=", zero).post();
        } else {
          // Non-simplified formula : prodInPeriod = (t - delay_prod) % periodProd - periodProd + taux_prod
          final IntVar prod_modulo = model.intVar("prod_modulo_" + fifo.getId(), 0,
              Math.min(prodTimings.period_var.getUB(), TOKENS_MAX));
          final IntVar inter2 = model.intVar("inter2_" + bk + "_var", 0, TOKENS_MAX); // forced to 0 or more later

          // prod_modulo = delta_prod % period_var
          model.mod(delta_prod, prodTimings.period_var, prod_modulo).post();

          inter2.eq(prod_modulo.sub(prodTimings.period_var).add(prod_rate)).post();

          model.max(prodInPeriod, inter2, zero).post(); // force it to be 0 or more
        }

        // Production from previous periods : ((t - delay_prod) / periodProd) * taux_prod
        // choco operates on natural numbers so the fraction results are automatically floored
        prodFromPreviousPeriods.eq((delta_prod.div(prodTimings.period_var)).mul(prod_rate)).post();

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
        model.max(delta_cons, t.sub(consTimings.startDate_var).intVar(), zero).post();

        if (chosenBreakpoints.equals("Cons")) {
          // in this special case, the breakpoint happens always after the consumer has consumed
          // a period's worth of tokens ==> consInPeriod = rate_cons
          model.arithm(consInPeriod, "=", cons_rate).post();

        } else {
          // this intermediate variable cannot be negative since we force delta_cons >= 0
          final IntVar cons_modulo = model.intVar("cons_modulo_" + bk + "_var", 0,
              Math.min(consTimings.period_var.getUB(), TOKENS_MAX));

          // (t - delta_cons) % periodCons : the consumption in this period
          model.mod(delta_cons, consTimings.period_var, cons_modulo).post();

          // min(x, rate_cons) <= rate_cons
          model.min(consInPeriod, cons_modulo, rate_cons).post();
        }

        // ((t - delay_cons) / periodCons) * taux_cons
        consFromPreviousPeriods.eq(delta_cons.div(consTimings.period_var).mul(cons_rate)).post();

        // consFromPreviousPeriods + consInPeriod
        model.arithm(cumC[bk - 1], "=", consFromPreviousPeriods, "+", consInPeriod).post();

        // CONSTRAINT : production superior or equal to consumption at the breakpoint
        model.arithm(cumP[bk - 1], ">=", cumC[bk - 1]).post();
      }
    }

    final ActorTimings at = extractMostConstrainedPeriodTimings(schedule);
    if (at.repetitionCount >= THRESHOLD_RV) {
      createEnumeratedDomainForPeriod(at, model);
    }

    period = at.period_var;

    // objectif : optimiser la latence = la date de fin du dernier acteur relative à une période
    // on pourrait utiliser le chemin critique, mais pour le moment je vais juste optimiser la fin d'exécution de
    // l'acteur le plus tardif
    // TODO : trouver des contraintes pour réduire l'espace d'état de latency, là on ne fait qu'énumérer comme des cons.
    final IntVar latency = model.max("latency", getEdgeActorsEndDates(schedule).toArray(new IntVar[0]));

    // branch on it even if no gantt, to reduce latency's domain size
    variablesToAssign.add(period);
    // variablesToAssign.addAll(Arrays.asList(latencies));

    if (this.computeGantt) {
      // we want to know all the actor's periods, to reduce domain size and/or for the gantt. Stream() preserves order
      // so we can optimize first the first actors, with the lowest startDate, to further constraints their successors,
      // as well as their start date, to reduce domain size and/or for the gantt
      // variablesToAssign.addAll(actors.stream().map(a -> schedule.get(a).period_var).toList());

      // actors.stream().map(a -> schedule.get(a).startDatesFromInputs)
      // .forEach(sdfi -> variablesToAssign.addAll(Arrays.asList(sdfi)));

      // variablesToAssign.addAll(actors.stream().map(a -> schedule.get(a).startDate_var).toList());
    }

    // Branch on latency in last, because otherwise choco tries all values...
    variablesToAssign.add(latency);

    return latency; // we return the variable to minimize (could be the period too)
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

    // solver.setSearch(Search.lastConflict(Search.intVarSearch(// our strategy :
    // new InputOrder<>(solver.getModel()), // We will branch on variables in input order
    // new IntDomainMiddle(true), // and assign them to their domain's middle value
    // variablesToAssign)));

    final IntValueSelector vs = var -> {
      System.out.println("Selecting value " + var.getLB() + " for " + var);
      return var.getLB();
    };

    // solver.setSearch(Search.intVarSearch(// our strategy :
    // new InputOrder<>(solver.getModel()), // We will branch on variables in input order
    // new IntDomainMiddle(true), // and assign them to their domain's middle value
    // variablesToAssign));
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
      if (logLevel >= 1) {
        writer.println(e);
      }
      writer.println("Initial propagation caught a contradiction : model might be unsolvable.");
    }
  }

  // ====================================================
  // ============= Logging/saving functions =============
  // ====================================================

  private FpgaSchedule saveAndPrintResults(Solution solution, IntVar varToOptimize, List<IntVar> variablesToAssign,
      Map<ExecutableActor, ActorTimings> schedule, PrintStream writer, PrintStream resultsCsv, Solver solver,
      PrintStream gantt_data) {

    final FpgaSchedule result = new FpgaSchedule(solution.getIntVal(varToOptimize));

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

        // the tokens a produces
        for (final var s : succs) {
          final var fifos = getLinkingFifos(a, s);
          prod_rates.append("\"").append(s.getName()).append("\":")
              .append(fifos.getFirst().getSourcePort().getPortRateExpression().evaluateAsLong()).append(", ");
        }

        // the tokens a consumes
        for (final var p : preds) {
          final var fifos = getLinkingFifos(p, a);
          // TODO gérer les cas où on a plusieurs fifos vers un même acteur !
          cons_rates.append("\"").append(p.getName()).append("\":")
              .append(fifos.getFirst().getTargetPort().getPortRateExpression().evaluateAsLong()).append(", ");
        }
        prod_rates.append("}");

        cons_rates.append("}");

        gantt_data.printf(
            "{%n \"%s\": \"%s\",%n \"%s\": %s,%n \"%s\":%s,%n \"%s\":%s,%n \"%s\":%s,%n \"%s\":%s,%n \"%s\":%s,%n "
                + "\"%s\":%s,%n \"%s\":%s,%n \"%s\":%s,%n}, %n",
            "name", a.getName(), "start", res.startDate, "II", res.initiationInterval, "duration", res.executionTime,
            "period", res.period, "predecessors", predsString, "successors", succsString, "prod_rates", prod_rates,
            "cons_rates", cons_rates, "RC", res.repetitionCount);
      }
    }

    final long hyperperiod = lcm(schedule.values().stream().map(a -> (long) a.period).toList());

    if (this.computeGantt) {
      gantt_data.println("\n]");
      gantt_data.println("hyperperiod = " + hyperperiod);
    }

    writer.println("\nlatency = " + result.latency);
    writer.println("hyperperiod = " + hyperperiod);
    writer.println("Solving time : " + solver.getTimeCount() + "s");
    writer.print("\n");

    resultsCsv.printf("%d ; %d ; %d ; %f ; %d ; %d %n", CYCLE_MAX, TOKENS_MAX, MAX_START_TIME, solver.getTimeCount(),
        result.latency, hyperperiod);

    return result;
  }

  private void printFailureAndLog(Model model, PrintStream writer) {
    writer.println("No solution was found !");
    if (logLevel >= 2) {
      final List<String> truc = Arrays.asList(model.getVars()).stream().filter(v -> v.getName().endsWith("_var"))
          .map(Object::toString).toList();
      writer.printf("%s %n", String.join("\n", truc));
      final List<String> blockingVars = Arrays.asList(model.getVars()).stream()
          .filter(v -> (v instanceof IntVar) && v.getDomainSize() == 0).map(Object::toString).toList();
      writer.printf("\nblocking variables : \n", String.join("\n", blockingVars));
    } else {
      final List<
          String> truc = Arrays.asList(model.getVars()).stream()
              .filter(v -> v.getName().startsWith("period_") || v.getName().startsWith("start_")
                  || v.getName().startsWith("end_") || v.getName().startsWith("min_delay"))
              .map(Object::toString).toList();
      writer.printf("%s %n", String.join("\n", truc));
    }
  }

}
