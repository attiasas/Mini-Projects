using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml;
using System.Xml.XPath;

namespace ConsoleApplication1
{
    class Assignment1
    {

        //queries
        public XmlNodeList Query1 (XmlDocument xmlDoc)// returns all the movies
        {
            String query = "//movie";
            XmlNodeList allMovies = xmlDoc.SelectNodes(query);
            return allMovies;
        }

        public XmlNodeList Query2(XmlDocument xmlDoc)// returns all the  movies after 2014
        {
            String query = "Netflix/movies/movie[year > '"+2014+"']";
            XmlNodeList moviesAfter2014 = xmlDoc.SelectNodes(query);
            return moviesAfter2014;
        }

        public XmlNodeList Query3(XmlDocument xmlDoc, String actorFirstName, String actorLastName)// returns all the awards of all TV-shows of an actor
        {
            String query = "Netflix/TV-shows/TV-show/actors/actor[first-name='" + actorFirstName + "' and last-name='" + actorLastName + "']/awards/award";
            XmlNodeList allTVShowAwards = xmlDoc.SelectNodes(query);
            return allTVShowAwards;
        }
        public XmlNodeList Query4(XmlDocument xmlDoc)// returns all the TV-shows with more than one seasons
        {
            String query = "Netflix/TV-shows/TV-show[count(./seasons/season)>'1']";
            XmlNodeList moreThanOneSeason = xmlDoc.SelectNodes(query);
            return moreThanOneSeason;
        }
        public int Query5(XmlDocument xmlDoc, String genre)// retuens the amount of movies in the genre
        {
            String query = "Netflix/movies/movie[./genre = '"+genre+"']";
            int genreMovies = xmlDoc.SelectNodes(query).Count;
            return genreMovies;
        }
        public int Query6(XmlDocument xmlDoc, String yearOfBirth, int amountOfAwards)// returns the amount of different actors that were born after the year and that have more than the award amount in one movie or one TV-show
        {
            String query = "//actor[year-of-birth > '" + yearOfBirth + "' and count(./awards/award) > '"+amountOfAwards+ "']";
            XmlNodeList actors = xmlDoc.SelectNodes(query);
            int counterIn=0, counterOut=0;

            for (int i= 0; i < actors.Count; i++)
            {
                for(int j = i; j < actors.Count; j++)
                {
                    if (actors[i].FirstChild.InnerText.Equals(actors[j].FirstChild.InnerText) && actors[i].FirstChild.NextSibling.InnerText.Equals(actors[j].FirstChild.NextSibling.InnerText))
                    {
                        counterIn++;
                    }
                }
                counterOut = counterOut + counterIn - 1; //cause always it will find itself
                counterIn = 0;
            }
            int res = xmlDoc.SelectNodes(query).Count - counterOut;
            return res;
        }
        public XmlNodeList Query7(XmlDocument xmlDoc, int amountOfEpisodes)// returns the TV-shows that have more than the amount of epidods in all its seasons
        {
            String query = "Netflix/TV-shows/TV-show[sum(./seasons/season/episodes) > '"+amountOfEpisodes+"']";
            XmlNodeList res = xmlDoc.SelectNodes(query);
            return res;
        }

        //insertions
        public void InsertTVShow(XmlDocument xmlDoc, String name, String genre, String year)
        {
            XmlNode tvShows = xmlDoc.SelectSingleNode("Netflix/TV-shows");
            XmlElement tvShow = xmlDoc.CreateElement("TV-show");
            XmlElement tvShowName = CreateNewXmlElement(xmlDoc, "name", name);
            tvShow.AppendChild(tvShowName);
            XmlElement tvShowGenre = CreateNewXmlElement(xmlDoc, "genre", genre);
            tvShow.AppendChild(tvShowGenre);
            XmlElement movieYear = CreateNewXmlElement(xmlDoc, "year", year);
            tvShow.AppendChild(movieYear);
            tvShows.AppendChild(tvShow);
            xmlDoc.Save("netflix2.xml");
        }



