/*
 * modified implementation of assert.h of the c standard-lib
 *
 * http://www.cplusplus.com/reference/cassert/assert/
 */

#ifndef __CLIB_ASSERT__

#define __CLIB_ASSERT__

#include <regex.h>

//------------------- Forward declarations

void library assert(bool con, string msg);

void library assertTrue(bool var, string msg);
void library assertFalse(bool var, string msg);

void library assertEqBool(bool b1, bool b2, string msg);
void library assertNotEqBool(bool b1, bool b2, string msg);

void library assertEqChar(char c1, char c2, string msg);
void library assertNotEqChar(char c1, char c2, string msg);

void library assertEqInt(int i1, int i2, string msg);
void library assertNotEqInt(int i1, int i2, string msg);

void library assertEqFloat(float f1, float f2, float delta, string msg);
void library assertNotEqFloat(float f1, float f2, float delta, string msg);

void library assertEqString(string str1, string str2, string msg);
void library assertNotEqString(string str1, string str2, string msg);

void library assertMatch(string reg, string str, string msg);
void library assertNotMatch(string reg, string str, string msg);

//------------------- declarations

void library assert(bool con, string msg) {
    __assert__(con, "Assertion failed: " +  msg);
}

void library assertTrue(bool var, string msg) {
    if(var != true)
        __assert__(false, "Assertion failed: " +  msg);
}

void library assertFalse(bool var, string msg) {
    if(var != false)
        __assert__(false, "Assertion failed: " +  msg);
}

void library assertEqBool(bool b1, bool b2, string msg) {
    if(b1 == b2)
        __assert__(false, "Assertion failed: " +  msg);
}

void library assertNotEqBool(bool b1, bool b2, string msg) {
    if(b1 == b2)
        __assert__(false, "Assertion failed: " +  msg);
}

void library assertEqChar(char c1, char c2, string msg) {
    if(c1 != c2)
        __assert__(false, "Assertion failed: " +  msg);
}

void library assertNotEqChar(char c1, char c2, string msg) {
    if(c1 == c2)
        __assert__(false, "Assertion failed: " +  msg);
}

void library assertEqInt(int i1, int i2, string msg) {
    if(i1 != i2)
        __assert__(false, "Assertion failed: " +  msg);
}

void library assertNotEqInt(int i1, int i2, string msg) {
    if(i1 == i2)
        __assert__(false, "Assertion failed: " +  msg);
}

void library assertEqFloat(float f1, float f2, float delta, string msg) {
    if(f1-f2 >= delta || f2-f1 >= delta)
        __assert__(false, "Assertion failed: " +  msg);
}

void library assertNotEqFloat(float f1, float f2, float delta, string msg) {
    if(f1-f2 <= delta && f2-f1 <= delta)
        __assert__(false, "Assertion failed: " +  msg);
}

void library assertEqString(string str1, string str2, string msg) {
    if(str1 != str2)
        __assert__(false, "Assertion failed: " +  msg);
}

void library assertNotEqString(string str1, string str2, string msg) {
    if(str1 == str2)
        __assert__(false, "Assertion failed: " +  msg);
}

void library assertMatch(string reg, string str, string msg) {
    if(!match(reg, str))
        __assert__(false, "Assertion failed: " +  msg);
}

void library assertNotMatch(string reg, string str, string msg) {
    if(match(reg, str))
        __assert__(false, "Assertion failed: " +  msg);
}

#endif /* __CLIB_ASSERT__ */
