package com.raphfrk.craftproxy;

import java.util.ArrayList;

public class EntityMetadata {

	ArrayList<Byte> ids = new ArrayList<Byte>();
	ArrayList<Object> elements = new ArrayList<Object>();
	
	@Override
	public boolean equals(Object obj) {

		if( obj == null  ) {
			return false;
		} else if( !obj.getClass().equals(this.getClass())) {
			return false;
		} else {
			
			EntityMetadata other = (EntityMetadata)obj;
			
			if( other.ids.size() != this.ids.size() ) {
				return false;
			}
			
			if( other.elements.size() != this.elements.size() ) {
				return false;
			}
			
			int pos;
			
			for(pos=0;pos<ids.size();pos++) {
				if( !other.ids.get(pos).equals(ids.get(pos))) {
					return false;
				}
			}
			
			for(pos=0;pos<elements.size();pos++) {
				if( !other.elements.get(pos).equals(elements.get(pos))) {
					return false;
				}
			}
			
			return true;
						
		}

	}
	
	 @Override public String toString() {

		 if( elements.size() != ids.size() ) {
			 return "Error elements and id array different lengths";
		 }
		 
		 StringBuilder sb = new StringBuilder("[");
		 
		 for(int pos=0;pos<elements.size();pos++) {
			 sb.append(" (" + ids.get(pos) + ":" + elements.get(pos) + ") ");
		 }
		 
		 return sb.toString() + "]";
		 
	 }
	
	
}
