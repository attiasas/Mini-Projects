using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Components
{
    // this class is a MuxGate Gate
    // implemented by assaf attias
    class MuxGate : TwoInputGate
    {
        public Wire ControlInput { get; private set; }

        private OrGate orGate;
        private AndGate andGate1;
        private AndGate andGate2;
        private NotGate notGate;


        public MuxGate()
        {
            ControlInput = new Wire();

            //init the gates
            orGate = new OrGate();
            andGate1 = new AndGate();
            andGate2 = new AndGate();
            notGate = new NotGate();
            //connect the gate to wires
            andGate1.ConnectInput1(Input1);
            andGate2.ConnectInput1(Input2);
            //connect the gate to control
            andGate2.ConnectInput2(ControlInput);
            notGate.ConnectInput(ControlInput);
            andGate1.ConnectInput2(notGate.Output);
            //connect the or on the output
            orGate.ConnectInput1(andGate1.Output);
            orGate.ConnectInput2(andGate2.Output);
            //set the  output of the or gate
            Output.ConnectInput(orGate.Output);

        }

        public void ConnectControl(Wire wControl)
        {
            ControlInput.ConnectInput(wControl);
        }

        public override string ToString()
        {
            return "Mux " + Input1.Value + "," + Input2.Value + ",C" + ControlInput.Value + " -> " + Output.Value;
        }

        //this method is used to test the gate.
        public override bool TestGate()
        {
            Input1.Value = 0;
            Input2.Value = 1;
            ControlInput.Value = 0;
            if (Output.Value != 0) return false;

            Input1.Value = 1;
            Input2.Value = 0;
            ControlInput.Value = 0;
            if (Output.Value != 1) return false;
            
            Input1.Value = 0;
            Input2.Value = 1;
            ControlInput.Value = 1;
            if (Output.Value != 1) return false;
            
            Input1.Value = 1;
            Input2.Value = 0;
            ControlInput.Value = 1;
            if (Output.Value != 0) return false;
            
            return true;
        }
    }
}
