using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SimpleComponents;

namespace Machine
{
    public class CPU16 
    {
        //this "enum" defines the different control bits names
        public const int J3 = 0, J2 = 1, J1 = 2, D3 = 3, D2 = 4, D1 = 5, C6 = 6, C5 = 7, C4 = 8, C3 = 9, C2 = 10, C1 = 11, A = 12, X2 = 13, X1 = 14, Type = 15;

        public int Size { get; private set; }

        //CPU inputs
        public WireSet Instruction { get; private set; }
        public WireSet MemoryInput { get; private set; }
        public Wire Reset { get; private set; }

        //CPU outputs
        public WireSet MemoryOutput { get; private set; }
        public Wire MemoryWrite { get; private set; }
        public WireSet MemoryAddress { get; private set; }
        public WireSet InstructionAddress { get; private set; }

        //CPU components
        private ALU m_gALU;
        private Counter m_rPC;
        private MultiBitRegister m_rA, m_rD;
        private BitwiseMux m_gAMux, m_gMAMux;

        //here we initialize and connect all the components, as in Figure 5.9 in the book
        public CPU16()
        {
            Size =  16;

            Instruction = new WireSet(Size);
            MemoryInput = new WireSet(Size);
            MemoryOutput = new WireSet(Size);
            MemoryAddress = new WireSet(Size);
            InstructionAddress = new WireSet(Size);
            MemoryWrite = new Wire();
            Reset = new Wire();

            m_gALU = new ALU(Size);
            m_rPC = new Counter(Size);
            m_rA = new MultiBitRegister(Size);
            m_rD = new MultiBitRegister(Size);

            m_gAMux = new BitwiseMux(Size);
            m_gMAMux = new BitwiseMux(Size);

            m_gAMux.ConnectInput1(Instruction);
            m_gAMux.ConnectInput2(m_gALU.Output);

            m_rA.ConnectInput(m_gAMux.Output);

            m_gMAMux.ConnectInput1(m_rA.Output);
            m_gMAMux.ConnectInput2(MemoryInput);
            m_gALU.InputY.ConnectInput(m_gMAMux.Output);

            m_gALU.InputX.ConnectInput(m_rD.Output);

            m_rD.ConnectInput(m_gALU.Output);

            MemoryOutput.ConnectInput(m_gALU.Output);
            MemoryAddress.ConnectInput(m_rA.Output);

            InstructionAddress.ConnectInput(m_rPC.Output);
            m_rPC.ConnectInput(m_rA.Output);
            m_rPC.ConnectReset(Reset);

            //now, we call the code that creates the control unit
            ConnectControls();
        }

        //add here components to implement the control unit 
        private BitwiseMultiwayMux m_gJumpMux; //an example of a control unit compnent - a mux that controls whether a jump is made
        private AndGate m_rD_andGate;
        private NotGate m_rA_notGate;
        private OrGate m_rA_orGate;
        private AndGate m_Mem_andGate;

        private WireSet m_JumpMux_Load;
        private AndGate m_Jump_J1_andGate;
        private AndGate m_Jump_J2_andGate;
        private AndGate m_Jump_J3_andGate;

        private Wire m_fixedValue_Zero;

        private NotGate m_Jump_In_Not_Ng;
        private NotGate m_Jump_In_Not_Zero;
        private AndGate m_Jump_In_andGate;
        private OrGate m_Jump_In_orGate;

        private WireSet m_gJump_NoJump;
        private WireSet m_gJump_JGT;
        private WireSet m_gJump_JEQ;
        private WireSet m_gJump_JGE;
        private WireSet m_gJump_JLT;
        private WireSet m_gJump_JNE;
        private WireSet m_gJump_JLE;
        private WireSet m_gJump_JMP;

