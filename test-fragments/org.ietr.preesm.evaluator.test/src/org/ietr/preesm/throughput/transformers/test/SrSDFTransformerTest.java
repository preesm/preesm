/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 * Hamza Deroui <hamza.deroui@insa-rennes.fr> (2017)
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
package org.ietr.preesm.throughput.transformers.test;

import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.throughput.tools.helpers.GraphStructureHelper;
import org.ietr.preesm.throughput.tools.transformers.SrSDFTransformer;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test of SrSDFTransformer class
 *
 * @author hderoui
 *
 */
public class SrSDFTransformerTest {

  @Test
  public void testSrSDFGraphShouldBeTranformedToHSDFGraph() {

    // generate a srSDF graph
    final SDFGraph srSDF = generateSrSDFGraphABC326();

    // convert the srSDF graph to an HSDF graph
    final SDFGraph hsdf = SrSDFTransformer.convertToHSDF(srSDF);

    // verify that the consumption/production rate of all edges equal 1
    for (final SDFEdge e : hsdf.edgeSet()) {
      final int cons = e.getCons().intValue();
      final int prod = e.getProd().intValue();

      Assert.assertEquals(1, cons);
      Assert.assertEquals(1, prod);

    }
  }

  @Test
  public void testSrSDFGraphShouldBeTranformedToDAG() {

    // generate a srSDF graph
    final SDFGraph srSDF = generateSrSDFGraphABC326();

    // convert the srSDF graph to a DAG
    final SDFGraph dag = SrSDFTransformer.convertToDAG(srSDF);

    // check if all the edges between Ai and Bi have been removed
    int nbEdge;
    nbEdge = dag.getVertex("A1").getSinks().size();
    Assert.assertEquals(0, nbEdge);

    nbEdge = dag.getVertex("A2").getSinks().size();
    Assert.assertEquals(0, nbEdge);

    nbEdge = dag.getVertex("A3").getSinks().size();
    Assert.assertEquals(0, nbEdge);

    // check if all the edges have zero delay
    for (final SDFEdge e : dag.edgeSet()) {
      final int delay = e.getDelay().intValue();
      Assert.assertEquals(0, delay);

    }
  }

  /**
   * generates a normalized SDF graph
   *
   * @return SDF graph
   */
  public SDFGraph generateSrSDFGraphABC326() {
    // Actors: A B C
    // Edges: AB=(2,3); BC=(3,1); CA=(1,2)
    // RV[A=3, B=2, C=6]
    // Actors duration: A=1, B=1, C=1
    // Duration of the first iteration = 3

    // create srSDF graph testABC326
    final SDFGraph graph = new SDFGraph();
    graph.setName("testABC326");

    // add actors
    GraphStructureHelper.addActor(graph, "A1", null, 1, 1., null, null);
    GraphStructureHelper.addActor(graph, "A2", null, 1, 1., null, null);
    GraphStructureHelper.addActor(graph, "A3", null, 1, 1., null, null);
    GraphStructureHelper.addActor(graph, "B1", null, 1, 1., null, null);
    GraphStructureHelper.addActor(graph, "B2", null, 1, 1., null, null);
    GraphStructureHelper.addActor(graph, "C1", null, 1, 1., null, null);
    GraphStructureHelper.addActor(graph, "C2", null, 1, 1., null, null);
    GraphStructureHelper.addActor(graph, "C3", null, 1, 1., null, null);
    GraphStructureHelper.addActor(graph, "C4", null, 1, 1., null, null);
    GraphStructureHelper.addActor(graph, "C5", null, 1, 1., null, null);
    GraphStructureHelper.addActor(graph, "C6", null, 1, 1., null, null);

    // add edges
    GraphStructureHelper.addEdge(graph, "A1", null, "B1", null, 2, 2, 2, null);
    GraphStructureHelper.addEdge(graph, "A2", null, "B1", null, 1, 1, 1, null);
    GraphStructureHelper.addEdge(graph, "A2", null, "B2", null, 1, 1, 1, null);
    GraphStructureHelper.addEdge(graph, "A3", null, "B2", null, 2, 2, 2, null);
    GraphStructureHelper.addEdge(graph, "B1", null, "C1", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "B1", null, "C2", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "B1", null, "C3", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "B2", null, "C4", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "B2", null, "C5", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "B2", null, "C6", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "C1", null, "A1", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "C2", null, "A1", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "C3", null, "A2", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "C4", null, "A2", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "C5", null, "A3", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "C6", null, "A3", null, 1, 1, 0, null);

    return graph;
  }
}
