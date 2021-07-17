using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Scheduling
{
    class RoundRobin : FirstComeFirstServedPolicy
    {
        protected int mQuantum;

        public RoundRobin(int iQuantum) : base()
        {
            mQuantum = iQuantum;
        }

        public override int NextProcess(Dictionary<int, ProcessTableEntry> dProcessTable)
        {
            int result = base.NextProcess(dProcessTable);

            if(result != -1) dProcessTable[result].Quantum = mQuantum;

            return result;
        }

        public override bool RescheduleAfterInterrupt()
        {
            return false;
        }
    }
}
