package org.ietr.preesm.ui.fields;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Text;

public class FieldUtils {

  private FieldUtils() {
    // disallow instantiation
  }

  public static final boolean testPathValidInWorkspace(String textFieldContent) {
    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
    final IWorkspaceRoot root = workspace.getRoot();
    final IResource findMember = root.findMember(Path.fromOSString(textFieldContent));
    return findMember != null;
  }

  public static final void colorRedOnCondition(final Text text, final boolean condition) {
    if (condition) {
      text.setBackground(new Color(null, 240, 150, 150));
    } else {
      text.setBackground(new Color(null, 255, 255, 255));
    }
  }
}
