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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
    switch (typeOfSearch.toLowerCase()) {
      case "thorough":
        for (Entry<String, Integer> instance : coresOfEachType.entrySet()) {
          if (coresUsedOfEachType.isEmpty()) {
            coresUsedOfEachType.put(instance.getKey(), 1);
          } else {
            coresUsedOfEachType.put(instance.getKey(), 0);
          }
        }
        break;
      default:
        break;
    }
    return coresUsedOfEachType;
  }

  /**
   * 
   * @param scenario
   *          original {@link Scenario}
   * @param scenarioMapping
   *          {@link Scenario} to be updated
   * @param coresUsedOfEachType
   *          config
   */
  public static void updateConfigConstrains(Scenario scenario, Scenario scenarioMapping,
      Map<String, Integer> coresUsedOfEachType) {
    for (Entry<String, Integer> instance : coresUsedOfEachType.entrySet()) {
      List<Entry<ComponentInstance, EList<AbstractActor>>> constraints = scenario.getConstraints().getGroupConstraints()
          .stream().filter(e -> e.getKey().getComponent().getVlnv().getName().equals(instance.getKey()))
          .collect(Collectors.toList()).subList(0, instance.getValue());
      scenarioMapping.getConstraints().getGroupConstraints().addAll(constraints);
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
    switch (typeOfSearch.toLowerCase()) {
      case "thorough":
        for (Entry<String, Integer> peType : coresUsedOfEachType.entrySet()) {
          peType.setValue(peType.getValue() + 1);
          if (peType.getValue() > coresOfEachType.get(peType.getKey())) {
            peType.setValue(0);
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
   * @return
   */
  public static boolean configValid(Map<String, Integer> coresUsedOfEachType) {
    return !coresUsedOfEachType.entrySet().stream().filter(e -> e.getValue() != 0).collect(Collectors.toList())
        .isEmpty();
  }
}
