/*
 * Employee.h
 *
 *  Created on: Dec 10, 2018
 *      Author: Assaf Attias
 */

#ifndef EMPLOYEE_H_
#define EMPLOYEE_H_

#include <string.h>
#include <iostream>
using namespace std;

class Employee
{
	private:
		char *name;
		int id;
		double salary;
		static int createCounter;

	public:
		Employee(); 									/* Default Constructor */
		Employee(char *name, int id, double salary); 	/* Constructor */
		Employee(const Employee& other); 				/* Copy Constructor */
		~Employee(); 									/* Destructor */

		int planOfficeParty(); 							/* Return the cost for the party: (Number_Of_Employees * 10) */

		bool operator==(const Employee& other)const; 					/* Equal Operator - By ID */
		bool operator>(const Employee& other)const; 					/* Bigger Operator - By Salary */
		Employee& operator=(const Employee& other); 					/* Assign the name of 'other' to 'this' name */
		Employee operator+(const Employee& other);		 				/* Add the salary of 'other' to 'this' salary */
		Employee& operator++(); 										/* Increase Operator - Increase salary by 100 */
		Employee operator++(int x); 									/* Increase Operator - Increase salary by 100 */
		Employee& operator+=(const Employee& other); 					/* Change the name of 'this' to the concatenation of 'this' and 'other' names */
		friend ostream& operator<<(ostream& out, const Employee& e); 	/* Print the information of Employee */

		int getId()const; 								/* Getter for Id */
		double getSalary()const; 						/* Getter for Salary */
};

#endif /* EMPLOYEE_H_ */
