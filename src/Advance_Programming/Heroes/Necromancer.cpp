/*
 * Necromancer.cpp
 *
 *  Created on: Dec 29, 2018
 *      Author: Assaf Attias
 */

#include "Necromancer.h"
using namespace std;

/* Constructor */
Necromancer::Necromancer(const string& heroName):Hero(heroName){}

Necromancer::Necromancer(const string& heroName, ifstream& in):Hero(heroName, in) {}

/* Use Special Ability */
bool Necromancer::SpecialAbility(Hero& other)
{
	army["Zombie"]->ChangeUnits(1);
	cout << "Zombie added successfully" << endl;
	return true;
}
