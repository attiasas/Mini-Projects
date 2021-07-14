/* implemented by Assaf Attias */

#include "Defs.h"
#include "HashTable.h"
#include "Countries.h"

#define buffer 300

// Function Declare =============================
status readCountries(hashTable table, int numOfCountries, char *fileAddress); 	/* read country data from a given data path */
status AddCountry(hashTable table); 											/* Add new country */
status AddCity(hashTable table);												/* add a city to a country */
status DeleteCity(hashTable table); 											/* delete a city to a country */
status SearchName(hashTable table);												/* Print country by name */
status DeleteCountry(hashTable table); 											/* delete a country */
status SearchArea(hashTable table);			 									/* search country by coordinates */
// ==============================================

// Key (String/Char *) Function Declare =========
Element CopyString (Element string); 						/* Copy String - For ADT Hash Table */
status printString (Element string); 						/* Print String - For ADT Hash Table */
status freeString (Element string); 						/* Free String - For ADT Hash Table */
int TransformString (Element string); 						/* Transform String to int (for key) - For ADT Hash Table */
bool CompareString (Element string, Element otherString); 	/* Compare Strings - For ADT Hash Table */
// ==============================================

/* Main Menu function */
int main (int argc, char* argv[])
{
	//Get the configuration parameters
	int hashNumber = atoi(argv[1]);
	int numOfCountries = atoi(argv[2]);
	char *configurationFile = argv[3];

	/*  Value - Country
		Key - String     */
	hashTable countries = createHashTable(CopyString, freeString, printString, copyCountryADT, freeCountryADT, printCountryADT, CompareString, TransformString, hashNumber);

	//Read from file
	readCountries(countries, numOfCountries, configurationFile);


	int choice = -1;

	// Main Menu

	while( choice != 8 )
	{
		printf("please choose one of the following numbers:\n");
		printf("1 : print Countries\n");
		printf("2 : add country\n");
		printf("3 : add city to country\n");
		printf("4 : delete city from country\n");
		printf("5 : print country by name\n");
		printf("6 : delete country\n");
		printf("7 : is country in area\n");
		printf("8 : exit\n");

		scanf("%d", &choice);

		switch(choice)
			{
				case 1:	/* print all countries in the hash table */
						displayHashElements(countries);
						break;

				case 2:	/* Add new country */
						AddCountry(countries);
						break;

				case 3:	/* add a city to a country */
						AddCity(countries);
						break;

				case 4:	/* delete a city to a country */
						DeleteCity(countries);
						break;

				case 5:	/* Print country by name */
						SearchName(countries);
						break;

				case 6:	/* delete a country */
						DeleteCountry(countries);
						break;

				case 7:	/* search country by coordinates */
						SearchArea(countries);
						break;

				case 8:	/* Exit Atlas Program */
						destroyHashTable(countries);
						printf("all the memory cleaned and the program is safely closed\n");
						break;

				default: /* Wrong Input */
						printf("please choose a valid number\n");
			}
	}

	return 0;
}

// Menu Function ================================

/* read country data from a given data path */
status readCountries(hashTable table, int numOfCountries, char *fileAddress)
{
	// Validate
	if(table == NULL || numOfCountries <= 0 || fileAddress == NULL) return failure;

	//reading file configuration
	char *configurationMode = "r";
	FILE *configuration = fopen(fileAddress, configurationMode);

	char line[buffer];
	int counter = 0;
	Country currentCountry = NULL;

	while (fgets(line, buffer, configuration) != NULL && counter < numOfCountries)
	{
		if(line[0] != '\t') //line doesn't start with a tab - a Country
		{
			if(currentCountry != NULL)
			{
				// Add country To Table
				addToHashTable(table, getCountryName(currentCountry), (Element)currentCountry);

				freeCountry(currentCountry);

				currentCountry = NULL;
				counter++;
			}

			// Create country
			char *countryName = strtok(line, ",");
			int x1 = atoi(strtok(NULL,","));
			int y1 = atoi(strtok(NULL,","));
			int x2 = atoi(strtok(NULL,","));
			int y2 = atoi(strtok(NULL,","));

			currentCountry = createCountry(countryName, x1, y1, x2, y2);

		}
		else
		{
			//Add city
			char *newLine = line +1; //remove the \t
			char *cityName = strtok(newLine, ",");
			char *food = strtok(NULL, ",");
			int residents = atoi(strtok(NULL, ","));

			City newCity = createCity(cityName, food, residents);
			addCityToCountry(currentCountry, newCity);
			freeCity(newCity);
		}
	}

	if(currentCountry != NULL)
	{
		if(counter < numOfCountries) addToHashTable(table, getCountryName(currentCountry), (Element)currentCountry);

		freeCountry(currentCountry);
		currentCountry = NULL;
	}

	fclose(configuration);
	return success;
}

