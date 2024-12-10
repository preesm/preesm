/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008 - 2010)
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
package org.ietr.dftools.graphiti.ui.wizards;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.ietr.dftools.graphiti.model.Configuration;
import org.ietr.dftools.graphiti.model.Edge;
import org.ietr.dftools.graphiti.model.Graph;
import org.ietr.dftools.graphiti.model.ObjectType;
import org.ietr.dftools.graphiti.model.Vertex;
import org.ietr.dftools.graphiti.ui.editors.GraphEditor;

/**
 * This class provides a convert page for the save as wizard.
 *
 * @author Matthieu Wipliez
 */
public class WizardConvertPage extends WizardPage implements IGraphTypeSettable {

  /** The edge combo list. */
  private List<Combo> edgeComboList;

  /** The new configuration. */
  private Configuration newConfiguration;

  /** The new graph type. */
  private ObjectType newGraphType;

  /** The original edge types. */
  private Set<ObjectType> originalEdgeTypes;

  /** The original graph. */
  private Graph originalGraph;

  /** The original vertex types. */
  private Set<ObjectType> originalVertexTypes;

  /** The vertex combo list. */
  private List<Combo> vertexComboList;

  /**
   * Constructor for SampleNewWizardPage.
   *
   * @param selection
   *          the selection
   */
  public WizardConvertPage(final IStructuredSelection selection) {
    super("convertGraph");

    setTitle("Convert types");

    final Object obj = selection.getFirstElement();
    if (obj instanceof GraphEditor) {
      final GraphEditor editor = (GraphEditor) obj;
      this.originalGraph = editor.getContents();

      // fills the original graph, vertex and edge types
      final Configuration configuration = this.originalGraph.getConfiguration();
      this.originalEdgeTypes = configuration.getEdgeTypes();
      this.originalVertexTypes = configuration.getVertexTypes();
    }
  }

  /**
   * Convert edge types.
   *
   * @param graph
   *          the graph
   * @param edgeTypes
   *          the edge types
   */
  private void convertEdgeTypes(final Graph graph, final Map<ObjectType, ObjectType> edgeTypes) {
    final Set<Edge> edges = this.originalGraph.edgeSet();
    for (Edge edge : edges) {
      final ObjectType newType = edgeTypes.get(edge.getType());
      if (newType != null) {
        edge = new Edge(edge);
        final String sourceId = (String) edge.getSource().getValue(ObjectType.PARAMETER_ID);
        final String targetId = (String) edge.getTarget().getValue(ObjectType.PARAMETER_ID);
        final Vertex source = graph.findVertex(sourceId);
        final Vertex target = graph.findVertex(targetId);

        if ((source != null) && (target != null)) {
          edge.setSource(source);
          edge.setTarget(target);
          edge.setType(newType);
          graph.addEdge(edge);
        }
      }
    }
  }

