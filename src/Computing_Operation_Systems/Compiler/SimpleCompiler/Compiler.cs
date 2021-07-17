using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SimpleCompiler
{
    public class Compiler
    {
        private int artificialIndex;

        public Compiler()
        {

        }


        public List<VarDeclaration> ParseVarDeclarations(List<string> lVarLines)
        {
            List<VarDeclaration> lVars = new List<VarDeclaration>();
            for(int i = 0; i < lVarLines.Count; i++)
            {
                List<Token> lTokens = Tokenize(lVarLines[i], i);
                TokensStack stack = new TokensStack(lTokens);
                VarDeclaration var = new VarDeclaration();
                var.Parse(stack);
                lVars.Add(var);
            }
            return lVars;
        }


        public List<LetStatement> ParseAssignments(List<string> lLines)
        {
            List<LetStatement> lParsed = new List<LetStatement>();
            List<Token> lTokens = Tokenize(lLines);
            TokensStack sTokens = new TokensStack();
            for (int i = lTokens.Count - 1; i >= 0; i--)
                sTokens.Push(lTokens[i]);
            while(sTokens.Count > 0)
            {
                LetStatement ls = new LetStatement();
                ls.Parse(sTokens);
                lParsed.Add(ls);

            }
            return lParsed;
        }


        public List<string> GenerateCode(LetStatement aSimple, Dictionary<string, int> dSymbolTable)
        {
            List<string> lAssembly = new List<string>();
            //add here code for computing a single let statement containing only a simple expression

            // Check Variable
            if (!dSymbolTable.ContainsKey(aSimple.Variable))
                throw new SyntaxErrorException("Variable '" + aSimple.Variable + "' not found in symbol table.", null);

            // Check Value Expression
            if (aSimple.Value is NumericExpression)
            {
                // RESULT = Number
                lAssembly.Add("@" + ((NumericExpression)aSimple.Value).Value);
                lAssembly.Add("D=A");
                lAssembly.Add("@RESULT");
                lAssembly.Add("M=D");
            }
            else if (aSimple.Value is VariableExpression)
            {
                // RESULT = LOCAL[VarIndex]
                if (!dSymbolTable.ContainsKey(((VariableExpression)aSimple.Value).Name))
                    throw new SyntaxErrorException("Variable '" + ((VariableExpression)aSimple.Value).Name + "' not found in symbol table.", null);

                lAssembly.Add("@LCL");
                lAssembly.Add("D=M");
                lAssembly.Add("@" + dSymbolTable[((VariableExpression)aSimple.Value).Name]);
                lAssembly.Add("A=D+A");
                lAssembly.Add("D=M");
                lAssembly.Add("@RESULT");
                lAssembly.Add("M=D");
            }
            else if (aSimple.Value is BinaryOperationExpression)
            {
                // == ( +- <ID/Number>(Op1) <ID/Number>(Op2) ) ==
                // OPERAND1 = LCL[i]/Number
                if(((BinaryOperationExpression)aSimple.Value).Operand1 is NumericExpression)
                {
                    lAssembly.Add("@" + ((NumericExpression)((BinaryOperationExpression)aSimple.Value).Operand1).Value);
                    lAssembly.Add("D=A");
                    lAssembly.Add("@OPERAND1");
                    lAssembly.Add("M=D");
                }
                else if (((BinaryOperationExpression)aSimple.Value).Operand1 is VariableExpression)
                {
                    if (!dSymbolTable.ContainsKey(((VariableExpression)((BinaryOperationExpression)aSimple.Value).Operand1).Name))
                        throw new SyntaxErrorException("Variable '" + ((VariableExpression)((BinaryOperationExpression)aSimple.Value).Operand1).Name + "' not found in symbol table.", null);

                    lAssembly.Add("@LCL");
                    lAssembly.Add("D=M");
                    lAssembly.Add("@" + dSymbolTable[((VariableExpression)((BinaryOperationExpression)aSimple.Value).Operand1).Name]);
                    lAssembly.Add("A=D+A");
                    lAssembly.Add("D=M");
                    lAssembly.Add("@OPERAND1");
                    lAssembly.Add("M=D");
                }
                else
                    throw new SyntaxErrorException("Value Expression is Unknown Expression", null);

                // OPERAND2 = LCL[i]/Number
                if (((BinaryOperationExpression)aSimple.Value).Operand2 is NumericExpression)
                {
                    lAssembly.Add("@" + ((NumericExpression)((BinaryOperationExpression)aSimple.Value).Operand2).Value);
                    lAssembly.Add("D=A");
                    lAssembly.Add("@OPERAND2");
                    lAssembly.Add("M=D");
                }
                else if (((BinaryOperationExpression)aSimple.Value).Operand2 is VariableExpression)
                {
                    if (!dSymbolTable.ContainsKey(((VariableExpression)((BinaryOperationExpression)aSimple.Value).Operand2).Name))
                        throw new SyntaxErrorException("Variable '" + ((VariableExpression)((BinaryOperationExpression)aSimple.Value).Operand2).Name + "' not found in symbol table.", null);

                    lAssembly.Add("@LCL");
                    lAssembly.Add("D=M");
                    lAssembly.Add("@" + dSymbolTable[((VariableExpression)((BinaryOperationExpression)aSimple.Value).Operand2).Name]);
                    lAssembly.Add("A=D+A");
                    lAssembly.Add("D=M");
                    lAssembly.Add("@OPERAND2");
                    lAssembly.Add("M=D");
                }
                else
                    throw new SyntaxErrorException("Value Expression is Unknown Expression", null);

                // Compute
                lAssembly.Add("@OPERAND1");
                lAssembly.Add("D=M");
                lAssembly.Add("@OPERAND2");
                lAssembly.Add("D=D" + ((BinaryOperationExpression)aSimple.Value).Operator + "M");
                lAssembly.Add("@RESULT");
                lAssembly.Add("M=D");

            }
            else
                throw new SyntaxErrorException("Value Expression is Unknown Expression", null);

            // Copy From Result to Variable -> LOCAL[VarIndex] = RESULT
            lAssembly.Add("@LCL");
            lAssembly.Add("D=M");
            lAssembly.Add("@" + dSymbolTable[aSimple.Variable]);
            lAssembly.Add("D=D+A");
            lAssembly.Add("@ADDRESS");
            lAssembly.Add("M=D");
            lAssembly.Add("@RESULT");
            lAssembly.Add("D=M");
            lAssembly.Add("@ADDRESS");
            lAssembly.Add("A=M");
            lAssembly.Add("M=D");

            return lAssembly;
        }


        public Dictionary<string, int> ComputeSymbolTable(List<VarDeclaration> lDeclerations)
        {
            Dictionary<string, int> dTable = new Dictionary<string, int>();

            Queue<string> realVars = new Queue<string>();
            Queue<string> artiVars = new Queue<string>();

            for (int i = 0; i < lDeclerations.Count; i++)
            {
                if(lDeclerations[i].Name.StartsWith("_"))
                {
                    if (artiVars.Contains(lDeclerations[i].Name))
                        throw new SyntaxErrorException("the var '" + lDeclerations[i].Name + "' defined more than once", null);
                    artiVars.Enqueue(lDeclerations[i].Name);
                }
                else
                {
                    if (realVars.Contains(lDeclerations[i].Name))
                        throw new SyntaxErrorException("the var '" + lDeclerations[i].Name + "' defined more than once", null);
                    realVars.Enqueue(lDeclerations[i].Name);
                }
            }

            int counter = 0;

            while(realVars.Count > 0)
            {
                dTable[realVars.Dequeue()] = counter;
                counter++;
            }

            while (artiVars.Count > 0)
            {
                dTable[artiVars.Dequeue()] = counter;
                counter++;
            }

            return dTable;
        }


        public List<string> GenerateCode(List<LetStatement> lSimpleAssignments, List<VarDeclaration> lVars)
        {
            List<string> lAssembly = new List<string>();
            Dictionary<string, int> dSymbolTable = ComputeSymbolTable(lVars);
            foreach (LetStatement aSimple in lSimpleAssignments)
                lAssembly.AddRange(GenerateCode(aSimple, dSymbolTable));
            return lAssembly;
        }

        public VariableExpression Simplify(BinaryOperationExpression expression, List<VarDeclaration> lVars, List<LetStatement> result)
        {
            Console.WriteLine("(OP1): '" + expression.Operand1.ToString() + "'");
            Console.WriteLine("(OP2): '" + expression.Operand2.ToString() + "'");

            if (expression.Operand1 is BinaryOperationExpression)
            {
                Console.WriteLine("Simplify Op1 - " + expression.Operand1.ToString());
                expression.Operand1 = Simplify((BinaryOperationExpression)(expression.Operand1), lVars, result);
                Console.WriteLine("Result: " + expression.ToString());

            }
            if (expression.Operand2 is BinaryOperationExpression)
            {
                Console.WriteLine("Simplify Op2 - " + expression.Operand2.ToString());
                expression.Operand2 = Simplify((BinaryOperationExpression)(expression.Operand2), lVars, result);
                Console.WriteLine("Result: " + expression.ToString());

            }

            artificialIndex++;

            //add var declarations for artificial variables.
            lVars.Add(new VarDeclaration("Int", ("_" + artificialIndex)));

            LetStatement statement = new LetStatement();
            statement.Value = expression;
            statement.Variable = "_" + artificialIndex;

            result.Add(statement);

            VariableExpression vExpression = new VariableExpression();
            vExpression.Name = "_" + artificialIndex;

            return vExpression;
        }

        public List<LetStatement> SimplifyExpressions(LetStatement s, List<VarDeclaration> lVars)
        {
            //add here code to simply expressins in a statement.
            List<LetStatement> result = new List<LetStatement>();

            if ((s.Value is BinaryOperationExpression))
            {
                Console.WriteLine("(OP1): '" + ((BinaryOperationExpression)s.Value).Operand1.ToString() + "'");
                Console.WriteLine("(OP2): '" + ((BinaryOperationExpression)s.Value).Operand2.ToString() + "'");

                if (((BinaryOperationExpression)s.Value).Operand1 is BinaryOperationExpression)
                {
                    Console.WriteLine("Simplify Op1 - " + ((BinaryOperationExpression)s.Value).Operand1.ToString());
                    ((BinaryOperationExpression)s.Value).Operand1 = Simplify((BinaryOperationExpression)(((BinaryOperationExpression)s.Value).Operand1), lVars, result);
                    Console.WriteLine("Result: " + ((BinaryOperationExpression)s.Value).ToString());
                }

                if (((BinaryOperationExpression)s.Value).Operand2 is BinaryOperationExpression)
                {
                    Console.WriteLine("Simplify Op2 - " + ((BinaryOperationExpression)s.Value).Operand2.ToString());
                    ((BinaryOperationExpression)s.Value).Operand2 = Simplify((BinaryOperationExpression)(((BinaryOperationExpression)s.Value).Operand2), lVars, result);
                    Console.WriteLine("Result: " + ((BinaryOperationExpression)s.Value).ToString());
                }

            }

            result.Add(s);

            return result;
        }
        public List<LetStatement> SimplifyExpressions(List<LetStatement> ls, List<VarDeclaration> lVars)
        {
            List<LetStatement> lSimplified = new List<LetStatement>();
            foreach (LetStatement s in ls)
                lSimplified.AddRange(SimplifyExpressions(s, lVars));
            return lSimplified;
        }

 
        public LetStatement ParseStatement(List<Token> lTokens)
        {
            TokensStack sTokens = new TokensStack();
            for (int i = lTokens.Count - 1; i >= 0; i--)
                sTokens.Push(lTokens[i]);
            LetStatement s = new LetStatement();
            s.Parse(sTokens);
            return s;
        }

        private bool Contains(string[] a, string s)
        {
            foreach (string s1 in a)
                if (s1 == s)
                    return true;
            return false;
        }
        private bool Contains(char[] a, char c)
        {
            foreach (char c1 in a)
                if (c1 == c)
                    return true;
            return false;
        }

        private string Next(string s, char[] aDelimiters, out string sToken, out int cChars)
        {
            cChars = 1;
            sToken = s[0] + "";
            if (Contains(aDelimiters, s[0]))
                return s.Substring(1);
            int i = 0;
            for (i = 1; i < s.Length; i++)
            {
                if (Contains(aDelimiters, s[i]))
                    return s.Substring(i);
                else
                    sToken += s[i];
                cChars++;
            }
            return null;
        }

        private int isNumber(string str)
        {
            if (str.Length == 0) return -1;
            if (Contains(Token.Statements, str)) return -1;
            if (isAlphaNumeric(str[0]) == false || str[0] == '_') return -1;

            int result = 0;
            Boolean number = isDigit(str[0]);
            if (number) result = 1;

            for (int i = 1; i < str.Length; i++)
            {
                if (isAlphaNumeric(str[i]) == false) return -1;
                if (number && !isDigit(str[i])) return -1;
            }

            return result;
        }

        private Boolean isAlphaNumeric(char ch) { return (ch >= 65 && ch <= 90) || (ch >= 97 && ch <= 122) || ch == 95 || isDigit(ch); }

        private Boolean isDigit(char ch) { return ch == '0' || ch == '1' || ch == '2' || ch == '3' || ch == '4' || ch == '5' || ch == '6' || ch == '7' || ch == '8' || ch == '9'; }

        public List<Token> Tokenize(string sLine, int iLine)
        {
            List<Token> lTokens = new List<Token>();

            //Symbols
            char[] tokenDelimiters = { ' ', '\n', '\t', '{', '}', '[', ']', '(', ')', '+', '-', '*', '/', '|', '&', '=', '<', '>', '!', ';', ',' };
            string[] operators = { "+", "-", "*", "/", "|", "&", "=", "<", ">", "!" };
            string[] paranthesisTypes = { "{", "}", "[", "]", "(", ")" };
            string[] separators = { ";", "," };

            // Init
            int cuerrentPosition = 0;
            int positionAddCount = 0;
            string line = sLine;

            // Decipher Line
            while (line != null && line.Length != 0 && !line.StartsWith("//"))
            {
                string token = "";
                line = Next(line, tokenDelimiters, out token, out positionAddCount);

                if (!token.Equals(" ") && !token.Equals("\t") && !token.Equals("\n"))
                {
                    // Add Token
                    if (Contains(Token.Statements, token)) lTokens.Add(new Statement(token, iLine, cuerrentPosition));
                    else if (Contains(Token.VarTypes, token)) lTokens.Add(new VarType(token, iLine, cuerrentPosition));
                    else if (Contains(Token.Constants, token)) lTokens.Add(new Constant(token, iLine, cuerrentPosition));
                    else if (Contains(operators, token))
                    {
                        foreach (char oper in Token.Operators)
                        {
                            if (line.Length > 0 && oper == line[0])
                            {
                                Token problem = new Token();
                                problem.Line = iLine;
                                problem.Position = cuerrentPosition;
                                throw new SyntaxErrorException("Syntax Error Detected at Line " + iLine + ", in Position " + cuerrentPosition + " - '" + token + "' is not a legal statment", problem);
                            }
                        }
                        lTokens.Add(new Operator(token[0], iLine, cuerrentPosition));
                    }
                    else if (Contains(paranthesisTypes, token)) lTokens.Add(new Parentheses(token[0], iLine, cuerrentPosition));
                    else if (Contains(separators, token)) lTokens.Add(new Separator(token[0], iLine, cuerrentPosition));
                    else
                    {
                        if (isNumber(token) == 0) lTokens.Add(new Identifier(token, iLine, cuerrentPosition));
                        else if (isNumber(token) == 1) lTokens.Add(new Number(token, iLine, cuerrentPosition));
                        else
                        {
                            Token problem = new Token();
                            problem.Line = iLine;
                            problem.Position = cuerrentPosition;
                            throw new SyntaxErrorException("Syntax Error Detected at Line " + iLine + ", in Position " + cuerrentPosition + " - '" + token + "' is not a legal statment", problem);
                        }
                    }

                }

                cuerrentPosition += positionAddCount;
            }

            return lTokens;
        }

        public List<Token> Tokenize(List<string> lCodeLines)
        {
            List<Token> lTokens = new List<Token>();
            for (int i = 0; i < lCodeLines.Count; i++)
            {
                string sLine = lCodeLines[i];
                List<Token> lLineTokens = Tokenize(sLine, i);
                lTokens.AddRange(lLineTokens);
            }
            return lTokens;
        }

    }
}
