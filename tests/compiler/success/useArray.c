/**
* declare struct and use array
*
* @programmer Thomas Pointhuber
* @date 8 July 2014
*/

int[8] arr;

void main() {
	arr[0] = 12;
	arr[7] = arr[0];
	arr[0+1] = 5;
	arr[(2*3)-2] = 8;
}
