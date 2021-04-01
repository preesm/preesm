/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2015 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * blaunay [bapt.launay@gmail.com] (2015)
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
package org.preesm.algorithm.evaluator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.preesm.algorithm.model.AbstractEdgePropertyType;
import org.preesm.algorithm.model.sdf.SDFAbstractVertex;
import org.preesm.algorithm.model.sdf.SDFEdge;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.model.sdf.SDFInterfaceVertex;
import org.preesm.algorithm.model.sdf.SDFVertex;
import org.preesm.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.preesm.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.math.MathFunctionsHelper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Component;

/**
 * Class used to search for the optimal periodic schedule and its throughput for a given hierarchical graph IBSDF.
 *
 * @author blaunay
 */
public class IBSDFThroughputEvaluator extends ThroughputEvaluator {

  private Scenario scenario;

  public IBSDFThroughputEvaluator(Scenario scenario) {
    this.scenario = scenario;
  }

  /**
   * Computes (not necessarily optimally) the throughput on the optimal periodic schedule (if it exists) of a given
   * graph under the given scenario.
   *
   * @param inputGraph
   *          the input graph
   * @return the double
   */
  @Override
  public double launch(final SDFGraph inputGraph) {

    // Find a lower bound on the minimal period by inspecting the bottom levels
    double kMin = startingPeriod(inputGraph);
    double k = 0;

    // Step 2 : Test if k_min a valid period for the graph
    if (testPeriod(kMin, inputGraph) != null) {
      k = kMin;
    } else {
      // Step 3 : Find a value for K_max
      double kMax = 10 * kMin;
      // increase Kmax until it is a valid period
      while (testPeriod(kMax, inputGraph) == null) {
        kMin = kMax;
        kMax *= 10;
        // avoid infinite loop if there is no periodic schedule
        if (kMax >= (Double.MAX_VALUE / 10)) {
          PreesmLogger.getLogger().log(Level.SEVERE, "No periodic schedule for this graph");
          return 0;
        }
      }

      // adjust the precision
      double eps = kMax / Math.pow(10, 6);
      k = kMax;
      // Step 4 : Improve (minimize) K
      while (Math.abs(kMax - kMin) > eps) {
        k = (kMax + kMin) / 2;
        if (testPeriod(k, inputGraph) != null) {
          kMax = k; // continue to search on the interval [Kmin,K]
        } else {
          kMin = k; // continue to search on the interval [K,Kmax]
          k = kMax;
        }
      }
    }
    return k;
  }

