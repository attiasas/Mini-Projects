package Computer_Information_Security.Hash_Cracker.src;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Created By: Assaf, On 04/01/2020
 * Description:
 */
public class CrackerPacket
{

    public enum Type
    {
        DISCOVER,OFFER,REQUEST,ACK,NACK
    }

    public static final String TEAM_NAME = "CRACK3R5";
    public static final String ENCODING = "UTF-8";
    public static final int SERVER_PORT = 3117;

    private static final int TEAM_BYTE_SIZE = 32;
    private static final int TYPE_BYTE_SIZE = 1;
    private static final int HASH_BYTE_SIZE = 40;
    private static final int RANGE_LEN_BYTE_SIZE = 1;
    private static final int START_RANGE_BYTE_SIZE = 256;
    private static final int END_RANGE_BYTE_SIZE = 256;

    private static byte[] teamBytes = setBytesName();

    private static int packetSize = TEAM_BYTE_SIZE + TYPE_BYTE_SIZE + HASH_BYTE_SIZE +
                                    RANGE_LEN_BYTE_SIZE + START_RANGE_BYTE_SIZE + END_RANGE_BYTE_SIZE;

    //<editor-fold desc="Help Methods">
    private static void insertDataToPacket(byte[] buffer,Type packetType,String hash, int len, String startRange, String endRange)
    {
        int i;

        // team
        for(i = 0; i < teamBytes.length; i++)
        {
            buffer[i] = teamBytes[i];
        }

        // type
        buffer[i++] = (byte)((packetType.ordinal() + 1));

        if(Type.REQUEST.equals(packetType) || Type.ACK.equals(packetType))
        {
            // hash
            byte[] hashBytes = hash.getBytes();
            for(int j = 0; j < hashBytes.length && i < (TEAM_BYTE_SIZE + TYPE_BYTE_SIZE + HASH_BYTE_SIZE); j++)
            {
                buffer[i] = hashBytes[j];
                i++;
            }

            // answerLen
            buffer[i++] = (byte)(len);
            // startRange
            byte[] startBytes = startRange.getBytes();
            for(int j = 0; j < startBytes.length && i < (TEAM_BYTE_SIZE + TYPE_BYTE_SIZE + HASH_BYTE_SIZE + RANGE_LEN_BYTE_SIZE + START_RANGE_BYTE_SIZE); j++)
            {
                buffer[i] = startBytes[j];
                i++;
            }

            if(Type.REQUEST.equals(packetType))
            {
                // endRange
                byte[] endBytes = endRange.getBytes();
                //i = TEAM_BYTE_SIZE + TYPE_BYTE_SIZE + HASH_BYTE_SIZE + RANGE_LEN_BYTE_SIZE + START_RANGE_BYTE_SIZE; // if padding start range
                for(int j = 0; j < endBytes.length && i < packetSize; j++)
                {
                    buffer[i] = endBytes[j];
                    i++;
                }
            }
        }
    }

    public static String toString(DatagramPacket packet) throws UnsupportedEncodingException
    {
        if(packet.getData().length < TEAM_BYTE_SIZE + TYPE_BYTE_SIZE)
        {
            return "DATA IS NOT BY FORMAT";
        }
        String s = "============================================\n";
        s += "From: " + new String(Arrays.copyOfRange(packet.getData(),0,TEAM_BYTE_SIZE - 1),ENCODING) + "\n";
        Type type = getPacketType(packet);
        s += "Type: " + type + "\n";
        if(!Type.DISCOVER.equals(type) && !Type.OFFER.equals(type) && !Type.NACK.equals(type))
        {
            s += "Hash: " + getHash(packet) + "\n" + "StartDomain: " + getStartRange(packet) + "\n";
            if(!Type.ACK.equals(type))
            {
                s += "EndDomain: " + getEndRange(packet) + "\n";
            }
        }
        s += "============================================\n";
        return s;
    }

    private static byte[] setBytesName()
    {
        byte[] res = new byte[TEAM_BYTE_SIZE];

        byte[] teamFromString = TEAM_NAME.getBytes();

        for(int i = 0; i < res.length; i++)
        {
            if(i < teamFromString.length) res[i] = teamFromString[i];
            else res[i] = 0;
        }

        return res;
    }

    public static String getHash(DatagramPacket workInfo) throws UnsupportedEncodingException
    {
        // check
        if(workInfo.getData().length < TEAM_BYTE_SIZE + TYPE_BYTE_SIZE + HASH_BYTE_SIZE) return null;

        return new String(Arrays.copyOfRange(workInfo.getData(),TEAM_BYTE_SIZE + TYPE_BYTE_SIZE,TEAM_BYTE_SIZE + TYPE_BYTE_SIZE + HASH_BYTE_SIZE),ENCODING);
    }

