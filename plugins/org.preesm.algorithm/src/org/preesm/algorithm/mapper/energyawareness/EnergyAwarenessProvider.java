/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
 * Daniel Madroñal [daniel.madronal@upm.es] (2019)
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
package org.preesm.algorithm.mapper.energyawareness;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.eclipse.emf.common.util.EList;
import org.preesm.algorithm.mapper.abc.impl.latency.LatencyAbc;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.model.dag.DAGVertex;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.impl.ActorImpl;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.util.ScenarioUserFactory;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;

/**
 * 
 * @author dmadronal
 *
 */
public class EnergyAwarenessProvider {

  /** The original scenario **/
  private Scenario scenarioOriginal;

  /** The mapping scenario **/
  private Scenario scenarioMapping;

  /** The solution if the FPS objective is not reached **/
  private Map<String, Object> mappingFPS = new LinkedHashMap<>();

  /** The solution if the FPS objective is reached **/
  private Map<String, Object> mappingBest = new LinkedHashMap<>();

  /** The best configuration (Type of PE - Number of PEs of that type) **/
  Map<String, Integer> bestConfig = new LinkedHashMap<>();

  /** Objective data **/
  double objective = Double.MAX_VALUE;

  /** Searching info **/
  Set<Map<String, Integer>> configsAlreadyUsed  = new LinkedHashSet<>();
  Map<String, Integer>      coresOfEachType     = new LinkedHashMap<>();
  Set<String>               pesAlwaysAdded      = new LinkedHashSet<>();
  Map<String, Integer>      coresUsedOfEachType = new LinkedHashMap<>();
  Map<String, Integer>      configToAdd         = new LinkedHashMap<>();
  double                    minEnergy           = Double.MAX_VALUE;
  double                    energyNoObjective   = Double.MAX_VALUE;
  double                    closestFPS          = Double.MAX_VALUE;
  boolean                   finished            = false;

  /** Configuration variables **/
  String startingPoint = "";
  String searchingMode = "";
  String nextChange    = "";

  /**
   * @brief This constructor initializes everything related to energy awareness in mapping/scheduling process
   * @param scenarioOriginal
   *          original {@link Scenario}
   */

  public EnergyAwarenessProvider(Scenario scenarioOriginal, String startingPoint, String searchingMode) {
    /** Backup the original scenario **/
    this.scenarioOriginal = scenarioOriginal;

    /** Update everything related to the objective **/
    this.objective = this.scenarioOriginal.getEnergyConfig().getPerformanceObjective().getObjectiveEPS();

    /** Create the mapping scenario **/
    this.scenarioMapping = ScenarioUserFactory.createScenario();
    copyScenario(this.scenarioOriginal, this.scenarioMapping);

    /** Analyze the constraints and initialize the configs **/
    this.coresOfEachType = getCoresOfEachType(this.scenarioMapping);
    this.pesAlwaysAdded = getImprescindiblePes(this.scenarioMapping);

    String messageLogger = "Imprescindible PEs = " + this.pesAlwaysAdded.toString();
    PreesmLogger.getLogger().log(Level.INFO, messageLogger);
    /** Save the searching parameters **/
    this.startingPoint = startingPoint;
    this.searchingMode = searchingMode;
    this.finished = false;
  }

  /**
   * @return return the current status of scenario mapping
   */
  public Scenario getScenarioMapping() {
    return this.scenarioMapping;
  }

  /**
   * @brief update scenario mapping with the current configuration
   */
  public void updateScenario() {

    /** Reset --> Like this so as to keep group constraints order unaltered */
    this.scenarioMapping.getConstraints().getGroupConstraints()
        .addAll(this.scenarioOriginal.getConstraints().getGroupConstraints());
    this.scenarioOriginal.getConstraints().getGroupConstraints()
        .addAll(this.scenarioMapping.getConstraints().getGroupConstraints());

    /** Add the constraints that represents the new config */
    updateConfigConstrains(this.scenarioOriginal, this.scenarioMapping, this.pesAlwaysAdded, this.coresUsedOfEachType);
    this.configToAdd = getCoresOfEachType(this.scenarioMapping);

    /** Check whether we have tested everything or not */
    String messageLogger = this.configToAdd.toString() + " is being checked";
    PreesmLogger.getLogger().log(Level.INFO, messageLogger);
    if (!configValid(this.configToAdd, this.configsAlreadyUsed)) {
      this.finished = true;
    } else {
      updateConfigSimu(this.scenarioOriginal, this.scenarioMapping);
      this.configsAlreadyUsed.add(this.configToAdd);
    }
  }

