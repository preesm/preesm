package org.preesm.algorithm.clustering;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.scenario.Scenario;
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
      @Port(name = AbstractWorkflowNodeImplementation.KEY_SUBGRAPHS_LIST, type = List.class),
      @Port(name = AbstractWorkflowNodeImplementation.KEY_SCENARIO, type = Scenario.class) },

    description = "Workflow task responsible for clustering hierarchical actors."

)
public class ClusteringTask extends AbstractTaskImplementation {
  static final PiMMUserFactory PiMMFactory = org.preesm.model.pisdf.factory.PiMMUserFactory.instance;

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    final PiGraph algorithm = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);
    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);

    final Map<String, Object> outputs = new LinkedHashMap<>();

    // bi-directional map that links clusters to their placeholder actor
    // final Map<AbstractActor, AbstractActor> clusterToActorMap = new HashMap<>();

    final boolean CLUSTERIZE = "true".equalsIgnoreCase(parameters.get("clusterize"));
    String heuristicName = parameters.get("clusterize");
    if (heuristicName == null) {
      heuristicName = "";
    }

    List<PiGraph> clustersList = new LinkedList<>();

    if (CLUSTERIZE) {
      PreesmLogger.getLogger().info(" -- Clustering task --");
      clustersList = ClusterBuilder.buildArchHierarchyGraph(algorithm, scenario, heuristicName);
      updateSubgraphsMappings(algorithm.getAllClusters(), scenario);

    } else {
      PreesmLogger.getLogger().info(" - Clustering was not activated");
    }

    outputs.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, algorithm);
    // outputs.put(AbstractWorkflowNodeImplementation.KEY_SUBGRAPHS_LIST, clusterToActorMap);
    outputs.put(AbstractWorkflowNodeImplementation.KEY_SUBGRAPHS_LIST, clustersList);
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
  private void updateSubgraphsMappings(List<PiGraph> clustersList, Scenario scenario) {
    for (final PiGraph cluster : clustersList) {
      // a cluster is mapped to only 1 component
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
