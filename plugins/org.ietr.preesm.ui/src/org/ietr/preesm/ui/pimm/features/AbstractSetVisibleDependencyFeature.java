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
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.Parameterizable;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractSetVisibleDependencyFeature.
 */
public abstract class AbstractSetVisibleDependencyFeature extends AbstractCustomFeature {

  /** The fp. */
  protected IFeatureProvider fp;

  /** The visible. */
  protected boolean visible;

  /**
   * Instantiates a new abstract set visible dependency feature.
   *
   * @param fp
   *          the fp
   * @param visible
   *          the visible
   */
  public AbstractSetVisibleDependencyFeature(final IFeatureProvider fp, final boolean visible) {
    super(fp);
    this.fp = fp;
    this.visible = visible;
  }

  /**
   * Sets the visible.
   *
   * @param d
   *          the new visible
   */
  protected void setVisible(final Dependency d) {
    final PictogramElement[] depPes = this.fp.getAllPictogramElementsForBusinessObject(d);
    if (depPes != null) {
      for (final PictogramElement pe : depPes) {
        if (pe.isVisible() != this.visible) {
          pe.setVisible(this.visible);
        }
      }
    }
  }

  /**
   * Sets the visible outgoing dependencies.
   *
   * @param setter
   *          the new visible outgoing dependencies
   */
  protected void setVisibleOutgoingDependencies(final ISetter setter) {
    for (final Dependency d : setter.getOutgoingDependencies()) {
      setVisible(d);
    }
  }

  /**
   * Sets the visible ingoing dependencies.
   *
   * @param param
   *          the new visible ingoing dependencies
   */
  protected void setVisibleIngoingDependencies(final Parameterizable param) {
    for (final ConfigInputPort p : param.getConfigInputPorts()) {
      setVisible(p.getIncomingDependency());
    }
  }
}
