/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2012 - 2014)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/

package org.ietr.preesm.mapper.abc.impl.latency;

import java.util.ArrayList;
import java.util.List;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.mapper.abc.AbcType;
import org.ietr.preesm.mapper.abc.edgescheduling.EdgeSchedType;
import org.ietr.preesm.mapper.abc.route.CommunicationRouter;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.model.special.PrecedenceEdgeAdder;
import org.ietr.preesm.mapper.params.AbcParameters;

// TODO: Auto-generated Javadoc
/**
 * The accurately timed ABC schedules edges and setup times.
 *
 * @author mpelcat
 */
public class AccuratelyTimedAbc extends LatencyAbc {

  /** The types. */
  List<Integer> types = null;

  /**
   * Constructor of the simulator from a "blank" implementation where every vertex has not been
   * mapped yet.
   *
   * @param params
   *          the params
   * @param dag
   *          the dag
   * @param archi
   *          the archi
   * @param abcType
   *          the abc type
   * @param scenario
   *          the scenario
   */
  public AccuratelyTimedAbc(final AbcParameters params, final MapperDAG dag, final Design archi,
      final AbcType abcType, final PreesmScenario scenario) {
    super(params, dag, archi, abcType, scenario);

    this.types = new ArrayList<>();
    this.types.add(CommunicationRouter.transferType);
    this.types.add(CommunicationRouter.overheadType);
    this.types.add(CommunicationRouter.involvementType);
    this.types.add(CommunicationRouter.synchroType);
  }

  /**
   * Called when a new vertex operator is set.
   *
   * @param vertex
   *          the vertex
   * @param updateRank
   *          the update rank
   */
  @Override
  protected void fireNewMappedVertex(final MapperDAGVertex vertex, final boolean updateRank) {

    super.fireNewMappedVertex(vertex, updateRank);

    final ComponentInstance effectiveOp = vertex.getEffectiveOperator();

    if (effectiveOp != DesignTools.NO_COMPONENT_INSTANCE) {
      new PrecedenceEdgeAdder(this.orderManager, this.implementation).scheduleVertex(vertex);
      this.comRouter.routeNewVertex(vertex, this.types);
    }
  }

  /**
   * Edge scheduling vertices are added. Thus useless edge costs are removed
   *
   * @param edge
   *          the new edge cost
   */
  @Override
  protected final void setEdgeCost(final MapperDAGEdge edge) {

    edge.getTiming().setCost(0);

    // Setting edge costs for special types
    // super.setEdgeCost(edge);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ietr.preesm.mapper.abc.impl.latency.LatencyAbc#getEdgeSchedType()
   */
  @Override
  public EdgeSchedType getEdgeSchedType() {
    return this.edgeScheduler.getEdgeSchedType();
  }
}
