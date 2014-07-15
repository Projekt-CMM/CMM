/*
 * implementation of stdio.h of the c standard-lib
 */

//------------------- Forward declarations

void putc(char ch);
void prints(string s);

//------------------- declarations

// print character
void putc(char ch) {
    print(ch);
}

// print string
void prints(string s) {
    int i;
    i = 0;    
    while(i < length(s)) {
        putc(s[i];
        i = i+1;    
    }
}
