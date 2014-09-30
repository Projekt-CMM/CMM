/**
* declare struct and use it
*
* @programmer Thomas Pointhuber
* @date 8 July 2014
*/

struct Point {
	int x, y;
}

Point p;

void main() {
	p.x = 1;
	p.y = p.x;
}
