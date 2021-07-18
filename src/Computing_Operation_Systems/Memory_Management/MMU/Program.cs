using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;
using System.Threading;
using System.IO;

namespace MemoryManagement
{
    class Program
    {
        public static bool VerifySort(int[] aToSort, int[] aSorted)
        {
            //first, check that the sorted array is sorted
            int idx = 0;
            for (idx = 0; idx < aSorted.Length - 1; idx++)
                if (aSorted[idx] > aSorted[idx + 1])
                    return false;
            //now, check that every number in the original array appears in the target array
            List<int> lSorted = new List<int>(aSorted);
            for (idx = 0; idx < aToSort.Length - 1; idx++)
            {
                if (!lSorted.Remove(aToSort[idx]))
                    return false;
            }
            return true;
       }
        static void Main(string[] args)
        {
            SwappingMemoryManagementUnit.SetMemoryManagementUnitType(50);
            //BoundedMemoryManagementUnit.SetMemoryManagementUnitType(1000);
            //SwappingMemoryManagementUnit.SetMemoryManagementUnitType(150);
            FileStream fs = new FileStream("Debug.txt", FileMode.Create);
            Debug.Listeners.Add(new TextWriterTraceListener(Console.Out));
            Debug.Listeners.Add(new TextWriterTraceListener(fs));
            Random rnd = new Random();

            for (int i = 1; i < 50; i++)
            {
                try
                {
                    SortingThread st = new SortingThread();
                    int[] a = new int[1 * i];
                    int[] b = new int[1 * i];
                    int idx = 0;
                    for (idx = 0; idx < a.Length; idx++)
                    {
                        a[idx] = rnd.Next(1000);
                    }
                    st.CopyFrom(a);
                    Debug.WriteLine("Start: [" + st + "]");
                    st.Start();
                    Thread.Sleep(2000);
                    st.Join();
                    st.CopyTo(b);
                    Debug.WriteLine("End: [" + st + "]");
                    Debug.Assert(VerifySort(a, b));
                    st.DeleteArray();
                    //Console.WriteLine(SwappingMemoryManagementUnit.getInstance().ToString());
                }
                catch(Exception e)
                {
                    Console.WriteLine(" >>>>>>>>>> " + e.Message);
                }
                
            }

            Debug.Close();
            Console.WriteLine("Done");
            Console.ReadLine();
        }
    }
}
