/*
 * BlackDragon.cpp
 *
 *  Created on: Dec 28, 2018
 *      Author: Assaf Attias
 */

#include "BlackDragon.h"

using namespace std;

/* Constructor */
BlackDragon::BlackDragon():Creature()
{
	cost = 200;
	attack = 9;
	defense = 10;
}

/* attack the given creature */
bool BlackDragon::Attack(Creature& other)
{
	if(unitSize <= 0 || other.GetUnitSize() <= 0)
	{
		GameException e("Creature not found in heroes army");
		throw e;
	}

	int damage = attack * unitSize;
	int otherHp = other.GetDefense();

	Wizard* pW = dynamic_cast<Wizard*>(&other);
	if(pW != NULL) otherHp =(other.GetDefense() * 2);

	while(other.GetUnitSize() > 0 && damage >= otherHp)
	{
		other.ChangeUnits(-1);
		damage -= otherHp;
	}

	return true;
}

/* Print format: <Number Of Units> <type> - NO 'endl' */
bool BlackDragon::Print()
{
	Creature::Print();
	cout << "Black_Dragon";
	return true;
}
