package Computer_Information_Security.Hash_Cracker.src;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class Server extends Thread
{

    private DatagramSocket socket;
    //private boolean running;
    private byte[] buf;

    private final String TEAM_NAME_STR = "CRACK3R5";


    private final int TEAMNAME_START = 0;
    private final int TEAMNAME_END = 31;
    private final int TYPE_START = 32;
    private final int TYPE_END = 32;
    private final int HASH_START = 33;
    private final int HASH_END = 72;
    private final int ORIGINAL_LENGTH_START = 73;
    private final int ORIGINAL_LENGTH_END = 73;
    private final int ORIGINAL_STRING_START_START = 74;
    private final int ORIGINAL_STRING_START_END = 329;
    private final int ORIGINAL_STRING_END_START = 330;
    private final int ORIGINAL_STRING_END_END = 585;


    private final char DISCOVER_TYPE = 1;
    private final char OFFER_TYPE = 2;
    private final char REQUEST_TYPE = 3;
    private final char ACK_TYPE = 4;
    private final char NACK_TYPE = 5;

    private final int BUF_SIZE = 586;

    private HelperFunctions2 helperFunctions;


    public Server()
    {
        try
        {
            socket = new DatagramSocket(3117);
            buf = new byte[BUF_SIZE];
            helperFunctions = new HelperFunctions2();
        }
        catch (SocketException e)
        {
            e.printStackTrace();
        }
    }


    public void run()
    {
        try
        {

            while(true)
            {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);


                //server receives a message

                //System.out.println("Received Type: " + type);
                HelperFunctions.debugServer(CrackerPacket.toString(packet));

                ResponseThread runnableResponse = new ResponseThread(socket, packet);
                Thread serviceThread = new Thread(runnableResponse);
                serviceThread.start();

//                serviceThread.start();
//                //serviceThread.join();
//
//                //send back to client
//
//                InetAddress sourceAddress = packet.getAddress();
//                int sourcePort = packet.getPort();
//
//                packet = new DatagramPacket(buf, buf.length, sourceAddress, sourcePort);
//                socket.send(packet);

            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }


    private class ResponseThread implements Runnable
    {
        DatagramSocket socket;
        DatagramPacket receivedPacket;

        public ResponseThread(DatagramSocket socket, DatagramPacket receivedPacket)
        {
            this.socket = socket;
            this.receivedPacket = receivedPacket;
        }

        public void run()
        {
            buf = giveServiceByType(receivedPacket.getData());

            InetAddress sourceAddress = receivedPacket.getAddress();
            int sourcePort = receivedPacket.getPort();
            DatagramPacket packetToSend = new DatagramPacket(buf, buf.length, sourceAddress, sourcePort);

            try {
                socket.send(packetToSend );
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    private byte[] giveServiceByType(byte[] received)
    {

        byte[] res = new byte[BUF_SIZE];

        char type = (char)received[TYPE_START];

        switch (type)
        {
            case DISCOVER_TYPE:
            {
                res = serveDiscover();
                break;
            }
            case REQUEST_TYPE:
            {

                res = serveRequest(received);
                break;
            }
        }

//        System.out.println("giveServiceByType: final res is: " + new String(res));

        return res;
    }


    private byte[] serveDiscover()
    {
        byte[] res = new byte[BUF_SIZE];

        byte[] teamNameByte = TEAM_NAME_STR.getBytes();

        insertArrayToArray(teamNameByte, res, TEAMNAME_START, TEAMNAME_END);
        res[TYPE_START] = OFFER_TYPE;

        return res;
    }

    private byte[] serveRequest(byte[] buffer)
    {
        byte[] res;

        int originalLength = buffer[ORIGINAL_LENGTH_START];

        int originalStringStartEnd = ORIGINAL_STRING_START_START + originalLength - 1;
        int originalStringEndStart = originalStringStartEnd + 1;
        int originalStringEndEnd = originalStringEndStart + originalLength - 1;

        String startRange = getStringFromByteArray(buffer, ORIGINAL_STRING_START_START, originalStringStartEnd);
        String endRange = getStringFromByteArray(buffer, originalStringEndStart, originalStringEndEnd);
        String originalHash = getStringFromByteArray(buffer, HASH_START, HASH_END);

        long currTime = System.currentTimeMillis();

        String deHashed = null;

        try
        {
            deHashed = helperFunctions.tryDeHash(startRange, endRange, originalHash, currTime);
        }
        catch (Exception e) {}

        if(deHashed != null)
        {//make res ack
            res = makeAckPacket(deHashed, originalHash, originalLength);
        }
        else
        {//make res nack
            res = makeNackPacket(originalHash, originalLength);
        }

        return res;
    }

    private byte[] makeAckPacket(String deHashed, String originalHash, int originalLength)
    {
        byte[] res = new byte[BUF_SIZE];
        byte[] deHashedByteArr = stringToByteArr(deHashed);
        byte[] originalHashArr = stringToByteArr(originalHash);
        byte[] teamNameArr = stringToByteArr(TEAM_NAME_STR);

        int deHashedStartInd = ORIGINAL_STRING_START_START;
        int deHashedEndInd = deHashedStartInd + deHashed.length() - 1;


        res[ORIGINAL_LENGTH_START] = (byte)originalLength;
        insertArrayToArray(deHashedByteArr, res, deHashedStartInd, deHashedEndInd);
        insertArrayToArray(originalHashArr, res, HASH_START, HASH_END);
        insertArrayToArray(teamNameArr, res, TEAMNAME_START, TEAMNAME_END);


        res[TYPE_START] = ACK_TYPE;

        return res;
    }

    private byte[] makeNackPacket(String originalHash, int originalLength)
    {
        byte[] res = new byte[BUF_SIZE];
        byte[] originalHashArr = stringToByteArr(originalHash);
        byte[] teamNameArr = stringToByteArr(TEAM_NAME_STR);

        res[ORIGINAL_LENGTH_START] = (byte)originalLength;
        insertArrayToArray(originalHashArr, res, HASH_START, HASH_END);
        insertArrayToArray(teamNameArr, res, TEAMNAME_START, TEAMNAME_END);

        res[TYPE_START] = NACK_TYPE;

        return res;
    }

    private byte[] stringToByteArr (String str)
    {
        byte[] res = new byte[str.length()];

        for(int i=0; i<res.length; i++)
        {
            res[i] = (byte)str.charAt(i);
        }

        return res;
    }


    private void insertArrayToArray(byte[] toInsert, byte[] arr, int start, int end)
    {
        int i = start;
        int j=0;

        for (j= 0; j<toInsert.length; j++)
        {
            arr[i] = toInsert[j];
            i++;
        }

        while(i<=end)
        {
            arr[i] = (byte)' ';
            i++;
        }
    }

    private String getStringFromByteArray(byte[] arr, int start, int end)
    {
        String res = "";

        for (int i=start; i<=end; i++)
        {
            res += (char)arr[i];
        }

        return res;
    }

}