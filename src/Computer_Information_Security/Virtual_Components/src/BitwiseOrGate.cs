using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Components
{
    // this class is a Or Gate for bitwise use
    // implemented by assaf attias
    class BitwiseOrGate : BitwiseTwoInputGate
    {
        private OrGate orGate;

        public BitwiseOrGate(int iSize)
            : base(iSize)
        {
            for (int i = 0; i < Size; i++)
            {
                //init the gates
                orGate = new OrGate();
                //connect the or gate
                orGate.ConnectInput1(Input1[i]);
                orGate.ConnectInput2(Input2[i]);
                //set the output of the or gate
                Output[i].ConnectInput(orGate.Output);
            }
        }

        public override string ToString()
        {
            return "Or " + Input1 + ", " + Input2 + " -> " + Output;
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
                if (Output[i].Value != 1) return false;

                Input1[i].Value = 1;
                Input2[i].Value = 0;
                if (Output[i].Value != 1) return false;

                Input1[i].Value = 1;
                Input2[i].Value = 1;
                if (Output[i].Value != 1) return false;

            }

            return true;
        }
    }
}
