/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2024) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
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
package org.preesm.algorithm.io.gml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.Path;
import org.preesm.algorithm.model.AbstractGraph;
import org.preesm.algorithm.model.AbstractVertex;
import org.preesm.algorithm.model.CodeRefinement;
import org.preesm.algorithm.model.PropertyFactory;
import org.preesm.algorithm.model.PropertySource;
import org.preesm.algorithm.model.factories.IModelVertexFactory;
import org.preesm.algorithm.model.parameters.Argument;
import org.preesm.algorithm.model.parameters.Parameter;
import org.preesm.algorithm.model.parameters.Variable;
import org.preesm.commons.GMLKey;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSParser;

/**
 * Class used to import a Graph from a GML InputStream.
 *
 * @author jpiat
 * @param <G>
 *          the generic type
 * @param <V>
 *          the value type
 */
public abstract class GMLImporter<G extends AbstractGraph<?, ?>, V extends AbstractVertex<?>> {

  private static final String ARGUMENTS_LITERAL = "arguments";

  private static final String VARIABLES_LITERAL = "variables";

  private static final String PARAMETERS_LITERAL = "parameters";

  /** The class key set. */
  private final Map<String, List<GMLKey>> classKeySet;

  /** The vertex factory. */
  protected IModelVertexFactory<V> vertexFactory;

  /** The path. */
  protected String path;

  /** The vertex from id. */
  protected Map<String, V> vertexFromId = new LinkedHashMap<>();

  /**
   * Creates a new GMLImporter.
   *
   */
  protected GMLImporter() {
    this.classKeySet = new LinkedHashMap<>();
  }

  /**
   * Gives this Importer.
   *
   * @return This Importer Key set
   */
  public Map<String, List<GMLKey>> getKeySet() {
    return this.classKeySet;
  }

  /**
   * Parses the given file.
   *
   * @param f
   *          The file to parse
   * @return The parsed graph
   * @throws PreesmException
   *           the invalid model exception
   * @throws FileNotFoundException
   *           the file not found exception
   */
  public G parse(final File f) throws FileNotFoundException {
    this.path = f.getAbsolutePath();
    return parse(new FileInputStream(f));
  }

  /**
   * Parses the given file.
   *
   * @param input
   *          The input stream to parse
   * @param path
   *          The of the file to parse
   * @return The parsed graph
   * @throws PreesmException
   *           the invalid model exception
   * @throws FileNotFoundException
   *           the file not found exception
   */
  public G parse(final InputStream input, final String path) throws FileNotFoundException {
    this.path = path;
    return parse(input);
  }

  /**
   * Parses the input stream as a GML document.
   *
   * @param input
   *          The InputStream to parse
   * @return The graph parsed from the document
   * @throws PreesmException
   *           the invalid model exception
   */
  private G parse(final InputStream input) {

    // using DOM3
    DOMImplementationRegistry registry = null;
    DOMImplementationLS impl = null;
    try {
      registry = DOMImplementationRegistry.newInstance();
      impl = (DOMImplementationLS) registry.getDOMImplementation("Core 3.0 XML 3.0 LS");
    } catch (ClassCastException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
      throw new PreesmRuntimeException("Could not import graph", e);
    }

    final LSInput lsInput = impl.createLSInput();
    lsInput.setByteStream(input);

    // parse without comments and whitespace
    final LSParser builder = impl.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, null);
    final DOMConfiguration config = builder.getDomConfig();
    config.setParameter("comments", false);
    config.setParameter("element-content-whitespace", false);

    final Document doc = builder.parse(lsInput);

