/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
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
package org.ietr.preesm.mapper.graphtransfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.ietr.dftools.algorithm.model.AbstractEdge;
import org.ietr.dftools.algorithm.model.AbstractEdgePropertyType;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.types.DAGDefaultEdgePropertyType;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.visitors.DAGTransformation;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.architecture.slam.component.Operator;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.core.scenario.ConstraintGroup;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.RelativeConstraintManager;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.mapper.abc.SpecialVertexManager;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.model.MapperEdgeFactory;
import org.ietr.preesm.mapper.model.MapperVertexFactory;
import org.ietr.preesm.mapper.model.property.EdgeInit;
import org.ietr.preesm.mapper.model.property.VertexInit;
import org.ietr.preesm.mapper.model.special.TransferVertex;
import org.ietr.preesm.mapper.tools.TopologicalDAGIterator;

// TODO: Auto-generated Javadoc
/**
 * Uses the SDF4J library to convert the input SDF into a DAG before scheduling.
 *
 * @author mpelcat
 */
public class SdfToDagConverter {

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
  public static MapperDAG convert(final SDFGraph sdfIn, final Design architecture, final PreesmScenario scenario) {

    WorkflowLogger.getLogger().log(Level.INFO, "Converting from SDF to DAG.");

    try {

      if (!sdfIn.validateModel(WorkflowLogger.getLogger())) {
        return null;
      }
    } catch (final SDF4JException e) {
      WorkflowLogger.getLogger().log(Level.SEVERE, e.getMessage());
      return null;
    }
    final SDFGraph sdf = sdfIn.clone();
    // Generates a dag
    final MapperDAG dag = new MapperDAG(new MapperEdgeFactory(), sdf);

    // Creates a visitor parameterized with the DAG
    final DAGTransformation<MapperDAG> visitor = new DAGTransformation<>(dag, MapperVertexFactory.getInstance());

    // visits the SDF to generate the DAG
    try {
      sdf.accept(visitor);
    } catch (final SDF4JException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      WorkflowLogger.getLogger().log(Level.SEVERE, e.getMessage());
    }

    // Adds the necessary properties to vertices and edges
    SdfToDagConverter.addInitialProperties(dag, architecture, scenario);

    if (dag.vertexSet().isEmpty()) {
      WorkflowLogger.getLogger().log(Level.SEVERE, "Can not map a DAG with no vertex.");
    } else {
      WorkflowLogger.getLogger().log(Level.INFO, "Conversion finished.");
      WorkflowLogger.getLogger().log(Level.INFO,
          "mapping a DAG with " + dag.vertexSet().size() + " vertices and " + dag.edgeSet().size() + " edges.");
    }

    scenario.getDAGs2SDFs().put(dag.getName(), sdfIn);

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
   * @return The DAG with initial properties
   */
  public static MapperDAG addInitialProperties(final MapperDAG dag, final Design architecture,
      final PreesmScenario scenario) {

    SdfToDagConverter.addInitialVertexProperties(dag, architecture, scenario);
    SdfToDagConverter.addInitialEdgeProperties(dag, architecture, scenario);
    SdfToDagConverter.addInitialSpecialVertexProperties(dag, architecture, scenario);
    SdfToDagConverter.addInitialConstraintsProperties(dag, architecture, scenario);
    SdfToDagConverter.addInitialRelativeConstraintsProperties(dag, architecture, scenario);
    SdfToDagConverter.addInitialTimingProperties(dag, architecture, scenario);
    return null;
  }

  /**
   * Retrieves the relative constraints and adds them to the DAG initial properties. It consists in sharing between vertices information stored in VertexMapping
   * objects.
   *
   * @param dag
   *          the dag
   * @param architecture
   *          the architecture
   * @param scenario
   *          the scenario
   */
  public static void addInitialRelativeConstraintsProperties(final MapperDAG dag, final Design architecture,
      final PreesmScenario scenario) {

    // Initial relative constraints are stored in the scenario
    final RelativeConstraintManager manager = scenario.getRelativeconstraintManager();
    final Map<Integer, Set<MapperDAGVertex>> relativeConstraints = new LinkedHashMap<>();

    // We iterate the dag to add to each vertex its relative constraints (if any)
    for (final DAGVertex dv : dag.vertexSet()) {
      final String sdfVId = dv.getId();

      // If a group was found, the actor is associated to a group with other actors
      if (manager.hasRelativeConstraint(sdfVId)) {

        // We get the group ID if any was set in the scenario
        final int group = manager.getConstraintOrDefault(sdfVId);

        if (!relativeConstraints.containsKey(group)) {
          relativeConstraints.put(group, new LinkedHashSet<MapperDAGVertex>());
        }
        relativeConstraints.get(group).add((MapperDAGVertex) dv);
      } else {
        // otherwise, the actor has no relative constraint
        dag.getMappings().dedicate((MapperDAGVertex) dv);
      }
    }

    // A DAGMappings object is stored in a DAG. It stores relative constraints
    // between vertices of the DAG.
    // We associate here vertices that will share mapping constraints
    for (final int group : relativeConstraints.keySet()) {
      dag.getMappings().associate(relativeConstraints.get(group));
    }
  }

  /**
   * Sets timing dependencies between vertices.
   *
   * @param dag
   *          the dag
   * @param architecture
   *          the architecture
   * @param scenario
   *          the scenario
   */
  public static void addInitialTimingProperties(final MapperDAG dag, final Design architecture,
      final PreesmScenario scenario) {

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
  public static void addInitialVertexProperties(final MapperDAG dag, final Design architecture,
      final PreesmScenario scenario) {

    /**
     * Importing default timings
     */
    // Iterating over dag vertices
    final TopologicalDAGIterator dagiterator = new TopologicalDAGIterator(dag);

    while (dagiterator.hasNext()) {
      final MapperDAGVertex currentVertex = (MapperDAGVertex) dagiterator.next();
      final VertexInit currentVertexInit = currentVertex.getInit();

      // Setting repetition number
      final int nbRepeat = currentVertex.getNbRepeat().intValue();
      currentVertexInit.setNbRepeat(nbRepeat);

      // The SDF vertex id is used to reference the timings
      final List<Timing> timelist = scenario.getTimingManager().getGraphTimings(currentVertex,
          DesignTools.getOperatorComponentIds(architecture));

      // Iterating over timings for each DAG vertex
      final Iterator<Timing> listiterator = timelist.iterator();

      // Special vertices time computation is delayed until edge sizes are initialized
      final boolean special = SpecialVertexManager.isSpecial(currentVertex);
      if (!special) {
        if (timelist.size() != 0) {
          // If there is no time defined
          while (listiterator.hasNext()) {
            final Timing timing = listiterator.next();

            // Importing timings from scenario
            currentVertexInit.addTiming(timing);
          }
          // If the vertex is not special and has no defined timings
        } else {
          // Default timings are given
          for (final ComponentInstance op : DesignTools.getOperatorInstances(architecture)) {
            final Timing time = new Timing(op.getComponent().getVlnv().getName(), currentVertex.getId(),
                Timing.DEFAULT_TASK_TIME);
            currentVertexInit.addTiming(time);
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
   * @param architecture
   *          the architecture
   * @param scenario
   *          the scenario
   */
  public static void addInitialSpecialVertexProperties(final MapperDAG dag, final Design architecture,
      final PreesmScenario scenario) {

    // Iterating over dag vertices
    final TopologicalDAGIterator dagiterator = new TopologicalDAGIterator(dag);

    while (dagiterator.hasNext()) {
      final MapperDAGVertex currentVertex = (MapperDAGVertex) dagiterator.next();

      // Special vertices have timings computed from data copy speed
      if (SpecialVertexManager.isSpecial(currentVertex)) {
        final VertexInit currentVertexInit = currentVertex.getInit();

        for (final String opDef : scenario.getTimingManager().getMemcpySpeeds().keySet()) {
          final long sut = scenario.getTimingManager().getMemcpySetupTime(opDef);
          final float tpu = scenario.getTimingManager().getMemcpyTimePerUnit(opDef);
          final Timing timing = new Timing(opDef, currentVertex.getId());

          // Depending on the type of vertex, time is given by the size of output or input buffers
          if (SpecialVertexManager.isFork(currentVertex) || SpecialVertexManager.isJoin(currentVertex)
              || SpecialVertexManager.isEnd(currentVertex)) {
            timing.setTime(sut + (long) (tpu * SdfToDagConverter.getVertexInputBuffersSize(currentVertex)));
          } else if (SpecialVertexManager.isBroadCast(currentVertex) || SpecialVertexManager.isInit(currentVertex)) {
            timing.setTime(sut + (long) (tpu * SdfToDagConverter.getVertexOutputBuffersSize(currentVertex)));
          }

          currentVertexInit.addTiming(timing);
        }
      }
    }
  }

  /**
   * Retrieves the edge weights and adds them to the DAG initial properties.
   *
   * @param dag
   *          the dag
   * @param architecture
   *          the architecture
   * @param scenario
   *          the scenario
   */
  @SuppressWarnings("unchecked")
  public static void addInitialEdgeProperties(final MapperDAG dag, final Design architecture,
      final PreesmScenario scenario) {

    /**
     * Importing data edge weights and multiplying by type size when available
     */
    final Iterator<DAGEdge> edgeiterator = dag.edgeSet().iterator();

    while (edgeiterator.hasNext()) {
      final MapperDAGEdge currentEdge = (MapperDAGEdge) edgeiterator.next();
      final EdgeInit currentEdgeInit = currentEdge.getInit();

      int weight = 0;
      if (currentEdge.getAggregate().isEmpty()) {
        final DAGDefaultEdgePropertyType dagDefaultEdgePropertyType = (DAGDefaultEdgePropertyType) currentEdge
            .getWeight();
        weight = dagDefaultEdgePropertyType.intValue();
        final String dataType = currentEdge.getPropertyBean()
            .getValue(SDFEdge.DATA_TYPE, AbstractEdgePropertyType.class).toString();
        final int typeSize = scenario.getSimulationManager().getDataTypeSizeOrDefault(dataType);
        weight *= typeSize;
      } else {

        // Calculating the edge weight for simulation:
        // edge production * number of source repetitions * type size
        for (final AbstractEdge<SDFGraph, SDFAbstractVertex> aggMember : currentEdge.getAggregate()) {
          final SDFEdge sdfAggMember = (SDFEdge) aggMember;
          int prod;
          try {
            prod = sdfAggMember.getProd().intValue();
          } catch (final InvalidExpressionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            prod = 1;
          }
          final int nbRepeat = currentEdge.getSource().getNbRepeat().intValue();
          final int typeSize = scenario.getSimulationManager()
              .getDataTypeSizeOrDefault(sdfAggMember.getDataType().toString());
          weight += prod * nbRepeat * typeSize;
        }
      }
      currentEdgeInit.setDataSize(weight);

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
  public static void addInitialConstraintsProperties(final MapperDAG dag, final Design architecture,
      final PreesmScenario scenario) {
    /**
     * Importing scenario: Only the timings corresponding to allowed mappings are set.
     */

    // Iterating over constraint groups
    final Iterator<ConstraintGroup> cgit = scenario.getConstraintGroupManager().getConstraintGroups().iterator();

    while (cgit.hasNext()) {
      final ConstraintGroup cg = cgit.next();

      // Iterating over vertices in DAG with their SDF ref in the
      // constraint group
      final Set<String> sdfVertexIds = cg.getVertexPaths();

      for (final DAGVertex v : dag.vertexSet()) {
        final MapperDAGVertex mv = (MapperDAGVertex) v;

        final String lookingFor = mv.getInfo();

        if (sdfVertexIds.contains(lookingFor)) {

          for (final String opId : cg.getOperatorIds()) {
            final ComponentInstance currentIOp = DesignTools.getComponentInstance(architecture, opId);
            if (currentIOp.getComponent() instanceof Operator) {

              if (!mv.getInit().isMapable(currentIOp)) {

                mv.getInit().addOperator(currentIOp);

                // Initializes a default timing that may be erased
                // when timings are imported
                final Timing newTiming = new Timing(currentIOp.getComponent().getVlnv().getName(), mv.getName());
                mv.getInit().addTiming(newTiming);
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
    final Set<String> specialOpIds = scenario.getSimulationManager().getSpecialVertexOperatorIds();

    while (it.hasNext()) {
      final MapperDAGVertex v = (MapperDAGVertex) it.next();
      if (SpecialVertexManager.isSpecial(v)) {
        vList.add(v);
        for (final String id : specialOpIds) {
          final ComponentInstance o = DesignTools.getComponentInstance(architecture, id);
          v.getInit().addOperator(o);
        }
      }
    }
  }
}
