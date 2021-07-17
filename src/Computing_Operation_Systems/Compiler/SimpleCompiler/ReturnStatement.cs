using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SimpleCompiler
{
    public class ReturnStatement : StatetmentBase
    {
        public Expression Expression { get; private set; }

        public override void Parse(TokensStack sTokens)
        {
            Token tRet = sTokens.Pop();//return
            if (!(tRet is Statement) || ((Statement)tRet).Name != "return")
                throw new SyntaxErrorException("Expected return received: " + tRet + " In Line: " + tRet.Line + " Position: " + tRet.Position, tRet);
            Expression = Expression.Create(sTokens);
            Expression.Parse(sTokens);
            Token tEnd = sTokens.Pop();//;
        }

        public override string ToString()
        {
            return "return " + Expression + ";";
        }
    }
}
