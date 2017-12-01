package org.ietr.preesm.experiment.model.pimm;

import org.ietr.preesm.experiment.model.factory.PiMMUserFactory;
import org.ietr.preesm.experiment.model.pimm.impl.PiSDFRefinementImpl;
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
    topGraph.getActors().add(actor1);
    final PiGraph secondLevelGraph = PiMMUserFactory.instance.createPiGraph();
    secondLevelGraph.setName("secondLevelGraph");
    topGraph.getActors().add(secondLevelGraph);
    final Actor actor2 = PiMMUserFactory.instance.createActor();
    actor2.setName("titi");
    secondLevelGraph.getActors().add(actor2);

    final PiGraph refinementGraph = PiMMUserFactory.instance.createPiGraph();
    refinementGraph.setName("refinementGraph");
    final PiSDFRefinement pisdfRefinmentMock = Mockito.mock(PiSDFRefinementImpl.class);
    Mockito.when(pisdfRefinmentMock.getAbstractActor()).thenReturn(refinementGraph);
    actor2.setRefinement(pisdfRefinmentMock);
    final Actor actor3 = PiMMUserFactory.instance.createActor();
    actor3.setName("tutu");
    refinementGraph.getActors().add(actor3);

    // usual requests

    final AbstractActor query1 = topGraph.lookupActorFromPath("topGraph");
    Assert.assertNotNull(query1);
    Assert.assertEquals(topGraph, query1);

    final AbstractActor query2 = topGraph.lookupActorFromPath("toto");
    Assert.assertNotNull(query2);
    Assert.assertEquals(actor1, query2);

    final AbstractActor query2_1 = topGraph.lookupActorFromPath("titi");
    Assert.assertNull(query2_1);

    final AbstractActor query2_2 = topGraph.lookupActorFromPath("secondLevelGraph");
    Assert.assertNotNull(query2_2);
    Assert.assertEquals(secondLevelGraph, query2_2);

    final AbstractActor query3 = topGraph.lookupActorFromPath("topGraph/toto");
    Assert.assertNotNull(query3);
    Assert.assertEquals(actor1, query3);

    final AbstractActor query3_1 = topGraph.lookupActorFromPath("topGraph/secondLevelGraph");
    Assert.assertNotNull(query3_1);
    Assert.assertEquals(secondLevelGraph, query3_1);

    final AbstractActor query3_2 = secondLevelGraph.lookupActorFromPath("titi");
    Assert.assertNotNull(query3_2);
    Assert.assertEquals(actor2, query3_2);

    final AbstractActor query3_3 = topGraph.lookupActorFromPath("topGraph/secondLevelGraph/titi");
    Assert.assertNotNull(query3_3);
    Assert.assertEquals(actor2, query3_3);

    final AbstractActor query3_4 = topGraph.lookupActorFromPath("topGraph/secondLevelGraph/titi/tutu");
    Assert.assertNotNull(query3_4);
    Assert.assertEquals(actor3, query3_4);

    // unusual requests

    final AbstractActor query4 = topGraph.lookupActorFromPath("topGraph/");
    Assert.assertNotNull(query4);
    Assert.assertEquals(topGraph, query4);

    final AbstractActor query5 = topGraph.lookupActorFromPath("/topGraph");
    Assert.assertNotNull(query5);
    Assert.assertEquals(topGraph, query5);

    final AbstractActor query6 = topGraph.lookupActorFromPath("topGraph/toto/");
    Assert.assertNotNull(query6);
    Assert.assertEquals(actor1, query6);

    final AbstractActor query7 = topGraph.lookupActorFromPath("topGraph//toto");
    Assert.assertNotNull(query7);
    Assert.assertEquals(actor1, query7);

    final AbstractActor query8 = topGraph.lookupActorFromPath("");
    Assert.assertNotNull(query8);
    Assert.assertEquals(topGraph, query8);

    final AbstractActor query9 = topGraph.lookupActorFromPath("/");
    Assert.assertNotNull(query9);
    Assert.assertEquals(topGraph, query9);

    final AbstractActor query10 = topGraph.lookupActorFromPath("///topGraph/////secondLevelGraph/////titi///");
    Assert.assertNotNull(query10);
    Assert.assertEquals(actor2, query10);

  }
}
