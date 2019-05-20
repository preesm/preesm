/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2016 - 2019) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2019)
 * Julien Hascoet <jhascoet@kalray.eu> (2016 - 2017)
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
package org.preesm.algorithm.clustering;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.model.scenario.PreesmScenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 *
 *
 */
@PreesmTask(id = "org.ietr.preesm.Clustering", name = "Clustering",

    inputs = { @Port(name = "SDF", type = SDFGraph.class), @Port(name = "scenario", type = PreesmScenario.class),
        @Port(name = "architecture", type = Design.class) },

    outputs = { @Port(name = "SDF", type = SDFGraph.class) },

    description = "Workflow task responsible for clustering hierarchical actors."

)
public class Clustering extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) throws PreesmException {
    final Map<String, Object> outputs = new LinkedHashMap<>();
    final SDFGraph algorithm = (SDFGraph) inputs.get("SDF");
    final PreesmScenario scenario = (PreesmScenario) inputs.get("scenario");
    final Design architecture = (Design) inputs.get("architecture");
    final HSDFBuildLoops loopBuilder = new HSDFBuildLoops(scenario, architecture);
    outputs.put("SDF", loopBuilder.execute(algorithm));
    return outputs;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    return Collections.emptyMap();
  }

  @Override
  public String monitorMessage() {
    return "Clustering";
  }

}
