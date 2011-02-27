package com.raphfrk.craftproxy;

import java.util.Arrays;

public class MultiBlockArray {

	public short[] coords;
	public byte[] type;
	public byte[] data;

	int getXAt( int index ) {
		return ( coords[index] >> 12 ) & 0x000F;
	}

	int getZAt( int index ) {
		return ( coords[index] >> 8 ) & 0x000F;
	}

	int getYAt( int index ) {
		return coords[index] & 0x00FF;
	}

	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		int size = coords.length;
		for(int cnt=0;cnt<size;cnt++) {
			sb.append(
				"[("+Integer.toHexString(coords[cnt]&0xFFFF) + ":" 
					+getXAt(cnt)+","
					+getYAt(cnt)+","
					+getZAt(cnt)+"),"
					+type[cnt] + "," 
					+data[cnt] + "],");
		}

		return sb.toString();
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
