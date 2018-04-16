/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
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
package org.ietr.preesm.experiment.model.pimm;

import org.ietr.preesm.experiment.model.factory.PiMMUserFactory;
import org.ietr.preesm.experiment.model.pimm.impl.PiSDFRefinementImpl;
import org.ietr.preesm.experiment.model.pimm.util.ActorPath;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 *
 */
public class PiGraphLookupTest {

  @Test
  public void testLookup() {
    final PiGraph topGraph = PiMMUserFactory.instance.createPiGraph();
    topGraph.setName("topGraph");
    final Actor actor1 = PiMMUserFactory.instance.createActor();
    actor1.setName("toto");
    topGraph.addActor(actor1);
    final PiGraph secondLevelGraph = PiMMUserFactory.instance.createPiGraph();
    secondLevelGraph.setName("secondLevelGraph");
    topGraph.addActor(secondLevelGraph);
    final Actor actor2 = PiMMUserFactory.instance.createActor();
    actor2.setName("titi");
    secondLevelGraph.addActor(actor2);

    final PiGraph refinementGraph = PiMMUserFactory.instance.createPiGraph();
    refinementGraph.setName("refinementGraph");
    final PiSDFRefinement pisdfRefinmentMock = Mockito.mock(PiSDFRefinementImpl.class);
    Mockito.when(pisdfRefinmentMock.getAbstractActor()).thenReturn(refinementGraph);
    Mockito.when(pisdfRefinmentMock.isHierarchical()).thenReturn(true);
    actor2.setRefinement(pisdfRefinmentMock);
    final Actor actor3 = PiMMUserFactory.instance.createActor();
    actor3.setName("tutu");
    refinementGraph.addActor(actor3);

    // usual requests

    final AbstractActor query1 = ActorPath.lookup(topGraph, "topGraph");
    Assert.assertNotNull(query1);
    Assert.assertEquals(topGraph, query1);

    final AbstractActor query2 = ActorPath.lookup(topGraph, "toto");
    Assert.assertNotNull(query2);
    Assert.assertEquals(actor1, query2);

    final AbstractActor query2_1 = ActorPath.lookup(topGraph, "titi");
    Assert.assertNull(query2_1);

    final AbstractActor query2_2 = ActorPath.lookup(topGraph, "secondLevelGraph");
    Assert.assertNotNull(query2_2);
    Assert.assertEquals(secondLevelGraph, query2_2);

    final AbstractActor query3 = ActorPath.lookup(topGraph, "topGraph/toto");
    Assert.assertNotNull(query3);
    Assert.assertEquals(actor1, query3);

    final AbstractActor query3_1 = ActorPath.lookup(topGraph, "topGraph/secondLevelGraph");
    Assert.assertNotNull(query3_1);
    Assert.assertEquals(secondLevelGraph, query3_1);

    final AbstractActor query3_2 = ActorPath.lookup(secondLevelGraph, "titi");
    Assert.assertNotNull(query3_2);
    Assert.assertEquals(actor2, query3_2);

    final AbstractActor query3_3 = ActorPath.lookup(topGraph, "topGraph/secondLevelGraph/titi");
    Assert.assertNotNull(query3_3);
    Assert.assertEquals(actor2, query3_3);

    final AbstractActor query3_4 = ActorPath.lookup(topGraph, "topGraph/secondLevelGraph/titi/tutu");
    Assert.assertNotNull(query3_4);
    Assert.assertEquals(actor3, query3_4);

    // unusual requests

    final AbstractActor query4 = ActorPath.lookup(topGraph, "topGraph/");
    Assert.assertNotNull(query4);
    Assert.assertEquals(topGraph, query4);

    final AbstractActor query5 = ActorPath.lookup(topGraph, "/topGraph");
    Assert.assertNotNull(query5);
    Assert.assertEquals(topGraph, query5);

    final AbstractActor query6 = ActorPath.lookup(topGraph, "topGraph/toto/");
    Assert.assertNotNull(query6);
    Assert.assertEquals(actor1, query6);

    final AbstractActor query7 = ActorPath.lookup(topGraph, "topGraph//toto");
    Assert.assertNotNull(query7);
    Assert.assertEquals(actor1, query7);

    final AbstractActor query8 = ActorPath.lookup(topGraph, "");
    Assert.assertNotNull(query8);
    Assert.assertEquals(topGraph, query8);

    final AbstractActor query9 = ActorPath.lookup(topGraph, "/");
    Assert.assertNotNull(query9);
    Assert.assertEquals(topGraph, query9);

    final AbstractActor query10 = ActorPath.lookup(topGraph, "///topGraph/////secondLevelGraph/////titi///");
    Assert.assertNotNull(query10);
    Assert.assertEquals(actor2, query10);

  }
}