        public void InsertMovie(XmlDocument xmlDoc, String name, String genre, String year)
        {
            XmlNode movies = xmlDoc.SelectSingleNode("Netflix/movies");
            XmlElement movie = xmlDoc.CreateElement("movie");
            XmlElement movieName = CreateNewXmlElement(xmlDoc, "name", name);
            movie.AppendChild(movieName);
            XmlElement movieGenre = CreateNewXmlElement(xmlDoc, "genre", genre);
            movie.AppendChild(movieGenre);
            XmlElement movieYear = CreateNewXmlElement(xmlDoc, "year", year);
            movie.AppendChild(movieYear);
            movies.AppendChild(movie);
            xmlDoc.Save("netflix2.xml");
        }

        

        public void InsertActorToMovie(XmlDocument xmlDoc, String movieName, String actorFirstName, String actorLastName,
            String actorBirthYear)
        {
            XmlNode movie = xmlDoc.SelectSingleNode("Netflix/movies/movie[name='" + movieName + "']");
            XmlNode actors = xmlDoc.SelectSingleNode("Netflix/movies/movie[name='" + movieName + "']/actors");

            if(movie != null && actors == null)
            {
                actors = xmlDoc.CreateElement("actors");
                movie.AppendChild(actors);
            }

            XmlElement actor = xmlDoc.CreateElement("actor");
            XmlElement firstName = CreateNewXmlElement(xmlDoc, "first-name", actorFirstName);
            actor.AppendChild(firstName);
            XmlElement lastName = CreateNewXmlElement(xmlDoc, "last-name", actorLastName);
            actor.AppendChild(lastName);
            XmlElement birthYear = CreateNewXmlElement(xmlDoc, "year-of-birth", actorBirthYear);
            actor.AppendChild(birthYear);
            actors.AppendChild(actor);
            xmlDoc.Save("netflix2.xml");
        }
        
        public void InsertActorToTVShow(XmlDocument xmlDoc, String showName, String actorFirstName, String actorLastName,
    String actorBirthYear)
        {
            XmlNode tvShow = xmlDoc.SelectSingleNode("Netflix/TV-shows/TV-show[name='" + showName + "']");
            XmlNode actors = xmlDoc.SelectSingleNode("Netflix/TV-shows/TV-show[name='" + showName + "']/actors");

            if (tvShow != null && actors == null)
            {
                actors = xmlDoc.CreateElement("actors");
                tvShow.AppendChild(actors);
            }

            XmlElement actor = xmlDoc.CreateElement("actor");
            XmlElement firstName = CreateNewXmlElement(xmlDoc, "first-name", actorFirstName);
            actor.AppendChild(firstName);
            XmlElement lastName = CreateNewXmlElement(xmlDoc, "last-name", actorLastName);
            actor.AppendChild(lastName);
            XmlElement birthYear = CreateNewXmlElement(xmlDoc, "year-of-birth", actorBirthYear);
            actor.AppendChild(birthYear);
            actors.AppendChild(actor);
            xmlDoc.Save("netflix2.xml");
        }

        public void InsertSeasonToTVShow(XmlDocument xmlDoc, String showName, String numberOfEpisodes)
        {
            XmlNode tvShow = xmlDoc.SelectSingleNode("Netflix/TV-shows/TV-show[name='" + showName + "']");
            XmlNode seasons = xmlDoc.SelectSingleNode("Netflix/TV-shows/TV-show[name='" + showName + "']/seasons");

            if (tvShow != null && seasons == null)
            {
                seasons = xmlDoc.CreateElement("seasons");
                tvShow.AppendChild(seasons);
            }

            XmlElement season = xmlDoc.CreateElement("season");
            XmlElement episodes = CreateNewXmlElement(xmlDoc, "episodes", numberOfEpisodes);
            season.AppendChild(episodes);
            seasons.AppendChild(season);
            xmlDoc.Save("netflix2.xml");
        }

