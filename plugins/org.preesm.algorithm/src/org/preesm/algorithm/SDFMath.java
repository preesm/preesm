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
package org.preesm.algorithm;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.math.array.DoubleArray;
import org.preesm.algorithm.model.sdf.SDFAbstractVertex;
import org.preesm.algorithm.model.sdf.SDFEdge;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.model.sdf.SDFInterfaceVertex;
import org.preesm.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.preesm.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.commons.math.LongFraction;
import org.preesm.commons.math.MathFunctionsHelper;

/**
 * Provides static math method useful for SDF analysis.
 *
 * @author jpiat
 * @author jheulot
 */
public interface SDFMath {
  /**
   * Computes the basic repetition vector of an SDFAbstractGraph using LongFraction.
   *
   * @param subgraph
   *          the subgraph
   * @param graph
   *          the graph
   * @return the hash map mapping vertices to their repetition factor
   */
  public static Map<SDFAbstractVertex, Long> computeRationnalVRB(final List<SDFAbstractVertex> subgraph,
      final SDFGraph graph) {
    final Map<SDFAbstractVertex, Long> trueVrb = new LinkedHashMap<>();
    int i = 0;

    final double[][] topology = graph.getTopologyMatrix(subgraph);
    final List<LongFraction> vrb = MathFunctionsHelper.computeRationnalNullSpace(topology);
    try {
      final List<Long> result = MathFunctionsHelper.toNatural(vrb);
      for (final SDFAbstractVertex vertex : subgraph) {
        trueVrb.put(vertex, result.get(i));
        i++;
      }
      return trueVrb;
    } catch (Exception e) {
      throw new PreesmException("Could not compute LongFraction VRB", e);
    }
  }

  /**
   * Compute the graphs LongFraction vrb with interfaces being taken into account.
   *
   * @param subgraph
   *          the subgraph
   * @param graph
   *          The graph on which to perform the vrb
   * @return The basic repetition vector of the graph
   */
  public static Map<SDFAbstractVertex, Long> computeRationnalVRBWithInterfaces(final List<SDFAbstractVertex> subgraph,
      final SDFGraph graph) {

    final List<SDFAbstractVertex> subgraphWOInterfaces = new ArrayList<>();
    for (final SDFAbstractVertex vertex : subgraph) {
      if (!(vertex instanceof SDFInterfaceVertex)) {
        subgraphWOInterfaces.add(vertex);
      }
    }

    final Map<SDFAbstractVertex, Long> vrb = SDFMath.computeRationnalVRB(subgraphWOInterfaces, graph);

    final List<double[]> interfaceTopology = new ArrayList<>();
    double[][] interfaceArrayTopology;

    int nbInterfaceEdges = 0;
    int decal = 0;

    for (final SDFAbstractVertex vertex : subgraph) {
      if (vertex instanceof SDFInterfaceVertex) {
        if (vertex instanceof SDFSinkInterfaceVertex) {
          nbInterfaceEdges += graph.incomingEdgesOf(vertex).size();
        } else if (vertex instanceof SDFSourceInterfaceVertex) {
          nbInterfaceEdges += graph.outgoingEdgesOf(vertex).size();
        }
      }
    }

    for (final SDFAbstractVertex vertex : subgraph) {
      if (vertex instanceof SDFInterfaceVertex) {
        if (vertex instanceof SDFSinkInterfaceVertex) {
          for (final SDFEdge edge : graph.incomingEdgesOf(vertex)) {
            if (!(edge.getSource() instanceof SDFInterfaceVertex)) {
              final double[] line = DoubleArray.fill(nbInterfaceEdges + 1, 0);
              line[decal] = -edge.getCons().longValue();
              line[nbInterfaceEdges] = (edge.getProd().longValue() * (vrb.get(edge.getSource())));
              interfaceTopology.add(line);
              decal++;
            }
          }
        } else if (vertex instanceof SDFSourceInterfaceVertex) {
          for (final SDFEdge edge : graph.outgoingEdgesOf(vertex)) {
            if (!(edge.getTarget() instanceof SDFInterfaceVertex)) {
              final double[] line = DoubleArray.fill(nbInterfaceEdges + 1, 0);
              line[decal] = edge.getProd().longValue();
              line[nbInterfaceEdges] = (-edge.getCons().longValue() * (vrb.get(edge.getTarget())));
              interfaceTopology.add(line);
              decal++;
            }
          }
        }
      }
    }

    if (interfaceTopology.isEmpty()) {
      interfaceArrayTopology = new double[0][0];
    } else {
      interfaceArrayTopology = new double[interfaceTopology.size()][interfaceTopology.get(0).length];

      int i = 0;
      for (final double[] line : interfaceTopology) {
        interfaceArrayTopology[i] = line;
        i++;
      }
    }

    final List<LongFraction> nullSpace = MathFunctionsHelper.computeRationnalNullSpace(interfaceArrayTopology);
    final List<Long> result = MathFunctionsHelper.toNatural(nullSpace);
    for (Entry<SDFAbstractVertex, Long> e : vrb.entrySet()) {
      vrb.put(e.getKey(), e.getValue() * result.get(result.size() - 1));
    }

    return vrb;
  }

}
