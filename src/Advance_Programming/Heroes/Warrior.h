/*
 * Warrior.h
 *
 *  Created on: Dec 29, 2018
 *      Author: Assaf Attias
 */

#ifndef WARRIOR_H_
#define WARRIOR_H_

#include "Hero.h"

class Warrior: public Hero
{
	public:
		Warrior(const std::string& heroName);		/* Constructor */
		Warrior(const std::string& heroName, std::ifstream& in); 	/* Constructor - Load from file */
		virtual ~Warrior() {}						/* Destructor */
		virtual bool SpecialAbility(Hero& other); 	/* Use Special Ability  */
};

#endif /* WARRIOR_H_ */
