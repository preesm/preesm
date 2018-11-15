package org.ietr.preesm.memory.script;

/**
 * This enumeration represent the type of the current {@link Match}
 */
public enum MatchType {

  /**
   * The {@link Match} links several inputs (or outputs) together. Not allowed anymore
   */
  // INTER_SIBLINGS,
  /**
   * The {@link Match} is internal to an actor and links an input {@link Buffer} to an output {@link Buffer}, <b>or</b>
   * the {@link Match} is external to an actor (i.e. correspond to an edge) and it links an output {@link Buffer} of an
   * actor to the input {@link Buffer} of the next.
   */
  FORWARD,

  /**
   * Opposite of FORWARD
   */
  BACKWARD
}
