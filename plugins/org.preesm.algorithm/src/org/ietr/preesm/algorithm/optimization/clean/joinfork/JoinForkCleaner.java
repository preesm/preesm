/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Hamza Deroui <hamza.deroui@insa-rennes.fr> (2016)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2016)
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
package org.ietr.preesm.algorithm.optimization.clean.joinfork;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.preesm.algorithm.model.parameters.InvalidExpressionException;
import org.preesm.algorithm.model.sdf.SDFAbstractVertex;
import org.preesm.algorithm.model.sdf.SDFEdge;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.model.sdf.SDFInterfaceVertex;
import org.preesm.algorithm.model.sdf.SDFVertex;
import org.preesm.algorithm.model.sdf.esdf.SDFBroadcastVertex;
import org.preesm.algorithm.model.sdf.esdf.SDFForkVertex;
import org.preesm.algorithm.model.sdf.esdf.SDFJoinVertex;
import org.preesm.algorithm.model.sdf.esdf.SDFRoundBufferVertex;
import org.preesm.algorithm.model.sdf.transformations.SpecialActorPortsIndexer;
import org.preesm.algorithm.model.sdf.visitors.SingleRateChecker;
import org.preesm.algorithm.model.types.LongEdgePropertyType;
import org.preesm.algorithm.model.types.StringEdgePropertyType;
import org.preesm.algorithm.model.visitors.SDF4JException;
import org.preesm.workflow.WorkflowException;

/**
 * Class cleaning the useless join-fork pairs of vertices which may have been introduced by hierarchy flattening and
 * single rate transformation.
 *
 * @author cguy
 * @author kdesnos
 */
public class JoinForkCleaner {

  private JoinForkCleaner() {
    // forbid instantiation
  }

  /**
   * Top method to call in order to remove all the join-fork pairs which can be removed safely from an SDFGraph.<br>
   * <br>
   *
   * <b>This algorithm should be called only on single-rate graphs. Verification is performed.</b>
   *
   * @param graph
   *          the SDFGraph we want to clean
   * @return true if some join-fork pairs has be removed
   * @throws InvalidExpressionException
   *           the invalid expression exception
   * @throws SDF4JException
   *           the SDF 4 J exception
   */
  public static boolean cleanJoinForkPairsFrom(final SDFGraph graph) throws SDF4JException {
    boolean result = false;

    // Check that the graph is single rate.
    final SingleRateChecker srChecker = new SingleRateChecker();
    graph.accept(srChecker);
    if (!srChecker.isSingleRate()) {
      throw new SDF4JException("Cannot clean fork/join pairs in a non-single-rate graph.");
    }

    // Set of edges to remove from graph
    SDFEdge edgeToRemove;

    // Set of vertices to remove from graph
    Set<SDFAbstractVertex> verticesToRemove;

    boolean changeDone;
    do {
      // reset
      edgeToRemove = null;
      verticesToRemove = new LinkedHashSet<>();

      // Scan every edge e of the graph
      // Stop as soon as there is an edge between a join and a fork
      for (final SDFEdge e : graph.edgeSet()) {
        // Check whether it stands between an SDFJoinVertex and an
        // SDFForkVertex and whether it's safe to remove it
        if (JoinForkCleaner.isBetweenJoinAndFork(e)) {
          // If it is the case, add e, its source and its target to
          // the
          // elements to remove from graph
          edgeToRemove = e;
          verticesToRemove.add(e.getSource());
          verticesToRemove.add(e.getTarget());
          break;
        }
      }

      result |= changeDone = edgeToRemove != null;

      // if any, replace edgeToRemove
      if (edgeToRemove != null) {
        // Use the single-rate transformation to replace the edge.
        JoinForkCleaner.replaceEdge(edgeToRemove, graph);

        // Finally, remove all useless elements from graph
        graph.removeEdge(edgeToRemove);
        graph.removeAllVertices(verticesToRemove);
      }
    } while (changeDone);

    return result;
  }