        private void ConnectControls()
        {
            //1. connect control of mux 1 (selects entrance to register A)
            m_gAMux.ConnectControl(Instruction[Type]);

            //2. connect control to mux 2 (selects A or M entrance to the ALU)
            m_gMAMux.ConnectControl(Instruction[A]);

            //3. consider all instruction bits only if C type instruction (MSB of instruction is 1)
            m_Jump_J1_andGate = new AndGate();
            m_rA_orGate = new OrGate();
            m_rA_notGate = new NotGate();
            m_rD_andGate = new AndGate();
            m_Mem_andGate = new AndGate();
            m_Jump_J1_andGate = new AndGate();
            m_Jump_J2_andGate = new AndGate();
            m_Jump_J3_andGate = new AndGate();

            //4. connect ALU control bits
            m_gALU.ZeroX.ConnectInput(Instruction[C1]);
            m_gALU.NotX.ConnectInput(Instruction[C2]);
            m_gALU.ZeroY.ConnectInput(Instruction[C3]);
            m_gALU.NotY.ConnectInput(Instruction[C4]);
            m_gALU.F.ConnectInput(Instruction[C5]);
            m_gALU.NotOutput.ConnectInput(Instruction[C6]);

            //5. connect control to register D (very simple)
            m_rD_andGate.ConnectInput1(Instruction[Type]);
            m_rD_andGate.ConnectInput2(Instruction[D2]);

            m_rD.Load.ConnectInput(m_rD_andGate.Output);

            //6. connect control to register A (a bit more complicated)
            m_rA_notGate.ConnectInput(Instruction[Type]);

            m_rA_orGate.ConnectInput1(m_rA_notGate.Output);
            m_rA_orGate.ConnectInput2(Instruction[D1]);

            m_rA.Load.ConnectInput(m_rA_orGate.Output);

            //7. connect control to MemoryWrite
            m_Mem_andGate.ConnectInput1(Instruction[Type]);
            m_Mem_andGate.ConnectInput2(Instruction[D3]);

            MemoryWrite.ConnectInput(m_Mem_andGate.Output);

            //8. create inputs for jump mux    
            m_fixedValue_Zero = new Wire();
            m_fixedValue_Zero.Value = 1;

            m_Jump_In_Not_Ng = new NotGate();
            m_Jump_In_Not_Ng.ConnectInput(m_gALU.Negative);
            m_Jump_In_Not_Zero = new NotGate();
            m_Jump_In_Not_Zero.ConnectInput(m_gALU.Zero);

            m_Jump_In_andGate = new AndGate();
            m_Jump_In_andGate.ConnectInput1(m_Jump_In_Not_Ng.Output);
            m_Jump_In_andGate.ConnectInput2(m_Jump_In_Not_Zero.Output);

            m_Jump_In_orGate = new OrGate();
            m_Jump_In_orGate.ConnectInput1(m_gALU.Negative);
            m_Jump_In_orGate.ConnectInput2(m_gALU.Zero);

            m_gJump_NoJump = new WireSet(1);
            m_gJump_NoJump[0].ConnectInput(m_fixedValue_Zero);
            m_gJump_JGT = new WireSet(1);
            m_gJump_JGT[0].ConnectInput(m_Jump_In_andGate.Output);
            m_gJump_JEQ = new WireSet(1);
            m_gJump_JEQ[0].ConnectInput(m_gALU.Zero);
            m_gJump_JGE = new WireSet(1);
            m_gJump_JGE[0].ConnectInput(m_Jump_In_Not_Ng.Output);
            m_gJump_JLT = new WireSet(1);
            m_gJump_JLT[0].ConnectInput(m_gALU.Negative);
            m_gJump_JNE = new WireSet(1);
            m_gJump_JNE[0].ConnectInput(m_Jump_In_Not_Zero.Output);
            m_gJump_JLE = new WireSet(1);
            m_gJump_JLE[0].ConnectInput(m_Jump_In_orGate.Output);
            m_gJump_JMP = new WireSet(1);
            m_gJump_JMP[0].ConnectInput(Instruction[Type]);

            //9. connect jump mux (this is the most complicated part)
            m_gJumpMux = new BitwiseMultiwayMux(1, 3);

                // Connect Input
            m_gJumpMux.ConnectInput(0, m_gJump_NoJump);
            m_gJumpMux.ConnectInput(1, m_gJump_JGT);
            m_gJumpMux.ConnectInput(2, m_gJump_JEQ);
            m_gJumpMux.ConnectInput(3, m_gJump_JGE);
            m_gJumpMux.ConnectInput(4, m_gJump_JLT);
            m_gJumpMux.ConnectInput(5, m_gJump_JNE);
            m_gJumpMux.ConnectInput(6, m_gJump_JLE);
            m_gJumpMux.ConnectInput(7, m_gJump_JMP);

                // Connect Load
            m_Jump_J1_andGate.ConnectInput1(Instruction[Type]);
            m_Jump_J1_andGate.ConnectInput2(Instruction[J1]);
            m_Jump_J2_andGate.ConnectInput1(Instruction[Type]);
            m_Jump_J2_andGate.ConnectInput2(Instruction[J2]);
            m_Jump_J3_andGate.ConnectInput1(Instruction[Type]);
            m_Jump_J3_andGate.ConnectInput2(Instruction[J3]);

            m_JumpMux_Load = new WireSet(3);
            m_JumpMux_Load[0].ConnectInput(m_Jump_J3_andGate.Output);
            m_JumpMux_Load[1].ConnectInput(m_Jump_J2_andGate.Output);
            m_JumpMux_Load[2].ConnectInput(m_Jump_J1_andGate.Output);

            m_gJumpMux.ConnectControl(m_JumpMux_Load);

            //10. connect PC load control
            m_rPC.ConnectLoad(m_gJumpMux.Output[0]);
        }

        public override string ToString()
        {
            return "A=" + m_rA + ", D=" + m_rD + ", PC=" + m_rPC + ",Ins=" + Instruction;
        }

        private string GetInstructionString()
        {
            if (Instruction[Type].Value == 0)
                return "@" + Instruction.GetValue();
            return Instruction[Type].Value + "XX " +
               "a" + Instruction[A] + " " +
               "c" + Instruction[C1] + Instruction[C2] + Instruction[C3] + Instruction[C4] + Instruction[C5] + Instruction[C6] + " " +
               "d" + Instruction[D1] + Instruction[D2] + Instruction[D3] + " " +
               "j" + Instruction[J1] + Instruction[J2] + Instruction[J3];
        }

        //use this function in debugging to print the current status of the ALU. Feel free to add more things for printing.
        public void PrintState()
        {
            Console.WriteLine("CPU state:");
            Console.WriteLine("PC=" + m_rPC + "=" + m_rPC.Output.GetValue());
            Console.WriteLine("A=" + m_rA + "=" + m_rA.Output.GetValue());
            Console.WriteLine("D=" + m_rD + "=" + m_rD.Output.GetValue());
            Console.WriteLine("Ins=" + GetInstructionString());
            Console.WriteLine("ALU=" + m_gALU);
            Console.WriteLine("inM=" + MemoryInput);
            Console.WriteLine("outM=" + MemoryOutput);
            Console.WriteLine("addM=" + MemoryAddress);
        }
    }
}
