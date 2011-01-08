package com.raphfrk.craftproxy;

public class Globals {
	
	static private boolean verbose = false;

	synchronized static boolean isVerbose() {
		return verbose;
	}
	
	synchronized static void setVerbose( boolean newVerbose ) {
		verbose = newVerbose;
	}
	
	static private boolean authenticate = false;
	
	synchronized static boolean isAuth() {
		return authenticate;
	}
	
	synchronized static void setAuth( boolean newAuth ) {
		authenticate = newAuth;
	}
	
}
