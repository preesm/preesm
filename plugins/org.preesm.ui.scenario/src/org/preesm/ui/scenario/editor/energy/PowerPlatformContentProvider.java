/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Daniel Madroñal [daniel.madronal@upm.es] (2019)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2008 - 2013)
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
package org.preesm.ui.scenario.editor.energy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.preesm.model.scenario.EnergyConfig;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.ScenarioConstants;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.Design;

/**
 * Provides the elements contained in the static power of the platform editor.
 *
 * @author dmadronal
 */
public class PowerPlatformContentProvider implements IStructuredContentProvider {

  private List<Entry<String, Double>> elementList = null;

  @Override
  public Object[] getElements(final Object inputElement) {

    if (inputElement instanceof Scenario) {
      final Scenario inputScenario = (Scenario) inputElement;
      final Design design = inputScenario.getDesign();
      final EnergyConfig energyConfig = inputScenario.getEnergyConfig();

      /**
       * The base power goes apart, that's why we cannot use Component as key here
       */
      if (!energyConfig.getPlatformPower().containsKey("Base")) {
        energyConfig.getPlatformPower().put("Base", (double) ScenarioConstants.DEFAULT_POWER_PE.getValue());
      }
      /**
       * PE powers are added for all operator types if non present
       */
      for (final Component opDefId : design.getProcessingElements()) {
        if (!energyConfig.getPlatformPower().containsKey(opDefId.getVlnv().getName())) {
          energyConfig.getPlatformPower().put(opDefId.getVlnv().getName(),
              (double) ScenarioConstants.DEFAULT_POWER_PE.getValue());
        }
      }

      /**
       * Retrieving the PE powers in operator definition order
       */
      final Set<Entry<String, Double>> entrySet = energyConfig.getPlatformPower().entrySet();
      this.elementList = new ArrayList<>(entrySet);

      Collections.sort(this.elementList, (o1, o2) -> o1.getKey().compareTo(o2.getKey()));
    }
    return this.elementList.toArray();
  }

}
