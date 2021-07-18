using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Components
{
    // this class is a And Gate for multibit use control and bitwise wires
    // implemented by assaf attias
    class BitwiseMultiwayMux : Gate
    {
        public int Size { get; private set; }
        public int ControlBits { get; private set; }
        public WireSet Output { get; private set; }
        public WireSet Control { get; private set; }
        public WireSet[] Inputs { get; private set; }

        private BitwiseMux[][] muxGates;

        public BitwiseMultiwayMux(int iSize, int cControlBits)
        {
            Size = iSize;
            Output = new WireSet(Size);
            Control = new WireSet(cControlBits);
            Inputs = new WireSet[(int)Math.Pow(2, cControlBits)];
            
            for (int i = 0; i < Inputs.Length; i++) Inputs[i] = new WireSet(Size);
            
            muxGates = new BitwiseMux[cControlBits][];

            int muxCount = Inputs.Length / 2;
            for(int i =  0; i < muxGates.Length; i++)
            {
                muxGates[i] = new BitwiseMux[muxCount];
                for (int j = 0; j < muxGates[i].Length; j++) muxGates[i][j] = new BitwiseMux(Size);
                muxCount = muxCount / 2;
            }

            for (int c = 0; c < muxGates.Length; c++)
            {
               
                if (c == 0)
                {
                    for (int gate = 0; gate < muxGates[c].Length; gate++)
                    {
                        int inputConnectControl = gate * 2;

                        muxGates[c][gate].ConnectInput1(Inputs[inputConnectControl]);
                        muxGates[c][gate].ConnectInput2(Inputs[inputConnectControl + 1]);
                        muxGates[c][gate].ConnectControl(Control[c]);
                    }
                }
                else
                {
                    for (int gate = 0; gate < muxGates[c].Length; gate++)
                    {
                        int inputConnectControl = gate * 2;

                        muxGates[c][gate].ConnectInput1(muxGates[c - 1][inputConnectControl].Output);
                        muxGates[c][gate].ConnectInput2(muxGates[c - 1][inputConnectControl + 1].Output);
                        muxGates[c][gate].ConnectControl(Control[c]);
                    }
                }
            }
            
            Output.ConnectInput(muxGates[muxGates.Length-1][0].Output);
           
        }

        public void ConnectInput(int i, WireSet wsInput)
        {
            Inputs[i].ConnectInput(wsInput);
        }
        public void ConnectControl(WireSet wsControl)
        {
            Control.ConnectInput(wsControl);
        }

        public override string ToString()
        {
            String res = "Multi: " + Inputs + "C " + Control + " -> " + Output;
            Console.Write("Control: ");
            for (int j = 0; j < Control.Size; j++)
            {
                Console.Write("| i=" + j + " [" + Control[j].Value + "] |");
            }
            Console.WriteLine("");
            Console.WriteLine("Input Size:  " + Inputs.Length);
            Console.WriteLine("---------------------------------");
            Console.WriteLine("Inputs:");

            for (int j = 0; j < Inputs.Length; j++)
            {
                Console.Write("row: " + j + " | ");
                for (int k = 0; k < Inputs[j].Size; k++)
                {
                    Console.Write(Inputs[j][k] + " ");
                }
                Console.WriteLine("");
            }

            Console.WriteLine("---------------------------------");
            Console.WriteLine("---------------------------------");
            Console.WriteLine("OutPut:");

            for (int j = 0; j < Output.Size; j++)
            {
                Console.Write(Output[j] + " ");
            }
            Console.WriteLine("");
            Console.WriteLine("---------------------------------");
            Console.WriteLine("---------------------------------");
            for (int c = 0; c < muxGates.Length; c++)
            {
                for (int gate = 0; gate < muxGates[c].Length; gate++)
                {
                    Console.WriteLine("in c: " + c + " in gate: " + gate + " the class is: " + muxGates[c][gate]);
                }
            }
            Console.WriteLine("---------------------------------");

            return res;
        }

        //this method is used to test the gate.
        public override bool TestGate()
        {
            for (int test = 0; test < Inputs.Length; test++)
            {
                // Init
                for (int i = 0; i < Inputs.Length; i++)
                {
                    for (int j = 0; j < Size; j++)
                    {
                        if (i == test) Inputs[i][j].Value = 1;
                        else Inputs[i][j].Value = 0;
                    }
                }

                int trackNum = test;
                for (int i = 0; i < Control.Size; i++)
                {
                    if (trackNum % 2 == 0) Control[i].Value = 0;
                    else Control[i].Value = 1;

                    trackNum = trackNum / 2;
                }

                // test
                for (int i = 0; i < Size; i++)
                {
                    if (Output[i].Value != 1) return false;
                }
            }

            return true;
        }
    }
}
