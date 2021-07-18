using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Scheduling
{
    class FirstComeFirstServedPolicy : SchedulingPolicy
    {
        protected Queue<int> processQueue;
        
        public FirstComeFirstServedPolicy()
        {
            processQueue = new Queue<int>();
        }

        public override int NextProcess(Dictionary<int, ProcessTableEntry> dProcessTable)
        {
            /*
            Console.WriteLine("======= Proccess ===========");
            foreach (ProcessTableEntry e in dProcessTable.Values.OrderByDescending(x => x.ProcessId))
            {
                Console.WriteLine("Process " + e.ProcessId + " (p=" + e.Priority + "): Done - " + e.Done + " Blocked - " + e.Blocked + " | Start: " + e.StartTime + " End: " + e.EndTime + " (total: " + (e.EndTime - e.StartTime) + ") | LastCPU: " + e.LastCPUTime + " MaxStarv: " + e.MaxStarvation);
            }
            Console.WriteLine("============================");
            */

            if (processQueue.Count <= 0) return -1;

            bool bOnlyIdleRemains = true;
            int idleProccessId = -1;

            foreach (ProcessTableEntry e in dProcessTable.Values)
            {
                if (e.Name != "idle" && e.Done != true && e.Blocked != true)
                {
                    bOnlyIdleRemains = false;
                }
                else if (e.Name == "idle") idleProccessId = e.ProcessId;
            }

            if (bOnlyIdleRemains && idleProccessId != -1) return idleProccessId;
            else
            {
                int first = processQueue.Dequeue();
                processQueue.Enqueue(first);

                if (dProcessTable[first].Name != "idle" && !dProcessTable[first].Done && !dProcessTable[first].Blocked)
                {
                    dProcessTable[first].Quantum = -1;
                    return first;
                }

                while (processQueue.Peek() != first)
                {
                    int current = processQueue.Dequeue();
                    processQueue.Enqueue(current);

                    if (dProcessTable[current].Name != "idle" && !dProcessTable[current].Done && !dProcessTable[current].Blocked)
                    {
                        dProcessTable[current].Quantum = -1;
                        return current;
                    }
                }
            }
            
            return -1;
        }

        public override void AddProcess(int iProcessId)
        {
            processQueue.Enqueue(iProcessId);
        }

        public override bool RescheduleAfterInterrupt()
        {
            return false;
        }
    }
}
