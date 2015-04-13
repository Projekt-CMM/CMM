/*
 * implementation of math.h of the c standard-lib
 *
 * https://de.wikipedia.org/wiki/Math.h
 * http://www.acm.uiuc.edu/webmonkeys/book/c_guide/2.7.html
 * http://www.cplusplus.com/reference/cmath/
 */

#ifndef __CLIB_MATH__

#define __CLIB_MATH__

//------------------- const declarations

const float library M_E     = 2.718281828;      //-- The base of natural logarithms (e).
const float library M_LOG2E = 1.442695040;      //-- The base-2 logarithm of e.
const float library M_LOG10E= 0.434294482;      //-- The base-10 logarithm of e.
const float library M_LN2   = 0.693147181;      //-- The natural logarithm of 2.
const float library M_LN10  = 2.302585093;      //-- The natural logarithm of 10.

const float library M_PI    = 3.141592654;      //-- pi.
const float library M_PI_2  = 1.570796327;      //-- pi/2
const float library M_PI_4  = 0.785398164;      //-- pi/4
 
const float library M_1_PI  = 0.318309886;      //-- 1/pi
const float library M_2_PI  = 0.636619772;      //-- 2/pi

const float library M_2_SQRTPI  = 1.128379167;  //-- 2/sqrt(pi)

const float library M_SQRT2     = 1.414213562;  //-- sqrt(2)
const float library M_SQRT1_2   = 0.707106781;  //-- sqrt(1/2)

// TODO
const float library MAXFLOAT    = 1.E+2000000;  //-- The maximum value of a non-infinite single- precision floating point number.
const float library HUGE        = 1.E+2000000;  //-- The maximum value of a single-precision floating-point number. 
const float library HUGE_VAL    = 1.E+2000000;  //-- positive infinity. 


//------------------- Forward declarations

float library acos(float x);
float library asin(float x);
float library atan(float x);  // TODO
float library atan2(float y, float x);  // TODO
float library ceil(float x);
float library cos(float x);
float library cosh(float x);
float library exp(float x);
int library fak(int x);
float library fabs(float x);
float library floor(float x);
float library fmod(float x, float y);
//float library frexp(float x);  // TODO
float library ldexp(float x, int y);
float library log(float x);
float library log10(float x);
//float library modf();  // TODO
float library pow(float x, float y);
float library ipow(float x, int y);
float library sin(float x);
float library sinh(float x);
float library sqrt(float x);
float library tan(float x);
float library tanh(float x);


//------------------- declarations

/** Arkuskosinus \arccos x
 *
 * @working yes
 *
 * https://de.wikipedia.org/wiki/Taylorreihe#Trigonometrische_Funktionen
 * http://www.cplusplus.com/reference/cmath/acos/
 */
float library acos(float x) {
	if(x < -1 || x > 1)
		__assert__(false, "math.h::acos: parameter has to be between -1 and +1");

    float result = M_PI_2-asin(x);

    result -= result*(int)(result/M_PI);

    return result; // TODO
}

/** Arkussinus 	\arcsin x
 *
 * @working yes
 *
 * https://de.wikipedia.org/wiki/Taylorreihe#Trigonometrische_Funktionen
 * http://www.cplusplus.com/reference/cmath/asin/
 */
float library asin(float x) {
	if(x < -1 || x > 1)
		__assert__(false, "math.h::asin: parameter has to be between -1 and +1");

    float result = 0;
    int n = 0;

    while(n<=6) { // TODO
        result += fak(2*n)/(ipow(4,n)*ipow(fak(n),2)*(2*n+1))*ipow(x, 2*n+1);
        n++;
    }
    return result; // TODO
}

/** Arkustangens 	\arctan x
 *
 * @working partly
 *
 * https://de.wikipedia.org/wiki/Taylorreihe#Trigonometrische_Funktionen
 * http://www.cplusplus.com/reference/cmath/atan/
 */
float library atan(float x) {
    float result = 0;
    int n = 0;

    while(n<=6) { // TODO
        result += ipow(-1,n)*1/(2*n+1)*ipow(x,2*n+1);
        n++;
    }
    return result;  // TODO
}

