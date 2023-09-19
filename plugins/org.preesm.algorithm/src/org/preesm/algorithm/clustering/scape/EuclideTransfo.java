package org.preesm.algorithm.clustering.scape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.check.CheckerErrorLevel;
import org.preesm.model.pisdf.check.PiGraphConsistenceChecker;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.util.LOOPSeeker;
import org.preesm.model.pisdf.util.PiSDFSubgraphBuilder;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.TimingType;

/**
 * This class divides the data parallelism according to Euclidean division based on the architecture's equivalent core
 * count. The number of equivalent cores comes down to calculating the linear function that relates the frequency of the
 * cores that make up the architecture.
 *
 * @author orenaud
 *
 */
public class EuclideTransfo {
  /**
   * Input graph.
   */
  private final PiGraph  graph;
  /**
   * Workflow scenario.
   */
  private final Scenario scenario;
  /**
   * Architecture design.
   */
  private final Design   archi;
  /**
   * Number of Processing elements.
   */
  private Long           coreEquivalent = 1L;

  private final Map<Long, List<PiGraph>> hierarchicalLevelOrdered = new HashMap<>();
  private Long                           totalLevelNumber         = 0L;
  private Long                           levelBound               = 0L;

  /**
   * Builds a EuclideTransfo object.
   *
   * @param scenario
   *          Workflow scenario.
   *
   */
  public EuclideTransfo(Scenario scenario) {
    this.graph = scenario.getAlgorithm();
    this.scenario = scenario;
    this.archi = scenario.getDesign();

  }

  /**
   * @return Transformed PiGraph
   */
  public PiGraph execute() {
    coreEquivalent = computeSingleNodeCoreEquivalent();
    // construct hierarchical structure
    fillHierarchicalStrcuture();
    // compute Euclide-able level ID
    computeDividableLevel();
    // divide ID happens between bound level that gonna be coarsely clustered and the top level
    divideIDs();
    final Map<AbstractVertex, Long> brv = PiBRV.compute(graph, BRVMethod.LCM);

    final PiGraphConsistenceChecker pgcc = new PiGraphConsistenceChecker(CheckerErrorLevel.FATAL_ALL,
        CheckerErrorLevel.NONE);
    pgcc.check(graph);
    PiBRV.printRV(brv);
    return graph;
  }

  /**
   * the method compute the number of equivalent cores that comes down to calculating the linear function that relates
   * the frequency of the cores that make up the architecture.
   *
   * @return The number of equivalent cores (not sure for Long value, may be an int)
   */

  public Long computeSingleNodeCoreEquivalent() {
    Long coreEq = 0L;
    int actorNumber = 0;
    for (final AbstractActor actor : graph.getOnlyActors()) {
      // sink and source actor replace interface for SimSDP
      if (actor instanceof Actor && !actor.getName().contains("src_") && !actor.getName().contains("snk_")) {

        Long sumTiming = 0L;
        Long slow = Long.valueOf(
            scenario.getTimings().getActorTimings().get(actor).get(0).getValue().get(TimingType.EXECUTION_TIME));
        for (final ComponentInstance opId : archi.getOperatorComponentInstances()) {
          sumTiming += Long.valueOf(scenario.getTimings().getExecutionTimeOrDefault(actor, opId.getComponent()));
          final Long timeSeek = Long
              .valueOf(scenario.getTimings().getExecutionTimeOrDefault(actor, opId.getComponent()));
          if (timeSeek < slow) {
            slow = timeSeek;
          }
        }
        coreEq += (sumTiming / slow);
        actorNumber++;
      }
    }
    if (actorNumber > 0) {
      coreEq /= actorNumber;
    } else {
      coreEq = (long) archi.getOperatorComponentInstances().size();
    }
    return coreEq;
  }

  /**
   * Identify potential candidates, i.e. players with a degree of parallelism not divisible by the number of equivalent
   * cores in the target.
   *
   */
  private void divideIDs() {
    for (Long i = levelBound; i >= 0L; i--) {
      for (final PiGraph g : hierarchicalLevelOrdered.get(i)) {
        final Map<AbstractVertex, Long> rv = PiBRV.compute(g, BRVMethod.LCM);
        for (final AbstractActor a : g.getOnlyActors()) {
          // maybe not for Special Actor
          if (rv.get(a) % coreEquivalent > 0 && rv.get(a) > coreEquivalent) {
            euclide(a, rv);
          }
        }
      }
    }
  }

