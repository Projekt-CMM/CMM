/*
 *  This file is part of C-Compact.
 *
 *  C-Compact is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  C-Compact is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with C-Compact. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright (c) 2014-2015 Fabian Hummer
 *  Copyright (c) 2014-2015 Thomas Pointhuber
 *  Copyright (c) 2014-2015 Peter Wassermair
 */
 
package at.jku.ssw.cmm.compiler;

/*--------------------------------------------------------------------------------
Strings   String storage of a C-- program
=======   ===============================
A string is an immutable, null-terminated sequence of characters.
The string storage holds all string constants of a C-- program as well as all
strings that are created by casting a char array to a string or by concattenating
strings. Strings with the same values are stored only once in the string storage.
--------------------------------------------------------------------------------*/

import java.util.Map;
import java.util.HashMap;

public final class Strings {
	static private char[] data = new char[4096]; // grows automatically
	static private int top = 0;
	static private Map<String, Integer> map = new HashMap<>();

	// Puts the string s into the string storage and returns its address there.
	// If s is already in the string storage, s is not added again, but the address of
	// the existing string is returned.
	// s may still contain escape sequences (such as \t or \r) that must be converted first.
	public static int put(String s) {
		// make bigger array if nessesary
		if(top+s.length()+1 >= data.length) {
			char[] oldData = data;
			data = new char[data.length+4096];
			data = oldData.clone();
		}
		
		if(!map.containsKey(s)) {
			int j = top;
			map.put(s, top);
			top += s.length()+1;
			
			for(int i=0;i< s.length();i++,j++) {
				data[j] = s.charAt(i);
			}

			data[j] = '\0';	// set 0-terminator
		}
		
		return map.get(s);
	}

	// Returns the string that is stored at adr in the string storage
	public static String get(int adr) {
		if(!checkAdr(adr)) {
			return new String();
		}
		String returnStr = new String();
		while(data[adr] != '\0') {
			returnStr += data[adr];
			adr ++;
		}
		
		return returnStr;
	}

	// Returns the character at adr in the string storage
	public static char charAt(int adr) {
		if(checkAdr(adr)) {
			return data[adr];
		} else {
			return ' ';
		}
	}
	
	public static boolean checkAdr(int adr) {
		if(adr < 0 || adr > data.length) {
			return false;
		}
		else {
			return true;
		}
	}
	
	public void dump() {
		System.out.println("Strings:");
		for(int i=0; i < top;i++) {
			String curChar = ""+charAt(i);
			switch(curChar.charAt(0)) {
				case '\0':
					curChar = "\\0";
					break;
				case '\n':
					curChar = "\\n";
					break;
				case '\r':
					curChar = "\\r";
					break;
				case '\t':
					curChar = "\\t";
					break;
				default:
					break;
			}
			System.out.println("  char[" + i + "]='" + curChar + "'");
		}
	}
}