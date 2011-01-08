package com.raphfrk.craftproxy;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;


public class SocketBridge implements Runnable {

	DataInputStream  in;
	DataOutputStream out;

	MyBoolean run = new MyBoolean(true);
	MyBoolean running = new MyBoolean(true);

	boolean server = false;
	SocketMonitor monitor;

	void kill() {

		synchronized( running ) {
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


	SocketBridge( DataInputStream  in, DataOutputStream out, SocketMonitor monitor, boolean server ) {

		this.in = in;
		this.out = out;
		this.monitor = monitor;
		this.server = server;

	}
	
	PacketIdStore packetIdStore = new PacketIdStore();
	
	public void run() {

		boolean localRun = true;

		Packet currentPacket = new Packet(in, server);

		try {

			while( localRun && !currentPacket.eof ) {

				synchronized( run ) {
					localRun = run.get();
				}

				if( localRun ) {

					if( currentPacket.valid ) {
						monitor.process(currentPacket, out);
					}

					currentPacket = new Packet(in, server);
					
					packetIdStore.add(currentPacket.packetId);
					
					if( !currentPacket.valid && !currentPacket.eof && !currentPacket.timeout ) {
						currentPacket.eof = true;
						System.out.println( "Most recent packets: " + packetIdStore );
						
					}

				}
			}
		} finally {
			if( out != null ) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
			if( in != null ) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}

		synchronized(running) {
			running.set(false);
			running.notifyAll();
		}

	}



}