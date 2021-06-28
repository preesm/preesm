/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019 - 2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2013 - 2020)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2012 - 2015)
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

import java.util.ArrayList;
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
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.PersistenceLevel;
import org.preesm.model.pisdf.PiSDFRefinement;
import org.preesm.model.pisdf.RefinementContainer;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.header.parser.HeaderParser;
import org.preesm.ui.pisdf.util.PiMMUtil;
import org.preesm.ui.utils.FileUtils;

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
    /** The init delay actor. */
    INIT_DELAY_ACTOR,
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
      if (bo instanceof Actor || bo instanceof InitActor
          || (bo instanceof Delay && ((Delay) bo).getLevel() == PersistenceLevel.PERMANENT)) {
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
      if (bo instanceof Actor || bo instanceof Delay || bo instanceof InitActor) {
        RefinementContainer rc = null;
        boolean acceptPiFiles = false;
        if (bo instanceof Delay) {
          rc = ((Delay) bo).getActor();
          if (rc == null) {
            return;
          }
        } else if (bo instanceof Actor) {
          rc = (Actor) bo;
          acceptPiFiles = true;
        } else if (bo instanceof InitActor) {
          rc = (InitActor) bo;
        }

        final String question = "Please select a valid refinement file (.h, or .pi if Actor)";
        final String dialogTitle = "Select a refinement file";
        final IPath path = askRefinement(question, dialogTitle, acceptPiFiles);
        if (path != null) {
          setActorRefinement(rc, path);
        }

        // Call the layout feature
        layoutPictogramElement(pes[0]);
      }
    }
  }

  /**
   * Ask refinement.
   *
   * @param question
   *          the question
   * @param dialogTitle
   *          the dialog title
   * @param acceptPiFiles
   *          Whether or not pi files are accepted as refinements.
   * @return the i path
   */
  private IPath askRefinement(final String question, final String dialogTitle, final boolean acceptPiFiles) {
    // Ask user for Actor name until a valid name is entered.
    // For now, authorized refinements are other PiGraphs (.pi files) and
    // .idl prototypes
    final Set<String> fileExtensions = new LinkedHashSet<>();
    if (acceptPiFiles) {
      fileExtensions.add("pi");
    }
    fileExtensions.add("h");
    return FileUtils.browseFiles(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), dialogTitle, question,
        fileExtensions);
  }

  /**
   * Set the refinement file of the actor and ask for the prototype in case the file is a C Header.
   *
   * @param actor
   *          the actor
   * @param newFilePath
   *          The {@link IPath} to the file. (Must not be null)
   */
  protected void setActorRefinement(RefinementContainer actor, IPath newFilePath) {
    final String dialogTitle = "Select a refinement file";

    if (!(actor instanceof DelayActor) && !(actor instanceof Actor) && !(actor instanceof InitActor)) {
      return;
    }

    boolean acceptPiFiles = actor instanceof Actor;
    boolean validRefinement = false;
    do {
      // If the file is a .h header
      if (newFilePath.getFileExtension().equals("h")) {

        List<FunctionPrototype> loopPrototypes;
        FunctionPrototype[] allProtoArray;
        IFile file;

        // We get it
        file = ResourcesPlugin.getWorkspace().getRoot().getFile(newFilePath);
        if (file == null) {
          throw new PreesmRuntimeException("Unable to open file " + newFilePath.toString());
        }

        // Get all prototypes first (no filter)
        final List<FunctionPrototype> allPrototypes = HeaderParser.parseCXXHeader(file);
        allProtoArray = allPrototypes.toArray(new FunctionPrototype[allPrototypes.size()]);

        validRefinement = !allPrototypes.isEmpty();
        if (!validRefinement) {
          final String message = "The .h file you selected does not contain any prototype."
              + ".\nPlease select another valid file.";
          newFilePath = askRefinement(message, dialogTitle, acceptPiFiles);

          // If the cancel button of the dialog box was clicked
          // stop the setRefinement process.
          if (newFilePath == null) {
            return;
          }
        } else {
          String title = "";
          String message = "";
          FunctionPrototype loopProto = null;

          if (actor instanceof Actor) {
            // The file is a valid .h file.
            title = "Loop Function Selection";
            message = "Select a loop function for actor " + ((AbstractActor) actor).getName()
                + "\n(* = any string, ? = any char):";

            loopPrototypes = getPrototypes(actor, PrototypeFilter.LOOP_ACTOR, allPrototypes);
            final FunctionPrototype[] loopProtoArray = loopPrototypes
                .toArray(new FunctionPrototype[loopPrototypes.size()]);
            loopProto = PiMMUtil.selectFunction(loopProtoArray, allProtoArray, title, message,
                this.showOnlyValidPrototypes);
          }

          List<FunctionPrototype> initPrototypes = null;
          List<FunctionPrototype> allInitPrototypes = null;
          boolean showOnlyCorresponding = false;
          if (actor instanceof Actor) {
            initPrototypes = getPrototypes(actor, PrototypeFilter.INIT_ACTOR, allPrototypes);
            allInitPrototypes = getPrototypes(actor, PrototypeFilter.INIT, allPrototypes);
          } else {
            initPrototypes = getPrototypes(actor, PrototypeFilter.INIT_DELAY_ACTOR, allPrototypes);
            allInitPrototypes = new ArrayList<>();
            showOnlyCorresponding = true;
          }
          title = "Init Function Selection";
          message = "Select an optionnal init function for actor " + ((AbstractActor) actor).getName()
              + ", or click Cancel to set none.\nNote: prototypes with pointers or arrays as "
              + "arguments are filtered out.\n(* = any string, ? = any char):";
          final FunctionPrototype[] initProtoArray = initPrototypes
              .toArray(new FunctionPrototype[initPrototypes.size()]);
          final FunctionPrototype[] allInitProtoArray = allInitPrototypes
              .toArray(new FunctionPrototype[allInitPrototypes.size()]);
          final FunctionPrototype initProto = PiMMUtil.selectFunction(initProtoArray, allInitProtoArray, title, message,
              showOnlyCorresponding);

          if ((loopProto != null) || (initProto != null)) {
            this.hasDoneChanges = true;
            final CHeaderRefinement newRefinement = PiMMUserFactory.instance.createCHeaderRefinement();
            newRefinement.setLoopPrototype(loopProto);
            newRefinement.setInitPrototype(initProto);
            newRefinement.setFilePath(newFilePath.toString());
            actor.setRefinement(newRefinement);
          }
        }
      } else {
        // The file is either a .pi or a .IDL file, it is not accepted for DelayActor
        validRefinement = true;
        final PiSDFRefinement createPiSDFRefinement = PiMMUserFactory.instance.createPiSDFRefinement();
        createPiSDFRefinement.setFilePath(newFilePath.toString());
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
   * @param actor
   *          the actor
   * @param prototypeFilter
   *          the prototype filter
   * @param allPrototypes
   *          All the prototypes found in the file
   * @return the prototypes
   */
  private List<FunctionPrototype> getPrototypes(final RefinementContainer actor, final PrototypeFilter prototypeFilter,
      final List<FunctionPrototype> allPrototypes) {

    List<FunctionPrototype> result = null;

    switch (prototypeFilter) {
      case INIT_ACTOR:
        result = HeaderParser.filterInitPrototypesFor((AbstractActor) actor, allPrototypes);
        break;
      case LOOP_ACTOR:
        result = HeaderParser.filterLoopPrototypesFor((AbstractActor) actor, allPrototypes);
        break;
      case INIT:
        result = HeaderParser.filterInitPrototypes(allPrototypes);
        break;
      case INIT_DELAY_ACTOR:
        result = HeaderParser.filterInitBufferPrototypes(allPrototypes);
        break;
      case NONE:
        break;
      default:
    }

    if (result == null) {
      throw new PreesmRuntimeException();
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
