package com.raphfrk.craftproxy;

import java.util.ArrayList;
import java.util.List;

public class ItemArray {

	ArrayList<ItemElement> array = new ArrayList<ItemElement>();
	
	@Override
	public boolean equals(Object obj) {

		if( obj == null || array == null ) {
			return false;
		} else if( !obj.getClass().equals(this.getClass())) {
			return false;
		} else {
			
			ItemArray other = (ItemArray)obj;
			
			if( other.array == null ) {
				return false;
			} else {
				return this.array.equals(other.array);
			}
			
		}

	}
	
}
