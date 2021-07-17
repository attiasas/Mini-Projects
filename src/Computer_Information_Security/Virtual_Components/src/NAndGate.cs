using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Components
{
    // this class is a Nand Gate
    // implemented by assaf attias
    class NAndGate : TwoInputGate
    {
        private AndGate andGate;
        private NotGate notGate;

        public NAndGate()
        {
            //init the gates
            andGate = new AndGate();
            notGate = new NotGate();
            //connect the and gate to wire
            andGate.ConnectInput1(Input1);
            andGate.ConnectInput2(Input2);
            //connect the not on the output
            notGate.ConnectInput(andGate.Output);
            //set the  output of the nand gate
            Output = notGate.Output;
        }

        public override string ToString()
        {
            return "NAnd " + Input1.Value + "," + Input2.Value + " -> " + Output.Value;
        }

        //this method is used to test the gate.
        public override bool TestGate()
        {
            Input1.Value = 0;
            Input2.Value = 0;
            if (Output.Value != 1) return false;

            Input1.Value = 0;
            Input2.Value = 1;
            if (Output.Value != 1) return false;

            Input1.Value = 1;
            Input2.Value = 0;
            if (Output.Value != 1) return false;

            Input1.Value = 1;
            Input2.Value = 1;
            if (Output.Value != 0) return false;

            return true;
        }

        
    }
}
