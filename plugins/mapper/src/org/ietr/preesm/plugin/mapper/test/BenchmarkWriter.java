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

package org.ietr.preesm.plugin.mapper.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * Writing latencies in a file to compare scheduling algorithms.
 * TODO: should be improved
 * 
 * @author mpelcat
 * 
 */
public class BenchmarkWriter {

	// Latency is displayed relatively to the initial list scheduling latency
	public static final int relativeLatency = 0;
	
	// Latency is simply displayed
	public static final int absoluteLatency = 1;
	
	 // Writing test results in a file
	 private PrintWriter writer = null;
	 
	 private long initialTime = 0;
	 
	 private long initialLatency = -1;
	 
	 private long latencyDisplayType = -1;

	public BenchmarkWriter(int latencyDisplayType) {
		
		this.latencyDisplayType = latencyDisplayType;
		
		try {
			writer = new PrintWriter(new FileOutputStream(new File("d:/simu.txt")),true);
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Initializing time and latency
	 */
	public void init() {
		
		this.initialTime = System.currentTimeMillis();
		
		initialLatency = -1;
	}

	/**
	 * Printing latency to the log file
	 */
	public void printLatency(long latency) {
		
		// initialized at first call
		if(initialLatency < 0){
			initialLatency = latency;
		}
		
		if(latencyDisplayType == relativeLatency){
			double relativeLatency = (double)latency/initialLatency;
			writer.println((System.currentTimeMillis() - initialTime) + "  " + relativeLatency);
		}
		else{
			writer.println((System.currentTimeMillis() - initialTime) + "  " + latency);
		}
	}
	
	public void println(String message) {
		
		writer.println(message);
	}
}
