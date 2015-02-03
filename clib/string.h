/*
 * implementation of string.h of the c standard-lib
 *
 * http://www.cplusplus.com/reference/cstring/
 */

#ifndef __CLIB_STRING__

#define __CLIB_STRING__

//------------------- Forward declarations

string library strcat ( string destination, string source );
int library strcmp ( string str1, string str2 );
string library substr(string str, int offset);

//------------------- declarations

/** Concatenate strings
 *
 */
string library strcat ( string destination, string source ) {
    destination += source;
    return destination;
}

/** Compare two strings
 *
 */
int library strcmp ( string str1, string str2 ) {
    if(str1 == str2) {
        return 1;
        }
    else {
        return 0;
    }
}


string library substr(string str, int offset) {
	string retStr = "";
	int i = offset;
	for(; i < length(str); i++) {
		retStr += str[i];
	}

	return retStr;
}

#endif /* __CLIB_STRING__ */