  /**
   * @brief Checks if the energy awareness algorithm has already finished
   */
  public boolean hasFinished() {
    return this.finished;
  }

  /**
   * @param mapping
   *          mapping done
   */
  public void evaluateMapping(Map<String, Object> mapping) {
    /** Check the energy **/
    double powerPlatform = computePlatformPower(this.configToAdd, this.scenarioMapping);
    MapperDAG dagMapping = (MapperDAG) mapping.get("DAG");
    double energyDynamic = computeDynamicEnergy(dagMapping, this.scenarioMapping);

    LatencyAbc abcMapping = (LatencyAbc) mapping.get("ABC");
    // We consider that timing tab is filled with us (extracted with PAPIFY timing, for example)
    double fps = 1000000.0 / abcMapping.getFinalLatency();
    // We consider that energy tab is filled with uJ
    double totalDynamicEnergy = (energyDynamic / 1000000.0) * fps;
    double energyThisOne = powerPlatform + totalDynamicEnergy;
    String messageLogger = this.configToAdd.toString() + " reaches " + fps + " FPS consuming " + energyThisOne
        + " joules per second";
    PreesmLogger.getLogger().log(Level.INFO, messageLogger);

    /**
     * Check if it is the best one
     */
    if (fps >= this.objective) {
      if (this.minEnergy > energyThisOne) {
        this.minEnergy = energyThisOne;
        this.closestFPS = fps;
        this.bestConfig.putAll(this.configToAdd);
        this.mappingBest.putAll(mapping);
      }
    } else if (fps > this.closestFPS && this.minEnergy == Double.MAX_VALUE) {
      this.closestFPS = fps;
      this.energyNoObjective = energyThisOne;
      this.bestConfig.putAll(this.configToAdd);
      this.mappingFPS.putAll(mapping);
    }
    /**
     * Compute the next configuration
     */
    if (fps < this.objective) {
      this.nextChange = "up";
    } else {
      this.nextChange = "down";
    }
  }

  /**
   * @brief Computes the next configuration to perform mapping/scheduling
   */
  public void computeNextConfig() {
    if (this.coresUsedOfEachType.isEmpty()) {
      this.coresUsedOfEachType = getFirstConfig(this.coresOfEachType, this.startingPoint);
    } else {
      if (this.searchingMode.equalsIgnoreCase("halves")) {
        getNextConditionalConfig(this.coresUsedOfEachType, this.coresOfEachType, this.nextChange,
            this.configsAlreadyUsed);
      } else if (this.searchingMode.equalsIgnoreCase("thorough")) {
        getNextConfig(this.coresUsedOfEachType, this.coresOfEachType, this.nextChange);
      } else {
        PreesmLogger.getLogger().log(Level.SEVERE,
            "Searching mode in energy-aware mapping/scheduling may be either 'halves' or 'thorough'");
      }
    }
  }

  /**
   * @brief this method checks which is the best mapping and returns its value
   */

  public Map<String, Object> getFinalMapping() {
    /** Getting the best one **/
    Map<String, Object> finalMapping;
    if (this.minEnergy == Double.MAX_VALUE) {
      this.minEnergy = this.energyNoObjective;
      finalMapping = this.mappingFPS;
    } else {
      finalMapping = this.mappingBest;
    }
    String messageLogger = "";
    messageLogger = "The best one is " + this.bestConfig.toString() + ". Retrieving its result";
    PreesmLogger.getLogger().log(Level.INFO, messageLogger);
    messageLogger = "Performance reached =  " + this.closestFPS + " FPS with an energy consumption of " + this.minEnergy
        + " joules per second";
    PreesmLogger.getLogger().log(Level.INFO, messageLogger);
    return finalMapping;
  }

