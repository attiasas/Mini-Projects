using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Components
{
    // this class is a MultiBitAdder
    // implemented by assaf attias
    class MultiBitAdder : Gate
    {
        public int Size { get; private set; }
        public WireSet Input1 { get; private set; }
        public WireSet Input2 { get; private set; }
        public WireSet Output { get; private set; }
        public Wire Overflow { get; private set; }

        private HalfAdder halfAdder;
        private FullAdder[] fullAdders;

        public MultiBitAdder(int iSize)
        {
            Size = iSize;
            Input1 = new WireSet(Size);
            Input2 = new WireSet(Size);
            Output = new WireSet(Size);
            Overflow = new Wire();

            //Init
            halfAdder = new HalfAdder();
            fullAdders = new FullAdder[Size - 1];
            for (int i = 0; i < fullAdders.Length; i++) fullAdders[i] = new FullAdder();

            // Connect First
            halfAdder.ConnectInput1(Input1[0]);
            halfAdder.ConnectInput2(Input2[0]);
            Output[0].ConnectInput(halfAdder.Output);

            // Connect Overflow
            if (Size == 1)
            {
                Overflow.ConnectInput(halfAdder.CarryOutput);
            }
            else
            {
                Overflow.ConnectInput(fullAdders[Size - 2].CarryOutput);
                fullAdders[0].CarryInput.ConnectInput(halfAdder.CarryOutput);
            }
            
            
            // Connect Middle
            for (int i = 0; i < Size - 1; i++)
            {
                fullAdders[i].ConnectInput1(Input1[i + 1]);
                fullAdders[i].ConnectInput2(Input2[i + 1]);
                if (i != 0) fullAdders[i].CarryInput.ConnectInput(fullAdders[i-1].CarryOutput);

                Output[i + 1].ConnectInput(fullAdders[i].Output);
            }

        }

        public override string ToString()
        {
            return Input1 + "(" + Input1.Get2sComplement() + ")" + " + " + Input2 + "(" + Input2.Get2sComplement() + ")" + " = " + Output + "(" + Output.Get2sComplement() + ")";
        }

        public void ConnectInput1(WireSet wInput)
        {
            Input1.ConnectInput(wInput);
        }
        public void ConnectInput2(WireSet wInput)
        {
            Input2.ConnectInput(wInput);
        }

        //this method is used to test the gate.
        public override bool TestGate()
        {
            int numOfOptions = (int)((Math.Pow(2, Size)) / 2);
            for (int i = -numOfOptions; i < numOfOptions; i++)
            {
                for (int j = -numOfOptions; j < numOfOptions; j++)
                {
                    if ((i + j) < -numOfOptions || (i + j) >= numOfOptions) continue;
                    Input1.Set2sComplement(i);
                    Input2.Set2sComplement(j);
                    if (Output.Get2sComplement() != (i + j)) return false;
                    if (Input1[Size - 1].Value == 1 && Input2[Size - 1].Value == 1 && Overflow.Value != 1) return false;
                }
            }

            return true;
        }
    }
}
