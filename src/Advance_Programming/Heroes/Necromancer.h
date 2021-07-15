/*
 * Necromancer.h
 *
 *  Created on: Dec 29, 2018
 *      Author: Assaf Attias
 */

#ifndef NECROMANCER_H_
#define NECROMANCER_H_

#include "Hero.h"

class Necromancer: public Hero
{
	public:
		Necromancer(const std::string& heroName);						/* Constructor */
		Necromancer(const std::string& heroName, std::ifstream& in); 	/* Constructor - Load from file */
		virtual ~Necromancer() {}										/* Destructor */
		virtual bool SpecialAbility(Hero& other); 						/* Use Special Ability  */
};

#endif /* NECROMANCER_H_ */
