/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2013)
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
package org.preesm.algorithm.io.sdf3;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import org.preesm.algorithm.model.sdf.SDFAbstractVertex;
import org.preesm.algorithm.model.sdf.SDFEdge;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.model.sdf.SDFInterfaceVertex;
import org.preesm.algorithm.model.sdf.SDFVertex;
import org.preesm.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.preesm.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.preesm.algorithm.model.types.LongEdgePropertyType;
import org.preesm.algorithm.model.types.StringEdgePropertyType;
import org.preesm.commons.DomUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

// TODO: Auto-generated Javadoc
/**
 * This class is used to parse a {@link File} in the SDF3 (SDF For Free) Xml format indo the corresponding
 * {@link SDFGraph}.
 *
 * @author kdesnos
 * @see this page http://www.es.ele.tue.nl/sdf3/manuals/xml/sdf
 */
public class Sdf3XmlParser {

  /**
   * This {@link Map} associates each data type of the graph {@link SDFEdge} to their size.
   */
  private final Map<String, Integer> dataTypes = new LinkedHashMap<>();

  /**
   * This {@link Map} associates the name of an edge from the SDF3 file to its corresponding {@link SDFEdge}.
   */
  private final Map<String, SDFEdge> edges = new LinkedHashMap<>();

  /**
   * This {@link Map} associates the actors of the parsed graph to their execution time.
   */
  private final Map<SDFAbstractVertex, Integer> actorExecTimes = new LinkedHashMap<>();

  /**
   * Find the unique {@link Element} with the specified name in the children of the given {@link Element}.
   *
   * @param elt
   *          the {@link Element} whose childs are scanned.
   * @param elementName
   *          the name of the searched {@link Element}
   * @return the searched {@link Element}
   * @throws RuntimeException
   *           if the given {@link Element} has no child Element with the appropriate name or has more than one child
   *           with this name.
   */
  private Element findElement(final Element elt, final String elementName) {
    final NodeList nodes = elt.getElementsByTagName(elementName);
    if (nodes.getLength() == 0) {
      throw new RuntimeException("Parsed " + elt.getLocalName() + " does not contain any " + elementName + " element");
    }
    if (nodes.getLength() > 1) {
      throw new RuntimeException("Parsed " + elt.getLocalName() + " contains too many " + elementName
          + " elements (expected 1, found " + nodes.getLength() + ")");
    }
    return (Element) nodes.item(0);
  }

  /**
   * Get the execution times of all actors parsed during call to {@link #parse(InputStream)}.<br>
   * <br>
   * The returned {@link Map} associates the {@link SDFVertex} of the parsed graph to their execution time.
   *
   * @return actorExecTimes the execution time of the parsed graph actors
   */
  public Map<SDFAbstractVertex, Integer> getActorExecTimes() {
    return this.actorExecTimes;
  }

  /**
   * Get all the data types encountered while parsing the graph with {@link #parse(InputStream)}.<br>
   * <br>
   * The return {@link Map} associates each data type of the graph {@link SDFEdge} to their size.
   *
   * @return dataTypes the data types of the parsed SDF3 graph
   */
  public Map<String, Integer> getDataTypes() {
    return this.dataTypes;
  }

  /**
   * Parse the {@link SDFGraph} from the given {@link InputStream} using the SDF3 XML format.
   *
   * @param inputStream
   *          The Parsed input stream
   * @return The parsed {@link SDFGraph} or null is something went wrong
   * @throws RuntimeException
   *           the runtime exception
   */
  public SDFGraph parse(final InputStream inputStream) throws RuntimeException {
    // Instantiate the new graph
    final SDFGraph graph = new SDFGraph();

    // Parse the input stream
    final Document document = DomUtil.parseDocument(inputStream);

    // Retrieve the root element
    final Element rootElt = document.getDocumentElement();

    try {
      // Fill the graph with parsed information
      parseSdf3Xml(rootElt, graph);

    } catch (final RuntimeException e) {
      e.printStackTrace();
      throw e;
    }

    return graph;
  }

