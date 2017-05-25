package org.abo.preesm.plugin.dataparallel.test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.abo.preesm.plugin.dataparallel.DAGConstructor;
import org.abo.preesm.plugin.dataparallel.SubsetTopologicalIterator;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;

/**
 * Utility functions helpful for testing
 * 
 * @author sudeep
 *
 */
public class Util {
  /**
   * Selects a root vertex based on the name given
   * 
   * @param graph
   *          DAG or SDF graph
   * @param vertexName
   *          Name of the root node
   * @return Root node
   * @throws NoSuchElementException
   *           if no such name is found
   */
  public static SDFAbstractVertex getRootNode(SDFGraph graph, String vertexName) throws NoSuchElementException {
    for (SDFAbstractVertex vertex : graph.getAllVertices()) {
      if (vertex.getName().equals(vertexName)) {
        return vertex;
      }
    }
    throw (new NoSuchElementException("No vertex named: " + vertexName + " found in the graph"));
  }

  /**
   * Return the array containing names of the vertices from the DAG for the associated rootNode
   * 
   * @param dagGen
   *          The DAG constructor object
   * @param nodeName
   *          Root node
   * @return Array of node names
   */
  public static String[] getNodes(DAGConstructor dagGen, String nodeName) {
    SDFAbstractVertex rootNode = Util.getRootNode(dagGen.getOutputGraph(), nodeName);
    SubsetTopologicalIterator it = new SubsetTopologicalIterator(dagGen, rootNode);
    List<String> seenNodes = new ArrayList<>();
    while (it.hasNext()) {
      seenNodes.add(it.next().getName());
    }
    System.out.println(seenNodes + "\n");
    return seenNodes.toArray(new String[seenNodes.size()]);
  }
}
