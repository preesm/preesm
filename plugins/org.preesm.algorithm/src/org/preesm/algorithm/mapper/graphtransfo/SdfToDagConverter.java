/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2019)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
 * Jonathan Piat <jpiat@laas.fr> (2008 - 2011)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2014)
 * Pengcheng Mu <pengcheng.mu@insa-rennes.fr> (2008)
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
package org.preesm.algorithm.mapper.graphtransfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.eclipse.emf.common.util.EList;
import org.preesm.algorithm.mapper.abc.SpecialVertexManager;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.mapper.model.MapperDAGEdge;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.algorithm.mapper.model.MapperVertexFactory;
import org.preesm.algorithm.mapper.model.property.EdgeInit;
import org.preesm.algorithm.mapper.model.property.Timing;
import org.preesm.algorithm.mapper.model.property.VertexInit;
import org.preesm.algorithm.mapper.model.special.TransferVertex;
import org.preesm.algorithm.model.dag.DAGEdge;
import org.preesm.algorithm.model.dag.DAGVertex;
import org.preesm.algorithm.model.iterators.TopologicalDAGIterator;
import org.preesm.algorithm.model.sdf.SDFEdge;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.model.sdf.visitors.DAGTransformation;
import org.preesm.algorithm.model.types.LongEdgePropertyType;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.ScenarioConstants;
import org.preesm.model.scenario.Timings;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.component.Component;
import org.preesm.model.slam.component.Operator;

/**
 * Uses the SDF4J library to convert the input SDF into a DAG before scheduling.
 *
 * @author mpelcat
 */
public class SdfToDagConverter {

  private SdfToDagConverter() {
    // forbid instantiation
  }

  /**
   * Converts a SDF in a DAG and retrieves the interesting properties from the SDF.
   *
   * @author mpelcat
   * @param sdfIn
   *          the sdf in
   * @param architecture
   *          the architecture
   * @param scenario
   *          the scenario
   * @return the mapper DAG
   */
  public static MapperDAG convert(final SDFGraph sdfIn, final Design architecture, final Scenario scenario) {

    PreesmLogger.getLogger().log(Level.INFO, "Converting from SDF to DAG.");

    final SDFGraph sdf = sdfIn.copy();
    SdfToDagConverter.setDataSizeForSDF(sdf, scenario);

    final PiGraph pisdGraph = sdf.getPropertyBean().getValue(PiGraph.class.getCanonicalName());

    // Generates a dag
    final MapperDAG dag = new MapperDAG(pisdGraph);

    // Creates a visitor parameterized with the DAG
    final DAGTransformation<MapperDAG> visitor = new DAGTransformation<>(dag, MapperVertexFactory.getInstance());

    // visits the SDF to generate the DAG
    sdf.accept(visitor);

    // Adds the necessary properties to vertices and edges
    SdfToDagConverter.addInitialProperties(dag, architecture, scenario);

    if (dag.vertexSet().isEmpty()) {
      final String msg = "Can not map a DAG with no vertex.";
      throw new PreesmRuntimeException(msg);
    } else {
      PreesmLogger.getLogger().log(Level.INFO, "Conversion finished.");
      final String msg = "mapping a DAG with " + dag.vertexSet().size() + " vertices and " + dag.edgeSet().size()
          + " edges.";
      PreesmLogger.getLogger().log(Level.INFO, msg);
    }

    return dag;
  }

  /**
   * Retrieves the constraints and adds them to the DAG initial properties.
   *
   * @param dag
   *          the dag
   * @param architecture
   *          the architecture
   * @param scenario
   *          the scenario
   */
  public static void addInitialProperties(final MapperDAG dag, final Design architecture, final Scenario scenario) {
    SdfToDagConverter.addInitialVertexProperties(dag, architecture, scenario);
    SdfToDagConverter.addInitialEdgeProperties(dag);
    SdfToDagConverter.addInitialSpecialVertexProperties(dag, scenario);
    SdfToDagConverter.addInitialConstraintsProperties(dag, scenario);

    // We iterate the dag to add to each vertex its relative constraints (if any)
    for (final DAGVertex dv : dag.vertexSet()) {
      dag.getMappings().dedicate((MapperDAGVertex) dv);
    }

    SdfToDagConverter.addInitialTimingProperties(dag);
  }

