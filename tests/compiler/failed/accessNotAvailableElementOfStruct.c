/**
* acces a not available struct element
*
* @programmer Thomas Pointhuber
* @date 9 July 2014
*/

struct Point {
	int x,y;
};

void main() {
	Point p;
	p.z = 1;
}