/* implemented by Assaf Attias */

#include "HashTable.h"

// == Structure & Definition =================================================

struct hashTable_s
{
	int tableSize;
	LinkedList *table;

	copyFunction copyKey;
	freeFunction freeKey;
	printFunction printKey;
	equalFunction compareKey;

	transformIntoNumberFunction transformKey;

	 copyFunction copyValue;
	 freeFunction freeValue;
	 printFunction printValue;
};

// ===========================================================================

// == STATIC Functions =======================================================

/* Apply hash function on a given key (int type) */
static int hash(hashTable table, int key)
{
	// Validate
	if(key < 0) key = -key;

	return (key % table->tableSize);
}

// ===========================================================================

// == PUBLIC Functions =======================================================

/* create a new hash table */
hashTable createHashTable(copyFunction copyKey, freeFunction freeKey, printFunction printKey, copyFunction copyValue, freeFunction freeValue, printFunction printValue, equalFunction equalKey, transformIntoNumberFunction transformKeyIntoNumber, int hashNumber)
{
	// Validate
	if(copyKey == NULL || freeKey == NULL || printKey == NULL || copyValue == NULL || freeValue == NULL || printValue == NULL || equalKey == NULL || transformKeyIntoNumber == NULL || hashNumber <= 0) return NULL;

	// Allocate
	LinkedList *table = (LinkedList *)calloc(hashNumber, sizeof(LinkedList));
	if(table == NULL)
	{
		printf("no memory available");
		exit(1);
	}

	for(int i = 0; i < hashNumber; i++)
	{
		// Create Lists
		table[i] = createLinkedList(copyPair, freePair, printPair, comparePair);
		if(table[i] == NULL)
		{
			while(i > 0)
			{
				destroyList(table[i - 1]);
				i--;
			}
			free(table);

			return NULL;
		}
	}

	hashTable result = (hashTable)malloc(sizeof(struct hashTable_s));
	if(result == NULL)
	{
		for(int i = 0; i < hashNumber; i++) destroyList(table[i]);
		free(table);
		printf("no memory available");
		exit(1);
	}

	// Init
	result->tableSize = hashNumber;
	result->table = table;

	result->copyKey=copyKey;
	result->freeKey=freeKey;
	result->printKey=printKey;
	result->compareKey=equalKey;
	result->transformKey=transformKeyIntoNumber;
	result->copyValue=copyValue;
	result->freeValue=freeValue;
	result->printValue=printValue;

	return result;
}

/* destroy a given table */
status destroyHashTable(hashTable table)
{
	// Validate
	if(table == NULL) return failure;

	for(int i = 0; i < table->tableSize; i++) destroyList(table->table[i]);

	free(table->table);

	free(table);

	return success;
}

/* add a new element (with a given key) to the table */
status addToHashTable(hashTable table, Element key,Element value)
{
	// Validate
	if(table == NULL || key == NULL || value == NULL || lookupInHashTable(table, key) != NULL) return failure;

	int index = hash(table, table->transformKey(key));
	KeyValuePair pair = createKeyValuePair(key, table->copyKey, table->freeKey, table->printKey, table->compareKey, value, table->copyValue, table->freeValue, table->printValue, comparePair);

	status result = appendNode(table->table[index], pair);

	destroyKeyValuePair(pair);

	return result;
}

/* search an element in the table by key and receive the value */
Element lookupInHashTable(hashTable table, Element key)
{
	// Validate
	if(table == NULL || key == NULL) return NULL;

	int index = hash(table, table->transformKey(key));

	return getValue((KeyValuePair)searchInList(table->table[index], key));
}

/* remove an element from the table */
status removeFromHashTable(hashTable table, Element key)
{
	// Validate
	if(table == NULL || key == NULL || lookupInHashTable(table, key) == NULL) return failure;

	int index = hash(table, table->transformKey(key));

	return deleteNode(table->table[index], key);
}

/* print all the elements in the table */
status displayHashElements(hashTable table)
{
	// Validate
	if(table == NULL) return failure;

	for(int i = 0; i < table->tableSize; i++) displayList(table->table[i]);

	return success;
}

// ===========================================================================
