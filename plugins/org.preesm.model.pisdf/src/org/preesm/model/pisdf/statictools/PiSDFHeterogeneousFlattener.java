/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2027) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2018 - 2021)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2022 - 2024)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
 * Jacques Morin [jacques.morin@insa-rennes.fr] (2024-2027)
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
/**
 *
 */
package org.preesm.model.pisdf.statictools;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataInterface;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.check.CheckerErrorLevel;
import org.preesm.model.pisdf.check.PiGraphConsistenceChecker;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.util.PiMMSwitch;

/**
 * @author farresti
 *
 */
public class PiSDFHeterogeneousFlattener extends PiMMSwitch<Boolean> {

  /** The result. */
  // Flat graph created from the outer graph
  private final PiGraph result;

  /** Basic repetition vector of the graph */
  private final Map<AbstractVertex, Long> brv;

  /** Map from original PiMM vertices to generated DAG vertices */
  private final Map<AbstractActor, AbstractActor> actor2actor = new LinkedHashMap<>();

  /** Current Single-Rate Graph name */
  private final String graphName;

  /** Current graph prefix */
  private final String graphPrefix;

  private final Map<Parameter, Parameter> param2param = new LinkedHashMap<>();

  private final List<String> flatteningExclusionList;

  /**
   * Instantiates a new abstract StaticPiMM2ASrPiMMVisitor.
   *
   *
   */
  private PiSDFHeterogeneousFlattener(Map<AbstractVertex, Long> brv) {
    this.result = PiMMUserFactory.instance.createPiGraph();
    this.brv = brv;
    this.graphName = "";
    this.graphPrefix = "";
    this.flatteningExclusionList = new LinkedList<>();
  }

  /**
   * Instantiates a new abstract StaticPiMM2ASrPiMMVisitor. Creates result as a cluster if necessary.
   *
   */
  private PiSDFHeterogeneousFlattener(Map<AbstractVertex, Long> brv, boolean cluster, List<String> exclusionList) {
    this.result = PiMMUserFactory.instance.createPiGraph();
    this.result.setClusterValue(cluster);
    this.brv = brv;
    this.graphName = "";
    this.graphPrefix = "";
    this.flatteningExclusionList = new LinkedList<>();
  }

  public static final PiGraph flatten(final PiGraph graph, List<String> exclusionList) {
    // Check consistency of the graph (throw exception if recoverable or fatal error)
    final PiGraphConsistenceChecker pgcc = new PiGraphConsistenceChecker(CheckerErrorLevel.FATAL_ANALYSIS,
        CheckerErrorLevel.NONE);
    pgcc.check(graph);

    // 0. we copy the graph since the transformation has side effects (especially on delay actors)
    final PiGraph graphCopy = PiMMUserFactory.instance.copyPiGraphWithHistory(graph);

    recursiveFlatten(graphCopy, exclusionList);

    return graphCopy;
  }

  private static void recursiveFlatten(final PiGraph graph, List<String> exclusionList) {
    for (final PiGraph subGraph : graph.getChildrenGraphs()) {
      recursiveFlatten(subGraph, exclusionList);
      if (!subGraph.isCluster() && !exclusionList.contains(subGraph.getName())) {
        // the graph is not a cluster, nor excluded from flattening
        // flattened it will be
        flattenGraph(subGraph);
      }
    }
  }

  private static void flattenGraph(PiGraph graph) {
    final PiGraph upperGraph = graph.getContainingPiGraph();
    PreesmCopyTracker.getOriginalSource(graph);
    // 1 : redirect all config interfaces' links the the parameter in the upper graph
    for (final ConfigInputInterface cii : graph.getConfigInputInterfaces()) {
      // get all config links coming out of this cii
      final ISetter upperSetter = cii.getGraphPort().getIncomingDependency().getSetter();

      // copy because I'm removing from the dependencies list as I iterate
      final var innerDeps = cii.getOutgoingDependencies().stream().toList();
      final var outerDep = cii.getGraphPort().getIncomingDependency();

      for (final Dependency dep : innerDeps) {
        // redirect inner dependencies to the upper parameter
        // if the dependency's target is a data interface, simply remove the dependency
        if (dep.getTarget() instanceof DataInterface) {
          graph.removeDependency(dep);
        } else {
          dep.setSetter(upperSetter);
          upperGraph.addDependency(dep);
        }
      }
      // lastly we remove the cii from the graph (which automatically removes its dependencies)
      graph.removeParameter(cii);
      upperGraph.removeDependency(outerDep);
      upperSetter.getOutgoingDependencies().remove(outerDep);

    }

    // 2 : replace connections to data interfaces by the upper graph's connected actor
    // inner fifos are discarded, upper fifos are re-routed from the interface to the inner actor
    for (final DataInputInterface dip : graph.getDataInputInterfaces()) {
      final Fifo upperFifo = dip.getGraphPort().getIncomingFifo();
      final Fifo innerFifo = dip.getDataOutputPorts().getFirst().getOutgoingFifo();
      final long innerRate = innerFifo.getTargetPort().getPortRateExpression().evaluateAsLong();
      final DataInputPort newTargetPort = innerFifo.getTargetPort();

      graph.removeFifo(innerFifo);
      newTargetPort.setExpression(innerRate);
      upperFifo.setTargetPort(newTargetPort);
      graph.removeActor(dip);
    }
    for (final DataOutputInterface dop : graph.getDataOutputInterfaces()) {
      final Fifo upperFifo = dop.getGraphPort().getOutgoingFifo();
      final Fifo innerFifo = dop.getDataInputPorts().getFirst().getIncomingFifo();
      final long innerRate = innerFifo.getSourcePort().getPortRateExpression().evaluateAsLong();
      final DataOutputPort newSourcePort = innerFifo.getSourcePort();

      graph.removeFifo(innerFifo);
      newSourcePort.setExpression(innerRate);
      upperFifo.setSourcePort(newSourcePort);
      graph.removeActor(dop);
    }

    // 3 : move all parameters and actors (except data interfaces) to the upper graph
    for (final AbstractActor a : graph.getActors()) {
      upperGraph.addActor(a);
      for (final DataPort dp : a.getAllDataPorts()) {
        final Fifo f = dp.getFifo();
        if (f.getTarget() instanceof DataOutputInterface || f.getSource() instanceof DataInputInterface) {
          // should never happen since we discard fifos when dealing with data ports
          graph.removeFifo(f);
        } else {
          graph.removeFifo(f);
          upperGraph.addFifo(f);
        }
      }
    }
    for (final Parameter p : graph.getParameters()) {
      upperGraph.addParameter(p);
      // copy all the parameter's dependencies that aren't linked to data interfaces
      final List<Dependency> depToRemove = new LinkedList<>();
      for (final var dep : p.getOutgoingDependencies()) {
        if (!(dep.getTarget() instanceof DataInterface)) {
          upperGraph.addDependency(dep);
        } else {
          depToRemove.add(dep);
        }
      }
      p.getOutgoingDependencies().removeAll(depToRemove);
    }

    upperGraph.removeActorAndDependencies(graph);

  }

}
