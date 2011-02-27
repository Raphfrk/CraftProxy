package com.raphfrk.craftproxy;

public class Globals {
	
	static private String password = "null";
	
	synchronized static String getPassword() {
		return password;
	}
	
	synchronized static void setPassword(String password) {
		Globals.password = password;
	}
	
	static private boolean quiet = false;

	static boolean isQuiet() {
		return quiet;
	}
	
	synchronized static void setQuiet( boolean newQuiet ) {
		quiet = newQuiet;
	}
	
	static private boolean hell = false;

	synchronized static boolean isHell() {
		return hell;
	}
	
	synchronized static void setHell( boolean newHell ) {
		hell = newHell;
	}
	
	static private boolean verbose = false;

	synchronized static boolean isVerbose() {
		return verbose;
	}
	
	synchronized static void setVerbose( boolean newVerbose ) {
		verbose = newVerbose;
	}
	
	static private boolean info = false;

	synchronized static boolean isInfo() {
		return info;
	}
	
	synchronized static void setInfo( boolean newInfo ) {
		info = newInfo;
	}
	
	static private boolean authenticate = false;
	
	synchronized static boolean isAuth() {
		return authenticate;
	}
	
	synchronized static void setAuth( boolean newAuth ) {
		authenticate = newAuth;
	}
	
	static private int defaultPlayerId = 123456;
		
	synchronized static int getDefaultPlayerId() {
		
		return defaultPlayerId;
		
	}
	
	static private int clientVersion = 9;
	
	synchronized static int getClientVersion() {
		return clientVersion;
	}
	
	synchronized static void setClientVersion( int newClientVersion ) {
		clientVersion = newClientVersion;
	}
	
}
