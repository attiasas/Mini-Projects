using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Components
{
    // this class is a Mux Gate for bitwise use
    // implemented by assaf attias
    class BitwiseMux : BitwiseTwoInputGate
    {
        public Wire ControlInput { get; private set; }

        private MuxGate[] muxGates;

        public BitwiseMux(int iSize)
            : base(iSize)
        {
            ControlInput = new Wire();
            muxGates = new MuxGate[Size];

            for (int i = 0; i < Size; i++)
            {
                //init the gates
                muxGates[i] = new MuxGate();
                //connect the mux gate
                muxGates[i].ConnectInput1(Input1[i]);
                muxGates[i].ConnectInput2(Input2[i]);
                muxGates[i].ConnectControl(ControlInput);
                //set the output of the mux gate
                Output[i].ConnectInput(muxGates[i].Output);
            }

        }

        public void ConnectControl(Wire wControl)
        {
            ControlInput.ConnectInput(wControl);
        }

        public override string ToString()
        {
            return "BitwiseMux " + Input1 + "," + Input2 + ",C" + ControlInput + " -> " + Output;
        }

        //this method is used to test the gate.
        public override bool TestGate()
        {
            for (int i = 0; i < Size; i++)
            {
                Input1[i].Value = 0;
                Input2[i].Value = 1;
                ControlInput.Value = 0;
                if (Output[i].Value != 0) return false;
                
                Input1[i].Value = 1;
                Input2[i].Value = 0;
                ControlInput.Value = 0;
                if (Output[i].Value != 1) return false;
                
                Input1[i].Value = 0;
                Input2[i].Value = 1;
                ControlInput.Value = 1;
                if (Output[i].Value != 1) return false;
                
                Input1[i].Value = 1;
                Input2[i].Value = 0;
                ControlInput.Value = 1;
                if (Output[i].Value != 0) return false;
                
            }

            return true;
        }
    }
}
