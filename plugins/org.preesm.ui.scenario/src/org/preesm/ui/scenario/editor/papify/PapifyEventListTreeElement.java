/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Daniel Madroñal <daniel.madronal@upm.es> (2018)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2011)
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
package org.preesm.ui.scenario.editor.papify;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.preesm.model.pisdf.AbstractActor;

// TODO: Auto-generated Javadoc
/**
 * Provides the elements contained in the papify editor.
 *
 * @author dmadronal
 */

/**
 *
 * @author anmorvan
 *
 */

class PapifyEventListTreeElement {
  Object                       algorithmElement;
  String                       label;
  String                       actorPath;
  Map<String, PAPIEventStatus> PAPIStatuses;

  /** The image ok. */
  private final Image imageOk;

  /** The image error. */
  private final Image imageError;

  /**
   *
   * @author dmadronal
   *
   */
  enum PAPIEventStatus {
    // PAPI component supported
    YES,

    // PAPI component not supported
    NO;

    PAPIEventStatus next() {
      switch (this) {
        case YES:

          return NO;
        case NO:
          return YES;
        default:
          return null;
      }
    }
  }

  PapifyEventListTreeElement(final Object algorithmElement) {
    this.algorithmElement = algorithmElement;
    if (algorithmElement instanceof AbstractActor) {
      this.label = ((AbstractActor) algorithmElement).getName();
      this.actorPath = ((AbstractActor) algorithmElement).getVertexPath();
    }
    this.PAPIStatuses = new LinkedHashMap<>();

    final Bundle bundle = FrameworkUtil.getBundle(this.getClass());

    URL url = FileLocator.find(bundle, new Path("icons/error.png"), null);
    ImageDescriptor imageDcr = ImageDescriptor.createFromURL(url);
    this.imageError = imageDcr.createImage();

    url = FileLocator.find(bundle, new Path("icons/ok.png"), null);
    imageDcr = ImageDescriptor.createFromURL(url);
    this.imageOk = imageDcr.createImage();
  }

  public Image getImage(String name) {
    if (this.PAPIStatuses.get(name).equals(PAPIEventStatus.YES)) {
      return this.imageOk;
    } else {
      return this.imageError;
    }
  }

  public Object getAlgorithmElement() {
    return this.algorithmElement;
  }

  @Override
  public String toString() {
    return this.algorithmElement.toString().concat(label).concat(this.PAPIStatuses.toString());
  }
}
