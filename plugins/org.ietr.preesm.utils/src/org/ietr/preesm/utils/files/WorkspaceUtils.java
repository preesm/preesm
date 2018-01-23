package org.ietr.preesm.utils.files;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 *
 * @author anmorvan
 *
 */
public class WorkspaceUtils {

  /**
   * Update workspace.
   */
  public static final void updateWorkspace() {

    try {
      final IWorkspace workspace = ResourcesPlugin.getWorkspace();

      workspace.getRoot().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }
}
