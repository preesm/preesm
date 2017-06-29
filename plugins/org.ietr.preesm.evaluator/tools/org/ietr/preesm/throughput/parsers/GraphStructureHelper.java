package org.ietr.preesm.throughput.parsers;

import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.types.SDFIntEdgePropertyType;

/**
 * @author hderoui
 *
 *         A structure helper to simplify the manipulation of an SDF graph structure
 * 
 */
public abstract class GraphStructureHelper {

  /**
   * Adds a new edge to an SDF graph.
   * 
   * @param graph
   *          SDF graph
   * @param srcActorName
   *          the name of the source actor
   * @param srcPortName
   *          the name of the source port
   * @param trgActorName
   *          the name of the target actor
   * @param trgPortName
   *          the name of the target port
   * @param prod
   *          the production rate of the edge
   * @param cons
   *          the consumption of the edge
   * @param d
   *          the delay of the edge
   * @param base
   *          the base edge
   * @return the created edge
   */
  public static SDFEdge addEdge(SDFGraph graph, String srcActorName, String srcPortName, String trgActorName, String trgPortName, int prod_rate, int cons_rate,
      int delay, SDFEdge base) {
    // get the source actor if not exists create one
    SDFAbstractVertex srcActor = graph.getVertex(srcActorName);
    if (srcActor == null) {
      srcActor = addActor(graph, srcActorName, null, null, null, null, null);
    }
    // get the source port if not create one
    SDFInterfaceVertex srcPort = srcActor.getInterface(srcPortName);
    if (srcPort == null) {
      srcPort = addSinkPort(srcActor, srcPortName, prod_rate);
    }

    // get the target actor if not exists create one
    SDFAbstractVertex tgtActor = graph.getVertex(trgActorName);
    if (tgtActor == null) {
      tgtActor = addActor(graph, trgActorName, null, null, null, null, null);
    }
    // get the target port if not exists create one
    SDFInterfaceVertex tgtPort = tgtActor.getInterface(trgPortName);
    if (tgtPort == null) {
      tgtPort = addSrcPort(tgtActor, trgPortName, cons_rate);
    }

    // add the edge to the srSDF graph
    SDFEdge edge = graph.addEdge(srcActor, srcPort, tgtActor, tgtPort);
    // edge.setPropertyValue("edgeName", "from_" + e.getSource().getName() + "_" + i + "_to_" + e.getTarget().getName() + "_" + j);
    edge.setProd(new SDFIntEdgePropertyType(prod_rate));
    edge.setCons(new SDFIntEdgePropertyType(cons_rate));
    edge.setDelay(new SDFIntEdgePropertyType(delay));
    edge.setPropertyValue("baseEdge", base);

    return edge;
  }

  /**
   * Adds an actor to an SDF graph
   * 
   * @param graph
   *          SDF graph
   * @param actorName
   *          name of the actor
   * @param subgraph
   *          subgraph of the actor if is hierarchical
   * @param rv
   *          repetition factor of the actor
   * @param l
   *          the execution duration of the actor
   * @param z
   *          the normalized consumption/production rate of the actor
   * @param Base
   *          the actor base
   * @return the created actor
   */
  public static SDFVertex addActor(SDFGraph graph, String actorName, SDFGraph subgraph, Integer rv, Double l, Double z, SDFAbstractVertex Base) {
    SDFVertex actor = new SDFVertex(graph);
    // set the name
    actor.setId(actorName);
    actor.setName(actorName);

    // add the subgraph if the actor is hierarchical
    if (subgraph != null) {
      actor.setGraphDescription(subgraph);
    }

    // set the repetition factor
    if (rv != null) {
      actor.setNbRepeat(rv);
    }

    // set the execution duration
    if (l != null) {
      actor.setPropertyValue("duration", l);
    }

    // set the normalized consumption/production rate
    if (z != null) {
      actor.setPropertyValue("normalizedRate", z);
    }

    // set the base actor
    if (Base != null) {
      actor.setPropertyValue("baseActor", Base);
    } else {
      actor.setPropertyValue("baseActor", actor);
    }

    graph.addVertex(actor);
    return actor;
  }

  /**
   * Adds a source port to an actor
   * 
   * @param actor
   *          SDF actor
   * @param portName
   *          name of the port
   * @param portRate
   *          rate of the port
   * @return created port
   */
  public static SDFSourceInterfaceVertex addSrcPort(SDFAbstractVertex actor, String portName, Integer portRate) {
    SDFSourceInterfaceVertex port = new SDFSourceInterfaceVertex();
    port.setId(portName);
    port.setName(portName);
    port.setPropertyValue("port_rate", portRate);
    actor.addInterface(port);
    return port;
  }

  /**
   * Adds a sink port to an actor
   * 
   * @param actor
   *          SDF actor
   * @param portName
   *          name of the actor
   * @param portRate
   *          rate of the actor
   * @return created port
   */
  public static SDFSinkInterfaceVertex addSinkPort(SDFAbstractVertex actor, String portName, Integer portRate) {
    SDFSinkInterfaceVertex port = new SDFSinkInterfaceVertex();
    port.setId(portName);
    port.setName(portName);
    port.setPropertyValue("port_rate", portRate);
    actor.addInterface(port);
    return port;
  }
}
