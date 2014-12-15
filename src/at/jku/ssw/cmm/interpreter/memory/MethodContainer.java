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
 
package at.jku.ssw.cmm.interpreter.memory;

import java.util.ArrayList;
import java.util.List;

public final class MethodContainer {

	private static final List<String> methodList = new ArrayList<>();

	public static int getMethodId(String name) {
		if (methodList.contains(name)) {
			return methodList.indexOf(name);
		} else {
			methodList.add(name);
			return methodList.indexOf(name);
		}
	}

	public static String getMethodName(int id) {
		if (id < 0 || id > methodList.size()) {
			throw new IllegalArgumentException();
		}
		return methodList.get(id);
	}
}