  /**
   * Parse an {@link SDFVertex} from the parsed graph.
   *
   * @param actorElt
   *          the {@link Element} of the parsed {@link SDFVertex}
   * @param graph
   *          the parsed {@link SDFGraph}
   */
  private void parseActor(final Element actorElt, final SDFGraph graph) {
    final SDFVertex actor = new SDFVertex(graph);
    final String name = actorElt.getAttribute("name");
    if (name.isEmpty()) {
      throw new RuntimeException("Unnamed actor was found.");
    }
    actor.setId(name);
    actor.setName(name);

    final NodeList portElts = actorElt.getElementsByTagName("port");
    for (int i = 0; i < portElts.getLength(); i++) {
      final Element portElt = (Element) portElts.item(i);
      parsePort(portElt, actor);
    }

    graph.addVertex(actor);
  }

  /**
   * Parse the actor properties of an actor.
   *
   * @param actorPtyElt
   *          the {@link Element} containing the Actor Properties
   * @param graph
   *          the parsed graph
   */
  private void parseActorProperties(final Element actorPtyElt, final SDFGraph graph) {
    // Get the actor whose properties are parsed
    final String actorName = actorPtyElt.getAttribute("actor");
    if (actorName.isEmpty()) {
      throw new RuntimeException("Cannot parse properties of an unspecified actor");
    }
    final SDFAbstractVertex actor = graph.getVertex(actorName);
    if (actor == null) {
      throw new RuntimeException("Parsing properties of a non-existing actor: " + actorName);
    }

    // Parse the properties
    try {
      final Element procElement = findElement(actorPtyElt, "processor");
      final Element execTimeElement = findElement(procElement, "executionTime");
      final String time = execTimeElement.getAttribute("time");
      if (!time.isEmpty()) {
        final Integer execTime = new Integer(time);
        this.actorExecTimes.put(actor, execTime);
      }

      final Element memoryElement = findElement(procElement, "memory");
      final Element stateSizeElement = findElement(memoryElement, "stateSize");
      final String stateSize = stateSizeElement.getAttribute("max");
      if (!stateSize.isEmpty()) {
        actor.setPropertyValue("working_memory", new Integer(stateSize));
      }
    } catch (final RuntimeException e) {
      if (e.getMessage().contains("does not contain any")) {
        // do nothing
        e.printStackTrace();
      } else if (e.getMessage().contains("too many processor")) {
        throw new RuntimeException("Multiproc architecture are not supported yet.");
      } else {
        throw e;
      }
    }

  }

  /**
   * Parse the applicationGraph {@link Element} of the xml {@link File}.
   *
   * @param elt
   *          the applicationGraph {@link Element} of the parsed {@link File}.
   * @param graph
   *          the parsed {@link SDFGraph}.
   */
  private void parseApplicationGraph(final Element elt, final SDFGraph graph) {

    final Element sdfElt = findElement(elt, "sdf");
    parseSDF(sdfElt, graph);

    final Element sdfPropertiesElt = findElement(elt, "sdfProperties");
    parseSDFProperties(sdfPropertiesElt, graph);
  }

