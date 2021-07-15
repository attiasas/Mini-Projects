/*
 * BlackDragon.h
 *
 *  Created on: Dec 28, 2018
 *      Author: Assaf Attias
 */

#ifndef BLACKDRAGON_H_
#define BLACKDRAGON_H_

#include "Creature.h"
#include "Wizard.h"

class BlackDragon: public Creature
{
	public:
		BlackDragon();							/* Constructor */
		virtual ~BlackDragon() {}				/* Destructor */
		virtual bool Attack(Creature& other);	/* attack the given creature */
		virtual bool Print(); 					/* Print format: <Number Of Units> <type> - NO 'endl' */

};

#endif /* BLACKDRAGON_H_ */
