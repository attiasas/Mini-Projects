/*
 * Thief.h
 *
 *  Created on: Dec 29, 2018
 *      Author: Assaf Attias
 */

#ifndef THIEF_H_
#define THIEF_H_

#include "Hero.h"

class Thief: public Hero
{
	public:
		Thief(const std::string& heroName);						/* Constructor */
		Thief(const std::string& heroName, std::ifstream& in); 	/* Constructor - Load from file */
		virtual ~Thief() {}										/* Destructor */
		virtual bool SpecialAbility(Hero& other); 				/* Use Special Ability  */
};

#endif /* THIEF_H_ */
