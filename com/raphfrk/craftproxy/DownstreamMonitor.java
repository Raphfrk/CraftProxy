package com.raphfrk.craftproxy;
import java.io.DataOutputStream;


public class DownstreamMonitor extends SocketMonitor{
	
	DownstreamMonitor() {
		super();
	}
	
	DownstreamMonitor( SynchronizedEntityMap synchronizedEntityMap ) {
		super(synchronizedEntityMap);
	}

	DownstreamMonitor(SocketMonitor other) {
		super(other);
	}

	@Override
	public void process(Packet packet, DataOutputStream out) {

		packet = super.convertEntityIds(packet, true);

		// Use return to cancel sending packet to client
		switch(packet.packetId) {
		
		case ((byte)0xFF):    System.out.println( "Kicked with: " + ((String)packet.fields[0]) ); 

		}
		
		if( !packet.test() ) {
			System.exit(0);
		}
		
		packet.writeBytes(out);


	}

}
