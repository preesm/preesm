/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.ietr.dftools.algorithm.model.AbstractGraph;
import org.ietr.dftools.algorithm.model.IInterface;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFAbstractSpecialVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFBroadcastVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFRoundBufferVertex;

/**
 * The purpose of this class is to automatically add indexes to all ordered ports of special actors (Fork, Join,
 * RoundBuffer, Broadcast).
 *
 * @author kdesnos
 */
public class SpecialActorPortsIndexer {

  private SpecialActorPortsIndexer() {
    // prevent isntantiation
  }

  /**
   * Regular expression used on the name of a port in order to extract its index(es). Explanation:
   * <ul>
   * <li><code>.*?</code>: matches as many characters as possible (0..*) corresponding to the name of the port</li>
   * <li><code>(_([0-9]*))?</code>: matches an underscore followed by a number corresponding to the YY index. (optional
   * match)</li>
   * <li><code>_([0-9]*)\\z</code>: matches an underscore followed by a number corresponding to the XX index,
   * immediately before the end of the matched string. (mandatory match)</li>
   * </ul>
   */
  public static final String INDEX_REGEX = ".*?(_([0-9]*))?_([0-9]*)\\z";
  @Deprecated
  public static final String indexRegex  = INDEX_REGEX;

  /**
   * Group of the XX index in the {@link #INDEX_REGEX}
   */
  public static final int GROUP_XX = 3;
  @Deprecated
  public static final int groupXX  = GROUP_XX;

  /**
   * Group of the YY index in the {@link #INDEX_REGEX}
   */
  public static final int GROUP_YY = 2;
  @Deprecated
  public static final int groupYY  = GROUP_YY;

  /**
   * This method rename all ordered ports of special actors in the following fashion:
   * <ul>
   * <li>Join and Fork: <code>original_port_name_XX</code> where XX is the index of the first data token accessed by
   * this edge (out of the total number of data tokens fork/joined)</li>
   * <li>
   * <li>Broadcast and RoundBuffer: <code>original_port_name_YY_XX</code
   * > where XX is the index of the first data token accessed by this edge ( out of the total number of data tokens
   * broadcasted/roundbuffered) and YY is the absolute index of this first token if the total number of tokens
   * distributed/kept by the broadcast/roundbuffer is not considered as a modulo.</li>
   * </ul>
   *
   * @param sdfGraph
   *          {@link SDFGraph} whose special actor ports are renamed. Subgraph associated to actors of this graph will
   *          also be processed.
   */
  public static void addIndexes(SDFGraph sdfGraph) {
    // Initialize the list of processed graph with the given one.
    final List<SDFGraph> processedGraphs = new ArrayList<>();
    processedGraphs.add(sdfGraph);

    for (int i = 0; i < processedGraphs.size(); i++) {
      SDFGraph graph = processedGraphs.get(i);

      for (SDFAbstractVertex actor : graph.vertexSet()) {
        final AbstractGraph<?, ?> actorGraphDesc = actor.getGraphDescription();
        // If the actor is hierarchical, add its subgraph to the list of graphs to process
        if (actorGraphDesc != null) {
          processedGraphs.add((SDFGraph) actorGraphDesc);
        }

        // Check if the ports already have indexed names
        // In such a case, index names are assumed to be valid and will
        // be used to sort the ports of the actor.
        // This should be documented somewhere (in a tutorial)
        final boolean alreadyIndexed = checkIndexes(actor);

        // Add an index to the ports only if they are not already present
        if (!alreadyIndexed) {
          // Get ordered Fifos of special actors
          boolean isSource = true;
          long modulo = 0L;
          final List<SDFEdge> fifos;
          if (actor instanceof SDFJoinVertex) {
            isSource = false;
            fifos = ((SDFJoinVertex) actor).getIncomingConnections();
          } else if (actor instanceof SDFRoundBufferVertex) {
            isSource = false;
            modulo = graph.outgoingEdgesOf(actor).iterator().next().getProd().longValue();
            fifos = ((SDFRoundBufferVertex) actor).getIncomingConnections();
          } else if (actor instanceof SDFForkVertex) {
            fifos = ((SDFForkVertex) actor).getOutgoingConnections();
          } else if (actor instanceof SDFBroadcastVertex) {
            modulo = graph.incomingEdgesOf(actor).iterator().next().getCons().longValue();
            fifos = ((SDFBroadcastVertex) actor).getOutgoingConnections();
          } else {
            fifos = new ArrayList<>();
          }

          long indexX = 0L;
          long indexY = 0L;

          for (IInterface iface : actor.getInterfaces()) {
            for (SDFEdge fifo : fifos) {
              if (fifo.getSourceInterface() == iface || fifo.getTargetInterface() == iface) {
                final String indexString = ((modulo > 0) ? indexY + "_" : "") + indexX;
                if (isSource) {
                  fifo.getSourceInterface().setName(fifo.getSourceInterface().getName() + "_" + indexString);
                  indexY += fifo.getProd().longValue();
                } else {
                  fifo.getTargetInterface().setName(fifo.getTargetInterface().getName() + "_" + indexString);
                  indexY += fifo.getCons().longValue();
                }
                indexX = (modulo > 0) ? indexY % modulo : indexY;
              }
            }
          }
        }
      }
    }
  }

