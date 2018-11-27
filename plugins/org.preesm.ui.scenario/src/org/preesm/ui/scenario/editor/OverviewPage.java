/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011)
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
package org.preesm.ui.scenario.editor;

import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.CoreException;
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
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.model.scenario.PreesmScenario;
import org.preesm.ui.fields.FieldUtils;

/**
 * This page contains general informations of the scenario including current algorithm and current architecture.
 *
 * @author mpelcat
 */
public class OverviewPage extends FormPage {

  /** The current scenario being edited. */
  private final PreesmScenario scenario;

  /**
   * Instantiates a new overview page.
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
  public OverviewPage(final PreesmScenario scenario, final FormEditor editor, final String id, final String title) {
    super(editor, id, title);

    this.scenario = scenario;
  }

  /**
   * Creation of the sections and their initialization.
   *
   * @param managedForm
   *          the managed form
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {

    final ScrolledForm form = managedForm.getForm();
    form.setText(Messages.getString("Overview.title"));
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
    algoExtensions.add("pi");
    algoExtensions.add("graphml");

    // Algorithm file chooser section
    createFileSection(managedForm, Messages.getString("Overview.algorithmFile"),
        Messages.getString("Overview.algorithmDescription"), Messages.getString("Overview.algorithmFileEdit"),
        this.scenario.getAlgorithmURL(), Messages.getString("Overview.algorithmBrowseTitle"), algoExtensions);

    final Set<String> archiExtensions = new LinkedHashSet<>();
    archiExtensions.add("slam");
    archiExtensions.add("design");

    // Architecture file chooser section
    createFileSection(managedForm, Messages.getString("Overview.architectureFile"),
        Messages.getString("Overview.architectureDescription"), Messages.getString("Overview.architectureFileEdit"),
        this.scenario.getArchitectureURL(), Messages.getString("Overview.architectureBrowseTitle"), archiExtensions);

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
   * Creates a section to edit a file.
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
   * @param fileExtensions
   *          the file extensions
   */
  private void createFileSection(final IManagedForm mform, final String title, final String desc, final String fileEdit,
      final String initValue, final String browseTitle, final Set<String> fileExtensions) {

    final Composite client = createSection(mform, title, desc, 2);

    final FormToolkit toolkit = mform.getToolkit();

    final GridData gd = new GridData();
    toolkit.createLabel(client, fileEdit);

    final Text text = toolkit.createText(client, initValue, SWT.SINGLE);
    text.setData(title);
    colorRedIfFileAbsent(text);

    text.addModifyListener(e -> {
      final String type = ((String) text.getData());

      colorRedIfFileAbsent(text);

      final String path = FilenameUtils.separatorsToUnix(text.getText());
      if (type.equals(Messages.getString("Overview.algorithmFile"))) {
        OverviewPage.this.scenario.setAlgorithmURL(path);
        try {
          OverviewPage.this.scenario.update(true, false);
        } catch (PreesmException | CoreException ex) {
          ex.printStackTrace();
        }
      } else if (type.equals(Messages.getString("Overview.architectureFile"))) {
        OverviewPage.this.scenario.setArchitectureURL(path);
        try {
          OverviewPage.this.scenario.update(false, true);
        } catch (PreesmException | CoreException ex) {
          ex.printStackTrace();
        }
      }

      firePropertyChange(IEditorPart.PROP_DIRTY);

    });

    gd.widthHint = 400;
    text.setLayoutData(gd);

    final Button button = toolkit.createButton(client, Messages.getString("Overview.browse"), SWT.PUSH);
    final SelectionAdapter adapter = new FileSelectionAdapter(text, browseTitle, fileExtensions);
    button.addSelectionListener(adapter);

    toolkit.paintBordersFor(client);
  }

  private void colorRedIfFileAbsent(final Text text) {
    final String textFieldContent = text.getText();
    final boolean testPathValidInWorkspace = FieldUtils.testPathValidInWorkspace(textFieldContent);
    FieldUtils.colorRedOnCondition(text, !testPathValidInWorkspace);
  }

}
