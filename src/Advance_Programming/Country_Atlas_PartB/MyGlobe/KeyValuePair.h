/* implemented by Assaf Attias */

#ifndef KEY_VALUE_H
#define KEY_VALUE_H

#include "Defs.h"

typedef struct keyValuePair_s *KeyValuePair;

/* create a new pair */
KeyValuePair createKeyValuePair(Element key, copyFunction copyKey, freeFunction freeKey, printFunction printKey, equalFunction compKey, Element value, copyFunction copyValue, freeFunction freeValue, printFunction printValue, equalFunction compValue);

status destroyKeyValuePair(KeyValuePair pair); 						/* destroy a given pair */
status displayValue(KeyValuePair pair); 							/* prints the value */
status displayKey(KeyValuePair pair); 								/* prints the key */
Element getValue(KeyValuePair pair); 								/* Getter for the value */
Element getKey(KeyValuePair pair); 									/* Getter for the key */
bool isEqualKey(KeyValuePair pair, KeyValuePair otherPair); 		/* check if two pairs has the same key */

Element copyPair(Element element); 				/* copy function for nested generic ADT */
status freePair(Element element); 				/* free function for nested generic ADT */
status printPair(Element element); 				/* print function for nested generic ADT - Printing the VALUE */
bool comparePair(Element pair, Element key); 	/* compare function for nested generic ADT - Comparing by KEY */

#endif /* KEY_VALUE_H */
