/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
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
package org.ietr.preesm.ui.pimm.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ietr.preesm.ui.pimm.util.PiMMUtil;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.util.PortNameValidator;

// TODO: Auto-generated Javadoc
/**
 * Custom feature to rename a port.
 *
 * @author kdesnos
 *
 */
public class RenameActorPortFeature extends AbstractCustomFeature {

  /** The Constant HINT. */
  public static final String HINT = "rename";

  /** The has done changes. */
  protected boolean hasDoneChanges = false;

  /**
   * Default Constructor.
   *
   * @param fp
   *          the feature provider
   */
  public RenameActorPortFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
   */
  @Override
  public String getName() {
    return "Rename Port\tF2";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getDescription()
   */
  @Override
  public String getDescription() {
    return "Change the name of the Port";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#canExecute(org.eclipse.graphiti.features.context.
   * ICustomContext)
   */
  @Override
  public boolean canExecute(final ICustomContext context) {
    // allow rename if exactly one pictogram element
    // representing a Port is selected
    boolean ret = false;
    final PictogramElement[] pes = context.getPictogramElements();
    if ((pes != null) && (pes.length == 1)) {
      final Object bo = getBusinessObjectForPictogramElement(pes[0]);
      if (bo instanceof Port) {
        if (((Port) bo).eContainer() instanceof ExecutableActor) {
          ret = true;
        }
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
      if (bo instanceof Port) {
        final Port port = (Port) bo;
        final AbstractActor vertex = (AbstractActor) port.eContainer();
        final String currentName = port.getName();

        // Ask user for Port name until a valid name is entered.
        final String question = "Enter new port name";
        String newPortName = port.getName();

        newPortName = PiMMUtil.askString(getName(), question, newPortName, new PortNameValidator(vertex, port));

        if ((newPortName != null) && !newPortName.equals(currentName)) {
          this.hasDoneChanges = true;
          port.setName(newPortName);

          // Layout the Port
          layoutPictogramElement(pes[0]);
          updatePictogramElement(pes[0]);

          // Layout the actor
          final GraphicsAlgorithm bra = ((BoxRelativeAnchor) pes[0]).getReferencedGraphicsAlgorithm();
          layoutPictogramElement(bra.getPictogramElement());
          updatePictogramElement(bra.getPictogramElement());

          getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().refresh();
        }
      }
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
