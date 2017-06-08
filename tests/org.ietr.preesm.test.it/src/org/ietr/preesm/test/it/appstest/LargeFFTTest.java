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
package org.ietr.preesm.test.it.appstest;

import java.io.FileNotFoundException;
import org.eclipse.core.runtime.CoreException;
import org.ietr.dftools.algorithm.importer.InvalidModelException;
import org.ietr.preesm.test.it.api.WorkflowRunner;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 */
@Ignore
public class LargeFFTTest {

  @Test
  public void testLargeFFT11() throws FileNotFoundException, InvalidModelException, CoreException {
    final String projectName = "org.ietr.preesm.largeFFT";
    final String[] scenarios = new String[] { "largeFFT_hawking.scenario" };
    final String[] workflows = new String[] { "CodegenWMemScripts.workflow" };

    for (final String workflow : workflows) {
      for (final String scenario : scenarios) {
        final String workflowFilePathStr = "/Workflows/" + workflow;
        final String scenarioFilePathStr = "/Scenarios/" + scenario;
        final boolean success = WorkflowRunner.runWorkFlow(projectName, workflowFilePathStr, scenarioFilePathStr);
        Assert.assertTrue("Workflow [" + workflow + "] with scenario [" + scenario + "] caused failure", success);
      }
    }
  }

  @Test
  public void testLargeFFT21() throws FileNotFoundException, InvalidModelException, CoreException {
    final String projectName = "org.ietr.preesm.largeFFT";
    final String[] scenarios = new String[] { "largeFFT_lamaar.scenario" };
    final String[] workflows = new String[] { "CodegenWMemScripts.workflow" };

    for (final String workflow : workflows) {
      for (final String scenario : scenarios) {
        final String workflowFilePathStr = "/Workflows/" + workflow;
        final String scenarioFilePathStr = "/Scenarios/" + scenario;
        final boolean success = WorkflowRunner.runWorkFlow(projectName, workflowFilePathStr, scenarioFilePathStr);
        Assert.assertTrue("Workflow [" + workflow + "] with scenario [" + scenario + "] caused failure", success);
      }
    }
  }

  @Test
  public void testLargeFFT31() throws FileNotFoundException, InvalidModelException, CoreException {
    final String projectName = "org.ietr.preesm.largeFFT";
    final String[] scenarios = new String[] { "largeFFT_monox86.scenario" };
    final String[] workflows = new String[] { "CodegenWMemScripts.workflow" };

    for (final String workflow : workflows) {
      for (final String scenario : scenarios) {
        final String workflowFilePathStr = "/Workflows/" + workflow;
        final String scenarioFilePathStr = "/Scenarios/" + scenario;
        final boolean success = WorkflowRunner.runWorkFlow(projectName, workflowFilePathStr, scenarioFilePathStr);
        Assert.assertTrue("Workflow [" + workflow + "] with scenario [" + scenario + "] caused failure", success);
      }
    }
  }

  @Test
  public void testLargeFFT12() throws FileNotFoundException, InvalidModelException, CoreException {
    final String projectName = "org.ietr.preesm.largeFFT";
    final String[] scenarios = new String[] { "largeFFT_hawking.scenario" };
    final String[] workflows = new String[] { "SDF3Exporter.workflow" };

    for (final String workflow : workflows) {
      for (final String scenario : scenarios) {
        final String workflowFilePathStr = "/Workflows/" + workflow;
        final String scenarioFilePathStr = "/Scenarios/" + scenario;
        final boolean success = WorkflowRunner.runWorkFlow(projectName, workflowFilePathStr, scenarioFilePathStr);
        Assert.assertTrue("Workflow [" + workflow + "] with scenario [" + scenario + "] caused failure", success);
      }
    }
  }

  @Test
  public void testLargeFFT22() throws FileNotFoundException, InvalidModelException, CoreException {
    final String projectName = "org.ietr.preesm.largeFFT";
    final String[] scenarios = new String[] { "largeFFT_lamaar.scenario" };
    final String[] workflows = new String[] { "SDF3Exporter.workflow" };

    for (final String workflow : workflows) {
      for (final String scenario : scenarios) {
        final String workflowFilePathStr = "/Workflows/" + workflow;
        final String scenarioFilePathStr = "/Scenarios/" + scenario;
        final boolean success = WorkflowRunner.runWorkFlow(projectName, workflowFilePathStr, scenarioFilePathStr);
        Assert.assertTrue("Workflow [" + workflow + "] with scenario [" + scenario + "] caused failure", success);
      }
    }
  }

  @Test
  public void testLargeFFT32() throws FileNotFoundException, InvalidModelException, CoreException {
    final String projectName = "org.ietr.preesm.largeFFT";
    final String[] scenarios = new String[] { "largeFFT_monox86.scenario" };
    final String[] workflows = new String[] { "SDF3Exporter.workflow" };

    for (final String workflow : workflows) {
      for (final String scenario : scenarios) {
        final String workflowFilePathStr = "/Workflows/" + workflow;
        final String scenarioFilePathStr = "/Scenarios/" + scenario;
        final boolean success = WorkflowRunner.runWorkFlow(projectName, workflowFilePathStr, scenarioFilePathStr);
        Assert.assertTrue("Workflow [" + workflow + "] with scenario [" + scenario + "] caused failure", success);
      }
    }
  }

