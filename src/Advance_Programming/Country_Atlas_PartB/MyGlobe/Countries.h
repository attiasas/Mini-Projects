/* implemented by Assaf Attias */

#ifndef COUNTRIES_H_
#define COUNTRIES_H_
#include "Defs.h"

typedef struct Country_c* Country;
typedef struct City_c* City;

Country createCountry(char *countryName, int x1, int y1, int x2, int y2);		/* Create a new country */
City createCity (char *name, char *food, int numOfResidents);					/* Create a new city */
Country copyCountry(Country country);											/* Deep Copy a given country */
status addCityToCountry(Country country, City newCity);							/* Add a city to a country */
status deleteCityFromCountry(Country country, char *city);						/* Remove a city from the country */
bool isCoordinateInCountry(int x, int y, Country country);						/* Check if a given point is in a country territory */
void freeCity (City city);														/* Delete a given city from memory (Dynamic) */
void freeCountry(Country country);												/* Delete a given country from memory (Dynamic) */
status printCountry(Country country);											/* Print a given country */
char *createString(char *);														/* Create a new string */
bool isCityExist(Country country, char *nameOfCity); 							/* search for a city by a given country and name */
City copyCity(City);															/* Deep Copy a given city */
char *getCountryName(Country country); 											/* Getter for the name of a given country */

Element copyCountryADT (Element element);										/* copy function for generic ADT */
status freeCountryADT(Element element); 										/* free function for generic ADT */
status printCountryADT(Element element); 										/* print function for generic ADT*/


#endif /* COUNTRIES_H_ */
