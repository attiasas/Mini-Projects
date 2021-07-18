using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Components
{
    abstract class MultiBitGate : Gate
    {
        protected WireSet m_wsInput;
        public Wire Output { get; protected set; }

        public MultiBitGate(int iInputCount)
        {
            m_wsInput = new WireSet(iInputCount);
            Output = new Wire();
        }

        public void ConnectInput(WireSet ws)
        {
            m_wsInput.ConnectInput(ws);
        }
    }
}
