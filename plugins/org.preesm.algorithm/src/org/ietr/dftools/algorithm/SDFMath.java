/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2014)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011)
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
package org.ietr.dftools.algorithm;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.math.array.DoubleArray;

/**
 * Provides static math method useful for SDF analysis.
 *
 * @author jpiat
 * @author jheulot
 */
public class SDFMath {

  private SDFMath() {
    // prevent instantiation
  }

  /**
   * Computes the basic repetition vector of an SDFAbstractGraph using rational.
   *
   * @param subgraph
   *          the subgraph
   * @param graph
   *          the graph
   * @return the hash map mapping vertices to their repetition factor
   * @throws InvalidExpressionException
   *           the invalid expression exception
   */
  public static Map<SDFAbstractVertex, Long> computeRationnalVRB(final List<SDFAbstractVertex> subgraph,
      final SDFGraph graph) {
    final Map<SDFAbstractVertex, Long> trueVrb = new LinkedHashMap<>();
    int i = 0;

    final double[][] topology = graph.getTopologyMatrix(subgraph);
    final List<Rational> vrb = SDFMath.computeRationnalNullSpace(topology);
    try {
      final List<Long> result = Rational.toNatural(new Vector<>(vrb));
      for (final SDFAbstractVertex vertex : subgraph) {
        trueVrb.put(vertex, result.get(i));
        i++;
      }
      return trueVrb;
    } catch (Exception e) {
      throw new DFToolsAlgoException("Could not compute Rational VRB", e);
    }
  }

  /**
   * Compute rationnal null space.
   *
   * @param matrix
   *          the matrix
   * @return the vector
   */
  private static List<Rational> computeRationnalNullSpace(final double[][] matrix) {
    final List<Rational> vrb = new ArrayList<>();
    final int li = matrix.length;
    int col = 1;

    if (li != 0) {
      col = matrix[0].length;
    }

    if ((li == 0) || (col == 1)) {
      for (int i = 0; i < col; i++) {
        vrb.add(new Rational(1, 1));
      }
      return vrb;
    }

    final Rational[][] rationnalTopology = new Rational[li][col];

    for (int i = 0; i < li; i++) {
      for (int j = 0; j < col; j++) {
        rationnalTopology[i][j] = new Rational(((Double) matrix[i][j]).longValue(), 1);
      }
    }
    int switchIndices = 1;
    while (rationnalTopology[0][0].zero()) {
      final Rational[] buffer = rationnalTopology[0];
      rationnalTopology[0] = rationnalTopology[switchIndices];
      rationnalTopology[switchIndices] = buffer;
      switchIndices++;
    }
    int pivot = 0;
    for (int i = 0; i < col; i++) {
      double pivotMax = 0;
      int maxIndex = i;
      for (int t = i; t < li; t++) {
        if (Math.abs(rationnalTopology[t][i].doubleValue()) > pivotMax) {
          maxIndex = t;
          pivotMax = Math.abs(rationnalTopology[t][i].doubleValue());
        }
      }
      if ((pivotMax != 0) && (maxIndex != i)) {
        final Rational[] buffer = rationnalTopology[i];
        rationnalTopology[i] = rationnalTopology[maxIndex];
        rationnalTopology[maxIndex] = buffer;
        pivot = i;
      } else if ((maxIndex == i) && (pivotMax != 0)) {
        pivot = i;
      } else {
        break;
      }
      final Rational odlPivot = new Rational(rationnalTopology[i][i]);
      for (int t = i; t < col; t++) {
        rationnalTopology[i][t] = Rational.div(rationnalTopology[i][t], odlPivot);
      }
      for (int j = i + 1; j < li; j++) {
        if (!rationnalTopology[j][i].zero()) {
          final Rational oldji = new Rational(rationnalTopology[j][i].getNum(), rationnalTopology[j][i].getDenum());
          for (int k = 0; k < col; k++) {
            rationnalTopology[j][k] = Rational.sub(rationnalTopology[j][k],
                Rational.prod(rationnalTopology[i][k], Rational.div(oldji, rationnalTopology[pivot][pivot])));
          }
        }
      }
    }
    for (int i = 0; i < col; i++) {
      vrb.add(new Rational(1, 1));
    }
    int i = li - 1;
    while (i >= 0) {
      Rational val = new Rational(0, 0);
      for (int k = i + 1; k < col; k++) {
        val = Rational.add(val, Rational.prod(rationnalTopology[i][k], vrb.get(k)));
      }
      if (!val.zero()) {
        if (rationnalTopology[i][i].zero()) {
          throw new DFToolsAlgoException("Should have zero elements in the diagonal");
        }
        vrb.set(i, Rational.div(val.abs(), rationnalTopology[i][i]));
      }
      i--;
    }
    return vrb;
  }

  /**
   * Compute the graphs rational vrb with interfaces being taken into account.
   *
   * @param subgraph
   *          the subgraph
   * @param graph
   *          The graph on which to perform the vrb
   * @return The basic repetition vector of the graph
   * @throws InvalidExpressionException
   *           the invalid expression exception
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

    final List<Rational> nullSpace = SDFMath.computeRationnalNullSpace(interfaceArrayTopology);
    final List<Long> result = Rational.toNatural(nullSpace);
    for (Entry<SDFAbstractVertex, Long> e : vrb.entrySet()) {
      vrb.put(e.getKey(), e.getValue() * result.get(result.size() - 1));
    }

    return vrb;
  }

  /**
   * Computes the gcd (greatest common divider) of a list of integer.
   *
   * @param valList
   *          The list of integer to compute
   * @return The gcd (greatest common divider) of the list
   */
  public static long gcd(final List<Long> valList) {
    long gcd = 0;
    for (final Long val : valList) {
      if (gcd == 0) {
        gcd = val;
      } else {
        gcd = ArithmeticUtils.gcd(gcd, val);
      }
    }
    return gcd;
  }

}
