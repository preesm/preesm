/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 * Hamza Deroui <hamza.deroui@insa-rennes.fr> (2017 - 2018)
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
import java.util.Hashtable;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.ietr.preesm.deadlock.IBSDFConsistency;
import org.ietr.preesm.throughput.tools.helpers.GraphStructureHelper;
import org.ietr.preesm.throughput.tools.helpers.Stopwatch;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test of GraphStrucutureHelper class
 *
 * @author hderoui
 *
 */
public class GraphStrucutureHelperTest {

  @Test
  public void testNewActorShouldBeAdded() {

    // create an empty SDF graph
    final SDFGraph sdf = new SDFGraph();
    sdf.setName("test");

    // Add a new actor to the SDF graph
    GraphStructureHelper.addActor(sdf, "newActor", null, 3, 7., null, null);

    // check the results
    Assert.assertEquals(1, sdf.vertexSet().size());
    Assert.assertNotNull(sdf.getVertex("newActor"));
    Assert.assertEquals(3, sdf.getVertex("newActor").getNbRepeat());
    Assert.assertEquals(7, (double) sdf.getVertex("newActor").getPropertyBean().getValue("duration"), 0);
  }

  @Test
  public void testNewEdgeShouldBeAdded() {

    // create an empty SDF graph
    final SDFGraph sdf = new SDFGraph();
    sdf.setName("test");

    // Add actor A and B
    GraphStructureHelper.addActor(sdf, "A", null, null, null, null, null);
    GraphStructureHelper.addActor(sdf, "B", null, null, null, null, null);

    // Add the edge A to B
    GraphStructureHelper.addEdge(sdf, "A", null, "B", null, 2, 3, 5, null);

    // check the results
    Assert.assertEquals(1, sdf.edgeSet().size());
    final SDFEdge e = sdf.edgeSet().iterator().next();
    Assert.assertEquals(3, e.getCons().intValue());
    Assert.assertEquals(2, e.getProd().intValue());
    Assert.assertEquals(5, e.getDelay().intValue());
  }

  @Test
  public void testNewInputInterfaceShouldBeAdded() {

    // create an empty SDF graph
    final SDFGraph sdf = new SDFGraph();
    sdf.setName("test");

    // Add a new InputInterface to the SDF graph
    GraphStructureHelper.addInputInterface(sdf, "in", 1, 1., null, null);

    // check the results
    Assert.assertEquals(1, sdf.vertexSet().size());
    Assert.assertNotNull(sdf.getVertex("in"));
    Assert.assertTrue(sdf.getVertex("in") instanceof SDFSourceInterfaceVertex);
  }

  @Test
  public void testNewOutputInterfaceShouldBeAdded() {

    // create an empty SDF graph
    final SDFGraph sdf = new SDFGraph();
    sdf.setName("test");

    // Add a new OutputInterface to the SDF graph
    GraphStructureHelper.addOutputInterface(sdf, "out", 1, 1., null, null);

    // check the results
    Assert.assertEquals(1, sdf.vertexSet().size());
    Assert.assertNotNull(sdf.getVertex("out"));
    Assert.assertTrue(sdf.getVertex("out") instanceof SDFSinkInterfaceVertex);
  }

  @Test
  public void testNewSourcePortShouldBeAdded() {

    // create an empty SDF graph
    final SDFGraph sdf = new SDFGraph();
    sdf.setName("test");

    // Add actor A
    final SDFAbstractVertex A = GraphStructureHelper.addActor(sdf, "A", null, null, null, null, null);
    Assert.assertEquals(0, A.getSinks().size());
    Assert.assertEquals(0, A.getSources().size());

    // Add a source port to actor A
    GraphStructureHelper.addSrcPort(A, "srcPort", 2);
    Assert.assertEquals(0, A.getSinks().size());
    Assert.assertEquals(1, A.getSources().size());

    // check the results
    final int portRate = (Integer) A.getSources().iterator().next().getPropertyBean().getValue("port_rate");
    Assert.assertEquals(2, portRate);
  }

  @Test
  public void testNewSinkPortShouldBeAdded() {

    // create an empty SDF graph
    final SDFGraph sdf = new SDFGraph();
    sdf.setName("test");

    // Add actor A
    final SDFAbstractVertex A = GraphStructureHelper.addActor(sdf, "A", null, null, null, null, null);
    Assert.assertEquals(0, A.getSinks().size());
    Assert.assertEquals(0, A.getSources().size());

    // Add a sink port to actor A
    GraphStructureHelper.addSinkPort(A, "sinkPort", 3);
    Assert.assertEquals(1, A.getSinks().size());
    Assert.assertEquals(0, A.getSources().size());

    // check the results
    final int portRate = (Integer) A.getSinks().iterator().next().getPropertyBean().getValue("port_rate");
    Assert.assertEquals(3, portRate);
  }

