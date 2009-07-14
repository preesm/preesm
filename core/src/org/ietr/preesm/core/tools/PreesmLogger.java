/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.core.tools;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.ietr.preesm.core.ui.Activator;

/**
 * Displaying information or error messages through the console
 * 
 * @author mwipliez
 * @author mpelcat
 */
public class PreesmLogger extends Logger {

	private static final PreesmLogger logger = new PreesmLogger();

	private static final String LOGGER_NAME = "org.ietr.preesm.log.PreesmLogger";

	MessageConsole console = null;

	/**
	 * GIves this Logger
	 * 
	 * @return a Logger
	 */
	public static PreesmLogger getLogger() {
		return logger;
	}

	@Override
	public void setLevel(Level newLevel) throws SecurityException {
		// Enabling only info level
		super.setLevel(Level.INFO);
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
		final int levelVal = level.intValue();
		if (getLevel() != null) {
			if (levelVal >= getLevel().intValue()) {

				// Writes a log in standard output
				if (console == null) {
					if (levelVal < Level.INFO.intValue()) {
						String msg = record.getMillis() + " "
								+ level.toString() + ": " + record.getMessage()
								+ " (in " + record.getSourceClassName() + "#"
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
				} else {
					// Writes a log in console
					final MessageConsoleStream stream = console
							.newMessageStream();

					Activator.getDefault().getWorkbench().getDisplay()
							.asyncExec(new Runnable() {
								@Override
								public void run() {
									if (levelVal < Level.WARNING.intValue())
										stream
												.setColor(new Color(null, 0, 0,
														0));
									else if (levelVal == Level.WARNING
											.intValue())
										stream.setColor(new Color(null, 255, 150,
												0));
									else if (levelVal > Level.WARNING
											.intValue())
										stream.setColor(new Color(null, 255,
												0, 0));
								}
							});

					stream.println(getFormattedTime() + record.getMessage());

					if (getLevel().intValue() >= Level.SEVERE.intValue()) {
						// throw (new PreesmException(record.getMessage()));
					}
				}
			}
		}

	}

	public static String getFormattedTime() {
		Calendar cal = Calendar.getInstance();

		String time = String.format("%2d:%2d:%2d ", cal
				.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal
				.get(Calendar.SECOND));
		return time;
	}

	public void createConsole() {

		IConsoleManager mgr = ConsolePlugin.getDefault().getConsoleManager();

		if (console == null) {
			console = new MessageConsole("Preesm console", null);
			mgr.addConsoles(new IConsole[] { console });
		}

		console.activate();
		console.setBackground(new Color(null, 0, 0, 0));
		// console.newMessageStream().println("test");

		mgr.refresh(console);
	}
}
