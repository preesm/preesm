/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
package org.ietr.preesm.experiment.model.transformation.properties;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.context.impl.LayoutContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.ExecutableActor;
import org.ietr.preesm.experiment.ui.pimm.features.ClearActorMemoryScriptFeature;
import org.ietr.preesm.experiment.ui.pimm.features.ClearActorRefinementFeature;
import org.ietr.preesm.experiment.ui.pimm.features.OpenMemoryScriptFeature;
import org.ietr.preesm.experiment.ui.pimm.features.OpenRefinementFeature;
import org.ietr.preesm.experiment.ui.pimm.features.SetActorMemoryScriptFeature;
import org.ietr.preesm.experiment.ui.pimm.features.SetActorRefinementFeature;

/**
 * Properties Section used for Actors
 * 
 * @author jheulot
 * 
 */
public class ActorPropertiesSection extends GFPropertySection implements
		ITabbedPropertyConstants {

	/**
	 * Items of the {@link ActorPropertiesSection}
	 */
	private CLabel lblName;
	private Text txtNameObj;
	private CLabel lblRefinement;
	private CLabel lblRefinementObj;

	private Button butRefinementClear;
	private Button butRefinementEdit;
	private Button butRefinementOpen;

	private CLabel lblMemoryScript;
	private CLabel lblMemoryScriptObj;

	private Button butMemoryScriptClear;
	private Button butMemoryScriptEdit;
	private Button butMemoryScriptOpen;

	private final int FIRST_COLUMN_WIDTH = 150;

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);
		FormData data;

		/**** NAME ****/
		txtNameObj = factory.createText(composite, "");
		data = new FormData();
		data.left = new FormAttachment(0, FIRST_COLUMN_WIDTH);
		data.right = new FormAttachment(50, 0);
		txtNameObj.setLayoutData(data);
		txtNameObj.setEnabled(true);

		lblName = factory.createCLabel(composite, "Name:");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(txtNameObj, -HSPACE);
		lblName.setLayoutData(data);

		/**
		 * Refinement
		 */

		/*** Clear Button ***/
		butRefinementClear = factory.createButton(composite, "Clear", SWT.PUSH);
		data = new FormData();
		data.left = new FormAttachment(100, -100);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(txtNameObj);
		butRefinementClear.setLayoutData(data);
		butRefinementClear.setEnabled(true);

		/*** Edit Button ***/
		butRefinementEdit = factory.createButton(composite, "Edit", SWT.PUSH);
		data = new FormData();
		data.left = new FormAttachment(100, -205);
		data.right = new FormAttachment(100, -105);
		data.top = new FormAttachment(txtNameObj);
		butRefinementEdit.setLayoutData(data);
		butRefinementEdit.setEnabled(true);

		/*** Open Button ***/
		butRefinementOpen = factory.createButton(composite, "Open", SWT.PUSH);
		data = new FormData();
		data.left = new FormAttachment(100, -310);
		data.right = new FormAttachment(100, -210);
		data.top = new FormAttachment(txtNameObj);
		butRefinementOpen.setLayoutData(data);
		butRefinementOpen.setEnabled(true);

		/**** Refinement ****/
		lblRefinementObj = factory.createCLabel(composite, "");
		data = new FormData();
		data.left = new FormAttachment(0, FIRST_COLUMN_WIDTH);
		data.right = new FormAttachment(butRefinementEdit, 0);
		data.top = new FormAttachment(txtNameObj);
		lblRefinementObj.setLayoutData(data);
		lblRefinementObj.setEnabled(true);

		lblRefinement = factory.createCLabel(composite, "Refinement:");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(lblRefinementObj, -HSPACE);
		data.top = new FormAttachment(lblName);
		lblRefinement.setLayoutData(data);

		/*** Clear Button Listener ***/
		butRefinementClear.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PictogramElement pes[] = new PictogramElement[1];
				pes[0] = getSelectedPictogramElement();

				CustomContext context = new CustomContext(pes);
				ICustomFeature[] clearRefinementFeature = getDiagramTypeProvider()
						.getFeatureProvider().getCustomFeatures(context);

				for (ICustomFeature feature : clearRefinementFeature) {
					if (feature instanceof ClearActorRefinementFeature) {
						getDiagramTypeProvider().getDiagramBehavior()
								.executeFeature(feature, context);
						LayoutContext contextLayout = new LayoutContext(
								getSelectedPictogramElement());
						ILayoutFeature layoutFeature = getDiagramTypeProvider()
								.getFeatureProvider().getLayoutFeature(
										contextLayout);
						getDiagramTypeProvider().getDiagramBehavior()
								.executeFeature(layoutFeature, contextLayout);
					}
				}

				refresh();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

		});

		/*** Edit Button Listener ***/
		butRefinementEdit.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PictogramElement pes[] = new PictogramElement[1];
				pes[0] = getSelectedPictogramElement();

				CustomContext context = new CustomContext(pes);
				ICustomFeature[] setRefinementFeature = getDiagramTypeProvider()
						.getFeatureProvider().getCustomFeatures(context);

				for (ICustomFeature feature : setRefinementFeature) {
					if (feature instanceof SetActorRefinementFeature) {
						getDiagramTypeProvider().getDiagramBehavior()
								.executeFeature(feature, context);
						LayoutContext contextLayout = new LayoutContext(
								getSelectedPictogramElement());
						ILayoutFeature layoutFeature = getDiagramTypeProvider()
								.getFeatureProvider().getLayoutFeature(
										contextLayout);
						getDiagramTypeProvider().getDiagramBehavior()
								.executeFeature(layoutFeature, contextLayout);
					}
				}

				refresh();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

		});

		/*** Open Button Listener ***/
		butRefinementOpen.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PictogramElement pes[] = new PictogramElement[1];
				pes[0] = getSelectedPictogramElement();

				CustomContext context = new CustomContext(pes);
				ICustomFeature[] openRefinementFeature = getDiagramTypeProvider()
						.getFeatureProvider().getCustomFeatures(context);

				for (ICustomFeature feature : openRefinementFeature) {
					if (feature instanceof OpenRefinementFeature) {
						getDiagramTypeProvider().getDiagramBehavior()
								.executeFeature(feature, context);
						LayoutContext contextLayout = new LayoutContext(
								getSelectedPictogramElement());
						ILayoutFeature layoutFeature = getDiagramTypeProvider()
								.getFeatureProvider().getLayoutFeature(
										contextLayout);
						getDiagramTypeProvider().getDiagramBehavior()
								.executeFeature(layoutFeature, contextLayout);
					}
				}

				refresh();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

		});

		/*** Name box listener ***/

		txtNameObj.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				PictogramElement pe = getSelectedPictogramElement();
				if (pe != null) {
					EObject bo = Graphiti.getLinkService()
							.getBusinessObjectForLinkedPictogramElement(pe);
					if (bo == null)
						return;

					if (bo instanceof ExecutableActor) {
						ExecutableActor actor = (ExecutableActor) bo;
						if (txtNameObj.getText().compareTo(actor.getName()) != 0) {
							setNewName(actor, txtNameObj.getText());
							getDiagramTypeProvider().getDiagramBehavior()
									.refreshContent();
						}
					}// end Actor
				}
			}
		});

		/**
		 * Memory script
		 */
		createMemoryScriptControl(data, factory, composite);
	}

	private void createMemoryScriptControl(FormData data,
			TabbedPropertySheetWidgetFactory factory, Composite composite) {
		/*** Clear Button ***/
		butMemoryScriptClear = factory.createButton(composite, "Clear",
				SWT.PUSH);
		data = new FormData();
		data.left = new FormAttachment(100, -100);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(butRefinementClear);
		butMemoryScriptClear.setLayoutData(data);
		butMemoryScriptClear.setEnabled(true);

		/*** Edit Button ***/
		butMemoryScriptEdit = factory.createButton(composite, "Edit", SWT.PUSH);
		data = new FormData();
		data.left = new FormAttachment(100, -205);
		data.right = new FormAttachment(100, -105);
		data.top = new FormAttachment(butRefinementEdit);
		butMemoryScriptEdit.setLayoutData(data);
		butMemoryScriptEdit.setEnabled(true);

		/*** Open Button ***/
		butMemoryScriptOpen = factory.createButton(composite, "Open", SWT.PUSH);
		data = new FormData();
		data.left = new FormAttachment(100, -310);
		data.right = new FormAttachment(100, -210);
		data.top = new FormAttachment(butRefinementOpen);
		butMemoryScriptOpen.setLayoutData(data);
		butMemoryScriptOpen.setEnabled(true);

		/**** Memory Script ****/
		lblMemoryScriptObj = factory.createCLabel(composite, "");
		data = new FormData();
		data.left = new FormAttachment(0, FIRST_COLUMN_WIDTH);
		data.right = new FormAttachment(butMemoryScriptEdit, 0);
		data.top = new FormAttachment(lblRefinementObj);
		lblMemoryScriptObj.setLayoutData(data);
		lblMemoryScriptObj.setEnabled(true);

		lblMemoryScript = factory.createCLabel(composite, "Memory script:");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(lblMemoryScriptObj, -HSPACE);
		data.top = new FormAttachment(lblRefinement);
		lblMemoryScript.setLayoutData(data);

		/*** Clear Button Listener ***/
		butMemoryScriptClear.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PictogramElement pes[] = new PictogramElement[1];
				pes[0] = getSelectedPictogramElement();

				CustomContext context = new CustomContext(pes);
				ICustomFeature[] clearMemoryScriptFeature = getDiagramTypeProvider()
						.getFeatureProvider().getCustomFeatures(context);

				for (ICustomFeature feature : clearMemoryScriptFeature) {
					if (feature instanceof ClearActorMemoryScriptFeature) {
						getDiagramTypeProvider().getDiagramBehavior()
								.executeFeature(feature, context);
						LayoutContext contextLayout = new LayoutContext(
								getSelectedPictogramElement());
						ILayoutFeature layoutFeature = getDiagramTypeProvider()
								.getFeatureProvider().getLayoutFeature(
										contextLayout);
						getDiagramTypeProvider().getDiagramBehavior()
								.executeFeature(layoutFeature, contextLayout);
					}
				}

				refresh();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

		});

		/*** Edit Button Listener ***/
		butMemoryScriptEdit.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PictogramElement pes[] = new PictogramElement[1];
				pes[0] = getSelectedPictogramElement();

				CustomContext context = new CustomContext(pes);
				ICustomFeature[] setMemoryScriptFeature = getDiagramTypeProvider()
						.getFeatureProvider().getCustomFeatures(context);

				for (ICustomFeature feature : setMemoryScriptFeature) {
					if (feature instanceof SetActorMemoryScriptFeature) {
						getDiagramTypeProvider().getDiagramBehavior()
								.executeFeature(feature, context);
						LayoutContext contextLayout = new LayoutContext(
								getSelectedPictogramElement());
						ILayoutFeature layoutFeature = getDiagramTypeProvider()
								.getFeatureProvider().getLayoutFeature(
										contextLayout);
						getDiagramTypeProvider().getDiagramBehavior()
								.executeFeature(layoutFeature, contextLayout);
					}
				}

				refresh();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

		});

		/*** Open Button Listener ***/
		butMemoryScriptOpen.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PictogramElement pes[] = new PictogramElement[1];
				pes[0] = getSelectedPictogramElement();

				CustomContext context = new CustomContext(pes);
				ICustomFeature[] openMemoryScriptFeature = getDiagramTypeProvider()
						.getFeatureProvider().getCustomFeatures(context);

				for (ICustomFeature feature : openMemoryScriptFeature) {
					if (feature instanceof OpenMemoryScriptFeature) {
						getDiagramTypeProvider().getDiagramBehavior()
								.executeFeature(feature, context);
						LayoutContext contextLayout = new LayoutContext(
								getSelectedPictogramElement());
						ILayoutFeature layoutFeature = getDiagramTypeProvider()
								.getFeatureProvider().getLayoutFeature(
										contextLayout);
						getDiagramTypeProvider().getDiagramBehavior()
								.executeFeature(layoutFeature, contextLayout);
					}
				}

				refresh();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

		});

	}

	/**
	 * Safely set a new name to the {@link Actor}.
	 * 
	 * @param actor
	 *            {@link Actor} to set
	 * @param value
	 *            String value
	 */
	private void setNewName(final ExecutableActor actor, final String value) {
		TransactionalEditingDomain editingDomain = getDiagramTypeProvider()
				.getDiagramBehavior().getEditingDomain();
		editingDomain.getCommandStack().execute(
				new RecordingCommand(editingDomain) {

					@Override
					protected void doExecute() {
						actor.setName(value);
					}
				});
	}

	@Override
	public void refresh() {
		PictogramElement pe = getSelectedPictogramElement();

		if (pe != null) {
			Object bo = Graphiti.getLinkService()
					.getBusinessObjectForLinkedPictogramElement(pe);
			if (bo == null)
				return;

			if (bo instanceof ExecutableActor) {
				ExecutableActor exexcutableActor = (ExecutableActor) bo;
				txtNameObj.setEnabled(false);
				if (exexcutableActor.getName() == null
						&& txtNameObj.getText() != "") {
					txtNameObj.setText("");
				} else if (txtNameObj.getText().compareTo(
						exexcutableActor.getName()) != 0) {
					txtNameObj.setText(exexcutableActor.getName());
				}
				txtNameObj.setEnabled(true);

				if (bo instanceof Actor) {
					Actor actor = (Actor) bo;
					if (actor.getRefinement().getFilePath() == null) {
						lblRefinementObj.setText("(none)");
						butRefinementClear.setEnabled(false);
						butRefinementEdit.setEnabled(true);
						butRefinementOpen.setEnabled(false);
					} else {
						IPath path = actor.getRefinement().getFilePath();
						String text = path.lastSegment();

						lblRefinementObj.setText(text);
						butRefinementClear.setEnabled(true);
						butRefinementEdit.setEnabled(true);
						butRefinementOpen.setEnabled(true);
					}

					if (actor.getMemoryScriptPath() == null) {
						lblMemoryScriptObj.setText("(none)");
						butMemoryScriptClear.setEnabled(false);
						butMemoryScriptEdit.setEnabled(true);
						butMemoryScriptOpen.setEnabled(false);
					} else {
						IPath path = actor.getMemoryScriptPath();
						String text = path.lastSegment();

						lblMemoryScriptObj.setText(text);
						butMemoryScriptClear.setEnabled(true);
						butMemoryScriptEdit.setEnabled(true);
						butMemoryScriptOpen.setEnabled(true);
					}
					lblRefinement.setVisible(true);
					lblRefinementObj.setVisible(true);
					butRefinementClear.setVisible(true);
					butRefinementEdit.setVisible(true);
					butRefinementOpen.setVisible(true);
					lblMemoryScript.setVisible(true);
					lblMemoryScriptObj.setVisible(true);
					butMemoryScriptClear.setVisible(true);
					butMemoryScriptEdit.setVisible(true);
					butMemoryScriptOpen.setVisible(true);
				} else {
					lblRefinement.setVisible(false);
					lblRefinementObj.setVisible(false);
					butRefinementClear.setVisible(false);
					butRefinementEdit.setVisible(false);
					butRefinementOpen.setVisible(false);
					lblMemoryScript.setVisible(false);
					lblMemoryScriptObj.setVisible(false);
					butMemoryScriptClear.setVisible(false);
					butMemoryScriptEdit.setVisible(false);
					butMemoryScriptOpen.setVisible(false);
				}

			}// end ExecutableActor

		}
	}
}
