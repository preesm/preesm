
package org.preesm.ui.pisdf.features;

import java.util.LinkedHashSet;
import java.util.Set;
import org.eclipse.core.runtime.IPath;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.ui.PlatformUI;
import org.preesm.model.pisdf.Actor;
import org.preesm.ui.utils.FileUtils;

/**
 * Custom Feature to set a new read passive script to an {@link Actor}.
 *
 * @author rcazoulat
 */
public class SetActorReadPassiveScriptFeature extends AbstractCustomFeature {

  /** The has done changes. */
  protected boolean hasDoneChanges = false;

  /**
   * Default Constructor of {@link SetActorReadPassiveScriptFeature}.
   *
   * @param fp
   *          the feature provider
   */
  public SetActorReadPassiveScriptFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
   */
  @Override
  public String getName() {
    return "Set read passive script path";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getDescription()
   */
  @Override
  public String getDescription() {
    return "Set the path to the read passive script of an Actor";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#canExecute(org.eclipse.graphiti.features.context.
   * ICustomContext)
   */
  @Override
  public boolean canExecute(final ICustomContext context) {
    // Allow setting if exactly one pictogram element
    // representing an Actor is selected
    boolean ret = false;
    final PictogramElement[] pes = context.getPictogramElements();
    if ((pes != null) && (pes.length == 1)) {
      final Object bo = getBusinessObjectForPictogramElement(pes[0]);
      if (bo instanceof Actor) {
        ret = true;
      }
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.eclipse.graphiti.features.custom.ICustomFeature#execute(org.eclipse.graphiti.features.context.ICustomContext)
   */
  @Override
  public void execute(final ICustomContext context) {
    // Re-check if only one element is selected
    final PictogramElement[] pes = context.getPictogramElements();
    if ((pes != null) && (pes.length == 1)) {
      final Object bo = getBusinessObjectForPictogramElement(pes[0]);
      if (bo instanceof final Actor actor) {
        final String dialogTitle = "Select a read passive script";
        askReadPassiveScript(actor, dialogTitle);

        // Call the layout feature
        layoutPictogramElement(pes[0]);
      }
    }
  }

  /**
   * Ask read passive script.
   *
   * @param actor
   *          the actor
   * @param question
   *          the question
   * @param dialogTitle
   *          the dialog title
   */
  private void askReadPassiveScript(final Actor actor, final String dialogTitle) {
    // Ask user for read passive script
    final Set<String> fileExtensions = new LinkedHashSet<>();
    fileExtensions.add("bsh");
    final IPath newFilePath = FileUtils.browseFiles(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
        dialogTitle, fileExtensions);

    final String filePathString = newFilePath.toString();
    if ((filePathString != null) && (!newFilePath.toString().equals(actor.getReadPassiveScriptPath()))) {
      this.hasDoneChanges = true;
      actor.setReadPassiveScriptPath(newFilePath.toString());
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.impl.AbstractFeature#hasDoneChanges()
   */
  @Override
  public boolean hasDoneChanges() {
    return this.hasDoneChanges;
  }

}
