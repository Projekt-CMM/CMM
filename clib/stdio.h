/*
 * implementation of stdio.h of the c standard-lib
 *
 * http://www.cplusplus.com/reference/cstdio/
 */

//------------------- Forward declarations

void library putc(char ch);
void library prints(string s);
string library scanf();

//------------------- declarations

/** print character
 *
 * @working yes
 */
void library putc(char ch) {
    print(ch);
}

/** print string
 *
 * @working yes
 */
void library prints(string s) {
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
string library scanf() {
    string s = "";
    char c;
    while(1 == 1) {
        // read character
        c = read();
        // check if end of scanf is reached
        if(c == ' ' 
            || c == '\t'
            || c == '\r' 
            || c == '\n'
            || c == '\0') {
            return s;
        } else {
            s += c;
        }
    }
}
