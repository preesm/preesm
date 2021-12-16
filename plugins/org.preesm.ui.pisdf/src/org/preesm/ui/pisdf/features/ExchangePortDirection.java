package org.preesm.ui.pisdf.features;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.PortKind;

/**
 * Exchange the port direction (from in to out and vice versa).
 * 
 * @author ahonorat
 */
public class ExchangePortDirection extends ExchangePortCategory {

  /** The Constant HINT. */
  public static final String HINT = "exchangeDirection";

  /**
   * Default Constructor.
   *
   * @param fp
   *          the feature provider
   */
  public ExchangePortDirection(IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
   */
  @Override
  public String getName() {
    return "Exchange Port Direction In/Out";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getDescription()
   */
  @Override
  public String getDescription() {
    return "Exchange the port direction (from Input to Output and vice versa).";
  }

  @Override
  public void execute(ICustomContext context) {

    final PictogramElement[] pes = context.getPictogramElements();
    if ((pes != null) && (pes.length > 0)) {
      final Map<ExecutableActor, List<Pair<String, PortKind>>> actorsToNewPorts = new LinkedHashMap<>();

      // store new ports to create
      for (PictogramElement pe : pes) {
        final Object bo = getBusinessObjectForPictogramElement(pe);
        if ((bo instanceof Port) && ((Port) bo).eContainer() instanceof ExecutableActor) {
          final Port portToExchange = (Port) bo;
          final ExecutableActor actor = (ExecutableActor) (portToExchange.eContainer());

          final List<Pair<String, PortKind>> newPorts = actorsToNewPorts.computeIfAbsent(actor, k -> new ArrayList<>());

          // Switch Port into opposite direction
          switch (portToExchange.getKind()) {
            case DATA_INPUT:
              newPorts.add(new Pair<>(portToExchange.getName(), PortKind.DATA_OUTPUT));
              break;
            case DATA_OUTPUT:
              newPorts.add(new Pair<>(portToExchange.getName(), PortKind.DATA_INPUT));
              break;
            case CFG_INPUT:
              newPorts.add(new Pair<>(portToExchange.getName(), PortKind.CFG_OUTPUT));
              break;
            case CFG_OUTPUT:
              newPorts.add(new Pair<>(portToExchange.getName(), PortKind.CFG_INPUT));
              break;
            default:
              break;
          }
        }
      }

      // delete old ports
      deletePortsToExchange(pes);
      // recreate ports with new category context
      recreateExchangedPorts(actorsToNewPorts);

    }
  }

}
