package com.raphfrk.craftproxy;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;



public class Protocol {

	public static byte[] tobytes( Byte[] inp ) {

		byte[] ret = new byte[inp.length];

		int cnt=0;
		for( Byte current: inp ) {
			ret[cnt++] = current;
		}

		return ret;

	}

	public static byte[] tobytes( ArrayList<Byte> inp ) {

		byte[] ret = new byte[inp.size()];

		int cnt=0;
		for( Byte current: inp ) {
			ret[cnt++] = current;
		}

		return ret;

	}

	public static Byte[] bytesToBytes( byte[] inp ) {

		Byte[] ret = new Byte[inp.length];

		int cnt=0;
		for( byte current: inp ) {
			ret[cnt++] = current;
		}

		return ret;

	}

	public static ArrayList<Byte> bytesToArrayList( byte[] inp ) {

		ArrayList<Byte> list = new ArrayList<Byte>();

		int cnt=0;
		for( byte current: inp ) {
			list.add(current);
		}

		return list;

	}

	public static ArrayList<Byte> genBoolean( boolean in ) {

		ArrayList<Byte> bytes = new ArrayList<Byte>();

		bytes.add((byte)(in?1:0));

		return bytes;
	}

	public static boolean getBoolean( DataInputStream in ) {

		try {
			return (in.readByte())!=0;
		} catch (IOException e) {
			System.out.println( "Unable to getByte" );
			return false;
		}

	}

	public static ArrayList<Byte> genByte( byte in ) {

		ArrayList<Byte> bytes = new ArrayList<Byte>();

		bytes.add(in);

		return bytes;
	}

	public static byte getByte( DataInputStream in ) {

		try {
			return in.readByte();
		} catch (IOException e) {
			System.out.println( "Unable to getByte" );
			return 0;
		}

	}

	public static ArrayList<Byte> genShort( short in ) {

		ArrayList<Byte> bytes = new ArrayList<Byte>();

		bytes.add((byte)(in >> 8));
		bytes.add((byte)(in >> 0));

		return bytes;
	}

	public static short getShort( DataInputStream in ) {

		try {
			return in.readShort();
		} catch (IOException e) {
			System.out.println( "Unable to getShort" );
			return 0;
		}

	}

	public static ArrayList<Byte> genInt( int in ) {

		ArrayList<Byte> bytes = new ArrayList<Byte>();

		bytes.add((byte)(in >> 24));
		bytes.add((byte)(in >> 16));
		bytes.add((byte)(in >> 8));
		bytes.add((byte)(in >> 0));

		return bytes;
	}

	public static int getInt( DataInputStream in ) {

		try {
			return in.readInt();
		} catch (IOException e) {
			System.out.println( "Unable to getInt" );
			return 0;
		}

	}

	public static ArrayList<Byte> genLong( long in ) {

		ArrayList<Byte> bytes = new ArrayList<Byte>();

		bytes.add((byte)(in >> 56));
		bytes.add((byte)(in >> 48));
		bytes.add((byte)(in >> 40));
		bytes.add((byte)(in >> 32));
		bytes.add((byte)(in >> 24));
		bytes.add((byte)(in >> 16));
		bytes.add((byte)(in >> 8));
		bytes.add((byte)(in >> 0));

		return bytes;
	}

	public static long getLong( DataInputStream in ) {

		try {
			return in.readLong();
		} catch (IOException e) {
			System.out.println( "Unable to getLong" );
			return 0;
		}

	}

	public static ArrayList<Byte> genFloat( float in ) {

		ByteArrayOutputStream array = new ByteArrayOutputStream();

		DataOutputStream out = new DataOutputStream( array );

		try {
			out.writeFloat(in);
		} catch (IOException e) {
			System.out.println( "Float generation failed");
			return new ArrayList<Byte>();
		}

		return bytesToArrayList(array.toByteArray());
	}

	public static float getFloat( DataInputStream in ) {

		try {
			return in.readFloat();
		} catch (IOException e) {
			System.out.println( "Unable to getFloat" );
			return 0;
		}

	}

