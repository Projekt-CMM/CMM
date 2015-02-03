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

public class SaveIntOperator {

	public static final int add(int left, int right) throws ArithmeticException {
		if (right > 0 ? left > Integer.MAX_VALUE - right
           : left < Integer.MIN_VALUE - right) {
			throw new ArithmeticException("Integer overflow");
		}
		return left + right;
	}

	public static final int subtract(int left, int right) throws ArithmeticException {
		if (right > 0 ? left < Integer.MIN_VALUE + right
		   : left > Integer.MAX_VALUE + right) {
			throw new ArithmeticException("Integer overflow");
		}
		return left - right;
	}

	public static final int multiply(int left, int right) throws ArithmeticException {
		if (right > 0 ? left > Integer.MAX_VALUE/right || left < Integer.MIN_VALUE/right
           : (right < -1 ? left > Integer.MIN_VALUE/right || left < Integer.MAX_VALUE/right
           : right == -1 && left == Integer.MIN_VALUE) ) {
			throw new ArithmeticException("Integer overflow");
		}
		return left * right;
	}

	public static final int divide(int left, int right) throws ArithmeticException {
		if ((left == Integer.MIN_VALUE) && (right == -1)) {
			throw new ArithmeticException("Integer overflow");
		}
		return left / right;
	}

	public static final int negate(int a) throws ArithmeticException {
		if (a == Integer.MIN_VALUE) {
			throw new ArithmeticException("Integer overflow");
		}
		return -a;
	}

	public static final int abs(int a) throws ArithmeticException {
		if (a == Integer.MIN_VALUE) {
			throw new ArithmeticException("Integer overflow");
		}
		return Math.abs(a);
	}

}