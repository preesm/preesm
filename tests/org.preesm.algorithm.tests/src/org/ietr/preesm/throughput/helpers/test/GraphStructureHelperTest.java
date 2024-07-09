/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Hamza Deroui [hamza.deroui@insa-rennes.fr] (2017 - 2018)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2022)
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
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.preesm.algorithm.deadlock.IBSDFConsistency;
import org.preesm.algorithm.model.sdf.SDFAbstractVertex;
import org.preesm.algorithm.model.sdf.SDFEdge;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.preesm.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.preesm.algorithm.throughput.sdf.tools.GraphStructureHelper;

/**
 * Unit test of GraphStructureHelper class
 *
 * @author hderoui
 *
 */
public class GraphStructureHelperTest {

  @Test
  public void testNewActorShouldBeAdded() {

    final String actorName = "newActor";

    // create an empty SDF graph
    final SDFGraph sdf = new SDFGraph();
    sdf.setName("test");

    // Add a new actor to the SDF graph
    GraphStructureHelper.addActor(sdf, actorName, null, 3L, 7., 0, null);

    // check the results
    Assert.assertEquals(1, sdf.vertexSet().size());
    Assert.assertNotNull(sdf.getVertex(actorName));
    Assert.assertEquals(3L, sdf.getVertex(actorName).getNbRepeat());
    Assert.assertEquals(7, (double) sdf.getVertex(actorName).getPropertyBean().getValue("duration"), 0);
  }

  @Test
  public void testNewEdgeShouldBeAdded() {

    // create an empty SDF graph
    final SDFGraph sdf = new SDFGraph();
    sdf.setName("test");

    // Add actor A and B
    GraphStructureHelper.addActor(sdf, "A", null, 0, 0, 0, null);
    GraphStructureHelper.addActor(sdf, "B", null, 0, 0, 0, null);

    // Add the edge A to B
    GraphStructureHelper.addEdge(sdf, "A", null, "B", null, 2, 3, 5, null);

    // check the results
    Assert.assertEquals(1, sdf.edgeSet().size());
    final SDFEdge e = sdf.edgeSet().iterator().next();
    Assert.assertEquals(3L, e.getCons().longValue());
    Assert.assertEquals(2L, e.getProd().longValue());
    Assert.assertEquals(5L, e.getDelay().longValue());
  }

  @Test
  public void testNewInputInterfaceShouldBeAdded() {

    // create an empty SDF graph
    final SDFGraph sdf = new SDFGraph();
    sdf.setName("test");

    // Add a new InputInterface to the SDF graph
    GraphStructureHelper.addInputInterface(sdf, "in", 1, 1., 0, null);

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
    GraphStructureHelper.addOutputInterface(sdf, "out", 1, 1., 0, null);

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

    // Add actor
    final SDFAbstractVertex vertex = GraphStructureHelper.addActor(sdf, "A", null, 0, 0, 0, null);
    Assert.assertEquals(0, vertex.getSinks().size());
    Assert.assertEquals(0, vertex.getSources().size());

    // Add a source port to actor
    GraphStructureHelper.addSrcPort(vertex, "srcPort", 2);
    Assert.assertEquals(0, vertex.getSinks().size());
    Assert.assertEquals(1, vertex.getSources().size());

    // check the results
    final long portRate = (long) vertex.getSources().iterator().next().getPropertyBean().getValue("port_rate");
    Assert.assertEquals(2, portRate);
  }

  @Test
  public void testNewSinkPortShouldBeAdded() {

    // create an empty SDF graph
    final SDFGraph sdf = new SDFGraph();
    sdf.setName("test");

    // Add actor
    final SDFAbstractVertex vertex = GraphStructureHelper.addActor(sdf, "A", null, 0, 0, 0, null);
    Assert.assertEquals(0, vertex.getSinks().size());
    Assert.assertEquals(0, vertex.getSources().size());

    // Add a sink port to actor
    GraphStructureHelper.addSinkPort(vertex, "sinkPort", 3);
    Assert.assertEquals(1, vertex.getSinks().size());
    Assert.assertEquals(0, vertex.getSources().size());

    // check the results
    final long portRate = (long) vertex.getSinks().iterator().next().getPropertyBean().getValue("port_rate");
    Assert.assertEquals(3, portRate);
  }