/** „Arkustangens“ mit zwei Argumenten 	\operatorname{atan2}(y, x)
 *
 * @working no (probably partly)
 *
 * https://en.wikipedia.org/wiki/Atan2#Definition_and_computation
 * http://www.cplusplus.com/reference/cmath/atan2/
 */
float library atan2(float y, float x) {
    return (2*atan(y/(sqrt(ipow(x,2)+ipow(y,2))+x)));
}

/** Aufrundungsfunktion 	\lceil x \rceil
 *
 * @working yes
 *
 * http://www.cplusplus.com/reference/cmath/ceil/
 */
float library ceil(float x) {
    int xAsInt = x;
    if(xAsInt >= x) {
        return (float)((int)x);
    } else {
        return (float)((int)x)+1;
    }
}

/** Kosinus 	\cos x
 *
 * @working yes
 *
 * https://de.wikipedia.org/wiki/Taylorreihe#Trigonometrische_Funktionen
 * http://www.cplusplus.com/reference/cmath/cos/
 */
float library cos(float x) {
    float result = 0;
    int n = 0;
    x = fmod(x, M_PI);

    while(n<=6) {
        result += ipow(-1.,n) * ipow(x,2*n)/fak(2*n);
        n++;
    }
    return result;
}

/** Kosinus Hyperbolicus 	\cosh x
 *
 * @working yes
 *
 * https://de.wikipedia.org/wiki/Sinus_Hyperbolicus_und_Kosinus_Hyperbolicus#Reihenentwicklungen
 * http://www.cplusplus.com/reference/cmath/cosh/
 */
float library cosh(float x) {
    float result = 0;
    int n = 0;

    while(n<=6) { // TODO
        result += ipow(x,2*n) / fak(2*n);
        n++;
    }
    return result;
}

/** Exponentialfunktion 	e^x
 *
 * @working yes
 *
 * https://de.wikipedia.org/wiki/Taylorreihe#Exponentialfunktionen_und_Logarithmen
 * http://www.cplusplus.com/reference/cmath/exp/
 */
float library exp(float x) {
    float result = 0;
    int n = 0;

    while(n<=6) { // TODO
        result += ipow(x,n)/fak(n);
        n++;
    }
    return result;
}

/** Fakultät
 *
 * @working yes
 */
int library fak(int x) {
    int result = 1, n = 1;

    while(n <= x) {
        result *= n;
        n++;
    }
    return result;
}

/** Betragsfunktion |x|
 *
 * @working yes
 *
 * http://www.cplusplus.com/reference/cmath/fabs/
 */
float library fabs(float x) {
    if(x >= 0)
        return x;    
    else
        return x * -1;
}

/** Ganzteilfunktion 	\lfloor x \rfloor
 *
 * @working yes
 *
 * http://www.cplusplus.com/reference/cmath/floor/
 */
float library floor(float x) {
    int xAsInt = x;
    if(xAsInt <= x)
        return (float)((int)x);
    else
        return (float)((int)x)-1;
}

/** Führt die Modulo Funktion für Gleitkommazahlen durch 	x \bmod y
 *
 * @working yes
 *
 * http://www.cplusplus.com/reference/cmath/fmod/
 */
float library fmod(float x, float y) {
    x -= y*floor((x)/y);
    return x;
}

/** Teilt eine Gleitkommazahl in Faktor und Potenz mit der Basis 2 auf
 *
 * @working no
 *
 * http://www.cplusplus.com/reference/cmath/frexp/
 */
/*float library frexp(float x) {
    return 0.;
}*/

/** Multipliziert den ersten Parameter mit 2 um den zweiten Parameter potenziert 	x 2^y
 *
 * @working yes
 *
 * http://www.cplusplus.com/reference/cmath/ldexp/
 */
float library ldexp(float x, int y) {
    return pow(x*2,y);
}

/** Natürlicher Logarithmus 	\ln x
 *
 * @working yes
 *
 * http://fiziko.bureau42.com/teaching_tidbits/manual_logarithms.pdf
 * http://www.cplusplus.com/reference/cmath/log/
 */
