using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Components
{
    class Program
    {
         static void Main(string[] args)
        {
            #region Ass 1.1 - Test 1
            // Init
            AndGate and = new AndGate();
            NAndGate nand = new NAndGate();
            XorGate xor = new XorGate();

            BitwiseAndGate bitAnd = new BitwiseAndGate(17);
            BitwiseOrGate bitOr = new BitwiseOrGate(17);
            BitwiseNotGate bitNot = new BitwiseNotGate(17);

            MultiBitAndGate multiAnd = new MultiBitAndGate(16);
            MultiBitOrGate multiOr = new MultiBitOrGate(16);

            
            // Test 1
            if (!and.TestGate())
            {
                Console.WriteLine("bugbug AndGate");
                Console.WriteLine(and);
            }
            if (!nand.TestGate())
            {
                Console.WriteLine("bugbug NAndGate");
                Console.WriteLine(nand);
            }
            if (!xor.TestGate())
            {
                Console.WriteLine("bugbug XorGate");
                Console.WriteLine(xor);
            }
            if (!bitAnd.TestGate())
            {
                Console.WriteLine("bugbug BitwiseAndGate");
                Console.WriteLine(bitAnd);
            }
            if (!bitOr.TestGate())
            {
                Console.WriteLine("bugbug BitwiseOrGate");
                Console.WriteLine(bitOr);
            }
            if (!bitNot.TestGate())
            {
                Console.WriteLine("bugbug BitwiseNotGate");
                Console.WriteLine(bitNot);
            }
            if (!multiAnd.TestGate())
            {
                Console.WriteLine("bugbug MultiBitAndGate");
                Console.WriteLine(multiAnd);
            }
            if (!multiOr.TestGate())
            {
                Console.WriteLine("bugbug MultiBitOrGate");
                Console.WriteLine(multiOr);
            }
            #endregion

            #region Ass 1.2 - Test 1
            // Init
            MuxGate muxGate = new MuxGate();
            Demux demuxGate = new Demux();
            BitwiseMux bitMux = new BitwiseMux(4);
            BitwiseDemux bitDemux = new BitwiseDemux(13);
            BitwiseMultiwayMux multiwayMux = new BitwiseMultiwayMux(4, 4);
            BitwiseMultiwayDemux multiwayDemux = new BitwiseMultiwayDemux(4, 4);

            // Test
            if (!muxGate.TestGate())
            {
                Console.WriteLine("bugbug muxGate");
                Console.WriteLine(muxGate);
            }
            if (!demuxGate.TestGate())
            {
                Console.WriteLine("bugbug Demux");
                Console.WriteLine(demuxGate);
            }
            if (!bitMux.TestGate())
            {
                Console.WriteLine("bugbug BitwiseMux");
                Console.WriteLine(bitMux);
            }
            if (!bitDemux.TestGate())
            {
                Console.WriteLine("bugbug BitwiseDemux");
                Console.WriteLine(bitDemux);
            }
            if (!multiwayMux.TestGate())
            {
                Console.WriteLine("bugbug BitwiseMultiwayMux");
                Console.WriteLine(multiwayMux);
            }
            if (!multiwayDemux.TestGate())
            {
                Console.WriteLine("bugbug BitwiseMultiwayDemux");
                Console.WriteLine(multiwayDemux);
            }

            #endregion

            #region Ass 1.3 - Test 1

            // Init
            HalfAdder halfAdder = new HalfAdder();
            FullAdder fullAdder = new FullAdder();
            MultiBitAdder adder = new MultiBitAdder(4);
            ALU alu = new ALU(4);

            // Test
            if (!halfAdder.TestGate())
            {
                Console.WriteLine("bugbug HalfAdder");
                Console.WriteLine(halfAdder);
            }
            if (!fullAdder.TestGate())
            {
                Console.WriteLine("bugbug FullAdder");
                Console.WriteLine(fullAdder);
            }
            if (!adder.TestGate())
            {
                Console.WriteLine("bugbug MultiBitAdder");
                Console.WriteLine(adder);
            }
            if (!alu.TestGate())
            {
                Console.WriteLine("bugbug ALU");
                Console.WriteLine(alu);
            }

            #endregion

            #region Ass 1.4 - Test 1
            //Init
            SingleBitRegister bitRegister = new SingleBitRegister();
            MultiBitRegister multiBitRegister = new MultiBitRegister(4);
            Memory memory = new Memory(4, 4);
            Counter counter = new Counter(4);

            //Test
            if (!bitRegister.TestGate())
            {
                Console.WriteLine("bugbug SingleBitRegister");
                Console.WriteLine(bitRegister);
            }
            if (!multiBitRegister.TestGate())
            {
                Console.WriteLine("bugbug MultiBitRegister");
                Console.WriteLine(multiBitRegister);
            }
            if (!memory.TestGate())
            {
                Console.WriteLine("bugbug Memory");
                Console.WriteLine(memory);
            }
            if (!counter.TestGate())
            {
                Console.WriteLine("bugbug Counter");
                Console.WriteLine(counter);
            }

            #endregion

            OrGate.Corrupt = true;

            #region Ass 1.1 - Test 2
            // Test 2
            if (and.TestGate())
            {
                Console.WriteLine("bugbug AndGate (Corrupt)");
                Console.WriteLine(and);
            }
            if (nand.TestGate())
            {
                Console.WriteLine("bugbug NAndGate (Corrupt)");
                Console.WriteLine(nand);
            }
            if (xor.TestGate())
            {
                Console.WriteLine("bugbug XorGate (Corrupt)");
                Console.WriteLine(xor);
            }
            if (bitAnd.TestGate())
            {
                Console.WriteLine("bugbug BitwiseAndGate (Corrupt)");
                Console.WriteLine(bitAnd);
            }
            if (bitOr.TestGate())
            {
                Console.WriteLine("bugbug BitwiseOrGate (Corrupt)");
                Console.WriteLine(bitOr);
            }
            if (multiAnd.TestGate())
            {
                Console.WriteLine("bugbug MultiBitAndGate (Corrupt)");
                Console.WriteLine(multiAnd);
            }
            if (multiAnd.TestGate())
            {
                Console.WriteLine("bugbug MultiBitOrGate (Corrupt)");
                Console.WriteLine(multiOr);
            }
            #endregion

            #region Ass 1.2 - Test 2
            // Test
            if (muxGate.TestGate())
            {
                Console.WriteLine("bugbug muxGate (Corrupt)");
                Console.WriteLine(muxGate);
            }
            if (demuxGate.TestGate())
            {
                Console.WriteLine("bugbug Demux (Corrupt)");
                Console.WriteLine(demuxGate);
            }
            if (bitMux.TestGate())
            {
                Console.WriteLine("bugbug BitwiseMux (Corrupt)");
                Console.WriteLine(bitMux);
            }
            if (bitDemux.TestGate())
            {
                Console.WriteLine("bugbug BitwiseDemux (Corrupt)");
                Console.WriteLine(bitDemux);
            }
            if (multiwayMux.TestGate())
            {
                Console.WriteLine("bugbug BitwiseMultiwayMux (Corrupt)");
                Console.WriteLine(multiwayMux);
            }
            if (multiwayDemux.TestGate())
            {
                Console.WriteLine("bugbug BitwiseMultiwayDemux (Corrupt)");
                Console.WriteLine(multiwayDemux);
            }

            #endregion

            #region Ass 1.3 - Test 2
            // Test
            if (halfAdder.TestGate())
            {
                Console.WriteLine("bugbug HalfAdder (Corrupt)");
                Console.WriteLine(halfAdder);
            }
            if (fullAdder.TestGate())
            {
                Console.WriteLine("bugbug FullAdder (Corrupt)");
                Console.WriteLine(fullAdder);
            }
            if (adder.TestGate())
            {
                Console.WriteLine("bugbug MultiBitAdder (Corrupt)");
                Console.WriteLine(adder);
            }
            if (alu.TestGate())
            {
                Console.WriteLine("bugbug ALU (Corrupt)");
                Console.WriteLine(alu);
            }

            #endregion

            #region Ass 1.4 - Test 2
            if (bitRegister.TestGate())
            {
                Console.WriteLine("bugbug SingleBitRegister (Corrupt)");
                Console.WriteLine(bitRegister);
            }
            if (multiBitRegister.TestGate())
            {
                Console.WriteLine("bugbug MultiBitRegister (Corrupt)");
                Console.WriteLine(multiBitRegister);
            }
            if (memory.TestGate())
            {
                Console.WriteLine("bugbug Memory (Corrupt)");
                Console.WriteLine(memory);
            }
            if (counter.TestGate())
            {
                Console.WriteLine("bugbug Counter (Corrupt)");
                Console.WriteLine(counter);
            }

            #endregion

            #region Ass 1.5 - Test 1
            #endregion

            Console.WriteLine("done");
            Console.ReadLine();
        }
    }
}
