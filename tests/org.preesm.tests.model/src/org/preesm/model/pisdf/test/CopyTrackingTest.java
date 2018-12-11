/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
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
package org.preesm.model.pisdf.test;

import org.junit.Assert;
import org.junit.Test;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

/**
 *
 */
public class CopyTrackingTest {

  @Test
  public void testCopy() {
    final PiGraph originalGraph = PiMMUserFactory.instance.createPiGraph();
    final PiGraph copy = PiMMUserFactory.instance.copyWithHistory(originalGraph);
    final PiGraph originalObject = PreesmCopyTracker.getSource(copy);
    Assert.assertEquals(originalGraph, originalObject);
  }

  @Test
  public void testMultiCopy() {
    final PiGraph originalGraph = PiMMUserFactory.instance.createPiGraph();
    final PiGraph copy = PiMMUserFactory.instance.copyWithHistory(originalGraph);
    final PiGraph copy2 = PiMMUserFactory.instance.copyWithHistory(copy);
    final PiGraph copy3 = PiMMUserFactory.instance.copyWithHistory(copy2);
    final PiGraph copy4 = PiMMUserFactory.instance.copyWithHistory(copy3);

    final PiGraph sourceCopy3 = PreesmCopyTracker.getSource(copy3);
    Assert.assertEquals(sourceCopy3, copy2);

    final PiGraph originalSourceCopy4 = PreesmCopyTracker.getOriginalSource(copy4);
    Assert.assertEquals(originalSourceCopy4, originalGraph);

  }
}
