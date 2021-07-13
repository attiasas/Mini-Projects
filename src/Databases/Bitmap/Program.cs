using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Xml;

namespace assignment2
{
    class Program
    {
        public static string dataPath = @"D:\University\SecondYear\Semester 4\Implementation of database systems\Exercises\Assignment 2\data.csv";
        public static string bitmapPath = @"D:\University\SecondYear\Semester 4\Implementation of database systems\Exercises\Assignment 2\bitmap.csv";
        public static int testNum = 0;
        public static int passNum = 0;

        static void Main(string[] args)
        {
            Assignment2 assignment = new Assignment2();
            string query1 = @"D:\University\SecondYear\Semester 4\Implementation of database systems\Exercises\Assignment 2\testQuery.xml";
            string query2 = @"D:\University\SecondYear\Semester 4\Implementation of database systems\Exercises\Assignment 2\testQuery2.xml";
            string query3 = @"D:\University\SecondYear\Semester 4\Implementation of database systems\Exercises\Assignment 2\testQuery3.xml";
            string query4 = @"D:\University\SecondYear\Semester 4\Implementation of database systems\Exercises\Assignment 2\testQuery4.xml";

            TestQuery1(assignment,query1);
            TestQuery2(assignment, query2);
            TestQuery3(assignment, query3);
            TestQuery4(assignment, query4);

            Console.WriteLine("Done testing, " + passNum + "/" + testNum + " tests passed.");
            Console.Read();
		}

        public static void TestQuery1(Assignment2 assignment, string queryPath)
        {
            assignment.Index(dataPath, bitmapPath);
            XmlDocument document = new XmlDocument();
            document.Load(queryPath);
            List<string> result = assignment.SelectRecords(dataPath, assignment.CreateOutputVector(document, assignment.SelectVectors(document, bitmapPath)));
            // tests
            Console.WriteLine("-- Test ------------------------");
            Test(result.Count == 7, "numOfRecords needs to be 7"); // check size
            Test(result.Contains("12"), "Record id 12 should be in result"); // check if contian random records that should exist
            Test(result.Contains("5"), "Record id 5 should be in result"); // check if contian random records that should exist
            Test(!result.Contains("1"), "Record id 1 should not be in result"); // check if contian random records that should not exist
            Test(!result.Contains("20"), "Record id 20 should not be in result"); // check if contian random records that should not exist
            Console.WriteLine("--------------------------------");
        }

        public static void TestQuery2(Assignment2 assignment, string queryPath)
        {
            assignment.Index(dataPath, bitmapPath);
            XmlDocument document = new XmlDocument();
            document.Load(queryPath);
            List<string> result = assignment.SelectRecords(dataPath, assignment.CreateOutputVector(document, assignment.SelectVectors(document, bitmapPath)));
            // tests
            Console.WriteLine("-- Test ------------------------");
            Test(result.Count == 12, "numOfRecords needs to be 12"); // check size
            Test(result.Contains("16"), "Record id 16 should be in result"); // check if contian random records that should exist
            Test(result.Contains("4"), "Record id 4 should be in result"); // check if contian random records that should exist
            Test(!result.Contains("3"), "Record id 3 should not be in result"); // check if contian random records that should not exist
            Test(!result.Contains("19"), "Record id 19 should not be in result"); // check if contian random records that should not exist
            Console.WriteLine("--------------------------------");
        }

        public static void TestQuery3(Assignment2 assignment, string queryPath)
        {
            assignment.Index(dataPath, bitmapPath);
            XmlDocument document = new XmlDocument();
            document.Load(queryPath);
            List<string> result = assignment.SelectRecords(dataPath, assignment.CreateOutputVector(document, assignment.SelectVectors(document, bitmapPath)));
            // tests
            Console.WriteLine("-- Test ------------------------");
            Test(result.Count == 5, "numOfRecords needs to be 5"); // check size
            Test(result.Contains("6"), "Record id 6 should be in result"); // check if contian random records that should exist
            Test(result.Contains("9"), "Record id 9 should be in result"); // check if contian random records that should exist
            Test(!result.Contains("13"), "Record id 13 should not be in result"); // check if contian random records that should not exist
            Test(!result.Contains("15"), "Record id 15 should not be in result"); // check if contian random records that should not exist
            Console.WriteLine("--------------------------------");
        }

        public static void TestQuery4(Assignment2 assignment, string queryPath)
        {
            assignment.Index(dataPath, bitmapPath);
            XmlDocument document = new XmlDocument();
            document.Load(queryPath);
            List<string> result = assignment.SelectRecords(dataPath, assignment.CreateOutputVector(document, assignment.SelectVectors(document, bitmapPath)));
            // tests
            Console.WriteLine("-- Test ------------------------");
            Test(result.Count == 10, "numOfRecords needs to be 10"); // check size
            Test(result.Contains("2"), "Record id 2 should be in result"); // check if contian random records that should exist
            Test(result.Contains("3"), "Record id 3 should be in result"); // check if contian random records that should exist
            Test(!result.Contains("4"), "Record id 4 should not be in result"); // check if contian random records that should not exist
            Test(!result.Contains("14"), "Record id 14 should not be in result"); // check if contian random records that should not exist
            Console.WriteLine("--------------------------------");
        }


        public static void Test(bool test, string msg)
        {
            testNum++;
            Console.Write("Test " + testNum + ": ");
            if (test)
            {
                passNum++;
                Console.WriteLine("Passed.");
            }
            else
                Console.WriteLine("Faild, " + msg);
        }
                

    }
}
