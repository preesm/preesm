/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008 - 2011)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2013)
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
package org.ietr.dftools.graphiti.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.xml.transform.TransformerException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.draw2d.geometry.Rectangle;
import org.ietr.dftools.graphiti.GraphitiException;
import org.ietr.dftools.graphiti.model.AbstractObject;
import org.ietr.dftools.graphiti.model.Configuration;
import org.ietr.dftools.graphiti.model.Edge;
import org.ietr.dftools.graphiti.model.FileFormat;
import org.ietr.dftools.graphiti.model.Graph;
import org.ietr.dftools.graphiti.model.ObjectType;
import org.ietr.dftools.graphiti.model.Parameter;
import org.ietr.dftools.graphiti.model.Transformation;
import org.ietr.dftools.graphiti.model.Vertex;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class provides a generic graph parser. Generic means that it can parse any format (text-based or XML-based) that
 * contains a graph.
 *
 * @author Matthieu Wipliez
 *
 */
public class GenericGraphParser {

  private static final String             VALUE_ATTRIBUTE_NAME = "value";
  /** The configurations. */
  private final Collection<Configuration> configurations;

  /**
   * Creates a new parser using the given configuration list.
   *
   * @param configurations
   *          A {@link List} of {@link Configuration}s.
   */
  public GenericGraphParser(final Collection<Configuration> configurations) {
    this.configurations = configurations;
  }

  /**
   * Returns the first sibling of <code>node</code>, or <code>node</code> itself, which has the given name. If none is
   * found, the function returns <code>null</code>.
   *
   * @param node
   *          A node.
   * @param name
   *          The name of the node we are looking for.
   * @return The first node whose name matches, or <code>null</code>.
   */
  private static Node getFirstSiblingNamed(Node node, final String name) {
    while ((node != null) && !node.getNodeName().equals(name)) {
      node = node.getNextSibling();
    }
    if (node == null) {
      throw new GraphitiException("Could not find sibling node named [" + name + "]", null);
    }
    return node;
  }

  /**
   * Checks that every vertex in <code>graph</code> has layout information. If it is the case, the
   * {@link Graph#PROPERTY_HAS_LAYOUT} is set to <code>true</code>. Otherwise it is set to false.
   *
   * @param graph
   *          the graph
   */
  private void checkLayout(final Graph graph) {
    final Set<Vertex> vertices = graph.vertexSet();
    for (final Vertex vertex : vertices) {
      if (vertex.getValue(Vertex.PROPERTY_SIZE) == null) {
        graph.setValue(Graph.PROPERTY_HAS_LAYOUT, Boolean.FALSE);
        return;
      }
    }

    graph.setValue(Graph.PROPERTY_HAS_LAYOUT, Boolean.TRUE);
  }

  /**
   * With a given configuration and file format, try to parse the file contents given as a byte input stream.
   *
   * @param configuration
   *          A supposedly valid configuration for the given contents.
   * @param file
   *          the file
   * @return A {@link Graph} if successful.
   */
  private Graph parse(final Configuration configuration, final IFile file)
      throws CoreException, IOException, URISyntaxException, TransformerException, TransformedDocumentParseError {
    final FileFormat format = configuration.getFileFormat();
    final List<Transformation> transformations = format.getImportTransformations();
    Element element;
    if (transformations.isEmpty()) {
      final InputStream in = file.getContents();
      element = DomHelper.parseDocument(in).getDocumentElement();
    } else {
      element = null;
      for (final Transformation transformation : transformations) {
        if (transformation.isXslt()) {
          // fills the element from the input stream
          if (element == null) {
            final InputStream in = file.getContents();
            element = DomHelper.parseDocument(in).getDocumentElement();
          }

          final XsltTransformer transformer = new XsltTransformer(configuration.getContributorId(),
              transformation.getFileName());
          transformer.setParameter("path", file.getLocation().toString());
          element = transformer.transformDomToDom(element);
        } else {
          final ITransformation tr = transformation.getInstance();
          return tr.transform(file);
        }
      }
    }
    if (element == null) {
      throw new NullPointerException();
    }
    return parseGraph(configuration, element);
  }

