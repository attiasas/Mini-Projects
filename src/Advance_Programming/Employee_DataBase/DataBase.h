/*
 * DataBase.h
 *
 *  Created on: Dec 10, 2018
 *      Author: Assaf Attias
 */

#ifndef DATABASE_H_
#define DATABASE_H_

#include "Employee.h"
#include <stdlib.h>
#include <iostream>
using namespace std;

class DataBase
{
	private:
		Employee **data;
		int size;
		int maxSize;

		bool expand(); 						/* Expand data size twice as large */
		int getIndexById(int id); 			/* get the index in data of the employee by id, Not Exist: ( -1 is returned ). */

	public:
		DataBase(); 						/* Default Constructor */
		DataBase(const DataBase& other); 	/* Copy Constructor */
		~DataBase(); 						/* Destructor */

		Employee *getEmployee(int id)const; /* Find Employee By his ID */
		bool addEmployee(Employee *toAdd); 	/* Add A new Employee to the DB */
		bool removeEmployee(int id); 		/* Remove an Employee from the DB */
		bool print()const; 					/* Print all employees in the DB */
};

#endif /* DATABASE_H_ */
