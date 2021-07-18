using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Diagnostics;

namespace MemoryManagement
{
    class SortingThread
    {
        private IntArray m_aArrayToSort; //array that requires sorting
        private Thread m_tThread; //the thread that runs the process
        private static int ID = 0;
        private static Mutex m_mCreationMutex = new Mutex(); //mutex for assigning thread names
        private static Mutex m_sortMutex = new Mutex(); 

        public SortingThread()
        {
            m_mCreationMutex.WaitOne();
            m_tThread = new Thread(Sort);
            m_tThread.Name = "T" + ID;
            ID++;
            m_mCreationMutex.ReleaseMutex();
        }

        //runs the thread
        public void Start()
        {
            m_tThread.Start();
        }

        //waits (sleeps) until the thread terminates
        public void Join()
        {
            m_tThread.Join();
        }

        public void DeleteArray()
        {
            MemoryManagementUnit.getInstance().Delete(m_aArrayToSort);
        }

        //Copies a section of the memory from another IntArray
        public void CopyFrom(IntArray a, int iStart, int iEnd)
        {
            int idx = 0;
            m_aArrayToSort = MemoryManagementUnit.getInstance().New(m_tThread, iEnd - iStart + 1);
            for (idx = iStart; idx <= iEnd; idx++)
                m_aArrayToSort[idx - iStart] = a[idx];
        }

        //Copies data from a regular array (to be called only from Main)
        public void CopyFrom(int[] a)
        {
            int idx = 0;
            m_aArrayToSort = MemoryManagementUnit.getInstance().New(m_tThread, a.Length);
            for (idx = 0; idx < a.Length; idx++)
                m_aArrayToSort[idx] = a[idx];
        }

        //Copies data into a regular array (to be called only from Main)
        public void CopyTo(int[] a)
        {
            int idx = 0;
            for (idx = 0; idx < a.Length; idx++)
                a[idx] = m_aArrayToSort[idx];
        }

        //returns the value of the array at a certain index
        public int ValueAt(int idx)
        {
            return m_aArrayToSort[idx];
        }

        //writes the current contents of the thread memory
        public override string ToString()
        {
            string sArray = "";
            int idx = 0;
            for (idx = 0; idx < m_aArrayToSort.Length; idx++)
                sArray += m_aArrayToSort[idx] + " ";
            return sArray;
        }

        //uses merge sort to sort an array
        public void Sort()
        {
            if (m_aArrayToSort.Length <= 1)
                return;
        
            //Create two threads - each with about half size array (note the even-odd size case).
            SortingThread firstHalf = new SortingThread();
            int middle = (m_aArrayToSort.Length -1) / 2;
            SortingThread secondHalf = new SortingThread();


            //Copy the first part of the array into the memory of the first thread
            firstHalf.CopyFrom(m_aArrayToSort, 0, middle);
            //Copy the second part of the array into the  memory of the second thread
            secondHalf.CopyFrom(m_aArrayToSort, middle + 1, m_aArrayToSort.Length - 1);

       
            //Run the two threads and wait for them to terminate
            firstHalf.Start();
            secondHalf.Start();
            
            firstHalf.Join();
            secondHalf.Join();

            //Merge the two sorted arrays from the memory of the threads back into the original array
            int firstIndex = 0;
            int lastIndex = 0;
            int currentIndex = 0;
            
            while (firstIndex < firstHalf.m_aArrayToSort.Length && lastIndex < secondHalf.m_aArrayToSort.Length)
            {
                m_sortMutex.WaitOne();
                if (firstHalf.m_aArrayToSort[firstIndex] < secondHalf.m_aArrayToSort[lastIndex])
                {
                    
                    m_aArrayToSort[currentIndex] = firstHalf.m_aArrayToSort[firstIndex];
                    firstIndex++;
                    currentIndex++;
                    m_sortMutex.ReleaseMutex();
                }
                else
                {
                    m_aArrayToSort[currentIndex] = secondHalf.m_aArrayToSort[lastIndex];
                    lastIndex++;
                    currentIndex++;
                    m_sortMutex.ReleaseMutex();
                }
                
            }
            while(firstIndex < firstHalf.m_aArrayToSort.Length)
            {
                m_aArrayToSort[currentIndex] = firstHalf.m_aArrayToSort[firstIndex];
                firstIndex++;
                currentIndex++;
            }
            while (lastIndex < secondHalf.m_aArrayToSort.Length)
            {
                m_aArrayToSort[currentIndex] = secondHalf.m_aArrayToSort[lastIndex];
                lastIndex++;
                currentIndex++;
            }
            
            //Delete the arrays
            firstHalf.DeleteArray();
            secondHalf.DeleteArray();
        }
    }
}
