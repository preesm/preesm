package org.ietr.preesm.algorithm.randomsdf;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.architecture.slam.SlamFactory;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.junit.Assert;
import org.junit.Test;

/**
 */
public class RandomSDFTest {

  @Test
  public void testRandomSDFTaskDefaultParams() {
    final Map<String, String> params = new RandomSDF().getDefaultParameters();

    final String param = params.get("nbVertex");
    final int nbVertex = (param != null) ? Integer.decode(param) : -1;
    Assert.assertNotEquals(-1, nbVertex);
  }

  @Test
  public void testRandomSdfGen() {
    final RandomSDF task = new RandomSDF();
    final Map<String, String> params = task.getDefaultParameters();

    final HashMap<String, Object> inputs = new HashMap<>();

    // create dummy inputs
    inputs.put("SDF", new SDFGraph());
    inputs.put("architecture", SlamFactory.eINSTANCE.createDesign());
    inputs.put("scenario", new PreesmScenario());

    Map<String, Object> outputs = null;
    try {
      outputs = task.execute(inputs, params, null, null, null);
    } catch (final WorkflowException e) {
      Assert.fail(e.getLocalizedMessage());
    }

    final Object resultSDF = outputs.get("SDF");
    Assert.assertNotNull(resultSDF);
    Assert.assertTrue(SDFGraph.class.isInstance(resultSDF));
    final SDFGraph sdf = (SDFGraph) resultSDF;

    final Set<SDFAbstractVertex> allVertices = sdf.getAllVertices();
    Assert.assertNotNull(allVertices);
    Assert.assertFalse(allVertices.isEmpty());
    final int nbVertices = allVertices.size();
    Assert.assertEquals(10, nbVertices);

  }
}
