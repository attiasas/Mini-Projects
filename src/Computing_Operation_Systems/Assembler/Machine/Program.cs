using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using SimpleComponents;

namespace Machine
{
    class Program
    {
        [STAThread]
        static void Main(string[] args)
        {
            Machine16 machine = new Machine16(false, true);
            machine.Code.LoadFromFile(".\Assembly examples\TestJumping.hack");
            machine.Data[0] = 100;
            machine.Data[1] = 15;
            DateTime dtStart = DateTime.Now;
            machine.Reset();
            for (int i = 0; i < 1000; i++)
            {
                machine.CPU.PrintState();
                Console.WriteLine();
                Clock.ClockDown();
                Clock.ClockUp();
            }
            Console.WriteLine("Done " + (DateTime.Now - dtStart).TotalSeconds);
            Console.ReadLine();
        }
    }
}
