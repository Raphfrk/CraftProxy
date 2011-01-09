package com.raphfrk.craftproxy;

import java.util.Hashtable;
import java.util.Set;

public class SynchronizedEntityMap {

	final int increment = 3;

	Hashtable<Integer,Integer> clientToServer;
	Hashtable<Integer,Integer> serverToClient;

	int counter = 10000;
	Object counterSync = new Object();

	SynchronizedEntityMap() {
		reset();
	}
	
	SynchronizedEntityMap(int playerServerId) {
		reset(playerServerId);
	}

	synchronized int serverToClient( Integer serverId ) {

		if( !serverToClient.containsKey(serverId)) {
			//if( !clientToServer.containsKey(serverId)) {
			//	addMap(serverId, serverId);
			// else {
			synchronized( counterSync ) {
				counter+=increment;
				while( clientToServer.contains(counter) ) {
					counter+=increment;
				}
				addMap(serverId, counter);
				//	}
			}
		}
		return serverToClient.get(serverId);
	}

	synchronized int clientToServer( Integer clientId ) {

		if( !clientToServer.containsKey(clientId)) {
			//if( !serverToClient.containsKey(clientId)) {
			//	addMap(clientId, clientId);
			//} else {
			synchronized( counterSync ) {
				counter+=increment;
				while( serverToClient.contains(counter) ) {
					counter+=increment;
				}
				addMap(counter, clientId);
			}
			//}
		}
		return clientToServer.get(clientId);	
	}

	synchronized public void reset() {

		clientToServer = new Hashtable<Integer,Integer>();
		serverToClient = new Hashtable<Integer,Integer>();

		addMap(-1, -1);

	}

	synchronized public void reset(int playerId) {

		reset();
		addMap(playerId,Globals.getDefaultPlayerId());

	}

	synchronized public void addMap( Integer server , Integer client ) {

		clientToServer.put(client, server);
		serverToClient.put(server, client);

	}

	synchronized Set<Integer> getClientEntities() {
		return clientToServer.keySet();
	}

}