  /**
   * The process consists in dividing an actor instance into 2. One will repeat QxD, the other R. To do this, we
   * duplicate the instance and redistribute the data.
   *
   * @param a
   *          The identified actor
   * @param rv
   *          The genuine repetition vector
   */

  private void euclide(AbstractActor a, Map<AbstractVertex, Long> rv) {
    //
    final Long rv2 = rv.get(a) % coreEquivalent; // rest
    final Long rv1 = rv.get(a) - rv2;// quotient * divisor
    // copy instance
    final AbstractActor copy = PiMMUserFactory.instance.copy(a);
    copy.setName(a.getName() + "2");
    copy.setContainingGraph(a.getContainingGraph());
    int index = 0;
    for (final DataInputPort in : a.getDataInputPorts()) {
      if (!in.getFifo().isHasADelay()) {
        final ForkActor frk = PiMMUserFactory.instance.createForkActor();
        frk.setName("Fork_" + a.getName() + index);
        frk.setContainingGraph(a.getContainingGraph());

        // connect din to frk
        final DataInputPort din = PiMMUserFactory.instance.createDataInputPort();
        din.setName("in");
        final Long dt = in.getFifo().getSourcePort().getExpression().evaluate() * rv.get(in.getFifo().getSource());
        final Long rt1 = in.getExpression().evaluate() * rv1;
        final Long rt2 = in.getExpression().evaluate() * rv2;
        din.setExpression(dt);

        frk.getDataInputPorts().add(din);
        final Fifo fin = PiMMUserFactory.instance.createFifo();
        fin.setType(in.getFifo().getType());
        fin.setSourcePort(in.getFifo().getSourcePort());
        fin.setTargetPort(din);

        fin.setContainingGraph(a.getContainingGraph());

        // connect fork to oEmpty_0
        final DataOutputPort dout = PiMMUserFactory.instance.createDataOutputPort();
        dout.setName("out_0");
        dout.setExpression(rt1);
        frk.getDataOutputPorts().add(dout);
        final Fifo fout = PiMMUserFactory.instance.createFifo();
        fout.setType(in.getFifo().getType());
        fout.setSourcePort(dout);
        fout.setTargetPort(in);
        fout.setContainingGraph(a.getContainingGraph());

        // connect fork to duplicated actors
        final DataOutputPort doutn = PiMMUserFactory.instance.createDataOutputPort();
        doutn.setName("out_" + 1);
        doutn.setExpression(rt2);
        frk.getDataOutputPorts().add(doutn);
        final Fifo foutn = PiMMUserFactory.instance.createFifo();
        foutn.setType(fin.getType());
        foutn.setSourcePort(doutn);
        foutn.setContainingGraph(a.getContainingGraph());
        copy.getDataInputPorts().stream().filter(x -> x.getName().equals(in.getName()))
            .forEach(x -> x.setIncomingFifo(foutn));

        index++;
      }
    }
    index = 0;
    for (final DataOutputPort out : a.getDataOutputPorts()) {
      if (!out.getFifo().isHasADelay()) {
        final JoinActor jn = PiMMUserFactory.instance.createJoinActor();
        jn.setName("Join_" + a.getName() + index);
        jn.setContainingGraph(a.getContainingGraph());

        // connect Join to dout
        final DataOutputPort dout = PiMMUserFactory.instance.createDataOutputPort();

        dout.setName("out");
        final Long dt = out.getFifo().getTargetPort().getExpression().evaluate() * rv.get(out.getFifo().getTarget());
        final Long rt1 = out.getExpression().evaluate() * rv1;
        final Long rt2 = out.getExpression().evaluate() * rv2;
        dout.setExpression(dt);
        jn.getDataOutputPorts().add(dout);
        final Fifo fout = PiMMUserFactory.instance.createFifo();
        fout.setType(out.getFifo().getType());
        fout.setSourcePort(dout);
        fout.setTargetPort(out.getFifo().getTargetPort());
        fout.setContainingGraph(a.getContainingGraph());

        // connect oEmpty_0 to Join
        final DataInputPort din = PiMMUserFactory.instance.createDataInputPort();
        din.setName("in_0");
        din.setExpression(rt1);
        jn.getDataInputPorts().add(din);
        final Fifo fin = PiMMUserFactory.instance.createFifo();
        fin.setSourcePort(out);
        fin.setTargetPort(din);
        fin.setContainingGraph(a.getContainingGraph());
        out.getFifo().setType(fout.getType());

        // connect duplicated actors to Join
        final DataInputPort dinn = PiMMUserFactory.instance.createDataInputPort();
        dinn.setName("in_" + 1);
        dinn.setExpression(rt2);
        jn.getDataInputPorts().add(dinn);
        final Fifo finn = PiMMUserFactory.instance.createFifo();
        finn.setType(fout.getType());
        finn.setTargetPort(dinn);
        finn.setContainingGraph(a.getContainingGraph());
        copy.getDataOutputPorts().stream().filter(x -> x.getName().equals(out.getName()))
            .forEach(x -> x.setOutgoingFifo(finn));

        index++;
      } else {

        // connect oEmpty delayed output to 1st duplicated actor
        final Fifo fdin = PiMMUserFactory.instance.createFifo();
        fdin.setSourcePort(out);
        copy.getDataInputPorts().stream().filter(x -> x.getFifo() == null).forEach(x -> x.setIncomingFifo(fdin));
        fdin.setContainingGraph(a.getContainingGraph());

      }
    }
    for (final ConfigInputPort cfg : a.getConfigInputPorts()) {
      copy.getConfigInputPorts().stream().filter(x -> x.getName().equals(cfg.getName()))
          .forEach(x -> PiMMUserFactory.instance.createDependency(cfg.getIncomingDependency().getSetter(), x));
      copy.getConfigInputPorts().stream().filter(x -> x.getName().equals(cfg.getName()))
          .forEach(x -> x.getIncomingDependency().setContainingGraph(cfg.getIncomingDependency().getContainingGraph()));

    }

    // remove delay
    ((PiGraph) a.getContainingGraph()).getDelays().stream().filter(x -> x.getContainingFifo().getSourcePort() == null)
        .forEach(x -> ((PiGraph) a.getContainingGraph()).removeDelay(x));
    // remove empty fifo
    ((PiGraph) a.getContainingGraph()).getFifos().stream().filter(x -> x.getSourcePort() == null)
        .forEach(x -> ((PiGraph) a.getContainingGraph()).removeFifo(x));
    ((PiGraph) a.getContainingGraph()).getFifos().stream().filter(x -> x.getTargetPort() == null)
        .forEach(x -> ((PiGraph) a.getContainingGraph()).removeFifo(x));

  }

