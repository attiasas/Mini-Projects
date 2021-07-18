using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SimpleCompiler
{
    public class WhileStatement : StatetmentBase
    {
        public Expression Term { get; private set; }
        public List<StatetmentBase> Body { get; private set; }

        public override void Parse(TokensStack sTokens)
        {
            if (sTokens.Count < 6) throw new SyntaxErrorException("Early termination ", null);

            Body = new List<StatetmentBase>();

            // while
            Token tWhile = sTokens.Pop();
            if (!(tWhile is Statement) || ((Statement)tWhile).Name != "while")
                throw new SyntaxErrorException("Expected while received: " + tWhile + " In Line: " + tWhile.Line + " Position: " + tWhile.Position, tWhile);

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

            // Body
            while (sTokens.Count > 0 && !(sTokens.Peek() is Parentheses))
            {
                StatetmentBase s = StatetmentBase.Create(sTokens.Peek());
                s.Parse(sTokens);
                Body.Add(s);
            }

            // }
            if (sTokens.Count == 0) throw new SyntaxErrorException("Early termination ", null);

            t = sTokens.Pop();
            if (!(t is Parentheses) || ((Parentheses)t).Name != '}')
                throw new SyntaxErrorException("Expected } received " + t + " In Line: " + t.Line + " Position: " + t.Position, t);

        }

        public override string ToString()
        {
            string sWhile = "while(" + Term + "){\n";
            foreach (StatetmentBase s in Body)
                sWhile += "\t\t\t" + s + "\n";
            sWhile += "\t\t}";
            return sWhile;
        }

    }
}
