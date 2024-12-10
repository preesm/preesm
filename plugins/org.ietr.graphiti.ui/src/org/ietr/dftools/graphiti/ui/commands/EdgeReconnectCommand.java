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
 * This class provides a Command that reconnects a dependency. Reconnection is a bit trickier than creation, since we
 * must remember the previous dependency. We inherit from EdgeCreateCommand so we just need to store the previous
 * dependency and parent graph, while keeping most of the original behavior.
 *
 * @author Matthieu Wipliez
 */
public class EdgeReconnectCommand extends Command {

  /**
   * The edge is stored as an attribute so it can be used both in the <code>execute</code> and <code>undo</code>
   * methods.
   */
  private Edge edge;

  /**
   * The parentGraph is stored as an attribute so it can be used both in the <code>execute</code> and <code>undo</code>
   * methods.
   */
  private Graph parentGraph;

  /** The previous edge. */
  private Edge previousEdge;

  /** The source. */
  private Vertex source;

  /** The target. */
  private Vertex target;

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.commands.Command#execute()
   */
  @Override
  public void execute() {
    // Disconnect
    this.parentGraph = this.source.getParent();
    this.parentGraph.removeEdge(this.previousEdge);

    // Clone edge and assign ports
    this.edge = new Edge(this.previousEdge);

    if (this.edge.getSource() != this.source) {
      this.edge.setSource(this.source);

      final String connection = this.edge.getSource().getValue(ObjectType.PARAMETER_ID) + " - "
          + this.edge.getTarget().getValue(ObjectType.PARAMETER_ID);
      final PortChooser portChooser = new PortChooser(connection);
      if (this.edge.getParameter(ObjectType.PARAMETER_SOURCE_PORT) != null) {
        this.edge.setValue(ObjectType.PARAMETER_SOURCE_PORT, portChooser.getSourcePort(this.source));
      }
    } else if (this.edge.getTarget() != this.target) {
      this.edge.setTarget(this.target);

      final String connection = this.edge.getSource().getValue(ObjectType.PARAMETER_ID) + " - "
          + this.edge.getTarget().getValue(ObjectType.PARAMETER_ID);
      final PortChooser portChooser = new PortChooser(connection);
      if (this.edge.getParameter(ObjectType.PARAMETER_TARGET_PORT) != null) {
        this.edge.setValue(ObjectType.PARAMETER_TARGET_PORT, portChooser.getTargetPort(this.target));
      }
    }

    this.parentGraph.addEdge(this.edge);
  }

  /**
   * Sets the original dependency (before it is reconnected).
   *
   * @param edge
   *          The edge.
   */
  public void setOriginalEdge(final Edge edge) {
    this.previousEdge = edge;

    // We also set these because we do not know which one will be set by the
    // EdgeGraphicalNodeEditPolicy (ie if getReconnectSourceCommand or
    // getReconnectTargetCommand is called)
    this.source = edge.getSource();
    this.target = edge.getTarget();
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
    this.parentGraph.addEdge(this.previousEdge);
  }
}
