package com.raphfrk.craftproxy;
import java.io.DataOutputStream;
import java.util.LinkedList;


public class SocketMonitor {

	SynchronizedEntityMap synchronizedEntityMap;

	SocketMonitor other;

	SocketMonitor() {
	}

	SocketMonitor( SynchronizedEntityMap synchronizedEntityMap ) {
		this.synchronizedEntityMap = synchronizedEntityMap;
	}

	SocketMonitor( SocketMonitor other ) {
		this.other = other;
	}

	void setOtherMonitor( SocketMonitor other ) {
		this.other = other;
	}

	LinkedList<CommandElement> commands = new LinkedList<CommandElement>();

	public void addCommand( CommandElement command ) {

		synchronized( commands ) {
			commands.addLast(new CommandElement(command));
		}

	}

	CommandElement getCommand() {

		CommandElement command;

		synchronized( commands ) {
			if( commands.isEmpty() ) {
				command = null;
			} else {
				command = new CommandElement(commands.getFirst());
			}
		}

		return command;

	}


	void process( Packet packet , DataOutputStream out ) {
	}

	class CommandElement {

		String command;
		Object target;

		CommandElement( CommandElement command ) {

			this.command = new String( command.command );
			this.target = command.target;

		}

	}

	Packet convertEntityIds( Packet packet, boolean server ) {

		Integer[] entityIDs = EntityFieldIndex.getEntityIds(packet.packetId);

		if( entityIDs == null ) {
			return packet;
		}
		
		for( Integer offset : entityIDs ) {
			
			Integer entityID = (Integer)packet.fields[offset];
			
			if( Globals.isVerbose() ) {
				System.out.print( "Entity ID conversion: " + entityID );
			}
			if( server ) {
				entityID = synchronizedEntityMap.serverToClient(entityID);
			} else {
				entityID = synchronizedEntityMap.clientToServer(entityID);
			}
			
			if( Globals.isVerbose() ) {
				System.out.println( " to " + entityID );
			}
			
			packet.fields[offset] = entityID;
			
		}
		
		return packet;

	}

}
