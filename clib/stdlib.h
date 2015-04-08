/*
 * implementation of stdlib.h of the c standard-lib
 *
 * http://www.cplusplus.com/reference/cstdlib/
 */

#ifndef __CLIB_STDLIB__

#define __CLIB_STDLIB__

#include <math.h>

//------------------- Forward declarations

string library itoa(int x);
string library ftoa(float f);

int library atoi(string s);
float library atof(string s);

int library rand();
void library srand (int seed);

//------------------- variable declarations

int library __cur_rand_generator_state__;

//------------------- declarations

/** convert int to string
 *
 * @working yes
 */
string library itoa(int x) {
    string s = "";
    char ch;
    int y = x;
      
    if(x == 0) {
        return "0";
    } else {
        // y has to be positive
        if(y < 0) {
            y *= -1;
        }
        // calculate characters
        while(y != 0) {
            ch = (char)((y%10)+'0');
            s = ch + s;
            y /= 10;
        }
        // add minus-sign at begin of string if required
        if(x < 0) {
            s = '-' + s;
        }
    }
    
    // return string
    return s;
}

/** convert float to string
 *
 * @working yes
 * @todo using exponential formating
 */
string library ftoa(float f) {
    // calculate decimal part
    string s = itoa((int) f);
    s += '.';
    // f has to be positive
	if(f < 0) {
	    f *= -1;
	}
    // calculate decimal places
    int n = 0;
    while(n <= 4) {
        f -= (int)f;
        // get next decimal place
        f *=10;
        s += itoa((int) f);
        n += 1;
    }
    	
    // return string
    return s;
}

/** convert string to int 
 *
 * @working yes
 * @todo remove leading whitespaces
 */
int library atoi(string s) {
    int ret = 0;
    int i = 0;
    int multpl = 1;

    if(s[i] == '+') {
        multpl = 1;
        i++;
    } else if(s[i] == '-') {
      	multpl = -1;
        i++;
    }
    
    // parse digits
    for(; length(s) > i; i++) {
        if(s[i] >= '0' && s[i] <= '9') {
            ret *= 10;
            ret += s[i]-'0';
        }
    }
    
    // return integer
    return ret * multpl;
}


/** convert string to float
 *
 * @working yes
 * @todo remove leading whitespaces
 */
float library atof(string s) {
    float ret = 0.;
    int i = 0;
    float multpl = 1.;

    // check if string is not empty
    if(length(s) == 0)
    	return 0.;

    // TODO: remove leading whitespaces

    // check for leading sign
    if(s[i] == '+') {
    	multpl = 1.;
    	i++;
    } else if(s[i] == '-') {
    	multpl = -1.;
    	i++;
    }

    // parse predecimals
    for(; i < length(s); i++) {
    	if(s[i] >= '0' && s[i] <= '9') {
    		// parse digit
    		ret *= 10;
        	ret += s[i]-'0';
        } else
            break;
    }

    // check if function is already finished
    if(i >= length(s))
    	return ret*multpl;

    // parse decimal places if possible
    float factor = 0.1;
    if(s[i] == '.') {
    	i++;
    	for(; i < length(s); i++) {
    		if(s[i] >= '0' && s[i] <= '9')
    	        ret += (s[i]-'0')*factor;
    		else
    			break;
    		factor /= 10.;
    	}
    }

    // check if function is already finished
    if(i >= length(s))
    	return ret*multpl;

    // parse exponent
    float exponentMultpl = 1.;
    float exponent = 0;
    if(s[i] == 'e' || s[i] == 'E') {
    	i++;

    	// check if function is already finished
    	if(i >= length(s))
    		return ret*multpl;

    	if(s[i] == '+') {
    		exponentMultpl = 1.;
    	    i++;
    	} else if(s[i] == '-') {
    		exponentMultpl = -1.;
    	    i++;
    	}

    	// parse exponent
    	for(; i < length(s); i++) {
    	    if(s[i] >= '0' && s[i] <= '9') {
    	    	// parse digit
    	    	exponent *= 10;
    	    	exponent += s[i]-'0';
    	    } else
    	    	break;
    	}

    	ret *= pow(10, exponent*exponentMultpl);
    }

    return ret*multpl;
}

/** get random number
 *
 * implemented as Xorshift
 * https://de.wikipedia.org/wiki/Xorshift
 *
 * @working yes
 */
int library rand() {
    // TODO not defined variable become start value
    if(!__is_def_int__(__cur_rand_generator_state__))
        __cur_rand_generator_state__ = 13;

    if(__cur_rand_generator_state__ == 0)
        __cur_rand_generator_state__ += 97;

    // doing Xorshift
    __cur_rand_generator_state__ ^= __cur_rand_generator_state__ << 13;
    __cur_rand_generator_state__ ^= __cur_rand_generator_state__ >> 17;
    __cur_rand_generator_state__ ^= __cur_rand_generator_state__ << 5;

    // only positive results are allowed
    if(__cur_rand_generator_state__ < 0)
        __cur_rand_generator_state__ *= -1;

    return __cur_rand_generator_state__;
}

/** init random number generator
 *
 * @working yes
 */
void library srand (int seed) {
    __cur_rand_generator_state__ = seed;
}

#endif /* __CLIB_STDLIB__ */
