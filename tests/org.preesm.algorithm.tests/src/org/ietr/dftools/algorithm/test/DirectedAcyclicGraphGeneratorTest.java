/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2024) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2022)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2011)
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

import java.security.SecureRandom;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.preesm.algorithm.generator.DirectedAcyclicGraphGenerator;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.model.sdf.visitors.ConsistencyChecker;

/**
 */
public class DirectedAcyclicGraphGeneratorTest {

  /**
   * Testing the generator.
   */
  @Test
  public void testDAGGen() {
    final DirectedAcyclicGraphGenerator generator = new DirectedAcyclicGraphGenerator();
    final ConsistencyChecker checker = new ConsistencyChecker();

    final Random rand = new SecureRandom();
    for (int i = 0; i < 5; i++) {
      final SDFGraph graph = generator.createAcyclicRandomGraph(100, 1, 3, 1, 3, rand.nextInt(5));
      Assert.assertTrue(checker.verifyGraph(graph));
    }
  }

}
