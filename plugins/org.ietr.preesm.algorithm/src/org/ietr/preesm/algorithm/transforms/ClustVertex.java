package org.ietr.preesm.algorithm.transforms;

import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;

/**
 *
 */
public class ClustVertex extends AbstractClust {

  private SDFAbstractVertex vertex;

  public SDFAbstractVertex getVertex() {
    return this.vertex;
  }

  public void setVertex(final SDFAbstractVertex vertex) {
    this.vertex = vertex;
  }
}