  /**
   * @brief this method updates the scenario and returns it
   */

  public Scenario getFinalScenario() {
    this.scenarioMapping.getConstraints().getGroupConstraints()
        .addAll(this.scenarioOriginal.getConstraints().getGroupConstraints());
    this.scenarioOriginal.getConstraints().getGroupConstraints()
        .addAll(this.scenarioMapping.getConstraints().getGroupConstraints());
    copyScenario(this.scenarioMapping, this.scenarioOriginal);
    return this.scenarioOriginal;
  }

  /**
   * 
   * @param original
   *          original {@link Scenario}
   * @param copy
   *          copy of the original {@link Scenario}
   */
  public static void copyScenario(Scenario original, Scenario copy) {
    copy.setAlgorithm(original.getAlgorithm());
    copy.setDesign(original.getDesign());
    copy.setTimings(original.getTimings());
    copy.setEnergyConfig(original.getEnergyConfig());
    copy.getConstraints().getGroupConstraints().addAll(original.getConstraints().getGroupConstraints());
  }

  /**
   * 
   * @param scenario
   *          original {@link Scenario}
   */
  public static Map<String, Integer> getCoresOfEachType(Scenario scenario) {
    Map<String, Integer> coresOfEachType = new LinkedHashMap<>();
    for (Component component : scenario.getDesign().getOperatorComponents()) {
      int numOfConstrainedComps = scenario.getConstraints().nbConstrainsWithComp(component.getVlnv().getName());
      if (numOfConstrainedComps > 0) {
        coresOfEachType.put(component.getVlnv().getName(), numOfConstrainedComps);
      } else {
        coresOfEachType.put(component.getVlnv().getName(), 0);
      }
    }
    return coresOfEachType;
  }

  /**
   * 
   * @param coresOfEachType
   *          cores available of each type
   * @param typeOfSearch
   *          String to select the type of search we will do
   */
  public static Map<String, Integer> getFirstConfig(Map<String, Integer> coresOfEachType, String typeOfSearch) {
    Map<String, Integer> coresUsedOfEachType = new LinkedHashMap<>();
    switch (typeOfSearch.toLowerCase()) {
      case "first":
        for (Entry<String, Integer> instance : coresOfEachType.entrySet()) {
          if (coresUsedOfEachType.isEmpty()) {
            coresUsedOfEachType.put(instance.getKey(), 1);
          } else {
            coresUsedOfEachType.put(instance.getKey(), 0);
          }
        }
        break;
      case "max":
        coresUsedOfEachType.putAll(coresOfEachType);
        break;
      case "middle":
        for (Entry<String, Integer> instance : coresOfEachType.entrySet()) {
          int value = ((Double) Math.ceil(instance.getValue() / 2.0)).intValue();
          coresUsedOfEachType.put(instance.getKey(), value);
        }
        break;
      case "random":
        do {
          for (Entry<String, Integer> instance : coresOfEachType.entrySet()) {
            int value = ((Double) (Math.random() * instance.getValue())).intValue();
            coresUsedOfEachType.put(instance.getKey(), value);
          }
        } while (!configValid(coresUsedOfEachType, null));
        break;
      default:
        PreesmLogger.getLogger().log(Level.SEVERE,
            "Init mode in energy-aware mapping/scheduling may be 'first', 'max', 'middle' or 'random'");
        break;
    }
    return coresUsedOfEachType;
  }

  /**
   * 
   */
  public static List<Entry<ComponentInstance, EList<AbstractActor>>> getConstraintsOfType(Scenario scenario,
      String peType) {
    return scenario.getConstraints().getGroupConstraints().stream()
        .filter(e -> e.getKey().getComponent().getVlnv().getName().equalsIgnoreCase(peType))
        .collect(Collectors.toList());
  }

  /**
   * 
   */
  public static Entry<ComponentInstance, EList<AbstractActor>> getConstraintByPeName(Scenario scenario, String peName) {
    return scenario.getConstraints().getGroupConstraints().stream()
        .filter(e -> e.getKey().getInstanceName().equalsIgnoreCase(peName)).collect(Collectors.toList()).get(0);
  }

