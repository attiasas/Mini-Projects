/*
 * Archer.h
 *
 *  Created on: Dec 28, 2018
 *      Author: Assaf Attias
 */

#ifndef ARCHER_H_
#define ARCHER_H_

#include "Creature.h"
#include "BlackDragon.h"

class Archer: public Creature
{
	public:
		Archer();								/* Constructor */
		virtual ~Archer() {}					/* Destructor */
		virtual bool Attack(Creature& other);	/* attack the given creature */
		virtual bool Print(); 					/* Print format: <Number Of Units> <type> - NO 'endl' */
};

#endif /* ARCHER_H_ */
