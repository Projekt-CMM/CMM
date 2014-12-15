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
 
package at.jku.ssw.cmm.interpreter;

public class SaveFloatOperator {

	static final float add(float left, float right) throws ArithmeticException {
		if (right > 0 ? left > Float.MAX_VALUE - right
           : left < Integer.MIN_VALUE - right) {
			throw new ArithmeticException("Float overflow");
		}
		return left + right;
	}

	static final float subtract(float left, float right) throws ArithmeticException {
		if (right > 0 ? left < Float.MIN_VALUE + right
		   : left > Float.MAX_VALUE + right) {
			throw new ArithmeticException("Float overflow");
		}
		return left - right;
	}

	static final float multiply(float left, float right) throws ArithmeticException {
		if (right > 0 ? left > Float.MAX_VALUE/right || left < Float.MIN_VALUE/right
           : (right < -1 ? left > Float.MIN_VALUE/right || left < Float.MAX_VALUE/right
           : right == -1 && left == Float.MIN_VALUE) ) {
			throw new ArithmeticException("Float overflow");
		}
		return left * right;
	}

	static final float divide(float left, float right) throws ArithmeticException {
		if ((left == Float.MIN_VALUE) && (right == -1)) {
			throw new ArithmeticException("Float overflow");
		}
		return left / right;
	}

	static final float negate(float a) throws ArithmeticException {
		if (a == Float.MIN_VALUE) {
			throw new ArithmeticException("Float overflow");
		}
		return -a;
	}

	static final float abs(float a) throws ArithmeticException {
		if (a == Float.MIN_VALUE) {
			throw new ArithmeticException("Float overflow");
		}
		return Math.abs(a);
	}

}