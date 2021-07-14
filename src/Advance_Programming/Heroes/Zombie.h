/*
 * Zombie.h
 *
 *  Created on: Dec 28, 2018
 *      Author: Assaf Attias
 */

#ifndef ZOMBIE_H_
#define ZOMBIE_H_

#include "Creature.h"
#include "Archer.h"

class Zombie: public Creature
{
	public:
		Zombie();								/* Constructor */
		virtual ~Zombie() {}					/* Destructor */
		virtual bool Attack(Creature& other);	/* attack the given creature */
		virtual bool Print(); 					/* Print format: <Number Of Units> <type> - NO 'endl' */
};

#endif /* ZOMBIE_H_ */
