/*
 * Archer.cpp
 *
 *  Created on: Dec 28, 2018
 *      Author: Assaf Attias
 */

#include "Archer.h"

using namespace std;

/* Constructor */
Archer::Archer():Creature()
{
	cost = 90;
	attack = 5;
	defense = 4;
}

/* attack the given creature */
bool Archer::Attack(Creature& other)
{
	if(unitSize <= 0 || other.GetUnitSize() <= 0)
	{
		GameException e("Creature not found in heroes army");
		throw e;
	}

	int damage = attack * unitSize;

	BlackDragon* pBD = dynamic_cast<BlackDragon*>(&other);
	if(pBD != NULL) damage = (attack + 1) * unitSize;

	while(other.GetUnitSize() > 0 && damage >= other.GetDefense())
	{
		other.ChangeUnits(-1);
		damage -= other.GetDefense();
	}

	return true;
}

/* Print format: <Number Of Units> <type> - NO 'endl' */
bool Archer::Print()
{
	Creature::Print();
	cout << "Archer";
	return true;
}
