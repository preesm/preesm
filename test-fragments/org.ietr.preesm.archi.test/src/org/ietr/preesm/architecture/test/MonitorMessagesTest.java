package org.ietr.preesm.architecture.test;

import org.ietr.preesm.architecture.slam.SlamHierarchyFlattening;
import org.ietr.preesm.architecture.transforms.ArchitectureExporter;
import org.junit.Assert;
import org.junit.Test;

public class MonitorMessagesTest {

	@Test
	public void testArchitectureExporterMonitorMessage() {
		final ArchitectureExporter architectureExporter = new ArchitectureExporter();
		final String monitorMessage = architectureExporter.monitorMessage();
		Assert.assertTrue("Exporting architecture.".equals(monitorMessage));
	}

	@Test
	public void testSlamHierarchyFlatteningMonitorMessage() {
		final SlamHierarchyFlattening architectureExporter = new SlamHierarchyFlattening();
		final String monitorMessage = architectureExporter.monitorMessage();
		Assert.assertTrue("Flattening an S-LAM model hierarchy.".equals(monitorMessage));
	}

}
