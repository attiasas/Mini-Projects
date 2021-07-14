/*
 * Creature.cpp
 *
 *  Created on: Dec 28, 2018
 *      Author: Assaf Attias
 */

#include "Creature.h"
using namespace std;

/* Constructor */
Creature::Creature()
{
	unitSize = 0;

	attack = 0;
	defense = 0;
	cost = 0;
}

/* attack the given creature */
bool Creature::Attack(Creature& other)
{
	if(unitSize <= 0 || other.unitSize <= 0)
	{
		GameException e("Creature not found in heroes army");
		throw e;
	}

	int damage = attack * unitSize;

	while(other.unitSize > 0 && damage >= other.defense)
	{
		other.unitSize--;
		damage -= other.defense;
	}

	return true;
}

/* Print format: <Attack> <Defense> */
bool Creature::PrintInfo()
{
	cout << "Attack level: " << attack << ", Defense level: " << defense << endl;
	return true;
}

/* Print format: <Number Of Units> <type> - NO 'endl' */
bool Creature::Print()
{
	cout << unitSize << " ";
	return true;
}

/* return the cost of a single unit */
int Creature::GetCost() { return cost; }

/* Get how many units of the creature */
int Creature::GetUnitSize() { return unitSize; }

/* Get the Creature Defense */
int Creature::GetDefense() { return defense; }

/* Add more Units */
bool Creature::ChangeUnits(int amount)
{
	unitSize += amount;
	if(unitSize <= 0) unitSize = 0;

	return true;
}

bool Creature::operator>(const Creature& other)const
{
	return cost > other.cost;
}
