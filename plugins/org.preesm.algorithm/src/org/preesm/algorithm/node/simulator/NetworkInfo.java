package org.preesm.algorithm.node.simulator;

/**
 * This class represents network information for a specific configuration. It stores details such as network type,
 * number of nodes, throughput, memory usage, energy consumption, and cost.
 *
 * The class provides methods to access and retrieve information about each attribute. Additionally, it includes a
 * method to map network types to unique identifiers (type IDs).
 *
 * @author orenaud
 */
public class NetworkInfo {

  public static final String CLUSTER_CROSSBAR = "Cluster with crossbar";
  public static final String CLUSTER_SHARED   = "Cluster with shared backbone";
  public static final String TORUS            = "Torus cluster";
  public static final String FAT_TREE         = "Fat-tree cluster";
  public static final String DRAGONFLY        = "Dragonfly cluster";

  String type;
  int    node;
  int    core;
  int    coreFrequency;
  Double finalLatency;
  Double memory;
  Double energy;
  Double cost;

  /**
   * Constructs a NetworkInfo object with the given parameters.
   *
   * @param type
   *          The type of network.
   * @param node
   *          The number of nodes in the network.
   * @param throughput
   *          The network throughput.
   * @param memory
   *          The memory usage of the network.
   * @param energy
   *          The energy consumption of the network.
   * @param cost
   *          The cost of the network.
   */
  public NetworkInfo(String type, int node, int core, int coreFrequency, Double throughput, Double memory,
      Double energy, Double cost) {
    this.type = type;
    this.node = node;
    this.core = core;
    this.coreFrequency = coreFrequency;
    this.finalLatency = throughput;
    this.memory = memory;
    this.energy = energy;
    this.cost = cost;
  }

  /**
   * Returns the type of the network.
   *
   * @return The network type.
   */
  public String getType() {
    return type;
  }

  /**
   * Maps a network type to a unique identifier (type ID).
   *
   * @return The corresponding type ID.
   */
  public int getTypeID() {
    return switch (type) {
      case CLUSTER_CROSSBAR -> 1;
      case CLUSTER_SHARED -> 2;
      case TORUS -> 3;
      case FAT_TREE -> 4;
      case DRAGONFLY -> 5;
      default -> 0;
    };
  }

  /**
   * Returns the number of nodes in the network.
   *
   * @return The number of nodes.
   */
  public int getNode() {
    return node;
  }

  public int getCore() {
    return core;
  }

  public int getCoreFrequency() {
    return coreFrequency;
  }

  /**
   * Returns the final Latency of the network.
   *
   * @return The network final Latency.
   */
  public Double getFinalLatency() {
    return finalLatency;
  }

  /**
   * Returns the memory usage of the network.
   *
   * @return The memory usage.
   */
  public Double getMemory() {
    return memory;
  }

  /**
   * Returns the energy consumption of the network.
   *
   * @return The energy consumption.
   */
  public Double getEnergy() {
    return energy;
  }

  /**
   * Returns the cost of the network.
   *
   * @return The network cost.
   */
  public Double getCost() {
    return cost;
  }

}
