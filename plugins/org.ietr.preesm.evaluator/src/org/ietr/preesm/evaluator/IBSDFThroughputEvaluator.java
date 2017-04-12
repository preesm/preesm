/*******************************************************************************
 * Copyright or © or Copr. 2015 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * blaunay <bapt.launay@gmail.com> (2015)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
package org.ietr.preesm.evaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.ietr.dftools.algorithm.model.AbstractEdgePropertyType;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.ietr.dftools.workflow.tools.WorkflowLogger;

// TODO: Auto-generated Javadoc
/**
 * Class used to search for the optimal periodic schedule and its throughput for a given hierarchical graph IBSDF.
 *
 * @author blaunay
 */
public class IBSDFThroughputEvaluator extends ThroughputEvaluator {

  /**
   * Computes (not necessarily optimally) the throughput on the optimal periodic schedule (if it exists) of a given graph under the given scenario.
   *
   * @param inputGraph
   *          the input graph
   * @return the double
   * @throws InvalidExpressionException
   *           the invalid expression exception
   */
  @Override
  public double launch(final SDFGraph inputGraph) throws InvalidExpressionException {

    // Find a lower bound on the minimal period by inspecting the bottom levels
    double Kmin = starting_period(inputGraph);
    double K = 0;

    double eps = 0.1; // precision of the solution

    // Step 2 : Test if k_min a valid period for the graph
    if (test_period(Kmin, inputGraph) != null) {
      K = Kmin;
    } else {
      // Step 3 : Find a value for K_max
      double Kmax = 10 * Kmin;
      // increase Kmax until it is a valid period
      while (test_period(Kmax, inputGraph) == null) {
        Kmin = Kmax;
        Kmax *= 10;
        // avoid infinite loop if there is no periodic schedule
        if (Kmax >= (Double.MAX_VALUE / 10)) {
          WorkflowLogger.getLogger().log(Level.SEVERE, "No periodic schedule for this graph");
          return 0;
        }
      }

      // adjust the precision
      eps = Kmax / Math.pow(10, 6);
      K = Kmax;
      // Step 4 : Improve (minimize) K
      while (Math.abs(Kmax - Kmin) > eps) {
        K = (Kmax + Kmin) / 2;
        if (test_period(K, inputGraph) != null) {
          Kmax = K; // continue to search on the interval [Kmin,K]
        } else {
          Kmin = K; // continue to search on the interval [K,Kmax]
          K = Kmax;
        }
      }
    }
    return K;
  }

  /**
   * Computes a lower bound on the optimal normalized period, helpful to get the algorithm started.
   *
   * @param inputGraph
   *          the input graph
   * @return the double
   * @throws InvalidExpressionException
   *           the invalid expression exception
   */
  private double starting_period(final SDFGraph inputGraph) throws InvalidExpressionException {
    boolean hierarchical = false;
    double K;
    double Kmax = 0;
    for (final SDFAbstractVertex vertex : inputGraph.vertexSet()) {
      // if hierarchical vertex, go check its subgraph
      if ((vertex.getGraphDescription() != null) && (vertex.getGraphDescription() instanceof SDFGraph)) {
        K = starting_period((SDFGraph) vertex.getGraphDescription());
        if (K > Kmax) {
          Kmax = K;
        }
        hierarchical = true;
      }
    }
    // we are in a graph without sublevels of hierarchy,
    // compute the optimal period of this graph with the method for SDF
    if (!hierarchical) {
      final ThroughputEvaluator eval = new SDFThroughputEvaluator();
      eval.scenar = this.scenar;
      Kmax = eval.launch(inputGraph);
    }
    return Kmax;
  }

