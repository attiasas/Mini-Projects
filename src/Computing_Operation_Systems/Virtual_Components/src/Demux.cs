using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Components
{
    // this class is a Demux Gate
    // implemented by assaf attias
    class Demux : Gate
    {
        public Wire Output1 { get; private set; }
        public Wire Output2 { get; private set; }
        public Wire Input { get; private set; }
        public Wire Control { get; private set; }

        private AndGate andGate1;
        private AndGate andGate2;
        private NotGate notGate;

        public Demux()
        {
            Input = new Wire();
            Output1 = new Wire();
            Output2 = new Wire();
            Control = new Wire();

            //init the gates
            andGate1 = new AndGate();
            andGate2 = new AndGate();
            notGate = new NotGate();
            //connect the gate to wires
            andGate1.ConnectInput1(Input);
            andGate2.ConnectInput1(Input);
            //connect the gate to control
            andGate1.ConnectInput2(Control);
            notGate.ConnectInput(Control);
            andGate2.ConnectInput2(notGate.Output);
            //set the  output of the and gates
            Output2 = andGate1.Output;
            Output1 = andGate2.Output;
        }

        public void ConnectControl(Wire wControl)
        {
            Control.ConnectInput(wControl);
        }
        public void ConnectInput(Wire wInput)
        {
            Input.ConnectInput(wInput);
        }

        public override string ToString()
        {
            return "Demux " + Input.Value + ",C" + Control.Value + " -> O0: " + Output1.Value + ", O1: " + Output2.Value;
        }

        //this method is used to test the gate.
        public override bool TestGate()
        {
            Input.Value = 0;
            Control.Value = 0;
            if (Output1.Value != 0 || Output2.Value != 0) return false;

            Input.Value = 0;
            Control.Value = 1;
            if (Output1.Value != 0 || Output2.Value != 0) return false;

            Input.Value = 1;
            Control.Value = 0;
            if (Output1.Value != 1 || Output2.Value != 0) return false;

            Input.Value = 1;
            Control.Value = 1;
            if (Output1.Value != 0 || Output2.Value != 1) return false;

            return true;
        }
    }
}
