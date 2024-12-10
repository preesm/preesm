/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008 - 2011)
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

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.ietr.dftools.graphiti.model.Graph;
import org.ietr.dftools.graphiti.model.ObjectType;
import org.ietr.dftools.graphiti.model.Vertex;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class reads the .layout file associated with graphs.
 *
 * @author Matthieu Wipliez
 *
 */
public class LayoutReader {

  /**
   * Read.
   *
   * @param graph
   *          the graph
   * @param byteStream
   *          the byte stream
   */
  public void read(final Graph graph, final InputStream byteStream) {
    final Map<String, Point> pointMap = new LinkedHashMap<>();
    final Document document = DomHelper.parseDocument(byteStream);
    final Element layout = document.getDocumentElement();
    final NodeList vertices = layout.getElementsByTagName("vertex");
    for (int i = 0; i < vertices.getLength(); i++) {
      final Element vertex = (Element) vertices.item(i);
      final String id = vertex.getAttribute("id");
      final String x = vertex.getAttribute("x");
      final String y = vertex.getAttribute("y");
      final Point point = new Point(Integer.parseInt(x), Integer.parseInt(y));
      pointMap.put(id, point);
    }

    graph.setValue(Graph.PROPERTY_HAS_LAYOUT, true);
    for (final Vertex vertex : graph.vertexSet()) {
      final String id = (String) vertex.getValue(ObjectType.PARAMETER_ID);
      final Point p = pointMap.get(id);
      vertex.setValue(Vertex.PROPERTY_SIZE, new Rectangle(p.x, p.y, 0, 0));
    }
  }

}
