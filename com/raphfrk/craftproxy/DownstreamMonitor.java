package com.raphfrk.craftproxy;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;


public class DownstreamMonitor extends SocketMonitor{

	HashSet<ChunkLoc> chunks = new HashSet<ChunkLoc>();

	DownstreamMonitor() {
		super();
	}

	DownstreamMonitor( SynchronizedEntityMap synchronizedEntityMap ) {
		super(synchronizedEntityMap);
	}

	DownstreamMonitor(SocketMonitor other) {
		super(other);
	}

	int posx = 0;
	int posy = 0;
	int posz = 0;

	float pitch = 0;
	float yaw = 0;

	String hostName="";
	int portNum=-1;

	LinkedList<Packet> packetFIFO = new LinkedList<Packet>();
	boolean playerMoved = false;

	boolean destroyed = false;

	@Override
	public boolean process(Packet packet, DataOutputStream out) {

		if( packet.eof ) {
			other.addCommand(new CommandElement( "EOFBREAK" , null ));
			return false;
		}

		if( !packet.valid && !packet.timeout ) {
			other.addCommand(new CommandElement( "INVALIDBREAK" , null ));
			return false;
		}

		if( packet.timeout ) {
			return true; 
		}

		//System.out.print("Old: ");
		//packet.printBytes();

		if(!destroyed) {
			destroyed = true;
			//synchronizedEntityMap.destroy(out);
			//synchronizedEntityMap.addToReserved();

		}

		CommandElement command;

		while( (command = getCommand()) != null ) {

			if(!Globals.isQuiet() && !command.command.equals("MOVEMENT")) {
				System.out.println( "Command received from upstream: " + command.command );
			}

			if( command.command.equals("REDIRECTBREAK")) {
				return false;
			} else if( command.command.equals("EOFBREAK")) {
				return false;
			} else if( command.command.equals("INVALIDBREAK")) {
				return false;
			} /*else if( command.command.equals("MOVEMENT")) {

				Object[] posArray = (Object[])command.target;

				posx = (int)Math.floor((Double)posArray[0]);
				posy = (int)Math.floor((Double)posArray[1]);
				posz = (int)Math.floor((Double)posArray[2]);

				pitch = (Float)posArray[3];
				yaw = (Float)posArray[4];

			} else if (command.command.equals("DROP")) {
				Packet pickupSpawn = new Packet( (byte)0x15 , new Object[] {

						new Integer((Integer)command.target),
						new Short((short)4),
						new Byte((byte)1),
						new Short((short)0),
						new Integer(posx*32),
						new Integer(posy*32+4),
						new Integer(posz*32),
						new Byte((byte)0),
						new Byte((byte)0),
						new Byte((byte)0)

				}, true);
				System.out.println( "Dropping at " + posx + ", " + posy + ", " + posz );
				pickupSpawn.writeBytes(out);
			} else if (command.command.equals("DROPMOB")) {
				EntityMetadata emd = new EntityMetadata();
				emd.elements.add(new Byte((byte)0));
				emd.ids.add(new Byte((byte)0));


				Packet pickupSpawn = new Packet( (byte)0x18 , new Object[] {

						new Integer((Integer)command.target),
						new Byte((byte)57),
						new Integer(posx*32),
						new Integer(posy*32+4),
						new Integer(posz*32),
						new Byte((byte)0),
						new Byte((byte)0),
						emd

				}, true);
				System.err.println( "Dropping mob at " + posx + ", " + posy + ", " + posz );
				pickupSpawn.writeBytes(out);
			} else if (command.command.equals("DESTROY")) {
				Packet pickupSpawn = new Packet( (byte)0x1d , new Object[] {
						new Integer((Integer)command.target)
				}, true);
				pickupSpawn.writeBytes(out);
			} else if (command.command.equals("INIT")) {
				Packet pickupSpawn = new Packet( (byte)0x1e , new Object[] {
						new Integer((Integer)command.target)
				}, true);
				pickupSpawn.writeBytes(out);
			} else if (command.command.equals("NUKE")) {
				super.synchronizedEntityMap.nukeReserve(out);
			} else if (command.command.equals("REFRESH")) {
				System.err.println("Refreshing");
				System.out.println("Refreshing");
				super.synchronizedEntityMap.initAll(out);
			} else if (command.command.equals("DESTROYRESERVE")) {
				System.err.println("destroying reserve");
				super.synchronizedEntityMap.destroyReserve(out);
			} else if (command.command.equals("TORCH")) {
				System.err.println("Writing glass block");
				writeBlock(out, -95,70,18,1,0);
				System.err.println("Writing base");
				writeBlock(out, -94,69,18,3,0);
				writeBlock(out, -96,69,18,3,0);
				writeBlock(out, -95,69,19,3,0);
				writeBlock(out, -95,69,17,3,0);
				System.err.println("Writing torches");
				writeBlock(out, -94,70,18,50,1);
				writeBlock(out, -96,70,18,50,1);
				writeBlock(out, -95,70,19,50,1);
				writeBlock(out, -95,70,17,50,1);
				System.err.println("Writing torches");
				setBlock(out, -94,70,18,50,2);
				setBlock(out, -96,70,18,50,2);
				setBlock(out, -95,70,19,50,2);
				setBlock(out, -95,70,17,50,2);

			} */


		}

		if( !playerMoved ) {
			if( EntityFieldIndex.getEntityIds(packet.packetId) != null && packet.packetId != 0x0d ) {
				packetFIFO.addLast(packet);
				return true;
			} else if( packet.packetId == 0x0d ){
				playerMoved = true;
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}

				//System.out.println("Move Packet: " + packet);
				//System.out.println("Coords: " + ((Double)packet.fields[0])*32 + ", " + ((Double)packet.fields[3])*32 );

				packet.writeBytes(out);

				while( !packetFIFO.isEmpty() ) {
					Packet current = packetFIFO.removeFirst();
					if(!process(current,out)) {
						return false;
					}
				}
				if(!Globals.isQuiet()) {
					System.out.println( "burst complete");
				}
				//System.out.println("Move Packet: " + packet);

				packet.writeBytes( out );

				chunkCache = new IntSizedByteArray();
				arrayListByte = new ArrayList<Byte>();
			}
		}

