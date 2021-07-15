/*
 * Employee.cpp
 *
 *  Created on: Dec 10, 2018
 *      Author: Assaf Attias
 */

#include "Employee.h"

int Employee::createCounter = 0;

/* Default Constructor */
Employee::Employee()
{
	name = new char[strlen("Name") + 1];
	if(name != NULL) strcpy(name, "Name");

	id = 0;
	salary = 0;

	createCounter++;
}

/* Constructor */
Employee::Employee(char *name, int id, double salary)
{
	if(name == NULL)
	{
		this->name = new char[strlen("Name") + 1];
		if(this->name != NULL) strcpy(this->name, "Name");
	}
	else
	{
		this->name = new char[strlen(name) + 1];
		if(this->name != NULL) strcpy(this->name, name);
	}

	if(salary < 0) this->salary = 0;
	else this->salary = salary;

	this->id = id;

	createCounter++;
}

/* Copy Constructor */
Employee::Employee(const Employee& other)
{
	if(&other == NULL)
	{
		name = new char[strlen("Name") + 1];
		if(name != NULL) strcpy(name, "Name");
		id = 0;
		salary = 0;
	}
	else
	{
		name = new char[strlen(other.name) + 1];
		if(name != NULL) strcpy(name, other.name);

		id = other.id;
		salary = other.salary;
	}

	createCounter++;
}

/* Destructor */
Employee::~Employee()
{
	if(name != NULL) delete[] name;
	name = NULL;

	createCounter--;
}

/* Return the cost for the party: (Number_Of_Employees * 10) */
int Employee::planOfficeParty()
{
	return (createCounter * 10);
}

/* Equal Operator - By ID */
bool Employee::operator==(const Employee& other)const
{
	return id == other.id;
}

/* Bigger Operator - By Salary */
bool Employee::operator>(const Employee& other)const
{
	return salary > other.salary;
}

/* Assign the name of 'other' to 'this' name */
Employee& Employee::operator=(const Employee& other)
{
	char *temp = new char[strlen(other.name) + 1];
	if(temp == NULL) return *this;

	strcpy(temp, other.name);
	if(name != NULL) delete[] name;
	name = temp;

	return *this;
}

/* Add the salary of 'other' to 'this' salary */
Employee Employee::operator+(const Employee& other)
{
	salary += other.salary;
	return *this;
}

/* Increase Operator - Increase salary by 100 */
Employee& Employee::operator++()
{
	salary += 100;
	return *this;
}

/* Increase Operator - Increase salary by 100 */
Employee Employee::operator++(int x)
{
	Employee temp(*this);
	salary += 100;
	return temp;
}

/* Change the name of 'this' to the concatenation of 'this' and 'other' names */
Employee& Employee::operator+=(const Employee& other)
{
	// validate
	if(name == NULL || other.name == NULL) return *this;

	char *temp = new char[strlen(name) + 1 + strlen(other.name)];
	if(temp == NULL) return  *this;
	else
	{
		char *holder = temp + strlen(name);
		strcpy(temp, name);
		strcpy(holder, other.name);

		delete[] name;
		name = temp;
	}

	return *this;
}

/* Print the information of Employee */
ostream& operator<<(ostream& out, const Employee& e)
{
	out << "Name:" << e.name << endl << "ID:" << e.id << endl << "Salary:" << e.salary << endl;
	return out;
}

/* Getter for ID */
int Employee::getId()const { return id; }

/* Getter for Salary */
double Employee::getSalary()const { return salary; }
