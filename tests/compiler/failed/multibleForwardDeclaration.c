/**
* multible forward-declaration of function
*
* @programmer Thomas Pointhuber
* @date 8 July 2014
*/


void foo();

// generate the error, second forward-declaration
void foo();

void foo() {
}

void main() {
}