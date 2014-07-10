/**
* implicite conversation
*
* @programmer Thomas Pointhuber
* @date 10 July 2014
*/

void foo(int y) {
}

int bar() {
	return 'a';
}

void main() {
	int x;
	float f;
	char ch;
	
	f = x;
	ch = x;
	
	x = ch;
	x = f;
	f = ch;
	
	foo(f);
	foo(ch);
	
	x = bar();
	f = bar();
	ch = bar();
}