package com.raphfrk.craftproxy;

import java.util.Arrays;

public class MultiBlockArray {

	public short[] coords;
	public byte[] type;
	public byte[] data;

	int getXAt( int index ) {
		return ( coords[index] >> 24 ) & 0x000F;
	}

	int getZAt( int index ) {
		return ( coords[index] >> 16 ) & 0x000F;
	}

	int getYAt( int index ) {
		return coords[index] & 0x00FF;
	}

	@Override
	public boolean equals(Object obj) {

		if( obj == null || coords == null || type == null || data == null ) {
			return false;
		} else if( !obj.getClass().equals(this.getClass())) {
			return false;
		} else {
			
			MultiBlockArray other = (MultiBlockArray)obj;
			
			if( other.coords == null || other.data == null || other.type == null ) {
				return false;
			} else {
				return Arrays.equals(this.coords, other.coords) && Arrays.equals(this.data, other.data) && Arrays.equals(this.type, other.type);
			}
			
		}

	}

}
