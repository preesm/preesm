/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
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
package org.ietr.preesm.algorithm.randomsdf;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.preesm.algorithm.model.sdf.SDFAbstractVertex;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.randomsdf.RandomSDF;
import org.preesm.model.slam.SlamFactory;
import org.preesm.scenario.PreesmScenario;
import org.preesm.workflow.WorkflowException;

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

    final Map<String, Object> inputs = new LinkedHashMap<>();

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
