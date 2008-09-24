package org.ietr.preesm.plugin.mapper;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.ietr.preesm.plugin.mapper.fastalgo.FastAlgoTest;

/**
 * @author mpelcat
 * 
 *         Mapper plugin junit tester
 */
public class AllMapperTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.ietr.preesm.plugin.mapper");
		// $JUnit-BEGIN$
		// suite.addTestSuite(GraphTimeKeeperTest.class);
		// $JUnit-END$
		// $JUnit-BEGIN$
		suite.addTestSuite(FastAlgoTest.class);
		// $JUnit-END$
		return suite;
	}

}
