/**
* forward-declaration is after real declaration
*
* @programmer Thomas Pointhuber
* @date 8 July 2014
*/

void foo() {
}

// generate the error, object already declared
void foo();

void main() {
}