  @Test
  public void testTargetActorShouldBeReplaced() {

    // create an empty SDF graph
    final SDFGraph sdf = new SDFGraph();
    sdf.setName("test");

    // Add actor A and B
    GraphStructureHelper.addActor(sdf, "A", null, null, null, null, null);
    GraphStructureHelper.addActor(sdf, "B", null, null, null, null, null);
    GraphStructureHelper.addActor(sdf, "C", null, null, null, null, null);

    // Add the edge A to B
    SDFEdge e = GraphStructureHelper.addEdge(sdf, "A", null, "B", null, 2, 3, 5, null);

    SDFAbstractVertex sourceActor = e.getSource();
    SDFAbstractVertex targetActor = e.getTarget();

    Assert.assertEquals("A", sourceActor.getName());
    Assert.assertEquals("B", targetActor.getName());

    Assert.assertEquals(0, sdf.getVertex("A").getSources().size());
    Assert.assertEquals(1, sdf.getVertex("A").getSinks().size());

    Assert.assertEquals(1, sdf.getVertex("B").getSources().size());
    Assert.assertEquals(0, sdf.getVertex("B").getSinks().size());

    Assert.assertEquals(0, sdf.getVertex("C").getSources().size());
    Assert.assertEquals(0, sdf.getVertex("C").getSinks().size());

    // replace the target actor B by actor C
    e = GraphStructureHelper.replaceEdgeTargetActor(sdf, e, "C", null);

    sourceActor = e.getSource();
    targetActor = e.getTarget();

    Assert.assertEquals("A", sourceActor.getName());
    Assert.assertEquals("C", targetActor.getName());

    Assert.assertEquals(0, sdf.getVertex("A").getSources().size());
    Assert.assertEquals(1, sdf.getVertex("A").getSinks().size());

    Assert.assertEquals(0, sdf.getVertex("B").getSources().size());
    Assert.assertEquals(0, sdf.getVertex("B").getSinks().size());

    Assert.assertEquals(1, sdf.getVertex("C").getSources().size());
    Assert.assertEquals(0, sdf.getVertex("C").getSinks().size());

  }

  @Test
  public void testSourceActorShouldBeReplaced() {

    // create an empty SDF graph
    final SDFGraph sdf = new SDFGraph();
    sdf.setName("test");

    // Add actor A and B
    GraphStructureHelper.addActor(sdf, "A", null, null, null, null, null);
    GraphStructureHelper.addActor(sdf, "B", null, null, null, null, null);
    GraphStructureHelper.addActor(sdf, "C", null, null, null, null, null);

    // Add the edge A to B
    SDFEdge e = GraphStructureHelper.addEdge(sdf, "A", null, "B", null, 2, 3, 5, null);

    SDFAbstractVertex sourceActor = e.getSource();
    SDFAbstractVertex targetActor = e.getTarget();

    Assert.assertEquals("A", sourceActor.getName());
    Assert.assertEquals("B", targetActor.getName());

    Assert.assertEquals(0, sdf.getVertex("A").getSources().size());
    Assert.assertEquals(1, sdf.getVertex("A").getSinks().size());

    Assert.assertEquals(1, sdf.getVertex("B").getSources().size());
    Assert.assertEquals(0, sdf.getVertex("B").getSinks().size());

    Assert.assertEquals(0, sdf.getVertex("C").getSources().size());
    Assert.assertEquals(0, sdf.getVertex("C").getSinks().size());

    // replace the target actor B by actor C
    e = GraphStructureHelper.replaceEdgeSourceActor(sdf, e, "C", null);

    sourceActor = e.getSource();
    targetActor = e.getTarget();

    Assert.assertEquals("C", sourceActor.getName());
    Assert.assertEquals("B", targetActor.getName());

    Assert.assertEquals(0, sdf.getVertex("A").getSources().size());
    Assert.assertEquals(0, sdf.getVertex("A").getSinks().size());

    Assert.assertEquals(1, sdf.getVertex("B").getSources().size());
    Assert.assertEquals(0, sdf.getVertex("B").getSinks().size());

    Assert.assertEquals(0, sdf.getVertex("C").getSources().size());
    Assert.assertEquals(1, sdf.getVertex("C").getSinks().size());

  }

  @Test
  public void testHierarchicalAcotrsShouldBeReturnd() {

    // generate an IBSDF graph
    final SDFGraph ibsdf = generateIBSDFGraph3levels();

    // get the hierarchical acotrs of the topgraph
    final Hashtable<String, SDFAbstractVertex> listOfHierarchicalActors = GraphStructureHelper.getHierarchicalActors(ibsdf);

    // check the results
    Assert.assertNotNull(listOfHierarchicalActors);
    Assert.assertEquals(1, listOfHierarchicalActors.size());
    Assert.assertEquals("B", listOfHierarchicalActors.entrySet().iterator().next().getKey());
  }

