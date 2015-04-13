#ifndef __CLIB_ARRAY__

#define __CLIB_ARRAY__

#include <stdlib.h>
#include <stdio.h>

//------------------- Forward declarations

void library readIntArray(int a[], int length);
void library readFloatArray(float a[], int length);

void library printIntArray(int a[], int length);
void library printFloatArray(float a[], int length);


//------------------- declarations

/** store input data into array
 *
 * @working yes
 */
void library readIntArray(int a[], int length) {
	int i;
	for( i = 0; i < length; i++ ) {
		a[i] = atoi(scanf());
	}
}

void library readFloatArray(float a[], int length) {
	int i;
	for( i = 0; i < length; i++ ) {
		a[i] = atof(scanf());
	}
}

/** Print contents of an array
 *
 * @working yes
 */
void library printIntArray(int a[], int length) {
	int i;
	for( i = 0; i < length; i++ ) {
		printf("%d, ", a[i]);
	}
}

void library printFloatArray(float a[], int length) {
	int i;
	for( i = 0; i < length; i++ ) {
		printf("%f, ", a[i]);
	}
}

#endif /* __CLIB_ARRAY__ */
