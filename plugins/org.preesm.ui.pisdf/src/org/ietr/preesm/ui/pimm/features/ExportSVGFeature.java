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
import org.eclipse.core.runtime.IPath;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ietr.preesm.ui.pimm.util.PiMMUtil;
import org.ietr.preesm.ui.utils.ErrorWithExceptionDialog;
import org.preesm.model.pisdf.PiGraph;

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

    final SVGExporterSwitch visitor = new SVGExporterSwitch(this);
    final String svgContent = visitor.exportPiGraphToSVG(graph);

    /* Ask SVG File Location */
    final Set<String> fileExtensions = new LinkedHashSet<>();
    fileExtensions.add("*.svg");
    final IPath path = PiMMUtil.askSaveFile("Choose the exported SVG file", fileExtensions);

    if (path == null) {
      return;
    }

    final File svgFile = new File(path.toOSString());
    try (Writer out = new FileWriter(svgFile)) {
      out.append(svgContent);
    } catch (final IOException e) {
      ErrorWithExceptionDialog.errorDialogWithStackTrace("Could not write SVG file", e);
    }
  }

}
