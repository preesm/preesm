package org.ietr.preesm.core.scenario.editor;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This class is used to gather all texts displayed in scenario editor. The strings
 * are stored in message.properties and retrieved through {@link Messages}
 * 
 * @author mpelcat
 */
public class Messages {
	private static final String BUNDLE_NAME = "org.ietr.preesm.core.scenario.editor.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	public static String getString(String key) {
		// TODO Auto-generated method stub
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
