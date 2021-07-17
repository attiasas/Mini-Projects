using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Components
{
    // this class is a Demux Gate for bitwise use
    // implemented by assaf attias
    class BitwiseDemux : Gate
    {
        public int Size { get; private set; }
        public WireSet Output1 { get; private set; }
        public WireSet Output2 { get; private set; }
        public WireSet Input { get; private set; }
        public Wire Control { get; private set; }

        private Demux[] demuxGates;

        public BitwiseDemux(int iSize)
        {
            Size = iSize;
            Control = new Wire();
            Input = new WireSet(Size);
            Output1 = new WireSet(Size);
            Output2 = new WireSet(Size);

            demuxGates = new Demux[Size];

            for (int i = 0; i < Size; i++)
            {
                //init the gates
                demuxGates[i] = new Demux();
                //connect the demux gate
                demuxGates[i].ConnectInput(Input[i]);
                demuxGates[i].ConnectControl(Control);
                //set the output of the demux gate
                Output1[i].ConnectInput(demuxGates[i].Output1);
                Output2[i].ConnectInput(demuxGates[i].Output2);
            }
        }

        public void ConnectControl(Wire wControl)
        {
            Control.ConnectInput(wControl);
        }
        public void ConnectInput(WireSet wsInput)
        {
            Input.ConnectInput(wsInput);
        }

        //this method is used to test the gate.
        public override bool TestGate()
        {
            for (int i = 0; i < Size; i++)
            {
                Input[i].Value = 0;
                Control.Value = 0;
                if (Output1[i].Value != 0 && Output2[i].Value != 0) return false;

                Input[i].Value = 0;
                Control.Value = 1;
                if (Output1[i].Value != 0 && Output2[i].Value != 0) return false;

                Input[i].Value = 1;
                Control.Value = 0;
                if (Output1[i].Value != 1 && Output2[i].Value != 0) return false;

                Input[i].Value = 1;
                Control.Value = 1;
                if (Output1[i].Value != 0 && Output2[i].Value != 1) return false;

            }

            return true;
        }
    }
}
