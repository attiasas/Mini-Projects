using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Components
{
    // this class is a Or Gate for multibit use
    // implemented by assaf attias
    class MultiBitOrGate : MultiBitGate
    {
        private OrGate orGate1;
        private OrGate orGate2;
        private bool gate;

        public MultiBitOrGate(int iInputCount)
            : base(iInputCount)
        {
            //init
            orGate1 = new OrGate();
            orGate1.ConnectInput1(m_wsInput[0]);
            orGate1.ConnectInput2(m_wsInput[1]);

            for (int i = 2; i < m_wsInput.Size; i++)
            {
                if (gate)
                {
                    //init the gates
                    orGate1 = new OrGate();
                    //connect the or gate
                    orGate1.ConnectInput1(m_wsInput[i]);
                    orGate1.ConnectInput2(orGate2.Output);

                    gate = false;
                }
                else
                {
                    //init the gates
                    orGate2 = new OrGate();
                    //connect the or gate
                    orGate2.ConnectInput1(m_wsInput[i]);
                    orGate2.ConnectInput2(orGate1.Output);

                    gate = true;
                }

            }

            //set the output of the or gate
            if (gate) Output.ConnectInput(orGate2.Output);
            if (!gate) Output.ConnectInput(orGate1.Output);

        }

        //this method is used to test the gate.
        public override bool TestGate()
        {
            // true test
            for (int test = 0; test < m_wsInput.Size; test++)
            {
                for (int i = 0; i < m_wsInput.Size; i++)
                {
                    m_wsInput[i].Value = 0;
                    if (i == test) m_wsInput[i].Value = 1;
                }

                if (Output.Value != 1) return false;
            }

            // false test
            for (int i = 0; i < m_wsInput.Size; i++)
            {
                m_wsInput[i].Value = 0;
            }
            if (Output.Value != 0) return false;

            return true;
        }
    }
}
