package org.ietr.preesm.codegen.xtend.test;

import org.eclipse.emf.common.util.EList;
import org.ietr.preesm.codegen.xtend.model.codegen.ActorBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.CodeElt;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenFactory;
import org.junit.Assert;
import org.junit.Test;

public class ActorBlockTest {

	@Test
	public void testConstruct() {
		final ActorBlock actor = CodegenFactory.eINSTANCE.createActorBlock();
		Assert.assertNotNull(actor);
		final EList<CodeElt> codeElts = actor.getCodeElts();
		Assert.assertNotNull(codeElts);

		final int size = codeElts.size();
		Assert.assertEquals(2, size);

	}
}
