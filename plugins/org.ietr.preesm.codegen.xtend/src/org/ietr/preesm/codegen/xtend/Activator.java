/*******************************************************************************
 * Copyright or © or Copr. 2013 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013)
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
package org.ietr.preesm.codegen.xtend;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

// TODO: Auto-generated Javadoc
/**
 * The Class Activator.
 */
public class Activator extends Plugin {

  /** The Constant PLUGIN_ID. */
  // The plug-in ID
  public static final String PLUGIN_ID = "org.ietr.preesm.codegen.xtend"; //$NON-NLS-1$

  /** The plugin. */
  // The shared instance
  private static Activator plugin;

  /**
   * The constructor.
   */
  public Activator() {
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
   */
  @Override
  public void start(final BundleContext context) throws Exception {
    super.start(context);
    Activator.plugin = this;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
   */
  @Override
  public void stop(final BundleContext context) throws Exception {
    Activator.plugin = null;
    super.stop(context);
  }

  /**
   * Returns the shared instance.
   *
   * @return the shared instance
   */
  public static Activator getDefault() {
    return Activator.plugin;
  }
}
