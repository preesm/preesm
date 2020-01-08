package org.preesm.ui.slam;

import org.eclipse.jface.dialogs.IInputValidator;

/**
 * Check if the number of cores is strictly positive.
 * 
 * @author ahonorat
 *
 */
public class NbCoresValidator implements IInputValidator {

  public static final String errorInput = "/!\\ You must enter a strictly positive integer. /!\\";

  @Override
  public String isValid(String newText) {
    if (newText == null || newText.isEmpty()) {
      return null;
    }
    try {
      int a = Integer.parseInt(newText);
      if (a < 1) {
        return errorInput;
      }
    } catch (NumberFormatException e) {
      return errorInput;
    }
    return null;
  }

}
