package org.ietr.preesm.test.it.api;

import org.ietr.dftools.workflow.AbstractWorkflowExecutor;
import org.ietr.dftools.workflow.tools.CLIWorkflowLogger;

/**
 *
 */
public class PreesmTestWorkflowExecutor extends AbstractWorkflowExecutor {

  public PreesmTestWorkflowExecutor() {
    setLogger(new CLIWorkflowLogger(true));
  }

}
