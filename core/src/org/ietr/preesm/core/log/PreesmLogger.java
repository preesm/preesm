/**
 * PREESM
 * Copyright 2007 IETR under CeCILL license.
 *
 * Matthieu WIPLIEZ <Matthieu.Wipliez@insa-rennes.fr>
 */
package org.ietr.preesm.core.log;

import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * @author mwipliez
 * 
 */
public class PreesmLogger extends Logger {

	private static final Logger logger = new PreesmLogger();

	private static final String LOGGER_NAME = "org.ietr.preesm.log.PreesmLogger";

	/**
	 * GIves this Logger
	 * 
	 * @return a Logger
	 */
	public static Logger getLogger() {
		return logger;
	}

	/**
	 * 
	 */
	private PreesmLogger() {
		super(LOGGER_NAME, null);
		LogManager.getLogManager().addLogger(this);
	}

	@Override
	public void log(LogRecord record) {
		Level level = record.getLevel();
		int levelVal = level.intValue();
		if (getLevel() != null) {
			if (levelVal >= getLevel().intValue()) {
				if (levelVal < Level.INFO.intValue()) {
					String msg = record.getMillis() + " " + level.toString()
							+ ": " + record.getMessage() + " (in "
							+ record.getSourceClassName() + "#"
							+ record.getSourceMethodName() + ")";
					System.out.println(msg);
				} else {
					Date date = new Date(record.getMillis());
					DateFormat df = DateFormat.getTimeInstance();
					String msg = df.format(date) + " " + level.toString()
							+ ": " + record.getMessage();

					if (levelVal < Level.WARNING.intValue()) {
						System.out.println(msg);
					} else {
						System.err.println(msg);
					}
				}
			}
		}
	}
}
