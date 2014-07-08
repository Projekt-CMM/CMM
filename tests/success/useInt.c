/**
* use int with different number-formats
*
* @programmer Thomas Pointhuber
* @date 8 July 2014
*/

int x;

void main() {
	// decimal
	x = 10;
	x = -510;
	//x = +13;
	
	// octal
	x = 0123;
	x = -0563;
	//x = +0562;
	
	// hex
	x = 0xF153;
	x = -0xF153;
	//x = +0xA;
	x = 0Xad5;
	x = #5d3;
}
