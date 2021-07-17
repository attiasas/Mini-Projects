using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Components
{
    // this class is a And Gate for multibit use
    // implemented by assaf attias
    class MultiBitAndGate : MultiBitGate
    {
        private AndGate andGate1;
        private AndGate andGate2;
        private bool gate;

        public MultiBitAndGate(int iInputCount)
            : base(iInputCount)
        {
            //init
            andGate1 = new AndGate();
            andGate1.ConnectInput1(m_wsInput[0]);
            andGate1.ConnectInput2(m_wsInput[1]);

            for (int i = 2; i < m_wsInput.Size; i++)
            {
                if(gate)
                {
                    //init the gates
                    andGate1 = new AndGate();
                    //connect the and gate
                    andGate1.ConnectInput1(m_wsInput[i]);
                    andGate1.ConnectInput2(andGate2.Output);

                    gate = false;
                }
                else
                {
                    //init the gates
                    andGate2 = new AndGate();
                    //connect the and gate
                    andGate2.ConnectInput1(m_wsInput[i]);
                    andGate2.ConnectInput2(andGate1.Output);

                    gate = true;
                }
                
            }

            //set the output of the and gate
            if (gate) Output.ConnectInput(andGate2.Output);
            if (!gate) Output.ConnectInput(andGate1.Output);
        }

        //this method is used to test the gate.
        public override bool TestGate()
        {
            // false test
            for (int test = 0; test < m_wsInput.Size; test++)
            {
                for (int i = 0; i < m_wsInput.Size; i++)
                {
                    m_wsInput[i].Value = 1;
                    if(i == test) m_wsInput[i].Value = 0;
                }

                if(Output.Value != 0) return false;
            }

            // true test
            for (int i = 0; i < m_wsInput.Size; i++)
            {
                m_wsInput[i].Value = 1;
            }
            if (Output.Value != 1) return false;

            return true;
        }
    }
}
