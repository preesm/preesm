/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2008 - 2011)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2015 - 2017)
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
package org.ietr.preesm.mapper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.types.DAGDefaultVertexPropertyType;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.architecture.slam.SlamFactory;
import org.ietr.dftools.architecture.slam.attributes.AttributesFactory;
import org.ietr.dftools.architecture.slam.attributes.VLNV;
import org.ietr.dftools.architecture.slam.component.ComNode;
import org.ietr.dftools.architecture.slam.component.Component;
import org.ietr.dftools.architecture.slam.component.ComponentFactory;
import org.ietr.dftools.architecture.slam.component.Operator;
import org.ietr.dftools.architecture.slam.link.Link;
import org.ietr.dftools.architecture.slam.link.LinkFactory;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.mapper.abc.AbstractAbc;
import org.ietr.preesm.mapper.abc.IAbc;
import org.ietr.preesm.mapper.abc.impl.latency.InfiniteHomogeneousAbc;
import org.ietr.preesm.mapper.abc.taskscheduling.SimpleTaskSched;
import org.ietr.preesm.mapper.algo.dynamic.DynamicQueuingScheduler;
import org.ietr.preesm.mapper.graphtransfo.SdfToDagConverter;
import org.ietr.preesm.mapper.graphtransfo.TagDAG;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.model.property.EdgeInit;
import org.ietr.preesm.mapper.params.AbcParameters;

// TODO: Auto-generated Javadoc
/**
 * Plug-in class for dynamic queuing scheduling. Dynamic queuing is a type of list scheduling that enables study of multiple graph iterations.
 *
 * @author mpelcat
 * @author kdesnos (minor bugfix)
 */
public class DynamicQueuingMapping extends AbstractMapping {

