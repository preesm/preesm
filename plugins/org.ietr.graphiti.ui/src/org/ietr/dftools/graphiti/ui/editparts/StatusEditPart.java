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
package org.ietr.dftools.graphiti.ui.editparts;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.ietr.dftools.graphiti.ui.editpolicies.LayoutPolicy;

/**
 * This class extends {@link AbstractGraphicalEditPart} by setting its figure layout manager to
 * {@link GraphLayoutManager}. It also extends the {@link EditPart#isSelectable()} method to return false, causing the
 * selection tool to act like the marquee tool when no particular children has been selected.
 *
 * @author Matthieu Wipliez
 *
 */
public class StatusEditPart extends AbstractGraphicalEditPart {

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
   */
  @Override
  protected void createEditPolicies() {
    installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
    installEditPolicy(EditPolicy.LAYOUT_ROLE, new LayoutPolicy());
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
   */
  @Override
  protected IFigure createFigure() {
    // The figure associated with this graph edit part is only a
    // free form layer
    final Figure root = new FreeformLayer();
    root.setLayoutManager(new FreeformLayout());

    final IStatus status = (IStatus) getModel();
    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    final Throwable exc = status.getException();
    final Throwable cause = exc.getCause();
    if (cause == null) {
      exc.printStackTrace(new PrintStream(bos));
    } else {
      cause.printStackTrace(new PrintStream(bos));
    }

    final Display d = Display.getCurrent();
    final Image image = d.getSystemImage(SWT.ICON_ERROR);
    final Label labelImage = new Label(image);
    root.add(labelImage, new Rectangle(5, 5, -1, -1));

    final Label label = new Label(status.getMessage() + ": " + exc.getMessage() + "\n" + bos.toString());
    root.add(label, new Rectangle(10 + image.getBounds().width, 5, -1, -1));

    return root;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#isSelectable()
   */
  @Override
  public boolean isSelectable() {
    return false;
  }
}
