/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2013 - 2020)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2012 - 2013)
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

import org.eclipse.graphiti.features.IFeatureProvider;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

/**
 * Create Feature for {@link Parameter}s.
 *
 * @author kdesnos
 * @author jheulot
 */
public class CreateParameterFeature extends AbstractCreateConfigurableFeature {

  /** The Constant FEATURE_NAME. */
  private static final String FEATURE_NAME = "Parameter";

  /** The Constant FEATURE_DESCRIPTION. */
  private static final String FEATURE_DESCRIPTION = "Create Parameter";

  private static final String DEFAULT_NAME = "ParameterName";

  private static final String QUESTION = "Enter new parameter name";

  /**
   * Default constructor for the {@link CreateParameterFeature}.
   *
   * @param fp
   *          the feature provider
   */
  public CreateParameterFeature(final IFeatureProvider fp) {
    super(fp, FEATURE_NAME, FEATURE_DESCRIPTION);
    this.hasDoneChanges = false;
  }

  @Override
  String getFeatureDescription() {
    return FEATURE_DESCRIPTION;
  }

  @Override
  String getDefaultName() {
    return DEFAULT_NAME;
  }

  @Override
  String getQuestion() {
    return QUESTION;
  }

  @Override
  Parameter createConfigurable(final String newParamName) {
    return PiMMUserFactory.instance.createParameter(newParamName);
  }

}
