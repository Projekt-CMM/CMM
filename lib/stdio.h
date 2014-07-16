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

// print character
void putc(char ch) {
    print(ch);
}

// print string
void prints(string s) {
    int i = 0; 
    while(i < length(s)) {
        putc(s[i]);
        i += 1;    
    }
}

// get string
string scanf() {
    string s;
    while(length(s) == 0 
            && s[length(s)-1] != ' ' && s[length(s)-1] != '\t' 
            && s[length(s)-1] != '\r' && s[length(s)-1] != '\n') {
        s += read();
    }
    return s;
}
