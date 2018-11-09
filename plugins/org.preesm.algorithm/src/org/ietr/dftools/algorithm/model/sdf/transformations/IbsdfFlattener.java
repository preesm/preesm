/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 * Antoine Morvan <antoine.morvan.pro@gmail.com> (2018)
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
package org.ietr.dftools.algorithm.model.sdf.transformations;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import org.ietr.dftools.algorithm.model.AbstractGraph;
import org.ietr.dftools.algorithm.model.parameters.Argument;
import org.ietr.dftools.algorithm.model.parameters.Variable;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFBroadcastVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFRoundBufferVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.ietr.dftools.algorithm.model.types.ExpressionEdgePropertyType;
import org.ietr.dftools.algorithm.model.types.LongEdgePropertyType;
import org.ietr.dftools.algorithm.model.types.StringEdgePropertyType;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;

/**
 *
 * @author anmorvan
 *
 */
public class IbsdfFlattener {

  /**
   * The original {@link SDFGraph IBSDF graph} to flatten, must not be modified.
   */
  private final SDFGraph originalGraph;

  /**
   * Depth to which the graph will be flattened.
   */
  private final int depth;

  private SDFGraph flattenedGraph;

  public IbsdfFlattener(final SDFGraph sdf, final int depth) {
    this.originalGraph = sdf;
    this.depth = (depth < 0) ? Integer.MAX_VALUE : depth;
  }

  public SDFGraph getFlattenedGraph() {
    return flattenedGraph;
  }

  public void setFlattenedGraph(SDFGraph flattenedGraph) {
    this.flattenedGraph = flattenedGraph;
  }

