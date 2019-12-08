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
            if (element instanceof FunctionPrototype) {
              return PrototypeFormatter.format((FunctionPrototype) element);
            } else {
              return "";
            }
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
