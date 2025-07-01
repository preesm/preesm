package org.preesm.algorithm.clustering;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.CPU;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/***
 * @author jmorin
 **/

@PreesmTask(id = "clustering", name = "Clustering",

    inputs = { @Port(name = AbstractWorkflowNodeImplementation.KEY_SCENARIO, type = Scenario.class),
        @Port(name = AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, type = PiGraph.class),
        @Port(name = AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE, type = Design.class) },

    outputs = { @Port(name = AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, type = PiGraph.class),
        @Port(name = AbstractWorkflowNodeImplementation.KEY_SUBGRAPHS_LIST, type = Map.class),
        @Port(name = AbstractWorkflowNodeImplementation.KEY_SCENARIO, type = Scenario.class) },

    description = "Workflow task responsible for clustering hierarchical actors."

)
public class ClusteringTask extends AbstractTaskImplementation {
  static final PiMMUserFactory PiMMFactory = org.preesm.model.pisdf.factory.PiMMUserFactory.instance;

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {
    final PiGraph original_algorithm = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);
    // faire une copie du scénario pour la modifier et ensuite la retourner via outputs ? Mais comme il n'y a pas de
    // copie facile de scénario ça m'emmerde
    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);

    final PiGraph algorithm = PiMMUserFactory.instance.copyPiGraphWithHistory(original_algorithm);

    final Map<String, Object> outputs = new LinkedHashMap<>();

    // bi-directional map that links clusters to their placeholder actor
    final Map<AbstractActor, AbstractActor> clusterToActorMap = new HashMap<>();

    final boolean CLUSTERIZE = "true".equalsIgnoreCase(parameters.get("clusterize"));

    if (CLUSTERIZE) {
      final List<PiGraph> clustersList = ClusterBuilder.buildArchHierarchyGraph(algorithm, scenario);
      UpdateSubgraphsMappings(clustersList, scenario);

      // --------------------------------------------------------------------------------------
      // -------------------- replace hierarchical actors with placeholders -------------------
      // --------------------------------------------------------------------------------------

      // find the main PE
      ComponentInstance mainCPU;
      if (scenario.getSimulationInfo().getMainOperator() instanceof CPU) {
        mainCPU = scenario.getSimulationInfo().getMainOperator();
      } else {
        mainCPU = architecture.getComponentInstances().stream().filter(c -> c.getComponent() instanceof CPU).toList()
            .getFirst();
      }

      // TODO find an adapted accelerator, not just any non-x86 core
      final ComponentInstance accelerator = architecture.getComponentInstances().stream()
          .filter(c -> c.getComponent() != mainCPU.getComponent()).toList().getFirst();

      for (final PiGraph cluster : clustersList) {
        final Actor placeholder = PiMMFactory.createActor(cluster.getName() + "_placeholder");
        placeholder.setRefinement(PiMMFactory.createCHeaderRefinement());

        // algorithm.addActor(placeholder);
        // replaceAndRemoveActor(cluster, placeholder, algorithm);

        scenario.getConstraints().addConstraint(accelerator, placeholder); // test, idéalement ça serait une "non-archi"

        clusterToActorMap.put(cluster, placeholder);
        clusterToActorMap.put(placeholder, cluster);
      }
    }

    outputs.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, algorithm);
    outputs.put(AbstractWorkflowNodeImplementation.KEY_SUBGRAPHS_LIST, clusterToActorMap);
    outputs.put(AbstractWorkflowNodeImplementation.KEY_SCENARIO, scenario);

    return outputs;
  }

  /***
   * Sets all actors in each cluster to a single mapping, the same as the cluster that contains it
   *
   * @param clustersList
   *          the list of clusters
   * @param scenario
   *          the scenario
   */
  private void UpdateSubgraphsMappings(List<PiGraph> clustersList, Scenario scenario) {
    for (final PiGraph cluster : clustersList) {
      // a cluster is mapped to only 1 component
      // vu que ce sont les acteurs du srdag mais qu'on a les mappings du pisdf, il ne va trouver aucun acteur ! Que
      // faire ?
      final ComponentInstance mapping = scenario.getPossibleMappings(cluster).getFirst();

      // Acteurs abstraits ou seulement acteurs avec refinement ?
      for (final AbstractActor actor : cluster.getActors()) {

        // get all cores and their mapped actors. If the core is not the same as mapping and a is mapped to it, remove
        // actor from the constraint
        scenario.getConstraints().getGroupConstraints().stream().filter(entry -> entry.getValue().contains(actor))
            .forEach(entry -> {
              if (entry.getKey() != mapping) { // if the actor is mapped to another core that mapping
                // remove that mapping
                entry.getValue().remove(actor);
              }
            });

        // scenario.setConstraints(null);
      }
    }
  }

  /***
   * Creates a placeholder actor to replace a cluster actor, with the same timing characteristics. public or private, I
   * don't care
   *
   * @param oldA
   *          clusterActor
   * @param newA
   *          the new placeholder actor
   * @param graph
   *          the application graph
   */
  private void replaceAndRemoveActor(AbstractActor oldA, AbstractActor newA, PiGraph graph) {

    // TODO brancher les dépendances dans le placeholder
    // clone input and output outer interfaces
    // plug fifos and copy rates

    for (final DataInputPort olddip : oldA.getDataInputPorts()) {
      final DataInputPort newdip = PiMMFactory.createDataInputPort(olddip.getName());

      newdip.setExpression(olddip.getExpression());
      newA.getDataInputPorts().add(newdip);
      newdip.setIncomingFifo(olddip.getFifo());
    }
    for (final DataOutputPort olddop : oldA.getDataOutputPorts()) {
      final DataOutputPort newdop = PiMMFactory.createDataOutputPort(olddop.getName());

      newdop.setExpression(olddop.getExpression());
      newA.getDataOutputPorts().add(newdop);
      newdop.setOutgoingFifo(olddop.getFifo());
    }

    for (final ConfigInputPort oldcip : oldA.getConfigInputPorts()) {
      // create a new dependency that will be plugged to a new config port
      final ConfigInputPort newcip = PiMMFactory.createConfigInputPort();
      newcip.setName(oldcip.getName());

      final Dependency newDep = PiMMFactory.createDependency(oldcip.getIncomingDependency().getSetter(), newcip);

      newA.getConfigInputPorts().add(newcip);
      graph.addDependency(newDep);
    }

    // remove the old cluster actor from the graph
    graph.removeActorAndDependencies(oldA);

  }

  @Override
  public Map<String, String> getDefaultParameters() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String monitorMessage() {
    // TODO Auto-generated method stub
    return null;
  }

}
