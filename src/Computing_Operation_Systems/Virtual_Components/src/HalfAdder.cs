using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Components
{
    // this class is a Half Adder
    // implemented by assaf attias
    class HalfAdder : TwoInputGate
    {
        public Wire CarryOutput { get; private set; }

        private AndGate andGate;
        private XorGate xorGate;

        public HalfAdder()
        {
            //Init
            CarryOutput = new Wire();
            andGate = new AndGate();
            xorGate = new XorGate();

            // Connect Inputs
            andGate.ConnectInput1(Input1);
            andGate.ConnectInput2(Input2);
            xorGate.ConnectInput1(Input1);
            xorGate.ConnectInput2(Input2);

            // Connect Carry
            CarryOutput.ConnectInput(andGate.Output);

            // Connect Output
            Output.ConnectInput(xorGate.Output);
        }


        public override string ToString()
        {
            return "HA " + Input1.Value + "," + Input2.Value + " -> " + Output.Value + " (C" + CarryOutput + ")";
        }

        //this method is used to test the gate.
        public override bool TestGate()
        {
            Input1.Value = 0;
            Input2.Value = 0;
            if (CarryOutput.Value != 0 || Output.Value != 0) return false;

            Input1.Value = 0;
            Input2.Value = 1;
            if (CarryOutput.Value != 0 || Output.Value != 1) return false;

            Input1.Value = 1;
            Input2.Value = 0;
            if (CarryOutput.Value != 0 || Output.Value != 1) return false;

            Input1.Value = 1;
            Input2.Value = 1;
            if (CarryOutput.Value != 1 || Output.Value != 0) return false;

            return true;
        }
    }
}
