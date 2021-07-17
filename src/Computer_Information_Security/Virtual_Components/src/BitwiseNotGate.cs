using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Components
{
    // this class is a Not Gate for bitwise use
    // implemented by assaf attias
    class BitwiseNotGate : Gate
    {
        public WireSet Input { get; private set; }
        public WireSet Output { get; private set; }
        public int Size { get; private set; }

        private NotGate notGate;

        public BitwiseNotGate(int iSize)
        {
            Size = iSize;
            Input = new WireSet(Size);
            Output = new WireSet(Size);

            for (int i = 0; i < Size; i++)
            {
                //init the gates
                notGate = new NotGate();
                //connect the and gate
                notGate.ConnectInput(Input[i]);
                //set the output of the and gate
                Output[i].ConnectInput(notGate.Output);
            }
        }

        public void ConnectInput(WireSet ws)
        {
            Input.ConnectInput(ws);
        }

        public override string ToString()
        {
            return "Not " + Input + " -> " + Output;
        }

        //this method is used to test the gate.
        public override bool TestGate()
        {
            for (int i = 0; i < Size; i++)
            {
                Input[i].Value = 0;
                if (Output[i].Value != 1) return false;

                Input[i].Value = 1;
                if (Output[i].Value != 0) return false;
            }

            return true;
        }
    }
}