  /**
   * Each fifo with a delay will be replaced with:
   * <ul>
   * <li>A fork with two outputs</li>
   * <li>A join with two inputs</li>
   * <li>The two outputs of the fork (o_0 and o_1) are respectively connected to the two inputs (i_1 and i_0) of the
   * join.</li>
   * <li>Delays of the fifos between fork and join are computed to ensure the correct single-rate transformation of the
   * application.</li>
   * </ul>
   */
  protected void addDelaySubstitutes(final SDFGraph subgraph, final long nbRepeat) {
    // Scan the fifos with delays in the subgraph
    final List<SDFEdge> fifoList = subgraph.edgeSet().stream()
        .filter(e -> e.getDelay() != null && e.getDelay().longValue() != 0).collect(Collectors.toList());
    for (final SDFEdge fifo : fifoList) {
      // Get the number of tokens produced and consumed during each
      // subgraph iteration for this fifo
      final long tgtRepeat = fifo.getTarget().getNbRepeatAsLong();
      final long tgtCons = fifo.getCons().longValue();
      final long nbDelay = fifo.getDelay().longValue();

      // Compute the prod and cons rate of the FIFOs between fork/join
      final long rate1 = nbDelay % (tgtCons * tgtRepeat);
      final long rate0 = (tgtCons * tgtRepeat) - rate1;

      if (rate1 == 0) {
        // The number of delay is a perfect modulo of the number of
        // tokens produced/consumed during an iteration, there is no
        // need to add fork and join, only to set the correct number
        // of delays
        fifo.setDelay(new LongEdgePropertyType(nbDelay * nbRepeat));
      } else {
        // Minimum difference of iteration between the production and
        // consumption of tokens
        final long minIterDiff = nbDelay / (tgtCons * tgtRepeat);

        // Add fork and join
        final SDFForkVertex fork = new SDFForkVertex();
        fork.setName("exp_" + fifo.getSource().getName() + "_" + fifo.getSourceLabel());
        subgraph.addVertex(fork);

        final SDFJoinVertex join = new SDFJoinVertex();
        join.setName("imp_" + fifo.getTarget().getName() + "_" + fifo.getTargetLabel());
        subgraph.addVertex(join);

        // Add connection between them
        final SDFEdge fifo0 = subgraph.addEdge(fork, join);
        final SDFEdge fifo1 = subgraph.addEdge(fork, join);
        join.swapEdges(0, 1);

        // Set fifo properties
        fifo0.copyProperties(fifo);
        fifo0.setSourceInterface(new SDFSinkInterfaceVertex());
        fifo0.getSourceInterface().setName(fifo.getSourceLabel() + "_0");
        fifo0.setTargetInterface(new SDFSourceInterfaceVertex());
        fifo0.getTargetInterface().setName(fifo.getTargetLabel() + "_" + rate1);
        fifo0.setProd(new LongEdgePropertyType(rate0));
        fifo0.setCons(new LongEdgePropertyType(rate0));
        fifo0.setDelay(new LongEdgePropertyType(rate0 * nbRepeat * minIterDiff));
        fifo0.setDataType(fifo.getDataType().copy());
        fifo0.setTargetPortModifier(new StringEdgePropertyType(SDFEdge.MODIFIER_READ_ONLY));
        fifo0.setSourcePortModifier(new StringEdgePropertyType(SDFEdge.MODIFIER_WRITE_ONLY));

        fifo1.copyProperties(fifo);
        fifo1.setSourceInterface(new SDFSinkInterfaceVertex());
        fifo1.getSourceInterface().setName(fifo.getSourceLabel() + "_" + rate0);
        fifo1.setTargetInterface(new SDFSourceInterfaceVertex());
        fifo1.getTargetInterface().setName(fifo.getTargetLabel() + "_0");
        fifo1.setProd(new LongEdgePropertyType(rate1));
        fifo1.setCons(new LongEdgePropertyType(rate1));
        fifo1.setDelay(new LongEdgePropertyType(rate1 * nbRepeat * (minIterDiff + 1)));
        fifo1.setDataType(fifo.getDataType().copy());
        fifo1.setTargetPortModifier(new StringEdgePropertyType(SDFEdge.MODIFIER_READ_ONLY));
        fifo1.setSourcePortModifier(new StringEdgePropertyType(SDFEdge.MODIFIER_WRITE_ONLY));

        // Connect producers and consumers of original fifo to fork/join
        final SDFEdge fifoIn = subgraph.addEdge(fifo.getSource(), fork);
        fifoIn.copyProperties(fifo);
        fifoIn.setTargetInterface(fifo.getSourceInterface().copy());
        fifoIn.getPropertyBean().removeProperty(SDFEdge.EDGE_DELAY);
        fifoIn.setCons(new LongEdgePropertyType((tgtCons * tgtRepeat)));

        final SDFEdge fifoOut = subgraph.addEdge(join, fifo.getTarget());
        fifoOut.copyProperties(fifo);
        fifoOut.setTargetInterface(fifo.getTargetInterface().copy());
        fifoOut.getPropertyBean().removeProperty(SDFEdge.EDGE_DELAY);
        fifoOut.setProd(new LongEdgePropertyType((tgtCons * tgtRepeat)));

        fork.addSource(fifoIn.getTargetInterface());
        fork.addSink(fifo0.getSourceInterface());
        fork.addSink(fifo1.getSourceInterface());
        join.addSource(fifo0.getTargetInterface());
        join.addSource(fifo1.getTargetInterface());
        join.addSink(fifoOut.getSourceInterface());

        // Remove original FIFO from the graph
        subgraph.removeEdge(fifo);
      }
    }
  }

