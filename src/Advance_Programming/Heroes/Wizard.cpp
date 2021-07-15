/*
 * Wizard.cpp
 *
 *  Created on: Dec 28, 2018
 *      Author: Assaf Attias
 */

#include "Wizard.h"
using namespace std;

/* Constructor */
Wizard::Wizard()
{
	cost = 150;
	attack = 8;
	defense = 2;
}

/* Destructor */
Wizard::~Wizard() {}

/* attack the given creature */
bool Wizard::Attack(Creature& other){ return Creature::Attack(other); }

/* Print format: <Number Of Units> <type> - NO 'endl' */
bool Wizard::Print()
{
	Creature::Print();
	cout << "Wizard";
	return true;
}
