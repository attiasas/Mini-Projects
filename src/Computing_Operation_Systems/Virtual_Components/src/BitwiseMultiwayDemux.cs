using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Components
{
    // this class is a And Gate for multibit use control and bitwise wires
    // implemented by assaf attias
    class BitwiseMultiwayDemux : Gate
    {
        public int Size { get; private set; }
        public int ControlBits { get; private set; }
        public WireSet Input { get; private set; }
        public WireSet Control { get; private set; }
        public WireSet[] Outputs { get; private set; }

        private BitwiseDemux[][] demuxGates;

        public BitwiseMultiwayDemux(int iSize, int cControlBits)
        {
            Size = iSize;
            Input = new WireSet(Size);
            Control = new WireSet(cControlBits);
            Outputs = new WireSet[(int)Math.Pow(2, cControlBits)];
            for (int i = 0; i < Outputs.Length; i++) Outputs[i] = new WireSet(Size);

            //Init
            demuxGates = new BitwiseDemux[cControlBits][];

            int demuxCount = 1;
            for (int i = 0; i < demuxGates.Length; i++)
            {
                demuxGates[i] = new BitwiseDemux[demuxCount];
                for (int j = 0; j < demuxGates[i].Length; j++) demuxGates[i][j] = new BitwiseDemux(Size);
                demuxCount = demuxCount * 2;
            }

            // Connect
            for (int c = 0; c < demuxGates.Length; c++)
            {

                if (c == 0)
                {
                    demuxGates[c][0].ConnectInput(Input);
                    demuxGates[c][0].ConnectControl(Control[Control.Size-1 - c]);
                }
                else
                {
                    for (int gate = 0; gate < demuxGates[c].Length; gate++)
                    {
                        int inputConnectControl = gate / 2;

                        if (gate % 2 == 0)
                        {
                            demuxGates[c][gate].ConnectInput(demuxGates[c - 1][inputConnectControl].Output1);
                        }
                        else
                        {
                            demuxGates[c][gate].ConnectInput(demuxGates[c - 1][inputConnectControl].Output2);
                        }

                        demuxGates[c][gate].ConnectControl(Control[Control.Size - 1 - c]);
                    }
                }
            }

            // Connect to output
            for (int i = 0; i < Outputs.Length; i++)
            {
                int inputConnectControl = i / 2;

                if (i % 2 == 0)
                {
                    Outputs[i].ConnectInput(demuxGates[demuxGates.Length - 1][inputConnectControl].Output1);
                }
                else
                {
                    Outputs[i].ConnectInput(demuxGates[demuxGates.Length - 1][inputConnectControl].Output2);
                }
            }

        }


        public void ConnectInput(WireSet wsInput)
        {
            Input.ConnectInput(wsInput);
        }
        public void ConnectControl(WireSet wsControl)
        {
            Control.ConnectInput(wsControl);
        }

        //this method is used to test the gate.
        public override bool TestGate()
        {
            for (int test = 0; test < Outputs.Length; test++)
            {
                // Init
                for (int i = 0; i < Input.Size; i++) Input[i].Value = 1;

                int trackNum = test;
                for (int i = 0; i < Control.Size; i++)
                {
                    if (trackNum % 2 == 0) Control[i].Value = 0;
                    else Control[i].Value = 1;

                    trackNum = trackNum / 2;
                }

                // test
                for (int i = 0; i < Outputs.Length; i++)
                {
                    for(int j = 0; j < Outputs[i].Size; j++)
                    {
                        if (i == test && Outputs[i][j].Value != 1) return false;

                        if (i != test && Outputs[i][j].Value != 0) return false;
                    }
                }
            }

            return true;

        }
    }
}