    final Element rootElt = (Element) doc.getFirstChild();
    if (!rootElt.getNodeName().equals("graphml")) {
      throw new PreesmRuntimeException("Root element is not graphml");
    }
    recoverKeys(rootElt);
    final NodeList childList = rootElt.getChildNodes();
    for (int i = 0; i < childList.getLength(); i++) {
      if (childList.item(i).getNodeName().equals("graph")) {
        final Element graphElt = (Element) childList.item(i);
        final G graph = parseGraph(graphElt);
        // Record the path of the graph
        graph.setPropertyValue(AbstractGraph.PATH_PROPERTY_LITERAL, this.path);
        return graph;
      }
    }
    return null;
  }

  /**
   * Parses an Edge in the DOM document.
   *
   * @param edgeElt
   *          The DOM Element
   * @param parentGraph
   *          The parent Graph of this Edge
   * @throws PreesmException
   *           the invalid model exception
   */
  protected abstract void parseEdge(Element edgeElt, G parentGraph);

  /**
   * Parses a Graph in the DOM document.
   *
   * @param graphElt
   *          The graph Element in the DOM document
   * @return The parsed Graph
   * @throws PreesmException
   *           the invalid model exception
   */
  protected abstract G parseGraph(Element graphElt);

  /**
   * Parse an element keys.
   *
   * @param elt
   *          The DOM element parent of the keys
   * @param src
   *          The property source to fill
   */
  protected void parseKeys(final Element elt, final PropertySource src) {
    final NodeList childList = elt.getChildNodes();
    for (int i = 0; i < childList.getLength(); i++) {
      if (childList.item(i).getNodeName().equals("data")) {
        final String key = ((Element) childList.item(i)).getAttribute("key");
        if ((!(key.equals(GMLImporter.ARGUMENTS_LITERAL) || key.equals(GMLImporter.PARAMETERS_LITERAL)
            || key.equals(GMLImporter.VARIABLES_LITERAL))) && src.getPropertyBean().keys().contains(key)) {
          final String propertyName = ((Element) childList.item(i)).getAttribute("key");
          final PropertyFactory factory = src.getFactoryForProperty(propertyName);
          if (factory != null) {
            src.setPropertyValue(propertyName, factory.create(childList.item(i).getTextContent()));
          } else {
            src.setPropertyValue(propertyName, childList.item(i).getTextContent());
          }
        }
      }
    }
  }

  /**
   * Parses a Vertex from the DOM document.
   *
   * @param vertexElt
   *          The node Element in the DOM document
   * @param parentGraph
   *          the parent graph
   * @return The parsed node
   * @throws PreesmException
   *           the invalid model exception
   */
  protected abstract V parseNode(Element vertexElt, G parentGraph);

  /**
   * Parses an Interface from the DOM document.
   *
   * @param portElt
   *          The DOM Element to parse
   * @param parentGraph
   *          the parent graph
   * @return The ineterface parsed from the DOM document
   * @throws PreesmException
   *           the invalid model exception
   */
  protected abstract V parsePort(Element portElt, G parentGraph);

  /**
   * Recover the key set from the GML document.
   *
   * @param rootElt
   *          The rootElt of the document
   */
  private void recoverKeys(final Element rootElt) {
    final NodeList childList = rootElt.getChildNodes();
    for (int i = 0; i < childList.getLength(); i++) {
      final Node childNode = childList.item(i);
      if (childNode.getNodeName().equals("key")) {
        final Element childElt = (Element) childNode;
        final String attrName = childElt.getAttribute("attr.name");
        String typeParamType = childElt.getAttribute("attr.type");
        if (typeParamType != null && typeParamType.isEmpty()) {
          typeParamType = null;
        }
        final String isFor = childElt.getAttribute("for");
        final String id = childElt.getAttribute("id");
        childElt.getChildNodes();
        final Class<?> type = null;
        final GMLKey newKey = new GMLKey(attrName, isFor, typeParamType, type);
        newKey.setId(id);
        final List<GMLKey> keys;
        if (!this.classKeySet.containsKey(isFor)) {
          keys = new ArrayList<>();
          this.classKeySet.put(isFor, keys);
        } else {
          keys = this.classKeySet.get(isFor);
        }
        keys.add(newKey);
      }
    }

  }

  /**
   * Parses the arguments.
   *
   * @param vertex
   *          the vertex
   * @param parentElt
   *          the parent elt
   */
  @SuppressWarnings({ "unchecked" })
  protected void parseArguments(final AbstractVertex<?> vertex, final Element parentElt) {
    final NodeList childList = parentElt.getChildNodes();
    for (int i = 0; i < childList.getLength(); i++) {
      if (childList.item(i).getNodeName().equals("data")
          && ((Element) childList.item(i)).getAttribute("key").equals(GMLImporter.ARGUMENTS_LITERAL)) {
        final NodeList argsList = childList.item(i).getChildNodes();
        for (int j = 0; j < argsList.getLength(); j++) {
          if (argsList.item(j).getNodeName().equals("argument")) {
            final Element arg = (Element) argsList.item(j);
            final Argument vArg = vertex.getBase().getArgumentFactory().create(arg.getAttribute("name"),
                arg.getAttribute("value"));
            vertex.addArgument(vArg);
          }
        }
      }
    }
  }

  /**
   * Parses the parameters.
   *
   * @param graph
   *          the graph
   * @param parentElt
   *          the parent elt
   */
  protected void parseParameters(final AbstractGraph<?, ?> graph, final Element parentElt) {
    final NodeList childList = parentElt.getChildNodes();
    for (int i = 0; i < childList.getLength(); i++) {
      if (childList.item(i).getNodeName().equals("data")
          && ((Element) childList.item(i)).getAttribute("key").equals(GMLImporter.PARAMETERS_LITERAL)) {
        final NodeList argsList = childList.item(i).getChildNodes();
        for (int j = 0; j < argsList.getLength(); j++) {
          if (argsList.item(j).getNodeName().equals("parameter")) {
            final Element param = (Element) argsList.item(j);
            final Parameter gParam = graph.getParameterFactory().create(param.getAttribute("name"));
            graph.addParameter(gParam);
          }
        }
      }
    }
  }

  /**
   * Parses the model.
   *
   * @param parentElt
   *          the parent elt
   * @return the string
   */
  protected String parseModel(final Element parentElt) {
    final NodeList childList = parentElt.getChildNodes();
    for (int i = 0; i < childList.getLength(); i++) {
      if (childList.item(i).getNodeName().equals("data")
          && ((Element) childList.item(i)).getAttribute("key").equals(AbstractGraph.KIND_PROPERTY_LITERAL)) {
        return childList.item(i).getTextContent();
      }
    }
    return "generic";
  }

  /**
   * Parses the variables.
   *
   * @param graph
   *          the graph
   * @param parentElt
   *          the parent elt
   */
  protected void parseVariables(final AbstractGraph<?, ?> graph, final Element parentElt) {
    final NodeList childList = parentElt.getChildNodes();
    for (int i = 0; i < childList.getLength(); i++) {
      if (childList.item(i).getNodeName().equals("data")
          && ((Element) childList.item(i)).getAttribute("key").equals(GMLImporter.VARIABLES_LITERAL)) {
        final NodeList argsList = childList.item(i).getChildNodes();
        for (int j = 0; j < argsList.getLength(); j++) {
          if (argsList.item(j).getNodeName().equals("variable")) {
            final Element variable = (Element) argsList.item(j);
            graph.addVariable(new Variable(variable.getAttribute("name"), variable.getAttribute("value")));
          }
        }
      }
    }
  }

  /**
   * Parses the graph description.
   *
   * @param vertex
   *          the vertex
   * @param parentElt
   *          the parent elt
   * @throws PreesmException
   *           the invalid model exception
   */
  protected void parseGraphDescription(final AbstractVertex<?> vertex, final Element parentElt) {
    final NodeList childList = parentElt.getChildNodes();
    for (int i = 0; i < childList.getLength(); i++) {
      if (childList.item(i).getNodeName().equals("data")
          && ((Element) childList.item(i)).getAttribute("key").equals(AbstractVertex.REFINEMENT_LITERAL)) {
        final Element graphDesc = (Element) childList.item(i);
        final String refinementPath = graphDesc.getTextContent();
        if (refinementPath.contains(".graphml")) {
          if ((this.path != null) && (refinementPath.length() > 0)) {
            final String directoryPath = this.path.substring(0, this.path.lastIndexOf(File.separator) + 1);
            importRefinement(vertex, refinementPath, directoryPath);
          }
        } else if (refinementPath.length() > 0) {
          vertex.setRefinement(new CodeRefinement(new Path(refinementPath)));
        }
      }
    }
  }

  private void importRefinement(final AbstractVertex<?> vertex, final String refinementPath,
      final String directoryPath) {
    final GMLGenericImporter importer = new GMLGenericImporter();
    try {
      final AbstractGraph<?, ?> refine = importer.parse(new File(directoryPath + refinementPath));
      vertex.setGraphDescription(refine);
    } catch (FileNotFoundException | PreesmException e) {
      throw new PreesmRuntimeException("Could not parse gaph description", e);
    }
  }

}
