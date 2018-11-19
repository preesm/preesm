/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2012)
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
package org.ietr.preesm.codegen.idl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.ietr.preesm.codegen.model.containers.CodeSectionType;
import org.preesm.algorithm.model.IRefinement;
import org.preesm.commons.logger.PreesmLogger;

// TODO: Auto-generated Javadoc
/**
 * Different function prototypes associated to an actor and retrieved from a prototype file.
 *
 * @author jpiat
 * @author mpelcat
 */
public class ActorPrototypes implements IRefinement {

  /** Prototypes for the different phases of initialization of an actor. */
  private Map<Integer, Prototype> initPrototypes = null;

  /** Prototype for the loop execution of an actor. */
  private Prototype loopPrototype = null;

  /** The path. */
  private String path = null;

  /**
   * Instantiates a new actor prototypes.
   *
   * @param path
   *          the path
   */
  public ActorPrototypes(final String path) {
    this.initPrototypes = new LinkedHashMap<>();
    this.path = path;
  }

  /**
   * Getting the initialization prototype for phase 0.
   *
   * @return the inits the prototype
   */
  public Prototype getInitPrototype() {
    return getInitPrototype(0);
  }

  /**
   * Getting the initialization prototype for phase (-) i.
   *
   * @param i
   *          the i
   * @return the inits the prototype
   */
  public Prototype getInitPrototype(final int i) {
    if (this.initPrototypes.keySet().contains(0)) {
      return this.initPrototypes.get(i);
    } else {
      // Returning null means that the actor has no prototype for phase is
      return null;
    }
  }

  /**
   * Default init call is call 0.
   *
   * @param init
   *          the new inits the prototype
   */
  public void setInitPrototype(final Prototype init) {
    setInitPrototype(init, 0);
  }

  /**
   * Init i is added before init i-1 in the code.
   *
   * @param init
   *          the init
   * @param i
   *          the i
   */
  public void setInitPrototype(final Prototype init, final int i) {
    if (this.initPrototypes.containsKey(i)) {
      PreesmLogger.getLogger().log(Level.WARNING,
          "IDL: Init phase number (-)" + i + " was defined several time for file " + this.path);
    }
    this.initPrototypes.put(i, init);
  }

  /**
   * Gets the loop prototype.
   *
   * @return the loop prototype
   */
  public Prototype getLoopPrototype() {
    return this.loopPrototype;
  }

  /**
   * Sets the loop prototype.
   *
   * @param init
   *          the new loop prototype
   */
  public void setLoopPrototype(final Prototype init) {
    this.loopPrototype = init;
  }

  /**
   * Gets the prototype.
   *
   * @param sectionType
   *          the section type
   * @return the prototype
   */
  public Prototype getPrototype(final CodeSectionType sectionType) {
    if (sectionType.getMajor().equals(CodeSectionType.MajorType.INIT)) {
      return getInitPrototype(sectionType.getMinor());
    } else if (sectionType.getMajor().equals(CodeSectionType.MajorType.LOOP)) {
      return getLoopPrototype();
    }
    return null;
  }

  /**
   * Checks for prototype.
   *
   * @param sectionType
   *          the section type
   * @return true, if successful
   */
  public boolean hasPrototype(final CodeSectionType sectionType) {
    return getPrototype(sectionType) != null;
  }
}
