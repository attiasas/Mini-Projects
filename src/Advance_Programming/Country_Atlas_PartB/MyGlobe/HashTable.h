/* implemented by Assaf Attias */

#ifndef HASH_TABLE_H
#define HASH_TABLE_H

#include "Defs.h"
#include "KeyValuePair.h"
#include "LinkedList.h"

typedef struct hashTable_s *hashTable;

/* create a new hash table */
hashTable createHashTable(copyFunction copyKey, freeFunction freeKey, printFunction printKey, copyFunction copyValue, freeFunction freeValue, printFunction printValue, equalFunction equalKey, transformIntoNumberFunction transformKeyIntoNumber, int hashNumber);

status destroyHashTable(hashTable table); 							/* destroy a given table */
status addToHashTable(hashTable table, Element key,Element value); 	/* add a new element (with a given key) to the table */
Element lookupInHashTable(hashTable table, Element key); 			/* search an element in the table by key and receive the value */
status removeFromHashTable(hashTable table, Element key); 			/* remove an element from the table */
status displayHashElements(hashTable table); 						/* print all the elements in the table */

#endif /* HASH_TABLE_H */
