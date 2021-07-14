/*
 * Thief.cpp
 *
 *  Created on: Dec 29, 2018
 *      Author: Assaf Attias
 */

#include "Thief.h"
#include "Warrior.h"

using namespace std;

/* Constructor */
Thief::Thief(const string& heroName):Hero(heroName){}

Thief::Thief(const string& heroName, ifstream& in):Hero(heroName, in){}

/* Use Special Ability */
bool Thief::SpecialAbility(Hero& other)
{
	if(gold >= 2500) return false;
	int total = other.GetGold();

	if(total < 70 && (gold + total) <= 2500)
	{
		other.ChangeGold(-total);
		ChangeGold(total);
	}
	else if(total >= 70 && (gold + 70 <= 2500))
	{
		other.ChangeGold(-70);
		ChangeGold(70);
	}
	else
	{
		other.ChangeGold(-(2500 - gold));
		ChangeGold(2500 - gold);
	}

	return true;
}
