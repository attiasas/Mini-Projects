/*
 * Hero.h
 *
 *  Created on: Dec 28, 2018
 *      Author: Assaf Attias
 */

#ifndef HERO_H_
#define HERO_H_

#include <iostream>
#include <map>
#include <vector>
#include "Creature.h"
#include "GameException.h"
#include <algorithm>
#include <dirent.h>
#include <fstream>

class Hero
{
private:
	bool checkString(const std::string& s); 															/* check if all the string is alpha numeric */
	protected:
		std::string name;
		int gold;
		std::map<std::string,Creature*> army;

	public:
		Hero(const std::string& heroName);																/* Constructor */
		Hero(const std::string& heroName, std::ifstream& in); 											/* Constructor - Load from file */
		virtual ~Hero();																				/* Destructor */
		virtual bool SpecialAbility(Hero& other)=0; 													/* Use Special Ability  */
		bool BuyCreatures(std::string& creature);														/* Buy Creatures for army */
		bool AttackHero(Hero& other, const std::string& myCreature, const std::string& otherCreature); 	/* Attack a given hero's creature */
		bool HasArmy(); 																				/* check if the hero has creatures in his army */
		bool ChangeGold(int amount); 																	/* change the gold of the hero by a given amount */
		void Print(); 																					/* Print hero's details */
		void PrintArmy(); 																				/* Print Current Hero's Army  */
		std::string GetName();																			/* Get Hero Name */
		int GetGold();																					/* Get Hero Gold */
		void SaveData(const std::string& path);															/* Save Hero data in given file */
};

#endif /* HERO_H_ */
