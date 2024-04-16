/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2013 - 2020)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2012 - 2013)
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

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.PiSDFRefinement;
import org.preesm.model.pisdf.Refinement;
import org.preesm.model.pisdf.RefinementContainer;

// TODO: Auto-generated Javadoc
/**
 * Custom Feature to clear the {@link PiSDFRefinement} of an {@link Actor}.
 *
 * @author jheulot
 */
public class ClearActorRefinementFeature extends AbstractCustomFeature {

  /** The has done changes. */
  protected boolean hasDoneChanges = false;

  /**
   * Default Constructor of {@link ClearActorRefinementFeature}.
   *
   * @param fp
   *          the feature provider
   */
  public ClearActorRefinementFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
   */
  @Override
  public String getName() {
    return "Clear Refinement";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getDescription()
   */
  @Override
  public String getDescription() {
    return "Clear the Refinement of an Actor";
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

      RefinementContainer rc = null;
      if (bo instanceof final Delay delay) {
        rc = delay.getActor();
      } else if (bo instanceof Actor || bo instanceof InitActor) {
        rc = (RefinementContainer) bo;
      }

      if (rc != null) {
        // Check if the actor has a valid refinement
        final Refinement refinement = rc.getRefinement();
        if (refinement != null && refinement.getFilePath() != null) {
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

      RefinementContainer rc = null;
      if (bo instanceof final Delay delay) {
        rc = delay.getActor();
      } else if (bo instanceof Actor || bo instanceof InitActor) {
        rc = (RefinementContainer) bo;
      }

      if (rc != null) {
        rc.setRefinement(null);

        // Call the layout feature
        layoutPictogramElement(pes[0]);
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
