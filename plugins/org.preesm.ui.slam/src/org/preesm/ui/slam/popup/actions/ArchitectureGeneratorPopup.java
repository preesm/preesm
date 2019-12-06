package org.preesm.ui.slam.popup.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.preesm.model.slam.generator.ArchitecturesGenerator;
import org.preesm.ui.PreesmUIPlugin;
import org.preesm.ui.wizards.PreesmProjectNature;

/**
 * Provides commands to generate default architectures.
 * 
 * @author ahonorat
 *
 */
public class ArchitectureGeneratorPopup extends AbstractHandler {

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    try {
      // Get the selected IProject
      final IWorkbenchPage page = PreesmUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
      final TreeSelection selection = (TreeSelection) page.getSelection();
      final IProject project = (IProject) selection.getFirstElement();
      // If it is a Preesm project, generate default design in Archi/ folder
      if (project.hasNature(PreesmProjectNature.ID)) {
        ArchitecturesGenerator generator = new ArchitecturesGenerator(project);
        generator.generateAndSaveArchitecture(4);
      }

    } catch (final Exception e) {
      throw new ExecutionException("Could not generate scenarios", e);
    }
    return null;
  }

}
