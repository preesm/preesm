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
package org.ietr.dftools.graphiti.ui.editpolicies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.jface.viewers.CellEditor;
import org.ietr.dftools.graphiti.model.Graph;
import org.ietr.dftools.graphiti.model.ObjectType;
import org.ietr.dftools.graphiti.model.Vertex;
import org.ietr.dftools.graphiti.ui.commands.VertexRenameCommand;
import org.ietr.dftools.graphiti.ui.editparts.VertexEditPart;
import org.ietr.dftools.graphiti.ui.figure.VertexFigure;

/**
 * This class provides a {@link DirectEditPolicy} for a vertex id.
 *
 * @author Matthieu Wipliez
 *
 */
public class VertexDirectEditPolicy extends DirectEditPolicy {

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.editpolicies.DirectEditPolicy#getDirectEditCommand(org.eclipse.gef.requests.DirectEditRequest)
   */
  @Override
  protected Command getDirectEditCommand(final DirectEditRequest request) {
    final CellEditor editor = request.getCellEditor();
    editor.setValidator(value -> {
      final VertexEditPart part = (VertexEditPart) getHost();
      Vertex vertex = (Vertex) part.getModel();
      final Graph graph = vertex.getParent();

      final String vertexId = (String) value;
      if (vertexId.isEmpty()) {
        return "";
      }

      vertex = graph.findVertex(vertexId);
      if ((vertex != null) && !vertex.equals(getHost().getModel())) {
        return "A vertex already exists with the same identifier";
      }

      return null;
    });

    final Vertex vertex = (Vertex) getHost().getModel();
    if (editor.getValidator().isValid(editor.getValue()) == null) {
      final VertexRenameCommand cmd = new VertexRenameCommand(vertex);
      cmd.setName((String) editor.getValue());
      return cmd;
    } else {
      final String id = (String) vertex.getValue(ObjectType.PARAMETER_ID);
      final VertexFigure figure = (VertexFigure) getHostFigure();
      figure.getLabelId().setText(id);
      figure.adjustSize();
      return null;
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.editpolicies.DirectEditPolicy#showCurrentEditValue(org.eclipse.gef.requests.DirectEditRequest)
   */
  @Override
  protected void showCurrentEditValue(final DirectEditRequest request) {
    final String value = (String) request.getCellEditor().getValue();
    final VertexFigure figure = (VertexFigure) getHostFigure();
    figure.getLabelId().setText(value);
    figure.adjustSize();
  }

}
