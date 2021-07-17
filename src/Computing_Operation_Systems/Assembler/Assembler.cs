using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Assembler
{
    public class Assembler
    {
        private const int WORD_SIZE = 16;

        private Dictionary<string, int[]> m_dControl, m_dJmp; //these dictionaries map command mnemonics to machine code - they are initialized at the bottom of the class

        //more data structures here (symbol map, ...)
        private Dictionary<string, int> m_SymbolMap;

        public Assembler()
        {
            InitCommandDictionaries();
        }

        //this method is called from the outside to run the assembler translation
        public void TranslateAssemblyFile(string sInputAssemblyFile, string sOutputMachineCodeFile)
        {
            //read the raw input, including comments, errors, ...
            StreamReader sr = new StreamReader(sInputAssemblyFile);
            List<string> lLines = new List<string>();
            while (!sr.EndOfStream)
            {
                lLines.Add(sr.ReadLine());
            }
            sr.Close();
            //translate to machine code
            List<string> lTranslated = TranslateAssemblyFile(lLines);
            //write the output to the machine code file
            StreamWriter sw = new StreamWriter(sOutputMachineCodeFile);
            foreach (string sLine in lTranslated)
                sw.WriteLine(sLine);
            sw.Close();
        }

        //translate assembly into machine code
        private List<string> TranslateAssemblyFile(List<string> lLines)
        {
            //init data structures here 

            //expand the macros
            List<string> lAfterMacroExpansion = ExpendMacros(lLines);

            //first pass - create symbol table and remove lable lines
            CreateSymbolTable(lAfterMacroExpansion);

            //second pass - replace symbols with numbers, and translate to machine code
            List<string> lAfterTranslation = TranslateAssemblyToMachineCode(lAfterMacroExpansion);
            return lAfterTranslation;
        }

        
        //first pass - replace all macros with real assembly
        private List<string> ExpendMacros(List<string> lLines)
        {
            List<string> lAfterExpansion = new List<string>();
            for (int i = 0; i < lLines.Count; i++)
            {
                //remove all redudant characters
                string sLine = CleanWhiteSpacesAndComments(lLines[i]);
                if (sLine == "")
                    continue;
                //if the line contains a macro, expand it, otherwise the line remains the same
                List<string> lExpanded = ExapndMacro(sLine);
                //we may get multiple lines from a macro expansion
                foreach (string sExpanded in lExpanded)
                {
                    lAfterExpansion.Add(sExpanded);
                }
            }
            return lAfterExpansion;
        }

        //expand a single macro line
        private List<string> ExapndMacro(string sLine)
        {
            List<string> lExpanded = new List<string>();
            
            if (IsCCommand(sLine))
            {
                string sDest, sCompute, sJmp;
                GetCommandParts(sLine, out sDest, out sCompute, out sJmp);
                //your code here - check for indirect addessing and for jmp shortcuts
                
                if(sJmp.Length == 0)
                {
                    if(sDest.Length == 0)
                    {
                        if(sCompute.EndsWith("++") || sCompute.EndsWith("--"))
                        {
                            char oper = sCompute[sCompute.Length - 1];
                            sCompute = sCompute.Substring(0, sCompute.Length - 2);
                            if (sCompute.Equals("D")) lExpanded.Add("D=D" + oper + "1");
                            else if (sCompute.Equals("A")) lExpanded.Add("A=A" + oper + "1");
                            else if (sCompute.Equals("M")) lExpanded.Add("M=M" + oper + "1");
                            else
                            {
                                lExpanded.Add("@" + sCompute);
                                lExpanded.Add("M=M" + oper + "1");
                            }
                        }
                        else
                            throw new FormatException("Cannot parse: " + sLine + " - macro not found");
                    }
                    else
                    {
                        if(isLabel(sDest) && isNumber(sCompute) == 1)
                        {
                            if(sDest.Equals("A")) lExpanded.Add("@" + sCompute);
                            else if(sDest.Equals("D"))
                            {
                                lExpanded.Add("@" + sCompute);
                                lExpanded.Add("D=A");
                            }
                            else
                            {
                                lExpanded.Add("@" + sCompute);
                                lExpanded.Add("D=A");
                                lExpanded.Add("@" + sDest);
                                lExpanded.Add("M=D");
                            }
                        }
                        else if (isLabel(sDest) && isLabel(sCompute))
                        {
                            lExpanded.Add("@" + sCompute);
                            lExpanded.Add("D=M");
                            lExpanded.Add("@" + sDest);
                            lExpanded.Add("M=D");
                        }
                        else if (isLabel(sDest))
                        {
                            if(sCompute.Equals("D"))
                            {
                                lExpanded.Add("@" + sDest);
                                lExpanded.Add("M=D");
                            }
                            else if (sCompute.Equals("A"))
                            {
                                lExpanded.Add("D=A");
                                lExpanded.Add("@" + sDest);
                                lExpanded.Add("M=D");
                            }
                            else if (sCompute.Equals("M"))
                            {
                                lExpanded.Add("D=M");
                                lExpanded.Add("@" + sDest);
                                lExpanded.Add("M=D");
                            }

                        }
                        else if(isLabel(sCompute))
                        {
                            lExpanded.Add("@" + sCompute);
                            lExpanded.Add(sDest + "=M");
                        }
                    }
                }
                else if(sJmp.Contains(":"))
                {
                    int idx = sLine.IndexOf(':');
                    string label = sLine.Substring(idx + 1);
                    sLine = sLine.Substring(0, idx);
                    lExpanded.Add("@" + label);
                    lExpanded.Add(sLine);
                }


            }
            if (lExpanded.Count == 0)
                lExpanded.Add(sLine);
            return lExpanded;
        }

        private bool isLabel(string str)
        {
            if (str.Length > 3) return true;
            if (m_dControl.ContainsKey(str)) return false;
            if (str.Equals("A") || str.Equals("M") || str.Equals("D")) return false;
            if (str.Equals("AD") || str.Equals("DA") || str.Equals("AM") || str.Equals("MA") || str.Equals("MD") || str.Equals("DM")) return false;
            if (str.Equals("ADM") || str.Equals("AMD") || str.Equals("MAD") || str.Equals("DAM") || str.Equals("DMA") || str.Equals("MDA")) return false;

            return true;
        }

        //second pass - record all symbols - labels and variables
        private void CreateSymbolTable(List<string> lLines)
        {
            m_SymbolMap = new Dictionary<string, int>();
            
            // Create Universal menmonics
            m_SymbolMap["R0"] = 0;
            m_SymbolMap["R1"] = 1;
            m_SymbolMap["R2"] = 2;
            m_SymbolMap["R3"] = 3;
            m_SymbolMap["R4"] = 4;
            m_SymbolMap["R5"] = 5;
            m_SymbolMap["R6"] = 6;
            m_SymbolMap["R7"] = 7;
            m_SymbolMap["R8"] = 8;
            m_SymbolMap["R9"] = 9;
            m_SymbolMap["R10"] = 10;
            m_SymbolMap["R11"] = 11;
            m_SymbolMap["R12"] = 12;
            m_SymbolMap["R13"] = 13;
            m_SymbolMap["R14"] = 14;
            m_SymbolMap["R15"] = 15;
            m_SymbolMap["SCREEN"] = 16384;
            m_SymbolMap["KBD"] = 24576;

            string sLine = "";

            List<string> varList = new List<string>();

            for (int i = 0; i < lLines.Count; i++)
            {
                sLine = lLines[i];

                if (IsLabelLine(sLine))
                {
                    int EMPTY = -1;
                    //record label in symbol table
                    if(m_SymbolMap.TryGetValue(sLine.Substring(1, sLine.Length - 2), out EMPTY) && EMPTY != -1)
                    {
                        throw new FormatException("Cannot parse line " + i + ": " + lLines[i] + ", variable - '" + sLine.Substring(1, sLine.Length - 2) + "' declared twice");
                    }

                    m_SymbolMap[sLine.Substring(1, sLine.Length - 2)] = i;
                    
                    lLines.Remove(sLine);
                    i--;
                }
                else if (IsACommand(sLine))
                {
                    //may contain a variable
                    if (isNumber(sLine.Substring(1)) == -1)
                    {
                        throw new FormatException("Cannot parse line " + i + ": " + lLines[i]);
                    }
                    else if (isNumber(sLine.Substring(1)) == 0 && !varList.Contains(sLine.Substring(1)))
                    {
                        varList.Add(sLine.Substring(1));
                    }
                }
                else if (IsCCommand(sLine))
                {
                    //do nothing here
                }
                else
                    throw new FormatException("Cannot parse line " + i + ": " + lLines[i]);
            }

            for (int i = 0; i < varList.Count; i++)
            {
                if (m_SymbolMap.ContainsKey(varList[i]))
                {
                    varList.Remove(varList[i]);
                    i--;
                }
            }

            // Add To Map
            for (int i = 0; i < varList.Count; i++)
            {
                m_SymbolMap[varList[i]] = 16 + i;
            }

        }
        
        //third pass - translate lines into machine code, replaicng symbols with numbers
        private List<string> TranslateAssemblyToMachineCode(List<string> lLines)
        {
            string sLine = "";
            List<string> lAfterPass = new List<string>();
            for (int i = 0; i < lLines.Count; i++)
            {
                sLine = lLines[i];

                if (IsACommand(sLine))
                {
                    //translate an A command into a sequence of bits

                    if(isNumber(sLine.Substring(1)) == 1)
                    {
                        // Number
                        int number = toInt(sLine.Substring(1));
                        
                        if(number > 32768) throw new FormatException("Cannot parse line " + i + ": " + lLines[i] + " - Value is bigger that 2^15");

                        lAfterPass.Add(ToBinary(number));
                    }
                    else if(isNumber(sLine.Substring(1)) == 0)
                    {
                        // Label
                        int labelAddress = 0;
                        if (!m_SymbolMap.TryGetValue(sLine.Substring(1), out labelAddress))
                            throw new FormatException("Cannot parse line " + i + ": " + lLines[i] + " - Symbol: '" + sLine.Substring(1) + "' not found");

                        lAfterPass.Add(ToBinary(labelAddress));
                    }
                    else
                    {
                        throw new FormatException("Cannot parse line " + i + ": " + lLines[i] + " variables can't start with a number");
                    }
                }
                else if (IsCCommand(sLine))
                {
                    string sDest, sControl, sJmp;
                    GetCommandParts(sLine, out sDest, out sControl, out sJmp);
                    //translate an C command into a sequence of bits
                    int[] iArrayControl, iArrayJmp;

                    //translate Control and Jump
                    if (!m_dControl.TryGetValue(sControl, out iArrayControl))
                        throw new FormatException("Cannot parse line " + i + ": " + lLines[i] + " - Symbol: '" + sControl + "' not found");
                    if (!m_dJmp.TryGetValue(sJmp, out iArrayJmp))
                        throw new FormatException("Cannot parse line " + i + ": " + lLines[i] + " - Symbol: '" + sJmp + "' not found");

                    //translate Destination
                    char M = '0';
                    char D = '0';
                    char A = '0';

                    for (int index = 0; index < sDest.Length; index++)
                    {
                        if (sDest[index] == 'M' && M == '0') M = '1';
                        else if(sDest[index] == 'M' && M != '0') throw new FormatException("Cannot parse line " + i + ": " + lLines[i] + " - '" + sDest + "' not a Legal prase");

                        if (sDest[index] == 'D' && D == '0') D = '1';
                        else if (sDest[index] == 'D' && D != '0') throw new FormatException("Cannot parse line " + i + ": " + lLines[i] + " - '" + sDest + "' not a Legal prase");

                        if (sDest[index] == 'A' && A == '0') A = '1';
                        else if (sDest[index] == 'A' && A != '0') throw new FormatException("Cannot parse line " + i + ": " + lLines[i] + " - '" + sDest + "' not a Legal prase");
                    }

                    lAfterPass.Add("100" + ToString(iArrayControl) + A + D + M + ToString(iArrayJmp));

                }
                else
                    throw new FormatException("Cannot parse line " + i + ": " + lLines[i]);
            }
            return lAfterPass;
        }

        /* Check if a string is a legal number (1) or label(0). - format error (-1) */
        private int isNumber(string str)
        {
            if (str.Length == 0) return -1;

            int result = 0;
            Boolean number = isDigit(str[0]);
            if (number) result = 1;

            for (int i = 1; i < str.Length; i++)
            {
                if(number && !isDigit(str[i])) result = -1;
            }

            return result;
        }

        private int toInt(string str)
        {
            if (str == null || isNumber(str) != 1) return 0;

            int result = 0;

            for (int i = 0; i < str.Length; i++) result +=  (int)((str[i] - '0') * Math.Pow(10,str.Length- i - 1));

            return result;
        }

        private Boolean isDigit(char ch) { return ch == '0' || ch == '1' || ch == '2' || ch == '3' || ch == '4' || ch == '5' || ch == '6' || ch == '7' || ch == '8' || ch == '9'; }

        //helper functions for translating numbers or bits into strings
        private string ToString(int[] aBits)
        {
            string sBinary = "";
            for (int i = 0; i < aBits.Length; i++)
                sBinary += aBits[i];
            return sBinary;
        }

        private string ToBinary(int x)
        {
            string sBinary = "";
            for (int i = 0; i < WORD_SIZE; i++)
            {
                sBinary = (x % 2) + sBinary;
                x = x / 2;
            }
            return sBinary;
        }


        //helper function for splitting the various fields of a C command
        private void GetCommandParts(string sLine, out string sDest, out string sControl, out string sJmp)
        {
            if (sLine.Contains('='))
            {
                int idx = sLine.IndexOf('=');
                sDest = sLine.Substring(0, idx);
                sLine = sLine.Substring(idx + 1);
            }
            else
                sDest = "";
            if (sLine.Contains(';'))
            {
                int idx = sLine.IndexOf(';');
                sControl = sLine.Substring(0, idx);
                sJmp = sLine.Substring(idx + 1);

            }
            else
            {
                sControl = sLine;
                sJmp = "";
            }
        }

        private bool IsCCommand(string sLine)
        {
            return !IsLabelLine(sLine) && sLine[0] != '@';
        }

        private bool IsACommand(string sLine)
        {
            return sLine[0] == '@';
        }

        private bool IsLabelLine(string sLine)
        {
            if (sLine.StartsWith("(") && sLine.EndsWith(")"))
                return true;
            return false;
        }

        private string CleanWhiteSpacesAndComments(string sDirty)
        {
            string sClean = "";
            for (int i = 0 ; i < sDirty.Length ; i++)
            {
                char c = sDirty[i];
                if (c == '/' && i < sDirty.Length - 1 && sDirty[i + 1] == '/') // this is a comment
                    return sClean;
                if (c > ' ' && c <= '~')//ignore white spaces
                    sClean += c;
            }
            return sClean;
        }

        private void InitCommandDictionaries()
        {
            m_dControl = new Dictionary<string, int[]>();

            m_dControl["0"] = new int[] { 0, 1, 0, 1, 0, 1, 0 };
            m_dControl["1"] = new int[] { 0, 1, 1, 1, 1, 1, 1 };
            m_dControl["-1"] = new int[] { 0, 1, 1, 1, 0, 1, 0 };
            m_dControl["D"] = new int[] { 0, 0, 0, 1, 1, 0, 0 };
            m_dControl["A"] = new int[] { 0, 1, 1, 0, 0, 0, 0 };
            m_dControl["!D"] = new int[] { 0, 0, 0, 1, 1, 0, 1 };
            m_dControl["!A"] = new int[] { 0, 1, 1, 0, 0, 0, 1 };
            m_dControl["-D"] = new int[] { 0, 0, 0, 1, 1, 1, 1 };
            m_dControl["-A"] = new int[] { 0, 1, 1, 0, 0,1, 1 };
            m_dControl["D+1"] = new int[] { 0, 0, 1, 1, 1, 1, 1 };
            m_dControl["A+1"] = new int[] { 0, 1, 1, 0, 1, 1, 1 };
            m_dControl["D-1"] = new int[] { 0, 0, 0, 1, 1, 1, 0 };
            m_dControl["A-1"] = new int[] { 0, 1, 1, 0, 0, 1, 0 };
            m_dControl["D+A"] = new int[] { 0, 0, 0, 0, 0, 1, 0 };
            m_dControl["A+D"] = new int[] { 0, 0, 0, 0, 0, 1, 0 };
            m_dControl["D-A"] = new int[] { 0, 0, 1, 0, 0, 1, 1 };
            m_dControl["A-D"] = new int[] { 0, 0, 0, 0, 1,1, 1 };
            m_dControl["D&A"] = new int[] { 0, 0, 0, 0, 0, 0, 0 };
            m_dControl["D|A"] = new int[] { 0, 0, 1, 0,1, 0, 1 };

            m_dControl["M"] = new int[] { 1, 1, 1, 0, 0, 0, 0 };
            m_dControl["!M"] = new int[] { 1, 1, 1, 0, 0, 0, 1 };
            m_dControl["-M"] = new int[] { 1, 1, 1, 0, 0, 1, 1 };
            m_dControl["M+1"] = new int[] { 1, 1, 1, 0, 1, 1, 1 };
            m_dControl["M-1"] = new int[] { 1, 1, 1, 0, 0, 1, 0 };
            m_dControl["D+M"] = new int[] { 1, 0, 0, 0, 0, 1, 0 };
            m_dControl["M+D"] = new int[] { 1, 0, 0, 0, 0, 1, 0 };
            m_dControl["D-M"] = new int[] { 1, 0, 1, 0, 0, 1, 1 };
            m_dControl["M-D"] = new int[] { 1, 0, 0, 0, 1, 1, 1 };
            m_dControl["D&M"] = new int[] { 1, 0, 0, 0, 0, 0, 0 };
            m_dControl["D|M"] = new int[] { 1, 0, 1, 0, 1, 0, 1 };


            m_dJmp = new Dictionary<string, int[]>();

            m_dJmp[""] = new int[] { 0, 0, 0 };
            m_dJmp["JGT"] = new int[] { 0, 0, 1 };
            m_dJmp["JEQ"] = new int[] { 0, 1, 0 };
            m_dJmp["JGE"] = new int[] { 0, 1, 1 };
            m_dJmp["JLT"] = new int[] { 1, 0, 0 };
            m_dJmp["JNE"] = new int[] { 1, 0, 1 };
            m_dJmp["JLE"] = new int[] { 1, 1, 0 };
            m_dJmp["JMP"] = new int[] { 1, 1, 1 };

        }
    }
}