        public void InsertAwardToActorInMovie(XmlDocument xmlDoc, String actorFirstName, String actorLastName,String movieName ,String awardCategory, String yearOfWinning)
        {
            XmlNode actor = xmlDoc.SelectSingleNode("Netflix/movies/movie[name='" + movieName + "']/actors/actor[first-name='"+actorFirstName+"' and last-name='"+actorLastName+"']");
            XmlNode awards = xmlDoc.SelectSingleNode("Netflix/movies/movie[name='" + movieName + "']/actors/actor[first-name='" + actorFirstName + "' and last-name='" + actorLastName + "']/awards");

            if (awards == null)
            {
                awards = xmlDoc.CreateElement("awards");
                actor.AppendChild(awards);
            }

            XmlElement award = xmlDoc.CreateElement("award");
            XmlElement category = CreateNewXmlElement(xmlDoc, "category", awardCategory);
            award.AppendChild(category);
            XmlElement year = CreateNewXmlElement(xmlDoc, "year", yearOfWinning);
            award.AppendChild(year);
            awards.AppendChild(award);
            xmlDoc.Save("netflix2.xml");
        }

        public void InsertAwardToActorInTVShow(XmlDocument xmlDoc, String actorFirstName, String actorLastName, String showName, String awardCategory, String yearOfWinning)
        {
            XmlNode actor = xmlDoc.SelectSingleNode("Netflix/TV-shows/TV-show[name='" + showName + "']/actors/actor[first-name='" + actorFirstName + "' and last-name='" + actorLastName + "']");
            XmlNode awards = xmlDoc.SelectSingleNode("Netflix/TV-shows/TV-show[name='" + showName + "']/actors/actor[first-name='" + actorFirstName + "' and last-name='" + actorLastName + "']/awards");

            if (awards == null)
            {
                awards = xmlDoc.CreateElement("awards");
                actor.AppendChild(awards);
            }

            XmlElement award = xmlDoc.CreateElement("award");
            XmlElement category = CreateNewXmlElement(xmlDoc, "category", awardCategory);
            award.AppendChild(category);
            XmlElement year = CreateNewXmlElement(xmlDoc, "year", yearOfWinning);
            award.AppendChild(year);
            awards.AppendChild(award);
            xmlDoc.Save("netflix2.xml");
        }

        private XmlElement CreateNewXmlElement(XmlDocument xmlDoc, string elemName, string elemValue)
        {
            XmlElement newXmlElem = xmlDoc.CreateElement(elemName);
            newXmlElem.InnerText = elemValue;
            return newXmlElem;
        }

        private void exampleOfCreateXML()
        {
            XmlDocument xmlDoc = new XmlDocument();
            xmlDoc.LoadXml("<DataBaseImplementationCourse/>");//insert your XML file path here
            XmlElement newXmlElem = xmlDoc.CreateElement("Lecturer");
            newXmlElem.InnerText = "Dr. Robert Moskovitch";
            xmlDoc.FirstChild.AppendChild(newXmlElem);
            newXmlElem = xmlDoc.CreateElement("TeachingAssistants");
            xmlDoc.FirstChild.AppendChild(newXmlElem);
            XmlNode tempXmlNode = newXmlElem;
            newXmlElem = CreateNewXmlElement(xmlDoc, "TeachingAssistant", "TeachingAssistant");
            newXmlElem.InnerText = "Guy Shitrit";
            tempXmlNode.AppendChild(newXmlElem);
            newXmlElem = CreateNewXmlElement(xmlDoc, "TeachingAssistant", "Ofir Dvir");
            tempXmlNode.AppendChild(newXmlElem);

            XmlNode xmlNode = xmlDoc.SelectSingleNode("DataBaseImplementationCourse/TeachingAssistants");
            XmlNodeList xmlNodesList = xmlDoc.SelectNodes("DataBaseImplementationCourse/TeachingAssistants/TeachingAssistant");
        }


    }
    }