	public static ArrayList<Byte> genDouble( double in ) {

		ByteArrayOutputStream array = new ByteArrayOutputStream();

		DataOutputStream out = new DataOutputStream( array );

		try {
			out.writeDouble(in);
			out.flush();
		} catch (IOException e) {
			System.out.println( "Double generation failed");
			return new ArrayList<Byte>();
		}

		return bytesToArrayList(array.toByteArray());
	}

	public static double getDouble( DataInputStream in ) {

		try {
			return in.readDouble();
		} catch (IOException e) {
			System.out.println( "Unable to getLong" );
			return 0;
		}

	}

	public static ArrayList<Byte> genItemElement( ItemElement itemElement ) {

		ArrayList<Byte> bytes = new ArrayList<Byte>();

		bytes.addAll(genShort(itemElement.id));

		if( itemElement.id != -1 ) {

			bytes.addAll(genByte(itemElement.count));
			bytes.addAll(genShort(itemElement.damage));

		}

		return bytes;


	}

	public static ItemElement getItemElement( DataInputStream in ) {

		ItemElement item= new ItemElement();

		item.id = getShort(in);


		if( item.id != -1 ) {
			item.count = getByte(in);
			item.damage = getShort(in);
		}

		return item;


	}

	public static ArrayList<Byte> genItemArray( ItemArray itemArray ) {

		ArrayList<Byte> bytes = new ArrayList<Byte>();

		bytes.addAll(genShort((short)itemArray.array.size()));

		for( ItemElement element : itemArray.array ) {

			bytes.addAll(genShort(element.id));

			if( element.id != -1 ) {

				bytes.addAll(genByte(element.count));
				bytes.addAll(genShort(element.damage));

			}

		}

		return bytes;


	}

	public static IntSizedByteArray getIntSizedByteArray( DataInputStream in ) {

		IntSizedByteArray data = new IntSizedByteArray();

		data.size = getInt(in);

		data.data = new byte[data.size];

		int offset=0;

		int read = 0;

		while( offset < data.size && read >= 0) {

			try {
				read = in.read(data.data, offset, data.size - offset );
			} catch (IOException e) {
				data.data = new byte[1];
				data.size = 1;
				return data;
			}
			offset += read;

		}

		return data;


	}

	public static ArrayList<Byte> genIntSizedByteArray( IntSizedByteArray data ) {

		ArrayList<Byte> bytes = new ArrayList<Byte>();

		bytes.addAll(genInt(data.data.length));

		bytes.addAll(bytesToArrayList(data.data));

		return bytes;


	}

	public static IntSizedTripleByteArray getIntSizedTripleByteArray( DataInputStream in ) {

		IntSizedTripleByteArray data = new IntSizedTripleByteArray();

		data.size = getInt(in)*3;

		if( data.size < 0 || data.size > 65536 ) {
			System.out.println( "Explosion size exceeded limit: " + data.size );
			return null;
		}

		data.data = new byte[data.size];

		int offset=0;

		int read = 0;

		while( offset < data.size && read >= 0) {

			try {
				read = in.read(data.data, offset, data.size - offset );
			} catch (IOException e) {
				data.data = new byte[1];
				data.size = 1;
				return data;
			}
			offset += read;

		}

		return data;


	}

	public static ArrayList<Byte> genIntSizedTripleByteArray( IntSizedTripleByteArray data ) {

		ArrayList<Byte> bytes = new ArrayList<Byte>();

		bytes.addAll(genInt(data.data.length/3));

		bytes.addAll(bytesToArrayList(data.data));

		return bytes;


	}

	public static ItemArray getItemArray( DataInputStream in ) {

		ItemArray itemArray = new ItemArray();

		itemArray.array = new ArrayList<ItemElement>();

		int count = getShort(in);

		//System.out.println( "Inv length: " + count );

		int pos = 0;

		while( pos++ < count ) {

			ItemElement current = new ItemElement();

			current.id = getShort(in);

			if( Globals.isVerbose() ) {
				System.out.println( "ID: " + current.id );
			}

			if( current.id != -1 ) {
				current.count = getByte(in);
				current.damage = getShort(in);
			}

			itemArray.array.add(current);

		}

		return itemArray;


	}

