/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2014)
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
package org.ietr.preesm.mapper.ui.stats;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.ietr.dftools.algorithm.model.PropertyBean;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.types.ImplementationPropertyNames;
import org.ietr.preesm.core.types.VertexType;
import org.ietr.preesm.mapper.abc.IAbc;
import org.ietr.preesm.mapper.abc.impl.latency.LatencyAbc;
import org.ietr.preesm.mapper.abc.impl.latency.SpanLengthCalculator;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.model.special.InvolvementVertex;
import org.ietr.preesm.mapper.model.special.OverheadVertex;
import org.ietr.preesm.mapper.model.special.PrecedenceEdge;
import org.ietr.preesm.mapper.model.special.ReceiveVertex;
import org.ietr.preesm.mapper.model.special.SendVertex;
import org.ietr.preesm.mapper.model.special.TransferVertex;

/**
 * Generating the statistics to be displayed in stat editor.
 *
 * @author mpelcat
 */
public class StatGenerator {

  /** The abc. */
  private IAbc abc = null;

  /** The scenario. */
  private PreesmScenario scenario = null;

  /** The params. */
  private Map<String, String> params = null;

  /** The final time. */
  private long finalTime = 0;

  /**
   * Instantiates a new stat generator.
   *
   * @param abc
   *          the abc
   * @param scenario
   *          the scenario
   * @param params
   *          the params
   */
  public StatGenerator(final IAbc abc, final PreesmScenario scenario, final Map<String, String> params) {
    super();
    this.params = params;
    this.scenario = scenario;
    if (abc instanceof LatencyAbc) {
      this.abc = abc;

      this.abc.updateFinalCosts();
      this.finalTime = ((LatencyAbc) this.abc).getFinalLatency();
    } else {
      this.abc = abc;
      this.abc.updateFinalCosts();
      this.finalTime = 0;
    }
  }

  /**
   * The span is the shortest possible execution time. It is theoretic because no communication time is taken into
   * account. We consider that we have an infinity of cores of main type totally connected with perfect media. The span
   * complex because the DAG is not serial-parallel but can be any DAG. It is retrieved from the DAG if it was set from
   * the infinite homogeneous simulation. If there was no such simulation, the span length can not be recalculated
   * because the original DAG without transfers is no more available.
   *
   * @return the DAG span length
   */
  public long getDAGSpanLength() {
    final Object span = this.abc.getDAG().getPropertyBean().getValue(SpanLengthCalculator.DAG_SPAN);
    if (span instanceof Long) {
      return (Long) span;
    }
    return 0;
  }

  /**
   * The work is the sum of all task lengths excluding vertices added by the mapping.
   *
   * @return the DAG work length
   * @throws WorkflowException
   *           the workflow exception
   */
  public long getDAGWorkLength() {

    long work = 0;
    final MapperDAG dag = this.abc.getDAG();

    final ComponentInstance mainOp = DesignTools.getComponentInstance(this.abc.getArchitecture(),
        this.scenario.getSimulationManager().getMainOperatorName());

    for (final DAGVertex vertex : dag.vertexSet()) {
      if (!(vertex instanceof TransferVertex) && !(vertex instanceof OverheadVertex)
          && !(vertex instanceof InvolvementVertex)) {

        // Looks for an operator able to execute currentvertex
        // (preferably
        // the given operator)
        final ComponentInstance adequateOp = this.abc.findOperator((MapperDAGVertex) vertex, mainOp, false);

        work += ((MapperDAGVertex) vertex).getInit().getTime(adequateOp);

      }
    }

    return work;
  }

  /**
   * Returns the final time of the current ABC.
   *
   * @return the result time
   */
  public long getResultTime() {
    if (this.abc instanceof LatencyAbc) {
      return ((LatencyAbc) this.abc).getFinalLatency();
    } else {
      return 0L;
    }
  }

