using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Components
{
    // this class is a Xor Gate
    // implemented by assaf attias
    class XorGate : TwoInputGate
    {
        private NAndGate nandGate;
        private AndGate andGate;
        private OrGate orGate;

        public XorGate()
        {
            //init the gates
            nandGate = new NAndGate();
            andGate = new AndGate();
            orGate = new OrGate();
            //connect the wire to the gates
            nandGate.ConnectInput1(Input1);
            nandGate.ConnectInput2(Input2);
            orGate.ConnectInput1(Input1);
            orGate.ConnectInput2(Input2);
            //connect the and on the output
            andGate.ConnectInput1(orGate.Output);
            andGate.ConnectInput2(nandGate.Output);
            //set the  output of the xor gate
            Output = andGate.Output;
        }

        public override string ToString()
        {
            return "Xor " + Input1.Value + "," + Input2.Value + " -> " + Output.Value;
        }


        //this method is used to test the gate.
        public override bool TestGate()
        {
            Input1.Value = 0;
            Input2.Value = 0;
            if (Output.Value != 0) return false;

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