	public static ArrayList<Byte> genEntityMetadata( EntityMetadata data ) {

		ArrayList<Byte> bytes = new ArrayList<Byte>();

		for(int pos=0;pos<data.elements.size();pos++) {

			bytes.addAll(genByte(data.ids.get(pos)));

			Object currentElement = data.elements.get(pos);

			if( currentElement instanceof Byte ) {
				bytes.addAll(genByte((Byte)currentElement));
			} else if( currentElement instanceof Short ) {
				bytes.addAll(genShort((Short)currentElement));
			} else if( currentElement instanceof Integer ) {
				bytes.addAll(genInt((Integer)currentElement));
			} else if( currentElement instanceof Float ) {
				bytes.addAll(genFloat((Float)currentElement));
			} else if( currentElement instanceof String ) {
				bytes.addAll(genString((String)currentElement));
			} else {
				bytes.addAll(genItemElement((ItemElement)currentElement));
				System.out.println( "unable to parse Meta Data");
			}

		}

		bytes.addAll(genByte((byte)127));

		return bytes;


	}

	public static EntityMetadata getEntityMetadata( DataInputStream in ) {

		EntityMetadata entityMetadata = new EntityMetadata();

		byte id = getByte(in);
		byte type = (byte)((id&(0xE0)) >> 5);

		do{

			Object element;

			switch(type) {
			case 0: element = getByte(in); break;
			case 1: element = getShort(in); break;
			case 2: element = getInt(in); break;
			case 3: element = getFloat(in); break;
			case 4: element = getString(in); break;
			case 5: element = getItemElement(in); break;
			default: 
				System.out.println( "Unable to process meta data"); 
				element = "";
				break;
			}

			entityMetadata.elements.add(element);
			entityMetadata.ids.add(id);

			id = getByte(in);
			type = (byte)((id&(0xE0)) >> 5);

		} while (id!=127);


		return entityMetadata;


	}

	public static ArrayList<Byte> genMultiBlockArray( MultiBlockArray data ) {

		ArrayList<Byte> bytes = new ArrayList<Byte>();

		bytes.addAll(genShort((short)(data.data.length)));

		for( short coord : data.coords ) {
			bytes.addAll( genShort(coord) );
		}

		bytes.addAll(bytesToArrayList(data.type));

		bytes.addAll(bytesToArrayList(data.data));

		return bytes;


	}

	public static MultiBlockArray getMultiBlockArray( DataInputStream in ) {

		MultiBlockArray multiBlockArray = new MultiBlockArray();

		int count = getShort(in);

		multiBlockArray.coords = new short[count];
		multiBlockArray.data   = new byte[count];
		multiBlockArray.type   = new byte[count];

		int pos;

		for(pos=0;pos<count;pos++) {
			multiBlockArray.coords[pos] = getShort(in); 
		}

		for(pos=0;pos<count;pos++) {
			multiBlockArray.type[pos] = getByte(in); 
		}

		for(pos=0;pos<count;pos++) {
			multiBlockArray.data[pos] = getByte(in); 
		}

		return multiBlockArray;


	}

	public static ArrayList<Byte> genString( String in) {

		ArrayList<Byte> bytes = new ArrayList<Byte>();

		byte[] utf8;
		try {
			utf8 = in.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.out.print("UTF-8 not supported");
			return new ArrayList<Byte>();
		}

		bytes.addAll(genShort( (short)utf8.length));

		for( byte current : utf8 ) {
			bytes.add(current);
		}

		return bytes;

	}

	public static String getString( DataInputStream in ) {

		short length = getShort(in);

		if( length < 0 ) {
			System.out.println( "Negative length string detected");
			return "";
		}

		if( length > 5000 ) {
			System.out.println( "Strings above 5000 characters are blocked");
			System.out.println( "This can represent a login attack");
			return "";
		}

		int pos = 0;
		byte[] buffer = new byte[length];
		while( pos < length ) {
			try {
				pos += in.read(buffer, pos, length-pos);
			} catch (IOException e) {
				System.out.println( "Unable to read string");
				return "";
			}
		}

		try {
			return new String( buffer , "UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.out.print("UTF-8 not supported in getString");
			return new String( "" );
		}

	}

	public static ArrayList<Byte> genKickPacket( String message ) {

		ArrayList<Byte> bytes = new ArrayList<Byte>();

		bytes.add((byte)0xFF);

		bytes.addAll(genString(message));

		return bytes;

	}

	static String sha1Hash( String inputString ) {

		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			md.reset();

			md.update(inputString.getBytes("utf-8"));

			BigInteger bigInt = new BigInteger( md.digest() );

			return bigInt.toString( 16 ) ;

		} catch (Exception ioe) {
			return "hash error";
		}

	}

