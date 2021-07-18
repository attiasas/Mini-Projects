using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Diagnostics;
using System.IO;

namespace MemoryManagement
{
    class SwappingMemoryManagementUnit : MemoryManagementUnit
    {        
        //you code here        
        private int[] m_aMemory;//physical memory
        private MemoryBlock m_lMemoryBlocks;
        private LinkedList<MemoryBlock> m_usedLinkList;
        private Dictionary<string, MemoryBlock> memoryTable;

        //singleton implementation
        public static void SetMemoryManagementUnitType(int cInts)
        {
            m_mmuInstance = new SwappingMemoryManagementUnit(cInts);
        }

        private SwappingMemoryManagementUnit(int cInts)
        {
            m_aMemory = new int[cInts];//this is the only "new" allowed in this class
            m_lMemoryBlocks = new MemoryBlock(0, cInts - 1, null, null);
            memoryTable = new Dictionary<string, MemoryBlock>();
            m_usedLinkList = new LinkedList<MemoryBlock>();
        }

        //Returns the allocated memory blocked that was not accessed for the longest time
        private MemoryBlock GetLeastRecentlyAccessedMemoryBlock()
        {
            MemoryBlock leastUsed = m_usedLinkList.Last.Value;
            m_usedLinkList.RemoveLast();

            return leastUsed;
        }

        //Merges holes before and after a free memory block
        private MemoryBlock MergeHoles(MemoryBlock mbFree)
        {
            if(mbFree.Previous != null && mbFree.Previous.Free() && mbFree.Next != null && mbFree.Next.Free())
            {
                MemoryBlock mergedBothSides = new MemoryBlock(mbFree.Previous.Start, mbFree.Next.End, mbFree.Previous.Previous, mbFree.Next.Next);
                if(mbFree.Previous.Previous != null) mbFree.Previous.Previous.Next = mergedBothSides;
                if (mbFree.Next.Next != null) mbFree.Next.Next.Previous = mergedBothSides;
                if (mbFree.Previous == m_lMemoryBlocks) m_lMemoryBlocks = mergedBothSides;
                return mergedBothSides;
            }
            else if (mbFree.Previous != null &&  mbFree.Previous.Free())
            {
                MemoryBlock mergedLeftSide = new MemoryBlock(mbFree.Previous.Start, mbFree.End, mbFree.Previous.Previous, mbFree.Next);
                if (mbFree.Previous.Previous != null) mbFree.Previous.Previous.Next = mergedLeftSide;
                if (mbFree.Next != null) mbFree.Next.Previous = mergedLeftSide;
                if (mbFree.Previous == m_lMemoryBlocks) m_lMemoryBlocks = mergedLeftSide;
                return mergedLeftSide;
            }
            else if(mbFree.Next != null &&  mbFree.Next.Free())
            {
                MemoryBlock mergedRightSide = new MemoryBlock(mbFree.Start, mbFree.Next.End, mbFree.Previous, mbFree.Next.Next);
                if (mbFree.Previous != null) mbFree.Previous.Next = mergedRightSide;
                if (mbFree.Next.Next != null) mbFree.Next.Next.Previous = mergedRightSide;
                if (mbFree == m_lMemoryBlocks) m_lMemoryBlocks = mergedRightSide;
                return mergedRightSide;
            }

            return mbFree;
        }

        //Returns the first available free block. 
        //If there isn't any sufficient hole, swap allocated blocks to the disk until a sufficient hole is created.
        private MemoryBlock GetFirstHole(int cInts)
        {
            bool found = false;
            MemoryBlock crawler = m_lMemoryBlocks;
            while(!found && crawler != null)
            {
                // search with first fit strategy
                if (crawler.Free() && crawler.Size() >= cInts) found = true;
                else crawler = crawler.Next;
            }

            // no free blocks
            while (!found)
            {
                // get leastUsed and update pointers
                crawler = GetLeastRecentlyAccessedMemoryBlock();
                memoryTable[crawler.Owner.Name] = null;

                // swapout and merge
                SwapOut(crawler);
                crawler = MergeHoles(crawler);
                if (crawler.Size() >= cInts) found = true;
            }

            return crawler;
        }

