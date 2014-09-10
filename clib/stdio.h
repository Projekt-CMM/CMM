/*
 * implementation of stdio.h of the c standard-lib
 *
 * http://www.cplusplus.com/reference/cstdio/
 */

//------------------- Forward declarations

void putc(char ch);
void prints(string s);
string scanf();

//------------------- declarations

/** print character
 *
 * @working yes
 */
void putc(char ch) {
    print(ch);
}

/** print string
 *
 * @working yes
 */
void prints(string s) {
    int i = 0; 
    while(i < length(s)) {
        putc(s[i]);
        i += 1;    
    }
}

/** get string
 *
 * @working yes
 */
string scanf() {
    string s = "";
    while(1 == 1) {
        // read character
        s += read();
        // check if end of scanf is reached
        if(s[length(s)-1] == ' ' 
            || s[length(s)-1] == '\t'
            || s[length(s)-1] == '\r' 
            || s[length(s)-1] == '\n'
            || s[length(s)-1] == '\0') {
            return s;
        }
    }
}
