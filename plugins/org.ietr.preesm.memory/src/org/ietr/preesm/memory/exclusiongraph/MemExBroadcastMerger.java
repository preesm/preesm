/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
 * Julien Hascoet <jhascoet@kalray.eu> (2017)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013 - 2015)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2015)
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
package org.ietr.preesm.memory.exclusiongraph;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.ietr.dftools.algorithm.iterators.DAGIterator;
import org.ietr.dftools.algorithm.model.AbstractEdge;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.algorithm.model.dag.edag.DAGBroadcastVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGForkVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFBroadcastVertex;
import org.ietr.dftools.algorithm.model.sdf.types.SDFStringEdgePropertyType;
import org.ietr.preesm.memory.script.MemoryScriptEngine;

// TODO: Auto-generated Javadoc
/**
 * The purpose of this class is to process a {@link MemoryExclusionGraph MemEx} in order to: <br>
 * - Merge the {@link MemoryExclusionVertex memory objects} corresponding to input/output edges of a {@link SDFBroadcastVertex} on condition that the output
 * edges are only read by their target {@link SDFAbstractVertex}.<br>
 * - Bring back (unmerge) the memory objects after a memory allocation of the {@link MemoryExclusionGraph MemEx} has been performed.
 *
 * @author kdesnos
 *
 * @deprecated Replaced with {@link MemoryScriptEngine} since november 2014
 *
 */
@Deprecated
public class MemExBroadcastMerger {

  /**
   * Property used to mark a {@link DAGEdge} as merged in the input {@link DirectedAcyclicGraph}.
   */
  public static final String MERGED_OBJECT_PROPERTY = "merged_object";

  /**
   * The {@link MemoryExclusionGraph} processed by the current instance.
   */
  private final MemoryExclusionGraph memEx;

  /**
   * The {@link DirectedAcyclicGraph} from which the {@link #memEx} was derived.
   */
  private final DirectedAcyclicGraph dag;

  /**
   * This {@link Map} associates a {@link MemoryExclusionVertex memory object} to {@link Set} of {@link MemoryExclusionVertex memory objects} that were merged
   * during a call to {@link #merge()}.
   */
  private final Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> mergedObjects;

  /**
   * Constructor of the {@link MemExBroadcastMerger}.
   *
   * @param memEx
   *          The {@link MemoryExclusionGraph} processed by the current instance.
   */
  public MemExBroadcastMerger(final MemoryExclusionGraph memEx) {
    this.memEx = memEx;
    this.dag = memEx.getPropertyBean().getValue(MemoryExclusionGraph.SOURCE_DAG, DirectedAcyclicGraph.class);
    this.mergedObjects = new LinkedHashMap<>();
  }

  /**
   * Merge the {@link MemoryExclusionVertex memory objects} corresponding to input/output edges of a {@link SDFBroadcastVertex} on condition that the output
   * edges are only read by their target {@link SDFAbstractVertex}.
   *
   * @return the int
   */
  public int merge() {
    // Create a property in the memex to store a list of merged vertices
    // This list is used by some allocator (eg. scheduling order alloc).
    this.memEx.setPropertyValue(MemExBroadcastMerger.MERGED_OBJECT_PROPERTY, new LinkedHashSet<MemoryExclusionVertex>());

    // Retrieve the dag vertices in scheduling order.
    final LinkedHashSet<DAGVertex> dagVertices = new LinkedHashSet<>(this.dag.vertexSet().size());
    { // Be careful, DAGiterator does not seem to work well if dag is
      // modified throughout the iteration.
      // That's why we use first copy the ordered dag vertex set.
      final DAGIterator iterDAGVertices = new DAGIterator(this.dag);
      while (iterDAGVertices.hasNext()) {
        dagVertices.add(iterDAGVertices.next());
      }
    }

    // Scan the dag vertices to find the broadcasts
    for (final DAGVertex vert : dagVertices) {
      // The dag is the one used during the build, so only task vertices
      // with a kind remain in this graph. (i.e. vertKind will never be
      // null.)
      final String vertKind = vert.getPropertyBean().getValue("kind").toString();
      // Process broadcast vertex only (not roundbuffers)
      // The vertex must have the broadcast kind and only one
      // input edge.
      if (vertKind.equals(DAGBroadcastVertex.DAG_BROADCAST_VERTEX) && (this.dag.inDegreeOf(vert) == 1)) {
        mergeBroadcast(vert);
      } else if (vertKind.equals(DAGBroadcastVertex.DAG_BROADCAST_VERTEX) && (this.dag.outDegreeOf(vert) == 1)) {
        // Process roundbuffer vertex only (not broadcasts)
        // The vertex must have the broadcast kind and only one
        // input edge.
        mergeRoundBuffer(vert);
      }

    }

    return this.mergedObjects.size();
  }