  /**
   * Tests if the given period is a valid one for a periodic schedule for the actors of the graph.
   *
   * @param K
   *          the k
   * @param sdf
   *          the sdf
   * @return null if the condition not respected
   */
  private HashMap<String, HashMap<String, Double>> test_period(final double K, final SDFGraph sdf) {
    final SDFGraph g = sdf.clone();

    // The set of edges that will be used to compute shortest paths
    final HashMap<SDFEdge, Double> e = new HashMap<>(g.edgeSet().size());
    // The set of vertices used to compute shortest paths
    final HashMap<String, Double> v = new HashMap<>();
    // Contains the results of the shortest paths
    HashMap<String, HashMap<String, Double>> dist = new HashMap<>();
    double H;
    double L;
    AbstractEdgePropertyType<?> E_in;
    AbstractEdgePropertyType<?> E_out;

    // Add looping edges on actors
    for (final SDFAbstractVertex vertex : g.vertexSet()) {
      if (!((vertex.getGraphDescription() != null) && (vertex.getGraphDescription() instanceof SDFGraph))) {
        final SDFEdge loop = g.addEdge(vertex, vertex);
        final SDFSourceInterfaceVertex in = new SDFSourceInterfaceVertex();
        in.setName(vertex.getName() + "In");
        final SDFSinkInterfaceVertex out = new SDFSinkInterfaceVertex();
        out.setName(vertex.getName() + "Out");
        AbstractEdgePropertyType<?> x;
        if (vertex.getSources().size() != 0) {
          x = vertex.getAssociatedEdge(vertex.getSources().get(0)).getCons();
        } else {
          x = vertex.getAssociatedEdge(vertex.getSinks().get(0)).getProd();
        }
        vertex.addSource(in);
        vertex.addSink(out);
        loop.setSourceInterface(out);
        loop.setTargetInterface(in);
        loop.setDelay(x);
        loop.setCons(x);
        loop.setProd(x);
      }
    }

    // Value all arcs of this level with L - K * H
    for (final SDFEdge edge : g.edgeSet()) {
      if ((edge.getSource() instanceof SDFSourceInterfaceVertex)
          || ((edge.getSource().getGraphDescription() != null) && (edge.getSource().getGraphDescription() instanceof SDFGraph))) {
        L = 0;
      } else {
        L = this.scenar.getTimingManager().getTimingOrDefault(edge.getSource().getId(), "x86").getTime();
      }

      H = ((double) (edge.getDelay().getValue()) + SDFMathD.gcd((double) (edge.getCons().getValue()), (double) (edge.getProd().getValue())))
          - (double) (edge.getCons().getValue());

      e.put(edge, -(L - (K * H)));
    }

    // We need a copy of the set of vertices, since we will add vertices in the original set
    // while going through its elements
    final Set<SDFAbstractVertex> vertexSetCopy = new HashSet<>(g.vertexSet());
    for (final SDFAbstractVertex vertex : vertexSetCopy) {
      // For each hierarchical actor
      if ((vertex.getGraphDescription() != null) && (vertex.getGraphDescription() instanceof SDFGraph)) {

        // compute shortest paths between its in/out ports
        dist = test_period(K, (SDFGraph) vertex.getGraphDescription());

        // if null, then subgraph not alive, so the whole graph is not.
        if (dist == null) {
          return null;
        } else {
          // Create new nodes corresponding to the interfaces
          for (final String input : dist.keySet()) {
            // Create a new vertex for each new input interface
            final SDFAbstractVertex VertexIn = new SDFVertex();
            VertexIn.setName(input);
            // Create a new port for the incoming edge
            final SDFSourceInterfaceVertex inPortIN = new SDFSourceInterfaceVertex();
            inPortIN.setName("in");
            VertexIn.addSource(inPortIN);
            // Add it to the graph
            g.addVertex(VertexIn);
            // Create the new incoming edge of this node
            final SDFEdge EdgeToIn = g.addEdge(vertex.getAssociatedEdge(vertex.getInterface(input)).getSource(), VertexIn);
            EdgeToIn.setSourceInterface(vertex.getAssociatedEdge(vertex.getInterface(input)).getSourceInterface());
            EdgeToIn.setTargetInterface(inPortIN);
            // Put the correct rates on the new edge
            E_in = vertex.getAssociatedEdge(vertex.getSources().get(0)).getCons();
            E_out = vertex.getAssociatedEdge(vertex.getSources().get(0)).getProd();
            EdgeToIn.setCons(E_out);
            EdgeToIn.setProd(E_in);
            // Put it on the list for the BellmanFord algo, remove the ancient one
            e.put(EdgeToIn, e.get(vertex.getAssociatedEdge(vertex.getInterface(input))));

            // New node for each output interface
            for (final String output : dist.get(input).keySet()) {
              SDFAbstractVertex VertexOut = g.getVertex(output);
              if (VertexOut == null) {
                // Create vertex out only if it does not exist already
                VertexOut = new SDFVertex();
                VertexOut.setName(output);
                // Create a new port port for the outgoing edge
                final SDFSinkInterfaceVertex outPortOUT = new SDFSinkInterfaceVertex();
                outPortOUT.setName("out");
                VertexOut.addSink(outPortOUT);
                g.addVertex(VertexOut);
                // Create the edge going from the node out if it does not loop
                if (vertex.getAssociatedEdge(vertex.getInterface(output)).getTarget() != vertex) {
                  final SDFEdge EdgeFromOut = g.addEdge(VertexOut, vertex.getAssociatedEdge(vertex.getInterface(output)).getTarget());
                  EdgeFromOut.setTargetInterface(vertex.getAssociatedEdge(vertex.getInterface(output)).getTargetInterface());

                  // Put the correct rates on the new edge
                  E_in = vertex.getAssociatedEdge(vertex.getSinks().get(0)).getProd();
                  E_out = vertex.getAssociatedEdge(vertex.getSinks().get(0)).getCons();
                  EdgeFromOut.setCons(E_out);
                  EdgeFromOut.setProd(E_in);

                  EdgeFromOut.setSourceInterface(outPortOUT);
                  // Put it on the list for the BellmanFord algo, remove the ancient one
                  e.put(EdgeFromOut, e.get(vertex.getAssociatedEdge(vertex.getInterface(output))));
                }
              }
              // Create the edge linking the new in and out
              final SDFEdge EdgeInOut = g.addEdge(VertexIn, VertexOut);
              // Put the correct rates on the new edge
              E_in = vertex.getAssociatedEdge(vertex.getSources().get(0)).getCons();
              E_out = vertex.getAssociatedEdge(vertex.getSinks().get(0)).getProd();
              EdgeInOut.setCons(E_out);
              EdgeInOut.setProd(E_in);
              // port of origin of this edge
              final SDFSinkInterfaceVertex outPortIN = new SDFSinkInterfaceVertex();
              outPortIN.setName(output);
              VertexIn.addSink(outPortIN);
              EdgeInOut.setSourceInterface(outPortIN);
              // target port of this edge
              final SDFSourceInterfaceVertex inPortOUT = new SDFSourceInterfaceVertex();
              inPortOUT.setName(VertexIn.getName());
              VertexOut.addSource(inPortOUT);
              EdgeInOut.setTargetInterface(inPortOUT);
              // new edge to use for BellmanFord
              e.put(EdgeInOut, dist.get(input).get(output));
              // new vertices to consider for BellmanFord
              v.put(VertexOut.getName(), Double.POSITIVE_INFINITY);
            }
            v.put(VertexIn.getName(), Double.POSITIVE_INFINITY);
            // check if the incoming edge loops on the actor
            if (vertex.getAssociatedEdge(vertex.getInterface(input)).getSource() == vertex) {
              final SDFEdge loop = g.addEdge(g.getVertex(VertexIn.getAssociatedEdge(VertexIn.getInterface("in")).getSourceInterface().getName()), VertexIn);
              loop.setTargetInterface(VertexIn.getInterface("in"));
              loop.setSourceInterface(g.getVertex(VertexIn.getAssociatedEdge(VertexIn.getInterface("in")).getSourceInterface().getName()).getInterface("out"));
              e.put(loop, e.get(EdgeToIn));
              e.remove(EdgeToIn);
            }
          }
        }
        // Remove the hierarchical actor from the graph
        // Remove the hierarchical actor from the graph
        for (final SDFInterfaceVertex inter : vertex.getSources()) {
          e.remove(vertex.getAssociatedEdge(inter));
        }
        for (final SDFInterfaceVertex inter : vertex.getSinks()) {
          e.remove(vertex.getAssociatedEdge(inter));
        }
        g.removeVertex(vertex);
      } else {
        // not a hierarchical actor
        v.put(vertex.getName(), Double.POSITIVE_INFINITY);
      }
      // clear the map of distances, reused for the next hierarchical actor
      dist.clear();
    }

    ArrayList<SDFAbstractVertex> origin;

    // when at level zero
    if (g.getParentVertex() == null) {
      // pick a random source node
      origin = new ArrayList<>();
      origin.add(g.vertexSet().iterator().next());
    } else {
      // otherwise, source nodes of the shortest paths to compute are all the input interfaces
      origin = new ArrayList<>(new ArrayList<>(g.getParentVertex().getSources()));
    }

    // BellmanFord from each input
    for (final SDFAbstractVertex input : origin) {
      // Source node for the shortest path
      v.put(input.getName(), (double) 0);

      // Relaxation
      for (int i = 1; i <= (v.size() - 1); i++) {
        for (final Map.Entry<SDFEdge, Double> entry : e.entrySet()) {
          if ((v.get(entry.getKey().getSource().getName()) + entry.getValue()) < v.get(entry.getKey().getTarget().getName())) {
            v.put(entry.getKey().getTarget().getName(), v.get(entry.getKey().getSource().getName()) + entry.getValue());
          }
        }
      }
      // Check for negative cycle
      for (final Map.Entry<SDFEdge, Double> entry : e.entrySet()) {
        if ((v.get(entry.getKey().getSource().getName()) + entry.getValue()) < v.get(entry.getKey().getTarget().getName())) {
          // Cycle of negative weight found, condition not respected -> graph not alive
          return null;
        }
      }
      // while we are not at level zero, fill the shortest paths table
      if (g.getParentVertex() != null) {
        dist.put(input.getName(), new HashMap<String, Double>());
        // distance from the input to all the outputs
        for (final SDFAbstractVertex output : g.getParentVertex().getSinks()) {
          dist.get(input.getName()).put(output.getName(), v.get(output.getName()));
        }
        // reset weight on vertices
        for (final SDFAbstractVertex ve : g.vertexSet()) {
          v.put(ve.getName(), Double.POSITIVE_INFINITY);
        }

      } else {
        return dist;
      }
    }
    return dist;
  }