  /**
   * Parses the given {@link IFile} and returns a graph. The file is parsed as follows:
   * <ol>
   * <li>Iterate through the configurations.</li>
   * <li>In each configuration, for each file format matching the file extension, try to parse the file with the given
   * configuration.</li>
   * <li>If parsing fails, go to step 2.</li>
   * </ol>
   *
   * @param file
   *          the file
   * @return the graph
   * @throws IncompatibleConfigurationFile
   *           the incompatible configuration file
   */
  public Graph parse(final IFile file) throws IncompatibleConfigurationFile {
    // finds all suitable configurations
    final String fileExt = file.getFileExtension();
    final List<Configuration> suitableConfigs = new ArrayList<>();
    for (final Configuration configuration : this.configurations) {
      final FileFormat format = configuration.getFileFormat();
      if (format.getFileExtension().equals(fileExt)) {
        suitableConfigs.add(configuration);
      }
    }

    Configuration configuration;
    if (suitableConfigs.isEmpty()) {
      throw new IncompatibleConfigurationFile("No configuration could parse the file" + file.getFullPath());
    } else if (suitableConfigs.size() == 1) {
      configuration = suitableConfigs.get(0);
    } else {
      throw new IncompatibleConfigurationFile("Many configurations could parse the file");
    }

    // parse with the configuration
    try {
      final Graph graph = parse(configuration, file);
      graph.setFileName(file.getFullPath());
      return graph;
    } catch (final Exception e) {
      throw new IncompatibleConfigurationFile("The file could not be parsed with the matching configuration", e);
    }
  }

  /**
   * Parses the edges.
   *
   * @param graph
   *          The graph to add edges to.
   * @param node
   *          A child node of &lt;graph&gt;.
   * @return The node following &lt;edges&gt;.
   * @throws TransformedDocumentParseError
   *           If the edges could not be parsed.
   */
  private void parseEdges(final Graph graph, Node node) throws TransformedDocumentParseError {
    final Configuration configuration = graph.getConfiguration();
    node = getFirstSiblingNamed(node, "edges");
    Node child = node.getFirstChild();
    while (child != null) {
      if (child.getNodeName().equals("edge")) {
        final Element element = (Element) child;

        final String typeName = element.getAttribute("type");
        final ObjectType type = configuration.getEdgeType(typeName);

        final String sourceId = element.getAttribute("source");
        final Vertex source = graph.findVertex(sourceId);

        final String targetId = element.getAttribute("target");
        final Vertex target = graph.findVertex(targetId);

        final String edgeName = "In the edge \"" + sourceId + "\" -> \"" + targetId + "\"";
        final String errorMessageEnd = "\" could not be found.";
        if ((source == null) && (target == null)) {
          final String message = edgeName + ", \"" + sourceId + "\" nor \"" + targetId + errorMessageEnd;
          throw new TransformedDocumentParseError(message);
        } else if (source == null) {
          final String message = edgeName + ", the source vertex \"" + sourceId + errorMessageEnd;
          throw new TransformedDocumentParseError(message);
        } else if (target == null) {
          final String message = edgeName + ", the target vertex \"" + targetId + errorMessageEnd;
          throw new TransformedDocumentParseError(message);
        }

        final Edge edge = new Edge(type, source, target);
        parseParameters(edge, type, child.getFirstChild());
        graph.addEdge(edge);
      }

      child = child.getNextSibling();
    }
  }

  /**
   * Parses the graph.
   *
   * @param configuration
   *          The configuration to create a graph with.
   * @param element
   *          The &lt;graph&gt; element.
   * @return A newly-created graph with the given configuration.
   * @throws TransformedDocumentParseError
   *           If <code>element</code> cannot be parsed.
   */
  private Graph parseGraph(final Configuration configuration, final Element element)
      throws TransformedDocumentParseError {
    final String typeName = element.getAttribute("type");
    final ObjectType type = configuration.getGraphType(typeName);
    final Graph graph = new Graph(configuration, type, true);

    // parse different sections
    Node node = element.getFirstChild();
    node = parseParameters(graph, type, node);
    node = parseVertices(graph, node);
    parseEdges(graph, node);

    checkLayout(graph);

    return graph;
  }

