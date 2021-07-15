/*
 * Main
 *
 *  Created on: Dec 28, 2018
 *      Author: Assaf Attias
 */

#include "Warrior.h"
#include "Thief.h"
#include "Necromancer.h"

#include <deque>
#include <algorithm>
#include <stdlib.h>
#include <fstream>
#include <dirent.h>
#include <string>

using namespace std;

// Global--------------------
static int round;
static bool gold;
static bool special;
static bool playing;
static bool loaded;

static map<string,Hero*> heroes;
static vector<string> order;
static deque<string> queue;
// --------------------------

// Game Functions -----------
int toInt(const string& s); 								/* turns string into int value (only 0-3, else will return -1)*/
bool LoadGame(int argc, char* argv[]); 						/* Load Game from data or Call CreateGame if new game */
bool CreateGame(int numWarriors,int numThief,int numNecro); /* Creates a new game */
void UpdateOrder(); 										/* Update order and remove dead heroes and folders from the game */
Hero* StartTurn(); 											/* starts a new turn and returns the current hero, if the round is over null will return */
void CloseGame(); 											/* Close Game - save it if its middle of a game or clean if the game is over */

void UseSpecial(Hero& hero); 								/* Use Special ability of current hero */
void Buy(Hero& hero); 										/* Buy Creatures to your army */
void Attack(Hero& hero); 									/* Current Hero Attack Menu */
void GetGold(Hero& hero); 									/* Get Daily Gold */
void Display(Hero& hero); 									/* Show Current Hero Details */
Hero* searchHero(const string& name); 						/* search for hero in */
// --------------------------

// Main Game
int main (int argc, char* argv[])
{
	// Init
	playing = LoadGame(argc, argv);
	Hero* current;

	// Game Loop
	while(playing)
	{
		// New Turn
		current = StartTurn();

		int choice = -1;

		while(choice != 6 && choice != 7)
		{
			string currentName = current->GetName();

			cout << "Welcome " << currentName << endl;
			cout << "What is your next step in the path to victory?" << endl;
			cout << "1. Attack" << endl;
			cout << "2. Get daily gold" << endl;
			cout << "3. Buy creatures" << endl;
			cout << "4. Show details" << endl;
			cout << "5. Special skill" << endl;
			cout << "6. End of my turn" << endl;
			cout << "7. Exit" << endl;

			cin >> choice;

			switch(choice)
			{
				case 1:	/* Attack */

						Attack(*current);
						try
						{
							searchHero(currentName);
							if(round >= 4 && !current->HasArmy()) choice = 6;
						}
						catch(GameException& e)
						{
							choice = 6;
						}

						if(heroes.size() <= 1)
						{
							// Game Over
							cout << heroes.begin()->second->GetName() << " is the winner!" << endl;
							choice = 7;
						}

						break;
				case 2:	/* Gold */
						try
						{
							GetGold(*current);
						}
						catch(GameException& e)
						{

						}
						break;
				case 3:	/* Buy */
						Buy(*current);
						break;
				case 4:	/* Print */
						Display(*current);
						break;
				case 5:	/* Special */
						try
						{
							UseSpecial(*current);
						}
						catch(GameException& e)
						{

						}
						break;
				case 7: /* Exit */
						queue.push_front(current->GetName());

			}
		}

		// Exit
		if(choice == 7) playing = false;

	}

	CloseGame();

	return 0;
}

