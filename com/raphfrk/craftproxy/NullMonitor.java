package com.raphfrk.craftproxy;

import java.io.DataOutputStream;

import com.raphfrk.craftproxy.SocketMonitor.CommandElement;

public class NullMonitor extends SocketMonitor {
	
	NullMonitor() {
		super();
	}
	
	NullMonitor(SocketMonitor other) {
		super(other);
	}
	
	NullMonitor( SynchronizedEntityMap synchronizedEntityMap ) {
		super(synchronizedEntityMap);
	}

	@Override
	public boolean process(Packet packet, DataOutputStream out) {
		
		CommandElement command;
		
		while( (command = getCommand()) != null ) {
			
			if(!Globals.isQuiet()) {
				System.out.println( "Command received: " + command.command );
			}
			
			if( command.command.equals("BREAK")) {
				return false;
			}
		}
		
		if( packet.timeout ) {
			return true; 
		}
		
		if( packet.eof ) {
			other.addCommand(new CommandElement( "BREAK" , null ));
			return false;
		}

		if( !packet.valid && !packet.timeout ) {
			other.addCommand(new CommandElement( "BREAK" , null ));
			return false;
		}
		
		if( packet.packetId == 0xFF ) {
			other.addCommand(new CommandElement( "BREAK" , null ));
		}
		
		packet.writeBytes(out);
		
		return true;

	}
	
}