/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Jonathan Piat [jpiat@laas.fr] (2008 - 2011)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2019 - 2020)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2016)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2008 - 2012)
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
package org.preesm.model.pisdf.serialize;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.WorkspaceUtils;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.reconnection.SubgraphDisconnector;
import org.preesm.model.pisdf.util.PiMMSwitch;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 *
 */
@PreesmTask(id = "pisdf-export", name = "PiSDF Exporter", category = "Graph Exporters",

    inputs = { @Port(name = "PiMM", type = PiGraph.class) },

    parameters = {
        @Parameter(name = "path", values = { @Value(name = "/Algo/generated/pisdf/", effect = "default path") }),
        @Parameter(name = "hierarchical",
            values = { @Value(name = "true/false",
                effect = "Export the whole hierarchy (default: true). When set to true, will export all the "
                    + "hierarchy in the folder given by 'path', replacing refinement paths. Note: exporting "
                    + "hierarchical graph with this option set to false can cause the  the consistency check "
                    + "fail if the children graphs do not exist.") }) })
public class PiSDFExporterTask extends AbstractTaskImplementation {

  /**
   */
  private class BottomUpChildrenGraphExporter extends PiMMSwitch<Boolean> {

    private final IPath xmlPath;

    public BottomUpChildrenGraphExporter(final IPath xmlPath) {
      this.xmlPath = xmlPath;
    }

    @Override
    public final Boolean casePiGraph(final PiGraph graph) {
      graph.getChildrenGraphs().forEach(this::doSwitch);
      exportGraph(graph, xmlPath);
      return true;
    }

  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#execute(java.util.Map, java.util.Map,
   * org.eclipse.core.runtime.IProgressMonitor, java.lang.String, org.ietr.dftools.workflow.elements.Workflow)
   */
  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {

    final PiGraph graph = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);

    final String hierarchicalParameter = parameters.get("hierarchical");
    final String pathParameter = parameters.get("path");

    // create a copy of the input graph so that subgraph disconnector does not impact other tasks
    final PiGraph graphCopy = PiMMUserFactory.instance.copyWithHistory(graph);

    // Creates the output file now
    final String sXmlPath = WorkspaceUtils.getAbsolutePath(pathParameter, workflow.getProjectName());
    IPath xmlPath = new Path(sXmlPath);
    // Get a complete valid path with all folders existing
    try {
      if (xmlPath.getFileExtension() != null) {
        WorkspaceUtils.createMissingFolders(xmlPath.removeFileExtension().removeLastSegments(1));
      } else {
        WorkspaceUtils.createMissingFolders(xmlPath);
      }
    } catch (CoreException | IllegalArgumentException e) {
      throw new PreesmRuntimeException("Path " + sXmlPath + " is not a valid path for export.\n" + e.getMessage());
    }

    final boolean doHiearchicalExport = "true".equalsIgnoreCase(hierarchicalParameter);

    if (doHiearchicalExport) {
      new BottomUpChildrenGraphExporter(xmlPath).doSwitch(graphCopy);
    } else {
      exportGraph(graphCopy, xmlPath);

    }
    WorkspaceUtils.updateWorkspace();

    return new LinkedHashMap<>();
  }

  private static final void exportGraph(final PiGraph graph, final IPath xmlPath) {

    final IPath graphPath;
    graphPath = xmlPath.append(graph.getName() + ".pi");

    final String string = graphPath.toString();
    final URI uri = URI.createPlatformResourceURI(string, true);
    // Get the project
    final String platformString = uri.toPlatformString(true);
    final IFile documentFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(platformString));
    final String osString = documentFile.getLocation().toOSString();
    try (final OutputStream outStream = new FileOutputStream(osString);) {
      // Write the Graph to the OutputStream using the Pi format
      SubgraphDisconnector.disconnectSubGraphs(graph, xmlPath.toString());
      new PiWriter(uri).write(graph, outStream);
      // the reconnection is useless since we copied the graph first
    } catch (IOException e) {
      throw new PreesmRuntimeException("Could not open outputstream file " + string);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#getDefaultParameters()
   */
  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();

    parameters.put("path", "/Algo/generated/pisdf/");
    parameters.put("hierarchical", "true");
    return parameters;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation#monitorMessage()
   */
  @Override
  public String monitorMessage() {
    return "Exporting algorithm graph";
  }

}
