package org.ietr.preesm.plugin.mapper.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * Writing latencies in a file to compare scheduling algorithms
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
	 
	 private int initialLatency = -1;
	 
	 private int latencyDisplayType = -1;

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
	public void printLatency(int latency) {
		
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
