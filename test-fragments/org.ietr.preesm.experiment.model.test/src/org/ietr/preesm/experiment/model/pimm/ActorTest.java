package org.ietr.preesm.experiment.model.pimm;

import org.junit.Assert;
import org.junit.Test;

public class ActorTest {

	@Test
	public void test1() {
		Actor actor = null;
		Assert.assertNull(actor);
		actor = PiMMFactory.eINSTANCE.createActor();
		Assert.assertNotNull(actor);
		Assert.assertNull(actor.getGraph());
	}
}