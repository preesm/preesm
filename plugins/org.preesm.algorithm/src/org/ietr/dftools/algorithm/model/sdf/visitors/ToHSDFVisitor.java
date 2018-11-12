/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Antoine Morvan <antoine.morvan.pro@gmail.com> (2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2012)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2016)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011 - 2014)
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
package org.ietr.dftools.algorithm.model.sdf.visitors;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.ietr.dftools.algorithm.DFToolsAlgoException;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFBroadcastVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFRoundBufferVertex;
import org.ietr.dftools.algorithm.model.sdf.transformations.SpecialActorPortsIndexer;
import org.ietr.dftools.algorithm.model.types.LongEdgePropertyType;
import org.ietr.dftools.algorithm.model.types.StringEdgePropertyType;
import org.ietr.dftools.algorithm.model.visitors.IGraphVisitor;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.dftools.algorithm.model.visitors.VisitorOutput;

/**
 * Visitor used to transform an SDF into a single-rate SDF (for all edges : prod = cons).
 *
 * @author jpiat
 * @author kdesnos
 */
public class ToHSDFVisitor implements IGraphVisitor<SDFGraph, SDFAbstractVertex, SDFEdge> {

  /** The output graph. */
  private SDFGraph outputGraph;

  /**
   * GIves this visitor output.
   *
   * @return The output of the visitor
   */
  public SDFGraph getOutput() {
    return this.outputGraph;
  }

