package com.raphfrk.craftproxy;

public class Verbose {
	
	static private boolean verbose = false;

	static boolean isVerbose() {
		return verbose;
	}
	
	static void setVerbose( boolean newVerbose ) {
		verbose = newVerbose;
	}
	
}
