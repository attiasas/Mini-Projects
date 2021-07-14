/*
 * GameException.h
 *
 *  Created on: Jan 3, 2019
 *      Author: Assaf Attias
 */

#ifndef GAMEEXCEPTION_H_
#define GAMEEXCEPTION_H_

#include <string>
#include <iostream>

class GameException: public std::exception
{
	private:
		std::string msg;
	public:
		GameException(std::string error);
		virtual ~GameException() throw() {};
		void Error();
};

#endif /* GAMEEXCEPTION_H_ */
