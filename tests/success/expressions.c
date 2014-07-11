/**
* different expressions
*
* @programmer Thomas Pointhuber
* @date 10 July 2014
*/

int foo() {
	return 10;
}

void main() {
	int x,y,z;
	float f;
	char ch;
	
	x = x+y;
	x = x-y;
	x = x*y;
	x = x/y;
	x = x%y;
	x = ~y;
	x = y >> 5;
	x = y << 5;
	x = y | 5;
	x = y & 5;
	x = y ^ 5;
	x = ++y;
	x = --y;
	
	f = x+y*z;
	f = (x+y)*foo();
	
	ch = x+y-z;
}