  /**
   * This method adds the {@link SDFEdge}s to the output Single-Rate {@link SDFGraph}.
   *
   * <b>The code of this method strongly inspired JoinForkCleaner.replaceEdge method, if bugs are found here, it is
   * likely they exist also there</b> (sorry for the poor code design).
   *
   * @param sdf
   *          the input {@link SDFGraph}
   * @param matchCopies
   *          a {@link Map} that associates each {@link SDFVertex} of the input {@link SDFGraph} to its corresponding
   *          {@link SDFVertex} in the output Single-Rate {@link SDFGraph}.
   * @param output
   *          the output Single-Rate {@link SDFGraph} where the {@link SDFVertex} have already been inserted by
   *          {@link #transformsTop(SDFGraph, SDFGraph)}.
   * @throws InvalidExpressionException
   *           the invalid expression exception
   */
  private void linkVerticesTop(final SDFGraph sdf, final Map<SDFAbstractVertex, List<SDFAbstractVertex>> matchCopies,
      final SDFGraph output) {

    // Scan the edges of the input graph
    for (final SDFEdge edge : sdf.edgeSet()) {
      // sdf.getEdgeSource(edge); -- Removed by kdesnos on the 2012.01.11
      // sdf.getEdgeTarget(edge); -- Removed by kdesnos on the 2012.01.11
      SDFInterfaceVertex inputVertex = null;
      SDFInterfaceVertex outputVertex = null;

      // Retrieve the duplicates of the source and target of the current
      // edge
      final List<SDFAbstractVertex> sourceCopies = matchCopies.get(sdf.getEdgeSource(edge));
      final List<SDFAbstractVertex> targetCopies = matchCopies.get(sdf.getEdgeTarget(edge));

      final List<SDFAbstractVertex> originalSourceCopies = new ArrayList<>(sourceCopies);
      final List<SDFAbstractVertex> originalTargetCopies = new ArrayList<>(targetCopies);

      long nbDelays = edge.getDelay().longValue();

      // Total number of token exchanged (produced and consumed) for this
      // edge
      final long totalNbTokens = edge.getCons().longValue() * targetCopies.size();

      // Absolute target is the targeted consumed token among the total
      // number of consumed/produced tokens
      long absoluteTarget = nbDelays;
      long absoluteSource = 0;

      // totProd is updated to store the number of token consumed by the
      // targets that are "satisfied" by the added edges.
      int totProd = 0;

      final List<SDFEdge> newEdges = new ArrayList<>();
      // Add edges until all consumed token are "satisfied"
      while (totProd < (edge.getCons().longValue() * targetCopies.size())) {

        // Index of the currently processed sourceVertex among the
        // duplicates of the current edge source.
        final long sourceIndex = (absoluteSource / edge.getProd().longValue()) % sourceCopies.size();
        // targetIndex is used to know which duplicates of the target
        // will
        // be targeted by the currently indexed copy of the source.
        final long targetIndex = (absoluteTarget / edge.getCons().longValue()) % targetCopies.size();

        // sourceProd and targetCons are the number of token already
        // produced/consumed by the currently indexed source/target
        final long sourceProd = absoluteSource % edge.getProd().longValue();
        final long targetCons = absoluteTarget % edge.getCons().longValue();

        // rest is both the production and consumption rate on the
        // created edge.
        final long rest = Math.min(edge.getProd().longValue() - sourceProd, edge.getCons().longValue() - targetCons);

        // This int represent the number of iteration separating the
        // currently indexed source and target (between which an edge is
        // added)
        // If this int is > to 0, this means that the added edge must
        // have
        // delays (with delay=prod=cons of the added edge).
        // With the previous example:
        // A_1 will target B_(1+targetIndex%3) = B_0 (with a delay of 1)
        // A_2 will target B_(2+targetIndex%3) = B_1 (with a delay of 1)
        // Warning, this integer division is not factorable
        final long iterationDiff = (absoluteTarget / totalNbTokens) - (absoluteSource / totalNbTokens);

        // Testing zone beginning
        // for inserting explode and implode vertices
        // boolean set to true if an explode should be added
        final boolean explode = rest < edge.getProd().longValue();
        final boolean implode = rest < edge.getCons().longValue();
        if (explode && !(sourceCopies.get((int) sourceIndex) instanceof SDFForkVertex)
            && (!(sourceCopies.get((int) sourceIndex) instanceof SDFBroadcastVertex)
                || (sourceCopies.get((int) sourceIndex) instanceof SDFRoundBufferVertex))) {

          // If an explode must be added
          final SDFAbstractVertex explodeVertex = new SDFForkVertex();
          final SDFAbstractVertex originVertex = sourceCopies.get((int) sourceIndex);
          explodeVertex.setName("explode_" + originVertex.getName() + "_" + edge.getSourceInterface().getName());
          output.addVertex(explodeVertex);

          // Replace the source vertex by the explode in the
          // sourceCopies list
          sourceCopies.set((int) sourceIndex, explodeVertex);

          // Add an edge between the source and the explode
          final SDFEdge newEdge = output.addEdge(originVertex, explodeVertex);
          newEdge.setDelay(new LongEdgePropertyType(0));
          newEdge.setProd(new LongEdgePropertyType(edge.getProd().longValue()));
          newEdge.setCons(new LongEdgePropertyType(edge.getProd().longValue()));
          newEdge.setDataType(edge.getDataType());
          newEdge.setSourceInterface(edge.getSourceInterface());
          explodeVertex.addInterface(edge.getTargetInterface());
          newEdge.setTargetInterface(edge.getTargetInterface());
          newEdge.setSourcePortModifier(edge.getSourcePortModifier());

          // Add a target port modifier to the edge
          newEdge.setTargetPortModifier(new StringEdgePropertyType(SDFEdge.MODIFIER_READ_ONLY));

        }
        if (implode && !(targetCopies.get((int) targetIndex) instanceof SDFJoinVertex)
            && !(targetCopies.get((int) targetIndex) instanceof SDFRoundBufferVertex)) {
          // If an implode must be added
          final SDFAbstractVertex implodeVertex = new SDFJoinVertex();
          final SDFAbstractVertex originVertex = targetCopies.get((int) targetIndex);
          implodeVertex.setName("implode_" + originVertex.getName() + "_" + edge.getTargetInterface().getName());
          output.addVertex(implodeVertex);

          // Replace the target vertex by the implode one in the
          // targetCopies List
          targetCopies.set((int) targetIndex, implodeVertex);

          // Add an edge between the implode and the target
          final SDFEdge newEdge = output.addEdge(implodeVertex, originVertex);
          newEdge.setDelay(new LongEdgePropertyType(0));
          newEdge.setProd(new LongEdgePropertyType(edge.getCons().longValue()));
          newEdge.setCons(new LongEdgePropertyType(edge.getCons().longValue()));
          newEdge.setDataType(edge.getDataType());
          implodeVertex.addInterface(edge.getSourceInterface());
          newEdge.setSourceInterface(edge.getSourceInterface());
          newEdge.setTargetInterface(edge.getTargetInterface());
          newEdge.setTargetPortModifier(edge.getTargetPortModifier());

          // Add a source port modifier to the edge
          newEdge.setSourcePortModifier(new StringEdgePropertyType(SDFEdge.MODIFIER_WRITE_ONLY));
        }
        // end of testing zone

        // Create the new Edge for the output graph
        final SDFEdge newEdge = output.addEdge(sourceCopies.get((int) sourceIndex),
            targetCopies.get((int) targetIndex));
        newEdges.add(newEdge);

        // Set the source interface of the new edge
        // If the source is a newly added fork/broadcast (or extra
        // output added to existing fork/broadcast) we rename the
        // new output ports. Contrary to ports of join/roundbuffer, no
        // special processing is needed to order the edges.
        if ((sourceCopies.get((int) sourceIndex) == originalSourceCopies.get((int) sourceIndex))
            && (!explode || !((originalSourceCopies.get((int) sourceIndex) instanceof SDFBroadcastVertex)
                || (originalSourceCopies.get((int) sourceIndex) instanceof SDFForkVertex)))) {
          // If the source does not need new ports
          if (sourceCopies.get((int) sourceIndex).getSink(edge.getSourceInterface().getName()) != null) {
            // if the source already has the appropriate interface
            newEdge
                .setSourceInterface(sourceCopies.get((int) sourceIndex).getSink(edge.getSourceInterface().getName()));
          } else {
            // if the source does not have the interface.
            newEdge.setSourceInterface(edge.getSourceInterface().copy());
            sourceCopies.get((int) sourceIndex).addInterface(newEdge.getSourceInterface());
          }
          // Copy the source port modifier of the original source
          newEdge.setSourcePortModifier(edge.getSourcePortModifier());
        } else {
          // If the source is a fork (new or not)
          // or a broadcast with a new port
          final SDFInterfaceVertex sourceInterface = edge.getSourceInterface().copy();

          String newInterfaceName = sourceInterface.getName() + "_" + sourceProd;

          // Get the current index of the port (if any)
          // and update it
          if (sourceInterface.getName().matches(SpecialActorPortsIndexer.INDEX_REGEX)) {
            final Pattern pattern = Pattern.compile(SpecialActorPortsIndexer.INDEX_REGEX);
            final Matcher matcher = pattern.matcher(sourceInterface.getName());
            matcher.find();
            final long existingIdx = Long.parseLong(matcher.group(SpecialActorPortsIndexer.GROUP_XX));
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
        if ((targetCopies.get((int) targetIndex) == originalTargetCopies.get((int) targetIndex))
            && (!implode || !((originalTargetCopies.get((int) targetIndex) instanceof SDFRoundBufferVertex)
                || (originalTargetCopies.get((int) targetIndex) instanceof SDFJoinVertex)))) {

          // if the target already has the appropriate interface
          if (targetCopies.get((int) targetIndex).getSource(edge.getTargetInterface().getName()) != null) {

            newEdge
                .setTargetInterface(targetCopies.get((int) targetIndex).getSource(edge.getTargetInterface().getName()));
          } else {
            // if the target does not have the interface.
            newEdge.setTargetInterface(edge.getTargetInterface().copy());
            targetCopies.get((int) targetIndex).addInterface(newEdge.getTargetInterface());
          }
          // Copy the target port modifier of the original source
          // Except for roundbuffers
          if (!(newEdge.getTarget() instanceof SDFRoundBufferVertex)) {
            newEdge.setTargetPortModifier(edge.getTargetPortModifier());
          } else {
            // The processing of roundBuffer portModifiers is done
            // after the while loop
          }
        } else {
          // If the target is join (new or not) /roundbuffer with new ports
          final SDFInterfaceVertex targetInterface = edge.getTargetInterface().copy();

          String newInterfaceName = targetInterface.getName() + "_" + targetCons;
          // Get the current index of the port (if any)
          // and update it
          if (targetInterface.getName().matches(SpecialActorPortsIndexer.INDEX_REGEX)) {
            final Pattern pattern = Pattern.compile(SpecialActorPortsIndexer.INDEX_REGEX);
            final Matcher matcher = pattern.matcher(targetInterface.getName());
            matcher.find();
            final long existingIdx = Long.parseLong(matcher.group(SpecialActorPortsIndexer.GROUP_XX));
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
        if (targetCopies.get((int) targetIndex) instanceof SDFVertex
            && ((SDFVertex) targetCopies.get((int) targetIndex))
                .getSource(edge.getTargetInterface().getName()) != null) {
          inputVertex = ((SDFVertex) targetCopies.get((int) targetIndex))
              .getSource(edge.getTargetInterface().getName());
          ((SDFVertex) targetCopies.get((int) targetIndex)).setInterfaceVertexExternalLink(newEdge, inputVertex);
        }
        if (sourceCopies.get((int) sourceIndex) instanceof SDFVertex
            && ((SDFVertex) sourceCopies.get((int) sourceIndex)).getSink(edge.getSourceInterface().getName()) != null) {
          outputVertex = ((SDFVertex) sourceCopies.get((int) sourceIndex)).getSink(edge.getSourceInterface().getName());
          ((SDFVertex) sourceCopies.get((int) sourceIndex)).setInterfaceVertexExternalLink(newEdge, outputVertex);
        }

        // Set the properties of the new edge
        newEdge.setProd(new LongEdgePropertyType(rest));
        newEdge.setCons(new LongEdgePropertyType(rest));
        newEdge.setDataType(edge.getDataType());

        // If the edge has a delay and that delay still exist in the
        // SRSDF (i.e. if the source & target do not belong to the same
        // "iteration")
        if (iterationDiff > 0) {
          final long addedDelays = (iterationDiff * newEdge.getCons().longValue());
          // Check that there are enough delays available
          if (nbDelays < addedDelays) {
            // kdesnos: I added this check, but it will most
            // probably never happen
            throw new DFToolsAlgoException("Insufficient delays on edge " + edge.getSource().getName() + "."
                + edge.getSourceInterface().getName() + "=>" + edge.getTarget().getName() + "."
                + edge.getTargetInterface().getName() + ". At least " + addedDelays + " delays missing.");
          }
          newEdge.setDelay(new LongEdgePropertyType(addedDelays));
          nbDelays = nbDelays - addedDelays;
        } else {
          newEdge.setDelay(new LongEdgePropertyType(0));
        }

        // Update the number of token produced/consumed by the currently
        // indexed source/target
        absoluteTarget += rest;
        absoluteSource += rest;

        // Update the totProd for the current edge (totProd is used in
        // the condition of the While loop)
        totProd += rest;

        // In case of a round buffer
        // If all needed tokens were already produced
        // but not all tokens were produced (i.e. not all source copies
        // were considered yet)
        if ((totProd == (edge.getCons().longValue() * targetCopies.size()))
            && (targetCopies.get(0) instanceof SDFInterfaceVertex)
            && ((absoluteSource / edge.getProd().longValue()) < sourceCopies.size())) {
          totProd = 0;
          // since roundbuffer behavior is handled in hierarchy
          // flattening, but should not have to be in single-rate
          // transfo.
          // However, because the HSDF transfo is sometimes applied
          // BEFORE any flattening, this code cannot be removed...
          // (cf. DynamicPiMM2SDFVisitor Code)

        }
      }

      // If the edge target was a round buffer
      // We set the port modifiers here
      if (edge.getTarget() instanceof SDFRoundBufferVertex) {
        // Set all target modifiers as unused
        // Sorted list of input
        SpecialActorPortsIndexer.sortFifoList(newEdges, false);
        final ListIterator<SDFEdge> iter = newEdges.listIterator();
        while (iter.hasNext()) {
          iter.next().setTargetPortModifier(new StringEdgePropertyType(SDFEdge.MODIFIER_UNUSED));
        }

        final StringEdgePropertyType portModifier = edge.getTargetPortModifier();
        if ((portModifier != null) && !portModifier.toString().equals(SDFEdge.MODIFIER_UNUSED)) {
          // If the target is not unused, set last edges
          // targetModifier as readOnly
          @SuppressWarnings("unchecked")
          // get the rate of the unique output of the roundbuffer
          long tokensToProduce = ((Set<SDFEdge>) (edge.getTarget().getBase().outgoingEdgesOf(edge.getTarget())))
              .iterator().next().getProd().longValue();

          // Scan the input edges in reverse order
          while ((tokensToProduce > 0) && iter.hasPrevious()) {
            final SDFEdge newEdge = iter.previous();
            newEdge.setTargetPortModifier(new StringEdgePropertyType(SDFEdge.MODIFIER_READ_ONLY));
            tokensToProduce -= newEdge.getCons().longValue();
          }
        }
      }

      // If fork/Join vertices were added during the function call
      // put back the true source/target in the match copies map.
      for (int i = 0; i < sourceCopies.size(); i++) {
        if ((sourceCopies.get(i) instanceof SDFForkVertex)
            && !originalSourceCopies.get(i).equals(sourceCopies.get(i))) {
          SDFAbstractVertex trueSource = null;
          for (final SDFEdge inEdge : output.incomingEdgesOf(sourceCopies.get(i))) {
            trueSource = inEdge.getSource();
          }
          sourceCopies.set(i, trueSource);
        }
      }
      for (int i = 0; i < targetCopies.size(); i++) {
        if ((targetCopies.get(i) instanceof SDFJoinVertex)
            && !originalTargetCopies.get(i).equals(targetCopies.get(i))) {
          SDFAbstractVertex trueTarget = null;
          for (final SDFEdge inEdge : output.outgoingEdgesOf(targetCopies.get(i))) {
            trueTarget = inEdge.getTarget();
          }
          targetCopies.set(i, trueTarget);
        }
      }
    }

    // Make sure all ports are in order
    if (!SpecialActorPortsIndexer.checkIndexes(output)) {
      throw new DFToolsAlgoException(
          "There are still special actors with non-indexed ports. Contact Preesm developers.");
    }

    SpecialActorPortsIndexer.sortIndexedPorts(output);
  }

  // This map associates each vertex of the input graph to corresponding
  /** The match copies. */
  // instances in the output graph
  private Map<SDFAbstractVertex, List<SDFAbstractVertex>> matchCopies;

  /**
   * Gets the match copies.
   *
   * @return the match copies
   */
  public Map<SDFAbstractVertex, List<SDFAbstractVertex>> getMatchCopies() {
    return this.matchCopies;
  }

  /**
   * This method transforms a schedulable {@link SDFGraph} into its equivalent Single-Rate {@link SDFGraph}. The method
   * duplicate the vertices according to the Repetition Vector of the {@link SDFGraph} then create the appropriate
   * {@link SDFEdge}s through a call to {@link #linkVerticesTop(SDFGraph, Map, SDFGraph)}.
   *
   * @param graph
   *          the input {@link SDFGraph}
   * @param output
   *          the Single-Rate output {@link SDFGraph}
   * @throws SDF4JException
   *           the SDF 4 J exception
   * @throws InvalidExpressionException
   *           the invalid expression exception
   */
  private void transformsTop(final SDFGraph graph, final SDFGraph output) throws SDF4JException {
    // This map associates each vertex of the input graph to corresponding
    // instances in the output graph
    this.matchCopies = new LinkedHashMap<>();

    if (graph.isSchedulable()) {
      // Scan the vertices of the input graph
      for (final SDFAbstractVertex vertex : graph.vertexSet()) {
        final List<SDFAbstractVertex> copies = new ArrayList<>();
        this.matchCopies.put(vertex, copies);

        // If the vertex is an interface, it will not be duplicated,
        // simply copy it in the output graph
        if (vertex instanceof SDFInterfaceVertex) {
          final SDFAbstractVertex copy = vertex.copy();
          copies.add(copy);
          output.addVertex(copy);
        } else {
          // If the vertex is not an interface, duplicate it as many
          // times as needed to obtain single rates edges
          VisitorOutput.getLogger().log(Level.INFO, vertex.getName() + " x" + vertex.getNbRepeat());
          // If the vertex does not need to be duplicated
          if (vertex.getNbRepeatAsLong() == 1) {
            final SDFAbstractVertex copy = vertex.copy();
            copy.setName(copy.getName());
            output.addVertex(copy);
            copies.add(copy);
          } else {
            // If the vertex needs to be duplicated
            for (long i = 0; i < vertex.getNbRepeatAsLong(); i++) {
              final SDFAbstractVertex copy = vertex.copy();
              copy.setName(copy.getName() + "_" + i);
              copy.setNbRepeat(1L);
              output.addVertex(copy);
              copies.add(copy);
            }
          }

        }
      }
      // The output graph has all its vertices, now deal with the edges
      linkVerticesTop(graph, this.matchCopies, output);
      output.getPropertyBean().setValue("schedulable", true);
    } else {
      VisitorOutput.getLogger().log(Level.SEVERE, "graph " + graph.getName() + " is not schedulable");
      throw (new SDF4JException("Graph " + graph.getName() + " is not schedulable"));
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.visitors.IGraphVisitor#visit(org.ietr.dftools.algorithm.model.AbstractGraph)
   */
  @Override
  public void visit(final SDFGraph sdf) throws SDF4JException {
    this.outputGraph = sdf.copy();
    boolean isHSDF = true;
    try {
      for (final SDFAbstractVertex vertex : this.outputGraph.vertexSet()) {

        if ((vertex instanceof SDFVertex) && (vertex.getNbRepeatAsLong() > 1)) {
          isHSDF = false;
          break;
        }

      }

      if (isHSDF) {
        for (final SDFEdge edge : this.outputGraph.edgeSet()) {
          long nbDelay;

          nbDelay = edge.getDelay().longValue();
          final long prod = edge.getProd().longValue();

          // No need to get the cons, if this code is reached cons ==
          // prod
          // If the number of delay on the edge is not a multiplier of
          // prod, the hsdf transformation is needed
          if ((nbDelay % prod) != 0) {
            isHSDF = false;
            break;
          }
        }
      }
    } catch (final InvalidExpressionException e) {
      throw (new SDF4JException(e.getMessage()));
    }

    if (!isHSDF) {
      this.hasChanged = true;
      this.outputGraph.clean();

      final ArrayList<SDFAbstractVertex> vertices = new ArrayList<>(sdf.vertexSet());
      for (int i = 0; i < vertices.size(); i++) {
        if (vertices.get(i) instanceof SDFVertex) {
          vertices.get(i).accept(this);
        }
      }
      try {
        transformsTop(sdf, this.outputGraph);
      } catch (final InvalidExpressionException e) {
        throw (new SDF4JException(e.getMessage()));
      }
    }

  }

  /** The has changed. */
  // Indicates whether the visited SDFGraph has been changed or not
  private boolean hasChanged = false;

  /**
   * Checks for changed.
   *
   * @return false if the visited SDFGraph was already in single rate, true otherwise
   */
  public boolean hasChanged() {
    return this.hasChanged;
  }

}
