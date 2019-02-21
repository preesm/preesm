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
