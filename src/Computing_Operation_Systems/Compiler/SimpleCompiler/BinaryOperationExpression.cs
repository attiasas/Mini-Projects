using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SimpleCompiler
{
    public class BinaryOperationExpression : Expression
    {
        public string Operator { get;  set; }
        public Expression Operand1 { get;  set; }
        public Expression Operand2 { get;  set; }

        public override string ToString()
        {
            return "(" + Operator + " " + Operand1 + " " + Operand2 + ")";
        }

        public override void Parse(TokensStack sTokens)
        {
            if (sTokens.Count < 5) throw new SyntaxErrorException("Early termination ", null);

            // (
            Token t = sTokens.Pop();
            if (!(t is Parentheses) || ((Parentheses)t).Name != '(')
                throw new SyntaxErrorException("Expected ( received " + t + " In Line: " + t.Line + " Position: " + t.Position, t);

            // Operator
            Token tOperator = sTokens.Pop();
            if (!(tOperator is Operator))
                throw new SyntaxErrorException("Expected Operator received " + tOperator + " In Line: " + tOperator.Line + " Position: " + tOperator.Position, tOperator);
            Operator = "" + ((Operator)tOperator).Name;

            // Operand 1
            Operand1 = Expression.Create(sTokens);
            Operand1.Parse(sTokens);

            // Operand 2
            Operand2 = Expression.Create(sTokens);
            Operand2.Parse(sTokens);

            // )
            t = sTokens.Pop();
            if (!(t is Parentheses) || ((Parentheses)t).Name != ')')
                throw new SyntaxErrorException("Expected ) received " + t + " In Line: " + t.Line + " Position: " + t.Position, t);
        }
    }
}