  /**
   * Parses a parameter.
   *
   * @param parameter
   *          The parameter we got from the configuration.
   * @param child
   *          The &lt;parameter&gt; element.
   * @return An object, either a {@link List}, a {@link Map}, an {@link Integer}, a {@link Float}, a {@link Boolean}, or
   *         a {@link String}. May be <code>null</code> if there are no elements/entries and the parameter is a
   *         list/map, or if the value field is absent or empty, and the parameter is a scalar.
   */
  private Object parseParameter(final Parameter parameter, final Element child) {
    final Class<?> parameterType = parameter.getType();
    if (parameterType == List.class) {
      return parseListParameter(child);
    } else if (parameterType == Map.class) {
      return parseMapParameter(child);
    } else {
      final Element element = child;
      final String value = element.getAttribute(GenericGraphParser.VALUE_ATTRIBUTE_NAME);
      if (!element.hasAttribute(GenericGraphParser.VALUE_ATTRIBUTE_NAME) || value.isEmpty()) {
        return null;
      } else {
        if (parameterType == Integer.class) {
          return Integer.valueOf(value);
        } else if (parameterType == Float.class) {
          return Float.valueOf(value);
        } else if (parameterType == Boolean.class) {
          return Boolean.valueOf(value);
        } else if (parameterType == String.class) {
          return value;
        } else {
          return value;
        }
      }
    }
  }

  private Object parseMapParameter(final Element child) {
    final Map<String, String> map = new TreeMap<>();
    Node element = child.getFirstChild();
    if (element == null) {
      return null;
    }

    while (element != null) {
      if (element.getNodeName().equals("entry")) {
        final String key = ((Element) element).getAttribute("key");
        final String value = ((Element) element).getAttribute(GenericGraphParser.VALUE_ATTRIBUTE_NAME);
        map.put(key, value);
      }

      element = element.getNextSibling();
    }

    return map;
  }

  private Object parseListParameter(final Element child) {
    final List<String> list = new ArrayList<>();
    Node element = child.getFirstChild();
    if (element == null) {
      return null;
    }

    while (element != null) {
      if (element.getNodeName().equals("element")) {
        final String eltValue = ((Element) element).getAttribute(GenericGraphParser.VALUE_ATTRIBUTE_NAME);
        list.add(eltValue);
      }

      element = element.getNextSibling();
    }

    return list;
  }

  /**
   * Parses the parameters and set the properties of the given <code>propertyBean</code>, that has the given type.
   *
   * @param abstractObject
   *          The target property bean.
   * @param type
   *          The type of <code>propertyBean</code>.
   * @param node
   *          A previous sibling of &lt;parameters&gt;, or &lt;parameters&gt; itself.
   * @return The node following &lt;parameters&gt;.
   */
  private Node parseParameters(final AbstractObject abstractObject, final ObjectType type, Node node) {
    node = getFirstSiblingNamed(node, "parameters");

    Node child = node.getFirstChild();
    while (child != null) {
      if (child.getNodeName().equals("parameter")) {
        final Element element = ((Element) child);
        final String parameterName = element.getAttribute("name");
        final Parameter parameter = type.getParameter(parameterName);
        final Object value = parseParameter(parameter, element);
        if (value != null) {
          abstractObject.setValue(parameterName, value);
        }
      }

      child = child.getNextSibling();
    }

    return node.getNextSibling();
  }

  /**
   * Parses the vertices.
   *
   * @param graph
   *          The graph to add vertices to.
   * @param node
   *          A child node of &lt;graph&gt;.
   * @return The node following &lt;vertices&gt;.
   */
  private Node parseVertices(final Graph graph, Node node) {
    final Configuration configuration = graph.getConfiguration();
    node = getFirstSiblingNamed(node, "vertices");
    Node child = node.getFirstChild();
    while (child != null) {
      if (child.getNodeName().equals("vertex")) {
        final String typeName = ((Element) child).getAttribute("type");
        final ObjectType type = configuration.getVertexType(typeName);
        final Vertex vertex = new Vertex(type);

        // set layout information if present
        final String xAttr = ((Element) child).getAttribute("x");
        final String yAttr = ((Element) child).getAttribute("y");
        if (!xAttr.isEmpty() && !yAttr.isEmpty()) {
          final int x = Integer.parseInt(xAttr);
          final int y = Integer.parseInt(yAttr);
          vertex.setValue(Vertex.PROPERTY_SIZE, new Rectangle(x, y, 0, 0));
        }

        parseParameters(vertex, type, child.getFirstChild());
        graph.addVertex(vertex);
      }

      child = child.getNextSibling();
    }

    return node.getNextSibling();
  }

}
