package com.raphfrk.craftproxy;

public class ItemElement {
	
	short id = -1;
	byte count = 0;
	short damage = 0;
	
	@Override
	public boolean equals(Object obj) {

		if( obj == null  ) {
			return false;
		} else if( !obj.getClass().equals(this.getClass())) {
			return false;
		} else {
			
			ItemElement other = (ItemElement)obj;
			
			return other.id == this.id && other.count == this.count && other.damage == this.damage;
			
		}

	}
	
	 @Override public String toString() {

		 if( id == -1 ) {
			 return new String( "Item[empty]" );
		 } else {
			 return new String( "Item[" + count + " of " + id + " (" + damage + ")]");
		 }
		 
	 }


}
