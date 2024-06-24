/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2020)
 * Jonathan Piat [jpiat@laas.fr] (2011)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2012)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2008 - 2014)
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
package org.preesm.algorithm.mapper.ui.stats;

import org.preesm.algorithm.mapper.abc.impl.latency.LatencyAbc;
import org.preesm.algorithm.mapper.gantt.GanttData;
import org.preesm.algorithm.mapper.graphtransfo.ImplementationPropertyNames;
import org.preesm.algorithm.mapper.graphtransfo.VertexType;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.mapper.model.MapperDAGEdge;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.algorithm.mapper.model.special.InvolvementVertex;
import org.preesm.algorithm.mapper.model.special.OverheadVertex;
import org.preesm.algorithm.mapper.model.special.PrecedenceEdge;
import org.preesm.algorithm.mapper.model.special.TransferVertex;
import org.preesm.algorithm.model.PropertyBean;
import org.preesm.algorithm.model.dag.DAGEdge;
import org.preesm.algorithm.model.dag.DAGVertex;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.model.slam.ComponentInstance;

/**
 * Generating the statistics to be displayed in stat editor.
 *
 * @author mpelcat
 */
public class StatGeneratorAbc extends AbstractStatGenerator {

  private LatencyAbc abc = null;

  /**
   * Instantiates a new stat generator.
   *
   * @param abc
   *          the abc
   */
  public StatGeneratorAbc(final LatencyAbc abc) {
    super(abc.getArchitecture(), abc.getScenario());
    this.abc = abc;
    this.abc.updateFinalCosts();
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
    return this.abc.getBestLatency();
  }

  /**
   * The work is the sum of all task lengths excluding vertices added by the mapping.
   *
   * @return the DAG work length
   * @throws PreesmException
   *           the workflow exception
   */
  public long getDAGWorkLength() {

    long work = 0;
    final MapperDAG dag = this.abc.getDAG();

    final ComponentInstance mainOp = this.scenario.getSimulationInfo().getMainOperator();

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
   * Returns the number of operators in the current architecture that execute vertices.
   *
   * @return the nb used operators
   */
  public int getNbUsedOperators() {
    int nbUsedOperators = 0;
    for (final ComponentInstance o : architecture.getOperatorComponentInstances()) {
      if (this.abc.getFinalCost(o) > 0) {
        nbUsedOperators++;
      }
    }
    return nbUsedOperators;
  }

  /**
   * The memory is the sum of all buffers allocated by the mapping.
   *
   * @param operator
   *          the operator
   * @return the mem
   */
  public long getMem(final ComponentInstance operator) {

    long mem = 0;

    if (this.abc != null) {

      for (final DAGEdge e : this.abc.getDAG().edgeSet()) {
        final MapperDAGEdge me = (MapperDAGEdge) e;
        final MapperDAGVertex scr = (MapperDAGVertex) me.getSource();
        final MapperDAGVertex tgt = (MapperDAGVertex) me.getTarget();

        final PropertyBean comSrcBeans = scr.getPropertyBean();
        final PropertyBean comTgtBeans = tgt.getPropertyBean();
        final Object srcVtxTypeObj = comSrcBeans.getValue(ImplementationPropertyNames.VERTEX_VERTEX_TYPE);
        final Object tgtVtxTypeObj = comTgtBeans.getValue(ImplementationPropertyNames.VERTEX_VERTEX_TYPE);

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
   * The load is the percentage of a processing resource used for the given algorithm.
   *
   * @param operator
   *          the operator
   * @return the load
   */
  public long getLoad(final ComponentInstance operator) {
    return this.abc.getLoad(operator);
  }

  public long getFinalTime() {
    return this.abc.getFinalLatency();
  }

  public GanttData getGanttData() {
    return this.abc.getGanttData();
  }

}
