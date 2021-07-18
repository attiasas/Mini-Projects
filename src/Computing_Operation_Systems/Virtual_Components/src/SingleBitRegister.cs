using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Components
{
    // this class is a 1 bit Register
    // implemented by assaf attias
    class SingleBitRegister : Gate
    {
        public Wire Input { get; private set; }
        public Wire Output { get; private set; }
        public Wire Load { get; private set; }

        private MuxGate muxGate;
        private DFlipFlopGate flipFlopGate;

        public SingleBitRegister()
        {
            Input = new Wire();
            Output = new Wire();
            Load = new Wire();

            //Init
            muxGate = new MuxGate();
            flipFlopGate = new DFlipFlopGate();

            //Connect Input
            muxGate.ConnectInput2(Input);
            muxGate.ConnectControl(Load);

            //Connect DFF
            flipFlopGate.ConnectInput(muxGate.Output);
            muxGate.ConnectInput1(flipFlopGate.Output);

            //Connect Output
            Output.ConnectInput(flipFlopGate.Output);
        }

        public void ConnectInput(Wire wInput)
        {
            Input.ConnectInput(wInput);
        }

        public void ConnectLoad(Wire wLoad)
        {
            Load.ConnectInput(wLoad);
        }

        //this method is used to test the gate.
        public override bool TestGate()
        {
            //Set Start
            Input.Value = 1;
            Load.Value = 1;
            Clock.ClockDown();
            Clock.ClockUp();

            //Test
            Input.Value = 0;
            Load.Value = 0;
            Clock.ClockDown();
            Clock.ClockUp();
            Input.Value = 1;
            if (Output.Value != 1) return false;

            Input.Value = 0;
            Clock.ClockDown();
            Clock.ClockUp();
            if (Output.Value != 1) return false;

            Load.Value = 1;
            Clock.ClockDown();
            Clock.ClockUp();
            Input.Value = 1;
            if (Output.Value != 0) return false;

            Input.Value = 0;
            Clock.ClockDown();
            Clock.ClockUp();
            if (Output.Value != 0) return false;

            return true;
        }
    }
}