        public override IntArray New(Thread tOwner, int cInts)
        {
            if (cInts >= m_aMemory.Length)
                throw new OutOfMemoryException("Requested " + cInts + " units. Memory capacity " + m_aMemory.Length + " units.");

            m_mMutex.WaitOne();
            MemoryBlock block = GetFirstHole(cInts);
            if(block.Size() > cInts)
            {
                // spliting the block
                MemoryBlock ownersBlock = new MemoryBlock(block.Start, block.Start + (cInts - 1), block.Previous, null);
                MemoryBlock freeBlock = new MemoryBlock(block.Start + cInts, block.End, ownersBlock, block.Next);
                ownersBlock.Next = freeBlock;
                if(block.Previous != null) block.Previous.Next = ownersBlock;
                if(block.Next != null) block.Next.Previous = freeBlock;
                if (m_lMemoryBlocks == block) m_lMemoryBlocks = ownersBlock;

                block = ownersBlock;
            }

            block.Owner = tOwner;

            // update pointers
            m_usedLinkList.AddFirst(block);
            memoryTable[tOwner.Name] = block;

            m_mMutex.ReleaseMutex();

            return new IntArray(cInts, tOwner);
        }

        public override void Delete(IntArray aToDelete)
        {
            m_mMutex.WaitOne();
            if(memoryTable[aToDelete.Owner.Name] != null)
            {
                // in memory, remove and merge
                m_usedLinkList.Remove(memoryTable[aToDelete.Owner.Name]);
                memoryTable[aToDelete.Owner.Name].Owner = null;
                MergeHoles(memoryTable[aToDelete.Owner.Name]);
            }

            memoryTable.Remove(aToDelete.Owner.Name);

            m_mMutex.ReleaseMutex();

            // check if a file exists and delete
            FileInfo file = new FileInfo(aToDelete.Owner.Name + ".data");
            if (file.Exists) file.Delete();
        }
        
        public override void SetValueAt(Thread tOwner, int iPrivateAddress, int iValue)
        {
            m_mMutex.WaitOne();

            if (memoryTable[tOwner.Name] == null) SwapIn(tOwner);

            int iStart = memoryTable[tOwner.Name].Start;
            m_aMemory[iStart + iPrivateAddress] = iValue;

            // update pointers
            m_usedLinkList.Remove(memoryTable[tOwner.Name]);
            m_usedLinkList.AddFirst(memoryTable[tOwner.Name]);
            
            m_mMutex.ReleaseMutex();
        }

        public override int ValueAt(Thread tOwner, int iPrivateAddress)
        {
            m_mMutex.WaitOne();

            if (memoryTable[tOwner.Name] == null) SwapIn(tOwner);

            int iStart = memoryTable[tOwner.Name].Start;
            int iValue = m_aMemory[iStart + iPrivateAddress];

            // update pointers
            m_usedLinkList.Remove(memoryTable[tOwner.Name]);
            m_usedLinkList.AddFirst(memoryTable[tOwner.Name]);

            m_mMutex.ReleaseMutex();
            return iValue;
        }

        private void SwapOut(MemoryBlock aOut)
        {
            Thread tOut = aOut.Owner;
            StreamWriter sw = new StreamWriter(tOut.Name + ".data");

            sw.Write(aOut.Size() + ",");
            for(int i = aOut.Start; i <= aOut.End; i++)
            {
                sw.Write(m_aMemory[i]);
                if (i != aOut.End) sw.Write(" ");
            }
            sw.Close();
            aOut.Owner = null;
        }

        private void SwapIn(Thread tIn)
        {
            StreamReader sr = new StreamReader(tIn.Name + ".data");
            //read relevant data from the file
            string sLine = sr.ReadLine();
            char[] delimiters = { ',', ' ' };
            string[] lineParsed = sLine.Split(delimiters);

            //allocate the required memory using New
            IntArray allocated = New(tIn, Int32.Parse(lineParsed[0]));
            
            //copy data to the allocated memory
            for (int i = 1; i <= allocated.Length; i++) allocated[i - 1] = Int32.Parse(lineParsed[i]);

            sr.Close();
        }
    }
}
