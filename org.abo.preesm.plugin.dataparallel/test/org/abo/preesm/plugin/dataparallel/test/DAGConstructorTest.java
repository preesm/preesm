package org.abo.preesm.plugin.dataparallel.test;

import org.abo.preesm.plugin.dataparallel.DAGConstructor;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test construction of DAG and its various features
 * 
 * @author sudeep
 *
 */
public final class DAGConstructorTest {
  @Test
  public void sdfIsNotHSDF() {
    SDFGraph sdf = ExampleGraphs.acyclicTwoActors();
    DAGConstructor dagGen = new DAGConstructor(sdf);
    Assert.assertFalse(dagGen.checkDAG());

    sdf = ExampleGraphs.twoActorSelfLoop();
    dagGen = new DAGConstructor(sdf);
    Assert.assertFalse(dagGen.checkDAG());

    sdf = ExampleGraphs.twoActorLoop();
    dagGen = new DAGConstructor(sdf);
    Assert.assertFalse(dagGen.checkDAG());

    sdf = ExampleGraphs.semanticallyAcyclicCycle();
    dagGen = new DAGConstructor(sdf);
    Assert.assertFalse(dagGen.checkDAG());

    sdf = ExampleGraphs.strictlyCyclic();
    dagGen = new DAGConstructor(sdf);
    Assert.assertFalse(dagGen.checkDAG());

    sdf = ExampleGraphs.mixedNetwork1();
    dagGen = new DAGConstructor(sdf);
    Assert.assertFalse(dagGen.checkDAG());

    sdf = ExampleGraphs.mixedNetwork2();
    dagGen = new DAGConstructor(sdf);
    Assert.assertFalse(dagGen.checkDAG());
  }
}
