using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Components
{
    // this class is a And Gate for bitwise use
    // implemented by assaf attias
    class BitwiseAndGate : BitwiseTwoInputGate
    {
        private AndGate andGate;

        public BitwiseAndGate(int iSize)
            : base(iSize)
        {
            for (int i = 0; i < Size; i++)
            {
                //init the gates
                andGate = new AndGate();
                //connect the and gate
                andGate.ConnectInput1(Input1[i]);
                andGate.ConnectInput2(Input2[i]);
                //set the output of the and gate
                Output[i].ConnectInput(andGate.Output);
            }
        }


        public override string ToString()
        {
            return "And " + Input1 + ", " + Input2 + " -> " + Output;
        }

        //this method is used to test the gate.
        public override bool TestGate()
        {
            for (int i = 0; i < Size; i++)
            {
                Input1[i].Value = 0;
                Input2[i].Value = 0;
                if (Output[i].Value != 0) return false;

                Input1[i].Value = 0;
                Input2[i].Value = 1;
                if (Output[i].Value != 0) return false;

                Input1[i].Value = 1;
                Input2[i].Value = 0;
                if (Output[i].Value != 0) return false;

                Input1[i].Value = 1;
                Input2[i].Value = 1;
                if (Output[i].Value != 1) return false;

            }

            return true;
        }
    }
}
