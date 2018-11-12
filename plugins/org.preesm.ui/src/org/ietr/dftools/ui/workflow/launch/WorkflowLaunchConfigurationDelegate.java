/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2011)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011)
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
package org.ietr.dftools.ui.workflow.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.ietr.dftools.ui.workflow.ScenarioConfiguration;
import org.ietr.dftools.workflow.WorkflowManager;

/**
 * Launch a workflow in run mode, using the previously created launch configuration.
 *
 * @author mwipliez
 * @author mpelcat
 */
public class WorkflowLaunchConfigurationDelegate implements ILaunchConfigurationDelegate {

  /** The Constant ATTR_WORKFLOW_FILE_NAME. */
  public static final String ATTR_WORKFLOW_FILE_NAME = "org.ietr.dftools.ui.workflow.fileName";

  // Beware: Changing this ID without modifying the corresponding ID in
  // org.ietr.dftools.ui/plugin.xml
  /** The workflow launch configuration type id. */
  // can break the launch shortcut for wokflows (Right Click > Run as)
  public static final String WORKFLOW_LAUNCH_CONFIG_TYPE_ID = "net.sf.dftools.ui.workflow.launchConfigurationType";

  /**
   * Launches a workflow.
   *
   * @param configuration
   *          Retrieved from configuration tabs
   * @param mode
   *          Run or debug
   * @param launch
   *          Not used
   * @param monitor
   *          Monitoring the workflow progress
   * @throws CoreException
   *           the core exception
   */
  @Override
  public void launch(final ILaunchConfiguration configuration, final String mode, final ILaunch launch,
      final IProgressMonitor monitor) throws CoreException {

    final String workflowPath = configuration.getAttribute(WorkflowLaunchConfigurationDelegate.ATTR_WORKFLOW_FILE_NAME,
        "");
    final String scenarioPath = configuration.getAttribute(ScenarioConfiguration.ATTR_SCENARIO_FILE_NAME, "");

    final WorkflowManager workflowManager = new WorkflowManager();
    workflowManager.execute(workflowPath, scenarioPath, monitor);
  }
}
