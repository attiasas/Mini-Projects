/*
 * DataBase.cpp
 *
 *  Created on: Dec 10, 2018
 *      Author: Assaf Attias
 */

#include "DataBase.h"

/* Default Constructor */
DataBase::DataBase()
{
	data = new Employee*[3];
	maxSize = 3;
	size = 0;

	for(int i = 0; i < size; i++) data[i] = NULL;
}

/* Copy Constructor */
DataBase::DataBase(const DataBase& other)
{
	if(&other == NULL)
	{
		data = new Employee*[3];
		maxSize = 3;
		size = 0;

		for(int i = 0; i < size; i++) data[i] = NULL;
	}
	else
	{
		size = other.size;
		maxSize = other.maxSize;

		data = new Employee*[maxSize];

		for(int i = 0; i < maxSize; i++)
		{
			if(i < size) data[i] = new Employee(*other.data[i]);
			else data[i] = NULL;
		}
	}
}

/* Destructor */
DataBase::~DataBase()
{
	for(int i = 0; i < size; i++)
	{
		if(data[i] != NULL) delete data[i];
	}

	if(data != NULL) delete[] data;
}

/* Find Employee By his ID */
Employee *DataBase::getEmployee(int id)const
{
	Employee *search = NULL;

	for(int i = 0; i < size && search == NULL; i++)
	{
		if(data[i]->getId() == id) search = data[i];
	}

	return search;
}

/* Add A new Employee to the DB */
bool DataBase::addEmployee(Employee *toAdd)
{
	// Validate
	if(toAdd == NULL) return false;
	if(getEmployee(toAdd->getId()) != NULL)
	{
		// Exist - delete toAdd
		delete toAdd;
		toAdd = NULL;
		cout << "This Employee is already in the database" << endl << endl;
		return false;
	}

	// Check size
	if(size == maxSize) expand();

	// Add New Employee
	data[size] = toAdd;
	size++;

	return true;
}

/* Expand data size twice as large */
bool DataBase::expand()
{
	if(size != maxSize) return false;

	Employee **temp = new Employee*[maxSize * 2];
	if(temp == NULL) return false;

	for(int i = 0; i < maxSize * 2; i++)
	{
		if(i < maxSize) temp[i] = data[i];
		else temp[i] = NULL;
	}

	//delete[] data;
	data = temp;
	maxSize = maxSize * 2;

	return true;
}

/* Remove an Employee from the DB */
bool DataBase::removeEmployee(int id)
{
	// Validate
	if(getEmployee(id) == NULL)
	{
		cout << "This employee isn't in the database" << endl << endl;
		return false;
	}

	// Remove
	int index = getIndexById(id);
	delete data[index];
	data[index] = NULL;

	// Fix Space
	for(int i = index; i < size - 1; i++)
	{
		data[i] = data[i + 1];
		data[i + 1] = NULL;
	}

	size--;
	return true;
}

/* get the index in data of the employee by id, Not Exist: ( -1 is returned ). */
int DataBase::getIndexById(int id)
{
	int index = -1;

	for(int i = 0; i < size && index == -1; i++)
	{
		if(data[i]->getId() == id) index = i;
	}

	return index;
}

/* Print all employees in the DB */
bool DataBase::print()const
{
	cout << "Employees database:" << endl;

	for(int i = 0; i < size; i++)
	{
		 cout << endl << *data[i];
	}

	 cout << endl;
	return true;
}
