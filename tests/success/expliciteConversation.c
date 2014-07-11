/**
* explicite conversation
*
* @programmer Thomas Pointhuber
* @date 10 July 2014
*/

float bar() {
	return (char)10.5;
}

void foo(char ch2) {
}

void main() {
	int x;
	float f;
	char ch;
	
	ch = (char) f;

	f = (char)f;
	x = (float)ch;
	
	x = (int)(float)(char)(int)ch;
	
	foo((char) f);
	
	ch = (char)bar();
}