  /**
   * Checks if the given graph (containing several levels of hierarchy) respects the condition of liveness. Recursive function.
   *
   * @param g
   *          the g
   * @return null only if the graph does not respect the condition
   */
  public HashMap<String, HashMap<String, Double>> is_alive(final SDFGraph g) {

    // The set of edges that will be used to compute shortest paths
    final HashMap<SDFEdge, Double> e = new HashMap<>(g.edgeSet().size());
    // The set of vertices used to compute shortest paths
    final HashMap<String, Double> v = new HashMap<>();
    // Contains the results of the shortest paths
    HashMap<String, HashMap<String, Double>> dist = new HashMap<>();

    // Liveness
    // Value all arcs of this level with M0 + gcd - Zj
    for (final SDFEdge edge : g.edgeSet()) {
      e.put(edge, (((double) (edge.getDelay().getValue()) + SDFMathD.gcd((double) (edge.getCons().getValue()), (double) (edge.getProd().getValue())))
          - (double) (edge.getCons().getValue())));
    }

    // We need a copy of the set of vertices, since we will add vertices in the original set
    // while going through its elements
    final Set<SDFAbstractVertex> vertexSetCopy = new HashSet<>(g.vertexSet());
    for (final SDFAbstractVertex vertex : vertexSetCopy) {
      // For each hierarchical actor
      if ((vertex.getGraphDescription() != null) && (vertex.getGraphDescription() instanceof SDFGraph)) {

        // compute shortest paths between its in/out ports
        dist = is_alive((SDFGraph) vertex.getGraphDescription());

        // if null, then subgraph not alive, so the whole graph is not.
        if (dist == null) {
          return null;
        } else {
          // Create new nodes corresponding to the interfaces
          for (final String input : dist.keySet()) {
            // Create a new vertex for each new input interface
            final SDFVertex VertexIn = new SDFVertex();
            VertexIn.setName(input);
            // Create a new port for the incoming edge
            final SDFSourceInterfaceVertex inPortIN = new SDFSourceInterfaceVertex();
            inPortIN.setName("in");
            VertexIn.addSource(inPortIN);
            // Add it to the graph
            g.addVertex(VertexIn);
            // Create the new incoming edge of this node
            final SDFEdge EdgeToIn = g.addEdge(vertex.getAssociatedEdge(vertex.getInterface(input)).getSource(), VertexIn);
            EdgeToIn.setSourceInterface(vertex.getAssociatedEdge(vertex.getInterface(input)).getSourceInterface());
            EdgeToIn.setTargetInterface(inPortIN);
            // Put it on the list for the BellmanFord algo
            e.put(EdgeToIn, e.get(vertex.getAssociatedEdge(vertex.getInterface(input))));

            // New node for each output interface
            for (final String output : dist.get(input).keySet()) {
              SDFAbstractVertex VertexOut = g.getVertex(output);
              if (VertexOut == null) {
                // Create vertex out only if it does not exist already
                VertexOut = new SDFVertex();
                VertexOut.setName(output);
                // Create a new port port for the outgoing edge
                final SDFSinkInterfaceVertex outPortOUT = new SDFSinkInterfaceVertex();
                outPortOUT.setName("out");
                VertexOut.addSink(outPortOUT);
                g.addVertex(VertexOut);
                // Create the edge going from the node out
                if (vertex.getAssociatedEdge(vertex.getInterface(output)).getTarget() != vertex) {
                  final SDFEdge EdgeFromOut = g.addEdge(VertexOut, vertex.getAssociatedEdge(vertex.getInterface(output)).getTarget());
                  EdgeFromOut.setTargetInterface(vertex.getAssociatedEdge(vertex.getInterface(output)).getTargetInterface());
                  EdgeFromOut.setSourceInterface(VertexOut.getSink("out"));
                  // Put it on the list for the BellmanFord algo, remove the ancient one
                  e.put(EdgeFromOut, e.get(vertex.getAssociatedEdge(vertex.getInterface(output))));
                }
              }
              // Create the edge linking the new in and out
              final SDFEdge EdgeInOut = g.addEdge(VertexIn, VertexOut);
              // port of origin of this edge
              final SDFSinkInterfaceVertex outPortIN = new SDFSinkInterfaceVertex();
              outPortIN.setName(output);
              VertexIn.addSink(outPortIN);
              EdgeInOut.setSourceInterface(outPortIN);
              // target port of this edge
              final SDFSourceInterfaceVertex inPortOUT = new SDFSourceInterfaceVertex();
              inPortOUT.setName(VertexIn.getName());
              VertexOut.addSource(inPortOUT);
              EdgeInOut.setTargetInterface(inPortOUT);
              // new edge to use for BellmanFord
              e.put(EdgeInOut, dist.get(input).get(output));
              // new vertices to consider for BellmanFord
              v.put(VertexOut.getName(), Double.POSITIVE_INFINITY);
            }
            v.put(VertexIn.getName(), Double.POSITIVE_INFINITY);
            // check if the incoming edge loops on the actor
            if (vertex.getAssociatedEdge(vertex.getInterface(input)).getSource() == vertex) {
              final SDFEdge loop = g.addEdge(g.getVertex(VertexIn.getAssociatedEdge(VertexIn.getInterface("in")).getSourceInterface().getName()), VertexIn);
              loop.setTargetInterface(VertexIn.getInterface("in"));
              loop.setSourceInterface(g.getVertex(VertexIn.getAssociatedEdge(VertexIn.getInterface("in")).getSourceInterface().getName()).getInterface("out"));
              e.put(loop, e.get(EdgeToIn));
              e.remove(EdgeToIn);
            }
          }
        }
        // Remove the hierarchical actor from the graph
        for (final SDFInterfaceVertex inter : vertex.getSources()) {
          e.remove(vertex.getAssociatedEdge(inter));
        }
        for (final SDFInterfaceVertex inter : vertex.getSinks()) {
          e.remove(vertex.getAssociatedEdge(inter));
        }
        g.removeVertex(vertex);
      } else {
        // not a hierarchical actor
        v.put(vertex.getName(), Double.POSITIVE_INFINITY);
      }
      // clear the map of distances, reused for the next hierarchical actor
      dist.clear();
    }

    ArrayList<SDFAbstractVertex> origin;

    // when at level zero
    if (g.getParentVertex() == null) {
      // pick a random source node
      origin = new ArrayList<>();
      origin.add(g.vertexSet().iterator().next());
    } else {
      // otherwise, source nodes of the shortest paths to compute are all the input interfaces
      origin = new ArrayList<>(new ArrayList<>(g.getParentVertex().getSources()));
    }

    // BellmanFord from each input
    for (final SDFAbstractVertex input : origin) {
      // Source node for the shortest path
      v.put(input.getName(), (double) 0);
      // Relaxation
      for (int i = 0; i < v.size(); i++) {
        for (final Map.Entry<SDFEdge, Double> entry : e.entrySet()) {
          if ((v.get(entry.getKey().getSource().getName()) + entry.getValue()) < v.get(entry.getKey().getTarget().getName())) {
            v.put(entry.getKey().getTarget().getName(), v.get(entry.getKey().getSource().getName()) + entry.getValue());
          }
        }
      }
      // Check for negative cycle
      for (final Map.Entry<SDFEdge, Double> entry : e.entrySet()) {
        if ((v.get(entry.getKey().getSource().getName()) + entry.getValue()) < v.get(entry.getKey().getTarget().getName())) {
          return null;
        }
      }
      // while we are not at level zero, fill the shortest paths table
      if (g.getParentVertex() != null) {
        dist.put(input.getName(), new HashMap<String, Double>());
        // distance from the input to all the outputs
        for (final SDFAbstractVertex output : g.getParentVertex().getSinks()) {
          dist.get(input.getName()).put(output.getName(), v.get(output.getName()));
        }
        // reset weight on vertices
        for (final SDFAbstractVertex ve : g.vertexSet()) {
          v.put(ve.getName(), Double.POSITIVE_INFINITY);
        }

      } else {
        return dist;
      }
    }
    return dist;
  }
}
