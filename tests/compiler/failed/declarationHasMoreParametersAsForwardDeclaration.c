/**
* forward-declaration and declaration have not the same parameter count
*
* @programmer Thomas Pointhuber
* @date 8 July 2014
*/

void foo(int x);

// generate the error, to much parameters
int foo(int x, int y) {
}

void main() {
}