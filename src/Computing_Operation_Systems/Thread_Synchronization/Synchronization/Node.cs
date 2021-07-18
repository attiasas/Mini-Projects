using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Diagnostics;

namespace ThreadSynchronization
{
    class Node 
    {
        /**/private MailBox m_mbMailBox; //incoming mailbox of the node
        private Dictionary<int, MailBoxWriter> m_dNeighbors; //maps node ids to outgoing mailboxes (the routing table)
        private bool m_bDone; //notifies the thread to terminate
        public int ID { get; private set; } //the identifier of the node

        /**/private Dictionary<int, int> distanceTable; // <index,distance>
        private Dictionary<int, int> routingTable; // <index, routerIndex>
        
        /**/private Dictionary<int, char[]> recivedMessages; // <messageID,msg>

        private Mutex createMsgMutex;
        private static Mutex changeDistanceTableMutex = new Mutex();

        public Node( int iID )
        {
            ID = iID;
            m_mbMailBox = new SynchronizedMailBox();
            m_dNeighbors = new Dictionary<int, MailBoxWriter>();
            m_bDone = false;

            distanceTable = new Dictionary<int, int>();
            routingTable = new Dictionary<int, int>();
            recivedMessages = new Dictionary<int, char[]>();

            createMsgMutex = new Mutex();

            distanceTable[ID] = 0;
        }

        //Returns access to the node's mailbox
        public MailBoxWriter GetMailBox()
        {
            return new MailBoxWriter(m_mbMailBox);
        }

        //sends routing messages to all the immediate neighbors
        private void SendRoutingMessages()
        {
            foreach(int iNeighbor in m_dNeighbors.Keys)
            {
                RoutingMessage routingMessage = new RoutingMessage(ID, iNeighbor, distanceTable);
                m_dNeighbors[iNeighbor].Send(routingMessage);
            }
        }

        //handles an incoming routing neighbors according to the Bellman-Ford algorithm
        private void HandleRoutingMessage(RoutingMessage rmsg)
        {
            bool changed = false;

            changeDistanceTableMutex.WaitOne();
            foreach (int iNode in rmsg.GetAllNodes())
            {
                if (!distanceTable.ContainsKey(iNode) || rmsg.GetDistance(iNode) + 1 < distanceTable[iNode])
                {
                    distanceTable[iNode] = rmsg.GetDistance(iNode) + 1;
                    routingTable[iNode] = rmsg.Sender;
                    changed = true;
                }
            }
            changeDistanceTableMutex.ReleaseMutex();

            if (changed)
            {
                SendRoutingMessages();
            }

            //PrintRoutingTable();
        }

        //handles an incoming packet message 
        //the message can be directed to the current node or to another node, in which case it should be forwarded
        private void HandlePacketMessage(PacketMessage pmsg)
        {
            int router = GetRouter(pmsg.Target);

            if (router == ID) // income msg
            {
                createMsgMutex.WaitOne();
                if(!recivedMessages.ContainsKey(pmsg.MessageID))
                {
                    recivedMessages[pmsg.MessageID] = new char[pmsg.Size];
                }
                createMsgMutex.ReleaseMutex();

                recivedMessages[pmsg.MessageID][pmsg.Location] = pmsg.Packet;
            }
            else if (router != -1) // routing msg
            {
                m_dNeighbors[router].Send(pmsg);
            }
        }

        //returns the neighboring router for the target node
        private int GetRouter(int iTarget)
        {
            if (m_dNeighbors.ContainsKey(iTarget))
                return iTarget;
            if (routingTable.ContainsKey(iTarget))
                return routingTable[iTarget];
            if (iTarget == ID)
                return ID;

            return -1;
        }

        //returns the distance of the routing node
        private int GetDistance(int iTarget)
        {
            //your code here
            return distanceTable[iTarget];
        }

        //returns the list of all reachable nodes (all the nodes that appear in the routing table)
        private List<int> ReachableNodes()
        {
            return new List<int>(distanceTable.Keys);
        }

        //returns the list of recieved messages
        //if a character in a message was not received (the message was not fully received), the array should contain
        //the sepcail character '\0'
        private List<char[]> ReceivedMessages()
        {
            List<char[]> messages = new List<char[]>();
            foreach (char[] msg in recivedMessages.Values)
                messages.Add(msg);
            return messages;
        }


        //Node (thread) main method - repeatedly checks for incoming mail and handles it.
        //when the thread is terminated using the KillMessage, outputs the routing table and the list of accepted messages
        public void Run()
        {
            SendRoutingMessages();
            while (!m_bDone)
            {
                Message msg = m_mbMailBox.Read();
                if (msg is RoutingMessage)
                {
                    HandleRoutingMessage((RoutingMessage)msg);                   
                }
                if (msg is PacketMessage)
                {
                    HandlePacketMessage((PacketMessage)msg);
                }
                if (msg is KillMessage)
                    m_bDone = true;
            }
            //PrintRoutingTable();
            //PrintTest();
            PrintAllMessages();
        }

        //Creates a thread that executes the Run method, starts it, and returns the created Thread object
        public Thread Start()
        {
            //your code here
            Thread thread = new Thread(() => Run());
            thread.Start();
            return thread;
        }

        //prints the routing table 
        public void PrintRoutingTable()
        {
            string s = "Routing table for " + ID + "\n";
            foreach (int iNode in ReachableNodes())
            {
                s += iNode + ", distance = " + GetDistance(iNode) + ", router = " + GetRouter(iNode) + "\n";
            }
            Debug.WriteLine(s);
        }

        public void PrintTest()
        {
            string s = "== Id: " + ID + " ======================" + "\n";
            s += "Immid neighbors \n";
            foreach (int iNode in m_dNeighbors.Keys)
            {
                s += iNode + "\n"; 
            }
            s += "-------------------------- \n";
            s += "Routing table for " + ID + "\n";
            foreach (int iNode in ReachableNodes())
            {
                s += iNode + ", distance = " + GetDistance(iNode) + ", router = " + GetRouter(iNode) + "\n";
            }
            s += "================================= \n";
            Debug.WriteLine(s);
        }

        //prints the list of accepted messages
        //if a char is missing, writes '?' instead
        public void PrintAllMessages()
        {
            Debug.WriteLine("Message list of " + ID);
            foreach (char[] aMessage in ReceivedMessages())
            {
                string s = "";
                for (int i = 0; i < aMessage.Length; i++)
                {
                    if (aMessage[i] == '\0')
                        s += "?";
                    else
                        s += aMessage[i];
                }
                Debug.WriteLine(s);
            }
        }


        //Sets a link (immediate access) between two nodes
        public static void SetLink(Node n1, Node n2)
        {
            n1.m_dNeighbors[n2.ID] = n2.GetMailBox();
            n1.distanceTable[n2.ID] = 1;

            n2.m_dNeighbors[n1.ID] = n1.GetMailBox();
            n2.distanceTable[n1.ID] = 1;
        }


        //Allows the administrator to send a string message from one machine to another
        //the message must be broken into packets
        //if the node does not recognize the target (the target is not in the routing table)
        //the method returns false
        public bool SendMessage(string sMessage, int iMessageID, int iTarget)
        {
            int router = GetRouter(iTarget);
            if (router == -1 || router == ID) return false;
            
            for(int i = 0; i < sMessage.Length; i++)
            {
                m_dNeighbors[router].Send(new PacketMessage(ID, iTarget, iMessageID, sMessage[i], i, sMessage.Length));
            }

            return true;
        }
    }
}
