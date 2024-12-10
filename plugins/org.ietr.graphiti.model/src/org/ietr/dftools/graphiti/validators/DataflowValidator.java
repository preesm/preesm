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
package org.ietr.dftools.graphiti.validators;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.ietr.dftools.graphiti.GraphitiException;
import org.ietr.dftools.graphiti.model.Edge;
import org.ietr.dftools.graphiti.model.Graph;
import org.ietr.dftools.graphiti.model.IValidator;
import org.ietr.dftools.graphiti.model.ObjectType;
import org.ietr.dftools.graphiti.model.Vertex;

/**
 * This class implements a model validator.
 *
 * @author Matthieu Wipliez
 *
 */
public class DataflowValidator implements IValidator {

  /**
   * Check input ports.
   *
   * @param graph
   *          the graph
   * @param file
   *          the file
   * @return true, if successful
   */
  private boolean checkInputPorts(final Graph graph, final IFile file) {
    boolean res = true;
    for (final Vertex vertex : graph.vertexSet()) {
      final Set<Edge> edges = graph.incomingEdgesOf(vertex);
      final Map<String, Integer> countMap = new LinkedHashMap<>();
      for (final Edge edge : edges) {
        final Object value = edge.getValue(ObjectType.PARAMETER_TARGET_PORT);
        if (value != null) {
          final String tgt = (String) value;
          Integer inCount = countMap.get(tgt);
          if (inCount == null) {
            inCount = 0;
          }

          countMap.put(tgt, inCount + 1);
        }
      }

      for (final Entry<String, Integer> count : countMap.entrySet()) {
        if (count.getValue() > 1) {
          res = false;
          final String message = "The input port " + count.getKey() + " of vertex "
              + vertex.getValue(ObjectType.PARAMETER_ID) + " has " + count.getValue() + " connections "
              + "but should not have more than one connection";
          createMarker(file, message);
        }
      }
    }

    return res;
  }

  /**
   * Creates the marker.
   *
   * @param file
   *          the file
   * @param message
   *          the message
   */
  protected void createMarker(final IFile file, final String message) {
    try {
      final IMarker marker = file.createMarker(IMarker.PROBLEM);
      marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
      marker.setAttribute(IMarker.MESSAGE, message);
    } catch (final CoreException e) {
      throw new GraphitiException("Could not create marker.", e);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.graphiti.model.IValidator#validate(org.ietr.dftools.graphiti.model.Graph,
   * org.eclipse.core.resources.IFile)
   */
  @Override
  public boolean validate(final Graph graph, final IFile file) {
    return checkInputPorts(graph, file);
  }

}
