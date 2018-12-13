/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
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
package org.preesm.algorithm;

import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.preesm.algorithm.mathematicalModels.PeriodicScheduleModelOjAlgo;
import org.preesm.algorithm.mathematicalModels.SolverMethod;
import org.preesm.algorithm.schedule.PeriodicSchedulerSDF.Method;

/**
 *
 */
public class PreesmAlgorithmPlugin extends AbstractUIPlugin {

  /** The Constant PLUGIN_ID. */
  private static final String PLUGIN_ID = "org.preesm.algorithm";

  public final Map<Method, SolverMethod> solverMethodRegistry = new LinkedHashMap<>();

  private static PreesmAlgorithmPlugin instance = null;

  /**
   *
   */
  public static final PreesmAlgorithmPlugin getInstance() {
    if (instance == null) {
      // special case when calling as plain java (outside eclipse framework)
      instance = new PreesmAlgorithmPlugin();
    }
    return instance;
  }

  public PreesmAlgorithmPlugin() {
    super();
  }

  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
    instance = this;
    solverMethodRegistry.put(Method.LINEAR_PROGRAMMING_OJALGO, new PeriodicScheduleModelOjAlgo());
  }

  @Override
  public void stop(final BundleContext context) throws Exception {
    super.stop(context);
    instance = null;
    solverMethodRegistry.clear();
  }

  /**
   * Returns an image for the image file at the given plug-in relative path.
   *
   * @param path
   *          the path
   * @return the image
   */
  public Image getImage(final String path) {
    final ImageRegistry ir = getImageRegistry();
    Image image = ir.get(path);
    if (image == null) {
      final ImageDescriptor id = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
      image = id.createImage();
      ir.put(path, image);
    }

    return image;
  }

  /**
   *
   */
  public ImageDescriptor getImageDescriptor(final String path) {
    final ImageRegistry ir = getImageRegistry();
    Image image = ir.get(path);
    ImageDescriptor id = null;
    if (image == null) {
      id = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
      image = id.createImage();
      ir.put(path, image);
    }

    return id;
  }
}
