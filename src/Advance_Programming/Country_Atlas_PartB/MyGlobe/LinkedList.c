/* implemented by Assaf Attias */

#include "LinkedList.h"

// == Structure & Definition =================================================
struct node_s
{
	Element data;
	struct node_s *next;
	struct node_s *previous;
};

typedef struct node_s *Node;

struct linkedList_s
{
	int size;
	Node head;
	copyFunction element_copy;
	freeFunction element_free;
	printFunction element_print;
	equalFunction element_comp;
};
// ===========================================================================

// == STATIC Functions =======================================================

/* creates new Node */
static Node createNode(Element element)
{
	// Validate
	if(element == NULL) return NULL;

	// Allocate
	Node node = (Node)malloc(sizeof(struct node_s));
	if(node == NULL)
	{
		printf("no memory available");
		exit(1);
	}

	// Init
	node->data = element;
	node->next = NULL;
	node->previous = NULL;

	return node;
}

static status freeNode(LinkedList list, Node node)
{
	// Validate
	if(list == NULL || node == NULL) return failure;

	list->element_free(node->data);
	free(node);

	return success;
}

// ===========================================================================

// == PUBLIC Functions =======================================================

/* creates new empty linked list */
LinkedList createLinkedList(copyFunction copyFunc, freeFunction freeFunc, printFunction printFunc, equalFunction compFunc)
{
	// Validate
	if(copyFunc == NULL || freeFunc == NULL || printFunc == NULL || compFunc == NULL) return NULL;

	// Allocate
	LinkedList list = (LinkedList)malloc(sizeof(struct linkedList_s));
	if(list == NULL)
	{
		printf("no memory available");
		exit(1);
	}

	// Init
	list->size = 0;
	list->head = NULL;
	list->element_copy = copyFunc;
	list->element_free = freeFunc;
	list->element_print = printFunc;
	list->element_comp = compFunc;

	return list;
}

/* Destroys a given list */
status destroyList(LinkedList listToDestroy)
{
	// Validate
	if(listToDestroy == NULL) return failure;

	// Destroy Nodes
	Node node = listToDestroy->head;

	while(node != NULL)
	{
		listToDestroy->head = listToDestroy->head->next;
		freeNode(listToDestroy, node);
		node = listToDestroy->head;
	}

	free(listToDestroy);

	return success;
}

/* Adds a given element to a given list */
status appendNode(LinkedList list, Element element)
{
	// Validate
	if(list == NULL || element == NULL) return failure;

	Element copy = list->element_copy(element);
	if(copy == NULL) return failure;

	// Create Node
	Node node = createNode(copy);
	if(node == NULL)
	{
		list->element_free(copy);
		return failure;
	}

	// Append
	if(list->size == 0)
	{
		list->head = node;
	}
	else
	{
		node->next = list->head;
		list->head->previous = node;
		list->head = node;
	}

	list->size++;
	return success;
}

/* Remove a given element from the list */
status deleteNode(LinkedList list, Element element)
{
	// Validate
	if(list == NULL || element == NULL || list->size == 0 || searchInList(list, element) == NULL) return failure;

	// element exist -> Search
	Node search = list->head;
	while(search != NULL && list->element_comp(search->data, element) != true) search = search->next;

	if(search->previous != NULL) search->previous->next = search->next;
	if(search->next != NULL)
	{
		if(search->previous == NULL) list->head = search->next;
		search->next->previous = search->previous;
	}

	// Destroy
	freeNode(list ,search);
	list->size--;
	if(list->size == 0) list->head = NULL;

	return success;
}

/* prints the elements of the list */
status displayList(LinkedList list)
{
	// Validate
	if(list == NULL) return failure;

	for(Node node = list->head; node != NULL; node = node->next)
	{
		list->element_print(node->data);
	}

	return success;
}

/* search if a given element is in the list (NULL if not found) */
Element searchInList(LinkedList list, Element element)
{
	// Validate
	if(list == NULL || element == NULL) return NULL;

	// Search
	Element result = NULL;

	for(Node node = list->head; node != NULL && result == NULL; node = node->next)
	{
		if(list->element_comp(node->data, element) == true) result = node->data;
	}

	return  result;
}
// ===========================================================================

