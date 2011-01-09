package com.raphfrk.craftproxy;
import java.io.DataOutputStream;
import java.io.IOException;


public class UpstreamMonitor extends SocketMonitor {

	UpstreamMonitor() {
		super();
	}
	
	UpstreamMonitor(SocketMonitor other) {
		super(other);
	}
	
	UpstreamMonitor( SynchronizedEntityMap synchronizedEntityMap ) {
		super(synchronizedEntityMap);
	}

	@Override
	public boolean process(Packet packet, DataOutputStream out) {
		
		CommandElement command;
		
		while( (command = getCommand()) != null ) {
			
			System.out.println( "Command received: " + command.command );
			
			if( command.command.equals("REDIRECTBREAK")) {
				return false;
			} else if( command.command.equals("EOFBREAK")) {
				return false;
			} else if( command.command.equals("INVALIDBREAK")) {
				return false;
			}
			
		}
		
		packet = super.convertEntityIds(packet, false);
		
		// Use return to cancel sending packet to client
		
		if( !packet.test() ) {
			System.exit(0);
		}
		
		packet.writeBytes(out);
		
		return true;

	}
	
}