/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008 - 2011)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2013)
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
package org.ietr.dftools.graphiti.ui.commands.refinement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;
import org.ietr.dftools.graphiti.GraphitiException;
import org.ietr.dftools.graphiti.GraphitiModelPlugin;
import org.ietr.dftools.graphiti.io.GenericGraphParser;
import org.ietr.dftools.graphiti.io.IncompatibleConfigurationFile;
import org.ietr.dftools.graphiti.model.Graph;
import org.ietr.dftools.graphiti.model.IRefinementPolicy;
import org.ietr.dftools.graphiti.model.ObjectType;
import org.ietr.dftools.graphiti.model.Vertex;

/**
 * This class provides facilities to prompt the user for a source port or target port by parsing the refinement (if
 * any), or by asking the user to enter a port name.
 *
 * @author Matthieu Wipliez
 *
 */
public class PortChooser {

  /**
   * The Class PortContentProvider.
   */
  private class PortContentProvider implements IStructuredContentProvider {

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    @Override
    public Object[] getElements(final Object inputElement) {
      return ((List<?>) inputElement).toArray();
    }

  }

  /** The connection. */
  private final String connection;

  /**
   * Creates a new port chooser using the given refinement manager.
   *
   * @param connection
   *          The title of the connection, as "source - target".
   */
  public PortChooser(final String connection) {
    this.connection = connection;
  }

  /**
   * Displays a {@link ListDialog} filled with the given port list, asking the user to choose one. The
   * <code>edgePort</code> string is used to identify the port (ie source or target) to choose.
   *
   * @param ports
   *          A list of port names.
   * @param edgePort
   *          A string identifying the port ("source port" or "target port").
   * @return The name of the chosen port.
   */
  private String choosePort(final List<String> ports, final String edgePort) {
    final IWorkbench workbench = PlatformUI.getWorkbench();
    final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    final Shell shell = window.getShell();

    final ListDialog dialog = new ListDialog(shell);
    dialog.setContentProvider(new PortContentProvider());
    dialog.setLabelProvider(new LabelProvider() {

      @Override
      public String getText(final Object element) {
        return (String) element;
      }

    });
    dialog.setAddCancelButton(false);
    dialog.setInput(ports);
    dialog.setMessage("Please choose a " + edgePort + ":");
    dialog.setTitle("Connection: " + this.connection);
    dialog.open();

    if ((dialog.getResult() == null) || (dialog.getResult().length == 0)) {
      return null;
    } else {
      return (String) dialog.getResult()[0];
    }
  }

  /**
   * Returns a port name from the current vertex (set by getSourcePort or getTargetPort).
   *
   * @param vertex
   *          vertex
   * @param edgePort
   *          The label to use when prompting the user to choose ("source port" or "target port").
   * @param portTypes
   *          An array where each entry is a valid port type in this context, either {@link Vertex#TYPE_INPUT_PORT},
   *          {@link Vertex#TYPE_OUTPUT_PORT} or {@link Vertex#TYPE_PORT}.
   * @return A port name if found, <code>null</code> otherwise.
   */
  private String getPort(final Vertex vertex, final String edgePort, final String[] portTypes) {
    final IRefinementPolicy policy = vertex.getConfiguration().getRefinementPolicy();
    final IFile sourceFile = policy.getRefinementFile(vertex);

    // open the refinement
    if (sourceFile != null) {
      final List<String> ports = getPorts(sourceFile, portTypes);
      if (!ports.isEmpty()) {
        return choosePort(ports, edgePort);
      }
    }

    return null;
  }

  /**
   * Prompts the user for an arbitrary port name using a simple {@link InputDialog}. The <code>portName</code> parameter
   * indicates the role of the given port.
   *
   * @param portName
   *          "source port" or "target port".
   * @return The port name, or <code>null</code> if the user entered a blank port name (equivalent to "no port").
   */
  private String getPortName(final String portName) {
    final IWorkbench workbench = PlatformUI.getWorkbench();
    final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    final Shell shell = window.getShell();

    final InputDialog dialog = new InputDialog(shell, "Connection: " + this.connection,
        "Please enter a " + portName + " name:", "", null);
    dialog.open();
    final String value = dialog.getValue();
    if (value.isEmpty()) {
      return null;
    } else {
      return value;
    }
  }

  /**
   * Returns the list of ports read from the given file that have the given port type.
   *
   * @param sourceFile
   *          The source file as an {@link IFile}.
   * @param portTypes
   *          An array where each entry is a valid port type in this context, either {@link Vertex#TYPE_INPUT_PORT},
   *          {@link Vertex#TYPE_OUTPUT_PORT} or {@link Vertex#TYPE_PORT}.
   * @return A list of port names.
   */
  private List<String> getPorts(final IFile sourceFile, final String[] portTypes) {
    // refinement graph
    final GenericGraphParser parser = new GenericGraphParser(GraphitiModelPlugin.getDefault().getConfigurations());
    Graph graph = null;
    try {
      graph = parser.parse(sourceFile);
    } catch (final IncompatibleConfigurationFile e) {
      throw new GraphitiException("Could not parse ports", e);
    }

    // get ports from graph
    if (graph == null) {
      return new ArrayList<>();
    } else {
      final Set<Vertex> vertices = graph.vertexSet();
      final List<String> ports = new ArrayList<>();
      for (final Vertex vertex : vertices) {
        // check the vertex type against all accepted port types.
        for (final String portType : portTypes) {
          if (vertex.getType().getName().equals(portType)) {
            final String id = (String) vertex.getValue(ObjectType.PARAMETER_ID);
            ports.add(id);

            // we break because a vertex has only one type.
            break;
          }
        }
      }
      return ports;
    }
  }

  /**
   * Returns a port from the given vertex.
   *
   * @param source
   *          The edge's source vertex.
   * @return A port name.
   */
  public String getSourcePort(final Vertex source) {
    final String port = getPort(source, "source port", new String[] { Vertex.TYPE_OUTPUT_PORT, Vertex.TYPE_PORT });
    if (port == null) {
      return getPortName("source port");
    } else {
      return port;
    }
  }

  /**
   * Returns a port from the given vertex.
   *
   * @param target
   *          The edge's target vertex.
   * @return A port name.
   */
  public String getTargetPort(final Vertex target) {
    final String port = getPort(target, "target port", new String[] { Vertex.TYPE_INPUT_PORT, Vertex.TYPE_PORT });
    if (port == null) {
      return getPortName("target port");
    } else {
      return port;
    }
  }
}
