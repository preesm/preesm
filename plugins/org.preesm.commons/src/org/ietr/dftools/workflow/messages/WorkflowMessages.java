/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2011)
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
package org.ietr.dftools.workflow.messages;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This class is used to gather all texts displayed while managing the workflow. The strings are stored in
 * message.properties and retrieved through a text file.
 *
 * @author mpelcat
 */
public class WorkflowMessages {

  /** The Constant BUNDLE_NAME. */
  private static final String BUNDLE_NAME = "org.ietr.dftools.workflow.messages.workflowMessages";

  /** The Constant RESOURCE_BUNDLE. */
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(WorkflowMessages.BUNDLE_NAME);

  /**
   * Instantiates a new workflow messages.
   */
  private WorkflowMessages() {
  }

  /**
   * Gets the string defined in the key. Replace the nth chain "%VAR%" by the nth string variable
   *
   * @param key
   *          the key
   * @param variables
   *          the variables
   * @return the string
   */
  public static String getString(final String key, final String... variables) {
    try {
      String message = WorkflowMessages.RESOURCE_BUNDLE.getString(key);
      for (final String var : variables) {
        if (var != null) {
          message = message.replaceFirst("%VAR%", var);
        }
      }
      return message;
    } catch (final MissingResourceException e) {
      return '!' + key + '!';
    }
  }
}