/* Current Hero Attack Menu */
void Attack(Hero& hero)
{
	int choice = 0;

	// Menu
	while(choice != 2 && choice != 1)
	{
		cout << "1. Show me my opponents" << endl;
		cout << "2. Attack hero" << endl;

		cin >> choice;
	}

	if(choice == 1)
	{
		vector<Hero*> opponents;
		for(map<string,Hero*>::iterator it = heroes.begin(); it != heroes.end(); ++it)
		{
			Warrior* pWarrior = dynamic_cast<Warrior*>(it->second);
			if(it->second->GetName() != hero.GetName() && pWarrior != NULL) opponents.push_back(it->second);
		}
		for(map<string,Hero*>::iterator it = heroes.begin(); it != heroes.end(); ++it)
		{
			Thief* pThief = dynamic_cast<Thief*>(it->second);
			if(it->second->GetName() != hero.GetName() && pThief != NULL) opponents.push_back(it->second);
		}
		for(map<string,Hero*>::iterator it = heroes.begin(); it != heroes.end(); ++it)
		{
			Necromancer* pNecro = dynamic_cast<Necromancer*>(it->second);
			if(it->second->GetName() != hero.GetName() && pNecro != NULL) opponents.push_back(it->second);
		}

		for(int i = 0; i < (int)opponents.size(); i++)
		{
			opponents[i]->Print();
			if(i != (int)opponents.size() - 1) cout << endl;
		}

		cin.ignore();
		while(cin.get() != '\n');
		return;
	}

	if(round < 4)
	{
		cout << "Attack is only possible after 3 rounds" << endl;
		return;
	}

	// Get Opponent
	string opponentName = "";
	Hero* opponent = NULL;
	bool found = false;

	while(!found)
	{
		cout << "Please insert your opponent name:";
		cin >> opponentName;

		try
		{
			opponent = searchHero(opponentName);
			found = true;
		}
		catch(GameException& e)
		{
			e.Error();
		}
	}

	// Attack
	bool turn = true;

	while(hero.HasArmy() && opponent->HasArmy())
	{
		try
		{
			if(turn)
			{
				hero.Print();
				cout << ":" << endl;
				hero.PrintArmy();

				opponent->Print();
				cout << ":" << endl;
				opponent->PrintArmy();

				cout << hero.GetName() << "'s turn:" << endl;

			}
			else
			{
				opponent->Print();
				cout << ":" << endl;
				opponent->PrintArmy();

				hero.Print();
				cout << ":" << endl;
				hero.PrintArmy();

				cout << opponent->GetName() << "'s turn:" << endl;
			}


			string myCreature;
			string otherCreature;

			if(turn)
			{
				cin >> myCreature >> otherCreature;
				hero.AttackHero(*opponent, myCreature, otherCreature);

				turn = false;
			}
			else
			{
				cin >> otherCreature >> myCreature;
				opponent->AttackHero(hero, otherCreature, myCreature);

				turn = true;
			}

		}
		catch(GameException& e)
		{
			e.Error();
		}
	}

	if(!hero.HasArmy())
	{
		hero.ChangeGold(opponent->GetGold());
		cout << "You have been perished" << endl;
	}
	else
	{
		opponent->ChangeGold(hero.GetGold());
		cout << "You have been victorious" << endl;
	}

	UpdateOrder();
}

/* Buy Creatures to your army */
void Buy(Hero& hero)
{
	cout << "1. Buy Zombies." << endl;
	cout << "2. Buy Archers." << endl;
	cout << "3. Buy Vampire." << endl;
	cout << "4. Buy Wizard." << endl;
	cout << "5. Buy Black Dragon." << endl;

	try
	{
		int choice = 0;
		cin >> choice;
		string creature = "";

		switch(choice)
		{
			case 1: creature = "Zombie";
					break;
			case 2:	creature = "Archer";
					break;
			case 3:	creature = "Vampire";
					break;
			case 4:	creature = "Wizard";
					break;
			case 5:	creature = "Black_Dragon";
					break;
		}

		hero.BuyCreatures(creature);
	}
	catch(GameException& e)
	{
		e.Error();
	}

}

/* Use Special ability of current hero */
void UseSpecial(Hero& hero)
{
	if(special)
	{
		GameException e("you can only use your special ability once in each turn");
		throw e;
	}

	Thief* pT = dynamic_cast<Thief*>(&hero);

	if(pT != NULL)
	{
		cout << "Please insert hero name:" << endl;
		string input;
		Hero* other = NULL;

		while(other == NULL)
		{
			cin >> input;
			try
			{
				other = searchHero(input);
			}
			catch(GameException& e)
			{
				e.Error();
				return;
			}
		}

		hero.SpecialAbility(*other);

	}
	else
	{
		hero.SpecialAbility(hero);
	}

	special = true;
}

/* Get Daily Gold */
void GetGold(Hero& hero)
{
	if(gold)
	{
		GameException e("Daily gold has been taken once");
		throw e;
	}

	hero.ChangeGold(100);
	gold = true;
}

/* Show Current Hero Details */
void Display(Hero& hero)
{
	hero.Print();
	cout << ":" << endl;
	cout << hero.GetGold() << " gold" << endl;
	hero.PrintArmy();
}

/* starts a new turn and returns the current hero, if the round is over null will return */
Hero* StartTurn()
{
	// Check end of round
	map<string,Hero*>::iterator search;
	while(queue.size() != 0 && (search = heroes.find(queue[0])) == heroes.end()) queue.pop_front();

	if((int)queue.size() == 0)
	{
		round++;

		if(round == 4) UpdateOrder();

		for(int i = 0; i < (int)order.size(); i++) queue.push_back(order[i]);
	}

	Hero* result = heroes[queue[0]];
	queue.pop_front();

	if(!loaded)
	{
		gold = false;
		special = false;
	}
	else loaded = false;

	return result;
}

/* Update order and remove dead heroes and folders from the game */
void UpdateOrder()
{
	for(int i = 0; i < (int)order.size(); i++)
	{
		Hero* hero = heroes[order[i]];
		if(!hero->HasArmy())
		{
			delete hero;
			heroes.erase(order[i]);
			order.erase(order.begin() + i);
			i--;
		}
	}
}

/* search for hero in */
Hero* searchHero(const string& name)
{
	map<string,Hero*>::iterator search = heroes.find(name);

	if(search == heroes.end())
	{
		GameException e("Hero name not found");
		throw e;
	}

	Hero* result = search->second;

	return result;
}

