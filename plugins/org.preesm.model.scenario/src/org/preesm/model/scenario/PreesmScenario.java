/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2019)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Daniel Madroñal <daniel.madronal@upm.es> (2018)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2012)
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
package org.preesm.model.scenario;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.serialize.PiParser;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.serialize.SlamParser;

/**
 * Storing all information of a scenario.
 *
 * @author mpelcat
 */
public class PreesmScenario {

  private String scenarioURL = "";

  private PiGraph algorithm;
  private Design  design;

  /** Manager of constraint groups. */
  private ConstraintGroupManager constraintgroupmanager = null;

  /** Manager of timings. */
  private TimingManager timingmanager = null;

  /** Manager of simulation parameters. */
  private SimulationManager simulationManager = null;

  /** Manager of code generation parameters. */
  private String codegenDirectory;

  /** Manager of parameters values for PiGraphs. */
  private ParameterValueManager parameterValueManager = null;

  /** Manager of PapifyConfig groups. */
  private PapifyConfigManager papifyconfiggroupmanager = null;

  /**
   * Instantiates a new preesm scenario.
   *
   */
  public PreesmScenario() {
    this.constraintgroupmanager = new ConstraintGroupManager(this);
    this.timingmanager = new TimingManager(this);
    this.simulationManager = new SimulationManager(this);
    this.parameterValueManager = new ParameterValueManager(this);
    this.papifyconfiggroupmanager = new PapifyConfigManager(this);
  }

  /**
   * Checks if is IBSDF scenario.
   *
   * @return true, if is IBSDF scenario
   */
  public boolean isProperlySet() {
    final boolean hasProperAlgo = this.getAlgorithm() != null;
    final boolean hasProperArchi = this.getDesign() != null;
    return hasProperAlgo && hasProperArchi;
  }

  public PiGraph getAlgorithm() {
    return this.algorithm;
  }

  public Design getDesign() {
    return this.design;
  }

  /**
   * Util method generating a name for a given PreesmSceario from its architecture and algorithm.
   *
   * @return the scenario name
   */
  public String getScenarioName() {
    final IPath algoPath = new Path(this.getAlgorithm().getUrl()).removeFileExtension();
    final String algoName = algoPath.lastSegment();
    final IPath archiPath = new Path(this.getDesign().getUrl()).removeFileExtension();
    final String archiName = archiPath.lastSegment();
    return algoName + "_" + archiName;
  }

  /**
   * Gets the constraint group manager.
   *
   * @return the constraint group manager
   */
  public ConstraintGroupManager getConstraintGroupManager() {
    return this.constraintgroupmanager;
  }

  /**
   * Gets the timing manager.
   *
   * @return the timing manager
   */
  public TimingManager getTimingManager() {
    return this.timingmanager;
  }

  public void setAlgorithm(final PiGraph algorithm) {
    this.algorithm = algorithm;
  }

  /**
   * Sets the architecture URL.
   */
  public void setesign(final Design design) {
    this.design = design;
  }

  /**
   * Gets the simulation manager.
   *
   * @return the simulation manager
   */
  public SimulationManager getSimulationManager() {
    return this.simulationManager;
  }

  /**
   * Gets the codegen directory.
   *
   * @return the codegen directory
   */
  public String getCodegenDirectory() {
    return this.codegenDirectory;
  }

  /**
   */
  public void setCodegenDirectory(final String dir) {
    this.codegenDirectory = dir;
  }

  /**
   * Gets the scenario URL.
   *
   * @return the scenario URL
   */
  public String getScenarioURL() {
    return this.scenarioURL;
  }

  /**
   * Gets the PapifyConfig group manager.
   *
   * @return the PapifyConfig group manager
   */
  public PapifyConfigManager getPapifyConfigManager() {
    return this.papifyconfiggroupmanager;
  }

  /**
   * Sets the new PapifyConfig group manager.
   *
   */
  public void setPapifyConfigManager(final PapifyConfigManager manager) {
    this.papifyconfiggroupmanager = manager;
  }

  /**
   * Sets the scenario URL.
   *
   * @param scenarioURL
   *          the new scenario URL
   */
  public void setScenarioURL(final String scenarioURL) {
    this.scenarioURL = scenarioURL;
  }

  /**
   * Gets the parameter value manager.
   *
   * @return the parameter value manager
   */
  public ParameterValueManager getParameterValueManager() {
    return this.parameterValueManager;
  }

  /**
   * From PiScenario.
   *
   * @param algoPath
   *          the algorithm change
   * @param archiPath
   *          the architecture change
   * @throws InvalidModelException
   *           the invalid model exception
   * @throws CoreException
   *           the core exception
   */

  public void update(final String algoPath, final String archiPath) throws CoreException {
    // If the architecture changes, operator ids, operator defintion ids and
    // com node ids are no more valid (they are extracted from the
    // architecture)
    if (archiPath != null) {

      // Extract the root object from the resource.
      final Design design = SlamParser.parseSlamDesign(archiPath);
      this.design = design;
    }
    // If the algorithm changes, parameters or variables are no more valid
    // (they are set in the algorithm)
    if (algoPath != null) {
      final PiGraph newPiGraph = PiParser.getPiGraphWithReconnection(algoPath);
      this.algorithm = newPiGraph;
      this.parameterValueManager.updateWith(newPiGraph);
    }
    // If the algorithm or the architecture changes, timings and constraints
    // are no more valid (they depends on both algo and archi)
    if (algoPath != null || archiPath != null) {
      this.timingmanager.clear();
      this.constraintgroupmanager.update();
      this.papifyconfiggroupmanager.update();
    }
  }

}