	static String getHashString() {
		long hashLong;
		synchronized( hashGenerator ) {
			hashLong = hashGenerator.nextLong();
		}

		return Long.toHexString(hashLong);
	}

	// High level functions

	static SecureRandom hashGenerator = new SecureRandom();

	static boolean processLogin( DataInputStream inputFromClient, DataOutputStream outputToClient, PlayerRecord playerRecord , String source ) {

		Packet handshakeFromClient = new Packet();
		if( !getPacket(handshakeFromClient, inputFromClient, (byte)0x02)) {
			return false;
		}

		if( Globals.isInfo() ) {
			System.out.println( "Packet received:" + handshakeFromClient);
		}

		String username = (String)handshakeFromClient.fields[0];

		System.out.println( username + " attempting to connect from " + source );

		playerRecord.username = new String( username );

		String hashString;

		if( Globals.isAuth() ) {
			hashString = getHashString();
		} else {
			hashString = "-";
		}

		Packet handshakeToClient = new Packet( 
				(byte)0x02, 
				new Object[] { hashString },
				true
		);

		if( Globals.isInfo() ) {
			System.out.println( "Server Handshake:\n" + handshakeToClient + "\n");
			System.out.flush();
		}

		if( !handshakeToClient.writeBytes(outputToClient) ) {
			return false;
		}

		Packet clientLogin = new Packet();
		if( !getPacket(clientLogin, inputFromClient, (byte)0x01)) {
			return false;
		}

		if( Globals.isInfo() ) {
			System.out.println( "Client Login:\n" + clientLogin + "\n");
		}

		String password = (String)clientLogin.fields[2];

		String[] split = password.split(":");

		boolean forward = false;

		if( split.length == 2 ) {
			String newHashString = split[0];
			String hashPassword = sha1Hash(Globals.getPassword() + hashString + newHashString);
			if(split[1].equals(hashPassword)) {
				forward = true;
				playerRecord.forward = true;
			}
		}

		if( Globals.isAuth() && !forward && !authenticated( username , hashString ) ) {

			System.out.println( username + " failed auth");

			return false;
		} else if( !Globals.isAuth() ) {
			System.out.println( "Skipping auth" );
		} else {
			if( forward ) {
				System.out.println( username + " is being forwarded" );
			} else {
				System.out.println( username + " passed auth" );
			}
		}

		/*playerRecord.clientEntityID = Globals.getDefaultPlayerId();

		Packet serverLogin = new Packet( (byte)0x01, 
				new Object[] { 
				new Integer(playerRecord.clientEntityID),
				new String(""),
				new String(""),
				//new Long(9069285780089680887L),
				new Long(7069285780089680987L),
				new Byte((byte)0)
		},
		true
		);

		if( !serverLogin.writeBytes(outputToClient) ) {
			return false;
		}*/

		return true;

	}

	static boolean serverLogin( DataInputStream inputFromServer, DataOutputStream outputToServer, PlayerRecord playerRecord ) {
		return serverLogin(inputFromServer, outputToServer, playerRecord, false );
	}

