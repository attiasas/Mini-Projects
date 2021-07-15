/* implemented by Assaf Attias */

#ifndef COUNTRIES_H_
#define COUNTRIES_H_
#include <stdlib.h>
#include <string.h>
#include <stdio.h>

typedef struct Country_c* Country;
typedef struct City_c* City;

typedef enum e_bool {false,true} bool;
typedef enum e_status {failure,success} status;

Country createCountry(char*, int, int, int, int);	/* Create a new country */
City createCity (char *, char *, int);
Country copyCountry(Country);	/* Deep Copy a given country */

status addCityToCountry(Country, City);	/* Add a city to a country */
status deleteCityFromCountry(Country, char *);	/* Remove a city from the country */
bool isCoordinateInCountry(int, int, Country);	/* Check if a given point is in a country territory */
void freeCity (City);	/* Delete a given city from memory (Dynamic) */
void freeCountry(Country);	/* Delete a given country from memory (Dynamic) */

status printCountry(Country);	/* Print a given country */
bool isCityExist(Country *, int, char *);	/* check if a city (by name) exist in a given country (provide list and index to check) */
int searchCountry(Country *, char *, int);	/* check if a country is in a given list by name */
char *getCountryName(Country); /* Getter for the name of a given country */

#endif /* COUNTRIES_H_ */