  /**
   * This method scans the {@link SDFInterfaceVertex} of an {@link SDFGraph IBSDF} subgraph and adds
   * {@link SDFBroadcastVertex} and {@link SDFRoundBufferVertex}, if needed.
   *
   * @param subgraph
   *          the {@link SDFGraph} whose {@link SDFInterfaceVertex} are to checked. This graph will be modified within
   *          the method. The schedulability of this subgraph must have been tested before being given to this method.
   *
   *
   * @throws SDF4JException
   *           if an interface is connected to several FIFOs.
   */
  static void addInterfaceSubstitutes(final SDFGraph subgraph) {

    final List<SDFInterfaceVertex> ifaceList = subgraph.vertexSet().stream()
        .filter(v -> v instanceof SDFInterfaceVertex).map(SDFInterfaceVertex.class::cast).collect(Collectors.toList());
    for (final SDFInterfaceVertex iface : ifaceList) {
      if (iface instanceof SDFSourceInterfaceVertex) {
        // Get successors
        final Set<SDFEdge> outEdges = subgraph.outgoingEdgesOf(iface);
        if (outEdges.size() > 1) {
          throw new SDF4JException("Input interface " + iface.getName() + " in subgraph " + subgraph.getName()
              + " is connected to multiple FIFOs although this is strictly forbidden.");
        }

        // Check if a broadcast is needed
        final SDFEdge outEdge = outEdges.iterator().next();
        final long prodRate = outEdge.getProd().longValue();
        final long consRate = outEdge.getCons().longValue();
        final long nbRepeatCons = outEdge.getTarget().getNbRepeatAsLong();

        // If more token are consumed during an iteration of
        // the subgraph than the number of available tokens
        // => broadcast needed
        final long nbConsumedTokens;
        try {
          nbConsumedTokens = Math.multiplyExact(consRate, nbRepeatCons);
        } catch (ArithmeticException e) {
          throw new SDF4JException(
              "Number of repetitions of actor " + outEdge.getTarget() + " (x " + nbRepeatCons + ") or number"
                  + "of consumed tokens on edge " + outEdge + " is too big and causes an overflow in the tool.",
              e);
        }
        if (prodRate < nbConsumedTokens) {
          // Add the broadcast and connect edges
          final SDFBroadcastVertex broadcast = new SDFBroadcastVertex();
          broadcast.setName("br_" + iface.getName());
          subgraph.addVertex(broadcast);
          final SDFEdge edgeIn = subgraph.addEdge(outEdge.getSource(), broadcast);
          final SDFEdge edgeOut = subgraph.addEdge(broadcast, outEdge.getTarget());

          // Set edges properties
          edgeIn.copyProperties(outEdge);
          edgeIn.setTargetInterface(new SDFSourceInterfaceVertex());
          edgeIn.getTargetInterface().setName(iface.getName());
          edgeIn.setTargetPortModifier(new StringEdgePropertyType(SDFEdge.MODIFIER_READ_ONLY));
          edgeIn.setDelay(new LongEdgePropertyType(0));
          edgeIn.setCons(new LongEdgePropertyType(prodRate));

          edgeOut.copyProperties(outEdge);
          edgeOut.setProd(new LongEdgePropertyType(consRate * nbRepeatCons));
          edgeOut.getPropertyBean().removeProperty(SDFEdge.SOURCE_PORT_MODIFIER);
          edgeOut.setSourceInterface(new SDFSinkInterfaceVertex());
          edgeOut.getSourceInterface().setName(iface.getName() + "_0_0");

          broadcast.addSink(edgeOut.getSourceInterface());
          broadcast.addSource(edgeIn.getTargetInterface());

          // Remove the original edge
          subgraph.removeEdge(outEdge);
        }
      } else { // interface instanceof SDFSinkInterfaceVertex
        // Get predecessor
        final Set<SDFEdge> inEdges = subgraph.incomingEdgesOf(iface);
        if (inEdges.size() > 1) {
          throw new SDF4JException("Output interface " + iface.getName() + " in subgraph " + subgraph.getName()
              + " is connected to multiple FIFOs although this is strictly forbidden.");
        }

        // Check if a roundbuffer is needed
        final SDFEdge inEdge = inEdges.iterator().next();
        final long prodRate = inEdge.getProd().longValue();
        final long consRate = inEdge.getCons().longValue();
        final long nbRepeatProd = inEdge.getSource().getNbRepeatAsLong();

        // If more token are produced during an iteration of
        // the subgraph than the number of consumed tokens
        // => roundbuffer needed
        final long nbProducedTokens;
        try {
          nbProducedTokens = Math.multiplyExact(prodRate, nbRepeatProd);
        } catch (ArithmeticException e) {
          throw new SDF4JException("Number of repetitions of actor " + inEdge.getSource() + " (x " + nbRepeatProd
              + ") or number of consumed tokens on edge " + inEdge + " is too big and causes an overflow in the tool.",
              e);
        }
        if (nbProducedTokens > consRate) {
          // Add the roundbuffer and connect edges
          final SDFRoundBufferVertex roundbuffer = new SDFRoundBufferVertex();
          roundbuffer.setName("rb_" + iface.getName());
          subgraph.addVertex(roundbuffer);
          final SDFEdge edgeIn = subgraph.addEdge(inEdge.getSource(), roundbuffer);
          final SDFEdge edgeOut = subgraph.addEdge(roundbuffer, inEdge.getTarget());

          // Set edges properties
          edgeOut.copyProperties(inEdge);
          edgeOut.setSourcePortModifier(new StringEdgePropertyType(SDFEdge.MODIFIER_WRITE_ONLY));
          edgeOut.setProd(new LongEdgePropertyType(consRate));
          edgeOut.setDelay(new LongEdgePropertyType(0));
          edgeIn.setSourceInterface(new SDFSinkInterfaceVertex());
          edgeIn.getSourceInterface().setName(iface.getName());

          edgeIn.copyProperties(inEdge);
          edgeIn.setCons(new LongEdgePropertyType(prodRate * nbRepeatProd));
          edgeIn.getPropertyBean().removeProperty(SDFEdge.TARGET_PORT_MODIFIER);
          edgeIn.setTargetInterface(new SDFSourceInterfaceVertex());
          edgeIn.getTargetInterface().setName(iface.getName() + "_0_0");

          roundbuffer.addSource(edgeIn.getTargetInterface());
          roundbuffer.addSink(edgeOut.getSourceInterface());

          // Remove the original edge
          subgraph.removeEdge(inEdge);
        }
      }
    }
  }

