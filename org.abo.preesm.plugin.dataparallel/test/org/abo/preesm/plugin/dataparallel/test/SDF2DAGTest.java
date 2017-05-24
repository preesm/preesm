package org.abo.preesm.plugin.dataparallel.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.abo.preesm.plugin.dataparallel.SDF2DAG;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.jgrapht.alg.CycleDetector;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test construction of DAG and its various features
 * 
 * @author sudeep
 *
 */
public final class SDF2DAGTest {
  @Test
  public void sdfIsNotHSDF() {
    SDFGraph sdf = ExampleGraphs.acyclicTwoActors();
    SDF2DAG dagGen = new SDF2DAG(sdf);
    Assert.assertFalse(dagGen.checkDAG());

    sdf = ExampleGraphs.twoActorSelfLoop();
    dagGen = new SDF2DAG(sdf);
    Assert.assertFalse(dagGen.checkDAG());

    sdf = ExampleGraphs.twoActorLoop();
    dagGen = new SDF2DAG(sdf);
    Assert.assertFalse(dagGen.checkDAG());

    sdf = ExampleGraphs.semanticallyAcyclicCycle();
    dagGen = new SDF2DAG(sdf);
    Assert.assertFalse(dagGen.checkDAG());

    sdf = ExampleGraphs.strictlyCyclic();
    dagGen = new SDF2DAG(sdf);
    Assert.assertFalse(dagGen.checkDAG());

    sdf = ExampleGraphs.mixedNetwork1();
    dagGen = new SDF2DAG(sdf);
    Assert.assertFalse(dagGen.checkDAG());

    sdf = ExampleGraphs.mixedNetwork2();
    dagGen = new SDF2DAG(sdf);
    Assert.assertFalse(dagGen.checkDAG());
  }

  @Test(expected = SDF4JException.class)
  public void exceptionHierGraph() {
    SDFGraph sdf = ExampleGraphs.acyclicHierarchicalTwoActors();
    new SDF2DAG(sdf);
  }

  protected int getExplodeInstances(List<SDFAbstractVertex> instances) {
    int count = 0;
    for (SDFAbstractVertex vertex : instances) {
      if (vertex instanceof SDFForkVertex) {
        String name = vertex.getName();
        if (name.toLowerCase().contains("explode")) {
          count++;
        }
      }
    }
    return count;
  }

  @Test
  public void checkExplodeCount() {
    List<SDFAbstractVertex> instanceList = new ArrayList<>();
    SDFGraph sdf = ExampleGraphs.acyclicTwoActors();
    SDF2DAG dagGen = new SDF2DAG(sdf);
    instanceList.addAll(dagGen.getOutputGraph().vertexSet());
    Assert.assertEquals(1, getExplodeInstances(instanceList));

    instanceList.clear();
    sdf = ExampleGraphs.twoActorSelfLoop();
    dagGen = new SDF2DAG(sdf);
    instanceList.addAll(dagGen.getOutputGraph().vertexSet());
    Assert.assertEquals(1, getExplodeInstances(instanceList));

    instanceList.clear();
    sdf = ExampleGraphs.twoActorLoop();
    dagGen = new SDF2DAG(sdf);
    instanceList.addAll(dagGen.getOutputGraph().vertexSet());
    Assert.assertEquals(4, getExplodeInstances(instanceList));

    instanceList.clear();
    sdf = ExampleGraphs.semanticallyAcyclicCycle();
    dagGen = new SDF2DAG(sdf);
    instanceList.addAll(dagGen.getOutputGraph().vertexSet());
    Assert.assertEquals(3, getExplodeInstances(instanceList));

    instanceList.clear();
    sdf = ExampleGraphs.strictlyCyclic();
    dagGen = new SDF2DAG(sdf);
    instanceList.addAll(dagGen.getOutputGraph().vertexSet());
    Assert.assertEquals(4, getExplodeInstances(instanceList));

    instanceList.clear();
    sdf = ExampleGraphs.mixedNetwork1();
    dagGen = new SDF2DAG(sdf);
    instanceList.addAll(dagGen.getOutputGraph().vertexSet());
    Assert.assertEquals(6, getExplodeInstances(instanceList));

    instanceList.clear();
    sdf = ExampleGraphs.mixedNetwork2();
    dagGen = new SDF2DAG(sdf);
    instanceList.addAll(dagGen.getOutputGraph().vertexSet());
    Assert.assertEquals(7, getExplodeInstances(instanceList));
  }

  protected int getImplodeInstances(List<SDFAbstractVertex> instances) {
    int count = 0;
    for (SDFAbstractVertex vertex : instances) {
      if (vertex instanceof SDFJoinVertex) {
        String name = vertex.getName();
        if (name.toLowerCase().contains("implode")) {
          count++;
        }
      }
    }
    return count;
  }