    public static String getStartRange(DatagramPacket workInfo) throws UnsupportedEncodingException
    {
        byte[] data = workInfo.getData();

        // check
        if(data == null || data.length < TEAM_BYTE_SIZE + TYPE_BYTE_SIZE + HASH_BYTE_SIZE) return null; // not by format

        int rangeLen = data[TEAM_BYTE_SIZE + TYPE_BYTE_SIZE + HASH_BYTE_SIZE];

        return new String(Arrays.copyOfRange(data,TEAM_BYTE_SIZE + TYPE_BYTE_SIZE + HASH_BYTE_SIZE + RANGE_LEN_BYTE_SIZE,TEAM_BYTE_SIZE + TYPE_BYTE_SIZE + HASH_BYTE_SIZE + RANGE_LEN_BYTE_SIZE + rangeLen),ENCODING);
    }

    public static String getEndRange(DatagramPacket workInfo) throws UnsupportedEncodingException {

        byte[] data = workInfo.getData();

        // check
        if(data == null || data.length < TEAM_BYTE_SIZE + TYPE_BYTE_SIZE + HASH_BYTE_SIZE) return null; // not by format

        int rangeLen = data[TEAM_BYTE_SIZE + TYPE_BYTE_SIZE + HASH_BYTE_SIZE];

        //return new String(Arrays.copyOfRange(data,TEAM_BYTE_SIZE + TYPE_BYTE_SIZE + HASH_BYTE_SIZE + RANGE_LEN_BYTE_SIZE + START_RANGE_BYTE_SIZE,TEAM_BYTE_SIZE + TYPE_BYTE_SIZE + HASH_BYTE_SIZE + RANGE_LEN_BYTE_SIZE + START_RANGE_BYTE_SIZE + rangeLen - 1),ENCODING); // if padding startRange
        return new String(Arrays.copyOfRange(data,TEAM_BYTE_SIZE + TYPE_BYTE_SIZE + HASH_BYTE_SIZE + RANGE_LEN_BYTE_SIZE + rangeLen,TEAM_BYTE_SIZE + TYPE_BYTE_SIZE + HASH_BYTE_SIZE + RANGE_LEN_BYTE_SIZE + (2 * rangeLen)),ENCODING);
    }

    public static Type getPacketType(DatagramPacket packet)
    {
        byte[] packetBuffer = packet.getData();
        if(packetBuffer == null || packetBuffer.length < TEAM_BYTE_SIZE + TYPE_BYTE_SIZE) return null; // not by format

        // interpret
        byte type = (byte) (packetBuffer[TEAM_BYTE_SIZE] - 1);

        if(type < 0 || type >= Type.values().length) return null; // not by format

        return Type.values()[type];
    }

    public static DatagramPacket rawPacket()
    {
        byte[] buffer = new byte[packetSize];

        return new DatagramPacket(buffer,buffer.length);
    }
    //</editor-fold>

    //<editor-fold desc="Packet Types">
    public static DatagramPacket discover() throws UnknownHostException
    {
        InetAddress broadcastIp = InetAddress.getByName("255.255.255.255");

        byte[] buffer = new byte[packetSize];

        // Insert info to msg
        insertDataToPacket(buffer,Type.DISCOVER,null,0,null,null);

        return new DatagramPacket(buffer,buffer.length,broadcastIp,SERVER_PORT);
    }

    public static DatagramPacket offer(DatagramPacket packet)
    {
        byte[] buffer = new byte[packetSize];

        insertDataToPacket(buffer,Type.OFFER,null,0,null,null);

        return new DatagramPacket(buffer,buffer.length,packet.getAddress(),packet.getPort());
    }

    public static DatagramPacket request(String hash, int answerLen, String startRange, String endRange, InetAddress serverIP)
    {
        byte[] buffer = new byte[packetSize];

        insertDataToPacket(buffer,Type.REQUEST,hash,answerLen,startRange,endRange);

        return new DatagramPacket(buffer,buffer.length,serverIP,SERVER_PORT);
    }

    public static DatagramPacket ack(DatagramPacket request, String answer) throws UnsupportedEncodingException
    {
        byte[] buffer = new byte[packetSize];
        byte[] data = request.getData();
        int rangeLen = data[TEAM_BYTE_SIZE + TYPE_BYTE_SIZE + HASH_BYTE_SIZE];

        insertDataToPacket(buffer,Type.ACK,getHash(request),rangeLen,answer,null);

        return new DatagramPacket(buffer,buffer.length,request.getAddress(),request.getPort());
    }

    public static DatagramPacket nack(DatagramPacket request) throws UnsupportedEncodingException
    {
        byte[] buffer = new byte[packetSize];

        insertDataToPacket(buffer,Type.NACK,getHash(request),0,null,null);

        return new DatagramPacket(buffer,buffer.length,request.getAddress(),request.getPort());
    }
    //</editor-fold>
}
