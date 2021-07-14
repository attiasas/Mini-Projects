/*
 * Vampire.cpp
 *
 *  Created on: Dec 28, 2018
 *      Author: Assaf Attias
 */

#include "Vampire.h"
using namespace std;

/* Constructor */
Vampire::Vampire()
{
	cost = 80;
	attack = 4;
	defense = 4;
}

/* Destructor */
Vampire::~Vampire(){}

/* attack the given creature */
bool Vampire::Attack(Creature& other){ return Creature::Attack(other); }

/* Print format: <Number Of Units> <type> - NO 'endl' */
bool Vampire::Print()
{
	Creature::Print();
	cout << "Vampire";
	return true;
}
