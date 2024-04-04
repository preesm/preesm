package org.preesm.ui.pisdf.features;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.context.impl.MultiDeleteInfo;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.PortKind;

/**
 * Exchange the port category (from data to config. and vice versa).
 *
 * @author ahonorat
 */
public class ExchangePortCategory extends AbstractCustomFeature {

  /** The Constant HINT. */
  public static final String HINT = "exchangeCategory";

  /**
   * Default Constructor.
   *
   * @param fp
   *          the feature provider
   */
  public ExchangePortCategory(IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
   */
  @Override
  public String getName() {
    // Should be "Exchange Port Category Data/Config.\tCtrl+Arrow Left" but printing keyboard shortcut involving arrows
    // seems complicated.
    return "Exchange Port Category Data/Config (Ctrl+Arrow Left)";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getDescription()
   */
  @Override
  public String getDescription() {
    return "Exchange the port category (from Data to Configuration and vice versa).";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#canExecute(org.eclipse.graphiti.features.context.
   * ICustomContext)
   */
  @Override
  public boolean canExecute(final ICustomContext context) {
    boolean ret = false;
    final PictogramElement[] pes = context.getPictogramElements();
    if ((pes != null) && (pes.length > 0)) {
      for (final PictogramElement pe : pes) {
        final Object bo = getBusinessObjectForPictogramElement(pe);
        if (!(bo instanceof final Port port) || !(port.eContainer() instanceof ExecutableActor)) {
          ret = false;
          break;
        }
        ret = true;
      }
    }
    return ret;
  }

  @Override
  public void execute(ICustomContext context) {

    final PictogramElement[] pes = context.getPictogramElements();
    if ((pes != null) && (pes.length > 0)) {
      final Map<ExecutableActor, List<Pair<String, PortKind>>> actorsToNewPorts = new LinkedHashMap<>();

      // store new ports to create
      for (final PictogramElement pe : pes) {
        final Object bo = getBusinessObjectForPictogramElement(pe);
        if ((bo instanceof final Port portToExchange)
            && (portToExchange.eContainer() instanceof final ExecutableActor executableActor)) {

          final List<Pair<String, PortKind>> newPorts = actorsToNewPorts.computeIfAbsent(executableActor,
              k -> new ArrayList<>());

          // Switch Port into opposite category
          switch (portToExchange.getKind()) {
            case DATA_INPUT -> newPorts.add(new Pair<>(portToExchange.getName(), PortKind.CFG_INPUT));
            case DATA_OUTPUT -> newPorts.add(new Pair<>(portToExchange.getName(), PortKind.CFG_OUTPUT));
            case CFG_INPUT -> newPorts.add(new Pair<>(portToExchange.getName(), PortKind.DATA_INPUT));
            case CFG_OUTPUT -> newPorts.add(new Pair<>(portToExchange.getName(), PortKind.DATA_OUTPUT));
            default -> {
              // empty
            }
          }

          // Need to also change the direction in the refinement
          if ((executableActor instanceof final Actor actor)
              && (actor.getRefinement() instanceof final CHeaderRefinement cRef) && (cRef.getLoopPrototype() != null)) {
            final FunctionPrototype fp = cRef.getLoopPrototype();

            final Boolean isConfig = switch (portToExchange.getKind()) {
              case DATA_INPUT -> true;
              case DATA_OUTPUT -> true;
              case CFG_INPUT -> false;
              case CFG_OUTPUT -> false;
            };

            fp.getArguments().stream().filter(fa -> fa.getName().equals(portToExchange.getName())).findFirst()
                .ifPresent(fa -> fa.setIsConfigurationParameter(isConfig));
          }
        }
      }

      // delete old ports
      deletePortsToExchange(pes);
      // recreate ports with new category context
      recreateExchangedPorts(actorsToNewPorts);
    }
  }

  protected void deletePortsToExchange(final PictogramElement[] ports) {
    for (final PictogramElement pe : ports) {
      final DeleteActorPortFeature delPortFeature = new DeleteActorPortFeature(getFeatureProvider());
      final DeleteContext delCtxt = new DeleteContext(pe);
      final MultiDeleteInfo multi = new MultiDeleteInfo(false, false, 0);
      delCtxt.setMultiDeleteInfo(multi);
      delPortFeature.delete(delCtxt);
    }
  }

  protected void recreateExchangedPorts(final Map<ExecutableActor, List<Pair<String, PortKind>>> actorsToNewPorts) {
    for (final Entry<ExecutableActor, List<Pair<String, PortKind>>> e : actorsToNewPorts.entrySet()) {
      // set the current selected element to the actor where to add ports
      final PictogramElement[] peActor = new PictogramElement[1];
      peActor[0] = getFeatureProvider().getPictogramElementForBusinessObject(e.getKey());
      final List<Pair<String, PortKind>> newPorts = e.getValue();
      for (final Pair<String, PortKind> newPort : newPorts) {
        final String name = newPort.getKey();
        final PortKind kind = newPort.getValue();
        // we need a new context since the original one may have more than one pe,
        // which is not supported by the add features
        final CustomContext cc = new CustomContext(peActor);
        switch (kind) {
          case DATA_INPUT:
            final AddDataInputPortFeature adipf = new AddDataInputPortFeature(getFeatureProvider());
            adipf.setGivenName(name);
            adipf.execute(cc);
            break;
          case DATA_OUTPUT:
            final AddDataOutputPortFeature adopf = new AddDataOutputPortFeature(getFeatureProvider());
            adopf.setGivenName(name);
            adopf.execute(cc);
            break;
          case CFG_INPUT:
            final AddConfigInputPortFeature acipf = new AddConfigInputPortFeature(getFeatureProvider());
            acipf.setGivenName(name);
            acipf.execute(cc);
            break;
          case CFG_OUTPUT:
            final AddConfigOutputPortFeature acopf = new AddConfigOutputPortFeature(getFeatureProvider());
            acopf.setGivenName(name);
            acopf.execute(cc);
            break;
          default:
            break;
        }
      }
    }

  }

}
