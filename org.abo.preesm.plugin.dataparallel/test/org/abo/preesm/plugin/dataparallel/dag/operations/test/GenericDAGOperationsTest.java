package org.abo.preesm.plugin.dataparallel.dag.operations.test;

import org.abo.preesm.plugin.dataparallel.DAGConstructor;
import org.abo.preesm.plugin.dataparallel.SDF2DAG;
import org.abo.preesm.plugin.dataparallel.dag.operations.DAGOperations;
import org.abo.preesm.plugin.dataparallel.dag.operations.GenericDAGOperations;
import org.abo.preesm.plugin.dataparallel.test.ExampleGraphs;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test setup
 * 
 * @author sudeep
 *
 */
public class GenericDAGOperationsTest {
  /**
   * Check if the root instances are correct
   */
  @Test
  public void checkRootInstances() {
    SDFGraph sdf = ExampleGraphs.acyclicTwoActors();
    DAGConstructor dagGen = new SDF2DAG(sdf);
    DAGOperations dagOps = new GenericDAGOperations(dagGen);
    String[] rootNames = dagOps.getRootInstances().stream().map((SDFAbstractVertex v) -> v.getName()).toArray(String[]::new);
    Assert.assertArrayEquals(new String[] { "a_0", "a_1", "a_2", "a_3", "a_4", "b_0" }, rootNames);

    sdf = ExampleGraphs.twoActorSelfLoop();
    dagGen = new SDF2DAG(sdf);
    dagOps = new GenericDAGOperations(dagGen);
    rootNames = dagOps.getRootInstances().stream().map((SDFAbstractVertex v) -> v.getName()).toArray(String[]::new);
    Assert.assertArrayEquals(new String[] { "a_0", "b_0" }, rootNames);

    sdf = ExampleGraphs.twoActorLoop();
    dagGen = new SDF2DAG(sdf);
    dagOps = new GenericDAGOperations(dagGen);
    rootNames = dagOps.getRootInstances().stream().map((SDFAbstractVertex v) -> v.getName()).toArray(String[]::new);
    Assert.assertArrayEquals(new String[] { "a_0", "a_1" }, rootNames);

    sdf = ExampleGraphs.semanticallyAcyclicCycle();
    dagGen = new SDF2DAG(sdf);
    dagOps = new GenericDAGOperations(dagGen);
    rootNames = dagOps.getRootInstances().stream().map((SDFAbstractVertex v) -> v.getName()).toArray(String[]::new);
    Assert.assertArrayEquals(new String[] { "c_0", "c_1", "c_2", "d_0" }, rootNames);

    sdf = ExampleGraphs.strictlyCyclic();
    dagGen = new SDF2DAG(sdf);
    dagOps = new GenericDAGOperations(dagGen);
    rootNames = dagOps.getRootInstances().stream().map((SDFAbstractVertex v) -> v.getName()).toArray(String[]::new);
    Assert.assertArrayEquals(new String[] { "a_0", "c_0" }, rootNames);

    sdf = ExampleGraphs.mixedNetwork1();
    dagGen = new SDF2DAG(sdf);
    dagOps = new GenericDAGOperations(dagGen);
    rootNames = dagOps.getRootInstances().stream().map((SDFAbstractVertex v) -> v.getName()).toArray(String[]::new);
    Assert.assertArrayEquals(new String[] { "b_0", "c_0", "z_0", "z_1", "z_2", "z_3", "z_4", "z_5" }, rootNames);

    sdf = ExampleGraphs.mixedNetwork2();
    dagGen = new SDF2DAG(sdf);
    dagOps = new GenericDAGOperations(dagGen);
    rootNames = dagOps.getRootInstances().stream().map((SDFAbstractVertex v) -> v.getName()).toArray(String[]::new);
    Assert.assertArrayEquals(new String[] { "b_0", "z_0", "z_1", "z_2", "z_3", "z_4", "z_5" }, rootNames);
  }

