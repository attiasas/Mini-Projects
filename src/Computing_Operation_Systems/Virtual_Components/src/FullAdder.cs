using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Components
{
    // this class is a Full Adder
    // implemented by assaf attias
    class FullAdder : TwoInputGate
    {
        public Wire CarryInput { get; private set; }
        public Wire CarryOutput { get; private set; }

        private HalfAdder halfAdder1;
        private HalfAdder halfAdder2;
        private OrGate orGate;

        public FullAdder()
        {
            // Init
            CarryInput = new Wire();
            CarryOutput = new Wire();
            halfAdder1 = new HalfAdder();
            halfAdder2 = new HalfAdder();
            orGate = new OrGate();

            // Connect first adder
            halfAdder1.ConnectInput1(CarryInput);
            halfAdder1.ConnectInput2(Input1);

            // Connect second adder
            halfAdder2.ConnectInput1(halfAdder1.Output);
            halfAdder2.ConnectInput2(Input2);

            // Connect Carry
            orGate.ConnectInput1(halfAdder1.CarryOutput);
            orGate.ConnectInput2(halfAdder2.CarryOutput);
            CarryOutput.ConnectInput(orGate.Output);

            // Connect Output
            Output.ConnectInput(halfAdder2.Output);
        }


        public override string ToString()
        {
            return Input1.Value + "+" + Input2.Value + " (C" + CarryInput.Value + ") = " + Output.Value + " (C" + CarryOutput.Value + ")";
        }

        //this method is used to test the gate.
        public override bool TestGate()
        {
            CarryInput.Value = 0;
            Input1.Value = 0;
            Input2.Value = 0;
            if (CarryOutput.Value != 0 && Output.Value != 0) return false;

            CarryInput.Value = 1;
            Input1.Value = 0;
            Input2.Value = 0;
            if (CarryOutput.Value != 0 && Output.Value != 1) return false;

            CarryInput.Value = 0;
            Input1.Value = 1;
            Input2.Value = 0;
            if (CarryOutput.Value != 0 && Output.Value != 1) return false;

            CarryInput.Value = 1;
            Input1.Value = 1;
            Input2.Value = 0;
            if (CarryOutput.Value != 1 && Output.Value != 0) return false;

            CarryInput.Value = 0;
            Input1.Value = 0;
            Input2.Value = 1;
            if (CarryOutput.Value != 0 && Output.Value != 1) return false;

            CarryInput.Value = 1;
            Input1.Value = 0;
            Input2.Value = 1;
            if (CarryOutput.Value != 1 && Output.Value != 0) return false;

            CarryInput.Value = 0;
            Input1.Value = 1;
            Input2.Value = 1;
            if (CarryOutput.Value != 1 && Output.Value != 0) return false;

            CarryInput.Value = 1;
            Input1.Value = 1;
            Input2.Value = 1;
            if (CarryOutput.Value != 1 && Output.Value != 1) return false;


            return true;
        }
    }
}