  @Test
  public void testTargetActorShouldBeReplaced() {

    // create an empty SDF graph
    final SDFGraph sdf = new SDFGraph();
    sdf.setName("test");

    // Add actor A and B
    GraphStructureHelper.addActor(sdf, "A", null, 0, 0, 0, null);
    GraphStructureHelper.addActor(sdf, "B", null, 0, 0, 0, null);
    GraphStructureHelper.addActor(sdf, "C", null, 0, 0, 0, null);

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
    GraphStructureHelper.addActor(sdf, "A", null, 0, 0, 0, null);
    GraphStructureHelper.addActor(sdf, "B", null, 0, 0, 0, null);
    GraphStructureHelper.addActor(sdf, "C", null, 0, 0, 0, null);

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
    final Map<String, SDFAbstractVertex> listOfHierarchicalActors = GraphStructureHelper.getHierarchicalActors(ibsdf);

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
    final Map<String,
        SDFAbstractVertex> listOfHierarchicalActors = GraphStructureHelper.getAllHierarchicalActors(ibsdf);

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
    final SDFGraph graphGH = new SDFGraph();
    graphGH.setName("subgraph");
    GraphStructureHelper.addActor(graphGH, "G", null, 0, 1., 0, null);
    GraphStructureHelper.addActor(graphGH, "H", null, 0, 1., 0, null);
    GraphStructureHelper.addInputInterface(graphGH, "f", 0, 0., 0, null);
    GraphStructureHelper.addOutputInterface(graphGH, "e", 0, 0., 0, null);

    GraphStructureHelper.addEdge(graphGH, "f", null, "G", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graphGH, "G", null, "F", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graphGH, "H", null, "e", null, 1, 1, 0, null);

    // create the subgraph DEF
    final SDFGraph graphDEF = new SDFGraph();
    graphDEF.setName("subgraph");
    GraphStructureHelper.addActor(graphDEF, "D", graphGH, 0, 1., 0, null);
    GraphStructureHelper.addActor(graphDEF, "E", null, 0, 1., 0, null);
    GraphStructureHelper.addActor(graphDEF, "F", null, 0, 1., 0, null);
    GraphStructureHelper.addInputInterface(graphDEF, "a", 0, 0., 0, null);
    GraphStructureHelper.addOutputInterface(graphDEF, "c", 0, 0., 0, null);

    GraphStructureHelper.addEdge(graphDEF, "a", null, "E", null, 2, 1, 0, null);
    GraphStructureHelper.addEdge(graphDEF, "E", null, "F", null, 2, 3, 0, null);
    GraphStructureHelper.addEdge(graphDEF, "F", null, "D", "f", 1, 2, 0, null);
    GraphStructureHelper.addEdge(graphDEF, "D", "e", "E", null, 3, 1, 3, null);
    GraphStructureHelper.addEdge(graphDEF, "F", null, "c", null, 3, 1, 0, null);

    // create the top graph and add the subgraph to the hierarchical actor B
    final SDFGraph topgraph = new SDFGraph();
    topgraph.setName("topgraph");
    GraphStructureHelper.addActor(topgraph, "A", null, 0, 1., 0, null);
    GraphStructureHelper.addActor(topgraph, "B", graphDEF, 0, 0, 0, null);
    GraphStructureHelper.addActor(topgraph, "C", null, 0, 1., 0, null);

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
    final List<SDFAbstractVertex> topologicalSorting = GraphStructureHelper.topologicalSorting(dag);

    // check the results
    Assert.assertNotNull(topologicalSorting);
    Assert.assertEquals(dag.vertexSet().size(), topologicalSorting.size());

    for (int i = 0; i < topologicalSorting.size(); i++) {
      Assert.assertEquals(expectedList.get(i).getName(), topologicalSorting.get(i).getName());
    }
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
    GraphStructureHelper.topologicalSorting(dag);

    final List<SDFAbstractVertex> partialTopologicalSorting = GraphStructureHelper
        .partialTopologicalSorting(dag.getVertex("0"));

    final Map<String,
        Double> expectedAllLongestPaths = Map.of("0", 0.0, "1", 3.0, "2", 9.0, "3", 14.0, "4", 14.0, "5", 14.0);
    final Map<String, Double> allLongestPaths = GraphStructureHelper.getLongestPathToAllTargets(dag.getVertex("0"),
        null, partialTopologicalSorting);
    Assert.assertEquals(expectedAllLongestPaths, allLongestPaths);

    final double longestPath = GraphStructureHelper.getLongestPath(dag, null, null);
    Assert.assertEquals(15.0, longestPath, 0.1);
  }

  @Test
  public void testPartialTopologicalSorting() {
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

    // expected result of the partial topological sorting
    // [5, 0, 2, 3, 1]
    final ArrayList<SDFAbstractVertex> expectedList = new ArrayList<>();
    expectedList.add(dag.getVertex("5"));
    expectedList.add(dag.getVertex("0"));
    expectedList.add(dag.getVertex("2"));
    expectedList.add(dag.getVertex("3"));
    expectedList.add(dag.getVertex("1"));

    // partial topological sorting
    final List<SDFAbstractVertex> result = GraphStructureHelper.partialTopologicalSorting(dag.getVertex("5"));
    Assert.assertEquals(expectedList, result);
  }

}
