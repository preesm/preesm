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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.serialize.PiParser;
import org.preesm.model.scenario.papi.PapifyConfigManager;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.serialize.SlamParser;
import org.preesm.model.slam.utils.DesignTools;

/**
 * Storing all information of a scenario.
 *
 * @author mpelcat
 */
public class PreesmScenario {

  private String scenarioURL = "";

  private PiGraph piGraph;
  private Design  slamDesign;

  /** Manager of constraint groups. */
  private ConstraintGroupManager constraintgroupmanager = null;

  /** Manager of relative constraints. */
  private RelativeConstraintManager relativeconstraintmanager = null;

  /** Manager of timings. */
  private TimingManager timingmanager = null;

  /** Manager of simulation parameters. */
  private SimulationManager simulationManager = null;

  /** Manager of code generation parameters. */
  private CodegenManager codegenManager = null;

  /** Manager of parameters values for PiGraphs. */
  private ParameterValueManager parameterValueManager = null;

  /** Manager of PapifyConfig groups. */
  private PapifyConfigManager papifyconfiggroupmanager = null;

  /**
   * Instantiates a new preesm scenario.
   *
   */
  public PreesmScenario() {
    this.constraintgroupmanager = new ConstraintGroupManager();
    this.relativeconstraintmanager = new RelativeConstraintManager();
    this.timingmanager = new TimingManager();
    this.simulationManager = new SimulationManager();
    this.codegenManager = new CodegenManager();
    this.parameterValueManager = new ParameterValueManager();
    this.papifyconfiggroupmanager = new PapifyConfigManager();
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
    return this.piGraph;
  }

  public Design getDesign() {
    return this.slamDesign;
  }

  /**
   * Util method generating a name for a given PreesmSceario from its architecture and algorithm.
   *
   * @return the scenario name
   */
  public String getScenarioName() {
    final IPath algoPath = new Path(this.getAlgorithmURL()).removeFileExtension();
    final String algoName = algoPath.lastSegment();
    final IPath archiPath = new Path(this.getArchitectureURL()).removeFileExtension();
    final String archiName = archiPath.lastSegment();
    return algoName + "_" + archiName;
  }

  /**
   * Gets the actor names.
   *
   * @return the actor names
   */
  public Set<String> getActorNames() {
    return getAlgorithm().getActors().stream().map(AbstractVertex::getName).collect(Collectors.toSet());
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
   * Gets the relativeconstraint manager.
   *
   * @return the relativeconstraint manager
   */
  public RelativeConstraintManager getRelativeconstraintManager() {
    return this.relativeconstraintmanager;
  }

  /**
   * Gets the timing manager.
   *
   * @return the timing manager
   */
  public TimingManager getTimingManager() {
    return this.timingmanager;
  }

  /**
   * Gets the algorithm URL.
   *
   * @return the algorithm URL
   */
  public String getAlgorithmURL() {
    if (getAlgorithm() != null) {
      return getAlgorithm().getUrl();
    } else {
      return null;
    }
  }

  public void setAlgorithm(final PiGraph algorithm) {
    this.piGraph = algorithm;
  }

  /**
   * Gets the architecture URL.
   *
   * @return the architecture URL
   */
  public String getArchitectureURL() {
    if (getDesign() != null) {
      return getDesign().getUrl();
    } else {
      return null;
    }
  }

  /**
   * Sets the architecture URL.
   */
  public void setArchitecture(final Design design) {
    this.slamDesign = design;
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
   * Gets the codegen manager.
   *
   * @return the codegen manager
   */
  public CodegenManager getCodegenManager() {
    return this.codegenManager;
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
   * Gets the operator ids.
   *
   * @return the operator ids
   */
  public Set<String> getOperatorIds() {
    return DesignTools.getOperatorInstanceIds(getDesign());
  }

  /**
   * Gets the ordered operator ids.
   *
   * @return the ordered operator ids
   */
  public List<String> getOrderedOperatorIds() {
    final List<String> opIdList = new ArrayList<>(getOperatorIds());
    Collections.sort(opIdList, (o1, o2) -> o1.compareTo(o2));

    return opIdList;
  }

  /**
   * Gets the operator definition ids.
   *
   * @return the operator definition ids
   */
  public Set<String> getOperatorDefinitionIds() {
    return DesignTools.getOperatorComponentIds(getDesign());
  }

  /**
   * Gets the com node ids.
   *
   * @return the com node ids
   */
  public Set<String> getComNodeIds() {
    return DesignTools.getComNodeInstanceIds(getDesign());
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
   * Sets the parameter value manager.
   *
   * @param parameterValueManager
   *          the new parameter value manager
   */
  public void setParameterValueManager(final ParameterValueManager parameterValueManager) {
    this.parameterValueManager = parameterValueManager;
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
      this.slamDesign = design;

      getOperatorIds().clear();
      getOperatorIds().addAll(DesignTools.getOperatorInstanceIds(design));

      getOperatorDefinitionIds().clear();
      getOperatorDefinitionIds().addAll(DesignTools.getOperatorComponentIds(design));

      getComNodeIds().clear();
      getComNodeIds().addAll(DesignTools.getComNodeInstanceIds(design));

    }
    // If the algorithm changes, parameters or variables are no more valid
    // (they are set in the algorithm)
    if (algoPath != null) {
      final PiGraph newPiGraph = PiParser.getPiGraphWithReconnection(algoPath);
      this.piGraph = newPiGraph;
      this.parameterValueManager.updateWith(newPiGraph);
    }
    // If the algorithm or the architecture changes, timings and constraints
    // are no more valid (they depends on both algo and archi)
    if (algoPath != null || archiPath != null) {
      this.timingmanager.getTimings().clear();
      this.constraintgroupmanager.update();
      this.papifyconfiggroupmanager.update();
    }
  }

}