  /**
   * Parse an {@link SDFEdge} from the parsed graph.
   *
   * @param channelElt
   *          the {@link Element} of the parsed {@link SDFEdge}
   * @param graph
   *          the parsed {@link SDFGraph}
   */
  private void parseChannel(final Element channelElt, final SDFGraph graph) {
    // Get Source actor and port
    final String srcActorName = channelElt.getAttribute("srcActor");
    if (srcActorName.isEmpty()) {
      throw new RuntimeException("Edges must have a source actor.");
    }
    final SDFAbstractVertex srcActor = graph.getVertex(srcActorName);
    if (srcActor == null) {
      throw new RuntimeException("Edge source actor " + srcActorName + " does not exist.");
    }
    final String srcPortName = channelElt.getAttribute("srcPort");
    if (srcPortName.isEmpty()) {
      throw new RuntimeException("Edges must have a source port.");
    }
    final SDFInterfaceVertex srcPort = srcActor.getInterface(srcPortName);
    if ((srcPort == null) || !(srcPort instanceof SDFSinkInterfaceVertex)) {
      throw new RuntimeException("Source port " + srcPortName + " does not exists for actor " + srcActorName);
    }

    // Get Target actor and port
    final String tgtActorName = channelElt.getAttribute("dstActor");
    if (tgtActorName.isEmpty()) {
      throw new RuntimeException("Edges must have a destination actor.");
    }
    final SDFAbstractVertex tgtActor = graph.getVertex(tgtActorName);
    if (tgtActor == null) {
      throw new RuntimeException("Edge destination actor " + tgtActorName + " does not exist.");
    }
    final String tgtPortName = channelElt.getAttribute("dstPort");
    if (tgtPortName.isEmpty()) {
      throw new RuntimeException("Edges must have a destination port.");
    }
    final SDFInterfaceVertex tgtPort = tgtActor.getInterface(tgtPortName);
    if ((tgtPort == null) || !(tgtPort instanceof SDFSourceInterfaceVertex)) {
      throw new RuntimeException("Destination port " + tgtPortName + " does not exists for actor " + tgtActorName);
    }

    // Create the edge
    final SDFEdge edge = graph.addEdge(srcActor, srcPort, tgtActor, tgtPort);

    // Set the prod/consumption rates
    edge.setProd(new LongEdgePropertyType((Integer) srcPort.getPropertyBean().getValue("port_rate")));
    edge.setCons(new LongEdgePropertyType((Integer) tgtPort.getPropertyBean().getValue("port_rate")));

    // Give a name to the edge (not really usefull in SDFGraphs but since
    // the name exists in SDF3, we might as well keep track of it
    final String edgeName = channelElt.getAttribute("name");
    if (edgeName.isEmpty()) {
      throw new RuntimeException("Edges must have a name.");
    }
    edge.setPropertyValue("sdf3_edge_name", edgeName);
    // Keep a map of the edge names and edges
    this.edges.put(edgeName, edge);

    // Add the delays (if any)
    final String delay = channelElt.getAttribute("initialTokens");
    if (!delay.isEmpty()) {
      final LongEdgePropertyType delayProperty = new LongEdgePropertyType(delay);
      edge.setDelay(delayProperty);
    }

  }

  /**
   * Parse the ChannelProperties {@link Element} corresponding to an {@link SDFEdge} of the parsed {@link SDFGraph}.
   *
   * @param channelPtyElt
   *          the {@link Element} containing the parsed properties
   * @param graph
   *          the parsed {@link SDFGraph}
   */
  private void parseChannelProperties(final Element channelPtyElt, final SDFGraph graph) {
    // Get the edge whose properties are parsed
    final String channelName = channelPtyElt.getAttribute("channel");
    if (channelName.isEmpty()) {
      throw new RuntimeException("Cannot parse properties of an unspecified channel");
    }
    final SDFEdge edge = this.edges.get(channelName);
    if (edge == null) {
      throw new RuntimeException("Parsing properties of a non-existing edge: " + channelName);
    }

    // The bufferSIze element exist but is not used (yet)
    // findElement(channelPtyElt, "bufferSize");

    try {
      final Element tokenSize = findElement(channelPtyElt, "tokenSize");
      final String tokenSz = tokenSize.getAttribute("sz");
      if (tokenSz.isEmpty()) {
        throw new RuntimeException("Channel " + channelName + " token size is not set properly.");
      }
      final String dataType = "t" + tokenSz;
      edge.setDataType(new StringEdgePropertyType(dataType));
      this.dataTypes.put(dataType, new Integer(tokenSz));
    } catch (final RuntimeException e) {
      e.printStackTrace();
    }

  }

