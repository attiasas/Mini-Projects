using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml;

namespace assignment2
{
    class Assignment2
    {

        public void Index(string DBFilePath, string vectorFilePath)
        {
            //if (File.Exists(vectorFilePath)) return;

            List<Dictionary<string, string>> sSubLabels = new List<Dictionary<string, string>>();
            string[] sLabels = null;

            using (StreamReader reader = new StreamReader(DBFilePath))
            {
                // get Labels
                sLabels = reader.ReadLine().Trim().Split(',');

                foreach (string label in sLabels)
                {
                    sSubLabels.Add(new Dictionary<string, string>());
                }

                // get data
                string sLine = null;

                while ((sLine = reader.ReadLine()) != null && sLine != ",,,,,,,,,")
                {
                    string[] sTokens = sLine.Trim().Split(',');

                    for (int i = 0; i < sTokens.Length; i++)
                    {
                        Dictionary<string, string> label = sSubLabels[i];

                        string sToken = sTokens[i].Trim();
                        sTokens[i] = sToken;

                        if (!label.ContainsKey(sToken))
                        {
                            // add new subLabel to label
                            if (label.Count == 0)
                            {
                                label[sToken] = "";
                            }
                            else
                            {
                                // catch to count
                                int count = label.First().Value.Length;
                                label[sToken] = "";

                                for (int c = 0; c < count; c++) label[sToken] += "0";
                            }
                        }

                        // add data to bitmap (to all sub label)
                        List<string> keys = new List<string>();
                        foreach (KeyValuePair<string, string> subLabel in label) keys.Add(subLabel.Key);

                        for (int key = 0; key < keys.Count; key++)
                        {
                            if (keys[key].Equals(sToken)) label[keys[key]] += "1";
                            else label[keys[key]] += "0";
                        }
                    }

                }
            }

            // write to result file
            try
            {
                using (StreamWriter file = File.AppendText(vectorFilePath))
                {
                    for (int i = 0; i < sLabels.Length; i++)
                    {
                        if (sLabels[i].Equals("id")) continue;

                        foreach (KeyValuePair<string, string> subLabel in sSubLabels[i])
                        {
                            file.WriteLine(sLabels[i] + "," + subLabel.Key + "," + subLabel.Value);
                        }
                    }
                    file.Close();
                    file.Dispose();
                }
            }
            catch(Exception e)
            {
                Console.WriteLine("Exception: " + e.Message);
            }
            
        }

        private string OR(string vector1, string vector2)
        {
            string result = "";

            for(int i = 0; i < vector1.Length; i++)
            {
                if (vector1[i] == '1' || vector2[i] == '1') result += "1";
                else result += "0";
            }

            return result;
        }

        private string AND(string vector1, string vector2)
        {
            string result = "";

            for (int i = 0; i < vector1.Length; i++)
            {
                if (vector1[i] == '1' && vector2[i] == '1') result += "1";
                else result += "0";
            }

            return result;
        }

        public List<string> SelectVectors(XmlDocument xmlDoc, string vectorFilePath)
        {
            List<string> result = new List<string>();

            // get vectors
            StreamReader reader = new StreamReader(vectorFilePath);
            Dictionary<string, Dictionary<string, string>> vectors = new Dictionary<string, Dictionary<string, string>>();
            while(!reader.EndOfStream)
            {
                string[] vector = reader.ReadLine().Split(',');
                if (!vectors.ContainsKey(vector[0])) vectors[vector[0]] = new Dictionary<string, string>();
                vectors[vector[0]][vector[1]] = vector[2];
            }

            // select vectors by quuery
            XmlNodeList elements = xmlDoc.SelectNodes("DB_EX2_QUERY/Query_Elements/Element");
            if (elements.Count != 0)
            {
                foreach (XmlNode element in elements)
                {
                    string vector = vectors[element.Attributes.Item(0).InnerText][element.ChildNodes.Item(0).InnerText];
                    for(int i = 1; i < element.ChildNodes.Count; i++)
                    {
                        vector = OR(vector, vectors[element.Attributes.Item(0).InnerText][element.ChildNodes.Item(i).InnerText]);
                    }
                    result.Add(vector);
                }
            }
            else
            {
                // set default
                string allIdVector = "";
                int vectorLength = vectors.First().Value.First().Value.Length;
                for (int i = 0; i < vectorLength; i++) allIdVector += "1";
                result.Add(allIdVector);
            }
                
            return result;
        }


        public string CreateOutputVector(XmlDocument xmlDoc, List<string> vectors)
        {
            string result = "";

            if(vectors.Count > 1)
            {
                // has operations
                XmlNode operation = xmlDoc.SelectSingleNode("DB_EX2_QUERY/Logical_Operation");
                if(operation.InnerText.ToLower().Equals("or"))
                {
                    result = OR(vectors[0], vectors[1]);
                }
                else if(operation.InnerText.ToLower().Equals("and"))
                {
                    result = AND(vectors[0], vectors[1]);
                }
            }

            if(result.Equals(""))
            {
                // no operation
                result = vectors.First();
            }

            return result;
        }

        public List<string> SelectRecords(string DBFilePath, string outputVector)
        {
            List<string> result = new List<string>();
            StreamReader reader = new StreamReader(DBFilePath);

            string[] sLine = reader.ReadLine().Trim().Split(',');
            int idIndex = 0;
            for (int i = 0; i < sLine.Length; i++) if (sLine[i].ToLower().Equals("id")) idIndex = i;
            int index = 0;

            string sToken = null;
            while((sToken = reader.ReadLine()) != null && sToken != ",,,,,,,,,")
            {
                sLine = sToken.Trim().Split(',');
                if(outputVector[index] == '1')
                {
                    result.Add(sLine[idIndex]);
                }
                index++;
            }

            reader.Close();


            return result;
        }

    }
}
