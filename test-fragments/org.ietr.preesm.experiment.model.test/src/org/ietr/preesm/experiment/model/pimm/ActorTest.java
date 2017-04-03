package org.ietr.preesm.experiment.model.pimm;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class ActorTest {

	@Test
	public void testCreateActor() {
		Actor actor = null;
		Assert.assertNull(actor);
		actor = PiMMFactory.eINSTANCE.createActor();
		Assert.assertNotNull(actor);
		Assert.assertNull(actor.getGraph());
	}

	@Test
	public void testFail() {
		Actor actor = null;
		Assert.assertNull(actor);
		actor = PiMMFactory.eINSTANCE.createActor();
		Assert.assertNull(actor);
	}

	@Test
	@Ignore
	public void test1() {
		//whatever ... it's skipped
	}
}