  /**
   * Parse an {@link SDFInterfaceVertex port} of an {@link SDFVertex actor}.
   *
   * @param portElt
   *          the {@link Element} of the parsed {@link SDFInterfaceVertex port}
   * @param actor
   *          the parsed {@link SDFVertex actor}
   */
  private void parsePort(final Element portElt, final SDFVertex actor) {
    SDFInterfaceVertex port;
    final String direction = portElt.getAttribute("type");
    switch (direction) {
      case "in":
        port = new SDFSourceInterfaceVertex();
        break;
      case "out":
        port = new SDFSinkInterfaceVertex();
        break;
      default:
        throw new RuntimeException("Unknown port direction: " + direction);
    }
    final String name = portElt.getAttribute("name");
    if (name.isEmpty()) {
      throw new RuntimeException("Unnamed ports found in actor " + actor.getId());
    }
    port.setId(name);
    port.setName(name);

    final String rate = portElt.getAttribute("rate");
    if (name.isEmpty()) {
      throw new RuntimeException("Port " + actor.getId() + "." + port.getId() + " has no rate");
    }
    port.setPropertyValue("port_rate", Integer.decode(rate).intValue());

    actor.addInterface(port);
  }

  /**
   * Parse the sdf {@link Element} of the xml {@link File}.
   *
   * @param sdfElt
   *          the sdf elt
   * @param graph
   *          the parsed {@link SDFGraph}.
   */
  private void parseSDF(final Element sdfElt, final SDFGraph graph) {
    // Retrieve the Name of the graph
    final String name = sdfElt.getAttribute("name");
    if (name.isEmpty()) {
      throw new RuntimeException("Parsed graph have no name.");
    }
    graph.setName(name);

    // Parse actors
    final NodeList actorElts = sdfElt.getElementsByTagName("actor");
    for (int i = 0; i < actorElts.getLength(); i++) {
      final Element actorElt = (Element) actorElts.item(i);
      parseActor(actorElt, graph);
    }

    // Parse channels
    final NodeList channelElts = sdfElt.getElementsByTagName("channel");
    for (int i = 0; i < channelElts.getLength(); i++) {
      final Element channelElt = (Element) channelElts.item(i);
      parseChannel(channelElt, graph);
    }
  }

  /**
   * Parse the root {@link Element} of the xml {@link File}.
   *
   * @param rootElt
   *          the root {@link Element} of the parsed {@link File}.
   * @param graph
   *          the parsed {@link SDFGraph}.
   */
  private void parseSdf3Xml(final Element rootElt, final SDFGraph graph) {
    // Check if the file contains a graph of appropriate type and version
    final String rootName = rootElt.getLocalName();
    if (!rootName.equals("sdf3")) {
      throw new RuntimeException("XML file does not contain a SDF3 graph.");
    }
    final String type = rootElt.getAttribute("type");
    if (!type.equals("sdf")) {
      throw new RuntimeException("SDF3 graph type not supported (only sdf is supported)");
    }
    final String version = rootElt.getAttribute("version");
    if (!version.equals("1.0")) {
      throw new RuntimeException("Graph version " + version + " is not supported (only version 1.0 is)");
    }

    final Element appGraphElt = findElement(rootElt, "applicationGraph");

    parseApplicationGraph(appGraphElt, graph);
  }

  /**
   * Parse the sdfProperties {@link Element} of the xml {@link File}.
   *
   * @param sdfPropElt
   *          the sdfProperties {@link Element} of the parsed {@link File}.
   * @param graph
   *          the parsed {@link SDFGraph}.
   */
  private void parseSDFProperties(final Element sdfPropElt, final SDFGraph graph) {
    // Parse actorProperties
    {
      final NodeList actorPtyElts = sdfPropElt.getElementsByTagName("actorProperties");
      for (int i = 0; i < actorPtyElts.getLength(); i++) {
        final Element actorPtyElt = (Element) actorPtyElts.item(i);
        parseActorProperties(actorPtyElt, graph);
      }
    }

    // Parse actorProperties
    final NodeList channelPtyElts = sdfPropElt.getElementsByTagName("channelProperties");
    for (int i = 0; i < channelPtyElts.getLength(); i++) {
      final Element channelPtyElt = (Element) channelPtyElts.item(i);
      parseChannelProperties(channelPtyElt, graph);
    }

    // There are some graphProperties that are not parsed.
  }
}
