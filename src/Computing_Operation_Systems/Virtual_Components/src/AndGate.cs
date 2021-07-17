using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Components
{
    //this class is provided as an example
    class AndGate : TwoInputGate
    {
        //we will use a nand and a not after it
        private NotGate m_gNotInput1;
        private NotGate m_gNotInput2;
        private NotGate m_gNotOutput;
        private OrGate m_gOR;

        public AndGate()
        {
            //init the gates
            m_gOR = new OrGate();
            m_gNotInput1 = new NotGate();
            m_gNotInput2 = new NotGate();
            m_gNotOutput = new NotGate();
            //wire the inputs to the not gates
            m_gNotInput1.ConnectInput(Input1);
            m_gNotInput2.ConnectInput(Input2);
            //connect the or gate
            m_gOR.ConnectInput1(m_gNotInput1.Output);
            m_gOR.ConnectInput2(m_gNotInput2.Output);
            //connect the not on the output
            m_gNotOutput.ConnectInput(m_gOR.Output);
            //set the  output of the and gate
            Output = m_gNotOutput.Output;
        }

        //an implementation of the ToString method is called, e.g. when we use Console.WriteLine(and)
        //this is very helpful during debugging
        public override string ToString()
        {
            return "And " + Input1.Value + "," + Input2.Value + " -> " + Output.Value;
        }

        //this method is used to test the gate. 
        //we simply check whether the truth table is properly implemented.
        public override bool TestGate()
        {
            Input1.Value = 0;
            Input2.Value = 0;
            if (Output.Value != 0)
                return false;
            Input1.Value = 0;
            Input2.Value = 1;
            if (Output.Value != 0)
                return false;
            Input1.Value = 1;
            Input2.Value = 0;
            if (Output.Value != 0)
                return false;
            Input1.Value = 1;
            Input2.Value = 1;
            if (Output.Value != 1)
                return false;
            return true;
        }
    }
}
