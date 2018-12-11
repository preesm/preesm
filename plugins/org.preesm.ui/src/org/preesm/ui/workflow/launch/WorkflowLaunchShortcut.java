/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
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
package org.preesm.ui.workflow.launch;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.messages.PreesmMessages;
import org.preesm.ui.utils.FileUtils;
import org.preesm.ui.workflow.ScenarioConfiguration;

/**
 * Shortcut for launching an executable directly from the navigator, without having to create a launch configuration
 * manually.
 *
 * @author mwipliez
 * @author mpelcat
 */
public class WorkflowLaunchShortcut implements ILaunchShortcut {

  /**
   * Creates configuration that references given workflow and scenario. The configuration is only created if
   * non-existing.
   *
   * @param file
   *          the file
   * @return the i launch configuration
   */
  public static ILaunchConfiguration createLaunchConfiguration(final IFile file) {

    final IPath scenarioPath = WorkflowLaunchShortcut.lookupScenarioFilePath();

    if ((scenarioPath == null) || scenarioPath.isEmpty()) {
      return null;
    }

    final IPath workflowPath = file.getFullPath();

    final ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
    final ILaunchConfigurationType type = manager
        .getLaunchConfigurationType(WorkflowLaunchConfigurationDelegate.WORKFLOW_LAUNCH_CONFIG_TYPE_ID);
    final ILaunchConfigurationWorkingCopy workingCopy;

    try {
      final String launchConfigurationName = WorkflowLaunchShortcut.generateLaunchConfigurationName(workflowPath,
          scenarioPath);
      workingCopy = type.newInstance(null, launchConfigurationName);
    } catch (final CoreException e) {
      PreesmLogger.getLogger().log(Level.SEVERE, "Problem creating the Preesm launch configuration.", e);
      return null;
    }

    workingCopy.setAttribute(WorkflowLaunchConfigurationDelegate.ATTR_WORKFLOW_FILE_NAME, workflowPath.toString());
    workingCopy.setAttribute(ScenarioConfiguration.ATTR_SCENARIO_FILE_NAME, scenarioPath.toString());

    // set the defaults on the common tab
    final CommonTab tab = new CommonTab();
    tab.setDefaults(workingCopy);
    tab.dispose();

    try {
      return workingCopy.doSave();
    } catch (final CoreException e) {
      return null;
    }
  }

  private static IPath lookupScenarioFilePath() {
    // We ask for the scenario to use with the selected workflow
    final Set<String> scenarioExtensions = new LinkedHashSet<>();
    scenarioExtensions.add("scenario");
    final String message = PreesmMessages.getString("Workflow.browseScenarioTitle");
    return FileUtils.browseFiles(message, scenarioExtensions);
  }

  private static String generateLaunchConfigurationName(final IPath workflowPath, final IPath scenarioPath) {
    final int workflowSegmentCount = workflowPath.segmentCount();
    final int scenarioSegmentCount = scenarioPath.segmentCount();
    if ((scenarioSegmentCount < 1) || (workflowSegmentCount < 1)) {
      throw new IllegalArgumentException("Given path arguments are mal formed");
    }

    final String projectName = workflowPath.segments()[0];
    final String workflowFileName = workflowPath.segments()[workflowSegmentCount - 1]
        .replace("." + workflowPath.getFileExtension(), "");
    final String scenarioFileName = scenarioPath.segments()[scenarioSegmentCount - 1]
        .replace("." + scenarioPath.getFileExtension(), "");

    // from org.eclipse.debug.internal.core.LaunchManager, disallow chars '@', '&','\\', '/', ':', '*', '?', '"', '<',
    // '>', '|', '\0'
    return projectName + " [" + workflowFileName + "] [" + scenarioFileName + "]";
  }

  /**
   * From a workflow editor, tries to reexecute the preceding scenario.
   *
   * @param editor
   *          the editor
   * @param mode
   *          the mode
   */
  @Override
  public void launch(final IEditorPart editor, final String mode) {
    if (editor.getEditorInput() instanceof FileEditorInput) {
      final FileEditorInput fileEditorInput = (FileEditorInput) editor.getEditorInput();

      final IFile file = fileEditorInput.getFile();

      final ILaunchConfiguration configuration = WorkflowLaunchShortcut.createLaunchConfiguration(file);
      if (configuration != null) {
        DebugUITools.launch(configuration, mode);
      }
    }
  }

  /**
   * From a workflow selection, asks for the scenario.
   *
   * @param selection
   *          the selection
   * @param mode
   *          the mode
   */
  @Override
  public void launch(final ISelection selection, final String mode) {
    if (selection instanceof IStructuredSelection) {
      final IStructuredSelection sel = (IStructuredSelection) selection;

      final Object first = sel.getFirstElement();
      if (first instanceof IFile) {
        final IFile file = (IFile) first;

        final ILaunchConfiguration configuration = WorkflowLaunchShortcut.createLaunchConfiguration(file);
        if (configuration != null) {
          DebugUITools.launch(configuration, mode);
        }
      }
    }
  }

}
