/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2015)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2008 - 2012)
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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.preesm.algorithm.mapper.abc.impl.latency.LatencyAbc;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.scenario.Scenario;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * Transform class that can be called in workflow. The transform method displays the gantt chart of the given mapped dag
 *
 * @author mpelcat
 */
@PreesmTask(id = "org.ietr.preesm.plugin.mapper.plot", name = "Gantt Display", category = "Analysis",

    inputs = { @Port(name = "ABC", type = LatencyAbc.class), @Port(name = "scenario", type = Scenario.class) },

    shortDescription = "Displays the result of a mapping/scheduling algorithm as a Gantt diagram.",

    description = "This task generates SDF3 code modeling the given SDF graph. SDF modeling in SDF3 follow the "
        + "specification introduced by Stuijk et al. in [1].\n\n"
        + "Known Limitations: Here is a list of known limitations of the SDF3 importation process: Only SDF"
        + " graphs can be imported, Actors of the SDF cannot be implemented on more than one processor type,"
        + " Timings cannot depend on parameters since SDF3 does not support parameterized SDF.",

    seeAlso = { "**Speedup assessment chart**: Maxime Pelcat. Prototypage Rapide et Génération de Code pour DSP Multi-"
        + "Coeurs Appliqués à la Couche Physique des Stations de Base 3GPP LTE. PhD thesis, INSA de Rennes, 2010." })
public class StatEditorTransform extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {

    final LatencyAbc abc = (LatencyAbc) inputs.get("ABC");
    final Scenario scenario = (Scenario) inputs.get("scenario");

    final IEditorInput input = new StatEditorInput(abc, scenario, parameters);

    // Check if the workflow is running in command line mode
    try {
      // Run statistic editor
      PlatformUI.getWorkbench().getDisplay().asyncExec(new EditorRunnable(input));
    } catch (final IllegalStateException e) {
      PreesmLogger.getLogger().log(Level.INFO, "Gantt display is impossible in this context."
          + " Ignore this log entry if you are running the command line version of Preesm.");
    }

    return new LinkedHashMap<>();
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    return Collections.emptyMap();
  }

  @Override
  public String monitorMessage() {
    return "Plots the Gantt chart";
  }

}
