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