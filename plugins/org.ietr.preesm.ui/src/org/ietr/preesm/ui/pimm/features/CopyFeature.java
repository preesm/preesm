package org.ietr.preesm.ui.pimm.features;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.AbstractCopyFeature;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

/**
 * Graphiti feature that implements the Copy action for PiMM vertices.
 *
 * @author anmorvan
 *
 */
public class CopyFeature extends AbstractCopyFeature {

  /**
   * Structural class to store the copied elements. The EObject inheritance is used for enabling the Graphiti clipboard (only stores EObjects).
   */
  public static class VertexCopy extends EObjectImpl {
    public int              originalX;
    public int              originalY;
    public PictogramElement originalPictogramElement;
    public Diagram          originalDiagram;
    public AbstractVertex   originalVertex;
    public PiGraph          originalPiGraph;

  }

  public CopyFeature(final IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public void copy(final ICopyContext context) {
    final PictogramElement[] pes = context.getPictogramElements();

    final List<VertexCopy> copies = new LinkedList<>();

    for (int i = 0; i < pes.length; i++) {
      final PictogramElement pe = pes[i];
      final Object bo = getBusinessObjectForPictogramElement(pe);
      if (bo instanceof AbstractVertex) {
        final VertexCopy vertexCopy = new VertexCopy();
        vertexCopy.originalDiagram = getDiagram();
        vertexCopy.originalPictogramElement = pe;
        vertexCopy.originalPiGraph = (PiGraph) getDiagram().getLink().getBusinessObjects().get(0);
        vertexCopy.originalVertex = (AbstractVertex) bo;
        vertexCopy.originalX = pe.getGraphicsAlgorithm().getX();
        vertexCopy.originalY = pe.getGraphicsAlgorithm().getY();
        copies.add(vertexCopy);
      }

    }
    // put all business objects to the clipboard
    final VertexCopy[] array = copies.toArray(new VertexCopy[copies.size()]);
    putToClipboard(array);
  }

  @Override
  public boolean canCopy(final ICopyContext context) {
    final PictogramElement[] pes = context.getPictogramElements();
    if ((pes == null) || (pes.length == 0)) { // nothing selected
      return false;
    }
    // return true, if all selected elements are a Vertices (cannot copy edges)
    for (final PictogramElement pe : pes) {
      final Object bo = getBusinessObjectForPictogramElement(pe);
      if (!(bo instanceof AbstractVertex) && !(bo instanceof Delay)) {
        return false;
      }
    }
    return true;
  }

}
