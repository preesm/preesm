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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.preesm.model.slam.ComponentInstance;

/**
 * Importing energy in a scenario from a papify generated csv file. task names are rows while operators are columns
 *
 * @author dmadronal
 */
public class PapifyEnergyParser {

  /** The scenario. */
  private Scenario scenario = null;

  /**
   * Instantiates a new papify energy parser.
   *
   * @param scenario
   *          the scenario
   */
  public PapifyEnergyParser(final Scenario scenario) {
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
  public void parse(final String url, final List<Component> opDefIds) {
    PreesmLogger.getLogger().log(Level.INFO,
        "Importing energy from a papify generated csv sheet. Non precised energies are kept unmodified.");

    final IWorkspace workspace = ResourcesPlugin.getWorkspace();

    WorkspaceUtils.updateWorkspace();

    final Path path = new Path(url);
    final IFile file = workspace.getRoot().getFile(path);
    try (final BufferedReader br = new BufferedReader(new InputStreamReader(file.getContents()))) {
      final Map<AbstractActor, Map<ComponentInstance, String>> energyInstances = new LinkedHashMap<>();
      final Map<AbstractActor, Map<Component, String>> energies = new LinkedHashMap<>();

      String line;

      /* Read header */
      line = br.readLine();
      if (line != null) {
        final String[] opNames = line.split(",");
        if (opNames.length <= 1) {
          PreesmLogger.getLogger().log(Level.WARNING, "Energy papify csv file must have values inside");
          return;
        }

        /* Parse the whole file to create the energies Map */
        while ((line = br.readLine()) != null) {
          processLine(energyInstances, line, opNames);
        }
        for (Entry<AbstractActor, Map<ComponentInstance, String>> actorEnergies : energyInstances.entrySet()) {
          storeEnergies(energies, actorEnergies);
        }

        parseEnergies(energies, opDefIds);
      } else {
        throw new IllegalArgumentException("Given URL points to an empty file");
      }
    } catch (final IOException | CoreException e) {
      PreesmLogger.getLogger().log(Level.WARNING, "Could not parse Papify energy", e);
    }
  }

  private void storeEnergies(final Map<AbstractActor, Map<Component, String>> energies,
      Entry<AbstractActor, Map<ComponentInstance, String>> actorEnergies) {
    final Map<Component, String> energy = new LinkedHashMap<>();
    final Map<Component, Integer> energyTimes = new LinkedHashMap<>();
    for (Entry<ComponentInstance, String> energyEntry : actorEnergies.getValue().entrySet()) {
      if (!energyEntry.getValue().equals(" ")) {
        String peType = energyEntry.getKey().getComponent().getVlnv().getName();
        Component comp = this.scenario.getDesign().getComponent(peType);
        if (!energy.containsKey(comp)) {
          energy.put(comp, energyEntry.getValue());
          energyTimes.put(comp, 1);
        } else {
          double valueStored = Double.parseDouble(energy.get(comp));
          double valueNew = Double.parseDouble(energyEntry.getValue());
          String addedValue = Double.toString(valueStored + valueNew);
          energy.put(comp, addedValue);
          energyTimes.put(comp, energyTimes.get(comp) + 1);
        }
      }
    }
    for (Entry<Component, String> energyToAverage : energy.entrySet()) {
      double valueStored = Double.parseDouble(energyToAverage.getValue());
      double valueAveraged = valueStored / energyTimes.get(energyToAverage.getKey());
      energy.put(energyToAverage.getKey(), Double.toString(valueAveraged));
    }
    energies.put(actorEnergies.getKey(), energy);
  }

  private void processLine(final Map<AbstractActor, Map<ComponentInstance, String>> energyInstances, String line,
      final String[] opNames) {
    final String[] cells = line.split(",");
    if (cells.length == opNames.length) {
      final Map<ComponentInstance, String> energy = new LinkedHashMap<>();

      for (int i = 1; i < cells.length; i++) {
        final String peName = opNames[i];
        final ComponentInstance com = this.scenario.getDesign().getComponentInstance(peName);
        energy.put(com, cells[i]);
      }

      final String string = cells[0];
      final AbstractActor lookupActor = VertexPath.lookup(this.scenario.getAlgorithm(), string);
      if (lookupActor != null) {
        energyInstances.put(lookupActor, energy);
      }
    } else {
      String errMessage = "Papify auto-generated energies csv file has incorrect data: "
          + "all rows have not the same number of columns.";
      PreesmLogger.getLogger().log(Level.SEVERE, errMessage);
      throw new PreesmRuntimeException(errMessage);
    }
  }

  /**
   * Parses the energies.
   *
   * @param energies
   *          the energies
   * @param opDefIds
   *          the op def ids
   * @throws CoreException
   *           the core exception
   */
  private void parseEnergies(final Map<AbstractActor, Map<Component, String>> energies,
      final List<Component> opDefIds) {
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
      final PiGraph currentGraph, final List<Component> opDefIds) {

    // parse energies of non hierarchical actors of currentGraph
    currentGraph.getActorsWithRefinement().stream().filter(a -> !a.isHierarchical())
        .forEach(a -> parseEnergyForVertex(energies, a, opDefIds));
    // parse energies of all direct subgraphs
    currentGraph.getChildrenGraphs().stream().forEach(g -> parseEnergiesForPISDFGraph(energies, g, opDefIds));
  }

  /**
   * Parses the energies for vertex.
   *
   * @param energies
   *          the energies
   * @param actor
   *          the vertex name
   * @param componentList
   *          the op def ids
   */
  private void parseEnergyForVertex(final Map<AbstractActor, Map<Component, String>> energies,
      final AbstractActor actor, final List<Component> componentList) {
    // For each kind of processing elements, we look for a energies for given vertex
    for (final Component component : componentList) {
      if (component != null && actor != null) {
        // Get the energies we are looking for
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