  /**
   *
   */
  enum Side {
    SRC, TGT, BOTH
  }

  /**
   * Flatten the graph up to the {@link #depth} specified in the {@link IbsdfFlattener} attributes. Result of the
   * flattening can be obtained through the {@link #getFlattenedGraph()} method.
   */
  public void flattenGraph() {
    // Copy the original graph
    setFlattenedGraph(originalGraph.copy());

    // Flatten depth times one hierarchy level of the graph
    for (int i = 1; i <= depth; i++) {
      // Check the schedulability of the top level graph (this will also
      // set the repetition vector for each actor).
      if (!getFlattenedGraph().isSchedulable()) {
        throw new SDF4JException("Graph " + getFlattenedGraph().getName() + " is not schedulable");
      }

      // Check if there is anything to flatten
      final boolean hasNoHierarchy = getFlattenedGraph().vertexSet().stream()
          .allMatch(it -> !(it.getGraphDescription() instanceof SDFGraph));

      // If there is nothing to flatten, leave the method
      if (hasNoHierarchy) {
        return;
      }

      // Flatten one level of the graph
      flattenOneLevel(i);
    }

    // Make sure the fifos of special actors are in order (according to
    // their indices)
    SpecialActorPortsIndexer.sortIndexedPorts(getFlattenedGraph());
    flattenedGraph.insertBroadcasts();
  }

