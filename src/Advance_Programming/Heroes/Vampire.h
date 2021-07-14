/*
 * Vampire.h
 *
 *  Created on: Dec 28, 2018
 *      Author: Assaf Attias
 */

#ifndef VAMPIRE_H_
#define VAMPIRE_H_

#include "Creature.h"

class Vampire: public Creature
{
	public:
		Vampire();								/* Constructor */
		virtual ~Vampire();						/* Destructor */
		virtual bool Attack(Creature& other);	/* attack the given creature */
		virtual bool Print(); 					/* Print format: <Number Of Units> <type> - NO 'endl' */
};

#endif /* VAMPIRE_H_ */
