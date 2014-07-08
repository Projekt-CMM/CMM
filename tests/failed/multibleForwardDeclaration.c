/**
* multible forward-declaration of function
*
* @programmer Thomas Pointhuber
* @date 8 July 2014
*/


void foo(); forward;

// generate the error, second forward-declaration
void foo(); forward;

void foo() {
}

void main() {
}