  /**
   * Convert vertex types.
   *
   * @param graph
   *          the graph
   * @param vertexTypes
   *          the vertex types
   */
  private void convertVertexTypes(final Graph graph, final Map<ObjectType, ObjectType> vertexTypes) {
    final Set<Vertex> vertices = this.originalGraph.vertexSet();
    for (Vertex vertex : vertices) {
      final ObjectType newType = vertexTypes.get(vertex.getType());
      if (newType != null) {
        vertex = new Vertex(vertex);
        vertex.setType(newType);
        graph.addVertex(vertex);
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createControl(final Composite parent) {
    final Composite container = new Composite(parent, SWT.NONE);
    final GridLayout layout = new GridLayout();
    container.setLayout(layout);

    layout.numColumns = 2;
    layout.verticalSpacing = 9;

    createExplanationLabel(container);
    createVertexTypes(container);
    createEdgeTypes(container);

    setControl(container);
    setPageComplete(true);
  }

  /**
   * Creates a {@link Combo} for each edge type in the original configuration.
   *
   * @param parent
   *          The parent {@link Composite}.
   */
  private void createEdgeTypes(final Composite parent) {
    this.edgeComboList = new ArrayList<>();
    for (final ObjectType type : this.originalEdgeTypes) {
      final Label label = new Label(parent, SWT.NULL);
      label.setText("Convert \"" + type.getName() + "\" to:");

      final Combo edgeCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.SIMPLE);
      this.edgeComboList.add(edgeCombo);
    }
  }

  /**
   * Creates a {@link Label} to tell the user what to do.
   *
   * @param parent
   *          The parent {@link Composite}.
   */
  private void createExplanationLabel(final Composite parent) {
    final Label label = new Label(parent, SWT.NULL);
    label.setText("Please choose how the following vertices and edges " + "shall be converted:");
    final GridData data = new GridData();
    data.horizontalSpan = 2;
    label.setLayoutData(data);
  }

  /**
   * Creates a {@link Combo} for each vertex type in the original configuration.
   *
   * @param parent
   *          The parent {@link Composite}.
   */
  private void createVertexTypes(final Composite parent) {
    this.vertexComboList = new ArrayList<>();
    for (final ObjectType type : this.originalVertexTypes) {
      final Label label = new Label(parent, SWT.NULL);
      label.setText("Convert \"" + type.getName() + "\" to:");

      final Combo vertexCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.SIMPLE);
      this.vertexComboList.add(vertexCombo);
    }
  }

  /**
   * Returns a {@link Map} that maps an existing {@link EdgeType} to a new one. If the new type is not specified (i.e.
   * left blank by the user), no mapping is inserted.
   *
   * @return A {@link Map} that maps an existing {@link EdgeType} to a new one.
   */
  private Map<ObjectType, ObjectType> fillEdgeTypes() {
    final Map<ObjectType, ObjectType> edgeTypes = new LinkedHashMap<>();
    int i = 0;
    for (final ObjectType type : this.originalEdgeTypes) {
      final Combo combo = this.edgeComboList.get(i);
      i++;
      final int index = combo.getSelectionIndex();
      if (index != -1) {
        final String name = combo.getItem(index);
        final ObjectType newType = this.newConfiguration.getEdgeType(name);
        edgeTypes.put(type, newType);
      }
    }

    return edgeTypes;
  }

  /**
   * Returns a {@link Map} that maps an existing {@link VertexType} to a new one. If the new type is not specified (i.e.
   * left blank by the user), no mapping is inserted.
   *
   * @return A {@link Map} that maps an existing {@link VertexType} to a new one.
   */
  private Map<ObjectType, ObjectType> fillVertexTypes() {
    final Map<ObjectType, ObjectType> vertexTypes = new LinkedHashMap<>();
    int i = 0;
    for (final ObjectType type : this.originalVertexTypes) {
      final Combo combo = this.vertexComboList.get(i);
      i++;
      final int index = combo.getSelectionIndex();
      if (index != -1) {
        final String name = combo.getItem(index);
        final ObjectType newType = this.newConfiguration.getVertexType(name);
        vertexTypes.put(type, newType);
      }
    }

    return vertexTypes;
  }

  /**
   * Returns the converted graph.
   *
   * @return The converted graph.
   */
  public Graph getGraph() {
    // creates a new empty graph with the same properties as originalGraph
    // but configuration and type, that are overridden by newConfiguration
    // and newGraphType
    final Graph graph = new Graph(this.originalGraph, this.newConfiguration, this.newGraphType);

    // change vertex and edge types
    final Map<ObjectType, ObjectType> vertexTypes = fillVertexTypes();
    final Map<ObjectType, ObjectType> edgeTypes = fillEdgeTypes();
    convertVertexTypes(graph, vertexTypes);
    convertEdgeTypes(graph, edgeTypes);

    return graph;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.ietr.dftools.graphiti.ui.wizards.IGraphTypeSettable#setGraphType(org.ietr.dftools.graphiti.model.Configuration,
   * org.ietr.dftools.graphiti.model.ObjectType)
   */
  @Override
  public void setGraphType(final Configuration configuration, final ObjectType type) {
    this.newConfiguration = configuration;
    this.newGraphType = type;
    ((IGraphTypeSettable) getNextPage()).setGraphType(configuration, type);

    updateDescription();
    updateVertexTypes();
    updateEdgeTypes();

    getControl().pack();
  }

  /**
   * Updates the description of this page.
   */
  private void updateDescription() {
    setDescription(
        "Converted \"" + this.originalGraph.getType().getName() + "\" to \"" + this.newGraphType.getName() + "\".");
  }

  /**
   * Updates each edge combo list using the new configuration's edge types.
   */
  private void updateEdgeTypes() {
    for (final Combo edgeCombo : this.edgeComboList) {
      final Set<ObjectType> newEdges = this.newConfiguration.getEdgeTypes();
      final String[] items = new String[newEdges.size()];
      int i = 0;
      for (final ObjectType edgeType : newEdges) {
        items[i] = edgeType.getName();
        i++;
      }
      edgeCombo.setItems(items);
      edgeCombo.select(-1);
    }
  }

  /**
   * Updates each vertex combo list using the new configuration's vertex types.
   */
  private void updateVertexTypes() {
    for (final Combo vertexCombo : this.vertexComboList) {
      final Set<ObjectType> newVertices = this.newConfiguration.getVertexTypes();
      final String[] items = new String[newVertices.size()];
      int i = 0;
      for (final ObjectType vertexType : newVertices) {
        items[i] = vertexType.getName();
        i++;
      }
      vertexCombo.setItems(items);
      vertexCombo.select(-1);
    }
  }

}
