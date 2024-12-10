/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008 - 2011)
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
package org.ietr.dftools.graphiti.ui;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public class GraphitiUiPlugin extends AbstractUIPlugin {

  /**
   * The plug-in ID.
   */
  public static final String PLUGIN_ID = "org.ietr.graphiti.ui";

  /**
   * The shared instance.
   */
  private static GraphitiUiPlugin plugin;

  private static final synchronized void setInstance(final GraphitiUiPlugin instance) {
    plugin = instance;
  }

  /**
   * Returns the shared instance.
   *
   * @return the shared instance
   */
  public static GraphitiUiPlugin getDefault() {
    return GraphitiUiPlugin.plugin;
  }

  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
    setInstance(this);
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    super.stop(context);
    setInstance(null);
  }

  /**
   * Returns an image for the image file at the given plug-in relative path.
   *
   * @param path
   *          the path
   * @return the image
   */
  public static Image getImage(final String path) {
    final ImageRegistry ir = GraphitiUiPlugin.plugin.getImageRegistry();
    Image image = ir.get(path);
    if (image == null) {
      final ImageDescriptor id = AbstractUIPlugin.imageDescriptorFromPlugin(GraphitiUiPlugin.PLUGIN_ID, path);
      image = id.createImage();
      ir.put(path, image);
    }

    return image;
  }

  /**
   * Returns an image descriptor for the image file at the given plug-in relative path.
   *
   * @param path
   *          the path
   * @return the image descriptor
   */
  public static ImageDescriptor getImageDescriptor(final String path) {
    return AbstractUIPlugin.imageDescriptorFromPlugin(GraphitiUiPlugin.PLUGIN_ID, path);
  }

  /**
   * Returns an {@link IStatus} with the ERROR level.
   *
   * @param message
   *          A message associated with the status.
   * @return An {@link IStatus} with the ERROR level.
   */
  public IStatus getErrorStatus(final String message) {
    return getStatus(IStatus.ERROR, message);
  }

  /**
   * Gets the status.
   *
   * @param severity
   *          the severity
   * @param message
   *          the message
   * @return the status
   */
  private IStatus getStatus(final int severity, final String message) {
    final Bundle bundle = getBundle();
    final String pluginId = Long.toString(bundle.getBundleId());
    return new Status(severity, pluginId, message);
  }

  /**
   * Returns an {@link IStatus} with the WARNING level.
   *
   * @param message
   *          A message associated with the status.
   * @return An {@link IStatus} with the WARNING level.
   */
  public IStatus getWarningStatus(final String message) {
    return getStatus(IStatus.WARNING, message);
  }

  /**
   * Logs the given <code>message</code> with the INFO status.
   *
   * @param message
   *          A {@link String} to be logged.
   */
  public void logInfoStatus(final String message) {
    final ILog log = Platform.getLog(getBundle());
    log.log(getStatus(IStatus.INFO, message));
  }
}
