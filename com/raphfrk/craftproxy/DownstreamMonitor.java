package com.raphfrk.craftproxy;
import java.io.DataOutputStream;
import java.io.IOException;


public class DownstreamMonitor implements SocketMonitor{

	@Override
	public void process(Packet packet, DataOutputStream out) {



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
