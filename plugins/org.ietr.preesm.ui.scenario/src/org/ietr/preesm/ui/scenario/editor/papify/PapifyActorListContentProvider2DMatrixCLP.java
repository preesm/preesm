/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Daniel Madroñal <daniel.madronal@upm.es> (2018)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
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
package org.ietr.preesm.ui.scenario.editor.papify;

import java.util.Map;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.papi.PapifyConfigActor;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.ui.scenario.editor.HierarchicalSDFVertex;
import org.ietr.preesm.ui.scenario.editor.papify.PapifyListTreeElement.PAPIStatus;

// TODO: Auto-generated Javadoc
/**
 * Provides the elements contained in the papify editor.
 *
 * @author dmadronal
 */

/**
 *
 * @author anmorvan
 *
 */

class PapifyActorListContentProvider2DMatrixCLP extends ColumnLabelProvider {

  /** Currently edited scenario. */
  private PreesmScenario scenario = null;
  String                 actorName;

  public PapifyActorListContentProvider2DMatrixCLP(final PreesmScenario scenario, final String name) {
    this.actorName = name;
    this.scenario = scenario;
  }

  @Override
  public String getText(final Object element) {
    if (element instanceof PapifyListTreeElement) {
      final PapifyListTreeElement treeElement = (PapifyListTreeElement) element;
      final Map<String, PAPIStatus> statuses = treeElement.PAPIStatuses;
      if (!statuses.containsKey(this.actorName)) {
        statuses.put(this.actorName, PAPIStatus.NO);
        if (this.scenario.getPapifyConfigManager().getCorePapifyConfigGroupActor(this.actorName) == null) {
          this.scenario.getPapifyConfigManager().addPapifyConfigActorGroup(new PapifyConfigActor(this.actorName));
        }
      }
      return statuses.get(this.actorName).toString();
    } else if (element instanceof HierarchicalSDFVertex) {
      return ((HierarchicalSDFVertex) element).getName();
    } else if (element instanceof SDFGraph) {
      return "graph";
    } else if (element instanceof AbstractActor) {
      return ((AbstractActor) element).getName();
    } else {
      return "ERROR";
    }
  }

  @Override
  public Image getImage(final Object element) {
    if (element instanceof PapifyListTreeElement) {
      final PapifyListTreeElement treeElement = (PapifyListTreeElement) element;
      final Map<String, PAPIStatus> statuses = treeElement.PAPIStatuses;
      if (statuses.containsKey(this.actorName)) {
        return treeElement.getImage(this.actorName);
      }
    }
    return null;
  }

}