  /**
   * depending on the highest loop and the further clustering, dividing is relevant only if cluster tend to be adapt to
   * the target
   */
  private void computeDividableLevel() {
    Long count = 0L;
    final Long gcd = 1L;
    final List<List<AbstractActor>> list = new ArrayList<>();
    // detect the highest delay
    for (final Fifo fd : graph.getFifosWithDelay()) {
      // detect loop
      final List<AbstractActor> graphLOOPs = new LOOPSeeker(fd.getContainingPiGraph()).seek();
      list.add(graphLOOPs);
      // detect
      if (!graphLOOPs.isEmpty()) {
        final PiGraph g = fd.getContainingPiGraph();
        for (Long i = 0L; i < totalLevelNumber; i++) {
          if (hierarchicalLevelOrdered.get(i).contains(g)) {
            count = Math.max(count, i);
          }
        }
      }
    }
    if (gcd >= this.coreEquivalent) {
      levelBound = count;
    } else {
      for (final List<AbstractActor> l : list) {
        new PiSDFSubgraphBuilder(graph, l, "sub_" + l.get(0).getContainingPiGraph().getName()).build();

      }
    }
  }

  /**
   * Order the hierarchical subgraph in order to compute cluster in the bottom up way
   */
  private void fillHierarchicalStrcuture() {

    for (final PiGraph g : graph.getAllChildrenGraphs()) {
      Long count = 0L;
      PiGraph tempg = g;
      while (tempg.getContainingPiGraph() != null) {
        tempg = tempg.getContainingPiGraph();
        count++;
      }
      final List<PiGraph> list = new ArrayList<>();
      list.add(g);
      if (hierarchicalLevelOrdered.get(count) == null) {
        hierarchicalLevelOrdered.put(count, list);
      } else {
        hierarchicalLevelOrdered.get(count).add(g);
      }
      if (count > totalLevelNumber) {
        totalLevelNumber = count;
      }

    }
    if (graph.getAllChildrenGraphs().isEmpty()) {
      final List<PiGraph> list = new ArrayList<>();
      list.add(graph);
      hierarchicalLevelOrdered.put(0L, list);
      totalLevelNumber = 0L;
    }

  }
}