  /**
   * 
   * @param scenario
   *          original {@link Scenario}
   * @param scenarioMapping
   *          {@link Scenario} to be updated
   * @param pesAlwaysAdded
   *          cores that must be always included
   * @param coresUsedOfEachType
   *          config
   */
  public static void updateConfigConstrains(Scenario scenario, Scenario scenarioMapping, Set<String> pesAlwaysAdded,
      Map<String, Integer> coresUsedOfEachType) {
    for (String peName : pesAlwaysAdded) {
      Entry<ComponentInstance, EList<AbstractActor>> constraint = getConstraintByPeName(scenario, peName);
      scenarioMapping.getConstraints().getGroupConstraints().add(constraint);
    }
    Map<String, Integer> coresOfEachTypeAlreadyAdded = EnergyAwarenessProvider.getCoresOfEachType(scenarioMapping);
    for (Entry<String, Integer> instance : coresUsedOfEachType.entrySet()) {
      List<Entry<ComponentInstance, EList<AbstractActor>>> constraints = getConstraintsOfType(scenario,
          instance.getKey());
      int coresLeft = 0;
      if (coresOfEachTypeAlreadyAdded.containsKey(instance.getKey())) {
        coresLeft = instance.getValue() - coresOfEachTypeAlreadyAdded.get(instance.getKey());
      } else {
        coresLeft = instance.getValue();
      }
      if (!constraints.isEmpty() && coresLeft > 0) {
        scenarioMapping.getConstraints().getGroupConstraints().addAll(constraints.subList(0, coresLeft));
      }
    }

  }

  /**
   * 
   * @param scenario
   *          original {@link Scenario}
   * @param scenarioMapping
   *          {@link Scenario} to be updated
   */
  public static void updateConfigSimu(Scenario scenario, Scenario scenarioMapping) {
    scenarioMapping.getSimulationInfo().setMainOperator(null);
    if (!scenarioMapping.getConstraints()
        .isCoreContained(scenario.getSimulationInfo().getMainOperator().getInstanceName())) {
      ComponentInstance newMainNode = scenarioMapping.getConstraints().getGroupConstraints().get(0).getKey();
      scenarioMapping.getSimulationInfo().setMainOperator(newMainNode);
    } else {
      scenarioMapping.getSimulationInfo().setMainOperator(scenario.getSimulationInfo().getMainOperator());
    }
    scenarioMapping.getSimulationInfo().setMainComNode(scenario.getSimulationInfo().getMainComNode());
    boolean needToUpdate = true;
    scenarioMapping.getSimulationInfo().getSpecialVertexOperators().clear();
    for (ComponentInstance specialVertex : scenario.getSimulationInfo().getSpecialVertexOperators()) {
      if (scenarioMapping.getConstraints().isCoreContained(specialVertex.getInstanceName())) {
        scenarioMapping.getSimulationInfo().addSpecialVertexOperator(specialVertex);
        needToUpdate = false;
        break;
      }
    }
    if (needToUpdate) {
      scenarioMapping.getSimulationInfo()
          .addSpecialVertexOperator(scenarioMapping.getConstraints().getGroupConstraints().get(0).getKey());
    }
  }

  /**
   * 
   * @param coresUsedOfEachType
   *          cores used of each type
   * @param scenarioMapping
   *          {@link Scenario} used in the mapping
   * @return
   */
  public static double computePlatformPower(Map<String, Integer> coresUsedOfEachType, Scenario scenarioMapping) {
    double powerPlatform = scenarioMapping.getEnergyConfig().getPeTypePowerOrDefault("Base");
    for (Entry<String, Integer> instance : coresUsedOfEachType.entrySet()) {
      double powerPe = scenarioMapping.getEnergyConfig().getPeTypePowerOrDefault(instance.getKey());
      powerPlatform = powerPlatform + (powerPe * instance.getValue());
    }
    return powerPlatform;
  }

