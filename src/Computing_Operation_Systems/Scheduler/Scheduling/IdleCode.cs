﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Scheduling
{
    class IdleCode : Code
    {
        public IdleCode() : base()
        {
            //your code here
            m_lLines.Add("yield");
            m_lLines.Add("goto 0");
        }
    }
}
