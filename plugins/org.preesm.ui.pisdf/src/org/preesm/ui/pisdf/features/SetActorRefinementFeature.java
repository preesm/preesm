/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2015)
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
package org.preesm.ui.pisdf.features;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.ui.PlatformUI;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.PiSDFRefinement;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.header.parser.HeaderParser;
import org.preesm.ui.pisdf.PiMMUtil;
import org.preesm.ui.utils.FileUtils;

// TODO: Auto-generated Javadoc
/**
 * Custom Feature to set a new {@link PiSDFRefinement} of an {@link Actor}.
 *
 * @author kdesnos
 */
public class SetActorRefinementFeature extends AbstractCustomFeature {

  /** The has done changes. */
  protected boolean hasDoneChanges = false;

  /**
   * The Enum PrototypeFilter.
   */
  enum PrototypeFilter {

    /** The none. */
    NONE,
    /** The init actor. */
    INIT_ACTOR,
    /** The loop actor. */
    LOOP_ACTOR,
    /** The init. */
    INIT
  }

  /** The show only valid prototypes. */
  boolean showOnlyValidPrototypes = true;

  /**
   * Default Constructor of {@link SetActorRefinementFeature}.
   *
   * @param fp
   *          the feature provider
   */
  public SetActorRefinementFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
   */
  @Override
  public String getName() {
    return "Set Refinement";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getDescription()
   */
  @Override
  public String getDescription() {
    return "Set/Change the Refinement of an Actor";
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
      if (bo instanceof Actor || bo instanceof Delay) {
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
      if (bo instanceof Actor) {
        final Actor actor = (Actor) bo;

        final String question = "Please select a valid file\n(.idl, .h or .pi)";
        final String dialogTitle = "Select a refinement file";
        final IPath path = askRefinement(actor, question, dialogTitle);
        if (path != null) {
          setActorRefinement(actor, path);
        }

        // Call the layout feature
        layoutPictogramElement(pes[0]);
      }
    }
  }

  /**
   * Ask refinement.
   *
   * @param actor
   *          the actor
   * @param question
   *          the question
   * @param dialogTitle
   *          the dialog title
   * @return the i path
   */
  private IPath askRefinement(final Actor actor, final String question, final String dialogTitle) {
    // Ask user for Actor name until a valid name is entered.
    // For now, authorized refinements are other PiGraphs (.pi files) and
    // .idl prototypes
    final Set<String> fileExtensions = new LinkedHashSet<>();
    fileExtensions.add("pi");
    fileExtensions.add("idl");
    fileExtensions.add("h");
    final IPath newFilePath = FileUtils.browseFiles(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
        dialogTitle, fileExtensions);

    return newFilePath;
  }

  /**
   * Set the refinement file of the actor and ask for the prototype in case the file is a C Header.
   *
   * @param actor
   *          the actor
   * @param newFilePath
   *          The {@link IPath} to the file. (Must not be null)
   */
  protected void setActorRefinement(final Actor actor, IPath newFilePath) {
    final String dialogTitle = "Select a refinement file";

    boolean validRefinement = false;
    do {
      // If the file is a .h header
      if (newFilePath.getFileExtension().equals("h")) {

        List<FunctionPrototype> loopPrototypes;
        FunctionPrototype[] allProtoArray;
        IFile file;

        // We get it
        file = ResourcesPlugin.getWorkspace().getRoot().getFile(newFilePath);

        // Get all prototypes first (no filter)
        final List<FunctionPrototype> allPrototypes = getPrototypes(file, actor, PrototypeFilter.NONE);
        allProtoArray = allPrototypes.toArray(new FunctionPrototype[allPrototypes.size()]);

        loopPrototypes = getPrototypes(file, actor, PrototypeFilter.LOOP_ACTOR);
        validRefinement = (!loopPrototypes.isEmpty()) || (!allPrototypes.isEmpty());
        if (!validRefinement) {
          final String message = "The .h file you selected does not contain any prototype."
              + ".\nPlease select another valid file.";
          newFilePath = askRefinement(actor, message, dialogTitle);

          // If the cancel button of the dialog box was clicked
          // stop the setRefinement process.
          if (newFilePath == null) {
            return;
          }
        } else {
          // The file is a valid .h file.
          String title = "Loop Function Selection";
          String message = "Select a loop function for actor " + actor.getName() + "\n(* = any string, ? = any char):";
          final FunctionPrototype[] loopProtoArray = loopPrototypes
              .toArray(new FunctionPrototype[loopPrototypes.size()]);
          final FunctionPrototype loopProto = PiMMUtil.selectFunction(loopProtoArray, allProtoArray, title, message,
              this.showOnlyValidPrototypes);

          final List<FunctionPrototype> initPrototypes = getPrototypes(file, actor, PrototypeFilter.INIT_ACTOR);
          final List<FunctionPrototype> allInitPrototypes = getPrototypes(file, actor, PrototypeFilter.INIT);

          FunctionPrototype initProto = null;
          if (!initPrototypes.isEmpty() || !allInitPrototypes.isEmpty()) {
            title = "Init Function Selection";
            message = "Select an optionnal init function for actor " + actor.getName()
                + ", or click Cancel\n(* = any string, ? = any char):";
            final FunctionPrototype[] initProtoArray = initPrototypes
                .toArray(new FunctionPrototype[initPrototypes.size()]);
            final FunctionPrototype[] allInitProtoArray = allInitPrototypes
                .toArray(new FunctionPrototype[allInitPrototypes.size()]);
            initProto = PiMMUtil.selectFunction(initProtoArray, allInitProtoArray, title, message, false);

          }
          if ((loopProto != null) || (initProto != null)) {
            this.hasDoneChanges = true;
            final CHeaderRefinement newRefinement = PiMMUserFactory.instance.createCHeaderRefinement();
            newRefinement.setLoopPrototype(loopProto);
            newRefinement.setInitPrototype(initProto);
            newRefinement.setFilePath(newFilePath);
            actor.setRefinement(newRefinement);
          }
        }
      } else {
        // The file is either a .pi or a .IDL file.
        validRefinement = true;
        final PiSDFRefinement createPiSDFRefinement = PiMMUserFactory.instance.createPiSDFRefinement();
        createPiSDFRefinement.setFilePath(newFilePath);
        actor.setRefinement(createPiSDFRefinement);
        this.hasDoneChanges = true;
      }
    } while (!validRefinement);
  }

  /**
   * Set whether all prototypes are to be listed, or only those matching actor ports, when setting a refinement.
   *
   * @param newValue
   *          the new show only valid prototypes
   */
  public void setShowOnlyValidPrototypes(final boolean newValue) {
    this.showOnlyValidPrototypes = newValue;
  }

  /**
   * Gets the prototypes.
   *
   * @param file
   *          the file
   * @param actor
   *          the actor
   * @param prototypeFilter
   *          the prototype filter
   * @return the prototypes
   */
  private List<FunctionPrototype> getPrototypes(final IFile file, final Actor actor,
      final PrototypeFilter prototypeFilter) {

    List<FunctionPrototype> result = null;

    if (file != null) {
      result = HeaderParser.parseHeader(file);

      switch (prototypeFilter) {
        case INIT_ACTOR:
          result = HeaderParser.filterInitPrototypesFor(actor, result);
          break;
        case LOOP_ACTOR:
          result = HeaderParser.filterLoopPrototypesFor(actor, result);
          break;
        case INIT:
          result = HeaderParser.filterInitPrototypes(result);
          break;
        case NONE:
          break;
        default:
      }
    }
    if (result == null) {
      throw new PreesmException();
    }
    return result;
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