  /**
   * Calls the checkIndexes(SDFAbstractVertex actor) method for all special actors of this graph and all actors of its
   * subgraphs
   */
  public static boolean checkIndexes(SDFGraph sdfGraph) {
    // Initialize the list of processed graph with the given one.
    final List<SDFGraph> processedGraphs = new ArrayList<>();
    processedGraphs.add(sdfGraph);

    for (int i = 0; i < processedGraphs.size(); i++) {
      final SDFGraph graph = processedGraphs.get(i);

      for (SDFAbstractVertex actor : graph.vertexSet()) {
        final AbstractGraph<?, ?> actorGraphDesc = actor.getGraphDescription();
        // If the actor is hierarchical, add its subgraph to the list of graphs to process
        if (actorGraphDesc != null) {
          processedGraphs.add((SDFGraph) actorGraphDesc);
        }

        // Check only special actors
        if (actor instanceof SDFAbstractSpecialVertex && !checkIndexes(actor)) {
          return false;
        }

      }
    }
    return true;
  }

  /**
   * This method checks if the ports the given {@link SDFAbstractVertex} passed as a parameter are already indexed.
   *
   * @return <code>true</code> if the actor is a special actor and its ports are already indexed, <code>false</code>
   *         otherwise.
   */
  public static boolean checkIndexes(SDFAbstractVertex actor) {
    boolean isSource = true;

    final List<SDFEdge> fifos;
    if (actor instanceof SDFJoinVertex) {
      isSource = false;
      fifos = ((SDFJoinVertex) actor).getIncomingConnections();
    } else if (actor instanceof SDFRoundBufferVertex) {
      isSource = false;
      fifos = ((SDFRoundBufferVertex) actor).getIncomingConnections();
    } else if (actor instanceof SDFForkVertex) {
      fifos = ((SDFForkVertex) actor).getOutgoingConnections();
    } else if (actor instanceof SDFBroadcastVertex) {
      fifos = ((SDFBroadcastVertex) actor).getOutgoingConnections();
    } else {
      fifos = new ArrayList<>();
    }
    return checkIndexes(fifos, isSource);
  }

  /**
   * Check wether all {@link SDFEdge} in the given {@link List} have a valid index.
   *
   * @param valIsSource
   *          whether the source or target ports of SDFEdge are considered
   *
   * @return <code>true</code> if the list of SDFEdge is not empty and if its ports are already indexed,
   *         <code>false</code> otherwise.
   */
  protected static boolean checkIndexes(List<SDFEdge> fifos, boolean valIsSource) {
    return fifos.stream().allMatch(it -> {
      final String name = (valIsSource) ? it.getSourceInterface().getName() : it.getTargetInterface().getName();
      return name.matches(INDEX_REGEX);
    }) && !fifos.isEmpty();
  }

