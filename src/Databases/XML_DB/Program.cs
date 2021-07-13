using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Xml;

namespace ConsoleApplication1
{
    class Program
    {

        static void Main(string[] args)
        {
            XmlDocument xmlDoc = new XmlDocument();
            xmlDoc.Load("netflix2.xml");//insert your XML file path here
            Assignment1 ass = new Assignment1();
            int a = ass.Query6(xmlDoc, "1950", 1);
            ass.Query6(xmlDoc, "1988", 1);
            //ass.InsertActorToTVShow(xmlDoc, "AmirShow", "Hahahahaha", "b", "1994");
        }

    }
}
