/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
 * Julien Hascoet <jhascoet@kalray.eu> (2017)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2017)
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

import java.util.AbstractMap;
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
import org.eclipse.xtext.xbase.lib.Pair;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.preesm.algorithm.iterators.TopologicalDAGIterator;
import org.preesm.algorithm.mapper.ScheduledDAGIterator;
import org.preesm.algorithm.memory.allocation.MemoryAllocationException;
import org.preesm.algorithm.memory.script.Range;
import org.preesm.algorithm.model.PropertyBean;
import org.preesm.algorithm.model.PropertyFactory;
import org.preesm.algorithm.model.PropertySource;
import org.preesm.algorithm.model.dag.DAGEdge;
import org.preesm.algorithm.model.dag.DAGVertex;
import org.preesm.algorithm.model.dag.DirectedAcyclicGraph;
import org.preesm.algorithm.model.dag.edag.DAGBroadcastVertex;
import org.preesm.algorithm.model.dag.edag.DAGEndVertex;
import org.preesm.algorithm.model.dag.edag.DAGForkVertex;
import org.preesm.algorithm.model.dag.edag.DAGInitVertex;
import org.preesm.algorithm.model.dag.edag.DAGJoinVertex;
import org.preesm.algorithm.model.parameters.InvalidExpressionException;
import org.preesm.algorithm.model.sdf.esdf.SDFInitVertex;
import org.preesm.commons.CloneableProperty;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.scenario.types.BufferAggregate;
import org.preesm.model.scenario.types.DataType;
import org.preesm.model.scenario.types.ImplementationPropertyNames;
import org.preesm.model.scenario.types.VertexType;
import org.preesm.model.slam.ComponentInstance;

