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
package org.ietr.dftools.graphiti.ui.commands;

import org.eclipse.gef.commands.Command;
import org.ietr.dftools.graphiti.model.Edge;
import org.ietr.dftools.graphiti.model.Graph;
import org.ietr.dftools.graphiti.model.ObjectType;
import org.ietr.dftools.graphiti.model.Vertex;
import org.ietr.dftools.graphiti.ui.commands.refinement.PortChooser;

/**
 * This class provides a Command that creates a dependency. ComplexSource and target are set when they are connected.
 *
 * @author Samuel Beaussier
 * @author Nicolas Isch
 * @author Matthieu Wipliez
 */
public class EdgeCreateCommand extends Command {

  /**
   * The edge is stored as an attribute so it can be used both in the <code>execute</code> and <code>undo</code>
   * methods.
   */
  private final Edge edge;

  /**
   * The parentGraph is stored as an attribute so it can be used both in the <code>execute</code> and <code>undo</code>
   * methods.
   */
  private Graph parentGraph;

  /** The source. */
  private Vertex source;

  /** The target. */
  private Vertex target;

  /**
   * Creates a new command using the given newly created edge.
   *
   * @param edge
   *          The newly created edge.
   */
  public EdgeCreateCommand(final Edge edge) {
    this.edge = edge;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.commands.Command#execute()
   */
  @Override
  public void execute() {
    // parent graph
    this.parentGraph = this.source.getParent();

    // edge has been set in the constructor.
    this.edge.setSource(this.source);
    this.edge.setTarget(this.target);

    final String connection = this.source.getValue(ObjectType.PARAMETER_ID) + " - "
        + this.target.getValue(ObjectType.PARAMETER_ID);

    final PortChooser portChooser = new PortChooser(connection);
    if (this.edge.getParameter(ObjectType.PARAMETER_SOURCE_PORT) != null) {
      this.edge.setValue(ObjectType.PARAMETER_SOURCE_PORT, portChooser.getSourcePort(this.source));
    }

    if (this.edge.getParameter(ObjectType.PARAMETER_TARGET_PORT) != null) {
      this.edge.setValue(ObjectType.PARAMETER_TARGET_PORT, portChooser.getTargetPort(this.target));
    }

    this.parentGraph.addEdge(this.edge);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.commands.Command#getLabel()
   */
  @Override
  public String getLabel() {
    if (this.edge != null) {
      final String type = this.edge.getType().getName();
      return "Create " + type;
    } else {
      return "Create edge";
    }
  }

  /**
   * Sets the source of the dependency to create/reconnect.
   *
   * @param source
   *          The dependency source as a Port.
   */
  public void setSource(final Vertex source) {
    this.source = source;
  }

  /**
   * Sets the target of the dependency to create/reconnect.
   *
   * @param target
   *          The dependency target as a Port.
   */
  public void setTarget(final Vertex target) {
    this.target = target;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.commands.Command#undo()
   */
  @Override
  public void undo() {
    this.parentGraph.removeEdge(this.edge);
  }
}
