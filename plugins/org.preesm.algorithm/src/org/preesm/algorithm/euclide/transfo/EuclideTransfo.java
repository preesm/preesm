package org.preesm.algorithm.euclide.transfo;

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
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.check.CheckerErrorLevel;
import org.preesm.model.pisdf.check.PiGraphConsistenceChecker;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

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
  private final Long     coreNumber;

  private final Map<Long, List<PiGraph>> hierarchicalLevelOrdered = new HashMap<>();
  private Long                           totalLevelNumber         = 0L;
  private Long                           levelBound               = 0L;

  public EuclideTransfo(PiGraph graph, Scenario scenario, Design archi, Long coreNumber) {
    this.graph = graph;
    this.scenario = scenario;
    this.archi = archi;
    this.coreNumber = coreNumber;

  }

  public PiGraph execute() {
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

  private void divideIDs() {
    for (Long i = levelBound; i >= 0L; i--) {
      for (final PiGraph g : hierarchicalLevelOrdered.get(i)) {
        final Map<AbstractVertex, Long> rv = PiBRV.compute(g, BRVMethod.LCM);
        for (final AbstractActor a : g.getActors()) {
          if (a instanceof Actor && !(a instanceof SpecialActor)) {
            if (rv.get(a) % coreNumber > 0 && rv.get(a) > coreNumber) {
              euclide(a, rv);
            }
          }
        }
      }
    }
  }

  private void euclide(AbstractActor a, Map<AbstractVertex, Long> rv) {
    //
    final Long rv2 = rv.get(a) % coreNumber; // rest
    final Long rv1 = rv.get(a) - rv2;// quotient * diviseur
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

        // subsCopy.get(subRank).put(frk, 1L);
        index++;
      } else {
        // if setter
        // if(in.getFifo().getDelay().hasSetterActor()) {
        // Fifo fd = PiMMUserFactory.instance.createFifo();
        // fd.setSourcePort(in.getFifo().getDelay().getSetterPort());
        // fd.setTargetPort(in);
        // fd.setContainingGraph(key.getContainingGraph());
        //
        // }//else {
        // PreesmLogger.getLogger().log(Level.INFO, "unrolled loops requires setter and getter affected to a local
        // delay");
        // Actor set = PiMMUserFactory.instance.createActor();
        // //InitActor set = PiMMUserFactory.instance.createInitActor();
        // set.setName("setter");
        // Refinement refinement = PiMMUserFactory.instance.createCHeaderRefinement();
        //
        // set.setRefinement(refinement);
        // //((Actor) oEmpty).getRefinement().getFileName()
        // // Set the refinement
        // CHeaderRefinement cHeaderRefinement = (CHeaderRefinement) (((Actor) set).getRefinement());
        // Prototype oEmptyPrototype = new Prototype();
        // oEmptyPrototype.setIsStandardC(true);
        // //String fileName = ;//lastLevel.getActorPath().replace("/", "_");
        // cHeaderRefinement.setFilePath(((Actor) key).getRefinement().getFilePath());
        // FunctionPrototype functionPrototype = PiMMUserFactory.instance.createFunctionPrototype();
        // cHeaderRefinement.setLoopPrototype(functionPrototype);
        // functionPrototype.setName(((Actor) key).getRefinement().getFileName());
        //
        // //set.setEndReference(oEmpty);
        // set.setContainingGraph(key.getContainingGraph());
        // DataOutputPort dout = PiMMUserFactory.instance.createDataOutputPort();
        // set.getDataOutputPorts().add(dout);
        // dout.setName("out");
        // dout.setExpression(in.getFifo().getDelay().getExpression().evaluate());
        // Fifo fd = PiMMUserFactory.instance.createFifo();
        // fd.setSourcePort(set.getDataOutputPorts().get(0));
        // fd.setTargetPort(in);
        // fd.setContainingGraph(key.getContainingGraph());
        // subs.get(subRank).put(set, 1L);
        // }
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

        // }
        // if(subsCopy.get(subRank+1)==null) {
        // subsCopy.get(subRank).put(jn, 1L);
        // subsCopy.get(subRank).remove(key);
        // subsCopy.get(subRank).put(copy, rv2-rv1);
        // }else {
        // subsCopy.get(subRank+1).put(jn, 1L);
        // subsCopy.get(subRank+1).remove(key);
        // subsCopy.get(subRank+1).put(copy, rv2-rv1);
        // }
        index++;
      } else {

        // if getter
        // connect last one to getter
        // Fifo fd = PiMMUserFactory.instance.createFifo();

        // if(out.getFifo().getDelay().hasGetterActor()) {
        // Fifo fdout = PiMMUserFactory.instance.createFifo();
        // copy.getDataOutputPorts().stream().filter(x -> x.getFifo()==null).forEach(x -> x.setOutgoingFifo(fdout));
        // fdout.setTargetPort(out.getFifo().getDelay().getGetterPort());
        // fdout.setContainingGraph(key.getContainingGraph());
        //
        // }//else {
        // PreesmLogger.getLogger().log(Level.INFO, "unrolled loops requires setter and getter affected to a local
        // delay");
        // //EndActor get = PiMMUserFactory.instance.createEndActor();
        // Actor get = PiMMUserFactory.instance.createActor();
        // get.setName("getter");
        // ///******
        // Refinement refinement = PiMMUserFactory.instance.createCHeaderRefinement();
        //
        // get.setRefinement(refinement);
        // //((Actor) oEmpty).getRefinement().getFileName()
        // // Set the refinement
        // CHeaderRefinement cHeaderRefinement = (CHeaderRefinement) (((Actor) get).getRefinement());
        // Prototype oEmptyPrototype = new Prototype();
        // oEmptyPrototype.setIsStandardC(true);
        // //String fileName = ;//lastLevel.getActorPath().replace("/", "_");
        // cHeaderRefinement.setFilePath(((Actor) oEmpty).getRefinement().getFilePath());
        // FunctionPrototype functionPrototype = PiMMUserFactory.instance.createFunctionPrototype();
        // cHeaderRefinement.setLoopPrototype(functionPrototype);
        // functionPrototype.setName(((Actor) oEmpty).getRefinement().getFileName());/**/
        // //get.setInitReference(dupActorsList.get((int) (value-2)));
        //
        // get.setContainingGraph(oEmpty.getContainingGraph());
        // DataInputPort din = PiMMUserFactory.instance.createDataInputPort();
        // get.getDataInputPorts().add(din);
        // din.setName("in");
        // din.setExpression(out.getFifo().getDelay().getExpression().evaluate());
        // Fifo fdout = PiMMUserFactory.instance.createFifo();
        // dupActorsList.get((int) (value-2)).getDataOutputPorts().stream().filter(x->x.getFifo()==null).forEach(x ->
        // x.setOutgoingFifo(fdout));
        // fdout.setTargetPort(get.getDataInputPorts().get(0));
        // fdout.setContainingGraph(oEmpty.getContainingGraph());
        // }
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
    // interconnect duplicated actor on their delayed port
    // for (int i = 0; i <= 2; i++) {
    // final Fifo fd = PiMMUserFactory.instance.createFifo();
    // copy.getDataOutputPorts().stream().filter(x -> x.getFifo() == null).forEach(x -> x.setOutgoingFifo(fd));
    // copy.getDataInputPorts().stream().filter(x -> x.getFifo() == null).forEach(x -> x.setIncomingFifo(fd));
    // fd.setContainingGraph(a.getContainingGraph());
    // }
    // remove delay
    ((PiGraph) a.getContainingGraph()).getDelays().stream().filter(x -> x.getContainingFifo().getSourcePort() == null)
        .forEach(x -> ((PiGraph) a.getContainingGraph()).removeDelay(x));
    // remove empty fifo
    ((PiGraph) a.getContainingGraph()).getFifos().stream().filter(x -> x.getSourcePort() == null)
        .forEach(x -> ((PiGraph) a.getContainingGraph()).removeFifo(x));
    ((PiGraph) a.getContainingGraph()).getFifos().stream().filter(x -> x.getTargetPort() == null)
        .forEach(x -> ((PiGraph) a.getContainingGraph()).removeFifo(x));

    // brv = PiBRV.compute(graph, BRVMethod.LCM);
  }

  private void computeDividableLevel() {
    Long count = 0L;
    // detect the highest delay
    for (final Fifo fd : graph.getFifosWithDelay()) {
      // detect loop
      // TODO
      // detect
      final PiGraph g = fd.getContainingPiGraph();
      for (Long i = 0L; i < totalLevelNumber; i++) {
        if (hierarchicalLevelOrdered.get(i).contains(g)) {
          count = Math.max(count, i);
        }
      }
    }
    levelBound = count;
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
