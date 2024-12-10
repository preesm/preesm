/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008 - 2011)
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
package org.ietr.dftools.graphiti.ui.commands;

import org.eclipse.gef.commands.Command;
import org.ietr.dftools.graphiti.model.AbstractObject;

/**
 * This class provides a command that changes the value of the currently selected parameter.
 *
 * @author Matthieu Wipliez
 *
 */
public class ParameterChangeValueCommand extends Command {

  /** The label. */
  private final String label;

  /**
   * Set by {@link #setValue(String, Object)}.
   */
  private String name;

  /**
   * The new value.
   */
  private Object newValue;

  /**
   * The old value.
   */
  private Object oldValue;

  /**
   * The property bean we're modifying.
   */
  private final AbstractObject source;

  /**
   * Creates a new add parameter command.
   *
   * @param source
   *          the source
   * @param label
   *          the label
   */
  public ParameterChangeValueCommand(final AbstractObject source, final String label) {
    this.source = source;
    this.label = label;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.commands.Command#execute()
   */
  @Override
  public void execute() {
    this.oldValue = this.source.setValue(this.name, this.newValue);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.commands.Command#getLabel()
   */
  @Override
  public String getLabel() {
    return this.label;
  }

  /**
   * Sets the value of the parameter whose name is given to the given value.
   *
   * @param name
   *          The parameter name.
   * @param value
   *          Its new value.
   */
  public void setValue(final String name, final Object value) {
    this.name = name;
    this.newValue = value;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.commands.Command#undo()
   */
  @Override
  public void undo() {
    this.source.setValue(this.name, this.oldValue);
  }

}