// TODO: Auto-generated Javadoc
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

  /** Mandatory when extending SimpleGraph. */
  private static final long serialVersionUID = 6491894138235944107L;

  /**
   * Property to store a {@link Map} corresponding to the allocation of the {@link DAGEdge}.
   */
  public static final String DAG_EDGE_ALLOCATION = "dag_edges_allocation";

  /** The Constant DAG_FIFO_ALLOCATION. */
  public static final String DAG_FIFO_ALLOCATION = "fifo_allocation";

  /** The Constant WORKING_MEM_ALLOCATION. */
  public static final String WORKING_MEM_ALLOCATION = "working_mem_allocation";

  /** The Constant SOURCE_DAG. */
  public static final String SOURCE_DAG = "source_dag";

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

  /** The public properties. */
  protected static List<String> public_properties = new ArrayList<String>() {
    /**
     *
     */
    private static final long serialVersionUID = -6039606828805117914L;

    {
    }
  };

  /**
   * Backup the vertex adjacent to a given vertex for speed-up purposes.
   */
  private Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> adjacentVerticesBackup;

  /**
   * This {@link Map} is used to identify and store the list of {@link MemoryExclusionVertex Memory Exclusion Vertices}
   * which are involved in an implode or an explode operation in the {@link DirectedAcyclicGraph DAG}. This information
   * is stored to make it possible to process those vertices differently depending on the context. For example, when
   * looking for the MaximumWeightClique in the graph, each vertex should be considered as a distinct memory object. On
   * the contrary, when trying to allocate the MemEx Graph in memory, all memory objects of an implode/explode operation
   * can (and must, for the codegen) be merged into a single memory object, in order to maximize the locality.
   */
  // private LinkedHashMap<String, Set<MemoryExclusionVertex>>
  // implodeExplodeMap;

  /**
   * Each DAGVertex is associated to a list of MemoryExclusionVertex that have a precedence relationship with this
   * DAGVertex. All successors of this DAGVertex will NOT have exclusion with MemoryExclusionVertex in this list. This
   * list is built along the build of the MemEx, and used for subsequent updates. We use the name of the DAGVertex as a
   * key.. Because using the DAG itself seems to be impossible.<br>
   * If there are {@link MemoryExclusionVertex} corresponding to the working memory of {@link DAGVertex}, they will be
   * added to the predecessor list of this vertex.
   */
  private Map<String, Set<MemoryExclusionVertex>> verticesPredecessors;

  /**
   * {@link MemoryExclusionVertex} of the {@link MemoryExclusionGraph} in the scheduling order retrieved in the
   * {@link #updateWithSchedule(DirectedAcyclicGraph)} method.
   */
  protected List<MemoryExclusionVertex> memExVerticesInSchedulingOrder = null;

  /**
   * The {@link PropertyBean} that stores the properties of the {@link MemoryExclusionGraph}.
   */
  protected PropertyBean properties;

  /**
   * Default constructor.
   */
  public MemoryExclusionGraph() {
    super(DefaultEdge.class);
    this.properties = new PropertyBean();
    this.adjacentVerticesBackup = new LinkedHashMap<>();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jgrapht.graph.AbstractBaseGraph#addEdge(java.lang.Object, java.lang.Object)
   */
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
  public MemoryExclusionVertex addNode(final DAGEdge edge) {
    // If the target and source vertices are tasks,
    // add a node corresponding to the memory transfer
    // to the exclusion graph. Else, nothing
    MemoryExclusionVertex newNode = null;

    // As the non-task vertices are removed at the beginning of the build
    // function
    // This if statement could probably be removed. (I keep it just in case)
    if (edge.getSource().getPropertyBean().getValue(ImplementationPropertyNames.Vertex_vertexType)
        .equals(VertexType.TASK)
        && edge.getTarget().getPropertyBean().getValue(ImplementationPropertyNames.Vertex_vertexType)
            .equals(VertexType.TASK)) {
      newNode = new MemoryExclusionVertex(edge);

      final boolean added = addVertex(newNode);

      // If false, this means that an equal node is already in the MemEx..
      // somehow..
      if (!added) {
        // This may come from several edges belonging to an implodeSet
        PreesmLogger.getLogger().log(Level.WARNING, "Vertex not added : " + newNode.toString());
        newNode = null;
      }

    }
    return newNode;
  }

  /**
   * Build the memory objects corresponding to the fifos of the input {@link DirectedAcyclicGraph}. The new memory
   * objects are added to the {@link MemoryExclusionGraph}. This method creates 1 or 2 {@link MemoryExclusionVertex} for
   * each pair of init/end {@link DAGVertex} encountered in the graph.
   *
   * @param dag
   *          the dag containing the fifos.
   */
  protected void buildFifoMemoryObjects(final DirectedAcyclicGraph dag) {
    // Scan the dag vertices
    for (final DAGVertex vertex : dag.vertexSet()) {
      final String vertKind = vertex.getKind();

      // Process Init vertices only
      if (vertKind.equals(DAGInitVertex.DAG_INIT_VERTEX)) {

        final DAGVertex dagInitVertex = vertex;

        // Retrieve the corresponding EndVertex
        final String endReferenceName = (String) vertex.getPropertyBean().getValue(DAGInitVertex.END_REFERENCE);
        final DAGVertex dagEndVertex = dag.getVertex(endReferenceName);
        // @farresti:
        // It may happens that there are no end vertex associated with an init
        // If a FIFO is ended using getter actors for instance
        // In that case, should we leave ?
        if (dagEndVertex == null) {
          continue;
        }

        // Create the Head Memory Object
        // Get the typeSize
        MemoryExclusionVertex headMemoryNode;
        long typeSize = 1; // (size of a token (from the scenario)
        {
          // TODO: Support the supprImplodeExplode option
          if (dag.outgoingEdgesOf(dagInitVertex).isEmpty()) {
            throw new RuntimeException("Init DAG vertex" + dagInitVertex
                + " has no outgoing edges.\n This is not supported by the MemEx builder");
          } else if (dag.outgoingEdgesOf(dagInitVertex).size() > 1) {
            throw new RuntimeException("Init DAG vertex " + dagInitVertex + " has several outgoing edges.\n"
                + "This is not supported by the MemEx builder.\n"
                + "Set \"ImplodeExplodeSuppr\" and \"Suppr Fork/Join\"" + " options to false in the workflow tasks"
                + " to get rid of this error.");
          }
          final DAGEdge outgoingEdge = dag.outgoingEdgesOf(dagInitVertex).iterator().next();
          final BufferAggregate buffers = (BufferAggregate) outgoingEdge.getPropertyBean()
              .getValue(BufferAggregate.propertyBeanName);
          if (buffers.isEmpty()) {
            throw new RuntimeException(
                "DAGEdge " + outgoingEdge + " has no buffer properties.\n This is not supported by the MemEx builder.");
          } else if (buffers.size() > 1) {
            throw new RuntimeException("DAGEdge " + outgoingEdge + " is equivalent to several SDFEdges.\n"
                + "This is not supported by the MemEx builder.\n" + "Please contact Preesm developers.");
          }
          final DataType type = MemoryExclusionVertex._dataTypes.get(buffers.get(0).getDataType());
          if (type != null) {
            typeSize = type.getSize();
          }
          headMemoryNode = new MemoryExclusionVertex("FIFO_Head_" + dagEndVertex.getName(), dagInitVertex.getName(),
              buffers.get(0).getSize() * typeSize);
          headMemoryNode.setPropertyValue(MemoryExclusionVertex.TYPE_SIZE, typeSize);
        }
        // Add the head node to the MEG
        addVertex(headMemoryNode);

        // Compute the list of all edges between init and end
        // Also compute the list of actors
        Set<DAGEdge> between;
        Set<MemoryExclusionVertex> betweenVert;
        {
          final Set<DAGEdge> endPredecessors = dag.getPredecessorEdgesOf(dagEndVertex);
          final Set<DAGEdge> initSuccessors = dag.getSuccessorEdgesOf(vertex);
          between = (new LinkedHashSet<>(initSuccessors));
          between.retainAll(endPredecessors);

          final Set<MemoryExclusionVertex> endPredecessorsVert = new LinkedHashSet<>();
          for (final DAGEdge edge : endPredecessors) {
            final String sourceName = edge.getSource().getName();
            endPredecessorsVert.add(new MemoryExclusionVertex(sourceName, sourceName, 0));
          }
          final Set<MemoryExclusionVertex> initSuccessorsVert = new LinkedHashSet<>();
          for (final DAGEdge edge : initSuccessors) {
            final String targetName = edge.getTarget().getName();
            initSuccessorsVert.add(new MemoryExclusionVertex(targetName, targetName, 0));
          }
          betweenVert = (new LinkedHashSet<>(initSuccessorsVert));
          betweenVert.retainAll(endPredecessorsVert);
        }

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
          } else if (memObject.getSource().equals(memObject.getSink())) {
            // For Working memory
            if (!betweenVert.contains(memObject)) {
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

        // Create the Memory Object for the remaining of the FIFO (if
        // any)
        final long fifoDepth = ((Long) dagInitVertex.getPropertyBean().getValue(SDFInitVertex.INIT_SIZE)).intValue();
        if (fifoDepth > (headMemoryNode.getWeight() / typeSize)) {
          final MemoryExclusionVertex fifoMemoryNode = new MemoryExclusionVertex("FIFO_Body_" + dagEndVertex.getName(),
              dagInitVertex.getName(), (fifoDepth * typeSize) - headMemoryNode.getWeight());
          fifoMemoryNode.setPropertyValue(MemoryExclusionVertex.TYPE_SIZE, typeSize);

          // Add to the graph and exclude with everyone
          addVertex(fifoMemoryNode);
          for (final MemoryExclusionVertex mObj : vertexSet()) {
            if (mObj != fifoMemoryNode) {
              this.addEdge(fifoMemoryNode, mObj);
            }
          }
        }
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
   * @throws InvalidExpressionException
   *           the invalid expression exception
   * @throws PreesmException
   *           the workflow exception
   */
  public void buildGraph(final DirectedAcyclicGraph dag) throws InvalidExpressionException, PreesmException {

    final String localOrdering = "memExBuildingLocalOrdering";

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

    this.verticesPredecessors = new LinkedHashMap<>();

    // Remove dag vertex of type other than "task"
    // And identify source vertices (vertices without predecessors)
    final Set<DAGVertex> nonTaskVertices = new LinkedHashSet<>(); // Set of
    // non-task
    // vertices
    final ArrayList<DAGVertex> sourcesVertices = new ArrayList<>(); // Set
    // of
    // source
    // vertices
    int newOrder = 0;

    for (final DAGVertex vert : dagVertices) {
      final boolean isTask = vert.getPropertyBean().getValue(ImplementationPropertyNames.Vertex_vertexType)
          .equals(VertexType.TASK);
      String vertKind = "";

      // Only task vertices have a kind
      if (isTask) {
        vertKind = vert.getKind();
      }

      // if (isTask) seem simpler ?
      // roundbuffers covered
      if (vertKind.equals(DAGVertex.DAG_VERTEX) || vertKind.equals(DAGBroadcastVertex.DAG_BROADCAST_VERTEX)
          || vertKind.equals(DAGInitVertex.DAG_INIT_VERTEX) || vertKind.equals(DAGEndVertex.DAG_END_VERTEX)
          || vertKind.equals(DAGForkVertex.DAG_FORK_VERTEX) || vertKind.equals(DAGJoinVertex.DAG_JOIN_VERTEX)) {
        // If the dagVertex is a task (except implode/explode task), set
        // the scheduling Order which will be used as a unique ID for
        // each vertex
        vert.getPropertyBean().setValue(localOrdering, newOrder);
        newOrder++;

        if (vert.incomingEdges().isEmpty()) {
          sourcesVertices.add(vert);
        }
      } else {
        // Send/Receive
        nonTaskVertices.add(vert);
      }
    }
    dag.removeAllVertices(nonTaskVertices);
    dagVertices.removeAll(nonTaskVertices);

    // iterDAGVertices = new DAGIterator(dag); // Iterator on DAG vertices

    // Each element of the "predecessors" list corresponds to a DagVertex
    // and
    // stores all its preceding ExclusionGraphNode except those
    // corresponding to incoming edges
    // The unique ID of the DAG vertices (their scheduling order) are used
    // as indexes in this list
    final ArrayList<Set<MemoryExclusionVertex>> predecessors = new ArrayList<>(dag.vertexSet().size());

    // Each element of the "incoming" list corresponds to a DAGVertex and
    // store only the ExclusionGraphNode that corresponds to its incoming
    // edges
    // The unique ID of the DAG vertices (their scheduling order) are used
    // as indexes in this list
    final ArrayList<Set<MemoryExclusionVertex>> incoming = new ArrayList<>(dag.vertexSet().size());

    // Initialize predecessors with empty LinkedHashSets
    for (int i = 0; i < dag.vertexSet().size(); i++) {
      predecessors.add(new LinkedHashSet<MemoryExclusionVertex>());
      incoming.add(new LinkedHashSet<MemoryExclusionVertex>());
    }

    // Scan of the DAG in order to:
    // - create Exclusion Graph nodes.
    // - add exclusion between consecutive Memory Transfer
    for (final DAGVertex vertexDAG : dagVertices) {
      // For each vertex of the DAG

      // Processing is done in the following order:
      // 1. Fork/Join/Broadcast/RoundBuffer specific processing
      // 2. Working Memory specific Processing
      // 3. Outgoing Edges processing

      // Retrieve the vertex to process
      // Retrieve the vertex unique ID
      final int vertexID = (Integer) vertexDAG.getPropertyBean().getValue(localOrdering);

      // 1. Fork/Join/Broadcast/Roundbuffer specific processing
      // Not usable yet ! Does not work because output edges are not
      // allocated
      // in the same order.. so overwrite are possible : inout needed here
      // !

      // Implicit Else if: broadcast/fork/join/roundBuffer have no working
      // mem
      // 2. Working Memory specific Processing
      // If the current vertex has some working memory, create the
      // associated MemoryExclusionGraphVertex
      final Long wMem = (Long) vertexDAG.getPropertyBean().getValue("working_memory");
      if (wMem != null) {
        final MemoryExclusionVertex workingMemoryNode = new MemoryExclusionVertex(vertexDAG.getName(),
            vertexDAG.getName(), wMem);
        workingMemoryNode.setVertex(vertexDAG);
        addVertex(workingMemoryNode);
        // Currently, there is no special alignment for working memory.
        // So we always assume a unitary typesize.
        workingMemoryNode.setPropertyValue(MemoryExclusionVertex.TYPE_SIZE, 1L);

        // Add Exclusions with all non-predecessors of the current
        // vertex
        final Set<MemoryExclusionVertex> inclusions = predecessors.get(vertexID);
        final Set<MemoryExclusionVertex> exclusions = new LinkedHashSet<>(vertexSet());
        exclusions.remove(workingMemoryNode);
        exclusions.removeAll(inclusions);
        for (final MemoryExclusionVertex exclusion : exclusions) {
          this.addEdge(workingMemoryNode, exclusion);
        }

        // Add the node to the "incoming" list of the DAGVertex.
        // Like incoming edges, the working memory must have
        // exclusions
        // with all outgoing edges but not with successors
        incoming.get(vertexID).add(workingMemoryNode);
      }

      // For each outgoing edge
      for (final DAGEdge edge : vertexDAG.outgoingEdges()) {
        // Add the node to the Exclusion Graph
        MemoryExclusionVertex newNode;
        if ((newNode = addNode(edge)) != null) {

          // If a node was added.(It should always be the case)

          // Add Exclusions with all non-predecessors of the current
          // vertex
          final Set<MemoryExclusionVertex> inclusions = predecessors.get(vertexID);
          final Set<MemoryExclusionVertex> exclusions = new LinkedHashSet<>(vertexSet());
          exclusions.remove(newNode);
          exclusions.removeAll(inclusions);
          for (final MemoryExclusionVertex exclusion : exclusions) {
            this.addEdge(newNode, exclusion);
          }

          // Add newNode to the incoming list of the consumer of this
          // edge
          incoming.get((Integer) edge.getTarget().getPropertyBean().getValue(localOrdering)).add(newNode);

          // Update the predecessor list of the consumer of this edge
          Set<MemoryExclusionVertex> predecessor;
          predecessor = predecessors.get((Integer) edge.getTarget().getPropertyBean().getValue(localOrdering));
          predecessor.addAll(inclusions);
          predecessor.addAll(incoming.get(vertexID));
        } else {
          // If the node was not added.
          // Should never happen
          throw new PreesmException(
              "The exclusion graph vertex corresponding to edge " + edge.toString() + " was not added to the graph.");
        }
      }
      // Save predecessor list, and include incoming to it.
      predecessors.get(vertexID).addAll(incoming.get(vertexID));
      this.verticesPredecessors.put(vertexDAG.getName(), predecessors.get(vertexID));
    }

    // Add the memory objects corresponding to the fifos.
    buildFifoMemoryObjects(dag);

    // Save the dag in the properties
    setPropertyValue(MemoryExclusionGraph.SOURCE_DAG, dag);
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
    final MemoryExclusionGraph o = (MemoryExclusionGraph) super.clone();
    o.adjacentVerticesBackup = new LinkedHashMap<>();
    return o;

  }

  /**
   * This method puts the {@link MemoryExclusionGraph} back to its state before any memory allocation was performed.<br>
   * Tasks performed are:<br>
   * - Put back the host memory objects that were replaced by their content during memory allocation. - Restore the MEG
   * to its original state before allocation
   */
  public void deallocate() {

    @SuppressWarnings("unchecked")
    final Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hostVertices = (Map<MemoryExclusionVertex,
        Set<MemoryExclusionVertex>>) getPropertyBean().getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);
    // Scan host vertices
    if (hostVertices != null) {
      for (final MemoryExclusionVertex hostVertex : hostVertices.keySet()) {
        // Put the host back to its original size (if it was changed,
        // i.e. if it was allocated)
        final Object hostSizeObj = hostVertex.getPropertyBean().getValue(MemoryExclusionVertex.HOST_SIZE);
        if (hostSizeObj != null) {
          final long hostSize = (long) hostSizeObj;
          hostVertex.setWeight(hostSize);
          hostVertex.getPropertyBean().removeProperty(MemoryExclusionVertex.HOST_SIZE);

          // Scan merged vertices
          for (final MemoryExclusionVertex mergedVertex : hostVertices.get(hostVertex)) {
            // If the merged vertex was in the graph (i.e. it was
            // already allocated)
            if (containsVertex(mergedVertex)) {
              // Add exclusions between host and adjacent vertex
              // of
              // the merged vertex
              for (final MemoryExclusionVertex adjacentVertex : getAdjacentVertexOf(mergedVertex)) {
                this.addEdge(hostVertex, adjacentVertex);
              }
              // Remove it from the MEG
              removeVertex(mergedVertex);

              // If the merged vertex is not split
              if (mergedVertex.getWeight() != 0) {
                // Put it back to its real weight
                final int emptySpace = (int) mergedVertex.getPropertyBean()
                    .getValue(MemoryExclusionVertex.EMPTY_SPACE_BEFORE);
                mergedVertex.setWeight(mergedVertex.getWeight() - emptySpace);
              } else {
                // The vertex was divided
                // Remove all fake mobjects
                @SuppressWarnings("unchecked")
                final List<MemoryExclusionVertex> fakeMobjects = (List<MemoryExclusionVertex>) mergedVertex
                    .getPropertyBean().getValue(MemoryExclusionVertex.FAKE_MOBJECT);
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
    // Shallow copy with clone
    // Handle shallow copy of attributes and deep copy of lists of vertices
    // and
    // exclusion
    final MemoryExclusionGraph result = new MemoryExclusionGraph();

    // Deep copy of all MObj (including merged ones)
    // Keep a record of the old and new mObj
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
      result.memExVerticesInSchedulingOrder.replaceAll(mObj -> {
        return mObjMap.get(mObj);
      });
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
  protected void deepCloneMegProperties(final MemoryExclusionGraph result,
      final Map<MemoryExclusionVertex, MemoryExclusionVertex> mObjMap) {
    // DAG_EDGE_ALLOCATION
    @SuppressWarnings("unchecked")
    final Map<DAGEdge,
        Long> dagEdgeAlloc = (Map<DAGEdge, Long>) getPropertyBean().getValue(MemoryExclusionGraph.DAG_EDGE_ALLOCATION);
    if (dagEdgeAlloc != null) {
      final Map<DAGEdge, Long> dagEdgeAllocCopy = new LinkedHashMap<>(dagEdgeAlloc);
      result.setPropertyValue(MemoryExclusionGraph.DAG_EDGE_ALLOCATION, dagEdgeAllocCopy);
    }

    // DAG_FIFO_ALLOCATION
    @SuppressWarnings("unchecked")
    final Map<MemoryExclusionVertex, Long> dagFifoAlloc = (Map<MemoryExclusionVertex, Long>) getPropertyBean()
        .getValue(MemoryExclusionGraph.DAG_FIFO_ALLOCATION);
    if (dagFifoAlloc != null) {
      final Map<MemoryExclusionVertex, Long> dagFifoAllocCopy = new LinkedHashMap<>();
      for (final Entry<MemoryExclusionVertex, Long> fifoAlloc : dagFifoAlloc.entrySet()) {
        dagFifoAllocCopy.put(mObjMap.get(fifoAlloc.getKey()), fifoAlloc.getValue());
      }
      result.setPropertyValue(MemoryExclusionGraph.DAG_FIFO_ALLOCATION, dagFifoAllocCopy);
    }

    // WORKING_MEM_ALLOCATION
    @SuppressWarnings("unchecked")
    final Map<MemoryExclusionVertex, Long> wMemAlloc = (Map<MemoryExclusionVertex, Long>) getPropertyBean()
        .getValue(MemoryExclusionGraph.WORKING_MEM_ALLOCATION);
    if (wMemAlloc != null) {
      final Map<MemoryExclusionVertex, Long> wMemAllocCopy = new LinkedHashMap<>();
      for (final Entry<MemoryExclusionVertex, Long> wMem : wMemAlloc.entrySet()) {
        wMemAllocCopy.put(mObjMap.get(wMem.getKey()), wMem.getValue());
      }
      result.setPropertyValue(MemoryExclusionGraph.WORKING_MEM_ALLOCATION, wMemAllocCopy);
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
    @SuppressWarnings("unchecked")
    final Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hostMemoryObject = (Map<MemoryExclusionVertex,
        Set<MemoryExclusionVertex>>) getPropertyBean().getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);
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
  protected void deepCloneVerticesProperties(final Map<MemoryExclusionVertex, MemoryExclusionVertex> mObjMap) {
    for (final Entry<MemoryExclusionVertex, MemoryExclusionVertex> entry : mObjMap.entrySet()) {
      final MemoryExclusionVertex vertex = entry.getKey();
      final MemoryExclusionVertex vertexClone = entry.getValue();

      // MEMORY_OFFSET_PROPERTY
      final Long memOffset = (Long) vertex.getPropertyBean().getValue(MemoryExclusionVertex.MEMORY_OFFSET_PROPERTY);
      if (memOffset != null) {
        vertexClone.setPropertyValue(MemoryExclusionVertex.MEMORY_OFFSET_PROPERTY, memOffset);
      }

      // REAL_TOKEN_RANGE_PROPERTY
      @SuppressWarnings("unchecked")
      final List<Pair<MemoryExclusionVertex,
          Pair<Range, Range>>> realTokenRange = (List<Pair<MemoryExclusionVertex, Pair<Range, Range>>>) vertex
              .getPropertyBean().getValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY);
      if (realTokenRange != null) {
        final List<Pair<MemoryExclusionVertex, Pair<Range, Range>>> realTokenRangeCopy = new ArrayList<>();
        for (final Pair<MemoryExclusionVertex, Pair<Range, Range>> pair : realTokenRange) {
          realTokenRangeCopy.add(Pair.of(mObjMap.get(pair.getKey()),
              Pair.of((Range) pair.getValue().getKey().copy(), (Range) pair.getValue().getValue().copy())));
        }
        vertexClone.setPropertyValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY, realTokenRangeCopy);
      }

      // FAKE_MOBJECT
      @SuppressWarnings("unchecked")
      final List<MemoryExclusionVertex> fakeMobject = (List<MemoryExclusionVertex>) vertex.getPropertyBean()
          .getValue(MemoryExclusionVertex.FAKE_MOBJECT);
      if (fakeMobject != null) {
        final List<MemoryExclusionVertex> fakeMobjectCopy = new ArrayList<>();
        for (final MemoryExclusionVertex fakeMobj : fakeMobject) {
          fakeMobjectCopy.add(mObjMap.get(fakeMobj));
        }
        vertexClone.setPropertyValue(MemoryExclusionVertex.FAKE_MOBJECT, fakeMobjectCopy);
      }

      // ADJACENT_VERTICES_BACKUP
      @SuppressWarnings("unchecked")
      final List<MemoryExclusionVertex> adjacentVerticesBackup = (List<MemoryExclusionVertex>) vertex.getPropertyBean()
          .getValue(MemoryExclusionVertex.ADJACENT_VERTICES_BACKUP);
      if (adjacentVerticesBackup != null) {
        final List<MemoryExclusionVertex> adjacentVerticesBackupCopy = new ArrayList<>();
        for (final MemoryExclusionVertex adjacentVertex : adjacentVerticesBackup) {
          adjacentVerticesBackupCopy.add(mObjMap.get(adjacentVertex));
        }
        vertexClone.setPropertyValue(MemoryExclusionVertex.ADJACENT_VERTICES_BACKUP, adjacentVerticesBackupCopy);
      }

      // EMPTY_SPACE_BEFORE
      final Integer emptySpaceBefore = (Integer) vertex.getPropertyBean()
          .getValue(MemoryExclusionVertex.EMPTY_SPACE_BEFORE);
      if (emptySpaceBefore != null) {
        vertexClone.setPropertyValue(MemoryExclusionVertex.EMPTY_SPACE_BEFORE, emptySpaceBefore);
      }

      // HOST_SIZE
      final Integer hostSize = (Integer) vertex.getPropertyBean().getValue(MemoryExclusionVertex.HOST_SIZE);
      if (hostSize != null) {
        vertexClone.setPropertyValue(MemoryExclusionVertex.HOST_SIZE, hostSize);
      }

      // DIVIDED_PARTS_HOSTS
      @SuppressWarnings("unchecked")
      final List<MemoryExclusionVertex> dividedPartHosts = (List<MemoryExclusionVertex>) vertex.getPropertyBean()
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
      @SuppressWarnings("unchecked")
      final List<Integer> interBufferSpaces = (List<Integer>) vertex.getPropertyBean()
          .getValue(MemoryExclusionVertex.INTER_BUFFER_SPACES);
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
    @SuppressWarnings("unchecked")
    final Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hosts = (Map<MemoryExclusionVertex,
        Set<MemoryExclusionVertex>>) getPropertyBean().getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);

    if (hosts != null) {
      hosts.keySet().removeAll(vertices); // Changes to the KeySet are
      // applied
      // to the map
      hosts.forEach((host, hosted) -> {
        if (hosted.removeAll(vertices)) {
          // List of hosted mObjects should never be impacted by
          // the remove operation. (in the context of Distributor
          // call)
          // indeed, deeply removed vertices correspond to vertices
          // belonging to other memory banks, hence if a hosted Mobj
          // belong to a list, and this Mobj is to be removed, then
          // the
          // host Mobj for this hosted Mobj is mandatorily associated
          // to
          // another bank, and has been removed in the previous lines
          // of
          // the code.
          throw new RuntimeException("A hosted Memory Object was removed (but its host was not).");
        }
      });

      // ADJACENT_VERTICES_BACKUP property vertices
      final Set<MemoryExclusionVertex> verticesWithAdjacentVerticesBackup = new LinkedHashSet<>();
      verticesWithAdjacentVerticesBackup.addAll(hosts.keySet());
      for (final Set<MemoryExclusionVertex> hosted : hosts.values()) {
        verticesWithAdjacentVerticesBackup.addAll(hosted);
      }
      for (final MemoryExclusionVertex vertex : verticesWithAdjacentVerticesBackup) {
        @SuppressWarnings("unchecked")
        final List<MemoryExclusionVertex> adjacentVerticesBackup = (List<MemoryExclusionVertex>) vertex
            .getPropertyBean().getValue(MemoryExclusionVertex.ADJACENT_VERTICES_BACKUP);
        adjacentVerticesBackup.removeAll(vertices);
      }
    }
  }

  /**
   * Remove a {@link MemoryExclusionVertex} from the {@link MemoryExclusionGraph}. Contrary to
   * {@link #removeVertex(MemoryExclusionVertex)}, this method scans the properties and attributes of the
   * {@link MemoryExclusionGraph} to remove all reference to the removed {@link MemoryExclusionVertex}.<br>
   * <br>
   * Properties and attributes affected by this method are:
   * <ul>
   * <li>List of vertices</li>
   * <li>List of edges</li>
   * <li>{@link #adjacentVerticesBackup}</li>
   * <li>{@link #memExVerticesInSchedulingOrder}</li>
   * <li>{@link #HOST_MEMORY_OBJECT_PROPERTY} property}</li>
   * <li>{@link MemoryExclusionVertex#ADJACENT_VERTICES_BACKUP} property of {@link MemoryExclusionVertex vertices}</li>
   * </ul>
   *
   * @param vertex
   *          the {@link MemoryExclusionVertex} removed from the graph.
   */
  public void deepRemoveVertex(final MemoryExclusionVertex vertex) {
    final List<MemoryExclusionVertex> list = new ArrayList<>();
    list.add(vertex);
    // Calls the list removing code to avoid duplicating the method code.
    deepRemoveAllVertices(list);
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

    // If this vertex was previously treated, simply access the backed-up
    // neighbors set.
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

    // The following lines ensure that the vertices stored in the neighbors
    // list
    // belong to the graph.vertexSet.
    // Indeed, it may happen that several "equal" instances of
    // MemoryExclusionGaphNodes
    // are referenced in the same MemoryExclusionGraph : one in the vertex
    // set and one in the source/target of an edge.
    // If someone retrieves this vertex using the getEdgeSource(edge), then
    // modifies the vertex, the changes
    // might not be applied to the same vertex retrieved in the vertexSet()
    // of the graph.
    // The following lines ensures that the vertices returned in the
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
    // Create a new Memory Exclusion Graph
    final MemoryExclusionGraph result = new MemoryExclusionGraph();
    // Copy the vertices of the current graph
    for (final MemoryExclusionVertex vertex : vertexSet()) {
      result.addVertex(vertex);
    }
    // Retrieve the vertices list
    final MemoryExclusionVertex[] vertices = vertexSet().toArray(new MemoryExclusionVertex[0]);
    // For each pair of vertex, check if the corresponding edge exists in
    // the current graph.
    // If not, add an edge in the complementary graph
    for (int i = 0; i < vertexSet().size(); i++) {
      for (int j = i + 1; j < vertexSet().size(); j++) {
        if (!this.containsEdge(vertices[i], vertices[j])) {
          result.addEdge(vertices[i], vertices[j]);
        }
      }
    }
    return result;
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
      return null;
    } else {
      return new ArrayList<>(this.memExVerticesInSchedulingOrder);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.PropertySource#getFactoryForProperty(java.lang.String)
   */
  @Override
  public PropertyFactory getFactoryForProperty(final String propertyName) {
    return null;
  }

  /**
   * Return the lifetime of the {@link MemoryExclusionVertex memory object} passed as a parameter. If the memory object
   * corresponds to a fifo, the first {@link Integer} will have a greater value than the second. If the memory object
   * always exists, its lifetime will be <code>(0L,0L)</code>
   *
   * @param vertex
   *          the vertex whose lifetime is searched
   * @param dag
   *          the scheduled {@link DirectedAcyclicGraph DAG} from which the {@link MemoryExclusionVertex MemEx Vertex}
   *          is derived
   * @return the lifetime of the memory object in the form of 2 integers corresponding to its life beginning and end.
   * @throws RuntimeException
   *           if the {@link MemoryExclusionVertex} is not derived from the given {@link DirectedAcyclicGraph DAG} or if
   *           the {@link DirectedAcyclicGraph DAG} was not scheduled.
   */
  protected Entry<Long, Long> getLifeTime(final MemoryExclusionVertex vertex, final DirectedAcyclicGraph dag) {

    // If the MemObject corresponds to an edge, its lifetime spans from the
    // execution start of its source until the execution end of its target
    final DAGEdge edge = vertex.getEdge();
    if (edge != null) {
      final DAGVertex source = edge.getSource();
      final DAGVertex target = edge.getTarget();

      if ((source == null) || (target == null)) {
        throw new RuntimeException("Cannot get lifetime of a memory object " + vertex.toString()
            + " because its corresponding DAGEdge has no valid source and/or target");
      }

      final long birth = (long) source.getPropertyBean().getValue("TaskStartTime");

      final long death = (long) target.getPropertyBean().getValue("TaskStartTime");

      final long duration = (long) target.getPropertyBean().getValue(ImplementationPropertyNames.Task_duration);

      return new AbstractMap.SimpleEntry<>((Long) birth, (Long) death + (Long) duration);
    }

    // Else the memEx vertex corresponds to a working memory
    if (vertex.getSink().equals(vertex.getSource())) {
      final DAGVertex dagVertex = dag.getVertex(vertex.getSink());
      if (dagVertex == null) {
        throw new RuntimeException("Cannot get lifetime of working memory object " + vertex
            + " because its corresponding DAGVertex does not exist in the given DAG.");
      }

      final long birth = (long) dagVertex.getPropertyBean().getValue("TaskStartTime");
      final long duration = (long) dagVertex.getPropertyBean().getValue(ImplementationPropertyNames.Task_duration);

      return new AbstractMap.SimpleEntry<>(birth, birth + duration);

    }

    if (vertex.getSource().startsWith("FIFO_")) {
      if (vertex.getSource().startsWith("FIFO_Body")) {
        return new AbstractMap.SimpleEntry<>(0L, 0L);
      }

      // Working memory exists from the beginning of the End until the end
      // of the init.
      // Since there is no exclusion with edges connected to the init/end
      // vertices they will not be added (since updating only consists in
      // removing existing exclusions)
      if (vertex.getSource().startsWith("FIFO_Head_")) {
        final DAGVertex dagEndVertex = dag.getVertex(vertex.getSource().substring(("FIFO_Head_").length()));
        final DAGVertex dagInitVertex = dag.getVertex(vertex.getSink());

        if ((dagEndVertex == null) || (dagInitVertex == null)) {
          throw new RuntimeException("Cannot get lifetime of a memory object " + vertex.toString()
              + " because its corresponding DAGVertex could not be found in the DAG");
        }
        final long birth = (long) dagEndVertex.getPropertyBean().getValue("TaskStartTime");

        final long death = (long) dagInitVertex.getPropertyBean().getValue("TaskStartTime");

        final long duration = (long) dagInitVertex.getPropertyBean()
            .getValue(ImplementationPropertyNames.Task_duration);

        return new AbstractMap.SimpleEntry<>(death + duration, birth);
      }
    }

    // the vertex does not come from an edge nor from working memory.
    // nor from a fifo
    // Error
    throw new RuntimeException("Cannot get lifetime of a memory object " + "that is not derived from a scheduled DAG."
        + " (MemObject: " + vertex.toString() + ")");

  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.PropertySource#getPropertyBean()
   */
  @Override
  public PropertyBean getPropertyBean() {
    return this.properties;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.PropertySource#getPublicProperties()
   */
  @Override
  public List<String> getPublicProperties() {
    return MemoryExclusionGraph.public_properties;
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
    @SuppressWarnings("unchecked")
    final Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hosts = (Map<MemoryExclusionVertex,
        Set<MemoryExclusionVertex>>) getPropertyBean().getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);
    if (hosts != null) {
      hosts.forEach((host, hosted) -> {
        allVertices.addAll(hosted);
      });
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
    final Iterator<MemoryExclusionVertex> iter = vertexSet().iterator();
    while (iter.hasNext()) {
      final MemoryExclusionVertex vertex = iter.next();
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

  /*
   * (non-Javadoc)
   *
   * @see org.jgrapht.graph.AbstractBaseGraph#removeEdge(java.lang.Object)
   */
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

  /*
   * (non-Javadoc)
   *
   * @see org.jgrapht.graph.AbstractBaseGraph#removeEdge(java.lang.Object, java.lang.Object)
   */
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
   * This method remove node A from the graph if:<br>
   * - Node A and node B are <b>NOT</b> linked by an edge.<br>
   * - Nodes A and B have ALL their neighbors in common.<br>
   * - Nodes A has a lighter weight than node B.<br>
   * Applying this method to an exclusion graph before a MaximumWeightCliqueSolver is executed remove nodes that will
   * never be part of the maximum weight clique.<br>
   * <br>
   * This method also clears the adjacentverticesBackup lists.
   *
   * @deprecated Not used anywhere
   */
  @Deprecated
  public void removeLightestEquivalentNodes() {

    // Retrieve the list of nodes of the gaph
    final ArrayList<MemoryExclusionVertex> nodes = new ArrayList<>(vertexSet());

    // Sort it in descending order of weights
    Collections.sort(nodes, Collections.reverseOrder());

    final Set<MemoryExclusionVertex> fusionned = new LinkedHashSet<>();

    // Look for a pair of nodes with the properties exposed in method
    // comments
    for (final MemoryExclusionVertex node : nodes) {
      if (!fusionned.contains(node)) {
        final Set<MemoryExclusionVertex> nonAdjacentSet = new LinkedHashSet<>(vertexSet());
        nonAdjacentSet.removeAll(getAdjacentVertexOf(node));
        nonAdjacentSet.remove(node);

        for (final MemoryExclusionVertex notNeighbor : nonAdjacentSet) {
          if (getAdjacentVertexOf(notNeighbor).size() == getAdjacentVertexOf(node).size()) {
            if (getAdjacentVertexOf(notNeighbor).containsAll(getAdjacentVertexOf(node))) {

              // Keep only the one with the max weight
              fusionned.add(notNeighbor);

            }
          }
        }
        removeAllVertices(fusionned);
      }
    }
    this.adjacentVerticesBackup = new LinkedHashMap<>();
  }

  /**
   * Method used to update the exclusions between memory objects corresponding to Fifo heads and other memory objects of
   * the {@link MemoryExclusionGraph}. Exclusions will be removed from the exclusion graph, but no exclusions will be
   * added.
   *
   * @param inputDAG
   *          the input DAG
   */
  protected void updateFIFOMemObjectWithSchedule(final DirectedAcyclicGraph inputDAG) {

    // Create a DAG with new edges from scheduling info
    final DirectedAcyclicGraph scheduledDAG = (DirectedAcyclicGraph) inputDAG.copy();

    // Create an List of the DAGVertices, in scheduling order.
    final List<DAGVertex> verticesMap = new ArrayList<>();

    // Iterator on DAG vertices
    final ScheduledDAGIterator iterDAGVertices = new ScheduledDAGIterator(scheduledDAG);

    // Get vertices in scheduling order and remove send/receive vertices
    // from the dag.
    // Also identify the init vertices
    final Set<DAGVertex> removedVertices = new LinkedHashSet<>();
    final Set<DAGVertex> initVertices = new LinkedHashSet<>();

    while (iterDAGVertices.hasNext()) {
      final DAGVertex currentVertex = iterDAGVertices.next();

      final boolean isTask = currentVertex.getPropertyBean().getValue(ImplementationPropertyNames.Vertex_vertexType)
          .equals(VertexType.TASK);

      String vertKind = "";

      // Only task vertices have a kind
      if (isTask) {
        vertKind = currentVertex.getKind();
      }

      if (vertKind.equals(DAGVertex.DAG_VERTEX) || vertKind.equals(DAGBroadcastVertex.DAG_BROADCAST_VERTEX)
          || vertKind.equals(DAGInitVertex.DAG_INIT_VERTEX) || vertKind.equals(DAGEndVertex.DAG_END_VERTEX)
          || vertKind.equals(DAGForkVertex.DAG_FORK_VERTEX) || vertKind.equals(DAGJoinVertex.DAG_JOIN_VERTEX)) {
        verticesMap.add(currentVertex);

        if (vertKind.equals(DAGInitVertex.DAG_INIT_VERTEX)) {
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

    // This map is used along the scan of the vertex of the dag.
    // Its purpose is to store the last vertex scheduled on each
    // component. This way, when a new vertex is executed on this
    // instance is encountered, an edge can be added between it and
    // the previous one.
    Map<ComponentInstance, DAGVertex> lastVerticesScheduled;
    lastVerticesScheduled = new LinkedHashMap<>();

    // Scan the dag and add new precedence edges caused by the schedule
    final Set<DAGEdge> addedEdges = new LinkedHashSet<>();
    for (final DAGVertex currentVertex : verticesMap) {

      // Retrieve component
      final ComponentInstance comp = (ComponentInstance) currentVertex.getPropertyBean().getValue("Operator");

      // Retrieve last DAGVertex executed on this component
      final DAGVertex lastScheduled = lastVerticesScheduled.get(comp);

      // If this is not the first time this component is encountered
      if (lastScheduled != null) {

        // Add an edge between the last and the currend vertex
        // if there is not already one
        if (scheduledDAG.getEdge(lastScheduled, currentVertex) == null) {
          final DAGEdge newEdge = scheduledDAG.addEdge(lastScheduled, currentVertex);
          addedEdges.add(newEdge);
        }
      }
      // Save currentVertex as lastScheduled on this component
      lastVerticesScheduled.put(comp, currentVertex);
    }

    // Now, remove fifo exclusion
    for (final DAGVertex dagInitVertex : initVertices) {
      // Retrieve the corresponding EndVertex
      final String endReferenceName = (String) dagInitVertex.getPropertyBean().getValue(DAGInitVertex.END_REFERENCE);
      final DAGVertex dagEndVertex = scheduledDAG.getVertex(endReferenceName);

      // Compute the list of all edges between init and end
      Set<DAGEdge> edgesBetween;
      {
        final Set<DAGEdge> endPredecessors = scheduledDAG.getPredecessorEdgesOf(dagEndVertex);
        final Set<DAGEdge> initSuccessors = scheduledDAG.getSuccessorEdgesOf(dagInitVertex);
        edgesBetween = (new LinkedHashSet<>(initSuccessors));
        edgesBetween.retainAll(endPredecessors);
        edgesBetween.removeAll(addedEdges);
      }

      // Remove exclusions with all buffer in the list (if any)
      if (!edgesBetween.isEmpty()) {

        // retrieve the head MObj for current fifo
        // size does not matter ("that's what she said") to retrieve the
        // Memory object from the exclusion graph
        final MemoryExclusionVertex headMemoryNode = new MemoryExclusionVertex("FIFO_Head_" + dagEndVertex.getName(),
            dagInitVertex.getName(), 0);
        for (final DAGEdge edge : edgesBetween) {
          final MemoryExclusionVertex mObj = new MemoryExclusionVertex(edge);
          this.removeEdge(headMemoryNode, mObj);
        }
      }

      // Compute the list of all actors between init and end
      Set<DAGVertex> verticesBetween = null;
      {
        final Set<DAGVertex> endPredecessors = scheduledDAG.getPredecessorVerticesOf(dagEndVertex);
        final Set<DAGVertex> initSuccessors = scheduledDAG.getSuccessorVerticesOf(dagInitVertex);
        verticesBetween = (new LinkedHashSet<>(initSuccessors));
        verticesBetween.retainAll(endPredecessors);
      }

      // retrieve the head MObj for current fifo
      // size does not matter ("that's what she said") to retrieve the
      // Memory object from the exclusion graph
      final MemoryExclusionVertex headMemoryNode = new MemoryExclusionVertex("FIFO_Head_" + dagEndVertex.getName(),
          dagInitVertex.getName(), 0);
      for (final DAGVertex dagVertex : verticesBetween) {
        final MemoryExclusionVertex wMemoryObj = new MemoryExclusionVertex(dagVertex.getName(), dagVertex.getName(), 0);
        if (containsVertex(wMemoryObj)) {
          this.removeEdge(headMemoryNode, wMemoryObj);
        }
      }

    }
  }

  /**
   * This function update a {@link MemoryExclusionGraph MemEx} by taking timing information contained in a
   * {@link DirectedAcyclicGraph DAG} into account.
   *
   *
   * @param dag
   *          the {@link DirectedAcyclicGraph DAG} used which must be the one from which the {@link MemoryExclusionGraph
   *          MemEx} graph is {@link #buildGraph(DirectedAcyclicGraph) built} (will not be modified)
   */
  public void updateWithMemObjectLifetimes(final DirectedAcyclicGraph dag) {

    final Set<DefaultEdge> removedExclusions = new LinkedHashSet<>();

    // Scan the exclusions
    for (final DefaultEdge exclusion : edgeSet()) {
      final MemoryExclusionVertex memObject1 = getEdgeSource(exclusion);
      final Entry<Long, Long> obj1Lifetime = getLifeTime(memObject1, dag);

      final MemoryExclusionVertex memObject2 = getEdgeTarget(exclusion);
      final Entry<Long, Long> obj2Lifetime = getLifeTime(memObject2, dag);

      // If one of the lifetime is a fifo_body (no need to update,
      // exclusions will remain)
      if (((obj1Lifetime.getKey() == 0L) && (obj1Lifetime.getValue() == 0L))
          || ((obj2Lifetime.getKey() == 0L) && (obj2Lifetime.getValue() == 0L))) {
        continue;
      }

      // If the two objects are fifo heads: exclusion hold
      if ((obj1Lifetime.getKey() > obj1Lifetime.getValue()) && (obj2Lifetime.getKey() > obj2Lifetime.getValue())) {
        continue;
      }

      // If one objects is fifo heads
      if ((obj1Lifetime.getKey() > obj1Lifetime.getValue()) || (obj2Lifetime.getKey() > obj2Lifetime.getValue())) {
        if ((obj1Lifetime.getKey() > obj2Lifetime.getValue()) && (obj1Lifetime.getValue() < obj2Lifetime.getKey())) {
          // Remove the exclution
          removedExclusions.add(exclusion);
        }
        continue;
      }

      // If this code is reached, the two objects are buffers or working
      // memory
      // If the lifetimes do not overlap
      if (!((obj1Lifetime.getKey() < obj2Lifetime.getValue()) && (obj2Lifetime.getKey() < obj1Lifetime.getValue()))) {
        // Remove the exclution
        removedExclusions.add(exclusion);
      }
    }

    this.removeAllEdges(removedExclusions);
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

      final boolean isTask = currentVertex.getPropertyBean().getValue(ImplementationPropertyNames.Vertex_vertexType)
          .equals(VertexType.TASK);

      String vertKind = "";

      // Only task vertices have a kind
      if (isTask) {
        vertKind = currentVertex.getKind();
      }

      if (vertKind.equals(DAGVertex.DAG_VERTEX) || vertKind.equals(DAGBroadcastVertex.DAG_BROADCAST_VERTEX)
          || vertKind.equals(DAGInitVertex.DAG_INIT_VERTEX) || vertKind.equals(DAGEndVertex.DAG_END_VERTEX)
          || vertKind.equals(DAGForkVertex.DAG_FORK_VERTEX) || vertKind.equals(DAGJoinVertex.DAG_JOIN_VERTEX)) {
        final Integer schedulingOrder = (Integer) currentVertex.getPropertyBean()
            .getValue(ImplementationPropertyNames.Vertex_schedulingOrder);
        if (schedulingOrder == null) {
          throw new MemoryAllocationException("Cannot build the memory exclusion graph of a non scheduled DAG",
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
      final ComponentInstance comp = (ComponentInstance) currentVertex.getPropertyBean().getValue("Operator");

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

        // Re-create the working memory exclusion vertex (weight does
        // not matter to find the vertex in the Memex)
        final MemoryExclusionVertex wMemVertex = new MemoryExclusionVertex(vertexName, vertexName, 0);
        if (containsVertex(wMemVertex)) {
          for (final MemoryExclusionVertex newPredecessor : newPredecessors) {
            if (this.removeEdge(wMemVertex, newPredecessor) == null) {
              throw new RuntimeException("Missing edge");
            }
          }
        }

        // Remove exclusion between ExclusionVertices corresponding
        // to outgoing edges of the currentVertex, and ExclusionVertices
        // in newPredecessors list
        for (final DAGEdge outgoingEdge : currentVertex.outgoingEdges()) {
          if (outgoingEdge.getTarget().getPropertyBean().getValue(ImplementationPropertyNames.Vertex_vertexType)
              .equals(VertexType.TASK)) {
            final MemoryExclusionVertex edgeVertex = new MemoryExclusionVertex(outgoingEdge);
            for (final MemoryExclusionVertex newPredecessor : newPredecessors) {
              if (this.removeEdge(edgeVertex, newPredecessor) == null) {
                /**
                 * Possible causes are: <br>
                 * -edgeVertex or newPredecessor no longer are in the graph <br>
                 * -this.verticesPredecessors was corrupted before calling updateWithSchedule() <br>
                 * -The exclusion or one of the vertex could not be found because the MemoryExclusionVertex.equals()
                 * method is corrupted -Explode Implode were removed when creating the MemEx but not when updating it.
                 */
                throw new RuntimeException(
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
            // Add the working memory object to the
            // successorPredecessor list
            if (containsVertex(wMemVertex)) {
              successorPredecessor.add(wMemVertex);
            }
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
    final List<MemoryExclusionVertex> memExVertices = new ArrayList<>(vertexSet());
    /** Begin by putting all FIFO related Memory objects (if any) */
    for (final MemoryExclusionVertex vertex : vertexSet()) {
      if (vertex.getSource().startsWith("FIFO_Head_") || vertex.getSource().startsWith("FIFO_Body_")) {
        this.memExVerticesInSchedulingOrder.add(vertex);
      }
    }

    for (final DAGVertex vertex : dagVerticesInSchedulingOrder) {
      /** 1- Retrieve the Working Memory MemEx Vertex (if any) */
      {
        // Re-create the working memory exclusion vertex (weight does
        // not matter to find the vertex in the Memex)
        final String vertexName = vertex.getName();
        final MemoryExclusionVertex wMemVertex = new MemoryExclusionVertex(vertexName, vertexName, 0);
        int index;
        if ((index = memExVertices.indexOf(wMemVertex)) != -1) {
          // The working memory exists
          this.memExVerticesInSchedulingOrder.add(memExVertices.get(index));
        }
      }

      /** 2- Retrieve the MemEx Vertices of outgoing edges (if any) */
      {
        for (final DAGEdge outgoingEdge : vertex.outgoingEdges()) {
          if (outgoingEdge.getTarget().getPropertyBean().getValue(ImplementationPropertyNames.Vertex_vertexType)
              .equals(VertexType.TASK)) {
            final MemoryExclusionVertex edgeVertex = new MemoryExclusionVertex(outgoingEdge);
            int index;
            if ((index = memExVertices.indexOf(edgeVertex)) != -1) {
              // The working memory exists
              this.memExVerticesInSchedulingOrder.add(memExVertices.get(index));
            } else {
              throw new RuntimeException("Missing MemEx Vertex: " + edgeVertex);
            }
          }
        }
      }
    }
  }
}
