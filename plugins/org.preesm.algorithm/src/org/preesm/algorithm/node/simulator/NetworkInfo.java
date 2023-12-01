package org.preesm.algorithm.node.simulator;

public class NetworkInfo {
  String type;
  int    node;
  Double throughput;
  Double memory;
  Double energy;
  Double cost;

  public NetworkInfo(String type, int node, Double throughput, Double memory, Double energy, Double cost) {
    this.type = type;
    this.node = node;
    this.throughput = throughput;
    this.memory = memory;
    this.energy = energy;
    this.cost = cost;
  }

  public String getType() {
    return type;
  }

  public int getNode() {
    return node;
  }

  public Double getThroughput() {
    return throughput;
  }

  public Double getMemory() {
    return memory;
  }

  public Double getEnergy() {
    return energy;
  }

  public Double getCost() {
    return cost;
  }

}
