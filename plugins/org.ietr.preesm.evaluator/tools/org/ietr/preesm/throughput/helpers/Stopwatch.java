package org.ietr.preesm.throughput.helpers;

public class Stopwatch {
	private double startTime;
	private double accumulatedTime;
	private String label;
	private boolean isRunning; 
	private String format;
	private double timeConverterFactor;
	
	public Stopwatch(String label){
		this.label = label;
		this.accumulatedTime = 0;
		this.setTimeSconds();
	}
	
	public Stopwatch(){
		this.label = "stopwatch";
		this.accumulatedTime = 0;
		this.setTimeSconds();
	}
	
	public void reset(){
		this.accumulatedTime = 0;
		this.isRunning = false;
	}
	
	public void start() {
		this.reset();
		this.isRunning = true;
		this.startTime = this.currentTime();
		
	}

	public void stop() {
		if(this.isRunning){
			this.accumulatedTime += this.currentTime() - startTime;
			this.isRunning = false;
		}			
	}
	
	public void pause(){
		if(this.isRunning)
			this.accumulatedTime += this.currentTime() - startTime;
	}
	
	public void resume(){
		this.startTime = this.currentTime();
	}
	
	private double currentTime(){
		return (System.currentTimeMillis()*this.timeConverterFactor);	
	}
	
	public double value(){
		return this.accumulatedTime;
	}
	
	public double watch(){
		double time = this.currentTime();
		if(this.isRunning)
			return this.accumulatedTime + time - startTime;
		else
			return 0;
	}
	
	public String getLabel(){
		return this.label;
	}
	
	public void setTimeMillis(){
		this.timeConverterFactor = 1;
		this.format = "Millis";
	}
	
	public void setTimeSconds(){
		this.timeConverterFactor = (double) 1/(1000);
		this.format = "Seconds";
	}
	
	public void setTimeMinutes(){
		this.timeConverterFactor = (double) 1/(1000*60);
		this.format = "Minutes";
	}
	
	public String getFormat(){
		return this.format;
	}
	
	@Override
	public String toString() {
		return this.accumulatedTime + " " + this.format;
	}
	
	
}
