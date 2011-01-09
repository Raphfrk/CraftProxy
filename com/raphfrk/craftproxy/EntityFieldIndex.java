package com.raphfrk.craftproxy;

public class EntityFieldIndex {
	
	static Integer[][] fields = null;
	
	
	static void init() {

		fields = new Integer[256][];
		
		fields[0x05] = new Integer[] {0};
		fields[0x07] = new Integer[] {0,1};
		fields[0x12] = new Integer[] {0};
		fields[0x14] = new Integer[] {0};
		fields[0x15] = new Integer[] {0};
		fields[0x16] = new Integer[] {0,1};
		fields[0x17] = new Integer[] {0};
		fields[0x18] = new Integer[] {0};
		fields[0x1C] = new Integer[] {0};
		fields[0x1D] = new Integer[] {0};
		fields[0x1E] = new Integer[] {0};
		fields[0x1F] = new Integer[] {0};
		fields[0x20] = new Integer[] {0};
		fields[0x21] = new Integer[] {0};
		fields[0x22] = new Integer[] {0};
		fields[0x26] = new Integer[] {0};
		fields[0x27] = new Integer[] {0,1};
		
	}
	
	static Integer[] getEntityIds( byte packetId ) {
		
		return fields[((int)packetId)&0xFF];
		
	}
	
}
