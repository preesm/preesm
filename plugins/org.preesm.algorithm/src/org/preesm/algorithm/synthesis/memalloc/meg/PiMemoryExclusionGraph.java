/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2019) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Julien Hascoet [jhascoet@kalray.eu] (2017)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2012 - 2017)
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
package org.preesm.algorithm.synthesis.memalloc.meg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.xbase.lib.Pair;
import org.jgrapht.Graphs;
import org.jgrapht.generate.ComplementGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memory.exclusiongraph.MemoryExclusionGraph;
import org.preesm.algorithm.memory.exclusiongraph.MemoryExclusionVertex;
import org.preesm.algorithm.model.PropertyBean;
import org.preesm.algorithm.model.PropertyFactory;
import org.preesm.algorithm.model.PropertySource;
import org.preesm.algorithm.model.dag.DAGEdge;
import org.preesm.algorithm.model.dag.DAGVertex;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.synthesis.memalloc.script.PiRange;
import org.preesm.algorithm.synthesis.schedule.ScheduleOrderManager;
import org.preesm.commons.CloneableProperty;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.EndActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.util.topology.PiSDFTopologyHelper;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;

/**
 * This class is used to handle the Memory Exclusion Graph
 *
 * <p>
 * This Graph, created by analyzing a DAG, is composed of:
 * <ul>
 * <li>Vertices which represent the memory needed to transfer data from a Task to another.</li>
 * <li>Undirected edges that signify that two memory transfers might be concurrent, and thus can not share the same
 * resource.</li>
 * </ul>
 * </p>
 *
 * @author kdesnos
 *
 */