  /**
   * This method is responsible for merging the {@link MemoryExclusionVertex memory objects} corresponding to the {@link DAGEdge}s of a broadcast
   * {@link DAGVertex}.
   *
   * @param vert
   *          the Broadcast {@link DAGVertex} whose {@link DAGEdge}s are merged (if possible)
   */
  @SuppressWarnings("unchecked")
  private void mergeBroadcast(final DAGVertex vert) {
    final DAGEdge incomingEdge = vert.incomingEdges().iterator().next();
    MemoryExclusionVertex inMemObject = new MemoryExclusionVertex(incomingEdge);
    final Set<DAGEdge> outgoingEdges = vert.outgoingEdges();

    // Check if the inMemObject was merged
    boolean mergedInMemObject = false;
    final Set<MemoryExclusionVertex> allMergedObjects = (Set<MemoryExclusionVertex>) this.memEx.getPropertyBean()
        .getValue(MemExBroadcastMerger.MERGED_OBJECT_PROPERTY);
    if (allMergedObjects.contains(inMemObject)) {

      mergedInMemObject = true;
      // In such case replace the object by its merged object
      for (final Entry<MemoryExclusionVertex, ?> entry : this.mergedObjects.entrySet()) {
        if (((Set<MemoryExclusionVertex>) entry.getValue()).contains(inMemObject)) {
          inMemObject = entry.getKey();
          break;
        }
      }
    }

    // In the current version we ONLY check if ALL outgoing edges
    // have the same rate as the input edge, and merge those with a
    // "const" input
    final Set<MemoryExclusionVertex> mergeableMemObjects = new LinkedHashSet<>();
    for (final DAGEdge edge : outgoingEdges) {
      final MemoryExclusionVertex outMemObject = new MemoryExclusionVertex(edge);
      // Check the weight equality.
      if (!outMemObject.getWeight().equals(inMemObject.getWeight())) {
        // There is a memobject with a different size. In the
        // current version
        // we do not consider this case.
        mergeableMemObjects.clear();
        System.out.println("Broadcast " + vert + " was not merged because all its" + " output do not have a size equal to its input.");
        break;
      }

      // If the target is only reads the token, this MemObject is
      // mergeable.
      boolean readOnly = true;
      for (final AbstractEdge<?, ?> aggrEdge : edge.getAggregate()) {
        final SDFStringEdgePropertyType modifier = ((DAGEdge) aggrEdge).getTargetPortModifier();
        // Check that all aggregated edges have the adequate
        // modifier
        if ((modifier == null) || !modifier.toString().contains(SDFEdge.MODIFIER_READ_ONLY)) {
          readOnly = false;
          break;
        }
      }

      // If the test was successful, add the memory object to the
      // list of mergeable objects.
      if (readOnly) {
        mergeableMemObjects.add(outMemObject);
      }
    }

    // Merge the mergeable memory objects with the input.
    // i.e. "Merge" process:
    // - Add to the inputMemObj all the exclusions of the merged
    // MemObj
    // - Remove the merged MemObjects from the graph
    // - Update the merge object property of the MemEx
    // - Backup the list of merged objects (for unmerge)
    if (!mergeableMemObjects.isEmpty()) {

      for (final MemoryExclusionVertex memObj : mergeableMemObjects) {

        // Add exclusion
        final Set<MemoryExclusionVertex> neighbors = this.memEx.getAdjacentVertexOf(memObj);
        for (final MemoryExclusionVertex neighbor : neighbors) {
          if (!neighbor.equals(inMemObject)) {
            this.memEx.addEdge(inMemObject, neighbor);

          }
        }

        // Remove mergeMemObject
        this.memEx.removeVertex(memObj);
        this.memEx.clearAdjacentVerticesBackup();

        // Mark the corresponding edge
        allMergedObjects.add(memObj);
      }

      // Backup the list of merged vertices for unmerge
      if (!mergedInMemObject) {
        this.mergedObjects.put(inMemObject, mergeableMemObjects);
      } else {
        this.mergedObjects.get(inMemObject).addAll(mergeableMemObjects);
      }
    }
  }

