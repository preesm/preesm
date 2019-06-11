/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2012)
 * Pengcheng Mu <pengcheng.mu@insa-rennes.fr> (2008)
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
package org.preesm.model.scenario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.slam.ComponentInstance;

/**
 * A ConstraintGroup associates a set of graph definitions and a set of processing units on which they can be matched.
 *
 * @author mpelcat
 */
public class ConstraintGroup {

  /** The set of processing units available for the constraint group. */
  private final ComponentInstance cmpInstnace;

  /** The set of graphs belonging to the constraint group. */
  private final List<AbstractActor> actors;

  /**
   * Instantiates a new constraint group.
   */
  public ConstraintGroup(final ComponentInstance opId) {
    this.cmpInstnace = opId;
    this.actors = new ArrayList<>();

  }

  /**
   * When a vertex is added to the constraints, its hierarchical path is added in its properties in order to separate
   * distinct vertices with same name.
   *
   * @param actor
   *          the vertex id
   */
  public void addActor(final AbstractActor actor) {
    if (!hasActor(actor)) {
      this.actors.add(actor);

    }
  }

  /**
   * Adds the vertex paths.
   *
   * @param actors
   *          the vertex id set
   */
  public void addActors(final Collection<AbstractActor> actors) {
    for (final AbstractActor actor : actors) {
      addActor(actor);
    }
  }

  /**
   * Removes the vertex paths.
   *
   * @param actors
   *          the vertex id set
   */
  public void removeVertexPaths(final Collection<AbstractActor> actors) {
    for (final AbstractActor actor : actors) {
      removeActor(actor);
    }
  }

  /**
   * Gets the operator ids.
   *
   * @return the operator ids
   */
  public ComponentInstance getComponentInstance() {
    return this.cmpInstnace;
  }

  /**
   * Gets the vertex paths.
   *
   * @return the vertex paths
   */
  public List<AbstractActor> getActors() {
    return Collections.unmodifiableList(this.actors);
  }

  /**
   * Checks for operator id.
   *
   * @param cmpInstance
   *          the operator id
   * @return true, if successful
   */
  public boolean isComponentInstance(final ComponentInstance cmpInstance) {
    return this.getComponentInstance().equals(cmpInstance);
  }

  /**
   * Checks for vertex path.
   *
   * @param actor
   *          the vertex info
   * @return true, if successful
   */
  public boolean hasActor(final AbstractActor actor) {
    for (final AbstractActor a : this.actors) {
      if (a.equals(actor)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Removes the vertex path.
   *
   * @param actor
   *          the sdf vertex info
   */
  public void removeActor(final AbstractActor actor) {
    final Iterator<AbstractActor> it = this.actors.iterator();
    while (it.hasNext()) {
      final AbstractActor a = it.next();
      if ((a.equals(actor))) {
        it.remove();
      }
    }

  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    String s = "<";
    s += this.cmpInstnace + " - ";
    s += this.actors.toString();
    s += ">";

    return s;
  }
}
