/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2015 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Julien Hascoet <jhascoet@kalray.eu> (2016)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2015 - 2016)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2016)
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
package org.ietr.preesm.ui.pimm.features;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.eclipse.core.runtime.IPath;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.ui.pimm.util.PiMMUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The Class ExportSVGFeature.
 */
public class ExportSVGFeature extends AbstractCustomFeature {

  /**
   * Instantiates a new export SVG feature.
   *
   * @param fp
   *          the fp
   */
  public ExportSVGFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
   */
  @Override
  public String getName() {
    return "Export to SVG";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getDescription()
   */
  @Override
  public String getDescription() {
    return "Export Graph to a SVG image file.";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#canExecute(org.eclipse.graphiti.features.context.
   * ICustomContext)
   */
  @Override
  public boolean canExecute(final ICustomContext context) {
    return true;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.impl.AbstractFeature#hasDoneChanges()
   */
  @Override
  public boolean hasDoneChanges() {
    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.eclipse.graphiti.features.custom.ICustomFeature#execute(org.eclipse.graphiti.features.context.ICustomContext)
   */
  @Override
  public void execute(final ICustomContext context) {
    /* Get PiGraph */
    final PictogramElement[] pes = context.getPictogramElements();
    final Object bo = getBusinessObjectForPictogramElement(pes[0]);
    final PiGraph graph = (PiGraph) bo;

    exportPiGraphToSVG(graph);
  }

  private void exportPiGraphToSVG(final PiGraph graph) {
    /* Create Document Builder */
    final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder;
    try {
      builder = dbf.newDocumentBuilder();
    } catch (final ParserConfigurationException e) {

      e.printStackTrace();
      return;
    }
    final Document doc = builder.newDocument();

    /* Populate XML Files with File Header */
    final Element svg = doc.createElement("svg");
    doc.appendChild(svg);
    svg.setAttribute("font-family", "Arial");
    svg.setAttribute("xmlns", "http://www.w3.org/2000/svg");

    final Element defs = doc.createElement("defs");
    svg.appendChild(defs);

    final Element fifoMarker = doc.createElement("marker");
    defs.appendChild(fifoMarker);
    {
      fifoMarker.setAttribute("id", "fifoEnd");
      fifoMarker.setAttribute("markerWidth", "4");
      fifoMarker.setAttribute("markerHeight", "4");
      fifoMarker.setAttribute("refX", "4");
      fifoMarker.setAttribute("refY", "2");
      final Element polygon = doc.createElement("polygon");
      fifoMarker.appendChild(polygon);
      polygon.setAttribute("points", "0,0 5,2 0,4");
      polygon.setAttribute("fill", "rgb(100, 100, 100)");
      polygon.setAttribute("stroke-width", "none");
    }

    final Element depMarker = doc.createElement("marker");
    defs.appendChild(depMarker);
    {
      depMarker.setAttribute("id", "depEnd");
      depMarker.setAttribute("markerWidth", "4");
      depMarker.setAttribute("markerHeight", "4");
      depMarker.setAttribute("refX", "4");
      depMarker.setAttribute("refY", "2");
      final Element polygon = doc.createElement("polygon");
      depMarker.appendChild(polygon);
      polygon.setAttribute("points", "0,0 5,2 0,4");
      polygon.setAttribute("fill", "rgb(98, 131, 167)");
      polygon.setAttribute("stroke-width", "none");
    }

    /* Populate SVG File with Graph Data */
    final SVGExporterSwitch visitor = new SVGExporterSwitch(this, doc, svg);
    for (final Dependency d : graph.getDependencies()) {
      visitor.doSwitch(d);
    }
    for (final Fifo f : graph.getFifos()) {
      visitor.doSwitch(f);
    }
    for (final Parameter p : graph.getParameters()) {
      visitor.doSwitch(p);
    }
    for (final AbstractActor aa : graph.getActors()) {
      visitor.doSwitch(aa);
    }

    svg.setAttribute("width", "" + (visitor.getTotalWidth() + 20));
    svg.setAttribute("height", "" + (visitor.getTotalHeight() + 20));

    /* Write the SVG File */
    Transformer tf;
    try {
      tf = TransformerFactory.newInstance().newTransformer();
    } catch (TransformerConfigurationException | TransformerFactoryConfigurationError e) {
      e.printStackTrace();
      return;
    }
    tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    tf.setOutputProperty(OutputKeys.INDENT, "yes");
    tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

    /* Ask SVG File Location */
    final Set<String> fileExtensions = new LinkedHashSet<>();
    fileExtensions.add("*.svg");
    final IPath path = PiMMUtil.askSaveFile("Choose the exported SVG file", fileExtensions);

    if (path == null) {
      return;
    }

    final File svgFile = new File(path.toOSString());
    try (Writer out = new FileWriter(svgFile)) {
      try {
        tf.transform(new DOMSource(doc), new StreamResult(out));
      } catch (final TransformerException e) {
        e.printStackTrace();
        return;
      }
    } catch (final IOException e) {
      e.printStackTrace();
      return;
    }
  }
}
