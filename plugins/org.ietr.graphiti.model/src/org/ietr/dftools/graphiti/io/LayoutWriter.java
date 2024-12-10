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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import org.eclipse.draw2d.geometry.Rectangle;
import org.ietr.dftools.graphiti.GraphitiException;
import org.ietr.dftools.graphiti.model.Graph;
import org.ietr.dftools.graphiti.model.ObjectType;
import org.ietr.dftools.graphiti.model.Vertex;

/**
 * This class writes the .layout file associated with graphs.
 *
 * @author Matthieu Wipliez
 *
 */
public class LayoutWriter {

  /**
   * Write.
   *
   * @param graph
   *          the graph
   * @param out
   *          the out
   */
  public void write(final Graph graph, final OutputStream out) {
    PrintWriter writer;
    try {
      writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));
      writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      writer.println("<layout>");
      writer.println("\t<vertices>");

      for (final Vertex vertex : graph.vertexSet()) {
        final String id = (String) vertex.getValue(ObjectType.PARAMETER_ID);
        final Rectangle bounds = (Rectangle) vertex.getValue(Vertex.PROPERTY_SIZE);
        final String x = String.valueOf(bounds.x);
        final String y = String.valueOf(bounds.y);

        writer.println("\t\t<vertex id=\"" + id + "\" x=\"" + x + "\" y=\"" + y + "\"/>");
      }

      writer.println("\t</vertices>");
      writer.println("</layout>");
      writer.close();
    } catch (final UnsupportedEncodingException e) {
      throw new GraphitiException("UTF-8 encoding unsupported", e);
    }
  }

}
