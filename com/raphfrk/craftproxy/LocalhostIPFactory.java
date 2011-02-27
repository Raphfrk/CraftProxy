package com.raphfrk.craftproxy;

public class LocalhostIPFactory {

	static Integer counter = 1;
	
	
	
	static String getNextIP() {
		
		
				
		int high, low;
		
		synchronized(counter) {
			
			do{
			high = (counter>>8) & 0x00FF;
			low = counter & 0x00FF;
			counter ++;
			} while ( high == 0 || high == 255 || low == 0 || low == 255 );
			
		}
		return "127." + high + "." + low + ".2";
		
	}
	
	static int getPortOffset() {
		
		synchronized(counter) {
			
			return (counter>>16) & 0x00FF; 

		}
		
	}
	
}
