/* implemented by Assaf Attias */

#include "Countries.h"

// == Structure & Definition =================================================

#define buffer 300

struct City_c
{
	char *name;
	char *food;
	int numOfResidents;
	struct City_c *nextCity;
} c_City;

struct Country_c
{
	char *name;
	City cities;
	int left,top,right,bottom;
} c_Country;

// ==================================================================================

// == PRIVATE Functions ==============================================================

char *createString(char *);
City copyCity(City);


/* Create a new string */
char *createString(char *str)
{
	char *res = (char *)malloc(buffer);
	if(res == NULL)
	{
		printf("no memory available");
		exit(1);
	}
	strcpy(res, str);
	return res;
}

/* Deep Copy a given city */
City copyCity(City city)
{
	// Validate Input
	if(city == NULL) return NULL;

	return createCity(city->name,city->food,city->numOfResidents);
}

// ==================================================================================

/* Create a new country */
Country createCountry(char *countryName, int x1, int y1, int x2, int y2)
{
	//Validate
	if(countryName == NULL)
		return NULL;
	Country res = (Country)malloc(sizeof(c_Country));//malloc for the pointer to country
	if(res == NULL)
	{
		printf("no memory available");
		exit(1);
	}

	res->name = createString(countryName);
	res->left = x1;
	res->top = y1;
	res->right = x2;
	res->bottom = y2;
	res->cities = NULL;

	return res;
}

/* Create a new city */
City createCity (char *name, char *food, int numOfResidents)
{
	if(name == NULL || food == NULL || numOfResidents<0)
		return NULL;
	City res = (City)malloc(sizeof(c_City));
	if(res == NULL)
	{
		printf("no memory available");
		exit(1);
	}

	res->name = createString(name);
	res->food = createString(food);
	res->numOfResidents = numOfResidents;
	res->nextCity = NULL;
	return res;
}

/* Add a city to a country */
status addCityToCountry(Country country, City newCity)
{
	if(country == NULL || newCity == NULL)
		return failure;
	//copy the city
	City cityCopy = copyCity(newCity);
	if(cityCopy == NULL)
		return failure;
	//Add the city
	if(country->cities == NULL)
	{
		country->cities = cityCopy;
	}
	else
	{
		City tempCity = country->cities;
		while(tempCity->nextCity != NULL)
		{
			tempCity = tempCity->nextCity;
		}
		tempCity->nextCity = cityCopy;
	}
	return success;
}

/* Remove a city from the country */
status deleteCityFromCountry(Country country, char *city)
{
	if(country == NULL || city == NULL)
	{
		return failure;
	}

	City prevCity = NULL;
	City tempCity = country->cities;
	while(tempCity !=NULL && strcmp(tempCity->name, city) != 0)
	{
		prevCity = tempCity;
		tempCity = tempCity->nextCity;
	}

	if(tempCity == NULL)
	{//no city to delete
		return failure;
	}
	else if(prevCity == NULL)
	{//tempCity != NULL
		country->cities = tempCity->nextCity;
	}
	else
	{
		prevCity->nextCity = tempCity->nextCity;
	}
	//prevCity != NULL and tempCity != NULL
	freeCity(tempCity);
	return success;
}

/* Check if a given point is in a country territory */
bool isCoordinateInCountry(int x, int y, Country country)
{
	if(country == NULL)
	{
		return false;
	}

	if(x>=country->left && x<=country->right && y>=country->bottom && y<=country->top)
	{
		return true;
	}
	return false;
}

/* Delete a given city from memory (Dynamic) */
void freeCity (City city)
{
	free(city->name);
	free(city->food);
	free(city);
}

/* Delete a given country from memory (Dynamic) */
void freeCountry(Country country)
{
	//free all the cities of the country
	City toFree = country->cities;
	City tempCity = NULL;
	while (toFree != NULL)
	{
		tempCity = toFree->nextCity;
		freeCity(toFree);
		toFree = tempCity;
	}
	free(country->name);
	free(country);
}

/* Deep Copy a given country */
Country copyCountry(Country country)
{
	Country res = createCountry(country->name, country->left, country->top, country->right, country->bottom);

	//copy the cities
	City currCityOrigin = country->cities;
	City currCityDest = copyCity(currCityOrigin);
	res->cities = currCityDest;
	while(currCityOrigin != NULL)
	{
		currCityDest->nextCity = copyCity(currCityOrigin->nextCity);

		currCityDest = currCityDest->nextCity;
		currCityOrigin = currCityOrigin->nextCity;
	}
	return res;
}

/* Print a given country */
status printCountry(Country country)
{
	if(country == NULL)
	{
		return failure;
	}
	if(printf("Country %s coordinates: <%d,%d> , <%d,%d>\n", country->name, country->left, country->top, country->right, country->bottom)<0)
	{
		return failure;
	}
	City currCity = country->cities;
	while(currCity != NULL)
	{
		if(printf("\t%s includes %d residents and their favorite food is %s.\n", currCity->name, currCity->numOfResidents, currCity->food)<0)
		{
			return failure;
		}
		currCity = currCity->nextCity;
	}
	return success;
}

/* check if a city (by name) exist in a given country (provide list and index to check) */
bool isCityExist(Country *list, int countryNum, char *cityName)
{
	City curr = list[countryNum]->cities;
	while(curr!=NULL)
	{
		if(strcmp(curr->name,cityName)==0)
		{
			return true;
		}
		curr = curr->nextCity;
	}
	return false;
}

/* check if a country is in a given list by name */
int searchCountry(Country *list, char *countryName, int numOfCountries)
{
	for(int i=0; i<numOfCountries; i++)
	{
		if(strcmp(list[i]->name, countryName)==0)
		{
			return i;
		}
	}
	return -1;
}

/* Getter for the name of a given country */
char *getCountryName(Country country)
{
	return country->name;
}
