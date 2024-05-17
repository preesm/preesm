/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
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
package org.preesm.ui.pisdf.util;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.platform.IDiagramBehavior;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.util.PrototypeFormatter;
import org.preesm.ui.pisdf.diagram.PiMMToolBehaviorProvider;
import org.preesm.ui.utils.DialogUtil;

/**
 * A few functions used for GUI: ask user or warn user.
 *
 * @author ahonorat
 */
public class PiMMUtil {

  private PiMMUtil() {
    // Forbids instantiation
  }

  /**
   * Select function.
   *
   * @param prototypes
   *          the prototypes
   * @param allProtoArray
   *          the all proto array
   * @param title
   *          the title
   * @param message
   *          the message
   * @param showOnlyValidPrototypes
   *          the show only valid prototypes
   * @return the function prototype
   */
  public static FunctionPrototype selectFunction(final FunctionPrototype[] prototypes,
      final FunctionPrototype[] allProtoArray, final String title, final String message,
      final boolean showOnlyValidPrototypes) {
    final ElementListSelectionDialog dialog = new ElementListSelectionDialog(DialogUtil.getShell(),
        new LabelProvider() {

          @Override
          public String getText(final Object element) {
            if (element instanceof final FunctionPrototype funcProto) {
              return PrototypeFormatter.format(funcProto);
            }
            return "";
          }
        }) {

      final FunctionPrototype[] filteredPrototypes = prototypes;
      final FunctionPrototype[] allPrototypes      = allProtoArray;

      protected void switchDisplayedPrototypes(final boolean filtered) {
        if (filtered) {
          setListElements(this.filteredPrototypes);
        } else {
          setListElements(this.allPrototypes);
        }

        // Trick to force an update
        setFilter(getFilter());
      }

      @Override
      protected Control createDialogArea(final Composite parent) {
        final Composite composite = (Composite) super.createDialogArea(parent);
        final GridData data = new GridData();
        data.grabExcessVerticalSpace = false;
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.BEGINNING;
        final Button check = new Button(composite, SWT.CHECK);
        check.setText("Show only functions with corresponding ports.");
        check.setSelection(showOnlyValidPrototypes);

        final SelectionListener listener = new SelectionAdapter() {
          @Override
          public void widgetSelected(final SelectionEvent e) {
            final boolean newState = ((Button) e.getSource()).getSelection();
            switchDisplayedPrototypes(newState);
          }
        };

        check.addSelectionListener(listener);

        return composite;
      }
    };

    dialog.setTitle(title);
    dialog.setMessage(message);
    if (showOnlyValidPrototypes) {
      dialog.setElements(prototypes);
    } else {
      dialog.setElements(allProtoArray);
    }

    final int retDialog = dialog.open();
    if (retDialog == Window.OK) {
      return (FunctionPrototype) (dialog.getResult()[0]);
    }
    return null;
  }

  /**
   * Utility method used to set a temporary tooltip message for a given {@link GraphicsAlgorithm}.
   *
   * @param fp
   *          the {@link IFeatureProvider}
   * @param ga
   *          the {@link GraphicsAlgorithm}
   * @param iDiagramEditor
   *          the {@link IDiagramEditor}
   * @param message
   *          the message to put in the tooltip
   */
  public static void setToolTip(final IFeatureProvider fp, final GraphicsAlgorithm ga,
      final IDiagramBehavior iDiagramEditor, final String message) {
    final IToolBehaviorProvider behaviorProvider = fp.getDiagramTypeProvider().getCurrentToolBehaviorProvider();
    ((PiMMToolBehaviorProvider) behaviorProvider).setToolTip(ga, message);

    iDiagramEditor.refresh();
    ((PiMMToolBehaviorProvider) behaviorProvider).setToolTip(ga, null);
  }

}
