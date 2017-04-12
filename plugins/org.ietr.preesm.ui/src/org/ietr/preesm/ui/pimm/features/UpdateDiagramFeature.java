/*******************************************************************************
 * Copyright or © or Copr. 2015 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
package org.ietr.preesm.ui.pimm.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.DefaultUpdateDiagramFeature;
import org.eclipse.graphiti.mm.Property;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.ExecutableActor;

// TODO: Auto-generated Javadoc
/**
 * This feature try to detect cases when a Diagram or a Network need to be updated according to data contained from other one. It is executed each time a
 * Diagram is opened, thanks to the result of isAutoUpdateAtStartup().
 *
 * <p>This feature only apply on a Diagram. For updates on sub-shapes (instances, ports, etc.) please see updates method in the corresponding patterns.</p>
 *
 * @author Antoine Lorence
 * @see OrccDiagramTypeProvider#isAutoUpdateAtStartup() Code adapted from ORCC (net.sf.orcc.xdf.ui.features, https://github.com/orcc/orcc)
 */
public class UpdateDiagramFeature extends DefaultUpdateDiagramFeature {

  /** The global version key. */
  // Name of the property of the diagram giving its version
  private static String GLOBAL_VERSION_KEY = "editor_version";
  /*
   * Versions number of the diagram editor
   */
  /** The version 1. */
  // First version
  private static int VERSION_1 = 1;

  /** The version 2. */
  // Second version: anchors added on actors
  private static int VERSION_2 = 2;

  /** The version 3. */
  // Third version: anchors added special actors
  private static int VERSION_3 = 3;

  /** The current editor version. */
  // Current version
  private static int CURRENT_EDITOR_VERSION = UpdateDiagramFeature.VERSION_3;

  /** The has done changes. */
  private boolean hasDoneChanges;

  /**
   * Instantiates a new update diagram feature.
   *
   * @param fp
   *          the fp
   */
  public UpdateDiagramFeature(final IFeatureProvider fp) {
    super(fp);
    this.hasDoneChanges = false;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.impl.DefaultUpdateDiagramFeature#hasDoneChanges()
   */
  @Override
  public boolean hasDoneChanges() {
    return this.hasDoneChanges;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.impl.DefaultUpdateDiagramFeature#update(org.eclipse.graphiti.features.context.IUpdateContext)
   */
  @Override
  public boolean update(final IUpdateContext context) {
    if (!(context.getPictogramElement() instanceof Diagram)) {
      throw new RuntimeException("UpdateDiagramFeature has been used with a non Diagram parameter: " + context.getPictogramElement().getClass().toString());
    }

    final Diagram diagram = (Diagram) context.getPictogramElement();

    updateVersion(diagram);

    return this.hasDoneChanges;
  }

  /**
   * Check if this diagram is outdated and update it according to the version number stored in its properties.
   *
   * @param diagram
   *          the diagram
   */
  private void updateVersion(final Diagram diagram) {
    final Property property = Graphiti.getPeService().getProperty(diagram, UpdateDiagramFeature.GLOBAL_VERSION_KEY);
    int version;
    // If the diagram has no version property, it is anterior to the
    // creation of the UpdateDiagramFeature and is thus version 1
    if ((property == null) || (property.getValue() == null)) {
      version = 1;
    } else {
      version = Integer.parseInt(property.getValue());
    }

    // The diagram is up-to-date, nothing to do
    if (UpdateDiagramFeature.CURRENT_EDITOR_VERSION == version) {
      return;
    }

    // The diagram is not up-to-date, some changes will appear
    this.hasDoneChanges = true;

    if (version < UpdateDiagramFeature.CURRENT_EDITOR_VERSION) {
      updateToCurrentVersion(diagram, version);
    }

    // Set the version to current
    if (property == null) {
      Graphiti.getPeService().setPropertyValue(diagram, UpdateDiagramFeature.GLOBAL_VERSION_KEY, String.valueOf(UpdateDiagramFeature.CURRENT_EDITOR_VERSION));
    } else {
      property.setValue(String.valueOf(UpdateDiagramFeature.CURRENT_EDITOR_VERSION));
    }
  }

  /**
   * Update incrementally a Diagram from its version number to the current one.
   *
   * @param diagram
   *          the Diagram to update
   * @param version
   *          the current version of diagram
   */
  private void updateToCurrentVersion(final Diagram diagram, int version) {
    if (version == UpdateDiagramFeature.VERSION_1) {
      updateFromVersion1(diagram);
      version = UpdateDiagramFeature.VERSION_2;
    }
    if (version == UpdateDiagramFeature.VERSION_2) {
      updateFromVersion2(diagram);
      version = UpdateDiagramFeature.VERSION_3;
    }
  }

  /**
   * Update from version 1.
   *
   * @param diagram
   *          the diagram
   */
  private void updateFromVersion1(final Diagram diagram) {
    // Update Shapes of Actors to add an anchor, allowing to start a
    // connection by clicking on an Actor rather than a Port
    for (final Shape s : diagram.getChildren()) {
      if (s instanceof ContainerShape) {
        final Object o = getBusinessObjectForPictogramElement(s);
        if (o instanceof Actor) {
          final Actor actor = (Actor) o;
          final ChopboxAnchor cba = Graphiti.getPeCreateService().createChopboxAnchor(s);
          link(cba, actor);
        }
      }
    }
  }

  /**
   * Update from version 2.
   *
   * @param diagram
   *          the diagram
   */
  private void updateFromVersion2(final Diagram diagram) {
    // Update Shapes of Actors to add an anchor, allowing to start a
    // connection by clicking on an Actor rather than a Port
    for (final Shape s : diagram.getChildren()) {
      if (s instanceof ContainerShape) {
        final Object o = getBusinessObjectForPictogramElement(s);
        if ((o instanceof ExecutableActor) && !(o instanceof Actor)) {
          final ExecutableActor actor = (ExecutableActor) o;
          final ChopboxAnchor cba = Graphiti.getPeCreateService().createChopboxAnchor(s);
          link(cba, actor);
        }
      }
    }
  }
}
