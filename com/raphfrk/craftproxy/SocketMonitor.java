package com.raphfrk.craftproxy;
import java.io.DataOutputStream;
import java.util.LinkedList;


public class SocketMonitor {

	SocketMonitor other;
	
	SocketMonitor() {
	}
	
	SocketMonitor( SocketMonitor other ) {
		this.other = other;
	}
	
	void setOtherMonitor( SocketMonitor other ) {
		this.other = other;
	}
	
	LinkedList<String> commands = new LinkedList<String>();
	
	public void addCommand( String command ) {
		
		synchronized( commands ) {
			commands.addLast(new String(command));
		}
		
	}
	
	String getCommand() {
		
		String command;
		
		synchronized( commands ) {
			if( commands.isEmpty() ) {
				command = null;
			} else {
				command = new String(commands.getFirst());
			}
		}
		
		return command;
		
	}
	
	
	void process( Packet packet , DataOutputStream out ) {
	}
	
}
