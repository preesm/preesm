/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
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

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.ietr.dftools.ui.Activator;

// TODO: Auto-generated Javadoc
/**
 * Launch Tab for algorithm options. From this tab, an {@link AlgorithmConfiguration} is generated that feeds an
 * {@link AlgorithmRetriever} to create the input algorithm.
 *
 * @author mpelcat
 */
public class WorkFlowLaunchWorkflowTab extends AbstractWorkFlowLaunchTab {

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.ui.workflow.launch.AbstractWorkFlowLaunchTab#createControl(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createControl(final Composite parent) {

    super.createControl(parent);
    drawFileChooser("Workflow file:", WorkflowLaunchConfigurationDelegate.ATTR_WORKFLOW_FILE_NAME);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
   */
  @Override
  public String getName() {
    return "Workflow";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.ui.workflow.launch.AbstractWorkFlowLaunchTab#initializeFrom(org.eclipse.debug.core.
   * ILaunchConfiguration)
   */
  @Override
  public void initializeFrom(final ILaunchConfiguration configuration) {
    super.initializeFrom(configuration);
    setDirty(false);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.ui.workflow.launch.AbstractWorkFlowLaunchTab#performApply(org.eclipse.debug.core.
   * ILaunchConfigurationWorkingCopy)
   */
  @Override
  public void performApply(final ILaunchConfigurationWorkingCopy configuration) {
    super.performApply(configuration);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.ui.workflow.launch.AbstractWorkFlowLaunchTab#setDefaults(org.eclipse.debug.core.
   * ILaunchConfigurationWorkingCopy)
   */
  @Override
  public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {

  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#getImage()
   */
  @Override
  public Image getImage() {
    final Image image = Activator.getImage("icons/workflow.png");

    if (image != null) {
      return image;
    }

    return super.getImage();
  }
}
