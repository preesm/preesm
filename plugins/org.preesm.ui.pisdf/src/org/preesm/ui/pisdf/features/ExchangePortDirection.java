/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2021 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2023 - 2024)
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

package org.preesm.ui.pisdf.features;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.Direction;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.FunctionPrototype;
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
    // Should be "Exchange Port Direction In/Out\tCtrl+Arrow Right" but printing keyboard shortcut involving arrows
    // seems complicated.
    return "Exchange Port Direction In/Out (Ctrl+Arrow Right)";
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
      for (final PictogramElement pe : pes) {
        final Object bo = getBusinessObjectForPictogramElement(pe);
        if ((bo instanceof final Port portToExchange)
            && (portToExchange.eContainer() instanceof final ExecutableActor executableActor)) {

          final List<Pair<String, PortKind>> newPorts = actorsToNewPorts.computeIfAbsent(executableActor,
              k -> new ArrayList<>());

          // Switch Port into opposite direction
          final PortKind portKind = switch (portToExchange.getKind()) {
            case DATA_INPUT -> PortKind.DATA_OUTPUT;
            case DATA_OUTPUT -> PortKind.DATA_INPUT;
            case CFG_INPUT -> PortKind.CFG_OUTPUT;
            case CFG_OUTPUT -> PortKind.CFG_INPUT;
          };
          newPorts.add(new Pair<>(portToExchange.getName(), portKind));

          // Need to also change the direction in the refinement
          if ((executableActor instanceof final Actor actor)
              && (actor.getRefinement() instanceof final CHeaderRefinement cRef) && (cRef.getLoopPrototype() != null)) {
            final FunctionPrototype fp = cRef.getLoopPrototype();

            final Direction newDirection = switch (portToExchange.getKind()) {
              case DATA_INPUT -> Direction.OUT;
              case DATA_OUTPUT -> Direction.IN;
              case CFG_INPUT -> Direction.OUT;
              case CFG_OUTPUT -> Direction.IN;
            };

            fp.getArguments().stream().filter(fa -> fa.getName().equals(portToExchange.getName())).findFirst()
                .ifPresent(fa -> fa.setDirection(newDirection));
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
