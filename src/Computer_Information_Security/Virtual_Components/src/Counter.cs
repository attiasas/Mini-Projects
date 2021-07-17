using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Components
{
    // this class is a Counter
    // implemented by assaf attias
    class Counter : Gate
    {
        public WireSet Input { get; private set; }
        public WireSet Output { get; private set; }
        public Wire Load { get; private set; }
        public int Size { get; private set; }

        private BitwiseMux muxGate;
        private MultiBitRegister register;
        private MultiBitAdder adder;
        private WireSet fixedOneValueWire;
        
        public Counter(int iSize)
        {
            Size = iSize;
            Input = new WireSet(Size);
            Output = new WireSet(Size);
            Load = new Wire();

            //Init
            fixedOneValueWire = new WireSet(Size);
            fixedOneValueWire.SetValue(1);

            muxGate = new BitwiseMux(Size);
            register = new MultiBitRegister(Size);
            adder = new MultiBitAdder(Size);

            // Connect Mux Gate
            muxGate.ConnectInput1(adder.Output);
            muxGate.ConnectInput2(Input);
            muxGate.ConnectControl(Load);

            // Connect Register
            register.ConnectInput(muxGate.Output);
            register.Load.ConnectInput(fixedOneValueWire[0]);

            // Connect Adder
            adder.ConnectInput1(register.Output);
            adder.ConnectInput2(fixedOneValueWire);

            // Connect Output
            Output.ConnectInput(register.Output);

        }

        public void ConnectInput(WireSet ws)
        {
            Input.ConnectInput(ws);
        }
        public void ConnectLoad(Wire w)
        {
            Load.ConnectInput(w);
        }

        public override string ToString()
        {
            return Output.ToString();
        }

        //this method is used to test the gate.
        public override bool TestGate()
        {

            //Set
            Load.Value = 1;
            Input.SetValue(0);

            Clock.ClockDown();
            Clock.ClockUp();
            Clock.ClockDown();
            Clock.ClockUp();
            if (Output.GetValue() != 0) return false;

            Load.Value = 0;

            for (int i = 0; i < (int)Math.Pow(2, Size); i++)
            {
                if (Output.GetValue() != i) return false;
                Clock.ClockDown();
                Clock.ClockUp();
            }
            
            return true;
        }
    }
}