  /**
   * Check that the exit instances are same
   */
  @Test
  public void checkExitInstances() {
    SDFGraph sdf = ExampleGraphs.acyclicTwoActors();
    DAGConstructor dagGen = new SDF2DAG(sdf);
    DAGOperations dagOps = new GenericDAGOperations(dagGen);
    String[] names = dagOps.getExitInstances().stream().map((SDFAbstractVertex v) -> v.getName()).toArray(String[]::new);
    Assert.assertArrayEquals(new String[] { "b_1", "b_2" }, names);

    sdf = ExampleGraphs.twoActorSelfLoop();
    dagGen = new SDF2DAG(sdf);
    dagOps = new GenericDAGOperations(dagGen);
    names = dagOps.getExitInstances().stream().map((SDFAbstractVertex v) -> v.getName()).toArray(String[]::new);
    Assert.assertArrayEquals(new String[] { "a_4", "b_1", "b_2" }, names);

    sdf = ExampleGraphs.twoActorLoop();
    dagGen = new SDF2DAG(sdf);
    dagOps = new GenericDAGOperations(dagGen);
    names = dagOps.getExitInstances().stream().map((SDFAbstractVertex v) -> v.getName()).toArray(String[]::new);
    Assert.assertArrayEquals(new String[] { "b_2" }, names);

    sdf = ExampleGraphs.semanticallyAcyclicCycle();
    dagGen = new SDF2DAG(sdf);
    dagOps = new GenericDAGOperations(dagGen);
    names = dagOps.getExitInstances().stream().map((SDFAbstractVertex v) -> v.getName()).toArray(String[]::new);
    Assert.assertArrayEquals(new String[] { "b_0", "b_1" }, names);

    sdf = ExampleGraphs.strictlyCyclic();
    dagGen = new SDF2DAG(sdf);
    dagOps = new GenericDAGOperations(dagGen);
    names = dagOps.getExitInstances().stream().map((SDFAbstractVertex v) -> v.getName()).toArray(String[]::new);
    Assert.assertArrayEquals(new String[] { "a_2", "b_1", "d_1" }, names);

    sdf = ExampleGraphs.mixedNetwork1();
    dagGen = new SDF2DAG(sdf);
    dagOps = new GenericDAGOperations(dagGen);
    names = dagOps.getExitInstances().stream().map((SDFAbstractVertex v) -> v.getName()).toArray(String[]::new);
    Assert.assertArrayEquals(new String[] { "a_1", "a_2", "b_1", "e_0", "e_1", "e_2" }, names);

    sdf = ExampleGraphs.mixedNetwork2();
    dagGen = new SDF2DAG(sdf);
    dagOps = new GenericDAGOperations(dagGen);
    names = dagOps.getExitInstances().stream().map((SDFAbstractVertex v) -> v.getName()).toArray(String[]::new);
    Assert.assertArrayEquals(new String[] { "a_1", "a_2", "e_0", "e_1", "e_2" }, names);
  }

  /**
   * Check root actors are generated correctly
   */
  @Test
  public void checkRootActors() {
    SDFGraph sdf = ExampleGraphs.acyclicTwoActors();
    DAGConstructor dagGen = new SDF2DAG(sdf);
    DAGOperations dagOps = new GenericDAGOperations(dagGen);
    String[] rootNames = dagOps.getRootActors().stream().map((SDFAbstractVertex v) -> v.getName()).toArray(String[]::new);
    Assert.assertArrayEquals(new String[] { "a", "b" }, rootNames);

    sdf = ExampleGraphs.twoActorSelfLoop();
    dagGen = new SDF2DAG(sdf);
    dagOps = new GenericDAGOperations(dagGen);
    rootNames = dagOps.getRootActors().stream().map((SDFAbstractVertex v) -> v.getName()).toArray(String[]::new);
    Assert.assertArrayEquals(new String[] { "a", "b" }, rootNames);

    sdf = ExampleGraphs.twoActorLoop();
    dagGen = new SDF2DAG(sdf);
    dagOps = new GenericDAGOperations(dagGen);
    rootNames = dagOps.getRootActors().stream().map((SDFAbstractVertex v) -> v.getName()).toArray(String[]::new);
    Assert.assertArrayEquals(new String[] { "a" }, rootNames);

    sdf = ExampleGraphs.semanticallyAcyclicCycle();
    dagGen = new SDF2DAG(sdf);
    dagOps = new GenericDAGOperations(dagGen);
    rootNames = dagOps.getRootActors().stream().map((SDFAbstractVertex v) -> v.getName()).toArray(String[]::new);
    Assert.assertArrayEquals(new String[] { "c", "d" }, rootNames);

    sdf = ExampleGraphs.strictlyCyclic();
    dagGen = new SDF2DAG(sdf);
    dagOps = new GenericDAGOperations(dagGen);
    rootNames = dagOps.getRootActors().stream().map((SDFAbstractVertex v) -> v.getName()).toArray(String[]::new);
    Assert.assertArrayEquals(new String[] { "a", "c" }, rootNames);

    sdf = ExampleGraphs.mixedNetwork1();
    dagGen = new SDF2DAG(sdf);
    dagOps = new GenericDAGOperations(dagGen);
    rootNames = dagOps.getRootActors().stream().map((SDFAbstractVertex v) -> v.getName()).toArray(String[]::new);
    Assert.assertArrayEquals(new String[] { "b", "c", "z" }, rootNames);

    sdf = ExampleGraphs.mixedNetwork2();
    dagGen = new SDF2DAG(sdf);
    dagOps = new GenericDAGOperations(dagGen);
    rootNames = dagOps.getRootActors().stream().map((SDFAbstractVertex v) -> v.getName()).toArray(String[]::new);
    Assert.assertArrayEquals(new String[] { "b", "z" }, rootNames);
  }
}