  /**
   * Sort all indexed ports of the special actors of the graph.
   */
  public static void sortIndexedPorts(SDFGraph sdfGraph) {

    // Initialize the list of processed graph with the given one.
    final List<SDFGraph> processedGraphs = new ArrayList<>();
    processedGraphs.add(sdfGraph);

    for (int i = 0; i < processedGraphs.size(); i++) {
      final SDFGraph graph = processedGraphs.get(i);

      for (SDFAbstractVertex actor : graph.vertexSet()) {
        final AbstractGraph<?, ?> actorGraphDesc = actor.getGraphDescription();
        // If the actor is hierarchical, add its subgraph to the list of graphs to process
        if (actorGraphDesc != null) {
          processedGraphs.add((SDFGraph) actorGraphDesc);
        }

        final boolean alreadyIndexed = checkIndexes(actor);

        // If the actor is special, and its ports are indexed
        if (alreadyIndexed) {

          boolean isSource = true;

          final List<SDFEdge> fifos;
          if (actor instanceof SDFJoinVertex) {
            isSource = false;
            fifos = ((SDFJoinVertex) actor).getIncomingConnections();
          } else if (actor instanceof SDFRoundBufferVertex) {
            isSource = false;
            fifos = ((SDFRoundBufferVertex) actor).getIncomingConnections();
          } else if (actor instanceof SDFForkVertex) {
            fifos = ((SDFForkVertex) actor).getOutgoingConnections();
          } else if (actor instanceof SDFBroadcastVertex) {
            fifos = ((SDFBroadcastVertex) actor).getOutgoingConnections();
          } else {
            fifos = new ArrayList<>();
          }

          final boolean valIsSource = isSource;
          // Sort the FIFOs according to their indexes
          sortFifoList(fifos, valIsSource);

          // Apply this new order to the edges
          long order = 0;
          for (IInterface iface : actor.getInterfaces()) {
            for (SDFEdge fifo : fifos) {
              if (fifo.getSourceInterface() == iface || fifo.getTargetInterface() == iface) {
                // Switch and implicit cast for each type
                if (actor instanceof SDFAbstractSpecialVertex) {
                  ((SDFAbstractSpecialVertex) actor).setEdgeIndex(fifo, order);
                }

                order++;
              }
            }
          }
        }
      }
    }
  }

  /**
   * Sort a {@link List} of {@link SDFEdge} according to their port index. The source or target ports will be considered
   * depending on the valIsSource boolean.
   */
  public static void sortFifoList(List<SDFEdge> fifos, boolean valIsSource) {
    // Check that all fifos have an index
    if (checkIndexes(fifos, valIsSource)) {
      // If indexes are valid, do the sort
      fifos.sort((fifo0, fifo1) -> {
        // Get the port names
        final String p0Name = (valIsSource) ? fifo0.getSourceInterface().getName()
            : fifo0.getTargetInterface().getName();
        final String p1Name = (valIsSource) ? fifo1.getSourceInterface().getName()
            : fifo1.getTargetInterface().getName();

        // Compile and apply the pattern
        final Pattern pattern = Pattern.compile(INDEX_REGEX);
        final Matcher m0 = pattern.matcher(p0Name);
        final Matcher m1 = pattern.matcher(p1Name);
        m0.find();
        m1.find();

        // Retrieve the indexes
        final long yy0 = (m0.group(GROUP_YY) != null) ? Long.parseLong(m0.group(GROUP_YY)) : 0L;
        final long yy1 = (m1.group(GROUP_YY) != null) ? Long.parseLong(m1.group(GROUP_YY)) : 0L;
        final long xx0 = Long.parseLong(m0.group(GROUP_XX));
        final long xx1 = Long.parseLong(m1.group(GROUP_XX));

        // Sort according to yy indexes if they are different,
        // and according to xx indexes otherwise
        return (int) ((yy0 != yy1) ? (yy0 - yy1) : (xx0 - xx1));
      });
    }
  }
}
