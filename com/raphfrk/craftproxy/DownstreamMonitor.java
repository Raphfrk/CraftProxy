package com.raphfrk.craftproxy;
import java.io.DataOutputStream;
import java.util.HashSet;


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

	String hostName="";
	int portNum=-1;;

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



		packet = super.convertEntityIds(packet, true);

		// Use return to cancel sending packet to client
		switch(packet.packetId) {

		case ((byte)0xFF):    
			if( (redirectDetected(packet)) != null ) {

				other.addCommand(new CommandElement( "REDIRECTBREAK" , null ));
				
				System.out.println( "Moving player");
				if( !sendPlayerAway(out)) {
					System.out.println( "Error sending player move packet");
				}
				
				
				if( !unloadChunks(out) ) {
					System.out.println( "Error unloading chunks");
				}
								
				return false;

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
			
		}


		if( !packet.test() ) {
			System.exit(0);
		}

		packet.writeBytes(out);

		return true;


	}

	String redirectDetected(Packet packet) {

		String reason = (String)packet.fields[0];
		System.out.println( "Kicked with: " + reason ); 

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
	
	boolean sendPlayerAway( DataOutputStream out ) {
		
		Packet position = new Packet( 
				(byte)0x0D,
				new Object[] {
						new Double(1000000),
						new Double(1000),
						new Double(1000),
						new Double(-1000000),
						new Float(0),
						new Float(0),
						new Boolean(false)
				},
				false
			);
		
		if( !position.writeBytes( out )) {
			return false;
		}
		
		return true;
		
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
