package org.ietr.preesm.experiment.model.pimm;

import org.ietr.preesm.experiment.model.pimm.impl.ActorImpl;
import org.junit.Assert;
import org.junit.Test;

public class ActorTest {

	@Test
	public void testCreateActor() {
		Actor actor = null;
		Assert.assertNull(actor);
		actor = PiMMFactory.eINSTANCE.createActor();
		Assert.assertNotNull(actor);
		Assert.assertTrue(ActorImpl.class.isInstance(actor));
		Assert.assertNull(actor.getGraph());
	}

}