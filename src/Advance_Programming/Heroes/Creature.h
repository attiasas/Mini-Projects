/*
 * Creature.h
 *
 *  Created on: Dec 28, 2018
 *      Author: Assaf Attias
 */

#ifndef CREATURE_H_
#define CREATURE_H_

#include <iostream>
#include "GameException.h"

class Creature
{
protected:
	int attack;
	int defense;
	int cost;
	int unitSize;
public:
	Creature(); 							/* Constructor */
	virtual ~Creature() {} 					/* Destructor */
	virtual bool Attack(Creature& other)=0; /* attack the given creature */
	virtual bool Print(); 					/* Print format: <Number Of Units> <type> - NO 'endl' */
	virtual bool PrintInfo(); 				/* Print format: <Attack> <Defense> */

	int GetCost(); 							/* return the cost of a single unit */
	int GetUnitSize(); 						/* Get how many units of the creature */
	int GetDefense();						/* Get the Creature Defense */
	bool ChangeUnits(int amount); 			/* Add more Units */
	bool operator>(const Creature& other)const;
};

#endif /* CREATURE_H_ */
