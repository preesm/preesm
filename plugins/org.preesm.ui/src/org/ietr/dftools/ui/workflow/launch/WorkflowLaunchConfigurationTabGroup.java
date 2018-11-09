/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2011)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011 - 2012)
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

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

// TODO: Auto-generated Javadoc
/**
 * Creating the tabs displaying launch properties for a workflow. The only options here are a scenario path and a
 * workflow path.
 *
 * @author Matthieu Wipliez
 * @author mpelcat
 */
public class WorkflowLaunchConfigurationTabGroup extends AbstractLaunchConfigurationTabGroup {

  /**
   * Instantiates a new workflow launch configuration tab group.
   */
  public WorkflowLaunchConfigurationTabGroup() {
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.debug.ui.ILaunchConfigurationTabGroup#createTabs(org.eclipse.debug.ui.ILaunchConfigurationDialog,
   * java.lang.String)
   */
  @Override
  public void createTabs(final ILaunchConfigurationDialog dialog, final String mode) {
    final ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] { new WorkFlowLaunchWorkflowTab(),
        // new WorkFlowLaunchArchitectureTab(),
        new WorkFlowLaunchScenarioTab() };
    setTabs(tabs);
  }

}