		if( packet.packetId == 0x18 && packet.fields[1].equals((Byte)(byte)91) ) {
			//System.out.println( "Mob Spawn: " + packet );
		}

		if( packet.packetId == 0x1E ) {
			//System.out.println( "Entity spawn: " + packet );
		}

		int oldEntityId=0;
		if( packet.packetId == 0x1D ) {
			oldEntityId = (Integer)packet.fields[0];
			//System.out.println( "Entity destroy: " + packet );
		}

		packet = super.convertEntityIds(packet, true);

		/*if( packet.packetId == 0x0d && Math.pow(((Double)packet.fields[0])-posx,2) > 1000) {
			System.out.println("Teleport detected");
			synchronizedEntityMap.listClientIds();
		}

		packet.printBytes();

		if( packet.packetId != 51 ) {
			System.out.println(packet);
		}*/

		if( packet.packetId == 0x18 && packet.fields[1].equals((Byte)(byte)91) ) {
			//System.out.println( "Mob Spawn (after conversion): " + packet );
		}

		if( packet.packetId == 0x15 || packet.packetId == 0x1E || packet.packetId == 0x1D ) {
			//System.out.println( "New EID: " + packet.fields[0]);
		}

		// Use return to cancel sending packet to client
		switch(packet.packetId) {

		case ((byte)0xFF): {
			if( (redirectDetected(packet)) != null ) {

				other.addCommand(new CommandElement( "REDIRECTBREAK" , null ));

				/*setBlock(out, posx,posy-1,posz,1);
				setBlock(out, posx,posy-2,posz,1);
				movePlayer(out, posx,posy,posz);

				chunks.remove(new ChunkLoc(posx>>4,posz>>4));*/

				if( !unloadChunks(out) ) {
					System.out.println( "Error unloading chunks");
				}

				return false;

			}
			break;
		}
		case ((byte)0x32): {

			int x = (Integer)packet.fields[0];
			int z = (Integer)packet.fields[1];

			boolean create = (Boolean)packet.fields[2];

			if( create ) {
				chunks.add(new ChunkLoc(x,z));
			} else {
				chunks.remove(new ChunkLoc(x,z));
			}

			break;

		}
		case ((byte)0x33): {

			int x = ((Integer)packet.fields[0])>>4;
			int z = ((Integer)packet.fields[2])>>4;

			chunks.add(new ChunkLoc(x,z));

			break;
		}
		case ((byte)0x1d) : {
			super.synchronizedEntityMap.removeEntity(oldEntityId);
		}

		}


