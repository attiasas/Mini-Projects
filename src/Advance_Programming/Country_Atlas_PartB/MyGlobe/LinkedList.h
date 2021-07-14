/* implemented by Assaf Attias */

#ifndef LINKED_LIST_H
#define LINKED_LIST_H

#include "Defs.h"

typedef struct linkedList_s *LinkedList;

LinkedList createLinkedList(copyFunction copyFunc, freeFunction freeFunc, printFunction printFunc, equalFunction compFunc); /* creates new empty linked list */
status destroyList(LinkedList listToDestroy); 																				/* Destroys a given list */
status appendNode(LinkedList list, Element element); 																		/* Adds a given element to a given list */
status deleteNode(LinkedList list, Element element); 																		/* Remove a given element from the list */
status displayList(LinkedList list); 																						/* prints the elements of the list */
Element searchInList(LinkedList list, Element element); 																	/* search if a given element is in the list (NULL if not found) */

#endif /* LINKED_LIST_H */
