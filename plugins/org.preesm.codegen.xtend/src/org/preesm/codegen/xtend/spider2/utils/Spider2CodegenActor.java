package org.preesm.codegen.xtend.spider2.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.commons.math3.util.Pair;
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
        this.refinementConfigInputPorts.addAll(refinement.getLoopConfigInputPorts());
        this.refinementConfigOutputPorts.addAll(refinement.getLoopConfigOutputPorts());
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
    final List<Entry<Component, String>> timings = this.scenario.getTimings().getActorTimings().get(actor);
    if (timings != null) {
      for (final Entry<Component, String> timing : timings) {
        final Component operator = timing.getKey();
        this.timingList.add(new Pair<>(operator.getVlnv().getName(), timing.getValue()));
      }
    }
    final List<Component> components = this.scenario.getDesign().getOperatorComponents();
    if (this.timingList.size() != components.size()) {
      final List<Component> defaultTimingComponents = components.stream()
          .filter(x -> this.timingList.stream().filter(p -> p.getFirst().equals(x.getVlnv().getName())).count() == 0)
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
          if (entry.getFirst().equals(fullNameString)) {
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
    return this.mappingConstraintList.stream().filter(p -> p.getSecond()).count() == this.mappingConstraintList.size();
  }
}
