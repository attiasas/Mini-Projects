using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SimpleCompiler
{
    public class IfStatement : StatetmentBase
    {
        public Expression Term { get; private set; }
        public List<StatetmentBase> DoIfTrue { get; private set; }
        public List<StatetmentBase> DoIfFalse { get; private set; }

        public override void Parse(TokensStack sTokens)
        {
            if (sTokens.Count < 6) throw new SyntaxErrorException("Early termination ", null);
            DoIfTrue = new List<StatetmentBase>();
            DoIfFalse = new List<StatetmentBase>();

            // if
            Token tif = sTokens.Pop();
            if (!(tif is Statement) || ((Statement)tif).Name != "if")
                throw new SyntaxErrorException("Expected if received: " + tif + " In Line: " + tif.Line + " Position: " + tif.Position, tif);

            // (
            Token t = sTokens.Pop();
            if (!(t is Parentheses) || ((Parentheses)t).Name != '(')
                throw new SyntaxErrorException("Expected ( received " + t + " In Line: " + t.Line + " Position: " + t.Position, t);

            // Term
            Term = Expression.Create(sTokens);
            Term.Parse(sTokens);

            // )
            t = sTokens.Pop();
            if (!(t is Parentheses) || ((Parentheses)t).Name != ')')
                throw new SyntaxErrorException("Expected ) received " + t + " In Line: " + t.Line + " Position: " + t.Position, t);

            // {
            t = sTokens.Pop();
            if (!(t is Parentheses) || ((Parentheses)t).Name != '{')
                throw new SyntaxErrorException("Expected { received " + t + " In Line: " + t.Line + " Position: " + t.Position, t);

            // Do if True
            while (sTokens.Count > 0 && !(sTokens.Peek() is Parentheses))
            {
                StatetmentBase s = StatetmentBase.Create(sTokens.Peek());
                s.Parse(sTokens);
                DoIfTrue.Add(s);
            }

            // }
            if (sTokens.Count == 0) throw new SyntaxErrorException("Early termination ", null);

            t = sTokens.Pop();
            if (!(t is Parentheses) || ((Parentheses)t).Name != '}')
                throw new SyntaxErrorException("Expected } received " + t + " In Line: " + t.Line + " Position: " + t.Position, t);

            // Check
            if(sTokens.Count > 0 && (sTokens.Peek() is Statement) && ((Statement)sTokens.Peek()).Name == "else")
            {
                if (sTokens.Count < 3) throw new SyntaxErrorException("Early termination ", null);

                // else
                Token tElse = sTokens.Pop();

                // {
                t = sTokens.Pop();
                if (!(t is Parentheses) || ((Parentheses)t).Name != '{')
                    throw new SyntaxErrorException("Expected { received " + t + " In Line: " + t.Line + " Position: " + t.Position, t);

                // Do if False
                while (sTokens.Count > 0 && !(sTokens.Peek() is Parentheses))
                {
                    StatetmentBase s = StatetmentBase.Create(sTokens.Peek());
                    s.Parse(sTokens);
                    DoIfFalse.Add(s);
                }

                // }
                if (sTokens.Count == 0) throw new SyntaxErrorException("Early termination ", null);

                t = sTokens.Pop();
                if (!(t is Parentheses) || ((Parentheses)t).Name != '}')
                    throw new SyntaxErrorException("Expected } received " + t + " In Line: " + t.Line + " Position: " + t.Position, t);
            }

        }

        public override string ToString()
        {
            string sIf = "if(" + Term + "){\n";
            foreach (StatetmentBase s in DoIfTrue)
                sIf += "\t\t\t" + s + "\n";
            sIf += "\t\t}";
            if (DoIfFalse.Count > 0)
            {
                sIf += "else{";
                foreach (StatetmentBase s in DoIfFalse)
                    sIf += "\t\t\t" + s + "\n";
                sIf += "\t\t}";
            }
            return sIf;
        }

    }
}
