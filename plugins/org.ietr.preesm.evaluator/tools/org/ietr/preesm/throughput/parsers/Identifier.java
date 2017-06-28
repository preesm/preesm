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

  public static String generateSDFGraphId() {
    String id = "g" + SDFGraphId;
    SDFGraphId++;
    return id;
  }

  public static String generateActorId() {
    String id = "a" + actorId;
    actorId++;
    return id;
  }

  public static String generateEdgeId() {
    String id = "e" + edgeId;
    edgeId++;
    return id;
  }

  public static String generateHierarchicalActorId() {
    String id = "H" + HierarchicalActorId;
    HierarchicalActorId++;
    return id;
  }

  public static String generateInputInterfaceId() {
    String id = "inI" + InputInterfaceId;
    InputInterfaceId++;
    return id;
  }

  public static String generateOutputInterfaceId() {
    String id = "outI" + OutputInterfaceId;
    OutputInterfaceId++;
    return id;
  }

  public static String generateInputPortId() {
    String id = "inP" + InputPortId;
    InputPortId++;
    return id;
  }

  public static String generateOutputPortId() {
    String id = "OutP" + OutputPortId;
    OutputPortId++;
    return id;
  }

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
