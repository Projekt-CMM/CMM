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