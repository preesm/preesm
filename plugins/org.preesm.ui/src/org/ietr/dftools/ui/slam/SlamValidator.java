/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011 - 2013)
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
/**
 *
 */
package org.ietr.dftools.ui.slam;

import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.ietr.dftools.graphiti.model.Edge;
import org.ietr.dftools.graphiti.model.Graph;
import org.ietr.dftools.graphiti.model.IValidator;
import org.ietr.dftools.graphiti.model.Vertex;
import org.ietr.dftools.ui.DFToolsUIException;

/**
 * Validating the System-Level Architecture Model.
 *
 * @author mpelcat
 */
public final class SlamValidator implements IValidator {

  /**
   * Validates the S-LAM graph.
   *
   * @param graph
   *          graph to validate
   * @param file
   *          file containing the serialized graph
   * @return true, if successful
   */
  @Override
  public boolean validate(final Graph graph, final IFile file) {
    boolean valid = true;

    valid &= validateGraph(graph, file);
    valid &= validateEnablerEdges(graph, file);
    valid &= validateEdgePorts(graph, file);
    valid &= validateDataLinks(graph, file);
    valid &= validateComNodes(graph, file);
    valid &= validateDmas(graph, file);
    valid &= validateMems(graph, file);
    valid &= validateControlLinks(graph, file);
    valid &= validateHierarchicalPorts(graph, file);
    valid &= validateHierarchicalConnections(graph, file);
    valid &= validateComponentInstances(graph, file);

    return valid;
  }

  /**
   * A graph should have VLNV data.
   *
   * @param graph
   *          the graph
   * @param file
   *          the file
   * @return true, if successful
   */
  private boolean validateGraph(final Graph graph, final IFile file) {

    boolean valid = true;
    boolean lackVLNVElement = false;

    if (graph.getValue("vendor") == null) {
      graph.setValue("vendor", "");
      lackVLNVElement = true;
    }

    if (graph.getValue("library") == null) {
      graph.setValue("library", "");
      lackVLNVElement = true;
    }

    if ((graph.getValue("name") == null) || graph.getValue("name").equals("")) {
      if ((file.getName() != null) && (file.getName().lastIndexOf('.') > 0)) {
        graph.setValue("name", file.getName().substring(0, file.getName().lastIndexOf('.')));
      }
      lackVLNVElement = true;
    }

    if (graph.getValue("version") == null) {
      graph.setValue("version", "");
      lackVLNVElement = true;
    }

    if (lackVLNVElement) {
      createMarker(file, "A graph should have VLNV data. Default values set", (String) graph.getValue("id"),
          IMarker.PROBLEM, IMarker.SEVERITY_WARNING);
      valid = false;
    }

    return valid;
  }

  /**
   * An enabler must at least receive a data link.
   *
   * @param graph
   *          the graph
   * @param file
   *          the file
   * @return true, if successful
   */
  private boolean validateEnablerEdges(final Graph graph, final IFile file) {

    boolean valid = true;
    boolean hasDataLink = false;

    for (final Vertex v : graph.vertexSet()) {
      hasDataLink = false;

      final String type = v.getType().getName();
      if (type.equals("Mem") || type.equals("Dma")) {
        for (final Edge e : graph.incomingEdgesOf(v)) {
          final String eType = e.getType().getName();
          if (eType.contains("DataLink")) {
            hasDataLink = true;
          }
        }

        for (final Edge e : graph.outgoingEdgesOf(v)) {
          final String eType = e.getType().getName();
          if (eType.contains("DataLink")) {
            hasDataLink = true;
          }
        }

        if (!hasDataLink) {
          createMarker(file, "An enabler (Mem or Dma) must at least receive a data link", (String) v.getValue("id"),
              IMarker.PROBLEM, IMarker.SEVERITY_ERROR);
          valid = false;
        }
      }
    }

    return valid;
  }

