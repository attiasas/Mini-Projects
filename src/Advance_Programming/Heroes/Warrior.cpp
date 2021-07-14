/*
 * Warrior.cpp
 *
 *  Created on: Dec 29, 2018
 *      Author: Assaf Attias
 */

#include "Warrior.h"

using namespace std;

/* Constructor */
Warrior::Warrior(const string& heroName):Hero(heroName){}

Warrior::Warrior(const string& heroName, ifstream& in):Hero(heroName, in) {}

/* Use Special Ability */
bool Warrior::SpecialAbility(Hero& other)
{
	cout << "Gold added successfully" << endl;
	return ChangeGold(50);
}
