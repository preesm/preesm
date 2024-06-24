/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2013)
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
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;
import org.preesm.model.pisdf.EndActor;

/**
 *
 */
public class AddEndActorFeature extends AbstractAddConfigurableFeature {

  /** The Constant BROADCAST_ACTOR_TEXT_FOREGROUND. */
  public static final IColorConstant END_ACTOR_TEXT_FOREGROUND = IColorConstant.BLACK;

  /** The Constant BROADCAST_ACTOR_FOREGROUND. */
  public static final IColorConstant END_ACTOR_FOREGROUND = new ColorConstant(100, 100, 100); // Grey
  // 98, 131, 167); // Blue

  /** The Constant BROADCAST_ACTOR_BACKGROUND. */
  public static final IColorConstant END_ACTOR_BACKGROUND = new ColorConstant(230, 185, 184);

  private static final int DEFAULT_WIDTH = 70;

  private static final int DEFAULT_HEIGHT = 50;

  /**
   * Instantiates a new adds the broadcast actor feature.
   *
   * @param fp
   *          the fp
   */
  public AddEndActorFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.IAdd#canAdd(org.eclipse.graphiti.features.context.IAddContext)
   */
  @Override
  public boolean canAdd(final IAddContext context) {
    // Check that the user wants to add an Actor to the Diagram
    return (context.getNewObject() instanceof EndActor) && (context.getTargetContainer() instanceof Diagram);
  }

  @Override
  int getDefaultWidth() {
    return DEFAULT_WIDTH;
  }

  @Override
  int getDefaultHeight() {
    return DEFAULT_HEIGHT;
  }

  @Override
  IColorConstant getForegroundColor() {
    return END_ACTOR_FOREGROUND;
  }

  @Override
  IColorConstant getBackgroundColor() {
    return END_ACTOR_BACKGROUND;
  }

  @Override
  IColorConstant getTextForegroundColor() {
    return END_ACTOR_TEXT_FOREGROUND;
  }

}
