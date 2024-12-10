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
package org.ietr.dftools.graphiti.ui.editors;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.draw2d.parts.Thumbnail;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.GraphicalViewerImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * This class provides a thumbnail outline page.
 *
 * @author Matthieu Wipliez
 *
 */
public class ThumbnailOutlinePage extends ContentOutlinePage {

  /** The canvas. */
  private Canvas canvas;

  /** The dispose listener. */
  private DisposeListener disposeListener;

  /** The editor. */
  private final GraphEditor editor;

  /** The thumbnail. */
  private Thumbnail thumbnail;

  /**
   * Instantiates a new thumbnail outline page.
   *
   * @param editor
   *          the editor
   */
  public ThumbnailOutlinePage(final GraphEditor editor) {
    super(new GraphicalViewerImpl());
    this.editor = editor;
  }

  @Override
  public void createControl(final Composite parent) {
    this.canvas = new Canvas(parent, SWT.BORDER);
    final LightweightSystem lws = new LightweightSystem(this.canvas);

    final RootEditPart root = this.editor.getGraphicalViewerRootEditPart();
    final ScalableFreeformRootEditPart scalable = (ScalableFreeformRootEditPart) root;
    this.thumbnail = new ScrollableThumbnail((Viewport) scalable.getFigure());
    this.thumbnail.setSource(scalable.getLayer(LayerConstants.PRINTABLE_LAYERS));

    lws.setContents(this.thumbnail);

    this.disposeListener = e -> {
      if (ThumbnailOutlinePage.this.thumbnail != null) {
        ThumbnailOutlinePage.this.thumbnail.deactivate();
        ThumbnailOutlinePage.this.thumbnail = null;
      }
    };

    final Control control = this.editor.getGraphicalViewerControl();
    control.addDisposeListener(this.disposeListener);
  }

  @Override
  public void dispose() {
    this.editor.removeSelectionSynchronizerViewer(getViewer());
    final Control control = this.editor.getGraphicalViewerControl();
    if ((control != null) && !control.isDisposed()) {
      control.removeDisposeListener(this.disposeListener);
    }
    super.dispose();
  }

  @Override
  public Control getControl() {
    return this.canvas;
  }

}
