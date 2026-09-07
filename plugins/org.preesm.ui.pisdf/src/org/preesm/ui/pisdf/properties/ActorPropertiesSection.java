/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2025) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2017 - 2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024 - 2025)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2013 - 2020)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2015)
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
package org.preesm.ui.pisdf.properties;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.context.impl.LayoutContext;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
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
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.PassiveActor;
import org.preesm.model.pisdf.PersistenceLevel;
import org.preesm.model.pisdf.Refinement;
import org.preesm.model.pisdf.RefinementContainer;
import org.preesm.model.pisdf.util.PrototypeFormatter;
import org.preesm.ui.pisdf.features.ClearActorMemoryScriptFeature;
import org.preesm.ui.pisdf.features.ClearActorRefinementFeature;
import org.preesm.ui.pisdf.features.OpenMemoryScriptFeature;
import org.preesm.ui.pisdf.features.OpenRefinementFeature;
import org.preesm.ui.pisdf.features.SetActorMemoryScriptFeature;
import org.preesm.ui.pisdf.features.SetActorRefinementFeature;

/**
 * Properties Section used for Actors.
 *
 * @author jheulot
 */
public class ActorPropertiesSection extends GFPropertySection implements ITabbedPropertyConstants {

  /** Items of the {@link ActorPropertiesSection}. */
  private Composite composite;

  /** The txt name obj. */
  private Text txtNameObj;

  // Refinement

  /** The lbl refinement. */
  private CLabel lblRefinement;

  /** The lbl refinement obj. */
  private CLabel lblRefinementObj;

  /** The lbl refinement view. */
  private CLabel lblRefinementView;

  /** The but refinement clear. */
  private Button butRefinementClear;

  /** The but refinement edit. */
  private Button butRefinementBrowse;

  /** The but refinement open. */
  private Button butRefinementOpen;

  // memory script

  /** The lbl memory script. */
  private CLabel lblMemoryScript;

  /** The lbl memory script obj. */
  private CLabel lblMemoryScriptObj;

  /** The but memory script clear. */
  private Button butMemoryScriptClear;

  /** The but memory script edit. */
  private Button butMemoryScriptBrowse;

  /** The but memory script open. */
  private Button butMemoryScriptOpen;

  // write passive script

  /** The lbl write passive script. */
  private CLabel lblWritePassive;

  /** The lbl write passive script obj. */
  private CLabel lblWritePassiveObj;

  /** The but write passive script clear. */
  private Button butWritePassiveClear;

  /** The but write passive script edit. */
  private Button butWritePassiveBrowse;

  /** The but write passive script open. */
  private Button butWritePassiveOpen;

  // read passive script

  /** The lbl read passive script. */
  private CLabel lblReadPassive;

  /** The lbl read passive script obj. */
  private CLabel lblReadPassiveObj;

  /** The but read passive script clear. */
  private Button butReadPassiveClear;

  /** The but read passive script edit. */
  private Button butReadPassiveBrowse;

  /** The but read passive script open. */
  private Button butReadPassiveOpen;

  // Buffer size for passive actor

  /** The buffer size of a passive actor */
  private CLabel lblBufferSizeObj;

  // Other

  /** The first column width. */
  private static final int FIRST_COLUMN_WIDTH = 150;

  private static final String NONE = "(none)";

  /*
   * (non-Javadoc)
   *
   * @see
   * org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#createControls(org.eclipse.swt.widgets.Composite,
   * org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
   */
  @Override
  public void createControls(final Composite parent, final TabbedPropertySheetPage tabbedPropertySheetPage) {

    super.createControls(parent, tabbedPropertySheetPage);

    final TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
    this.composite = factory.createFlatFormComposite(parent);

    FormData data;

    /**** NAME ****/
    this.txtNameObj = factory.createText(this.composite, "");
    data = new FormData();
    data.left = new FormAttachment(0, FIRST_COLUMN_WIDTH);
    data.right = new FormAttachment(50, 0);
    this.txtNameObj.setLayoutData(data);
    this.txtNameObj.setEnabled(true);

    /** The lbl name. */
    final CLabel lblName = factory.createCLabel(this.composite, "Name:");
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(this.txtNameObj, -ITabbedPropertyConstants.HSPACE);
    lblName.setLayoutData(data);

    /*** Name box listener ***/
    this.txtNameObj.addModifyListener(e -> {
      final PictogramElement pe = getSelectedPictogramElement();
      if (pe != null) {
        final EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
        if (bo == null) {
          return;
        }

        if (bo instanceof ExecutableActor) {
          final AbstractActor actor = (AbstractActor) bo;
          if (ActorPropertiesSection.this.txtNameObj.getText().compareTo(actor.getName()) != 0) {
            setNewName(actor, ActorPropertiesSection.this.txtNameObj.getText());
            getDiagramTypeProvider().getFeatureProvider().updateIfPossible(new UpdateContext(pe));
            getDiagramTypeProvider().getFeatureProvider().layoutIfPossible(new LayoutContext(pe));
          }
        } // end Actor
      }
    });

    /**
     * Refinement
     */
    createRefinementControl(factory, this.composite);

    /**
     * Memory script
     */
    createMemoryScriptControl(factory, this.composite);

    /**
     * Passive scripts
     */
    createWritePassiveScriptControl(factory, this.composite);
    createReadPassiveScriptControl(factory, this.composite);

    // buffer size
    this.lblBufferSizeObj = factory.createCLabel(composite, "");
    data = new FormData();
    data.left = new FormAttachment(0, FIRST_COLUMN_WIDTH);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(this.txtNameObj);
    this.lblBufferSizeObj.setLayoutData(data);

    final CLabel lblBufferSize;
    lblBufferSize = factory.createCLabel(composite, "Buffer size:");
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(this.lblBufferSizeObj, -ITabbedPropertyConstants.HSPACE);
    data.top = new FormAttachment(this.txtNameObj);
    lblBufferSize.setLayoutData(data);

  }

