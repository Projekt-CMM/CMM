/**
* parameter of forward declaration has ref, declaration not
*
* @programmer Thomas Pointhuber
* @date 8 July 2014
*/

void foo(ref int x); forward;

// ref in forward-declaration
int foo(int x) {
}

void main() {
}