public class PiMemoryExclusionGraph extends SimpleGraph<PiMemoryExclusionVertex, DefaultEdge>
    implements PropertySource, CloneableProperty<PiMemoryExclusionGraph> {

  public static final String FIFO_HEAD_PREFIX = "FIFO_Head_";

  private static final long serialVersionUID = 6491894138235944108L;

  /**
   * Property to store a {@link Map} corresponding to the allocation of the {@link DAGEdge}.
   */
  public static final String DAG_EDGE_ALLOCATION = "dag_edges_allocation";

  public static final String DAG_FIFO_ALLOCATION = "fifo_allocation";
  public static final String SOURCE_DAG          = "source_dag";

  /**
   * Property to store the merged memory objects resulting from the script processing. The stored object is a:<br>
   * <code>
   * Map&lt;MemoryExclusionVertex,Set&ltMemoryExclusionVertex&gt;&gt;
   * </code><br>
   * <br>
   * This {@link Map} associates of {@link PiMemoryExclusionVertex} that contain merged {@link PiMemoryExclusionVertex}
   * to the {@link Set} of contained {@link PiMemoryExclusionVertex}
   *
   */
  public static final String HOST_MEMORY_OBJECT_PROPERTY = "host_memory_objects";

  /**
   * Property to store an {@link Integer} corresponding to the amount of memory allocated.
   */
  public static final String ALLOCATED_MEMORY_SIZE = "allocated_memory_size";

  /**
   * Each DAGVertex is associated to a list of PiMemoryExclusionVertex that have a precedence relationship with this
   * DAGVertex. All successors of this DAGVertex will NOT have exclusion with PiMemoryExclusionVertex in this list. This
   * list is built along the build of the MemEx, and used for subsequent updates. We use the name of the DAGVertex as a
   * key.. Because using the DAG itself seems to be impossible.<br>
   * If there are {@link PiMemoryExclusionVertex} corresponding to the working memory of {@link DAGVertex}, they will be
   * added to the predecessor list of this vertex.
   */
  private final transient Map<String, Set<PiMemoryExclusionVertex>> verticesPredecessors = new LinkedHashMap<>();

  /**
   * {@link PiMemoryExclusionVertex} of the {@link MemoryExclusionGraph} in the scheduling order retrieved in the
   * {@link #updateWithSchedule(DirectedAcyclicGraph)} method.
   */
  private transient List<PiMemoryExclusionVertex> memExVerticesInSchedulingOrder = null;

  /**
   * The {@link PropertyBean} that stores the properties of the {@link MemoryExclusionGraph}.
   */
  private final transient PropertyBean properties = new PropertyBean();

  private final transient Scenario scenario;

  /**
   * Default constructor.
   */
  public PiMemoryExclusionGraph(final Scenario scenario) {
    super(DefaultEdge.class);
    this.scenario = scenario;
  }

  public Scenario getScenario() {
    return scenario;
  }

  /**
   * This method add the node corresponding to the passed edge to the ExclusionGraph. If the source or targeted vertex
   * isn't a task vertex, nothing is added. (should not happen)
   *
   * @param edge
   *          The memory transfer to add.
   * @return the exclusion graph node created (or null)
   */
  private PiMemoryExclusionVertex addNode(final Fifo edge) {
    // If the target and source vertices are tasks, add a node corresponding to the memory transfer to the exclusion
    // graph. Else, nothing
    final PiMemoryExclusionVertex resNode;

    // As the non-task vertices are removed at the beginning of the build function. This if statement could probably be
    // removed. (I keep it just in case)

    final PiMemoryExclusionVertex newNode = new PiMemoryExclusionVertex(edge, this.scenario);

    final boolean added = addVertex(newNode);

    // If false, this means that an equal node is already in the MemEx... somehow...
    if (!added) {
      // This may come from several edges belonging to an implodeSet
      PreesmLogger.getLogger().log(Level.WARNING, () -> "Vertex not added : " + newNode.toString());
      resNode = null;
    } else {
      resNode = newNode;
    }
    return resNode;
  }

  /**
   * Build the memory objects corresponding to the fifos of the input {@link DirectedAcyclicGraph}. The new memory
   * objects are added to the {@link MemoryExclusionGraph}. This method creates 1 or 2 {@link PiMemoryExclusionVertex}
   * for each pair of init/end {@link DAGVertex} encountered in the graph.
   *
   * @param dag
   *          the dag containing the fifos.
   */
  private void buildDelaysMemoryObjects(final PiGraph dag, PiSDFTopologyHelper helper) {
    // Scan the dag vertices
    for (final AbstractActor vertex : dag.getAllActors()) {

      if (vertex instanceof InitActor) {

        final InitActor dagInitVertex = (InitActor) vertex;

        final AbstractActor endReference = dagInitVertex.getEndReference();
        // @farresti:
        // It may happens that there are no end vertex associated with an init
        // If a FIFO is ended using getter actors for instance
        // In that case, should we leave ?
        if (!(endReference instanceof EndActor)) {
          continue;
        }
        final EndActor dagEndVertex = (EndActor) endReference;

        final DataPort dataPort = dagInitVertex.getDataPort();
        // Create the Head Memory Object. Get the typeSize
        final Fifo outgoingEdge = dataPort.getFifo();
        final String typeName = outgoingEdge.getType();
        final long typeSize = this.getScenario().getSimulationInfo().getDataTypeSizeOrDefault(typeName);
        final long fifoSize = dataPort.getPortRateExpression().evaluate();
        final PiMemoryExclusionVertex headMemoryNode = new PiMemoryExclusionVertex(
            PiMemoryExclusionGraph.FIFO_HEAD_PREFIX + dagEndVertex.getName(), dagInitVertex.getName(),
            fifoSize * typeSize, this.scenario);
        headMemoryNode.setPropertyValue(PiMemoryExclusionVertex.TYPE_SIZE, typeSize);
        // Add the head node to the MEG
        addVertex(headMemoryNode);

        // Compute the list of all edges between init and end
        // Also compute the list of actors
        final List<Fifo> endPredecessors = helper.getPredecessorEdgesOf(dagEndVertex);
        final List<Fifo> initSuccessors = helper.getSuccessorEdgesOf(vertex);
        final Set<Fifo> between = (new LinkedHashSet<>(initSuccessors));
        between.retainAll(endPredecessors);

        // Add exclusions between the head node and ALL MemoryObjects
        // that do not correspond to edges in the between list or to
        // the working memory of an actor in the betweenVert list.
        for (final PiMemoryExclusionVertex memObject : vertexSet()) {
          final Fifo correspondingEdge = memObject.getEdge();
          // For Edges
          if (correspondingEdge != null) {
            if (!between.contains(correspondingEdge)) {
              this.addEdge(headMemoryNode, memObject);
            }
          } else if (memObject != headMemoryNode) {
            this.addEdge(headMemoryNode, memObject);
          }
        }

        // No need to add exclusion between the head MObj and the
        // outgoing edge of the init or the incoming edge of the end.
        // (unless of course the init and the end have an empty
        // "between" list, but this will be handled by the previous
        // loop.)
      }
    }
  }

  /**
   * Method to build the graph based on a DirectedAcyclicGraph.
   *
   * @param dag
   *          This DirectedAcyclicGraph is analyzed to create the nodes and edges of the MemoryExclusionGraph. The DAG
   *          used must be the output of a scheduling process. This property ensures that all preceding nodes of a
   *          "merge" node are treated before treating the "merge" node. The DAG will be modified by this function.
   */
  public void buildGraph(final PiGraph dag) {

    final PiSDFTopologyHelper helper = new PiSDFTopologyHelper(dag);

    buildFifosMemoryObjects(dag, helper);

    // Add the memory objects corresponding to the delays.
    buildDelaysMemoryObjects(dag, helper);

    // Save the dag in the properties
    this.setPropertyValue(PiMemoryExclusionGraph.SOURCE_DAG, dag);
  }

  private void buildFifosMemoryObjects(final PiGraph dag, PiSDFTopologyHelper helper) {
    final Map<AbstractActor, Pair<Set<PiMemoryExclusionVertex>,
        Set<PiMemoryExclusionVertex>>> associatedMemExVertices = new LinkedHashMap<>();

    final List<AbstractActor> sort = helper.getTopologicallySortedActors();

    for (final AbstractActor v : sort) {
      associatedMemExVertices.put(v, Pair.of(new LinkedHashSet<PiMemoryExclusionVertex>() /* predecessor */,
          new LinkedHashSet<PiMemoryExclusionVertex>()) /* incoming */);
    }

    // Scan of the DAG in order to:
    // - create Exclusion Graph nodes.
    // - add exclusion between consecutive Memory Transfer
    for (final AbstractActor vertexDAG : sort) {
      // For each vertex of the DAG

      // Processing is done in the following order:
      // 1. Fork/Join/Broadcast/RoundBuffer specific processing
      // 2. Working Memory specific Processing
      // 3. Outgoing Edges processing

      // 1. Fork/Join/Broadcast/Roundbuffer specific processing
      // Not usable yet ! Does not work because output edges are not
      // allocated
      // in the same order.. so overwrite are possible : inout needed here
      // !

      // Implicit Else if: broadcast/fork/join/roundBuffer have no working
      // mem
      // 2. Working Memory specific Processing -> REMOVED

      // For each outgoing edge
      final List<
          Fifo> outFifos = vertexDAG.getDataOutputPorts().stream().map(DataPort::getFifo).collect(Collectors.toList());

      for (final Fifo edge : outFifos) {
        // Add the node to the Exclusion Graph
        final PiMemoryExclusionVertex newNode = addNode(edge);
        if (newNode != null) {
          // Add Exclusions with all non-predecessors of the current vertex
          final Set<PiMemoryExclusionVertex> inclusions = associatedMemExVertices.get(vertexDAG).getKey();
          final Set<PiMemoryExclusionVertex> exclusions = new LinkedHashSet<>(vertexSet());
          exclusions.remove(newNode);
          exclusions.removeAll(inclusions);
          for (final PiMemoryExclusionVertex exclusion : exclusions) {
            this.addEdge(newNode, exclusion);
          }

          final AbstractActor target = edge.getTargetPort().getContainingActor();
          // Add newNode to the incoming list of the consumer of this edge
          associatedMemExVertices.get(target).getValue().add(newNode);

          // Update the predecessor list of the consumer of this edge
          Set<PiMemoryExclusionVertex> predecessor;
          predecessor = associatedMemExVertices.get(target).getKey();
          predecessor.addAll(inclusions);
          predecessor.addAll(associatedMemExVertices.get(vertexDAG).getValue());
        } else {
          // If the node was not added. Should never happen
          throw new PreesmRuntimeException(
              "The exclusion graph vertex corresponding to edge " + edge.toString() + " was not added to the graph.");
        }
      }
      // Save predecessor list, and include incoming to it.
      associatedMemExVertices.get(vertexDAG).getKey().addAll(associatedMemExVertices.get(vertexDAG).getValue());
      this.verticesPredecessors.put(vertexDAG.getName(), associatedMemExVertices.get(vertexDAG).getKey());
    }
  }

  /**
   * Does a shallow copy (copy of the attribute reference) except for the list of vertices and edges where a deep copy
   * of the List is duplicated, but not its content.
   *
   * @return the object
   * @override
   */
  @Override
  public PiMemoryExclusionGraph copy() {
    return (PiMemoryExclusionGraph) super.clone();
  }

  /**
   * This method puts the {@link MemoryExclusionGraph} back to its state before any memory allocation was performed.
   * Tasks performed are:
   * <ul>
   * <li>Put back the host memory objects that were replaced by their content during memory allocation.</li>
   * <li>Restore the MEG to its original state before allocation</li>
   * </ul>
   */
  public void deallocate() {
    final Map<PiMemoryExclusionVertex, Set<PiMemoryExclusionVertex>> hostVertices = getPropertyBean()
        .getValue(PiMemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);
    // Scan host vertices
    if (hostVertices != null) {
      for (final Entry<PiMemoryExclusionVertex, Set<PiMemoryExclusionVertex>> entry : hostVertices.entrySet()) {
        final PiMemoryExclusionVertex hostVertex = entry.getKey();
        final Set<PiMemoryExclusionVertex> value = entry.getValue();
        // Put the host back to its original size (if it was changed, i.e. if it was allocated)
        final Object hostSizeObj = hostVertex.getPropertyBean().getValue(PiMemoryExclusionVertex.HOST_SIZE);
        if (hostSizeObj != null) {
          final long hostSize = (long) hostSizeObj;
          hostVertex.setWeight(hostSize);
          hostVertex.getPropertyBean().removeProperty(PiMemoryExclusionVertex.HOST_SIZE);

          // Scan merged vertices
          for (final PiMemoryExclusionVertex mergedVertex : value) {
            // If the merged vertex was in the graph (i.e. it was already allocated)
            if (containsVertex(mergedVertex)) {
              // Add exclusions between host and adjacent vertex of the merged vertex
              for (final PiMemoryExclusionVertex adjacentVertex : getAdjacentVertexOf(mergedVertex)) {
                this.addEdge(hostVertex, adjacentVertex);
              }
              // Remove it from the MEG
              removeVertex(mergedVertex);

              // If the merged vertex is not split
              if (mergedVertex.getWeight() != 0) {
                // Put it back to its real weight
                final long emptySpace = mergedVertex.getPropertyBean()
                    .getValue(PiMemoryExclusionVertex.EMPTY_SPACE_BEFORE);
                mergedVertex.setWeight(mergedVertex.getWeight() - emptySpace);
              } else {
                // The vertex was divided. Remove all fake mobjects
                final List<PiMemoryExclusionVertex> fakeMobjects = mergedVertex.getPropertyBean()
                    .getValue(PiMemoryExclusionVertex.FAKE_MOBJECT);
                for (final PiMemoryExclusionVertex fakeMobj : fakeMobjects) {
                  removeVertex(fakeMobj);
                }
                fakeMobjects.clear();
              }
            }
          }
        }
      }
    }
  }

  /**
   * This methods returns a clone of the calling {@link MemoryExclusionGraph} where attributes and properties are copied
   * as follows:
   * <ul>
   * <li>Deep copy (object is duplicated):</li>
   * <ul>
   * <li>List of Vertices (List of PiMemoryExclusionVertex)</li>
   * <li>MemoryExclusionVertex</li>
   * <li>Property of PiMemoryExclusionVertex</li>
   * <li>List of exclusions</li>
   * <li>{@link #adjacentVerticesBackup}</li>
   * <li>{@link #properties propertyBean} (but not all properties are deeply copied)</li>
   * <li>{@link #HOST_MEMORY_OBJECT_PROPERTY} property</li>
   * <li>{@link #dagVerticesInSchedulingOrder}</li>
   * </ul>
   * <li>Shallow copy (reference to the object is copied):</li>
   * <ul>
   * <li>{@link #SOURCE_DAG} property</li>
   * <li>{@link #verticesPredecessors} list</li>
   * </ul>
   * </ul>
   * .
   *
   * @return the memory exclusion graph
   */
  public PiMemoryExclusionGraph deepClone() {
    // Shallow copy with clone. Handle shallow copy of attributes and deep copy of lists of vertices and exclusion
    final PiMemoryExclusionGraph result = new PiMemoryExclusionGraph(this.getScenario());

    // Deep copy of all MObj (including merged ones). Keep a record of the old and new mObj
    final Map<PiMemoryExclusionVertex, PiMemoryExclusionVertex> mObjMap = new LinkedHashMap<>();
    for (final PiMemoryExclusionVertex vertex : getTotalSetOfVertices()) {
      final PiMemoryExclusionVertex vertexClone = vertex.getClone();
      mObjMap.put(vertex, vertexClone);
    }

    // Add mObjs to the graph (except merged ones)
    for (final PiMemoryExclusionVertex vertex : vertexSet()) {
      result.addVertex(mObjMap.get(vertex));
    }

    // Copy exclusions
    for (final DefaultEdge edge : edgeSet()) {
      result.addEdge(getEdgeSource(edge), getEdgeTarget(edge));
    }

    // Deep copy of mObj properties
    deepCloneVerticesProperties(mObjMap);

    // Deep copy of propertyBean
    deepCloneMegProperties(result, mObjMap);

    // Deep copy of memExVerticesInSchedulingOrder
    if (this.memExVerticesInSchedulingOrder != null) {
      result.memExVerticesInSchedulingOrder = new ArrayList<>(this.memExVerticesInSchedulingOrder);
      result.memExVerticesInSchedulingOrder.replaceAll(mObjMap::get);
    }

    return result;
  }

  /**
   * This method clones the {@link PropertyBean} of the current {@link MemoryExclusionGraph} into the clone
   * {@link MemoryExclusionGraph} passed as a parameter. The mObjMap parameter is used to make properties of the clone
   * reference only cloned {@link PiMemoryExclusionVertex} (and not {@link PiMemoryExclusionVertex} of the original
   * {@link MemoryExclusionGraph}).
   *
   * @param result
   *          The clone {@link MemoryExclusionGraph} created in the {@link #deepClone()} method.
   * @param mObjMap
   *          <code>Map&lt;MemoryExclusionVertex,MemoryExclusionVertex&gt;</code> associating original
   *          {@link PiMemoryExclusionVertex} of the current {@link MemoryExclusionGraph} to their clone.
   */
  private void deepCloneMegProperties(final PiMemoryExclusionGraph result,
      final Map<PiMemoryExclusionVertex, PiMemoryExclusionVertex> mObjMap) {

    // DAG_FIFO_ALLOCATION
    final Map<PiMemoryExclusionVertex,
        Long> dagFifoAlloc = getPropertyBean().getValue(PiMemoryExclusionGraph.DAG_FIFO_ALLOCATION);
    if (dagFifoAlloc != null) {
      final Map<PiMemoryExclusionVertex, Long> dagFifoAllocCopy = new LinkedHashMap<>();
      for (final Entry<PiMemoryExclusionVertex, Long> fifoAlloc : dagFifoAlloc.entrySet()) {
        dagFifoAllocCopy.put(mObjMap.get(fifoAlloc.getKey()), fifoAlloc.getValue());
      }
      result.setPropertyValue(PiMemoryExclusionGraph.DAG_FIFO_ALLOCATION, dagFifoAllocCopy);
    }

    // SOURCE_DAG
    if (getPropertyBean().getValue(PiMemoryExclusionGraph.SOURCE_DAG) != null) {
      result.setPropertyValue(PiMemoryExclusionGraph.SOURCE_DAG,
          getPropertyBean().getValue(PiMemoryExclusionGraph.SOURCE_DAG));
    }

    // ALLOCATED_MEMORY_SIZE
    if (getPropertyBean().getValue(PiMemoryExclusionGraph.ALLOCATED_MEMORY_SIZE) != null) {
      result.setPropertyValue(PiMemoryExclusionGraph.ALLOCATED_MEMORY_SIZE,
          getPropertyBean().getValue(PiMemoryExclusionGraph.ALLOCATED_MEMORY_SIZE));
    }

    // HOST_MEMORY_OBJECT_PROPERTY property
    final Map<PiMemoryExclusionVertex, Set<PiMemoryExclusionVertex>> hostMemoryObject = getPropertyBean()
        .getValue(PiMemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);
    if (hostMemoryObject != null) {
      final Map<PiMemoryExclusionVertex, Set<PiMemoryExclusionVertex>> hostMemoryObjectCopy = new LinkedHashMap<>();
      for (final Entry<PiMemoryExclusionVertex, Set<PiMemoryExclusionVertex>> host : hostMemoryObject.entrySet()) {
        final Set<PiMemoryExclusionVertex> hostedCopy = new LinkedHashSet<>();
        for (final PiMemoryExclusionVertex hosted : host.getValue()) {
          hostedCopy.add(mObjMap.get(hosted));
        }
        hostMemoryObjectCopy.put(mObjMap.get(host.getKey()), hostedCopy);
      }
      result.setPropertyValue(PiMemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY, hostMemoryObjectCopy);
    }
  }

  /**
   * This method create a deep copy of the properties of the {@link PiMemoryExclusionVertex} cloned in the
   * {@link #deepClone()} method. <br>
   * Keys of the mObjMap parameter are the original {@link PiMemoryExclusionVertex} whose properties are to be cloned,
   * and values of the map are the corresponding {@link PiMemoryExclusionVertex} clones. Cloned properties are:
   * <ul>
   * <li>{@link PiMemoryExclusionVertex#MEMORY_OFFSET_PROPERTY}</li>
   * <li>{@link PiMemoryExclusionVertex#REAL_TOKEN_RANGE_PROPERTY}</li>
   * <li>{@link PiMemoryExclusionVertex#FAKE_MOBJECT}</li>
   * <li>{@link PiMemoryExclusionVertex#ADJACENT_VERTICES_BACKUP}</li>
   * <li>{@link PiMemoryExclusionVertex#EMPTY_SPACE_BEFORE}</li>
   * <li>{@link PiMemoryExclusionVertex#HOST_SIZE}</li>
   * <li>{@link PiMemoryExclusionVertex#DIVIDED_PARTS_HOSTS}</li>
   * <li>{@link PiMemoryExclusionVertex#TYPE_SIZE}</li>
   * <li>{@link PiMemoryExclusionVertex#INTER_BUFFER_SPACES}</li>
   * </ul>
   * All cloned properties referencing {@link PiMemoryExclusionVertex} are referencing {@link PiMemoryExclusionVertex}
   * of the mObjMap values (i.e. the cloned {@link PiMemoryExclusionVertex}).
   *
   * @param mObjMap
   *          <code>Map&lt;MemoryExclusionVertex,MemoryExclusionVertex&gt;</code> associating original
   *          {@link PiMemoryExclusionVertex} of the current {@link MemoryExclusionGraph} to their clone.
   */
  private void deepCloneVerticesProperties(final Map<PiMemoryExclusionVertex, PiMemoryExclusionVertex> mObjMap) {
    for (final Entry<PiMemoryExclusionVertex, PiMemoryExclusionVertex> entry : mObjMap.entrySet()) {
      final PiMemoryExclusionVertex vertex = entry.getKey();
      final PiMemoryExclusionVertex vertexClone = entry.getValue();

      // MEMORY_OFFSET_PROPERTY
      final Long memOffset = vertex.getPropertyBean().getValue(PiMemoryExclusionVertex.MEMORY_OFFSET_PROPERTY);
      if (memOffset != null) {
        vertexClone.setPropertyValue(PiMemoryExclusionVertex.MEMORY_OFFSET_PROPERTY, memOffset);
      }

      // REAL_TOKEN_RANGE_PROPERTY
      final List<Pair<PiMemoryExclusionVertex, Pair<PiRange, PiRange>>> realTokenRange = vertex.getPropertyBean()
          .getValue(PiMemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY);
      if (realTokenRange != null) {
        final List<Pair<PiMemoryExclusionVertex, Pair<PiRange, PiRange>>> realTokenRangeCopy = new ArrayList<>();
        for (final Pair<PiMemoryExclusionVertex, Pair<PiRange, PiRange>> pair : realTokenRange) {
          realTokenRangeCopy.add(Pair.of(mObjMap.get(pair.getKey()),
              Pair.of(pair.getValue().getKey().copy(), pair.getValue().getValue().copy())));
        }
        vertexClone.setPropertyValue(PiMemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY, realTokenRangeCopy);
      }

      // FAKE_MOBJECT
      final List<PiMemoryExclusionVertex> fakeMobject = vertex.getPropertyBean()
          .getValue(PiMemoryExclusionVertex.FAKE_MOBJECT);
      if (fakeMobject != null) {
        final List<PiMemoryExclusionVertex> fakeMobjectCopy = new ArrayList<>();
        for (final PiMemoryExclusionVertex fakeMobj : fakeMobject) {
          fakeMobjectCopy.add(mObjMap.get(fakeMobj));
        }
        vertexClone.setPropertyValue(PiMemoryExclusionVertex.FAKE_MOBJECT, fakeMobjectCopy);
      }

      // ADJACENT_VERTICES_BACKUP
      final List<PiMemoryExclusionVertex> localAdjacentVerticesBackup = vertex.getPropertyBean()
          .getValue(PiMemoryExclusionVertex.ADJACENT_VERTICES_BACKUP);
      if (localAdjacentVerticesBackup != null) {
        final List<PiMemoryExclusionVertex> adjacentVerticesBackupCopy = new ArrayList<>();
        for (final PiMemoryExclusionVertex adjacentVertex : localAdjacentVerticesBackup) {
          adjacentVerticesBackupCopy.add(mObjMap.get(adjacentVertex));
        }
        vertexClone.setPropertyValue(PiMemoryExclusionVertex.ADJACENT_VERTICES_BACKUP, adjacentVerticesBackupCopy);
      }

      // EMPTY_SPACE_BEFORE
      final Integer emptySpaceBefore = vertex.getPropertyBean().getValue(PiMemoryExclusionVertex.EMPTY_SPACE_BEFORE);
      if (emptySpaceBefore != null) {
        vertexClone.setPropertyValue(PiMemoryExclusionVertex.EMPTY_SPACE_BEFORE, emptySpaceBefore);
      }

      // HOST_SIZE
      final Integer hostSize = vertex.getPropertyBean().getValue(PiMemoryExclusionVertex.HOST_SIZE);
      if (hostSize != null) {
        vertexClone.setPropertyValue(PiMemoryExclusionVertex.HOST_SIZE, hostSize);
      }

      // DIVIDED_PARTS_HOSTS
      final List<PiMemoryExclusionVertex> dividedPartHosts = vertex.getPropertyBean()
          .getValue(PiMemoryExclusionVertex.DIVIDED_PARTS_HOSTS);
      if (dividedPartHosts != null) {
        final List<PiMemoryExclusionVertex> dividedPartHostCopy = new ArrayList<>();
        for (final PiMemoryExclusionVertex host : dividedPartHosts) {
          dividedPartHostCopy.add(mObjMap.get(host));
        }
        vertexClone.setPropertyValue(PiMemoryExclusionVertex.DIVIDED_PARTS_HOSTS, dividedPartHostCopy);
      }

      final Object typeSizeValue = vertex.getPropertyBean().getValue(PiMemoryExclusionVertex.TYPE_SIZE);
      // TYPE_SIZE
      if (typeSizeValue != null) {
        final long typeSize = (long) typeSizeValue;
        vertexClone.setPropertyValue(PiMemoryExclusionVertex.TYPE_SIZE, typeSize);
      }

      // INTER_BUFFER_SPACES
      final List<
          Integer> interBufferSpaces = vertex.getPropertyBean().getValue(PiMemoryExclusionVertex.INTER_BUFFER_SPACES);
      if (interBufferSpaces != null) {
        vertexClone.setPropertyValue(PiMemoryExclusionVertex.INTER_BUFFER_SPACES, new ArrayList<>(interBufferSpaces));
      }
    }
  }

  /**
   * {@link #deepRemoveVertex(MemoryExclusionVertex)} for a {@link Collection} of {@link PiMemoryExclusionVertex}.
   *
   * @param vertices
   *          the {@link Collection} of {@link PiMemoryExclusionVertex} removed from the graph.
   */
  public void deepRemoveAllVertices(final Collection<? extends PiMemoryExclusionVertex> vertices) {

    // List of vertices
    // List of edges
    // adjacentVerticesBackup
    removeAllVertices(vertices);

    // memExVerticesInSchedulingOrder
    if (this.memExVerticesInSchedulingOrder != null) {
      this.memExVerticesInSchedulingOrder.removeAll(vertices);
    }

    // HOST_MEMORY_OBJECT_PROPERTY property
    final Map<PiMemoryExclusionVertex, Set<PiMemoryExclusionVertex>> hosts = getPropertyBean()
        .getValue(PiMemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);

    if (hosts != null) {
      // Changes to the KeySet are applied to the map
      hosts.keySet().removeAll(vertices);
      hosts.forEach((host, hosted) -> {
        if (hosted.removeAll(vertices)) {
          // List of hosted mObjects should never be impacted by the remove operation. (in the context of Distributor
          // call) indeed, deeply removed vertices correspond to vertices belonging to other memory banks, hence if a
          // hosted Mobj belong to a list, and this Mobj is to be removed, then the host Mobj for this hosted Mobj is
          // mandatorily associated to another bank, and has been removed in the previous lines of the code.
          throw new PreesmRuntimeException("A hosted Memory Object was removed (but its host was not).");
        }
      });

      // ADJACENT_VERTICES_BACKUP property vertices
      final Set<PiMemoryExclusionVertex> verticesWithAdjacentVerticesBackup = new LinkedHashSet<>();
      verticesWithAdjacentVerticesBackup.addAll(hosts.keySet());
      for (final Set<PiMemoryExclusionVertex> hosted : hosts.values()) {
        verticesWithAdjacentVerticesBackup.addAll(hosted);
      }
      for (final PiMemoryExclusionVertex vertex : verticesWithAdjacentVerticesBackup) {
        final List<PiMemoryExclusionVertex> vertexAdjacentVerticesBackup = vertex.getPropertyBean()
            .getValue(PiMemoryExclusionVertex.ADJACENT_VERTICES_BACKUP);
        vertexAdjacentVerticesBackup.removeAll(vertices);
      }
    }
  }

  /**
   * This method is used to access all the neighbors of a given vertex.
   *
   * @param vertex
   *          the vertex whose neighbors are retrieved
   * @return a set containing the neighbor vertices
   */
  public Set<PiMemoryExclusionVertex> getAdjacentVertexOf(final PiMemoryExclusionVertex vertex) {
    return Graphs.neighborSetOf(this, vertex);
  }

  /**
   * Get the complementary graph of the exclusion graph. The complementary graph possess the same nodes but the
   * complementary edges. i.e. if there is an edge between vi and vj in the exclusion graph, there will be no edge in
   * the complementary.
   *
   * @return the complementary
   */
  public PiMemoryExclusionGraph getComplementary() {
    final PiMemoryExclusionGraph target = new PiMemoryExclusionGraph(this.getScenario());
    final ComplementGraphGenerator<PiMemoryExclusionVertex,
        DefaultEdge> complementGraphGenerator = new ComplementGraphGenerator<>(this);
    complementGraphGenerator.generateGraph(target);
    return target;
  }

  /**
   * Gets the mem ex vertices in scheduling order.
   *
   * @return a copy of the {@link #memExVerticesInSchedulingOrder} or <code>null</code> if the
   *         {@link MemoryExclusionGraph MemEx} was not {@link #updateWithSchedule(DirectedAcyclicGraph) updated with a
   *         schedule}
   */
  public List<PiMemoryExclusionVertex> getMemExVerticesInSchedulingOrder() {
    if (this.memExVerticesInSchedulingOrder == null) {
      return Collections.emptyList();
    } else {
      return new ArrayList<>(this.memExVerticesInSchedulingOrder);
    }
  }

  @Override
  public PropertyFactory getFactoryForProperty(final String propertyName) {
    return null;
  }

  @Override
  public PropertyBean getPropertyBean() {
    return this.properties;
  }

  /**
   * Returns the total number of {@link PiMemoryExclusionVertex} in the {@link MemoryExclusionGraph} including these
   * merged as a result of a buffer merging operation, and stored in the {@value #HOST_MEMORY_OBJECT_PROPERTY} property.
   *
   * @return the total number of vertices
   */
  public int getTotalNumberOfVertices() {
    return getTotalSetOfVertices().size();
  }

  /**
   * Returns the {@link Set} of all {@link PiMemoryExclusionVertex} in the {@link MemoryExclusionGraph}, including these
   * merged as a result of a buffer merging operation, and stored in the {@value #HOST_MEMORY_OBJECT_PROPERTY} property.
   *
   * @return the total set of vertices
   */
  public Set<PiMemoryExclusionVertex> getTotalSetOfVertices() {
    final Set<PiMemoryExclusionVertex> allVertices = new LinkedHashSet<>(vertexSet());
    final Map<PiMemoryExclusionVertex, Set<PiMemoryExclusionVertex>> hosts = getPropertyBean()
        .getValue(PiMemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);
    if (hosts != null) {
      hosts.forEach((host, hosted) -> allVertices.addAll(hosted));
    }

    return allVertices;
  }

  /**
   * This method returns the local copy of the {@link PiMemoryExclusionVertex} that is
   * {@link PiMemoryExclusionVertex}{@link #equals(Object)} to the {@link PiMemoryExclusionVertex} passed as a
   * parameter.
   *
   * @param memObject
   *          a {@link PiMemoryExclusionVertex} searched in the {@link MemoryExclusionGraph}
   * @return an equal {@link PiMemoryExclusionVertex} from the {@link #vertexSet()}, null if there is no such vertex.
   */
  public PiMemoryExclusionVertex getVertex(final PiMemoryExclusionVertex memObject) {
    final Iterator<PiMemoryExclusionVertex> iter = vertexSet().iterator();
    while (iter.hasNext()) {
      final PiMemoryExclusionVertex vertex = iter.next();
      if (vertex.equals(memObject)) {
        return vertex;
      }
    }
    return null;
  }

  /**
   * Method used to update the exclusions between memory objects corresponding to Delay heads and other memory objects
   * of the {@link MemoryExclusionGraph}. Exclusions will be removed from the exclusion graph, but no exclusions will be
   * added.
   */
  private void updateDelayMemObjectWithSchedule(final PiGraph inputDAG, final Schedule schedule, final Mapping mapping,
      final ScheduleOrderManager orderMngr) {

    final List<AbstractActor> orderedActors = orderMngr.buildScheduleAndTopologicalOrderedList();

    // Get vertices in scheduling order and remove send/receive vertices from the dag. Also identify the init vertices
    final Set<InitActor> initVertices = new LinkedHashSet<>();

    for (final AbstractActor currentVertex : orderedActors) {
      if (currentVertex instanceof InitActor) {
        initVertices.add((InitActor) currentVertex);
      }
    }

    if (initVertices.isEmpty()) {
      // Nothing to update !
      return;
    }

    // This map is used along the scan of the vertex of the dag. Its purpose is to store the last vertex scheduled on
    // each component. This way, when a new vertex is executed on this instance is encountered, an edge can be added
    // between it and the previous one.
    final Map<ComponentInstance, AbstractActor> lastVerticesScheduled = new LinkedHashMap<>();

    // Scan the dag and add new precedence edges caused by the schedule
    for (final AbstractActor currentVertex : orderedActors) {

      // Retrieve component
      final EList<ComponentInstance> mapping2 = mapping.getMapping(currentVertex);
      if (mapping2.size() != 1) {
        throw new UnsupportedOperationException();
      }
      mapping2.get(0);
      final ComponentInstance comp = mapping2.get(0);

      // Save currentVertex as lastScheduled on this component
      lastVerticesScheduled.put(comp, currentVertex);
    }

    // Now, remove Delay exclusion
    for (final InitActor dagInitVertex : initVertices) {
      // Retrieve the corresponding EndVertex
      final AbstractActor endReference = dagInitVertex.getEndReference();
      if (!(endReference instanceof EndActor)) {
        continue;
      }
      final EndActor dagEndVertex = (EndActor) endReference;

      // Compute the list of all edges between init and end
      final List<Fifo> predecessorEdgesOf = orderMngr.getPredecessorEdgesOf(dagEndVertex);
      final List<Fifo> successorEdgesOf = orderMngr.getSuccessorEdgesOf(dagInitVertex);
      final Set<Fifo> edgesInBetween = new LinkedHashSet<>(successorEdgesOf);
      edgesInBetween.retainAll(predecessorEdgesOf);

      // Remove exclusions with all buffer in the list (if any)
      if (!edgesInBetween.isEmpty()) {
        // retrieve the head MObj for current fifo. Size does not matter to retrieve the Memory object from the
        // exclusion graph
        final PiMemoryExclusionVertex headMemoryNode = new PiMemoryExclusionVertex(
            PiMemoryExclusionGraph.FIFO_HEAD_PREFIX + dagEndVertex.getName(), dagInitVertex.getName(), 0,
            this.scenario);
        for (final Fifo edge : edgesInBetween) {
          final PiMemoryExclusionVertex mObj = new PiMemoryExclusionVertex(edge, this.scenario);
          final DefaultEdge removeEdge = this.removeEdge(headMemoryNode, mObj);
          if (removeEdge != null) {
            System.out.println("f");
          }
        }
      }

    }
  }

  /**
   * This function update a {@link MemoryExclusionGraph MemEx} by taking scheduling information contained in a
   * {@link DirectedAcyclicGraph DAG} into account. <br>
   * <br>
   * It is important to note that only scheduling order of actors on each core is taken into account in order to remove
   * exclusions. The scheduling order of communication primitives is currently ignored when removing exclusions because
   * they have no impact when allocating memory in shared memory. <br>
   * In the case of distributed memory, memory of a buffer could be freed as soon as the sendEnd communication delimiter
   * is passed. But this has not been implemented so far. <br>
   * <br>
   * kdesnos: This method could probably be accelerated a lot ! Instead of scanning the dag in scheduling order, the dag
   * could be updated with new precedence edges. Then, scanning the exclusions and checking if they still hold (as is
   * done with memory object lifetime) could be done to remove unnecessary exclusions.
   *
   * @param dag
   *          the {@link DirectedAcyclicGraph DAG} used (will not be modified)
   */
  public void updateWithSchedule(final PiGraph dag, final Schedule schedule, final Mapping mapping) {

    // This map is used along the scan of the vertex of the dag.
    // Its purpose is to store the last vertex scheduled on each
    // component. This way, when a new vertex is executed on this
    // instance is encountered, an edge can be added between it and
    // the previous one.
    final Map<ComponentInstance, AbstractActor> lastVerticesScheduled = new LinkedHashMap<>();

    // Same a verticesPredecessors but only store predecessors that results
    // from scheduling info
    final Map<String, Set<PiMemoryExclusionVertex>> newVerticesPredecessors = new LinkedHashMap<>();

    final ScheduleOrderManager scheduleOrderManager = new ScheduleOrderManager(dag, schedule);
    final List<AbstractActor> orderedActors = scheduleOrderManager.buildScheduleAndTopologicalOrderedList();

    for (final AbstractActor currentVertex : orderedActors) {
      if (!dag.getAllActors().contains(currentVertex)) {
        continue;
      }

      // retrieve new predecessor list, if any.
      // else, create an empty one
      final String vertexName = currentVertex.getName();
      Set<PiMemoryExclusionVertex> newPredecessors = newVerticesPredecessors.get(vertexName);
      if (newPredecessors == null) {
        newPredecessors = new LinkedHashSet<>();
        newVerticesPredecessors.put(vertexName, newPredecessors);
      }

      // Retrieve component
      final EList<ComponentInstance> mapping2 = mapping.getMapping(currentVertex);
      if (mapping2.size() != 1) {
        throw new UnsupportedOperationException();
      }
      mapping2.get(0);
      final ComponentInstance comp = mapping2.get(0);

      // Retrieve last DAGVertex executed on this component
      final AbstractActor lastScheduled = lastVerticesScheduled.get(comp);

      // If this is not the first time this component is encountered
      if (lastScheduled != null) {
        // update new predecessors of current vertex
        // with all predecessor (new and not new) of previous
        // DAGVertex executed on this component.
        newPredecessors.addAll(newVerticesPredecessors.get(lastScheduled.getName()));
        newPredecessors.addAll(this.verticesPredecessors.get(lastScheduled.getName()));
        // "old" predecessors will be excluded later
      }
      // Save currentVertex as lastScheduled on this component
      lastVerticesScheduled.put(comp, currentVertex);

      // Exclude all "old" predecessors from "new" list
      newPredecessors.removeAll(this.verticesPredecessors.get(vertexName));

      if (!newPredecessors.isEmpty()) {
        // Remove exclusion between the Exclusion Vertex corresponding
        // to the working memory (if any) of the currentVertex and the
        // exclusion vertices in the newPredecessors list

        // Remove exclusion between ExclusionVertices corresponding
        // to outgoing edges of the currentVertex, and ExclusionVertices
        // in newPredecessors list
        final List<Fifo> outFifos = currentVertex.getDataOutputPorts().stream().map(DataPort::getFifo)
            .collect(Collectors.toList());
        for (final Fifo outgoingEdge : outFifos) {
          final PiMemoryExclusionVertex edgeVertex = new PiMemoryExclusionVertex(outgoingEdge, this.scenario);
          for (final PiMemoryExclusionVertex newPredecessor : newPredecessors) {

            final DefaultEdge edge = this.getEdge(edgeVertex, newPredecessor);
            if (edge == null) {
              System.out.println("meh");
            }
            if (this.removeEdge(edgeVertex, newPredecessor) == null) {
              /**
               * Possible causes are: <br>
               * -edgeVertex or newPredecessor no longer are in the graph <br>
               * -this.verticesPredecessors was corrupted before calling updateWithSchedule() <br>
               * -The exclusion or one of the vertex could not be found because the MemoryExclusionVertex.equals()
               * method is corrupted -Explode Implode were removed when creating the MemEx but not when updating it.
               */
              throw new PreesmRuntimeException(
                  "Failed removing exclusion between " + edgeVertex + " and " + newPredecessor);
            }
          }
          // Update newPredecessor list of successors
          // DAGVertices (the target of the current edge)
          Set<PiMemoryExclusionVertex> successorPredecessor;
          final String targetName = outgoingEdge.getTargetPort().getContainingActor().getName();
          successorPredecessor = newVerticesPredecessors.get(targetName);
          if (successorPredecessor == null) {
            // if successor did not have a new predecessor
            // list, create one
            successorPredecessor = new LinkedHashSet<>();
            newVerticesPredecessors.put(targetName, successorPredecessor);
          }
          successorPredecessor.addAll(newPredecessors);
        }
      }
    }

    // Update the delays exclusions
    updateDelayMemObjectWithSchedule(dag, schedule, mapping, scheduleOrderManager);

    // Save memory object "scheduling" order
    this.memExVerticesInSchedulingOrder = new ArrayList<>();
    // Copy the set of graph vertices (as a list to speedup search in
    // remaining code)
    /** Begin by putting all FIFO related Memory objects (if any) */
    for (final PiMemoryExclusionVertex vertex : vertexSet()) {
      if (vertex.getSource().startsWith(PiMemoryExclusionGraph.FIFO_HEAD_PREFIX)) {
        this.memExVerticesInSchedulingOrder.add(vertex);
      }
    }
  }
}