	static boolean serverLogin( DataInputStream inputFromServer, DataOutputStream outputToServer, PlayerRecord playerRecord, boolean passKick ) {

		Packet handshakeToServer = new Packet( 
				(byte)0x02, 
				new Object[] { playerRecord.username },
				true
		);

		if( Globals.isInfo() ) {
			System.out.println( "Sending handshake to server:\n" + handshakeToServer + "\n");
			System.out.flush();
		}

		if( !handshakeToServer.writeBytes(outputToServer) ) {
			return false;
		}

		Packet handshakeFromServer = new Packet();

		if( !getPacket(handshakeFromServer, inputFromServer, (byte)0x02) ) {
			System.out.println( "returning false");
			return false;
		}

		if( Globals.isInfo() ) {
			System.out.println( "Received handshake from server:\n" + handshakeFromServer + "\n");
			System.out.flush();
		}

		String hashString = (String)handshakeFromServer.fields[0];

		String passwordSHA = "Password";
		String newHashString = getHashString();

		if( !hashString.equals("-") ) {
			System.out.println( "Authentication is enabled on the back-end server, attempting to use password");

			passwordSHA = newHashString + ":" + sha1Hash(Globals.getPassword() + hashString + newHashString);
		}

		Packet proxyLogin = new Packet( (byte)0x01, 
				new Object[] { 
				new Integer(Globals.getClientVersion()),
				new String(playerRecord.username),
				new String(passwordSHA),
				new Long(0),
				new Byte((byte)0)
		},
		true
		);

		if( Globals.isInfo() ) {
			System.out.println( "Sending login to server:\n" + proxyLogin+ "\n");
			System.out.flush();
		}

		if( !proxyLogin.writeBytes(outputToServer)) {
			return false;
		}

		Packet serverLogin = new Packet();
		if( !getPacket( serverLogin, inputFromServer, (passKick)?null:((byte)0x01))) {
			return false;
		}

		if(serverLogin.packetId != (byte)0x01 && serverLogin.packetId != -1) {
			return false;
		}

		if(serverLogin.packetId == (byte)0x01) {
			serverLogin.fields[4] = new Byte(Globals.isHell()?((byte)-1):(byte)0);
		}

		if( Globals.isInfo() ) {
			System.out.println( "Received login from server:\n" + serverLogin + "\n");
			System.out.flush();
		}

		if(serverLogin.packetId == (byte)0x01) {
			playerRecord.serverEntityID = (Integer)serverLogin.fields[0];
		}
		playerRecord.loginPacket = serverLogin;

		return serverLogin.packetId == 0x01;

	}

	static boolean getPacket( Packet packet, DataInputStream input, Byte packetId ) {

		Packet newPacket = new Packet( input , false );

		boolean first = true;

		long startTime = System.currentTimeMillis();

		while( !newPacket.valid || first ) {

			first = false;			

			if( newPacket.valid && packetId != null && newPacket.packetId != packetId ) {
				System.out.println( "Correct packet " + packetId + " not received");
				System.out.println("Packet received: " + newPacket.packetId );
				System.out.println(newPacket);
				return false;
			} else if( newPacket.eof ) {
				System.out.println( "Socket closed");
				return false;
			} else if ( newPacket.timeout ) {
				//System.out.println( "Login process going slowly");
				if( System.currentTimeMillis() > startTime + 20000 ) {
					System.out.println( "Login process timed out, minecraft.net is probably overloaded");
					return false;
				}
			}

			if( !newPacket.valid ) {
				newPacket = new Packet( input , false );
			}

		}

		packet.copy(newPacket);

		return true;

	}

	static boolean authenticated( String username , String hashString )  {

		try {
			String authURLString = new String( "http://www.minecraft.net/game/checkserver.jsp?user=" + username + "&serverId=" + hashString);
			if(!Globals.isQuiet()) {
				System.out.println( "Authing with " + authURLString);
			}
			System.out.flush();
			URL minecraft = new URL(authURLString);
			URLConnection minecraftConnection = minecraft.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(minecraftConnection.getInputStream()));

			String reply = in.readLine();

			if( Globals.isInfo() ) {
				System.out.println( "Server Response: " + reply );
			}

			if( reply != null && reply.equals("YES")) {
				in.close();
				if(!Globals.isQuiet()) {
					System.out.println( "Auth successful");
				}
				return true;
			}
		} catch (MalformedURLException mue) {
			System.out.println( "Auth URL error");
		} catch (IOException ioe) {
			System.out.println( "Problem connecting to auth server");
		}

		return false;


	}

	static void kick( DataOutputStream outputToClient, String message) {

		Packet kickPacket = new Packet(
				(byte)0xFF,
				new Object[]{ new String(message) },
				true
		);

		kickPacket.writeBytes(outputToClient);

	}



}