		if( !packet.test() ) {
			System.out.println("Packet Format Error (from server): Forcing connection break");
			other.addCommand(new CommandElement( "INVALIDBREAK" , null ));
			return false;
		}

		//System.out.print("New: ");
		//packet.printBytes();

		//if(packet.packetId != 0x1E) {
		packet.writeBytes(out);
		//}


		return true;


	}

	String redirectDetected(Packet packet) {

		String reason = (String)packet.fields[0];
		if(!Globals.isQuiet()) {
			System.out.println( "Kicked with: " + reason ); 
		}

		if( reason.indexOf("[Serverport]") == 0 ) {
			String[] split = reason.split( ":" );
			if( split.length == 3 ) {
				hostName = split[1].trim();
				try { 
					portNum = Integer.parseInt( split[2].trim() );
				} catch (Exception e) { portNum = -1; };
			} else  if( split.length == 2 ) {
				hostName = split[1].trim();
				try {
					portNum = 25565;
				} catch (Exception e) { portNum = -1; };
			}
		}

		if( portNum != -1 ) {
			return hostName + ":" + portNum;
		} else {
			return null;

		}
	}

	void writeBlock(DataOutputStream out, int x, int y, int z, int id, int data) {
		MultiBlockArray mba = new MultiBlockArray();
		int cx = x>>4;
		int bx = x - (cx<<4);
		int cz = z>>4;
		int bz = z - (cz<<4);

		Short packed = (short)((y&0xFF) | ((bx << 12)&0xF000) | ((bz << 8)&0x0F00));

		mba.coords = new short[] { packed };
		mba.data = new byte[] {(byte)data};
		mba.type = new byte[] {(byte)id};

		Packet multiBlockUpdate = new Packet((byte)0x34, new Object[] {

				new Integer(cx),
				new Integer(cz),
				mba

		}, false);

		multiBlockUpdate.writeBytes(out);

	}

	void setBlock(DataOutputStream out, int x, int y, int z, int id, int data) {

		if( y > 126 || y < 1 ) return; 

		Packet blockUpdate = new Packet( (byte)0x35, 
				new Object[] {
				(Integer)x,
				(Byte)(byte)y,
				(Integer)z,
				(Byte)(byte)id,
				(Byte)(byte)data},
				false); 

		blockUpdate.writeBytes(out);

	}

	void enableChunk(DataOutputStream out, int x, int z ) {

		Packet chunkInit = new Packet( (byte)0x32, 
				new Object[] {
				(Integer)x,
				(Integer)z,
				(Boolean)true},
				false); 

		chunkInit.writeBytes(out);

	}

	void movePlayer(DataOutputStream out, int x, int y, int z ) {
		Packet position = new Packet( 
				(byte)0x0D,
				new Object[] {
						new Double(x+0.5),
						new Double(y),
						new Double(y),
						new Double(z+0.5),
						new Float(yaw),
						new Float(pitch),
						new Boolean(false)
				},
				false
		);
		position.writeBytes(out);
	}



	boolean unloadChunks( DataOutputStream out ) {

		for( ChunkLoc current : chunks ) {

			if( Globals.isVerbose() ) {
				System.out.println( "Unloading " + current);
			}

			Packet unload = new Packet( 
					(byte)0x32,
					new Object[] {
							new Integer(current.locX),
							new Integer(current.locZ),
							new Boolean(false)
					},
					false
			);


			if( !unload.writeBytes( out ) ) {
				return false;
			}

		}

		return true;

	}

	class ChunkLoc {

		int locX;
		int locZ;

		ChunkLoc( int x , int z ) {
			locX = x;
			locZ = z;
		}

		@Override
		public boolean equals(Object obj) { 

			if( !obj.getClass().equals(ChunkLoc.class) ) {
				return false;
			}

			ChunkLoc other = (ChunkLoc)obj;

			return this.locX == other.locX && this.locZ == other.locZ;

		}

		@Override
		public int hashCode() { 
			return ( ( locX << 12 ) - locX ) << 12 + locX + locZ;
		}

		@Override 
		public String toString() {
			return locX + ", " + locZ;
		}


	}

}