  /**
   * Each edge must have port names.
   *
   * @param graph
   *          the graph
   * @param file
   *          the file
   * @return true, if successful
   */
  private boolean validateEdgePorts(final Graph graph, final IFile file) {
    Boolean valid = true;
    Boolean hasPortNames = true;
    for (final Edge e : graph.edgeSet()) {
      if (!e.getType().getName().equals("hierConnection")) {
        hasPortNames = true;
        final String sourcePort = (String) e.getValue("source port");
        final String targetPort = (String) e.getValue("target port");

        if ((sourcePort == null) || sourcePort.equals("")) {
          hasPortNames = false;
        }

        if ((targetPort == null) || targetPort.equals("")) {
          hasPortNames = false;
        }

        if (!hasPortNames) {
          createMarker(file, "Each link must have source and target port names.",
              (String) e.getSource().getValue("id") + "->" + (String) e.getTarget().getValue("id"), IMarker.PROBLEM,
              IMarker.SEVERITY_ERROR);
          valid = false;
        }
      }
    }

    return valid;
  }

  /**
   * Each data link must connect a communication node to a node of any type. A link without a communication node (either
   * parallel or with contention) is not valid
   *
   * @param graph
   *          the graph
   * @param file
   *          the file
   * @return true, if successful
   */
  private boolean validateDataLinks(final Graph graph, final IFile file) {
    Boolean valid = true;
    Boolean hasComNode = false;
    for (final Edge e : graph.edgeSet()) {
      if (e.getType().getName().contains("DataLink")) {
        hasComNode = false;

        if ((e.getSource() != null) && e.getSource().getType().getName().contains("ComNode")) {
          hasComNode = true;
        }

        if ((e.getTarget() != null) && e.getTarget().getType().getName().contains("ComNode")) {
          hasComNode = true;
        }

        if (!hasComNode) {
          createMarker(file, "Each data link must have at least one ComNode in its source/target components.",
              (String) e.getSource().getValue("id") + "->" + (String) e.getTarget().getValue("id"), IMarker.PROBLEM,
              IMarker.SEVERITY_ERROR);
          valid = false;
        }
      }
    }

    return valid;
  }

  /**
   * A Communication Node must specify a speed.
   *
   * @param graph
   *          the graph
   * @param file
   *          the file
   * @return true, if successful
   */
  private boolean validateComNodes(final Graph graph, final IFile file) {

    boolean valid = true;
    boolean hasSpeed = false;

    for (final Vertex v : graph.vertexSet()) {
      hasSpeed = false;

      final String type = v.getType().getName();
      if (type.contains("ComNode")) {

        final String speed = (String) v.getValue("speed");
        if ((speed != null) && !speed.equals("") && (Float.valueOf(speed) > 0)) {
          hasSpeed = true;
        }

        if (!hasSpeed) {
          createMarker(file, "A ComNode must specify a non-zero float-valued speed.", (String) v.getValue("id"),
              IMarker.PROBLEM, IMarker.SEVERITY_ERROR);
          valid = false;
        }
      }
    }

    return valid;
  }

  /**
   * A Dma must specify a setup time.
   *
   * @param graph
   *          the graph
   * @param file
   *          the file
   * @return true, if successful
   */
  private boolean validateDmas(final Graph graph, final IFile file) {

    boolean valid = true;
    boolean hasSetupTime = false;

    for (final Vertex v : graph.vertexSet()) {
      hasSetupTime = false;

      final String type = v.getType().getName();
      if (type.contains("Dma")) {

        final String setupTime = (String) v.getValue("setupTime");
        if ((setupTime != null) && !setupTime.equals("") && (Integer.valueOf(setupTime) >= 0)) {
          hasSetupTime = true;
        }

        if (!hasSetupTime) {
          createMarker(file, "A Dma must specify a positive integer-valued setup time.", (String) v.getValue("id"),
              IMarker.PROBLEM, IMarker.SEVERITY_ERROR);
          valid = false;
        }
      }
    }

    return valid;
  }

