/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot, Clément Guy, Yaset Oliva Venegas
 * 
 * [mpelcat,jnezan,kdesnos,jheulot,cguy,yoliva]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
package org.ietr.preesm.utils.log;

import java.text.DateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Define the singleton managing PREESM loggers. When using this helper class,
 * your debug, info, warning and errors messages will be displayed in the right
 * eclipse console. If no Eclipse GUI plugin is loaded (i.e. executing a job in
 * command line), all the messages will be sent to the system console.
 * 
 * @author cguy
 * 
 * Code adapted from ORCC (net.sf.orcc.core, https://github.com/orcc/orcc)
 * @author Antoine Lorence
 * 
 */
public class PreesmLogger {
	
	private static final String RAW_FLAG = "raw_record";
	
	/**
	 * Define how text must be printed to logger (Eclipse or System console)
	 * 
	 * @author Antoine Lorence
	 * 
	 */
	private static class DefaultPreesmFormatter extends Formatter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
		 */
		@Override
		public String format(LogRecord record) {

			StringBuilder output = new StringBuilder();

			if (!hasRawFlag(record)) {
				final Date date = new Date(record.getMillis());
				final DateFormat df = DateFormat.getTimeInstance();

				output.append(df.format(date));
				// Default printing for warn & severe
				if (record.getLevel().intValue() > NOTICE.intValue()) {
					output.append(" ").append(record.getLevel());
				} else if (record.getLevel().intValue() == NOTICE.intValue()) {
					output.append(" NOTICE");
				} else if (record.getLevel().intValue() == DEBUG.intValue()) {
					output.append(" DEBUG");
				}
				output.append(": ");
			}
			output.append(record.getMessage());
			return output.toString();
		}

		private boolean hasRawFlag(LogRecord record) {
			final Object[] params = record.getParameters();
			if(params == null) {
				return false;
			}

			for(Object param : params) {
				if(RAW_FLAG.equals(param)) {
					return true;
				}
			}
			return false;
		}
	}
	
	public final static Level SEVERE = Level.SEVERE;
	public final static Level WARNING = Level.WARNING;
	public final static Level NOTICE = Level.INFO;
	public final static Level TRACE = Level.FINE;
	public final static Level DEBUG = Level.FINER;

	public final static Level ALL = Level.ALL;
	
	private static Logger logger;

	/**
	 * Return the current logger, or a newly created one if it doesn't exists.
	 * If it is created here, a default ConsoleHandler is used as Logger's
	 * Handler.
	 * 
	 * @return
	 */
	private static Logger getLogger() {
		if (logger == null) {
			configureLoggerWithHandler(new ConsoleHandler());
		}
		return logger;
	}

	/**
	 * This method is the same as
	 * {@link #configureLoggerWithHandler(Handler, Formatter)}, but the
	 * {@link DefaultPreesmFormatter} is used as default {@link Formatter}.
	 * 
	 * @param handler
	 */
	public static void configureLoggerWithHandler(Handler handler) {
		configureLoggerWithHandler(handler, new DefaultPreesmFormatter());
	}

	/**
	 * Register specific log Handler to display messages sent threw PreesmLogger
	 * with a given {@link Formatter}. If this method is never called, the
	 * default Handler will be {@link java.util.logging.ConsoleHandler}.
	 * 
	 * @param handler
	 * @param formatter
	 */
	public static void configureLoggerWithHandler(Handler handler,
			Formatter formatter) {
		logger = null;

		Logger newLog = Logger.getAnonymousLogger();
		newLog.addHandler(handler);
		newLog.setUseParentHandlers(false);
		handler.setFormatter(formatter);

		logger = newLog;

		setLevel(TRACE);
	}
	
	/**
	 * Set the minimum level displayed. By default, PreesmLogger display messages
	 * from INFO level and highest. Call this method with DEBUG or ALL as
	 * argument to display debug messages.
	 * 
	 * @param level
	 */
	public static void setLevel(Level level) {
		getLogger().setLevel(level);
		for (Handler handler : getLogger().getHandlers()) {
			handler.setLevel(level);
		}
	}
	
	/**
	 * Display a debug message to current console.
	 * 
	 * @param message
	 */
	public static void debug(Object content) {
		getLogger().log(DEBUG, content.toString());
	}

	/**
	 * Display a debug message to current console, appended with a line
	 * separator character.
	 * 
	 * @param message
	 */
	public static void debugln(Object content) {
		debug(content.toString() + System.getProperty("line.separator"));
	}

	/**
	 * Display a debug message on the current console, without any prepended
	 * string (time or level info).
	 * 
	 * @param message
	 */
	public static void debugRaw(Object content) {
		LogRecord record = new LogRecord(DEBUG, content.toString());
		record.setParameters(new Object[] { RAW_FLAG });
		getLogger().log(record);
	}

	/**
	 * Display a notice message to current console.
	 * 
	 * @param message
	 */
	public static void notice(Object content) {
		getLogger().log(NOTICE, content.toString());
	}

	/**
	 * Display a notice message to current console, appended with a line
	 * separator character.
	 * 
	 * @param message
	 */
	public static void noticeln(Object content) {
		notice(content.toString() + System.getProperty("line.separator"));
	}

	/**
	 * Display a notice message on the current console, without any prepended
	 * string (time or level info).
	 * 
	 * @param message
	 */
	public static void noticeRaw(Object content) {
		LogRecord record = new LogRecord(NOTICE, content.toString());
		record.setParameters(new Object[] { RAW_FLAG });
		getLogger().log(record);
	}

	/**
	 * Display an error line on the current console.
	 * 
	 * @param message
	 */
	public static void severe(Object content) {
		getLogger().log(SEVERE, content.toString());
	}

	/**
	 * Display an error line on the current console, appended with a line
	 * separator character.
	 * 
	 * @param message
	 */
	public static void severeln(Object content) {
		severe(content.toString() + System.getProperty("line.separator"));
	}

	/**
	 * Display an error line on the current console, without any prepended
	 * string (time or level info).
	 * 
	 * @param message
	 */
	public static void severeRaw(Object content) {
		LogRecord record = new LogRecord(SEVERE, content.toString());
		record.setParameters(new Object[] { RAW_FLAG });
		getLogger().log(record);
	}

	/**
	 * Display an information message on current console.
	 * 
	 * @param message
	 */
	public static void trace(Object content) {
		getLogger().log(TRACE, content.toString());
	}

	/**
	 * Display an information message on current console. The message will be
	 * appended with a line separator character.
	 * 
	 * @param message
	 */
	public static void traceln(Object content) {
		trace(content.toString() + System.getProperty("line.separator"));
	}

	/**
	 * Display an information message on the current console, without any
	 * prepended string (time or level info).
	 * 
	 * @param message
	 */
	public static void traceRaw(Object content) {
		LogRecord record = new LogRecord(TRACE, content.toString());
		record.setParameters(new Object[] { RAW_FLAG });
		getLogger().log(record);
	}

	/**
	 * Display a warning line on the current console.
	 * 
	 * @param message
	 */
	public static void warn(Object content) {
		getLogger().log(WARNING, content.toString());
	}

	/**
	 * Display a warning line on the current console, appended with a line
	 * separator character.
	 * 
	 * @param message
	 */
	public static void warnln(Object content) {
		warn(content.toString() + System.getProperty("line.separator"));
	}

	/**
	 * Display a warning line on the current console, without any prepended
	 * string (time or level info).
	 * 
	 * @param message
	 */
	public static void warnRaw(Object content) {
		LogRecord record = new LogRecord(WARNING, content.toString());
		record.setParameters(new Object[] { RAW_FLAG });
		getLogger().log(record);
	}

	public static void log(Level level, String msg) {
        getLogger().log(level, msg);
    }

	public static void logln(Level level, String msg) {
        getLogger().log(level, msg + System.getProperty("line.separator"));
    }
}
