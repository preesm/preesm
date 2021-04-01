/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2015 - 2019) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2015)
 * Daniel Madroñal [daniel.madronal@upm.es] (2019)
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
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.util.VertexPath;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.ProcessingElement;

/**
 * Importing timings in a scenario from a papify-output folder.
 *
 * @author dmadronal
 */
public class PapifyOutputTimingParser {

  /** The scenario. */
  private Scenario scenario = null;

  /**
   * Instantiates a new papify timing parser.
   *
   * @param scenario
   *          the scenario
   */
  public PapifyOutputTimingParser(final Scenario scenario) {
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
        "Importing timings from a papify-output folder. Non precised timings are kept unmodified.");

    final Map<AbstractActor, Map<Component, String>> timings = new LinkedHashMap<>();
    final IFolder iFolder = ResourcesPlugin.getWorkspace().getRoot().getFolder(new Path(url));

    final String fullPath = iFolder.getRawLocation().toOSString();
    final File folder = new File(fullPath);
    final File[] fList = folder.listFiles();

    for (File file : fList) {
      parseFile(url, opDefIds, timings, file);
    }
    if (!timings.isEmpty()) {
      parseTimings(timings, opDefIds);
    }
  }

  private void parseFile(final String url, final List<ProcessingElement> opDefIds,
      final Map<AbstractActor, Map<Component, String>> timings, File file) {
    final String filePath = url.concat("//").concat(file.getName());
    final IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath));
    Map<String, Integer> timingsParsed = new LinkedHashMap<>();
    Map<String, Integer> times = new LinkedHashMap<>();
    try (final BufferedReader br = new BufferedReader(new InputStreamReader(iFile.getContents()))) {
      String line = br.readLine();
      String actorName = "";
      if (line != null) {
        while ((line = br.readLine()) != null) {
          actorName = processLine(timingsParsed, times, line);
        }
        final AbstractActor lookupActor = VertexPath.lookup(this.scenario.getAlgorithm(), actorName);
        if (lookupActor != null) {
          applyOnActor(opDefIds, timings, timingsParsed, times, lookupActor);
        }
      }
    } catch (final IOException | CoreException e) {
      PreesmLogger.getLogger().log(Level.WARNING, "Could not parse Papify output timings " + filePath, e);
    }
  }

  private void applyOnActor(final List<ProcessingElement> opDefIds,
      final Map<AbstractActor, Map<Component, String>> timings, Map<String, Integer> timingsParsed,
      Map<String, Integer> times, final AbstractActor lookupActor) {
    final Map<Component, String> timing = new LinkedHashMap<>();
    for (Component comp : opDefIds) {
      int averageTiming = 0;
      int totalTime = 0;
      int totalTimes = 0;
      for (ComponentInstance compInstance : comp.getInstances()) {
        if (timingsParsed.containsKey(compInstance.getInstanceName())) {
          totalTime = totalTime + timingsParsed.get(compInstance.getInstanceName());
          totalTimes = totalTimes + times.get(compInstance.getInstanceName());
        }
      }
      if (totalTimes != 0) {
        averageTiming = totalTime / totalTimes;
        timing.put(comp, Integer.toString(averageTiming));
      }
    }
    if (!timing.isEmpty()) {
      timings.put(lookupActor, timing);
    }
  }

  private String processLine(Map<String, Integer> timingsParsed, Map<String, Integer> times, String line) {
    String actorName;
    final String[] cells = line.split(",");
    String coreName = cells[0];
    actorName = cells[1];
    int tinit = Integer.parseInt(cells[2]);
    int tend = Integer.parseInt(cells[3]);
    if (tinit != 0 && tend != 0) {
      if (!timingsParsed.containsKey(coreName)) {
        timingsParsed.put(coreName, tend - tinit);
        times.put(coreName, 1);
      } else {
        timingsParsed.put(coreName, timingsParsed.get(coreName) + (tend - tinit));
        times.put(coreName, times.get(coreName) + 1);
      }
    }
    return actorName;
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