  /**
   * 
   * @param mapping
   *          mapping done
   * @param scenarioMapping
   *          {@link Scenario} used in the mapping
   * @return
   */
  public static double computeDynamicEnergy(Mapping mapping, Scenario scenarioMapping) {
    double energyDynamic = 0.0;
    for (Entry<AbstractActor, EList<ComponentInstance>> coreMapping : mapping.getMappings()) {
      for (ComponentInstance compInstance : coreMapping.getValue()) {
        AbstractActor actor = coreMapping.getKey();
        Component component = compInstance.getComponent();
        if (actor != null && actor.getClass().equals(ActorImpl.class)) {
          double energyActor = scenarioMapping.getEnergyConfig().getEnergyActorOrDefault(actor, component);
          energyDynamic = energyDynamic + energyActor;
        }
      }
    }
    return energyDynamic;
  }

  /**
   * 
   * @param dagMapping
   *          mapping done
   * @param scenarioMapping
   *          {@link Scenario} used in the mapping
   * @return
   */
  public static double computeDynamicEnergy(MapperDAG dagMapping, Scenario scenarioMapping) {
    double energyDynamic = 0.0;
    for (DAGVertex vertex : dagMapping.getHierarchicalVertexSet()) {
      ComponentInstance componentInstance = vertex.getPropertyBean().getValue("Operator");
      Component component = componentInstance.getComponent();
      AbstractActor actor = vertex.getReferencePiVertex();
      if (actor != null && actor.getClass().equals(ActorImpl.class)) {
        double energyActor = scenarioMapping.getEnergyConfig().getEnergyActorOrDefault(actor, component);
        energyDynamic = energyDynamic + energyActor;
      }
    }
    return energyDynamic;
  }

  /**
   * 
   * @param coresUsedOfEachType
   *          cores used of each type
   * @param coresOfEachType
   *          cores available of each type
   * @param typeOfSearch
   *          String to select the type of search we will do
   */
  public static void getNextConfig(Map<String, Integer> coresUsedOfEachType, Map<String, Integer> coresOfEachType,
      String typeOfSearch) {
    switch (typeOfSearch.toLowerCase()) {
      case "oneMore":
        for (Entry<String, Integer> peType : coresUsedOfEachType.entrySet()) {
          peType.setValue(peType.getValue() + 1);
          if (peType.getValue() > coresOfEachType.get(peType.getKey())) {
            peType.setValue(0);
          } else {
            break;
          }
        }
        break;
      case "oneLess":
        for (Entry<String, Integer> peType : coresUsedOfEachType.entrySet()) {
          peType.setValue(peType.getValue() - 1);
          if (peType.getValue() < 0) {
            peType.setValue(coresOfEachType.get(peType.getKey()));
          } else {
            break;
          }
        }
        break;
      default:
        PreesmLogger.getLogger().log(Level.SEVERE,
            "Searching steps in 'thorough' energy-aware mapping/scheduling mode may be either 'oneMore' or 'oneLess'");
        break;
    }
  }

