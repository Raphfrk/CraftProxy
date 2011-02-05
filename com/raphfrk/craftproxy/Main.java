package com.raphfrk.craftproxy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Main {

	public static Object sleeper = new Object();
	static boolean serverEnabled = true;

	static boolean consoleInput = true;

	public static void main(String[] args, boolean consoleInput) {
		Main.consoleInput = consoleInput;
		main(args);
	}

	public static void main(String [] args) {

		System.out.println( "Starting Craftproxy version " +  VersionNumbering.version );

		String defaultServer;
		int listenPort;
		int defaultPort;
		String password = "";

		String usageString = "craftproxy <port to bind to> <default server> <default port> [verbose] [info] [auth] [clientversion num]";

		if( args.length < 3 ) {
			System.out.println( "Usage: " + usageString );
			if(consoleInput) {
				System.exit(0);
			}
			return;

		} else {
			try {
				listenPort = Integer.parseInt(args[0]);
				defaultServer = args[1];
				defaultPort = Integer.parseInt(args[2]);
				for( int pos=3;pos<args.length;pos++) {

					if( args[pos].equals("verbose"))        Globals.setVerbose(true);
					else if( args[pos].equals("hell"))           Globals.setHell(true);
					else if( args[pos].equals("info"))           Globals.setInfo(true);
					else if( args[pos].equals("auth"))           Globals.setAuth(true);
					else if( args[pos].equals("clientversion")){ Globals.setClientVersion(Integer.parseInt(args[pos+1])); pos++;}
					else if( args[pos].equals("password"))     { Globals.setPassword(args[pos+1]); pos++;}
					else if( args[pos].equals("quiet"))          Globals.setQuiet(true);
					else                                         password = new String(args[pos]); // game password - not used

				}

			} catch (NumberFormatException nfe) {
				System.out.println( "Unable to parse numbers");
				System.out.println( "Usage: " + usageString );
				System.exit(0);
				return;
			}
		}

		if( !Globals.isAuth() ) {
			System.out.println( "" );
			System.out.println( "WARNING: You have not enabled player name authentication");
			System.out.println( "WARNING: This means that player logins are not checked with the minecraft server");
			System.out.println( "" );
			System.out.println( "To enable name authentication, add auth to the command line" );
			System.out.println( "" );
		} else {
			System.out.println( "Name authentication enabled");
		}


		System.out.println( "Use \"end\" to stop the server");

		EntityFieldIndex.init();

		PassthroughServer server = new PassthroughServer( listenPort, defaultServer, defaultPort, password );

		Thread t = new Thread( server );
		t.start();

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		if(consoleInput) {
			try {
				while( !in.readLine().equals("end") ) {
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			server.kill();
		} else {
			System.out.println("Server console disabled");
			while(true) {
				try {
					synchronized(sleeper) {
						while(serverEnabled) {
							sleeper.wait();
						}
					}
				} catch (InterruptedException ie) {
					server.kill();
				}
			}
		}

	}

	public static void killServer() {

		System.out.println("Killing server from Bukkit");
		System.out.println("Note: Players must disconnect for threads to end");

		synchronized(sleeper) {
			serverEnabled = false;
			sleeper.notify();
		}

	}


}