  /**
   * Sets the data size property of every edge of the HSDF graph
   *
   * @param graph
   *          the graph
   * @param scenario
   *          the scenario
   */
  private static void setDataSizeForSDF(final SDFGraph graph, final Scenario scenario) {
    for (final SDFEdge edge : graph.edgeSet()) {
      final String type = edge.getDataType().toString();
      final long size = scenario.getSimulationInfo().getDataTypeSizeOrDefault(type);
      edge.setDataSize(new LongEdgePropertyType(size));
    }
  }

  /**
   * Sets timing dependencies between vertices.
   *
   * @param dag
   *          the dag
   */
  private static void addInitialTimingProperties(final MapperDAG dag) {
    // New timing objects are created they are not initialized here
    for (final DAGVertex v : dag.vertexSet()) {
      dag.getTimings().dedicate((MapperDAGVertex) v);
    }
  }

  /**
   * Gets the vertex input buffers size.
   *
   * @param v
   *          the v
   * @return the vertex input buffers size
   */
  private static int getVertexInputBuffersSize(final MapperDAGVertex v) {
    int inputDataSize = 0;

    for (final DAGEdge e : v.incomingEdges()) {
      final MapperDAGEdge me = (MapperDAGEdge) e;
      if (!(me.getSource() instanceof TransferVertex)) {
        inputDataSize += me.getInit().getDataSize();
      }
    }

    return inputDataSize;
  }

  /**
   * Gets the vertex output buffers size.
   *
   * @param v
   *          the v
   * @return the vertex output buffers size
   */
  private static int getVertexOutputBuffersSize(final MapperDAGVertex v) {
    int outputDataSize = 0;

    for (final DAGEdge e : v.outgoingEdges()) {
      final MapperDAGEdge me = (MapperDAGEdge) e;
      if (!(me.getTarget() instanceof TransferVertex)) {
        outputDataSize += me.getInit().getDataSize();
      }
    }

    return outputDataSize;

  }

  /**
   * Retrieves the vertex timings and adds them to the DAG initial properties.
   *
   * @param dag
   *          the dag
   * @param architecture
   *          the architecture
   * @param scenario
   *          the scenario
   */
  private static void addInitialVertexProperties(final MapperDAG dag, final Design architecture,
      final Scenario scenario) {

    /**
     * Importing default timings
     */
    Timings tm = scenario.getTimings();

    // Iterating over dag vertices
    final TopologicalDAGIterator dagiterator = new TopologicalDAGIterator(dag);

    while (dagiterator.hasNext()) {
      final MapperDAGVertex currentVertex = (MapperDAGVertex) dagiterator.next();
      final VertexInit currentVertexInit = currentVertex.getInit();

      // Setting repetition number
      final long nbRepeat = currentVertex.getNbRepeat().longValue();
      currentVertexInit.setNbRepeat(nbRepeat);

      // Special vertices time computation is delayed until edge sizes are initialized
      final boolean special = SpecialVertexManager.isSpecial(currentVertex);
      if (!special) {
        // Default timings are given
        for (final ComponentInstance op : architecture.getComponentInstances()) {
          final AbstractVertex referencePiVertex = currentVertex.getReferencePiVertex();

          if (referencePiVertex instanceof AbstractActor) {
            final AbstractActor actor = ((AbstractActor) referencePiVertex);
            // info is set to the vertexPath of AbstractVertex
            final long originalTiming = tm.evaluateTimingOrDefault(actor, op.getComponent());
            final Timing copyTiming;
            if (originalTiming == ScenarioConstants.DEFAULT_TIMING_TASK.getValue()) {
              copyTiming = new Timing(op.getComponent(), actor);
            } else {
              copyTiming = new Timing(op.getComponent(), actor, originalTiming);
            }
            currentVertexInit.addTiming(copyTiming);
          } else {
            currentVertexInit.addTiming(new Timing(op.getComponent(), null));
          }
        }
      }
    }
  }

