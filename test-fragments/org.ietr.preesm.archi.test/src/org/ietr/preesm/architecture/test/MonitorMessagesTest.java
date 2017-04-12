package org.ietr.preesm.architecture.test;

import org.ietr.preesm.architecture.slam.SlamHierarchyFlattening;
import org.ietr.preesm.architecture.transforms.ArchitectureExporter;
import org.junit.Assert;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class MonitorMessagesTest.
 */
public class MonitorMessagesTest {

  /**
   * Test architecture exporter monitor message.
   */
  @Test
  public void testArchitectureExporterMonitorMessage() {
    final ArchitectureExporter architectureExporter = new ArchitectureExporter();
    final String monitorMessage = architectureExporter.monitorMessage();
    Assert.assertTrue("Exporting architecture.".equals(monitorMessage));
  }

  /**
   * Test slam hierarchy flattening monitor message.
   */
  @Test
  public void testSlamHierarchyFlatteningMonitorMessage() {
    final SlamHierarchyFlattening architectureExporter = new SlamHierarchyFlattening();
    final String monitorMessage = architectureExporter.monitorMessage();
    Assert.assertTrue("Flattening an S-LAM model hierarchy.".equals(monitorMessage));
  }

}
