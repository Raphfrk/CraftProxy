package com.raphfrk.craftproxy;
import java.io.DataOutputStream;
import java.io.IOException;

import com.raphfrk.craftproxy.SocketMonitor.CommandElement;


public class UpstreamMonitor extends SocketMonitor {

	double posx=0;
	double posy=0;
	double poss=0;
	double posz=0;
	double stance=0;
	float pitch=0;
	float yaw=0;
	boolean ground = false;

	short holding = 0;

	UpstreamMonitor() {
		super();
	}

	UpstreamMonitor(SocketMonitor other) {
		super(other);
	}

	UpstreamMonitor( SynchronizedEntityMap synchronizedEntityMap ) {
		super(synchronizedEntityMap);
	}

	@Override
	public boolean process(Packet packet, DataOutputStream out) {

		CommandElement command;

		while( (command = getCommand()) != null ) {

			if(!Globals.isQuiet()) {
				System.out.println( "Command received: " + command.command );
			}

			if( command.command.equals("REDIRECTBREAK")) {
				return false;
			} else if( command.command.equals("EOFBREAK")) {
				return false;
			} else if( command.command.equals("INVALIDBREAK")) {
				return false;
			}

		}

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

		if( packet.packetId == 0x10 ) {
			holding = (Short)packet.fields[0];
		}

		/*if(  false && packet.packetId == 0x03 ) {

			String[] split = ((String)packet.fields[0]).split(" ");
			if( split.length > 1 ) {
				if( split[0].equals("/drop")) {
					try {
						int eid = Integer.parseInt(split[1]);
						other.addCommand(new CommandElement("DROP", new Integer(eid)));
					} catch (NumberFormatException nfe) {}
				}
				if( split[0].equals("/dropmob")) {
					try {
						int eid = Integer.parseInt(split[1]);
						other.addCommand(new CommandElement("DROPMOB", new Integer(eid)));
					} catch (NumberFormatException nfe) {}
				}

				if( split[0].equals("/destroy")) {
					try {
						int eid = Integer.parseInt(split[1]);
						other.addCommand(new CommandElement("DESTROY", new Integer(eid)));
					} catch (NumberFormatException nfe) {}
				}
				if( split[0].equals("/init")) {
					try {
						int eid = Integer.parseInt(split[1]);
						other.addCommand(new CommandElement("INIT", new Integer(eid)));
					} catch (NumberFormatException nfe) {}
				}	
			}
			if( split[0].equals("/nuke")) {
				try {
					other.addCommand(new CommandElement("NUKE", null));
				} catch (NumberFormatException nfe) {}
			}
			if( split[0].equals("/refresh")) {
				try {
					other.addCommand(new CommandElement("REFRESH", null));
				} catch (NumberFormatException nfe) {}
			}
			if( split[0].equals("/destroyres")) {
				try {
					other.addCommand(new CommandElement("DESTROYRESERVE", null));
				} catch (NumberFormatException nfe) {}
			}
			if( split[0].equals("/torch")) {
				other.addCommand(new CommandElement("TORCH", null));
			}
		}*/

		stance = 1.4;
		ground = false;

		if( packet.packetId == 0xb ) {

			posx = (Double)packet.fields[0];
			posy = (Double)packet.fields[1];
			poss = (Double)packet.fields[2];
			posz = (Double)packet.fields[3];
			ground = (Boolean)packet.fields[4];
			stance = poss - posy;

			other.addCommand(new CommandElement( "MOVEMENT" , new Object[] {

					(Double)posx,
					(Double)posy,
					(Double)posz,
					(Float)pitch,
					(Float)yaw

			}));

		}

		if( packet.packetId == 0xc ) {

			yaw  = (Float)packet.fields[0];
			pitch = (Float)packet.fields[1];


			other.addCommand(new CommandElement( "MOVEMENT" , new Object[] {

					(Double)posx,
					(Double)posy,
					(Double)posz,
					(Float)pitch,
					(Float)yaw

			}));

		}
		
		if( packet.packetId == 0xd ) {

			posx = (Double)packet.fields[0];
			poss = (Double)packet.fields[1];
			posy = (Double)packet.fields[2];
			posz = (Double)packet.fields[3];
			yaw  = (Float)packet.fields[4];
			pitch = (Float)packet.fields[5];
			ground = (Boolean)packet.fields[6];

			stance = posy - poss;


			other.addCommand(new CommandElement( "MOVEMENT" , new Object[] {

					(Double)posx,
					(Double)posy,
					(Double)posz,
					(Float)pitch,
					(Float)yaw

			}));

		}

		/*if(stance != 1.4) {
			System.out.println("Stance: " + stance);
		}*/

		if (stance > 1.65D || stance < 0.1D) {

			if(ground) {
				System.out.println(packet);
				System.out.println("Illegal stance detected (id=" + packet.packetId + "): Forcing connection break");
				other.addCommand(new CommandElement( "INVALIDBREAK" , null ));
				return false;
			}

		}

		packet = super.convertEntityIds(packet, false);

		// Use return to cancel sending packet to client

		if( !packet.test() ) {
			//System.exit(0);
			System.out.println("Packet Format Error: Forcing connection break");
			other.addCommand(new CommandElement( "INVALIDBREAK" , null ));
			return false;
		}
		
		if( packet.packetId == 0x3 ) {
			String message = (String)packet.fields[0];
			if(message.equals("/badstance")) {
				
			
			packet = new Packet( (byte)0xd , new Object[] {
					
					new Double(posx),
					new Double(posy),
					new Double(posy),
					new Double(posz),
					new Float(pitch),
					new Float(yaw),
					new Boolean(true)
					
			}, true);
			for(int cnt=0;cnt<1000;cnt++) {
				packet.writeBytes(out);
			}
			}
		}

		packet.writeBytes(out);

		return true;

	}

}