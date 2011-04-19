package com.raphfrk.craftproxy;

import java.util.Iterator;
import java.util.LinkedList;

public class PacketIdStore {

	LinkedList<Integer> packetStore = new LinkedList<Integer>();

	int size;
	
	PacketIdStore() {
		this(10);
	}
	
	PacketIdStore( int size ) {
		this.size = size;
	}
	
	
	void add(int id) {
		
		packetStore.addFirst(id);
		while( packetStore.size() > size ) {
			packetStore.removeLast();
		}
		
	}
	
	@Override public String toString() {

		 Iterator<Integer> itr = packetStore.iterator();
		 
		 StringBuilder sb = new StringBuilder();
		 
		 boolean first = true;
		 while( itr.hasNext() ) {
			 
			 if( !first ) {
				 sb.append(", ");
			 } else {
				 first = false;
			 }
			 
			 sb.append( Integer.toHexString(packetStore.removeFirst()) );
			 
		 }
		 
		 return sb.toString();
		 
	 }


}
