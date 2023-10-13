package org.preesm.algorithm.node.partitioner;

import java.util.Comparator;
import java.util.List;
import org.preesm.algorithm.mapping.model.NodeMapping;
import org.preesm.model.pisdf.PiGraph;

public class NodeCompararator {
  int               totalCoreCount;
  List<NodeMapping> hierarchicalArchitecture;
  int               piGraphActorCount;

  public NodeCompararator(int totalCoreCount, List<NodeMapping> hierarchicalArchitecture, PiGraph graph) {
    this.totalCoreCount = totalCoreCount;
    this.hierarchicalArchitecture = hierarchicalArchitecture;
    this.piGraphActorCount = graph.getActorIndex();

  }

  // Define a custom comparator for sorting the nodes
  public Comparator<NodeMapping> nodeComparator = (node1, node2) -> {
    final Boolean variableFrequency = node2.getCores().size() < node2.getNbCoreEquivalent()
        || node1.getCores().size() < node1.getNbCoreEquivalent();
    // If the PiGraph has fewer actors than the total core count, sort in descending order
    if (piGraphActorCount < totalCoreCount) {
      return Integer.compare(node2.getCores().size(), node1.getCores().size());
    }
    // If core frequencies are variable, sort nodes with higher frequency first
    if (Boolean.TRUE.equals(variableFrequency)) {
      final int frequencyComparison = Integer.compare(node2.getNbCoreEquivalent() - node2.getCores().size(),
          node1.getNbCoreEquivalent() - node1.getCores().size());
      if (frequencyComparison != 0) {
        return frequencyComparison;
      }
    }
    // Sort nodes in ascending order until the middle, then in descending order
    final int middle = hierarchicalArchitecture.size() / 2;
    final int position1 = node1.getID();
    final int position2 = node2.getID();
    if (position1 < middle && position2 < middle) {
      return Integer.compare(node1.getCores().size(), node2.getCores().size());
    }
    if (position1 >= middle && position2 >= middle) {
      return Integer.compare(node2.getCores().size(), node1.getCores().size());
    }
    return Integer.compare(position1, position2);
  };

}