  @Test
  public void testAllHierarchicalAcotrsShouldBeReturnd() {

    // generate an IBSDF graph
    final SDFGraph ibsdf = generateIBSDFGraph3levels();

    // get the hierarchical acotrs of the topgraph
    final Hashtable<String, SDFAbstractVertex> listOfHierarchicalActors = GraphStructureHelper.getAllHierarchicalActors(ibsdf);

    // check the results
    Assert.assertNotNull(listOfHierarchicalActors);
    Assert.assertEquals(2, listOfHierarchicalActors.size());
    Assert.assertNotNull(listOfHierarchicalActors.get("B"));
    Assert.assertNotNull(listOfHierarchicalActors.get("D"));
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
    GraphStructureHelper.addActor(GH, "G", null, null, 1., null, null);
    GraphStructureHelper.addActor(GH, "H", null, null, 1., null, null);
    GraphStructureHelper.addInputInterface(GH, "f", null, 0., null, null);
    GraphStructureHelper.addOutputInterface(GH, "e", null, 0., null, null);

    GraphStructureHelper.addEdge(GH, "f", null, "G", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(GH, "G", null, "F", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(GH, "H", null, "e", null, 1, 1, 0, null);

    // create the subgraph DEF
    final SDFGraph DEF = new SDFGraph();
    DEF.setName("subgraph");
    GraphStructureHelper.addActor(DEF, "D", GH, null, 1., null, null);
    GraphStructureHelper.addActor(DEF, "E", null, null, 1., null, null);
    GraphStructureHelper.addActor(DEF, "F", null, null, 1., null, null);
    GraphStructureHelper.addInputInterface(DEF, "a", null, 0., null, null);
    GraphStructureHelper.addOutputInterface(DEF, "c", null, 0., null, null);

    GraphStructureHelper.addEdge(DEF, "a", null, "E", null, 2, 1, 0, null);
    GraphStructureHelper.addEdge(DEF, "E", null, "F", null, 2, 3, 0, null);
    GraphStructureHelper.addEdge(DEF, "F", null, "D", "f", 1, 2, 0, null);
    GraphStructureHelper.addEdge(DEF, "D", "e", "E", null, 3, 1, 3, null);
    GraphStructureHelper.addEdge(DEF, "F", null, "c", null, 3, 1, 0, null);

    // create the top graph and add the subgraph to the hierarchical actor B
    final SDFGraph topgraph = new SDFGraph();
    topgraph.setName("topgraph");
    GraphStructureHelper.addActor(topgraph, "A", null, null, 1., null, null);
    GraphStructureHelper.addActor(topgraph, "B", DEF, null, null, null, null);
    GraphStructureHelper.addActor(topgraph, "C", null, null, 1., null, null);

    GraphStructureHelper.addEdge(topgraph, "A", null, "B", "a", 3, 2, 3, null);
    GraphStructureHelper.addEdge(topgraph, "B", "c", "C", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(topgraph, "C", null, "A", null, 2, 3, 3, null);

    IBSDFConsistency.computeRV(topgraph);
    return topgraph;
  }

  @Test
  public void testTopologicalSorting() {
    // create the DAG to sort
    final SDFGraph dag = new SDFGraph();
    dag.setName("dag");
    GraphStructureHelper.addActor(dag, "0", null, 1, 1., null, null);
    GraphStructureHelper.addActor(dag, "1", null, 1, 1., null, null);
    GraphStructureHelper.addActor(dag, "2", null, 1, 1., null, null);
    GraphStructureHelper.addActor(dag, "3", null, 1, 1., null, null);
    GraphStructureHelper.addActor(dag, "4", null, 1, 1., null, null);
    GraphStructureHelper.addActor(dag, "5", null, 1, 1., null, null);

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

    System.out.println("topological sorting computed in " + timer.toString() + ", the ordered actors: ");

    // check the results
    Assert.assertNotNull(topologicalSorting);
    Assert.assertEquals(dag.vertexSet().size(), topologicalSorting.size());

    for (int i = 0; i < topologicalSorting.size(); i++) {
      System.out.print(topologicalSorting.get(i).getName() + " ");
      Assert.assertEquals(expectedList.get(i).getName(), topologicalSorting.get(i).getName());
    }

    System.out.println("\nPartial topological sorting computed in " + timer.toString() + ", the ordered actors: ");
    final ArrayList<SDFAbstractVertex> partialTopologicalSorting = GraphStructureHelper.partialTopologicalSorting(dag.getVertex("5"));
    for (int i = 0; i < partialTopologicalSorting.size(); i++) {
      System.out.print(partialTopologicalSorting.get(i).getName() + " ");
    }

  }

}
