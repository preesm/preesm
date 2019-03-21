/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Alexandre Honorat <alexandre.honorat@insa-rennes.fr> (2019)
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
package org.preesm.model.pisdf.check;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This class enables to check if an actor or port name is compliant with our policy.
 * 
 * @author ahonorat
 */
public class NameCheckerC {

  /**
   * Valid names correspond to this regex, close to the C variables name policy.
   */
  public static final String REGEX_C = "[a-zA-Z][a-zA-Z0-9_]*";

  /**
   * Only C for now, but it could be extended to C++ keywords also.
   */
  private static final String[] restrictedKeywordsArr = { "auto", "break", "case", "char", "const", "continue",
      "default", "do", "int", "long", "register", "return", "short", "signed", "sizeof", "static", "struct", "switch",
      "typedef", "union", "unsigned", "void", "volatile", "while", "double", "else", "enum", "extern", "float", "for",
      "goto", "if", "inline", "restrict" };

  /**
   * Set of reserved keyworkds for C, that are not allowed.
   */
  public static final SortedSet<String> restrictedKeywords = new TreeSet<>(Arrays.asList(restrictedKeywordsArr));

  /**
   * Check if the given actor or port name meets the policy.
   * <p>
   * Ideally this method should be called by all {@code setName} methods. It is not the case yet.
   * 
   * @param name
   *          Name to check.
   * @return True if valid, false otherwise.
   */
  public static boolean isValidName(String name) {
    return name.matches(REGEX_C) && !restrictedKeywords.contains(name);
  }

}
