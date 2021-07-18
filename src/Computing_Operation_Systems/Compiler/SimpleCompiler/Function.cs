using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SimpleCompiler
{
    public class Function : JackProgramElement
    {
        public VarDeclaration.VarTypeEnum ReturnType { get; private set; }
        public string Name { get; private set; }
        public List<VarDeclaration> Args { get; private set; }
        public List<VarDeclaration> Locals { get; private set; }
        public List<StatetmentBase> Body { get; private set; }
        public ReturnStatement Return { get; private set; }

        public Function()
        {
            Args = new List<VarDeclaration>();
            Locals = new List<VarDeclaration>();
            Body = new List<StatetmentBase>();
        }

        public override void Parse(TokensStack sTokens)
        {
            // function
            Token tFunc = sTokens.Pop();
            if (!(tFunc is Statement) || ((Statement)tFunc).Name != "function")
                throw new SyntaxErrorException("Expected function received: " + tFunc, tFunc);

            // return type
            Token tType = sTokens.Pop();
            if(!(tType is VarType))
                 throw new SyntaxErrorException("Expected var type, received " + tType, tType);
            ReturnType = VarDeclaration.GetVarType(tType);

            // function name (identifier)
            Token tName = sTokens.Pop();
            if(!(tName is Identifier))
                throw new SyntaxErrorException("Expected function name, received " + tType, tType);
            Name = ((Identifier)tName).Name;

            // (
            Token t = sTokens.Pop();
            if (!(t is Parentheses) || ((Parentheses)t).Name != '(')
                throw new SyntaxErrorException("Expected ( received " + t + " In Line: " + t.Line + " Position: " + t.Position, t);

            // Arg List
            while (sTokens.Count > 0 && !(sTokens.Peek() is Parentheses))
            {
                if (sTokens.Count < 3)
                    throw new SyntaxErrorException("Early termination ", t);
                Token tArgType = sTokens.Pop();
                Token tArgName = sTokens.Pop();
                VarDeclaration vc = new VarDeclaration(tArgType, tArgName);
                Args.Add(vc);
                if (sTokens.Count > 0 && sTokens.Peek() is Separator)//,
                    sTokens.Pop(); 
            }

            if (sTokens.Count < 3) throw new SyntaxErrorException("Early termination ", null);
            
            // )
            t = sTokens.Pop();
            if (!(t is Parentheses) || ((Parentheses)t).Name != ')')
                throw new SyntaxErrorException("Expected ) received " + t + " In Line: " + t.Line + " Position: " + t.Position, t);

            // {
            t = sTokens.Pop();
            if (!(t is Parentheses) || ((Parentheses)t).Name != '{')
                throw new SyntaxErrorException("Expected { received " + t + " In Line: " + t.Line + " Position: " + t.Position, t);

            // Var Decl
            while (sTokens.Count > 0 && (sTokens.Peek() is Statement) && (((Statement)sTokens.Peek()).Name == "var"))
            {
                VarDeclaration local = new VarDeclaration();
                local.Parse(sTokens);
                Locals.Add(local);
            }

            // Statments
            while (sTokens.Count > 0 && !(sTokens.Peek() is Parentheses))
            {
                StatetmentBase s = StatetmentBase.Create(sTokens.Peek());
                s.Parse(sTokens);
                Body.Add(s);
            }

            if (sTokens.Count == 0) throw new SyntaxErrorException("Early termination ", null);

            // }
            Token tEnd = sTokens.Pop();
            if (!(tEnd is Parentheses) || ((Parentheses)tEnd).Name != '}')
                throw new SyntaxErrorException("Expected } received " + tEnd + " In Line: " + tEnd.Line + " Position: " + tEnd.Position, tEnd);
            
        }

        public override string ToString()
        {
            string sFunction = "function " + ReturnType + " " + Name + "(";
            for (int i = 0; i < Args.Count - 1; i++)
                sFunction += Args[i].Type + " " + Args[i].Name + ",";
            if(Args.Count > 0)
                sFunction += Args[Args.Count - 1].Type + " " + Args[Args.Count - 1].Name;
            sFunction += "){\n";
            foreach (VarDeclaration v in Locals)
                sFunction += "\t\t" + v + "\n";
            foreach (StatetmentBase s in Body)
                sFunction += "\t\t" + s + "\n";
            sFunction += "\t}";
            return sFunction;
        }
    }
}
