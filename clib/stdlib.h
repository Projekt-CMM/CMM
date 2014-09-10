/*
 * implementation of stdlib.h of the c standard-lib
 *
 * http://www.cplusplus.com/reference/cstdlib/
 */

//------------------- Forward declarations

string itoa(int x);
//string ftoa(float f);

int atoi(string s);
//float atof(string s);

//------------------- declarations

/** convert int to string
 *
 * @working yes
 */
string itoa(int x) {
    string s = "";
    char ch;
    while(x != 0) {
        ch = (char)((x%10)+'0');
        s = ch + s;
        x /= 10;
    }

    return s;
}

// convert float to string
/*string ftoa(int f) {
}*/

/** convert string to int 
 *
 * @working yes
 */
int atoi(string s) {
    int ret = 0;
    int i = 0;
    while(length(s) > i) {
        if(s[i] >= '0' && s[i] <= '9') {
            ret *= 10;
            ret += s[i]-'0';
        }
        i += 1;
    }
    return ret;
}


// convert string to float
/*float atof(string s) {
    // TODO
    float ret = 0.;
    int i = 0;
    int afterPoint = 0;

    while(length(s) < i) {
        if(s[i] >= '0' && s[i] <= '9') {
            ret *= 10;
            ret += s[i]-'0';
        } else if(s[i] == '.') {
            afterPoint = 1;
        }
        i += 1;
    }
    return ret;
}*/
