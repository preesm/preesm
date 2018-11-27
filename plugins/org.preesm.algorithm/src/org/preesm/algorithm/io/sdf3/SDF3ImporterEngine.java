/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
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
package org.preesm.algorithm.io.sdf3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.logging.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.preesm.algorithm.model.sdf.SDFAbstractVertex;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.model.scenario.PreesmScenario;
import org.preesm.model.scenario.Timing;
import org.preesm.model.scenario.types.DataType;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;

// TODO: Auto-generated Javadoc
/**
 * The Class SDF3ImporterEngine.
 */
public class SDF3ImporterEngine {

  /** The sdf 3 parser. */
  private final Sdf3XmlParser sdf3Parser;

  /**
   * Instantiates a new SDF 3 importer engine.
   */
  public SDF3ImporterEngine() {
    this.sdf3Parser = new Sdf3XmlParser();
  }

  /**
   * Import from.
   *
   * @param path
   *          the path
   * @param scenario
   *          the scenario
   * @param architecture
   *          the architecture
   * @param logger
   *          the logger
   * @return the SDF graph
   * @throws PreesmException
   *           the workflow exception
   */
  public SDFGraph importFrom(final IPath path, final PreesmScenario scenario, final Design architecture,
      final Logger logger) throws PreesmException {
    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
    final IFile iFile = workspace.getRoot().getFile(path);

    if (!iFile.exists()) {
      final String message = "The parsed xml file does not exists: " + path.toOSString();
      throw new PreesmException(message);
    }

    final File file = new File(iFile.getRawLocation().toOSString());
    InputStream iStream = null;
    try {
      iStream = new FileInputStream(file);
    } catch (final FileNotFoundException e) {
      e.printStackTrace();
    }

    // Parse the input SDF3 graph
    SDFGraph graph = null;
    try {
      graph = (this.sdf3Parser.parse(iStream));
    } catch (final RuntimeException e) {
      final String msg = "SDF3 Parser Error: " + e.getMessage();
      throw new PreesmException(msg, e);
    }

    if (graph != null) {
      updateScenario(scenario, architecture);
    }

    return graph;
  }

  /**
   * Update scenario.
   *
   * @param graph
   *          the graph
   * @param scenario
   *          the scenario
   * @param architecture
   *          the architecture
   */
  private void updateScenario(final PreesmScenario scenario, final Design architecture) {
    // Update the input scenario so that all task can be scheduled
    // on all operators, and all have the same runtime.
    // For each operator of the architecture
    for (final ComponentInstance component : architecture.getComponentInstances()) {
      // for each actor of the graph
      for (final Entry<SDFAbstractVertex, Integer> entry : this.sdf3Parser.getActorExecTimes().entrySet()) {
        // Add the operator to the available operator for the
        // current actor
        entry.getKey().setInfo(entry.getKey().getName());
        // Set the timing of the actor
        final Timing t = scenario.getTimingManager().addTiming(entry.getKey().getName(),
            component.getComponent().getVlnv().getName());
        t.setTime(entry.getValue());
      }
    }
    // Add the data types of the SDF3 graph to the scenario
    for (final Entry<String, Integer> entry : this.sdf3Parser.getDataTypes().entrySet()) {
      final DataType type = new DataType(entry.getKey(), entry.getValue());
      scenario.getSimulationManager().putDataType(type);
    }
  }

}