/* turns string into int value (only 0-3, else will return -1)*/
int toInt(const string& s)
{
	if(s == "0") return 0;
	else if(s == "1") return 1;
	else if(s == "2") return 2;
	else if(s == "3") return 3;

	return -1;
}

/* Load Game from data or Call CreateGame if new game */
bool LoadGame(int argc, char* argv[])
{
	bool result = false;

	//Check First Time
	DIR* heroesDir = opendir("Players");
	if(heroesDir == NULL) system("mkdir Players");
	closedir(heroesDir);
	DIR* gameDir = opendir("Game");
	if(gameDir == NULL) system("mkdir Game");
	closedir(gameDir);

	if(argc == 2) // Continue
	{
		// Load - Heroes
		DIR* dir = opendir("Players");
		struct dirent* ent;
		vector<string> names;

		while((ent = readdir(dir)) != NULL)
		{
			string heroName;
			heroName = ent->d_name;
			if(heroName != "." && heroName != "..") names.push_back(heroName);
		}
		closedir(dir);

		for(int i = 0; i < (int)names.size(); i++)
		{
			ifstream in;
			in.open(("Players/" + names[i] + "/" + names[i] + ".txt").c_str());
			string type;
			in >> type;
			if(type == "Warrior") heroes[names[i]] = new Warrior(names[i], in);
			else if(type == "Thief") heroes[names[i]] = new Thief(names[i], in);
			else heroes[names[i]] = new Necromancer(names[i], in);
			in.close();
			system(("rm -r Players/" + names[i]).c_str());
		}

		// Load - Game
		ifstream gameFile;
		gameFile.open("Game/gameData.txt");
		gameFile >> round >> gold >> special;
		string search;
		gameFile >> search;
		while(search != ".")
		{
			order.push_back(search);
			gameFile >> search;
		}
		gameFile >> search;
		while(search != ".")
		{
			queue.push_back(search);
			gameFile >> search;
		}

		gameFile.close();
		system("rm Game/gameData.txt");

		result = true;
		loaded = true;
	}
	else if(argc == 5)
	{
		// New Game
		loaded = false;
		try
		{
			result = CreateGame(toInt(argv[2]),toInt(argv[3]),toInt(argv[4]));
		}
		catch(GameException& e)
		{
			e.Error();
			result = false;
		}

	}

	return result;
}

/* Creates a new game */
bool CreateGame(int numWarriors,int numThief,int numNecro)
{
	// Validate
	if(numWarriors < 0 || numThief < 0 || numNecro < 0)
	{
		GameException e("each Hero type can only be chosen 0-3 times");
		throw e;
	}

	bool result = true;
	int totalPlayers = numWarriors + numThief + numNecro;
	int numInit = 0;
	vector<string> holder;

	//Init
	round = 0;

	// Get Players
	while(numInit < totalPlayers)
	{
		try
		{
			string name;
			cout << "please insert ";

			if(numInit < numWarriors)
			{
				// New Warrior
				cout << "warrior number " << (numInit + 1) << " name:" << endl;
				cin >> name;
				Warrior* warrior = new Warrior(name);
				holder.push_back(name);
				heroes[name] = warrior;
			}
			else if(numInit - numWarriors < numThief)
			{
				// New Thief
				cout << "thief number " << (numInit - numWarriors + 1) << " name:" << endl;
				cin >> name;
				Thief* thief = new Thief(name);
				holder.push_back(name);
				heroes[name] = thief;
			}
			else
			{
				// New Necromancer
				cout << "necromancer number " << (numInit - numWarriors - numThief + 1) << " name:" << endl;
				cin >> name;
				Necromancer* necromancer = new Necromancer(name);
				holder.push_back(name);
				heroes[name] = necromancer;
			}

			numInit++;
		}
		catch(GameException& e)
		{
			e.Error();
		}

	}

	// Decide Turn Order and load first round
	srand(time(NULL));
	while(holder.size() > 0)
	{
		int random = rand() % holder.size();

		order.push_back(holder[random]);
		holder.erase(holder.begin() + random);
	}

	return result;
}

/* Close Game - save it if its middle of a game or clean if the game is over */
void CloseGame()
{
	if(heroes.size() == 1)
	{
		// END
		Hero* hero = heroes.begin()->second;
		delete hero;
		heroes.erase(heroes.begin());
	}
	else
	{
		// SAVE - GAME
		ofstream gameFile;
		gameFile.open("Game/gameData.txt");
		gameFile << round << " " << gold << " " << special << " ";
		for(int i = 0; i < (int)order.size(); i++) gameFile << order[i] << " ";
		gameFile << ". ";
		for(int i = 0; i < (int)queue.size(); i++) gameFile << queue[i] << " ";
		gameFile << ".";
		gameFile.close();

		// SAVE - HEROS
		while(heroes.size() > 0)
		{
			Hero* hero = heroes.begin()->second;
			hero->SaveData("Players");
			delete hero;
			heroes.erase(heroes.begin());
		}
	}


}

