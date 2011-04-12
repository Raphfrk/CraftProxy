package com.raphfrk.craftproxy;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.raphfrk.craftproxy.SocketMonitor.CommandElement;


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
	
	boolean monitorExit = true;

	public void run() {
		
		CPUUsage cpuUsage = new CPUUsage();

		boolean localRun = true;

		Packet currentPacket = new Packet(in, server);
		
		monitorExit = monitor.process(currentPacket, out);
		
		int timeoutDuration = 0;
		
		long window = Globals.getWindow();
		long threshold = Globals.getThreshold();

		while( localRun && !currentPacket.eof && monitorExit) {

			synchronized( run ) {
				localRun = run.get();
			}

			if( localRun && monitorExit ) {

				currentPacket = new Packet(in, server, monitor.chunkCache, monitor.arrayListByte);
				
				monitorExit = monitor.process(currentPacket, out);
				
				if( currentPacket.timeout ) {
					System.out.println("Duration: " + timeoutDuration);
					if( (timeoutDuration++) > 20) {
						if(!Globals.isQuiet()) {
							System.out.println( "Connection timed out");
						}
						localRun = false;
					}
				} else {
					timeoutDuration = 0;
				}

				packetIdStore.add(currentPacket.packetId);

				if( !currentPacket.valid && !currentPacket.eof && !currentPacket.timeout ) {
					currentPacket.eof = true;
					System.out.println( "Most recent packets: " + packetIdStore );

				}
				
				if(window > 0 && cpuUsage.CPUUsage(window)*100 > threshold) {
					System.out.println("Breaking connection due to CPU overload");
					monitorExit = false;
					monitor.other.addCommand(monitor.new CommandElement( "BREAK" , null ));
					monitor.other.addCommand(monitor.new CommandElement( "INVALIDBREAK" , null ));

				}

			}
		}


		synchronized(running) {
			running.set(false);
			running.notifyAll();
		}

	}



}