/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
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
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.part.FileEditorInput;
import org.ietr.dftools.ui.Activator;
import org.ietr.dftools.ui.util.FileUtils;
import org.ietr.dftools.ui.workflow.ScenarioConfiguration;
import org.ietr.dftools.workflow.messages.WorkflowMessages;
import org.ietr.dftools.workflow.tools.WorkflowLogger;

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
      WorkflowLogger.getLogger().log(Level.SEVERE, "Problem creating the Preesm launch configuration.", e);
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
    scenarioExtensions.add("piscenario");
    final String message = WorkflowMessages.getString("Workflow.browseScenarioTitle");
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
   * Show a selection dialog that allows the user to choose one of the specified launch configurations. Return the
   * chosen config, or <code>null</code> if the user canceled the dialog.
   *
   * @param configList
   *          the config list
   * @return the i launch configuration
   */
  protected ILaunchConfiguration chooseConfiguration(final List<ILaunchConfiguration> configList) {
    final IDebugModelPresentation labelProvider = DebugUITools.newDebugModelPresentation();
    final ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), labelProvider);
    dialog.setElements(configList.toArray());
    dialog.setTitle("Choose the workflow configuration");
    dialog.setMessage("");
    dialog.setMultipleSelection(false);
    final int result = dialog.open();
    labelProvider.dispose();
    if (result == Window.OK) {
      return (ILaunchConfiguration) dialog.getFirstResult();
    }
    return null;
  }

  /**
   * Search for the existing launch configurations with the same executable, so as to not create a new configuration if
   * there is already one for the same executable.
   *
   * @param file
   *          the file
   * @return the first matching configuration, or null if none was found
   */
  private ILaunchConfiguration findExistingLaunchConfiguration(final IFile file) {
    final ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
    final ILaunchConfigurationType type = manager
        .getLaunchConfigurationType(WorkflowLaunchConfigurationDelegate.WORKFLOW_LAUNCH_CONFIG_TYPE_ID);

    if (type != null) {
      final List<ILaunchConfiguration> candidateConfigs = new ArrayList<>();
      try {
        final ILaunchConfiguration[] configs = manager.getLaunchConfigurations(type);

        if ((configs != null) && (configs.length > 0)) {
          for (final ILaunchConfiguration configuration : configs) {
            final String candidateFile = configuration
                .getAttribute(WorkflowLaunchConfigurationDelegate.ATTR_WORKFLOW_FILE_NAME, "");

            final String newFile = file.getFullPath().toString();
            if (candidateFile.equals(newFile)) {
              candidateConfigs.add(configuration);
            }
          }
        }
      } catch (final CoreException e) {
        WorkflowLogger.getLogger().log(Level.SEVERE, "Error while looking for existing ILaunchConfiguration", e);

      }

      // If there are no existing configs associated with the IFile,
      // create one.
      // If there is exactly one config associated with the IFile, return
      // it.
      // Otherwise, if there is more than one config associated with the
      // IFile, prompt the
      // user to choose one.
      final int candidateCount = candidateConfigs.size();
      if (candidateCount < 1) {

        return WorkflowLaunchShortcut.createLaunchConfiguration(file);
      } else if (candidateCount == 1) {
        return candidateConfigs.get(0);
      } else {
        // Prompt the user to choose a config. A null result means the
        // user
        // canceled the dialog, in which case this method returns null,
        // since canceling the dialog should also cancel launching
        // anything.
        final ILaunchConfiguration config = chooseConfiguration(candidateConfigs);
        if (config != null) {
          return config;
        }
      }
    }

    return null;
  }

  /**
   * Gets the shell.
   *
   * @return the shell
   */
  private Shell getShell() {
    return Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
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
    if ((editor.getEditorInput() != null) && (editor.getEditorInput() instanceof FileEditorInput)) {
      final FileEditorInput fileEditorInput = (FileEditorInput) editor.getEditorInput();

      final IFile file = fileEditorInput.getFile();

      final ILaunchConfiguration configuration = findExistingLaunchConfiguration(file);
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
      if ((first != null) && (first instanceof IFile)) {
        final IFile file = (IFile) first;

        final ILaunchConfiguration configuration = WorkflowLaunchShortcut.createLaunchConfiguration(file);
        if (configuration != null) {
          DebugUITools.launch(configuration, mode);
        }
      }
    }
  }

}