  /**
   * A memory Node must specify a size. Two memories with the same definition must have the same size.
   *
   * @param graph
   *          the graph
   * @param file
   *          the file
   * @return true, if successful
   */
  private boolean validateMems(final Graph graph, final IFile file) {

    boolean valid = true;
    boolean hasSize = false;
    boolean conflictedSizes = false;
    final Map<String, String> definitionToSize = new LinkedHashMap<>();

    for (final Vertex v : graph.vertexSet()) {
      hasSize = false;
      conflictedSizes = false;

      final String type = v.getType().getName();
      if (type.contains("Mem")) {

        final String size = (String) v.getValue("memSize");
        final String definition = (String) v.getValue("definition");

        if ((size != null) && !size.equals("") && (Integer.valueOf(size) > 0)) {
          hasSize = true;
        }

        // Testing if instances with the same definition have different
        // sizes
        if ((definition != null) && !definition.equals("") && !definition.equals("default")) {

          if (definitionToSize.containsKey(definition)) {
            final boolean emptySize = (size == null) || size.isEmpty();
            final String storedSize = definitionToSize.get(definition);
            final boolean emptySSize = (storedSize == null) || storedSize.isEmpty();
            if ((emptySize && !emptySSize) || (!emptySize && emptySSize)
                || (!emptySize && !emptySSize && !size.equals(storedSize))) {
              conflictedSizes = true;
            }
          }
          definitionToSize.put(definition, size);
        }

        if (!hasSize) {
          createMarker(file, "A memory must specify a non-zero integer-valued size.", (String) v.getValue("id"),
              IMarker.PROBLEM, IMarker.SEVERITY_ERROR);
          valid = false;
        }

        if (conflictedSizes) {
          createMarker(file, "Two memories with the same definition must have the same size.",
              (String) v.getValue("id"), IMarker.PROBLEM, IMarker.SEVERITY_ERROR);
          valid = false;
        }
      }
    }

    return valid;
  }

  /**
   * A Control link must be between an operator and an enabler (Mem or Dma) and specify a setupTime.
   *
   * @param graph
   *          the graph
   * @param file
   *          the file
   * @return true, if successful
   */
  private boolean validateControlLinks(final Graph graph, final IFile file) {
    Boolean valid = true;
    Boolean hasOperatorSource = false;
    Boolean hasEnablerTarget = false;
    for (final Edge e : graph.edgeSet()) {
      if (e.getType().getName().equals("ControlLink")) {
        hasOperatorSource = false;
        hasEnablerTarget = false;

        if ((e.getSource() != null) && e.getSource().getType().getName().contains("Operator")) {
          hasOperatorSource = true;
        }

        if ((e.getTarget() != null) && (e.getTarget().getType().getName().contains("Mem")
            || e.getTarget().getType().getName().contains("Dma"))) {
          hasEnablerTarget = true;
        }

        if (!hasOperatorSource || !hasEnablerTarget) {
          createMarker(file, "Each control link must link an operator to an enabler (Mem or Dma).",
              (String) e.getSource().getValue("id") + "->" + (String) e.getTarget().getValue("id"), IMarker.PROBLEM,
              IMarker.SEVERITY_ERROR);
          valid = false;
        }
      }
    }

    return valid;
  }

  /**
   * A hierarchical connection link must link a commmunication node or an operator and a hierarchical connection node.
   *
   * @param graph
   *          the graph
   * @param file
   *          the file
   * @return true, if successful
   */
  private boolean validateHierarchicalConnections(final Graph graph, final IFile file) {
    Boolean valid = true;
    Boolean hasComNodeOrOpSource = false;
    Boolean hasHierConnectionTarget = false;

    for (final Edge e : graph.edgeSet()) {
      if (e.getType().getName().equals("hierConnection")) {
        hasComNodeOrOpSource = false;
        hasHierConnectionTarget = false;

        final Vertex source = e.getSource();
        if (source != null) {
          final String sourceType = source.getType().getName();
          if (sourceType.contains("ComNode") || sourceType.contains("Operator")) {
            hasComNodeOrOpSource = true;
          }
        }

        final Vertex target = e.getTarget();
        if (target != null) {
          final String targetType = target.getType().getName();
          if (targetType.contains("hierConnection")) {
            hasHierConnectionTarget = true;
          }
        }

        if (!(hasComNodeOrOpSource && hasHierConnectionTarget)) {

          // Remove the edge that cannot be saved.
          graph.removeEdge(e);

          createMarker(file,
              "A hierarchical connection link must link a commmunication node or an operator and a hierarchical "
                  + "connection node. It is undirected.",
              (String) e.getSource().getValue("id") + "->" + (String) e.getTarget().getValue("id"), IMarker.PROBLEM,
              IMarker.SEVERITY_ERROR);
          valid = false;
        }
      }
    }

    return valid;
  }

