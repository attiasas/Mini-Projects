/*
 * GameException.cpp
 *
 *  Created on: Jan 3, 2019
 *      Author: Assaf Attias
 */

#include "GameException.h"
using namespace std;

GameException::GameException(string error)
{
	msg = error;
}

void GameException::Error()
{
	cout << msg << endl;
}