  /**
   * This method is responsible for merging the {@link MemoryExclusionVertex memory objects} corresponding to the {@link DAGEdge}s of a roundbuffer
   * {@link DAGVertex}.
   *
   * @param vert
   *          the roundbuffer {@link DAGVertex} whose {@link DAGEdge}s are merged (if possible)
   */
  @SuppressWarnings("unchecked")
  private void mergeRoundBuffer(final DAGVertex vert) {
    final DAGEdge outgoingEdge = vert.outgoingEdges().iterator().next();
    final MemoryExclusionVertex outMemObject = new MemoryExclusionVertex(outgoingEdge);
    final Set<DAGEdge> incomingEdges = vert.incomingEdges();

    // Retrieve the last memobject
    final SDFAbstractVertex sdfVertex = vert.getPropertyBean().getValue(DAGVertex.SDF_VERTEX, SDFAbstractVertex.class);
    final Map<Integer, SDFEdge> orderedEdges = (Map<Integer, SDFEdge>) sdfVertex.getPropertyBean().getValue(DAGForkVertex.EDGES_ORDER);
    final SDFEdge lastEdge = orderedEdges.get(Collections.max(orderedEdges.keySet()));
    final DAGEdge lastDagEdge = this.dag.getEdge(this.dag.getVertex(lastEdge.getSource().getName()), this.dag.getVertex(lastEdge.getTarget().getName()));
    MemoryExclusionVertex lastMemObject = new MemoryExclusionVertex(lastDagEdge);

    // In the current version we ONLY check if ALL incoming edges
    // have the same rate as the output edge, and merge those with a
    // write_only producer (except the last one which will be merged
    // with the input, regardless from its modifiers)
    final Set<MemoryExclusionVertex> mergeableMemObjects = new LinkedHashSet<>();
    final Set<MemoryExclusionVertex> allMergedObjects = (Set<MemoryExclusionVertex>) this.memEx.getPropertyBean()
        .getValue(MemExBroadcastMerger.MERGED_OBJECT_PROPERTY);
    for (final DAGEdge edge : incomingEdges) {
      final MemoryExclusionVertex inMemObject = new MemoryExclusionVertex(edge);
      // Check the weight equality.
      if (!inMemObject.getWeight().equals(outMemObject.getWeight())) {
        // There is a memobject with a different size. In the
        // current version
        // we do not consider this case.
        mergeableMemObjects.clear();
        System.out.println("Roundbuffer " + vert + " was not merged because all its" + " input do not have a size equal to its input.");
        break;
      }

      // If the source is only writes the token, this MemObject is
      // mergeable.
      boolean writeOnly = true;
      for (final AbstractEdge<?, ?> aggrEdge : edge.getAggregate()) {
        final SDFStringEdgePropertyType modifier = ((DAGEdge) aggrEdge).getSourcePortModifier();
        // Check that all aggregated edges have the adequate
        // modifier
        if ((modifier == null) || !modifier.toString().contains(SDFEdge.MODIFIER_WRITE_ONLY)) {
          writeOnly = false;
          break;
        }
      }

      // If the test was successful, add the memory object to the
      // list of mergeable objects.
      if (writeOnly) {
        // Also check if the inMemObject was not already merged
        // (except for the last)
        if (!allMergedObjects.contains(inMemObject) || inMemObject.equals(lastMemObject)) {
          mergeableMemObjects.add(inMemObject);
        } else {
          // The input was already merged. Priority is given
          // to the previous merge.
          System.out.println("Roundbuffer input " + edge + " not merged because it has already been merged.");
        }
      }
    }

    // If the last object is mergeable, merge the last object with the
    // output (even if it has be merged before)

    if (mergeableMemObjects.contains(lastMemObject)) {
      mergeableMemObjects.remove(lastMemObject);
      // Check if the inMemObject was merged
      boolean mergedLastMemObject = false;

      if (allMergedObjects.contains(lastMemObject)) {
        mergedLastMemObject = true;

        // In such case replace the object by its merged object
        for (final Entry<MemoryExclusionVertex, ?> entry : this.mergedObjects.entrySet()) {
          if (((Set<MemoryExclusionVertex>) entry.getValue()).contains(lastMemObject)) {
            lastMemObject = entry.getKey();
            break;
          }
        }
      }

      // Add exclusion
      final Set<MemoryExclusionVertex> neighbors = this.memEx.getAdjacentVertexOf(outMemObject);
      for (final MemoryExclusionVertex neighbor : neighbors) {
        if (!neighbor.equals(lastMemObject)) {
          this.memEx.addEdge(lastMemObject, neighbor);
        }
      }

      // Remove mergeMemObject
      this.memEx.removeVertex(outMemObject);
      this.memEx.clearAdjacentVerticesBackup();

      // Mark the corresponding edge
      allMergedObjects.add(outMemObject);

      // Backup the list of merged vertices for unmerge
      if (!mergedLastMemObject) {
        final Set<MemoryExclusionVertex> outSet = new LinkedHashSet<>();
        outSet.add(outMemObject);
        this.mergedObjects.put(lastMemObject, outSet);
      } else {
        this.mergedObjects.get(lastMemObject).add(outMemObject);
      }
    }

    // Merge the mergeable memory objects together except the "last"
    // one (the one giving the actual output of the merge).
    // i.e. "Merge" process:
    // - Add to the outputMemObj all the exclusions of the merged
    // MemObj
    // - Remove the merged MemObjects from the MemEx
    // - Update the merge object property of the MemEx
    // - Backup the list of merged objects (for unmerge)
    if (mergeableMemObjects.size() > 1) {

      final Iterator<MemoryExclusionVertex> iter = mergeableMemObjects.iterator();
      final MemoryExclusionVertex mergedObject = iter.next();
      iter.remove();

      while (iter.hasNext()) {
        final MemoryExclusionVertex memObj = iter.next();
        // Add exclusion
        final Set<MemoryExclusionVertex> neighbors = this.memEx.getAdjacentVertexOf(memObj);
        for (final MemoryExclusionVertex neighbor : neighbors) {
          if (!neighbor.equals(mergedObject)) {
            this.memEx.addEdge(mergedObject, neighbor);
          }
        }

        // Remove mergeMemObject
        this.memEx.removeVertex(memObj);
        this.memEx.clearAdjacentVerticesBackup();

        // Mark the corresponding edge
        allMergedObjects.add(memObj);
      }

      // Backup the list of merged vertices for unmerge
      this.mergedObjects.put(mergedObject, mergeableMemObjects);
    }
  }

