using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Components
{
    //this class represents a set of wires (a cable)
    class WireSet
    {
        private Wire[] m_aWires;
        public int Size { get; private set; }
        public Boolean InputConected { get; private set; }
        public Wire this[int i]
        {
            get
            {
                return m_aWires[i];
            }
        }
        
        public WireSet(int iSize)
        {
            Size = iSize;
            InputConected = false;
            m_aWires = new Wire[iSize];
            for (int i = 0; i < m_aWires.Length; i++)
                m_aWires[i] = new Wire();
        }
        public override string ToString()
        {
            string s = "[";
            for (int i = m_aWires.Length - 1; i >= 0; i--)
                s += m_aWires[i].Value;
            s += "]";
            return s;
        }

        //transform a positive integer value into binary and set the wires accordingly, with 0 being the LSB
        public void SetValue(int iValue)
        {
            int trackNum = iValue;

            for (int i = 0; i < Size; i++)
            {
                if (trackNum % 2 == 0) m_aWires[i].Value = 0;
                else m_aWires[i].Value = 1;

                trackNum = trackNum / 2;
            }
        }

        //transform the binary code into a positive integer
        public int GetValue()
        {
            int res = 0;

            for(int i = 0; i < m_aWires.Length; i++)
            {
                if(m_aWires[i].Value == 1) res += (int)Math.Pow(2, i);
            }

            return res;
        }

        //transform an integer value into binary using 2`s complement and set the wires accordingly, with 0 being the LSB
        public void Set2sComplement(int iValue)
        {
            if (iValue >= 0)
            {
                SetValue(iValue);
            }
            else
            {
                // negative
                SetValue(-iValue);
                
                // NOT
                for (int i = 0; i < m_aWires.Length; i++)
                {
                    if (m_aWires[i].Value == 1) m_aWires[i].Value = 0;
                    else m_aWires[i].Value = 1;
                }
                
                // ADD 1
                int index = 0;
                while (index < m_aWires.Length && m_aWires[index].Value == 1)
                {
                    m_aWires[index].Value = 0;
                    index++;
                }
                if (index < m_aWires.Length) m_aWires[index].Value = 1;
                
            }
        }

        //transform the binary code in 2`s complement into an integer
        public int Get2sComplement()
        {
            if (m_aWires[m_aWires.Length-1].Value == 0)
            {
               return GetValue();
            }
            else
            {
                // negative
                int res = 0;
                int[] holder = new int[m_aWires.Length];
                for (int i = 0; i < holder.Length; i++) holder[i] = m_aWires[i].Value;

                // NOT
                for (int i = 0; i < holder.Length; i++)
                {
                    if (holder[i] == 1) holder[i] = 0;
                    else holder[i] = 1;
                }

                // ADD 1
                int index = 0;
                while (index < holder.Length && holder[index] == 1)
                {
                    holder[index] = 0;
                    index++;
                }
                if (index < holder.Length) holder[index] = 1;

                // Compute
                for (int i = 0; i < holder.Length; i++)
                {
                    if (holder[i] == 1) res += (int)Math.Pow(2, i);
                }

                return -res;
            }
        }

        public void ConnectInput(WireSet wIn)
        {
            if (InputConected)
                throw new InvalidOperationException("Cannot connect a wire to more than one inputs");
            if(wIn.Size != Size)
                throw new InvalidOperationException("Cannot connect two wiresets of different sizes.");
            for (int i = 0; i < m_aWires.Length; i++)
                m_aWires[i].ConnectInput(wIn[i]);

            InputConected = true;
            
        }

    }
}