  /**
   * Instantiates a new dynamic queuing mapping.
   */
  public DynamicQueuingMapping() {
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.AbstractMapping#getDefaultParameters()
   */
  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = super.getDefaultParameters();

    // The graph is repeated iterationNr times and iterationPeriod
    // defines the period of this repetition.
    // The period is simulated by Virtual delays: specific actors.
    parameters.put("iterationNr", "0");
    parameters.put("iterationPeriod", "0");
    parameters.put("listType", "optimised");
    return parameters;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.AbstractMapping#execute(java.util.Map, java.util.Map, org.eclipse.core.runtime.IProgressMonitor, java.lang.String,
   * org.ietr.dftools.workflow.elements.Workflow)
   */
  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters, final IProgressMonitor monitor,
      final String nodeName, final Workflow workflow) throws WorkflowException {

    final Map<String, Object> outputs = new LinkedHashMap<>();
    final Design architecture = (Design) inputs.get("architecture");
    final SDFGraph algorithm = (SDFGraph) inputs.get("SDF");
    final PreesmScenario scenario = (PreesmScenario) inputs.get("scenario");

    // The graph may be repeated a predefined number of times
    // with a predefined period
    final int iterationNr = Integer.valueOf(parameters.get("iterationNr"));
    final int iterationPeriod = Integer.valueOf(parameters.get("iterationPeriod"));

    // Repeating the graph to simulate several calls. Repetitions are
    // delayed through
    // Special dependencies to virtual operator tasks
    ComponentInstance virtualDelayManager = null;
    if (iterationNr != 0) {
      // Creating a virtual component
      final VLNV v = AttributesFactory.eINSTANCE.createVLNV();
      v.setVendor("nobody");
      v.setLibrary("none");
      v.setName("DelayManager");
      v.setVersion("1.0");

      final Component component = ComponentFactory.eINSTANCE.createOperator();
      component.setVlnv(v);
      architecture.getComponentHolder().getComponents().add(component);
      SlamFactory.eINSTANCE.createComponentInstance();
      virtualDelayManager = SlamFactory.eINSTANCE.createComponentInstance();
      virtualDelayManager.setInstanceName("VirtualDelayManager");
      virtualDelayManager.setComponent(component);
      architecture.getComponentInstances().add(virtualDelayManager);

      // Connecting the virtual component to all cores
      for (final ComponentInstance cmp : DesignTools.getComponentInstances(architecture)) {
        if ((cmp.getComponent() instanceof Operator) && !cmp.getInstanceName().equals("VirtualDelayManager")) {
          final VLNV v2 = AttributesFactory.eINSTANCE.createVLNV();
          v.setVendor("nobody");
          v.setLibrary("none");
          v.setName("DelayManagerNodeTo" + cmp.getInstanceName());
          v.setVersion("1.0");
          final ComNode nodeDef = ComponentFactory.eINSTANCE.createComNode();
          nodeDef.setParallel(true);
          nodeDef.setVlnv(v2);
          nodeDef.setSpeed(1000000);
          final ComponentInstance virtualNode = SlamFactory.eINSTANCE.createComponentInstance();
          virtualDelayManager.setInstanceName("virtualNodeTo" + cmp.getInstanceName());
          virtualDelayManager.setComponent(nodeDef);
          architecture.getComponentInstances().add(virtualNode);
          architecture.getComponentHolder().getComponents().add(nodeDef);
          final Link c1 = LinkFactory.eINSTANCE.createDataLink();
          c1.setDirected(false);
          c1.setSourceComponentInstance(virtualNode);
          c1.setDestinationComponentInstance(cmp);
          architecture.getLinks().add(c1);
          final Link c2 = LinkFactory.eINSTANCE.createDataLink();
          c2.setDirected(false);
          c2.setSourceComponentInstance(virtualNode);
          c2.setDestinationComponentInstance(virtualDelayManager);
          architecture.getLinks().add(c2);
        }
      }
    }

    super.execute(inputs, parameters, monitor, nodeName, workflow);

    final AbcParameters abcParameters = new AbcParameters(parameters);

    final MapperDAG dag = SdfToDagConverter.convert(algorithm, architecture, scenario);

    // Repeating the graph to simulate several calls.
    if (iterationNr != 0) {
      WorkflowLogger.getLogger().log(Level.INFO,
          "Repetition of the graph " + iterationNr + " time(s) with period " + iterationPeriod + " required in dynamic scheduling");

      // Creating virtual actors to delay iterations
      MapperDAGVertex lastCreatedVertex = null;
      for (int i = 0; i < iterationNr; i++) {
        // A virtual actor with SDF corresponding vertex to associate a
        // timing
        final MapperDAGVertex v = new MapperDAGVertex();
        final SDFAbstractVertex sdfV = new SDFVertex();
        sdfV.setName("VirtualDelay");
        sdfV.setId("VirtualDelay");
        v.setCorrespondingSDFVertex(sdfV);
        v.setName("VirtualDelay" + "__@" + (i + 2));
        v.setId("VirtualDelay" + "__@" + (i + 2));
        v.setNbRepeat(new DAGDefaultVertexPropertyType(1));
        v.getInit().addOperator(virtualDelayManager);
        final Timing timing = new Timing(virtualDelayManager.getComponent().getVlnv().getName(), sdfV.getName(), iterationPeriod);
        v.getInit().addTiming(timing);
        dag.addVertex(v);

        // Edges between actors ensure the order of appearance
        if (lastCreatedVertex != null) {
          final MapperDAGEdge e = (MapperDAGEdge) dag.addEdge(lastCreatedVertex, v);
          final EdgeInit p = new EdgeInit(0);
          e.setInit(p);
        }
        lastCreatedVertex = v;
      }

      for (int i = 0; i < iterationNr; i++) {
        final MapperDAG clone = dag.clone();

        final MapperDAGVertex correspondingVirtualVertex = (MapperDAGVertex) dag.getVertex("VirtualDelay" + "__@" + (i + 2));

        // Copy cloned vertices into dag
        for (final DAGVertex v : clone.vertexSet()) {
          if (!v.getName().contains("__@")) {
            // Cloning the vertices to duplicate the graph
            v.setName(v.getName() + "__@" + (i + 2));
            v.setId(v.getId() + "__@" + (i + 2));
            dag.addVertex(v);

            // Adding edges to delay correctly the execution of
            // iterations. Only vertices without predecessor are
            // concerned
            if (v.incomingEdges().isEmpty()) {
              final MapperDAGEdge e = (MapperDAGEdge) dag.addEdge(correspondingVirtualVertex, v);

              // 0 data edges will be ignored while routing
              final EdgeInit p = new EdgeInit(0);
              e.setInit(p);
            }
          }
        }

        // Copy cloned edges into dag
        final String currentPostFix = "__@" + (i + 2);
        for (final DAGEdge e : clone.edgeSet()) {
          if (e.getSource().getName().contains(currentPostFix) && e.getTarget().getName().contains(currentPostFix)) {
            dag.addEdge(e.getSource(), e.getTarget(), e);
          }
        }
      }
    }

    // calculates the DAG span length on the architecture main operator (the
    // tasks that can
    // not be executed by the main operator are deported without transfer
    // time to other operator)
    calculateSpan(dag, architecture, scenario, abcParameters);

    // Generating the vertex list in correct order
    final IAbc simu = new InfiniteHomogeneousAbc(abcParameters, dag, architecture, abcParameters.getSimulatorType().getTaskSchedType(), scenario);

    WorkflowLogger.getLogger().log(Level.INFO, "Dynamic Scheduling");

    final IAbc simu2 = AbstractAbc.getInstance(abcParameters, dag, architecture, scenario);
    simu2.setTaskScheduler(new SimpleTaskSched());

    // kdesnos fix : Reset DAG, otherwise all actors are mapped on the same
    // operator
    simu2.resetDAG();

    final DynamicQueuingScheduler dynamicSched = new DynamicQueuingScheduler(simu.getTotalOrder(), parameters);
    dynamicSched.mapVertices(simu2);

    simu2.retrieveTotalOrder();

    final TagDAG tagSDF = new TagDAG();

    try {
      tagSDF.tag(dag, architecture, scenario, simu2, abcParameters.getEdgeSchedType());
    } catch (final InvalidExpressionException e) {
      e.printStackTrace();
      throw (new WorkflowException(e.getMessage()));
    }

    outputs.put("DAG", dag);
    outputs.put("ABC", simu2);

    super.clean(architecture, scenario);
    super.checkSchedulingResult(parameters, dag);

    WorkflowLogger.getLogger().log(Level.INFO, "End of Dynamic Scheduling");

    return outputs;
  }

}
