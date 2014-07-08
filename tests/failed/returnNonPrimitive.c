/**
* return a none-primitive type
*
* @programmer Thomas Pointhuber
* @date 8 July 2014
*/

struct Point {
	int x,y;
}

Point foo() {
	Point p;
	return p;
}

void main() {
	Point p;
	p = foo();
}