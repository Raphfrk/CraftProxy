package com.raphfrk.craftproxy;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;


public class SocketMonitor {

	IntSizedByteArray chunkCache = null;
	ArrayList<Byte> arrayListByte = null;
	
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
			//System.out.println( "adding command" );
			commands.addLast(new CommandElement(command));
		}

	}

	CommandElement getCommand() {
		
		CommandElement command;

		synchronized( commands ) {
			if( commands.isEmpty() ) {
				return null;
			} else {
				command = new CommandElement(commands.removeFirst());
			}
		}

		return command;

	}


	boolean process( Packet packet , DataOutputStream out ) {
		return true;
	}

	class CommandElement {

		String command;
		Object target;
		
		CommandElement( String command, Object target ) {
			this.command = command;
			this.target = target;
		}


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
			boolean printed = false;
			Integer entityID = (Integer)packet.fields[offset];
			
			//System.out.println(packet);
			//packet.printBytes();
			//printed = true;
			
			if( Globals.isVerbose() ) {
				System.out.print( ((server)?"S->C":"C-S") + "(" + Integer.toHexString(packet.packetId) + ") Entity ID conversion: " + entityID );
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
			if( printed ) {
				//System.out.println(packet);
				packet.printBytes();
			}
			
			
		}
		
		return packet;

	}

}
