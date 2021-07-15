/*
 * Wizard.h
 *
 *  Created on: Dec 28, 2018
 *      Author: Assaf Attias
 */

#ifndef WIZARD_H_
#define WIZARD_H_

#include "Creature.h"

class Wizard: public Creature
{
	public:
		Wizard();								/* Constructor */
		virtual ~Wizard();						/* Destructor */
		virtual bool Attack(Creature& other);	/* attack the given creature */
		virtual bool Print(); 					/* Print format: <Number Of Units> <type> - NO 'endl' */
};

#endif /* WIZARD_H_ */
