using System;
using System.Collections.Generic;

namespace SimpleCompiler
{
    public class FunctionCallExpression : Expression
    {
        public string FunctionName { get; private set; }
        public List<Expression> Args { get; private set; }

        public override void Parse(TokensStack sTokens)
        {
            if (sTokens.Count < 3) throw new SyntaxErrorException("Early termination ", null);

            // function Name
            Token tName = sTokens.Pop();
            if (!(tName is Identifier))
                throw new SyntaxErrorException("Expected Identifier received: " + tName, tName);
            FunctionName = ((Identifier)tName).Name;

            // (
            Token t = sTokens.Pop();
            if (!(t is Parentheses) || ((Parentheses)t).Name != '(')
                throw new SyntaxErrorException("Expected ( received " + t + " In Line: " + t.Line + " Position: " + t.Position, t);

            // Exoression List
            Args = new List<Expression>();

            while (sTokens.Count > 0 && (!(sTokens.Peek() is Parentheses) || ((Parentheses)sTokens.Peek()).Name != ')'))
            {
                Expression e = Expression.Create(sTokens);
                e.Parse(sTokens);
                Args.Add(e);

                // ,
                if (sTokens.Count > 0 && sTokens.Peek() is Separator) sTokens.Pop();

            }

            if (sTokens.Count == 0) throw new SyntaxErrorException("Early termination ", null);

            // )
            t = sTokens.Pop();
            if (!(t is Parentheses) || ((Parentheses)t).Name != ')')
                throw new SyntaxErrorException("Expected ) received " + t + " In Line: " + t.Line + " Position: " + t.Position , t);

        }

        public override string ToString()
        {
            string sFunction = FunctionName + "(";
            for (int i = 0; i < Args.Count - 1; i++)
                sFunction += Args[i] + ",";
            if (Args.Count > 0)
                sFunction += Args[Args.Count - 1];
            sFunction += ")";
            return sFunction;
        }
    }
}