package org.ietr.preesm.throughput.parsers;

/**
 * @author hderoui
 *
 *         Generates id's for graphs, actors, edges, and ports
 */
public abstract class Identifier {

  public static int SDFGraphId          = 0;
  public static int actorId             = 0;
  public static int InputPortId         = 0;
  public static int OutputPortId        = 0;
  public static int edgeId              = 0;
  public static int HierarchicalActorId = 0;
  public static int InputInterfaceId    = 0;
  public static int OutputInterfaceId   = 0;

  /**
   * generates an Id for a graph
   * 
   * @return id
   */
  public static String generateSDFGraphId() {
    String id = "g" + SDFGraphId;
    SDFGraphId++;
    return id;
  }

  /**
   * generates an Id for an actor
   * 
   * @return id
   */
  public static String generateActorId() {
    String id = "a" + actorId;
    actorId++;
    return id;
  }

  /**
   * generates an Id for an edge
   * 
   * @return id
   */
  public static String generateEdgeId() {
    String id = "e" + edgeId;
    edgeId++;
    return id;
  }

  /**
   * generates an Id for a hierarchical actor
   * 
   * @return id
   */
  public static String generateHierarchicalActorId() {
    String id = "H" + HierarchicalActorId;
    HierarchicalActorId++;
    return id;
  }

  /**
   * generates an Id for an input interface
   * 
   * @return id
   */
  public static String generateInputInterfaceId() {
    String id = "inI" + InputInterfaceId;
    InputInterfaceId++;
    return id;
  }

  /**
   * generates an Id for an output interface
   * 
   * @return id
   */
  public static String generateOutputInterfaceId() {
    String id = "outI" + OutputInterfaceId;
    OutputInterfaceId++;
    return id;
  }

  /**
   * generates an Id for an input port
   * 
   * @return id
   */
  public static String generateInputPortId() {
    String id = "inP" + InputPortId;
    InputPortId++;
    return id;
  }

  /**
   * generates an Id for an output port
   * 
   * @return id
   */
  public static String generateOutputPortId() {
    String id = "OutP" + OutputPortId;
    OutputPortId++;
    return id;
  }

  /**
   * reset the Id generator
   */
  public static void reset() {
    SDFGraphId = 0;
    actorId = 0;
    InputPortId = 0;
    OutputPortId = 0;
    edgeId = 0;
    HierarchicalActorId = 0;
    InputInterfaceId = 0;
    OutputInterfaceId = 0;
  }
}
