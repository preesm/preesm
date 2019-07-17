/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Jonathan Piat [jpiat@laas.fr] (2008 - 2011)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2017 - 2018)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2008 - 2012)
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
package org.preesm.algorithm.mapper.energyAwareness;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.emf.common.util.EList;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.model.dag.DAGVertex;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.impl.ActorImpl;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;

/**
 * 
 * @author dmadronal
 *
 */
public class EnergyAwarenessHelper {

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
    for (Component component : scenario.getDesign().getComponents()) {
      int numOfConstrainedComps = scenario.getConstraints().nbConstrainsWithComp(component.getVlnv().getName());
      if (numOfConstrainedComps > 0) {
        coresOfEachType.put(component.getVlnv().getName(), numOfConstrainedComps);
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
    switch (typeOfSearch) {
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
    Map<String, Integer> coresOfEachTypeAlreadyAdded = EnergyAwarenessHelper.getCoresOfEachType(scenarioMapping);
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
    if (!scenarioMapping.getConstraints()
        .isCoreContained(scenario.getSimulationInfo().getMainOperator().getInstanceName())) {
      ComponentInstance newMainNode = scenarioMapping.getConstraints().getGroupConstraints().get(0).getKey();
      scenarioMapping.getSimulationInfo().setMainOperator(newMainNode);
    } else {
      scenarioMapping.getSimulationInfo().setMainOperator(scenario.getSimulationInfo().getMainOperator());
    }
    scenarioMapping.getSimulationInfo().setMainComNode(scenario.getSimulationInfo().getMainComNode());

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
    switch (typeOfSearch) {
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
    switch (typeOfSearch) {
      case "up":
        System.out.println("Going UP");
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
        System.out.println("Going DOWN");
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
