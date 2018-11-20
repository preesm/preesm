/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013)
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
package org.preesm.ui.pisdf.diagram;

import org.eclipse.graphiti.ui.platform.AbstractImageProvider;

// TODO: Auto-generated Javadoc
/**
 * Class used to provide icons used in the PiMM Diagram Editor.
 *
 * @author kdesnos
 */
public class PiMMImageProvider extends AbstractImageProvider {

  /** The Constant PREFIX. */
  // The prefix for all identifiers of this image provider
  protected static final String PREFIX = "org.ietr.preesm.experiment.ui.pimm.";

  /** The Constant IMG_WHITE_DOT_BLUE_LINE. */
  // The image identifier for white dot.
  public static final String IMG_WHITE_DOT_BLUE_LINE = PiMMImageProvider.PREFIX + "whitedotblueline";

  /** The Constant IMG_WHITE_DOT_GREY_LINE. */
  public static final String IMG_WHITE_DOT_GREY_LINE = PiMMImageProvider.PREFIX + "whitedotgreyline";

  /** The Constant IMG_PI. */
  public static final String IMG_PI = PiMMImageProvider.PREFIX + "pi";

  /**
   * Default constructor of {@link PiMMImageProvider}.
   */
  public PiMMImageProvider() {
    super();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.ui.platform.AbstractImageProvider#addAvailableImages()
   */
  @Override
  protected void addAvailableImages() {
    // register the path for each image identifier
    addImageFilePath(PiMMImageProvider.IMG_PI, "icons/pi.gif");
    addImageFilePath(PiMMImageProvider.IMG_WHITE_DOT_BLUE_LINE, "icons/whitedotblueline.gif");
    addImageFilePath(PiMMImageProvider.IMG_WHITE_DOT_GREY_LINE, "icons/whitedotgreyline.gif");
  }

}
