/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2020)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2020)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
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
package org.preesm.codegen.xtend.spider2.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.emf.common.util.EMap;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.ScenarioConstants;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.ProcessingElement;
import org.preesm.model.slam.TimingType;

/**
 * Class regrouping information related to an AbstractActor for the spider 2 codegen. Information such as timings,
 * mapping constraints, config input ports, etc.
 * 
 * @author farresti
 *
 */
public class Spider2CodegenActor {
  /** The default timing */
  private static final String DEFAULT_TIMING = Integer.toString(ScenarioConstants.DEFAULT_TIMING_TASK.getValue());

  /** The type of the actor */
  private final String type;

  /** The actor */
  private final AbstractActor actor;

  /** The refinement ConfigInputPort list */
  private final List<ConfigInputPort> refinementConfigInputPorts = new ArrayList<>();

  /** The refinement ConfigInputPort list */
  private final List<ConfigInputPort> rateConfigInputPorts = new ArrayList<>();

  /** The refinement ConfigOutputPort list */
  private final List<ConfigOutputPort> refinementConfigOutputPorts = new ArrayList<>();

  /** The scenario */
  private final Scenario scenario;

  /** The timing of the actor on the different Processing Elements */
  private final List<Pair<String, String>> timingList = new ArrayList<>();

  /** The mapping constraints of the actor */
  private final List<Pair<String, Boolean>> mappingConstraintList = new ArrayList<>();

  /**
   * Constructor of the class.
   * 
   * @param type
   *          spider 2 type of the actor ("NORMAL", "REPEAT", "FORK", etc.)
   * @param actor
   *          the actor
   * @param scenario
   *          the scenario of the application
   * @param clusters
   *          list of pre-processed clusters
   */
  public Spider2CodegenActor(final String type, final AbstractActor actor, final Scenario scenario,
      final List<Spider2CodegenCluster> clusters) {
    this.type = type;
    this.actor = actor;
    this.scenario = scenario;
    if (actor instanceof Actor) {
      final Actor a = (Actor) (actor);
      if (a.getRefinement() instanceof CHeaderRefinement) {
        final CHeaderRefinement refinement = (CHeaderRefinement) (a.getRefinement());
        if (refinement.getLoopPrototype() != null) {
          this.refinementConfigInputPorts.addAll(refinement.getLoopConfigInputPorts());
          this.refinementConfigOutputPorts.addAll(refinement.getLoopConfigOutputPorts());
        }
      }
    }
    this.rateConfigInputPorts.addAll(actor.getConfigInputPorts().stream()
        .filter(x -> !this.refinementConfigInputPorts.contains(x)).collect(Collectors.toList()));

    /* Get the timing of the actor */
    generateTimings();

    /* Get the constraints of the actor */
    generateMappingConstraints(clusters);
  }

  private void generateTimings() {
    final List<
        Entry<Component, EMap<TimingType, String>>> timings = this.scenario.getTimings().getActorTimings().get(actor);
    if (timings != null) {
      for (final Entry<Component, EMap<TimingType, String>> timing : timings) {
        final Component operator = timing.getKey();
        this.timingList.add(new Pair<>(operator.getVlnv().getName(), timing.getValue().get(TimingType.EXECUTION_TIME)));
      }
    }
    final List<ProcessingElement> components = this.scenario.getDesign().getProcessingElements();
    if (this.timingList.size() != components.size()) {
      final List<Component> defaultTimingComponents = components.stream()
          .filter(x -> this.timingList.stream().filter(p -> p.getKey().equals(x.getVlnv().getName())).count() == 0)
          .collect(Collectors.toList());
      for (final Component comp : defaultTimingComponents) {
        final String operatorName = comp.getVlnv().getName();
        this.timingList.add(new Pair<>(operatorName, DEFAULT_TIMING));
      }
    }
  }

  private Spider2CodegenCluster findClusterFromPE(final ComponentInstance operator,
      final List<Spider2CodegenCluster> clusters) {
    for (final Spider2CodegenCluster cluster : clusters) {
      for (final Spider2CodegenPE pe : cluster.getProcessingElements()) {
        if (pe.getName().equals(operator.getInstanceName())) {
          return cluster;
        }
      }
    }
    return null;
  }

  private void generateMappingConstraints(final List<Spider2CodegenCluster> clusters) {
    /* Setting mappable constraints */
    final List<ComponentInstance> mappableOperators = this.scenario.getPossibleMappings(this.actor);
    for (final ComponentInstance instance : mappableOperators) {
      final Spider2CodegenCluster cluster = findClusterFromPE(instance, clusters);
      if (cluster == null) {
        throw new PreesmRuntimeException("did not find operator " + instance.getInstanceName() + " in clusters.");
      }
      final String fullNameString = getFullNameMapping(cluster, instance);
      this.mappingConstraintList.add(new Pair<>(fullNameString, true));
    }

    final List<ComponentInstance> operators = this.scenario.getDesign().getOrderedOperatorComponentInstances();
    if (this.mappingConstraintList.size() != operators.size()) {
      /* Setting non mappable constraints */
      for (final ComponentInstance operator : operators) {
        final Spider2CodegenCluster cluster = findClusterFromPE(operator, clusters);
        if (cluster == null) {
          throw new PreesmRuntimeException("did not find operator " + operator.getInstanceName() + " in clusters.");
        }
        final String fullNameString = getFullNameMapping(cluster, operator);
        boolean found = false;
        for (final Pair<String, Boolean> entry : this.mappingConstraintList) {
          if (entry.getKey().equals(fullNameString)) {
            found = true;
            break;
          }
        }
        if (!found) {
          this.mappingConstraintList.add(new Pair<>(fullNameString, false));
        }
      }
    }
  }

  /**
   * Get the full name of the mapping constraint (cluster + pe)
   * 
   * @param cluster
   *          the cluster on which the actor is constrained
   * @param component
   *          the pe on which the actor is constrained
   * @return the full mapping name cluster.name_pe.name
   */
  private String getFullNameMapping(final Spider2CodegenCluster cluster, final ComponentInstance component) {
    return cluster.getName() + "_" + component.getInstanceName();
  }

  /**
   * 
   * @return spider 2 type of the actor
   */
  public String getType() {
    return this.type;
  }

  /**
   * 
   * @return the actor
   */
  public AbstractActor getActor() {
    return this.actor;
  }

  /**
   * 
   * @return the list of ConfigInputPort used only in rate expressions
   */
  public List<ConfigInputPort> getRateConfigInputPorts() {
    return this.rateConfigInputPorts;
  }

  /**
   * 
   * @return the list of ConfigInputPort used only in the refinement of the actor
   */
  public List<ConfigInputPort> getRefinementConfigInputPorts() {
    return this.refinementConfigInputPorts;
  }

  /**
   * 
   * @return the list of ConfigOutputPort used only in the refinement of the actor
   */
  public List<ConfigOutputPort> getRefinementConfigOutputPorts() {
    return this.refinementConfigOutputPorts;
  }

  /**
   * 
   * @return the list of timing as pair (PE, Timing expression)
   */
  public List<Pair<String, String>> getTimings() {
    return this.timingList;
  }

  /**
   * 
   * @return the list of mapping constraints (PE, mapping flag)
   */
  public List<Pair<String, Boolean>> getMappingConstraints() {
    return this.mappingConstraintList;
  }

  /**
   * 
   * @return true if actor is mappable on all pe, false else
   */
  public boolean isMappableOnAll() {
    return this.mappingConstraintList.stream().filter(p -> p.getValue()).count() == this.mappingConstraintList.size();
  }
}
