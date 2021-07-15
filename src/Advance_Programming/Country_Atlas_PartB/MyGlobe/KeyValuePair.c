/* implemented by Assaf Attias */

#include "KeyValuePair.h"

// == Structure & Definition =================================================

struct keyValuePair_s
{
	Element key;
	Element value;

	copyFunction copyKey;
	freeFunction freeKey;
	printFunction printKey;
	equalFunction compKey;

	copyFunction copyValue;
	freeFunction freeValue;
	printFunction printValue;
	equalFunction compValue;
};

// ===========================================================================

// == PUBLIC Functions =======================================================

/* create a new pair */
KeyValuePair createKeyValuePair(Element key, copyFunction copyKey, freeFunction freeKey, printFunction printKey, equalFunction compKey, Element value, copyFunction copyValue, freeFunction freeValue, printFunction printValue, equalFunction compValue)
{
	// Validate
	if(key == NULL || copyKey == NULL || freeKey == NULL || printKey == NULL || compKey == NULL) return NULL;
	if(value != NULL && (copyValue == NULL || freeValue == NULL || printValue == NULL || compValue == NULL)) return NULL;

	// Allocate
	KeyValuePair pair = (KeyValuePair)malloc(sizeof(struct keyValuePair_s));
	if(pair == NULL)
	{
		printf("no memory available");
		exit(1);
	}

	// Init
	pair->key = copyKey(key);
	pair->copyKey = copyKey;
	pair->freeKey = freeKey;
	pair->printKey = printKey;
	pair->compKey = compKey;

	if(value != NULL)
	{
		pair->value =  copyValue(value);
		pair->copyValue = copyValue;
		pair->freeValue = freeValue;
		pair->printValue = printValue;
		pair->compValue = compValue;
	}
	else
	{
		pair->value = NULL;
		pair->copyValue = NULL;
		pair->freeValue = NULL;
		pair->printValue = NULL;
		pair->compValue = NULL;
	}

	return pair;
}

/* destroy a given pair */
status destroyKeyValuePair(KeyValuePair pair)
{
	// Validate
	if(pair == NULL) return failure;

	pair->freeKey(pair->key);
	if(pair->value != NULL)
	{
		pair->freeValue(pair->value);
	}

	free(pair);

	return success;
}

/* prints the value */
status displayValue(KeyValuePair pair)
{
	// Validate
	if(pair == NULL || pair->value == NULL) return failure;

	pair->printValue(pair->value);

	return success;
}

/* prints the key */
status displayKey(KeyValuePair pair)
{
	// Validate
	if(pair == NULL) return failure;

	pair->printKey(pair->key);

	return success;
}

/* Getter for the value */
Element getValue(KeyValuePair pair)
{
	// Validate
	if(pair == NULL || pair->value == NULL) return NULL;

	return pair->value;
}

/* Getter for the key */
Element getKey(KeyValuePair pair)
{
	// Validate
	if(pair == NULL) return NULL;

	return pair->key;
}

/* check if two pairs has the same key */
bool isEqualKey(KeyValuePair pair, KeyValuePair otherPair)
{
	// Validate
	if(pair == NULL || otherPair == NULL) return false;
	if(pair->compKey != otherPair->compKey || pair->freeKey != otherPair->freeKey || pair->copyKey != otherPair->copyKey || pair->printKey != otherPair->printKey) return false; // Same Type Of Element

	return pair->compKey(pair->key, otherPair->key);
}

// ===========================================================================

// == Generic Nested ADT Functions ===========================================

/* copy function for nested generic ADT */
Element copyPair(Element element)
{
	// Validate
	if(element == NULL) return NULL;

	KeyValuePair pair = element;
	KeyValuePair copy = createKeyValuePair(pair->key, pair->copyKey, pair->freeKey, pair->printKey, pair->compKey, pair->value, pair->copyValue, pair->freeValue, pair->printValue, pair->compValue);

	return copy;
}

/* free function for nested generic ADT */
status freePair(Element element)
{
	return destroyKeyValuePair((KeyValuePair) element);
}

/* print function for nested generic ADT - Printing the VALUE */

/* print function for nested generic ADT - Printing the VALUE */
status printPair(Element element)
{
	return ((KeyValuePair)element)->printValue(getValue(element));
}


/* compare function for nested generic ADT - Comparing by KEY */
bool comparePair(Element pair, Element key)
{
	return ((KeyValuePair)pair)->compKey(((KeyValuePair)pair)->key, key);
}

// ===========================================================================