float library log(float x) {
	if(x < 0)
		__assert__(false, "math.h::log: parameter has to be positive");

	float result = 0;
	int n = 1;
	// taylor expansion is only working from 0 <= x <= 2
	if(x >= 2) {
		result = -1. * log(1./x);
	} else {
		while(n<=30) { // TODO
			result += ipow(-1, n+1) * (ipow(x-1.,n)/n);
			n++;
		}
	}
	return result;
}

/** Logarithmus zur Basis 10 	\log_{10} x
 *
 * @working yes
 *
 * http://www.cplusplus.com/reference/cmath/log10/
 */
float library log10(float x) {
	if(x < 0)
		__assert__(false, "math.h::log10: parameter has to be positive");

	return log(x)/M_LN10;
}

/** Teilt eine Gleitkommazahl in zwei Zahlen auf, vor und nach dem Komma
 *
 * @working no
 *
 * http://www.cplusplus.com/reference/cmath/modf/
 */
/*float library modf() {
    return 0.; // TODO
}*/

/** Potenziert ersten mit dem zweiten Parameter 	x^y
 *
 * @working yes
 *
 * http://www.cplusplus.com/reference/cmath/pow/
 */
float library pow(float x, float y) {
	return exp(y * log(x));
}

/** Potenziert ersten mit dem zweiten Parameter 	x^(int)y
 *
 * @working yes
 *
 * https://de.wikipedia.org/wiki/Bin%C3%A4re_Exponentiation
 * http://www.programminglogic.com/fast-exponentiation-algorithms/
 */
float library ipow(float x, int y) {
	// if potence is negative, using 1/(x^(-y))
	if(y < 0)
		return 1/ipow(x, -1*y);

    float result = 1;

    while (y != 0) {
        if (y&1 == 1) {
            result *= x;
        }
        y >>= 1 ;
        x *= x;
    }
    return result;
}

/** Sinus 	\sin x
 *
 * @working yes
 *
 * https://de.wikipedia.org/wiki/Taylorreihe#Trigonometrische_Funktionen
 * http://www.cplusplus.com/reference/cmath/sin/
 */
float library sin(float x) {
    float result = 0;
    int n = 0;

    x = fmod(x, M_PI);

    while(n<=5) {
        result += ipow(-1.,n) * ipow(x,2*n+1)/fak(2*n+1);
        n++;
    }
    return result;
}

/** Sinus Hyperbolicus 	\sinh x
 *
 * @working yes
 *
 * https://de.wikipedia.org/wiki/Sinus_Hyperbolicus_und_Kosinus_Hyperbolicus#Reihenentwicklungen
 * http://www.cplusplus.com/reference/cmath/sinh/
 */
float library sinh(float x) {
    float result = 0;
    int n = 0;

    while(n<=5) { // TODO
        result += ipow(x,2*n+1) / fak(2*n+1);
        n++;
    }
    return result;
}

/** Quadratwurzel 	\sqrt x
 *
 * @working no // TODO
 *
 * https://de.wikipedia.org/wiki/Babylonisches_Wurzelziehen
 * http://www.cplusplus.com/reference/cmath/sqrt/
 */
float library sqrt(float x) {
    float xn = (x+1)/2;
    int n = 0;

    while(n<=6) {
        xn += (xn + (x/xn)) / 2;
        n++;
    }
    return xn;
}

/** Tangens 	\tan x
 *
 * @working no // TODO
 *
 * https://de.wikipedia.org/wiki/Tangens_und_Kotangens#Reihenentwicklung
 * http://www.cplusplus.com/reference/cmath/tan/
 */
float library tan(float x) {
	float result = 0;
	int n = 1;
	// https://en.wikipedia.org/wiki/Bernoulli_number
	float bernoulli_number[6] = {1/6, -1/30, 1/42, -1/30, 5/66, -691/2730};

	while(n<=5) { // TODO
		result += (ipow(-1,n-1)*ipow(2,2*n)*(ipow(2,2*n)-1)*bernoulli_number[n-1])/fak(2*n)*ipow(x,2*n-1);
		n++;
	}
	return result;
}

/** Tangens Hyperbolicus 	\tanh x
 *
 * @working no // TODO
 *
 * http://www.cplusplus.com/reference/cmath/tanh/
 */
float library tanh(float x) {
    return 0.;
}

#endif /* __CLIB_MATH__ */
