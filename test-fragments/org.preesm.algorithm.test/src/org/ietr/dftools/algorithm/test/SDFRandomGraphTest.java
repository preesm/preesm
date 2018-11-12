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
package org.ietr.dftools.algorithm.test;

import java.util.Set;
import org.ietr.dftools.algorithm.generator.SDFRandomGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.junit.Assert;
import org.junit.Test;

/**
 * The Class SDFRandomGraphTest.
 */
public class SDFRandomGraphTest {

  /**
   * Test random gen.
   */
  @Test
  public void testRandomGen() {
    final int nbVertex = 20;

    final SDFRandomGraph sdfRandomGraph = new SDFRandomGraph();
    Assert.assertNotNull(sdfRandomGraph);
    try {
      final SDFGraph createRandomGraph = sdfRandomGraph.createRandomGraph(nbVertex, 1, 5, 1, 5, 1, 12);
      Assert.assertNotNull(createRandomGraph);
      final Set<SDFAbstractVertex> allVertices = createRandomGraph.getAllVertices();
      Assert.assertNotNull(allVertices);
      final int size = allVertices.size();
      Assert.assertEquals(nbVertex, size);

    } catch (final SDF4JException e) {
      Assert.fail("Should not fail with SDF4J, but catch exception " + e.getMessage());
    }
  }

}
