/*
 * Hero.cpp
 *
 *  Created on: Dec 28, 2018
 *      Author: Assaf Attias
 */

#include "Hero.h"
#include "Warrior.h"
#include "Thief.h"

#include "BlackDragon.h"
#include "Wizard.h"
#include "Archer.h"
#include "Vampire.h"
#include "Zombie.h"

using namespace std;

Hero::Hero(const string& heroName)
{
	if(!checkString(heroName))
	{
		GameException e("A legal name can only contain letters and numbers");
		throw e;
	}

	name = heroName;
	gold = 750;

	army["Black_Dragon"] = new BlackDragon;
	army["Wizard"] = new Wizard;
	army["Archer"] = new Archer;
	army["Vampire"] = new Vampire;
	army["Zombie"] = new Zombie;
}

Hero::~Hero()
{
	while(army.size() > 0)
	{
		Creature* creature = army.begin()->second;
		army.erase(army.begin());
		delete creature;

	}
}

/* check if all the string is alpha numeric */
bool Hero::checkString(const std::string& s)
{
	for(int i = 0; i < (int)s.size(); i++)
	{
		if(s[i] < 48 || s[i] > 122) return false;
		if(s[i] > 57 && s[i] < 65) return false;
		if(s[i] > 90 && s[i] < 97) return false;
	}

	return true;
}

/* Buy Creatures for army */
bool Hero::BuyCreatures(string& creature)
{
	map<string,Creature*>::iterator toBuy = army.find(creature);
	if(toBuy == army.end())
	{
		GameException e("Creature '" + creature + "' not exists");
		throw e;
	}

	toBuy->second->PrintInfo();

	int amount;
	cin >> amount;

	int total = amount * (*army[creature]).GetCost();
	if(total > gold)
	{
		GameException e("You don't have enough gold");
		throw e;
	}

	bool result = (*army[creature]).ChangeUnits(amount);

	gold -= total;

	return result;
}

/* Attack a given hero's creature */
bool Hero::AttackHero(Hero& other, const std::string& myCreature, const std::string& otherCreature)
{
	if(army.find(myCreature) == army.end() || army.find(otherCreature) == army.end())
	{
		GameException e("Creature not exists");
		throw e;
	}
	if(army[myCreature]->GetUnitSize() == 0 || other.army[otherCreature]->GetUnitSize() == 0)
	{
		GameException e("Creature not found in heroes army");
		throw e;
	}

	army[myCreature]->Attack((*other.army[otherCreature]));

	return true;
}

/* check if the hero has creatures in his army */
bool Hero::HasArmy()
{
	int totalUnits = 0;

	for(map<std::string,Creature*>::iterator it = army.begin(); it != army.end(); ++it) totalUnits += (*it).second->GetUnitSize();

	return totalUnits != 0;
}

/* change the gold of the hero by a given amount */
bool Hero::ChangeGold(int amount)
{
	if(gold + amount <= 0) gold = 0;
	else if(gold + amount >= 2500) gold = 2500;
	else gold += amount;

	return true;
}

/* Get Hero Name */
string Hero::GetName()
{
	return name;
}

/* Get Hero Gold */
int Hero::GetGold()
{
	return gold;
}

/* Print hero's details */
void Hero::Print()
{
	cout << name << " ";

	Warrior* pW = dynamic_cast<Warrior*>(this);
	Thief* pT = dynamic_cast<Thief*>(this);

	if(pW != NULL) cout << "Warrior";
	else if(pT != NULL) cout << "Thief";
	else cout << "Necromancer";
}

/* Print Current Hero's Army  */
void Hero::PrintArmy()
{
	vector<Creature*> temp;
	for(map<string,Creature*>::iterator it = army.begin(); it != army.end(); ++it)
	{
		if(it->second->GetUnitSize() != 0)
		{
			temp.push_back(it->second);
		}
	}

	sort(temp.begin(), temp.end());

	for(int i = 0; i < (int)temp.size(); i++)
	{
		temp[i]->Print();
		if(i != (int)(temp.size() - 1)) cout << " ";
	}

	if(HasArmy()) cout << "." << endl;
}

/* Save Hero data in given file */
void Hero::SaveData(const std::string& path)
{
	// Make Dir
	system(("mkdir " + path + "/" + name).c_str());

	// Make File
	ofstream file;
	file.open((path + "/" + name + "/" + name + ".txt").c_str());

	Warrior* pW = dynamic_cast<Warrior*>(this);
	Thief* pT = dynamic_cast<Thief*>(this);

	if(pW != NULL) file << "Warrior ";
	else if(pT != NULL) file << "Thief ";
	else file << "Necromancer ";

	file << gold << " " << army["Black_Dragon"]->GetUnitSize() << " " << army["Wizard"]->GetUnitSize() << " ";
	file << army["Vampire"]->GetUnitSize() << " " << army["Archer"]->GetUnitSize() << " " << army["Zombie"]->GetUnitSize();

	file.close();
}

Hero::Hero(const string& heroName, ifstream& in)
{
	if(!checkString(heroName))
	{
		GameException e("A legal name can only contain letters and numbers");
		throw e;
	}

	name = heroName;

	army["Black_Dragon"] = new BlackDragon;
	army["Wizard"] = new Wizard;
	army["Archer"] = new Archer;
	army["Vampire"] = new Vampire;
	army["Zombie"] = new Zombie;

	// Read Data
	in >> gold;
	int units[5];

	for(int i = 0; i < 5; i++) in >> units[i];

	army["Black_Dragon"]->ChangeUnits(units[0]);
	army["Wizard"]->ChangeUnits(units[1]);
	army["Vampire"]->ChangeUnits(units[2]);
	army["Archer"]->ChangeUnits(units[3]);
	army["Zombie"]->ChangeUnits(units[4]);

}
