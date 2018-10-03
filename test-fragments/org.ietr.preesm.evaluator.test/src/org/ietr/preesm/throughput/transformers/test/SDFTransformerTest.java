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

import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.throughput.tools.helpers.GraphStructureHelper;
import org.ietr.preesm.throughput.tools.transformers.SDFTransformer;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test of SDFTransformer class
 *
 * @author hderoui
 *
 */
public class SDFTransformerTest {

  @Test
  public void testSDFGraphShouldBeNormalized() {
    // generate a SDF graph
    final SDFGraph sdf = generateSDFGraphABC326();

    // normalize the graph
    SDFTransformer.normalize(sdf);

    // check the value of the normalized rate of each vertex
    final double Za = (double) sdf.getVertex("A").getPropertyBean().getValue("normalizedRate");
    Assert.assertEquals(2, Za, 0);

    final double Zb = (double) sdf.getVertex("B").getPropertyBean().getValue("normalizedRate");
    Assert.assertEquals(3, Zb, 0);

    final double Zc = (double) sdf.getVertex("C").getPropertyBean().getValue("normalizedRate");
    Assert.assertEquals(1, Zc, 0);

    // check the normalization factor of each edge
    for (final SDFEdge e : sdf.edgeSet()) {
      final double Zt = (double) e.getSource().getPropertyBean().getValue("normalizedRate");
      final double alpha_expected = Zt / e.getProd().intValue();
      final double alpha_current = (double) e.getPropertyBean().getValue("normalizationFactor");
      Assert.assertEquals(alpha_expected, alpha_current, 0);
    }
  }

  @Test
  public void testSDFGraphShouldBeTranformedToSrSDF() {

    // generate a SDF graph
    final SDFGraph sdf = generateSDFGraphABC326();

    // convert the SDF graph to an SrSDF
    final SDFGraph srSDF = SDFTransformer.convertToSrSDF(sdf);

    // check the number of actors and edges
    // number of actors: 11
    // number of edges: 16
    final int nbActor = srSDF.vertexSet().size();
    final int nbEdges = srSDF.edgeSet().size();

    Assert.assertEquals(11, nbActor);
    Assert.assertEquals(16, nbEdges);
  }

  @Test
  public void testSDFGraphShouldBeTranformedToHSDF() {

    // generate a SDF graph
    final SDFGraph sdf = generateSDFGraphABC326();

    // convert the SDF graph to an HSDF
    final SDFGraph hsdf = SDFTransformer.convertToHSDF(sdf);

    // check the number of actors and edges
    // number of actors: 11
    // number of edges: 36
    final int nbActor = hsdf.vertexSet().size();
    final int nbEdges = hsdf.edgeSet().size();

    Assert.assertEquals(11, nbActor);
    Assert.assertEquals(36, nbEdges);

    // verify that the consumption/production rate of all edges equal 1
    for (final SDFEdge e : hsdf.edgeSet()) {
      final int cons = e.getCons().intValue();
      final int prod = e.getProd().intValue();

      Assert.assertEquals(1, cons);
      Assert.assertEquals(1, prod);
    }
  }

  @Test
  public void testSDFGraphShouldBeTranformedToReducedHSDF() {

    // generate a SDF graph
    final SDFGraph sdf = generateSDFGraphABC326();

    // convert the SDF graph to a reduced HSDF
    final SDFGraph reducedHSDF = SDFTransformer.convertToReducedHSDF(sdf);

    // check the number of actors and edges
    // number of actors: 11
    // number of edges: 16
    final int nbActor = reducedHSDF.vertexSet().size();
    final int nbEdges = reducedHSDF.edgeSet().size();

    Assert.assertEquals(11, nbActor);
    Assert.assertEquals(16, nbEdges);

    // verify that the consumption/production rate of all edges equal 1
    for (final SDFEdge e : reducedHSDF.edgeSet()) {
      final int cons = e.getCons().intValue();
      final int prod = e.getProd().intValue();

      Assert.assertEquals(1, cons);
      Assert.assertEquals(1, prod);
    }
  }

  @Test
  public void testSDFGraphShouldBeTranformedToDAG() {

    // generate a SDF graph
    final SDFGraph sdf = generateSDFGraphABC326();

    // convert the SDF graph to a DAG
    final SDFGraph dag = SDFTransformer.convertToDAG(sdf);

    // check the number of actors and edges
    // number of actors: 11
    // number of edges: 12
    final int nbActor = dag.vertexSet().size();
    final int nbEdges = dag.edgeSet().size();

    Assert.assertEquals(11, nbActor);
    Assert.assertEquals(12, nbEdges);

    // check if all the edges between A and B have been removed
    for (final SDFAbstractVertex actor : dag.vertexSet()) {
      final SDFAbstractVertex baseActor = (SDFAbstractVertex) actor.getPropertyBean().getValue("baseActor");
      if (baseActor.getId().equals("A")) {
        final int nbEdge = actor.getSinks().size();
        Assert.assertEquals(0, nbEdge);
      }
    }

    // check if all the edges have zero delay
    for (final SDFEdge e : dag.edgeSet()) {
      final int delay = e.getDelay().intValue();
      Assert.assertEquals(0, delay);

    }
  }

  /**
   * generates a SDF graph
   *
   * @return SDF graph
   */
  public SDFGraph generateSDFGraphABC326() {
    // Actors: A B C
    // Edges: AB=(2,3); BC=(3,1); CA=(1,2)
    // RV[A=3, B=2, C=6]
    // Actors duration: A=1, B=1, C=1
    // Normalized rate of actors: A=2, B=3, C=1
    // normalization factor of edges: AB=1; BC=1; CA=0.5
    // normalized period K of the graph = 3

    // create SDF graph testABC3
    final SDFGraph graph = new SDFGraph();
    graph.setName("testABC3");

    // add actors
    GraphStructureHelper.addActor(graph, "A", null, 3L, 1., null, null);
    GraphStructureHelper.addActor(graph, "B", null, 2L, 1., null, null);
    GraphStructureHelper.addActor(graph, "C", null, 6L, 1., null, null);

    // add edges
    GraphStructureHelper.addEdge(graph, "A", null, "B", null, 2, 3, 6, null);
    GraphStructureHelper.addEdge(graph, "B", null, "C", null, 9, 3, 0, null);
    GraphStructureHelper.addEdge(graph, "C", null, "A", null, 2, 4, 0, null);

    return graph;
  }
}