  /**
   * Computes a lower bound on the optimal normalized period, helpful to get the algorithm started.
   *
   * @param inputGraph
   *          the input graph
   * @return the double
   */
  private double startingPeriod(final SDFGraph inputGraph) {
    boolean hierarchical = false;
    double k;
    double kMax = 0;
    for (final SDFAbstractVertex vertex : inputGraph.vertexSet()) {
      // if hierarchical vertex, go check its subgraph
      if (vertex.getGraphDescription() instanceof SDFGraph) {
        k = startingPeriod((SDFGraph) vertex.getGraphDescription());
        if (k > kMax) {
          kMax = k;
        }
        hierarchical = true;
      }
    }
    // we are in a graph without sublevels of hierarchy,
    // compute the optimal period of this graph with the method for SDF
    if (!hierarchical) {
      final ThroughputEvaluator eval = new SDFThroughputEvaluator();
      eval.setScenar(this.getScenar());
      kMax = eval.launch(inputGraph);
    }
    return kMax;
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
  private Map<String, Map<String, Double>> testPeriod(final double K, final SDFGraph sdf) {
    final SDFGraph g = sdf.copy();

    // The set of edges that will be used to compute shortest paths
    final Map<SDFEdge, Double> e = new LinkedHashMap<>(g.edgeSet().size());
    // The set of vertices used to compute shortest paths
    final Map<String, Double> v = new LinkedHashMap<>();
    // Contains the results of the shortest paths
    Map<String, Map<String, Double>> dist = new LinkedHashMap<>();
    AbstractEdgePropertyType<?> eIn;
    AbstractEdgePropertyType<?> eOut;

    // Add looping edges on actors
    addLoopingEdgesOnActors(g);

    // Value all arcs of this level with L - K * H
    setEdgeValues(K, g, e);

    // We need a copy of the set of vertices, since we will add vertices in the original set
    // while going through its elements
    final Set<SDFAbstractVertex> vertexSetCopy = new LinkedHashSet<>(g.vertexSet());
    for (final SDFAbstractVertex vertex : vertexSetCopy) {
      // For each hierarchical actor
      if (vertex.getGraphDescription() instanceof SDFGraph) {

        // compute shortest paths between its in/out ports
        dist = testPeriod(K, (SDFGraph) vertex.getGraphDescription());

        // if null, then subgraph not alive, so the whole graph is not.
        if (dist == null) {
          return null;
        } else {
          // Create new nodes corresponding to the interfaces
          for (final Entry<String, Map<String, Double>> entry : dist.entrySet()) {
            final String input = entry.getKey();
            final Map<String, Double> inputValue = entry.getValue();
            // Create a new vertex for each new input interface
            final SDFAbstractVertex vertexIn = new SDFVertex(null);
            vertexIn.setName(input);
            // Create a new port for the incoming edge
            final SDFSourceInterfaceVertex inPortIN = new SDFSourceInterfaceVertex(null);
            inPortIN.setName("in");
            vertexIn.addSource(inPortIN);
            // Add it to the graph
            g.addVertex(vertexIn);
            // Create the new incoming edge of this node
            final SDFEdge edgeToIn = g.addEdge(vertex.getAssociatedEdge(vertex.getInterface(input)).getSource(),
                vertexIn);
            edgeToIn.setSourceInterface(vertex.getAssociatedEdge(vertex.getInterface(input)).getSourceInterface());
            edgeToIn.setTargetInterface(inPortIN);
            // Put the correct rates on the new edge
            eIn = vertex.getAssociatedEdge(vertex.getSources().get(0)).getCons();
            eOut = vertex.getAssociatedEdge(vertex.getSources().get(0)).getProd();
            edgeToIn.setCons(eOut);
            edgeToIn.setProd(eIn);
            // Put it on the list for the BellmanFord algo, remove the ancient one
            e.put(edgeToIn, e.get(vertex.getAssociatedEdge(vertex.getInterface(input))));

            // New node for each output interface
            for (final Entry<String, Double> entry2 : inputValue.entrySet()) {
              final String output = entry2.getKey();
              final double outputValue = entry2.getValue();
              SDFAbstractVertex vertexOut = g.getVertex(output);
              if (vertexOut == null) {
                // Create vertex out only if it does not exist already
                vertexOut = new SDFVertex(null);
                vertexOut.setName(output);
                // Create a new port port for the outgoing edge
                final SDFSinkInterfaceVertex outPortOUT = new SDFSinkInterfaceVertex(null);
                outPortOUT.setName("out");
                vertexOut.addSink(outPortOUT);
                g.addVertex(vertexOut);
                // Create the edge going from the node out if it does not loop
                if (vertex.getAssociatedEdge(vertex.getInterface(output)).getTarget() != vertex) {
                  final SDFEdge edgeFromOut = g.addEdge(vertexOut,
                      vertex.getAssociatedEdge(vertex.getInterface(output)).getTarget());
                  edgeFromOut
                      .setTargetInterface(vertex.getAssociatedEdge(vertex.getInterface(output)).getTargetInterface());

                  // Put the correct rates on the new edge
                  eIn = vertex.getAssociatedEdge(vertex.getSinks().get(0)).getProd();
                  eOut = vertex.getAssociatedEdge(vertex.getSinks().get(0)).getCons();
                  edgeFromOut.setCons(eOut);
                  edgeFromOut.setProd(eIn);

                  edgeFromOut.setSourceInterface(outPortOUT);
                  // Put it on the list for the BellmanFord algo, remove the ancient one
                  e.put(edgeFromOut, e.get(vertex.getAssociatedEdge(vertex.getInterface(output))));
                }
              }
              // Create the edge linking the new in and out
              final SDFEdge edgeInOut = g.addEdge(vertexIn, vertexOut);
              // Put the correct rates on the new edge
              eIn = vertex.getAssociatedEdge(vertex.getSources().get(0)).getCons();
              eOut = vertex.getAssociatedEdge(vertex.getSinks().get(0)).getProd();
              edgeInOut.setCons(eOut);
              edgeInOut.setProd(eIn);
              // port of origin of this edge
              final SDFSinkInterfaceVertex outPortIN = new SDFSinkInterfaceVertex(null);
              outPortIN.setName(output);
              vertexIn.addSink(outPortIN);
              edgeInOut.setSourceInterface(outPortIN);
              // target port of this edge
              final SDFSourceInterfaceVertex inPortOUT = new SDFSourceInterfaceVertex(null);
              inPortOUT.setName(vertexIn.getName());
              vertexOut.addSource(inPortOUT);
              edgeInOut.setTargetInterface(inPortOUT);
              // new edge to use for BellmanFord
              e.put(edgeInOut, outputValue);
              // new vertices to consider for BellmanFord
              v.put(vertexOut.getName(), Double.POSITIVE_INFINITY);
            }
            v.put(vertexIn.getName(), Double.POSITIVE_INFINITY);
            // check if the incoming edge loops on the actor
            if (vertex.getAssociatedEdge(vertex.getInterface(input)).getSource() == vertex) {
              final SDFEdge loop = g.addEdge(
                  g.getVertex(vertexIn.getAssociatedEdge(vertexIn.getInterface("in")).getSourceInterface().getName()),
                  vertexIn);
              loop.setTargetInterface(vertexIn.getInterface("in"));
              loop.setSourceInterface(
                  g.getVertex(vertexIn.getAssociatedEdge(vertexIn.getInterface("in")).getSourceInterface().getName())
                      .getInterface("out"));
              e.put(loop, e.get(edgeToIn));
              e.remove(edgeToIn);
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
          if ((v.get(entry.getKey().getSource().getName()) + entry.getValue()) < v
              .get(entry.getKey().getTarget().getName())) {
            v.put(entry.getKey().getTarget().getName(), v.get(entry.getKey().getSource().getName()) + entry.getValue());
          }
        }
      }
      // Check for negative cycle
      for (final Map.Entry<SDFEdge, Double> entry : e.entrySet()) {
        if ((v.get(entry.getKey().getSource().getName()) + entry.getValue()) < v
            .get(entry.getKey().getTarget().getName())) {
          // Cycle of negative weight found, condition not respected -> graph not alive
          return null;
        }
      }
      // while we are not at level zero, fill the shortest paths table
      if (g.getParentVertex() != null) {
        dist.put(input.getName(), new LinkedHashMap<String, Double>());
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

  private void setEdgeValues(final double K, final SDFGraph g, final Map<SDFEdge, Double> e) {
    double h;
    double l;
    for (final SDFEdge edge : g.edgeSet()) {
      if ((edge.getSource() instanceof SDFSourceInterfaceVertex)
          || (edge.getSource().getGraphDescription() instanceof SDFGraph)) {
        l = 0;
      } else {
        final AbstractVertex referencePiMMVertex = edge.getSource().getReferencePiVertex();
        if (referencePiMMVertex instanceof AbstractActor) {
          final Component component = scenario.getSimulationInfo().getMainOperator().getComponent();
          final AbstractActor actor = (AbstractActor) referencePiMMVertex;
          l = this.getScenar().getTimings().evaluateExecutionTimeOrDefault(actor, component);
        } else {
          l = 0;
        }
      }

      h = ((double) (edge.getDelay().getValue())
          + MathFunctionsHelper.gcd((double) (edge.getCons().getValue()), (double) (edge.getProd().getValue())))
          - (double) (edge.getCons().getValue());

      e.put(edge, -(l - (K * h)));
    }
  }

  private void addLoopingEdgesOnActors(final SDFGraph g) {
    for (final SDFAbstractVertex vertex : g.vertexSet()) {
      if (!(vertex.getGraphDescription() instanceof SDFGraph)) {
        final SDFEdge loop = g.addEdge(vertex, vertex);
        final SDFSourceInterfaceVertex in = new SDFSourceInterfaceVertex(null);
        in.setName(vertex.getName() + "In");
        final SDFSinkInterfaceVertex out = new SDFSinkInterfaceVertex(null);
        out.setName(vertex.getName() + "Out");
        AbstractEdgePropertyType<?> x;
        if (!vertex.getSources().isEmpty()) {
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
  }

  /**
   * Checks if the given graph (containing several levels of hierarchy) respects the condition of liveness. Recursive
   * function.
   *
   * @param g
   *          the g
   * @return null only if the graph does not respect the condition
   */
  public Map<String, Map<String, Double>> isAlive(final SDFGraph g) {

    // The set of edges that will be used to compute shortest paths
    final Map<SDFEdge, Double> e = new LinkedHashMap<>(g.edgeSet().size());
    // The set of vertices used to compute shortest paths
    final Map<String, Double> v = new LinkedHashMap<>();
    // Contains the results of the shortest paths
    Map<String, Map<String, Double>> dist = new LinkedHashMap<>();

    // Liveness
    // Value all arcs of this level with M0 + gcd - Zj
    for (final SDFEdge edge : g.edgeSet()) {
      e.put(edge,
          (((double) (edge.getDelay().getValue())
              + MathFunctionsHelper.gcd((double) (edge.getCons().getValue()), (double) (edge.getProd().getValue())))
              - (double) (edge.getCons().getValue())));
    }

    // We need a copy of the set of vertices, since we will add vertices in the original set
    // while going through its elements
    final Set<SDFAbstractVertex> vertexSetCopy = new LinkedHashSet<>(g.vertexSet());
    for (final SDFAbstractVertex vertex : vertexSetCopy) {
      // For each hierarchical actor
      if (vertex.getGraphDescription() instanceof SDFGraph) {

        // compute shortest paths between its in/out ports
        dist = isAlive((SDFGraph) vertex.getGraphDescription());

        // if null, then subgraph not alive, so the whole graph is not.
        if (dist == null) {
          return null;
        } else {
          // Create new nodes corresponding to the interfaces
          for (final Entry<String, Map<String, Double>> entry : dist.entrySet()) {
            final String input = entry.getKey();
            final Map<String, Double> inputValue = entry.getValue();
            // Create a new vertex for each new input interface
            final SDFVertex vertexIn = new SDFVertex(null);
            vertexIn.setName(input);
            // Create a new port for the incoming edge
            final SDFSourceInterfaceVertex inPortIN = new SDFSourceInterfaceVertex(null);
            inPortIN.setName("in");
            vertexIn.addSource(inPortIN);
            // Add it to the graph
            g.addVertex(vertexIn);
            // Create the new incoming edge of this node
            final SDFEdge edgeToIn = g.addEdge(vertex.getAssociatedEdge(vertex.getInterface(input)).getSource(),
                vertexIn);
            edgeToIn.setSourceInterface(vertex.getAssociatedEdge(vertex.getInterface(input)).getSourceInterface());
            edgeToIn.setTargetInterface(inPortIN);
            // Put it on the list for the BellmanFord algo
            e.put(edgeToIn, e.get(vertex.getAssociatedEdge(vertex.getInterface(input))));

            // New node for each output interface
            for (final Entry<String, Double> entry2 : inputValue.entrySet()) {
              final String output = entry2.getKey();
              SDFAbstractVertex vertexOut = g.getVertex(output);
              if (vertexOut == null) {
                // Create vertex out only if it does not exist already
                vertexOut = new SDFVertex(null);
                vertexOut.setName(output);
                // Create a new port port for the outgoing edge
                final SDFSinkInterfaceVertex outPortOUT = new SDFSinkInterfaceVertex(null);
                outPortOUT.setName("out");
                vertexOut.addSink(outPortOUT);
                g.addVertex(vertexOut);
                // Create the edge going from the node out
                if (vertex.getAssociatedEdge(vertex.getInterface(output)).getTarget() != vertex) {
                  final SDFEdge edgeFromOut = g.addEdge(vertexOut,
                      vertex.getAssociatedEdge(vertex.getInterface(output)).getTarget());
                  edgeFromOut
                      .setTargetInterface(vertex.getAssociatedEdge(vertex.getInterface(output)).getTargetInterface());
                  edgeFromOut.setSourceInterface(vertexOut.getSink("out"));
                  // Put it on the list for the BellmanFord algo, remove the ancient one
                  e.put(edgeFromOut, e.get(vertex.getAssociatedEdge(vertex.getInterface(output))));
                }
              }
              // Create the edge linking the new in and out
              final SDFEdge edgeInOut = g.addEdge(vertexIn, vertexOut);
              // port of origin of this edge
              final SDFSinkInterfaceVertex outPortIN = new SDFSinkInterfaceVertex(null);
              outPortIN.setName(output);
              vertexIn.addSink(outPortIN);
              edgeInOut.setSourceInterface(outPortIN);
              // target port of this edge
              final SDFSourceInterfaceVertex inPortOUT = new SDFSourceInterfaceVertex(null);
              inPortOUT.setName(vertexIn.getName());
              vertexOut.addSource(inPortOUT);
              edgeInOut.setTargetInterface(inPortOUT);
              // new edge to use for BellmanFord
              e.put(edgeInOut, entry2.getValue());
              // new vertices to consider for BellmanFord
              v.put(vertexOut.getName(), Double.POSITIVE_INFINITY);
            }
            v.put(vertexIn.getName(), Double.POSITIVE_INFINITY);
            // check if the incoming edge loops on the actor
            if (vertex.getAssociatedEdge(vertex.getInterface(input)).getSource() == vertex) {
              final SDFEdge loop = g.addEdge(
                  g.getVertex(vertexIn.getAssociatedEdge(vertexIn.getInterface("in")).getSourceInterface().getName()),
                  vertexIn);
              loop.setTargetInterface(vertexIn.getInterface("in"));
              loop.setSourceInterface(
                  g.getVertex(vertexIn.getAssociatedEdge(vertexIn.getInterface("in")).getSourceInterface().getName())
                      .getInterface("out"));
              e.put(loop, e.get(edgeToIn));
              e.remove(edgeToIn);
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
          if ((v.get(entry.getKey().getSource().getName()) + entry.getValue()) < v
              .get(entry.getKey().getTarget().getName())) {
            v.put(entry.getKey().getTarget().getName(), v.get(entry.getKey().getSource().getName()) + entry.getValue());
          }
        }
      }
      // Check for negative cycle
      for (final Map.Entry<SDFEdge, Double> entry : e.entrySet()) {
        if ((v.get(entry.getKey().getSource().getName()) + entry.getValue()) < v
            .get(entry.getKey().getTarget().getName())) {
          return null;
        }
      }
      // while we are not at level zero, fill the shortest paths table
      if (g.getParentVertex() != null) {
        dist.put(input.getName(), new LinkedHashMap<String, Double>());
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