  /**
   * Retrieves the vertex timings and adds them to the DAG initial properties.
   *
   * @param dag
   *          the dag
   * @param scenario
   *          the scenario
   */
  private static void addInitialSpecialVertexProperties(final MapperDAG dag, final Scenario scenario) {

    // Iterating over dag vertices
    final TopologicalDAGIterator dagiterator = new TopologicalDAGIterator(dag);

    while (dagiterator.hasNext()) {
      final MapperDAGVertex currentVertex = (MapperDAGVertex) dagiterator.next();

      // Special vertices have timings computed from data copy speed
      if (SpecialVertexManager.isSpecial(currentVertex)) {
        final VertexInit currentVertexInit = currentVertex.getInit();
        final AbstractVertex referencePiVertex = currentVertex.getReferencePiVertex();

        for (final Component opDef : scenario.getTimings().getMemTimings().keySet()) {
          if (referencePiVertex instanceof AbstractActor) {
            final AbstractActor actor = ((AbstractActor) referencePiVertex);
            final long sut = scenario.getTimings().getMemTimings().get(opDef).getSetupTime();
            final double tpu = scenario.getTimings().getMemTimings().get(opDef).getTimePerUnit();
            final Timing timing = new Timing(opDef, actor);

            // Depending on the type of vertex, time is given by the size of output or input buffers
            if (SpecialVertexManager.isFork(currentVertex) || SpecialVertexManager.isJoin(currentVertex)
                || SpecialVertexManager.isEnd(currentVertex)) {
              timing.setTime(sut + (long) (tpu * SdfToDagConverter.getVertexInputBuffersSize(currentVertex)));
            } else if (SpecialVertexManager.isBroadCast(currentVertex) || SpecialVertexManager.isInit(currentVertex)) {
              timing.setTime(sut + (long) (tpu * SdfToDagConverter.getVertexOutputBuffersSize(currentVertex)));
            }

            currentVertexInit.addTiming(timing);
          } else {
            currentVertexInit.addTiming(new Timing(opDef, null));
          }
        }
      }
    }
  }

  /**
   * Retrieves the edge weights and adds them to the DAG initial properties.
   *
   * @param dag
   *          the dag
   */
  private static void addInitialEdgeProperties(final MapperDAG dag) {
    /**
     * Importing data edge weights and multiplying by type size when available
     */
    final Iterator<DAGEdge> edgeiterator = dag.edgeSet().iterator();
    while (edgeiterator.hasNext()) {
      final MapperDAGEdge currentEdge = (MapperDAGEdge) edgeiterator.next();
      final EdgeInit currentEdgeInit = currentEdge.getInit();
      currentEdgeInit.setDataSize(currentEdge.getWeight().longValue());

    }
  }

  /**
   * Retrieves the constraints and adds them to the DAG initial properties. Also imports timings
   *
   * @param dag
   *          the dag
   * @param architecture
   *          the architecture
   * @param scenario
   *          the scenario
   */
  private static void addInitialConstraintsProperties(final MapperDAG dag, final Scenario scenario) {
    final Set<Entry<ComponentInstance, EList<AbstractActor>>> entrySet = scenario.getConstraints().getGroupConstraints()
        .entrySet();

    for (final Entry<ComponentInstance, EList<AbstractActor>> cg : entrySet) {
      // Iterating over vertices in DAG with their SDF ref in the
      // constraint group
      final Set<
          String> sdfVertexIds = cg.getValue().stream().map(AbstractVertex::getVertexPath).collect(Collectors.toSet());

      for (final DAGVertex v : dag.vertexSet()) {
        final MapperDAGVertex mv = (MapperDAGVertex) v;

        final AbstractVertex referencePiVertex = mv.getReferencePiVertex();
        final String lookingFor = mv.getInfo();

        if (sdfVertexIds.contains(lookingFor)) {

          final ComponentInstance currentIOp = cg.getKey();
          if (currentIOp.getComponent() instanceof Operator) {

            if (!mv.getInit().isMapable(currentIOp)) {

              mv.getInit().addOperator(currentIOp);

              // Initializes a default timing that may be erased
              // when timings are imported
              if (referencePiVertex instanceof AbstractActor) {
                final AbstractActor actor = ((AbstractActor) referencePiVertex);
                final Timing newTiming = new Timing(currentIOp.getComponent(), actor);
                mv.getInit().addTiming(newTiming);
              } else {
                mv.getInit().addTiming(new Timing(currentIOp.getComponent(), null));
              }
            }

          }
        }
      }
    }

    /*
     * Special type vertices are first enabled on any core set in scenario to execute them
     */
    final TopologicalDAGIterator it = new TopologicalDAGIterator(dag);
    final List<DAGVertex> vList = new ArrayList<>();
    final List<ComponentInstance> specialOpIds = scenario.getSimulationInfo().getSpecialVertexOperators();

    while (it.hasNext()) {
      final MapperDAGVertex v = (MapperDAGVertex) it.next();
      if (SpecialVertexManager.isSpecial(v)) {
        vList.add(v);
        for (final ComponentInstance o : specialOpIds) {
          v.getInit().addOperator(o);
        }
      }
    }
  }
}
