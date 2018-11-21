/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 * Daniel Madroñal <daniel.madronal@upm.es> (2018)
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
package org.preesm.scenario.papi;

import java.util.List;

/**
 *
 * @author anmorvan
 *
 */
public class PapiEvent {

  private int                     index;
  private String                  name        = "";
  private String                  description = "";
  private List<PapiEventModifier> modifiers;

  public int getIndex() {
    return this.index;
  }

  public void setIndex(final int index) {
    this.index = index;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDesciption(final String desciption) {
    this.description = desciption;
  }

  public List<PapiEventModifier> getModifiers() {
    return this.modifiers;
  }

  public void setModifiers(final List<PapiEventModifier> modifiers) {
    this.modifiers = modifiers;
  }

  @Override
  public boolean equals(final Object comparer) {

    boolean decision = false;
    boolean nameComp = false;
    boolean descriptionComp = false;
    boolean idComp = false;
    boolean modifiersComp = false;

    if (comparer instanceof PapiEvent) {
      final PapiEvent tester = (PapiEvent) comparer;
      if (this.description.equals(tester.getDescription())) {
        descriptionComp = true;
      }
      if (this.name.equals(tester.getName())) {
        nameComp = true;
      }
      if (this.description.equals(tester.getDescription())) {
        descriptionComp = true;
      }
      if (this.index == tester.getIndex()) {
        idComp = true;
      }
      if (this.modifiers.equals(tester.getModifiers())) {
        modifiersComp = true;
      }
      if (nameComp && descriptionComp && idComp && modifiersComp) {
        decision = true;
      }
    }

    return decision;
  }

  @Override
  public int hashCode() {
    return this.index;
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder();
    b.append(
        String.format("      <event index=\"%d\" name=\"%s\" desc=\"%s\">%n", this.index, this.name, this.description));
    for (final PapiEventModifier modifier : this.modifiers) {
      b.append(modifier.toString());
    }
    b.append(String.format("      </event>%n"));
    return b.toString();
  }
}
