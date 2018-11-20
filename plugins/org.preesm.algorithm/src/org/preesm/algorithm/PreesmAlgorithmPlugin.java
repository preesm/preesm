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
  public static final String PLUGIN_ID = "org.preesm.algorithm";

  public final Map<Method, SolverMethod> solverMethodRegistry = new LinkedHashMap<>();

  private static PreesmAlgorithmPlugin instance = null;

  public static final PreesmAlgorithmPlugin getInstance() {
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
