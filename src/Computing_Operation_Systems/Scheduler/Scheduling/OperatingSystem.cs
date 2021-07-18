using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Scheduling
{
    class OperatingSystem
    {
        public Disk Disk { get; private set; }
        public CPU CPU { get; private set; }
        private Dictionary<int, ProcessTableEntry> m_dProcessTable;
        private List<ReadTokenRequest> m_lReadRequests;
        private int m_cProcesses;
        private SchedulingPolicy m_spPolicy;
        private static int IDLE_PROCESS_ID = 0;

        public OperatingSystem(CPU cpu, Disk disk, SchedulingPolicy sp)
        {
            CPU = cpu;
            Disk = disk;
            m_dProcessTable = new Dictionary<int, ProcessTableEntry>();
            m_lReadRequests = new List<ReadTokenRequest>();
            cpu.OperatingSystem = this;
            disk.OperatingSystem = this;
            m_spPolicy = sp;

            //create an "idle" process here
            IdleCode idle = new IdleCode();
            m_dProcessTable[m_cProcesses] = new ProcessTableEntry(m_cProcesses, "idle", idle);
            m_dProcessTable[m_cProcesses].StartTime = CPU.TickCount;
            m_spPolicy.AddProcess(m_cProcesses);
            m_cProcesses++;
        }


        public void CreateProcess(string sCodeFileName)
        {
            Code code = new Code(sCodeFileName);
            m_dProcessTable[m_cProcesses] = new ProcessTableEntry(m_cProcesses, sCodeFileName, code);
            m_dProcessTable[m_cProcesses].StartTime = CPU.TickCount;
            m_spPolicy.AddProcess(m_cProcesses);
            m_cProcesses++;
        }
        public void CreateProcess(string sCodeFileName, int iPriority)
        {
            Code code = new Code(sCodeFileName);
            m_dProcessTable[m_cProcesses] = new ProcessTableEntry(m_cProcesses, sCodeFileName, code);
            m_dProcessTable[m_cProcesses].Priority = iPriority;
            m_dProcessTable[m_cProcesses].StartTime = CPU.TickCount;
            m_spPolicy.AddProcess(m_cProcesses);
            m_cProcesses++;
        }

        public void ProcessTerminated(Exception e)
        {
            if (e != null)
                Console.WriteLine("Process " + CPU.ActiveProcess + " terminated unexpectedly. " + e);
            m_dProcessTable[CPU.ActiveProcess].Done = true;
            m_dProcessTable[CPU.ActiveProcess].Console.Close();
            m_dProcessTable[CPU.ActiveProcess].EndTime = CPU.TickCount;
            ActivateScheduler();
        }

        public void TimeoutReached()
        {
            ActivateScheduler();
        }

        public void ReadToken(string sFileName, int iTokenNumber, int iProcessId, string sParameterName)
        {
            ReadTokenRequest request = new ReadTokenRequest();
            request.ProcessId = iProcessId;
            request.TokenNumber = iTokenNumber;
            request.TargetVariable = sParameterName;
            request.Token = null;
            request.FileName = sFileName;
            m_dProcessTable[iProcessId].Blocked = true;
            if (Disk.ActiveRequest == null)
                Disk.ActiveRequest = request;
            else
                m_lReadRequests.Add(request);
            CPU.ProgramCounter = CPU.ProgramCounter + 1;
            ActivateScheduler();
        }

        public void Interrupt(ReadTokenRequest rFinishedRequest)
        {
            //implement an "end read request" interrupt handler.
            //translate the returned token into a value (double).
            double result = double.NaN;
            //when the token is null, EOF has been reached.
            if (rFinishedRequest.Token != null)
            {
                result = Double.Parse(rFinishedRequest.Token);
            }

            //write the value to the appropriate address space of the calling process.
            m_dProcessTable[rFinishedRequest.ProcessId].AddressSpace[rFinishedRequest.TargetVariable] = result;
            m_dProcessTable[rFinishedRequest.ProcessId].Blocked = false;

            /*
            Console.WriteLine("== Disk ===========");
            Console.WriteLine("<<Interrupt>> Request: (proccess " + rFinishedRequest.ProcessId + "): i=" + rFinishedRequest.TokenNumber + " data=" + rFinishedRequest.Token + " to var=" + rFinishedRequest.TargetVariable);
            Console.WriteLine("-- R wait List ----------");
            foreach(ReadTokenRequest request in m_lReadRequests)
            {
                Console.WriteLine("Request: (proccess" + request.ProcessId + "): i=" + request.TokenNumber);
            }
            Console.WriteLine("== Disk ===========");
            */

            //activate the next request in queue on the disk.
            if (m_lReadRequests.Count > 0)
            {
                Disk.ActiveRequest = m_lReadRequests.First();
                m_lReadRequests.Remove(Disk.ActiveRequest);
                //Disk.ProcessRequest();
            }

            if (m_spPolicy.RescheduleAfterInterrupt())
                ActivateScheduler();
        }

        private ProcessTableEntry ContextSwitch(int iEnteringProcessId)
        {
            //implement a context switch, switching between the currently active process on the CPU to the process with pid iEnteringProcessId
            ProcessTableEntry leavingProcess = null;

            if(CPU.ActiveProcess != -1)
            {
                leavingProcess = m_dProcessTable[CPU.ActiveProcess];
                leavingProcess.AddressSpace = CPU.ActiveAddressSpace;
                leavingProcess.Console = CPU.ActiveConsole;
                leavingProcess.ProgramCounter = CPU.ProgramCounter;

                leavingProcess.LastCPUTime = CPU.TickCount;

                m_dProcessTable[CPU.ActiveProcess] = leavingProcess;

                /*
                Console.WriteLine("--------------------");
                Console.WriteLine("Context switch from " + leavingProcess.ProcessId + " to " + iEnteringProcessId);
                Console.WriteLine("TickCount: " + CPU.TickCount);
                Console.WriteLine("Average turnaround: " + AverageTurnaround());
                Console.WriteLine("Maximal starvation: " + MaximalStarvation());
                Console.WriteLine("--------------------");
                */
            }

            int starveTime = CPU.TickCount - m_dProcessTable[iEnteringProcessId].LastCPUTime;
            if (starveTime > m_dProcessTable[iEnteringProcessId].MaxStarvation) m_dProcessTable[iEnteringProcessId].MaxStarvation = starveTime;

            CPU.ActiveProcess = iEnteringProcessId;
            CPU.ActiveAddressSpace = m_dProcessTable[iEnteringProcessId].AddressSpace;
            CPU.ProgramCounter = m_dProcessTable[iEnteringProcessId].ProgramCounter;
            CPU.RemainingTime = m_dProcessTable[iEnteringProcessId].Quantum;

            CPU.ActiveConsole = m_dProcessTable[iEnteringProcessId].Console;
            return leavingProcess;
        }

        public void ActivateScheduler()
        {
            int iNextProcessId = m_spPolicy.NextProcess(m_dProcessTable);
            if (iNextProcessId == -1)
            {
                Console.WriteLine("All processes terminated or blocked.");
                CPU.Done = true;
            }
            else
            {
                bool bOnlyIdleRemains = false;
                if (iNextProcessId == IDLE_PROCESS_ID)
                {
                    bOnlyIdleRemains = true;
                    foreach (ProcessTableEntry e in m_dProcessTable.Values)
                    {
                        if (e.Name != "idle" && e.Done != true)
                        {
                            bOnlyIdleRemains = false;
                        }
                    }
                }
                if(bOnlyIdleRemains)
                {
                    Console.WriteLine("Only idle remains.");
                    CPU.Done = true;
                }
                else
                    ContextSwitch(iNextProcessId);
            }
        }

        public double AverageTurnaround()
        {
            //Compute the average time from the moment that a process enters the system until it terminates.

            double sum = 0;
            int num = 0;

            foreach(ProcessTableEntry entry in m_dProcessTable.Values)
            {
                if(entry.Done)
                {
                    sum += entry.EndTime - entry.StartTime;
                    num++;
                }
            }

            return (sum / num);
        }
        public int MaximalStarvation()
        {
            //Compute the maximal time that some project has waited in a ready stage without receiving CPU time.
            int max = 0;

            foreach(ProcessTableEntry entry in m_dProcessTable.Values)
            {
                if (entry.MaxStarvation > max) max = entry.MaxStarvation;
            }

            return max;
        }
    }
}