  protected void flattenOneLevel(int level) {
    // Get the list of hierarchical actors
    final List<SDFAbstractVertex> hierActors = new ArrayList<>(getFlattenedGraph().vertexSet().stream()
        .filter(it -> (it.getGraphDescription() instanceof SDFGraph)).collect(Collectors.toList()));

    // Process actors to flatten one by one
    for (SDFAbstractVertex hierActor : hierActors) {
      // Copy the orginal subgraph
      final AbstractGraph<?, ?> graphDescription = hierActor.getGraphDescription();
      final SDFGraph subgraph = ((SDFGraph) graphDescription).copy();

      // Check its schedulability (this will also
      // set the repetition vector for each actor).
      if (!subgraph.isSchedulable()) {
        throw new SDF4JException("Subgraph " + subgraph.getName() + " at level " + level + " is not schedulable");
      }

      final long nbRepeat = hierActor.getNbRepeatAsLong();
      final boolean containsNoDelay = subgraph.edgeSet().stream()
          .allMatch(it -> it.getDelay() == null || it.getDelay().longValue() == 0);

      // Prepare the subgraph for instantiation:
      // - Add roundbuffers and broadcast actors next to interfaces
      // - fork/join delays if needed
      addInterfaceSubstitutes(subgraph);
      if (!containsNoDelay && nbRepeat > 1) {
        addDelaySubstitutes(subgraph, nbRepeat);
      }

      // Substitute subgraph parameters with expression set in their parent graph
      // /!\ Getting prod and cons rate from the subgraph will no longer
      // be possible afterwards, unless it is copied in the parent.
      substituteSubgraphParameters(hierActor, subgraph);

      // Change variable names in subgraph if they are in conflict (i.e.
      // identical) with variables from the flattened graph
      final Set<Variable> duplicateVar = new LinkedHashSet<>(
          subgraph.getVariables().entrySet().stream().filter(e -> getFlattenedGraph().getVariable(e.getKey()) != null)
              .map(Entry::getValue).collect(Collectors.toSet()));

      duplicateVar.stream().forEach(it -> renameSubgraphVariable(subgraph, it));

      // The subgraph is ready, put it in the top graph
      instantiateSubgraph(hierActor, subgraph);
      getFlattenedGraph().removeVertex(hierActor);
    }
  }

  /**
   * This method replaces {@link SDFGraph#getParameters() parameters} of a subgraph with the corresponding expression
   * associated to it in the hierarchical actor instance arguments.
   *
   * @param hierActor
   *          The hierarchical actor whose subgraph is processed. Instance arguments of the actor give the expression
   *          used to substitute parameters in the subgraph.
   * @param subgraph
   *          The subgraph whose expressions are substituted.
   */
  protected void substituteSubgraphParameters(SDFAbstractVertex hierActor, SDFGraph subgraph) {

    if (subgraph.getParameters() != null) {
      // Get list of subgraph parameters, except those masked with subgraph variables
      // Also get associated expression from parent graph
      final Map<String,
          String> subgraphParameters = subgraph.getParameters().entrySet().stream()
              .filter(e -> subgraph.getVariable(e.getKey()) == null)
              .collect(Collectors.toMap(Entry::getKey, e -> hierActor.getArgument(e.getValue().getName()).getValue()));

      // Do the substitution only for parameters whose expression differs
      // from the parameter name (to avoid unnecessary computations)
      subgraphParameters.entrySet().stream().filter(e -> e.getKey() != e.getValue())
          .forEach(e -> replaceInExpressions(subgraph, e.getKey(), e.getValue()));
    }
  }

  /**
   * If a {@link Variable} of the subgraph is in conflict with a variable of the parent graph (i.e. if it has the same
   * name), the variable of the subgraph must be given a new name in this method before flattening the subgraph.
   *
   * @param subgraph
   *          The subgraph that contains a conflicting variable.
   * @param variable
   *          The variable that is in conflict with a variable from the parent graph.
   */
  protected void renameSubgraphVariable(SDFGraph subgraph, Variable variable) {
    // Create the new variable name
    final String oldName = variable.getName();
    String newName = subgraph.getName() + "_" + variable.getName();

    // Ensure the uniqueness of this name in the flattened graph
    if (getFlattenedGraph().getVariable(newName) != null) {
      String uniqueName = newName + "_0";
      int i = 0;
      while (getFlattenedGraph().getVariable(uniqueName) != null) {
        i++;
        uniqueName = newName + "_" + i;
      }
      newName = uniqueName;
    }

    // replace the name everywhere
    // The Variable itself
    subgraph.getVariables().remove(oldName);
    variable.setName(newName);
    subgraph.addVariable(variable);

    replaceInExpressions(subgraph, oldName, newName);
  }