  /**
   * Each component instance must specify a definition id that identifies the instanciated component.
   *
   * @param graph
   *          the graph
   * @param file
   *          the file
   * @return true, if successful
   */
  private boolean validateComponentInstances(final Graph graph, final IFile file) {

    boolean valid = true;
    boolean hasRefName = false;
    boolean conflictedRefinements = false;
    final Map<String, String> definitionToRefinement = new LinkedHashMap<>();

    for (final Vertex v : graph.vertexSet()) {
      hasRefName = false;
      conflictedRefinements = false;

      final String type = v.getType().getName();
      if (!type.equals("hierConnection")) {
        final String definition = (String) v.getValue("definition");

        // Testing if instances with the same definition have different
        // refinements
        if ((definition != null) && !definition.equals("") && !definition.equals("default")) {
          hasRefName = true;

          final String refinement = (String) v.getValue("refinement");
          if (definitionToRefinement.containsKey(definition)) {
            final boolean emptyRef = (refinement == null) || refinement.isEmpty();
            final String storedRefinement = definitionToRefinement.get(definition);
            final boolean emptySRef = (storedRefinement == null) || storedRefinement.isEmpty();
            if ((emptyRef && !emptySRef) || (!emptyRef && emptySRef)
                || (!emptyRef && !emptySRef && !refinement.equals(storedRefinement))) {
              conflictedRefinements = true;
            }
          }
          definitionToRefinement.put(definition, refinement);
        }

        if (!hasRefName) {
          v.setValue("definition", "default" + v.getType().getName());

          createMarker(file,
              "Each component instance must specify a definition id that identifies the instanciated "
                  + "component. By default, it is set to \"default\"Type",
              (String) v.getValue("id"), IMarker.PROBLEM, IMarker.SEVERITY_ERROR);
          valid = false;
        }

        if (conflictedRefinements) {

          createMarker(file, "Two components with the same definition must have the same refinement",
              (String) v.getValue("id"), IMarker.PROBLEM, IMarker.SEVERITY_ERROR);
          valid = false;
        }
      }
    }

    return valid;
  }

  /**
   * Each hierarchy port must have exactly one hierarchical connection.
   *
   * @param graph
   *          the graph
   * @param file
   *          the file
   * @return true, if successful
   */
  private boolean validateHierarchicalPorts(final Graph graph, final IFile file) {

    boolean valid = true;

    for (final Vertex v : graph.vertexSet()) {
      final String type = v.getType().getName();
      if (type.contains("hierConnection")) {

        final int nbEdges = graph.incomingEdgesOf(v).size() + graph.outgoingEdgesOf(v).size();

        if (nbEdges != 1) {
          graph.removeVertex(v);

          createMarker(file, "Each hierarchy port must have exactly one hierarchical connection.",
              (String) v.getValue("id"), IMarker.PROBLEM, IMarker.SEVERITY_ERROR);
          valid = false;
        }
      }
    }

    return valid;
  }

  /**
   * Displays an error.
   *
   * @param file
   *          the file
   * @param message
   *          the message
   * @param location
   *          the location
   * @param type
   *          the type
   * @param severity
   *          the severity
   */
  protected void createMarker(final IFile file, final String message, final String location, final String type,
      final int severity) {
    try {
      final IMarker marker = file.createMarker(type);
      marker.setAttribute(IMarker.LOCATION, location);
      marker.setAttribute(IMarker.SEVERITY, severity);
      marker.setAttribute(IMarker.MESSAGE, message);
    } catch (final CoreException e) {
      throw new DFToolsUIException("Could not create marker", e);
    }
  }
}
