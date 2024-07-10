/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2021 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
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

package org.preesm.ui.pisdf.popup.actions;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.WorkspaceUtils;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Direction;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.Refinement;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.util.PrototypeFormatter;
import org.preesm.ui.utils.FileUtils;

/**
 * Popup menu action to generate the missing C header refinements of a PiGraph. Only one file is created, and only for
 * refinements of the top level.
 *
 * @author ahonorat
 */
public class PiSDFCHeaderGeneratorPopup extends AbstractGenericMultiplePiHandler {

  @Override
  public void processPiSDF(final PiGraph pigraph, final IProject iProject, final Shell shell) {

    // get all Actors without refinement
    final List<Actor> actorsWithoutRefinement = pigraph.getActorsWithRefinement().stream().filter(it -> {
      final Refinement ref = it.getRefinement();
      if (ref == null) {
        return true;
      }
      if (ref instanceof final CHeaderRefinement cref) {
        return (cref.getLoopPrototype() == null);
      }
      return false;
    }).toList();

    // if no actor needs new refinement, then do nothing
    if (actorsWithoutRefinement.isEmpty()) {
      MessageDialog.openInformation(shell, "PiGraph C header refinement generator",
          "The given PiSDF " + pigraph.getName() + " already has all its refinements.");
      return;
    }

    // get the target folder
    final IPath targetFolder = FileUtils.browseFiles(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
        "Select result location",
        "Select the folder where to write the computed refinement for PiGraph " + pigraph.getName() + ".",
        (Collection<String>) null);
    if (targetFolder != null) {
      // save the file
      final String fileName = pigraph.getName() + "_header.h";
      final URI uri = FileUtils.getPathToFileInFolder(iProject, targetFolder, fileName);

      // Get the project
      final String platformString = uri.toPlatformString(true);
      final IFile documentFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(platformString));
      final String osString = documentFile.getLocation().toOSString();
      try (final OutputStream outStream = new FileOutputStream(osString);) {
        // compute and write the result
        createAndPrintMissingRefinements(outStream, pigraph, actorsWithoutRefinement);
      } catch (final IOException e) {
        throw new PreesmRuntimeException("Could not open outputstream file " + uri.toPlatformString(false));
      }

      WorkspaceUtils.updateWorkspace();
      // should we save the changes of the pigraph as well?

    }

  }

  private static void createAndPrintMissingRefinements(final OutputStream outStream, final PiGraph piGraph,
      final List<Actor> actorsWithoutRefinement) throws IOException {
    final OutputStreamWriter streamWriter = new OutputStreamWriter(outStream);
    streamWriter.write("// autogenerated header for PiSDF graph: " + piGraph.getUrl() + "\n");

    final String headerName = "PREESM_REFINEMENT_OF_" + piGraph.getName().toUpperCase();
    streamWriter.write("#ifndef " + headerName + "\n");
    streamWriter.write("#define " + headerName + "\n");

    for (final Actor a : actorsWithoutRefinement) {
      // create the refinement
      if (a.getRefinement() == null) {
        a.setRefinement(PiMMUserFactory.instance.createCHeaderRefinement());
      }
      final CHeaderRefinement cref = (CHeaderRefinement) a.getRefinement();
      final FunctionPrototype loopProto = PiMMUserFactory.instance.createFunctionPrototype();
      cref.setLoopPrototype(loopProto);

      loopProto.setName(a.getName());
      // we do it in this order so the parameters appear first
      setFunctionArguments(a.getConfigInputPorts(), loopProto);
      setFunctionArguments(a.getConfigOutputPorts(), loopProto);
      setFunctionArguments(a.getDataInputPorts(), loopProto);
      setFunctionArguments(a.getDataOutputPorts(), loopProto);

      // print the refinement
      streamWriter.write(PrototypeFormatter.formatAsCDeclaration(loopProto) + "\n");
    }

    streamWriter.write("#endif //" + headerName + "\n");
    streamWriter.close();
  }

  private static void setFunctionArguments(final List<? extends Port> ports, final FunctionPrototype proto) {
    final EList<FunctionArgument> protoParameters = proto.getArguments();
    for (final Port p : ports) {
      final FunctionArgument fA = PiMMUserFactory.instance.createFunctionArgument();
      protoParameters.add(fA);
      fA.setName(p.getName());
      if (p instanceof ConfigInputPort) {
        fA.setIsConfigurationParameter(true);
        fA.setDirection(Direction.IN);
        fA.setType("int");
      } else if (p instanceof ConfigOutputPort) {
        fA.setIsConfigurationParameter(true);
        fA.setDirection(Direction.OUT);
        fA.setType("int"); // or should we check the fifo type ?
      } else if (p instanceof final DataPort dataPort) {
        fA.setIsConfigurationParameter(false);
        if (p instanceof DataInputPort) {
          fA.setDirection(Direction.IN);
        } else {
          fA.setDirection(Direction.OUT);
        }
        final Fifo f = dataPort.getFifo();
        if (f != null) {
          fA.setType(f.getType());
        } else {
          fA.setType("void");
        }
      }
    }

  }

}
