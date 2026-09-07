
package org.preesm.ui.pisdf.properties;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.preesm.model.pisdf.PassiveInputPort;
import org.preesm.model.pisdf.PassiveOutputPort;
import org.preesm.model.pisdf.PassivePort;

/**
 * GUI properties section of passive ports.
 *
 * @author rcazoulat
 */
public class PassivePortPropertiesSection extends DataPortPropertiesSection {

  private CLabel lblBufferSizeObj;

  private CLabel lblOffsetObj;

  private CLabel lblPassiveScriptObj;

  @Override
  public void createControls(final Composite parent, final TabbedPropertySheetPage tabbedPropertySheetPage) {
    CLabel lblBufferSize;
    CLabel lblOffset;
    CLabel lblPassiveScript;

    super.createControls(parent, tabbedPropertySheetPage);

    FormData data;

    // buffer size

    this.lblBufferSizeObj = factory.createCLabel(composite, "");
    data = new FormData();
    data.left = new FormAttachment(0, FIRST_COLUMN_WIDTH);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(this.memoryLabelAnnotation);
    this.lblBufferSizeObj.setLayoutData(data);

    lblBufferSize = factory.createCLabel(composite, "Buffer size:");
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(this.lblBufferSizeObj, -ITabbedPropertyConstants.HSPACE);
    data.top = new FormAttachment(this.memoryLabelAnnotation);
    lblBufferSize.setLayoutData(data);

    // offset

    this.lblOffsetObj = factory.createCLabel(composite, "");
    data = new FormData();
    data.left = new FormAttachment(0, FIRST_COLUMN_WIDTH);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(lblBufferSize);
    this.lblOffsetObj.setLayoutData(data);

    lblOffset = factory.createCLabel(composite, "Buffer offset:");
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(this.lblOffsetObj, -ITabbedPropertyConstants.HSPACE);
    data.top = new FormAttachment(lblBufferSize);
    lblOffset.setLayoutData(data);

    // passive script

    this.lblPassiveScriptObj = factory.createCLabel(composite, "");
    data = new FormData();
    data.left = new FormAttachment(0, FIRST_COLUMN_WIDTH);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(lblOffset);
    this.lblPassiveScriptObj.setLayoutData(data);

    lblPassiveScript = factory.createCLabel(composite, "Passive script :");
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(this.lblPassiveScriptObj, -ITabbedPropertyConstants.HSPACE);
    data.top = new FormAttachment(lblOffset);
    lblPassiveScript.setLayoutData(data);

    refresh();

  }

  @Override
  void updateProperties() {

    refresh();
  }

  @Override
  public void refresh() {

    final PictogramElement pe = getSelectedPictogramElement();
    if (pe == null) {
      return;
    }

    final EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);

    if (!(bo instanceof final PassivePort pa)) {
      return;
    }
    this.lblBufferSizeObj.setText(Integer.toString(pa.getSubBufferSize()));
    this.lblOffsetObj.setText(Integer.toString(pa.getOffset()));
    final String scriptPath = pa instanceof final PassiveInputPort p ? p.getWritePassiveScriptPath()
        : ((PassiveOutputPort) pa).getReadPassiveScriptPath();
    this.lblPassiveScriptObj.setText(scriptPath);

    super.refresh();
  }
}