  /**
   * 
   * @param coresUsedOfEachType
   *          cores used of each type
   * @param coresOfEachType
   *          cores available of each type
   * @param typeOfSearch
   *          String to select the type of search we will do
   */
  public static void getNextConditionalConfig(Map<String, Integer> coresUsedOfEachType,
      Map<String, Integer> coresOfEachType, String typeOfSearch, Set<Map<String, Integer>> configsAlreadyUsed) {
    Map<String, Integer> previousConfig = new LinkedHashMap<>();
    previousConfig.putAll(coresUsedOfEachType);
    boolean end = false;
    boolean foundSomething = false;
    String messageLogger = "";
    switch (typeOfSearch.toLowerCase()) {
      case "up":
        messageLogger = "FPS below the required ones, increasing number of PEs ...";
        PreesmLogger.getLogger().log(Level.INFO, messageLogger);
        for (Entry<String, Integer> peType : coresUsedOfEachType.entrySet()) {
          int valueUsedBefore = peType.getValue();
          int valueToMakeAverage = coresOfEachType.get(peType.getKey());
          for (Map<String, Integer> alreadyUsed : configsAlreadyUsed) {
            int valueThisConfig = alreadyUsed.get(peType.getKey());
            if (valueThisConfig < valueToMakeAverage && valueThisConfig > valueUsedBefore) {
              valueToMakeAverage = valueThisConfig;
              foundSomething = true;
            }
          }
          if (!foundSomething) {
            valueToMakeAverage = coresOfEachType.get(peType.getKey());
          }
          int valueUsedNext = (int) Math.ceil((valueToMakeAverage + valueUsedBefore) / 2.0);
          peType.setValue(valueUsedNext);
        }
        break;
      case "down":
        messageLogger = "FPS above the required ones, decreasing number of PEs ...";
        PreesmLogger.getLogger().log(Level.INFO, messageLogger);
        for (Entry<String, Integer> peType : coresUsedOfEachType.entrySet()) {
          int valueUsedBefore = peType.getValue();
          int valueToMakeAverage = 0;
          for (Map<String, Integer> alreadyUsed : configsAlreadyUsed) {
            if (alreadyUsed.containsKey(peType.getKey())) {
              int valueThisConfig = alreadyUsed.get(peType.getKey());
              if (valueThisConfig > valueToMakeAverage && valueThisConfig < valueUsedBefore) {
                valueToMakeAverage = valueThisConfig;
                foundSomething = true;
              }
            }
          }
          if (!foundSomething) {
            valueToMakeAverage = 0;
          }
          int valueUsedNext = (int) Math.floor((valueToMakeAverage + valueUsedBefore) / 2.0);
          peType.setValue(valueUsedNext);
        }
        break;
      default:
        PreesmLogger.getLogger().log(Level.SEVERE,
            "Searching steps in 'halves' energy-aware mapping/scheduling mode may be either 'up' or 'down'");
        break;
    }
  }

  /**
   * 
   * @param coresUsedOfEachType
   *          cores used of each type
   * @param configsAlreadyUsed
   *          configs already used
   * @return
   */
  public static boolean configValid(Map<String, Integer> coresUsedOfEachType,
      Set<Map<String, Integer>> configsAlreadyUsed) {
    boolean valid = true;
    if (configsAlreadyUsed != null && configsAlreadyUsed.contains(coresUsedOfEachType)) {
      valid = false;
    }
    if (coresUsedOfEachType.entrySet().stream().filter(e -> e.getValue() != 0).collect(Collectors.toList()).isEmpty()) {
      valid = false;
    }

    return valid;
  }

  /**
   * 
   * @param scenarioMapping
   *          {@link Scenario} used in the mapping
   * @return
   */
  public static Set<String> getImprescindiblePes(Scenario scenarioMapping) {
    Set<String> imprescindiblePes = new LinkedHashSet<>();
    for (AbstractActor actor : scenarioMapping.getAlgorithm().getAllActors()) {
      if (actor != null && actor.getClass().equals(ActorImpl.class)) {
        List<Entry<ComponentInstance, EList<AbstractActor>>> constraints = scenarioMapping.getConstraints()
            .getGroupConstraints().stream().filter(e -> e.getValue().contains(actor)).collect(Collectors.toList());
        if (constraints.size() == 1) {
          for (Entry<ComponentInstance, EList<AbstractActor>> constraint : constraints) {
            imprescindiblePes.add(constraint.getKey().getInstanceName());
          }
        }
      }
    }
    return imprescindiblePes;
  }

  /**
   * 
   * @param scenarioMapping
   *          {@link Scenario} used in the mapping
   * @param coresOfEachType
   *          Cores available of each type
   * @param pesAlwaysAdded
   *          Name of cores always added
   */
  public static void removeImprescindibleFromAvailableCores(Scenario scenarioMapping,
      Map<String, Integer> coresOfEachType, Set<String> pesAlwaysAdded) {
    for (String peName : pesAlwaysAdded) {
      String peType = getConstraintByPeName(scenarioMapping, peName).getKey().getComponent().getVlnv().getName();
      coresOfEachType.put(peType, coresOfEachType.get(peType) - 1);
    }
  }
}
