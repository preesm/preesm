/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
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
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.Configurable;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.ISetter;

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
  protected AbstractSetVisibleDependencyFeature(final IFeatureProvider fp, final boolean visible) {
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
  protected void setVisibleIngoingDependencies(final Configurable param) {
    for (final ConfigInputPort p : param.getConfigInputPorts()) {
      setVisible(p.getIncomingDependency());
    }
  }
}
