/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
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
package org.preesm.commons.math;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.Serializable;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;

/**
 * Representation of the fractional numbers field.
 * <p>
 * This class is a singleton.
 * </p>
 *
 * @see LongFraction
 * @since 2.0
 */
public class LongFractionField implements Field<LongFraction>, Serializable {

  /** Serializable version identifier */
  private static final long serialVersionUID = -1257768487499119313L;

  /**
   * Private constructor for the singleton.
   */
  private LongFractionField() {
  }

  /**
   * Get the unique instance.
   *
   * @return the unique instance
   */
  public static LongFractionField getInstance() {
    return LazyHolder.INSTANCE;
  }

  /** {@inheritDoc} */
  public LongFraction getOne() {
    return LongFraction.ONE;
  }

  /** {@inheritDoc} */
  public LongFraction getZero() {
    return LongFraction.ZERO;
  }

  /** {@inheritDoc} */
  public Class<? extends FieldElement<LongFraction>> getRuntimeClass() {
    return LongFraction.class;
  }

  // CHECKSTYLE: stop HideUtilityClassConstructor
  /**
   * Holder for the instance.
   * <p>
   * We use here the Initialization On Demand Holder Idiom.
   * </p>
   */
  private static class LazyHolder {
    /** Cached field instance. */
    private static final LongFractionField INSTANCE = new LongFractionField();
  }
  // CHECKSTYLE: resume HideUtilityClassConstructor

  /**
   * Handle deserialization of the singleton.
   *
   * @return the singleton instance
   */
  private Object readResolve() {
    // return the singleton instance
    return LazyHolder.INSTANCE;
  }

}
