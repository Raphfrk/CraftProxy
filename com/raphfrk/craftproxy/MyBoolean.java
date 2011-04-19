package com.raphfrk.craftproxy;

public class MyBoolean {
	
	boolean value;
	
	MyBoolean( boolean value ) {
		this.value = value;
	}
	
	void set( boolean value ) {
		this.value = value;
	}
	
	boolean get() {
		return value;
	}
	
	@Override 
	public boolean equals( Object other ) {
		
		if( other.getClass().equals(Boolean.class)) {
			return value == (Boolean)other;
		} else if( other.getClass().equals(MyBoolean.class)) {
			return value == ((MyBoolean)other).get();
		} else {
			return false;
		}
		
	}
}
