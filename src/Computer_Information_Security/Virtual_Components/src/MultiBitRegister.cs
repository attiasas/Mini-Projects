using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Components
{
    // this class is a multi bit Register
    // implemented by assaf attias
    class MultiBitRegister : Gate
    {
        public WireSet Input { get; private set; }
        public WireSet Output { get; private set; }
        public Wire Load { get; private set; }
        public int Size { get; private set; }

        private SingleBitRegister[] bitRegisters;

        public MultiBitRegister(int iSize)
        {
            Size = iSize;
            Input = new WireSet(Size);
            Output = new WireSet(Size);
            Load = new Wire();

            bitRegisters = new SingleBitRegister[Size];

            for(int i = 0; i < bitRegisters.Length; i++)
            {
                //Init
                bitRegisters[i] = new SingleBitRegister();

                //Connect Input
                bitRegisters[i].ConnectInput(Input[i]);

                //Connect Load
                bitRegisters[i].ConnectLoad(Load);

                //Connect Output
                Output[i].ConnectInput(bitRegisters[i].Output);
            }

        }

        public void ConnectInput(WireSet wsInput)
        {
            Input.ConnectInput(wsInput);
        }

        
        public override string ToString()
        {
            return Output.ToString();
        }

        //this method is used to test the gate.
        public override bool TestGate()
        {
            //Set Start
            Input.SetValue(1);
            Load.Value = 1;
            Clock.ClockDown();
            Clock.ClockUp();

            //Test
            Input.SetValue(3);
            Load.Value = 0;
            Clock.ClockDown();
            Clock.ClockUp();
            if (Output.GetValue() != 1) return false;

            Load.Value = 1;
            Clock.ClockDown();
            Clock.ClockUp();
            Input.SetValue(2);
            if (Output.GetValue() != 3) return false;

            Load.Value = 0;
            Clock.ClockDown();
            Clock.ClockUp();
            if (Output.GetValue() != 3) return false;

            Load.Value = 1;
            Clock.ClockDown();
            Clock.ClockUp();
            Input.SetValue(1);
            if (Output.GetValue() != 2) return false;

            return true;
        }
    }
}
