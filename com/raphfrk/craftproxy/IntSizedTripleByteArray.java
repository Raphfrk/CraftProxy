package com.raphfrk.craftproxy;

import java.util.Arrays;

public class IntSizedTripleByteArray {
	
	int size;
	byte[] data;
	
	@Override
	public boolean equals(Object obj) {

		if( obj == null || data == null ) {
			return false;
		} else if( !obj.getClass().equals(this.getClass())) {
			return false;
		} else {
			
			IntSizedTripleByteArray other = (IntSizedTripleByteArray)obj;
			
			if( other.data == null ) {
				return false;
			} else {
				return Arrays.equals(this.data, other.data) && this.size==other.size;
			}
			
		}

	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("("+size+")");
		
		for( byte current : data ) {
			sb.append(":" + current );
		}
		
		return sb.toString();
		
	}

}
