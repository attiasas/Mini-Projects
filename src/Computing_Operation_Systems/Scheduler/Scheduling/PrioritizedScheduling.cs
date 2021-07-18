using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Scheduling
{
    class PrioritizedScheduling : RoundRobin
    {
        public PrioritizedScheduling(int iQuantum) : base(iQuantum){}

        public override int NextProcess(Dictionary<int, ProcessTableEntry> dProcessTable)
        {
            if (dProcessTable.Count <= 0) return -1;

            /*
            Console.WriteLine("======= Proccess ===========");
            foreach (ProcessTableEntry e in dProcessTable.Values.OrderByDescending(x => x.ProcessId))
            {
                Console.WriteLine("Process " + e.ProcessId + " (p=" + e.Priority + "): Done - " + e.Done + " Blocked - " + e.Blocked + " | Start: " + e.StartTime + " End: " + e.EndTime + " (total: " + (e.EndTime - e.StartTime) + ") | LastCPU: " + e.LastCPUTime + " MaxStarv: " + e.MaxStarvation);
            }
            Console.WriteLine("============================");
            */

            for (int priority = dProcessTable.Values.OrderByDescending(x => x.Priority).First().Priority; priority >= 0; priority--)
            {
                int first = processQueue.Dequeue();
                processQueue.Enqueue(first);

                if (dProcessTable[first].Priority == priority && !dProcessTable[first].Done && !dProcessTable[first].Blocked)
                {
                    dProcessTable[first].Quantum = mQuantum;
                    return first;
                }

                while (processQueue.Peek() != first)
                {
                    int current = processQueue.Dequeue();
                    processQueue.Enqueue(current);

                    if (dProcessTable[current].Priority == priority && !dProcessTable[current].Done && !dProcessTable[current].Blocked)
                    {
                        dProcessTable[current].Quantum = mQuantum;
                        return current;
                    }
                }
            }

            return -1;
        }
        
    }
}
