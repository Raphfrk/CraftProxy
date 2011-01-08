package com.raphfrk.craftproxy;
import java.io.DataOutputStream;


public interface SocketMonitor {

	void process( Packet packet , DataOutputStream out ); 
	
}
