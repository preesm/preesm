package org.preesm.ui.scenario.editor.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.PlatformUI;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.WorkspaceUtils;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.serialize.ScenarioWriter;
import org.preesm.ui.utils.FileUtils;

public class SaveScenario {
  public static IPath save(final IProject iProject, final Scenario scenario, final String name) {
    final IPath targetFolder = FileUtils.browseFiles(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
        "Select result location", "Select the folder where to write the computed PiGraph " + name + ".",
        (Collection<String>) null);
    if (targetFolder == null) {
      // the user canceled the save operation.
      return targetFolder;
    }
    saveScenarioInFolder(iProject, targetFolder, scenario, name);

    return targetFolder;

  }

  public static void saveScenarioInFolder(final IProject iProject, final IPath targetFolder, final Scenario scenario,
      final String name) {

    final String fileName = name + ".scenario";
    final URI uri = FileUtils.getPathToFileInFolder(iProject, targetFolder, fileName);

    // Get the project
    final String platformString = uri.toPlatformString(true);
    final IFile documentFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(platformString));
    final String osString = documentFile.getLocation().toOSString();
    try (final OutputStream outStream = new FileOutputStream(osString);) {
      // Write the Graph to the OutputStream using the Pi format
      new ScenarioWriter(scenario);
      // new PiWriter(uri).write(graph, outStream);
    } catch (final IOException e) {
      throw new PreesmRuntimeException("Could not open outputstream file " + uri.toPlatformString(false));
    }

    WorkspaceUtils.updateWorkspace();
  }
}