  @Test
  public void checkImplodeCount() {
    List<SDFAbstractVertex> instanceList = new ArrayList<>();
    SDFGraph sdf = ExampleGraphs.acyclicTwoActors();
    SDF2DAG dagGen = new SDF2DAG(sdf);
    instanceList.addAll(dagGen.getOutputGraph().vertexSet());
    Assert.assertEquals(2, getImplodeInstances(instanceList));

    instanceList.clear();
    sdf = ExampleGraphs.twoActorSelfLoop();
    dagGen = new SDF2DAG(sdf);
    instanceList.addAll(dagGen.getOutputGraph().vertexSet());
    Assert.assertEquals(2, getImplodeInstances(instanceList));

    instanceList.clear();
    sdf = ExampleGraphs.twoActorLoop();
    dagGen = new SDF2DAG(sdf);
    instanceList.addAll(dagGen.getOutputGraph().vertexSet());
    Assert.assertEquals(4, getImplodeInstances(instanceList));

    instanceList.clear();
    sdf = ExampleGraphs.semanticallyAcyclicCycle();
    dagGen = new SDF2DAG(sdf);
    instanceList.addAll(dagGen.getOutputGraph().vertexSet());
    Assert.assertEquals(4, getImplodeInstances(instanceList));

    instanceList.clear();
    sdf = ExampleGraphs.strictlyCyclic();
    dagGen = new SDF2DAG(sdf);
    instanceList.addAll(dagGen.getOutputGraph().vertexSet());
    Assert.assertEquals(6, getImplodeInstances(instanceList));

    instanceList.clear();
    sdf = ExampleGraphs.mixedNetwork1();
    dagGen = new SDF2DAG(sdf);
    instanceList.addAll(dagGen.getOutputGraph().vertexSet());
    Assert.assertEquals(7, getImplodeInstances(instanceList));

    instanceList.clear();
    sdf = ExampleGraphs.mixedNetwork2();
    dagGen = new SDF2DAG(sdf);
    instanceList.addAll(dagGen.getOutputGraph().vertexSet());
    Assert.assertEquals(7, getImplodeInstances(instanceList));
  }

  @Test
  public void checkInstancesCount() {
    SDFGraph sdf = ExampleGraphs.acyclicTwoActors();
    SDF2DAG dagGen = new SDF2DAG(sdf);
    Assert.assertEquals(11, dagGen.getOutputGraph().vertexSet().size()); // 8 + 1 explode + 2 implode

    sdf = ExampleGraphs.twoActorSelfLoop();
    dagGen = new SDF2DAG(sdf);
    Assert.assertEquals(11, dagGen.getOutputGraph().vertexSet().size()); // 8 + 1 explode + 2 implode

    sdf = ExampleGraphs.twoActorLoop();
    dagGen = new SDF2DAG(sdf);
    Assert.assertEquals(16, dagGen.getOutputGraph().vertexSet().size()); // 8 + 4 explode + 4 implode

    sdf = ExampleGraphs.semanticallyAcyclicCycle();
    dagGen = new SDF2DAG(sdf);
    Assert.assertEquals(17, dagGen.getOutputGraph().vertexSet().size()); // 10 + 3 explode + 4 implode

    sdf = ExampleGraphs.strictlyCyclic();
    dagGen = new SDF2DAG(sdf);
    Assert.assertEquals(20, dagGen.getOutputGraph().vertexSet().size()); // 10 + 4 explode + 6 implode

    sdf = ExampleGraphs.mixedNetwork1();
    dagGen = new SDF2DAG(sdf);
    Assert.assertEquals(32, dagGen.getOutputGraph().vertexSet().size()); // 19 + 6 explode + 7 implode

    sdf = ExampleGraphs.mixedNetwork2();
    dagGen = new SDF2DAG(sdf);
    Assert.assertEquals(33, dagGen.getOutputGraph().vertexSet().size()); // 19 + 7 explode + 7 implode
  }

  /**
   * Check if any edge of the graph has delays
   */
  protected boolean hasDelay(SDFGraph sdf) {
    for (final SDFEdge edge : sdf.edgeSet()) {
      if (edge.getDelay().intValue() != 0) {
        return true;
      }
    }
    return false;
  }

  @Test
  public void noDelays() {
    SDFGraph sdf = ExampleGraphs.acyclicTwoActors();
    SDF2DAG dagGen = new SDF2DAG(sdf);
    Assert.assertFalse(hasDelay(dagGen.getOutputGraph()));

    sdf = ExampleGraphs.twoActorSelfLoop();
    dagGen = new SDF2DAG(sdf);
    Assert.assertFalse(hasDelay(dagGen.getOutputGraph()));

    sdf = ExampleGraphs.twoActorLoop();
    dagGen = new SDF2DAG(sdf);
    Assert.assertFalse(hasDelay(dagGen.getOutputGraph()));

    sdf = ExampleGraphs.semanticallyAcyclicCycle();
    dagGen = new SDF2DAG(sdf);
    Assert.assertFalse(hasDelay(dagGen.getOutputGraph()));

    sdf = ExampleGraphs.strictlyCyclic();
    dagGen = new SDF2DAG(sdf);
    Assert.assertFalse(hasDelay(dagGen.getOutputGraph()));

    sdf = ExampleGraphs.mixedNetwork1();
    dagGen = new SDF2DAG(sdf);
    Assert.assertFalse(hasDelay(dagGen.getOutputGraph()));

    sdf = ExampleGraphs.mixedNetwork2();
    dagGen = new SDF2DAG(sdf);
    Assert.assertFalse(hasDelay(dagGen.getOutputGraph()));
  }

