using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Components
{
    // this class is a Memory unit
    // implemented by assaf attias
    class Memory : SequentialGate
    {
        public int AddressSize { get; private set; }
        public int WordSize { get; private set; }

        public WireSet Input { get; private set; }
        public WireSet Output { get; private set; }
        public WireSet Address { get; private set; }
        public Wire Load { get; private set; }

        private WireSet readWriteWire;
        private BitwiseMultiwayDemux multiDemux;
        private BitwiseMultiwayMux multiMux;
        private MultiBitRegister[] registers;

        public Memory(int iAddressSize, int iWordSize)
        {
            AddressSize = iAddressSize;
            WordSize = iWordSize;

            Input = new WireSet(WordSize);
            Output = new WireSet(WordSize);
            Address = new WireSet(AddressSize);
            Load = new Wire();

            //Init
            multiDemux = new BitwiseMultiwayDemux(1, AddressSize);
            multiMux = new BitwiseMultiwayMux(WordSize, AddressSize);
            registers = new MultiBitRegister[(int)Math.Pow(2, AddressSize)];

            readWriteWire = new WireSet(1);
            readWriteWire[0].ConnectInput(Load);

            //Connect Load
            multiDemux.ConnectInput(readWriteWire);

            //Connect Registers and Mux
            for(int i = 0; i < registers.Length; i++)
            {
                //Init
                registers[i] = new MultiBitRegister(WordSize);
                //Connect Input
                registers[i].ConnectInput(Input);
                //Connect Load to Output of demux
                registers[i].Load.ConnectInput(multiDemux.Outputs[i][0]);

                //Connect Output to Mux
                multiMux.ConnectInput(i, registers[i].Output);
            }

            //Connect Address
            multiDemux.ConnectControl(Address);
            multiMux.ConnectControl(Address);

            //Connect Output
            Output.ConnectInput(multiMux.Output);
        }

        public void ConnectInput(WireSet wsInput)
        {
            Input.ConnectInput(wsInput);
        }
        public void ConnectAddress(WireSet wsAddress)
        {
            Address.ConnectInput(wsAddress);
        }


        public override void OnClockUp()
        {
        }

        public override void OnClockDown()
        {
        }

        public override string ToString()
        {
            /*
            Console.WriteLine("--Memory----------------");
            Console.WriteLine("Read(0)/write(1): " + Load.Value);
            Console.WriteLine("current Address: " + Address + " (" + Address.GetValue() + ")");
            Console.WriteLine("Input: " + Input);
            Console.WriteLine("Output: " + Output);
            Console.WriteLine("-----------");
            for (int i = 0; i < registers.Length;i++)
            {
                Console.WriteLine("" + i + ". " + registers[i]);
            }
            Console.WriteLine("------------------------");
            */
            throw new NotImplementedException();
        }

        //this method is used to test the gate.
        public override bool TestGate()
        {
            //Set Values in All Address
            Load.Value = 1;
            for(int i = 0; i < registers.Length; i++)
            {
                Address.SetValue(i);
                Input.SetValue(i);
                Clock.ClockDown();
                Clock.ClockUp();
            }
            Load.Value = 0;
            Clock.ClockDown();
            Clock.ClockUp();

            // Read And Test
            for (int i = 0; i < registers.Length; i++)
            {
                Address.SetValue(i);
                if (Output.GetValue() != i) return false;
            }
            Clock.ClockDown();
            Clock.ClockUp();

            //Change 0 and 1 address
            Load.Value = 1;
            Address.SetValue(0);
            Input.SetValue(1);
            Clock.ClockDown();
            Clock.ClockUp();

            Address.SetValue(1);
            Input.SetValue(0);
            Clock.ClockDown();
            Clock.ClockUp();
            Load.Value = 0;

            // Read And Test
            for (int i = 0; i < registers.Length; i++)
            {
                Address.SetValue(i);

                if (i == 0) if (Output.GetValue() != 1) return false;
                if (i == 1) if (Output.GetValue() != 0) return false;
                if (i != 0 && i != 1) if (Output.GetValue() != i) return false;

                Clock.ClockDown();
                Clock.ClockUp();
            }

            return true;
        }
    }
}
