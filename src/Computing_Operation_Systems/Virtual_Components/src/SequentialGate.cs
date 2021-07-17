using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Components
{
    abstract class SequentialGate
    {
        public SequentialGate()
        {
            Clock.RegisterSequentialGate(this);
        }
        public abstract void OnClockUp();
        public abstract void OnClockDown();

        public abstract bool TestGate();
    }
}
