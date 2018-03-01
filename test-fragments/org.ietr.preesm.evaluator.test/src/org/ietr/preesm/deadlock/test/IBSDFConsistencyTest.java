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

import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.types.SDFIntEdgePropertyType;
import org.ietr.preesm.deadlock.IBSDFConsistency;
import org.ietr.preesm.throughput.tools.helpers.GraphStructureHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * Unit test of IBSDFConsistency class
 * 
 * @author hderoui
 * 
 */
public class IBSDFConsistencyTest {

  @Test
  public void testRVShouldBeComputed() {
    // generate the IBSDF graph AB[DEF]C
    SDFGraph ABC = generateIBSDFGraph();
    // compute the repetition vector (RV) of AB[DEF]C
    IBSDFConsistency.computeRV(ABC);
    // check the RV value of the top-graph
    Assert.assertEquals(2, ABC.getVertex("A").getNbRepeat());
    Assert.assertEquals(3, ABC.getVertex("B").getNbRepeat());
    Assert.assertEquals(3, ABC.getVertex("C").getNbRepeat());

    // check the RV value of the sub-graph
    SDFGraph DEF = (SDFGraph) ABC.getVertex("B").getGraphDescription();
    Assert.assertEquals(2, DEF.getVertex("D").getNbRepeat());
    Assert.assertEquals(6, DEF.getVertex("E").getNbRepeat());
    Assert.assertEquals(4, DEF.getVertex("F").getNbRepeat());

    // TODO: check the consumption/production rates of interfaces
    SDFAbstractVertex in = DEF.getVertex("a");
    SDFEdge e = in.getAssociatedEdge(in.getSinks().iterator().next());
    Assert.assertEquals(6, e.getProd().intValue());

    SDFAbstractVertex out = DEF.getVertex("c");
    e = out.getAssociatedEdge(out.getSources().iterator().next());
    Assert.assertEquals(12, e.getCons().intValue());
  }

  @Test
  public void testConsistencyShouldBeEvaluated() {
    // generate the IBSDF graph AB[DEF]C (consistent)
    SDFGraph ibsdf = generateIBSDFGraph();
    // evaluate the consistency
    Boolean consistent = IBSDFConsistency.computeRV(ibsdf);
    Assert.assertTrue(consistent);

    // change the production rate of the edge EF so that the subgraph becomes non consistent
    SDFGraph subgraph = (SDFGraph) ibsdf.getVertex("B").getGraphDescription();
    SDFAbstractVertex E = subgraph.getVertex("E");
    E.getAssociatedEdge(E.getSinks().iterator().next()).setProd(new SDFIntEdgePropertyType(10));

    // evaluate the consistency
    consistent = IBSDFConsistency.computeRV(ibsdf);
    Assert.assertFalse(consistent);

  }

  /**
   * generate a IBSDF graph to test methods
   * 
   * @return IBSDF graph
   */
  public SDFGraph generateIBSDFGraph() {
    // Actors: A B[DEF] C
    // actor B is a hierarchical actor described by the subgraph DEF
    // a is the input interface of the subgraph DEF, associated with the input edge coming from A
    // the input interface is linked to the sub-actor E
    // c is the output interface of the subgraph DEF, associated with the output edge going to C
    // the output interface is linked to the sub-actor F

    // Edges of the top graph ABC : AB=(3,2); BC=(1,1); CA=(2,3)
    // Edges of the subgraph DEF : aE=(2,1); EF=(2,3); FD=(1,2); DE=(3,1); Fc=(3,1)

    // RV(top-graph) = [A=2, B=3, C=3]
    // RV(sub-graph) = [D=2, E=6, F=4]
    // after computing the RV of the subgraph the consumption/production rate of the interfaces are multiplied by their RV, then RV of interfaces is set to 1
    // the resulted rates of edges : aE=(6,1); Fc=(3,12)

    // create the subgraph
    SDFGraph subgraph = new SDFGraph();
    subgraph.setName("subgraph");
    GraphStructureHelper.addActor(subgraph, "D", null, null, null, null, null);
    GraphStructureHelper.addActor(subgraph, "E", null, null, null, null, null);
    GraphStructureHelper.addActor(subgraph, "F", null, null, null, null, null);
    GraphStructureHelper.addInputInterface(subgraph, "a", null, null, null, null);
    GraphStructureHelper.addOutputInterface(subgraph, "c", null, null, null, null);

    GraphStructureHelper.addEdge(subgraph, "a", null, "E", null, 2, 1, 0, null);
    GraphStructureHelper.addEdge(subgraph, "E", null, "F", null, 2, 3, 0, null);
    GraphStructureHelper.addEdge(subgraph, "F", null, "D", null, 1, 2, 2, null);
    GraphStructureHelper.addEdge(subgraph, "D", null, "E", null, 3, 1, 0, null);
    GraphStructureHelper.addEdge(subgraph, "F", null, "c", null, 3, 1, 0, null);

    // create the top graph and add the subgraph to the hierarchical actor B
    SDFGraph topgraph = new SDFGraph();
    topgraph.setName("topgraph");
    GraphStructureHelper.addActor(topgraph, "A", null, null, null, null, null);
    GraphStructureHelper.addActor(topgraph, "B", subgraph, null, null, null, null);
    GraphStructureHelper.addActor(topgraph, "C", null, null, null, null, null);

    GraphStructureHelper.addEdge(topgraph, "A", null, "B", "a", 3, 2, 0, null);
    GraphStructureHelper.addEdge(topgraph, "B", "c", "C", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(topgraph, "C", null, "A", null, 2, 3, 0, null);

    return topgraph;
  }

}