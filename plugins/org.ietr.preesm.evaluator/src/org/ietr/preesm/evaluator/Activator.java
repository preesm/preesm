/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * blaunay <bapt.launay@gmail.com> (2015)
 * Hamza Deroui <hamza.deroui@insa-rennes.fr> (2017 - 2018)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012)
 * Yaset Oliva <yaset.oliva@insa-rennes.fr> (2013)
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
package org.ietr.preesm.evaluator;

import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.runtime.Plugin;
import org.ietr.preesm.mathematicalModels.PeriodicScheduleModel_ojAlgo;
import org.ietr.preesm.mathematicalModels.SolverMethod;
import org.ietr.preesm.schedule.PeriodicScheduler_SDF.Method;
import org.osgi.framework.BundleContext;

// TODO: Auto-generated Javadoc
/**
 * The Class Activator.
 */
public class Activator extends Plugin {

  /** The context. */
  private static BundleContext context;

  public static final Map<Method, SolverMethod> solverMethodRegistry = new LinkedHashMap<>();

  /**
   * Gets the context.
   *
   * @return the context
   */
  static BundleContext getContext() {
    return Activator.context;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  @Override
  public void start(final BundleContext bundleContext) throws Exception {
    Activator.context = bundleContext;
    Activator.solverMethodRegistry.put(Method.LinearProgram_ojAlgo, new PeriodicScheduleModel_ojAlgo());
    super.start(bundleContext);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  @Override
  public void stop(final BundleContext bundleContext) throws Exception {
    super.stop(bundleContext);
    Activator.context = null;

    Activator.solverMethodRegistry.remove(Method.LinearProgram_ojAlgo, new PeriodicScheduleModel_ojAlgo());
  }

}