/* Add new country */
status AddCountry(hashTable table)
{
	// Validate
	if(table == NULL) return failure;

	printf("please enter a new country name\n");
	char countryName[buffer];
	scanf("%s", countryName);

	// search
	if(lookupInHashTable(table, countryName) != NULL)
	{
		printf("country with this name already exist\n");
		return failure;
	}

	printf("please enter two x and y coordinates :x1,y1,x2,y2\n");
	int x1=0;
	int y1=0;
	int x2=0;
	int y2=0;
	scanf("%d,%d,%d,%d", &x1, &y1, &x2, &y2);

	Country newCountry = createCountry(countryName, x1, y1, x2, y2);
	addToHashTable(table, countryName, (Element)newCountry);
	freeCountry(newCountry);

	return success;
}

/* add a city to a country */
status AddCity(hashTable table)
{
	// Validate
	if(table == NULL) return failure;

	printf("please enter a country name\n");
	char countryName[buffer];
	scanf("%s", countryName);

	// search
	Country countryToAdd = lookupInHashTable(table, countryName);
	if(countryToAdd == NULL)
	{
		printf("country not exist\n");
		return failure;
	}

	printf("please enter a city name\n");
	char cityName[buffer];
	scanf("%s", cityName);

	if(isCityExist(countryToAdd, cityName) == true)
	{
		printf("this city already exist in this country\n");
		return failure;
	}
	printf("please enter the city favorite food\n");
	char food[buffer];
	scanf("%s", food);
	printf("please enter number of residents in city\n");
	int numResidents = 0;
	scanf("%d", &numResidents);

	City newCity = createCity(cityName, food, numResidents);
	addCityToCountry(countryToAdd, newCity);
	freeCity(newCity);

	return success;
}

/* delete a city to a country */
status DeleteCity(hashTable table)
{
	// Validate
	if(table == NULL) return failure;

	printf("please enter a country name\n");
	char countryName[buffer];
	scanf("%s", countryName);

	// search
	Country countryToRemove = lookupInHashTable(table, countryName);
	if(countryToRemove == NULL)
	{
		printf("country not exist.\n");
		return failure;
	}

	printf("please enter a city name\n");
	char cityName[buffer];
	scanf("%s", cityName);

	if(isCityExist(countryToRemove, cityName) == false)
	{
		printf("the city not exist in this country\n");
		return failure;
	}

	return deleteCityFromCountry(countryToRemove, cityName);
}

/* Print country by name */
status SearchName(hashTable table)
{
	// Validate
	if(table == NULL) return failure;

	printf("please enter a country name\n");
	char countryName[buffer];
	scanf("%s", countryName);

	// search
	Country search = lookupInHashTable(table, countryName);
	if(search == NULL)
	{
		printf("country not exist.\n");
		return failure;
	}
	else
	{
		printCountry(search);
		return success;
	}
}

/* delete a country */
status DeleteCountry(hashTable table)
{
	// Validate
	if(table == NULL) return failure;

	printf("please enter a country name\n");
	char countryName[buffer];
	scanf("%s", countryName);

	// search
	Country search = lookupInHashTable(table, countryName);
	if(search == NULL)
	{
		printf("can't delete the country\n");
		return failure;
	}
	else
	{
		removeFromHashTable(table, getCountryName(search));
		printf("country deleted\n");
		return success;
	}

	return failure;
}

/* search country by coordinates */
status SearchArea(hashTable table)
{
	// Validate
	if(table == NULL) return failure;

	printf("please enter a country name\n");
	char countryName[buffer];
	scanf("%s", countryName);

	// search
	Country search = lookupInHashTable(table, countryName);
	if(search == NULL)
	{
		printf("country not exist\n");
		return failure;
	}
	else
	{
		printf("please enter x and y coordinates:x,y\n");
		int x=0;
		int y=0;
		scanf("%d,%d", &x, &y);

		if(isCoordinateInCountry(x,y,search))
		{
			printf("the coordinate in the country\n");
			return success;
		}

		printf("the coordinate not in the country\n");
		return failure;
	}
}

// ==============================================

// Key (String/Char *) Functions ================

/* Copy String - For ADT Hash Table */
Element CopyString (Element string)
{
	// Validate
	if(string == NULL) return NULL;

	return createString(string);
}

/* Print String - For ADT Hash Table */
status printString (Element string)
{
	// Validate
	if(string == NULL) return false;

	char *toPrint = string;
	printf("%s", toPrint);

	return success;
}

/* Transform String to int (for key) - For ADT Hash Table */
int TransformString (Element string)
{
	// Validate
	if(string == NULL) return -1;

	char *str = string;
	int result = 0;
	int size = strlen(str);

	for(int i = 0; i < size; i++)
	{
		result += str[i];
	}

	return result;
}

/* Compare Strings - For ADT Hash Table */
bool CompareString (Element string, Element otherString)
{
	// Validate
	if(string == NULL || otherString == NULL) return false;

	char *str = string;
	char *other = otherString;

	return strcmp(str, other) == 0;
}

/* Free String - For ADT Hash Table */
status freeString (Element string)
{
	if(string == NULL) return failure;

	free(string);

	return success;
}

// ==============================================