  /**
   * Replace an {@link SDFEdge} between a {@link SDFJoinVertex} and an {@link SDFForkVertex} of a single-rate
   * {@link SDFGraph} with equivalent connections so that the two {@link SDFForkVertex} and {@link SDFJoinVertex}
   * vertices can be removed from the graph.
   *
   * <b>The code of this method was strongly inspired by the HSDF transformation, if bugs are found here, it is likely
   * they exist also there</b> (sorry for the poor code design).
   *
   * @param replacedEdge
   *          the {@link SDFEdge} to replace.
   * @param graph
   *          the processed single-rate {@link SDFGraph}.
   * @throws InvalidExpressionException
   *           if some expressions associated to data ports or delays are invalid.
   */
  private static void replaceEdge(final SDFEdge replacedEdge, final SDFGraph graph) {

    // Retrieve the sources and targets actor to connect, as well as all
    // edges replaced b this algorithm.
    final List<SDFAbstractVertex> sourceCopies = new ArrayList<>();
    final List<SDFEdge> sourceEdges = new ArrayList<>();
    // retrieve connection in the right order
    for (final SDFEdge inEdge : ((SDFJoinVertex) replacedEdge.getSource()).getIncomingConnections()) {
      sourceCopies.add(inEdge.getSource());
      sourceEdges.add(inEdge);
    }

    final List<SDFAbstractVertex> targetCopies = new ArrayList<>();
    final List<SDFEdge> targetEdges = new ArrayList<>();
    // retrieve connection in the right order
    for (final SDFEdge outEdge : ((SDFForkVertex) replacedEdge.getTarget()).getOutgoingConnections()) {
      targetCopies.add(outEdge.getTarget());
      targetEdges.add(outEdge);
    }

    final List<SDFAbstractVertex> originalSourceCopies = new ArrayList<>(sourceCopies);
    final List<SDFAbstractVertex> originalTargetCopies = new ArrayList<>(targetCopies);

    // Delays of the edge between the fork and the join
    long nbDelays = replacedEdge.getDelay().longValue();

    // Total number of token exchanged (produced and consumed) for this edge
    final long totalNbTokens = replacedEdge.getCons().longValue();

    // Absolute target is the targeted consumed token among the total
    // number of consumed/produced tokens
    long absoluteTarget = nbDelays;
    long absoluteSource = 0;

    // totProd is updated to store the number of token consumed by the
    // targets that are "satisfied" by the added edges.
    long totProd = 0;

    final List<SDFEdge> newEdges = new ArrayList<>();
    // Add edges until all consumed token are "satisfied"
    while (totProd < totalNbTokens) {

      // sourceProd and targetCons are the number of token already
      // produced/consumed by the currently indexed source/target
      long sourceProd = 0;
      long targetCons = 0;
      // Index of the currently processed sourceVertex among the
      // duplicates of the current edge source.
      int sourceIndex = 0;
      long producedTokens = 0;
      while (producedTokens < absoluteSource) {
        producedTokens += sourceEdges.get(sourceIndex).getProd().longValue();
        sourceIndex++; // no need of modulo, producers are scanned
        // once.
      }
      if (producedTokens > absoluteSource) {
        sourceIndex--; // no need of modulo, producers are scanned
        // once.
        producedTokens -= sourceEdges.get(sourceIndex).getProd().longValue();
        sourceProd = absoluteSource - producedTokens;
      }

      // targetIndex is used to know which duplicates of the target
      // will
      // be targeted by the currently indexed copy of the source.
      int targetIndex = 0;
      long consumedTokens = 0;
      while (consumedTokens < absoluteTarget) {
        consumedTokens += targetEdges.get(targetIndex).getCons().longValue();
        targetIndex = (targetIndex + 1) % targetEdges.size();
      }
      if (consumedTokens > absoluteTarget) {
        targetIndex = ((targetIndex - 1) + targetEdges.size()) % targetEdges.size(); // modulo
        // because
        // of
        // delays
        consumedTokens -= targetEdges.get(targetIndex).getCons().longValue();
        targetCons = absoluteTarget - consumedTokens;
      }

      // rest is both the production and consumption rate on the
      // created edge.
      final long rest = Math.min(sourceEdges.get(sourceIndex).getProd().longValue() - sourceProd,
          targetEdges.get(targetIndex).getCons().longValue() - targetCons);

      // This int represent the number of iteration separating the
      // currently indexed source and target (between which an edge is
      // added)
      // If this int is > to 0, this means that the added edge must
      // have
      // delays (with delay=prod=cons of the added edge)
      // Warning, this integer division is not factorable
      final long iterationDiff = (absoluteTarget / totalNbTokens) - (absoluteSource / totalNbTokens);

      // Testing zone beginning
      // for inserting explode and implode vertices
      // boolean set to true if an explode should be added
      final boolean explode = rest < sourceEdges.get(sourceIndex).getProd().longValue();
      final boolean implode = rest < targetEdges.get(targetIndex).getCons().longValue();
      if (explode && !(sourceCopies.get(sourceIndex) instanceof SDFForkVertex)
          && (!(sourceCopies.get(sourceIndex) instanceof SDFBroadcastVertex)
              || (sourceCopies.get(sourceIndex) instanceof SDFRoundBufferVertex))) {

        // If an explode must be added
        final SDFAbstractVertex explodeVertex = new SDFForkVertex();
        graph.addVertex(explodeVertex);
        final SDFAbstractVertex originVertex = sourceCopies.get(sourceIndex);
        explodeVertex.setName(
            "explode_" + originVertex.getName() + "_" + sourceEdges.get(sourceIndex).getSourceInterface().getName());

        // Replace the source vertex by the explode in the
        // sourceCopies list
        sourceCopies.set(sourceIndex, explodeVertex);

        // Add an edge between the source and the explode
        final SDFEdge newEdge = graph.addEdge(originVertex, explodeVertex);
        newEdge.setDelay(new LongEdgePropertyType(0));
        newEdge.setProd(new LongEdgePropertyType(sourceEdges.get(sourceIndex).getProd().longValue()));
        newEdge.setCons(new LongEdgePropertyType(sourceEdges.get(sourceIndex).getProd().longValue()));
        newEdge.setDataType(sourceEdges.get(sourceIndex).getDataType());
        newEdge.setSourceInterface(sourceEdges.get(sourceIndex).getSourceInterface());
        explodeVertex.addInterface(sourceEdges.get(sourceIndex).getTargetInterface());
        newEdge.setTargetInterface(sourceEdges.get(sourceIndex).getTargetInterface());
        newEdge.setSourcePortModifier(sourceEdges.get(sourceIndex).getSourcePortModifier());

        // Add a target port modifier to the edge
        newEdge.setTargetPortModifier(new StringEdgePropertyType(SDFEdge.MODIFIER_READ_ONLY));
      }
      if (implode && !(targetCopies.get(targetIndex) instanceof SDFJoinVertex)
          && !(targetCopies.get(targetIndex) instanceof SDFRoundBufferVertex)) {
        // If an implode must be added
        final SDFAbstractVertex implodeVertex = new SDFJoinVertex();
        graph.addVertex(implodeVertex);
        final SDFAbstractVertex originVertex = targetCopies.get(targetIndex);
        implodeVertex.setName(
            "implode_" + originVertex.getName() + "_" + targetEdges.get(targetIndex).getTargetInterface().getName());

        // Replace the target vertex by the implode one in the
        // targetCopies List
        targetCopies.set(targetIndex, implodeVertex);

        // Add an edge between the implode and the target
        final SDFEdge newEdge = graph.addEdge(implodeVertex, originVertex);
        newEdge.setDelay(new LongEdgePropertyType(0));
        newEdge.setProd(new LongEdgePropertyType(targetEdges.get(targetIndex).getCons().longValue()));
        newEdge.setCons(new LongEdgePropertyType(targetEdges.get(targetIndex).getCons().longValue()));
        newEdge.setDataType(targetEdges.get(targetIndex).getDataType());
        implodeVertex.addInterface(targetEdges.get(targetIndex).getTargetInterface());
        newEdge.setSourceInterface(targetEdges.get(targetIndex).getSourceInterface());
        newEdge.setTargetInterface(targetEdges.get(targetIndex).getTargetInterface());
        newEdge.setTargetPortModifier(targetEdges.get(targetIndex).getTargetPortModifier());

        // Add a source port modifier to the edge
        newEdge.setSourcePortModifier(new StringEdgePropertyType(SDFEdge.MODIFIER_WRITE_ONLY));
      }
      // end of testing zone

      // Create the new Edge for the output graph
      final SDFEdge newEdge = graph.addEdge(sourceCopies.get(sourceIndex), targetCopies.get(targetIndex));
      newEdges.add(newEdge);

      // Set the source interface of the new edge
      // If the source is a newly added fork/broadcast (or extra
      // output added to existing fork/broadcast) we rename the
      // new output ports. Contrary to ports of join/roundbuffer, no
      // special processing is needed to order the edges.
      if ((sourceCopies.get(sourceIndex) == originalSourceCopies.get(sourceIndex))
          && (!explode || !((originalSourceCopies.get(sourceIndex) instanceof SDFBroadcastVertex)
              || (originalSourceCopies.get(sourceIndex) instanceof SDFForkVertex)))) {
        // If the source does not need new ports
        if (sourceCopies.get(sourceIndex)
            .getSink(sourceEdges.get(sourceIndex).getSourceInterface().getName()) != null) {
          // if the source already has the appropriate interface
          newEdge.setSourceInterface(
              sourceCopies.get(sourceIndex).getSink(sourceEdges.get(sourceIndex).getSourceInterface().getName()));
        } else {
          // if the source does not have the interface.
          newEdge.setSourceInterface(sourceEdges.get(sourceIndex).getSourceInterface().copy());
          sourceCopies.get(sourceIndex).addInterface(newEdge.getSourceInterface());
        }
        // Copy the source port modifier of the original source
        newEdge.setSourcePortModifier(sourceEdges.get(sourceIndex).getSourcePortModifier());
      } else {
        // If the source is a fork (new or not)
        // or a broadcast with a new port
        final SDFInterfaceVertex sourceInterface = sourceEdges.get(sourceIndex).getSourceInterface().copy();

        String newInterfaceName = sourceInterface.getName() + "_" + sourceProd;

        // Get the current index of the port (if any)
        // and update it
        if (sourceInterface.getName().matches(SpecialActorPortsIndexer.INDEX_REGEX)) {
          final Pattern pattern = Pattern.compile(SpecialActorPortsIndexer.INDEX_REGEX);
          final Matcher matcher = pattern.matcher(sourceInterface.getName());
          matcher.find();
          final int existingIdx = Integer.decode(matcher.group(SpecialActorPortsIndexer.GROUP_XX));
          final long newIdx = existingIdx + sourceProd;
          newInterfaceName = sourceInterface.getName().substring(0, matcher.start(SpecialActorPortsIndexer.GROUP_XX))
              + newIdx;
        }

        sourceInterface.setName(newInterfaceName);
        newEdge.setSourceInterface(sourceInterface);
        newEdge.getSource().addInterface(sourceInterface);
        // Add a source port modifier
        newEdge.setSourcePortModifier(new StringEdgePropertyType(SDFEdge.MODIFIER_WRITE_ONLY));
      }

      // Set the target interface of the new edge
      // If the target is a newly added join/roundbuffer
      // we need to take extra care to make sure the incoming edges
      // are in the right order (which might be a little bit complex
      // when playing with delays)

      // If the target is not an actor with new ports (because of an
      // explosion)
      if ((targetCopies.get(targetIndex) == originalTargetCopies.get(targetIndex))
          && (!implode || !((originalTargetCopies.get(targetIndex) instanceof SDFRoundBufferVertex)
              || (originalTargetCopies.get(targetIndex) instanceof SDFJoinVertex)))) {

        // if the target already has the appropriate interface
        if (targetCopies.get(targetIndex)
            .getSource(targetEdges.get(targetIndex).getTargetInterface().getName()) != null) {

          newEdge.setTargetInterface(
              targetCopies.get(targetIndex).getSource(targetEdges.get(targetIndex).getTargetInterface().getName()));
        } else {
          // if the target does not have the interface.
          newEdge.setTargetInterface(targetEdges.get(targetIndex).getTargetInterface().copy());
          targetCopies.get(targetIndex).addInterface(newEdge.getTargetInterface());
        }
        // Copy the target port modifier of the original source
        // Except for roundbuffers
        if (!(newEdge.getTarget() instanceof SDFRoundBufferVertex)) {
          newEdge.setTargetPortModifier(targetEdges.get(targetIndex).getTargetPortModifier());
        } else {
          // The processing of roundBuffer portModifiers is done
          // after the while loop
        }
      } else {
        // If the target is join (new or not) /roundbuffer with new
        // ports
        final SDFInterfaceVertex targetInterface = targetEdges.get(targetIndex).getTargetInterface().copy();

        String newInterfaceName = targetInterface.getName() + "_" + targetCons;
        // Get the current index of the port (if any)
        // and update it
        if (targetInterface.getName().matches(SpecialActorPortsIndexer.INDEX_REGEX)) {
          final Pattern pattern = Pattern.compile(SpecialActorPortsIndexer.INDEX_REGEX);
          final Matcher matcher = pattern.matcher(targetInterface.getName());
          matcher.find();
          final long existingIdx = Long.decode(matcher.group(SpecialActorPortsIndexer.GROUP_XX));
          final long newIdx = existingIdx + targetCons;
          newInterfaceName = targetInterface.getName().substring(0, matcher.start(SpecialActorPortsIndexer.GROUP_XX))
              + newIdx;
        }

        targetInterface.setName(newInterfaceName);
        newEdge.setTargetInterface(targetInterface);
        newEdge.getTarget().addInterface(targetInterface);
        // Add a target port modifier
        newEdge.setTargetPortModifier(new StringEdgePropertyType(SDFEdge.MODIFIER_READ_ONLY));

      }

      // Associate the interfaces to the new edge
      if (targetCopies.get(targetIndex) instanceof SDFVertex) {
        if (((SDFVertex) targetCopies.get(targetIndex))
            .getSource(targetEdges.get(targetIndex).getTargetInterface().getName()) != null) {
          final SDFInterfaceVertex inputVertex = ((SDFVertex) targetCopies.get(targetIndex))
              .getSource(targetEdges.get(targetIndex).getTargetInterface().getName());
          ((SDFVertex) targetCopies.get(targetIndex)).setInterfaceVertexExternalLink(newEdge, inputVertex);
        }
      }
      if (sourceCopies.get(sourceIndex) instanceof SDFVertex) {
        if (((SDFVertex) sourceCopies.get(sourceIndex))
            .getSink(sourceEdges.get(sourceIndex).getSourceInterface().getName()) != null) {
          final SDFInterfaceVertex outputVertex = ((SDFVertex) sourceCopies.get(sourceIndex))
              .getSink(sourceEdges.get(sourceIndex).getSourceInterface().getName());
          ((SDFVertex) sourceCopies.get(sourceIndex)).setInterfaceVertexExternalLink(newEdge, outputVertex);
        }
      }

      // Set the properties of the new edge
      newEdge.setProd(new LongEdgePropertyType(rest));
      newEdge.setCons(new LongEdgePropertyType(rest));
      newEdge.setDataType(replacedEdge.getDataType());

      // If the replacedEdge has a delay and that delay still exist in the
      // SRSDF (i.e. if the source & target do not belong to the same
      // "iteration")
      if (iterationDiff > 0) {
        final long addedDelays = (iterationDiff * newEdge.getCons().longValue());
        // Check that there are enough delays available
        if (nbDelays < addedDelays) {
          // kdesnos: I added this check, but it will most
          // probably never happen
          throw new WorkflowException("Insufficient delays on edge " + replacedEdge.getSource().getName() + "."
              + replacedEdge.getSourceInterface().getName() + "=>" + replacedEdge.getTarget().getName() + "."
              + replacedEdge.getTargetInterface().getName() + ". At least " + addedDelays + " delays missing.");
        }
        newEdge.setDelay(new LongEdgePropertyType(addedDelays));
        nbDelays = nbDelays - addedDelays;
      } else {
        newEdge.setDelay(new LongEdgePropertyType(0));
      }

      // Preserve delays of sourceEdge and targetEdge.
      if ((sourceEdges.get(sourceIndex).getDelay().longValue() > 0)
          || (targetEdges.get(targetIndex).getDelay().longValue() > 0)) {
        // Number of delays is a multiplier of production/consumption
        // rate (since the graph is single-rate).
        final long multSource = sourceEdges.get(sourceIndex).getDelay().longValue()
            / sourceEdges.get(sourceIndex).getProd().longValue();
        final long multTarget = targetEdges.get(targetIndex).getDelay().longValue()
            / targetEdges.get(targetIndex).getCons().longValue();

        // Compute the new number of delays
        final long nbPreservedDelays = newEdge.getDelay().longValue() + (multSource * newEdge.getProd().longValue())
            + (multTarget * newEdge.getProd().longValue());

        // Add the delays to the newEdge
        newEdge.setDelay(new LongEdgePropertyType(nbPreservedDelays));
      }

      // Update the number of token produced/consumed by the currently
      // indexed source/target
      absoluteTarget += rest;
      absoluteSource += rest;

      // Update the totProd for the current edge (totProd is used in
      // the condition of the While loop)
      totProd += rest;
    }

    // Make sure all ports are in order
    if (!SpecialActorPortsIndexer.checkIndexes(graph)) {
      throw new WorkflowException("There are still special actors with non-indexed ports. Contact Preesm developers.");
    }

    SpecialActorPortsIndexer.sortIndexedPorts(graph);
  }

  /**
   * Checks if is between join and fork.
   *
   * @param edge
   *          the edge
   * @return true, if is between join and fork
   */
  private static boolean isBetweenJoinAndFork(final SDFEdge edge) {
    return (edge.getSource() instanceof SDFJoinVertex) && (edge.getTarget() instanceof SDFForkVertex);
  }
}
