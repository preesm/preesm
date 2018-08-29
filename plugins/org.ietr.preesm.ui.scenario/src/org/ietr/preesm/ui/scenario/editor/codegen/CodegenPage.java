/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2011)
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
package org.ietr.preesm.ui.scenario.editor.codegen;

import java.util.LinkedHashSet;
import java.util.Set;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.ui.fields.FieldUtils;
import org.ietr.preesm.ui.scenario.editor.FileSelectionAdapter;
import org.ietr.preesm.ui.scenario.editor.Messages;

// TODO: Auto-generated Javadoc
/**
 * code generation properties editor within the implementation editor.
 *
 * @author mpelcat
 */
public class CodegenPage extends FormPage {

  /** Currently edited scenario. */
  private final PreesmScenario scenario;

  /**
   * Instantiates a new codegen page.
   *
   * @param scenario
   *          the scenario
   * @param editor
   *          the editor
   * @param id
   *          the id
   * @param title
   *          the title
   */
  public CodegenPage(final PreesmScenario scenario, final FormEditor editor, final String id, final String title) {
    super(editor, id, title);
    this.scenario = scenario;
  }

  /**
   * Initializes the display content.
   *
   * @param managedForm
   *          the managed form
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    super.createFormContent(managedForm);

    final ScrolledForm form = managedForm.getForm();
    form.setText(Messages.getString("Codegen.title"));

    final ColumnLayout layout = new ColumnLayout();
    layout.topMargin = 0;
    layout.bottomMargin = 5;
    layout.leftMargin = 10;
    layout.rightMargin = 10;
    layout.horizontalSpacing = 10;
    layout.verticalSpacing = 10;
    layout.maxNumColumns = 4;
    layout.minNumColumns = 1;
    form.getBody().setLayout(layout);

    final Set<String> algoExtensions = new LinkedHashSet<>();
    algoExtensions.add("graphml");

    // Algorithm file chooser section
    createDirectorySection(managedForm, Messages.getString("Codegen.codeDirectory"),
        Messages.getString("Codegen.codeDirectoryDescription"), Messages.getString("Codegen.codeDirectoryEdit"),
        this.scenario.getCodegenManager().getCodegenDirectory(),
        Messages.getString("Codegen.codeDirectoryBrowseTitle"));

    managedForm.refresh();
    managedForm.reflow(true);

  }

  /**
   * Creates a blank section with expansion capabilities.
   *
   * @param mform
   *          the mform
   * @param title
   *          the title
   * @param desc
   *          the desc
   * @param numColumns
   *          the num columns
   * @return the composite
   */
  private Composite createSection(final IManagedForm mform, final String title, final String desc,
      final int numColumns) {

    final ScrolledForm form = mform.getForm();
    final FormToolkit toolkit = mform.getToolkit();
    final Section section = toolkit.createSection(form.getBody(), ExpandableComposite.TWISTIE
        | ExpandableComposite.TITLE_BAR | Section.DESCRIPTION | ExpandableComposite.EXPANDED);
    section.setText(title);
    section.setDescription(desc);

    toolkit.createCompositeSeparator(section);
    final Composite client = toolkit.createComposite(section);
    final GridLayout layout = new GridLayout();
    layout.marginWidth = layout.marginHeight = 0;
    layout.numColumns = numColumns;
    client.setLayout(layout);
    section.setClient(client);
    section.addExpansionListener(new ExpansionAdapter() {
      @Override
      public void expansionStateChanged(final ExpansionEvent e) {
        form.reflow(false);
      }
    });
    return client;
  }

  /**
   * Creates a section to edit a directory.
   *
   * @param mform
   *          form containing the section
   * @param title
   *          section title
   * @param desc
   *          description of the section
   * @param fileEdit
   *          text to display in text label
   * @param initValue
   *          initial value of Text
   * @param browseTitle
   *          title of file browser
   */
  private void createDirectorySection(final IManagedForm mform, final String title, final String desc,
      final String fileEdit, final String initValue, final String browseTitle) {

    final Composite client = createSection(mform, title, desc, 2);

    final FormToolkit toolkit = mform.getToolkit();

    final GridData gd = new GridData();
    toolkit.createLabel(client, fileEdit);

    final Text text = toolkit.createText(client, initValue, SWT.SINGLE);
    colorRedIfFileAbsent(text);
    text.setData(title);
    text.addModifyListener(e -> {
      final Text text1 = (Text) e.getSource();
      colorRedIfFileAbsent(text1);
      CodegenPage.this.scenario.getCodegenManager().setCodegenDirectory(text1.getText());

      firePropertyChange(IEditorPart.PROP_DIRTY);

    });

    gd.widthHint = 400;
    text.setLayoutData(gd);

    final Button button = toolkit.createButton(client, Messages.getString("Codegen.browse"), SWT.PUSH);
    final SelectionAdapter adapter = new FileSelectionAdapter(text, client.getShell(), browseTitle);
    button.addSelectionListener(adapter);

    toolkit.paintBordersFor(client);
  }

  private void colorRedIfFileAbsent(final Text text) {
    final String codegenDirPath = text.getText().replaceAll("//", "/");
    final String sanitizedPath;
    if (codegenDirPath.startsWith("/")) {
      sanitizedPath = codegenDirPath.substring(1);
    } else {
      sanitizedPath = codegenDirPath;
    }
    final String rootSegment = sanitizedPath.substring(0, sanitizedPath.indexOf("/"));
    final boolean testPathValidInWorkspace = FieldUtils.testPathValidInWorkspace("/" + rootSegment);
    FieldUtils.colorRedOnCondition(text, !testPathValidInWorkspace);
  }

}
