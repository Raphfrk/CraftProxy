package com.raphfrk.craftproxy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Main {

	public static void main(String [] args) {
		
		System.out.println( "Starting Craftproxy version " +  VersionNumbering.version );
		
		String defaultServer;
		int listenPort;
		int defaultPort;
		String password;
		
		if( args.length < 3 ) {
			System.out.println( "Usage: craftproxy <port to bind to> <default server> <default port> [<password>] [verbose]");
			System.exit(0);
			return;
		} else {
			try {
			listenPort = Integer.parseInt(args[0]);
			defaultServer = args[1];
			defaultPort = Integer.parseInt(args[2]);
			if( args.length > 3 ) {
				if( args[3].equals("verbose")) {
					Verbose.setVerbose(true);
				}
				password = args[3];
			} else {
				password = "";
			}
			if( args.length > 4 && args[4].equals("verbose")) {
				Verbose.setVerbose(true);
			}
			} catch (NumberFormatException nfe) {
				System.out.println( "Unable to parse port numbers");
				System.out.println( "Usage: craftproxy <port to bind to> <default server> <default port> [<password>]");
				System.exit(0);
				return;
			}
		}

		PassthroughServer server = new PassthroughServer( listenPort, defaultServer, defaultPort, password );
		
		Thread t = new Thread( server );
		t.start();

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));


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
		
	}


}
