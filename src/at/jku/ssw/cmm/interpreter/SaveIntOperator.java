package at.jku.ssw.cmm.interpreter;

public class SaveIntOperator {

	static final int add(int left, int right) throws ArithmeticException {
		if (right > 0 ? left > Integer.MAX_VALUE - right
           : left < Integer.MIN_VALUE - right) {
			throw new ArithmeticException("Integer overflow");
		}
		return left + right;
	}

	static final int subtract(int left, int right) throws ArithmeticException {
		if (right > 0 ? left < Integer.MIN_VALUE + right
		   : left > Integer.MAX_VALUE + right) {
			throw new ArithmeticException("Integer overflow");
		}
		return left - right;
	}

	static final int multiply(int left, int right) throws ArithmeticException {
		if (right > 0 ? left > Integer.MAX_VALUE/right || left < Integer.MIN_VALUE/right
           : (right < -1 ? left > Integer.MIN_VALUE/right || left < Integer.MAX_VALUE/right
           : right == -1 && left == Integer.MIN_VALUE) ) {
			throw new ArithmeticException("Integer overflow");
		}
		return left * right;
	}

	static final int divide(int left, int right) throws ArithmeticException {
		if ((left == Integer.MIN_VALUE) && (right == -1)) {
			throw new ArithmeticException("Integer overflow");
		}
		return left / right;
	}

	static final int negate(int a) throws ArithmeticException {
		if (a == Integer.MIN_VALUE) {
			throw new ArithmeticException("Integer overflow");
		}
		return -a;
	}

	static final int abs(int a) throws ArithmeticException {
		if (a == Integer.MIN_VALUE) {
			throw new ArithmeticException("Integer overflow");
		}
		return Math.abs(a);
	}

}