  /**
   * In all expressions of the given subgraph, replace the string oldName with the given replacementString.
   *
   * @param subgraph
   *          The subgraph whose expressions will be altered (in {@link SDFEdge fifos}, {@SDFAbstractVertex actors}, and
   *          {@link Variable variables}).
   * @param oldName
   *          The String that should be replaced in all expressions.
   * @param replacementString
   *          The replacement.
   */
  protected void replaceInExpressions(SDFGraph subgraph, String oldName, String replacementString) {
    // Regular expression used when replacing oldName in expressions
    // Ensure that only the exact variable name will be replaced
    // but not variables "containing" the variable names
    // eg. Replacing "Test" will affect "Test*3" but not
    // "Testeur*2" or "Test_eur/3"
    final String oldNameRegex = "\\b" + oldName + "\\b";
    // In other variables expressions
    for (final Variable v : subgraph.getVariables().values()) {
      v.setValue(v.getValue().replaceAll(oldNameRegex, replacementString));
    }

    // In fifo prod/cons rates (expressions)
    for (SDFEdge fifo : subgraph.edgeSet()) {
      if (fifo.getCons() instanceof ExpressionEdgePropertyType) {
        ((ExpressionEdgePropertyType) fifo.getCons()).getValue().setValue(((ExpressionEdgePropertyType) fifo.getCons())
            .getValue().getValue().replaceAll(oldNameRegex, replacementString));
      }

      if (fifo.getProd() instanceof ExpressionEdgePropertyType) {
        ((ExpressionEdgePropertyType) fifo.getProd()).getValue().setValue(((ExpressionEdgePropertyType) fifo.getProd())
            .getValue().getValue().replaceAll(oldNameRegex, replacementString));
      }
    }

    // In instance arguments
    for (SDFAbstractVertex actor : subgraph.vertexSet()) {
      for (Argument argument : actor.getArguments().values()) {
        if (argument.getValue().contains(oldName)) {
          argument.setValue(argument.getValue().replaceAll(oldNameRegex, replacementString));
        }
      }
    }
  }

