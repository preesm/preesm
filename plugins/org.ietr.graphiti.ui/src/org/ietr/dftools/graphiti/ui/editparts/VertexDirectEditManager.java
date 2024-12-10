/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
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
package org.ietr.dftools.graphiti.ui.editparts;

import org.eclipse.draw2d.Label;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;
import org.ietr.dftools.graphiti.model.Vertex;
import org.ietr.dftools.graphiti.ui.figure.VertexCellEditorLocator;
import org.ietr.dftools.graphiti.ui.figure.VertexFigure;

/**
 * This class extends {@link DirectEditManager} to edit a {@link Vertex}'s id. It is based on Daniel Lee's
 * implementation for the flow example.
 *
 * @author Daniel Lee
 * @author Matthieu Wipliez
 */
public class VertexDirectEditManager extends DirectEditManager {

  /** The verify listener. */
  private VerifyListener verifyListener;

  /** The vertex label. */
  private final Label vertexLabel;

  /**
   * Creates a new VertexDirectEditManager with the given attributes.
   *
   * @param source
   *          the source EditPart
   * @param vertexFigure
   *          the vertex figure
   */
  public VertexDirectEditManager(final VertexEditPart source, final VertexFigure vertexFigure) {
    super(source, TextCellEditor.class, new VertexCellEditorLocator(vertexFigure));
    this.vertexLabel = vertexFigure.getLabelId();
  }

  /**
   * Inits the cell editor.
   *
   * @see org.eclipse.gef.tools.DirectEditManager#initCellEditor()
   */
  @Override
  protected void initCellEditor() {
    final CellEditor editor = getCellEditor();

    final Text text = (Text) editor.getControl();

    this.verifyListener = event -> {
      final Text text1 = (Text) getCellEditor().getControl();
      final String oldText = text1.getText();
      final String newText = oldText.substring(0, event.start) + event.text
          + oldText.substring(event.end, oldText.length());

      final GC gc = new GC(text1);
      Point size = gc.textExtent(newText);
      gc.dispose();
      if (size.x == 0) {
        size.x = size.y;
      } else {
        size = text1.computeSize(size.x, size.y);
      }
      text1.setSize(size.x, size.y);
    };
    text.addVerifyListener(this.verifyListener);

    final String initialLabelText = this.vertexLabel.getText();
    editor.setValue(initialLabelText);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.tools.DirectEditManager#showFeedback()
   */
  @Override
  public void showFeedback() {
    // this is to remove the shadow around the Text component
    getEditPart().showSourceFeedback(getDirectEditRequest());
  }

  /**
   * Unhook listeners.
   *
   * @see org.eclipse.gef.tools.DirectEditManager#unhookListeners()
   */
  @Override
  protected void unhookListeners() {
    super.unhookListeners();
    final Text text = (Text) getCellEditor().getControl();
    text.removeVerifyListener(this.verifyListener);
    this.verifyListener = null;
  }
}
