/*
 * little implementation of an regular expression matcher
 *
 * source: Beautiful Code, Page 26
 */

#ifndef __CLIB_REGEX__

#define __CLIB_REGEX__

#include <string.h>

//------------------- Forward declarations

bool library match(string regexp, string text);
bool library matchhere(string regexp, string text);
bool library matchstar(char c, string regexp, string text);

//------------------- declarations

/** search for regexp anywhere in text 
 *
 * supported controll-characters: ^, $, ., *
 */
bool library match(string regexp, string text) {
	if (regexp[0] == '^')
		return matchhere(substr(regexp,1), text);
	do {
		/* must look even if string is empty */
		if(matchhere(regexp, text))
			return true;
		text = substr(text,1);
	} while (length(text) >= 1);
	return false;
}

/** search for regexp at beginning of text 
 *
 */
bool library matchhere(string regexp, string text) {
	if (length(regexp) == 0)
		return true;
	if (length(regexp) > 1 && regexp[1] == '*')
		return matchstar(regexp[0], substr(regexp, 2), text);
	if (regexp[0] == '$' && length(regexp) == 1) {
		if(!length(text))
			return true;
		else
			return false;
	}
	if (length(text)>0 && (regexp[0]=='.' || regexp[0]==text[0]))
		return matchhere(substr(regexp,1), substr(text,1));
	return false;
}

/** search for c*regexp at beginning of text 
 *
 */
bool library matchstar(char c, string regexp, string text) {
	do {
		/* a * matches zero or more instances */
		if (matchhere(regexp, text))
			return true;
		if(length(text)>0)
			text = substr(text,1);
	} while (length(text)>0 && (text[0] == c || c == '.'));
	return false;
}

#endif /* __CLIB_REGEX__ */
