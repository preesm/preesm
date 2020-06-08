/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2015 - 2019) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2015)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2015)
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
package org.preesm.model.scenario.serialize;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.WorkspaceUtils;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.util.VertexPath;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ProcessingElement;

/**
 * Importing timings in a scenario from a csv file. task names are rows while operator types are columns
 *
 * @author jheulot
 */
public class CsvTimingParser {

  /** The scenario. */
  private Scenario scenario = null;

  /**
   * Instantiates a new csv timing parser.
   *
   * @param scenario
   *          the scenario
   */
  public CsvTimingParser(final Scenario scenario) {
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
   */
  public void parse(final String url, final List<ProcessingElement> opDefIds) {
    PreesmLogger.getLogger().log(Level.INFO,
        "Importing timings from a csv sheet. Non precised timings are kept unmodified.");

    final IWorkspace workspace = ResourcesPlugin.getWorkspace();

    WorkspaceUtils.updateWorkspace();

    final Path path = new Path(url);
    final IFile file = workspace.getRoot().getFile(path);
    try (final BufferedReader br = new BufferedReader(new InputStreamReader(file.getContents()))) {
      final Map<AbstractActor, Map<Component, String>> timings = new LinkedHashMap<>();
      String line;

      /* Read header */
      line = br.readLine();
      if (line != null) {
        final String[] opNames = line.split(";");
        if ((opNames.length <= 1) || !opNames[0].equals("Actors")) {
          PreesmLogger.getLogger().log(Level.WARNING,
              "Timing csv file must have an header line starting with \"Actors\"\nNothing done");
          return;
        }

        /* Parse the whole file to create the timings Map */
        while ((line = br.readLine()) != null) {
          processLine(timings, line, opNames);
        }

        parseTimings(timings, opDefIds);
      } else {
        throw new IllegalArgumentException("Given URL points to an empty file");
      }
    } catch (final IOException | CoreException e) {
      PreesmLogger.getLogger().log(Level.WARNING, "Could not par CSV timings.", e);
    }
  }

  private void processLine(final Map<AbstractActor, Map<Component, String>> timings, String lineToParse,
      final String[] opNames) {
    String line = lineToParse.trim();
    if (line.isEmpty()) {
      return;
    }
    final String[] cells = line.split(";");
    if (cells.length == opNames.length) {
      final Map<Component, String> timing = new LinkedHashMap<>();

      for (int i = 1; i < cells.length; i++) {
        final String vlnvName = opNames[i];
        final Component com = this.scenario.getDesign().getComponent(vlnvName);
        timing.put(com, cells[i]);
      }

      final String string = cells[0];
      final AbstractActor lookupActor = VertexPath.lookup(this.scenario.getAlgorithm(), string);
      if (lookupActor != null) {
        timings.put(lookupActor, timing);
      }
    } else {
      String errMessage = "Timing csv file has incorrect data: all rows have not the same number of columns.";
      PreesmLogger.getLogger().log(Level.SEVERE, errMessage);
      throw new PreesmRuntimeException(errMessage);
    }
  }

  /**
   * Parses the timings.
   *
   * @param timings
   *          the timings
   * @param opDefIds
   *          the op def ids
   * @throws CoreException
   *           the core exception
   */
  private void parseTimings(final Map<AbstractActor, Map<Component, String>> timings,
      final List<ProcessingElement> opDefIds) {
    // Depending on the type of SDF graph we process (IBSDF or PISDF), call
    // one or the other method
    final PiGraph currentGraph = scenario.getAlgorithm();
    parseTimingsForPISDFGraph(timings, currentGraph, opDefIds);
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
  private void parseTimingsForPISDFGraph(final Map<AbstractActor, Map<Component, String>> timings,
      final PiGraph currentGraph, final List<ProcessingElement> opDefIds) {

    // parse timings of non hierarchical actors of currentGraph
    currentGraph.getActorsWithRefinement().stream().filter(a -> !a.isHierarchical())
        .forEach(a -> parseTimingForVertex(timings, a, opDefIds));
    // parse timings of all direct subgraphs
    currentGraph.getChildrenGraphs().stream().forEach(g -> parseTimingsForPISDFGraph(timings, g, opDefIds));
  }

  /**
   * Parses the timing for vertex.
   *
   * @param timings
   *          the timings
   * @param actor
   *          the vertex name
   * @param componentList
   *          the op def ids
   */
  private void parseTimingForVertex(final Map<AbstractActor, Map<Component, String>> timings, final AbstractActor actor,
      final List<ProcessingElement> componentList) {
    // For each kind of processing elements, we look for a timing for given vertex
    for (final Component component : componentList) {
      if (component != null && actor != null) {
        // Get the timing we are looking for
        try {
          final String expression = timings.get(actor).get(component);

          this.scenario.getTimings().setExecutionTime(actor, component, expression);

          final String msg = "Importing timing: " + actor.getVertexPath() + " on " + component.getVlnv().getName()
              + " takes " + expression;
          PreesmLogger.getLogger().log(Level.INFO, msg);

        } catch (final Exception e) {
          PreesmLogger.getLogger().log(Level.INFO, "Cannot retreive timing for ({0}, {1})",
              new Object[] { actor, component });
        }
      }
    }
  }
}
