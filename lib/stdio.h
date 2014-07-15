/*
 * implementation of stdio.h of the c standard-lib
 */

//------------------- Forward declarations

void putc(char ch);
void prints(string s);

string itoa(int x);

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
        putc(s[i]);
        i += 1;    
    }
}

// convert int to string
string itoa(int x) {
    string s;
    char[1] ch;
    while(x != 0) {
        ch[0] = (char)((x%10)+48);
        s = (string)ch + s;
        x %= 10;
    }

    return s;
}