  /**
   * This method copy the subgraph of the hierarchical actor passed as a parameter into the {@link #flattenedGraph}.
   * Before calling this method, the subgraph must have been "prepared" by calling other methods from this class :
   * {@link #addDelaySubstitutes(SDFGraph,int)}, {@link #addInterfaceSubstitutes(SDFGraph)},
   * {@link #addDelaySubstitutes(SDFGraph,int)}, {@link #renameSubgraphVariable(SDFGraph,Variable)},
   * {@link #substituteSubgraphParameters(SDFAbstractVertex,SDFGraph)}.
   *
   * @param hierActor
   *          the hierarchical {@link SDFAbstractVertex actor} that is flattened.
   * @param subgraph
   *          the {@link SDFGraph subgraph} associated to the hierarchical actor.
   */
  protected void instantiateSubgraph(SDFAbstractVertex hierActor, SDFGraph subgraph) {
    // Rename actors of the subgraph
    renameSubgraphActors(hierActor, subgraph);

    // Clone subgraph variables in top graph
    subgraph.getVariables().entrySet().stream().forEach(e -> getFlattenedGraph().addVariable(e.getValue()));

    // Clone all subgraph actors in the flattened graph (except interfaces)
    final Map<SDFAbstractVertex, SDFAbstractVertex> clones = new LinkedHashMap<>();
    subgraph.vertexSet().stream().filter(it -> !(it instanceof SDFInterfaceVertex)).forEach(it -> {
      SDFAbstractVertex clone = it.copy();
      getFlattenedGraph().addVertex(clone);
      clones.put(it, clone);
    });

    // Now, copy all fifos, except those connected to interfaces
    final Map<SDFEdge, SDFEdge> fifoClones = new LinkedHashMap<>();
    for (SDFEdge fifo : subgraph.edgeSet().stream()
        .filter(it -> !(it.getSource() instanceof SDFInterfaceVertex || it.getTarget() instanceof SDFInterfaceVertex))
        .collect(Collectors.toList())) {
      SDFAbstractVertex src = clones.get(fifo.getSource());
      SDFAbstractVertex tgt = clones.get(fifo.getTarget());
      SDFEdge cloneFifo = getFlattenedGraph().addEdge(src, tgt);
      cloneFifo.copyProperties(fifo);

      fifoClones.put(fifo, cloneFifo);
    }

    // Connect FIFO that were connected to ports of the flattened actor
    // and those connected to interfaces in the subgraph
    for (SDFAbstractVertex iface : subgraph.vertexSet().stream().filter(it -> it instanceof SDFInterfaceVertex)
        .collect(Collectors.toList())) {
      // Get the actor port
      SDFInterfaceVertex port = hierActor.getInterface(iface.getName());
      SDFEdge externalFifo = hierActor.getAssociatedEdge(port);

      // Connect the new FIFO
      final SDFEdge newFifo;
      if (iface instanceof SDFSourceInterfaceVertex) {
        SDFEdge internalFifo = subgraph.outgoingEdgesOf(iface).iterator().next();
        newFifo = getFlattenedGraph().addEdge(externalFifo.getSource(), clones.get(internalFifo.getTarget()));
        newFifo.copyProperties(externalFifo);
        newFifo.setCons(internalFifo.getCons());
        if (internalFifo.getDelay() != null) {
          newFifo.setDelay(internalFifo.getDelay());
        }
        newFifo.setTargetInterface(internalFifo.getTargetInterface());
        newFifo.setTargetPortModifier(internalFifo.getTargetPortModifier());
      } else {
        // iface is instance of SDFSinkInterfaceVertex
        SDFEdge internalFifo = subgraph.incomingEdgesOf(iface).iterator().next();
        // if the edge loops on hierActor
        if (externalFifo.getTarget() == hierActor) {
          newFifo = getFlattenedGraph().addEdge(clones.get(internalFifo.getSource()),
              clones.get(subgraph.outgoingEdgesOf(subgraph.getVertex(externalFifo.getTargetInterface().getName()))
                  .iterator().next().getTarget()));
        } else {
          newFifo = getFlattenedGraph().addEdge(clones.get(internalFifo.getSource()), externalFifo.getTarget());
        }
        newFifo.copyProperties(externalFifo);
        newFifo.setProd(internalFifo.getProd());
        if (internalFifo.getDelay() != null) {
          newFifo.setDelay(internalFifo.getDelay());
        }
        newFifo.setSourceInterface(internalFifo.getSourceInterface());
        newFifo.setSourcePortModifier(internalFifo.getSourcePortModifier());
      }
      // Set delay of the new FIFO
      final long externDelay;
      if (externalFifo.getDelay() != null) {
        externDelay = externalFifo.getDelay().longValue();
      } else {
        externDelay = 0;
      }
      final long internDelay;
      if (newFifo.getDelay() != null) {
        internDelay = newFifo.getDelay().longValue();
      } else {
        internDelay = 0;
      }
      if (externDelay != 0) {
        newFifo.setDelay(new LongEdgePropertyType(externDelay + internDelay));
      }
    }
  }

  /**
   * Rename all actors (except interfaces) of the subgraph such that their name is prefixed with the name of the
   * hierarchical actor.
   */
  protected void renameSubgraphActors(SDFAbstractVertex hierActor, SDFGraph subgraph) {
    for (SDFAbstractVertex actor : subgraph.vertexSet().stream().filter(it -> !(it instanceof SDFInterfaceVertex))
        .collect(Collectors.toList())) {
      actor.setName(hierActor.getName() + "_" + actor.getName());
    }
  }
}
