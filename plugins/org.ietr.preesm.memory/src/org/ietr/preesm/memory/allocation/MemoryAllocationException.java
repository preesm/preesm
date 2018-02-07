package org.ietr.preesm.memory.allocation;

/**
 *
 */
public class MemoryAllocationException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = 3319770496160526351L;

  public MemoryAllocationException(final String message) {
    super(message);
  }

  public MemoryAllocationException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
