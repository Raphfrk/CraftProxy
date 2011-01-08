package com.raphfrk.craftproxy;

import java.util.Arrays;

public class IntSizedByteArray {
	
	int size;
	byte[] data;
	
	@Override
	public boolean equals(Object obj) {

		if( obj == null || data == null ) {
			return false;
		} else if( !obj.getClass().equals(this.getClass())) {
			return false;
		} else {
			
			IntSizedByteArray other = (IntSizedByteArray)obj;
			
			if( other.data == null ) {
				return false;
			} else {
				return Arrays.equals(this.data, other.data) && this.size==other.size;
			}
			
		}

	}

}