  /**
   * Create the part responsible for editing the refinement of the actor.
   *
   * @param factory
   *          the factory
   * @param composite
   *          the composite
   */
  protected void createRefinementControl(final TabbedPropertySheetWidgetFactory factory, final Composite composite) {

    /*** Clear Button ***/
    this.butRefinementClear = factory.createButton(composite, "Clear", SWT.PUSH);
    FormData data = new FormData();
    data.left = new FormAttachment(100, -100);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(this.txtNameObj);
    this.butRefinementClear.setLayoutData(data);
    this.butRefinementClear.setEnabled(true);

    /*** Browse Button ***/
    this.butRefinementBrowse = factory.createButton(composite, "Browse", SWT.PUSH);
    data = new FormData();
    data.left = new FormAttachment(100, -205);
    data.right = new FormAttachment(100, -105);
    data.top = new FormAttachment(this.txtNameObj);
    this.butRefinementBrowse.setLayoutData(data);
    this.butRefinementBrowse.setEnabled(true);

    /*** Open Button ***/
    this.butRefinementOpen = factory.createButton(composite, "Open", SWT.PUSH);
    data = new FormData();
    data.left = new FormAttachment(100, -310);
    data.right = new FormAttachment(100, -210);
    data.top = new FormAttachment(this.txtNameObj);
    this.butRefinementOpen.setLayoutData(data);
    this.butRefinementOpen.setEnabled(true);

    /**** Refinement ****/
    this.lblRefinementObj = factory.createCLabel(composite, "");
    data = new FormData();
    data.left = new FormAttachment(0, FIRST_COLUMN_WIDTH);
    data.right = new FormAttachment(this.butRefinementBrowse, 0);
    data.top = new FormAttachment(this.txtNameObj);
    this.lblRefinementObj.setLayoutData(data);
    this.lblRefinementObj.setEnabled(true);

    this.lblRefinement = factory.createCLabel(composite, "Refinement:");
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(this.lblRefinementObj, -ITabbedPropertyConstants.HSPACE);
    data.top = new FormAttachment(this.txtNameObj);
    this.lblRefinement.setLayoutData(data);

    /**** Refinement view ****/
    this.lblRefinementView = factory.createCLabel(composite, "loop: \n init:");
    data = new FormData();
    data.left = new FormAttachment(0, FIRST_COLUMN_WIDTH);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(this.lblRefinement);
    data.height = 30;
    this.lblRefinementView.setLayoutData(data);
    this.lblRefinementView.setEnabled(true);

    /*** Clear Button Listener ***/
    this.butRefinementClear.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(final SelectionEvent e) {
        final PictogramElement[] pes = new PictogramElement[1];
        pes[0] = getSelectedPictogramElement();

        final CustomContext context = new CustomContext(pes);
        final ICustomFeature[] clearRefinementFeature = getDiagramTypeProvider().getFeatureProvider()
            .getCustomFeatures(context);

        for (final ICustomFeature feature : clearRefinementFeature) {
          if (feature instanceof ClearActorRefinementFeature) {
            getDiagramTypeProvider().getDiagramBehavior().executeFeature(feature, context);
            final LayoutContext contextLayout = new LayoutContext(getSelectedPictogramElement());
            final ILayoutFeature layoutFeature = getDiagramTypeProvider().getFeatureProvider()
                .getLayoutFeature(contextLayout);
            if (layoutFeature != null) {
              // is null for Delay
              getDiagramTypeProvider().getDiagramBehavior().executeFeature(layoutFeature, contextLayout);
            }
          }
        }

        refresh();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent e) {
        // nothing by default
      }

    });

    /*** Browse Button Listener ***/
    this.butRefinementBrowse.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(final SelectionEvent e) {
        final PictogramElement[] pes = new PictogramElement[1];
        pes[0] = getSelectedPictogramElement();

        final CustomContext context = new CustomContext(pes);
        final ICustomFeature[] setRefinementFeature = getDiagramTypeProvider().getFeatureProvider()
            .getCustomFeatures(context);

        for (final ICustomFeature feature : setRefinementFeature) {
          if (feature instanceof SetActorRefinementFeature) {
            getDiagramTypeProvider().getDiagramBehavior().executeFeature(feature, context);
            final LayoutContext contextLayout = new LayoutContext(getSelectedPictogramElement());
            final ILayoutFeature layoutFeature = getDiagramTypeProvider().getFeatureProvider()
                .getLayoutFeature(contextLayout);
            if (layoutFeature != null) {
              // is null for Delay
              getDiagramTypeProvider().getDiagramBehavior().executeFeature(layoutFeature, contextLayout);
            }
          }
        }

        refresh();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent e) {
        // nothing by default
      }

    });

    /*** Open Button Listener ***/
    this.butRefinementOpen.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(final SelectionEvent e) {
        final PictogramElement[] pes = new PictogramElement[1];
        pes[0] = getSelectedPictogramElement();

        final CustomContext context = new CustomContext(pes);
        final ICustomFeature[] openRefinementFeature = getDiagramTypeProvider().getFeatureProvider()
            .getCustomFeatures(context);

        for (final ICustomFeature feature : openRefinementFeature) {
          if (feature instanceof OpenRefinementFeature) {
            getDiagramTypeProvider().getDiagramBehavior().executeFeature(feature, context);
            final LayoutContext contextLayout = new LayoutContext(getSelectedPictogramElement());
            final ILayoutFeature layoutFeature = getDiagramTypeProvider().getFeatureProvider()
                .getLayoutFeature(contextLayout);
            if (layoutFeature != null) {
              // is null for Delay
              getDiagramTypeProvider().getDiagramBehavior().executeFeature(layoutFeature, contextLayout);
            }
          }
        }

        refresh();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent e) {
        // nothing by default
      }

    });

  }

  /**
   * Creates the memory script control.
   *
   * @param factory
   *          the factory
   * @param composite
   *          the composite
   */
  protected void createMemoryScriptControl(final TabbedPropertySheetWidgetFactory factory, final Composite composite) {
    /*** Clear Button ***/
    this.butMemoryScriptClear = factory.createButton(composite, "Clear", SWT.PUSH);
    FormData data = new FormData();
    data.left = new FormAttachment(100, -100);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(this.lblRefinementView);
    this.butMemoryScriptClear.setLayoutData(data);
    this.butMemoryScriptClear.setEnabled(true);

    /*** Edit Button ***/
    this.butMemoryScriptBrowse = factory.createButton(composite, "Browse", SWT.PUSH);
    data = new FormData();
    data.left = new FormAttachment(100, -205);
    data.right = new FormAttachment(100, -105);
    data.top = new FormAttachment(this.lblRefinementView);
    this.butMemoryScriptBrowse.setLayoutData(data);
    this.butMemoryScriptBrowse.setEnabled(true);

    /*** Open Button ***/
    this.butMemoryScriptOpen = factory.createButton(composite, "Open", SWT.PUSH);
    data = new FormData();
    data.left = new FormAttachment(100, -310);
    data.right = new FormAttachment(100, -210);
    data.top = new FormAttachment(this.lblRefinementView);
    this.butMemoryScriptOpen.setLayoutData(data);
    this.butMemoryScriptOpen.setEnabled(true);

    /**** Memory Script ****/
    this.lblMemoryScriptObj = factory.createCLabel(composite, "");
    data = new FormData();
    data.left = new FormAttachment(0, FIRST_COLUMN_WIDTH);
    data.right = new FormAttachment(this.butMemoryScriptBrowse, 0);
    data.top = new FormAttachment(this.lblRefinementView);
    this.lblMemoryScriptObj.setLayoutData(data);
    this.lblMemoryScriptObj.setEnabled(true);

    this.lblMemoryScript = factory.createCLabel(composite, "Memory script:");
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(this.lblMemoryScriptObj, -ITabbedPropertyConstants.HSPACE);
    data.top = new FormAttachment(this.lblRefinementView);
    this.lblMemoryScript.setLayoutData(data);

    /*** Clear Button Listener ***/
    this.butMemoryScriptClear.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(final SelectionEvent e) {
        final PictogramElement[] pes = new PictogramElement[1];
        pes[0] = getSelectedPictogramElement();

        final CustomContext context = new CustomContext(pes);
        final ICustomFeature[] clearMemoryScriptFeature = getDiagramTypeProvider().getFeatureProvider()
            .getCustomFeatures(context);

        for (final ICustomFeature feature : clearMemoryScriptFeature) {
          if (feature instanceof ClearActorMemoryScriptFeature) {
            getDiagramTypeProvider().getDiagramBehavior().executeFeature(feature, context);
            final LayoutContext contextLayout = new LayoutContext(getSelectedPictogramElement());
            final ILayoutFeature layoutFeature = getDiagramTypeProvider().getFeatureProvider()
                .getLayoutFeature(contextLayout);
            getDiagramTypeProvider().getDiagramBehavior().executeFeature(layoutFeature, contextLayout);
          }
        }

        refresh();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent e) {
        // nothing by default
      }

    });

    /*** Edit Button Listener ***/
    this.butMemoryScriptBrowse.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(final SelectionEvent e) {
        final PictogramElement[] pes = new PictogramElement[1];
        pes[0] = getSelectedPictogramElement();

        final CustomContext context = new CustomContext(pes);
        final ICustomFeature[] setMemoryScriptFeature = getDiagramTypeProvider().getFeatureProvider()
            .getCustomFeatures(context);

        for (final ICustomFeature feature : setMemoryScriptFeature) {
          if (feature instanceof SetActorMemoryScriptFeature) {
            getDiagramTypeProvider().getDiagramBehavior().executeFeature(feature, context);
            final LayoutContext contextLayout = new LayoutContext(getSelectedPictogramElement());
            final ILayoutFeature layoutFeature = getDiagramTypeProvider().getFeatureProvider()
                .getLayoutFeature(contextLayout);
            getDiagramTypeProvider().getDiagramBehavior().executeFeature(layoutFeature, contextLayout);
          }
        }

        refresh();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent e) {
        // nothing by default
      }

    });

    /*** Open Button Listener ***/
    this.butMemoryScriptOpen.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(final SelectionEvent e) {
        final PictogramElement[] pes = new PictogramElement[1];
        pes[0] = getSelectedPictogramElement();

        final CustomContext context = new CustomContext(pes);
        final ICustomFeature[] openMemoryScriptFeature = getDiagramTypeProvider().getFeatureProvider()
            .getCustomFeatures(context);

        for (final ICustomFeature feature : openMemoryScriptFeature) {
          if (feature instanceof OpenMemoryScriptFeature) {
            getDiagramTypeProvider().getDiagramBehavior().executeFeature(feature, context);
            final LayoutContext contextLayout = new LayoutContext(getSelectedPictogramElement());
            final ILayoutFeature layoutFeature = getDiagramTypeProvider().getFeatureProvider()
                .getLayoutFeature(contextLayout);
            getDiagramTypeProvider().getDiagramBehavior().executeFeature(layoutFeature, contextLayout);
          }
        }

        refresh();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent e) {
        // nothing by default
      }

    });

  }

  /**
   * Creates the memory script control.
   *
   * @param factory
   *          the factory
   * @param composite
   *          the composite
   */
  protected void createWritePassiveScriptControl(final TabbedPropertySheetWidgetFactory factory,
      final Composite composite) {
    /*** Clear Button ***/
    this.butWritePassiveClear = factory.createButton(composite, "Clear", SWT.PUSH);
    FormData data = new FormData();
    data.left = new FormAttachment(100, -100);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(this.lblMemoryScript);
    data.top.offset = 10;
    this.butWritePassiveClear.setLayoutData(data);
    this.butWritePassiveClear.setEnabled(true);

    /*** Edit Button ***/
    this.butWritePassiveBrowse = factory.createButton(composite, "Browse", SWT.PUSH);
    data = new FormData();
    data.left = new FormAttachment(100, -205);
    data.right = new FormAttachment(100, -105);
    data.top = new FormAttachment(this.lblMemoryScript);
    data.top.offset = 10;
    this.butWritePassiveBrowse.setLayoutData(data);
    this.butWritePassiveBrowse.setEnabled(true);

    /*** Open Button ***/
    this.butWritePassiveOpen = factory.createButton(composite, "Open", SWT.PUSH);
    data = new FormData();
    data.left = new FormAttachment(100, -310);
    data.right = new FormAttachment(100, -210);
    data.top = new FormAttachment(this.lblMemoryScript);
    data.top.offset = 10;
    this.butWritePassiveOpen.setLayoutData(data);
    this.butWritePassiveOpen.setEnabled(true);

    /**** Memory Script ****/
    this.lblWritePassiveObj = factory.createCLabel(composite, "");
    data = new FormData();
    data.left = new FormAttachment(0, FIRST_COLUMN_WIDTH);
    data.right = new FormAttachment(this.butWritePassiveBrowse, 0);
    data.top = new FormAttachment(this.lblMemoryScript);
    data.top.offset = 10;
    this.lblWritePassiveObj.setLayoutData(data);
    this.lblWritePassiveObj.setEnabled(true);

    this.lblWritePassive = factory.createCLabel(composite, "Write passive script:");
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(this.lblWritePassiveObj, -ITabbedPropertyConstants.HSPACE);
    data.top = new FormAttachment(this.lblMemoryScript);
    data.top.offset = 10;
    this.lblWritePassive.setLayoutData(data);

    /*** Clear Button Listener ***/
    this.butWritePassiveClear.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(final SelectionEvent e) {
        final PictogramElement[] pes = new PictogramElement[1];
        pes[0] = getSelectedPictogramElement();

        final CustomContext context = new CustomContext(pes);
        final ICustomFeature[] clearMemoryScriptFeature = getDiagramTypeProvider().getFeatureProvider()
            .getCustomFeatures(context);

        for (final ICustomFeature feature : clearMemoryScriptFeature) {
          if (feature instanceof ClearActorMemoryScriptFeature) {
            getDiagramTypeProvider().getDiagramBehavior().executeFeature(feature, context);
            final LayoutContext contextLayout = new LayoutContext(getSelectedPictogramElement());
            final ILayoutFeature layoutFeature = getDiagramTypeProvider().getFeatureProvider()
                .getLayoutFeature(contextLayout);
            getDiagramTypeProvider().getDiagramBehavior().executeFeature(layoutFeature, contextLayout);
          }
        }

        refresh();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent e) {
        // nothing by default
      }

    });

    /*** Edit Button Listener ***/
    this.butWritePassiveBrowse.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(final SelectionEvent e) {
        final PictogramElement[] pes = new PictogramElement[1];
        pes[0] = getSelectedPictogramElement();

        final CustomContext context = new CustomContext(pes);
        final ICustomFeature[] setMemoryScriptFeature = getDiagramTypeProvider().getFeatureProvider()
            .getCustomFeatures(context);

        for (final ICustomFeature feature : setMemoryScriptFeature) {
          if (feature instanceof SetActorMemoryScriptFeature) {
            getDiagramTypeProvider().getDiagramBehavior().executeFeature(feature, context);
            final LayoutContext contextLayout = new LayoutContext(getSelectedPictogramElement());
            final ILayoutFeature layoutFeature = getDiagramTypeProvider().getFeatureProvider()
                .getLayoutFeature(contextLayout);
            getDiagramTypeProvider().getDiagramBehavior().executeFeature(layoutFeature, contextLayout);
          }
        }

        refresh();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent e) {
        // nothing by default
      }

    });

    /*** Open Button Listener ***/
    this.butWritePassiveOpen.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(final SelectionEvent e) {
        final PictogramElement[] pes = new PictogramElement[1];
        pes[0] = getSelectedPictogramElement();

        final CustomContext context = new CustomContext(pes);
        final ICustomFeature[] openMemoryScriptFeature = getDiagramTypeProvider().getFeatureProvider()
            .getCustomFeatures(context);

        for (final ICustomFeature feature : openMemoryScriptFeature) {
          if (feature instanceof OpenMemoryScriptFeature) {
            getDiagramTypeProvider().getDiagramBehavior().executeFeature(feature, context);
            final LayoutContext contextLayout = new LayoutContext(getSelectedPictogramElement());
            final ILayoutFeature layoutFeature = getDiagramTypeProvider().getFeatureProvider()
                .getLayoutFeature(contextLayout);
            getDiagramTypeProvider().getDiagramBehavior().executeFeature(layoutFeature, contextLayout);
          }
        }

        refresh();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent e) {
        // nothing by default
      }

    });

  }

  /**
   * Creates the memory script control.
   *
   * @param factory
   *          the factory
   * @param composite
   *          the composite
   */
  protected void createReadPassiveScriptControl(final TabbedPropertySheetWidgetFactory factory,
      final Composite composite) {
    /*** Clear Button ***/
    this.butReadPassiveClear = factory.createButton(composite, "Clear", SWT.PUSH);
    FormData data = new FormData();
    data.left = new FormAttachment(100, -100);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(this.lblWritePassive);
    data.top.offset = 10;
    this.butReadPassiveClear.setLayoutData(data);
    this.butReadPassiveClear.setEnabled(true);

    /*** Edit Button ***/
    this.butReadPassiveBrowse = factory.createButton(composite, "Browse", SWT.PUSH);
    data = new FormData();
    data.left = new FormAttachment(100, -205);
    data.right = new FormAttachment(100, -105);
    data.top = new FormAttachment(this.lblWritePassive);
    data.top.offset = 10;
    this.butReadPassiveBrowse.setLayoutData(data);
    this.butReadPassiveBrowse.setEnabled(true);

    /*** Open Button ***/
    this.butReadPassiveOpen = factory.createButton(composite, "Open", SWT.PUSH);
    data = new FormData();
    data.left = new FormAttachment(100, -310);
    data.right = new FormAttachment(100, -210);
    data.top = new FormAttachment(this.lblWritePassive);
    data.top.offset = 10;
    this.butReadPassiveOpen.setLayoutData(data);
    this.butReadPassiveOpen.setEnabled(true);

    /**** Memory Script ****/
    this.lblReadPassiveObj = factory.createCLabel(composite, "");
    data = new FormData();
    data.left = new FormAttachment(0, FIRST_COLUMN_WIDTH);
    data.right = new FormAttachment(this.butReadPassiveBrowse, 0);
    data.top = new FormAttachment(this.lblWritePassive);
    data.top.offset = 10;
    this.lblReadPassiveObj.setLayoutData(data);
    this.lblReadPassiveObj.setEnabled(true);

    this.lblReadPassive = factory.createCLabel(composite, "Read passive script:");
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(this.lblReadPassiveObj, -ITabbedPropertyConstants.HSPACE);
    data.top = new FormAttachment(this.lblWritePassive);
    data.top.offset = 10;
    this.lblReadPassive.setLayoutData(data);

    /*** Clear Button Listener ***/
    this.butReadPassiveClear.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(final SelectionEvent e) {
        final PictogramElement[] pes = new PictogramElement[1];
        pes[0] = getSelectedPictogramElement();

        final CustomContext context = new CustomContext(pes);
        final ICustomFeature[] clearMemoryScriptFeature = getDiagramTypeProvider().getFeatureProvider()
            .getCustomFeatures(context);

        for (final ICustomFeature feature : clearMemoryScriptFeature) {
          if (feature instanceof ClearActorMemoryScriptFeature) {
            getDiagramTypeProvider().getDiagramBehavior().executeFeature(feature, context);
            final LayoutContext contextLayout = new LayoutContext(getSelectedPictogramElement());
            final ILayoutFeature layoutFeature = getDiagramTypeProvider().getFeatureProvider()
                .getLayoutFeature(contextLayout);
            getDiagramTypeProvider().getDiagramBehavior().executeFeature(layoutFeature, contextLayout);
          }
        }

        refresh();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent e) {
        // nothing by default
      }

    });

    /*** Edit Button Listener ***/
    this.butReadPassiveBrowse.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(final SelectionEvent e) {
        final PictogramElement[] pes = new PictogramElement[1];
        pes[0] = getSelectedPictogramElement();

        final CustomContext context = new CustomContext(pes);
        final ICustomFeature[] setMemoryScriptFeature = getDiagramTypeProvider().getFeatureProvider()
            .getCustomFeatures(context);

        for (final ICustomFeature feature : setMemoryScriptFeature) {
          if (feature instanceof SetActorMemoryScriptFeature) {
            getDiagramTypeProvider().getDiagramBehavior().executeFeature(feature, context);
            final LayoutContext contextLayout = new LayoutContext(getSelectedPictogramElement());
            final ILayoutFeature layoutFeature = getDiagramTypeProvider().getFeatureProvider()
                .getLayoutFeature(contextLayout);
            getDiagramTypeProvider().getDiagramBehavior().executeFeature(layoutFeature, contextLayout);
          }
        }

        refresh();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent e) {
        // nothing by default
      }

    });

    /*** Open Button Listener ***/
    this.butReadPassiveOpen.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(final SelectionEvent e) {
        final PictogramElement[] pes = new PictogramElement[1];
        pes[0] = getSelectedPictogramElement();

        final CustomContext context = new CustomContext(pes);
        final ICustomFeature[] openMemoryScriptFeature = getDiagramTypeProvider().getFeatureProvider()
            .getCustomFeatures(context);

        for (final ICustomFeature feature : openMemoryScriptFeature) {
          if (feature instanceof OpenMemoryScriptFeature) {
            getDiagramTypeProvider().getDiagramBehavior().executeFeature(feature, context);
            final LayoutContext contextLayout = new LayoutContext(getSelectedPictogramElement());
            final ILayoutFeature layoutFeature = getDiagramTypeProvider().getFeatureProvider()
                .getLayoutFeature(contextLayout);
            getDiagramTypeProvider().getDiagramBehavior().executeFeature(layoutFeature, contextLayout);
          }
        }

        refresh();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent e) {
        // nothing by default
      }

    });

  }

  /**
   * Safely set a new name to the {@link Actor}.
   *
   * @param actor
   *          {@link Actor} to set
   * @param value
   *          String value
   */
  private void setNewName(final AbstractActor actor, final String value) {
    final TransactionalEditingDomain editingDomain = getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();
    editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {

      @Override
      protected void doExecute() {
        actor.setName(value);
      }
    });
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#refresh()
   */
  @Override
  public void refresh() {

    final PictogramElement pe = getSelectedPictogramElement();
    if (pe == null) {
      return;
    }

    final Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
    if ((bo == null) || (!(bo instanceof ExecutableActor) && !(bo instanceof Delay))) {
      return;
    }

    AbstractActor executableActor = null;
    if (bo instanceof final Delay delay) {
      executableActor = delay.getDelayActor();
    } else {
      executableActor = (AbstractActor) bo;
    }
    this.txtNameObj.setEnabled(false);
    if ((executableActor.getName() == null) && (!this.txtNameObj.getText().isEmpty())) {
      this.txtNameObj.setText("");
    } else if (this.txtNameObj.getText().compareTo(executableActor.getName()) != 0) {
      this.txtNameObj.setText(executableActor.getName());
    }
    this.txtNameObj.setEnabled(!(bo instanceof Delay));

    if (bo instanceof final PassiveActor pa) {
      PreesmLogger.getLogger().info("[DEBUG] " + pa.getName() + " buffer size is " + pa.getBufferSize());
      this.lblBufferSizeObj.setText(Integer.toString(pa.getBufferSize()));
    }

    if (bo instanceof Actor || bo instanceof InitActor || bo instanceof Delay) {
      PreesmLogger.getLogger().info("[DEBUG] going in if");

      Refinement refinement = null;
      boolean enabled = true;
      if (bo instanceof final Delay delay) {
        enabled = delay.getLevel() == PersistenceLevel.PERMANENT;
        refinement = delay.getDelayActor().getRefinement();
      } else {
        refinement = ((RefinementContainer) bo).getRefinement();
      }
      if ((refinement == null) || (refinement.getFilePath() == null)) {
        this.lblRefinementObj.setText(NONE);
        this.lblRefinementView.setText(NONE);
        this.butRefinementClear.setEnabled(false);
        this.butRefinementBrowse.setEnabled(enabled);
        this.butRefinementOpen.setEnabled(false);
      } else {
        final IPath path = new Path(refinement.getFilePath());
        final String text = path.lastSegment();
        this.lblRefinementObj.setText(text);

        String view = "";

        if (refinement instanceof final CHeaderRefinement cHeaderRefinement) {
          String tooltip = "";
          // Max length
          int maxLength = (int) ((this.composite.getBounds().width - FIRST_COLUMN_WIDTH) * 0.17);
          maxLength = Math.max(maxLength, 40);
          if (cHeaderRefinement.getLoopPrototype() != null) {
            final String loop = "loop: " + PrototypeFormatter.format(cHeaderRefinement.getLoopPrototype());
            view += (loop.length() <= maxLength) ? loop : loop.substring(0, maxLength) + "...";
            tooltip = loop;
          }
          if (cHeaderRefinement.getInitPrototype() != null) {
            final String init = "\ninit: " + PrototypeFormatter.format(cHeaderRefinement.getInitPrototype());
            view += (init.length() <= maxLength) ? init : init.substring(0, maxLength) + "...";
            tooltip += init;
          }
          this.lblRefinementView.setToolTipText(tooltip);
        }
        this.lblRefinementView.setText(view);
        this.butRefinementClear.setEnabled(true);
        this.butRefinementBrowse.setEnabled(enabled);
        this.butRefinementOpen.setEnabled(true);
      }

      this.lblRefinement.setVisible(true);
      this.lblRefinementObj.setVisible(true);
      this.lblRefinementView.setVisible(true);
      this.butRefinementClear.setVisible(true);
      this.butRefinementBrowse.setVisible(true);
      this.butRefinementOpen.setVisible(true);

      if (bo instanceof final Actor actor) {
        // memory script
        if (actor.getMemoryScriptPath() == null) {
          this.lblMemoryScriptObj.setText(NONE);
          this.butMemoryScriptClear.setEnabled(false);
          this.butMemoryScriptBrowse.setEnabled(true);
          this.butMemoryScriptOpen.setEnabled(false);
        } else {
          final IPath path = new Path(actor.getMemoryScriptPath());
          final String text = path.lastSegment();

          this.lblMemoryScriptObj.setText(text);
          this.butMemoryScriptClear.setEnabled(true);
          this.butMemoryScriptBrowse.setEnabled(true);
          this.butMemoryScriptOpen.setEnabled(true);
        }

        // write passive script
        if (actor.getWritePassiveScriptPath() == null) {
          this.lblWritePassiveObj.setText(NONE);
          this.butWritePassiveClear.setEnabled(false);
          this.butWritePassiveBrowse.setEnabled(true);
          this.butWritePassiveOpen.setEnabled(false);
        } else {
          final IPath path = new Path(actor.getWritePassiveScriptPath());
          final String text = path.lastSegment();

          this.lblWritePassiveObj.setText(text);
          this.butWritePassiveClear.setEnabled(true);
          this.butWritePassiveBrowse.setEnabled(true);
          this.butWritePassiveOpen.setEnabled(true);
        }

        // read passive script
        if (actor.getReadPassiveScriptPath() == null) {
          this.lblReadPassiveObj.setText(NONE);
          this.butReadPassiveClear.setEnabled(false);
          this.butReadPassiveBrowse.setEnabled(true);
          this.butReadPassiveOpen.setEnabled(false);
        } else {
          final IPath path = new Path(actor.getReadPassiveScriptPath());
          final String text = path.lastSegment();

          this.lblReadPassiveObj.setText(text);
          this.butReadPassiveClear.setEnabled(true);
          this.butReadPassiveBrowse.setEnabled(true);
          this.butReadPassiveOpen.setEnabled(true);
        }
        this.lblMemoryScript.setVisible(true);
        this.lblMemoryScriptObj.setVisible(true);
        this.butMemoryScriptClear.setVisible(true);
        this.butMemoryScriptBrowse.setVisible(true);
        this.butMemoryScriptOpen.setVisible(true);

        this.lblWritePassive.setVisible(true);
        this.lblWritePassiveObj.setVisible(true);
        this.butWritePassiveClear.setVisible(true);
        this.butWritePassiveBrowse.setVisible(true);
        this.butWritePassiveOpen.setVisible(true);

        this.lblReadPassive.setVisible(true);
        this.lblReadPassiveObj.setVisible(true);
        this.butReadPassiveClear.setVisible(true);
        this.butReadPassiveBrowse.setVisible(true);
        this.butReadPassiveOpen.setVisible(true);

      } else {
        this.lblMemoryScript.setVisible(false);
        this.lblMemoryScriptObj.setVisible(false);
        this.butMemoryScriptClear.setVisible(false);
        this.butMemoryScriptBrowse.setVisible(false);
        this.butMemoryScriptOpen.setVisible(false);

        this.lblWritePassive.setVisible(false);
        this.lblWritePassiveObj.setVisible(false);
        this.butWritePassiveClear.setVisible(false);
        this.butWritePassiveBrowse.setVisible(false);
        this.butWritePassiveOpen.setVisible(false);

        this.lblReadPassive.setVisible(false);
        this.lblReadPassiveObj.setVisible(false);
        this.butReadPassiveClear.setVisible(false);
        this.butReadPassiveBrowse.setVisible(false);
        this.butReadPassiveOpen.setVisible(false);
      }

    } else {
      this.lblRefinement.setVisible(false);
      this.lblRefinementObj.setVisible(false);
      this.lblRefinementView.setVisible(false);
      this.butRefinementClear.setVisible(false);
      this.butRefinementBrowse.setVisible(false);
      this.butRefinementOpen.setVisible(false);
      this.lblMemoryScript.setVisible(false);
      this.lblMemoryScriptObj.setVisible(false);
      this.butMemoryScriptClear.setVisible(false);
      this.butMemoryScriptBrowse.setVisible(false);
      this.butMemoryScriptOpen.setVisible(false);
      this.lblWritePassive.setVisible(false);
      this.lblWritePassiveObj.setVisible(false);
      this.butWritePassiveClear.setVisible(false);
      this.butWritePassiveBrowse.setVisible(false);
      this.butWritePassiveOpen.setVisible(false);
      this.lblReadPassive.setVisible(false);
      this.lblReadPassiveObj.setVisible(false);
      this.butReadPassiveClear.setVisible(false);
      this.butReadPassiveBrowse.setVisible(false);
      this.butReadPassiveOpen.setVisible(false);
    }
  } // end ExecutableActor

}