  /**
   * Bring back (unmerge) the memory objects after a memory allocation of the {@link MemoryExclusionGraph MemEx} has been performed.
   */
  @SuppressWarnings("unchecked")
  public void unmerge() {
    // Get the edgeAllocation
    final Map<DAGEdge, Integer> edgeAllocation = (Map<DAGEdge, Integer>) this.memEx.getPropertyBean().getValue(MemoryExclusionGraph.DAG_EDGE_ALLOCATION);

    // Unmerge the memory objects one by one
    for (final Entry<MemoryExclusionVertex, Set<MemoryExclusionVertex>> entry : this.mergedObjects.entrySet()) {
      // Get the unmerged object and it allocation.
      MemoryExclusionVertex unmergedObject = entry.getKey();
      unmergedObject = this.memEx.getVertex(unmergedObject);
      final Integer offset = unmergedObject.getPropertyBean().getValue(MemoryExclusionVertex.MEMORY_OFFSET_PROPERTY, Integer.class);

      // Scan the unmerged objects
      final Set<MemoryExclusionVertex> unmergedObjects = entry.getValue();

      for (final MemoryExclusionVertex memObject : unmergedObjects) {
        // Put the object back in the MemEx
        this.memEx.addVertex(memObject);

        // Allocate the object at the same place as the unmerged object.
        // No need to update the total amount of allocated memory since
        // unmerging does not have any effect on it.
        edgeAllocation.put(memObject.getEdge(), offset);
        memObject.setPropertyValue(MemoryExclusionVertex.MEMORY_OFFSET_PROPERTY, offset);

        // Add all exclusions of the unmerged object to the object
        for (final MemoryExclusionVertex neighbor : this.memEx.getAdjacentVertexOf(unmergedObject)) {
          this.memEx.addEdge(memObject, neighbor);
        }
        this.memEx.clearAdjacentVerticesBackup();

        // We do not add an exclusion between unmergedObjects so that
        // the allocation
        // remains valid according to the exclusion present in the
        // graph.
        // However, if someone were to perform an allocation with the
        // memEx outputed by this method, the absence of exclusion
        // between the unmerged objects may be a issue (since the
        // objects might be allocated in overlapping but not
        // superimposed objects.
      }
    }

    // Clear the merged objects list
    this.mergedObjects.clear();

    // Empty the Merged list in the memex
    this.memEx.getPropertyBean().removeProperty(MemExBroadcastMerger.MERGED_OBJECT_PROPERTY);
  }
}
