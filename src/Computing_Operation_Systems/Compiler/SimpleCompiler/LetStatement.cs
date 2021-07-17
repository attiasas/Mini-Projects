using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SimpleCompiler
{
    public class LetStatement : StatetmentBase
    {
        public string Variable { get; set; }
        public Expression Value { get; set; }

        public override string ToString()
        {
            return "let " + Variable + " = " + Value + ";";
        }

        public override void Parse(TokensStack sTokens)
        {
            if (sTokens.Count < 3) throw new SyntaxErrorException("Early termination ", null);

            // let
            Token tLet = sTokens.Pop();
            if (!(tLet is Statement) || ((Statement)tLet).Name != "let")
                throw new SyntaxErrorException("Expected let received: " + tLet + " In Line: " + tLet.Line + " Position: " + tLet.Position, tLet);

            // Identifier
            Token tId = sTokens.Pop();
            if (!(tId is Identifier))
                throw new SyntaxErrorException("Expected Identifier received: " + tId + " In Line: " + tId.Line + " Position: " + tId.Position, tId);
            Variable = ((Identifier)tId).Name;

            // =
            Token tEqual = sTokens.Pop();
            if (!(tEqual is Operator) || ((Operator)tEqual).Name != '=')
                throw new SyntaxErrorException("Expected = received " + tEqual + " In Line: " + tEqual.Line + " Position: " + tEqual.Position, tEqual);

            // Exoression
            Value = Expression.Create(sTokens);
            Value.Parse(sTokens);

            // ;
            Token tEnd = sTokens.Pop();
            if (!(tEnd is Separator) || ((Separator)tEnd).Name != ';')
                throw new SyntaxErrorException("Expected ; received " + tEnd + " In Line: " + tEnd.Line + " Position: " + tEnd.Position, tEnd);

        }

    }
}
