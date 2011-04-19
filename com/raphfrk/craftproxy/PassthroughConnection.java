package com.raphfrk.craftproxy;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class PassthroughConnection implements Runnable {

	String hostname = null;
	int port = -1;
	String password = null;
	short holding;
	int listenPort;

	Socket socketToClient = null;
	Socket socketToServer = null;

	SocketBridge upstreamBridge = null;
	SocketBridge downstreamBridge = null;

	MyBoolean threadsStarted = new MyBoolean(false);

	void kill() {

		synchronized(threadsStarted) {
			while( !threadsStarted.get() ) {
				try {
					threadsStarted.wait();
				} catch (InterruptedException e) {
				}
			}
		}

		if( upstreamBridge != null ) {
			upstreamBridge.kill();
		}

		if( downstreamBridge != null ) {
			downstreamBridge.kill();
		}

	}


	PassthroughConnection( Socket socket, String hostname, int port, String password, int listenPort ) {
		this.socketToClient = socket;
		this.hostname = hostname;
		this.port = port;
		this.password = password;
		this.listenPort = listenPort;
	}

	public void run() {

		DataInputStream inputFromClient = null;
		DataOutputStream outputToClient = null;

		try {
			inputFromClient = new DataInputStream( socketToClient.getInputStream() );
		} catch (IOException e) {
			System.out.println("Unable to open data stream to client");
			if( inputFromClient != null ) {
				try {
					inputFromClient.close();
				} catch (IOException e1) {
					System.out.println("Unable to close data stream to client");
				}
			}
			informEnd();
			return;
		}

		try {
			outputToClient = new DataOutputStream( socketToClient.getOutputStream() );
		} catch (IOException e) {
			System.out.println("Unable to open data stream from client");
			if( outputToClient != null ) {
				try {
					outputToClient.close();
				} catch (IOException e1) {
					System.out.println("Unable to close data stream from client");
				}
			}
			informEnd();
			return;
		}

		PlayerRecord playerRecord = new PlayerRecord();

		System.out.println( "Carrying out input handshake");
		String ipString = socketToClient.getInetAddress().getHostAddress();
		String kickMessage = null;
		if( (kickMessage = Protocol.processLogin(inputFromClient, outputToClient, playerRecord, ipString)) != null ) {

			Protocol.kick(outputToClient, kickMessage);
			try {
				/*ArrayList<Byte> kick = Protocol.genKickPacket(
				"Unable to open connection to target server");
				DataOutputStream outData = new DataOutputStream( socketToClient.getOutputStream() );
				outData.write(Protocol.tobytes(kick));
				outData.flush();*/
				outputToClient.flush();
				socketToClient.close();
				inputFromClient.close();
				outputToClient.close();
			} catch (IOException e) {
				System.out.println( "Unable to close connections properly");
			} 
			informEnd();
			return;
		}

		boolean firstLogin = true;

		SynchronizedEntityMap synchronizedEntityMap = null;

		String defaultHostname = hostname;
		int defaultPort = port;
		boolean initialConnection = true;

		while( port != -1 ) {

			int repeatAttempts = Globals.getRepeats();

			int cnt=0;

			boolean success = false;

			DataInputStream inputFromServer = null;
			DataOutputStream outputToServer = null;

			while( !success ) {
				
				if( initialConnection ) {
					String fullAddress = ReconnectCache.get(playerRecord.username);

					hostname = ReconnectCache.getHost(fullAddress, defaultHostname);
					port = ReconnectCache.getPort(fullAddress, defaultPort);
				} 

				if(hostname.equals(Globals.getLocalAlias())) {
					if(!Globals.isQuiet()) {
						System.out.println("replacing " + hostname + " with localhost" );
					}
					hostname = "localhost";
				}

				boolean localAddress = false;
				InetAddress addr;

				if(hostname.contains("localhost") || hostname.equals(Globals.getLocalAlias())) {
					localAddress = true;
				} else {
					try {
						addr = InetAddress.getByName(hostname);
						localAddress = isThisMyIpAddress(addr);
					} catch (UnknownHostException e) {
					}
				}

				boolean selfConnect = (localAddress && port == listenPort );
				
				if(selfConnect) {
					System.out.println("Self connect attempted, replacing target and hostname with defaults");
				}
				
				if ( selfConnect ) {
					hostname = defaultHostname;
					port = defaultPort;
				}
				
				try {

					if( hostname.trim().startsWith("localhost")) {
						String fakeLocalIP = LocalhostIPFactory.getNextIP();
						if(!Globals.isQuiet()) {
							System.out.print("Attempting to connect to: " + hostname + ":" + port + " from " + fakeLocalIP );
						}
						socketToServer = new Socket(hostname,
								port,
								InetAddress.getByName(fakeLocalIP),
								0);
						if(!Globals.isQuiet()) {
							System.out.println(":" + socketToServer.getLocalPort());
						}
					} else {
						if(!Globals.isQuiet()) {
							System.out.println("Attempting to connect to: " + hostname + ":" + port );
						}

						socketToServer = new Socket();
						socketToServer.connect(new InetSocketAddress(hostname, port), 60000);
					}
					socketToServer.setSoTimeout(2000);

				} catch (ConnectException ce) {
					if(!Globals.isQuiet()) {
						System.out.println( "Unable to connect to server at " + hostname + ":" + port);
					}
					try {
						if( cnt <= repeatAttempts ) {
							cnt++;
							ReconnectCache.remove(playerRecord.username);
							continue;
						}
						if(!Globals.isQuiet()) {
							System.out.println( "Closing client connection");
						}
						DataOutputStream outData = new DataOutputStream( socketToClient.getOutputStream() );

						ArrayList<Byte> kick;
						String reconnectServer = ReconnectCache.get(playerRecord.username);
						System.out.println("Player location: " + reconnectServer);
						if(!reconnectServer.equals("") ) {
							kick = Protocol.genKickPacket("Server for player location not responding");
						} else {
							kick = Protocol.genKickPacket("Default world server not responding");
						}

						outData.write(Protocol.tobytes(kick));
						outData.flush();
						socketToClient.close();
					} catch (IOException e) {
						System.out.println( "Unable to close client connection");
					}
					informEnd();
					return;
				} catch (IOException e) {
					informEnd();
					return;
				}

				if(!Globals.isQuiet()) {
					System.out.println( "Attempting to establish data streams");
				}
				try {
					inputFromServer = new DataInputStream( socketToServer.getInputStream() );
				} catch (IOException e) {
					System.out.println("Unable to open data stream to server");
					if( inputFromServer != null ) {
						try {
							inputFromServer.close();
						} catch (IOException e1) {
							System.out.println("Unable to close data stream to server");
						}
					}
					informEnd();
					return;
				}

				try {
					outputToServer = new DataOutputStream( socketToServer.getOutputStream() );
				} catch (IOException e) {
					System.out.println("Unable to open data stream from server");
					if( outputToServer != null ) {
						try {
							outputToServer.close();
						} catch (IOException e1) {
							System.out.println("Unable to close data stream from server");
						}
					}
					informEnd();
					return;
				}

				if(!Globals.isQuiet()) {
					System.out.println( "Connection to server established");
				}

				System.out.println( "Logging in on behalf of " + playerRecord.username );
				System.out.flush();

				if( !Protocol.serverLogin(inputFromServer, outputToServer, playerRecord, true) ) {
					try {
						socketToServer.close();
					} catch (IOException e1) {
						System.out.println( "Unable to close socket correctly");
					}
					cnt++;
					if( cnt >= repeatAttempts ) {
						if(playerRecord.loginPacket != null && playerRecord.loginPacket.packetId == -1) {
							Protocol.kick(outputToClient, (String)playerRecord.loginPacket.fields[0]);
						} else {
							Protocol.kick(outputToClient, "Unable to connect to backend server");
						}
						try {
							inputFromClient.close();
							outputToClient.close();
						} catch (IOException e) {
							System.out.println( "Unable to close connections properly");
						} 
						informEnd();
						return;
					}
					if( cnt==2 && initialConnection ) {
						ReconnectCache.remove(playerRecord.username);
					}
					if( cnt+1 >= repeatAttempts ) {
						System.out.println( "Connection failed, trying again");
					}
					try {
						Thread.sleep(Globals.getDelay());
					} catch (InterruptedException e) {}

				} else {
					success = true;
				}


			}

			playerRecord.loginPacket.fields[0] = Globals.getDefaultPlayerId();

			if( Globals.isInfo() ) {
				System.out.println( "Updated login packet\n" + playerRecord.loginPacket );
			}

			if( firstLogin ) {
				firstLogin = false; 
				if( !playerRecord.loginPacket.writeBytes(outputToClient) ) {
					informEnd();
					return;
				}
			}

			ReconnectCache.store(playerRecord.username, hostname, port);

			if( synchronizedEntityMap == null ) {
				synchronizedEntityMap = new SynchronizedEntityMap(playerRecord.serverEntityID);
			} else {
				synchronizedEntityMap.setPlayerServerId(playerRecord.serverEntityID);
			}

			SocketMonitor upstreamMonitor;
			SocketMonitor downstreamMonitor;

			if( !playerRecord.forward ) {
				upstreamMonitor = new UpstreamMonitor(synchronizedEntityMap);
				downstreamMonitor = new DownstreamMonitor(synchronizedEntityMap);

				upstreamMonitor.setOtherMonitor(downstreamMonitor);
				downstreamMonitor.setOtherMonitor(upstreamMonitor);
			} else {
				upstreamMonitor = new NullMonitor(synchronizedEntityMap);
				downstreamMonitor = new NullMonitor(synchronizedEntityMap);

				upstreamMonitor.setOtherMonitor(downstreamMonitor);
				downstreamMonitor.setOtherMonitor(upstreamMonitor);
			}

			boolean holdingSuccess = true;
			
			if( !playerRecord.forward && upstreamBridge != null ) {
				holding = ((UpstreamMonitor)upstreamBridge.monitor).holding;
				Packet holdingUpdate = new Packet( 
						(byte)0x10 , 
						new Object[] { (Short)holding },
						false);
				if(!Globals.isQuiet()) {
					System.out.println( "Updating hold slot to " + holding);
				}
				if(!holdingUpdate.writeBytes(outputToServer)) {
					holdingSuccess = false;
					System.out.println("Unable to send holding packet");
				}
			}

			upstreamBridge = new SocketBridge( inputFromClient, outputToServer, upstreamMonitor, false );

			downstreamBridge = new SocketBridge( inputFromServer, outputToClient, downstreamMonitor, true );

			if( !playerRecord.forward ) {
				((UpstreamMonitor)upstreamBridge.monitor).holding = holding;
			}

			if(holdingSuccess) {

				Thread t1 = new Thread( upstreamBridge );
				Thread t2 = new Thread( downstreamBridge );
				t1.start();
				t2.start();

				try {
					t1.join();
					t2.join();
				} catch (InterruptedException e) {
				}

			}
			initialConnection = false;

			if(!Globals.isQuiet()) {
				System.out.println( "Closing connection to server" );
			}

			try {
				outputToServer.close();
				inputFromServer.close();
				socketToServer.close();
			} catch (IOException e) {
				System.out.println( "Unable to close link");
			}

			if( !playerRecord.forward ) {
				port = ((DownstreamMonitor)downstreamBridge.monitor).portNum;
			} else {
				port = -1;
			}

			if( playerRecord.forward || port == -1 ) {

				if(!Globals.isQuiet()) {
					System.out.println( "Closing connection to client" );
				}

				try {
					if(!playerRecord.forward) {
						Protocol.kick(outputToClient, "Proxy lost connection to server");
					}
					outputToClient.flush();

					outputToClient.close();
					inputFromClient.close();
					socketToClient.close();
				} catch (IOException e) {
					System.out.println( "Unable to close link");
				}

				hostname = "";
				port = -1;

			} else {
				hostname = ((DownstreamMonitor)downstreamBridge.monitor).hostName;
				System.out.println( "Redirect to " + hostname + ":" + port );
				ReconnectCache.store(playerRecord.username, hostname, port);
				if( synchronizedEntityMap != null ) {
					synchronizedEntityMap.destroy(outputToClient);
				}
			}
		}

		informEnd();
	}

	void informEnd() {

		synchronized(threadsStarted) {
			threadsStarted.set(true);
			threadsStarted.notify();
		}
	}
	
	public static boolean isThisMyIpAddress(InetAddress addr) {
	    // Check if the address is a valid special local or loop back
	    if (addr.isAnyLocalAddress() || addr.isLoopbackAddress())
	        return true;

	    // Check if the address is defined on any interface
	    try {
	        return NetworkInterface.getByInetAddress(addr) != null;
	    } catch (SocketException e) {
	        return false;
	    }
	}


}