  /**
   * Returns the number of operators in the current architecture that execute vertices.
   *
   * @return the nb used operators
   */
  public int getNbUsedOperators() {
    int nbUsedOperators = 0;
    for (final ComponentInstance o : DesignTools.getOperatorInstances(this.abc.getArchitecture())) {
      if (this.abc.getFinalCost(o) > 0) {
        nbUsedOperators++;
      }
    }
    return nbUsedOperators;
  }

  /**
   * Returns the number of operators with main type.
   *
   * @return the nb main type operators
   */
  public int getNbMainTypeOperators() {
    int nbMainTypeOperators = 0;
    final ComponentInstance mainOp = DesignTools.getComponentInstance(this.abc.getArchitecture(),
        this.scenario.getSimulationManager().getMainOperatorName());
    nbMainTypeOperators = DesignTools.getInstancesOfComponent(this.abc.getArchitecture(), mainOp.getComponent()).size();
    return nbMainTypeOperators;
  }

  /**
   * The load is the percentage of a processing resource used for the given algorithm.
   *
   * @param operator
   *          the operator
   * @return the load
   */
  public long getLoad(final ComponentInstance operator) {

    if (this.abc instanceof LatencyAbc) {
      return ((LatencyAbc) this.abc).getLoad(operator);
    } else {
      return 0L;
    }
  }

  /**
   * The memory is the sum of all buffers allocated by the mapping.
   *
   * @param operator
   *          the operator
   * @return the mem
   */
  public Integer getMem(final ComponentInstance operator) {

    int mem = 0;

    if (this.abc != null) {

      for (final DAGEdge e : this.abc.getDAG().edgeSet()) {
        final MapperDAGEdge me = (MapperDAGEdge) e;
        final MapperDAGVertex scr = (MapperDAGVertex) me.getSource();
        final MapperDAGVertex tgt = (MapperDAGVertex) me.getTarget();

        final PropertyBean comSrcBeans = scr.getPropertyBean();
        final PropertyBean comTgtBeans = tgt.getPropertyBean();
        final Object srcVtxTypeObj = comSrcBeans.getValue(ImplementationPropertyNames.Vertex_vertexType);
        final Object tgtVtxTypeObj = comTgtBeans.getValue(ImplementationPropertyNames.Vertex_vertexType);

        final String srcVtxType = srcVtxTypeObj.toString();
        final String tgtVtxType = tgtVtxTypeObj.toString();

        if (!VertexType.TYPE_TASK.equals(srcVtxType) || !VertexType.TYPE_TASK.equals(tgtVtxType)) {
          // Skip the edge if source or target is not a task
          continue;
        }

        if (!(me instanceof PrecedenceEdge)) {
          final ComponentInstance srcOp = scr.getEffectiveComponent();
          final ComponentInstance tgtOp = tgt.getEffectiveComponent();

          if (srcOp.getInstanceName().equals(operator.getInstanceName())
              || tgtOp.getInstanceName().equals(operator.getInstanceName())) {
            mem += me.getInit().getDataSize();
          }
        }
      }
    }

    return mem;

  }

  /**
   * Gets the scenario.
   *
   * @return the scenario
   */
  public PreesmScenario getScenario() {
    return this.scenario;
  }

  /**
   * Gets the params.
   *
   * @return the params
   */
  public Map<String, String> getParams() {
    return this.params;
  }

  /**
   * Gets the final time.
   *
   * @return the final time
   */
  public long getFinalTime() {
    return this.finalTime;
  }

  /**
   * Gets the abc.
   *
   * @return the abc
   */
  public IAbc getAbc() {
    return this.abc;
  }

  /**
   * Removes the send receive.
   *
   * @param localDag
   *          the local dag
   */
  public static void removeSendReceive(final MapperDAG localDag) {

    // Every send and receive vertices are removed
    final Set<DAGVertex> vset = new LinkedHashSet<>(localDag.vertexSet());
    for (final DAGVertex v : vset) {
      if ((v instanceof SendVertex) || (v instanceof ReceiveVertex)) {
        localDag.removeVertex(v);
      }
    }

  }
}
