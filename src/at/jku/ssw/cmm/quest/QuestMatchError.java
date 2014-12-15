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
 
package at.jku.ssw.cmm.quest;

public class QuestMatchError {
	
	public QuestMatchError( String msg, Exception e ){
		this.msg = msg;
		this.e = e;
	}
	
	private final String msg;
	private final Exception e;
	
	public String getMessage(){
		return this.msg;
	}
	
	public void print(){
		// Head
		System.out.println("---------------------------------------------------------");
		System.out.println("        Quest output data match failure report");
		System.out.println("---------------------------------------------------------");
		
		// Body + description
		System.out.println("The reason is:		" + msg);
		
		if( this.e != null ){
			System.out.println("Terminated with:	" + e);
			System.out.println("");
			e.printStackTrace();
		}
		else
			System.out.println("... no further information available");
		
		// End
		System.out.println("---------------------------------------------------------");
	}

}
