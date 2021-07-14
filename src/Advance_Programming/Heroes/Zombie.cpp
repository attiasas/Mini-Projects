/*
 * Zomby.cpp
 *
 *  Created on: Dec 28, 2018
 *      Author: Assaf Attias
 */

#include "Zombie.h"
using namespace std;

/* Constructor */
Zombie::Zombie():Creature()
{
	cost = 50;
	attack = 2;
	defense = 5;
}

/* attack the given creature */
bool Zombie::Attack(Creature& other)
{
	if(unitSize <= 0 || other.GetUnitSize() <= 0)
	{
		GameException e("Creature not found in heroes army");
		throw e;
	}

	int damage = attack * unitSize;

	Archer* pA = dynamic_cast<Archer*>(&other);
	if(pA != NULL) damage = (attack * 2) * unitSize;

	while(other.GetUnitSize() > 0 && damage >= other.GetDefense())
	{
		other.ChangeUnits(-1);
		damage -= other.GetDefense();
	}

	return true;
}

/* Print format: <Number Of Units> <type> - NO 'endl' */
bool Zombie::Print()
{
	Creature::Print();
	cout << "Zombie";
	return true;
}
