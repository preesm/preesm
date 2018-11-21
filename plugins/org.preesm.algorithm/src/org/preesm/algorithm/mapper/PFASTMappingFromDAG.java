/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
 * Jonathan Piat <jpiat@laas.fr> (2008 - 2011)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2017)
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
package org.preesm.algorithm.mapper;

import java.util.Map;
import java.util.logging.Level;
import org.preesm.algorithm.mapper.abc.impl.latency.LatencyAbc;
import org.preesm.algorithm.mapper.abc.taskscheduling.AbstractTaskSched;
import org.preesm.algorithm.mapper.algo.list.InitialLists;
import org.preesm.algorithm.mapper.algo.pfast.PFastAlgorithm;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.mapper.params.AbcParameters;
import org.preesm.algorithm.mapper.params.PFastAlgoParameters;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.slam.Design;
import org.preesm.scenario.PreesmScenario;

/**
 * PFAST is a parallel mapping/scheduling method based on list scheduling followed by a neighborhood search phase. It
 * was invented by Y-K Kwok.
 *
 * @author mwipliez
 * @author pmenuet
 */
public class PFASTMappingFromDAG extends AbstractMappingFromDAG {

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.AbstractMapping#getDefaultParameters()
   */
  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = super.getDefaultParameters();

    parameters.put("nodesMin", "5");
    parameters.put("procNumber", "1");
    parameters.put("displaySolutions", "false");
    parameters.put("fastTime", "100");
    parameters.put("fastLocalSearchTime", "10");
    parameters.put("fastNumber", "100");

    return parameters;
  }

  @Override
  protected LatencyAbc schedule(final Map<String, Object> outputs, final Map<String, String> parameters,
      final InitialLists initial, final PreesmScenario scenario, final AbcParameters abcParameters, final MapperDAG dag,
      final Design architecture, final AbstractTaskSched taskSched) {

    final LatencyAbc simu2 = LatencyAbc.getInstance(abcParameters, dag, architecture, scenario);

    PreesmLogger.getLogger().log(Level.INFO, "Mapping");

    final PFastAlgorithm pfastAlgorithm = new PFastAlgorithm();
    final PFastAlgoParameters pFastParams = new PFastAlgoParameters(parameters);
    final MapperDAG resdag = pfastAlgorithm.map(dag, architecture, scenario, initial, abcParameters, pFastParams, false,
        0, pFastParams.isDisplaySolutions(), null, taskSched);

    simu2.setDAG(resdag);
    PreesmLogger.getLogger().log(Level.INFO, "Mapping finished");

    // The transfers are reordered using the best found order during
    // scheduling
    simu2.reschedule(pfastAlgorithm.getBestTotalOrder());

    return simu2;
  }

}
