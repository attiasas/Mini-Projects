using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;
using System.Threading;

namespace MemoryManagement
{
    class BoundedMemoryManagementUnit : MemoryManagementUnit
    {
        //your code here
        private int[] m_aMemory; //physical memory
        private int memoryPointer;
        private Dictionary<string, int> memoryTable;

        //Singleton implementation
        public static void SetMemoryManagementUnitType(int cInts)
        {
            m_mmuInstance = new BoundedMemoryManagementUnit(cInts);
        }

        private BoundedMemoryManagementUnit(int cInts)
        {
            m_aMemory = new int[cInts];//this is the only allowed "new" in this class
            memoryPointer = 0;
            memoryTable = new Dictionary<string, int>();
        }

        public override IntArray New(Thread tOwner, int cInts)
        {
            m_mMutex.WaitOne();
            if (memoryPointer + cInts > m_aMemory.Length)//change false to checking if there is no available memory
                throw new OutOfMemoryException("Cannot allocate " + cInts + " ints. (" + memoryPointer + "/" + m_aMemory.Length + ")");
        
            memoryTable[tOwner.Name] = memoryPointer;
            memoryPointer += cInts;
            
            m_mMutex.ReleaseMutex();
            return new IntArray(cInts, tOwner);
        }

        public override void Delete(IntArray aToDelete)
        {
            m_mMutex.WaitOne();
            memoryTable.Remove(aToDelete.Owner.Name);
            m_mMutex.ReleaseMutex();
        }

        public override void SetValueAt(Thread tOwner, int iPrivateAddress, int iValue)
        {
            m_mMutex.WaitOne();
            int iStart = memoryTable[tOwner.Name];//find the begining of the memory block assigned to this thread in the physical memory
            m_aMemory[iStart + iPrivateAddress] = iValue;
            m_mMutex.ReleaseMutex();
        }

        public override int ValueAt(Thread tOwner, int iPrivateAddress)
        {
            m_mMutex.WaitOne();
            int iStart = memoryTable[tOwner.Name];//find the begining of the memory block assigned to this thread in the physical memory
            int iValue = m_aMemory[iStart + iPrivateAddress];
            m_mMutex.ReleaseMutex();
            return iValue;
        }
    }
}
