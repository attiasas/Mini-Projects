/* implemented by Assaf Attias */

#include "Countries.h"
#define buffer 300

// Function Declare =============================
status readCountries(Country *, int, char *);
void printAllCountries(Country *, int);
void addCityToSpecificCountry(Country *, int);
int searchCountry(Country *, char *, int);
bool isCityExist(Country *, int, char *);
void addCityToSpecificCountry(Country *, int);
void removeCityFromCountry(Country *, int);
void findCoordinates(Country *, int);
void printCountryByName(Country *, int);
void freeList(Country *, int);
// ==============================================

/* Main Menu function */
int main (int argc, char* argv[])
{
	//Get the configuration params
	int numOfCountries = atoi(argv[1]);
	char *configurationFile = argv[2];

	//Read from file
	Country list[numOfCountries];
	readCountries(list, numOfCountries, configurationFile);

	//menu
	int choice = -1;
	while(choice !=6)
	{
		printf("please choose one of the following numbers:\n");
		printf("1: print countries\n");
		printf("2: add city to country\n");
		printf("3: remove city from country\n");
		printf("4: find country in area\n");
		printf("5: print country by name\n");
		printf("6: exit\n");

		scanf("%d", &choice);

		switch(choice)
		{
			case 1:
				printAllCountries(list, numOfCountries);
				break;
			case 2:
				addCityToSpecificCountry(list, numOfCountries);
				break;
			case 3:
				removeCityFromCountry(list, numOfCountries);
				break;
			case 4:
				findCoordinates(list, numOfCountries);
				break;
			case 5:
				printCountryByName(list, numOfCountries);
				break;
			case 6:
				freeList(list, numOfCountries);
				break;
			default: //wrong input
				printf("please choose a valid option\n");
		}
	}

	return 0;
}

/* read country data from a given data path */
status readCountries(Country *list, int numOfCountries, char *fileAddress)
{
	char *configurationMode = "r";//reading file configuration
	FILE *configuration = fopen(fileAddress, configurationMode);

	char line[buffer];
	int i=-1;
	while (fgets(line, buffer, configuration) != NULL && i<numOfCountries)
	{
		if(line[0] != '\t') //line doesn't start with a tab
		{//Add country
			i++;
			if(i<numOfCountries)
			{
				//the if stands for the last iteration
				char *countryName = strtok(line, ",");
				int x1 = atoi(strtok(NULL,","));
				int y1 = atoi(strtok(NULL,","));
				int x2 = atoi(strtok(NULL,","));
				int y2 = atoi(strtok(NULL,","));

				list[i] = createCountry(countryName, x1, y1, x2, y2);
			}
		}
		else
		{
			//Add city
			char *newLine = line +1; //remove the \t
			char *cityName = strtok(newLine, ",");
			char *food = strtok(NULL, ",");
			int residents = atoi(strtok(NULL, ","));
			City newCity = createCity(cityName, food, residents);
			addCityToCountry(list[i], newCity);
			freeCity(newCity);
		}
	}

	fclose(configuration);
	return success;
}

/* Print choice - handle choice functions */
void printAllCountries (Country *list, int numOfCountries)
{
	for(int i=0; i<numOfCountries; i++)
	{
		printCountry(list[i]);
	}
}

/* Add choice - handle choice functions */
void addCityToSpecificCountry(Country *list, int numOfCountries)
{
	printf("please enter a country name\n");
	char countryName[buffer];
	scanf("%s", countryName);
	int countryNum = searchCountry(list, countryName, numOfCountries);
	if(countryNum==-1)
	{
		printf("country name not exist\n");
		return;
	}

	char cityName[buffer];
	printf("please enter a city name\n");
	scanf("%s", cityName);

	if(isCityExist(list, countryNum, cityName) == true)
	{
		printf("this city already exist in this country.\n");
		return;
	}
	printf("please enter the city favorite food\n");
	char food[buffer];
	scanf("%s", food);
	printf("please enter number of residents in city\n");
	int numResidents = 0;
	scanf("%d", &numResidents);
	City newCity = createCity(cityName, food, numResidents);
	addCityToCountry(list[countryNum], newCity);
	freeCity(newCity);
}

/* Remove choice - handle choice functions */
void removeCityFromCountry(Country *list, int numOfCountries)
{
	printf("please enter a country name\n");
	char countryName[buffer];
	scanf("%s", countryName);
	int countryNum = searchCountry(list, countryName, numOfCountries);
	if(countryNum==-1)
	{
		printf("country name not exist\n");
		return;
	}
	printf("please enter a city name\n");
	char cityName[300];
	scanf("%s", cityName);
	if(isCityExist(list, countryNum, cityName) == false)
	{
		printf("city not exist in this country\n");
		return;
	}
	deleteCityFromCountry(list[countryNum], cityName);
}

/* Search choice - handle choice functions */
void findCoordinates(Country *list, int numOfCountries)
{
	printf("please enter x and y coordinates:x,y\n");
	int x=0;
	int y=0;
	scanf("%d,%d", &x, &y);
	for(int i=0; i<numOfCountries; i++)
	{
		if(isCoordinateInCountry(x,y,list[i]))
		{
			char *countryName = getCountryName(list[i]);
			printf("found in: %s\n", countryName);
			return;
		}
	}
	printf("there is no country in the area\n");
}

/* generic search for a country (for multiply choices functions) */
void printCountryByName(Country *list, int numOfCountries)
{
	printf("please enter a country name\n");
	char countryName[buffer];
	scanf("%s", countryName);
	int countryNum = searchCountry(list, countryName, numOfCountries);
	if(countryNum==-1)
	{
		printf("country name not exist.\n");
		return;
	}
	printCountry(list[countryNum]);
}

/* Free choice - handle choice functions */
void freeList(Country *list, int numOfCountries)
{
	for(int i=0; i<numOfCountries; i++)
	{
		freeCountry(list[i]);
	}
	printf("all the memory cleaned and the program is safely closed");
}
