/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2015 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019 - 2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2015)
 * Daniel Madroñal [daniel.madronal@upm.es] (2019)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2015 - 2020)
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EMap;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.util.VertexPath;
import org.preesm.model.scenario.PapiEvent;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.ProcessingElement;

/**
 * Importing energy in a scenario from a papify-output folder.
 *
 * @author dmadronal
 */
public class PapifyOutputEnergyParser {

  /** The scenario. */
  private Scenario scenario = null;

  /**
   * Instantiates a new papify energy parser.
   *
   * @param scenario
   *          the scenario
   */
  public PapifyOutputEnergyParser(final Scenario scenario) {
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
        "Importing energies from a papify-output folder. Non precised energies are kept unmodified.");

    final Map<AbstractActor, Map<Component, String>> energies = new LinkedHashMap<>();
    final IFolder iFolder = ResourcesPlugin.getWorkspace().getRoot().getFolder(new Path(url));

    final String fullPath = iFolder.getRawLocation().toOSString();
    final File folder = new File(fullPath);
    final File[] fList = folder.listFiles();

    final Map<String, EMap<PapiEvent, Double>> coreModels = readKPI(opDefIds);

    for (File file : fList) {
      readFile(url, opDefIds, energies, coreModels, file);
    }
    if (!energies.isEmpty()) {
      parseEnergies(energies, opDefIds);
    }
  }

  private void readFile(final String url, final List<ProcessingElement> opDefIds,
      final Map<AbstractActor, Map<Component, String>> energies, final Map<String, EMap<PapiEvent, Double>> coreModels,
      File file) {
    final String filePath = url.concat("//").concat(file.getName());
    final IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath));
    Map<String, Double> energiesParsed = new LinkedHashMap<>();
    Map<String, Integer> times = new LinkedHashMap<>();
    Set<String> coresParseable = new LinkedHashSet<>();
    try (final BufferedReader br = new BufferedReader(new InputStreamReader(iFile.getContents()))) {
      processFile(opDefIds, energies, coreModels, energiesParsed, times, coresParseable, br);
    } catch (final IOException | CoreException e) {
      PreesmLogger.getLogger().log(Level.WARNING, "Could not read " + filePath, e);
    }
  }

  private void processFile(final List<ProcessingElement> opDefIds,
      final Map<AbstractActor, Map<Component, String>> energies, final Map<String, EMap<PapiEvent, Double>> coreModels,
      Map<String, Double> energiesParsed, Map<String, Integer> times, Set<String> coresParseable,
      final BufferedReader br) throws IOException {
    String line = br.readLine();
    final String[] cellsInit = line.split(",");
    int totalCells = cellsInit.length;
    if (totalCells > 4) {
      Map<PapiEvent, Integer> papiEventsFileColumn = new LinkedHashMap<>();
      for (int i = 4; i < cellsInit.length; i++) {
        PapiEvent papiEvent = this.scenario.getPapifyConfig().getEventByName(cellsInit[i]);
        papiEventsFileColumn.put(papiEvent, i);
      }
      addParsedCores(coreModels, coresParseable, papiEventsFileColumn);
      String actorName = "";
      while ((line = br.readLine()) != null) {
        final String[] cells = line.split(",");
        if (totalCells == cells.length) {
          actorName = processLine(coreModels, energiesParsed, times, coresParseable, papiEventsFileColumn, cells);
        }
      }
      final AbstractActor lookupActor = VertexPath.lookup(this.scenario.getAlgorithm(), actorName);
      if (lookupActor != null) {
        applyOnActor(opDefIds, energies, energiesParsed, times, lookupActor);
      }
    }
  }

  private String processLine(final Map<String, EMap<PapiEvent, Double>> coreModels, Map<String, Double> energiesParsed,
      Map<String, Integer> times, Set<String> coresParseable, Map<PapiEvent, Integer> papiEventsFileColumn,
      final String[] cells) {
    String actorName;
    String coreName = cells[0];
    actorName = cells[1];
    if (coresParseable.contains(coreName)) {
      EMap<PapiEvent, Double> coreModel = coreModels.get(coreName);
      double energyEstimated = 0.0;
      for (Entry<PapiEvent, Double> modelParam : coreModel.entrySet()) {
        int columnInFile = papiEventsFileColumn.get(modelParam.getKey());
        energyEstimated = energyEstimated + modelParam.getValue() * Double.parseDouble(cells[columnInFile]);
      }
      if (!energiesParsed.containsKey(coreName)) {
        energiesParsed.put(coreName, energyEstimated);
        times.put(coreName, 1);
      } else {
        energiesParsed.put(coreName, energiesParsed.get(coreName) + (energyEstimated));
        times.put(coreName, times.get(coreName) + 1);
      }
    }
    return actorName;
  }

  private void applyOnActor(final List<ProcessingElement> opDefIds,
      final Map<AbstractActor, Map<Component, String>> energies, Map<String, Double> energiesParsed,
      Map<String, Integer> times, final AbstractActor lookupActor) {
    final Map<Component, String> energy = new LinkedHashMap<>();
    for (Component comp : opDefIds) {
      double averageEnergy = 0;
      double totalTime = 0;
      int totalTimes = 0;
      for (ComponentInstance compInstance : comp.getInstances()) {
        if (energiesParsed.containsKey(compInstance.getInstanceName())) {
          totalTime = totalTime + energiesParsed.get(compInstance.getInstanceName());
          totalTimes = totalTimes + times.get(compInstance.getInstanceName());
        }
      }
      if (totalTimes != 0) {
        averageEnergy = totalTime / totalTimes;
        energy.put(comp, Double.toString(averageEnergy));
      }
    }
    if (!energy.isEmpty()) {
      energies.put(lookupActor, energy);
    }
  }

  private void addParsedCores(final Map<String, EMap<PapiEvent, Double>> coreModels, Set<String> coresParseable,
      Map<PapiEvent, Integer> papiEventsFileColumn) {
    for (Entry<String, EMap<PapiEvent, Double>> coreModel : coreModels.entrySet()) {
      Set<PapiEvent> papiEventsModel = coreModel.getValue().keySet();
      if (papiEventsFileColumn.keySet().containsAll(papiEventsModel)) {
        coresParseable.add(coreModel.getKey());
      }
    }
  }

  private Map<String, EMap<PapiEvent, Double>> readKPI(final List<ProcessingElement> opDefIds) {
    final Map<String, EMap<PapiEvent, Double>> coreModels = new LinkedHashMap<>();
    for (Component comp : opDefIds) {
      for (ComponentInstance compInstance : comp.getInstances()) {
        coreModels.put(compInstance.getInstanceName(),
            this.scenario.getPapifyConfig().getPapifyEnergyKPIModels().get(comp));
      }
    }
    return coreModels;
  }

  /**
   * Parses the energy.
   *
   * @param energies
   *          the energies
   * @param opDefIds
   *          the op def ids
   * @throws CoreException
   *           the core exception
   */
  private void parseEnergies(final Map<AbstractActor, Map<Component, String>> energies,
      final List<ProcessingElement> opDefIds) {
    // Depending on the type of SDF graph we process (IBSDF or PISDF), call
    // one or the other method
    final PiGraph currentGraph = scenario.getAlgorithm();
    parseEnergiesForPISDFGraph(energies, currentGraph, opDefIds);
  }

  /**
   * Parses the energies for PISDF graph.
   *
   * @param energies
   *          the energies
   * @param currentGraph
   *          the current graph
   * @param opDefIds
   *          the op def ids
   */
  private void parseEnergiesForPISDFGraph(final Map<AbstractActor, Map<Component, String>> energies,
      final PiGraph currentGraph, final List<ProcessingElement> opDefIds) {

    // parse energies of non hierarchical actors of currentGraph
    currentGraph.getActorsWithRefinement().stream().filter(a -> !a.isHierarchical())
        .forEach(a -> parseEnergyForVertex(energies, a, opDefIds));
    // parse energies of all direct subgraphs
    currentGraph.getChildrenGraphs().stream().forEach(g -> parseEnergiesForPISDFGraph(energies, g, opDefIds));
  }

  /**
   * Parses the energy for vertex.
   *
   * @param energies
   *          the energies
   * @param actor
   *          the vertex name
   * @param componentList
   *          the op def ids
   */
  private void parseEnergyForVertex(final Map<AbstractActor, Map<Component, String>> energies,
      final AbstractActor actor, final List<ProcessingElement> componentList) {
    // For each kind of processing elements, we look for a energy for given vertex
    for (final Component component : componentList) {
      if (component != null && actor != null) {
        // Get the energy we are looking for
        try {
          final String expression = energies.get(actor).get(component);

          this.scenario.getEnergyConfig().setActorPeEnergy(actor, component, expression);

          final String msg = "Importing energy: " + actor.getVertexPath() + " on " + component.getVlnv().getName()
              + " takes " + expression;
          PreesmLogger.getLogger().log(Level.INFO, msg);

        } catch (final Exception e) {
          PreesmLogger.getLogger().log(Level.INFO, "Cannot retreive energy for ({0}, {1})",
              new Object[] { actor, component });
        }
      }
    }
  }
}
