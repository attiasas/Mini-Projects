using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Components
{
    //this gate is given to you fully implemented - you only have to use it in your code
    class OrGate : TwoInputGate, Component
    {
        public static bool Corrupt = false;
        public static int OR_COMPUTE = 0;
        public static int OR_GATES = 0;

        public OrGate()
        {
            OR_GATES++;
            Input1.ConnectOutput(this);
            Input2.ConnectOutput(this);
        }

        #region Component Members

        public void Compute()
        {
            OR_COMPUTE++;
            if (Corrupt)
                Output.Value = 0;
            else
            {
                if (Input1.Value == 1 || Input2.Value == 1)
                    Output.Value = 1;
                else
                    Output.Value = 0;
            }
        }

        #endregion

        public override string ToString()
        {
            return "Or " + Input1.Value + "," + Input2.Value + " -> " + Output.Value;
        }

        public override bool TestGate()
        {
            Input1.Value = 0;
            Input2.Value = 0;

            if (Output.Value != 0)
                return false;
            Input1.Value = 0;
            Input2.Value = 1;

            if (Output.Value != 1)
                return false;
            Input1.Value = 1;
            Input2.Value = 0;

            if (Output.Value != 1)
                return false;
            Input1.Value = 1;
            Input2.Value = 1;

            if (Output.Value != 1)
                return false;
            return true;
        }
    }

}
