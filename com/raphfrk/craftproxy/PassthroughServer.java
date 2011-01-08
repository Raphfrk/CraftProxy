package com.raphfrk.craftproxy;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;


public class PassthroughServer implements Runnable {

	MyBoolean running = new MyBoolean(true);
	MyBoolean run = new MyBoolean(true);

	int listenPort;
	
	String defaultServer;
	int defaultPortnum;
	String password;
	
	void kill() {
		System.out.println( "Killing server");
		synchronized(running) {
			synchronized(run) {
				run.set(false);
			}
			while( running.get() ) {
				try {
					running.wait();
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	}


	PassthroughServer(int listenPort, String defaultServer, int defaultPortnum, String password) {

		this.listenPort = listenPort;
		this.defaultServer = defaultServer;
		this.defaultPortnum = defaultPortnum;
		this.password = password;

	}

	ArrayList<PassthroughConnection> connections = new ArrayList<PassthroughConnection>();

	public void run() {

		ServerSocket listener = null;
		try {
			listener = new ServerSocket(listenPort);
			listener.setSoTimeout(2000);
		} catch (BindException be) {
			System.out.println( "Unable to bind to port");
			if( listener != null ) {
				try {
					listener.close();
				} catch (IOException e) {
					System.out.println( "Unable to close connection");
				}
			}
			informEnd();
			return;
		} catch (IOException ioe) {
			System.out.println("Unknown error");	
			if( listener != null ) {
				try {
					listener.close();
				} catch (IOException e) {
					System.out.println( "Unable to close connection");
				}
			}
			informEnd();
			return;
		} 

		System.out.println( "Server listening on port " + listenPort );
		
		boolean localRun;
		synchronized( run ) {
			localRun = run.get();
		}
		while( localRun ) {

				Socket socket = null;
				try {
					socket = listener.accept();
				} catch (SocketTimeoutException ste ) {
					if( socket != null ) {
						System.out.println("Socket not null after timeout" );
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			synchronized( run ) {
				localRun= run.get();
			}

			if( socket != null ) {
				PassthroughConnection passthrough = new PassthroughConnection( socket , defaultServer, defaultPortnum, password );
				Thread t = new Thread( passthrough );
				connections.add(passthrough);
				t.start();
			}


		}
		
		if( listener != null ) {
			try {
				listener.close();
			} catch (IOException e) {
				System.out.println( "Unable to close connection");
			}
		}


		System.out.println( "Killing threads");
		
		Iterator<PassthroughConnection> itr = connections.iterator();
		while( itr.hasNext() ) {
			PassthroughConnection current = itr.next();
			
			current.kill();
			System.out.println("- killed thread");

		}

		informEnd();

	}
	
	void informEnd() {
		synchronized( this.running ) {
			running.set(false);
			System.out.println( "Server stopped");
			running.notify();
		}
	}



}
