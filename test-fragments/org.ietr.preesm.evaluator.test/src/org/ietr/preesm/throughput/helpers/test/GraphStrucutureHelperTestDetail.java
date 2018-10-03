/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 * Hamza Deroui <hamza.deroui@insa-rennes.fr> (2018)
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
package org.ietr.preesm.throughput.helpers.test;

import java.util.ArrayList;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.deadlock.IBSDFConsistency;
import org.ietr.preesm.throughput.tools.helpers.GraphStructureHelper;
import org.ietr.preesm.throughput.tools.helpers.Stopwatch;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Unit test of GraphStrucutureHelper class
 *
 * @author hderoui
 *
 */
public class GraphStrucutureHelperTestDetail {

  @Test
  @Ignore
  public void testTopologicalSorting() {
    // create the DAG to sort
    final SDFGraph dag = new SDFGraph();
    dag.setName("dag");
    GraphStructureHelper.addActor(dag, "0", null, 1L, 1., 0, null);
    GraphStructureHelper.addActor(dag, "1", null, 1L, 1., 0, null);
    GraphStructureHelper.addActor(dag, "2", null, 1L, 1., 0, null);
    GraphStructureHelper.addActor(dag, "3", null, 1L, 1., 0, null);
    GraphStructureHelper.addActor(dag, "4", null, 1L, 1., 0, null);
    GraphStructureHelper.addActor(dag, "5", null, 1L, 1., 0, null);

    GraphStructureHelper.addEdge(dag, "5", null, "2", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(dag, "5", null, "0", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(dag, "4", null, "0", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(dag, "4", null, "1", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(dag, "2", null, "3", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(dag, "3", null, "1", null, 1, 1, 0, null);

    // expected result of the topological sorting
    final ArrayList<SDFAbstractVertex> expectedList = new ArrayList<>();
    expectedList.add(0, dag.getVertex("5"));
    expectedList.add(1, dag.getVertex("4"));
    expectedList.add(2, dag.getVertex("2"));
    expectedList.add(3, dag.getVertex("3"));
    expectedList.add(4, dag.getVertex("1"));
    expectedList.add(5, dag.getVertex("0"));

    // topological sorting
    final Stopwatch timer = new Stopwatch();
    timer.start();
    final ArrayList<SDFAbstractVertex> topologicalSorting = GraphStructureHelper.topologicalSorting(dag);
    timer.stop();

    // check the results
    Assert.assertNotNull(topologicalSorting);
    Assert.assertEquals(dag.vertexSet().size(), topologicalSorting.size());

    for (int i = 0; i < topologicalSorting.size(); i++) {
      Assert.assertEquals(expectedList.get(i).getName(), topologicalSorting.get(i).getName());
    }

    GraphStructureHelper.partialTopologicalSorting(dag.getVertex("5"));

  }

  @Test
  public void testLongestpath() {
    // create the DAG to sort
    final SDFGraph dag = new SDFGraph();
    dag.setName("dag");
    GraphStructureHelper.addActor(dag, "0", null, 1L, 3., 0, null);
    GraphStructureHelper.addActor(dag, "1", null, 1L, 6., 0, null);
    GraphStructureHelper.addActor(dag, "2", null, 1L, 5., 0, null);
    GraphStructureHelper.addActor(dag, "3", null, 1L, -1., 0, null);
    GraphStructureHelper.addActor(dag, "4", null, 1L, -3., 0, null);
    GraphStructureHelper.addActor(dag, "5", null, 1L, 1., 0, null);

    GraphStructureHelper.addEdge(dag, "0", null, "1", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(dag, "0", null, "2", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(dag, "1", null, "3", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(dag, "1", null, "2", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(dag, "2", null, "4", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(dag, "2", null, "5", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(dag, "2", null, "3", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(dag, "3", null, "5", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(dag, "3", null, "4", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(dag, "4", null, "5", null, 1, 1, 0, null);

    // topological sorting
    final Stopwatch timer = new Stopwatch();
    timer.start();
    GraphStructureHelper.topologicalSorting(dag);
    timer.stop();

    final ArrayList<SDFAbstractVertex> partialTopologicalSorting = GraphStructureHelper
        .partialTopologicalSorting(dag.getVertex("0"));

    timer.start();
    GraphStructureHelper.getLongestPathToAllTargets(dag.getVertex("0"), null, partialTopologicalSorting);
    timer.stop();

    timer.start();
    GraphStructureHelper.getLongestPath(dag, null, null);
    timer.stop();

  }

  /**
   * generate an IBSDF graph to test methods
   *
   * @return IBSDF graph
   */
  public SDFGraph generateIBSDFGraph3levels() {
    // Actors: A B[D[GH]EF] C
    // actor B and D are hierarchical

    // level 0 (toplevel): A B C
    // level 1: D E F
    // level 2: GH

    // create the subgraph GH
    final SDFGraph GH = new SDFGraph();
    GH.setName("subgraph");
    GraphStructureHelper.addActor(GH, "G", null, 0, 1., 0, null);
    GraphStructureHelper.addActor(GH, "H", null, 0, 1., 0, null);
    GraphStructureHelper.addInputInterface(GH, "f", 0, 0., 0, null);
    GraphStructureHelper.addOutputInterface(GH, "e", 0, 0., 0, null);

    GraphStructureHelper.addEdge(GH, "f", null, "G", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(GH, "G", null, "F", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(GH, "H", null, "e", null, 1, 1, 0, null);

    // create the subgraph DEF
    final SDFGraph DEF = new SDFGraph();
    DEF.setName("subgraph");
    GraphStructureHelper.addActor(DEF, "D", GH, 0, 1., 0, null);
    GraphStructureHelper.addActor(DEF, "E", null, 0, 1., 0, null);
    GraphStructureHelper.addActor(DEF, "F", null, 0, 1., 0, null);
    GraphStructureHelper.addInputInterface(DEF, "a", 0, 0., 0, null);
    GraphStructureHelper.addOutputInterface(DEF, "c", 0, 0., 0, null);

    GraphStructureHelper.addEdge(DEF, "a", null, "E", null, 2, 1, 0, null);
    GraphStructureHelper.addEdge(DEF, "E", null, "F", null, 2, 3, 0, null);
    GraphStructureHelper.addEdge(DEF, "F", null, "D", "f", 1, 2, 0, null);
    GraphStructureHelper.addEdge(DEF, "D", "e", "E", null, 3, 1, 3, null);
    GraphStructureHelper.addEdge(DEF, "F", null, "c", null, 3, 1, 0, null);

    // create the top graph and add the subgraph to the hierarchical actor B
    final SDFGraph topgraph = new SDFGraph();
    topgraph.setName("topgraph");
    GraphStructureHelper.addActor(topgraph, "A", null, 0, 1., 0, null);
    GraphStructureHelper.addActor(topgraph, "B", DEF, 0, 0, 0, null);
    GraphStructureHelper.addActor(topgraph, "C", null, 0, 1., 0, null);

    GraphStructureHelper.addEdge(topgraph, "A", null, "B", "a", 3, 2, 3, null);
    GraphStructureHelper.addEdge(topgraph, "B", "c", "C", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(topgraph, "C", null, "A", null, 2, 3, 3, null);

    IBSDFConsistency.computeRV(topgraph);
    return topgraph;
  }

}
