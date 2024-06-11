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
package org.preesm.algorithm.memory.exclusiongraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.xtext.xbase.lib.Pair;
import org.jgrapht.generate.ComplementGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.preesm.algorithm.mapper.ScheduledDAGIterator;
import org.preesm.algorithm.mapper.graphtransfo.BufferAggregate;
import org.preesm.algorithm.mapper.graphtransfo.ImplementationPropertyNames;
import org.preesm.algorithm.mapper.graphtransfo.VertexType;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.algorithm.memory.script.Range;
import org.preesm.algorithm.model.PropertyBean;
import org.preesm.algorithm.model.PropertyFactory;
import org.preesm.algorithm.model.PropertySource;
import org.preesm.algorithm.model.dag.DAGEdge;
import org.preesm.algorithm.model.dag.DAGVertex;
import org.preesm.algorithm.model.dag.DirectedAcyclicGraph;
import org.preesm.algorithm.model.iterators.TopologicalDAGIterator;
import org.preesm.commons.CloneableProperty;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
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
public class MemoryExclusionGraph extends SimpleGraph<MemoryExclusionVertex, DefaultEdge>
    implements PropertySource, CloneableProperty<MemoryExclusionGraph> {

  public static final String FIFO_HEAD_PREFIX = "FIFO_Head_";

  private static final long serialVersionUID = 6491894138235944107L;

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
   * This {@link Map} associates of {@link MemoryExclusionVertex} that contain merged {@link MemoryExclusionVertex} to
   * the {@link Set} of contained {@link MemoryExclusionVertex}
   *
   */
  public static final String HOST_MEMORY_OBJECT_PROPERTY = "host_memory_objects";

  /**
   * Property to store an {@link Integer} corresponding to the amount of memory allocated.
   */
  public static final String ALLOCATED_MEMORY_SIZE = "allocated_memory_size";

  /**
   * Backup the vertex adjacent to a given vertex for speed-up purposes.
   */
  private final transient Map<MemoryExclusionVertex,
      Set<MemoryExclusionVertex>> adjacentVerticesBackup = new LinkedHashMap<>();

  /**
   * Each DAGVertex is associated to a list of MemoryExclusionVertex that have a precedence relationship with this
   * DAGVertex. All successors of this DAGVertex will NOT have exclusion with MemoryExclusionVertex in this list. This
   * list is built along the build of the MemEx, and used for subsequent updates. We use the name of the DAGVertex as a
   * key.. Because using the DAG itself seems to be impossible.<br>
   * If there are {@link MemoryExclusionVertex} corresponding to the working memory of {@link DAGVertex}, they will be
   * added to the predecessor list of this vertex.
   */
  private final transient Map<String, Set<MemoryExclusionVertex>> verticesPredecessors = new LinkedHashMap<>();

  /**
   * {@link MemoryExclusionVertex} of the {@link MemoryExclusionGraph} in the scheduling order retrieved in the
   * {@link #updateWithSchedule(DirectedAcyclicGraph)} method.
   */
  private transient List<MemoryExclusionVertex> memExVerticesInSchedulingOrder = null;

  /**
   * The {@link PropertyBean} that stores the properties of the {@link MemoryExclusionGraph}.
   */
  private final transient PropertyBean properties = new PropertyBean();

  private final Scenario scenario;

  /**
   * Default constructor.
   */
  public MemoryExclusionGraph(final Scenario scenario) {
    super(DefaultEdge.class);
    this.scenario = scenario;
  }

  @Override
  public DefaultEdge addEdge(final MemoryExclusionVertex arg0, final MemoryExclusionVertex arg1) {
    final Set<MemoryExclusionVertex> set0 = this.adjacentVerticesBackup.get(arg0);
    if (set0 != null) {
      set0.add(arg1);
    }
    final Set<MemoryExclusionVertex> set1 = this.adjacentVerticesBackup.get(arg1);
    if (set1 != null) {
      set1.add(arg0);
    }
    return super.addEdge(arg0, arg1);
  }

  /**
   * This method add the node corresponding to the passed edge to the ExclusionGraph. If the source or targeted vertex
   * isn't a task vertex, nothing is added. (should not happen)
   *
   * @param edge
   *          The memory transfer to add.
   * @return the exclusion graph node created (or null)
   */
  private MemoryExclusionVertex addNode(final DAGEdge edge) {
    // If the target and source vertices are tasks, add a node corresponding to the memory transfer to the exclusion
    // graph. Else, nothing
    final MemoryExclusionVertex resNode;

    // As the non-task vertices are removed at the beginning of the build function. This if statement could probably be
    // removed. (I keep it just in case)
    if (edge.getSource().getPropertyBean().getValue(ImplementationPropertyNames.VERTEX_VERTEX_TYPE)
        .equals(VertexType.TASK)
        && edge.getTarget().getPropertyBean().getValue(ImplementationPropertyNames.VERTEX_VERTEX_TYPE)
            .equals(VertexType.TASK)) {
      final MemoryExclusionVertex newNode = new MemoryExclusionVertex(edge, this.scenario);

      final boolean added = addVertex(newNode);

      // If false, this means that an equal node is already in the MemEx... somehow...
      if (!added) {
        // This may come from several edges belonging to an implodeSet
        PreesmLogger.getLogger().log(Level.WARNING, () -> "Vertex not added : " + newNode.toString());
        resNode = null;
      } else {
        resNode = newNode;
      }
    } else {
      resNode = null;
    }
    return resNode;
  }

  /**
   * Build the memory objects corresponding to the fifos of the input {@link DirectedAcyclicGraph}. The new memory
   * objects are added to the {@link MemoryExclusionGraph}. This method creates 1 or 2 {@link MemoryExclusionVertex} for
   * each pair of init/end {@link DAGVertex} encountered in the graph.
   *
   * @param dag
   *          the dag containing the fifos.
   */
  private void buildDelaysMemoryObjects(final DirectedAcyclicGraph dag) {
    // Scan the dag vertices
    for (final DAGVertex vertex : dag.vertexSet()) {
      final String vertKind = vertex.getKind();

      // Process Init vertices only
      if (vertKind.equals(MapperDAGVertex.DAG_INIT_VERTEX)) {

        final DAGVertex dagInitVertex = vertex;

        // Retrieve the corresponding EndVertex
        final String endReferenceName = vertex.getPropertyBean().getValue(MapperDAGVertex.END_REFERENCE);
        final DAGVertex dagEndVertex = dag.getVertex(endReferenceName);
        // @farresti:
        // It may happens that there are no end vertex associated with an init
        // If a FIFO is ended using getter actors for instance
        // In that case, should we leave ?
        if (dagEndVertex == null) {
          continue;
        }

        // Create the Head Memory Object. Get the typeSize
        if (dag.outgoingEdgesOf(dagInitVertex).isEmpty()) {
          throw new PreesmRuntimeException("Init DAG vertex" + dagInitVertex
              + " has no outgoing edges.\n This is not supported by the MemEx builder");
        }
        if (dag.outgoingEdgesOf(dagInitVertex).size() > 1) {
          throw new PreesmRuntimeException("Init DAG vertex " + dagInitVertex + " has several outgoing edges.\n"
              + "This is not supported by the MemEx builder.\n" + "Set \"ImplodeExplodeSuppr\" and \"Suppr Fork/Join\""
              + " options to false in the workflow tasks" + " to get rid of this error.");
        }
        final DAGEdge outgoingEdge = dag.outgoingEdgesOf(dagInitVertex).iterator().next();
        final BufferAggregate buffers = outgoingEdge.getPropertyBean().getValue(BufferAggregate.PROPERTY_BEAN_NAME);
        if (buffers.isEmpty()) {
          throw new PreesmRuntimeException(
              "DAGEdge " + outgoingEdge + " has no buffer properties.\n This is not supported by the MemEx builder.");
        }
        if (buffers.size() > 1) {
          throw new PreesmRuntimeException("DAGEdge " + outgoingEdge + " is equivalent to several SDFEdges.\n"
              + "This is not supported by the MemEx builder.\n" + "Please contact Preesm developers.");
        }
        final String typeName = buffers.get(0).getDataType();
        final long typeSize = this.getScenario().getSimulationInfo().getDataTypeSizeInBit(typeName);
        final MemoryExclusionVertex headMemoryNode = new MemoryExclusionVertex(
            MemoryExclusionGraph.FIFO_HEAD_PREFIX + dagEndVertex.getName(), dagInitVertex.getName(),
            this.getScenario().getSimulationInfo().getBufferSizeInBit(typeName, buffers.get(0).getNbToken()),
            this.scenario);
        headMemoryNode.setPropertyValue(MemoryExclusionVertex.TYPE_SIZE, typeSize);
        // Add the head node to the MEG
        addVertex(headMemoryNode);

        // Compute the list of all edges between init and end
        // Also compute the list of actors

        final Set<DAGEdge> endPredecessors = dag.getPredecessorEdgesOf(dagEndVertex);
        final Set<DAGEdge> initSuccessors = dag.getSuccessorEdgesOf(vertex);
        final Set<DAGEdge> between = (new LinkedHashSet<>(initSuccessors));
        between.retainAll(endPredecessors);

        // Add exclusions between the head node and ALL MemoryObjects
        // that do not correspond to edges in the between list or to
        // the working memory of an actor in the betweenVert list.
        for (final MemoryExclusionVertex memObject : vertexSet()) {
          final DAGEdge correspondingEdge = memObject.getEdge();
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
  public void buildGraph(final DirectedAcyclicGraph dag) {

    buildFifosMemoryObjects(dag);

    // Add the memory objects corresponding to the delays.
    buildDelaysMemoryObjects(dag);

    // Save the dag in the properties
    this.setPropertyValue(MemoryExclusionGraph.SOURCE_DAG, dag);
  }

  private void buildFifosMemoryObjects(final DirectedAcyclicGraph dag) {
    final Map<DAGVertex,
        Pair<Set<MemoryExclusionVertex>, Set<MemoryExclusionVertex>>> associatedMemExVertices = new LinkedHashMap<>();

    /*
     * Declarations & initializations
     */
    final TopologicalDAGIterator iterDAGVertices = new TopologicalDAGIterator(dag); // Iterator on DAG
    // vertices
    // Be careful, DAGiterator does not seem to work well if dag is
    // modified throughout the iteration.
    // That's why we use first copy the ordered dag vertex set.
    final LinkedHashSet<DAGVertex> dagVertices = new LinkedHashSet<>(dag.vertexSet().size());
    while (iterDAGVertices.hasNext()) {
      final DAGVertex vert = iterDAGVertices.next();
      dagVertices.add(vert);
    }

    // Remove dag vertex of type other than "task"
    // And identify source vertices (vertices without predecessors)

    // Set of non-task vertices
    final Set<DAGVertex> nonTaskVertices = new LinkedHashSet<>();

    for (final DAGVertex vert : dagVertices) {
      final boolean isTask = vert.getPropertyBean().getValue(ImplementationPropertyNames.VERTEX_VERTEX_TYPE)
          .equals(VertexType.TASK);
      String vertKind = "";

      // Only task vertices have a kind
      if (isTask) {
        vertKind = vert.getKind();
      }

      // if isTask seem simpler roundbuffers covered
      if (vertKind.equals(DAGVertex.DAG_VERTEX) || vertKind.equals(MapperDAGVertex.DAG_BROADCAST_VERTEX)
          || vertKind.equals(MapperDAGVertex.DAG_INIT_VERTEX) || vertKind.equals(MapperDAGVertex.DAG_END_VERTEX)
          || vertKind.equals(MapperDAGVertex.DAG_FORK_VERTEX) || vertKind.equals(MapperDAGVertex.DAG_JOIN_VERTEX)) {
        // If the dagVertex is a task (except implode/explode task), set
        // the scheduling Order which will be used as a unique ID for
        // each vertex

      } else {
        // Send/Receive
        nonTaskVertices.add(vert);
      }
    }
    dag.removeAllVertices(nonTaskVertices);
    dagVertices.removeAll(nonTaskVertices);

    dag.vertexSet().forEach(v -> associatedMemExVertices.put(v,
        Pair.of(new LinkedHashSet<MemoryExclusionVertex>(), new LinkedHashSet<MemoryExclusionVertex>())));

    // Scan of the DAG in order to:
    // - create Exclusion Graph nodes.
    // - add exclusion between consecutive Memory Transfer
    for (final DAGVertex vertexDAG : dagVertices) {
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
      for (final DAGEdge edge : vertexDAG.outgoingEdges()) {
        // Add the node to the Exclusion Graph
        final MemoryExclusionVertex newNode = addNode(edge);
        if (newNode == null) {
          // If the node was not added. Should never happen
          throw new PreesmRuntimeException(
              "The exclusion graph vertex corresponding to edge " + edge.toString() + " was not added to the graph.");
        }
        // Add Exclusions with all non-predecessors of the current vertex
        final Set<MemoryExclusionVertex> inclusions = associatedMemExVertices.get(vertexDAG).getKey();
        final Set<MemoryExclusionVertex> exclusions = new LinkedHashSet<>(vertexSet());
        exclusions.remove(newNode);
        exclusions.removeAll(inclusions);

        exclusions.forEach(ex -> this.addEdge(newNode, ex));

        final DAGVertex target = edge.getTarget();
        // Add newNode to the incoming list of the consumer of this edge
        associatedMemExVertices.get(target).getValue().add(newNode);

        // Update the predecessor list of the consumer of this edge
        Set<MemoryExclusionVertex> predecessor;
        predecessor = associatedMemExVertices.get(target).getKey();
        predecessor.addAll(inclusions);
        predecessor.addAll(associatedMemExVertices.get(vertexDAG).getValue());
      }
      // Save predecessor list, and include incoming to it.
      associatedMemExVertices.get(vertexDAG).getKey().addAll(associatedMemExVertices.get(vertexDAG).getValue());
      this.verticesPredecessors.put(vertexDAG.getName(), associatedMemExVertices.get(vertexDAG).getKey());
    }
  }

  /**
   * Method to clear the adjacent vertices list. As the adjacent vertices lists are passed as references, their content
   * might be corrupted if they are modified by the user of the class. Moreover, if a vertex is removed from the class
   * without using the removeAllVertices overrode method, the list will still contain the vertice. Clearing the lists is
   * left to the user's care. Indeed, a systematic clear to maintain the integrity of the lists would considerably
   * reduce the performances of some algorithms. A solution to that problem would be to use a faster implementation of
   * simpleGraphs that would provide fast methods to retrieve adjacent neighbors of a vertex !
   *
   */
  public void clearAdjacentVerticesBackup() {
    this.adjacentVerticesBackup.clear();
  }

  /**
   * Does a shallow copy (copy of the attribute reference) except for the list of vertices and edges where a deep copy
   * of the List is duplicated, but not its content.
   *
   * @return the object
   * @override
   */
  @Override
  public MemoryExclusionGraph copy() {
    return (MemoryExclusionGraph) super.clone();
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
    final Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hostVertices = getPropertyBean()
        .getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);
    // Scan host vertices
    if (hostVertices != null) {
      for (final Entry<MemoryExclusionVertex, Set<MemoryExclusionVertex>> entry : hostVertices.entrySet()) {
        final MemoryExclusionVertex hostVertex = entry.getKey();
        final Set<MemoryExclusionVertex> value = entry.getValue();
        // Put the host back to its original size (if it was changed, i.e. if it was allocated)
        final Object hostSizeObj = hostVertex.getPropertyBean().getValue(MemoryExclusionVertex.HOST_SIZE);
        if (hostSizeObj != null) {
          final long hostSize = (long) hostSizeObj;
          hostVertex.setWeight(hostSize);
          hostVertex.getPropertyBean().removeProperty(MemoryExclusionVertex.HOST_SIZE);

          // Scan merged vertices
          for (final MemoryExclusionVertex mergedVertex : value) {
            // If the merged vertex was in the graph (i.e. it was already allocated)
            if (containsVertex(mergedVertex)) {
              // Add exclusions between host and adjacent vertex of the merged vertex
              for (final MemoryExclusionVertex adjacentVertex : getAdjacentVertexOf(mergedVertex)) {
                this.addEdge(hostVertex, adjacentVertex);
              }
              // Remove it from the MEG
              removeVertex(mergedVertex);

              // If the merged vertex is not split
              if (mergedVertex.getWeight() != 0) {
                // Put it back to its real weight
                final long emptySpace = mergedVertex.getPropertyBean()
                    .getValue(MemoryExclusionVertex.EMPTY_SPACE_BEFORE);
                mergedVertex.setWeight(mergedVertex.getWeight() - emptySpace);
              } else {
                // The vertex was divided. Remove all fake mobjects
                final List<MemoryExclusionVertex> fakeMobjects = mergedVertex.getPropertyBean()
                    .getValue(MemoryExclusionVertex.FAKE_MOBJECT);
                for (final MemoryExclusionVertex fakeMobj : fakeMobjects) {
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
   * <li>List of Vertices (List of MemoryExclusionVertex)</li>
   * <li>MemoryExclusionVertex</li>
   * <li>Property of MemoryExclusionVertex</li>
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
  public MemoryExclusionGraph deepClone() {
    // Shallow copy with clone. Handle shallow copy of attributes and deep copy of lists of vertices and exclusion
    final MemoryExclusionGraph result = new MemoryExclusionGraph(this.getScenario());

    // Deep copy of all MObj (including merged ones). Keep a record of the old and new mObj
    final Map<MemoryExclusionVertex, MemoryExclusionVertex> mObjMap = new LinkedHashMap<>();
    for (final MemoryExclusionVertex vertex : getTotalSetOfVertices()) {
      final MemoryExclusionVertex vertexClone = vertex.getClone();
      mObjMap.put(vertex, vertexClone);
    }

    // Add mObjs to the graph (except merged ones)
    for (final MemoryExclusionVertex vertex : vertexSet()) {
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
   * reference only cloned {@link MemoryExclusionVertex} (and not {@link MemoryExclusionVertex} of the original
   * {@link MemoryExclusionGraph}).
   *
   * @param result
   *          The clone {@link MemoryExclusionGraph} created in the {@link #deepClone()} method.
   * @param mObjMap
   *          <code>Map&lt;MemoryExclusionVertex,MemoryExclusionVertex&gt;</code> associating original
   *          {@link MemoryExclusionVertex} of the current {@link MemoryExclusionGraph} to their clone.
   */
  private void deepCloneMegProperties(final MemoryExclusionGraph result,
      final Map<MemoryExclusionVertex, MemoryExclusionVertex> mObjMap) {
    // DAG_EDGE_ALLOCATION
    final Map<DAGEdge, Long> dagEdgeAlloc = getPropertyBean().getValue(MemoryExclusionGraph.DAG_EDGE_ALLOCATION);
    if (dagEdgeAlloc != null) {
      final Map<DAGEdge, Long> dagEdgeAllocCopy = new LinkedHashMap<>(dagEdgeAlloc);
      result.setPropertyValue(MemoryExclusionGraph.DAG_EDGE_ALLOCATION, dagEdgeAllocCopy);
    }

    // DAG_FIFO_ALLOCATION
    final Map<MemoryExclusionVertex,
        Long> dagFifoAlloc = getPropertyBean().getValue(MemoryExclusionGraph.DAG_FIFO_ALLOCATION);
    if (dagFifoAlloc != null) {
      final Map<MemoryExclusionVertex, Long> dagFifoAllocCopy = new LinkedHashMap<>();
      for (final Entry<MemoryExclusionVertex, Long> fifoAlloc : dagFifoAlloc.entrySet()) {
        dagFifoAllocCopy.put(mObjMap.get(fifoAlloc.getKey()), fifoAlloc.getValue());
      }
      result.setPropertyValue(MemoryExclusionGraph.DAG_FIFO_ALLOCATION, dagFifoAllocCopy);
    }

    // SOURCE_DAG
    if (getPropertyBean().getValue(MemoryExclusionGraph.SOURCE_DAG) != null) {
      result.setPropertyValue(MemoryExclusionGraph.SOURCE_DAG,
          getPropertyBean().getValue(MemoryExclusionGraph.SOURCE_DAG));
    }

    // ALLOCATED_MEMORY_SIZE
    if (getPropertyBean().getValue(MemoryExclusionGraph.ALLOCATED_MEMORY_SIZE) != null) {
      result.setPropertyValue(MemoryExclusionGraph.ALLOCATED_MEMORY_SIZE,
          getPropertyBean().getValue(MemoryExclusionGraph.ALLOCATED_MEMORY_SIZE));
    }

    // HOST_MEMORY_OBJECT_PROPERTY property
    final Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hostMemoryObject = getPropertyBean()
        .getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);
    if (hostMemoryObject != null) {
      final Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hostMemoryObjectCopy = new LinkedHashMap<>();
      for (final Entry<MemoryExclusionVertex, Set<MemoryExclusionVertex>> host : hostMemoryObject.entrySet()) {
        final Set<MemoryExclusionVertex> hostedCopy = new LinkedHashSet<>();
        for (final MemoryExclusionVertex hosted : host.getValue()) {
          hostedCopy.add(mObjMap.get(hosted));
        }
        hostMemoryObjectCopy.put(mObjMap.get(host.getKey()), hostedCopy);
      }
      result.setPropertyValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY, hostMemoryObjectCopy);
    }
  }

  /**
   * This method create a deep copy of the properties of the {@link MemoryExclusionVertex} cloned in the
   * {@link #deepClone()} method. <br>
   * Keys of the mObjMap parameter are the original {@link MemoryExclusionVertex} whose properties are to be cloned, and
   * values of the map are the corresponding {@link MemoryExclusionVertex} clones. Cloned properties are:
   * <ul>
   * <li>{@link MemoryExclusionVertex#MEMORY_OFFSET_PROPERTY}</li>
   * <li>{@link MemoryExclusionVertex#REAL_TOKEN_RANGE_PROPERTY}</li>
   * <li>{@link MemoryExclusionVertex#FAKE_MOBJECT}</li>
   * <li>{@link MemoryExclusionVertex#ADJACENT_VERTICES_BACKUP}</li>
   * <li>{@link MemoryExclusionVertex#EMPTY_SPACE_BEFORE}</li>
   * <li>{@link MemoryExclusionVertex#HOST_SIZE}</li>
   * <li>{@link MemoryExclusionVertex#DIVIDED_PARTS_HOSTS}</li>
   * <li>{@link MemoryExclusionVertex#TYPE_SIZE}</li>
   * <li>{@link MemoryExclusionVertex#INTER_BUFFER_SPACES}</li>
   * </ul>
   * All cloned properties referencing {@link MemoryExclusionVertex} are referencing {@link MemoryExclusionVertex} of
   * the mObjMap values (i.e. the cloned {@link MemoryExclusionVertex}).
   *
   * @param mObjMap
   *          <code>Map&lt;MemoryExclusionVertex,MemoryExclusionVertex&gt;</code> associating original
   *          {@link MemoryExclusionVertex} of the current {@link MemoryExclusionGraph} to their clone.
   */
  private void deepCloneVerticesProperties(final Map<MemoryExclusionVertex, MemoryExclusionVertex> mObjMap) {
    for (final Entry<MemoryExclusionVertex, MemoryExclusionVertex> entry : mObjMap.entrySet()) {
      final MemoryExclusionVertex vertex = entry.getKey();
      final MemoryExclusionVertex vertexClone = entry.getValue();

      // MEMORY_OFFSET_PROPERTY
      final Long memOffset = vertex.getPropertyBean().getValue(MemoryExclusionVertex.MEMORY_OFFSET_PROPERTY);
      if (memOffset != null) {
        vertexClone.setPropertyValue(MemoryExclusionVertex.MEMORY_OFFSET_PROPERTY, memOffset);
      }

      // REAL_TOKEN_RANGE_PROPERTY
      final List<Pair<MemoryExclusionVertex, Pair<Range, Range>>> realTokenRange = vertex.getPropertyBean()
          .getValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY);
      if (realTokenRange != null) {
        final List<Pair<MemoryExclusionVertex, Pair<Range, Range>>> realTokenRangeCopy = new ArrayList<>();
        for (final Pair<MemoryExclusionVertex, Pair<Range, Range>> pair : realTokenRange) {
          realTokenRangeCopy.add(Pair.of(mObjMap.get(pair.getKey()),
              Pair.of(pair.getValue().getKey().copy(), pair.getValue().getValue().copy())));
        }
        vertexClone.setPropertyValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY, realTokenRangeCopy);
      }

      // FAKE_MOBJECT
      final List<
          MemoryExclusionVertex> fakeMobject = vertex.getPropertyBean().getValue(MemoryExclusionVertex.FAKE_MOBJECT);
      if (fakeMobject != null) {
        final List<MemoryExclusionVertex> fakeMobjectCopy = new ArrayList<>();
        for (final MemoryExclusionVertex fakeMobj : fakeMobject) {
          fakeMobjectCopy.add(mObjMap.get(fakeMobj));
        }
        vertexClone.setPropertyValue(MemoryExclusionVertex.FAKE_MOBJECT, fakeMobjectCopy);
      }

      // ADJACENT_VERTICES_BACKUP
      final List<MemoryExclusionVertex> localAdjacentVerticesBackup = vertex.getPropertyBean()
          .getValue(MemoryExclusionVertex.ADJACENT_VERTICES_BACKUP);
      if (localAdjacentVerticesBackup != null) {
        final List<MemoryExclusionVertex> adjacentVerticesBackupCopy = new ArrayList<>();
        for (final MemoryExclusionVertex adjacentVertex : localAdjacentVerticesBackup) {
          adjacentVerticesBackupCopy.add(mObjMap.get(adjacentVertex));
        }
        vertexClone.setPropertyValue(MemoryExclusionVertex.ADJACENT_VERTICES_BACKUP, adjacentVerticesBackupCopy);
      }

      // EMPTY_SPACE_BEFORE
      final Integer emptySpaceBefore = vertex.getPropertyBean().getValue(MemoryExclusionVertex.EMPTY_SPACE_BEFORE);
      if (emptySpaceBefore != null) {
        vertexClone.setPropertyValue(MemoryExclusionVertex.EMPTY_SPACE_BEFORE, emptySpaceBefore);
      }

      // HOST_SIZE
      final Integer hostSize = vertex.getPropertyBean().getValue(MemoryExclusionVertex.HOST_SIZE);
      if (hostSize != null) {
        vertexClone.setPropertyValue(MemoryExclusionVertex.HOST_SIZE, hostSize);
      }

      // DIVIDED_PARTS_HOSTS
      final List<MemoryExclusionVertex> dividedPartHosts = vertex.getPropertyBean()
          .getValue(MemoryExclusionVertex.DIVIDED_PARTS_HOSTS);
      if (dividedPartHosts != null) {
        final List<MemoryExclusionVertex> dividedPartHostCopy = new ArrayList<>();
        for (final MemoryExclusionVertex host : dividedPartHosts) {
          dividedPartHostCopy.add(mObjMap.get(host));
        }
        vertexClone.setPropertyValue(MemoryExclusionVertex.DIVIDED_PARTS_HOSTS, dividedPartHostCopy);
      }

      final Object typeSizeValue = vertex.getPropertyBean().getValue(MemoryExclusionVertex.TYPE_SIZE);
      // TYPE_SIZE
      if (typeSizeValue != null) {
        final long typeSize = (long) typeSizeValue;
        vertexClone.setPropertyValue(MemoryExclusionVertex.TYPE_SIZE, typeSize);
      }

      // INTER_BUFFER_SPACES
      final List<
          Integer> interBufferSpaces = vertex.getPropertyBean().getValue(MemoryExclusionVertex.INTER_BUFFER_SPACES);
      if (interBufferSpaces != null) {
        vertexClone.setPropertyValue(MemoryExclusionVertex.INTER_BUFFER_SPACES, new ArrayList<>(interBufferSpaces));
      }
    }
  }

  /**
   * {@link #deepRemoveVertex(MemoryExclusionVertex)} for a {@link Collection} of {@link MemoryExclusionVertex}.
   *
   * @param vertices
   *          the {@link Collection} of {@link MemoryExclusionVertex} removed from the graph.
   */
  public void deepRemoveAllVertices(final Collection<? extends MemoryExclusionVertex> vertices) {

    // List of vertices
    // List of edges
    // adjacentVerticesBackup
    removeAllVertices(vertices);

    // memExVerticesInSchedulingOrder
    if (this.memExVerticesInSchedulingOrder != null) {
      this.memExVerticesInSchedulingOrder.removeAll(vertices);
    }

    // HOST_MEMORY_OBJECT_PROPERTY property
    final Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hosts = getPropertyBean()
        .getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);

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
      final Set<MemoryExclusionVertex> verticesWithAdjacentVerticesBackup = new LinkedHashSet<>(hosts.keySet());
      for (final Set<MemoryExclusionVertex> hosted : hosts.values()) {
        verticesWithAdjacentVerticesBackup.addAll(hosted);
      }
      for (final MemoryExclusionVertex vertex : verticesWithAdjacentVerticesBackup) {
        final List<MemoryExclusionVertex> vertexAdjacentVerticesBackup = vertex.getPropertyBean()
            .getValue(MemoryExclusionVertex.ADJACENT_VERTICES_BACKUP);
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
  public Set<MemoryExclusionVertex> getAdjacentVertexOf(final MemoryExclusionVertex vertex) {
    Set<MemoryExclusionVertex> result;

    // If this vertex was previously treated, simply access the backed-up neighbors set.
    if ((result = this.adjacentVerticesBackup.get(vertex)) != null) {
      return result;
    }

    // Else create the list of neighbors of the vertex
    result = new LinkedHashSet<>();

    // Add to result all vertices that have an edge with vertex
    final Set<DefaultEdge> edges = edgesOf(vertex);
    for (final DefaultEdge edge : edges) {
      result.add(getEdgeSource(edge));
      result.add(getEdgeTarget(edge));
    }

    // Remove vertex from result
    result.remove(vertex);

    // Back-up the resulting set
    this.adjacentVerticesBackup.put(vertex, result);

    // The following lines ensure that the vertices stored in the neighbors list belong to the graph.vertexSet. Indeed,
    // it may happen that several "equal" instances of MemoryExclusionGaphNodes are referenced in the same
    // MemoryExclusionGraph : one in the vertex set and one in the source/target of an edge. If someone retrieves this
    // vertex using the getEdgeSource(edge), then modifies the vertex, the changes might not be applied to the same
    // vertex retrieved in the vertexSet() of the graph. The following lines ensures that the vertices returned in the
    // neighbors lists always belong to the vertexSet().
    final Set<MemoryExclusionVertex> toAdd = new LinkedHashSet<>();

    for (final MemoryExclusionVertex vert : result) {
      for (final MemoryExclusionVertex vertin : vertexSet()) {
        if (vert.equals(vertin)) {
          // Correct the reference
          toAdd.add(vertin);
          break;
        }
      }
    }

    result.clear();
    result.addAll(toAdd);

    return result;
  }

  /**
   * Get the complementary graph of the exclusion graph. The complementary graph possess the same nodes but the
   * complementary edges. i.e. if there is an edge between vi and vj in the exclusion graph, there will be no edge in
   * the complementary.
   *
   * @return the complementary
   */
  public MemoryExclusionGraph getComplementary() {
    final MemoryExclusionGraph target = new MemoryExclusionGraph(this.getScenario());
    final ComplementGraphGenerator<MemoryExclusionVertex,
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
  public List<MemoryExclusionVertex> getMemExVerticesInSchedulingOrder() {
    if (this.memExVerticesInSchedulingOrder == null) {
      return Collections.emptyList();
    }
    return new ArrayList<>(this.memExVerticesInSchedulingOrder);
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
   * Returns the total number of {@link MemoryExclusionVertex} in the {@link MemoryExclusionGraph} including these
   * merged as a result of a buffer merging operation, and stored in the {@value #HOST_MEMORY_OBJECT_PROPERTY} property.
   *
   * @return the total number of vertices
   */
  public int getTotalNumberOfVertices() {
    return getTotalSetOfVertices().size();
  }

  /**
   * Returns the {@link Set} of all {@link MemoryExclusionVertex} in the {@link MemoryExclusionGraph}, including these
   * merged as a result of a buffer merging operation, and stored in the {@value #HOST_MEMORY_OBJECT_PROPERTY} property.
   *
   * @return the total set of vertices
   */
  public Set<MemoryExclusionVertex> getTotalSetOfVertices() {
    final Set<MemoryExclusionVertex> allVertices = new LinkedHashSet<>(vertexSet());
    final Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hosts = getPropertyBean()
        .getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);
    if (hosts != null) {
      hosts.forEach((host, hosted) -> allVertices.addAll(hosted));
    }

    return allVertices;
  }

  /**
   * This method returns the local copy of the {@link MemoryExclusionVertex} that is
   * {@link MemoryExclusionVertex}{@link #equals(Object)} to the {@link MemoryExclusionVertex} passed as a parameter.
   *
   * @param memObject
   *          a {@link MemoryExclusionVertex} searched in the {@link MemoryExclusionGraph}
   * @return an equal {@link MemoryExclusionVertex} from the {@link #vertexSet()}, null if there is no such vertex.
   */
  public MemoryExclusionVertex getVertex(final MemoryExclusionVertex memObject) {
    for (final MemoryExclusionVertex vertex : vertexSet()) {
      if (vertex.equals(memObject)) {
        return vertex;
      }
    }
    return null;
  }

  /**
   * Removes the all vertices.
   *
   * @param arg0
   *          the arg 0
   * @return true, if successful
   * @override
   */
  @Override
  public boolean removeAllVertices(final Collection<? extends MemoryExclusionVertex> arg0) {
    boolean result = super.removeAllVertices(arg0);

    for (final Set<MemoryExclusionVertex> backup : this.adjacentVerticesBackup.values()) {
      result |= backup.removeAll(arg0);
    }
    return result;
  }

  @Override
  public boolean removeEdge(final DefaultEdge arg0) {
    final MemoryExclusionVertex source = getEdgeSource(arg0);
    final MemoryExclusionVertex target = getEdgeTarget(arg0);

    final boolean result = super.removeEdge(arg0);
    if (result) {
      final Set<MemoryExclusionVertex> targetNeighbors = this.adjacentVerticesBackup.get(target);
      if (targetNeighbors != null) {
        targetNeighbors.remove(source);
      }
      final Set<MemoryExclusionVertex> sourceNeighbors = this.adjacentVerticesBackup.get(source);
      if (sourceNeighbors != null) {
        sourceNeighbors.remove(target);
      }
    }
    return result;
  }

  @Override
  public DefaultEdge removeEdge(final MemoryExclusionVertex arg0, final MemoryExclusionVertex arg1) {
    final DefaultEdge result = super.removeEdge(arg0, arg1);
    if (result != null) {
      final Set<MemoryExclusionVertex> arg0Neighbors = this.adjacentVerticesBackup.get(arg0);
      if (arg0Neighbors != null) {
        arg0Neighbors.remove(arg1);
      }
      final Set<MemoryExclusionVertex> arg1Neighbors = this.adjacentVerticesBackup.get(arg1);
      if (arg1Neighbors != null) {
        arg1Neighbors.remove(arg0);
      }
    }

    return result;
  }

  /**
   * Method used to update the exclusions between memory objects corresponding to Fifo heads and other memory objects of
   * the {@link MemoryExclusionGraph}. Exclusions will be removed from the exclusion graph, but no exclusions will be
   * added.
   *
   * @param inputDAG
   *          the input DAG
   */
  private void updateFIFOMemObjectWithSchedule(final DirectedAcyclicGraph inputDAG) {

    // Create a DAG with new edges from scheduling info
    final DirectedAcyclicGraph scheduledDAG = inputDAG.copy();

    // Create an List of the DAGVertices, in scheduling order.
    final List<DAGVertex> verticesMap = new ArrayList<>();

    // Iterator on DAG vertices
    final ScheduledDAGIterator iterDAGVertices = new ScheduledDAGIterator(scheduledDAG);

    // Get vertices in scheduling order and remove send/receive vertices from the dag. Also identify the init vertices
    final Set<DAGVertex> removedVertices = new LinkedHashSet<>();
    final Set<DAGVertex> initVertices = new LinkedHashSet<>();

    while (iterDAGVertices.hasNext()) {
      final DAGVertex currentVertex = iterDAGVertices.next();

      final boolean isTask = currentVertex.getPropertyBean().getValue(ImplementationPropertyNames.VERTEX_VERTEX_TYPE)
          .equals(VertexType.TASK);

      String vertKind = "";

      // Only task vertices have a kind
      if (isTask) {
        vertKind = currentVertex.getKind();
      }

      if (vertKind.equals(DAGVertex.DAG_VERTEX) || vertKind.equals(MapperDAGVertex.DAG_BROADCAST_VERTEX)
          || vertKind.equals(MapperDAGVertex.DAG_INIT_VERTEX) || vertKind.equals(MapperDAGVertex.DAG_END_VERTEX)
          || vertKind.equals(MapperDAGVertex.DAG_FORK_VERTEX) || vertKind.equals(MapperDAGVertex.DAG_JOIN_VERTEX)) {
        verticesMap.add(currentVertex);

        if (vertKind.equals(MapperDAGVertex.DAG_INIT_VERTEX)) {
          initVertices.add(currentVertex);
        }
      } else {
        removedVertices.add(currentVertex);
      }
    }

    if (initVertices.isEmpty()) {
      // Nothing to update !
      return;
    }

    // Remove unwanted vertices from the scheduledDag
    scheduledDAG.removeAllVertices(removedVertices);

    // This map is used along the scan of the vertex of the dag. Its purpose is to store the last vertex scheduled on
    // each component. This way, when a new vertex is executed on this instance is encountered, an edge can be added
    // between it and the previous one.
    Map<ComponentInstance, DAGVertex> lastVerticesScheduled;
    lastVerticesScheduled = new LinkedHashMap<>();

    // Scan the dag and add new precedence edges caused by the schedule
    final Set<DAGEdge> addedEdges = new LinkedHashSet<>();
    for (final DAGVertex currentVertex : verticesMap) {

      // Retrieve component
      final ComponentInstance comp = currentVertex.getPropertyBean().getValue("Operator");

      // Retrieve last DAGVertex executed on this component
      final DAGVertex lastScheduled = lastVerticesScheduled.get(comp);

      // If this is not the first time this component is encountered
      if ((lastScheduled != null) && (scheduledDAG.getEdge(lastScheduled, currentVertex) == null)) {
        // Add an edge between the last and the current vertex if there is not already one
        final DAGEdge newEdge = scheduledDAG.addEdge(lastScheduled, currentVertex);
        addedEdges.add(newEdge);
      }
      // Save currentVertex as lastScheduled on this component
      lastVerticesScheduled.put(comp, currentVertex);
    }

    // Now, remove fifo exclusion
    for (final DAGVertex dagInitVertex : initVertices) {
      // Retrieve the corresponding EndVertex
      final String endReferenceName = dagInitVertex.getPropertyBean().getValue(MapperDAGVertex.END_REFERENCE);
      final DAGVertex dagEndVertex = scheduledDAG.getVertex(endReferenceName);

      // Compute the list of all edges between init and end
      Set<DAGEdge> edgesBetween;
      final Set<DAGEdge> endPredecessors = scheduledDAG.getPredecessorEdgesOf(dagEndVertex);
      final Set<DAGEdge> initSuccessors = scheduledDAG.getSuccessorEdgesOf(dagInitVertex);
      edgesBetween = (new LinkedHashSet<>(initSuccessors));
      edgesBetween.retainAll(endPredecessors);
      edgesBetween.removeAll(addedEdges);

      // Remove exclusions with all buffer in the list (if any)
      if (!edgesBetween.isEmpty()) {
        // retrieve the head MObj for current fifo
        // size does not matter ("that's what she said") to retrieve the
        // Memory object from the exclusion graph
        final MemoryExclusionVertex headMemoryNode = new MemoryExclusionVertex(
            MemoryExclusionGraph.FIFO_HEAD_PREFIX + dagEndVertex.getName(), dagInitVertex.getName(), 0, this.scenario);
        edgesBetween.forEach(e -> this.removeEdge(headMemoryNode, new MemoryExclusionVertex(e, this.scenario)));
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
  public void updateWithSchedule(final DirectedAcyclicGraph dag) {

    // Since the MemEx is modified, the AdjacentVerticesBackup will be
    // deprecated. Clear it !
    clearAdjacentVerticesBackup();

    // This map is used along the scan of the vertex of the dag.
    // Its purpose is to store the last vertex scheduled on each
    // component. This way, when a new vertex is executed on this
    // instance is encountered, an edge can be added between it and
    // the previous one.
    Map<ComponentInstance, DAGVertex> lastVerticesScheduled;
    lastVerticesScheduled = new LinkedHashMap<>();

    // Same a verticesPredecessors but only store predecessors that results
    // from scheduling info
    final Map<String, Set<MemoryExclusionVertex>> newVerticesPredecessors = new LinkedHashMap<>();
    final TopologicalDAGIterator iterDAGVertices = new TopologicalDAGIterator(dag); // Iterator on DAG vertices

    // Create an array list of the DAGVertices, in scheduling order.
    // As the DAG are scanned following the precedence order, the
    // computation needed to sort the list should not be too heavy.
    final Map<Integer, DAGVertex> verticesMap = new LinkedHashMap<>();

    while (iterDAGVertices.hasNext()) {
      final DAGVertex currentVertex = iterDAGVertices.next();

      final boolean isTask = currentVertex.getPropertyBean().getValue(ImplementationPropertyNames.VERTEX_VERTEX_TYPE)
          .equals(VertexType.TASK);

      String vertKind = "";

      // Only task vertices have a kind
      if (isTask) {
        vertKind = currentVertex.getKind();
      }

      if (vertKind.equals(DAGVertex.DAG_VERTEX) || vertKind.equals(MapperDAGVertex.DAG_BROADCAST_VERTEX)
          || vertKind.equals(MapperDAGVertex.DAG_INIT_VERTEX) || vertKind.equals(MapperDAGVertex.DAG_END_VERTEX)
          || vertKind.equals(MapperDAGVertex.DAG_FORK_VERTEX) || vertKind.equals(MapperDAGVertex.DAG_JOIN_VERTEX)) {
        final Integer schedulingOrder = currentVertex.getPropertyBean()
            .getValue(ImplementationPropertyNames.VERTEX_SCHEDULING_ORDER);
        if (schedulingOrder == null) {
          throw new PreesmRuntimeException("Cannot build the memory exclusion graph of a non scheduled DAG",
              new NullPointerException());
        }
        verticesMap.put(schedulingOrder, currentVertex);
      }
    }

    final ArrayList<Integer> schedulingOrders = new ArrayList<>(verticesMap.keySet());
    Collections.sort(schedulingOrders);

    final List<DAGVertex> dagVerticesInSchedulingOrder = new ArrayList<>();

    // Update the buffer exclusions
    // Scan the vertices in scheduling order
    for (final int order : schedulingOrders) {
      final DAGVertex currentVertex = verticesMap.get(order);
      dagVerticesInSchedulingOrder.add(currentVertex);

      // retrieve new predecessor list, if any.
      // else, create an empty one
      final String vertexName = currentVertex.getName();
      Set<MemoryExclusionVertex> newPredecessors = newVerticesPredecessors.get(vertexName);
      if (newPredecessors == null) {
        newPredecessors = new LinkedHashSet<>();
        newVerticesPredecessors.put(vertexName, newPredecessors);
      }

      // Retrieve component
      final ComponentInstance comp = currentVertex.getPropertyBean().getValue("Operator");

      // Retrieve last DAGVertex executed on this component
      final DAGVertex lastScheduled = lastVerticesScheduled.get(comp);

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
        for (final DAGEdge outgoingEdge : currentVertex.outgoingEdges()) {
          if (outgoingEdge.getTarget().getPropertyBean().getValue(ImplementationPropertyNames.VERTEX_VERTEX_TYPE)
              .equals(VertexType.TASK)) {
            final MemoryExclusionVertex edgeVertex = new MemoryExclusionVertex(outgoingEdge, this.scenario);
            for (final MemoryExclusionVertex newPredecessor : newPredecessors) {
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
            Set<MemoryExclusionVertex> successorPredecessor;
            final String targetName = outgoingEdge.getTarget().getName();
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
    }

    // Update the fifo exclusions
    updateFIFOMemObjectWithSchedule(dag);

    // Save memory object "scheduling" order
    this.memExVerticesInSchedulingOrder = new ArrayList<>();
    // Copy the set of graph vertices (as a list to speedup search in
    // remaining code)
    /** Begin by putting all FIFO related Memory objects (if any) */
    vertexSet().stream().filter(v -> v.getSource().startsWith(MemoryExclusionGraph.FIFO_HEAD_PREFIX))
        .forEach(v -> this.memExVerticesInSchedulingOrder.add(v));

  }

  public Scenario getScenario() {
    return scenario;
  }
}