  protected int getAllVerticesFromMaps(Map<SDFAbstractVertex, List<SDFAbstractVertex>> map) {
    int vertices = 0;
    for (final List<SDFAbstractVertex> instances : map.values()) {
      vertices += instances.size();
    }
    return vertices;
  }

  /**
   * Check that actor2Instances has same size as vertices set
   */
  @Test
  public void actor2InstancesAllHasAllVertices() {
    SDFGraph sdf = ExampleGraphs.acyclicTwoActors();
    SDF2DAG dagGen = new SDF2DAG(sdf);
    Assert.assertEquals(getAllVerticesFromMaps(dagGen.getActor2Instances()), dagGen.getOutputGraph().vertexSet().size());

    sdf = ExampleGraphs.twoActorSelfLoop();
    dagGen = new SDF2DAG(sdf);
    Assert.assertEquals(getAllVerticesFromMaps(dagGen.getActor2Instances()), dagGen.getOutputGraph().vertexSet().size());

    sdf = ExampleGraphs.twoActorLoop();
    dagGen = new SDF2DAG(sdf);
    Assert.assertEquals(getAllVerticesFromMaps(dagGen.getActor2Instances()), dagGen.getOutputGraph().vertexSet().size());

    sdf = ExampleGraphs.semanticallyAcyclicCycle();
    dagGen = new SDF2DAG(sdf);
    Assert.assertEquals(getAllVerticesFromMaps(dagGen.getActor2Instances()), dagGen.getOutputGraph().vertexSet().size());

    sdf = ExampleGraphs.strictlyCyclic();
    dagGen = new SDF2DAG(sdf);
    Assert.assertEquals(getAllVerticesFromMaps(dagGen.getActor2Instances()), dagGen.getOutputGraph().vertexSet().size());

    sdf = ExampleGraphs.mixedNetwork1();
    dagGen = new SDF2DAG(sdf);
    Assert.assertEquals(getAllVerticesFromMaps(dagGen.getActor2Instances()), dagGen.getOutputGraph().vertexSet().size());

    sdf = ExampleGraphs.mixedNetwork2();
    dagGen = new SDF2DAG(sdf);
    Assert.assertEquals(getAllVerticesFromMaps(dagGen.getActor2Instances()), dagGen.getOutputGraph().vertexSet().size());
  }

  protected int getImplodeExplodeFromMaps(Map<SDFAbstractVertex, List<SDFAbstractVertex>> map) {
    int vertex = 0;
    for (final List<SDFAbstractVertex> instances : map.values()) {
      vertex += getImplodeInstances(instances);
      vertex += getExplodeInstances(instances);
    }
    return vertex;
  }

  /**
   * Check if graph has cycles
   * 
   * @param graph
   *          Graph to test for cycles
   * @return boolean True if it has cycles and false otherwise
   */
  protected boolean hasCycles(SDFGraph graph) {
    final CycleDetector<SDFAbstractVertex, SDFEdge> detector = new CycleDetector<SDFAbstractVertex, SDFEdge>(graph);
    return detector.detectCycles();
  }

  /**
   * Check that DAG has no cycles
   */
  @Test
  public void dagHasNoCycles() {
    SDFGraph sdf = ExampleGraphs.acyclicTwoActors();
    SDF2DAG dagGen = new SDF2DAG(sdf);
    Assert.assertFalse(hasCycles(dagGen.getOutputGraph()));

    sdf = ExampleGraphs.strictlyCyclic();
    dagGen = new SDF2DAG(sdf);
    Assert.assertFalse(hasCycles(dagGen.getOutputGraph()));

    sdf = ExampleGraphs.semanticallyAcyclicCycle();
    dagGen = new SDF2DAG(sdf);
    Assert.assertFalse(hasCycles(dagGen.getOutputGraph()));

    sdf = ExampleGraphs.twoActorSelfLoop();
    dagGen = new SDF2DAG(sdf);
    Assert.assertFalse(hasCycles(dagGen.getOutputGraph()));

    sdf = ExampleGraphs.twoActorLoop();
    dagGen = new SDF2DAG(sdf);
    Assert.assertFalse(hasCycles(dagGen.getOutputGraph()));

    sdf = ExampleGraphs.mixedNetwork1();
    dagGen = new SDF2DAG(sdf);
    Assert.assertFalse(hasCycles(dagGen.getOutputGraph()));

    sdf = ExampleGraphs.mixedNetwork2();
    dagGen = new SDF2DAG(sdf);
    Assert.assertFalse(hasCycles(dagGen.getOutputGraph()));
  }
}
