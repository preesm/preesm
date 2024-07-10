/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2024) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2015)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2013)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2012 - 2013)
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
package org.preesm.ui.pisdf.wizards;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramLink;
import org.eclipse.graphiti.mm.pictograms.PictogramsFactory;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

/**
 * The Class NewPiMMPage.
 */
public class NewPiMMPage extends WizardNewFileCreationPage {

  /** The name of the saved file. */
  private String graphName;

  /**
   * Constructor for {@link NewPiMMPage}.
   *
   * @param pageName
   *          The name of the Page
   * @param selection
   *          The current resource selection
   */
  public NewPiMMPage(final String pageName, final IStructuredSelection selection) {
    super(pageName, selection);
    // if the selection is a file, gets its file name and removes its
    // extension. Otherwise, let fileName be null.
    final Object obj = selection.getFirstElement();
    if (obj instanceof final IFile file) {
      final String ext = file.getFileExtension();
      this.graphName = file.getName();
      if (this.graphName == null) {
        throw new PreesmRuntimeException();
      }
      final int idx = this.graphName.indexOf(ext);
      if (idx != -1) {
        this.graphName = this.graphName.substring(0, idx - 1);
      }
    }

    setTitle("Choose file name and parent folder");
  }

  /**
   * Creates the graph.
   *
   * @param path
   *          the path
   * @return the pi graph
   */
  private PiGraph createGraph() {
    this.graphName = getFileName();
    final int idx = this.graphName.indexOf("diagram");
    if (idx != -1) {
      this.graphName = this.graphName.substring(0, idx - 1);
    }

    final PiGraph graph = PiMMUserFactory.instance.createPiGraph();
    graph.setName(this.graphName);

    return graph;
  }

  /**
   * Save graph.
   *
   * @param set
   *          the set
   * @param path
   *          the path
   * @param graph
   *          the graph
   */
  private void saveGraph(final ResourceSet set, final IPath path, final PiGraph graph) {
    final URI uri = URI.createPlatformResourceURI(path.toString(), true);

    // Following lines corresponds to a copy of
    // EcoreHelper.putEObject(set, uri, graph)
    // from net.sf.orcc.util.util
    // @author mwipliez
    // date of copy 2012.10.12
    Resource resource = set.getResource(uri, false);
    if (resource == null) {
      resource = set.createResource(uri);
    } else {
      resource.getContents().clear();
    }

    resource.getContents().add(graph);
    try {
      resource.save(null);
    } catch (final IOException e) {
      PreesmLogger.getLogger().log(Level.WARNING, "Could not save PiGraph", e);
    }
    resource.setTrackingModification(true);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#getInitialContents()
   */
  @Override
  protected InputStream getInitialContents() {
    final IPath path = getContainerFullPath();

    // create graph
    final IPath piPath = path.append(getFileName()).removeFileExtension().addFileExtension("pi");
    final PiGraph graph = createGraph();

    // save graph
    final ResourceSet set = new ResourceSetImpl();
    saveGraph(set, piPath, graph);

    // create diagram
    final Diagram diagram = Graphiti.getPeCreateService().createDiagram("PiMM", this.graphName, true);

    // link diagram to network
    final PictogramLink link = PictogramsFactory.eINSTANCE.createPictogramLink();
    link.getBusinessObjects().add(graph);
    diagram.setLink(link);

    // create the resource (safe because the wizard does not allow existing
    // resources to be overridden)
    final URI uri = URI.createPlatformResourceURI(path.append(getFileName()).toString(), true);
    final Resource resource = set.createResource(uri);
    resource.getContents().add(diagram);

    // save to a byte array output stream
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try {
      resource.save(outputStream, null);
    } catch (final IOException e) {
      PreesmLogger.getLogger().log(Level.WARNING, "Could not save PiGraph", e);
    }

    return new ByteArrayInputStream(outputStream.toByteArray());
  }

}
