package com.raphfrk.craftproxy;

import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

public class SynchronizedEntityMap {

	int playerServerId;
	
	final int increment = 3;
	
	final Object syncObject = new Object();
	int counter = 50000;

	Hashtable<Integer,Integer> clientToServer;
	Hashtable<Integer,Integer> serverToClient;

	HashSet<Integer> reservedIds = new HashSet<Integer>();

	SynchronizedEntityMap(int playerServerId) {
		this.playerServerId = playerServerId;
		reset();
	}
	
	synchronized void addToReserved(int playerServerId) {
		this.playerServerId = playerServerId;
		addToReserved();
	}

	synchronized void addToReserved() {

		reservedIds.addAll(clientToServer.keySet());
		reset();

	}

	synchronized int serverToClient( Integer serverId ) {

		HashMap<Integer,Integer> x = new HashMap<Integer,Integer>();

		x.values().toArray(new Integer[0]);

		if( !serverToClient.containsKey(serverId)) {
			//System.out.println("serverToClient does not contain " + serverId);
			int otherId = getNextCounter(); 
			while( clientToServer.containsKey(otherId) || reservedIds.contains(otherId)) 
				{
				otherId = getNextCounter(); 
			}
			//System.out.println("clientToServer does not contain " + otherId );
			
			if(otherId != serverId && Globals.isInfo()) {
				System.out.println( "Entity Id Collision (S->C), mapping " + serverId + " to " + otherId );
			}
			addMap(serverId, otherId);
		}

		//System.out.println("Mapped S:" + serverId + " to C:" + serverToClient.get(serverId));
		return serverToClient.get(serverId);
	}

	synchronized int clientToServer( Integer clientId ) {
		if( !clientToServer.containsKey(clientId)) {
			int otherId = getNextCounter(); 
			while( serverToClient.containsKey(otherId) ) {
				otherId = getNextCounter(); 
			}
			if(otherId != clientId && Globals.isInfo()) {
				System.out.println( "Entity Id Collision (C->S), mapping " + otherId + " to " + clientId );
			}	
			addMap(otherId, clientId);
		}
		//System.out.println("Mapped C:" + clientId + " to S:" + clientToServer.get(clientId));
		return clientToServer.get(clientId);	
	}

	int getNextCounter() {
		synchronized(syncObject) {
			return counter++;
		}
	}
	
	synchronized public void reset() {

		clientToServer = new Hashtable<Integer,Integer>();
		serverToClient = new Hashtable<Integer,Integer>();

		addMap(-1, -1);
		addMap(playerServerId,Globals.getDefaultPlayerId());

	}
	
	synchronized public void addMap( Integer server , Integer client ) {

		clientToServer.put(client, server);
		serverToClient.put(server, client);
		//System.out.println( "Added key " + client + " to clientToServer");
		//System.out.println( "Added key " + server + " to serverToServer");

	}

	synchronized Set<Integer> getClientEntities() {
		return clientToServer.keySet();
	}

	synchronized void initAll(DataOutputStream out) {

		if(!Globals.isQuiet()) {
			System.out.println( "Sending entity init burst");
		}

		int clientId = Globals.getDefaultPlayerId();

		Set<Integer> clientEntities = getClientEntities();
		
		System.out.println("Client entities size: " + clientEntities.size() );

		for( Integer current : clientEntities ) {
			if( current != clientId && current != -1 ) {

				Packet initEntity = new Packet( (byte)0x1e , new Object[] {
						new Integer(current),
				}, true);


				System.out.println( "Sending init packet\n" + initEntity);

				initEntity.writeBytes(out);


			}

		}
		
		for( Integer current : reservedIds ) {
			if( current != clientId && current != -1 ) {

				Packet initEntity = new Packet( (byte)0x1e , new Object[] {
						new Integer(current),
				}, true);


				System.out.println( "Sending init packet\n" + initEntity);

				initEntity.writeBytes(out);


			}

		}

	}
	
	synchronized void destroyReserve( DataOutputStream out ) {
		
		if(!Globals.isQuiet()) {
			System.out.println( "Destroy reserve list");
		}
		
		int clientId = Globals.getDefaultPlayerId();
		
		for( Integer current : reservedIds ) {
			if( current != clientId && current != -1 ) {

				Packet destroyEntity = new Packet( (byte)0x1D, 
						new Object[] { 
						new Integer(current)
				},
				true
				);

				//if(Globals.isVerbose()) {
					System.err.println( "Destroying reserve: \n" + destroyEntity);
				//}
					
				destroyEntity.writeBytes(out);


			}

		}
		
	}
	
	synchronized void nukeReserve( DataOutputStream out ) {
		
		if(!Globals.isQuiet()) {
			System.out.println( "Destroy reserve list");
		}
		
		int clientId = Globals.getDefaultPlayerId();
		
		for( Integer current : reservedIds ) {
			if( current != clientId && current != -1 ) {

				Packet pickupSpawn = new Packet( (byte)0x15 , new Object[] {
						
						new Integer(current),
						new Short((short)4),
						new Byte((byte)1),
						new Short((short)0),
						new Integer(0),
						new Integer(36),
						new Integer(0),
						new Byte((byte)0),
						new Byte((byte)0),
						new Byte((byte)0)
						
				}, true);

				Packet destroyEntity = new Packet( (byte)0x1D, 
						new Object[] { 
						new Integer(current)
				},
				true
				);

				//if(Globals.isVerbose()) {
					System.out.println( "Nuking reserve: \n" + destroyEntity);
				//}

				pickupSpawn.writeBytes(out);
				pickupSpawn.writeBytes(out);
					
				destroyEntity.writeBytes(out);
				destroyEntity.writeBytes(out);


			}

		}
		
	}

	void listClientIds() {
		System.out.println("Client ID list:");
		int clientId = Globals.getDefaultPlayerId();

		Set<Integer> clientEntities = getClientEntities();

		for( Integer current : clientEntities ) {
			if(current == clientId) {
				System.out.print("*");
			}
			System.out.println(current);
			
		}
	}
	
	boolean temp = true;
	
	void destroy( DataOutputStream out ) {

		if(!temp) return;
		
		if(!Globals.isQuiet()) {
			System.out.println( "Sending entity destroy burst");
		}

		int clientId = Globals.getDefaultPlayerId();

		Set<Integer> clientEntities = getClientEntities();

		for( Integer current : clientEntities ) {
			if( current != clientId && current != -1 ) {

				Packet initEntity = new Packet( (byte)0x1e, 
						new Object[] { 
						new Integer(current)
				},
				true
				);
				
				Packet destroyEntity = new Packet( (byte)0x1D, 
						new Object[] { 
						new Integer(current)
				},
				true
				);

				if(Globals.isVerbose()) {
					System.out.println( "Sending destroy packet\n" + destroyEntity);
				}

				initEntity.writeBytes(out);
				destroyEntity.writeBytes(out);

			}

		}

	}
}
