/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2015 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2015)
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
package org.ietr.preesm.core.scenario.serialize;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.ietr.dftools.algorithm.importer.InvalidModelException;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.Activator;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.Refinement;
import org.ietr.preesm.experiment.model.pimm.serialize.PiParser;

// TODO: Auto-generated Javadoc
/**
 * Importing timings in a scenario from a csv file. task names are rows while operator types are columns
 *
 * @author jheulot
 */
public class CsvTimingParser {

  /** The scenario. */
  private PreesmScenario scenario = null;

  /**
   * Instantiates a new csv timing parser.
   *
   * @param scenario
   *          the scenario
   */
  public CsvTimingParser(final PreesmScenario scenario) {
    super();
    this.scenario = scenario;
  }

  /**
   * Parses the.
   *
   * @param url
   *          the url
   * @param opDefIds
   *          the op def ids
   * @throws InvalidModelException
   *           the invalid model exception
   * @throws FileNotFoundException
   *           the file not found exception
   */
  public void parse(final String url, final Set<String> opDefIds) throws InvalidModelException, FileNotFoundException {
    WorkflowLogger.getLogger().log(Level.INFO, "Importing timings from a csv sheet. Non precised timings are kept unmodified.");

    final IWorkspace workspace = ResourcesPlugin.getWorkspace();

    Activator.updateWorkspace();

    final Path path = new Path(url);
    final IFile file = workspace.getRoot().getFile(path);
    try {
      final Map<String, Map<String, String>> timings = new LinkedHashMap<>();
      final BufferedReader br = new BufferedReader(new InputStreamReader(file.getContents()));

      String line;

      /* Read header */
      line = br.readLine();
      if (line != null) {
        final String[] opNames = line.split(",");
        if ((opNames.length <= 1) || !opNames[0].equals("Actors")) {
          WorkflowLogger.getLogger().log(Level.WARNING, "Timing csv file must have an header line starting with \"Actors\"\nNothing done");
          return;
        }

        /* Parse the whole file to create the timings Map */
        while ((line = br.readLine()) != null) {
          final String[] cells = line.split(",");
          if (cells.length > 1) {
            final Map<String, String> timing = new LinkedHashMap<>();

            for (int i = 1; i < cells.length; i++) {
              timing.put(opNames[i], cells[i]);
            }

            timings.put(cells[0], timing);
          }
        }

        parseTimings(timings, opDefIds);
      } else {
        throw new IllegalArgumentException("Given URL points to an empty file");
      }
    } catch (final IOException e) {
      e.printStackTrace();
    } catch (final CoreException e) {
      e.printStackTrace();
    }
  }

  /**
   * Parses the timings.
   *
   * @param timings
   *          the timings
   * @param opDefIds
   *          the op def ids
   * @throws FileNotFoundException
   *           the file not found exception
   * @throws InvalidModelException
   *           the invalid model exception
   * @throws CoreException
   *           the core exception
   */
  private void parseTimings(final Map<String, Map<String, String>> timings, final Set<String> opDefIds)
      throws FileNotFoundException, InvalidModelException, CoreException {
    // Depending on the type of SDF graph we process (IBSDF or PISDF), call
    // one or the other method
    if (this.scenario.isIBSDFScenario()) {
      throw new InvalidModelException();
    } else if (this.scenario.isPISDFScenario()) {
      final PiGraph currentGraph = PiParser.getPiGraph(this.scenario.getAlgorithmURL());
      parseTimingsForPISDFGraph(timings, currentGraph, opDefIds);
    }

  }

  /**
   * Parses the timings for PISDF graph.
   *
   * @param timings
   *          the timings
   * @param currentGraph
   *          the current graph
   * @param opDefIds
   *          the op def ids
   */
  private void parseTimingsForPISDFGraph(final Map<String, Map<String, String>> timings, final PiGraph currentGraph, final Set<String> opDefIds) {
    // Each of the vertices of the graph is either itself a graph (hierarchical vertex), in which case we call recursively this method
    // a standard actor, in which case we parser its timing; or a special
    // vertex, in which case we do nothing
    for (final AbstractActor vertex : currentGraph.getActors()) {
      // Handle connected graphs from hierarchical vertices
      if (vertex instanceof PiGraph) {
        parseTimingsForPISDFGraph(timings, (PiGraph) vertex, opDefIds);
      } else if (vertex instanceof Actor) {
        final Actor actor = (Actor) vertex;

        // Handle unconnected graphs from hierarchical vertices
        final Refinement refinement = actor.getRefinement();
        AbstractActor subgraph = null;
        if (refinement != null) {
          subgraph = refinement.getAbstractActor();
        }

        if ((subgraph != null) && (subgraph instanceof PiGraph)) {
          parseTimingsForPISDFGraph(timings, (PiGraph) subgraph, opDefIds);
        } else {
          // If the actor is not hierarchical, parse its timing
          parseTimingForVertex(timings, vertex.getName(), opDefIds);
        }
      }
    }

  }

  /**
   * Parses the timing for vertex.
   *
   * @param timings
   *          the timings
   * @param vertexName
   *          the vertex name
   * @param opDefIds
   *          the op def ids
   */
  private void parseTimingForVertex(final Map<String, Map<String, String>> timings, final String vertexName, final Set<String> opDefIds) {
    // For each kind of processing elements, we look for a timing for given vertex
    for (final String opDefId : opDefIds) {
      if (!opDefId.isEmpty() && !vertexName.isEmpty()) {
        // Get the timing we are looking for
        try {
          final String expression = timings.get(vertexName).get(opDefId);
          final Timing timing = new Timing(opDefId, vertexName, expression);

          this.scenario.getTimingManager().addTiming(timing);

          WorkflowLogger.getLogger().log(Level.INFO, "Importing timing: " + timing.toString());

        } catch (final Exception e) {
          WorkflowLogger.getLogger().log(Level.INFO, "Cannot retreive timing for (" + vertexName + ", " + opDefId + ")");
        }
      }
    }
  }
}