  @Test
  public void testLargeFFT13() throws FileNotFoundException, InvalidModelException, CoreException {
    final String projectName = "org.ietr.preesm.largeFFT";
    final String[] scenarios = new String[] { "largeFFT_hawking.scenario" };
    final String[] workflows = new String[] { "Simulate.workflow" };

    for (final String workflow : workflows) {
      for (final String scenario : scenarios) {
        final String workflowFilePathStr = "/Workflows/" + workflow;
        final String scenarioFilePathStr = "/Scenarios/" + scenario;
        final boolean success = WorkflowRunner.runWorkFlow(projectName, workflowFilePathStr, scenarioFilePathStr);
        Assert.assertTrue("Workflow [" + workflow + "] with scenario [" + scenario + "] caused failure", success);
      }
    }
  }

  @Test
  public void testLargeFFT23() throws FileNotFoundException, InvalidModelException, CoreException {
    final String projectName = "org.ietr.preesm.largeFFT";
    final String[] scenarios = new String[] { "largeFFT_lamaar.scenario" };
    final String[] workflows = new String[] { "Simulate.workflow" };

    for (final String workflow : workflows) {
      for (final String scenario : scenarios) {
        final String workflowFilePathStr = "/Workflows/" + workflow;
        final String scenarioFilePathStr = "/Scenarios/" + scenario;
        final boolean success = WorkflowRunner.runWorkFlow(projectName, workflowFilePathStr, scenarioFilePathStr);
        Assert.assertTrue("Workflow [" + workflow + "] with scenario [" + scenario + "] caused failure", success);
      }
    }
  }

  @Test
  public void testLargeFFT33() throws FileNotFoundException, InvalidModelException, CoreException {
    final String projectName = "org.ietr.preesm.largeFFT";
    final String[] scenarios = new String[] { "largeFFT_monox86.scenario" };
    final String[] workflows = new String[] { "Simulate.workflow" };

    for (final String workflow : workflows) {
      for (final String scenario : scenarios) {
        final String workflowFilePathStr = "/Workflows/" + workflow;
        final String scenarioFilePathStr = "/Scenarios/" + scenario;
        final boolean success = WorkflowRunner.runWorkFlow(projectName, workflowFilePathStr, scenarioFilePathStr);
        Assert.assertTrue("Workflow [" + workflow + "] with scenario [" + scenario + "] caused failure", success);
      }
    }
  }

  @Test
  public void testLargeFFT14() throws FileNotFoundException, InvalidModelException, CoreException {
    final String projectName = "org.ietr.preesm.largeFFT";
    final String[] scenarios = new String[] { "largeFFT_hawking.scenario" };
    final String[] workflows = new String[] { "XTendCodegen.workflow" };

    for (final String workflow : workflows) {
      for (final String scenario : scenarios) {
        final String workflowFilePathStr = "/Workflows/" + workflow;
        final String scenarioFilePathStr = "/Scenarios/" + scenario;
        final boolean success = WorkflowRunner.runWorkFlow(projectName, workflowFilePathStr, scenarioFilePathStr);
        Assert.assertTrue("Workflow [" + workflow + "] with scenario [" + scenario + "] caused failure", success);
      }
    }
  }

  @Test
  public void testLargeFFT24() throws FileNotFoundException, InvalidModelException, CoreException {
    final String projectName = "org.ietr.preesm.largeFFT";
    final String[] scenarios = new String[] { "largeFFT_lamaar.scenario" };
    final String[] workflows = new String[] { "XTendCodegen.workflow" };

    for (final String workflow : workflows) {
      for (final String scenario : scenarios) {
        final String workflowFilePathStr = "/Workflows/" + workflow;
        final String scenarioFilePathStr = "/Scenarios/" + scenario;
        final boolean success = WorkflowRunner.runWorkFlow(projectName, workflowFilePathStr, scenarioFilePathStr);
        Assert.assertTrue("Workflow [" + workflow + "] with scenario [" + scenario + "] caused failure", success);
      }
    }
  }

  @Test
  public void testLargeFFT34() throws FileNotFoundException, InvalidModelException, CoreException {
    final String projectName = "org.ietr.preesm.largeFFT";
    final String[] scenarios = new String[] { "largeFFT_monox86.scenario" };
    final String[] workflows = new String[] { "XTendCodegen.workflow" };

    for (final String workflow : workflows) {
      for (final String scenario : scenarios) {
        final String workflowFilePathStr = "/Workflows/" + workflow;
        final String scenarioFilePathStr = "/Scenarios/" + scenario;
        final boolean success = WorkflowRunner.runWorkFlow(projectName, workflowFilePathStr, scenarioFilePathStr);
        Assert.assertTrue("Workflow [" + workflow + "] with scenario [" + scenario + "] caused failure", success);
      }
    }
  }
}
