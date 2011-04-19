package com.raphfrk.craftproxy;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class CPUUsage {

	double cpuUsage = 0;
	long lastTime = -1;
	long lastCPUTime = -1;
	long timeWindow = 0;
	long cpuDelta = 0;
	
	boolean supported = false;
	
	ThreadMXBean TMB; 
	
	CPUUsage() {
		TMB = ManagementFactory.getThreadMXBean();
		supported = TMB.isThreadCpuTimeSupported();
		if(!supported) {
			try {
				TMB.setThreadCpuTimeEnabled(true);
			} catch (UnsupportedOperationException uoe) {
				System.out.println("CPU time monitoring not supported");
			}
		}
		supported = TMB.isThreadCpuTimeSupported();
	}
	
	public double CPUUsage(long window) {
		
		if(!supported) {
			return 0;
		}
		
		updateCPU(window);
		
		return cpuUsage;
	}
	
	void updateCPU(long window) {
		
		long time = System.currentTimeMillis();
		long cpuTime = TMB.getCurrentThreadCpuTime();
		
		if(time > lastTime + window) {
			
			timeWindow = time - lastTime;
			cpuDelta = cpuTime - lastCPUTime;
			
			if(lastTime != -1) {
				cpuUsage = (cpuDelta/1000000.0)/(timeWindow);
				//System.out.println("CPU: " + cpuUsage*100.0);
			}
			
			lastTime = time;
			lastCPUTime = cpuTime;
		}
		
	}
}
