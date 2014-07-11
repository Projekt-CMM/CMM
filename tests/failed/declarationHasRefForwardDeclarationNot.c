/**
* prarameter of declaration has ref, forward-declaration not
*
* @programmer Thomas Pointhuber
* @date 8 July 2014
*/

void foo(int x);

// ref not in forward-declaration
int foo(ref int x) {
}

void main() {
}