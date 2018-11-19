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
package org.ietr.preesm.deadlock.test;

import org.ietr.preesm.deadlock.SDFConsistency;
import org.ietr.preesm.evaluator.EvaluationException;
import org.ietr.preesm.throughput.tools.helpers.GraphStructureHelper;
import org.junit.Assert;
import org.junit.Test;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.model.types.LongEdgePropertyType;

/**
 *
 * Unit test of SDFConsistency class
 *
 * @author hderoui
 *
 */
public class SDFConsistencyTest {

  @Test
  public void testRVShouldBeComputed() {
    // generate the SDF graph ABC326
    final SDFGraph ABC = generateSDFGraphABC326();
    // compute the repetition vector (RV) of ABC326
    SDFConsistency.computeRV(ABC);
    // check the RV value
    Assert.assertEquals(3L, ABC.getVertex("A").getNbRepeat());
    Assert.assertEquals(2L, ABC.getVertex("B").getNbRepeat());
    Assert.assertEquals(6L, ABC.getVertex("C").getNbRepeat());
  }

  @Test
  public void testConsistencyShouldBeEvaluated() {
    // generate the SDF graph ABC326 (consistent)
    final SDFGraph ABC = generateSDFGraphABC326();
    // evaluate the consistency
    Boolean consistent = SDFConsistency.computeRV(ABC);
    Assert.assertTrue(consistent);

    // change the consumption rate of a random edge so that the graph becomes non consistent
    ABC.edgeSet().iterator().next().setCons(new LongEdgePropertyType(10));
    // evaluate the consistency

    try {
      SDFConsistency.computeRV(ABC);
      Assert.fail();
    } catch (EvaluationException e) {
      // success
    }
  }

  /**
   * generate a SDF graph to test methods
   *
   * @return SDF graph
   */
  public SDFGraph generateSDFGraphABC326() {
    // Actors: A B C
    // Edges: AB=(2,3); BC=(3,1); CA=(1,2)
    // RV[A=3, B=2, C=6]

    // create SDF graph testABC326
    final SDFGraph graph = new SDFGraph();
    graph.setName("testABC326");

    // add actors
    GraphStructureHelper.addActor(graph, "A", null, 0, 0, 0, null);
    GraphStructureHelper.addActor(graph, "B", null, 0, 0, 0, null);
    GraphStructureHelper.addActor(graph, "C", null, 0, 0, 0, null);

    // add edges
    GraphStructureHelper.addEdge(graph, "A", null, "B", null, 2, 3, 0, null);
    GraphStructureHelper.addEdge(graph, "B", null, "C", null, 3, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "C", null, "A", null, 1, 2, 0, null);

    return graph;
  }

}
