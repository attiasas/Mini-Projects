using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Components
{
    // this class is a ALU
    // implemented by assaf attias
    class ALU : Gate
    {
        public WireSet InputX { get; private set; }
        public WireSet InputY { get; private set; }
        public WireSet Output { get; private set; }

        public Wire ZeroX { get; private set; }
        public Wire ZeroY { get; private set; }
        public Wire NotX { get; private set; }
        public Wire NotY { get; private set; }
        public Wire F { get; private set; }
        public Wire NotOutput { get; private set; }

        public Wire Zero { get; private set; }
        public Wire Negative { get; private set; }

        public int Size { get; private set; }

        private WireSet zeroWires;
        private BitwiseMux zxMux;
        private BitwiseMux zyMux;

        private BitwiseNotGate nxNot;
        private BitwiseNotGate nyNot;
        private BitwiseMux nxMux;
        private BitwiseMux nyMux;

        private BitwiseAndGate andGate;
        private MultiBitAdder adder;
        private BitwiseMux fMux;

        private BitwiseNotGate noNot;
        private BitwiseMux noMux;

        private BitwiseNotGate zrNot;
        private MultiBitAndGate zrMultiAnd;

        public ALU(int iSize)
        {
            Size = iSize;

            InputX = new WireSet(Size);
            InputY = new WireSet(Size);
            Output = new WireSet(Size);

            ZeroX = new Wire();
            ZeroY = new Wire();
            NotX = new Wire();
            NotY = new Wire();
            F = new Wire();
            NotOutput = new Wire();

            Negative = new Wire();            
            Zero = new Wire();

            // Init
            zeroWires = new WireSet(Size);
            zeroWires.SetValue(0);

            zxMux = new BitwiseMux(Size);
            zyMux = new BitwiseMux(Size);
            nxMux = new BitwiseMux(Size);
            nyMux = new BitwiseMux(Size);
            fMux = new BitwiseMux(Size);
            noMux = new BitwiseMux(Size);

            nxNot = new BitwiseNotGate(Size);
            nyNot = new BitwiseNotGate(Size);
            noNot = new BitwiseNotGate(Size);
            zrNot = new BitwiseNotGate(Size);
            andGate = new BitwiseAndGate(Size);
            zrMultiAnd = new MultiBitAndGate(Size);

            adder = new MultiBitAdder(Size);

            // Connect Zx and Zy
            zxMux.ConnectInput1(InputX);
            zxMux.ConnectInput2(zeroWires);
            zxMux.ConnectControl(ZeroX);

            zyMux.ConnectInput1(InputY);
            zyMux.ConnectInput2(zeroWires);
            zyMux.ConnectControl(ZeroY);

            // Connect Nx and Ny
            nxNot.ConnectInput(zxMux.Output);
            nxMux.ConnectInput1(zxMux.Output);
            nxMux.ConnectInput2(nxNot.Output);
            nxMux.ConnectControl(NotX);

            nyNot.ConnectInput(zyMux.Output);
            nyMux.ConnectInput1(zyMux.Output);
            nyMux.ConnectInput2(nyNot.Output);
            nyMux.ConnectControl(NotY);

            // Compute 'And' and 'Adder' and Connect F
            andGate.ConnectInput1(nxMux.Output);
            andGate.ConnectInput2(nyMux.Output);
            adder.ConnectInput1(nxMux.Output);
            adder.ConnectInput2(nyMux.Output);

            fMux.ConnectInput1(andGate.Output);
            fMux.ConnectInput2(adder.Output);
            fMux.ConnectControl(F);

            // Connect No
            noNot.ConnectInput(fMux.Output);
            noMux.ConnectInput1(fMux.Output);
            noMux.ConnectInput2(noNot.Output);
            noMux.ConnectControl(NotOutput);

            // Connect Negative, Zero and Output
            Negative.ConnectInput(noMux.Output[Size-1]);

            zrNot.ConnectInput(noMux.Output);
            zrMultiAnd.ConnectInput(zrNot.Output);
            Zero.ConnectInput(zrMultiAnd.Output);

            Output.ConnectInput(noMux.Output);
        }

        //this method is used to test the gate.
        public override bool TestGate()
        {
            int x = 2;
            int y = 4;

            InputX.SetValue(x);
            InputY.SetValue(y);

            WireSet control = new WireSet(6);
            WireSet notX = new WireSet(Size);
            WireSet notY = new WireSet(Size);
            BitwiseAndGate XandY = new BitwiseAndGate(Size);
            BitwiseOrGate XorY = new BitwiseOrGate(Size);
            XandY.ConnectInput1(InputX);
            XandY.ConnectInput2(InputY);
            XorY.ConnectInput1(InputX);
            XorY.ConnectInput2(InputY);

            for (int i = 0; i < Size; i++)
            {
                if (InputX[i].Value == 1) notX[i].Value = 0;
                else notX[i].Value = 1;

                if (InputY[i].Value == 1) notY[i].Value = 0;
                else notY[i].Value = 1;
            }

            for (int test = 0; test < 18; test++)
            {
                //Set Test
                if (test == 0) control.SetValue(21);
                if (test == 1) control.SetValue(63);
                if (test == 2) control.SetValue(23);
                if (test == 3) control.SetValue(12);
                if (test == 4) control.SetValue(3);
                if (test == 5) control.SetValue(44);
                if (test == 6) control.SetValue(35);
                if (test == 7) control.SetValue(60);
                if (test == 8) control.SetValue(51);
                if (test == 9) control.SetValue(62);
                if (test == 10) control.SetValue(59);
                if (test == 11) control.SetValue(28);
                if (test == 12) control.SetValue(19);
                if (test == 13) control.SetValue(16);
                if (test == 14) control.SetValue(50);
                if (test == 15) control.SetValue(56);
                if (test == 16) control.SetValue(0);
                if (test == 17) control.SetValue(42);


                //Connect Control
                ZeroX.Value = control[0].Value;
                NotX.Value = control[1].Value;
                ZeroY.Value = control[2].Value;
                NotY.Value = control[3].Value;
                F.Value = control[4].Value;
                NotOutput.Value = control[5].Value;

                //Test
                if (test == 0 && Output.Get2sComplement() != 0 && Zero.Value != 1 && Negative.Value != 0) return false;
                if (test == 1 && Output.Get2sComplement() != 1 && Zero.Value != 0 && Negative.Value != 0) return false;
                if (test == 2 && Output.Get2sComplement() != -1 && Zero.Value != 0 && Negative.Value != 1) return false;
                if (test == 3 && Output.Get2sComplement() != x && Zero.Value != 0 && Negative.Value != 0) return false;
                if (test == 4 && Output.Get2sComplement() != y && Zero.Value != 0 && Negative.Value != 0) return false;
                if (test == 5 && Output.Get2sComplement() != notX.Get2sComplement()) return false;
                if (test == 6 && Output.Get2sComplement() != notY.Get2sComplement()) return false;
                if (test == 7 && Output.Get2sComplement() != -x && Zero.Value != 0 && Negative.Value != 1) return false;
                if (test == 8 && Output.Get2sComplement() != -y && Zero.Value != 0 && Negative.Value != 1) return false;
                if (test == 9 && Output.Get2sComplement() != (x + 1) && Zero.Value != 0 && Negative.Value != 0) return false;
                if (test == 10 && Output.Get2sComplement() != (y + 1) && Zero.Value != 0 && Negative.Value != 0) return false;
                if (test == 11 && Output.Get2sComplement() != (x - 1) && Zero.Value != 0 && Negative.Value != 0) return false;
                if (test == 12 && Output.Get2sComplement() != (y - 1) && Zero.Value != 0 && Negative.Value != 0) return false;
                if (test == 13 && Output.Get2sComplement() != (x + y) && Zero.Value != 0 && Negative.Value != 0) return false;
                if (test == 14 && Output.Get2sComplement() != (x - y) && Zero.Value != 0 && Negative.Value != 1) return false;
                if (test == 15 && Output.Get2sComplement() != (y - x) && Zero.Value != 0 && Negative.Value != 0) return false;
                if (test == 16 && Output.Get2sComplement() != XandY.Output.Get2sComplement()) return false;
                if (test == 17 && Output.Get2sComplement() != XorY.Output.Get2sComplement()) return false;
            }
            
            return true;
        }
    }
}
